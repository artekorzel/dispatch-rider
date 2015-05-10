package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetStartNegotiationBehaviour extends CyclicBehaviour {

    private TransportAgent agent;

    public GetStartNegotiationBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.START_NEGOTIATION.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {

            agent.startNegotiation();
        } else {
            block();
        }

    }
}
