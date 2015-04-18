package dtp.gui;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.graph.GraphGenerator;
import dtp.graph.GraphPoint;
import dtp.jade.ProblemType;
import dtp.jade.eunit.EUnitInfo;
import dtp.jade.gui.GUIAgent;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.simulation.SimInfo;
import dtp.visualisation.VisGUI;

import java.awt.*;
import java.util.Calendar;
import java.util.List;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SimLogic extends javax.swing.JFrame {

    protected GUIAgent guiAgent;
    protected Graph networkGraph;
    protected VisGUI visGui;
    protected int timestamp;
    protected SimInfo simInfo;
    protected int problemType;
    protected List<TransportElementInitialDataTruck> trucksProperties;
    protected List<TransportElementInitialDataTrailer> trailersProperties;

    // //////// GUI components //////////

    protected SimTab simTab;
    protected CommissionsTab commissionsTab;
    protected CrisisManagementTab crisisTab;
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

        simTab = new SimTab(this, guiAgent);
        commissionsTab = new CommissionsTab(this, guiAgent);
        crisisTab = new CrisisManagementTab(this, guiAgent);

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

    public int getProblemType() {
        return problemType;
    }

    public void setProblemType(int problemType) {
        this.problemType = problemType;
    }

    public void enableSimStartButton() {
        simTab.enableSimStartButton();
    }

    public void refreshComsWaiting() {
        simTab.setComsWaiting(guiAgent.getComsWaiting());
    }

    public void autoSimulation() {
        FileDialog fd = new FileDialog(this, "Plik do zapisu wynikow", FileDialog.SAVE);
        fd.setDirectory(".");
        fd.setFile("wynik.xls");
        fd.setVisible(true);
        autoSimulation(fd.getDirectory() + fd.getFile());
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

                    if (coms > 0 || (guiAgent.getCommissionHandler() != null && guiAgent.getCommissionHandler().isAnyEUnitAtNode(timestamp, true))) {
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
                    simTab.validate();

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
        simTab.setLabelDate();

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
        commissionsTab.markSentCommissions();
    }

    public void setTimerDelay(int timerDelay) {
        guiAgent.setTimerDelay(timerDelay);
    }

    public void displayMessage(String txt) {
        simTab.appendInfo(txt);
    }

    public Graph getNetworkGraph() {
        return this.networkGraph;
    }

    public void setNetworkGraph(Graph networkGraph) {
        this.networkGraph = networkGraph;
        guiAgent.sendGraphToEUnits(networkGraph);
    }

    public Graph createNetworkGraph() {
        List<GraphPoint> points;

        points = guiAgent.getLocations();

        if (points == null)
            return null;

        return new GraphGenerator().create(points);
    }

    public Graph generateNeighboursNetworkGraph(int howManyNeighbours,
                                                int howManyPoints) {
        List<GraphPoint> points;
        Graph graph;

        points = guiAgent.getLocations();

        if (points == null) {

            displayMessage("GUI - add some commissions to generate graph!");
            return null;
        }

        graph = new GraphGenerator().generateWithNeighbours(points,
                howManyNeighbours, howManyPoints);
        setNetworkGraph(graph);

        return graph;
    }

    public Graph generateRandomNetworkGraph(double linksRatio) {

        List<GraphPoint> points;
        Graph graph;

        points = guiAgent.getLocations();

        if (points == null) {

            displayMessage("GUI - add some commissions to generate graph!");
            return null;
        }

        graph = new GraphGenerator().generateRandom(points,
                (int) (linksRatio * points.size()));
        setNetworkGraph(graph);

        return graph;
    }

    public Point getDepotLocation() {
        return commissionsTab.getDepotLocation();
    }

    public void setVisGui(VisGUI visGui) {
        this.visGui = visGui;
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

    public List<TransportElementInitialDataTruck> getTrucksProperties() {
        return trucksProperties;
    }

    public void setTrucksProperties(List<TransportElementInitialDataTruck> trucksProperties) {
        this.trucksProperties = trucksProperties;
    }

    public List<TransportElementInitialDataTrailer> getTrailersProperties() {
        return trailersProperties;
    }

    public void setTrailersProperties(List<TransportElementInitialDataTrailer> trailersProperties) {
        this.trailersProperties = trailersProperties;
    }
}
