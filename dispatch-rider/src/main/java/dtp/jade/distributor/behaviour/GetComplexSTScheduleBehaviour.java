package dtp.jade.distributor.behaviour;

import algorithm.Schedule;
import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetComplexSTScheduleBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetComplexSTScheduleBehaviour.class);

    private final DistributorAgent agent;

    public GetComplexSTScheduleBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.HOLONS_CALENDAR.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                Schedule schedule = (Schedule) msg.getContentObject();
                if (schedule != null && schedule.getRefreshCurrentLocation()) {
                    agent.putEUnitSchedule(msg.getSender(), schedule);
                } else {
                    agent.addComplexSTSchedule(schedule, msg.getSender());
                }
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
