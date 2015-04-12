package dtp.jade.crisismanager.crisisevents;

public class EUnitFailureEvent extends CrisisEvent {

    private static final String EVENT_TYPE = "Vehicle Failure";

    private int eUnitID;

    private double failureDuration;

    public int getEUnitID() {

        return eUnitID;
    }

    public void setEUnitID(int id) {

        eUnitID = id;
    }

    public double getFailureDuration() {

        return failureDuration;
    }

    public void setFailureDuration(double failureDuration) {

        this.failureDuration = failureDuration;
    }

    public String getEventType() {

        return EVENT_TYPE;
    }

    public String toString() {

        return "<" + getEventTime() + ">" + " type = " + EVENT_TYPE + ", ID = " + getEventID() + ", vehicle ID = "
                + getEUnitID() + ", duration = " + getFailureDuration();
    }
}
