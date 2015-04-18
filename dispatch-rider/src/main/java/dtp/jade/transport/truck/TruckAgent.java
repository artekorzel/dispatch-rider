package dtp.jade.transport.truck;

import dtp.commission.Commission;
import dtp.jade.transport.*;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Truck, transport team element
 *
 * @author Michal Golacki
 */
public class TruckAgent extends TransportAgent {

    @Override
    protected TransportType getType() {
        return TransportType.TRUCK;
    }

    /**
     * {@inherit-doc}
     */
    @Override
    public double getRatio() {
        return 1.0;
        // return 1.0 / 3 + (2.0 * getCapacity()) / (3.0 *
        // getDefaultCapacity());
    }

    @Override
    protected synchronized void makeHolonPartsList() {
        double dist = (commission.getPickupX() - commission.getDeliveryX())
                * (commission.getPickupX() - commission.getDeliveryX())
                + (commission.getPickupY() - commission.getDeliveryY())
                * (commission.getPickupY() - commission.getDeliveryY());
        double cost;
        holonPartsCostList = new LinkedList<HolonPartsCost>();
        TransportElementInitialDataTrailer trailerData;
        for (TransportAgentData agent : trailers) {
            trailerData = (TransportElementInitialDataTrailer) agent.getData();
            if (getConnectorType() != trailerData.getConnectorType())
                continue;
            if (getPower() < trailerData.getMass() + trailerData.getCapacity())
                continue;
            if (trailerData.getCapacity() < commission.getLoad())
                continue;

            cost = costFunctionValue(initialData.getCostFunction(), dist, null,
                    (TransportElementInitialDataTruck) initialData,
                    trailerData, commission, null);
            for (TransportAgentData agent2 : drivers) {
                holonPartsCostList.add(new HolonPartsCost(
                        new TransportAgentData[]{agent, agent2}, cost,
                        commission));
            }
        }
        Collections.sort(holonPartsCostList);
    }

    @Override
    protected synchronized void makeHolonPartsListFromAllAgents() {
        holonPartsCostList = new LinkedList<HolonPartsCost>();
        TransportElementInitialDataTrailer trailerData;
        for (TransportAgentData agent : trailers) {
            trailerData = (TransportElementInitialDataTrailer) agent.getData();
            if (getConnectorType() != trailerData.getConnectorType())
                continue;
            if (getPower() < trailerData.getMass() + trailerData.getCapacity())
                continue;

            for (TransportAgentData agent2 : drivers) {
                holonPartsCostList.add(new HolonPartsCost(
                        new TransportAgentData[]{agent, agent2}, 0.0));
            }
        }
        Collections.sort(holonPartsCostList);
    }

    @Override
    protected synchronized double getCostFunctionValue(HolonPartsCost part,
                                                       double dist, Commission com) {
        TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) part
                .getAgents()[0].getData();
        TransportElementInitialData driverData = part.getAgents()[1].getData();
        return costFunctionValue(initialData.getCostFunction(), dist,
                driverData, (TransportElementInitialDataTruck) initialData,
                trailerData, com, null);
    }

    @Override
    protected synchronized boolean canCarryCommission(Commission com,
                                                      HolonPartsCost part) {
        TransportElementInitialDataTrailer trailerData;
        trailerData = (TransportElementInitialDataTrailer) part.getAgents()[0]
                .getData();
        if (trailerData.getCapacity() >= calculateLoad(com,
                part.getCommissions()))
            return true;
        return false;
    }

    /**
     * {@inherit-doc}
     */
    @Override
    public TransportType getTransportType() {
        return TransportType.TRUCK;
    }

    /**
     * @return the reliability
     */
    public int getReliability() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getReliability();
    }

    public int getConnectorType() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getConnectorType();
    }

    /**
     * @return the comfort
     */
    public int getComfort() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getComfort();
    }

    /**
     * @return the fuelComsuption
     */
    public int getFuelComsuption() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getFuelConsumption();
    }

    public int getPower() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getPower();
    }

    @Override
    public synchronized void checkNewCommission(TransportCommission commission) {
        if (isBooked() || commission.getLoad() > getCapacity()) {
            TransportOffer offer = new TransportOffer();
            offer.setRatio(-1);
            offer.setAid(getAID());
            offer.setOfferType(getTransportType());
            sendOfferToEUnit(commission.getSenderId(), offer);
            // logger.info("Sending refusal");
        } else {
            TransportOffer offer = new TransportOffer();
            offer.setRatio(getRatio());
            offer.setTransportElementData(initialData);
            offer.setAid(getAID());
            offer.setDepot(getDepot());
            offer.setOfferType(getTransportType());
            sendOfferToEUnit(commission.getSenderId(), offer);
            // logger.info("sending acceprance");
        }
    }

    @Override
    public synchronized void checkReorganize(TransportCommission commission) {
        if (isBooked() || commission.getLoad() > getCapacity()) {
            TransportOffer offer = new TransportOffer();
            offer.setRatio(-1);
            offer.setAid(getAID());
            offer.setOfferType(getTransportType());
            sendReorganizeOfferToEUnit(commission.getSenderId(), offer);
            // logger.info("Sending refusal");
        } else {
            TransportOffer offer = new TransportOffer();
            offer.setRatio(getRatio());
            offer.setTransportElementData(initialData);
            offer.setAid(getAID());
            offer.setDepot(getDepot());
            offer.setOfferType(getTransportType());
            sendReorganizeOfferToEUnit(commission.getSenderId(), offer);
            // logger.info("sending acceprance");
        }
    }
}
