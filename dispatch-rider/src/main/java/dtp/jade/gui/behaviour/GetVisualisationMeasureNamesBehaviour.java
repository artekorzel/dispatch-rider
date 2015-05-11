package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetVisualisationMeasureNamesBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(GetVisualisationMeasureNamesBehaviour.class);
    private final GUIAgent guiAgent;

    public GetVisualisationMeasureNamesBehaviour(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.VISUALISATION_MEASURE_NAMES.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.setVisualisationMeasures((String[]) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
