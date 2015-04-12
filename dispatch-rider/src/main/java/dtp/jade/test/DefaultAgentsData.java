package dtp.jade.test;

import java.io.Serializable;

public class DefaultAgentsData implements Serializable {


    private int power;
    private int reliability;
    private int comfort;
    private int fuelConsumption;
    private int mass;
    private int capacity;
    private int cargoType;
    private int universality;

    public DefaultAgentsData(int power, int reliability, int comfort,
                             int fuelConsumption, int mass, int capacity, int cargoType,
                             int universality) {
        super();
        this.power = power;
        this.reliability = reliability;
        this.comfort = comfort;
        this.fuelConsumption = fuelConsumption;
        this.mass = mass;
        this.capacity = capacity;
        this.cargoType = cargoType;
        this.universality = universality;
        if (power < mass + capacity) {
            System.err.println("Domysly EUnit nie moze zostac utworzony\n" +
                    "powod: power<mass+capacity");
            System.exit(0);
        }
    }

    public int getPower() {
        return power;
    }

    public int getReliability() {
        return reliability;
    }

    public int getComfort() {
        return comfort;
    }

    public int getFuelConsumption() {
        return fuelConsumption;
    }

    public int getMass() {
        return mass;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCargoType() {
        return cargoType;
    }

    public int getUniversality() {
        return universality;
    }

}
