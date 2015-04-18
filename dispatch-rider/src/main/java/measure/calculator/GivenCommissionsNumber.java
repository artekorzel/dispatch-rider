package measure.calculator;

import algorithm.Schedule;
import dtp.commission.Commission;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;

import java.util.Map;

public class GivenCommissionsNumber extends MeasureCalculator {



    @Override
    public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                    Map<AID, Schedule> newSchedules) {
        Measure result = new Measure();

        if (newSchedules == null) {
            for (AID aid : oldSchedules.keySet()) {
                result.put(aid, 0.0);
            }
            return result;
        }

        Schedule oldSchedule;
        Schedule newSchedule;
        boolean found;
        int numberOfGivenCommissions;
        for (AID aid : oldSchedules.keySet()) {
            oldSchedule = oldSchedules.get(aid);
            newSchedule = newSchedules.get(aid);
            numberOfGivenCommissions = 0;
            for (Commission com : oldSchedule.getCommissions()) {
                found = false;
                for (Commission com2 : newSchedule.getCommissions()) {
                    if (com.getID() == com2.getID()) {
                        found = true;
                        break;
                    }
                }
                if (found == false)
                    numberOfGivenCommissions++;
            }
            result.put(aid, 1.0 * numberOfGivenCommissions);
        }
        return result;
    }

    @Override
    public String getName() {
        return "GivenCommissionsNumber";
    }
}
