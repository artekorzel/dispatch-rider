package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetGraphChangedBehaviour extends CyclicBehaviour {

    private final GUIAgent guiAgent;

    public GetGraphChangedBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.GRAPH_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                guiAgent.graphChanged((Boolean) msg.getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {

            block();
        }
    }
}
