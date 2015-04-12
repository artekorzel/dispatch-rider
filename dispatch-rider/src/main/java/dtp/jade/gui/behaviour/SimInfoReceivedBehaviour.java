package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimInfoReceivedBehaviour extends CyclicBehaviour {




    public SimInfoReceivedBehaviour(GUIAgent agent) {
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO_RECEIVED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {


        } else {
            block();
        }
    }

}
