package measure;

import algorithm.Schedule;
import jade.core.AID;

import java.util.Map;

public class StandardDeviationOfDistanceFromCurLocationToBaseFromUndeliveredCommissions
        extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {
        Measure measure = new Measure();
        Map<AID, Schedule> schedules;
        if (newSchedules == null)
            schedules = oldSchedules;
        else
            schedules = newSchedules;
        for (AID aid : schedules.keySet()) {
            measure.put(aid, MeasureHelper
                    .standardDeviationDistanceFromCurLocationToBase(
                            schedules.get(aid).getUndeliveredCommissions(
                                    info.getDepot(), timestamp), commissions,
                            info.getDepot(), schedules.get(aid)
                                    .getCurrentLocation()));
        }
        return measure;
    }

    @Override
    public String getName() {
        return "StandardDeviationOfDistanceFromCurLocationToBaseFromUndeliveredCommissions";
    }

}
