package dtp.jade.simulation.behaviour;

import dtp.graph.GraphLink;
import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetAskForGraphChangesBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetAskForGraphChangesBehaviour.class);

    private final SimulationAgent agent;

    public GetAskForGraphChangesBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.ASK_IF_GRAPH_LINK_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.addChangedLink((GraphLink) msg.getContentObject());

            } catch (UnreadableException e) {
                logger.error(this.agent.getLocalName()
                        + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
