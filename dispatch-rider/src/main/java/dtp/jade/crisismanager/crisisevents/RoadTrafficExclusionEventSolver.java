package dtp.jade.crisismanager.crisisevents;

import dtp.jade.AgentsService;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;

public class RoadTrafficExclusionEventSolver {

    private RoadTrafficExclusionEvent event;

    private CrisisManagerAgent CMAgent;

    public RoadTrafficExclusionEventSolver(RoadTrafficExclusionEvent event, CrisisManagerAgent agent) {

        this.event = event;
        CMAgent = agent;
    }

    public RoadTrafficExclusionEvent getEvent() {

        return event;
    }

    public void solve() {

        sendRoadTrafficExlusionInfoToEUnits();
    }

    private void sendRoadTrafficExlusionInfoToEUnits() {
        AID[] aids = AgentsService.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (AID aid : aids) {
            CMAgent.sentCrisisEvent(aid, CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
