package measure.calculator;

import algorithm.Schedule;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;

import java.util.Map;

public class NumberOfCommissions extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {

        Measure result = new Measure();
        Map<AID, Schedule> schedules;
        if (newSchedules != null)
            schedules = newSchedules;
        else
            schedules = oldSchedules;
        for (AID aid : schedules.keySet())
            result.put(aid, new Double(schedules.get(aid).size() / 2));
        return result;
    }

    @Override
    public String getName() {
        return "NumberOfCommissions";
    }

}
