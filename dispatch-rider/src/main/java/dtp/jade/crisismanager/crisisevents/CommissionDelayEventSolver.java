package dtp.jade.crisismanager.crisisevents;

import dtp.jade.AgentsService;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;

public class CommissionDelayEventSolver {

    private CommissionDelayEvent event;

    private CrisisManagerAgent CMAgent;

    public CommissionDelayEventSolver(CommissionDelayEvent event, CrisisManagerAgent agent) {

        this.event = event;
        CMAgent = agent;
    }

    public CommissionDelayEvent getEvent() {

        return event;
    }

    public void solve() {

        sendDelayInfoToEUnits();
    }

    private void sendDelayInfoToEUnits() {
        AID[] aids = AgentsService.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (AID aid : aids) {
            CMAgent.sentCrisisEvent(aid, CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
