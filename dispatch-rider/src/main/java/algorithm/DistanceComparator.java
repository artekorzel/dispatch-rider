package algorithm;

import dtp.commission.Commission;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class DistanceComparator implements Comparator<Commission> {
    private Point2D.Double currentLocation;
    private Boolean isPickup;

    public DistanceComparator(Boolean isPickup, Point2D.Double currentLocation) {
        this.currentLocation = currentLocation;
        this.isPickup = isPickup;
    }

    public int compare(Commission com1, Commission com2) {
        double dist1;
        double dist2;
        if (isPickup == null) {
            dist1 = Helper.calculateDistance(currentLocation, new Point2D.Double(com1.getPickupX(), com1.getPickupY()));
            dist2 = Helper.calculateDistance(currentLocation, new Point2D.Double(com2.getDeliveryX(), com2.getDeliveryY()));
        } else if (isPickup) {
            dist1 = Helper.calculateDistance(currentLocation, new Point2D.Double(com1.getPickupX(), com1.getPickupY()));
            dist2 = Helper.calculateDistance(currentLocation, new Point2D.Double(com2.getPickupX(), com2.getPickupY()));
        } else {
            dist1 = Helper.calculateDistance(currentLocation, new Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()));
            dist2 = Helper.calculateDistance(currentLocation, new Point2D.Double(com2.getDeliveryX(), com2.getDeliveryY()));
        }

        if (dist1 < dist2) return -1;
        if (dist1 > dist2) return 1;
        return 0;
    }
}
