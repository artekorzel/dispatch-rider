package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;


public class GetCalendarStatsBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetCalendarStatsBehaviour.class);

    private DistributorAgent agent;

    public GetCalendarStatsBehaviour(DistributorAgent agent) {

        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.EUNIT_MY_STATS.name());
        ACLMessage msg = myAgent.receive(template);

        CalendarStats calendarStats;

        if (msg != null) {

            try {

                calendarStats = (CalendarStats) msg.getContentObject();
                agent.addCalendarStats(calendarStats);

            } catch (UnreadableException e) {

                logger.error(this.agent.getLocalName() + " - UnreadableException ", e);
            }

        } else {

            block();
        }
    }
}
