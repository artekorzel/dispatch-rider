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
        holonPartsCostList = new LinkedList<>();
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
        return trailerData.getCapacity() >= calculateLoad(com,
                part.getCommissions());
    }

    @Override
    public TransportType getTransportType() {
        return TransportType.TRUCK;
    }

    public int getReliability() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getReliability();
    }

    public int getConnectorType() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getConnectorType();
    }

    public int getComfort() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getComfort();
    }

    public int getPower() {
        return ((TransportElementInitialDataTruck) (this.initialData))
                .getPower();
    }

}
