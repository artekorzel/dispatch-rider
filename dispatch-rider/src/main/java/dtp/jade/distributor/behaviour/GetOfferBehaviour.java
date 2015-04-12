package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import dtp.jade.eunit.EUnitOffer;
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
public class GetOfferBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetCommissionBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     *
     * @param agent - Distributor Agent
     */
    public GetOfferBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves EUnit offers and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.COMMISSION_OFFER);
        ACLMessage msg = myAgent.receive(template);

        EUnitOffer offer;

        if (msg != null) {

            try {

                offer = (EUnitOffer) msg.getContentObject();

                //                logger.info(this.distributorAgent.getLocalName() + " - got offer from "
                //                        + offer.getAgent().getLocalName() + "\t= " + offer.getValue());

                // distributorAgent.sendGUIMessage("got offer from "
                // + offer.getAgent().getLocalName() + ", value = "
                // + offer.getValue());

                distributorAgent.addOffer(offer);

            } catch (UnreadableException e1) {

                logger.error(this.distributorAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

        } else {

            block();
        }
    }
}
