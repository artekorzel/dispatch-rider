package dtp.jade.transport.driver;

import dtp.commission.Commission;
import dtp.jade.transport.*;

import java.util.Collections;
import java.util.LinkedList;

public class DriverAgent extends TransportAgent {

    @Override
    protected TransportType getType() {
        return TransportType.DRIVER;
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
        TransportElementInitialDataTruck truckData;
        for (TransportAgentData agent : trailers) {
            trailerData = (TransportElementInitialDataTrailer) agent.getData();
            for (TransportAgentData agent2 : trucks) {
                truckData = (TransportElementInitialDataTruck) agent2.getData();
                if (truckData.getConnectorType() != trailerData
                        .getConnectorType())
                    continue;
                if (truckData.getPower() < trailerData.getMass()
                        + trailerData.getCapacity())
                    continue;
                if (trailerData.getCapacity() < commission.getLoad())
                    continue;
                cost = costFunctionValue(initialData.getCostFunction(), dist,
                        initialData, truckData, trailerData, commission, null);
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
        TransportElementInitialDataTruck truckData;
        for (TransportAgentData agent : trailers) {
            trailerData = (TransportElementInitialDataTrailer) agent.getData();
            for (TransportAgentData agent2 : trucks) {
                truckData = (TransportElementInitialDataTruck) agent2.getData();
                if (truckData.getConnectorType() != trailerData
                        .getConnectorType())
                    continue;
                if (truckData.getPower() < trailerData.getMass()
                        + trailerData.getCapacity())
                    continue;
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
        TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) part
                .getAgents()[1].getData();
        return costFunctionValue(initialData.getCostFunction(), dist,
                initialData, truckData, trailerData, com, null);
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
        return TransportType.DRIVER;
    }

}
