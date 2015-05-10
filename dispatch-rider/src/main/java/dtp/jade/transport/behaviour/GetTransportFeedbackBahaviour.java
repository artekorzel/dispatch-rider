package dtp.jade.transport.behaviour;

import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportFeedback;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTransportFeedbackBahaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportCommisionBehaviour.class);

    private TransportAgent agent;

    public GetTransportFeedbackBahaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRANSPORT_FEEDBACK.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                TransportFeedback content = (TransportFeedback) message.getContentObject();

                if (content != null) {
                    agent.setBooked(true);
                } else {
                    agent.setBooked(false);
                }
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UnreadableException - " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
