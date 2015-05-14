package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;
import xml.elements.SimulationData;

/**
 * @author kony.pl
 */
public class GetSimulationDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetSimulationDataBehaviour.class);

    private SimulationAgent agent;

    public GetSimulationDataBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIMULATION_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.addSimulationData((SimulationData) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {

            block();
        }
    }
}
