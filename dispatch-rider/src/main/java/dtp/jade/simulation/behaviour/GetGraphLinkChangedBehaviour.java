package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetGraphLinkChangedBehaviour extends CyclicBehaviour {

    private final SimulationAgent agent;

    public GetGraphLinkChangedBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.GRAPH_LINK_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.linkChanged();
        } else {
            block();
        }
    }
}
