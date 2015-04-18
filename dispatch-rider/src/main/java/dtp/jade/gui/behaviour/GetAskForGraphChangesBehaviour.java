package dtp.jade.gui.behaviour;

import dtp.graph.GraphLink;
import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetAskForGraphChangesBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetAskForGraphChangesBehaviour.class);

    private final GUIAgent guiAgent;

    public GetAskForGraphChangesBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.ASK_IF_GRAPH_LINK_CHANGED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.addChangedLink((GraphLink) msg.getContentObject());

            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName()
                        + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
