package adapter;

import dtp.commission.CommissionHandler;
import dtp.simmulation.SimInfo;

import java.util.List;

public interface Adapter {

    List<CommissionHandler> readCommissions();

    SimInfo getSimInfo();
}
