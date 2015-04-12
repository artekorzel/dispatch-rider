package measure;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.simulation.SimInfo;
import jade.core.AID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Base class for all other calculators which calculate specific measure. To add
 * new MeasureCalculator (and use it), you have to: 1. create your class which
 * extends MeasureCalculator 2. It has to be in measure package 3. Now you can
 * use it in configuration (you using class name)
 */
public abstract class MeasureCalculator implements Serializable {


    protected SimInfo info;
    protected int timestamp;
    protected List<Commission> commissions;

    /**
     * @param oldSchedules - schedules before ST
     * @param newSchedules - schedules after ST
     * @return
     */
    public abstract Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
                                             Map<AID, Schedule> newSchedules);

    public abstract String getName();

    public void setSimInfo(SimInfo info) {
        this.info = info;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setCommissions(List<Commission> commissions) {
        this.commissions = commissions;
    }
}
