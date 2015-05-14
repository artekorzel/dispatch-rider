package dtp.jade.simulation;

import adapter.Adapter;
import algorithm.Algorithm;
import algorithm.BruteForceAlgorithm;
import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.CommissionsHandler;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.graph.GraphLink;
import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.gui.CommissionsHolder;
import dtp.jade.gui.TestConfiguration;
import dtp.jade.gui.behaviour.GetUndeliveredCommissionBehaviour;
import dtp.jade.simulation.behaviour.*;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportType;
import dtp.logic.SimLogic;
import dtp.simulation.SimInfo;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;
import pattern.ConfigurationChooser;
import xml.elements.SimulationData;

import java.util.*;

public class SimulationAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(SimulationAgent.class);
    // trzyma obiekty CommissionHandler (zlecenie wraz z czasem naplyniecia
    // do systemu), w odpowiednim czasie wysyla zlecenie do dystrybutora
    private CommissionsHandler commissionsHandler;

    private SimLogic simLogic;
    private int eUnitsCount;
    private int agentsCount;
    private int simInfoReceived;
    private Map<Integer, List<SimulationData>> simulationData = new TreeMap<>();
    private int stamps;
    private List<SimulationData> data;
    private Integer timeStamp;
    private List<NewTeamData> undeliveredCommissions = new LinkedList<>();
    private Graph graph;
    private int graphChangeTimestamp = -1;
    private LinkedList<GraphLink> changedGraphLinks;
    private int backToDepotTimestamp = -1;

    private String chooseWorstCommission;
    private Algorithm algorithm = new BruteForceAlgorithm();
    private boolean dist;
    private int STDepth = 1;

    private TestConfiguration configuration;
    private int transportAgentsCreated;
    private int level;
    private int transportAgentsCount;

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " - Hello World!");

		/* -------- SERVICES SECTION ------- */
        registerServices();

		/* -------- BEHAVIOURS SECTION ------- */
        this.addBehaviour(new ConfigurationReceivedBehaviour(this));
        this.addBehaviour(new GetDriversCreationDataBehaviour(this));
        this.addBehaviour(new GetTrailersCreationDataBehaviour(this));
        this.addBehaviour(new GetTrucksCreationDataBehaviour(this));
        this.addBehaviour(new GetTransportAgentCreatedBehaviour(this));
        this.addBehaviour(new GetTransportAgentConfirmationBehaviour(this));
        this.addBehaviour(new SimInfoReceivedBehaviour(this));
        this.addBehaviour(new GetSimInfoRequestBehaviour(this));
        this.addBehaviour(new GetMessageBehaviour(this));
        this.addBehaviour(new GetConfirmOfTimeStampBehaviour(this));
        this.addBehaviour(new GetUndeliveredCommissionBehaviour(this));
        this.addBehaviour(new GetGraphChangedBehaviour(this));
        this.addBehaviour(new GetAskForGraphChangesBehaviour(this));
        this.addBehaviour(new GetGraphLinkChangedBehaviour(this));
        this.addBehaviour(new SendStatsDataBehaviour(this));

        logger.info("SimulationAgent - end of initialization");
    }

    private void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /* -------- GUI SERVICE ------- */
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("SimulationService");
        sd1.setName("SimulationService");
        dfd.addServices(sd1);
        logger.info(this.getLocalName() + " - registering SimulationService");

        /* -------- REGISTRATION ------- */
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException "
                    + fe.getMessage());
        }
    }

    public void nextTest(TestConfiguration configuration) {
        this.configuration = configuration;
        GraphChangesConfiguration graphConfChanges = configuration
                .getGraphChangesConf();
        if (graphConfChanges != null) {
            simLogic.setGraphConfChanges(graphConfChanges);
        }

        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, "", MessageType.DRIVERS_DATA);
    }

    public void nextTestCreateDrivers(TransportElementInitialData[] initialData) {
        simLogic = new SimLogic(this);

        commissionsHandler = new CommissionsHandler();

        transportAgentsCreated = 0;
        agentsCount = initialData.length;
        level = 1;
        for (TransportElementInitialData data : initialData) {
            createNewTransportElement(data, TransportType.DRIVER);
        }
    }

    public void nextTestCreateTrailers(TransportElementInitialDataTrailer[] initialData) {
        transportAgentsCreated = 0;
        agentsCount = initialData.length;
        level = 2;
        for (TransportElementInitialDataTrailer data : initialData) {
            createNewTransportElement(data, TransportType.TRAILER);
        }
    }

    public void nextTestCreateTrucks(TransportElementInitialDataTruck[] initialData) {
        transportAgentsCreated = 0;
        agentsCount = initialData.length;
        level = 3;
        for (TransportElementInitialDataTruck data : initialData) {
            createNewTransportElement(data, TransportType.TRAILER);
        }
    }

    private void createNewTransportElement(TransportElementInitialData data,
                                           TransportType type) {
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {
            MessageType msgType;
            if (type == TransportType.DRIVER) {
                msgType = MessageType.DRIVER_CREATION;
            } else if (type == TransportType.TRAILER) {
                msgType = MessageType.TRAILER_CREATION;
            } else {
                msgType = MessageType.TRUCK_CREATION;
            }
            send(aids[0], data, msgType);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public void transportAgentCreated() {
        transportAgentsCreated++;
        AID[] aids;
        if (transportAgentsCreated == agentsCount) {
            switch (level) {
                case 1:
                    aids = AgentsService.findAgentByServiceName(this, "GUIService");
                    send(aids, "", MessageType.TRAILERS_DATA);
                    break;
                case 2:
                    aids = AgentsService.findAgentByServiceName(this, "GUIService");
                    send(aids, "", MessageType.TRUCKS_DATA);
                    break;
                case 3:
                    level = 0;
                    next();
                    break;
            }
        }
    }

    private void next() {
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "AgentCreationService");
        transportAgentsCount = AgentsService.findAgentByServiceName(this,
                "TransportUnitService").length;

        if (aids.length == 1) {
            send(aids[0], "", MessageType.AGENTS_DATA);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public void transportAgentConfirmationOfReceivingAgentsData() {
        transportAgentsCount--;
        if (transportAgentsCount == -1) {
            next2();
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

        if (configuration.isAutoConfigure()) {
            Map<String, Object> conf = new ConfigurationChooser()
                    .getConfiguration(configuration.getCommissions());
            setSTDepth((Integer) conf.get("STDepth"));
            setAlgorithm((Algorithm) conf.get("algorithm"));
            boolean time = (Boolean) conf.get("chooseWorstCommissionByGlobalTime");
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

    public void sendSimInfo(AID aid) {
        SimInfo info = simLogic.getSimInfo();
        info.setPunishmentFunction(configuration.getPunishmentFunction());
        info.setDefaultPunishmentFunValues(configuration.getDefaultPunishmentFunValues());
        info.setDelayLimit(configuration.getDelayLimit());
        info.setHolons(configuration.getHolons());
        info.setFirstComplexSTResultOnly(configuration.isFirstComplexSTResultOnly());
        info.setMlAlgorithm(configuration.getMlAlgorithm());
        info.setExploration(configuration.isExploration());
        info.setTrackFinder(configuration.getTrackFinder(), configuration.getGraphLinkPredictor());
        info.setSTAfterGraphChange(configuration.isSTAfterGraphChange());
        info.setExchangeAlgFactory(configuration.getExchangeAlgFactory());
        info.setBrute2Sorter(configuration.getBrute2Sorter());

        logger.info(getLocalName() + " - sending SimInfo to "
                + aid.getLocalName());
        send(aid, info, MessageType.SIM_INFO);
    }

    public void sendSimInfoToAll(SimInfo simInfo) {

        AID[] aids = AgentsService.findAgentByServiceName(this, "CommissionService");

        logger.info(getLocalName() + " - sending SimInfo to Distributor");

        simInfo.setCalculatorsHolder(configuration.getCalculatorsHolder());
        simInfo.setPunishmentFunction(configuration.getPunishmentFunction());
        simInfo.setDefaultPunishmentFunValues(configuration.getDefaultPunishmentFunValues());
        simInfo.setDelayLimit(configuration.getDelayLimit());
        simInfo.setHolons(configuration.getHolons());
        simInfo.setFirstComplexSTResultOnly(configuration.isFirstComplexSTResultOnly());
        simInfo.setMlAlgorithm(configuration.getMlAlgorithm());
        simInfo.setExploration(configuration.isExploration());
        simInfo.setTrackFinder(configuration.getTrackFinder(), configuration.getGraphLinkPredictor());
        simInfo.setSTAfterGraphChange(configuration.isSTAfterGraphChange());
        simInfo.setExchangeAlgFactory(configuration.getExchangeAlgFactory());
        simInfo.setBrute2Sorter(configuration.getBrute2Sorter());
        if (aids.length != 0) {
            simInfoReceived = aids.length;
            send(aids, simInfo, MessageType.SIM_INFO);
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

        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, simLogic.getSimInfo(), MessageType.GUI_SIMULATION_PARAMS);

        logger.info("Starting test: " + configuration.getResults());
        simLogic.autoSimulation();
    }

    public void sendCrisisEvent(CrisisEvent event) {
        AID[] aids = AgentsService.findAgentByServiceName(this, "CrisisManagementService");
        send(aids, event, MessageType.CRISIS_EVENT);
    }

    public void saveStatsToFile(long simTime) {
        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, Long.toString(simTime), MessageType.SIM_TIME);

        aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");

        if (aids.length > 0) {
            send(aids, "", MessageType.EUNIT_SHOW_STATS_TO_WRITE);
        } else {
            logger.info(getLocalName()
                    + " - There are no agents with ExecutionUnitService in the system");
        }
    }

    public synchronized void addUndeliveredCommission(NewTeamData data) {
        undeliveredCommissions.add(data);
    }

    public synchronized void changeGraph(Graph graph, int timestamp) {
        boolean updateAfterArrival;
        if (configuration.getGraphChangeTime().equals("immediately")) {
            updateAfterArrival = false;
        } else {
            if (graphChangeTimestamp == -1) {
                graphChangeTimestamp = timestamp;
            }
            if (configuration.getGraphChangeTime().equals("afterTime")
                    && timestamp >= graphChangeTimestamp + configuration.getGraphChangeFreq()) {
                updateAfterArrival = false;
                graphChangeTimestamp = -1;
            } else {
                updateAfterArrival = true;
            }
        }
        logger.info("graph changed");
        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, graph, MessageType.GRAPH_CHANGED);
        this.graph = graph;
        aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");
        this.eUnitsCount = aids.length;
        send(aids, new Object[]{graph, updateAfterArrival}, MessageType.GRAPH_CHANGED);
    }

    public synchronized void graphChanged(boolean isEUnit) {
        if (isEUnit) {
            eUnitsCount--;
            if (eUnitsCount == 0) {
                send(AgentsService.findAgentByServiceName(this, "CommissionService"), graph, MessageType.GRAPH_CHANGED);
            }
        } else
            simLogic.nextSimStep4();
    }

    public synchronized void addChangedLink(GraphLink link) {
        eUnitsCount--;
        if (link != null)
            changedGraphLinks.add(link);
        if (eUnitsCount == 0) {
            AID[] aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");
            this.eUnitsCount = aids.length;
            send(aids, changedGraphLinks, MessageType.GRAPH_LINK_CHANGED);
        }
    }

    public synchronized void linkChanged() {
        eUnitsCount--;
        if (eUnitsCount == 0) {
            simLogic.nextSimStep5();
        }
    }

    public int getNextTimestamp(int timestamp) {
        ++timestamp;

        AID[] aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");
        sendUpdateCurrentLocationRequest(aids, timestamp);

        while (timestamp <= simLogic.getSimInfo().getDeadline()
                && commissionsHandler.getCommissionsBeforeTime(timestamp).length == 0
                && !commissionsHandler.isAnyEUnitAtNode(false)) {
            if (timestamp == backToDepotTimestamp) {
                for (AID aid : aids) {
                    send(aid, "", MessageType.BACK_TO_DEPOT);
                }
            }

            timestamp++;
            sendUpdateCurrentLocationRequest(aids, timestamp);
        }

        return timestamp;
    }

    private void sendUpdateCurrentLocationRequest(AID[] aids, int timestamp) {
        sendString(aids, Integer.toString(timestamp), MessageType.UPDATE_CURRENT_LOCATION);

        //czekamy az kazdy z eunitow potwierdzi otrzymanie komunikatu
        for (AID ignored : aids) {
            blockingReceive(MessageTemplate.MatchConversationId(MessageType.UPDATE_CURRENT_LOCATION.name()));
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

        AID[] aids = AgentsService.findAgentByServiceName(this, "CommissionService");

        logger.info(getLocalName() + " - sending " + tempCommissions.length
                + " commission(s) to Distributor Agent");

        send(aids, new CommissionsHolder(tempCommissions, configuration, STDepth,
                dist, algorithm, chooseWorstCommission), MessageType.COMMISSION);

        if (commissionsHandler.getComsSize() == 0) {
            backToDepotTimestamp = simTime + 10;
        }
    }

    public void sendTimestamp(int time) {
        logger.info(getLocalName() + " - sending timestamp [" + time + "]");

        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, time, MessageType.TIME_CHANGED);

        /* -------- EUNITS SECTION ------- */
        aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");

        stamps = aids.length + 2;
        if (aids.length > 0) {
            send(aids, time, MessageType.TIME_CHANGED);
        } else {
            logger.info(getLocalName()
                    + " - there are no EUnit Agents in the system");
        }

        /* -------- CRISIS MANAGER SECTION ------- */
        aids = AgentsService.findAgentByServiceName(this,
                "CrisisManagementService");

        if (aids.length == 1) {
            send(aids, time, MessageType.TIME_CHANGED);
        } else {
            logger.info(getLocalName()
                    + " - none or more than one Crisis Manager Agent in the system");
        }

        /* -------- DISTRIBUTOR SECTION ------- */
        aids = AgentsService.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {
            send(aids, time, MessageType.TIME_CHANGED);
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

    public void nextAutoSimStep() {
        simLogic.nextAutoSimStep();
    }

    public void getSimulationData(int timestamp) {
        data = new LinkedList<>();
        this.timeStamp = timestamp;

        AID[] aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");
        eUnitsCount = aids.length;
        send(aids, "", MessageType.SIMULATION_DATA);

        if (eUnitsCount == 0)
            simLogic.nextSimStep2();

    }

    public synchronized void addSimulationData(SimulationData data) {
        eUnitsCount--;
        this.data.add(data);

        AID[] aids = AgentsService.findAgentByServiceName(this, "GUIService");
        send(aids, data, MessageType.SIMULATION_DATA);

        if (eUnitsCount == 0) {
            simulationData.put(timeStamp, this.data);
            simLogic.nextSimStep2();
        }
    }

    public synchronized void askForGraphChanges() {
        if (!configuration.getGraphChangeTime().equals("afterChangeNotice")) {
            simLogic.nextSimStep5();
            return;
        }
        changedGraphLinks = new LinkedList<>();
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "ExecutionUnitService");
        this.eUnitsCount = aids.length;
        send(aids, "", MessageType.ASK_IF_GRAPH_LINK_CHANGED);
    }

    public void sendStatsData(AID sender) {
        send(sender, new Object[]{
                Integer.toString(simLogic.getCommissionsLogic().getCommissionsCount()),
                undeliveredCommissions.toArray(new NewTeamData[undeliveredCommissions.size()]),
                simulationData
        }, MessageType.STATS_DATA);
    }

    public CommissionsHandler getCommissionHandler() {
        return commissionsHandler;
    }

    public TestConfiguration getConfiguration() {
        return configuration;
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        commissionsHandler.addCommissionHandler(commissionHandler);
    }

    public void removeCommissionHandler(CommissionHandler comHandler) {
        commissionsHandler.removeCommissionHandler(comHandler);
    }

    public void setChooseWorstCommission(String chooseWorstCommission) {
        this.chooseWorstCommission = chooseWorstCommission;
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
}
