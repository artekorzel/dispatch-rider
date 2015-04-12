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

    public void setCommissionID(int commissionID) {

        this.commissionID = commissionID;
    }

    public int getSourceCommissionID() {
        return sourceCommissionID;
    }

    public void setSourceCommissionID(int sourceCommissionID) {
        this.sourceCommissionID = sourceCommissionID;
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

    public void setSource(Point2D.Double source) {

        this.source = source;
    }

    public Point2D.Double getDestination() {

        return destination;
    }

    public void setDestination(Point2D.Double destination) {

        this.destination = destination;
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

    @Override
    public void print() {

        if (type.equals("DEPOT")) {

            System.out.println("------------------  DEPOT   -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad());
            System.out.println("---------------------------------------------");

        } else if (type.equals("DRIVE")) {

            System.out.println("------------------  DRIVE   -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("track = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "] --> ["
                    + MyNumberFormat.formatDouble(getDestination().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getDestination().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad());
            System.out.println("---------------------------------------------");

        } else if (type.equals("WAIT")) {

            System.out.println("------------------   WAIT   -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad());
            System.out.println("---------------------------------------------");

        } else if (type.equals("PICKUP")) {

            System.out.println("------------------  PICKUP  -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad() + "\t\t\t comID = " + getCommissionID());
            System.out.println("---------------------------------------------");

        } else if (type.equals("DELIVERY")) {

            System.out.println("------------------ DELIVERY -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad() + "\t\t\t comID = " + getCommissionID());
            System.out.println("---------------------------------------------");

        } else {

            System.out.println("CalendarAction -> print -> sth wrong!");
        }
    }

    public void printAll() {

        System.out.println("type = " + getType() + " com ID = " + getCommissionID());
        System.out.println("source = [" + getSource().getX() + ", " + getSource().getY() + "]" + " destination = ["
                + getDestination().getX() + ", " + getDestination().getY() + "]");
        System.out.println("time = [" + getStartTime() + ", " + getEndTime() + "]" + " total = "
                + (getEndTime() - getStartTime()));
        System.out.println("current load = " + getCurrentLoad());
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        if (type.equals("DEPOT")) {

            str.append("------------------  DEPOT   -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\n");

        } else if (type.equals("DRIVE")) {

            str.append("------------------  DRIVE   -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("track = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "] --> ["
                    + MyNumberFormat.formatDouble(getDestination().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getDestination().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\n");

        } else if (type.equals("WAIT")) {

            str.append("------------------   WAIT   -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\n");

        } else if (type.equals("PICKUP")) {

            str.append("------------------  PICKUP  -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\t\t comID = " + getCommissionID() + "\n");

        } else if (type.equals("DELIVERY")) {

            str.append("------------------ DELIVERY -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\t\t comID = " + getCommissionID() + "\n");

        } else {

            str.append("CalendarAction -> toString -> sth wrong!\n");
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
