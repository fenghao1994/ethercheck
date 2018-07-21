package com.shfound.ethckeck.model;

public class Result {
    private String id;//序号
    private String address;
    private String etherScanLimit;
    private String excelLimit;

    private String unFindInEther;
    private String wxName;
    private String cover;//是否重复

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEtherScanLimit() {
        return etherScanLimit;
    }

    public void setEtherScanLimit(String etherScanLimit) {
        this.etherScanLimit = etherScanLimit;
    }

    public String getExcelLimit() {
        return excelLimit;
    }

    public void setExcelLimit(String excelLimit) {
        this.excelLimit = excelLimit;
    }

    public String getUnFindInEther() {
        return unFindInEther;
    }

    public void setUnFindInEther(String unFindInEther) {
        this.unFindInEther = unFindInEther;
    }

    public String getWxName() {
        return wxName;
    }

    public void setWxName(String wxName) {
        this.wxName = wxName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
