package dtp.jade.eunit.behaviour;

import dtp.graph.Graph;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetGraphUpdateBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetGraphBehaviour.class);

    private final ExecutionUnitAgent eunitAgent;

    public GetGraphUpdateBehaviour(ExecutionUnitAgent agent) {
        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.GRAPH_UPDATE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                Graph graph = (Graph) msg.getContentObject();

                logger.info(this.eunitAgent.getLocalName() + " - graph update received");

                eunitAgent.updateGraph(graph);

            } catch (UnreadableException e) {
                logger.error(this.eunitAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
