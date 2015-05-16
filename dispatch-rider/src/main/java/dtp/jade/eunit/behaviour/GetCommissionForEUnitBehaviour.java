package dtp.jade.eunit.behaviour;

import dtp.commission.Commission;
import dtp.jade.AgentsService;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetCommissionForEUnitBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetCommissionForEUnitBehaviour.class);

    private final ExecutionUnitAgent executionUnitAgent;

    public GetCommissionForEUnitBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    /**
     * Recieves a new commission and processes it.
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.COMMISSION_FOR_EUNIT.name());
        ACLMessage msg = myAgent.receive(template);
        if (msg != null) {

            try {

                Commission commission = (Commission) msg.getContentObject();
                if (!executionUnitAgent.addCommissionToCalendar(commission)) {
                    logger.error("Fatal error: GetCommissionForEUnitBehaviour com=" + commission.getID());
                    executionUnitAgent.send(msg.getSender(), commission, MessageType.COMMISSION_SEND_AGAIN);
                    return;
                } else
                    logger.info(executionUnitAgent.getLocalName() + ": commission " + commission.getID() + " added to calendar");

                AID[] aids = AgentsService.findAgentByServiceName(executionUnitAgent, "CommissionService");
                executionUnitAgent.send(aids[0], "", MessageType.HOLON_FEEDBACK);
            } catch (UnreadableException e1) {
                logger.error(this.executionUnitAgent.getLocalName() + " - UnreadableException ", e1);
            }

        } else {
            block();
        }
    }
}
