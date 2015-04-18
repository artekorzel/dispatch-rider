package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetConfirmOfTimeStampBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;

    public GetConfirmOfTimeStampBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TIME_STAMP_CONFIRM);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            guiAgent.stampConfirmed();
        } else {
            block();
        }
    }
}
