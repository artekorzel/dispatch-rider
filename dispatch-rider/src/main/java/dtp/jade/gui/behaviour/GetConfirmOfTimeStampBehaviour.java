package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetConfirmOfTimeStampBehaviour extends CyclicBehaviour {
    protected static Logger logger = Logger.getLogger(GetConfirmOfTimeStampBehaviour.class);

    private GUIAgent guiAgent;

    public GetConfirmOfTimeStampBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TIME_STAMP_CONFIRM);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
//            logger.info(msg.getSender().getName() + " - stampConfirmed");
            guiAgent.stampConfirmed();
        } else {
            block();
        }
    }
}
