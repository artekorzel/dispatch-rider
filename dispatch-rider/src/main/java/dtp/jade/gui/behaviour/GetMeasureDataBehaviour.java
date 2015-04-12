package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import measure.printer.MeasureData;
import org.apache.log4j.Logger;

public class GetMeasureDataBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger
            .getLogger(GetMeasureDataBehaviour.class);

    private final dtp.jade.gui.GUIAgent GUIAgent;

    public GetMeasureDataBehaviour(GUIAgent agent) {

        this.GUIAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.MEASURE_DATA);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                GUIAgent.printMeasures((MeasureData) msg.getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        } else {

            block();
        }
    }
}
