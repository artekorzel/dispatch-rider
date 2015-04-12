package dtp.jade.eunit.behaviour;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetCommissionFromDistributorAgentBehaviour extends CyclicBehaviour {



    private static Logger logger = Logger.getLogger(GetCommissionFromDistributorAgentBehaviour.class);

    private final ExecutionUnitAgent executionUnitAgent;

    public GetCommissionFromDistributorAgentBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    /**
     * Recieves a new commission and processes it.
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.COMMISSION_OFFER_REQUEST);
        ACLMessage msg = myAgent.receive(template);

        Commission commission = null;

        if (msg != null) {

            try {

                commission = (Commission) msg.getContentObject();
                executionUnitAgent.checkNewCommission(commission);


            } catch (UnreadableException e1) {
                logger.error(this.executionUnitAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

        } else {
            block();
        }
    }
}
