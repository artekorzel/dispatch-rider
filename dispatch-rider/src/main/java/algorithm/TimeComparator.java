package algorithm;

import dtp.commission.Commission;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class TimeComparator implements Comparator<Commission> {
    private Boolean pickups;

    public TimeComparator(Boolean pickups, Point2D.Double currentLocation) {
        this.pickups = pickups;
    }

    public int compare(Commission com1, Commission com2) {
        if (pickups == null) {
            return Double.compare(com1.getPickupTime1(), com2.getDeliveryTime1());
        }
        if (pickups) {
            return Double.compare(com1.getPickupTime1(), com2.getPickupTime1());
        } else {
            return Double.compare(com1.getDeliveryTime1(), com2.getDeliveryTime1());
        }
    }
}
