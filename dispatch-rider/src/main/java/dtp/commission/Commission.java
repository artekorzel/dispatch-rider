package dtp.commission;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Commission implements Serializable, Comparable<Commission> {

    private int id;

    private double pickupX;

    private double pickupY;

    private double pickupTime1;

    private double pickupTime2;

    private double deliveryX;

    private double deliveryY;

    private double deliveryTime1;

    private double deliveryTime2;

    private int load;

    private int pickUpServiceTime;

    private int deliveryServiceTime;

    private int pickUpId;

    private int deliveryId;

    private double actualLoad;

    private boolean isPickup;

    private Map<String, Double> punishmentFunParamsPickup = new HashMap<>();
    private Map<String, Double> punishmentFunParamsDelivery = new HashMap<>();

    public Commission() {

        pickUpId = 0;
        deliveryId = 0;
        id = 0;
        pickupX = 0;
        pickupY = 0;
        pickupTime1 = 0;
        pickupTime2 = 0;
        deliveryX = 0;
        deliveryY = 0;
        deliveryTime1 = 0;
        deliveryTime2 = 0;
        load = -1;
        pickUpServiceTime = 0;
        deliveryServiceTime = 0;
        actualLoad = 0;
    }

    public Commission(int id, double pickupX, double pickupY,
                      double pickupTime1, double pickupTime2, double deliveryX,
                      double deliveryY, double deliveryTime1, double deliveryTime2,
                      int load, int pickUpServiceTime, int deliveryServiceTime) {

        this.id = id;
        this.pickUpId = 0;
        this.deliveryId = 0;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.pickupTime1 = pickupTime1;
        this.pickupTime2 = pickupTime2;
        this.deliveryX = deliveryX;
        this.deliveryY = deliveryY;
        this.deliveryTime1 = deliveryTime1;
        this.deliveryTime2 = deliveryTime2;
        this.load = load;
        this.pickUpServiceTime = pickUpServiceTime;
        this.deliveryServiceTime = deliveryServiceTime;
        actualLoad = 0;
    }

    public Commission(int id, int pickUpId, double pickupX, double pickupY,
                      double pickupTime1, double pickupTime2, int deliveryId,
                      double deliveryX, double deliveryY, double deliveryTime1,
                      double deliveryTime2, int load, int pickUpServiceTime,
                      int deliveryServiceTime) {

        this.id = id;
        this.pickUpId = pickUpId;
        this.deliveryId = deliveryId;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.pickupTime1 = pickupTime1;
        this.pickupTime2 = pickupTime2;
        this.deliveryX = deliveryX;
        this.deliveryY = deliveryY;
        this.deliveryTime1 = deliveryTime1;
        this.deliveryTime2 = deliveryTime2;
        this.load = load;
        this.pickUpServiceTime = pickUpServiceTime;
        this.deliveryServiceTime = deliveryServiceTime;
        actualLoad = 0;
    }

    public static Commission copy(Commission com) {
        Commission result = new Commission();
        result.setActualLoad(com.getActualLoad());
        result.setDeliveryId(com.getDeliveryId());
        result.setDeliveryTime1(com.getDeliveryTime1());
        result.setDeliveryTime2(com.getDeliveryTime2());
        result.setDeliveryX(com.getDeliveryX());
        result.setDeliveryY(com.getDeliveryY());
        result.setID(com.getID());
        result.setLoad(com.getLoad());
        result.setPickup(com.isPickup());
        result.setPickUpId(com.getPickUpId());
        result.setPickupTime1(com.getPickupTime1());
        result.setPickupTime2(com.getPickupTime2());
        result.setPickupX(com.getPickupX());
        result.setPickupY(com.getPickupY());
        result.setPickUpServiceTime(com.getPickUpServiceTime());
        result.setDeliveryServiceTime(com.getDeliveryServiceTime());
        result.setPunishmentFunParamsPickup(com.getPunishmentFunParamsPickup());
        result.setPunishmentFunParamsDelivery(com
                .getPunishmentFunParamsDelivery());
        return result;
    }

    public Map<String, Double> getPunishmentFunParamsDelivery() {
        return punishmentFunParamsDelivery;
    }

    public void setPunishmentFunParamsDelivery(
            Map<String, Double> punishmentFunParamsDelivery) {
        this.punishmentFunParamsDelivery = punishmentFunParamsDelivery;
    }

    public Map<String, Double> getPunishmentFunParamsPickup() {
        return punishmentFunParamsPickup;
    }

    public void setPunishmentFunParamsPickup(
            Map<String, Double> punishmentFunParamsPickup) {
        this.punishmentFunParamsPickup = punishmentFunParamsPickup;
    }

    public boolean isPickup() {
        return isPickup;
    }

    public void setPickup(boolean isPickup) {
        this.isPickup = isPickup;
    }

    public int getPickUpServiceTime() {
        return pickUpServiceTime;
    }

    public void setPickUpServiceTime(int pickUpServiceTime) {
        this.pickUpServiceTime = pickUpServiceTime;
    }

    public int getDeliveryServiceTime() {
        return deliveryServiceTime;
    }

    public void setDeliveryServiceTime(int deliveryServiceTime) {
        this.deliveryServiceTime = deliveryServiceTime;
    }

    public int getPickUpId() {
        return pickUpId;
    }

    public void setPickUpId(int pickUpId) {
        this.pickUpId = pickUpId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getID() {

        return id;
    }

    public void setID(int id) {

        this.id = id;
    }

    public double getPickupX() {

        return pickupX;
    }

    public void setPickupX(double pickupX) {

        this.pickupX = pickupX;
    }

    public double getPickupY() {

        return pickupY;
    }

    public void setPickupY(double pickupY) {

        this.pickupY = pickupY;
    }

    public double getPickupTime1() {

        return pickupTime1;
    }

    public void setPickupTime1(double pickupTime1) {

        this.pickupTime1 = pickupTime1;
    }

    public double getPickupTime2() {

        return pickupTime2;
    }

    public void setPickupTime2(double pickupTime2) {

        this.pickupTime2 = pickupTime2;
    }

    public double getDeliveryX() {

        return deliveryX;
    }

    public void setDeliveryX(double deliveryX) {

        this.deliveryX = deliveryX;
    }

    public double getDeliveryY() {

        return deliveryY;
    }

    public void setDeliveryY(double deliveryY) {

        this.deliveryY = deliveryY;
    }

    public double getDeliveryTime1() {

        return deliveryTime1;
    }

    public void setDeliveryTime1(double deliveryTime1) {

        this.deliveryTime1 = deliveryTime1;
    }

    public double getDeliveryTime2() {

        return deliveryTime2;
    }

    public void setDeliveryTime2(double deliveryTime2) {

        this.deliveryTime2 = deliveryTime2;
    }

    public int getLoad() {

        return load;
    }

    public void setLoad(int load) {

        this.load = load;
    }

    public void printCommision() {

        System.out.println("ID = " + id + "\tLoad = " + load);
        System.out.println("Pickup:   location = [" + pickupX + "," + pickupY
                + "] " + "\ttime = [" + pickupTime1 + "," + pickupTime2
                + "], service time = " + pickUpServiceTime);
        System.out.println("Delivery: location = [" + deliveryX + ","
                + deliveryY + "] " + "\ttime = [" + deliveryTime1 + ","
                + deliveryTime2 + "], service time = " + deliveryServiceTime);
    }

    @Override
    public String toString() {

        return "ID = " + id + " Pickup: loc = (" + pickupX + ", " + pickupY
                + ") time = [" + pickupTime1 + ", " + pickupTime2
                + "] Delivery: loc = (" + deliveryX + ", " + deliveryY
                + ") time = [" + deliveryTime1 + ", " + deliveryTime2
                + "] Load = " + load + " pickUpServiceTime = "
                + pickUpServiceTime + " deliveryServiceTime = "
                + deliveryServiceTime;
    }

    public int compareTo(Commission o) {
        return this.id - o.id;
    }

    public double getActualLoad() {
        return actualLoad;
    }

    public void setActualLoad(double load) {
        this.actualLoad = load;
    }
}
