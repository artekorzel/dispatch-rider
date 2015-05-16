package dtp.jade.gui.behaviour;

import dtp.graph.Graph;
import dtp.jade.MessageType;
import gui.main.SingletonGUI;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GraphChangedBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(GraphChangedBehaviour.class);

    public GraphChangedBehaviour() {
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.GRAPH_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                SingletonGUI.getInstance().update((Graph) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }

    }
}
