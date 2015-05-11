package dtp.jade.distributor;

import algorithm.*;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.simulatedTrading.SimulatedTradingParameters;
import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.behaviour.*;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.EUnitOffer;
import dtp.jade.eunit.behaviour.GetCalendarStatsBehaviour;
import dtp.jade.gui.CalendarStatsHolder;
import dtp.jade.gui.CommissionsHolder;
import dtp.jade.gui.DefaultAgentsData;
import dtp.jade.transport.*;
import dtp.simulation.SimInfo;
import dtp.util.AIDsComparator;
import gui.main.SingletonGUI;
import gui.parameters.DRParams;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import machineLearning.MLAlgorithm;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorsHolder;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import measure.printer.MeasureData;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a distributor as a jade agent.
 *
 * @author kony.pl
 */
public class DistributorAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(DistributorAgent.class);
    private static Map<AID, Schedule> holons;
    Map<TransportType, List<TransportAgentData>> agents;
    // kolejka zlecen do obsluzenia
    private LinkedList<Commission> commissionsQueue;
    private ArrayList<Commission> nooneList;
    private Auction currentAuction;
    // Simmulated Trading auction
    private AuctionST currentSTAuction;
    private List<Commission> commissions;
    // private double pattern=0.0;
    private String chooseWorstCommission;
    private int timestamp;
    private int STTimestampGap;
    private int nextSTTimestamp;
    private int nextMeasureTimestamp;
    private int STCommissionsGap;
    private MeasureData measureData;
    private boolean isConfigurationChangeable = true;
    private SimInfo simInfo;
    private MeasureCalculatorsHolder calculatorsHolder;
    private MLAlgorithm mlAlgorithm;
    private int handledCommissionsCount = 0;
    private boolean commissionSendingType;
    private boolean choosingByCost;
    private int simulatedTradingCount;
    private int STDepth;
    private Commission[] commsGroup;
    private DefaultAgentsData defaultAgentsData;
    private boolean dist;
    private boolean confSet = false;
    private Commission currentCommission;
    private CalendarStatsHolder calendarStatsHolder;
    private boolean simulatedTrading;
    private int transportUnitsPrepare;
    private int transportUnitCount;
    private Set<NewHolonOffer> newHolonOffers;
    private List<NewHolonOffer> newHolonOffersList;
    private NewHolonOffer bestOffer;
    private double bestCost;
    private int eUnitsCount;
    private List<EUnitOffer> eUnitOffers;
    private int transportAgentsCount;
    private NewTeamData newTeamData;
    private boolean defaultEUnitCreation;
    private Commission complexSTCommission;
    private int maxFullSTDepth = 8;
    private boolean fullSimulatedTrading = false;
    private Algorithm algorithm;
    private boolean calendarsForMeasures = false;
    private Map<AID, Schedule> oldSchedule;
    private Map<AID, Schedule> newSchedule;
    private boolean graphChanged = false;
    private boolean shouldSendVisualisationMeasureData = false;

    public static Map<AID, Schedule> getEUnits() {
        return holons;
    }

    public static double calculateCost(
            TransportElementInitialDataTruck truckData,
            TransportElementInitialDataTrailer trailerData,
            TransportElementInitialData driver, double distance,
            Commission commission) {
        double truckValue = TransportAgent.costFunctionValue(
                truckData.getCostFunction(), distance, driver, truckData,
                trailerData, commission, null);
        double trailerValue = TransportAgent.costFunctionValue(
                trailerData.getCostFunction(), distance, driver, truckData,
                trailerData, commission, null);
        double driverValue = TransportAgent.costFunctionValue(
                driver.getCostFunction(), distance, driver, truckData,
                trailerData, commission, null);
        return truckValue + trailerValue + driverValue;
    }

    public void putEUnitSchedule(AID sender, Schedule schedule) {
        holons.put(sender, schedule);
    }

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " - Hello World!");

        /* -------- INITIALIZATION SECTION ------- */
        commissionsQueue = new LinkedList<>();
        nooneList = new ArrayList<>();
        currentAuction = null;
        currentSTAuction = null;

        /* -------- SERVICES SECTION ------- */
        registerServices();

        /* -------- BEHAVIOURS SECTION ------- */
        addBehaviour(new GetCommissionBehaviour(this));
        addBehaviour(new GetOfferBehaviour(this));
        addBehaviour(new GetResetRequestBehaviour(this));
        addBehaviour(new GetNooneRequestBehaviour(this));
        addBehaviour(new SimEndBehaviour(this));
        addBehaviour(new GetTransportUnitPrepareForNegotiationBehaviour(this));
        addBehaviour(new GetNewHolonOfferBehaviour(this));
        addBehaviour(new GetTransportAgentsDataBehaviour(this));
        addBehaviour(new GetHolonFeedbackOfferBehaviour(this));
        addBehaviour(new GetSimInfoBehaviour(this));
        addBehaviour(new GetEUnitCreatedBehaviour(this));
        addBehaviour(new GetCommissionSendedAgainBehaviour(this));
        addBehaviour(new GetCalendarStatsBehaviour(this));
        addBehaviour(new GetSTBeginResponseBehaviour(this));
        addBehaviour(new GetComplexSTScheduleBehaviour(this));
        addBehaviour(new GetComplexSTScheduleChangedBehaviour(this));
        addBehaviour(new GetChangeScheduleBehaviour(this));
        addBehaviour(new GetTimestampBehaviour(this));
        addBehaviour(new GetUndeliveredCommissionResponseBehaviour(this));

        addBehaviour(new GetMeasureDataBehaviour(this));
        addBehaviour(new GetConfigurationChangeBehaviour(this));
        addBehaviour(new GetMLTableBehaviour(this));

        addBehaviour(new GetGraphChangedBehaviour(this));

        calendarStatsHolder = null;
        transportUnitsPrepare = 0;
        transportUnitCount = -1;
        simulatedTradingCount = 0;
        nextSTTimestamp = 0;
        nextMeasureTimestamp = 0;
        measureData = new MeasureData();
        logger.info("DistributorAgent - end of initialization");
    }

    public void simEnd() {
        resetAgent();
    }

    public void nextSimstep(int timestamp) {
        this.timestamp = timestamp;
        if (timestamp >= nextSTTimestamp + STTimestampGap) {
            nextSTTimestamp += STTimestampGap;
        }
        if (calculatorsHolder != null
                && timestamp >= nextMeasureTimestamp
                + calculatorsHolder.getTimeGap())
            nextMeasureTimestamp += calculatorsHolder.getTimeGap();

    }

    /**
     * Registers Distributor Agent's services in a DF Service.
     */
    public void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /* --------- COMMISSION SERVICE --------- */
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("CommissionService");
        sd1.setName("CommissionService");
        dfd.addServices(sd1);
        logger.info(this.getLocalName() + " - registering CommissionService");

        /* --------- REGISTRATION --------- */
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException "
                    + fe.getMessage());
        }
    }

    public synchronized void setSimInfo(SimInfo simInfo) {
        this.simInfo = simInfo;
        this.calculatorsHolder = simInfo.getCalculatorsHolder();

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "GUIService");

        if (calculatorsHolder != null) {
            shouldSendVisualisationMeasureData = true;
            List<String> visualizationMeasuresNames = calculatorsHolder.getVisualizationMeasuresNames();
            send(aids, visualizationMeasuresNames.<String>toArray(new String[visualizationMeasuresNames.size()]),
                    MessageType.VISUALISATION_MEASURE_NAMES);
        }

        this.mlAlgorithm = simInfo.getMlAlgorithm();
        if (calculatorsHolder != null)
            this.calculatorsHolder.setSimInfo(simInfo);
        if (mlAlgorithm != null)
            this.mlAlgorithm.setSimInfo(simInfo);

        send(aids[0], "", MessageType.SIM_INFO_RECEIVED);
    }

    public synchronized void setAgentsData(
            Map<TransportType, List<TransportAgentData>> agents) {
        this.agents = agents;

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "GUIService");

        if (aids.length == 1) {
            send(aids[0], "", MessageType.TRANSPORT_AGENT_CONFIRMATION);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with GUIService in the system");
        }
    }

    private TransportElementInitialDataTruck getTruck(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRUCK)) {
            if (agent.getAid().equals(aid))
                return (TransportElementInitialDataTruck) agent.getData();
        }
        return null;
    }

    private TransportElementInitialDataTrailer getTrailer(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRAILER)) {
            if (agent.getAid().equals(aid))
                return (TransportElementInitialDataTrailer) agent.getData();
        }
        return null;
    }

    private TransportElementInitialData getDriver(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.DRIVER)) {
            if (agent.getAid().equals(aid))
                return agent.getData();
        }
        return null;
    }

    /**
     * Wolana po przeslaniu zlecen przez dystrybutora
     */
    public synchronized void setCommissions(Commission[] com,
                                            CommissionsHolder holder) {

        if (!confSet) {
            this.commissionSendingType = holder.getType();
            this.dist = holder.isDist();
            this.choosingByCost = holder.isChoosingByCost();
            this.simulatedTradingCount = holder.getSimulatedTrading();
            this.commsGroup = com;
            this.defaultAgentsData = holder.getDefaultAgentsData();
            this.chooseWorstCommission = holder.getChooseWorstCommission();
            this.algorithm = holder.getAlgorithm();
            this.maxFullSTDepth = holder.getSTDepth();
            this.STTimestampGap = holder.getSTTimestampGap();
            this.STCommissionsGap = holder.getSTCommissionGap();
            this.isConfigurationChangeable = holder.isConfChange();
            this.STDepth = holder.getSTDepth();
            confSet = true;

            DRParams params = new DRParams();
            params.setChoosingByCost(this.choosingByCost);
            params.setCommissionSendingType(this.commissionSendingType);
            params.setDist(this.dist);
            params.setAlgorithm("" + this.algorithm);
            params.setChooseWorstCommission(this.chooseWorstCommission);
            params.setMaxFullSTDepth(this.maxFullSTDepth);
            params.setSimulatedTradingCount(this.simulatedTradingCount);
            params.setSTCommissionsionsGap(this.STCommissionsGap);
            params.setSTTimestampGap(this.STTimestampGap);
            SingletonGUI.getInstance().update(params);
        }

        commissions = new LinkedList<>();
        simulatedTrading = false;
        Collections.addAll(commissions, com);

        Collections.sort(commissions,
                new CommissionsComparator(simInfo.getDepot()));

        if (commissions.size() > 0)
            if (!commissionSendingType)
                addCommission(commissions.remove(0));
            else
                addCommission(null);
    }

    // odbiera zlecenie od GUIAgenta...
    public synchronized void addCommission(Commission commission) {
        if (!commissionSendingType) {
            handledCommissionsCount++;
            sendGUIMessage("new commission added to the queue (id = "
                    + commission.getID() + ")");
            logger.info(getLocalName()
                    + " - new commission added to the queue (id = "
                    + commission.getID() + ")");
        } else {
            sendGUIMessage("new commission package added - "
                    + commissions.size());
            logger.info(getLocalName() + " - new commission package added - "
                    + commissions.size());
        }
        commissionsQueue.add(commission);

        if (commissionSendingType) {
            carryCommissions();
        } else {
            carryCommission();
        }
    }

    // jezeli kolejka zlecen nie jest pusta i nie jest wlasnie przeprowadzana
    // jakas aukcja, pobiera zlecenie i rozpoczyna aukcje
    private synchronized void carryCommission() {

        if (simInfo.getPunishmentFunction() != null) {
            int holons = AgentsService.findAgentByServiceName(this,
                    "ExecutionUnitService").length;
            choosingByCost = holons < simInfo.getHolons();

        }

        simulatedTrading = false;
        currentCommission = commissionsQueue.poll();

        eUnitOffers = new LinkedList<>();
        eUnitsCount = sendOffers(currentCommission);
        if (eUnitsCount == 0) {
            beginTransportUnitsNegotiation();
        }

    }

    private synchronized void carryCommissions() {
        simulatedTrading = false;

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "TransportUnitService");

        logger.info(getLocalName()
                + " - sending commission(s) to Transport Agents");

        send(aids, commsGroup, MessageType.COMMISSION);
    }

    // wysyla oferty do wszystkich EUnitow
    private int sendOffers(Commission commission) {

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (aids.length != 0) {
            send(aids, commission, MessageType.COMMISSION_OFFER_REQUEST);
        }

        return aids.length;
    }

    public synchronized void addOffer(EUnitOffer offer) {

        if (simulatedTrading) {
            currentAuction.addOffer(offer);
            if (currentAuction.gotAllOffers()) {
                sendGUIMessage("all EUnit offres has been collected (com id = "
                        + currentAuction.getCommission().getID() + ")");
                logger.info(getLocalName()
                        + " - all EUnit offres has been collected (com id = "
                        + currentAuction.getCommission().getID() + ")");

                chooseBestSTOffer();
            }
        } else {
            eUnitsCount--;
            if (offer.getValue() > 0) {
                eUnitOffers.add(offer);
            }
            if (eUnitsCount == 0) {
                sendGUIMessage("eUnits offers are collected");
                if (!choosingByCost && eUnitOffers.size() > 0)
                    chooseBestOffer();
                else
                    beginTransportUnitsNegotiation();
            }
        }
    }

    private void beginTransportUnitsNegotiation() {
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "TransportUnitService");

        logger.info(getLocalName()
                + " - sending commission(s) to Transport Agents");
        send(aids, new Commission[]{currentCommission}, MessageType.COMMISSION);
    }

    // TODO algorithm
    private void chooseBestSTOffer() {
        EUnitOffer[] offers = currentAuction.getOffers();
        double bestValue = Double.MAX_VALUE;
        AID bestHolon = null;

        for (EUnitOffer offer : offers) {
            if (offer.getValue() < bestValue && offer.getValue() >= 0) {
                bestValue = offer.getValue();
                bestHolon = offer.getAgent();
            }
        }

        if (bestHolon != null) {
            sendGUIMessage("commission goes to " + bestHolon.getLocalName()
                    + " (com id = " + currentAuction.getCommission().getID()
                    + ")");
            logger.info(getLocalName() + " - commission goes to "
                    + bestHolon.getLocalName() + " (com id = "
                    + currentAuction.getCommission().getID() + ")");

            sendGUIMessage("sending feedback to EUnit Agents (com id = "
                    + currentAuction.getCommission().getID() + ")");
            logger.info(getLocalName()
                    + " - sending feedback to EUnit Agents (com id = "
                    + currentAuction.getCommission().getID() + ")");

            sendFeedback(bestHolon, currentAuction.getCommission());
        } else {
            sendFeedback(null,
                    currentAuction.getCommission());
        }

    }

    private void checkSTStatus() {
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (aids.length > 0) {
            calendarStatsHolder = new CalendarStatsHolder(aids.length);
            send(aids, "", MessageType.EUNIT_SHOW_STATS);
        }
    }

    public synchronized void addCalendarStats(CalendarStats calendarStats) {

        if (calendarStatsHolder == null) {

            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            System.exit(0);
        }

        calendarStatsHolder.addCalendarStats(calendarStats);
    }

    private void simulatedTrading() {

        logger.info("fullSimulatedTrading");
        fullSimulatedTrading = true;
        complexSimulatedTrading(null);

    }

    private void fullSimulatedTrading(Map<AID, Schedule> holons) {

        Map<AID, Schedule> tmpMapForMeasure = Helper.copyAID(holons);

        for (AID i : holons.keySet()) {
            Map<AID, Schedule> tmpMap = Helper.copyAID(holons);

            holons = simInfo
                    .getExchangeAlgFactory()
                    .getAlgAfterComAdd()
                    .doExchangesAfterComAdded(holons.keySet(), holons, i,
                            simInfo, timestamp);

            if (Helper.calculateCalendarCost(holons, simInfo.getDepot()) > Helper
                    .calculateCalendarCost(tmpMap, simInfo.getDepot())) {
                holons = tmpMap;
            }
        }

        oldSchedule = tmpMapForMeasure;
        newSchedule = holons;
        // calculateMeasure(tmpMapForMeasure, holons);

        eUnitsCount = holons.size();// getEUnitsAids().length;
        fullSimulatedTrading = false;
        for (AID aid : holons.keySet()) {// getEUnitsAids()) {
            send(aid, holons.get(aid), MessageType.HOLONS_NEW_CALENDAR);
        }

        if (eUnitsCount == 0) {
            newSchedule = null;
            scheduleChanged();
        }

    }

    public synchronized void STBeginResponse() {
        eUnitsCount--;
        if (eUnitsCount == 0) {
            checkSTStatus();
        }
    }

    public synchronized void changeSchedule() {
        checkSTStatus();
    }

    private void sendFeedback(AID aid, Commission commission) {
        SimulatedTradingParameters params = currentSTAuction.getParams();
        params.commission = commission;
        send(aid, params, MessageType.FEEDBACK);
    }

    private void sendGUIMessage(String messageText) {

        if (messageText.equals("NEXT_SIMSTEP") && timestamp >= nextSTTimestamp)
            nextSTTimestamp += STTimestampGap;

        if (messageText.equals("NEXT_SIMSTEP") && calculatorsHolder != null
                && timestamp >= nextMeasureTimestamp)
            nextMeasureTimestamp += calculatorsHolder.getTimeGap();

        AID[] aids;

        aids = AgentsService.findAgentByServiceName(this, "GUIService");

        if (aids.length == 1) {
            send(aids[0], getLocalName() + " - " + messageText,
                    MessageType.GUI_MESSAGE);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with GUIService in the system");
        }
    }

    public synchronized void sendNooneList() {

        AID[] aids;
        // ACLMessage cfp = null;

        aids = AgentsService.findAgentByServiceName(this, "GUIService");

        if (aids.length == 1) {
            Serializable content;
            if (nooneList == null) {
                content = 0;
            } else {
                content = nooneList.size();
            }
            send(aids[0], content, MessageType.NOONE_LIST);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with GUIService in the system");
        }
    }

    // zwraca POSORTOWANE identyfikatory AID EUnitow
    @SuppressWarnings("unchecked")
    private AID[] getEUnitsAids() {

        AID[] aids;
        ArrayList<AID> aidsList;
        Iterator<AID> iter;
        int count;

        aids = AgentsService.findAgentByServiceName(this,
                "ExecutionUnitService");

        aidsList = new ArrayList<>();
        Collections.addAll(aidsList, aids);
        Collections.sort(aidsList, new AIDsComparator());

        iter = aidsList.iterator();
        aids = new AID[aids.length];
        count = 0;
        while (iter.hasNext()) {

            aids[count++] = iter.next();
        }

        return aids;
    }

    public synchronized void resetAgent() {

        commissionsQueue = new LinkedList<>();
        nooneList = new ArrayList<>();
        currentAuction = null;
        currentSTAuction = null;
        transportUnitsPrepare = 0;
        transportUnitCount = -1;
        measureData = new MeasureData();
        confSet = false;
        nextSTTimestamp = 0;
        nextMeasureTimestamp = 0;
    }

    /**
     * Wprowadzona w celach synchronizacyjnych
     */
    public synchronized void transportUnitPreparedForNegotiation() {
        transportUnitsPrepare++;

        if (transportUnitCount == -1)
            transportUnitCount = AgentsService.findAgentByServiceName(
                    this, "TransportUnitService").length;
        if (transportUnitsPrepare == transportUnitCount) {
            transportUnitsPrepare = 0;

            logger.info("START");
            AID[] aids = AgentsService.findAgentByServiceName(this,
                    "TransportUnitService");

            logger.info(getLocalName()
                    + " - sending signal to Transport Agents to start negotiation process");

            newHolonOffers = new TreeSet<>();

            send(aids, "", MessageType.START_NEGOTIATION);
        }
    }

    public synchronized void newHolonOffer(NewHolonOffer offer) {
        newHolonOffers.add(offer);
        transportUnitsPrepare++;
        if (transportUnitsPrepare == transportUnitCount) {
            transportUnitsPrepare = 0;
            if (commissionSendingType) {
                List<NewHolonOffer> offers = new LinkedList<>();
                for (NewHolonOffer o : newHolonOffers) {
                    if (o.isValid())
                        offers.add(o);
                }
                newHolonOffersList = offers;
                sendCommissionsToBestHolons();
            } else {
                chooseBestHolon();
            }
        }
    }

    private void chooseBestHolon() {
        bestOffer = null;
        bestCost = Double.MAX_VALUE;

        double dist = (currentCommission.getPickupX() - currentCommission
                .getDeliveryX())
                * (currentCommission.getPickupX() - currentCommission
                .getDeliveryX())
                + (currentCommission.getPickupY() - currentCommission
                .getDeliveryY())
                * (currentCommission.getPickupY() - currentCommission
                .getDeliveryY());

        double cost;
        for (NewHolonOffer offer : newHolonOffers) {
            if (!offer.isValid()) {
                continue;
            }
            cost = calculateCost(getTruck(offer.getTruck()),
                    getTrailer(offer.getTrailer()),
                    getDriver(offer.getDriver()), dist, currentCommission);
            if (cost < bestCost) {
                bestCost = cost;
                bestOffer = offer;
            }

        }
        if (bestOffer != null) {
            bestCost = calculateCost(bestOffer.getTruckData(),
                    bestOffer.getTrailerData(), bestOffer.getDriverData(),
                    dist, currentCommission);
            logger.info(newHolonOffers.size());
            logger.info("Best offer with cost = " + bestCost
                    + " consist of following holons : ");
            logger.info(bestOffer.getDriver() + " "
                    + bestOffer.getTruck() + " " + bestOffer.getTrailer());
        } else {
            bestCost = Double.MAX_VALUE;
        }

        chooseBestOffer();
    }

    /**
     * Metoda wolana po tym jak wszystkie TransportUnity zakonczyli nagocjacje,
     * w trybie wysylania paczkami
     */
    private synchronized void sendCommissionsToBestHolons() {
        if (simInfo.getPunishmentFunction() != null) {
            int holons = AgentsService.findAgentByServiceName(this,
                    "ExecutionUnitService").length;
            choosingByCost = holons < simInfo.getHolons();
        }

        if (newHolonOffersList == null || newHolonOffersList.size() == 0) {
            sendNextCommissionToEUnit();
        } else {
            transportAgentsCount = 3;

            NewHolonOffer offer = newHolonOffersList.remove(0);
            bestOffer = offer;
            sendGUIMessage("new holon: [" + offer.getDriver().getLocalName()
                    + ", " + offer.getTrailer().getLocalName() + ", "
                    + offer.getTruck().getLocalName() + "]");
            NewTeamData data = new NewTeamData(offer.getTruck(),
                    getTruck(offer.getTruck()), offer.getTrailer(),
                    getTrailer(offer.getTrailer()), offer.getDriver(),
                    getDriver(offer.getDriver()), null, STDepth, algorithm,
                    dist, timestamp);
            createNewEUnit(data);
        }
    }

    /**
     * Przesyla zlecenia do EUnitow, w trybie przesylania paczkami
     */
    private synchronized void sendNextCommissionToEUnit() {
        handledCommissionsCount++;
        simulatedTrading = false;
        if (commissions.size() == 0) {
            logger.info("Zlecenia przyznane");
            sendGUIMessage("NEXT_SIMSTEP");
            return;
        }
        eUnitOffers = new LinkedList<>();
        bestCost = Double.MAX_VALUE;
        currentCommission = commissions.remove(0);
        sendGUIMessage("search for EUnit to carry commission (id="
                + currentCommission.getID() + ")");
        eUnitsCount = sendOffers(currentCommission);
        if (eUnitsCount == 0) {
            createDefaultHolon(currentCommission);
        }
    }

    /**
     * Wybor oferty w trybie wysylania zlecenia po zleceniu
     */
    private void chooseBestOffer() {
        transportAgentsCount = 1;
        Collections.sort(eUnitOffers);
        if (eUnitOffers.size() == 0) {
            sendTeamToEUnit();
            return;
        }

        if (!choosingByCost) {
            sendCommissionToEUnit();
            return;
        }

        if (bestCost < eUnitOffers.get(0).getValue()) {
            sendTeamToEUnit();
        } else {
            sendCommissionToEUnit();
        }

    }

    /**
     * Inicjuje tworzenie nowego EUnita
     */
    private void sendTeamToEUnit() {
        if (commissionSendingType) {
            createDefaultHolon(currentCommission);
            return;
        }

        logger.info("Send Team");
        if (bestOffer == null) {
            createDefaultHolon(currentCommission);
            return;
        }

        NewTeamData data = new NewTeamData(bestOffer.getTruck(),
                getTruck(bestOffer.getTruck()), bestOffer.getTrailer(),
                getTrailer(bestOffer.getTrailer()), bestOffer.getDriver(),
                getDriver(bestOffer.getDriver()), currentCommission, STDepth,
                algorithm, dist, timestamp);

        transportAgentsCount = 3;

        createNewEUnit(data);

    }

    /**
     *
     */
    private void sendCommissionToEUnit() {
        logger.info("SendCommission to "
                + eUnitOffers.get(0).getAgent().getLocalName() + ", cost = "
                + eUnitOffers.get(0).getValue());
        sentCommissionToEUnit();
    }

    private void sentConfirmationToTransportUnit(AID aid) {
        send(aid, "", MessageType.CONFIRMATIO_FROM_DISTRIBUTOR);
    }

    /**
     *
     */
    private void sentCommissionToEUnit() {
        send(eUnitOffers.get(0).getAgent(), currentCommission, MessageType.COMMISSION_FOR_EUNIT);
    }

    /**
     * Uzywana w celach synchronizacyjnych
     */
    public synchronized void feedbackFromEUnit() {
        transportAgentsCount--;
        if (transportAgentsCount == 0) {
            if (simulatedTradingCount != 0 && checkSTCondition()) {
                if (commissionSendingType) {
                    if (currentCommission != null)
                        simulatedTrading();
                    else
                        getCalendarsForMesure();
                } else {
                    simulatedTrading();
                }
            } else {
                getCalendarsForMesure();
            }
        }
    }

    protected void createNewEUnit(NewTeamData data) {
        newTeamData = data;
        if (checkSTCondition()) {
            complexSimulatedTrading(Commission.copy(data.getCommission()));
        } else {
            createNewEUnit();
        }
    }

    private synchronized void createNewEUnit() {
        sendGUIMessage("create new EUnit");
        NewTeamData data = newTeamData;

        if (!checkCommission(data)) {
            AID aids[] = AgentsService.findAgentByServiceName(this,
                    "GUIService");
            send(aids[0], data, MessageType.UNDELIVERED_COMMISSION);
            return;
        }

        EUnitInitialData initialData = new EUnitInitialData(simInfo, data);

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {
            send(aids[0], initialData, MessageType.EXECUTION_UNIT_CREATION);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public synchronized void undeliveredCommissionResponse() {
        if (commissionSendingType) {
            sendCommissionsToBestHolons();
        } else {
            if (commissions.size() > 0)
                addCommission(commissions.remove(0));
            else
                sendGUIMessage("NEXT_SIMSTEP");
        }
    }

    private synchronized boolean checkCommission(NewTeamData data) {
        Algorithm algorithm = data.getAlgorithm();
        algorithm.init(data.getTrailer().getCapacity(), simInfo);
        Schedule schedule = simInfo.createSchedule(null);
        schedule.setAlgorithm(algorithm);
        schedule.setCreationTime(data.getCreationTime());

        if (data.getCommission() != null
                && data.getTrailer().getCapacity() < data.getCommission()
                .getLoad()) {
            logger.error("Ustawiono za mala pojemnosc domyslnej przyczepy (zlecenie id="
                    + data.getCommission().getID()
                    + " nie moze byc zrealizowane)");
            System.exit(0);
        }
        if (data.getCommission() != null) {
            Schedule tmpSchedule = algorithm.makeSchedule(data.getCommission(),
                    null, schedule, timestamp);
            if (tmpSchedule == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Powiadomienie o stworzeniu EUnita
     */
    public synchronized void eUnitCreated() {
        if (defaultEUnitCreation) {
            defaultEUnitCreation = false;
            if (simulatedTradingCount == 0 || !checkSTCondition()) {
                getCalendarsForMesure();
            } else
                simulatedTrading();
            return;
        }
        sentConfirmationToTransportUnit(bestOffer.getTruck());
        sentConfirmationToTransportUnit(bestOffer.getTrailer());
        sentConfirmationToTransportUnit(bestOffer.getDriver());
    }

    private synchronized void createDefaultHolon(Commission commission) {
        sendGUIMessage("create new default EUnit");
        defaultEUnitCreation = true;
        TransportElementInitialData truck = new TransportElementInitialDataTruck(
                null, defaultAgentsData.getPower(), 0, 0,
                defaultAgentsData.getPower(),
                defaultAgentsData.getReliability(),
                defaultAgentsData.getComfort(),
                defaultAgentsData.getFuelConsumption(), 0);
        TransportElementInitialDataTrailer trailer = new TransportElementInitialDataTrailer(
                null, defaultAgentsData.getCapacity(), 0, 0,
                defaultAgentsData.getMass(), defaultAgentsData.getCapacity(),
                defaultAgentsData.getCargoType(),
                defaultAgentsData.getUniversality(), 0);
        NewTeamData data = new NewTeamData(null, truck, null, trailer, null,
                null, commission, STDepth, algorithm, dist, timestamp);
        createNewEUnit(data);
    }

    public synchronized void complexSimulatedTrading(Commission com) {
        holons = new HashMap<>();
        AID aids[] = getEUnitsAids();
        eUnitsCount = aids.length;
        if (com != null)
            complexSTCommission = Commission.copy(com);
        if (eUnitsCount == 0) {
            createNewEUnit();
            return;
        }
        send(aids, "", MessageType.HOLONS_CALENDAR);
    }

    public synchronized void addComplexSTSchedule(Schedule schedule, AID sender) {
        if (schedule == null)
            eUnitsCount--;
        else
            holons.put(sender, schedule);

        if (holons.size() == eUnitsCount) {
            if (calendarsForMeasures) {
                calculateMeasure(holons, null);
                return;
            }

            Map<AID, Schedule> map = new HashMap<>();
            for (AID key : holons.keySet()) {
                if (holons.get(key).size() > 0)
                    map.put(key, holons.get(key));
            }
            holons = map;
            if (fullSimulatedTrading) {
                fullSimulatedTrading(holons);
                return;
            }

            Map<AID, Schedule> tmp = simInfo
                    .getExchangeAlgFactory()
                    .getAlgWhenCantAdd()
                    .doExchangesWhenCantAddCom(holons.keySet(),
                            Helper.copyAID(holons),
                            Commission.copy(complexSTCommission), simInfo,
                            timestamp);

            // Map<AID, Schedule> tmp = SimmulatedTrading
            // .complexSimulatedTrading(holons.keySet(),
            // Helper.copyAID(holons),
            // Commission.copy(complexSTCommission),
            // maxFullSTDepth, new TreeSet<Integer>(), timestamp,
            // simInfo, simInfo.isFirstComplexSTResultOnly());

            if (tmp != null) {
                oldSchedule = holons;
                newSchedule = tmp;
                // calculateMeasure(holons, tmp);
                eUnitsCount = tmp.size();// getEUnitsAids().length;
                for (AID aid : getEUnitsAids()) {
                    send(aid, tmp.get(aid), MessageType.HOLONS_NEW_CALENDAR);
                }
                if (eUnitsCount == 0)
                    scheduleChanged();
            } else {
                createNewEUnit();
            }
        }
    }


    public synchronized void scheduleChanged() {
        eUnitsCount--;
        if (eUnitsCount <= 0) {
            calculateMeasure(oldSchedule, newSchedule);
        }
    }

    private boolean checkSTCondition() {
        return timestamp >= nextSTTimestamp
                && handledCommissionsCount % STCommissionsGap == 0;
    }

    private void getCalendarsForMesure() {
        calendarsForMeasures = true;
        complexSimulatedTrading(null);
    }

    private void calculateMeasure(Map<AID, Schedule> oldSchedule,
                                  Map<AID, Schedule> newSchedule) {

        Set<AID> oldScheduleAids = oldSchedule.keySet();
        if (calculatorsHolder != null && timestamp >= nextMeasureTimestamp) {

            List<Measure> measures = new LinkedList<>();

            Measure measure;

            calculatorsHolder.setTimestamp(timestamp);
            calculatorsHolder.setCommissions(commissions);

            AID[] aids = null;

            if (shouldSendVisualisationMeasureData) {
                aids = AgentsService.findAgentByServiceName(this, "GUIService");
                send(aids, oldScheduleAids.<AID>toArray(new AID[oldScheduleAids.size()]), MessageType.VISUALISATION_MEASURE_SET_HOLONS);
            }

            for (MeasureCalculator measureCalc : calculatorsHolder
                    .getCalculators()) {
                measure = measureCalc
                        .calculateMeasure(oldSchedule, newSchedule);

                measure.setTimestamp(timestamp);
                measure.setComId(this.currentCommission.getID());

                measures.add(measure);

                measure.setName(measureCalc.getName());
                if (shouldSendVisualisationMeasureData) {
                    send(aids, measure, MessageType.VISUALISATION_MEASURE_UPDATE);
                }
            }

            measureData.addMeasures(measures);

            if (isConfigurationChangeable) {
                changeConfiguration();
                return;
            }
        }

        if (mlAlgorithm != null) {
            mlAlgorithm.setTimestamp(timestamp);
            mlAlgorithm.setCommissions(commissions);

            Map<AID, Schedule> copyOfNewSchedule = null;
            if (newSchedule != null) {
                if (newSchedule.size() == 0)
                    newSchedule = null;
                else {
                    copyOfNewSchedule = new HashMap<>();
                    for (AID key : newSchedule.keySet()) {
                        copyOfNewSchedule.put(key,
                                Schedule.copy(newSchedule.get(key)));
                    }
                }
            }
            Map<AID, Schedule> copyOfOldSchedule = new HashMap<>();
            for (AID key : oldScheduleAids) {
                copyOfOldSchedule.put(key, Schedule.copy(oldSchedule.get(key)));
            }

            GlobalConfiguration globalConf = null;
            if (oldSchedule.size() > 0) {
                globalConf = mlAlgorithm.getGlobalConfiguration(
                        copyOfOldSchedule, copyOfNewSchedule, simInfo,
                        simInfo.isExploration());
            }
            Map<AID, HolonConfiguration> holonConfigurations = mlAlgorithm
                    .getHolonsConfiguration(oldSchedule, newSchedule, simInfo,
                            simInfo.isExploration());
            changeConfiguration(globalConf, holonConfigurations);
            return;

        }

        continueAfterMeasureGathered();
    }

    private synchronized void continueAfterMeasureGathered() {
        if (calendarsForMeasures) {
            calendarsForMeasures = false;
            if (defaultEUnitCreation) {
                defaultEUnitCreation = false;
                if (simulatedTradingCount == 0 || !checkSTCondition()) {
                    if (commissionSendingType) {
                        sendCommissionsToBestHolons();
                    } else {
                        if (commissions.size() > 0)
                            addCommission(commissions.remove(0));
                        else {
                            if (graphChanged) {
                                graphChanged = false;
                                AID sender = AgentsService
                                        .findAgentByServiceName(this,
                                                "GUIService")[0];
                                this.send(sender, false, MessageType.GRAPH_CHANGED);
                            } else
                                sendGUIMessage("NEXT_SIMSTEP");
                        }
                    }
                }
            } else {
                if (simulatedTradingCount != 0 && checkSTCondition()) {
                    sendCommissionsToBestHolons();
                } else {
                    if (commissionSendingType) {
                        sendCommissionsToBestHolons();
                    } else {
                        if (commissions.size() > 0)
                            addCommission(commissions.remove(0));
                        else {
                            if (graphChanged) {
                                graphChanged = false;
                                AID sender = AgentsService
                                        .findAgentByServiceName(this,
                                                "GUIService")[0];
                                this.send(sender, false, MessageType.GRAPH_CHANGED);
                            } else
                                sendGUIMessage("NEXT_SIMSTEP");
                        }
                    }
                }
            }
        } else {
            if (commissionSendingType) {
                sendCommissionsToBestHolons();
            } else {
                if (commissions.size() > 0)
                    addCommission(commissions.remove(0));
                else
                    sendGUIMessage("NEXT_SIMSTEP");
            }
        }
    }

    public synchronized MeasureData getMeasureData() {
        return measureData;
    }

    private void changeConfiguration() {
        changeConfiguration(null, null);
    }

    private void changeConfiguration(GlobalConfiguration globalConf,
                                     Map<AID, HolonConfiguration> conf) {

        if (globalConf != null) {
            if (globalConf.isType() != null)
                this.commissionSendingType = globalConf.isType();
            if (globalConf.isChoosingByCost() != null)
                this.choosingByCost = globalConf.isChoosingByCost();
            if (globalConf.getSimulatedTrading() != null)
                this.simulatedTradingCount = globalConf.getSimulatedTrading();

            ExchangeAlgorithmsFactory factory = simInfo.getExchangeAlgFactory();
            if (globalConf.getChooseWorstCommission() != null) {
                this.chooseWorstCommission = globalConf
                        .getChooseWorstCommission();
                factory.getAlgAfterComAdd()
                        .getParameters()
                        .put("chooseWorstCommission",
                                this.chooseWorstCommission);
            }
            if (globalConf.getSTDepth() != null) {
                this.maxFullSTDepth = globalConf.getSTDepth();
                factory.getAlgAfterComAdd()
                        .getParameters()
                        .put("maxFullSTDepth",
                                Integer.toString(this.maxFullSTDepth));
            }

        }

        if (conf != null) {
            eUnitsCount = conf.keySet().size();
            for (AID aid : conf.keySet()) {
                this.send(aid, conf.get(aid), MessageType.CONFIGURATION_CHANGE);
            }

            if (eUnitsCount == 0)
                continueAfterMeasureGathered();
        } else {
            continueAfterMeasureGathered();
        }
    }

    public synchronized void configurationChanged() {
        eUnitsCount--;
        if (eUnitsCount <= 0)
            continueAfterMeasureGathered();
    }

    public MLAlgorithm getMLAlgorithm() {
        return mlAlgorithm;
    }

    public synchronized void graphChanged(Graph graph, AID sender) {
        ((GraphSchedule) this.simInfo.getScheduleCreator()).getTrackFinder()
                .setGraph(graph);

        if (simInfo.isSTAfterGraphChange()) {
            graphChanged = true;

            simulatedTrading();
        } else {
            send(sender, false, MessageType.GRAPH_CHANGED);
        }
    }
}
