package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import xml.elements.SimmulationData;

/**
 * @author kony.pl
 */
public class GetSimulationDataBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;

    public GetSimulationDataBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIMULATION_DATA);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                //System.err.println(msg.getSender() + " " + ((SimmulationData) msg.getContentObject()).getLocation());
                guiAgent.addSimmulationData((SimmulationData) msg.getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {

            block();
        }
    }
}
