package com.shfound.ethckeck.model;

public class ExcelModel {
    private String id;
    private String address;
    private String limit;
    private String wxName;
    private String isCover;// 是否重复

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

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getWxName() {
        return wxName;
    }

    public void setWxName(String wxName) {
        this.wxName = wxName;
    }

    public String getIsCover() {
        return isCover;
    }

    public void setIsCover(String isCover) {
        this.isCover = isCover;
    }
}
