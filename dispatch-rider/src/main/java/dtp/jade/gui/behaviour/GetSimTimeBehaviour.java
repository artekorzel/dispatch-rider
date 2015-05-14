package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetSimTimeBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;

    public GetSimTimeBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIM_TIME.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            guiAgent.setSimTime(Long.parseLong(msg.getContent()));
        } else {
            block();
        }
    }
}
