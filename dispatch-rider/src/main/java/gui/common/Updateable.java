package gui.common;

import dtp.simulation.SimInfo;
import xml.elements.SimulationData;

public interface Updateable {
    void newTimestampUpdate(int val);

    int getDrawnTimestamp();

    void setDrawnTimestamp(int val);

    void update(SimulationData data);

    void setSimInfo(SimInfo simInfo);
}
