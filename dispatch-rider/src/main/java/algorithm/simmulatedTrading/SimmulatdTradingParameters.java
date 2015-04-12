package algorithm.simmulatedTrading;

import dtp.commission.Commission;

import java.io.Serializable;
import java.util.Set;

public class SimmulatdTradingParameters implements Serializable {

    public int STDepth;
    public Set<Integer> commissionsId;
    public Commission commission;
    public String msg;
}
