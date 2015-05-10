package dtp.jade.eunit.behaviour;

import dtp.graph.GraphLink;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

import java.util.LinkedList;

/**
 * @author KONY
 */
public class GetGraphLinkChangedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetGraphLinkChangedBehaviour.class);

    private final ExecutionUnitAgent executionUnitAgent;

    public GetGraphLinkChangedBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.GRAPH_LINK_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                executionUnitAgent.changeGraphLinks(
                        (LinkedList<GraphLink>) msg.getContentObject(),
                        msg.getSender());
            } catch (UnreadableException e) {
                logger.error(e);
            }

        } else {

            block();
        }
    }
}
