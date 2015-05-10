package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetCalendarRequestToFileWriteBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent executionUnitAgent;

    public GetCalendarRequestToFileWriteBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.EUNIT_SHOW_STATS_TO_WRITE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            executionUnitAgent.sendCalendarStatsToFile();

        } else {
            block();
        }
    }
}
