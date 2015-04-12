package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetResetRequestBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetResetRequestBehaviour.class);

    private ExecutionUnitAgent eunitAgent;

    public GetResetRequestBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.RESET);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - RESET requested");

            eunitAgent.resetAgent();

        } else {
            block();
        }
    }
}
