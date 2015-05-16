package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.TestConfiguration;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class ConfigurationReceivedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(SimulationAgent.class);
    private SimulationAgent agent;

    public ConfigurationReceivedBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.CONFIGURATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.nextTest((TestConfiguration) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }
    }
}
