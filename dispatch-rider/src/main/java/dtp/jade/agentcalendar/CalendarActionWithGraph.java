package dtp.jade.agentcalendar;

import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.util.MyNumberFormat;

import java.io.Serializable;

public class CalendarActionWithGraph implements Serializable, CalendarAction {

    private int commissionID;

    private int sourceCommissionID;

    private String type = "";

    private GraphPoint source;

    private GraphPoint destination;

    private GraphTrack track;

    private double startTime;

    private double endTime;

    private double currentLoad;

    public CalendarActionWithGraph() {

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

    public GraphPoint getSource() {

        return source;
    }

    public void setSource(GraphPoint source) {

        this.source = source;
    }

    public GraphPoint getDestination() {

        return destination;
    }

    public void setDestination(GraphPoint destination) {

        this.destination = destination;
    }

    public GraphTrack getTrack() {

        return track;
    }

    public void setTrack(GraphTrack track) {

        this.track = track;
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

        return "PICKUP".equals(getType())
                || "DELIVERY".equals(getType())
                || "SWITCH".equals(getType())
                || "BROKEN".equals(getType())
                || "DEPOT".equals(getType());
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
            track.print();
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

        } else if (type.equals("SWITCH")) {

            System.out.println("------------------- SWITCH ------------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad());
            System.out.println("---------------------------------------------");

        } else if (type.equals("BROKEN")) {

            System.out.println("------------------  BROKEN  -----------------");
            System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
            System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
            System.out.println("load = " + getCurrentLoad());

        } else {

            System.out.println("CalendarAction -> unknown action type = " + type);
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
            str.append(track.toString());
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

        } else if (type.equals("SWITCH")) {

            str.append("------------------  SWITCH  ------------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\n");

        } else if (type.equals("BROKEN")) {

            str.append("------------------  BROKEN  -----------------\n");
            str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                    + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                    + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
            str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                    + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
            str.append("load = " + getCurrentLoad() + "\n");

        } else {

            str.append("CalendarAction -> unknown action type = " + type + "\n");
        }

        return str.toString();
    }

    public CalendarActionWithGraph clone() {

        CalendarActionWithGraph newCalendarAction;

        newCalendarAction = new CalendarActionWithGraph();
        newCalendarAction.commissionID = this.commissionID;
        newCalendarAction.sourceCommissionID = this.sourceCommissionID;
        newCalendarAction.type = this.type;
        newCalendarAction.source = this.source;
        newCalendarAction.destination = this.destination;
        if (this.track == null)
            newCalendarAction.track = null;
        else
            newCalendarAction.track = this.track.Clone();
        newCalendarAction.startTime = this.startTime;
        newCalendarAction.endTime = this.endTime;
        newCalendarAction.currentLoad = this.currentLoad;

        return newCalendarAction;
    }

    public boolean equals(CalendarActionWithGraph other) {

        if (this.getCommissionID() != other.getCommissionID())
            return false;

        if (!this.type.equals(other.getType()))
            return false;

        if (!this.source.equals(other.getSource()))
            return false;

        if (!this.destination.equals(other.getDestination()))
            return false;

        if (this.track != null && other.getTrack() != null)
            if (!this.track.equals(other.getTrack()))
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
