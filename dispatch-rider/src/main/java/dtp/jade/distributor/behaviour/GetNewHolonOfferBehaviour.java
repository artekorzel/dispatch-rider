package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import dtp.jade.transport.NewHolonOffer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

/**
 * Represents a behaviour of acceptance of new offer from EUnit Agent
 *
 * @author KONY
 */
public class GetNewHolonOfferBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetCommissionBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     *
     * @param agent - Distributor Agent
     */
    public GetNewHolonOfferBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves EUnit offers and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.NEW_HOLON_OFFER.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {

                NewHolonOffer offer = (NewHolonOffer) msg.getContentObject();
                distributorAgent.newHolonOffer(offer);
            } catch (UnreadableException e1) {

                logger.error(this.distributorAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

        } else {

            block();
        }
    }
}
