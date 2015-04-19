package dtp.logic;

import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.jade.ProblemType;
import dtp.jade.gui.GUIAgent;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.simulation.SimInfo;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.List;


public class SimLogic {

    private static Logger logger = Logger.getLogger(SimLogic.class);

    protected GUIAgent guiAgent;
    protected int timestamp;
    protected SimInfo simInfo;
    protected int problemType;
    protected List<TransportElementInitialDataTruck> trucksProperties;
    protected List<TransportElementInitialDataTrailer> trailersProperties;

    protected CommissionsLogic commissionsLogic;
    protected volatile boolean isAutoSimulation = false;
    protected volatile boolean comsReady = true;
    private GraphChangesConfiguration graphConfChanges;
    private volatile boolean sthChanged;
    private Object[] params;

    public SimLogic(GUIAgent agent) {
        super();
        guiAgent = agent;
        timestamp = -1;
        problemType = ProblemType.WITHOUT_GRAPH;
        commissionsLogic = new CommissionsLogic(this, guiAgent);
    }

    public void setGraphConfChanges(GraphChangesConfiguration graphConfChanges) {
        this.graphConfChanges = graphConfChanges;
    }

    public CommissionsLogic getCommissionsLogic() {
        return commissionsLogic;
    }

    public void simStart() {
        guiAgent.simulationStart();
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }

    public void setSimInfo(SimInfo simInfo) {
        this.simInfo = simInfo;
        guiAgent.sendSimInfoToAll(this.simInfo);
    }

    public void refreshComsWaiting() {
    }

    public void autoSimulation(String file) {
        final String fileName;
        if (!file.endsWith(".xls")) {
            fileName = file + ".xls";
        } else {
            fileName = file;
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                isAutoSimulation = true;
                long startTime = Calendar.getInstance().getTimeInMillis();
                while (simInfo.getDeadline() > timestamp) {

                    comsReady = false;
                    sthChanged = false;

                    int coms = commissionsLogic.newCommissions();

                    if (coms > 0
                            || (guiAgent.getCommissionHandler() != null
                            && guiAgent.getCommissionHandler().isAnyEUnitAtNode(true))) {
                        comsReady = false;
                        sthChanged = true;
                    }
                    nextSimStep();
                    logger.info("simauto (timestamp : " + timestamp + ") guiAgent queueSize = " + guiAgent.getCurQueueSize());

                    // int waitCount=0;
                    while (!comsReady) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                long endTime = Calendar.getInstance().getTimeInMillis();
                long simTime = endTime - startTime;
                guiAgent.saveStatsToFile(fileName, simTime);
            }
        });
        thread.start();
    }

    public synchronized void nextAutoSimStep() {
        if (isAutoSimulation && !comsReady)
            comsReady = true;
    }

    public void nextSimStep() {
        if (timestamp >= 0 && guiAgent.isRecording() && sthChanged) {
            guiAgent.getSimulationData(timestamp);
        } else {
            nextSimStep2();
        }
    }

    public void nextSimStep2() {
        timestamp = guiAgent.getNextTimestamp(timestamp);

        if (graphConfChanges != null) {
            params = graphConfChanges.changeGraph(timestamp);
            if (params != null) {
                timestamp = (Integer) params[1];
            }
        }
        guiAgent.sendTimestamp(timestamp);
    }

    public void nextSimStep3() {
        if (params != null) {
            Graph graph = (Graph) params[0];
            guiAgent.changeGraph(graph, timestamp);
        } else {
            nextSimStep5();
        }
    }

    public void nextSimStep4() {
        guiAgent.askForGraphChanges();
    }

    public void nextSimStep5() {
        guiAgent.sendCommissions(timestamp);
        refreshComsWaiting();
    }

    public void setTrucksProperties(List<TransportElementInitialDataTruck> trucksProperties) {
        this.trucksProperties = trucksProperties;
    }

    public void setTrailersProperties(List<TransportElementInitialDataTrailer> trailersProperties) {
        this.trailersProperties = trailersProperties;
    }
}
