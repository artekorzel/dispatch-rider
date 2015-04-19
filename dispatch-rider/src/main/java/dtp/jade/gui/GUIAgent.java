package dtp.jade.gui;

import adapter.Adapter;
import algorithm.Algorithm;
import algorithm.Brute2Sorter;
import algorithm.BruteForceAlgorithm;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.Schedule;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.CommissionsHandler;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.graph.GraphLink;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarAction;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.gui.behaviour.*;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportType;
import dtp.logic.SimLogic;
import dtp.optimization.TrackFinder;
import dtp.simulation.SimInfo;
import dtp.util.ExtensionFilter;
import dtp.xml.ConfigurationParser;
import dtp.xml.ParseException;
import gui.main.SingletonGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import measure.printer.MeasureData;
import measure.printer.PrintersHolder;
import org.apache.log4j.Logger;
import pattern.ConfigurationChooser;
import xml.elements.SimulationData;
import xml.elements.XMLBuilder;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class GUIAgent extends Agent {

    protected static Logger logger = Logger.getLogger(GUIAgent.class);
    // trzyma obiekty CommissionHandler (zlecenie wraz z czasem naplyniecia
    // do systemu), w odpowiednim czasie wysyla zlecenie do dystrybutora
    protected CommissionsHandler commissionsHandler;

    protected SimLogic simLogic;
    protected Timer timer;
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
    protected Map<Integer, List<SimulationData>> simulationData = new TreeMap<>();
    protected long simTime;
    protected String punishmentFunction;
    protected Map<String, Double> punishmentFunctionDefaults;
    protected Double delayLimit;
    protected int holons;
    protected boolean firstComplexSTResultOnly;
    protected MLAlgorithm mlAlgorithm;
    protected boolean exploration;
    protected TrackFinder trackFinder;
    protected GraphLinkPredictor graphLinkPredictor;
    protected boolean STAfterChange;
    protected ExchangeAlgorithmsFactory exchangeAlgFactory;
    protected boolean commissionSendingType = false;
    protected boolean choosingByCost = true;
    protected int simulatedTradingCount = 0;
    protected int STDepth = 1;
    protected DefaultAgentsData defaultAgentsData = null;
    protected String chooseWorstCommission;
    protected Algorithm algorithm = new BruteForceAlgorithm();
    protected boolean dist;
    protected int STTimestampGap;
    protected int STCommissionGap;
    protected PrintersHolder printersHolder;
    protected MeasureCalculatorsHolder calculatorsHolder;
    protected boolean confChange;
    protected String mlTableFileName;
    protected int stamps;
    protected int defaultStats;
    protected List<SimulationData> data;
    protected Integer timeStamp;
    protected List<NewTeamData> undeliveredCommissions = new LinkedList<>();
    protected Graph graph;
    protected int graphChangeTimestamp = -1;
    protected LinkedList<GraphLink> changedGraphLinks;
    protected Brute2Sorter brute2Sorter;
    protected int backToDepotTimestamp = -1;

    private Iterator<TestConfiguration> configurationIterator = null;
    private TestConfiguration configuration;
    private int transportAgentsCreated;
    private int level;
    private int transportAgentsCount;

    public void setBrute2Sorter(Brute2Sorter sorter) {
        this.brute2Sorter = sorter;
    }

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " - Hello World!");

        try {
            javax.swing.UIManager.setLookAndFeel(Plastic3DLookAndFeel.class.getCanonicalName());
        } catch (Exception e) {
            logger.warn(e);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

		/* -------- SERVICES SECTION ------- */
        registerServices();

		/* -------- BEHAVIOURS SECTION ------- */
        this.addBehaviour(new GetSimInfoRequestBehaviour(this));
        this.addBehaviour(new GetMessageBehaviour(this));
        this.addBehaviour(new GetCalendarBehaviour(this));
        this.addBehaviour(new GetCalendarStatsBehaviour(this));
        this.addBehaviour(new GetGraphUpdateBehaviour(this));
        this.addBehaviour(new GetCalenderStatsToFileBehaviour(this));
        this.addBehaviour(new GetTransportAgentCreatedBehaviour(this));
        this.addBehaviour(new SimInfoReceivedBehaviour(this));
        this.addBehaviour(new GetConfirmOfTimeStampBehaviour(this));
        this.addBehaviour(new GetTransportAgentConfirmationBehaviour(this));
        this.addBehaviour(new GetSimulationDataBehaviour(this));
        this.addBehaviour(new GetUndeliveredCommissionBehaviour(this));
        this.addBehaviour(new GetMeasureDataBehaviour(this));
        this.addBehaviour(new GetMLTableBehaviour(this));
        this.addBehaviour(new GetGraphChangedBehaviour(this));
        this.addBehaviour(new GetAskForGraphChangesBehaviour(this));
        this.addBehaviour(new GetGraphLinkChangedBehaviour(this));

        logger.info("GuiAgent - end of initialization");

        Object[] args = getArguments();
        String configurationFile;
        if (args != null && args.length == 1) {
            /* Use supplied argument as location of configuration file */
            configurationFile = args[0].toString();
        } else {
            /* Allow use to choose configuration file */
            JFileChooser chooser = new JFileChooser(".");
            chooser.setSelectedFile(new File("configuration.xml"));
            chooser.setDialogTitle("Choose DispatchRider configuration file");
            chooser.setFileFilter(new ExtensionFilter(new String[]{"xml"}));
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                configurationFile = chooser.getSelectedFile().getAbsolutePath();
            } else {
                throw new RuntimeException("No configuration file supplied");
            }
        }

		/* -------- READ CONFIGURATION -------- */
        try {
            configurationIterator = ConfigurationParser
                    .parse(configurationFile).iterator();
        } catch (ParseException cause) {
            throw new RuntimeException(
                    "Error while parsing configuration file", cause);
        }

        nextTest();
    }

    public void nextTest() {
        if (!configurationIterator.hasNext()) {
            logger.info("End of simulation: "
                    + Calendar.getInstance().getTime());
        } else {
            configuration = configurationIterator.next();

		/* -------- INTERFACE CREATION SECTION ------- */
            simLogic = new SimLogic(this);
        /* -------- TIME TASK PERFORMER SECTION ------- */
            ActionListener timerTaskPerformer = new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    simLogic.nextSimStep();
                }
            };

            timerDelay = 200;
            timer = new Timer(timerDelay, timerTaskPerformer);

		/* -------- COMMISSIONS HANDLER SECTION ------- */
            commissionsHandler = new CommissionsHandler();

            try {
                transportAgentsCreated = 0;
                level = 1;
                agentsCount = loadDriversProperties(configuration
                        .getConfigurationDirectory()
                        + File.separator
                        + "drivers.properties");
            } catch (FileNotFoundException e) {
                logger.fatal("properties file not found", e);

            } catch (IOException e) {
                logger.fatal("reading properties file failed", e);
            }
        }
    }

    public boolean isRecording() {
        return recording;
    }

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

    public void transportAgentConfirmationOfReceivingAgentsData() {
        transportAgentsCount--;
        if (transportAgentsCount == -1) {
            next2();
        }
    }

    public void transportAgentCreated() {
        transportAgentsCreated++;
        if (transportAgentsCreated == agentsCount) {
            switch (level) {
                case 1:
                    level = 2;
                    try {
                        transportAgentsCreated = 0;
                        agentsCount = loadTrailersProperties(configuration
                                .getConfigurationDirectory()
                                + File.separator
                                + "trailers.properties");
                    } catch (IOException e) {
                        logger.fatal("reading properties file failed", e);
                    }
                    break;
                case 2:
                    try {
                        level = 3;
                        transportAgentsCreated = 0;
                        agentsCount = loadTrucksProperties(configuration
                                .getConfigurationDirectory()
                                + File.separator
                                + "trucks.properties");
                    } catch (IOException e) {
                        logger.fatal("reading properties file failed", e);
                    }
                    break;
                case 3:
                    level = 0;
                    next();
                    break;
            }
        }
    }

    protected int loadTrucksProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);

        String firstLine = br.readLine();
        String[] parts = firstLine.split("\t");
        int trucksCount = Integer.parseInt(parts[0]);
        String defaultCostFunction = null;
        if (parts.length > 1)
            defaultCostFunction = parts[1];

        List<TransportElementInitialDataTruck> trucksProperties = new ArrayList<>();

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

        simLogic.setTrucksProperties(trucksProperties);
        return trucksCount;
    }

    protected int loadTrailersProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);

        String firstLine = br.readLine();
        String[] parts = firstLine.split("\t");
        int trailersCount = Integer.parseInt(parts[0]);
        String defaultCostFunction = null;
        if (parts.length > 1)
            defaultCostFunction = parts[1];

        ArrayList<TransportElementInitialDataTrailer> trailersProperties = new ArrayList<>();

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
        simLogic.setTrailersProperties(trailersProperties);
        br.close();
        fr.close();
        return trailersCount;
    }

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

    public void sendSimInfo(AID aid) {

        ACLMessage cfp;

        cfp = new ACLMessage(CommunicationHelper.SIM_INFO);
        cfp.addReceiver(aid);

        try {

            SimInfo info = simLogic.getSimInfo();
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

    public void sendSimInfoToAll(SimInfo simInfo) {

        AID[] aids;
        ACLMessage cfp;

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
            for (AID aid : aids) {
                cfp = new ACLMessage(CommunicationHelper.SIM_INFO);
                cfp.addReceiver(aid);
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

    public void sendUpdatedGraphToEunits(Graph graph) {
        AID[] aids;
        ACLMessage cfp;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        logger.info(getLocalName() + " - sending graph update to "
                + aids.length + " EUnitAgents");

        if (aids.length != 0) {
            for (AID aid : aids) {

                cfp = new ACLMessage(CommunicationHelper.GRAPH_UPDATE);
                cfp.addReceiver(aid);
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

    public void updateGraph(Graph graph) {
        sendUpdatedGraphToEunits(graph);
    }

    public void simulationStart() {
        SingletonGUI.getInstance().update(simLogic.getSimInfo());
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        commissionsHandler.addCommissionHandler(commissionHandler);
    }

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

    public void setSimulatedTrading(int simulatedTrading) {
        this.simulatedTradingCount = simulatedTrading;
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

    public int getNextTimestamp(int timestamp) {
        ++timestamp;

        AID[] aids = CommunicationHelper.findAgentByServiceName(this, "ExecutionUnitService");
        sendUpdateCurrentLocationRequest(aids, timestamp);

        while (timestamp <= simLogic.getSimInfo().getDeadline()
                && commissionsHandler.getCommissionsBeforeTime(timestamp).length == 0
                && !commissionsHandler.isAnyEUnitAtNode(false)) {
            if (timestamp == backToDepotTimestamp) {
                ACLMessage cfp;
                for (AID aid : aids) {
                    cfp = new ACLMessage(CommunicationHelper.BACK_TO_DEPOT);
                    cfp.addReceiver(aid);
                    cfp.setContent("");
                    send(cfp);
                }
            }

            timestamp++;
            sendUpdateCurrentLocationRequest(aids, timestamp);
        }

        return timestamp;
    }

    /**
     * Trzeba to robic ze wzgledu na to ze sprawdzamy czy eunit w danym momencie dojechal do commissiona (do tego potrzeba zupdatowac ich current location)
     */
    private void sendUpdateCurrentLocationRequest(AID[] aids, int timestamp) {
        ACLMessage cfp;

        for (AID aid : aids) {
            cfp = new ACLMessage(CommunicationHelper.UPDATE_CURRENT_LOCATION);
            cfp.addReceiver(aid);
            cfp.setContent(Integer.toString(timestamp));
            send(cfp);
        }

        //czekamy az kazdy z eunitow potwierdzi otrzymanie komunikatu
        for (AID ignored : aids) {
            blockingReceive(MessageTemplate.MatchPerformative(CommunicationHelper.UPDATE_CURRENT_LOCATION));
        }

    }

    public void sendCommissions(int simTime) {
        CommissionHandler[] tempCommissionsHandler = commissionsHandler
                .getCommissionsBeforeTime(simTime);

        if (tempCommissionsHandler.length == 0) {
            simLogic.nextAutoSimStep();
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
                        simulatedTradingCount, STDepth, defaultAgentsData,
                        chooseWorstCommission, algorithm, dist, STTimestampGap,
                        STCommissionGap, confChange));

            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);

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

    public void sendTimestamp(int time) {
        logger.info(getLocalName() + " - sending timestamp [" + time + "]");
        SingletonGUI.getInstance().newTimestamp(time);

        AID[] aids;
        ACLMessage cfp;

        /* -------- EUNITS SECTION ------- */
        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        stamps = aids.length + 2;
        if (aids.length > 0) {
            cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
            for (AID aid : aids) {
                cfp.addReceiver(aid);
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
            for (AID aid : aids) {
                cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
                cfp.addReceiver(aid);
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
            for (AID aid : aids) {
                cfp = new ACLMessage(CommunicationHelper.TIME_CHANGED);
                cfp.addReceiver(aid);
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
        if (--stamps == 0) {
            simLogic.nextSimStep3();
        }
    }

    public void addCalendar(String agent, String calendar) {
        if (calendarsHolder == null) {
            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            return;
        }

        calendarsHolder.addCalendar(agent, calendar);

        if (calendarsHolder.gotAllCalendarStats()) {
            calendarsHolder = null;
        }
    }

    public void saveStatsToFile(String fileName, long simTime) {

        this.simTime = simTime;

        AID[] aids;
        ACLMessage cfp;

        saveFileName = fileName;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "ExecutionUnitService");

        if (calendarStatsHolderForFile != null) {
            return;
        }

        if (aids.length > 0) {

            defaultStats = 0;
            calendarStatsHolderForFile = new CalendarStatsHolder(aids.length);

            for (AID aid : aids) {
                cfp = new ACLMessage(
                        CommunicationHelper.EUNIT_SHOW_STATS_TO_WRITE);
                cfp.addReceiver(aid);
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
        }
    }

    public void addCalendarStats(CalendarStats calendarStats) {
        if (calendarStatsHolder == null) {
            logger.error(getLocalName()
                    + " - no calendarStatsHolder to add stats to");
            return;
        }

        calendarStatsHolder.addCalendarStats(calendarStats);

        if (calendarStatsHolder.gotAllCalendarStats()) {
            calendarStatsHolder = null;
        }
    }

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
                if (saveFileName == null) {
                    file = new File("wynik.xls");
                } else {
                    file = new File(saveFileName);
                }
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
                    wr.write("Total cost\tTotal distance\tTotal WAIT time\tTotal drive time\tTotal punishment\tSim Time\tCommissions Count\tDelivered");
                    wr.newLine();
                    wr.flush();
                    wr.write(Double.toString(calendarStatsHolderForFile
                            .calculateCost(null)) + "\t");
                    wr.write(Double.toString(calendarStatsHolderForFile
                            .calculateDistanceSum()) + "\t");
                    wr.write(Double.toString(calendarStatsHolderForFile
                            .calculateWaitTime()) + "\t");
                    wr.write(Double.toString(calendarStatsHolderForFile
                            .calculateDriveTime()) + "\t");
                    wr.write(Double.toString(calendarStatsHolderForFile
                            .calculatePunishment()) + "\t");
                    wr.write(Long.toString(simTime) + "\t");
                    wr.write(simLogic.getCommissionsLogic().getCommissionsCount()
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
                    new XMLBuilder(simulationData, simLogic.getSimInfo().getDepot())
                            .save(file.getAbsolutePath() + ".xml");
            } catch (Exception e) {
                logger.error(e);
            }
            saveMeasures();
            saveMLTable();
            // don't write any code here
        }
    }

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
            wr.write(Double.toString(stat.getDistance()) + "\t");
            wr.write(Double.toString(stat.getWaitTime()) + "\t");
            wr.write(Double.toString(stat.getDriveTime()) + "\t");
            wr.write(Double.toString(stat.getPunishment()) + "\t");
            wr.write(Double.toString(stat.getMass()) + "\t");
            wr.write(Double.toString(stat.getPower()) + "\t");
            wr.write(Double.toString(stat.getReliability()) + "\t");
            wr.write(Double.toString(stat.getComfort()) + "\t");
            wr.write(Double.toString(stat.getFuelConsumption()) + "\t");
            wr.write(Integer.toString(stat.getMaxSTDepth()));
            wr.newLine();
            wr.flush();
            if (stat.getSchedule2() == null)
                for (CalendarAction action : stat.getSchedule()) {
                    if (action.getType().equals("DELIVERY")) {
                        delivery++;
                        wr_road.write(Integer.toString(action
                                .getSourceCommissionID()) + " ");
                    } else if (action.getType().equals("PICKUP")) {
                        wr_road.write(Integer.toString(action
                                .getSourceCommissionID()) + " ");
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
        wr.write(Double.toString(holder.calculateCost(null)) + "\t");
        wr.write(Double.toString(holder.calculateDistanceSum()) + "\t");
        wr.write(Double.toString(holder.calculateWaitTime()) + "\t");
        wr.write(Double.toString(holder.calculateDriveTime()) + "\t");
        wr.write(Double.toString(holder.calculatePunishment()) + "\t");
        wr.write(Long.toString(simTime) + "\t");
        wr.write(simLogic.getCommissionsLogic().getCommissionsCount() + "\t");
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
            logger.error(e.getMessage());
        }

        send(msg);
    }

    public void printMeasures(MeasureData data) {

        try {
            printersHolder.print(saveFileName, data, calculatorsHolder);
        } catch (Exception e) {
            logger.error(e);
        }
        if (mlAlgorithm == null)
            simEnd();
    }

    private void saveMLTable() {
        if (mlAlgorithm != null && exploration) {
            AID aid = CommunicationHelper.findAgentByServiceName(this,
                    "CommissionService")[0];
            ACLMessage msg = new ACLMessage(CommunicationHelper.MLTable);
            msg.addReceiver(aid);
            try {
                msg.setContentObject("");
            } catch (IOException e) {
                logger.error(e);
            }
            this.send(msg);
        }
    }

    public void saveMLAlgorithm(MLAlgorithm table) {
        try {
            table.save(mlTableFileName, saveFileName);
        } catch (Exception e) {
            logger.error(e);
        }
        simEnd();
    }

    public void sendCrisisEvent(CrisisEvent event) {

        AID[] aids;
        ACLMessage cfp;

        aids = CommunicationHelper.findAgentByServiceName(this,
                "CrisisManagementService");

        if (aids.length == 1) {

            for (AID aid : aids) {

                cfp = new ACLMessage(CommunicationHelper.CRISIS_EVENT);
                cfp.addReceiver(aid);
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
        }
    }

    private void createNewTransportElement(TransportElementInitialData data,
                                           TransportType type) {
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {

            ACLMessage cfp;
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

    public void nextAutoSimStep() {
        simLogic.nextAutoSimStep();
    }

    public void simEnd() {
        logger.info("Test end");
        resetGUIAgent();
        nextTest();
    }

    public void getSimulationData(int timestamp) {
        data = new LinkedList<>();
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
            logger.error(e);
        }
        send(msg);

        if (eUnitsCount == 0)
            simLogic.nextSimStep2();

    }

    public synchronized void addSimulationData(SimulationData data) {
        eUnitsCount--;
        this.data.add(data);
        ///
        SingletonGUI.getInstance().update(data);
        if (eUnitsCount == 0) {
            simulationData.put(timeStamp, this.data);
            simLogic.nextSimStep2();
        }
    }

    public void resetGUIAgent() {
        undeliveredCommissions = new LinkedList<>();
    }

    public synchronized void addUndeliveredCommission(NewTeamData data) {
        undeliveredCommissions.add(data);
    }

    public synchronized void changeGraph(Graph graph, int timestamp) {
        boolean updateAfterArrival;
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
        logger.info("graph changed");
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
            logger.error(e);
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
                    logger.error(e);
                }
                this.send(msg);
            }
        } else
            simLogic.nextSimStep4();
    }

    public synchronized void askForGraphChanges() {
        if (!graphChangeTime.equals("afterChangeNotice")) {
            simLogic.nextSimStep5();
            return;
        }
        changedGraphLinks = new LinkedList<>();
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
            logger.error(e);
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
                logger.error(e);
            }
            this.send(msg);
        }
    }

    public synchronized void linkChanged() {
        eUnitsCount--;
        if (eUnitsCount == 0) {
            simLogic.nextSimStep5();
        }
    }

    public CommissionsHandler getCommissionHandler() {
        return commissionsHandler;
    }

    private void next() {
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "AgentCreationService");
        transportAgentsCount = CommunicationHelper.findAgentByServiceName(this,
                "TransportUnitService").length;

        if (aids.length == 1) {

            ACLMessage cfp = new ACLMessage(CommunicationHelper.AGENTS_DATA);
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject("");
            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    private void next2() {
        logger.info("End of initialization");
        Adapter adapter = configuration.getAdapter();
        if (adapter == null) {
            simLogic.getCommissionsLogic().addCommissionGroup(
                    configuration.getCommissions(), configuration.isDynamic());
        } else {
            try {
                for (CommissionHandler com : adapter.readCommissions()) {
                    simLogic.getCommissionsLogic().addCommissionHandler(com);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        GraphChangesConfiguration graphConfChanges = configuration
                .getGraphChangesConf();
        if (graphConfChanges != null) {
            simLogic.setGraphConfChanges(graphConfChanges);
            graphChangeTime = configuration.getGraphChangeTime();
            graphChangeFreq = configuration.getGraphChangeFreq();
            this.setSTAfterChange(configuration.isSTAfterGraphChange());
        }
        TrackFinder finder = configuration.getTrackFinder();
        if (finder != null) {
            setTrackFinder(finder);
            setGraphLinkPredictor(configuration.getGraphLinkPredictor());
        }
        setBrute2Sorter(configuration.getBrute2Sorter());
        setExchangeAlgFactory(configuration.getExchangeAlgFactory());
        setDefaultAgentsData(configuration.getDefaultAgentsData());
        setSendingCommissionsInGroups(configuration.isPackageSending());
        setChoosingByCost(configuration.isChoosingByCost());
        setSimulatedTrading(configuration.getSimulatedTrading());
        recording = configuration.isRecording();
        setSTTimestampGap(configuration.getSTTimeGap());
        setSTCommissionGap(configuration.getSTCommissionGap());
        setPrintersHolder(configuration.getPrintersHolder());
        setCalculatorsHolder(configuration.getCalculatorsHolder());
        setConfChange(configuration.isConfChange());
        setPunishmentFunction(configuration.getPunishmentFunction());
        setPunishmentFunctionDefaults(configuration
                .getDefaultPunishmentFunValues());
        setHolons(configuration.getHolons());
        setDelayLimit(configuration.getDelayLimit());
        setFirstComplexSTResultOnly(configuration.isFirstComplexSTResultOnly());
        if (configuration.getMlTableFileName() != null) {
            MLAlgorithm table = configuration.getMlAlgorithm();
            setMLAlgorithm(table);
        }
        setExploration(configuration.isExploration());
        setMlTableFileName(configuration.getMlTableFileName());
        if (configuration.isAutoConfigure()) {
            Map<String, Object> conf = new ConfigurationChooser()
                    .getConfiguration(configuration.getCommissions());
            setSTDepth((Integer) conf.get("STDepth"));
            setAlgorithm((Algorithm) conf.get("algorithm"));
            boolean time = (Boolean) conf
                    .get("chooseWorstCommissionByGlobalTime");
            if (time) {
                setChooseWorstCommission("time");
            } else {
                setChooseWorstCommission("wTime");
            }
            setDist((Boolean) conf.get("dist"));
        } else {
            setSTDepth(configuration.getSTDepth());
            setAlgorithm(configuration.getAlgorithm());
            setChooseWorstCommission(configuration.getWorstCommissionChoose());
            setDist(configuration.isDist());
        }

        if (adapter == null) {
            simLogic.getCommissionsLogic().setConstraintsTestMode();
        } else {
            simLogic.setSimInfo(adapter.getSimInfo());
        }
    }

    public void simInfoReceived() {
        simInfoReceived--;
        if (simInfoReceived == 0) {
            next3();
        }
    }

    private void next3() {
        for (CrisisEvent event : configuration.getEvents()) {
            sendCrisisEvent(event);
        }

        simLogic.simStart();
        logger.info("Starting test: " + configuration.getResults());
        simLogic.autoSimulation(configuration.getResults());
    }
}
