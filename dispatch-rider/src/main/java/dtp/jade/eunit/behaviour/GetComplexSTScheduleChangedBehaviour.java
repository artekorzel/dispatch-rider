package dtp.jade.eunit.behaviour;

import algorithm.Schedule;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetComplexSTScheduleChangedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetComplexSTScheduleChangedBehaviour.class);

    private ExecutionUnitAgent agent;

    public GetComplexSTScheduleChangedBehaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.HOLONS_NEW_CALENDAR.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.setNewSchedule((Schedule) msg.getContentObject(), msg.getSender());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
