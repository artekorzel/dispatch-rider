package measure.calculator;

import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class MaxWaitTime extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {
        Map<AID, Schedule> schedules;
        if (newSchedules == null)
            schedules = oldSchedules;
        else
            schedules = newSchedules;
        Measure result = new Measure();
        for (AID aid : schedules.keySet())
            result.put(aid, calculateMaxWaitTime(schedules.get(aid)));
        return result;
    }

    @Override
    public String getName() {
        return "MaxWaitTime";
    }

    private double calculateMaxWaitTime(Schedule schedule) {
        double time = 0.0;
        double maxWaitTime = 0.0;
        double waitTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double dist;
        List<Commission> commissions = schedule.getAllCommissions();
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (schedule.isPickup(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getPickupTime1()) {
                    waitTime = (com.getPickupTime1() - (time + dist));
                    if (waitTime > maxWaitTime)
                        maxWaitTime = waitTime;
                    time = com.getPickupTime1();
                } else
                    time += dist;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getDeliveryTime1()) {
                    waitTime = (com.getDeliveryTime1() - (time + dist));
                    if (waitTime > maxWaitTime)
                        maxWaitTime = waitTime;
                    time = com.getDeliveryTime1();
                } else
                    time += dist;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        return maxWaitTime;
    }

}
