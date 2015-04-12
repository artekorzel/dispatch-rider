package dtp.jade.distributor;

import algorithm.Algorithm;
import dtp.commission.Commission;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import jade.core.AID;

import java.io.Serializable;

public class NewTeamData implements Serializable {

    private TransportElementInitialDataTruck truck;
    private TransportElementInitialDataTrailer trailer;
    private TransportElementInitialData driver;
    private Commission commission;
    private int STDepth;
    private Algorithm algorithm;
    private boolean dist;
    private int timestamp;

    private AID truckAID;
    private AID trailerAID;
    private AID driverAID;

    public NewTeamData(AID truckAID, TransportElementInitialData truck, AID trailerAID, TransportElementInitialData trailer, AID driverAID, TransportElementInitialData driver, Commission commission, int STDepth, Algorithm algorithm, boolean dist, int timestamp) {
        this.truck = (TransportElementInitialDataTruck) truck;
        this.timestamp = timestamp;
        this.trailer = (TransportElementInitialDataTrailer) trailer;
        this.driver = driver;
        this.commission = commission;
        this.STDepth = STDepth;
        this.truckAID = truckAID;
        this.trailerAID = trailerAID;
        this.driverAID = driverAID;
        this.algorithm = algorithm;
        this.dist = dist;
    }

    public int getCreationTime() {
        return timestamp;
    }

    public boolean isDist() {
        return dist;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public int getSTDepth() {
        return STDepth;
    }

    public TransportElementInitialDataTruck getTruck() {
        return truck;
    }

    public TransportElementInitialDataTrailer getTrailer() {
        return trailer;
    }

    public TransportElementInitialData getDriver() {
        return driver;
    }

    public Commission getCommission() {
        return commission;
    }

    public AID getTruckAID() {
        return truckAID;
    }

    public AID getTrailerAID() {
        return trailerAID;
    }

    public AID getDriverAID() {
        return driverAID;
    }
}
