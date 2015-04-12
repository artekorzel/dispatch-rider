package dtp.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

/**
 * Base class for all agents
 *
 * @author Michal Golacki
 */
public class BaseAgent extends Agent {

    /**
     * Method used to send objects between agents.
     *
     * @param aid         receiver agent id
     * @param object      serializable object to be send
     * @param messageCode message code
     */
    public synchronized void send(AID aid, Serializable object, int messageCode) {
        ACLMessage message = new ACLMessage(messageCode);
        message.addReceiver(aid);
        try {
            message.setContentObject(object);

            send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
