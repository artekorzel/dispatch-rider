package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimInfoReceivedBehaviour extends CyclicBehaviour {

    private final SimulationAgent agent;

    public SimInfoReceivedBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIM_INFO_RECEIVED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.simInfoReceived();
        } else {
            block();
        }
    }
}
