package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTrailersCreationDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(SimulationAgent.class);
    private SimulationAgent agent;

    public GetTrailersCreationDataBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRAILERS_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.nextTestCreateTrailers((TransportElementInitialDataTrailer[]) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }
    }
}
