package adapter;

import dtp.commission.CommissionHandler;
import dtp.simulation.SimInfo;

import java.io.Serializable;
import java.util.List;

public interface Adapter extends Serializable {

    List<CommissionHandler> readCommissions();

    SimInfo getSimInfo();
}
