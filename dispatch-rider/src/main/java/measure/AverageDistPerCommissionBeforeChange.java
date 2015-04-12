package measure;

import algorithm.Schedule;
import jade.core.AID;

import java.util.Map;

public class AverageDistPerCommissionBeforeChange extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {

        return MeasureHelper
                .averageDistToCarryOneCommission(oldSchedules, info);
    }

    @Override
    public String getName() {
        return "AverageDistPerCommissionBeforeChange";
    }

}
