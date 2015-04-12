package dtp.jade.transport;

import dtp.graph.GraphPoint;
import jade.core.AID;

import java.io.Serializable;

/**
 * Offer data for Execution Unit
 *
 * @author Michal Golacki
 */
public class TransportOffer implements Serializable, Comparable<TransportOffer> {

    /**
     * Transport Element Agent's ID.
     */
    private AID aid;
    /**
     * Transport element location.
     */
    private GraphPoint location;
    /**
     * Type of transport offer.
     */
    private TransportType offerType;
    private TransportElementInitialData transportElementData;
    /**
     * Cost ratio.
     */
    private double ratio;
    /**
     * Agents depot
     */
    private int depot;

    public int compareTo(TransportOffer o) {
        return aid.compareTo(o.getAid());
    }

    /**
     * @return the aid
     */
    public AID getAid() {
        return aid;
    }

    /**
     * @param aid the aid to set
     */
    public void setAid(AID aid) {
        this.aid = aid;
    }

    /**
     * @return the location
     */
    public GraphPoint getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(GraphPoint location) {
        this.location = location;
    }

    /**
     * @return the offerType
     */
    public TransportType getOfferType() {
        return offerType;
    }

    /**
     * @param offerType the offerType to set
     */
    public void setOfferType(TransportType offerType) {
        this.offerType = offerType;
    }

    /**
     * @return the ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public TransportElementInitialData getTransportElementData() {
        return transportElementData;
    }

    public void setTransportElementData(
            TransportElementInitialData transportElementData) {
        this.transportElementData = transportElementData;
    }

    /**
     * @return the maxLoad
     */
    public int getMaxLoad() {
        return getTransportElementData().getCapacity();
    }

    /**
     * getter
     *
     * @return the depot
     */
    public int getDepot() {
        return depot;
    }

    /**
     * setter
     *
     * @param depot the depot to set
     */
    public void setDepot(int depot) {
        this.depot = depot;
    }

}
