package dtp.jade.crisismanager.crisisevents;

import java.awt.geom.Point2D;

public class TrafficJamEvent extends CrisisEvent {

    private static final String EVENT_TYPE = "Traffic Jam";

    Point2D startPoint;

    Point2D endPoint;

    // nowy (wiekszy) koszt przejazdu
    double jamCost;

    public Point2D getStartPoint() {

        return startPoint;
    }

    public void setStartPoint(Point2D startPoint) {

        this.startPoint = startPoint;
    }

    public Point2D getEndPoint() {

        return endPoint;
    }

    public void setEndPoint(Point2D endPoint) {

        this.endPoint = endPoint;
    }

    public double getJamCost() {

        return jamCost;
    }

    public void setJamCost(double jamCost) {

        this.jamCost = jamCost;
    }

    public String getEventType() {

        return EVENT_TYPE;
    }

    public String toString() {

        return "<" + getEventTime() + ">" + " type = " + EVENT_TYPE + ", ID = " + getEventID() + ", road = " + "("
                + getStartPoint().getX() + ", " + getStartPoint().getY() + ") -> (" + getEndPoint().getX() + ", "
                + getEndPoint().getY() + ")" + ", jam cost = " + getJamCost();
    }
}
