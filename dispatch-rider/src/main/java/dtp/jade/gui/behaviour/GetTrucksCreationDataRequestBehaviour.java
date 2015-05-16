package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GetTrucksCreationDataRequestBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(SimulationAgent.class);
    private GUIAgent agent;

    public GetTrucksCreationDataRequestBehaviour(GUIAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRUCKS_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.loadTrucksProperties();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }
    }
}

