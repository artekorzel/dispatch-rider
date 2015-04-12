package dtp.jade.eunit.behaviour;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

import java.io.IOException;

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

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.COMMISSION_FOR_EUNIT);
        ACLMessage msg = myAgent.receive(template);
        if (msg != null) {

            try {

                Commission commission = (Commission) msg.getContentObject();
                if (executionUnitAgent.addCommissionToCalendar(commission) == false) {
                    System.err.println("Fatal error: GetCommissionForEUnitBehaviour");
                    logger.error("Fatal error: GetCommissionForEUnitBehaviour com=" + commission.getID());

                    ACLMessage cfp = new ACLMessage(CommunicationHelper.COMMISSION_SEND_AGAIN);

                    cfp.addReceiver(msg.getSender());
                    try {
                        cfp.setContentObject(commission);
                        executionUnitAgent.send(cfp);
                    } catch (IOException e) {
                        logger.error("EunitCreationBehaviour - IOException " + e.getMessage());
                    }
                    return;
                } else
                    logger.info(executionUnitAgent.getLocalName() + ": commission " + commission.getID() + " added to calendar");

                AID[] aids = CommunicationHelper.findAgentByServiceName(executionUnitAgent, "CommissionService");
                ACLMessage cfp = new ACLMessage(CommunicationHelper.HOLON_FEEDBACK);

                cfp.addReceiver(aids[0]);
                try {
                    cfp.setContentObject("");
                    executionUnitAgent.send(cfp);
                } catch (IOException e) {
                    logger.error("EunitCreationBehaviour - IOException " + e.getMessage());
                }

            } catch (UnreadableException e1) {
                logger.error(this.executionUnitAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

        } else {
            block();
        }
    }
}
