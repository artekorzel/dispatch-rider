package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import dtp.jade.transport.TransportAgentsMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

/**
 * Represents a behaviour of acceptance of new commission(s) from GUI Agent
 *
 * @author KONY
 */
public class GetTransportAgentsDataBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetTransportAgentsDataBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     *
     * @param agent - Distributor Agent
     */
    public GetTransportAgentsDataBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves commissions and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                TransportAgentsMessage agents = (TransportAgentsMessage) msg.getContentObject();
                distributorAgent.setAgentsData(agents.getAgents());
            } catch (UnreadableException e1) {

                logger.error(this.distributorAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

        } else {

            block();
        }
    }
}
