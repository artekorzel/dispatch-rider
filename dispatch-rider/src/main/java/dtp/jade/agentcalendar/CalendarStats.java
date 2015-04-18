package dtp.jade.agentcalendar;

import algorithm.Schedule;
import jade.core.AID;

import java.io.Serializable;
import java.util.List;

public class CalendarStats implements Serializable {

    private AID aid;

    private int capacity;

    private AID driverAID;
    private AID truckAID;
    private AID trailerAID;

    private Schedule schedule2;

    // MODIFY by LP
    private int trailer_mass;
    private int truck_power;
    private int truck_reliability;
    private int truck_comfort;
    private int truck_fuelConsumption;
    // end of modifications

    private double distance;

    private double cost;

    private double waitTime;

    private double driveTime;
    private double punishment;

    private List<CalendarAction> schedule;

    private boolean isDefault;
    private int maxSTDepth;
    private long reorganizationTime;
    private long organizationTime;

    public CalendarStats(AID aid) {

        maxSTDepth = 0;
        this.aid = aid;
        isDefault = false;
    }

    public double getDriveTime() {
        return driveTime;
    }

    public void setDriveTime(double driveTime) {
        this.driveTime = driveTime;
    }

    public double getPunishment() {
        return punishment;
    }

    public void setPunishment(double punishment) {
        this.punishment = punishment;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getMaxSTDepth() {
        return maxSTDepth;
    }

    public void setMaxSTDepth(int STDepth) {
        maxSTDepth = STDepth;
    }

    public AID getAID() {

        return aid;
    }

    public void setAID(AID aid) {

        this.aid = aid;
    }

    public double getDistance() {

        return distance;
    }

    public void setDistance(double distance) {

        this.distance = distance;
    }

    public double getWaitTime() {

        return waitTime;
    }

    public void setWaitTime(double waitTime) {

        this.waitTime = waitTime;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public AID getDriverAID() {
        return driverAID;
    }

    public void setDriverAID(AID driverAID) {
        this.driverAID = driverAID;
    }

    public AID getTruckAID() {
        return truckAID;
    }

    public void setTruckAID(AID truckAID) {
        this.truckAID = truckAID;
    }

    public AID getTrailerAID() {
        return trailerAID;
    }

    public void setTrailerAID(AID trailerAID) {
        this.trailerAID = trailerAID;
    }

    public void setSchedule(List<CalendarAction> schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule2() {
        return schedule2;
    }

    public List<CalendarAction> getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule2 = schedule;
    }

    public long getReorganizationTime() {
        return reorganizationTime;
    }

    public void setReorganizationTime(long time) {
        reorganizationTime = time;
    }

    public long getOrganizationTime() {
        return organizationTime;
    }

    public void setOrganizationTime(long time) {
        organizationTime = time;
    }

    // Modification by LP

    /**
     * @return the mass
     */
    public int getMass() {
        return trailer_mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(int mass) {
        this.trailer_mass = mass;
    }

    /**
     * @return the reliability
     */
    public int getReliability() {
        return truck_reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability(int reliability) {
        this.truck_reliability = reliability;
    }

    /**
     * @return the comfort
     */
    public int getComfort() {
        return truck_comfort;
    }

    /**
     * @param comfort the comfort to set
     */
    public void setComfort(int comfort) {
        this.truck_comfort = comfort;
    }

    /**
     * @return the fuelComsuption
     */
    public int getFuelConsumption() {
        return truck_fuelConsumption;
    }

    public void setFuelConsumption(int fuelConsumption) {
        this.truck_fuelConsumption = fuelConsumption;
    }

    /**
     * @return the power
     */
    public int getPower() {
        return truck_power;
    }

    /**
     * @param power the power to set
     */
    public void setPower(int power) {
        this.truck_power = power;
    }
}
