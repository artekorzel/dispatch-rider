package adapter;

import dtp.commission.CommissionHandler;
import dtp.simulation.SimInfo;

import java.util.List;

public interface Adapter {

    List<CommissionHandler> readCommissions();

    SimInfo getSimInfo();
}
