package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
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
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.START_NEGOTIATION);
        ACLMessage message = myAgent.receive(template);

        if (message != null) {

            agent.startNegotiation();
        } else {
            block();
        }

    }
}
