package dtp.jade.eunit;

import algorithm.Algorithm;
import algorithm.GraphSchedule;
import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.ProblemType;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.eunit.behaviour.*;
import dtp.jade.transport.*;
import dtp.simulation.SimInfo;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import measure.configuration.HolonConfiguration;
import org.apache.log4j.Logger;
import xml.elements.CommissionData;
import xml.elements.SimulationData;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an execution unit as a jade agent.
 *
 * @author kony.pl
 */
public class ExecutionUnitAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(ExecutionUnitAgent.class);

    private int problemType;

    private SimInfo simInfo;
    private Graph graph;

    private Schedule schedule;
    private Algorithm algorithm;
    private Point2D.Double depot;
    private boolean dist = true;

    private int timestamp;
    private int creationTime;

    private TransportOffer driver;

    private TransportOffer truck;
    private TransportOffer trailer;

    private boolean isDefault;

    private int commissionsCount;

    /*
     * These objects are useful to complete the xls file. They are initialised
     * in getSummaryCost method
     */
    private TransportElementInitialDataTruck truckData;
    private TransportElementInitialDataTrailer trailerData;

    private EUnitInitialData initialData;

    private boolean isSimulatedTradingEnabled = true;
    private int maxSTDepth;
    private long reorganizationTime;

    @Override
    protected void setup() {

        logger.info("Hello World!");

        /* -------- SERVICES SECTION ------- */
        registerServices();

        /* -------- BEHAVIOURS SECTION ------- */
        this.addBehaviour(new GetTimestampBehaviour(this));
        this.addBehaviour(new GetCommissionFromDistributorAgentBehaviour(this));
        this.addBehaviour(new GetWorstCommissionRequestBehaviour(this));
        this.addBehaviour(new GetCrisisEventBehaviour(this));
        this.addBehaviour(new GetTransportOfferBehaviour(this));
        this.addBehaviour(new GetTransportReorganizeOfferBahaviour(this));
        this.addBehaviour(new GetInitialDataBehaviour(this));
        this.addBehaviour(new GetCalendarRequestToFileWriteBehaviour(this));
        this.addBehaviour(new GetCommissionForEUnitBehaviour(this));

        this.addBehaviour(new GetComplexSTScheduleBehaviour(this));
        this.addBehaviour(new GetComplexSTScheduleChangedBehaviour(this));

        this.addBehaviour(new GetSimulationDataBehaviour(this));
        this.addBehaviour(new GetConfigurationChangeBehaviour(this));

        this.addBehaviour(new GetGraphChangedBehaviour(this));
        this.addBehaviour(new GetAskForGraphChangesBehavoiur(this));
        this.addBehaviour(new GetGraphLinkChangedBehaviour(this));

        this.addBehaviour(new GetUpdateCurrentLocationBehaviour(this));
        this.addBehaviour(new GetBackToDepotBehaviour(this));

        isDefault = false;
        commissionsCount = 0;
        driver = null;
        truck = null;
        trailer = null;
        maxSTDepth = 0;
        /* -------- AID SECTION ------- */
        sendAidToInfoAgent();
    }

    public EUnitInitialData getInitialData() {
        return initialData;
    }

    public void setInitialData(EUnitInitialData initialData) {
        this.initialData = initialData;
        setSimInfo(initialData.getSimInfo());
        newHolonTeam(initialData.getData());

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "CommissionService");
        send(aids[0], "", MessageType.EXECUTION_UNIT_CREATION);
    }

    public void setMaxLoad(double maxLoad) {
        algorithm.setMaxLoad(maxLoad);
    }

    private void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /* -------- EXECUTION UNIT SERVICE ------- */
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ExecutionUnitService");
        sd.setName("ExecutionUnitService");
        dfd.addServices(sd);

        logger.info("ExecutionUnitService registration");

        /* -------- REGISTRATION ------- */
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException ", fe);
        }
    }

    /**
     * Sends driver agent AID to Info Agent. This method is invoked just after
     * creation of new driver agent.
     */
    private void sendAidToInfoAgent() {

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {
            AID distributorAgentAID = aids[0];
            send(distributorAgentAID, this.getAID(), MessageType.EXECUTION_UNIT_AID);
        } else {
            logger.error("None or more than one Distributor Agent in the system");
        }
    }

    public void initCalendar() {
        schedule = simInfo.createSchedule(algorithm);
        schedule.setCreationTime(creationTime);
        this.depot = simInfo.getDepot();

        logger.info("AgentCalendar initiated [" + problemType + "]");
    }

    public Graph getGraph() {
        return this.graph;
    }

    public synchronized void setGraph(Graph graph) {
        this.graph = graph;

        if (graph != null)
            this.problemType = ProblemType.WITH_GRAPH;
        else
            this.problemType = ProblemType.WITHOUT_GRAPH;

        if (this.simInfo != null)
            initCalendar();
    }

    public synchronized void setCurrentTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }

    public synchronized void setSimInfo(SimInfo simInfo) {
        this.simInfo = simInfo;
        initCalendar();
    }

    public int getTimestamp() {
        return timestamp;
    }

    public synchronized TransportOffer getDriver() {
        return driver;
    }

    public synchronized void setDriver(TransportOffer driver) {
        this.driver = driver;
    }

    public synchronized TransportOffer getTruck() {
        return truck;
    }

    public synchronized void setTruck(TransportOffer truck) {
        this.truck = truck;
    }

    public synchronized TransportOffer getTrailer() {
        return trailer;
    }

    public synchronized void setTrailer(TransportOffer trailer) {
        this.trailer = trailer;
    }

    public void sendOfferToDistributor(EUnitOffer offer) {

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {
            send(aids, offer, MessageType.COMMISSION_OFFER);
        } else {
            logger.warn("none or more than one EUnit agent in the system");
        }
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public synchronized boolean addCommissionToCalendar(Commission commission) {
        schedule.setCreationTime(creationTime);
        Schedule tmpSchedule = algorithm.makeSchedule(commission, null,
                schedule, timestamp);
        if (tmpSchedule != null) {
            Commission currentCommission = schedule.getCurrentCommission();
            schedule = tmpSchedule;
            commissionsCount++;
            if (currentCommission != null && currentCommission.getID() == -1) {
                schedule.setCurrentCommission(commission, simInfo.getDepot());
            }
            return true;
        } else {
            logger.info(getLocalName()
                    + " - couldn't add commission to calendar (com id = "
                    + commission.getID() + ")");
            return false;
        }
    }

    public synchronized void sendCalendarStatsToFile() {

        CalendarStats calendarStatsToSend;

        calendarStatsToSend = new CalendarStats(getAID());
        calendarStatsToSend.setDistance(schedule.getDistance(depot));
        calendarStatsToSend.setWaitTime(schedule.calculateWaitTime(depot));
        calendarStatsToSend.setCost(getSummaryCost());
        calendarStatsToSend.setDriveTime(schedule.calculateDriveTime(simInfo));
        calendarStatsToSend.setPunishment(schedule.calculateSummaryPunishment(
                simInfo, true));
        calendarStatsToSend.setSchedule(schedule);
        calendarStatsToSend.setReorganizationTime(reorganizationTime);
        calendarStatsToSend.setOrganizationTime(0);
        calendarStatsToSend.setMaxSTDepth(maxSTDepth);

        if (driver != null)
            calendarStatsToSend.setDriverAID(driver.getAid());
        else
            calendarStatsToSend.setDriverAID(new AID("no driver", false));

        if (trailer != null) {
            calendarStatsToSend.setTrailerAID(trailer.getAid());
            calendarStatsToSend.setCapacity(trailer.getMaxLoad());
            calendarStatsToSend.setMass(trailerData.getMass());
        } else
            calendarStatsToSend.setTrailerAID(new AID("no trailer", false));

        if (truck != null) {
            calendarStatsToSend.setTruckAID(truck.getAid());
            calendarStatsToSend.setPower(truckData.getPower());
            calendarStatsToSend.setReliability(truckData.getReliability());
            calendarStatsToSend.setComfort(truckData.getComfort());
            calendarStatsToSend.setFuelConsumption(truckData
                    .getFuelConsumption());
        } else
            calendarStatsToSend.setTruckAID(new AID("no truck", false));

        calendarStatsToSend.setDefault(isDefault);

        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, calendarStatsToSend, MessageType.EUNIT_MY_FILE_STATS);
    }

    public synchronized void sendWorstCommissionCost(
            Commission worstCommission, AID sender) {
        Schedule tmpSchedule = algorithm.makeSchedule(
                Commission.copy(worstCommission), null, schedule, timestamp);
        CalendarStats stat = new CalendarStats(getAID());
        if (tmpSchedule != null) {
            double extraDistance = tmpSchedule.getDistance(depot)
                    - schedule.getDistance(depot);
            double cost = Helper.getRatio(extraDistance, worstCommission);
            stat.setCost(cost);
        }
        stat.setSchedule(tmpSchedule);

        send(sender, stat, MessageType.WORST_COMMISSION_COST);
    }

    public synchronized HolonCreationAuction getAuction() {
        return null;
    }

    private double calculatePunishment(Schedule tmpSchedule) {
        return tmpSchedule.calculateSummaryPunishment(simInfo)
                - schedule.calculateSummaryPunishment(simInfo);
    }

    private double calculateDefaultEUnitCost(
            TransportElementInitialDataTrailer trailerData,
            TransportElementInitialDataTruck truckData, double distance,
            Commission commission, Schedule tmpSchedule) {
        return 3 * TransportAgent.costFunctionValue(
                truckData.getCostFunction(), distance, null, truckData,
                trailerData, commission, calculatePunishment(tmpSchedule));
    }

    private double calculateCost(TransportElementInitialDataTruck truckData,
                                 TransportElementInitialDataTrailer trailerData,
                                 TransportElementInitialData driver, double distance,
                                 Commission commission, Schedule tmpSchedule) {
        if (driver == null)
            return calculateDefaultEUnitCost(trailerData, truckData, distance,
                    commission, tmpSchedule);
        double truckValue = TransportAgent.costFunctionValue(
                truckData.getCostFunction(), distance, driver, truckData,
                trailerData, commission, calculatePunishment(tmpSchedule));
        double trailerValue = TransportAgent.costFunctionValue(
                trailerData.getCostFunction(), distance, driver, truckData,
                trailerData, commission, calculatePunishment(tmpSchedule));
        double driverValue = TransportAgent.costFunctionValue(
                driver.getCostFunction(), distance, driver, truckData,
                trailerData, commission, calculatePunishment(tmpSchedule));
        return truckValue + trailerValue + driverValue;
    }

    public double getRatio(double distance, Commission commission,
                           Schedule tmpSchedule) {
        if (commission == null) {
            logger.warn("Commission null\n");
        }

        if (getDriver() != null && getTrailer() != null && getTruck() != null
                && commission != null) {
            TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();
            TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer
                    .getTransportElementData();

            if (truckData != null && trailerData != null) {
                return calculateCost(truckData, trailerData,
                        driver.getTransportElementData(), distance, commission,
                        tmpSchedule);
            }
            if (trailerData == null) {
                logger.warn("trailerData is null\n");
            } else {
                logger.warn("truckData is null\n");
            }
        }
        return -1;
    }

    public double getSummaryCost() {
        if (getDriver() != null && getTrailer() != null && getTruck() != null) {
            truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();
            trailerData = (TransportElementInitialDataTrailer) trailer
                    .getTransportElementData();

            if (schedule != null) {
                return schedule.calculateSummaryCostWithoutPredicted(simInfo);
            }
        }
        return 0;
    }

    public void setBackupTeam(TransportOffer[] backupTeam) {
        logger.info("Backup Team Set to: " + Arrays.toString(backupTeam));
    }

    public HolonReorganizeAuction getReauction() {
        return null;
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignored) {
        }
    }

    public void addReorganizationTime(long time) {
        reorganizationTime += time;
    }

    // sprawdza, czy zlecenie ktore naplynelo od dystrybutora moze zostac
    // obsuzone
    public synchronized void checkNewCommission(Commission commission) {
        Schedule tmpSchedule = algorithm.makeSchedule(
                Commission.copy(commission), null, schedule, timestamp);
        if (tmpSchedule != null) {
            if (dist) {
                double extraDistance = tmpSchedule.getDistance(depot)
                        - schedule.getDistance(depot);
                sendOfferToDistributor(new EUnitOffer(getAID(), getRatio(
                        extraDistance, commission, tmpSchedule),
                        commissionsCount));
            } else {
                double extraTime = tmpSchedule.calculateTime(depot)
                        - schedule.calculateTime(depot);
                sendOfferToDistributor(new EUnitOffer(getAID(), extraTime,
                        commissionsCount));
            }
        } else
            sendOfferToDistributor(new EUnitOffer(getAID(), -1.0,
                    commissionsCount));
    }

    public void newHolonTeam(NewTeamData data) {
        TransportOffer truckOffer = new TransportOffer();
        truckOffer.setAid(data.getTruckAID());
        truckOffer.setTransportElementData(data.getTruck());
        TransportOffer trailerOffer = new TransportOffer();
        trailerOffer.setAid(data.getTrailerAID());
        trailerOffer.setTransportElementData(data.getTrailer());
        TransportOffer driverOffer = new TransportOffer();
        driverOffer.setAid(data.getDriverAID());
        driverOffer.setTransportElementData(data.getDriver());
        creationTime = data.getCreationTime();

        this.dist = data.isDist();
        this.algorithm = data.getAlgorithm();
        this.algorithm.init(trailerOffer.getMaxLoad(), simInfo);

        if (schedule != null) {
            schedule.setAlgorithm(algorithm);
            schedule.setCreationTime(creationTime);
        }

        this.truck = truckOffer;
        this.trailer = trailerOffer;
        this.driver = driverOffer;
        if (data.getCommission() != null
                && data.getTrailer().getCapacity() < data.getCommission()
                .getLoad()) {
            logger.warn("Ustawiono za mala pojemnosc domyslnej przyczepy (zlecenie id="
                    + data.getCommission().getID()
                    + " nie moze byc zrealizowane)");
            System.exit(0);
        }
        if (data.getDriverAID() == null)
            isDefault = true;
        if (data.getCommission() != null) {
            if (!addCommissionToCalendar(data.getCommission())) {
                logger.error("Tu jest blad!!!");
                System.exit(0);
            }
            schedule.setCurrentCommission(null, depot);
            updateCurrentLocation(0);
        }
    }

    /* ComplexST Part */
    //refresh current location is just used for gui refreshment, nothing else!
    public synchronized void sendSchedule(AID sender, boolean refreshCurrentLocation) {
        if (isSimulatedTradingEnabled) {
            schedule.setRefreshCurrentLocation(refreshCurrentLocation);
            schedule.setAlgorithm(algorithm);
            send(sender, Schedule.copy(schedule), MessageType.HOLONS_CALENDAR);
        } else {
            send(sender, null, MessageType.HOLONS_CALENDAR);
        }
    }

    public synchronized void setNewSchedule(Schedule schedule, AID sender) {
        if (schedule != null) {
            this.schedule = Schedule.copy(schedule);
            this.schedule.setCreationTime(creationTime);
            this.schedule.setAlgorithm(algorithm);
        }
        send(sender, "", MessageType.HOLONS_NEW_CALENDAR);
    }

    /* *** end of ST part *** */

    public synchronized void sendSimmulationData(AID sender) {
        //SummaryLatency sumLat = new SummaryLatency();
        //sumLat.calculateMeasure(this.schedule, newSchedules);

        SimulationData data = new SimulationData();
        data.setHolonId(Integer.parseInt(getAID().getLocalName().split("#")[1]));
        data.setHolonCreationTime(creationTime);
        data.setDriver(driver.getTransportElementData());
        data.setTrailer((TransportElementInitialDataTrailer) trailer
                .getTransportElementData());
        data.setTruck((TransportElementInitialDataTruck) truck
                .getTransportElementData());
        data.setCommissions(getCommissionsData());

        if (schedule.getCurrentLocation() == null) {
            logger.warn("Mamy nulla " + getAID().getLocalName() + " " + timestamp);
            updateCurrentLocation(timestamp);
            /*schedule.setCurrentLocation(depot);
            schedule.setCurrentCommission(schedule.getCommission(0), null);*/
        }

        data.setLocation(schedule.getCurrentLocation());
        data.setSchedule(schedule);
        send(sender, data, MessageType.SIMULATION_DATA);
    }

    private List<CommissionData> getCommissionsData() {
        List<CommissionData> result = new LinkedList<>();
        CommissionData data;
        Commission com;
        for (int i = 0; i < schedule.size(); i++) {
            data = new CommissionData();
            com = schedule.getCommission(i);
            if (schedule.isPickup(i))
                data.comId = com.getPickUpId();
            else
                data.comId = com.getDeliveryId();
            double arrivalTime = schedule.getArrivalTime(data.comId, depot);
            data.arrivalTime = arrivalTime;
            if (schedule.isPickup(i)) {
                if (arrivalTime < com.getPickupTime1())
                    data.departTime = com.getPickupTime1()
                            + com.getPickUpServiceTime();
                else
                    data.departTime = arrivalTime + com.getPickUpServiceTime();
            } else {
                if (arrivalTime < creationTime + com.getDeliveryTime1())
                    data.departTime = com.getDeliveryTime1()
                            + com.getDeliveryServiceTime();
                else
                    data.departTime = arrivalTime
                            + com.getDeliveryServiceTime();
            }
            result.add(data);
        }

        return result;
    }

    public void updateCurrentLocation(int timestamp) {
        schedule.updateCurrentLocation(timestamp, simInfo.getDepot(), getAID());

        AID[] aids = AgentsService.findAgentByServiceName(this, "CommissionService");
        sendSchedule(aids[0], true);
    }

    public synchronized void configurationChanged(HolonConfiguration conf, AID sender) {
        if (conf != null) {
            if (conf.isDist() != null)
                this.dist = conf.isDist();
            if (conf.getAlgorithm() != null) {
                this.algorithm = conf.getAlgorithm();
                this.algorithm.init(trailer.getMaxLoad(), simInfo);

                if (schedule != null) {
                    schedule.setAlgorithm(algorithm);
                }
            }
            if (conf.getSimulatedTrading() != null)
                isSimulatedTradingEnabled = conf.getSimulatedTrading();

        }
        send(sender, "", MessageType.CONFIGURATION_CHANGE);
    }

    public synchronized void graphChanged(Graph graph,
                                          Boolean updateAfterArrival, AID sender) {
        ((GraphSchedule) this.simInfo.getScheduleCreator()).getTrackFinder()
                .setGraph(graph);
        ((GraphSchedule) this.schedule).changeGraph(graph, timestamp,
                simInfo.getDepot(), updateAfterArrival);

        send(sender, true, MessageType.GRAPH_CHANGED);
    }

    public synchronized void askForGraphChanges(AID sender) {
        GraphLink link = ((GraphSchedule) schedule).getChangeLink();
        send(sender, link, MessageType.ASK_IF_GRAPH_LINK_CHANGED);
    }

    public synchronized void changeGraphLinks(LinkedList<GraphLink> links,
                                              AID sender) {
        ((GraphSchedule) schedule).insertGraphChanges(links);
        send(sender, "", MessageType.GRAPH_LINK_CHANGED);
    }

    public void confirmUpdateCurrentLocationRequest(AID sender) {
        send(sender, "", MessageType.UPDATE_CURRENT_LOCATION);
    }
}
