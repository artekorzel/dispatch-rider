package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import measure.printer.MeasureData;
import org.apache.log4j.Logger;

public class GetMeasureDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetMeasureDataBehaviour.class);

    private final dtp.jade.gui.GUIAgent guiAgent;

    public GetMeasureDataBehaviour(GUIAgent agent) {

        this.guiAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.MEASURE_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.printMeasures((MeasureData) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {

            block();
        }
    }
}
