package algorithm.simulatedTrading;

import dtp.commission.Commission;

import java.io.Serializable;

public class SimulatedTradingParameters implements Serializable {

    public int STDepth;
    public Commission commission;
    public String msg;
}
