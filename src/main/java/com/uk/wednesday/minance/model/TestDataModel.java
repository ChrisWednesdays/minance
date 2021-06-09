package com.uk.wednesday.minance.model;

public class TestDataModel {

    private String varId;
    private String date;
    private String percent;

    public TestDataModel(String varId, String date, String percent) {
        this.varId = varId;
        this.date = date;
        this.percent = percent;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
