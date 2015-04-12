package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author KONY
 */
public class GetCalendarStatsRequestBehaviour extends CyclicBehaviour {


    private final ExecutionUnitAgent executionUnitAgent;

    public GetCalendarStatsRequestBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_SHOW_STATS);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            executionUnitAgent.sendCalendarStats(msg.getSender());

        } else {

            block();
        }
    }
}
