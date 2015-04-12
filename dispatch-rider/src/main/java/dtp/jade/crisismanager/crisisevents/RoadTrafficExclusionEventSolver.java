package dtp.jade.crisismanager.crisisevents;

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

        AID[] aids;

        aids = CommunicationHelper.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (int i = 0; i < aids.length; i++) {

            CMAgent.sentCrisisEvent(aids[i], CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
