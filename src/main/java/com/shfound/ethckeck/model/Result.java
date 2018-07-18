package com.shfound.ethckeck.model;

public class Result {
    private String address;
    private String etherScanLimit;
    private String excelLimit;

    private String unFindInEther;

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
}
