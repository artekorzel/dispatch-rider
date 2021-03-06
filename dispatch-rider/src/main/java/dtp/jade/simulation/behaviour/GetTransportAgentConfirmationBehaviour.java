package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetTransportAgentConfirmationBehaviour extends CyclicBehaviour {

    private SimulationAgent agent;

    public GetTransportAgentConfirmationBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRANSPORT_AGENT_CONFIRMATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.transportAgentConfirmationOfReceivingAgentsData();
        } else {
            block();
        }
    }
}
