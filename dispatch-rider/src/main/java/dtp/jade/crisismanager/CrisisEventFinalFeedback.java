package dtp.jade.crisismanager;

import dtp.commission.Commission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class CrisisEventFinalFeedback implements Serializable {

    private int eventID;

    private String message;

    private ArrayList<Commission> coms4Auction;

    public CrisisEventFinalFeedback() {

        this.coms4Auction = new ArrayList<Commission>();
    }

    public CrisisEventFinalFeedback(int eventId, String message) {

        this.eventID = eventId;
        this.message = message;
        this.coms4Auction = new ArrayList<Commission>();
    }

    public int getEventID() {

        return eventID;
    }

    public void setEventID(int eventID) {

        this.eventID = eventID;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("event ID = " + eventID + ", message = " + message);

        if (coms4Auction.size() > 0)
            str.append(", commissions 4 auction = " + coms4Auction.size());

        return str.toString();
    }

    public void addCom4Auction(Commission commission) {

        coms4Auction.add(commission);
    }

    public Commission[] getComs4Auction() {

        Commission[] coms;
        Iterator<Commission> iter;
        int i;

        coms = new Commission[coms4Auction.size()];
        iter = coms4Auction.iterator();
        i = 0;

        while (iter.hasNext()) {

            coms[i] = (Commission) iter.next();
            i++;
        }

        return coms;
    }
}
