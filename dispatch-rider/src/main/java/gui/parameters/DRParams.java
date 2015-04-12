package gui.parameters;

public class DRParams {
    private boolean commissionSendingType;
    private boolean dist;
    private boolean choosingByCost;
    private int simmulatedTradingCount;
    private String chooseWorstCommission;
    private String algorithm;
    private int maxFullSTDepth;
    private int STTimestampGap;
    private int STCommissionsionsGap;

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

    public void setSTTimestampGap(int sTTimestampGap) {
        STTimestampGap = sTTimestampGap;
    }

    public int getSTCommissionsionsGap() {
        return STCommissionsionsGap;
    }

    public void setSTCommissionsionsGap(int sTCommissionsionsGap) {
        STCommissionsionsGap = sTCommissionsionsGap;
    }

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

    public int getSimmulatedTradingCount() {
        return simmulatedTradingCount;
    }

    public void setSimmulatedTradingCount(int simmulatedTradingCount) {
        this.simmulatedTradingCount = simmulatedTradingCount;
    }

    public String getChooseWorstCommission() {
        return chooseWorstCommission;
    }

    public void setChooseWorstCommission(String chooseWorstCommission) {
        this.chooseWorstCommission = chooseWorstCommission;
    }
}
