package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetConfirmationFromDistributorBehaviour extends CyclicBehaviour {

    private TransportAgent agent;

    public GetConfirmationFromDistributorBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.CONFIRMATION_FROM_DISTRIBUTOR.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            agent.confirmationFromDistributor();
        } else {
            block();
        }

    }
}
