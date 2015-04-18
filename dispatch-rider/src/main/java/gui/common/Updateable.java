package gui.common;

import dtp.simulation.SimInfo;
import xml.elements.SimulationData;

public interface Updateable {
    public void newTimestampUpdate(int val);

    public int getDrawnTimestamp();

    public void setDrawnTimestamp(int val);

    public void update(SimulationData data);

    public void setSimInfo(SimInfo simInfo);
}
