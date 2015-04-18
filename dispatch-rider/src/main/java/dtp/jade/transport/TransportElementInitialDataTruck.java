package dtp.jade.transport;

public class TransportElementInitialDataTruck extends TransportElementInitialData {



    private int power;
    private int reliability;
    private int comfort;
    private int fuelConsumption;
    private int connectorType;

    public TransportElementInitialDataTruck(String costFunction, int capacity, int defaultCapacity,
                                            int depot, int power, int reliability,
                                            int comfort, int fuelConsumption, int connectorType) {
        super(costFunction, capacity, defaultCapacity, depot);
        this.power = power;
        this.reliability = reliability;
        this.comfort = comfort;
        this.fuelConsumption = fuelConsumption;
        this.connectorType = connectorType;
    }

    /**
     * @return the reliability
     */
    public int getReliability() {
        return reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability(int reliability) {
        this.reliability = reliability;
    }

    /**
     * @return the comfort
     */
    public int getComfort() {
        return comfort;
    }

    /**
     * @param comfort the comfort to set
     */
    public void setComfort(int comfort) {
        this.comfort = comfort;
    }

    /**
     * @return the fuelComsuption
     */
    public int getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(int fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    /**
     * @return the power
     */
    public int getPower() {
        return power;
    }

    /**
     * @param power the power to set
     */
    public void setPower(int power) {
        this.power = power;
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

}
