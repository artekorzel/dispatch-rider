package gui.common;

import dtp.simulation.SimInfo;
import xml.elements.SimulationData;

import java.util.Arrays;
import java.util.Vector;

/*
 * abstrakcyjna klasa, zapewnia hashmap dla kadego timestampa, mona sobie wrzucac co sie chce
 */
public abstract class TimestampUpdateable implements Updateable {
    public TimestampRecord visualisedRecord;
    protected SimInfo simInfo;
    protected Vector<TimestampRecord> records = new Vector<TimestampRecord>();
    protected TimestampRecord newRecord;
    protected int visualisedTimestamp = 0;
    protected int newTimestamp = 0;

    public TimestampUpdateable() {
        newRecord = new TimestampRecord(0, null);
        visualisedRecord = newRecord;
        records.add(newRecord);
    }

    public void newTimestampUpdate(int val) {
        if (records.size() > 1 && visualisedRecord == records.get(records.size() - 2)) {
            visualisedTimestamp = records.lastElement().timestamp;
            visualisedRecord = records.lastElement();
        }

        newRecord = new TimestampRecord(val, null);
        newTimestamp = val;
        records.add(newRecord);
    }

    abstract public void update(SimulationData data);

    public void setSimInfo(SimInfo simInfo) {
        this.simInfo = simInfo;
    }

    public int getDrawnTimestamp() {
        return visualisedTimestamp;
    }

    public void setDrawnTimestamp(int val) {
        TimestampRecord search = new TimestampRecord(val, null);
        try {
            visualisedRecord = records.get(Arrays.binarySearch(records.toArray(), search, new TimestampRecordComparator()));
            visualisedTimestamp = val;
            //System.err.println(val + " " + visualisedRecord.getTimestamp() +  " " + visualisedRecord.getData());
        } catch (Exception e) {
            //out of bounds
        }
    }
}
