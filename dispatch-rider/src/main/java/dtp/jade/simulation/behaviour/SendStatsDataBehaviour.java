package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SendStatsDataBehaviour extends CyclicBehaviour {

    private final SimulationAgent agent;

    public SendStatsDataBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.STATS_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.sendStatsData(msg.getSender());
        } else {
            block();
        }
    }
}
