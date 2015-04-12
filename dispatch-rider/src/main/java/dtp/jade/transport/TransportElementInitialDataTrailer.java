package dtp.jade.transport;

public class TransportElementInitialDataTrailer extends TransportElementInitialData {

    /**
     *
     */

    private int mass;
    private int capacity_;
    private int cargoType;
    private int universality;
    private int connectorType;

    public TransportElementInitialDataTrailer(String costFunction, int capacity,
                                              int defaultCapacity, int depot) {
        super(costFunction, capacity, defaultCapacity, depot);
        // TODO Auto-generated constructor stub
    }


    public TransportElementInitialDataTrailer(String costFunction, int capacity, int defaultCapacity,
                                              int depot, int mass, int capacity_, int cargoType,
                                              int universality, int connectorType) {
        super(costFunction, capacity, defaultCapacity, depot);
        this.mass = mass;
        this.capacity_ = capacity_;
        this.cargoType = cargoType;
        this.universality = universality;
        this.connectorType = connectorType;
    }

    /**
     * @return the capacity_
     */
    public int getCapacity_() {
        return capacity_;
    }

    /**
     * @param capacity_ the capacity_ to set
     */
    public void setCapacity_(int capacity_) {
        this.capacity_ = capacity_;
    }

    /**
     * @return the cargoType
     */
    public int getCargoType() {
        return cargoType;
    }

    /**
     * @param cargoType the cargoType to set
     */
    public void setCargoType(int cargoType) {
        this.cargoType = cargoType;
    }

    /**
     * @return the universality
     */
    public int getUniversality() {
        return universality;
    }

    /**
     * @param universality the universality to set
     */
    public void setUniversality(int universality) {
        this.universality = universality;
    }

    /**
     * @return the connectorType
     */
    public int getConnectorType() {
        return connectorType;
    }

    /**
     * @param connectorType the connectorType to set
     */
    public void setConnectorType(int connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * @return the mass
     */
    public int getMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(int mass) {
        this.mass = mass;
    }

}
