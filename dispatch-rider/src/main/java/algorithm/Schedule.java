package algorithm;

import dtp.commission.Commission;
import dtp.simulation.SimInfo;
import jade.core.AID;
import org.apache.log4j.Logger;
import punishment.PunishmentFunction;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

//TODO uzupelnic o uwzglednianie currentLocation w time!!!
public abstract class Schedule implements Serializable {

    private static Logger logger = Logger.getLogger(Schedule.class);

    protected List<Commission> commissions;
    protected List<Commission> originalCommissions;
    protected List<Boolean> types;
    protected Algorithm algorithm;
    protected double creationTime = 0.0;
    protected int currentTimestamp;
    protected Point2D.Double currentLocation;
    protected Commission currentCommission = null;
    private int currentCommissionNr = -1;

    private boolean refreshCurrentLocation = false;

    public Schedule(Algorithm algorithm) {
        this.algorithm = algorithm;
        commissions = new LinkedList<>();
        types = new LinkedList<>();
        originalCommissions = new LinkedList<>();
    }

    public Schedule(Algorithm algorithm, int currentCommission,
                    double creationTime) {
        this.creationTime = creationTime;
        this.currentCommissionNr = currentCommission;
        this.algorithm = algorithm;
        commissions = new LinkedList<>();
        types = new LinkedList<>();
        originalCommissions = new LinkedList<>();
    }

    public static Schedule copy(Schedule schedule) {

        Schedule result = schedule.createSchedule(schedule.getAlgorithm());
        for (int i = 0; i < schedule.size(); i++) {
            result.addCommission(Commission.copy(schedule.getCommission(i)),
                    schedule.isPickup(i));
        }
        for (Commission com : schedule.getOriginalCommissions())
            result.addOriginalCommission(Commission.copy(com));
        result.setAlgorithm(schedule.getAlgorithm());
        // result.setDistance(schedule.distance);
        result.currentTimestamp = schedule.currentTimestamp;
        result.currentCommissionNr = schedule.currentCommissionNr;
        result.creationTime = schedule.creationTime;
        result.setCurrentLocation(schedule.getCurrentLocation());
        result.currentCommission = schedule.currentCommission;
        result.refreshCurrentLocation = schedule.getRefreshCurrentLocation();
        result = schedule.copySpecificFields(result);
        return result;
    }

    public boolean getRefreshCurrentLocation() {
        return refreshCurrentLocation;
    }

    public void setRefreshCurrentLocation(boolean refresh) {
        refreshCurrentLocation = refresh;
    }

    public Commission getCurrentCommission() {
        return currentCommission;
    }

    public void setCurrentCommission(Commission currentCommission,
                                     Point2D.Double depot) {
        this.currentCommission = currentCommission;
    }

    public double getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(double creationTime) {
        this.creationTime = creationTime;
    }

    public int getCurrentCommissionNr() {
        return currentCommissionNr;
    }

    public List<Commission> getOriginalCommissions() {
        return originalCommissions;
    }

    public void addOriginalCommission(Commission com) {
        for (Commission c : originalCommissions)
            if (c.getID() == com.getID())
                return;
        this.originalCommissions.add(com);
    }


    public void addCommission(Commission com, boolean pickup) {
        commissions.add(Commission.copy(com));
        types.add(pickup);
    }

    public void addCommission(int index, Commission com, boolean pickup) {
        commissions.add(index, com);
        types.add(index, pickup);
    }

    public void removeCommission(Commission com) {

        int i = 0;
        while (i < commissions.size()) {
            if (commissions.get(i).getID() == com.getID()) {
                commissions.remove(i);
                types.remove(i);
                continue;
            }
            i++;
        }
        for (int j = 0; j < originalCommissions.size(); j++)
            if (originalCommissions.get(j).getID() == com.getID()) {
                originalCommissions.remove(j);
                break;
            }
    }

    public void removeCommission(int index) {
        List<Commission> tmp = new LinkedList<>();
        List<Boolean> typesTmp = new LinkedList<>();
        for (int i = 0; i < index; i++) {
            tmp.add(commissions.get(i));
            typesTmp.add(types.get(i));
        }
        for (int i = index + 1; i < commissions.size(); i++) {
            tmp.add(commissions.get(i));
            typesTmp.add(types.get(i));
        }
        commissions = tmp;
        types = typesTmp;
    }

    public boolean isPickup(int index) {
        return types.get(index);
    }

    public int size() {
        return commissions.size();
    }

    public List<Commission> getAllCommissions() {
        return commissions;
    }

    public List<Commission> getCommissions() {
        List<Commission> result = new LinkedList<>();
        for (int i = 0; i < commissions.size(); i++) {
            if (types.get(i))
                result.add(commissions.get(i));
        }
        return result;
    }

    public List<Commission> getUndeliveredCommissions(Point2D.Double depot,
                                                      int timestamp) {
        List<Commission> result = new LinkedList<>();
        int begin = getNextLocationId(depot, timestamp);
        for (int i = begin; i < commissions.size(); i++)
            if (types.get(i))
                result.add(commissions.get(i));
        return result;
    }

    public Commission getCommission(int index) {
        Commission result = commissions.get(index);
        if (types.get(index))
            result.setPickup(true);
        else
            result.setPickup(false);
        return result;
    }

    public double isLoadOK(double maxLoad) {
        double load = 0.0;
        Commission com;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                load += com.getLoad();
            } else {
                load -= com.getLoad();
            }
            if (load < 0 || load > maxLoad)
                return load;
        }
        return load;
    }

    public int getIndexOf(Commission com, boolean isPickup) {
        for (int i = 0; i < commissions.size(); i++) {
            if (commissions.get(i).getID() == com.getID()
                    && types.get(i) == isPickup)
                return i;
        }
        return -1;
    }

    // TODO - dynamic
    public Commission getWorstCommission(int timestamp, int STDepth,
                                         SimInfo info, String chooseWorstCommission) {
        int begin = getNextLocationId(info.getDepot(), timestamp);

        Commission worstCommission;
        List<Container> times = new LinkedList<>();
        Set<Commission> coms = new TreeSet<>();
        
        /*
         * Set containing commissions which at least pickup part was done
		 * This is same change as in BruteForceAlgorithm2 
		 * (there is a possibility that in dynamic problem pickup might have been done whereas delivery wasn't
		 * even though in previous code we didn't take that into account - look at for loop "from begin to size".
		 * If statement was added there)
		 */
        Set<Integer> doneIDs = new TreeSet<>();
        for (int i = 0; i < begin; i++) {
            doneIDs.add(commissions.get(i).getID());
        }

        for (int i = begin; i < size(); i++) {
            if (!doneIDs.contains(commissions.get(i).getID()))
                coms.add(commissions.get(i));
            else {
                logger.warn(commissions.get(i).getID());
            }

        }

        if (STDepth >= coms.size())
            return null;
        switch (chooseWorstCommission) {
            case "time":
                for (Commission com : coms) {
                    times.add(new Container(com,
                            calculateTime(info.getDepot(), com)));
                }
                break;
            case "wTime":
                for (Commission com : coms) {
                    times.add(new Container(com, waitTime(info.getDepot(), com)));
                }
                break;
            case "timeWithPunishment":
                for (Commission com : coms) {
                    times.add(new Container(com, calculateTimeWithPunishment(info,
                            com)));
                }
                break;
            case "distWithPunishment":
                for (Commission com : coms) {
                    times.add(new Container(com, calculateDistWithPunishment(info,
                            com)));
                }
                break;
            default:
                throw new IllegalArgumentException("Bad argument: "
                        + chooseWorstCommission);
        }

        Collections.sort(times);
        Collections.reverse(times);
        worstCommission = times.get(STDepth - 1).worstCommission;
        removeCommission(worstCommission);

        return worstCommission;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getNextLocationId(Point2D.Double depot, int timestamp) {
        if (timestamp == 0)
            return 0;
        Point2D.Double nextLocation;
        Point2D.Double currentLocation = depot;
        double time = creationTime;
        Commission com;
        double dist;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += dist;
                time += com.getPickUpServiceTime();
                if (time >= timestamp)
                    return i + 1;
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += dist;
                time += com.getDeliveryServiceTime();
                if (time >= timestamp)
                    return i + 1;
            }

            currentLocation = nextLocation;
        }
        return commissions.size();
    }

    public int getNextLocationPickupId(Point2D.Double depot, int timestamp) {
        if (timestamp == 0)
            return 0;
        Point2D.Double nextLocation;
        Point2D.Double currentLocation = depot;
        double time = creationTime;
        Commission com;
        Commission result = null;
        double dist;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += dist;
                time += com.getPickUpServiceTime();
                if (time >= timestamp) {
                    result = com;
                    break;
                }
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                dist = Helper.calculateDistance(currentLocation, nextLocation);
                if (time + dist < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += dist;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        if (result == null)
            return getCommissions().size();
        List<Commission> coms = getCommissions();
        for (int i = 0; i < getCommissions().size(); i++) {
            if (coms.get(i).getID() == result.getID())
                return i;
        }
        return getCommissions().size();
    }

    public Point2D.Double getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Point2D.Double currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double calculatePercentageOfLatency(SimInfo info) {
        this.beginTimeCalculating();
        double summaryLatency = 0.0;
        double time = creationTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        PunishmentFunction fun = info.getPunishmentFunction();
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getPickupTime2())
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getPickupTime2();
                        summaryLatency += latency;
                    }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getDeliveryTime2())
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getDeliveryTime2();
                        summaryLatency += latency;
                    }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        driveTime = calculateTime(currentLocation, info.getDepot(),
                info.getDepot());
        time += driveTime;
        if (time > info.getDeadline())
            return -1;
        return summaryLatency / time * 100;
    }

    public double calculateSummaryCost(SimInfo info) {
        this.beginTimeCalculating();
        double cost = 0.0;
        double time = creationTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        PunishmentFunction fun = info.getPunishmentFunction();
        for (int i = 0; i < size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getPickupTime2()) {
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getPickupTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, true);
                    }
                }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getDeliveryTime2()) {
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getDeliveryTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);
                    }
                }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        driveTime = calculateTime(currentLocation, info.getDepot(),
                info.getDepot());
        time += driveTime;
        if (time > info.getDeadline())
            return -1;
        return cost + time - creationTime;
    }

    public double calculateSummaryCostWithoutPredicted(SimInfo info) {
        this.beginTimeCalculating();
        double cost = 0.0;
        double time = creationTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        PunishmentFunction fun = info.getPunishmentFunction();
        for (int i = 0; i < size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                if (this.getClass().equals(GraphSchedule.class)) {
                    driveTime = ((GraphSchedule) this).calculateTime(
                            currentLocation, nextLocation, info.getDepot(),
                            false);
                } else {
                    driveTime = calculateTime(currentLocation, nextLocation,
                            info.getDepot());
                }
                if (time + driveTime > com.getPickupTime2()) {
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getPickupTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, true);
                    }
                }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                if (this.getClass().equals(GraphSchedule.class)) {
                    driveTime = ((GraphSchedule) this).calculateTime(
                            currentLocation, nextLocation, info.getDepot(),
                            false);
                } else {
                    driveTime = calculateTime(currentLocation, nextLocation,
                            info.getDepot());
                }
                if (time + driveTime > com.getDeliveryTime2()) {
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getDeliveryTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);
                    }
                }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        driveTime = calculateTime(currentLocation, info.getDepot(),
                info.getDepot());
        time += driveTime;
        // comment because of changable graph links
        // if (time > info.getDeadline())
        // return -1;
        return cost + time - creationTime;
    }

    public double calculateCost(SimInfo info) {
        this.beginTimeCalculating();
        double cost = 0.0;
        double time = creationTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        Double delayLimit = info.getDelayLimit();
        if (delayLimit != null)
            if (calculatePercentageOfLatency(info) > delayLimit) {
                return -1;
            }
        PunishmentFunction fun = info.getPunishmentFunction();
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                cost += driveTime;
                if (time + driveTime > com.getPickupTime2())
                    if (fun == null) {
                        return -1;
                    } else {
                        latency = time + driveTime - com.getPickupTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, true);
                    }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                cost += driveTime;
                if (time + driveTime > com.getDeliveryTime2())
                    if (fun == null) {
                        return -1;
                    } else {
                        latency = time + driveTime - com.getDeliveryTime2();
                        cost += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);
                    }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        /*driveTime = calculateTime(currentLocation, info.getDepot(),
                info.getDepot());
        time += driveTime;
        cost += driveTime;*/
        if (time > info.getDeadline()) {
            return -1;
        }
        return cost;
    }

    public double getDistance(Point2D.Double depot) {
        int begin = 0;
        // for(int i=0;i<size();i++) {
        // if(isPickup(i)) {
        // if(getCommission(i).getPickupTime1()>=currentTimestamp) {
        // begin=i;
        // break;
        // }
        // } else {
        // if(getCommission(i).getDeliveryTime1()>=currentTimestamp) {
        // begin=i;
        // break;
        // }
        // }
        // }
        double result = 0.0;// distance;
        Point2D.Double currentLocation = depot;
        for (int i = begin; i < size(); i++) {
            if (types.get(i)) {
                result += calculateDistance(currentLocation,
                        new Point2D.Double(commissions.get(i).getPickupX(),
                                commissions.get(i).getPickupY()));
                currentLocation = new Point2D.Double(commissions.get(i)
                        .getPickupX(), commissions.get(i).getPickupY());
            } else {
                result += calculateDistance(currentLocation,
                        new Point2D.Double(commissions.get(i).getDeliveryX(),
                                commissions.get(i).getDeliveryY()));
                currentLocation = new Point2D.Double(commissions.get(i)
                        .getDeliveryX(), commissions.get(i).getDeliveryY());
            }
        }
        result += calculateDistance(currentLocation, depot);
        return result;
    }

    // public double getDistance() {
    // return distance;
    // }

    public double calculateSummaryPunishment(SimInfo info) {
        return calculateSummaryPunishment(info, false);
    }

    public double calculateSummaryPunishment(SimInfo info, boolean summary) {
        this.beginTimeCalculating();
        if (info.getPunishmentFunction() == null)
            return 0.0;
        double result = 0.0;
        double time = creationTime;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        Double delayLimit = info.getDelayLimit();
        if (delayLimit != null)
            if (calculatePercentageOfLatency(info) > delayLimit)
                return -1;
        PunishmentFunction fun = info.getPunishmentFunction();
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getPickupTime2())
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getPickupTime2();
                        result += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, true);
                    }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getDeliveryTime2())
                    if (fun == null)
                        return -1;
                    else {
                        latency = time + driveTime - com.getDeliveryTime2();
                        result += fun.getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);
                    }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        if (!summary)
            if (time > info.getDeadline())
                return -1;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < commissions.size(); i++) {
            if (types.get(i))
                buffer.append("P ");
            else
                buffer.append("D ");
            buffer.append(commissions.get(i).toString());
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public double calculateTime(Point2D.Double depot) {
        return calculateTime(depot, creationTime);
    }

    public double calculateTime(Point2D.Double depot, double cTime) {
        this.beginTimeCalculating();
        double time = cTime;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                if (time + driveTime > com.getPickupTime2())
                    return -1;
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                if (time + driveTime > com.getDeliveryTime2())
                    return -1;
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }
            currentLocation = nextLocation;
        }
        time += calculateTime(currentLocation, depot, depot);
        return time;
    }

    public double calculateWaitTime(Point2D.Double depot) {
        this.beginTimeCalculating();
        double time = 0.0;
        double waitTime = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                if (this.getClass().equals(GraphSchedule.class)) {
                    driveTime = ((GraphSchedule) this).calculateTime(
                            currentLocation, nextLocation, depot, false);
                } else {
                    driveTime = calculateTime(currentLocation, nextLocation,
                            depot);
                }
                // if (time + driveTime > com.getPickupTime2())
                // return -1;
                if (time + driveTime < com.getPickupTime1()) {
                    waitTime += (com.getPickupTime1() - (time + driveTime));
                    time = com.getPickupTime1();
                } else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                if (this.getClass().equals(GraphSchedule.class)) {
                    driveTime = ((GraphSchedule) this).calculateTime(
                            currentLocation, nextLocation, depot, false);
                } else {
                    driveTime = calculateTime(currentLocation, nextLocation,
                            depot);
                }
                // if (time + driveTime > com.getDeliveryTime2())
                // return -1;
                if (time + driveTime < com.getDeliveryTime1()) {
                    waitTime += (com.getDeliveryTime1() - (time + driveTime));
                    time = com.getDeliveryTime1();
                } else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        return waitTime;
    }

    protected double calculateTime(Point2D.Double depot, Commission commission) {
        this.beginTimeCalculating();
        double time = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (com.getID() == commission.getID())
                continue;
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                // if (time + driveTime > com.getPickupTime2())
                // return -1;
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                // if (time + driveTime > com.getDeliveryTime2())
                // return -1;
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        time += calculateTime(currentLocation, depot, depot);
        return time;
    }

    protected double calculateTimeWithPunishment(SimInfo info,
                                                 Commission commission) {
        this.beginTimeCalculating();
        double time = 0.0;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        double cost = 0.0;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (com.getID() == commission.getID())
                continue;
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getPickupTime2()) {
                    if (info.getPunishmentFunction() == null)
                        return -1;
                    else {

                        latency = time + driveTime - com.getPickupTime2();
                        cost += info.getPunishmentFunction().getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);

                    }
                }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                if (time + driveTime > com.getDeliveryTime2())
                    if (info.getPunishmentFunction() == null)
                        return -1;
                    else {

                        latency = time + driveTime - com.getDeliveryTime2();
                        cost += info.getPunishmentFunction().getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);

                    }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        time += calculateTime(currentLocation, info.getDepot(), info.getDepot());
        return time + cost;
    }

    protected double calculateDistWithPunishment(SimInfo info,
                                                 Commission commission) {
        this.beginTimeCalculating();
        double time = 0.0;
        double summaryDist = 0.0;
        Point2D.Double currentLocation = info.getDepot();
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        double latency;
        double dist;
        double cost = 0.0;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (com.getID() == commission.getID())
                continue;
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                dist = calculateDistance(currentLocation, nextLocation);
                summaryDist += dist;
                if (time + driveTime > com.getPickupTime2()) {
                    if (info.getPunishmentFunction() == null)
                        return -1;
                    else {

                        latency = time + driveTime - com.getPickupTime2();
                        cost += info.getPunishmentFunction().getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);

                    }
                }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation,
                        info.getDepot());
                dist = calculateDistance(currentLocation, nextLocation);
                summaryDist += dist;
                if (time + driveTime > com.getDeliveryTime2())
                    if (info.getPunishmentFunction() == null)
                        return -1;
                    else {

                        latency = time + driveTime - com.getDeliveryTime2();
                        cost += info.getPunishmentFunction().getValue(
                                info.getDefaultPunishmentFunValues(), com,
                                latency, false);

                    }
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        summaryDist += calculateTime(currentLocation, info.getDepot(),
                info.getDepot());
        return summaryDist + cost;
    }

    public double getArrivalTime(int comId, Point2D.Double depot) {
        this.beginTimeCalculating();
        double time = creationTime;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                if (time + driveTime > com.getPickupTime2())
                    return -1;
                if (com.getPickUpId() == comId)
                    return time + driveTime;
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                if (com.getDeliveryId() == comId)
                    return time + driveTime;
                if (time + driveTime > com.getDeliveryTime2())
                    return -1;
                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        return -1;
    }

    protected double waitTime(Point2D.Double depot, Commission commission) {
        this.beginTimeCalculating();
        double time = 0.0;
        double waitTime = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);
            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                // if (time + driveTime > com.getPickupTime2())
                // return -1;
                if (time + driveTime < com.getPickupTime1()) {
                    if (commission.getID() == com.getID())
                        waitTime += com.getPickupTime1() - (time + driveTime);
                    time = com.getPickupTime1();
                } else
                    time += driveTime;
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot);
                // if (time + driveTime > com.getDeliveryTime2())
                // return -1;
                if (time + driveTime < com.getDeliveryTime1()) {
                    if (commission.getID() == com.getID())
                        waitTime += com.getDeliveryTime1() - (time + driveTime);
                    time = com.getDeliveryTime1();
                } else
                    time += driveTime;
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        return waitTime;
    }

    protected void beginTimeCalculating() {
    }

    public abstract double calculateDriveTime(SimInfo info);

    public abstract Schedule createSchedule(Algorithm algorithm);

    public abstract Schedule createSchedule(Algorithm algorithm,
                                            int currentCommission, double creationTime);

    protected abstract Schedule copySpecificFields(Schedule result);

    protected abstract double calculateDistance(Point2D.Double point1,
                                                Point2D.Double point2);

    protected abstract double calculateTime(Point2D.Double point1,
                                            Point2D.Double point2, Point2D.Double depot);

    public abstract void updateCurrentLocation(int timestamp,
                                               Point2D.Double depot, AID aid);

    public abstract void initSchedule(Schedule schedule);

    private class Container implements Comparable<Container> {
        public Commission worstCommission;
        public double time;

        public Container(Commission com, double time) {
            this.worstCommission = com;
            this.time = time;
        }

        public int compareTo(Container c) {
            return java.lang.Double.compare(time, c.time);
        }
    }
}
