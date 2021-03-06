package measure.calculator;

import algorithm.Schedule;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureHelper;

import java.util.Map;

public class AverageTimeWindowsSizeForAllCommissions extends MeasureCalculator {

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
            measure.put(aid, MeasureHelper.averageTimeWindowSize(
                    schedules.get(aid).getCommissions(), commissions));
        }
        return measure;
    }
}
