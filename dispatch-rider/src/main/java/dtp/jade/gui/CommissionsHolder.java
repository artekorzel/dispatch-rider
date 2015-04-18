package dtp.jade.gui;

import algorithm.Algorithm;
import dtp.commission.Commission;

import java.io.Serializable;

public class CommissionsHolder implements Serializable {

    private final Commission[] commissions;
    private final boolean type;
    private final boolean choosingByCost;
    private final DefaultAgentsData defaultAgentsData;
    private final int simulatedTrading;
    private final int STDepth;
    private final String chooseWorstCommission;
    private final Algorithm algorithm;
    private final boolean dist;
    private final int STTimestampGap;
    private final int STCommissionGap;
    private final boolean confChange;

    public CommissionsHolder(Commission[] commissions, boolean type,
                             boolean choosingByCost, int simulatedTrading, int STDepth,
                             DefaultAgentsData defaultAgentsData, String chooseWorstCommission,
                             Algorithm algorithm, boolean isDist, int STTimestampGap,
                             int STCommissionGap, boolean confChange) {
        this.commissions = commissions;
        this.dist = isDist;
        this.type = type;
        this.simulatedTrading = simulatedTrading;
        this.STDepth = STDepth;
        this.choosingByCost = choosingByCost;
        this.defaultAgentsData = defaultAgentsData;
        this.chooseWorstCommission = chooseWorstCommission;
        this.algorithm = algorithm;
        this.STTimestampGap = STTimestampGap;
        this.STCommissionGap = STCommissionGap;
        this.confChange = confChange;
    }

    public boolean isConfChange() {
        return confChange;
    }

    public boolean isDist() {
        return dist;
    }

    public String getChooseWorstCommission() {
        return chooseWorstCommission;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public int getSTDepth() {
        return STDepth;
    }

    public int getSimulatedTrading() {
        return simulatedTrading;
    }

    public DefaultAgentsData getDefaultAgentsData() {
        return defaultAgentsData;
    }

    public boolean isChoosingByCost() {
        return choosingByCost;
    }

    public Commission[] getCommissions() {
        return commissions;
    }

    public boolean getType() {
        return type;
    }

    public int getSTTimestampGap() {
        return STTimestampGap;
    }

    public int getSTCommissionGap() {
        return STCommissionGap;
    }
}
