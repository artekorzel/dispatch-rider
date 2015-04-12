package algorithm;

import dtp.commission.Commission;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;

/**
 * This algorithm uses full review of possibilities of insertion commissions.
 * Each commission part (pickup and delivery) is insert in any possible place in
 * schedule, then the best combination is chosen.
 */
public class BruteForceAlgorithm implements Algorithm {
    private double maxLoad;
    private Point2D.Double depot;
    // private int deadline;
    private SimInfo simInfo;


    /**
     * @param maxLoad - maksymalny zaladunek dla naszego pojazdu
     * @param simInfo - informacje o symulacji (dlaczego nie zmienna statyczna? po co przekazywac przez parametr za kazdym razem)
     */
    public BruteForceAlgorithm(double maxLoad, SimInfo simInfo) {
        this.maxLoad = maxLoad;
        this.simInfo = simInfo;
        depot = simInfo.getDepot();
        // deadline = (int) simInfo.getDeadline();
    }

    /**
     * Jezeli juz z tego korzystamy to *musimy* uzyc nastepnie metody init
     */
    public BruteForceAlgorithm() {
    }

    /**
     * @param currentSchedule
     * @param simInfo
     * @return Copied currentSchedule. Things which are copied : all commissions, originalCommissions,
     * algorithm, currentCommissionNr, creationTime.
     * Schedule is also initialized.
     */
    protected static Schedule copyScheduleContent(Schedule currentSchedule, SimInfo simInfo) {
        Schedule schedule = simInfo.createSchedule(null);

        if (currentSchedule != null) {
            schedule = simInfo.createSchedule(currentSchedule.getAlgorithm(),
                    currentSchedule.getCurrentCommissionNr(),
                    currentSchedule.getCreationTime());
            schedule.initSchedule(currentSchedule);
            for (int i = 0; i < currentSchedule.size(); i++) {
                schedule.addCommission(
                        Commission.copy(currentSchedule.getCommission(i)),
                        currentSchedule.getCommission(i).isPickup());
                schedule.addOriginalCommission(currentSchedule.getCommission(i));
            }
        }

        return schedule;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }

    public Point2D.Double getDepot() {
        return depot;
    }

    public void init(double maxLoad, SimInfo simInfo) {
        this.maxLoad = maxLoad;
        this.simInfo = simInfo;
        depot = simInfo.getDepot();
        // deadline = (int) simInfo.getDeadline();
    }

    /**
     * currentLocation - NOT USED!!
     */
    public Schedule makeSchedule(Commission commissionToAdd,
                                 Point2D.Double currentLocation, Schedule currentSchedule,
                                 int timestamp) {

        Schedule schedule = BruteForceAlgorithm.copyScheduleContent(currentSchedule, simInfo);

        double load;

        /*
         * bestIndex - index where we should insert pickup
         * bestIndex2 - index where we should insert delivery
         */
        int bestIndex = -1;
        int bestIndex2 = -1;
        // double bestDist = Double.MAX_VALUE;
        // double tmpDistance;
        double bestCost = Double.MAX_VALUE;
        double tmpCost;

        /*
         * We get index of first commission, which can be change. It is
         * necessary, because of dynamic problem, where commission has 3 states:
         * already realized, during realization and to realize. Only commission
         * with status 'to realize' can be moved. Other commissions can't be
         * moved and any commission can be insert between them.
         */
        int begin = currentSchedule.getNextLocationId(depot, timestamp);

        if (begin != 0 && timestamp == 0) {
            System.out.println("[BruteForceAlgorithm] begin " + begin);
            System.exit(0);
        }

        /*
         * We check every possibility of insert pickup and delivery. The best
         * combination is determined by distance, which unit will have to travel
         * (here you can also use time of realization all commissions, by we
         * chose distance, because after running some tests, it was giving
         * better results)
         */
        for (int i = begin; i < schedule.size(); i++) {
            schedule.addCommission(i, commissionToAdd, true);
            for (int j = i + 1; j < schedule.size(); j++) {
                schedule.addCommission(j, commissionToAdd, false);
                // tmpDistance = schedule.getDistance(depot);
                tmpCost = schedule.calculateCost(simInfo);
                load = schedule.isLoadOK(maxLoad);
                /*
                 * Lines like that is for check if new schedule meets time and
                 * load restrictions
                 */
                // if (tmpDistance < bestDist
                // && schedule.calculateTime(depot) <= deadline
                // && schedule.calculateTime(depot) > 0 && load == 0.0) {
                if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
                    // bestDist = tmpDistance;
                    bestCost = tmpCost;
                    bestIndex = i;
                    bestIndex2 = j;
                }
                schedule.removeCommission(j);
            }
            // i jeszcze przypadek dodawania delivery na sam koniec
            schedule.addCommission(commissionToAdd, false);
            // tmpDistance = schedule.getDistance(depot);
            tmpCost = schedule.calculateCost(simInfo);
            load = schedule.isLoadOK(maxLoad);
            // if (tmpDistance < bestDist
            // && schedule.calculateTime(depot) <= deadline
            // && schedule.calculateTime(depot) > 0 && load == 0.0) {
            // bestDist = tmpDistance;
            if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
                bestCost = tmpCost;
                bestIndex = i;
                bestIndex2 = -2;
            }
            schedule.removeCommission(schedule.size() - 1);
            schedule.removeCommission(i);
        }

        //no i jeszcze jeden przypadek, dodawania na sam koniec kolejno pickup i delivery
        schedule.addCommission(commissionToAdd, true);
        schedule.addCommission(commissionToAdd, false);
        // tmpDistance = schedule.getDistance(depot);
        tmpCost = schedule.calculateCost(simInfo);

        load = schedule.isLoadOK(maxLoad);
        // if (tmpDistance < bestDist && schedule.calculateTime(depot) <=
        // deadline
        // && schedule.calculateTime(depot) > 0 && load == 0.0) {
        if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
            return schedule;
        }

        schedule.removeCommission(schedule.size() - 1);
        schedule.removeCommission(schedule.size() - 1);

        /*
         * Creation of optimal schedule
         */
        if (bestIndex == -1 || bestIndex2 == -1) {
            return null;
        }
        schedule.addCommission(bestIndex, commissionToAdd, true);
        if (bestIndex2 == -2)
            schedule.addCommission(commissionToAdd, false);
        else
            schedule.addCommission(bestIndex2, commissionToAdd, false);
        return schedule;
    }

    public void setMaxLoad(double maxLoad) {
        this.maxLoad = maxLoad;
    }
}
