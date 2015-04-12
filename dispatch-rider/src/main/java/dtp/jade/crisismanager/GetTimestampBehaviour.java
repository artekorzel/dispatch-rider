package dtp.jade.crisismanager;

import dtp.jade.CommunicationHelper;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GetTimestampBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTimestampBehaviour.class);

    private CrisisManagerAgent crisisManagerAgent;

    public GetTimestampBehaviour(CrisisManagerAgent agent) {

        crisisManagerAgent = agent;
    }

    public void action() {

        /*-------- RECIEVING CURRENT TIME STAMP -------*/
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TIME_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            Integer time = null;
            try {

                time = (Integer) msg.getContentObject();

            } catch (UnreadableException e) {

                logger.error(this.crisisManagerAgent.getLocalName() + " - IOException " + e.getMessage());
            }

            logger.info(myAgent.getLocalName() + "\t- got time stamp [" + time.toString() + "]");

            crisisManagerAgent.nextSimstep(time);

            AID[] aids = CommunicationHelper.findAgentByServiceName(crisisManagerAgent, "GUIService");
            ACLMessage cfp = new ACLMessage(CommunicationHelper.TIME_STAMP_CONFIRM);

            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject("");
                crisisManagerAgent.send(cfp);
            } catch (IOException e) {
                logger.error("GetTimestampBehaviour (CrisisManager) - IOException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
