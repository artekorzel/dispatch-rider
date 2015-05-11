package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import measure.Measure;
import org.apache.log4j.Logger;

public class VisualisationMeasureUpdateBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(VisualisationMeasureUpdateBehaviour.class);
    private final GUIAgent guiAgent;

    public VisualisationMeasureUpdateBehaviour(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.VISUALISATION_MEASURE_UPDATE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.visualisationMeasuresUpdate((Measure) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
