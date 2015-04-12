package measure;

import algorithm.Schedule;
import jade.core.AID;

import java.util.Map;

public class NumberOfCommissionsWeCanAddToOthersAfterChanges extends
        MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {
        return MeasureHelper.numberOfCommissionsWeCanAddToOthers(newSchedules,
                timestamp);
    }

    @Override
    public String getName() {
        return "NumberOfCommissionsWeCanAddToOthersAfterChanges";
    }

}
