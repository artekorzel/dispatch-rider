package dtp.jade.agentcalendar;

import dtp.util.MyNumberFormat;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class CalendarActionWithoutGraph implements Serializable, CalendarAction {

    private int commissionID;

    private int sourceCommissionID;

    private String type = "";

    private Point2D.Double source;

    private Point2D.Double destination;

    private double startTime;

    private double endTime;

    private double currentLoad;

    public CalendarActionWithoutGraph() {

    }

    public CalendarActionWithoutGraph(int commissionID, int sourceCommissionID, String type, Point2D.Double source, Point2D.Double destination,
                                      double startTime, double endTime, double currentLoad) {

        this.commissionID = commissionID;
        this.sourceCommissionID = sourceCommissionID;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.startTime = startTime;
        this.endTime = endTime;
        this.currentLoad = currentLoad;
    }

    public int getCommissionID() {
        return commissionID;
    }

    public int getSourceCommissionID() {
        return sourceCommissionID;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public double getCurrentLoad() {

        return currentLoad;
    }

    public void setCurrentLoad(double currentLoad) {

        this.currentLoad = currentLoad;
    }

    public Point2D.Double getSource() {

        return source;
    }

    public Point2D.Double getDestination() {

        return destination;
    }

    public double getStartTime() {

        return startTime;
    }

    public void setStartTime(double startTime) {

        this.startTime = startTime;
    }

    public double getEndTime() {

        return endTime;
    }

    public void setEndTime(double endTime) {

        this.endTime = endTime;
    }

    public boolean isPDD() {
        return "PICKUP".equals(getType()) || "DELIVERY".equals(getType()) || "DEPOT".equals(getType());
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        switch (type) {
            case "DEPOT":

                str.append("------------------  DEPOT   -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("location = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("]\n");
                str.append("load = ").append(getCurrentLoad()).append("\n");

                break;
            case "DRIVE":

                str.append("------------------  DRIVE   -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("track = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("] --> [").append(MyNumberFormat.formatDouble(getDestination().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getDestination().getY(), 3, 2)).append("]\n");
                str.append("load = ").append(getCurrentLoad()).append("\n");

                break;
            case "WAIT":

                str.append("------------------   WAIT   -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("location = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("]\n");
                str.append("load = ").append(getCurrentLoad()).append("\n");

                break;
            case "PICKUP":

                str.append("------------------  PICKUP  -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("location = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("]\n");
                str.append("load = ").append(getCurrentLoad()).append("\t\t comID = ").append(getCommissionID()).append("\n");

                break;
            case "DELIVERY":

                str.append("------------------ DELIVERY -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("location = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("]\n");
                str.append("load = ").append(getCurrentLoad()).append("\t\t comID = ").append(getCommissionID()).append("\n");

                break;
            default:

                str.append("CalendarAction -> toString -> sth wrong!\n");
                break;
        }

        return str.toString();
    }

    public CalendarActionWithoutGraph clone() {

        CalendarActionWithoutGraph newCalendarAction;

        newCalendarAction = new CalendarActionWithoutGraph();
        newCalendarAction.commissionID = this.commissionID;
        newCalendarAction.sourceCommissionID = this.sourceCommissionID;
        newCalendarAction.type = this.type;
        newCalendarAction.source = (Point2D.Double) this.source.clone();
        newCalendarAction.destination = (Point2D.Double) this.destination.clone();
        newCalendarAction.startTime = this.startTime;
        newCalendarAction.endTime = this.endTime;
        newCalendarAction.currentLoad = this.currentLoad;

        return newCalendarAction;
    }

    public boolean equals(CalendarActionWithoutGraph other) {

        if (this.getCommissionID() != other.getCommissionID())
            return false;

        if (!this.type.equals(other.getType()))
            return false;

        if (!this.source.equals(other.getSource()))
            return false;

        if (!this.destination.equals(other.getDestination()))
            return false;

        if (this.startTime != other.getStartTime())
            return false;

        if (this.endTime != other.getEndTime())
            return false;

        if (this.currentLoad != other.getCurrentLoad())
            return false;

        return true;
    }
}
