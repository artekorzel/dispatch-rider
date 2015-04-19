package algorithm;

import dtp.commission.Commission;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * This algorithm uses BruteForceAlgorithm. It differs only at the beginning. In
 * BruteForceAlgorithm new commission is being inserted to current schedule. In
 * this algorithm, every commissions are inserted once again (including new
 * commission)
 */
public class BruteForceAlgorithm2 implements Algorithm {
    private double maxLoad;
    private Point2D.Double depot;
    // private int deadline;
    private SimInfo simInfo;

    public BruteForceAlgorithm2(double maxLoad, SimInfo simInfo) {
        this.maxLoad = maxLoad;
        this.simInfo = simInfo;
        this.depot = simInfo.getDepot();
    }

    public BruteForceAlgorithm2() {
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }

    public void init(double maxLoad, SimInfo simInfo) {
        this.maxLoad = maxLoad;
        this.simInfo = simInfo;
        this.depot = simInfo.getDepot();
    }

    public Point2D.Double getDepot() {
        return depot;
    }

    /**
     * It works like makeSchedule in BruteForceAlgorithm
     * currentLocation - not used!!
     */
    private Schedule addCommissionToSchedule(Commission commissionToAdd,
                                             Schedule currentSchedule,
                                             int timestamp) {

        Schedule schedule = BruteForceAlgorithm.copyScheduleContent(currentSchedule, simInfo);

        double load;

        int bestIndex = -1;
        int bestIndex2 = -1;
        // double bestDistance = Double.MAX_VALUE;
        // double tmpDistance;
        double bestCost = Double.MAX_VALUE;
        double tmpCost;

        int begin = currentSchedule.getNextLocationId(depot, timestamp);
        for (int i = begin; i < schedule.size(); i++) {
            schedule.addCommission(i, commissionToAdd, true);
            for (int j = i + 1; j < schedule.size(); j++) {
                schedule.addCommission(j, commissionToAdd, false);
                // tmpDistance = schedule.getDistance(depot);
                tmpCost = schedule.calculateCost(simInfo);

                load = schedule.isLoadOK(maxLoad);
                // if (tmpDistance < bestDistance
                // && schedule.calculateTime(depot) <= deadline
                // && schedule.calculateTime(depot) > 0 && load == 0.0) {
                // bestDistance = tmpDistance;
                if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
                    bestCost = tmpCost;
                    bestIndex = i;
                    bestIndex2 = j;
                }
                schedule.removeCommission(j);
            }
            schedule.addCommission(commissionToAdd, false);
            // tmpDistance = schedule.getDistance(depot);
            tmpCost = schedule.calculateCost(simInfo);
            load = schedule.isLoadOK(maxLoad);
            // if (tmpDistance < bestDistance
            // && schedule.calculateTime(depot) <= deadline
            // && schedule.calculateTime(depot) > 0 && load == 0.0) {
            // bestDistance = tmpDistance;
            if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
                bestCost = tmpCost;
                bestIndex = i;
                bestIndex2 = -2;
            }
            schedule.removeCommission(schedule.size() - 1);
            schedule.removeCommission(i);
        }

        schedule.addCommission(commissionToAdd, true);
        schedule.addCommission(commissionToAdd, false);
        // tmpDistance = schedule.getDistance(depot);
        tmpCost = schedule.calculateCost(simInfo);

        load = schedule.isLoadOK(maxLoad);
        // if (tmpDistance < bestDistance
        // && schedule.calculateTime(depot) <= deadline
        // && schedule.calculateTime(depot) > 0 && load == 0.0) {
        if (tmpCost < bestCost && tmpCost > 0 && load > -0.0001 && load < 0.0001) {
            //System.err.println(" " + tmpCost);
            return schedule;
        }

        schedule.removeCommission(schedule.size() - 1);
        schedule.removeCommission(schedule.size() - 1);

        if (bestIndex == -1 || bestIndex2 == -1)
            return null;
        schedule.addCommission(bestIndex, commissionToAdd, true);
        if (bestIndex2 == -2)
            schedule.addCommission(commissionToAdd, false);
        else
            schedule.addCommission(bestIndex2, commissionToAdd, false);

        //System.err.println(" " + bestCost);
        return schedule;

    }

    /**
     * This function will be similar to addCommissionToSchedule but
     * with the small difference - we are adding only delivery since pickups are already carried out
     *
     * @return schedule with added delivery
     */
    private Schedule addDeliveryToSchedule(Commission commissionToAdd,
                                           Schedule currentSchedule, int timestamp) {

        Schedule schedule = BruteForceAlgorithm.copyScheduleContent(currentSchedule, simInfo);

        //index where the delivery should be put
        int bestIndex = -1;
        double bestCost = Double.MAX_VALUE;
        double tempCost;

        int begin = currentSchedule.getNextLocationId(depot, timestamp);

        for (int i = begin; i < schedule.size() + 1; i++) {
            schedule.addCommission(i, commissionToAdd, false);

            tempCost = schedule.calculateCost(simInfo);

            if (tempCost < bestCost && tempCost > 0) {
                bestCost = tempCost;
                bestIndex = i;
            }

            schedule.removeCommission(i);
        }

        if (bestIndex == -1) {
            return null;
        }
        schedule.addCommission(bestIndex, commissionToAdd, false);

        return schedule;
    }

    /*
     * Main method which insert commissions into schedule
     */
    public Schedule makeSchedule(Commission commissionToAdd, Point2D.Double currentLocation,
                                 Schedule currentSchedule, int timestamp) {

        if (timestamp == 10)
            System.err.println("$ " + currentSchedule.getCurrentLocation());

        Schedule schedule = simInfo.createSchedule(null);
        List<Commission> commissions = new LinkedList<>();

		/*
         * Lists containing ids, from current schedule, of pickups and deliveries
		 * So we can see if there are deliveries without pickups
		 * And a map ID -> Commission so that its more efficient
		 */
        List<Integer> pickupIDs = new LinkedList<>();
        List<Integer> deliveryIDs = new LinkedList<>();
        Map<Integer, Commission> idToCommission = new HashMap<>();

        if (currentSchedule != null) {
			/*
			 * We calculate from which index, we can do changes in schedule
			 */
            schedule = simInfo.createSchedule(currentSchedule.getAlgorithm());
            schedule.initSchedule(currentSchedule);

            int begin = currentSchedule.getNextLocationId(depot, timestamp);
            Commission com;
            for (int i = 0; i < begin; i++) {
                com = currentSchedule.getCommission(i);
                schedule.addCommission(com, currentSchedule.isPickup(i));
            }

			/*
			 * Commissions which will be inserting are being stored in
			 * commissions list
			 */
            for (int i = begin; i < currentSchedule.size(); i++) {
                Commission commission = currentSchedule.getCommission(i);
                idToCommission.put(commission.getID(), Commission.copy(commission));

                if (currentSchedule.isPickup(i)) {
                    pickupIDs.add(commission.getID());
                    commissions.add(Commission.copy(commission));
                    schedule.addOriginalCommission(Commission.copy(commission));
                } else {
                    deliveryIDs.add(commission.getID());
                }
            }
        }

        commissions.add(commissionToAdd);
        schedule.addOriginalCommission(commissionToAdd);
        Schedule tmpSchedule;

        //so we got the deliveries without pickups
        deliveryIDs.removeAll(pickupIDs);
		
		/*
		 * First we're adding deliveries without pickups to the schedule
		 */
        if (deliveryIDs.size() > 0) {
            for (Integer i : deliveryIDs) {
                tmpSchedule = addDeliveryToSchedule(idToCommission.get(i), schedule, timestamp);
                if (tmpSchedule == null) {
                    System.err.println("EUnit did pickup the cargo but can't fit the delivery(" + i + ") in his schedule!");
                    return null;
                }
                schedule = tmpSchedule;
            }
        }
		
		/*
		 * We can sort commissions before inserting them
		 */
        sortCommissions(commissions);
			
		/*
		 * then we try to assign rest commissions (which 100% has pickup and delivery) to schedule
		 */
        for (Commission com : commissions) {
            tmpSchedule = addCommissionToSchedule(com, schedule, timestamp);
            if (tmpSchedule == null) {
                return null;
            }
            schedule = tmpSchedule;
        }

        return schedule;
    }

    /*
     * TODO weird results for 101lrc
     * Should change this code so that SimInfo contains object implementing interface with 2 methods :
     * sort and compare. And here there'd be only call obj.sort(commissions).
     * To do this one need to make classes xxxComparator to implement this interface (it must be made)
     * and solve compatibility problems in classes like ComparatorTest. One could also think whether to use
     * ComparatorType or newly implemented enum in TestConfiguration (line 62) - Brute2Sorter
     */
    private void sortCommissions(List<Commission> commissions) {
        switch (simInfo.getBrute2Sorter()) {
            case TIME:
                Collections.sort(commissions, new TimeComparator(true));
                break;
            case DISTANCE:
                Collections.sort(commissions, new DistanceComparator(true, depot));
                break;
            case WORST_DISTANCE_FIRST:
                Collections.sort(commissions, new DistanceComparator(true, depot));
                if (commissions.size() > 0) {
                    Commission c = commissions.get(commissions.size() - 1);
                    commissions.remove(commissions.size() - 1);
                    commissions.add(0, c);
                }
                break;
            case NONE:
                //just to show there's such option
                break;
        }
        //Collections.sort(commissions, new CommissionsComparator(depot));
        //Collections.reverse(commissions);
    }

    public void setMaxLoad(double maxLoad) {
        this.maxLoad = maxLoad;
    }
}
