package dtp.jade.crisismanager.crisisevents;

public class CommissionWithdrawalEvent extends CrisisEvent {

    private static final String EVENT_TYPE = "Commission Withdrawal";

    private int commissionID;

    public int getCommissionID() {

        return commissionID;
    }

    public void setCommissionID(int id) {

        commissionID = id;
    }

    public String getEventType() {

        return EVENT_TYPE;
    }

    public String toString() {

        return "<" + getEventTime() + ">" + " type = " + EVENT_TYPE + ", ID = " + getEventID() + ", com ID = "
                + getCommissionID();
    }
}
