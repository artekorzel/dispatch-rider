package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetWorstCommissionRequestBehaviour extends CyclicBehaviour {

    private final DistributorAgent agent;

    public GetWorstCommissionRequestBehaviour(DistributorAgent agent) {

        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.WORST_COMMISSION_COST);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.addWorstCommissionCost((CalendarStats) msg
                        .getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            block();
        }
    }
}
