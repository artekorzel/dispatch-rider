package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimInfoReceivedBehaviour extends CyclicBehaviour {

    private final GUIAgent agent;

    public SimInfoReceivedBehaviour(GUIAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIM_INFO_RECEIVED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.simInfoReceived();
        } else {
            block();
        }
    }
}
