package dtp.jade.eunit.behaviour;

import dtp.commission.Commission;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetWorstCommissionRequestBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetWorstCommissionRequestBehaviour.class);

    private ExecutionUnitAgent eunitAgent;

    public GetWorstCommissionRequestBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.WORST_COMMISSION_COST.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                eunitAgent.sendWorstCommissionCost((Commission) msg.getContentObject(), msg.getSender());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }
    }
}
