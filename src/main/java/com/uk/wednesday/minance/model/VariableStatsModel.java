package com.uk.wednesday.minance.model;


import org.springframework.data.annotation.Id;

public class VariableStatsModel {

    @Id
    private String varId;
    private double percentageGain;
    private int numberOfRecords;

    public VariableStatsModel(String varId, double percentageGain, int numberOfRecords) {
        this.varId = varId;
        this.percentageGain = percentageGain;
        this.numberOfRecords = numberOfRecords;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public double getPercentageGain() {
        return percentageGain;
    }

    public void setPercentageGain(double percentageGain) {
        this.percentageGain = percentageGain;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
}
