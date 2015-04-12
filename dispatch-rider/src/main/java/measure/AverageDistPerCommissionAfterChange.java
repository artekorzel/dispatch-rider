package measure;

import algorithm.Schedule;
import jade.core.AID;

import java.util.Map;

public class AverageDistPerCommissionAfterChange extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {

        return MeasureHelper
                .averageDistToCarryOneCommission(newSchedules, info);
    }

    @Override
    public String getName() {
        return "AverageDistPerCommissionBeforeChange";
    }

}
