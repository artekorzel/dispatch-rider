package dtp.jade.gui.behaviour;

/**
 * @author Szyna
 */

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetConfirmUpdateCurrentLocation extends CyclicBehaviour {

    private final GUIAgent gui;

    public GetConfirmUpdateCurrentLocation(GUIAgent agent) {
        gui = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.UPDATE_CURRENT_LOCATION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            //GUIAgent.updateCurrentLocationSemaphore.release(1);
        } else {
            block();
        }
    }
}