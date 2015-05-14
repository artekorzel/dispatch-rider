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
    private int capacity;
    private int defaultCapacity;
    private int depot = 0;
    private String costFunction;

    public TransportElementInitialData() {
    }

    public TransportElementInitialData(String costFunction, int capacity, int defaultCapacity, int depot) {
        this.capacity = capacity;
        this.defaultCapacity = defaultCapacity;
        this.depot = depot;
        if (costFunction != null) this.costFunction = costFunction;
        else this.costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public void setDefaultCapacity(int defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }

    public int getDepot() {
        return depot;
    }

    public void setDepot(int depot) {
        this.depot = depot;
    }

    public String getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(String costFunction) {
        this.costFunction = costFunction;
    }
}
