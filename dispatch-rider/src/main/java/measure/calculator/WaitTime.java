package measure.calculator;

import algorithm.Schedule;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;

import java.util.Map;

public class WaitTime extends MeasureCalculator {

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
            result.put(aid,
                    schedules.get(aid).calculateWaitTime(info.getDepot()));
        return result;
    }

    @Override
    public String getName() {
        return "WaitTime";
    }

}
