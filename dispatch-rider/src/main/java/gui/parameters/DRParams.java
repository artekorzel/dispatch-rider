package gui.parameters;

import java.io.Serializable;

public class DRParams implements Serializable {
    private boolean commissionSendingType;
    private boolean dist;
    private boolean choosingByCost;
    private int simulatedTradingCount;
    private String chooseWorstCommission;
    private String algorithm;
    private int maxFullSTDepth;
    private int STTimestampGap;
    private int STCommissionsionsGap;

    public boolean isCommissionSendingType() {
        return commissionSendingType;
    }

    public void setCommissionSendingType(boolean commissionSendingType) {
        this.commissionSendingType = commissionSendingType;
    }

    public boolean isDist() {
        return dist;
    }

    public void setDist(boolean dist) {
        this.dist = dist;
    }

    public boolean isChoosingByCost() {
        return choosingByCost;
    }

    public void setChoosingByCost(boolean choosingByCost) {
        this.choosingByCost = choosingByCost;
    }

    public int getSimulatedTradingCount() {
        return simulatedTradingCount;
    }

    public void setSimulatedTradingCount(int simulatedTradingCount) {
        this.simulatedTradingCount = simulatedTradingCount;
    }

    public String getChooseWorstCommission() {
        return chooseWorstCommission;
    }

    public void setChooseWorstCommission(String chooseWorstCommission) {
        this.chooseWorstCommission = chooseWorstCommission;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getMaxFullSTDepth() {
        return maxFullSTDepth;
    }

    public void setMaxFullSTDepth(int maxFullSTDepth) {
        this.maxFullSTDepth = maxFullSTDepth;
    }

    public int getSTTimestampGap() {
        return STTimestampGap;
    }

    public void setSTTimestampGap(int STTimestampGap) {
        this.STTimestampGap = STTimestampGap;
    }

    public int getSTCommissionsionsGap() {
        return STCommissionsionsGap;
    }

    public void setSTCommissionsionsGap(int STCommissionsionsGap) {
        this.STCommissionsionsGap = STCommissionsionsGap;
    }
}
