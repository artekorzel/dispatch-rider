package dtp.jade.eunit.behaviour;

/**
 * @author Szyna
 */

import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.geom.Point2D;

public class GetBackToDepotBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent executionUnitAgent;

    public GetBackToDepotBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.BACK_TO_DEPOT.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            Schedule schedule = executionUnitAgent.getSchedule();
            if (schedule.size() > 0) {

                Commission lastCommission = schedule.getCommission(schedule.size() - 1);
                Point2D.Double depot = executionUnitAgent.getSimInfo().getDepot();
                /*new Commission(id, pickupX, pickupY, pickupTime1, pickupTime2, deliveryX, deliveryY, deliveryTime1, deliveryTime2,
                        load, pickUpServiceTime, deliveryServiceTime)*/
                double pickupTime = executionUnitAgent.getSimInfo().getDeadline() -
                        Helper.calculateDistance(new Point2D.Double(lastCommission.getDeliveryX(), lastCommission.getDeliveryY()), depot) - 2;
                schedule.addCommission(new Commission(789, lastCommission.getDeliveryX(), lastCommission.getDeliveryY(),
                        pickupTime - 10, pickupTime, depot.x, depot.y, pickupTime - 10,
                        executionUnitAgent.getSimInfo().getDeadline(), 0, 0, 0), true);

                schedule.addCommission(new Commission(0, lastCommission.getDeliveryX(), lastCommission.getDeliveryY(),
                        pickupTime - 10, pickupTime, depot.x, depot.y, pickupTime - 10,
                        executionUnitAgent.getSimInfo().getDeadline(), 0, 0, 0), false);

            }
        } else {
            block();
        }
    }
}
