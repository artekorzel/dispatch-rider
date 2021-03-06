package dtp.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;

/**
 * Base class for all agents
 *
 * @author Michal Golacki
 */
public class BaseAgent extends Agent {

    private static Logger logger = Logger.getLogger(BaseAgent.class);

    /**
     * Method used to send objects between agents.
     *
     * @param aid         receiver agent id
     * @param object      serializable object to be send
     * @param messageCode message code
     */
    public synchronized void send(AID aid, Serializable object, MessageType messageCode) {
        send(new AID[]{aid}, object, messageCode);
    }

    public synchronized void send(AID[] aids, Serializable object, MessageType messageCode) {
        ACLMessage message = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        message.setConversationId(messageCode.name());
        for (AID aid : aids) {
            message.addReceiver(aid);
        }
        try {
            message.setContentObject(object);
            send(message);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public synchronized void sendString(AID aid, String content, MessageType messageCode) {
        sendString(new AID[]{aid}, content, messageCode);
    }

    public synchronized void sendString(AID[] aids, String content, MessageType messageCode) {
        ACLMessage message = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        message.setConversationId(messageCode.name());
        for (AID aid : aids) {
            message.addReceiver(aid);
        }
        message.setContent(content);
        send(message);
    }

}
