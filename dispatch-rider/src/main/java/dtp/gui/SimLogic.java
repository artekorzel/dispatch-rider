package dtp.gui;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.jade.ProblemType;
import dtp.jade.eunit.EUnitInfo;
import dtp.jade.gui.GUIAgent;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.simulation.SimInfo;
import dtp.visualisation.VisGUI;

import java.util.Calendar;
import java.util.List;


public class SimLogic {

    protected GUIAgent guiAgent;
    protected Graph networkGraph;
    protected VisGUI visGui;
    protected int timestamp;
    protected SimInfo simInfo;
    protected int problemType;
    protected List<TransportElementInitialDataTruck> trucksProperties;
    protected List<TransportElementInitialDataTrailer> trailersProperties;

    protected CommissionsTab commissionsTab;
    protected boolean isAutoSimulation = false;
    protected boolean comsReady = true;
    private GraphChangesConfiguration graphConfChanges;
    private boolean sthChanged;
    private Object[] params;

    {
        try {
            javax.swing.UIManager.setLookAndFeel(Plastic3DLookAndFeel.class.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SimLogic(GUIAgent agent) {
        super();
        guiAgent = agent;
        timestamp = -1;
        problemType = ProblemType.WITHOUT_GRAPH;
        commissionsTab = new CommissionsTab(this, guiAgent);
    }

    public void setGraphConfChanges(GraphChangesConfiguration graphConfChanges) {
        this.graphConfChanges = graphConfChanges;
    }

    public CommissionsTab getCommissionsTab() {
        return commissionsTab;
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

                    int coms = commissionsTab.newCommissions();

                    if (coms > 0 || (guiAgent.getCommissionHandler() != null && guiAgent.getCommissionHandler().isAnyEUnitAtNode(true))) {
                        comsReady = false;
                        sthChanged = true;
                    }
                    nextSimStep();
                    System.out.println("simauto (timestamp : " + timestamp + ") guiAgent queueSize = " + guiAgent.getCurQueueSize());

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
        timestamp = guiAgent.getNextTimestamp(timestamp);// timestamp++;

        params = null;
        if (graphConfChanges == null) {
            guiAgent.sendTimestamp(timestamp);
        } else {
            params = graphConfChanges.changeGraph(timestamp);
            if (params == null) {
                guiAgent.sendTimestamp(timestamp);
                return;
            }
            timestamp = (Integer) params[1];
            guiAgent.sendTimestamp(timestamp);
        }

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

    public void updateEUnitsInfo(EUnitInfo eUnitInfo) {
        if (this.visGui == null) {
            return;
        }
        this.visGui.updateEUnitsInfo(eUnitInfo);
    }

    public void updateGraph(Graph graph) {
        networkGraph = graph;
        if (visGui != null) {
            visGui.updateGraph(graph);
        }
    }

    public void setTrucksProperties(List<TransportElementInitialDataTruck> trucksProperties) {
        this.trucksProperties = trucksProperties;
    }

    public void setTrailersProperties(List<TransportElementInitialDataTrailer> trailersProperties) {
        this.trailersProperties = trailersProperties;
    }
}
