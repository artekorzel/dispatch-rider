package algorithm;

import dtp.commission.Commission;

import java.awt.geom.Point2D;
import java.util.Comparator;

/**
 * This comparator is used to sort commissions before they are being distributed by Distributor Agent.
 * Commissions are sorted, because we want to first assign commissions, which are difficult to fulfill.
 */
public class CommissionsComparator implements Comparator<Commission> {
    private Point2D.Double depot;

    public CommissionsComparator(Point2D.Double depot) {
        this.depot = depot;
    }

    public int compare(Commission com1, Commission com2) {
        double dist1;
        double dist2;
        double tmp;
        dist1 = Helper.calculateDistance(depot, new Point2D.Double(com1.getPickupX(), com1.getPickupY()));
        tmp = Helper.calculateDistance(depot, new Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()));
        if (tmp > dist1) dist1 = tmp;
        dist2 = Helper.calculateDistance(depot, new Point2D.Double(com2.getPickupX(), com2.getPickupY()));
        tmp = Helper.calculateDistance(depot, new Point2D.Double(com2.getDeliveryX(), com2.getDeliveryY()));
        if (tmp > dist2) dist2 = tmp;
        if (dist1 > dist2) return 1;
        else return -1;
    }
}
