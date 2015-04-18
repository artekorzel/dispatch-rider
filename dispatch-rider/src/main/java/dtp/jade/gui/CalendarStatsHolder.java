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
        collectedStats = new HashMap<>();
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
        return collectedCalendarStatsNumber == calendarStatsNumber;
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

            str.append(collectedStats.get(i).getAID().getLocalName()).append(": \n");
            str.append("truck: ").append(collectedStats.get(i).getTruckAID().getLocalName()).append(" trailer: ").append(collectedStats.get(i).getTrailerAID().getLocalName()).append(" capacity: ").append(collectedStats.get(i).getCapacity()).append("\n");
            str.append("\t distance = ").append(collectedStats.get(i).getDistance()).append("\n");
            str.append("\t WAIT time = ").append(collectedStats.get(i).getWaitTime()).append("\n");
            str.append("\t cost = ").append(collectedStats.get(i).getCost()).append("\n");
        }

        str.append("***********************************************************************\n");
        str.append("Summary: \n");
        str.append("\t total distance = ").append(calculateDistanceSum()).append("\n");
        str.append("\t total WAIT time = ").append(calculateWaitTime());
        str.append("\t total cost = ").append(calculateCost(null));

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
