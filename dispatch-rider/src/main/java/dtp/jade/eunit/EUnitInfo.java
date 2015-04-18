package dtp.jade.eunit;

import jade.core.AID;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class EUnitInfo implements Serializable {

    private AID aid;

    private Point2D currentLocation;

    public EUnitInfo(AID aid) {

        this.aid = aid;
    }

    public AID getAID() {

        return aid;
    }

    public void setAID(AID aid) {

        this.aid = aid;
    }

    public Point2D getCurrentLocation() {

        return currentLocation;
    }

    public void setCurrentLocation(Point2D location) {

        this.currentLocation = location;
    }
}
