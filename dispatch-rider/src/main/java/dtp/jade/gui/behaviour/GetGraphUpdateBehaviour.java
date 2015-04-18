package dtp.jade.gui.behaviour;

import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetGraphUpdateBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetGraphUpdateBehaviour.class);

    private GUIAgent guiAgent;

    public GetGraphUpdateBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.GRAPH_UPDATE);
        ACLMessage msg = myAgent.receive(template);

        Graph graph;

        if (msg != null) {
            try {
                graph = (Graph) msg.getContentObject();
                guiAgent.updateGraph(graph);
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }

}
