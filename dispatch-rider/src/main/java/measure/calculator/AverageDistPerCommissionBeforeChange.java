package measure.calculator;

import algorithm.Schedule;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureHelper;

import java.util.Map;

public class AverageDistPerCommissionBeforeChange extends MeasureCalculator {

    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {

        return MeasureHelper
                .averageDistToCarryOneCommission(oldSchedules, info);
    }
}
