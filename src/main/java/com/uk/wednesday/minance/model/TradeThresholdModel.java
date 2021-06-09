package com.uk.wednesday.minance.model;

public class TradeThresholdModel {

    private double purchasePrice;
    private double upperSellLimit;
    private double lowerSellLimit;

    public void PurchaseModel(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void PurchaseModel(double purchasePrice, double upperSellLimit, double lowerSellLimit) {
        this.purchasePrice = purchasePrice;
        this.upperSellLimit = upperSellLimit;
        this.lowerSellLimit = lowerSellLimit;
    }

    public TradeThresholdModel(double basePrice) {
        this.purchasePrice = basePrice;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getUpperSellLimit() {
        return upperSellLimit;
    }

    public void setUpperSellLimit(double upperSellLimit) {
        this.upperSellLimit = upperSellLimit;
    }

    public double getLowerSellLimit() {
        return lowerSellLimit;
    }

    public void setLowerSellLimit(double lowerSellLimit) {
        this.lowerSellLimit = lowerSellLimit;
    }
}
