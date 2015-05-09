package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetResetRequestBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetResetRequestBehaviour.class);

    private final DistributorAgent distributorAgent;

    public GetResetRequestBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.RESET.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - RESET requested");

            distributorAgent.resetAgent();

        } else {
            block();
        }
    }
}
