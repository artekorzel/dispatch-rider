package dtp.jade.crisismanager.crisisevents;

import dtp.jade.AgentsService;
import dtp.jade.MessageType;
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
        AID[] aids = AgentsService.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (AID aid : aids) {
            CMAgent.sentCrisisEvent(aid, MessageType.CRISIS_EVENT, event);
        }
    }
}
