package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

/**
 * @author KONY
 */
public class GetCalendarBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetCalendarBehaviour.class);

    private GUIAgent guiAgent;

    public GetCalendarBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.EUNIT_MY_CALENDAR.name());
        ACLMessage msg = myAgent.receive(template);

        String calendar;

        if (msg != null) {
            try {
                calendar = (String) msg.getContentObject();
                guiAgent.addCalendar(msg.getSender().getLocalName(), calendar);
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
