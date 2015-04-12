package dtp.jade.crisismanager.crisisevents;

public class CommissionDelayEvent extends CrisisEvent {

    private static final String EVENT_TYPE = "Commission Delay";

    private int commissionID;

    private int delay;

    public int getCommissionID() {

        return commissionID;
    }

    public void setCommissionID(int id) {

        commissionID = id;
    }

    public int getDelay() {

        return delay;
    }

    public void setDelay(int delay) {

        this.delay = delay;
    }

    public String getEventType() {

        return EVENT_TYPE;
    }

    public String toString() {

        return "<" + getEventTime() + ">" + " type = " + EVENT_TYPE + ", ID = " + getEventID() + ", com ID = "
                + getCommissionID() + ", delay = " + getDelay();
    }
}
