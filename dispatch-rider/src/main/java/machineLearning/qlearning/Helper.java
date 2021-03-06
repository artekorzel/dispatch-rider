package machineLearning.qlearning;

import algorithm.Schedule;
import dtp.simulation.SimInfo;
import jade.core.AID;

import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static Map<String, Double> getParameters(
            Map<AID, Schedule> oldSchedule, Map<AID, Schedule> newSchedule,
            SimInfo info, int timestamp) {
        Map<AID, Schedule> schedules;
        if (newSchedule != null)
            schedules = newSchedule;
        else
            schedules = oldSchedule;

        Map<String, Double> params = new HashMap<>();
        params.put("holonsCount", (double) schedules.size());
        Double dist = 0.0;
        Double commissions = 0.0;
        Double cost = 0.0;
        Double timeFromCreationOfLastUnit = Double.MAX_VALUE;
        for (Schedule schedule : schedules.values()) {
            dist += schedule.getDistance(info.getDepot());
            commissions += schedule.size();
            cost += schedule.calculateCost(info);
            if (timestamp - schedule.getCreationTime() < timeFromCreationOfLastUnit) {
                timeFromCreationOfLastUnit = timestamp
                        - schedule.getCreationTime();
            }
        }
        params.put("dist", dist);
        params.put("commissions", commissions);
        params.put("costOfCommission", cost / commissions);
        params.put("timeFromCreationOfLastUnit", timeFromCreationOfLastUnit);
        return params;
    }

    public static Map<AID, Map<String, Double>> getHolonParameters(
            Map<AID, Schedule> oldSchedule, Map<AID, Schedule> newSchedule,
            SimInfo info, int timestamp) {
        Map<AID, Schedule> schedules;
        if (newSchedule != null)
            schedules = newSchedule;
        else
            schedules = oldSchedule;
        Map<AID, Map<String, Double>> holonParams = new HashMap<>();
        Map<String, Double> globalParams = getParameters(oldSchedule,
                newSchedule, info, timestamp);
        Map<String, Double> params;
        Schedule schedule;
        for (AID aid : schedules.keySet()) {
            params = new HashMap<>(globalParams);
            schedule = schedules.get(aid);
            params.put("holonDist", schedule.getDistance(info.getDepot()));
            params.put("holonCommissions", (double) schedule.size());
            params.put("holonCostOfCommission", schedule.calculateCost(info)
                    / schedule.size());
            holonParams.put(aid, params);
        }
        return holonParams;
    }
}
