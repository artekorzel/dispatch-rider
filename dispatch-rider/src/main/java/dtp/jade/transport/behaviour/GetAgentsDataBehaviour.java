package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportAgentsMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetAgentsDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetAgentsDataBehaviour.class);

    private TransportAgent agent;

    public GetAgentsDataBehaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.AGENTS_DATA_FOR_TRANSPORTUNITS.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            try {
                TransportAgentsMessage agents = (TransportAgentsMessage) message.getContentObject();
                agent.setAgentsData(agents.getAgents());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }

        } else {
            block();
        }

    }

}
