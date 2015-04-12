package dtp.jade.eunit.behaviour;

/**
 * @author Szyna
 */

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetUpdateCurrentLocationBehaviour extends CyclicBehaviour {


    private final ExecutionUnitAgent executionUnitAgent;

    public GetUpdateCurrentLocationBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.UPDATE_CURRENT_LOCATION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            executionUnitAgent.updateCurrentLocation(Integer.parseInt(msg.getContent()));

            //potwierdzamy przyjecie prosby
            executionUnitAgent.confirmUpdateCurrentLocationRequest(msg.getSender());
            GUIAgent.updateCurrentLocationSemaphore.release(1);
        } else {
            block();
        }
    }
}
