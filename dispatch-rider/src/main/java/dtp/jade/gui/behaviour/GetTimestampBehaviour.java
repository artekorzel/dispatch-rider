package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import gui.main.SingletonGUI;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTimestampBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(GetTimestampBehaviour.class);

    public GetTimestampBehaviour() {
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.GUI_SIMULATION_PARAMS.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                SingletonGUI.getInstance().newTimestamp((Integer) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {
            block();
        }

    }
}
