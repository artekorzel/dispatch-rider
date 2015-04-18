package dtp.jade.transport.trailer;

import dtp.commission.Commission;
import dtp.jade.transport.*;

import java.util.Collections;
import java.util.LinkedList;

public class TrailerAgent extends TransportAgent {

    @Override
    protected TransportType getType() {
        return TransportType.TRAILER;
    }

    @Override
    public double getRatio() {
        return 1.0;
    }

    @Override
    protected synchronized void makeHolonPartsList() {
        double dist = (commission.getPickupX() - commission.getDeliveryX())
                * (commission.getPickupX() - commission.getDeliveryX())
                + (commission.getPickupY() - commission.getDeliveryY())
                * (commission.getPickupY() - commission.getDeliveryY());
        double cost;
        holonPartsCostList = new LinkedList<>();
        TransportElementInitialDataTruck truckData;
        for (TransportAgentData agent : trucks) {
            truckData = (TransportElementInitialDataTruck) agent.getData();
            if (getConnectorType() != truckData.getConnectorType())
                continue;
            if (truckData.getPower() < getMass() + getCapacity())
                continue;
            if (initialData.getCapacity() < commission.getLoad())
                continue;

            cost = costFunctionValue(initialData.getCostFunction(), dist, null,
                    truckData,
                    (TransportElementInitialDataTrailer) initialData,
                    commission, null);
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
        holonPartsCostList = new LinkedList<>();
        TransportElementInitialDataTruck truckData;
        for (TransportAgentData agent : trucks) {
            truckData = (TransportElementInitialDataTruck) agent.getData();
            if (getConnectorType() != truckData.getConnectorType())
                continue;
            if (truckData.getPower() < getMass() + getCapacity())
                continue;

            for (TransportAgentData agent2 : drivers) {
                holonPartsCostList.add(new HolonPartsCost(
                        new TransportAgentData[]{agent, agent2}, 0.0));
            }
        }
        Collections.sort(holonPartsCostList);
    }

    @Override
    protected synchronized boolean canCarryCommission(Commission com,
                                                      HolonPartsCost part) {
        TransportElementInitialDataTrailer trailerData;
        trailerData = (TransportElementInitialDataTrailer) initialData;
        return trailerData.getCapacity() >= calculateLoad(com,
                part.getCommissions());
    }

    @Override
    protected synchronized double getCostFunctionValue(HolonPartsCost part,
                                                       double dist, Commission com) {
        TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) part
                .getAgents()[0].getData();
        TransportElementInitialData driverData = part.getAgents()[1].getData();
        return costFunctionValue(initialData.getCostFunction(), dist,
                driverData, truckData,
                (TransportElementInitialDataTrailer) initialData, com, null);
    }

    @Override
    public TransportType getTransportType() {
        return TransportType.TRAILER;
    }

    public int getConnectorType() {
        return ((TransportElementInitialDataTrailer) initialData)
                .getConnectorType();
    }

    public int getMass() {
        return ((TransportElementInitialDataTrailer) initialData).getMass();
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
        // System.out.println("JESTEM PRZYCZEPA i sie mam reorganizowac moje cap, aid, loadreq "+
        // getCapacity() + " " + getAID() + " "+commission.getLoad());
        if (isBooked() || commission.getLoad() > getCapacity()) {
            TransportOffer offer = new TransportOffer();
            offer.setRatio(-1);
            offer.setAid(getAID());
            offer.setOfferType(getTransportType());
            sendReorganizeOfferToEUnit(commission.getSenderId(), offer);
            // logger.info("Sending refusal");
        } else {
            // System.out.println(getAID() + "JESTEM PRZYCZEPA i to wezme ");
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
