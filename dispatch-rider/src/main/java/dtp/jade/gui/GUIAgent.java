package dtp.jade.gui;

import algorithm.Schedule;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.agentcalendar.CalendarAction;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.gui.behaviour.*;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.simulation.SimInfo;
import dtp.util.ExtensionFilter;
import dtp.xml.ConfigurationParser;
import dtp.xml.ParseException;
import gui.main.SingletonGUI;
import gui.parameters.DRParams;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import machineLearning.MLAlgorithm;
import machineLearning.clustering.Clustering;
import measure.Measure;
import measure.printer.MeasureData;
import measure.visualization.MeasuresVisualizationRunner;
import org.apache.log4j.Logger;
import xml.elements.SimulationData;
import xml.elements.XMLBuilder;

import javax.swing.*;
import java.io.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class GUIAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(GUIAgent.class);
    private String saveFileName;
    private String mlTableFileName;
    private long simTime;
    private int defaultStats;
    private CalendarStatsHolder calendarStatsHolderForFile;
    private MeasuresVisualizationRunner measuresVisualizationRunner;
    private Iterator<TestConfiguration> configurationIterator = null;
    private TestConfiguration configuration;
    private SimInfo simInfo;

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

        registerServices();

        this.addBehaviour(new GetDriversCreationDataRequestBehaviour(this));
        this.addBehaviour(new GetTrailersCreationDataRequestBehaviour(this));
        this.addBehaviour(new GetTrucksCreationDataRequestBehaviour(this));
        this.addBehaviour(new GetVisualisationMeasureNamesBehaviour(this));
        this.addBehaviour(new VisualisationMeasureSetHolonsBehaviour(this));
        this.addBehaviour(new VisualisationMeasureUpdateBehaviour(this));
        this.addBehaviour(new SetGUISimulationParams(this));
        this.addBehaviour(new SetSimInfoParams(this));
        this.addBehaviour(new GetSimulationDataBehaviour(this));
        this.addBehaviour(new PrepareCalendarStatsToSaveInFileBehaviour(this));
        this.addBehaviour(new SaveCalendarStatsBehaviour(this));
        this.addBehaviour(new GetMeasureDataBehaviour(this));
        this.addBehaviour(new GetMLTableBehaviour(this));
        this.addBehaviour(new GetSimTimeBehaviour(this));
        this.addBehaviour(new GraphChangedBehaviour());
        this.addBehaviour(new GetTimestampBehaviour(this));

        logger.info("GUIAgent - end of initialization");

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
            configurationIterator = ConfigurationParser.parse(configurationFile).iterator();
        } catch (ParseException cause) {
            throw new RuntimeException("Error while parsing configuration file", cause);
        }

        nextTest();
    }

    public void nextTest() {
        if (!configurationIterator.hasNext()) {
            logger.info("End of simulation: " + Calendar.getInstance().getTime());
            return;
        } else {
            configuration = configurationIterator.next();
            saveFileName = configuration.getResults();
            mlTableFileName = configuration.getMlTableFileName();

            if (!saveFileName.endsWith(".xls")) {
                saveFileName = saveFileName + ".xls";
            }
        }

        AID[] aids = AgentsService.findAgentByServiceName(this, "SimulationService");
        send(aids, configuration, MessageType.CONFIGURATION);
        logger.info("Waiting for agents creation...");
    }

    private void registerServices() {

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
            logger.error(this.getLocalName() + " - FIPAException ", fe);
        }
    }

    public void loadDriversProperties() throws IOException {
        String filePath = configuration
                .getConfigurationDirectory()
                + File.separator
                + "drivers.properties";

        String line;
        try(BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            line = br.readLine();
        }
        String[] lineParts = line.split("\t");
        int driversCount = Integer.parseInt(lineParts[0]);
        String costFunction = "0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
        if (lineParts.length > 1)
            costFunction = lineParts[1];

        TransportElementInitialData[] initialData = new TransportElementInitialData[driversCount];
        for (int i = 0; i < driversCount; i++) {
            initialData[i] = new TransportElementInitialData(costFunction, 100000, 100000, 0);
        }
        logger.info("Drivers created: " + driversCount);
        AID[] aids = AgentsService.findAgentByServiceName(this, "SimulationService");
        send(aids, initialData, MessageType.DRIVERS_DATA);
    }

    public void loadTrailersProperties() throws IOException {
        String filePath = configuration
                .getConfigurationDirectory()
                + File.separator
                + "trailers.properties";

        try(BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {

            String firstLine = br.readLine();
            String[] parts = firstLine.split("\t");
            int trailersCount = Integer.parseInt(parts[0]);
            String defaultCostFunction = null;
            if (parts.length > 1)
                defaultCostFunction = parts[1];

            TransportElementInitialDataTrailer[] trailersProperties = new TransportElementInitialDataTrailer[trailersCount];

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

                trailersProperties[i] = new TransportElementInitialDataTrailer(
                        costFunction, capacity, 0, 0, mass, capacity, cargoType,
                        universality, connectorType);
            }
            logger.info("Trailers created: " + trailersCount);
            AID[] aids = AgentsService.findAgentByServiceName(this, "SimulationService");
            send(aids, trailersProperties, MessageType.TRAILERS_DATA);
        }
    }

    public void loadTrucksProperties() throws IOException {
        String filePath = configuration
                .getConfigurationDirectory()
                + File.separator
                + "trucks.properties";

        try(BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {

            String firstLine = br.readLine();
            String[] parts = firstLine.split("\t");
            int trucksCount = Integer.parseInt(parts[0]);
            String defaultCostFunction = null;
            if (parts.length > 1)
                defaultCostFunction = parts[1];

            TransportElementInitialDataTruck[] trucksProperties = new TransportElementInitialDataTruck[trucksCount];

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

                trucksProperties[i] = new TransportElementInitialDataTruck(
                        costFunction, power, 0, 0, power, reliability, comfort,
                        fuelConsumption, connectorType);
            }

            logger.info("Trucks created: " + trucksCount);
            AID[] aids = AgentsService.findAgentByServiceName(this, "SimulationService");
            send(aids, trucksProperties, MessageType.TRUCKS_DATA);
        }
    }

    public void setVisualisationMeasures(String[] measureNames) {
        if (measureNames != null && measureNames.length > 0) {
            measuresVisualizationRunner = new MeasuresVisualizationRunner(measureNames);
        }
    }

    public void setVisualisationMeasuresHolons(AID[] holons) {
        if (measuresVisualizationRunner != null) {
            measuresVisualizationRunner.setCurrentHolons(holons);
        }
    }

    public void visualisationMeasuresUpdate(Measure measure) {
        if (measuresVisualizationRunner != null) {
            measuresVisualizationRunner.update(measure);
        }
    }

    public void setGUISimulationParams(DRParams params) {
        SingletonGUI.getInstance().update(params);
    }

    public void setSimulationInfoParams(SimInfo params) {
        this.simInfo = params;
        SingletonGUI.getInstance().update(params);
    }

    public synchronized void addSimulationData(SimulationData data) {
        SingletonGUI.getInstance().update(data);
    }

    public void addCalendarStatsToFile(CalendarStats calendarStats) {
        if (calendarStatsHolderForFile == null) {
            defaultStats = 0;
            AID[] aids = AgentsService.findAgentByServiceName(this, "ExecutionUnitService");
            calendarStatsHolderForFile = new CalendarStatsHolder(aids.length);
        }

        if (calendarStats.isDefault()) {
            defaultStats++;
        }
        calendarStatsHolderForFile.addCalendarStats(calendarStats);

        if (calendarStatsHolderForFile.gotAllCalendarStats()) {

            AID[] aids = AgentsService.findAgentByServiceName(this, "SimulationService");
            send(aids, "", MessageType.STATS_DATA);
        }
    }

    public void performStatsSave(String commissionsCount, NewTeamData[] undeliveredCommissions,
                                 TreeMap<Integer, List<SimulationData>> simulationData) {
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
            Integer delivery = printStats(calendarStatsHolder, wr, wr_road, commissionsCount);
            wr.newLine();
            wr.newLine();
            wr.newLine();
            if (defaultStats > 0) {
                wr.append("Default Agents Status");
                wr.newLine();
                wr.flush();
                delivery += printStats(defaultHolons, wr, wr_road, commissionsCount);
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
                wr.write(commissionsCount + "\t");
                wr.write(delivery.toString());
                wr.flush();
            }
            wr.flush();
            if (undeliveredCommissions.length > 0) {
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

            if (configuration.isRecording())
                new XMLBuilder(simulationData, simInfo.getDepot())
                        .save(file.getAbsolutePath() + ".xml");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        saveMeasures();
        saveMLTable();
        // don't write any code here
    }

    private void saveMeasures() {
        if (configuration.getPrintersHolder() == null) {
            if (configuration.getMlAlgorithm() == null)
                simEnd();
            return;
        }
        AID[] aids = AgentsService.findAgentByServiceName(this, "CommissionService");
        send(aids, "", MessageType.MEASURE_DATA);
    }

    private void saveMLTable() {
        if (configuration.getMlAlgorithm() != null && configuration.isExploration()) {
            AID[] aids = AgentsService.findAgentByServiceName(this, "CommissionService");
            send(aids, "", MessageType.MLTable);
        }
    }

    public void saveMLAlgorithm(MLAlgorithm table) {
        if (table instanceof Clustering) {
            logger.info("saveMLAlgorithm in case of Clustering");
            Clustering clust = (Clustering) table;

            if (clust.isLearning()) {
                clust.clustering();
            }
        }

        try {
            table.save(mlTableFileName, saveFileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        simEnd();
    }

    public void printMeasures(MeasureData data) {

        try {
            configuration.getPrintersHolder().print(saveFileName, data, configuration.getCalculatorsHolder());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (configuration.getMlAlgorithm() == null)
            simEnd();
    }

    public void simEnd() {
        logger.info("Test end");
        nextTest();
    }

    private int printStats(CalendarStatsHolder holder, BufferedWriter wr,
                           BufferedWriter wr_road, String commissionsCount) throws IOException {
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
        wr.write(commissionsCount + "\t");
        wr.write(delivery.toString());
        wr.flush();
        return delivery;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }
}
