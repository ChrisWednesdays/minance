package com.uk.wednesday.minance.model;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class OrderHistoryModel {

    private String varId;
    private String time;
    private double offerPurchasePrice;
    private double offerSellPrice;
    private double percentGainLoss;

    public OrderHistoryModel(String varId, String time, double offerPurchasePrice, double offerSellPrice, double percentGainLoss) {
        this.varId = varId;
        this.time = time;
        this.offerPurchasePrice = offerPurchasePrice;
        this.offerSellPrice = offerSellPrice;
        this.percentGainLoss = percentGainLoss;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getOfferPurchasePrice() {
        return offerPurchasePrice;
    }

    public void setOfferPurchasePrice(double offerPurchasePrice) {
        this.offerPurchasePrice = offerPurchasePrice;
    }

    public double getOfferSellPrice() {
        return offerSellPrice;
    }

    public void setOfferSellPrice(double offerSellPrice) {
        this.offerSellPrice = offerSellPrice;
    }

    public double getPercentGainLoss() {
        return percentGainLoss;
    }

    public void setPercentGainLoss(double percentGainLoss) {
        this.percentGainLoss = percentGainLoss;
    }

    @Override
    public String toString() {
        return "OrderHistoryModel{" +
                "varId=" + varId +
                ", time=" + time +
                ", offerPurchasePrice=" + offerPurchasePrice +
                ", offerGainPrice=" + offerSellPrice +
                ", percentGainLoss=" + percentGainLoss +
                '}';
    }

    public DBObject toDBObject() {
        BasicDBObjectBuilder builder = BasicDBObjectBuilder
                .start("varId", varId)
                .append("time", time)
                .append("offerPurchasePrice", offerPurchasePrice)
                .append("offerGainPrice", offerSellPrice)
                .append("percentGainLoss", percentGainLoss);
        return builder.get();
    }
}
