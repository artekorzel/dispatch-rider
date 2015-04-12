package dtp.jade.test.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import dtp.jade.test.TestAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimInfoReceivedBehaviour extends CyclicBehaviour {


    private final GUIAgent agent;

    public SimInfoReceivedBehaviour(GUIAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO_RECEIVED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            ((TestAgent) agent).simInfoReceived();

        } else {
            block();
        }
    }

}
