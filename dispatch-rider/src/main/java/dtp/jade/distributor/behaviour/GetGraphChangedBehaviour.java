package dtp.jade.distributor.behaviour;

import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
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
public class GetGraphChangedBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger
            .getLogger(GetGraphChangedBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent
     * from Action method
     *
     * @param agent - Distributor Agent
     */
    public GetGraphChangedBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves commissions and processes it
     */
    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.GRAPH_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                distributorAgent.graphChanged((Graph) msg.getContentObject(),
                        msg.getSender());

            } catch (UnreadableException e1) {
                logger.error(this.distributorAgent.getLocalName()
                        + " - UnreadableException", e1);
            }

        } else {

            block();
        }
    }
}
