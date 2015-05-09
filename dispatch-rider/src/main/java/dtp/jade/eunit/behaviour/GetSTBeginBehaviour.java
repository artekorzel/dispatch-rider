package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author KONY
 */
public class GetSTBeginBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetSTBeginBehaviour.class);

    private final ExecutionUnitAgent executionUnitAgent;

    public GetSTBeginBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.ST_BEGIN);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            ACLMessage message = new ACLMessage(CommunicationHelper.ST_BEGIN);
            message.addReceiver(msg.getSender());
            try {
                message.setContentObject("");
            } catch (IOException e) {
                logger.error(e);
            }
            executionUnitAgent.send(message);

        } else {

            block();
        }
    }
}
