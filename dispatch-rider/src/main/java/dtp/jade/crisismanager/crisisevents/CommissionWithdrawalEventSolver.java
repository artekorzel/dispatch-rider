package dtp.jade.crisismanager.crisisevents;

import dtp.jade.CommunicationHelper;
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

        AID[] aids;

        aids = CommunicationHelper.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (int i = 0; i < aids.length; i++) {

            CMAgent.sentCrisisEvent(aids[i], CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
