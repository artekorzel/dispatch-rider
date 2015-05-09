package dtp.jade.distributor.behaviour;

import dtp.commission.Commission;
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
public class GetCommissionSendedAgainBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetCommissionSendedAgainBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     *
     * @param agent - Distributor Agent
     */
    public GetCommissionSendedAgainBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves commissions and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.COMMISSION_SEND_AGAIN.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {

                Commission com = (Commission) msg.getContentObject();
                logger.info(this.distributorAgent.getLocalName() + " - got comission again id=" + com);
                distributorAgent.addCommission(com);

            } catch (UnreadableException e1) {

                logger.error(this.distributorAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

            //for (int i = 0; i < commissions.length; i++)
            //    distributorAgent.addCommission(commissions[i]);


        } else {

            block();
        }
    }
}
