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

    public GraphPoint getSource() {

        return source;
    }

    public GraphPoint getDestination() {

        return destination;
    }

    public GraphTrack getTrack() {

        return track;
    }

    public double getStartTime() {

        return startTime;
    }

    public double getEndTime() {

        return endTime;
    }

    @Override
    public void print() {

        switch (type) {
            case "DEPOT":

                System.out.println("------------------  DEPOT   -----------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad());
                System.out.println("---------------------------------------------");

                break;
            case "DRIVE":

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

                break;
            case "WAIT":

                System.out.println("------------------   WAIT   -----------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad());
                System.out.println("---------------------------------------------");

                break;
            case "PICKUP":

                System.out.println("------------------  PICKUP  -----------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad() + "\t\t\t comID = " + getCommissionID());
                System.out.println("---------------------------------------------");

                break;
            case "DELIVERY":

                System.out.println("------------------ DELIVERY -----------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad() + "\t\t\t comID = " + getCommissionID());
                System.out.println("---------------------------------------------");

                break;
            case "SWITCH":

                System.out.println("------------------- SWITCH ------------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad());
                System.out.println("---------------------------------------------");

                break;
            case "BROKEN":

                System.out.println("------------------  BROKEN  -----------------");
                System.out.println("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2));
                System.out.println("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]");
                System.out.println("load = " + getCurrentLoad());

                break;
            default:

                System.out.println("CalendarAction -> unknown action type = " + type);
                break;
        }
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        switch (type) {
            case "DEPOT":

                str.append("------------------  DEPOT   -----------------\n");
                str.append("time = [").append(MyNumberFormat.formatDouble(getStartTime(), 4, 2)).append(", ").append(MyNumberFormat.formatDouble(getEndTime(), 4, 2)).append("] total = ").append(MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2)).append("\n");
                str.append("location = [").append(MyNumberFormat.formatDouble(getSource().getX(), 3, 2)).append(", ").append(MyNumberFormat.formatDouble(getSource().getY(), 3, 2)).append("]\n");
                str.append("load = " + getCurrentLoad() + "\n");

                break;
            case "DRIVE":

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

                break;
            case "WAIT":

                str.append("------------------   WAIT   -----------------\n");
                str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
                str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
                str.append("load = " + getCurrentLoad() + "\n");

                break;
            case "PICKUP":

                str.append("------------------  PICKUP  -----------------\n");
                str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
                str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
                str.append("load = " + getCurrentLoad() + "\t\t comID = " + getCommissionID() + "\n");

                break;
            case "DELIVERY":

                str.append("------------------ DELIVERY -----------------\n");
                str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
                str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
                str.append("load = " + getCurrentLoad() + "\t\t comID = " + getCommissionID() + "\n");

                break;
            case "SWITCH":

                str.append("------------------  SWITCH  ------------------\n");
                str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
                str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
                str.append("load = " + getCurrentLoad() + "\n");

                break;
            case "BROKEN":

                str.append("------------------  BROKEN  -----------------\n");
                str.append("time = [" + MyNumberFormat.formatDouble(getStartTime(), 4, 2) + ", "
                        + MyNumberFormat.formatDouble(getEndTime(), 4, 2) + "] total = "
                        + MyNumberFormat.formatDouble(getEndTime() - getStartTime(), 3, 2) + "\n");
                str.append("location = [" + MyNumberFormat.formatDouble(getSource().getX(), 3, 2) + ", "
                        + MyNumberFormat.formatDouble(getSource().getY(), 3, 2) + "]\n");
                str.append("load = " + getCurrentLoad() + "\n");

                break;
            default:

                str.append("CalendarAction -> unknown action type = " + type + "\n");
                break;
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
