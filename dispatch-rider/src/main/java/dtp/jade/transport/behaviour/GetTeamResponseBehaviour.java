package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTeamResponseBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTeamResponseBehaviour.class);

    private final TransportAgent agent;

    public GetTeamResponseBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.TEAM_OFFER_RESPONSE.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                String response = (String) message.getContentObject();
                switch (response) {
                    case "yes":
                        agent.response(message.getSender(), true);
                        break;
                    case "no":
                        agent.response(message.getSender(), false);
                        break;
                    default:
                        agent.response(message.getSender(), null);
                        break;
                }
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }

    }
}
