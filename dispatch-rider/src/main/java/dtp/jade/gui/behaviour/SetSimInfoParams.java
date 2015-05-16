package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import dtp.simulation.SimInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class SetSimInfoParams extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(SetGUISimulationParams.class);
    private GUIAgent guiAgent;

    public SetSimInfoParams(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.SIM_INFO.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.setSimulationInfoParams((SimInfo) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }

    }
}