package dtp.jade.crisismanager;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.crisisevents.*;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CrisisManagerAgent extends Agent {

    private static Logger logger = Logger.getLogger(CrisisManagerAgent.class);

    ArrayList<CrisisEvent> eventsHolder;

    ArrayList<CommissionWithdrawalEventSolver> commissionWithdrawalEventSolvers;

    ArrayList<CommissionDelayEventSolver> commissionDelayEventSolvers;

    ArrayList<EUnitFailureEventSolver> eUnitFailureEventSolvers;

    ArrayList<TrafficJamEventSolver> trafficJamEventSolvers;

    ArrayList<RoadTrafficExclusionEventSolver> roadTrafficExclusionEventSolvers;

    protected void setup() {
        logger.info(this.getLocalName() + " - Hello World!");

        /*-------- INITIALIZATION SECTION -------*/

        /*-------- SERVICES SECTION -------*/
        registerServices();

        /*-------- BEHAVIOURS SECTION -------*/
        addBehaviour(new GetTimestampBehaviour(this));
        addBehaviour(new GetCrisisEventBehaviour(this));
        addBehaviour(new GetCrisisEventFinalFeedbackBehaviour(this));
        addBehaviour(new EndOfSimulationBehaviour(this));

        eventsHolder = new ArrayList<>();

        commissionWithdrawalEventSolvers = new ArrayList<>();
        commissionDelayEventSolvers = new ArrayList<>();
        eUnitFailureEventSolvers = new ArrayList<>();
        trafficJamEventSolvers = new ArrayList<>();
        roadTrafficExclusionEventSolvers = new ArrayList<>();


        System.out.println("CrisisManagementAgent - end of initialization");
    }

    public void simEnd() {
        eventsHolder = new ArrayList<>();

        commissionWithdrawalEventSolvers = new ArrayList<>();
        commissionDelayEventSolvers = new ArrayList<>();
        eUnitFailureEventSolvers = new ArrayList<>();
        trafficJamEventSolvers = new ArrayList<>();
        roadTrafficExclusionEventSolvers = new ArrayList<>();
    }

    public void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /*--------- CRISIS MANAGEMENT SERVICE ---------*/
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("CrisisManagementService");
        sd1.setName("CrisisManagementService");
        dfd.addServices(sd1);
        logger.info(this.getLocalName() + " - registering CrisisManagementService");

        /*--------- REGISTRATION ---------*/
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException " + fe.getMessage());
        }
    }

    public void nextSimstep(int simstep) {

        ArrayList<CrisisEvent> eventsOnTime;
        Iterator<CrisisEvent> iter;

        eventsOnTime = getEventsOnTime(simstep);

        iter = eventsOnTime.iterator();
        while (iter.hasNext()) {

            createNewCrisisEventSolver(iter.next());
        }
    }

    public void addCrisisEvent(CrisisEvent event) {

        eventsHolder.add(event);

        sendGUIMessage("new crisis event added to the list \n" + "\t" + event.toString());
        logg("new crisis event added to the list \n" + "\t" + event.toString());
    }

    private void createNewCrisisEventSolver(CrisisEvent event) {

        sendGUIMessage("creating new crisis event solver for event: \n" + "\t" + event.toString());
        logg("creating new crisis event solver for event: \n" + "\t" + event.toString());

        if (event.getClass().equals(CommissionWithdrawalEvent.class)) {

            CommissionWithdrawalEventSolver solver = new CommissionWithdrawalEventSolver(
                    (CommissionWithdrawalEvent) event, this);

            commissionWithdrawalEventSolvers.add(solver);
            solver.solve();

        } else if (event.getClass().equals(CommissionDelayEvent.class)) {

            CommissionDelayEventSolver solver = new CommissionDelayEventSolver((CommissionDelayEvent) event, this);

            commissionDelayEventSolvers.add(solver);
            solver.solve();

        } else if (event.getClass().equals(EUnitFailureEvent.class)) {

            EUnitFailureEventSolver solver = new EUnitFailureEventSolver((EUnitFailureEvent) event, this);

            eUnitFailureEventSolvers.add(solver);
            solver.solve();

        } else if (event.getClass().equals(TrafficJamEvent.class)) {

            TrafficJamEventSolver solver = new TrafficJamEventSolver((TrafficJamEvent) event, this);

            trafficJamEventSolvers.add(solver);
            solver.solve();

        } else if (event.getClass().equals(RoadTrafficExclusionEvent.class)) {

            RoadTrafficExclusionEventSolver solver = new RoadTrafficExclusionEventSolver(
                    (RoadTrafficExclusionEvent) event, this);

            roadTrafficExclusionEventSolvers.add(solver);
            solver.solve();

        } else {

            logg("unknown event type [" + event.getEventType() + "] \n\t");
        }
    }

    private ArrayList<CrisisEvent> getEventsOnTime(int timestamp) {

        ArrayList<CrisisEvent> eventsOnTime;
        Iterator<CrisisEvent> iter;
        CrisisEvent tmpEvent;

        eventsOnTime = new ArrayList<>();

        iter = eventsHolder.iterator();
        while (iter.hasNext()) {

            tmpEvent = iter.next();
            if (tmpEvent.getEventTime() == timestamp) {

                eventsOnTime.add(tmpEvent);
            }
        }

        eventsHolder.removeAll(eventsOnTime);

        return eventsOnTime;
    }

    public void sentCrisisEvent(AID aid, int perf, CrisisEvent crisisEvent) {

        logg("sending crisis event to " + aid.getLocalName() + " (event ID = " + crisisEvent.getEventID() + ")");

        ACLMessage cfp;

        cfp = new ACLMessage(perf);
        cfp.addReceiver(aid);

        try {

            cfp.setContentObject(crisisEvent);

        } catch (IOException e) {

            logger.error(getLocalName() + " - IOException " + e.getMessage());
            return;
        }

        send(cfp);
    }

    public void addCrisisEventFinalFeedback(CrisisEventFinalFeedback crisisEventFinalFeedback) {

        logg("got crisis event final feedback: \n" + "\t" + crisisEventFinalFeedback.toString());
        sendGUIMessage("got crisis event final feedback: \n" + "\t" + crisisEventFinalFeedback.toString());

        if (crisisEventFinalFeedback.getComs4Auction().length > 0) {

            sendCommissions2Auction(crisisEventFinalFeedback.getComs4Auction());
        }
    }

    private void sendCommissions2Auction(Commission[] commissions) {

        AID[] aids = CommunicationHelper.findAgentByServiceName(this, "CommissionService");

        logg("sending " + commissions.length + " commission(s) to Distributor Agent");

        if (aids.length == 1) {

            ACLMessage cfp = new ACLMessage(CommunicationHelper.COMMISSION);
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject(commissions);

            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);

            sendGUIMessage(commissions.length + " commission(s) sent to Distributor Agent");

        } else if (aids.length == 0) {

            logger.error("There is no Distributor Agent in the system");
        } else {

            logger.error("More than one Distributor Agent in the system");
        }
    }

    private void sendGUIMessage(String messageText) {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this, "GUIService");

        if (aids.length == 1) {
            for (AID aid : aids) {

                cfp = new ACLMessage(CommunicationHelper.GUI_MESSAGE);
                cfp.addReceiver(aid);
                try {
                    cfp.setContentObject(getLocalName() + " - " + messageText);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException " + e.getMessage());
                }
                send(cfp);
            }
        } else {
            logger.error(getLocalName() + " - none or more than one agent with GUIService in the system");
        }
    }

    public void logg(String message) {
        logger.info(getLocalName() + " - " + message);
    }
}
