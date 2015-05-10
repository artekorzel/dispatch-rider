package dtp.jade.crisismanager.crisisevents;

import dtp.jade.AgentsService;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;

public class EUnitFailureEventSolver {

    private EUnitFailureEvent event;

    private CrisisManagerAgent CMAgent;

    public EUnitFailureEventSolver(EUnitFailureEvent event, CrisisManagerAgent agent) {

        this.event = event;
        CMAgent = agent;
    }

    public EUnitFailureEvent getEvent() {

        return event;
    }

    public void solve() {

        sendVehicleFailureInfoToEUnits();
    }

    private void sendVehicleFailureInfoToEUnits() {
        AID[] aids = AgentsService.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (AID aid : aids) {
            CMAgent.sentCrisisEvent(aid, CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
