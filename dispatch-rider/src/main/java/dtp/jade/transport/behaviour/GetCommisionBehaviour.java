package dtp.jade.transport.behaviour;

import dtp.commission.Commission;
import dtp.jade.MessageType;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

/**
 * Behaviour used by transport elements to receive commission information.
 *
 * @author Michal Golacki
 */
public class GetCommisionBehaviour extends CyclicBehaviour {

    /**
     * Serial version
     */

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(GetCommisionBehaviour.class);
    /**
     * Agent
     */
    private TransportAgent agent;

    public GetCommisionBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.COMMISSION.name());
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                //logger.info("Got commision from e unit");
                Commission[] commissions = (Commission[]) message.getContentObject();
                if (commissions.length == 1)
                    agent.setCommission(commissions[0]);
                else agent.setCommissions(commissions);
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UndeadableException - ", e);
            }
        } else {
            block();
        }

    }
}
