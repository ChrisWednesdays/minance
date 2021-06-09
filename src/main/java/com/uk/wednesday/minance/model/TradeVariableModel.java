package com.uk.wednesday.minance.model;

public class TradeVariableModel {

    private String varId;
    private int candleTime;
    private int rsiIndicator;
    private double rsiLowerLimit;
    private double entryPoint;
    private double profitCut;
    private int releaseCount;

    public TradeVariableModel() {
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public int getCandleTime() {
        return candleTime;
    }

    public void setCandleTime(int candleTime) {
        this.candleTime = candleTime;
    }

    public int getRsiIndicator() {
        return rsiIndicator;
    }

    public void setRsiIndicator(int rsiIndicator) {
        this.rsiIndicator = rsiIndicator;
    }

    public double getRsiLowerLimit() {
        return rsiLowerLimit;
    }

    public void setRsiLowerLimit(double rsiLowerLimit) {
        this.rsiLowerLimit = rsiLowerLimit;
    }

    public double getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(double entryPoint) {
        this.entryPoint = entryPoint;
    }

    public double getProfitCut() {
        return profitCut;
    }

    public void setProfitCut(double profitCut) {
        this.profitCut = profitCut;
    }

    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }

    @Override
    public String toString() {
        return "TradeVariableModel{" +
                "varId=" + varId +
                ", candleTime=" + candleTime +
                ", rsiIndicator=" + rsiIndicator +
                ", rsiLowerLimit=" + rsiLowerLimit +
                ", entryPoint=" + entryPoint +
                ", profitCut=" + profitCut +
                ", releaseCount=" + releaseCount +
                '}';
    }
}
