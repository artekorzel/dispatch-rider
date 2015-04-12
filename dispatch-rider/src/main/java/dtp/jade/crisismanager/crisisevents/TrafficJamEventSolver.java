package dtp.jade.crisismanager.crisisevents;

import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;

public class TrafficJamEventSolver {

    private TrafficJamEvent event;

    private CrisisManagerAgent CMAgent;

    public TrafficJamEventSolver(TrafficJamEvent event, CrisisManagerAgent agent) {

        this.event = event;
        CMAgent = agent;
    }

    public TrafficJamEvent getEvent() {

        return event;
    }

    public void solve() {

        sendTrafficJamInfoToEUnits();
    }

    private void sendTrafficJamInfoToEUnits() {

        AID[] aids;

        aids = CommunicationHelper.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (int i = 0; i < aids.length; i++) {

            CMAgent.sentCrisisEvent(aids[i], CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
