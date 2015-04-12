package dtp.jade.test;

import adapter.Adapter;
import algorithm.Algorithm;
import dtp.commission.CommissionHandler;
import dtp.commission.CommissionsHandler;
import dtp.graph.GraphChangesConfiguration;
import dtp.gui.ExtensionFilter;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.gui.GUIAgent;
import dtp.jade.gui.behaviour.*;
import dtp.jade.test.behaviour.GetTransportAgentConfirmationBehaviour;
import dtp.jade.test.behaviour.GetTransportAgentCreatedBehaviour;
import dtp.optimization.TrackFinder;
import dtp.xml.ConfigurationParser;
import dtp.xml.ParseException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import machineLearning.MLAlgorithm;
import pattern.ConfigurationChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

public class TestAgent extends GUIAgent {


    boolean otherBenhmarks = false;
    private Iterator<TestConfiguration> configurationIterator = null;
    private TestConfiguration configuration;
    private int transportAgentsCreated;
    private int level;
    private int transportAgentsCount;

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " - Hello World!");

		/* -------- INITIALIZATION SECTION ------- */
        /*
		 * potrzebne tylko do tego, aby GUI platformy JADE zdazylo sie
		 * zainicjalizowac bez tego czasem rzucany jest NullPointerException
		 * mozna wyrzucic ten fragment i wylaczyc opcje GUI platformy JADE
		 * nalezy usunac -gui z wiersza polecen przy uruchamianiu wyjatek jest
		 * powodowany prawdopodobnie przez zmiane skorki:
		 * javax.swing.UIManager.setLookAndFeel
		 * ("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		 */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

		/* -------- SERVICES SECTION ------- */
        registerServices();

		/* -------- BEHAVIOURS SECTION ------- */
        this.addBehaviour(new GetSimInfoRequestBehaviour(this));
        this.addBehaviour(new GetMessageBehaviour(this));
        this.addBehaviour(new GetCalendarBehaviour(this));
        this.addBehaviour(new GetCalendarStatsBehaviour(this));
        this.addBehaviour(new GetEUnitInfoBehaviour(this));
        this.addBehaviour(new GetNooneListBehaviour(this));
        this.addBehaviour(new GetGraphUpdateBehaviour(this));
        this.addBehaviour(new GetCalenderStatsToFileBehaviour(this));
        this.addBehaviour(new GetTransportAgentCreatedBehaviour(this));
        this.addBehaviour(new dtp.jade.test.behaviour.SimInfoReceivedBehaviour(this));
        this.addBehaviour(new GetConfirmOfTimeStampBehaviour(this));
        this.addBehaviour(new GetTransportAgentConfirmationBehaviour(this));
        this.addBehaviour(new GetSimulationDataBehaviour(this));
        this.addBehaviour(new GetUndeliveredCommissionBehaviour(this));
        this.addBehaviour(new GetMeasureDataBehaviour(this));
        this.addBehaviour(new GetMLTableBehaviour(this));
        this.addBehaviour(new GetGraphChangedBehaviour(this));
        this.addBehaviour(new GetAskForGraphChangesBehaviour(this));
        this.addBehaviour(new GetGraphLinkChangedBehaviour(this));
        this.addBehaviour(new GetConfirmUpdateCurrentLocation(this));
        System.out.println("TestAgent - end of initialization");

        try {
            javax.swing.UIManager
                    .setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

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
				/* TODO Ask again for file ? */
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
		/* If there's no more test then exit */
        if (!configurationIterator.hasNext()) {
            logger.info("End of simmulation: "
                    + Calendar.getInstance().getTime());
            System.out.println(Calendar.getInstance().getTime());
            //System.exit(0);
        } else {

            configuration = configurationIterator.next();

		/* -------- INTERFACE CREATION SECTION ------- */
            gui = new TestHelper(this);
		/* -------- TIME TASK PERFORMER SECTION ------- */
            timerTaskPerformer = new ActionListener() {

                public void actionPerformed(ActionEvent evt) {

                    gui.nextSimStep();

                    // w simGOD timer startowany jest zeby zapisac statystyki, zaraz
                    // potem trzeba go zatrzynamc
                }
            };

            timerDelay = 200;
            timer = new Timer(timerDelay, timerTaskPerformer);

		/* -------- COMMISSIONS HANDLER SECTION ------- */
            commissionsHandler = new CommissionsHandler();

            // eUnitsCount=configuration.getEunits();
		/* -------- EUNITS CREATION SECTION ------- */
		/*
		 * for (int i = 0; i < configuration.getEunits(); i++) {
		 * EUnitInitialData data = new EUnitInitialData(
		 * configuration.getReorganization(),
		 * configuration.getReorganizationParam(),
		 * configuration.getOrganization(),
		 * configuration.getOrganizationParam()); data.setDepot(0);
		 * createNewEUnit(data); }
		 */
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

    @Override
    public void simEnd() {
        logger.info("Test end");
        sendEndOfSimInfo("ExecutionUnitService");
        sendEndOfSimInfo("TransportUnitService");
        sendEndOfSimInfo("InfoAgentService");
        sendEndOfSimInfo("CommissionService");
        sendEndOfSimInfo("CrisisManagementService");

        resetGUIAgent();
        nextTest();
    }

    @Override
    public synchronized void transportAgentCreated() {
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

    @Override
    public synchronized void transportAgentConfirmationOfReceivingAgentsData() {
        transportAgentsCount--;
        if (transportAgentsCount == -1) {
            next2();
        }
    }

    private void next2() {

        System.out.println("End of initialization");
        Adapter adapter = configuration.getAdapter();
        if (adapter == null) {
            gui.getCommissionsTab().addCommissionGroup(
                    configuration.getCommissions(), configuration.isDynamic());
            otherBenhmarks = false;
        } else {
            otherBenhmarks = true;
            try {
                for (CommissionHandler com : adapter.readCommissions()) {
                    gui.getCommissionsTab().addCommissionHandler(com);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GraphChangesConfiguration graphConfChanges = configuration
                .getGraphChangesConf();
        if (graphConfChanges != null) {
            gui.setGraphConfChanges(graphConfChanges);
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
        setSimmulatedTrading(configuration.getSimulatedTrading());
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

        if (adapter == null)
            gui.getCommissionsTab().setConstraintsTestMode();
        else
            gui.setSimInfo(adapter.getSimInfo());
    }

    public void simInfoReceived() {
        simInfoReceived--;
        if (simInfoReceived == 0) {
            if (!otherBenhmarks)
                gui.getCommissionsTab().setConstraints();
            next3();
        }
    }

    private void next3() {
		/* Configure crisis events */
        for (CrisisEvent event : configuration.getEvents())
            sendCrisisEvent(event);

        // try { Thread.sleep(10000); } catch (InterruptedException e) {}
        gui.simStart();
        // try { Thread.sleep(10000); } catch (InterruptedException e) {}
        logger.info("Starting test: " + configuration.getResults());
        gui.autoSimulation(configuration.getResults());
    }

    private void sendEndOfSimInfo(String serviceName) {
		/*AID[] aids = null;
		ACLMessage cfp = null;

		aids = CommunicationHelper.findAgentByServiceName(this, serviceName);

		logger.info(getLocalName() + " - sending SimEndInfo to " + aids.length
				+ " " + serviceName);

		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.SIM_END);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject("");
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
			}

		}*/
    }
}
