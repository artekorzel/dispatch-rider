package algorithm.simulatedTrading;

import algorithm.Algorithm;
import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.simulation.SimInfo;
import jade.core.AID;

import java.util.*;

/**
 * This class implements more complex simulated trading. In this approach units
 * declare that they can carry commission from other unit if someone carry one
 * of they commission. You can see that there is two methods with the same
 * names. The one which return Map<Integer,Schedule> is used in tests
 * (algorithm.AlgorithmTest, algorithm.DynamicAlgorithmTest) and the 2nd one is
 * used in system.
 */
public class SimulatedTrading {

    /*
     * This is implementation of simple simulated trading algorithm. It is based
     * on moving commissions between units.
     */
    public static Map<AID, Schedule> fullSimulatedTrading(Map<AID, Schedule> holons, AID holon, int STDepth, SimInfo info,
                                                          Set<Integer> commissionsId, String chooseWorstCommission,
                                                          int timestamp) {
        Schedule schedule = holons.get(holon);
        Schedule backup = Schedule.copy(schedule);
        /*
         * Getting worst commission - commission which current unit wants to
         * remove from his calendar. If STDepth reaches commissions count, null
         * value is return.
         */
        Commission worstCommission = schedule.getWorstCommission(0, STDepth,
                info, chooseWorstCommission);
        if (worstCommission == null)
            return holons;
        /*
         * This prevent for loops, when the same commission is moved between the
         * same units.
         */
        if (commissionsId.contains(worstCommission.getID())) {
            holons.put(holon, backup);
            return holons;
        } else
            commissionsId.add(worstCommission.getID());
        double bestCost = Double.MAX_VALUE;
        AID bestHolon = null;
        boolean added = false;
        Schedule tmpSchedule;
        Schedule bestSchedule = null;
        Algorithm algorithm;
        // double extraDistance;
        double extraCost;
        /*
         * We search for unit which will carry worsCommission with best (min)
         * cost
         */
        for (AID key : holons.keySet()) {
            schedule = holons.get(key);
            algorithm = schedule.getAlgorithm();
            tmpSchedule = algorithm
                    .makeSchedule(Commission.copy(worstCommission), null,
                            schedule, timestamp);
            if (tmpSchedule != null) {
                tmpSchedule.setAlgorithm(algorithm);
                // extraDistance = tmpSchedule.getDistance(depot)
                // - schedule.getDistance(depot);
                extraCost = tmpSchedule.calculateCost(algorithm.getSimInfo())
                        - schedule.calculateCost(algorithm.getSimInfo());
                // if (bestCost > Helper.getRatio(extraDistance,
                // worstCommission)) {
                // bestCost = Helper.getRatio(extraDistance, worstCommission);
                if (bestCost > extraCost && extraCost > 0) {
                    bestCost = extraCost;
                    bestHolon = key;
                    bestSchedule = tmpSchedule;
                }
                if (bestSchedule != null)
                    added = true;
            }
        }
        if (added) {
            holons.put(bestHolon, bestSchedule);
        } else {
            // System.err.println("fatal error (new ST)");
            // System.exit(0);
            holons.put(holon, backup);
            return holons;
        }

        /*
         * It is possible, that there is no other unit, which can carry current
         * unit worstCommission, with better cost. In this situation there is
         * send next worstCommission. If there is unit, which carry
         * worstCommission, the procedure is begun for it.
         */
        if (bestHolon == holon) {
            return fullSimulatedTrading(holons, bestHolon, STDepth + 1,
                    info, commissionsId, chooseWorstCommission, timestamp);
        }
        return fullSimulatedTrading(holons, bestHolon, 1, info,
                commissionsId, chooseWorstCommission, timestamp);
    }

    public static List<Container> getCommissionsToReplace(
            Commission commission, Schedule schedule, Algorithm algorithm,
            int timestamp) {
        List<Container> result = new LinkedList<>();
        Schedule backup = Schedule.copy(schedule);
        Schedule tmp;
        Commission com;
        double cost;
        for (int i = backup.getNextLocationPickupId(algorithm.getDepot(),
                timestamp); i < backup.getCommissions().size(); i++) {
            com = backup.getCommissions().get(i);
            schedule.removeCommission(com);
            tmp = algorithm.makeSchedule(Commission.copy(commission), null,
                    schedule, timestamp);
            if (tmp != null) {
                cost = tmp.calculateCost(algorithm.getSimInfo());
                if (cost > 0)
                    result.add(new Container(cost, com));
            }
            // result.add(new Container(tmp.getDistance(algorithm.getDepot()),
            // com));// tmp.calculateTime(algorithm.getDepot()),com));
            schedule = Schedule.copy(backup);
        }
        /*
         * Commissions are sorted by cost
         */
        Collections.sort(result);
        return result;
    }

    /*
     * Used only for testing. Not used in system
     */
    public static Map<Integer, Schedule> simulatedTrading(
            Map<Integer, Schedule> holons, Commission com, Algorithm algorithm,
            int timestamp) {
        Schedule schedule;
        Schedule tmp;
        for (int i = 0; i < holons.size(); i++) {
            schedule = holons.get(i);
            List<Container> commissions = getCommissionsToReplace(com,
                    Schedule.copy(schedule), algorithm, timestamp);
            for (Container comToReplace : commissions) {
                for (int j = 0; j < holons.size(); j++) {
                    if (i == j)
                        continue;
                    schedule = holons.get(j);
                    tmp = algorithm.makeSchedule(
                            Commission.copy(comToReplace.commission), null,
                            schedule, timestamp);
                    if (tmp != null) {
                        holons.put(j, tmp);
                        schedule = holons.get(i);
                        schedule.removeCommission(Commission
                                .copy(comToReplace.commission));
                        schedule = algorithm.makeSchedule(Commission.copy(com),
                                null, schedule, timestamp);
                        if (schedule != null) {
                            holons.put(i, schedule);
                            return holons;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Map<AID, Schedule> complexSimmulatedTrading(Set<AID> aids,
                                                              Map<AID, Schedule> holons, Commission com, int depth,
                                                              Set<Integer> comsId, int timestamp, SimInfo info,
                                                              boolean firstComplexSTResultOnly) {
        return complexSimmulatedTrading(aids, holons, com, depth, comsId,
                timestamp, info, null, Double.MAX_VALUE,
                firstComplexSTResultOnly);
    }

    public static Map<AID, Schedule> complexSimmulatedTrading(Set<AID> aids,
                                                              Map<AID, Schedule> holons, Commission com, int depth,
                                                              Set<Integer> comsId, int timestamp, SimInfo info,
                                                              Map<AID, Schedule> bestResult, Double bestResultCost,
                                                              boolean firstComplexSTResultOnly) {
        /*
         * We have to limit number of negotiations, because if we don't do that,
         * there is great possibility of loops
         */
        if (depth == 0) {
            return bestResult;
            // return null;
        }
        Schedule schedule;
        Schedule tmp;
        Schedule scheduleBackup;
        Map<AID, Schedule> holonsBackup = Helper.copyAID(holons);
        Map<AID, Schedule> holonsTmp;
        Algorithm algorithm;
        /*
         * We search for unit which will get commission from other unit
         */
        for (AID i : aids) {
            holons = Helper.copyAID(holonsBackup);
            schedule = holons.get(i);
            scheduleBackup = Schedule.copy(schedule);
            algorithm = schedule.getAlgorithm();
            tmp = algorithm.makeSchedule(Commission.copy(com), null,
                    Schedule.copy(schedule), timestamp);

            /*
             * If unit can carry commission, then it is added to his calendar
             * and algorithm finishes
             */
            if (tmp != null) {
                tmp.setAlgorithm(algorithm);
                holons.put(i, tmp);
                if (firstComplexSTResultOnly)
                    return holons;
                double cost = calculateSummaryCost(holons, info);
                if (cost < bestResultCost) {
                    bestResult = holons;
                    bestResultCost = cost;
                }
                // return holons;
            }

            /*
             * We get all possible commissions, which we can replace
             */
            List<Container> commissions = getCommissionsToReplace(com,
                    Schedule.copy(schedule), algorithm, timestamp);
            /*
             * We start negotiation phase
             */
            for (Container comToReplace : commissions) {
                /*
                 * This prevents from loops
                 */
                if (comsId.contains(comToReplace.commission.getID()))
                    continue;
                else
                    comsId.add(comToReplace.commission.getID());
                if (com.getID() == comToReplace.commission.getID())
                    continue;
                holons = Helper.copyAID(holonsBackup);
                schedule = Schedule.copy(scheduleBackup);
                schedule.removeCommission(Commission
                        .copy(comToReplace.commission));
                schedule = algorithm.makeSchedule(Commission.copy(com), null,
                        schedule, timestamp);
                if (schedule == null) {
                    System.err.println("complexST err");
                    System.exit(0);
                }
                holons.put(i, schedule);
                // for(int j=0;j<holons.size();j++) {
                // if(i==j) continue;
                /*
                 * Current unit asks others if someone could get his commission
                 * (then he can carry commission from other agent or new
                 * commission)
                 */
                holonsTmp = complexSimmulatedTrading(aids, holons,
                        Commission.copy(comToReplace.commission), depth - 1,
                        comsId, timestamp, info, bestResult, bestResultCost,
                        firstComplexSTResultOnly);
                if (holonsTmp != null) {
                    // return holonsTmp;
                    if (firstComplexSTResultOnly)
                        return holonsTmp;
                    double cost = calculateSummaryCost(holonsTmp, info);
                    if (cost < bestResultCost) {
                        bestResult = holonsTmp;
                        bestResultCost = cost;
                    }
                }
                // }
            }
        }
        return bestResult;
    }

    private static double calculateSummaryCost(Map<AID, Schedule> schedules,
                                               SimInfo info) {
        double result = 0.0;
        for (AID aid : schedules.keySet())
            result += schedules.get(aid).calculateSummaryCost(info);
        return result;
    }
}
