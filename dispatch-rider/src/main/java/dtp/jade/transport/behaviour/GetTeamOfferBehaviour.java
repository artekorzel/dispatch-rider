package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetTeamOfferBehaviour extends CyclicBehaviour {

    private TransportAgent agent;

    public GetTeamOfferBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TEAM_OFFER.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            agent.teamOfferArrived(message.getSender());
        } else {
            block();
        }

    }
}
