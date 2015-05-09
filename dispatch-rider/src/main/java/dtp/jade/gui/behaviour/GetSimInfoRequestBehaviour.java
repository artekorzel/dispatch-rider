package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetSimInfoRequestBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;

    public GetSimInfoRequestBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.SIM_INFO.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            guiAgent.sendSimInfo(msg.getSender());
        } else {
            block();
        }
    }
}
