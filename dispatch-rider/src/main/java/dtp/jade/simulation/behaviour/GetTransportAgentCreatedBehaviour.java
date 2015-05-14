package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetTransportAgentCreatedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportAgentCreatedBehaviour.class);

    private SimulationAgent agent;

    public GetTransportAgentCreatedBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRANSPORT_AGENT_CREATED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            logger.info("new TransportAgent was created ");
            agent.transportAgentCreated();
        } else {
            block();
        }
    }
}
