package gui.map;

import dtp.graph.GraphPoint;

public class HolonGraphPoint extends GraphPoint {

    private Integer holonID;
    private Integer holonCreationTime, truckComfort, trailerCapacity;
    private Double summaryCost, waitTime;
    private String driver;

    public HolonGraphPoint(double xVal, double yVal) {
        super(xVal, yVal);
    }

    public Integer getHolonID() {
        return holonID;
    }

    public void setHolonID(Integer holonID) {
        this.holonID = holonID;
    }

    public Integer getTruckComfort() {
        return truckComfort;
    }

    public void setTruckComfort(Integer truckCapacity) {
        this.truckComfort = truckCapacity;
    }

    public Integer getTrailerCapacity() {
        return trailerCapacity;
    }

    public void setTrailerCapacity(Integer trailerCapacity) {
        this.trailerCapacity = trailerCapacity;
    }

    public Integer getHolonCreationTime() {
        return holonCreationTime;
    }

    public void setHolonCreationTime(Integer holonCreationTime) {
        this.holonCreationTime = holonCreationTime;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Double getSummaryCost() {
        return summaryCost;
    }

    public void setSummaryCost(Double summaryCost) {
        this.summaryCost = summaryCost;
    }

    public Double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Double waitTime) {
        this.waitTime = waitTime;
    }
}
