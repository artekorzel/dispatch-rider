package measure.calculator;

import algorithm.Schedule;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureHelper;

import java.util.Map;

public class StandardDeviationOfLoadFromUndeliveredCommissions extends
        MeasureCalculator {



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
                    .standardDeviationOfLoadFromCommissions(
                            schedules.get(aid).getUndeliveredCommissions(
                                    info.getDepot(), timestamp), commissions));
        }

        return measure;
    }

    @Override
    public String getName() {
        return "StandardDeviationOfLoadFromUndeliveredCommissions";
    }

}
