package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetGraphChangedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetGraphChangedBehaviour.class);

    private final GUIAgent guiAgent;

    public GetGraphChangedBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.GRAPH_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                guiAgent.graphChanged((Boolean) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {

            block();
        }
    }
}
