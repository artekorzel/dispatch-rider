package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GetTimestampBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger
            .getLogger(GetTimestampBehaviour.class);

    private final ExecutionUnitAgent eunitAgent;

    public GetTimestampBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    @Override
    public void action() {

        /*-------- RECIEVING CURRENT TIME STAMP -------*/
        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.TIME_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            Integer time = null;
            try {

                time = (Integer) msg.getContentObject();
                eunitAgent.setCurrentTimestamp(time);
                eunitAgent.nextSimStep();

            } catch (UnreadableException e) {

                logger.error(this.eunitAgent.getLocalName() + " - IOException "
                        + e.getMessage());
            }

            logger.info(myAgent.getLocalName() + "\t- got time stamp ["
                    + time + "]");

            AID[] aids = CommunicationHelper.findAgentByServiceName(eunitAgent,
                    "GUIService");
            ACLMessage cfp = new ACLMessage(
                    CommunicationHelper.TIME_STAMP_CONFIRM);

            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject("");
                eunitAgent.send(cfp);
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
