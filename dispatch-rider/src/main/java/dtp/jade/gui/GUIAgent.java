package dtp.jade.gui;

import algorithm.Algorithm;
import algorithm.Brute2Sorter;
import algorithm.BruteForceAlgorithm;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.CommissionsHandler;
import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.gui.SimLogic;
import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarAction;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.eunit.EUnitInfo;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.test.DefaultAgentsData;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportType;
import dtp.optimization.TrackFinder;
import dtp.simmulation.SimInfo;
import gui.main.SingletonGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import measure.printer.MeasureData;
import measure.printer.PrintersHolder;
import org.apache.log4j.Logger;
import xml.elements.SimmulationData;
import xml.elements.XMLBuilder;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Main entry point for project
 *
 * @author kony.pl
 */
public abstract class GUIAgent extends Agent {


    public static Semaphore updateCurrentLocationSemaphore;
    /**
     * Logger.
     */
    protected static Logger logger = Logger.getLogger(GUIAgentImpl.class);
    // trzyma obiekty CommissionHandler (zlecenie wraz z czasem naplyniecia
    // do systemu), w odpowiednim czasie wysyla zlecenie do dystrybutora
    protected CommissionsHandler commissionsHandler;
    /**
     * Graphic User Interface
     */
    protected SimLogic gui;
    protected Timer timer;
    protected ActionListener timerTaskPerformer;
    protected int timerDelay;
    protected CalendarsHolder calendarsHolder;
    protected CalendarStatsHolder calendarStatsHolder;
    protected CalendarStatsHolder calendarStatsHolderForFile;
    protected String saveFileName;
    protected int eUnitsCount;
    protected int agentsCount;
    protected boolean recording;
    protected String graphChangeTime;
    protected int graphChangeFreq;
    protected int simInfoReceived;
    Map<Integer, List<SimmulationData>> simmulationData = new TreeMap<Integer, List<SimmulationData>>();
    private long simTime;
    private String punishmentFunction;
    private Map<String, Double> punishmentFunctionDefaults;
    private Double delayLimit;
    private int holons;
    private boolean firstComplexSTResultOnly;
    private MLAlgorithm mlAlgorithm;
    private boolean exploration;
    private TrackFinder trackFinder;
    private GraphLinkPredictor graphLinkPredictor;
    private boolean STAfterChange;
    private ExchangeAlgorithmsFactory exchangeAlgFactory;
    private boolean commissionSendingType = false;
    private boolean choosingByCost = true;
    private int simmulatedTradingCount = 0;
    private int STDepth = 1;
    private DefaultAgentsData defaultAgentsData = null;
    private String chooseWorstCommission;
    private Algorithm algorithm = new BruteForceAlgorithm();
    private boolean dist;
    private int STTimestampGap;
    private int STCommissionGap;
    private PrintersHolder printersHolder;
    private MeasureCalculatorsHolder calculatorsHolder;
    private boolean confChange;
    private String mlTableFileName;
    private int stamps;
    private int defaultStats;
    private List<SimmulationData> data;
    private Integer timeStamp;
    private List<NewTeamData> undeliveredCommissions = new LinkedList<NewTeamData>();
    private Graph graph;
    private Boolean updateAfterArrival;
    private int graphChangeTimestamp = -1;
    private LinkedList<GraphLink> changedGraphLinks;
    private Brute2Sorter brute2Sorter;
    private int backToDepotTimestamp = -1;

    public Brute2Sorter getBrute2Sorter() {
        return brute2Sorter;
    }

    public void setBrute2Sorter(Brute2Sorter sorter) {
        this.brute2Sorter = sorter;
    }

    @Override
    protected abstract void setup();

    protected int getEUnitsCount() {
        return eUnitsCount;
    }

    public boolean isRecording() {
        return recording;
    }

    /**
     * Reads configuration file and adds agents representing drivers
     *
     * @param filePath path where drivers configuration file is
     * @throws IOException
     */
    protected int loadDriversProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        String[] lineParts = line.split("\t");
        int driversCount = Integer.parseInt(lineParts[0]);
        String costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
        if (lineParts.length > 1)
            costFunction = lineParts[1];

        for (int i = 0; i < driversCount; i++) {
            TransportElementInitialData initial = new TransportElementInitialData(
                    costFunction, 100000, 100000, 0);
            createNewTransportElement(initial, TransportType.DRIVER);
        }
        return driversCount;
    }

    public abstract void transportAgentConfirmationOfReceivingAgentsData();

    public abstract void transportAgentCreated();

    /**
     * Reads configuration file and adds agents representing trucks
     *
     * @param filePath path where trucks configuration file is
     * @throws IOException
     */
    protected int loadTrucksProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);

        String firstLine = br.readLine();
        String[] parts = firstLine.split("\t");
        int trucksCount = Integer.parseInt(parts[0]);
        String defaultCostFunction = null;
        if (parts.length > 1)
            defaultCostFunction = parts[1];

        ArrayList<TransportElementInitialDataTruck> trucksProperties = new ArrayList<TransportElementInitialDataTruck>();

        for (int i = 0; i < trucksCount; i++) {

            String lineParts[] = br.readLine().split("\t");

            int power = Integer.parseInt(lineParts[0]);
            int reliability = Integer.parseInt(lineParts[1]);
            int comfort = Integer.parseInt(lineParts[2]);
            int fuelConsumption = Integer.parseInt(lineParts[3]);
            int connectorType = Integer.parseInt(lineParts[4]);
            String costFunction;
            if (lineParts.length == 6) {
                costFunction = lineParts[5];
            } else {
                if (defaultCostFunction != null) {
                    costFunction = defaultCostFunction;
                } else {
                    costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
                }
            }

            TransportElementInitialDataTruck initial = new TransportElementInitialDataTruck(
                    costFunction, power, 0, 0, power, reliability, comfort,
                    fuelConsumption, connectorType);
            createNewTransportElement(initial, TransportType.TRUCK);
            trucksProperties.add(initial);
        }

        gui.setTrucksProperties(trucksProperties);
        return trucksCount;
    }

    /**
     * Reads configuration file and adds agents representing trailers
     *
     * @param filePath path where trailers configuration file is
     * @throws IOException
     */
    protected int loadTrailersProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);

        String firstLine = br.readLine();
        String[] parts = firstLine.split("\t");
        int trailersCount = Integer.parseInt(parts[0]);
        String defaultCostFunction = null;
        if (parts.length > 1)
            defaultCostFunction = parts[1];

        ArrayList<TransportElementInitialDataTrailer> trailersProperties = new ArrayList<TransportElementInitialDataTrailer>();

        for (int i = 0; i < trailersCount; i++) {

            String lineParts[] = br.readLine().split("\t");

            int mass = Integer.parseInt(lineParts[0]);
            int capacity = Integer.parseInt(lineParts[1]);
            int cargoType = Integer.parseInt(lineParts[2]);
            int universality = Integer.parseInt(lineParts[3]);
            int connectorType = Integer.parseInt(lineParts[4]);
            String costFunction;
            if (lineParts.length == 6) {
                costFunction = lineParts[5];
            } else {
                if (defaultCostFunction != null) {
                    costFunction = defaultCostFunction;
                } else {
                    costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
                }
            }

            TransportElementInitialDataTrailer initial = new TransportElementInitialDataTrailer(
                    costFunction, capacity, 0, 0, mass, capacity, cargoType,
                    universality, connectorType);

            createNewTransportElement(initial, TransportType.TRAILER);

            trailersProperties.add(initial);
        }
        gui.setTrailersProperties(trailersProperties);
        br.close();
        fr.close();
        return trailersCount;
    }

    /**
     * Registers services such as GuiService offered by a GuiAgent.
     */
    protected void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /* -------- GUI SERVICE ------- */
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("GUIService");
        sd1.setName("GUIService");
        dfd.addServices(sd1);
        logger.info(this.getLocalName() + " - registering GUIService");

        /* -------- REGISTRATION ------- */
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException "
                    + fe.getMessage());
        }
    }

    public void setExchangeAlgFactory(ExchangeAlgorithmsFactory factory) {
        this.exchangeAlgFactory = factory;
    }

    public void setSTAfterChange(boolean STAfterChange) {
        this.STAfterChange = STAfterChange;
    }

    public void setTrackFinder(TrackFinder finder) {
        this.trackFinder = finder;
    }

    public void setMLAlgorithm(MLAlgorithm table) {
        mlAlgorithm = table;
    }

    public void setExploration(boolean exploration) {
        this.exploration = exploration;
    }

    public void setFirstComplexSTResultOnly(boolean firstComplexSTResultOnly) {
        this.firstComplexSTResultOnly = firstComplexSTResultOnly;
    }

    public void setHolons(int holons) {
        this.holons = holons;
    }

    public void setDelayLimit(Double limit) {
        delayLimit = limit;
    }

    public void setPunishmentFunction(String fun) {
        punishmentFunction = fun;
    }

    public void setPunishmentFunctionDefaults(
            Map<String, Double> punishmentFunctionDefaults) {
        this.punishmentFunctionDefaults = punishmentFunctionDefaults;
    }

    public void setGraphLinkPredictor(GraphLinkPredictor predictor) {
        this.graphLinkPredictor = predictor;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendSimInfo(jade.core.AID)
     */
    public void sendSimInfo(AID aid) {

        ACLMessage cfp = null;

        cfp = new ACLMessage(CommunicationHelper.SIM_INFO);
        cfp.addReceiver(aid);

        try {

            SimInfo info = gui.getSimInfo();
            info.setPunishmentFunction(punishmentFunction);
            info.setDefaultPunishmentFunValues(punishmentFunctionDefaults);
            info.setDelayLimit(delayLimit);
            info.setHolons(holons);
            info.setFirstComplexSTResultOnly(firstComplexSTResultOnly);
            info.setMlAlgorithm(mlAlgorithm);
            info.setExploration(exploration);
            info.setTrackFinder(trackFinder, graphLinkPredictor);
            info.setSTAfterGraphChange(STAfterChange);
            info.setExchangeAlgFactory(exchangeAlgFactory);
            info.setBrute2Sorter(brute2Sorter);
            cfp.setContentObject(info);

        } catch (IOException e) {
            logger.error(getLocalName() + " - IOException " + e.getMessage());
        }

        logger.info(getLocalName() + " - sending SimInfo to "
                + aid.getLocalName());
        send(cfp);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendSimInfoToAll(dtp.simmulation.SimInfo)
     */
    public void sendSimInfoToAll(SimInfo simInfo) {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        logger.info(getLocalName() + " - sending SimInfo to Distributor");

        simInfo.setCalculatorsHolder(calculatorsHolder);
        simInfo.setPunishmentFunction(punishmentFunction);
        simInfo.setDefaultPunishmentFunValues(punishmentFunctionDefaults);
        simInfo.setDelayLimit(delayLimit);
        simInfo.setHolons(holons);
        simInfo.setFirstComplexSTResultOnly(firstComplexSTResultOnly);
        simInfo.setMlAlgorithm(mlAlgorithm);
        simInfo.setExploration(exploration);
        simInfo.setTrackFinder(trackFinder, graphLinkPredictor);
        simInfo.setSTAfterGraphChange(STAfterChange);
        simInfo.setExchangeAlgFactory(exchangeAlgFactory);
        simInfo.setBrute2Sorter(brute2Sorter);
        if (aids.length != 0) {
            simInfoReceived = aids.length;
            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.SIM_INFO);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(simInfo);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendGraphToEUnits(dtp.graph.Graph)
     */
    public void sendGraphToEUnits(Graph graph) {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        logger.info(getLocalName() + " - sending graph to " + aids.length
                + " EUnitAgents");

        if (aids.length != 0) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.GRAPH);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(graph);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendUpdatedGraphToEunits(dtp.graph.Graph)
     */
    public void sendUpdatedGraphToEunits(Graph graph) {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        logger.info(getLocalName() + " - sending graph update to "
                + aids.length + " EUnitAgents");

        if (aids.length != 0) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.GRAPH_UPDATE);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(graph);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#updateGraph(dtp.graph.Graph)
     */
    public void updateGraph(Graph graph) {

        gui.updateGraph(graph);

        sendUpdatedGraphToEunits(graph);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#simulationStart()
     */
    public void simulationStart() {
        SingletonGUI.getInstance().update(gui.getSimInfo());
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#nextSimstep()
     */
    public void nextSimstep() {

        gui.nextSimStep();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * dtp.jade.gui.GUIAgent2#addCommissionHandler(dtp.commission.CommissionHandler
     * )
     */
    public void addCommissionHandler(CommissionHandler commissionHandler) {

        commissionsHandler.addCommissionHandler(commissionHandler);
    }

    /*
     * (non-Javadoc)
     *
     * @seedtp.jade.gui.GUIAgent2#removeCommissionHandler(dtp.commission.
     * CommissionHandler)
     */
    public void removeCommissionHandler(CommissionHandler comHandler) {

        commissionsHandler.removeCommissionHandler(comHandler);
    }

    public void setMlTableFileName(String mlTableFileName) {
        this.mlTableFileName = mlTableFileName;
    }

    public void setChooseWorstCommission(String chooseWorstCommission) {
        this.chooseWorstCommission = chooseWorstCommission;
    }

    public void setConfChange(boolean confChange) {
        this.confChange = confChange;
    }

    public void setPrintersHolder(PrintersHolder printersHolder) {
        this.printersHolder = printersHolder;
    }

    public void setCalculatorsHolder(MeasureCalculatorsHolder calculatorsHolder) {
        this.calculatorsHolder = calculatorsHolder;
    }

    public void setDist(boolean isDist) {
        dist = isDist;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setSTDepth(int STDepth) {
        this.STDepth = STDepth;
    }

    public void setSimmulatedTrading(int simmulatedTrading) {
        this.simmulatedTradingCount = simmulatedTrading;
    }

    public void setDefaultAgentsData(DefaultAgentsData data) {
        defaultAgentsData = data;
    }

    public void setSendingCommissionsInGroups(boolean send) {
        commissionSendingType = send;
    }

    public void setChoosingByCost(boolean choosingByCost) {
        this.choosingByCost = choosingByCost;
    }

    public void setSTTimestampGap(int sTTimestampGap) {
        STTimestampGap = sTTimestampGap;
    }

    public void setSTCommissionGap(int sTCommissionGap) {
        STCommissionGap = sTCommissionGap;
    }

    public int getNextTimestamp(int timestamp) {//TODO
        int i = timestamp + 1;

        AID[] aids = CommunicationHelper.findAgentByServiceName(this, "ExecutionUnitService");

        sendUpdateCurrentLocationRequest(aids, i);

        while (i <= gui.getSimInfo().getDeadline() && commissionsHandler.getCommissionsBeforeTime(i).length == 0 && !commissionsHandler.isAnyEUnitAtNode(i, false)) {
            if (i == backToDepotTimestamp) {
                ACLMessage cfp;
                for (AID aid : aids) {
                    cfp = new ACLMessage(CommunicationHelper.BACK_TO_DEPOT);
                    cfp.addReceiver(aid);
                    cfp.setContent(new String(""));
                    send(cfp);
                }
            }

            i++;

            sendUpdateCurrentLocationRequest(aids, i);
        }

        return i;
    }

    /**
     * Trzeba to robic ze wzgledu na to ze sprawdzamy czy eunit w danym momencie dojechal do commissiona (do tego potrzeba zupdatowac ich current location)
     *
     * @param aids
     * @param timestamp
     * @author Szyna
     */
    private void sendUpdateCurrentLocationRequest(AID[] aids, int timestamp) {
        ACLMessage cfp;

        updateCurrentLocationSemaphore = new Semaphore(0);

        for (AID aid : aids) {
            cfp = new ACLMessage(CommunicationHelper.UPDATE_CURRENT_LOCATION);
            cfp.addReceiver(aid);
            cfp.setContent(new Integer(timestamp).toString());
            send(cfp);
        }

        //czekamy az kazdy z eunitow potwierdzi otrzymanie komunikatu
        try {
            updateCurrentLocationSemaphore.acquire(aids.length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendCommissions(int)
     */
    public void sendCommissions(int simTime) {

        CommissionHandler[] tempCommissionsHandler = commissionsHandler
                .getCommissionsBeforeTime(simTime);

        if (tempCommissionsHandler.length == 0) {
            gui.nextAutoSimStep();
            return;
        }

        Commission[] tempCommissions = new Commission[tempCommissionsHandler.length];
        for (int i = 0; i < tempCommissionsHandler.length; i++) {

            tempCommissions[i] = tempCommissionsHandler[i].getCommission();
            removeCommissionHandler(tempCommissionsHandler[i]);
        }
        Arrays.sort(tempCommissions);

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        logger.info(getLocalName() + " - sending " + tempCommissions.length
                + " commission(s) to Distributor Agent");

        if (aids.length == 1) {

            ACLMessage cfp = new ACLMessage(CommunicationHelper.COMMISSION);
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject(new CommissionsHolder(tempCommissions,
                        commissionSendingType, choosingByCost,
                        simmulatedTradingCount, STDepth, defaultAgentsData,
                        chooseWorstCommission, algorithm, dist, STTimestampGap,
                        STCommissionGap, confChange));

            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);

            gui.displayMessage(getLocalName() + " - " + tempCommissions.length
                    + " commission(s) sent to Distributor Agent");

            /**
             * @author Szyna
             * Dodane zeby kazdy eunit wrocil do depotu przed skonczeniem symulacji
             */
            if (commissionsHandler.getComsSize() == 0) {
                backToDepotTimestamp = simTime + 10;
            }

        } else if (aids.length == 0) {

            logger.error("There is no Distributor Agent in the system");
        } else {

            logger.error("More than one Distributor Agent in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#sendTimestamp(int)
     */
    public void sendTimestamp(int time) {

        logger.info(getLocalName() + " - sending time stamp [" + time + "]");
        gui.displayMessage(getLocalName() + " - sending time stamp [" + time + "]");
        SingletonGUI.getInstance().newTimestamp(time);

        AID[] aids = null;
        ACLMessage cfp = null;

        /* -------- EUNITS SECTION ------- */
        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        stamps = aids.length + 2;
        if (aids.length > 0) {
            cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
            for (int i = 0; i < aids.length; i++) {
                cfp.addReceiver(aids[i]);
            }
            try {
                cfp.setContentObject(time);
            } catch (IOException e) {
                logger.error(getLocalName() + " - IOException "
                        + e.getMessage());
            }
            send(cfp);

        } else {

            logger.info(getLocalName()
                    + " - there are no EUnit Agents in the system");
        }

        /* -------- CRISIS MANAGER SECTION ------- */
        aids = CommunicationHelper.findAgentByServiceName(this,
                "CrisisManagementService");

        if (aids.length == 1) {

            for (int i = 0; i < aids.length; i++) {
                cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(time);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - none or more than one Crisis Manager Agent in the system");
        }

        /* -------- DISTRIBUTOR SECTION ------- */
        aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {

            for (int i = 0; i < aids.length; i++) {
                cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(time);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - none or more than one Crisis Manager Agent in the system");
        }
    }

    public synchronized void stampConfirmed() {
        stamps--;
        if (stamps == 0) {
            gui.nextSimStep3();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#timerStart()
     */
    public void timerStart() {

        timer.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#timerStop()
     */
    public void timerStop() {

        timer.stop();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#isTimerRunning()
     */
    public boolean isTimerRunning() {

        return timer.isRunning();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#getTimerDelay()
     */
    public int getTimerDelay() {

        return timerDelay;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#setTimerDelay(int)
     */
    public void setTimerDelay(int timerDelay) {

        this.timerDelay = timerDelay;
        timer.setDelay(timerDelay);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#getComsWaiting()
     */
    public int getComsWaiting() {

        return commissionsHandler.getComsSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#getLocations()
     */
    public ArrayList<GraphPoint> getLocations() {

        CommissionHandler[] coms;
        ArrayList<GraphPoint> points;
        double px, py;

        coms = commissionsHandler.getCommissionHandlers();

        if (coms.length == 0)
            return null;

        points = new ArrayList<GraphPoint>();

        // POINTS
        for (int i = 0; i < coms.length; i++) {

            px = coms[i].getCommission().getPickupX();
            py = coms[i].getCommission().getPickupY();

            if (!containsPoint(points, px, py))
                points.add(new GraphPoint(px, py, "pt_" + px + "_" + py));

            px = coms[i].getCommission().getDeliveryX();
            py = coms[i].getCommission().getDeliveryY();

            if (!containsPoint(points, px, py))
                points.add(new GraphPoint(px, py, "pt_" + px + "_" + py));
        }

        // BASE
        Point depot = gui.getDepotLocation();
        if (!containsPoint(points, (int) depot.getX(), (int) depot.getY()))
            points.add(new GraphPoint((int) depot.getX(), (int) depot.getY(),
                    "base_" + depot.getX() + "_" + depot.getY(), true, 0));

        return points;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#askForEUnitsCalendars()
     */
    public void askForEUnitsCalendars() {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (calendarsHolder != null) {

            gui.displayMessage(getLocalName()
                    + " - can't ask for agent's clendars,"
                    + " previous request in progress");
            return;
        }

        if (aids.length > 0) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.EUNIT_SHOW_CALENDAR);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

            calendarsHolder = new CalendarsHolder(aids.length);

        } else {

            logger.info(getLocalName()
                    + " - There are no agents with ExecutionUnitService in the system");
            gui.displayMessage("There are no agents with ExecutionUnitService in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#addCalendar(java.lang.String,
     * java.lang.String)
     */
    public void addCalendar(String agent, String calendar) {

        if (calendarsHolder == null) {

            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            return;
        }

        calendarsHolder.addCalendar(agent, calendar);

        if (calendarsHolder.gotAllCalendarStats()) {

            displayCalendars();
            calendarsHolder = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#displayCalendars()
     */
    public void displayCalendars() {

        displayMessage(calendarsHolder.getAllStats());
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#askForEUnitsCalendarStats()
     */
    public void askForEUnitsCalendarStats() {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (calendarStatsHolder != null) {

            gui.displayMessage(getLocalName()
                    + " - can't ask for clendar stats,"
                    + " previous request in progress");
            return;
        }

        if (aids.length > 0) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.EUNIT_SHOW_STATS);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

            calendarStatsHolder = new CalendarStatsHolder(aids.length);

        } else {

            logger.info(getLocalName()
                    + " - There are no agents with ExecutionUnitService in the system");
            gui.displayMessage("There are no agents with ExecutionUnitService in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#saveStatsToFile(java.lang.String)
     */
    public void saveStatsToFile(String fileName, long simTime) {

        this.simTime = simTime;

        AID[] aids = null;
        ACLMessage cfp = null;

        saveFileName = fileName;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (calendarStatsHolderForFile != null) {

            gui.displayMessage(getLocalName()
                    + " - can't ask for clendar stats for file,"
                    + " previous request in progress");
            return;
        }

        if (aids.length > 0) {

            defaultStats = 0;
            calendarStatsHolderForFile = new CalendarStatsHolder(aids.length);

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(
                        CommunicationHelper.EUNIT_SHOW_STATS_TO_WRITE);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - There are no agents with ExecutionUnitService in the system");
            gui.displayMessage("There are no agents with ExecutionUnitService in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * dtp.jade.gui.GUIAgent2#addCalendarStats(dtp.jade.agentcalendar.CalendarStats
     * )
     */
    public void addCalendarStats(CalendarStats calendarStats) {

        if (calendarStatsHolder == null) {

            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            return;
        }

        calendarStatsHolder.addCalendarStats(calendarStats);

        if (calendarStatsHolder.gotAllCalendarStats()) {
            displayStats();
            calendarStatsHolder = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * dtp.jade.gui.GUIAgent2#addCalendarStatsToFile(dtp.jade.agentcalendar.
     * CalendarStats)
     */
    public void addCalendarStatsToFile(CalendarStats calendarStats) {
        if (calendarStatsHolderForFile == null) {

            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            return;
        }

        if (calendarStats.isDefault())
            defaultStats++;
        calendarStatsHolderForFile.addCalendarStats(calendarStats);

        if (calendarStatsHolderForFile.gotAllCalendarStats()) {

            /* Zapis do statystyk do pliku */

            CalendarStatsHolder calendarStatsHolder = new CalendarStatsHolder(
                    calendarStatsHolderForFile
                            .getCollectedCalendarStatsNumber() - defaultStats);
            CalendarStatsHolder defaultHolons = new CalendarStatsHolder(
                    defaultStats);

            for (CalendarStats stat : calendarStatsHolderForFile.getAllStats()) {
                if (stat.isDefault())
                    defaultHolons.addCalendarStats(stat);
                else
                    calendarStatsHolder.addCalendarStats(stat);
            }

            SingletonGUI.getInstance().setHolonStatsHolder(calendarStatsHolder);

            try {
                File file;
                File roadFile;
                if (saveFileName == null)
                    file = new File("wynik.xls");
                else
                    file = new File(saveFileName);
                roadFile = new File(file.getAbsolutePath() + "_road.txt");
                file.createNewFile();
                roadFile.createNewFile();
                BufferedWriter wr = new BufferedWriter(new FileWriter(file));
                BufferedWriter wr_road = new BufferedWriter(new FileWriter(
                        roadFile));
                Integer delivery = printStats(calendarStatsHolder, wr, wr_road);
                wr.newLine();
                wr.newLine();
                wr.newLine();
                if (defaultStats > 0) {
                    wr.append("Default Agents Status");
                    wr.newLine();
                    wr.flush();
                    delivery += printStats(defaultHolons, wr, wr_road);
                    wr.newLine();
                    wr.newLine();
                    wr.newLine();
                    wr.append("SUMMARY");
                    wr.newLine();
                    wr.write("Total cost\tTotal distance\tTotal WAIT time\tTotal drive time\tTotal punishment\tSim Time\tCommisions Count\tDelivered");
                    wr.newLine();
                    wr.flush();
                    wr.write(new Double(calendarStatsHolderForFile
                            .calculateCost(null)).toString() + "\t");
                    wr.write(new Double(calendarStatsHolderForFile
                            .calculateDistanceSum()).toString() + "\t");
                    wr.write(new Double(calendarStatsHolderForFile
                            .calculateWaitTime()).toString() + "\t");
                    wr.write(new Double(calendarStatsHolderForFile
                            .calculateDriveTime()).toString() + "\t");
                    wr.write(new Double(calendarStatsHolderForFile
                            .calculatePunishment()).toString() + "\t");
                    wr.write(new Long(simTime).toString() + "\t");
                    wr.write(gui.getCommissionsTab().getCommisionsCount()
                            + "\t");
                    wr.write(delivery.toString());
                    wr.flush();
                }
                wr.flush();
                if (undeliveredCommissions.size() > 0) {
                    wr.write("Undelivered commissions:");
                    wr.newLine();
                    for (NewTeamData data : undeliveredCommissions) {
                        wr.write(data.getCreationTime() + ": "
                                + data.getCommission());
                        wr.newLine();
                    }
                    wr.flush();
                }
                wr.close();
                calendarStatsHolderForFile = null;

                if (recording)
                    new XMLBuilder(simmulationData, gui.getSimInfo().getDepot())
                            .save(file.getAbsolutePath() + ".xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveMeasures();
            saveMLTable();
            // don't write any code here
        }
    }

    // TODO algorithm
    private int printStats(CalendarStatsHolder holder, BufferedWriter wr,
                           BufferedWriter wr_road) throws IOException {
        wr.write("Name\tCapacity\tCost\tDistance\tWait time\tDrive time\tPunishment\tTrailer mass\tTruck power\tTruck reliability\tTruck comfort\tTruck fuel consumption\tMaxSTDepth");
        wr.newLine();
        wr.flush();
        Integer delivery = 0;
        for (CalendarStats stat : holder.getAllStats()) {
            if (stat.getCost() == 0 && stat.getDistance() == 0)
                continue;
            wr.write(stat.getAID().getLocalName() + "\t");
            wr.write(stat.getCapacity() + "\t");
            wr.write(stat.getCost() + "\t");
            wr.write(new Double(stat.getDistance()).toString() + "\t");
            wr.write(new Double(stat.getWaitTime()).toString() + "\t");
            wr.write(new Double(stat.getDriveTime()).toString() + "\t");
            wr.write(new Double(stat.getPunishment()).toString() + "\t");
            wr.write(new Double(stat.getMass()).toString() + "\t");
            wr.write(new Double(stat.getPower()).toString() + "\t");
            wr.write(new Double(stat.getReliability()).toString() + "\t");
            wr.write(new Double(stat.getComfort()).toString() + "\t");
            wr.write(new Double(stat.getFuelConsumption()).toString() + "\t");
            wr.write(new Integer(stat.getMaxSTDepth()).toString());
            wr.newLine();
            wr.flush();
            if (stat.getSchedule2() == null)
                for (CalendarAction action : stat.getSchedule()) {
                    if (action.getType().equals("DELIVERY")) {
                        delivery++;
                        wr_road.write(new Integer(action
                                .getSourceCommissionID()).toString() + " ");
                    } else if (action.getType().equals("PICKUP")) {
                        wr_road.write(new Integer(action
                                .getSourceCommissionID()).toString() + " ");
                    }
                }
            else {
                Schedule schedule = stat.getSchedule2();
                for (int i = 0; i < schedule.size(); i++) {
                    if (schedule.isPickup(i)) {
                        wr_road.write(schedule.getCommission(i).getPickUpId()
                                + " ");
                    } else {
                        delivery++;
                        wr_road.write(schedule.getCommission(i).getDeliveryId()
                                + " ");
                    }
                }
            }
            wr_road.newLine();
            wr_road.flush();
        }
        wr.newLine();
        wr.write("SUMMARY");
        wr.newLine();
        wr.write("Total cost\tTotal distance\tTotal WAIT time\tTotal drive time\tTotal punishment\tSim Time\tCommisions Count\tDelivered");
        wr.newLine();
        wr.flush();
        wr.write(new Double(holder.calculateCost(null)).toString() + "\t");
        wr.write(new Double(holder.calculateDistanceSum()).toString() + "\t");
        wr.write(new Double(holder.calculateWaitTime()).toString() + "\t");
        wr.write(new Double(holder.calculateDriveTime()).toString() + "\t");
        wr.write(new Double(holder.calculatePunishment()).toString() + "\t");
        wr.write(new Long(simTime).toString() + "\t");
        wr.write(gui.getCommissionsTab().getCommisionsCount() + "\t");
        wr.write(delivery.toString());
        wr.flush();
        return delivery;
    }

    private void saveMeasures() {
        if (printersHolder == null) {
            if (mlAlgorithm == null)
                simEnd();
            return;
        }
        ACLMessage msg = new ACLMessage(CommunicationHelper.MEASURE_DATA);
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        msg.addReceiver(aids[0]);

        try {
            msg.setContentObject("");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        send(msg);
    }

    public void printMeasures(MeasureData data) {

        try {
            printersHolder.print(saveFileName, data, calculatorsHolder);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        if (mlAlgorithm == null)
            simEnd();
    }

    private void saveMLTable() {
        if (mlAlgorithm != null && exploration == true) {
            AID aid = CommunicationHelper.findAgentByServiceName(this,
                    "CommissionService")[0];
            ACLMessage msg = new ACLMessage(CommunicationHelper.MLTable);
            msg.addReceiver(aid);
            try {
                msg.setContentObject("");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.send(msg);
        } // else {
        // simEnd();
        // }
    }

    public void saveMLAlgorithm(MLAlgorithm table) {
        try {
            table.save(mlTableFileName, saveFileName);

            simEnd();
        } catch (Exception e) {
            e.printStackTrace();
            simEnd();
        }
    }

    protected void displayStats() {

        displayMessage(calendarStatsHolder.getAllStatsToString());
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#askForDistributorNooneList()
     */
    public void askForDistributorNooneList() {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(
                        CommunicationHelper.DISTRIBUTOR_SHOW_NOONE_LIST);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - none or more than one agent with CommissionService in the system");
            gui.displayMessage("None or more than one agent with CommissionService in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#addNooneList(int)
     */
    public void addNooneList(int nooneListSize) {
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#displayMessage(java.lang.String)
     */
    public synchronized void displayMessage(String msg) {

        gui.displayMessage(msg);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#updateEUnitInfo(dtp.jade.eunit.EUnitInfo)
     */
    public void updateEUnitInfo(EUnitInfo eUnitInfo) {
        gui.updateEUnitsInfo(eUnitInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * dtp.jade.gui.GUIAgent2#sendCrisisEvent(dtp.jade.crisismanager.crisisevents
     * .CrisisEvent)
     */
    public void sendCrisisEvent(CrisisEvent event) {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "CrisisManagementService");

        if (aids.length == 1) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.CRISIS_EVENT);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject(event);
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - none or more than one agent with CrisisManagementService in the system");
            gui.displayMessage("None or more than one agent with CrisisManagementService in the system");
        }
    }

    protected boolean containsPoint(ArrayList<GraphPoint> points, double px,
                                    double py) {

        Iterator<GraphPoint> iter;
        GraphPoint tmpPoint;

        iter = points.iterator();

        while (iter.hasNext()) {

            tmpPoint = iter.next();
            if (tmpPoint.getX() == px && tmpPoint.getY() == py)
                return true;
        }

        return false;
    }

    protected void createNewEUnit(EUnitInitialData initialData) {

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {

            ACLMessage cfp = new ACLMessage(
                    CommunicationHelper.EXECUTION_UNIT_CREATION);
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject(initialData);
            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    protected void createNewTransportElement(TransportElementInitialData data,
                                             TransportType type) {
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {

            ACLMessage cfp = null;
            if (type == TransportType.DRIVER) {
                cfp = new ACLMessage(CommunicationHelper.DRIVER_CREATION);
            } else if (type == TransportType.TRAILER) {
                cfp = new ACLMessage(CommunicationHelper.TRAILER_CREATION);
            } else {
                cfp = new ACLMessage(CommunicationHelper.TRUCK_CREATION);
                // data = data;
            }
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject(data);
            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#resetEnvironment()
     */
    public void resetEnvironment() {

        commissionsHandler = new CommissionsHandler();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#resetEUnitAgents()
     */
    public void resetEUnitAgents() {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        logger.info(getLocalName()
                + " - sending RESET request to ExecutionUnit Agents");
        gui.displayMessage("Sending RESET request to ExecutionUnit Agents");

        if (aids.length > 0) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.RESET);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - There are no agents with ExecutionUnitService in the system");
            gui.displayMessage("There are no agents with ExecutionUnitService in the system");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#resetTransportUnits()
     */
    public void resetTransportUnits() {
        AID[] aids = null;
        ACLMessage msg = null;
        aids = CommunicationHelper.findAgentByServiceName(this,
                "TransportUnitService");

        logger.info(getLocalName()
                + " - sending RESET request to TransportUnit Agents");
        gui.displayMessage("Sending RESET request to TransportUnit Agents");

        if (aids.length > 0) {

            for (int i = 0; i < aids.length; i++) {

                msg = new ACLMessage(CommunicationHelper.RESET);
                msg.addReceiver(aids[i]);
                try {
                    msg.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(msg);
            }

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#resetDistributorAgent()
     */
    public void resetDistributorAgent() {

        AID[] aids = null;
        ACLMessage cfp = null;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        logger.info(getLocalName()
                + " - sending RESET request to DistributorAgent");
        gui.displayMessage("Sending RESET request to DistributorAgent");

        if (aids.length == 1) {

            for (int i = 0; i < aids.length; i++) {

                cfp = new ACLMessage(CommunicationHelper.RESET);
                cfp.addReceiver(aids[i]);
                try {
                    cfp.setContentObject("");
                } catch (IOException e) {
                    logger.error(getLocalName() + " - IOException "
                            + e.getMessage());
                }
                send(cfp);
            }

        } else {

            logger.info(getLocalName()
                    + " - None or more than one agent with CommissionService in the system ("
                    + aids.length + ")");
            gui.displayMessage("None or more than one agent with CommissionService in the system ("
                    + aids.length + ")");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.jade.gui.GUIAgent2#nextAutoSimStep()
     */
    public void nextAutoSimStep() {
        gui.nextAutoSimStep();
    }

    public void simEnd() {

    }

    public void getSimmulationData(int timestamp) {
        data = new LinkedList<SimmulationData>();
        this.timeStamp = timestamp;

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");
        eUnitsCount = aids.length;
        ACLMessage msg = new ACLMessage(CommunicationHelper.SIMULATION_DATA);
        for (AID aid : aids)
            msg.addReceiver(aid);
        try {
            msg.setContentObject("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        send(msg);

        if (eUnitsCount == 0)
            gui.nextSimStep2();

    }

    public synchronized void addSimmulationData(SimmulationData data) {
        //System.err.println(data);
        eUnitsCount--;
        this.data.add(data);
        ///
        SingletonGUI.getInstance().update(data);
        if (eUnitsCount == 0) {
            simmulationData.put(timeStamp, this.data);
            gui.nextSimStep2();
        }
    }

    public void resetGUIAgent() {
        undeliveredCommissions = new LinkedList<NewTeamData>();
    }

    public synchronized void addUndeliveredCommission(NewTeamData data) {
        undeliveredCommissions.add(data);
    }

    public synchronized void changeGraph(Graph graph, int timestamp) {
        if (graphChangeTime.equals("immediately")) {
            updateAfterArrival = false;
        } else {
            if (graphChangeTimestamp == -1) {
                graphChangeTimestamp = timestamp;
            }
            if (graphChangeTime.equals("afterTime")
                    && timestamp >= graphChangeTimestamp + graphChangeFreq) {
                updateAfterArrival = false;
                graphChangeTimestamp = -1;
            } else {
                updateAfterArrival = true;
            }
        }
        System.out.println("graph changed");
        SingletonGUI.getInstance().update(graph);
        ACLMessage msg = new ACLMessage(CommunicationHelper.GRAPH_CHANGED);
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");
        this.eUnitsCount = aids.length;
        this.graph = graph;
        for (AID aid : aids)
            msg.addReceiver(aid);
        try {
            msg.setContentObject(new Object[]{graph, updateAfterArrival});
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.send(msg);
    }

    public synchronized void graphChanged(boolean isEUnit) {
        if (isEUnit) {
            eUnitsCount--;
            if (eUnitsCount == 0) {
                ACLMessage msg = new ACLMessage(
                        CommunicationHelper.GRAPH_CHANGED);
                msg.addReceiver(CommunicationHelper.findAgentByServiceName(
                        this, "CommissionService")[0]);
                try {
                    msg.setContentObject(graph);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.send(msg);
            }
        } else
            gui.nextSimStep4();
    }

    public synchronized void askForGraphChanges() {
        if (!graphChangeTime.equals("afterChangeNotice")) {
            gui.nextSimStep5();
            return;
        }
        changedGraphLinks = new LinkedList<GraphLink>();
        ACLMessage msg = new ACLMessage(
                CommunicationHelper.ASK_IF_GRAPH_LINK_CHANGED);
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");
        this.eUnitsCount = aids.length;
        for (AID aid : aids)
            msg.addReceiver(aid);
        try {
            msg.setContentObject("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.send(msg);
    }

    public synchronized void addChangedLink(GraphLink link) {
        eUnitsCount--;
        if (link != null)
            changedGraphLinks.add(link);
        if (eUnitsCount == 0) {
            ACLMessage msg = new ACLMessage(
                    CommunicationHelper.GRAPH_LINK_CHANGED);
            AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                    "ExecutionUnitService");
            this.eUnitsCount = aids.length;
            for (AID aid : aids)
                msg.addReceiver(aid);
            try {
                msg.setContentObject(changedGraphLinks);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.send(msg);
        }
    }

    public synchronized void linkChanged() {
        eUnitsCount--;
        if (eUnitsCount == 0) {
            gui.nextSimStep5();
        }
    }

    public CommissionsHandler getCommissionHandler() {
        return commissionsHandler;
    }
}
