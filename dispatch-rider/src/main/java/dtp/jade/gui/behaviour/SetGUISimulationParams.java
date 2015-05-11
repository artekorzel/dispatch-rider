package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import gui.parameters.DRParams;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class SetGUISimulationParams extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(SetGUISimulationParams.class);
    private GUIAgent guiAgent;

    public SetGUISimulationParams(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.GUI_SIMULATION_PARAMS.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.setGUISimulationParams((DRParams) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {
            block();
        }

    }
}
