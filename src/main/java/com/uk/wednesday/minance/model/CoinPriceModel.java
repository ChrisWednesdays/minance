package com.uk.wednesday.minance.model;

import java.util.Date;

public class CoinPriceModel {

    private String varId;
    private String symbol;
    private String dateTime;
    private double price;
    private double percentChange;

    public CoinPriceModel(String varId, String symbol, String dateTime, double price, double percentChange) {
        this.varId = varId;
        this.symbol = symbol;
        this.dateTime = dateTime;
        this.price = price;
        this.percentChange = percentChange;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }
}
