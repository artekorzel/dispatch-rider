package dtp.jade.gui;

import dtp.jade.agentcalendar.CalendarStats;
import dtp.util.AgentIDResolver;

import java.awt.geom.Point2D;
import java.util.HashMap;

public class CalendarStatsHolder {

    private final HashMap<Integer, CalendarStats> collectedStats;
    private final int calendarStatsNumber;
    private int collectedCalendarStatsNumber;

    public CalendarStatsHolder(int calendarStatsNumber) {
        this.calendarStatsNumber = calendarStatsNumber;
        collectedStats = new HashMap<Integer, CalendarStats>();
        collectedCalendarStatsNumber = 0;
    }

    public void addCalendarStats(CalendarStats calendarStats) {

        int eUnitAgentID;

        eUnitAgentID = AgentIDResolver.getEUnitIDFromName(calendarStats
                .getAID().getLocalName());

        collectedStats.put(eUnitAgentID, calendarStats);
        collectedCalendarStatsNumber++;
    }

    public int getCollectedCalendarStatsNumber() {
        return collectedCalendarStatsNumber;
    }

    public boolean gotAllCalendarStats() {

        if (collectedCalendarStatsNumber == calendarStatsNumber) {

            return true;
        }

        return false;
    }

    public CalendarStats[] getAllStats() {
        CalendarStats[] result = new CalendarStats[calendarStatsNumber];
        int i = 0;
        for (int key : collectedStats.keySet()) {
            result[i] = collectedStats.get(key);
            i++;
        }
        return result;
    }

    public String getAllStatsToString() {

        StringBuilder str;

        str = new StringBuilder();

        str.append("****************************** STATISTICS ******************************\n");

        for (int i : collectedStats.keySet()) {

            str.append(collectedStats.get(i).getAID().getLocalName() + ": \n");
            str.append("truck: "
                    + collectedStats.get(i).getTruckAID().getLocalName()
                    + " trailer: "
                    + collectedStats.get(i).getTrailerAID().getLocalName()
                    + " capacity: " + collectedStats.get(i).getCapacity()
                    + "\n");
            str.append("\t distance = " + collectedStats.get(i).getDistance()
                    + "\n");
            str.append("\t WAIT time = " + collectedStats.get(i).getWaitTime()
                    + "\n");
            str.append("\t cost = " + collectedStats.get(i).getCost() + "\n");
        }

        str.append("***********************************************************************\n");
        str.append("Summary: \n");
        str.append("\t total distance = " + calculateDistanceSum() + "\n");
        str.append("\t total WAIT time = " + calculateWaitTime());
        str.append("\t total cost = " + calculateCost(null));

        return str.toString();
    }

    public double calculateDistanceSum() {

        double distanceSum;

        distanceSum = 0;

        for (int i : collectedStats.keySet()) {

            distanceSum += collectedStats.get(i).getDistance();
        }

        return distanceSum;
    }

    public double calculateCost(Point2D.Double depot) {
        double costSum = 0;
        for (int i : collectedStats.keySet()) {
            if (depot != null)
                costSum += collectedStats.get(i).getSchedule2()
                        .getDistance(depot);
            else
                costSum += collectedStats.get(i).getCost();
        }

        return costSum;
    }

    public double calculateWaitTime() {

        double waitTimeSum;

        waitTimeSum = 0;

        for (int i : collectedStats.keySet()) {

            waitTimeSum += collectedStats.get(i).getWaitTime();
        }

        return waitTimeSum;
    }

    public double calculateDriveTime() {
        double result = 0.0;
        for (int i : collectedStats.keySet())
            result += collectedStats.get(i).getDriveTime();
        return result;
    }

    public double calculatePunishment() {
        double result = 0.0;
        for (int i : collectedStats.keySet())
            result += collectedStats.get(i).getPunishment();
        return result;
    }
}
