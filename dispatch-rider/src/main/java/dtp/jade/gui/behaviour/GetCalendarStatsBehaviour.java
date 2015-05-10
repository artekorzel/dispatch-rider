package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetCalendarStatsBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetCalendarStatsBehaviour.class);

    private GUIAgent guiAgent;

    public GetCalendarStatsBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.EUNIT_MY_STATS.name());
        ACLMessage msg = myAgent.receive(template);

        CalendarStats calendarStats;

        if (msg != null) {
            try {
                calendarStats = (CalendarStats) msg.getContentObject();
                guiAgent.addCalendarStats(calendarStats);
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
