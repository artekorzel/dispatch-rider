package gui.common;

import dtp.simmulation.SimInfo;
import xml.elements.SimmulationData;

public interface Updateable {
    public void newTimestampUpdate(int val);

    public int getDrawnTimestamp();

    public void setDrawnTimestamp(int val);

    public void update(SimmulationData data);

    public void setSimInfo(SimInfo simInfo);
}
