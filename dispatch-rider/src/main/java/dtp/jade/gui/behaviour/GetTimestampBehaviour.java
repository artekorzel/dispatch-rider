package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import gui.main.SingletonGUI;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTimestampBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(GetTimestampBehaviour.class);
    private GUIAgent agent;

    public GetTimestampBehaviour(GUIAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.TIME_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                Integer time = (Integer) msg.getContentObject();
                SingletonGUI.getInstance().newTimestamp(time);
                logger.info(myAgent.getLocalName() + "\t- got time stamp [" + time + "]");
                agent.send(msg.getSender(), "", MessageType.TIME_STAMP_CONFIRM);
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }

    }
}
