package com.shfound.ethckeck.controller;

import com.google.gson.Gson;
import com.shfound.ethckeck.model.EthModel;
import com.shfound.ethckeck.model.ExcelModel;
import com.shfound.ethckeck.model.Result;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/eth")
public class ETHCheckController {

    public static String key = "6IK27S2VZ1A8JFI7SYZVUUINCCWU1XZS8S";


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String ethCheck() {
        return "check";
    }

    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    public String uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("ethAddress") String ethAddress, @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime, ModelMap modelMap) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = simpleDateFormat.parse(startTime.replace("T", " "));
            Date endDate = simpleDateFormat.parse(endTime.replace("T", " "));
            List<Map<String, String>> list;
            Sheet sheet;
            Workbook wb = null;
            List<ExcelModel> filterExcelList;
            List<EthModel.ResultBean> filterEthModeList;
            Row row;
            List<ExcelModel> excelModels = new ArrayList<>();
            String extString = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(inputStream);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(inputStream);
            }

            if (wb != null) {
                //用来存放表中数据
                list = new ArrayList<>();
                //获取第一个sheet
                sheet = wb.getSheetAt(0);
                //获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();
                //获取第一行
                row = sheet.getRow(0);
                //获取最大列数
                int colnum = row.getPhysicalNumberOfCells();

                for (int i = 1; i < rownum; i++) {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    row = sheet.getRow(i);
                    if (row != null && colnum >= 3) {
                        ExcelModel excelModel = new ExcelModel();
                        excelModel.setId((String) getCellFormatValue(row.getCell(0)));
                        excelModel.setAddress((String) getCellFormatValue(row.getCell(1)));
                        excelModel.setLimit((String) getCellFormatValue(row.getCell(2)));
                        excelModel.setWxName((String) getCellFormatValue(row.getCell(3)));
                        excelModels.add(excelModel);
                    } else {
                        break;
                    }
                    list.add(map);
                }
            }
            filterExcelList = excelFilter(excelModels);
            filterEthModeList = getDate(ethAddress, excelModels, startDate, endDate);
            List<Result> check = check(filterEthModeList, filterExcelList);
            modelMap.put("result", check);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "result";

    }

    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            //判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    private List<EthModel.ResultBean> getDate(String address, List<ExcelModel> excelModelList, Date startTime, Date endTime) {
        String url = "http://api.etherscan.io/api?module=account&action=txlist&address=" + address + "&startblock=0&endblock=99999999&sort=desc&apikey=" + key;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        EthModel ethModel = null;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();// 响应体
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 正确返回
                responseContent = EntityUtils.toString(entity, "UTF-8");
                ethModel = new Gson().fromJson(responseContent, EthModel.class);

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<EthModel.ResultBean> list = checkDate(address, ethModel, excelModelList, startTime, endTime);
        return list;
    }

    private List<EthModel.ResultBean> checkDate(String address, EthModel ethModel, List<ExcelModel> excelModelList, Date startTime, Date endTime) {
        if (ethModel == null || excelModelList == null || excelModelList.isEmpty()) {
            return null;
        }
        List<EthModel.ResultBean> list = spiltSomeDate(ethModel, startTime, endTime, address);
        List<EthModel.ResultBean> filterList = ethmodelFilter(list);
        return filterList;

    }

    //通过时间过滤一些多虑的数据
    private List<EthModel.ResultBean> spiltSomeDate(EthModel ethModel, Date startTime, Date endTime, String address) {
        List<EthModel.ResultBean> startResult = ethModel.getResult();
        List<EthModel.ResultBean> result = new ArrayList<>();
        for (EthModel.ResultBean bean : startResult) {
            String time = bean.getTimeStamp() + "000";
            Date date = new Date(Long.parseLong(time));
            if (date.compareTo(startTime) > 0 && date.compareTo(endTime) < 0) {
                if (isCollection(bean, address)) {
                    result.add(bean);
                }
            }
        }
        return result;
    }

    /**
     * 多笔打币记录合并
     */
    private List<EthModel.ResultBean> ethmodelFilter(List<EthModel.ResultBean> result) {
        List<EthModel.ResultBean> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            if (map.containsKey(result.get(i).getFrom())) {
                String s = map.get(result.get(i).getFrom());
                double value = Double.parseDouble(s) + Double.parseDouble(result.get(i).getValue());
                map.put(result.get(i).getFrom(), String.valueOf(value));
            } else {
                map.put(result.get(i).getFrom(), result.get(i).getValue());
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            EthModel.ResultBean resultBean = new EthModel.ResultBean();
            resultBean.setFrom(entry.getKey());
            resultBean.setValue(entry.getValue());
            list.add(resultBean);
        }
        return list;
    }

    /**
     * excel将重复填写的人进行过滤
     * @param list
     * @return
     */
    private List<ExcelModel> excelFilter(List<ExcelModel> list) {
        List<ExcelModel> result = new ArrayList<>();
        Map<String, ExcelModel> map = new HashMap<>();
        if (list != null && !list.isEmpty()) {
            for (ExcelModel excelModel : list) {
                if (map.containsKey(excelModel.getAddress())) {
                    ExcelModel excel = map.get(excelModel.getAddress());
                    double value = Double.parseDouble(excelModel.getLimit()) + Double.parseDouble(excel.getLimit());
                    excel.setLimit(String.valueOf(value));
                } else {
                    map.put(excelModel.getAddress(), excelModel);
                }
            }
        }
        return result;
    }

    private List<Result> check(List<EthModel.ResultBean> resultBeans, List<ExcelModel> excelModelList) {
        List<Result> list = new ArrayList<>();
        for (int i = 0; i < excelModelList.size(); i++) {
            boolean flag = false;
            Result result = new Result();
            for (int j = 0; j < resultBeans.size(); j++) {
                if (excelModelList.get(i).getAddress().equalsIgnoreCase(resultBeans.get(j).getFrom())) {
                    flag = true;
                    result.setAddress(excelModelList.get(i).getAddress());
                    result.setEtherScanLimit(String.valueOf(Double.parseDouble(resultBeans.get(j).getValue()) / 1000000000000000000l));
                    result.setExcelLimit(excelModelList.get(i).getLimit());
                    list.add(result);
                    break;
                }
            }
            if (!flag) {
                result.setUnFindInEther(excelModelList.get(i).getAddress());
            }
        }
        return list;
    }


    private boolean isCollection(EthModel.ResultBean eth, String address) {
        if (eth != null && eth.getTo().equalsIgnoreCase(address)) {
            return true;
        }
        return false;
    }
}
