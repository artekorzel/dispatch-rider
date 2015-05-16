package dtp.jade.gui;

import adapter.Adapter;
import adapter.AdapterType;
import algorithm.Algorithm;
import algorithm.AlgorithmType;
import algorithm.Brute2Sorter;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.graph.predictor.GraphLinkPredictorType;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.optimization.TrackFinder;
import dtp.optimization.TrackFinderType;
import dtp.xml.ParseException;
import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import measure.printer.PrintersHolder;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TestConfiguration implements Serializable {

    private static Logger logger = Logger.getLogger(TestConfiguration.class);
    private String adapterName;
    private Adapter adapter;
    private String commissions;
    private String configurationDirectory;
    private String results;
    private List<CrisisEvent> events;
    private boolean packageSending;
    private boolean choosingByCost;
    private int simulatedTrading;
    private int STDepth;
    private DefaultAgentsData defaultAgentsData;
    private Algorithm algorithm;
    private boolean dist;
    private boolean autoConfigure;
    private boolean recording;
    private int STTimeGap;
    private int STCommissionGap;
    private MeasureCalculatorsHolder calculatorsHolder;
    private PrintersHolder printersHolder;
    private boolean confChange;
    private String punishmentFunction;
    private Map<String, Double> defaultPunishmentFunValues;
    private Double delayLimit;
    private int holons;
    private String worstCommissionChoose;
    private boolean firstComplexSTResultOnly;
    private TrackFinder trackFinder;
    private GraphLinkPredictor graphLinkPredictor;
    private boolean exploration;
    private String mlTableFileName;
    private GraphChangesConfiguration graphChangesConf;
    private MLAlgorithm mlAlgorithm;
    private boolean STAfterGraphChange;
    private ExchangeAlgorithmsFactory exchangeAlgFactory;
    private String graphChangeTime;
    private int graphChangeFreq;
    private Brute2Sorter bruteForceAlgorithm2Sorter;

    public Brute2Sorter getBrute2Sorter() {
        return bruteForceAlgorithm2Sorter;
    }

    public void setBrute2Sorter(String option) {
        switch (option) {
            case "time":
                bruteForceAlgorithm2Sorter = Brute2Sorter.TIME;
                break;
            case "dist":
                bruteForceAlgorithm2Sorter = Brute2Sorter.DISTANCE;
                break;
            case "worstDistanceFirst":
                bruteForceAlgorithm2Sorter = Brute2Sorter.WORST_DISTANCE_FIRST;
                break;
            case "none":
                bruteForceAlgorithm2Sorter = Brute2Sorter.NONE;
                break;
            default:
                logger.error("Invalid brute2Sorter option, setting it to NONE (option)");
                bruteForceAlgorithm2Sorter = Brute2Sorter.NONE;
                break;
        }
    }

    public MLAlgorithm getMlAlgorithm() {
        return mlAlgorithm;
    }

    public void setMlAlgorithm(MLAlgorithm mlAlgorithm) {
        this.mlAlgorithm = mlAlgorithm;
    }

    public int getGraphChangeFreq() {
        return graphChangeFreq;
    }

    public void setGraphChangeFreq(int graphChangeFreq) {
        this.graphChangeFreq = graphChangeFreq;
    }

    public String getGraphChangeTime() {
        return graphChangeTime;
    }

    public void setGraphChangeTime(String graphChangeTime) {
        this.graphChangeTime = graphChangeTime;
    }

    public ExchangeAlgorithmsFactory getExchangeAlgFactory() {
        return exchangeAlgFactory;
    }

    public void setExchangeAlgFactory(
            ExchangeAlgorithmsFactory exchangeAlgFactory) {
        this.exchangeAlgFactory = exchangeAlgFactory;
    }

    public boolean isSTAfterGraphChange() {
        return STAfterGraphChange;
    }

    public void setSTAfterGraphChange(boolean sTAfterGraphChange) {
        STAfterGraphChange = sTAfterGraphChange;
    }

    public GraphChangesConfiguration getGraphChangesConf() {
        return graphChangesConf;
    }

    public void setGraphChangesConf(GraphChangesConfiguration graphChangesConf) {
        this.graphChangesConf = graphChangesConf;
    }

    public TrackFinder getTrackFinder() {
        return trackFinder;
    }

    public GraphLinkPredictor getGraphLinkPredictor() {
        return graphLinkPredictor;
    }

    public void setGraph(Graph graph, String trackFinder, String predictor,
                         int historySize) throws ParseException {
        try {
            this.trackFinder = TrackFinderType.valueOf(trackFinder).typeClass()
                    .getConstructor(Graph.class).newInstance(graph);

            this.graphLinkPredictor = GraphLinkPredictorType.valueOf(predictor).typeClass().newInstance();
            this.graphLinkPredictor.setHistoryMaxSize(historySize);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public String getMlTableFileName() {
        return mlTableFileName;
    }

    public void setMlTableFileName(String mlTableFileName) {
        if (confChange) {
            throw new IllegalStateException("confChange cannot be true if you want use machine learning");
        }
        this.mlTableFileName = mlTableFileName;
    }

    public boolean isExploration() {
        return exploration;
    }

    public void setExploration(boolean exploration) {
        this.exploration = exploration;
    }

    public boolean isFirstComplexSTResultOnly() {
        return firstComplexSTResultOnly;
    }

    public void setFirstComplexSTResultOnly(boolean firstComplexSTResultOnly) {
        this.firstComplexSTResultOnly = firstComplexSTResultOnly;
    }

    public String getWorstCommissionChoose() {
        return worstCommissionChoose;
    }

    public void setWorstCommissionChoose(String worstCommissionChoose) {
        this.worstCommissionChoose = worstCommissionChoose;
    }

    public int getHolons() {
        return holons;
    }

    public void setHolons(int holons) {
        this.holons = holons;
    }

    public Double getDelayLimit() {
        return delayLimit;
    }

    public void setDelayLimit(Double delayLimit) {
        this.delayLimit = delayLimit;
    }

    public Map<String, Double> getDefaultPunishmentFunValues() {
        return defaultPunishmentFunValues;
    }

    public void setDefaultPunishmentFunValues(Map<String, Double> defaultPunishmentFunValues) {
        this.defaultPunishmentFunValues = defaultPunishmentFunValues;
    }

    public String getPunishmentFunction() {
        return punishmentFunction;
    }

    public void setPunishmentFunction(String punishmentFunction) {
        this.punishmentFunction = punishmentFunction;
    }

    public int getSTTimeGap() {
        return STTimeGap;
    }

    public void setSTTimeGap(int sTTimeGap) {
        STTimeGap = sTTimeGap;
    }

    public boolean isConfChange() {
        return confChange;
    }

    public void setConfChange(boolean confChange) {
        this.confChange = confChange;
    }

    public int getSTCommissionGap() {
        return STCommissionGap;
    }

    public void setSTCommissionGap(int sTComissionGap) {
        STCommissionGap = sTComissionGap;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public boolean isAutoConfigure() {
        return autoConfigure;
    }

    public void setAutoConfigure(boolean autoConfigure) {
        this.autoConfigure = autoConfigure;
    }

    public boolean isDist() {
        return dist;
    }

    public void setDist(boolean isDist) {
        dist = isDist;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithmName) {
        try {
            this.algorithm = AlgorithmType.valueOf(algorithmName).typeClass().newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public int getSTDepth() {
        return STDepth;
    }

    public void setSTDepth(int sTDepth) {
        STDepth = sTDepth;
    }

    public int getSimulatedTrading() {
        return simulatedTrading;
    }

    public void setSimulatedTrading(int simulatedTrading) {
        this.simulatedTrading = simulatedTrading;
    }

    public DefaultAgentsData getDefaultAgentsData() {
        return defaultAgentsData;
    }

    public void setDefaultAgentsData(DefaultAgentsData defaultAgentsData) {
        this.defaultAgentsData = defaultAgentsData;
    }

    public boolean isChoosingByCost() {
        return choosingByCost;
    }

    public void setChoosingByCost(boolean choosingByCost) {
        this.choosingByCost = choosingByCost;
    }

    public boolean isPackageSending() {
        return packageSending;
    }

    public void setPackageSending(boolean packageSending) {
        this.packageSending = packageSending;
    }

    public boolean isDynamic() {
        return !"false".equals(adapterName);
    }

    public Adapter getAdapter() {
        if ("false".equals(adapterName) || "true".equals(adapterName))
            this.adapter = null;
        else {
            try {
                this.adapter = AdapterType.valueOf(adapterName).typeClass()
                        .getConstructor(String.class).newInstance(getCommissions());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return adapter;
    }

    public void setAdapter(String adapter) {
        adapterName = adapter;
    }

    public String getCommissions() {
        return commissions;
    }

    public void setCommissions(String commissions) {
        this.commissions = commissions;
    }

    public String getConfigurationDirectory() {
        return configurationDirectory;
    }

    public void setConfigurationDirectory(String configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public List<CrisisEvent> getEvents() {
        return events;
    }

    public void setEvents(List<CrisisEvent> events) {
        this.events = events;
    }

    public MeasureCalculatorsHolder getCalculatorsHolder() {
        return calculatorsHolder;
    }

    public void setCalculatorsHolder(MeasureCalculatorsHolder calculatorsHolder) {
        this.calculatorsHolder = calculatorsHolder;
    }

    public PrintersHolder getPrintersHolder() {
        return printersHolder;
    }

    public void setPrintersHolder(PrintersHolder printersHolder) {
        this.printersHolder = printersHolder;
    }
}
