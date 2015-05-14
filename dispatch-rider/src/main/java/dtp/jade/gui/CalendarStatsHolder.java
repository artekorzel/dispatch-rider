package dtp.jade.gui;

import dtp.jade.agentcalendar.CalendarStats;
import dtp.util.AgentIDResolver;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;

public class CalendarStatsHolder implements Serializable {

    private HashMap<Integer, CalendarStats> collectedStats;
    private int calendarStatsNumber;
    private int collectedCalendarStatsNumber;

    public CalendarStatsHolder() {
    }

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

    public HashMap<Integer, CalendarStats> getCollectedStats() {
        return collectedStats;
    }

    public void setCollectedStats(HashMap<Integer, CalendarStats> collectedStats) {
        this.collectedStats = collectedStats;
    }

    public int getCalendarStatsNumber() {
        return calendarStatsNumber;
    }

    public void setCalendarStatsNumber(int calendarStatsNumber) {
        this.calendarStatsNumber = calendarStatsNumber;
    }

    public int getCollectedCalendarStatsNumber() {
        return collectedCalendarStatsNumber;
    }

    public void setCollectedCalendarStatsNumber(int collectedCalendarStatsNumber) {
        this.collectedCalendarStatsNumber = collectedCalendarStatsNumber;
    }
}
