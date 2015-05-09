package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetUpdateCurrentLocationBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent executionUnitAgent;

    public GetUpdateCurrentLocationBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.UPDATE_CURRENT_LOCATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            executionUnitAgent.updateCurrentLocation(Integer.parseInt(msg.getContent()));
            executionUnitAgent.confirmUpdateCurrentLocationRequest(msg.getSender());
        } else {
            block();
        }
    }
}
