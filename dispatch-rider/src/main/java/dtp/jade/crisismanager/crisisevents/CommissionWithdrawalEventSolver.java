package dtp.jade.crisismanager.crisisevents;

import dtp.jade.AgentsService;
import dtp.jade.MessageType;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;

public class CommissionWithdrawalEventSolver {

    private CommissionWithdrawalEvent event;

    private CrisisManagerAgent CMAgent;

    public CommissionWithdrawalEventSolver(CommissionWithdrawalEvent event, CrisisManagerAgent agent) {
        this.event = event;
        CMAgent = agent;
    }

    public CommissionWithdrawalEvent getEvent() {
        return event;
    }

    public void solve() {
        sendWithdrawalInfoToEUnits();
    }

    private void sendWithdrawalInfoToEUnits() {
        AID[] aids = AgentsService.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (AID aid : aids) {
            CMAgent.sentCrisisEvent(aid, MessageType.CRISIS_EVENT, event);
        }
    }
}
