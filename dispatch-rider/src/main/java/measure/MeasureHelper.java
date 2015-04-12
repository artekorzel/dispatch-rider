package measure;

import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.simmulation.SimInfo;
import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MeasureHelper {
    public static Measure averageDistToCarryOneCommission(
            Map<AID, Schedule> schedules, SimInfo info) {
        Measure result = new Measure();
        if (schedules == null)
            return result;
        Schedule schedule;
        Point2D.Double currentLocation;
        Point2D.Double nextLocation;
        int comId;
        Commission com;
        double dist;
        for (AID aid : schedules.keySet()) {
            schedule = schedules.get(aid);
            currentLocation = schedule.getCurrentLocation();
            dist = 0.0;
            for (comId = 0; comId < schedule.size(); comId++) {
                com = schedule.getCommission(comId);
                if (schedule.isPickup(comId))
                    nextLocation = new Point2D.Double(com.getPickupX(),
                            com.getPickupY());
                else
                    nextLocation = new Point2D.Double(com.getDeliveryX(),
                            com.getDeliveryY());

                dist += Helper.calculateDistance(currentLocation, nextLocation);

                currentLocation = nextLocation;
            }

            dist += Helper.calculateDistance(currentLocation, info.getDepot());

            result.put(aid, dist / schedule.size());
        }
        return result;
    }

    public static Measure numberOfCommissionsWeCanAddToOthers(
            Map<AID, Schedule> schedules, int timestamp) {
        Measure result = new Measure();
        if (schedules == null)
            return result;
        Schedule schedule;
        double value;
        for (AID aid : schedules.keySet()) {
            value = 0.0;
            schedule = schedules.get(aid);
            for (Commission com : schedule.getCommissions()) {
                for (Schedule s : schedules.values()) {
                    if (s.equals(schedule))
                        continue;
                    if (s.getAlgorithm().makeSchedule(Commission.copy(com),
                            null, Schedule.copy(s), timestamp) != null) {
                        value++;
                        break;
                    }
                }
            }
            result.put(aid, value);
        }
        return result;
    }

    public static Measure numberOfCommissionsOthersCanAddToUs(
            Map<AID, Schedule> schedules, int timestamp) {
        Measure result = new Measure();
        if (schedules == null)
            return result;
        Schedule schedule;
        double value;
        for (AID aid : schedules.keySet()) {
            value = 0.0;
            schedule = schedules.get(aid);

            for (Schedule s : schedules.values()) {
                if (s.equals(schedule))
                    continue;
                for (Commission com : s.getCommissions()) {
                    if (schedule.getAlgorithm().makeSchedule(
                            Commission.copy(com), null,
                            Schedule.copy(schedule), timestamp) != null) {
                        value++;
                        break;
                    }
                }
            }
            result.put(aid, value);
        }
        return result;
    }

    public static double average(List<Double> values) {
        if (values.size() == 0)
            return 0.0;
        double result = 0.0;
        for (Double v : values)
            result += v;
        result /= values.size();
        return result;
    }

    public static double standardDeviation(List<Double> values) {
        double result = 0.0;
        double avg = average(values);
        for (Double v : values)
            result += Math.pow(v - avg, 2);
        result = Math.sqrt(result);
        return result;
    }

    private static List<Double> getLoadListFromCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        List<Double> values = new LinkedList<Double>();
        for (Commission com : commissionsFromSchedule) {
            values.add((double) com.getLoad());
        }
        for (Commission com : commissions) {
            values.add((double) com.getLoad());
        }
        return values;
    }

    public static double averageLoadFromCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {

        return average(getLoadListFromCommissions(commissionsFromSchedule,
                commissions));
    }

    public static double standardDeviationOfLoadFromCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {

        return standardDeviation(getLoadListFromCommissions(
                commissionsFromSchedule, commissions));
    }

    private static List<Double> getDistancesFromCurLocationToBase(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions, Point2D.Double depot,
            Point2D.Double curLocation) {
        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        double dist;
        for (Commission com : coms) {
            dist = 0.0;
            dist += Helper.calculateDistance(curLocation, new Point2D.Double(
                    com.getPickupX(), com.getPickupY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getPickupX(), com.getPickupY()),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()),
                    depot);
            values.add(dist);
        }
        return values;
    }

    public static double averageDistanceFromCurLocationToBase(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions, Point2D.Double depot,
            Point2D.Double curLocation) {
        return average(getDistancesFromCurLocationToBase(
                commissionsFromSchedule, commissions, depot, curLocation));
    }

    public static double standardDeviationDistanceFromCurLocationToBase(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions, Point2D.Double depot,
            Point2D.Double curLocation) {
        return standardDeviation(getDistancesFromCurLocationToBase(
                commissionsFromSchedule, commissions, depot, curLocation));
    }

    private static List<Double> getTimeWindowsSize(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        for (Commission com : coms) {
            values.add(com.getPickupTime2() - com.getPickupTime1());
            values.add(com.getDeliveryTime2() - com.getDeliveryTime1());
        }
        return values;
    }

    public static double averageTimeWindowSize(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        return average(getTimeWindowsSize(commissionsFromSchedule, commissions));
    }

    public static double standardDeviationFromTimeWindowSize(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        return standardDeviation(getTimeWindowsSize(commissionsFromSchedule,
                commissions));
    }

    private static Point2D.Double getNearestLocation(
            List<Commission> commissions, Point2D.Double location, int comId) {
        Point2D.Double nearestLocation = null;
        double bestDistance = Double.MAX_VALUE;
        double dist;
        for (Commission com : commissions) {
            dist = Helper.calculateDistance(location,
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));
            if (comId != com.getPickUpId() && dist < bestDistance) {
                bestDistance = dist;
                nearestLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
            }
            dist = Helper.calculateDistance(location,
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            if (comId != com.getDeliveryId() && dist < bestDistance) {
                bestDistance = dist;
                nearestLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
            }
        }
        return nearestLocation;
    }

    public static List<Double> getMinDistBetweenCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {

        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        Point2D.Double location;
        for (Commission com : coms) {
            location = new Point2D.Double(com.getPickupX(), com.getPickupY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(coms, location, com.getPickUpId())));
            location = new Point2D.Double(com.getDeliveryX(),
                    com.getDeliveryY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(coms, location, com.getDeliveryId())));
        }
        return values;
    }

    public static double averageMinDistBetweenCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        return average(getMinDistBetweenCommissions(commissionsFromSchedule,
                commissions));
    }

    public static double standardDeviationMinDistBetweenCommissions(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        return standardDeviation(getMinDistBetweenCommissions(
                commissionsFromSchedule, commissions));
    }

    public static double distCenterOfGravityFromHolon(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions, Point2D.Double curLocation) {
        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);

        List<Double> x = new LinkedList<Double>();
        List<Double> y = new LinkedList<Double>();
        for (Commission com : coms) {
            x.add(com.getPickupX());
            x.add(com.getDeliveryX());
            y.add(com.getPickupY());
            y.add(com.getDeliveryY());
        }
        return Helper.calculateDistance(curLocation, new Point2D.Double(
                average(x), average(y)));
    }

    private static double getCommissionsBetweenTimeWindow(
            List<Commission> commissions, double time1, double time2, int id) {
        int result = 0;
        for (Commission com : commissions) {
            if (com.getPickUpId() != id) {
                if (com.getPickupTime1() >= time1
                        && com.getPickupTime2() <= time2)
                    result++;
            }
            if (com.getDeliveryId() != id) {
                if (com.getDeliveryTime1() >= time1
                        && com.getDeliveryTime2() <= time2)
                    result++;
            }
        }
        return result;
    }

    public static double averagenNumberOfComsWithinTimeWin(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {

        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);

        List<Double> values = new LinkedList<Double>();
        for (Commission com : coms) {
            values.add(getCommissionsBetweenTimeWindow(coms,
                    com.getPickupTime1(), com.getPickupTime2(),
                    com.getPickUpId()));
            values.add(getCommissionsBetweenTimeWindow(coms,
                    com.getDeliveryTime1(), com.getDeliveryTime2(),
                    com.getDeliveryId()));
        }
        return average(values);
    }

    public static double averageMinTimeWindowsSize(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        for (Commission com : coms) {
            values.add(com.getDeliveryTime1() - com.getPickupTime2());
        }
        return average(values);
    }

    public static double averageMaxTimeWindowsSize(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions) {
        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        for (Commission com : coms) {
            values.add(com.getDeliveryTime2() - com.getPickupTime1());
        }
        return average(values);
    }

    public static double standardDeviationComsFromHolon(
            List<Commission> commissionsFromSchedule,
            List<Commission> commissions, Point2D.Double curLocation) {

        List<Commission> coms = new LinkedList<Commission>(
                commissionsFromSchedule);
        coms.addAll(commissions);
        List<Double> values = new LinkedList<Double>();
        for (Commission com : coms) {
            values.add(Helper.calculateDistance(curLocation,
                    new Point2D.Double(com.getPickupX(), com.getPickupY())));
            values.add(Helper.calculateDistance(curLocation,
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY())));
        }

        return standardDeviation(values);

    }

}
