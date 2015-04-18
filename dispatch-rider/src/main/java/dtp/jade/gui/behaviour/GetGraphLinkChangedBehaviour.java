package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetGraphLinkChangedBehaviour extends CyclicBehaviour {

    private final GUIAgent guiAgent;

    public GetGraphLinkChangedBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.GRAPH_LINK_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            guiAgent.linkChanged();
        } else {
            block();
        }
    }
}
