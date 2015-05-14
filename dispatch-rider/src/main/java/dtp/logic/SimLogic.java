package dtp.logic;

import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.jade.ProblemType;
import dtp.jade.simulation.SimulationAgent;
import dtp.simulation.SimInfo;
import org.apache.log4j.Logger;

import java.util.Calendar;


public class SimLogic {

    private static Logger logger = Logger.getLogger(SimLogic.class);

    protected SimulationAgent simulationAgent;
    protected int timestamp;
    protected SimInfo simInfo;
    protected int problemType;

    protected CommissionsLogic commissionsLogic;
    protected volatile boolean isAutoSimulation = false;
    protected volatile boolean comsReady = true;
    private GraphChangesConfiguration graphConfChanges;
    private volatile boolean sthChanged;
    private Object[] params;

    public SimLogic(SimulationAgent agent) {
        super();
        simulationAgent = agent;
        timestamp = -1;
        problemType = ProblemType.WITHOUT_GRAPH;
        commissionsLogic = new CommissionsLogic(this, simulationAgent);
    }

    public void setGraphConfChanges(GraphChangesConfiguration graphConfChanges) {
        this.graphConfChanges = graphConfChanges;
    }

    public CommissionsLogic getCommissionsLogic() {
        return commissionsLogic;
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
        simulationAgent.sendSimInfoToAll(this.simInfo);
    }

    public void refreshComsWaiting() {
    }

    public void autoSimulation() {
        Thread thread = new Thread(() -> {
            isAutoSimulation = true;
            long startTime = Calendar.getInstance().getTimeInMillis();
            while (simInfo.getDeadline() > timestamp) {
                comsReady = false;
                sthChanged = false;

                int coms = commissionsLogic.newCommissions();

                if (coms > 0
                        || (simulationAgent.getCommissionHandler() != null
                        && simulationAgent.getCommissionHandler().isAnyEUnitAtNode(true))) {
                    comsReady = false;
                    sthChanged = true;
                }
                nextSimStep();
                logger.info("simauto (timestamp : " + timestamp +
                        ") simulationAgent queueSize = " + simulationAgent.getCurQueueSize());

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
            simulationAgent.saveStatsToFile(simTime);
        });
        thread.start();
    }

    public synchronized void nextAutoSimStep() {
        if (isAutoSimulation && !comsReady)
            comsReady = true;
    }

    public void nextSimStep() {
        if (timestamp >= 0 && simulationAgent.getConfiguration().isRecording() && sthChanged) {
            simulationAgent.getSimulationData(timestamp);
        } else {
            nextSimStep2();
        }
    }

    public void nextSimStep2() {
        timestamp = simulationAgent.getNextTimestamp(timestamp);

        if (graphConfChanges != null) {
            params = graphConfChanges.changeGraph(timestamp);
            if (params != null) {
                timestamp = (Integer) params[1];
            }
        }
        simulationAgent.sendTimestamp(timestamp);
    }

    public void nextSimStep3() {
        if (params != null) {
            Graph graph = (Graph) params[0];
            simulationAgent.changeGraph(graph, timestamp);
        } else {
            nextSimStep5();
        }
    }

    public void nextSimStep4() {
        simulationAgent.askForGraphChanges();
    }

    public void nextSimStep5() {
        simulationAgent.sendCommissions(timestamp);
        refreshComsWaiting();
    }
}
