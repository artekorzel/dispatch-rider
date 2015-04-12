package dtp.jade.transport;

import jade.core.AID;

import java.io.Serializable;

/**
 * Initial data for transport team element
 *
 * @author Michal Golacki
 */
public class TransportElementInitialData implements Serializable {

    private AID aid;
    /**
     * This element capacity
     */
    private int capacity;
    /**
     * Referral element capacity
     */
    private int defaultCapacity;
    private int depot = 0;
    private String costFunction;

    public TransportElementInitialData(String costFunction, int capacity, int defaultCapacity, int depot) {
        this.capacity = capacity;
        this.defaultCapacity = defaultCapacity;
        this.depot = depot;
        if (costFunction != null) this.costFunction = costFunction;
        else this.costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
    }

    public AID getAID() {
        return aid;
    }

    public void setAID(AID aid) {
        this.aid = aid;
    }

    public String getCostFunction() {
        return costFunction;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the defaultCapacity
     */
    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    /**
     * @param defaultCapacity the defaultCapacity to set
     */
    public void setDefaultCapacity(int defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
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
