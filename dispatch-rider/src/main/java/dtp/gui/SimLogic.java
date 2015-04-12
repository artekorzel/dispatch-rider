package dtp.gui;

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

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

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

    protected ArrayList<TransportElementInitialDataTruck> trucksProperties;

    protected ArrayList<TransportElementInitialDataTrailer> trailersProperties;

    // //////// GUI components //////////

    protected JTabbedPane mainPane;

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
            javax.swing.UIManager
                    .setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
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

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getSimTab()
     */
    public SimTab getSimTab() {

        return simTab;
    }

    // :(
    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getCommissionsTab()
     */
    public CommissionsTab getCommissionsTab() {

        return commissionsTab;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#simStart()
     */
    public void simStart() {

        guiAgent.simulationStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getTimestamp()
     */
    public int getTimestamp() {

        return timestamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setTimestamp(int)
     */
    public void setTimestamp(int timestamp) {

        this.timestamp = timestamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getSimInfo()
     */
    public SimInfo getSimInfo() {

        return simInfo;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setSimInfo(dtp.simulation.SimInfo)
     */
    public void setSimInfo(SimInfo simInfo) {

        this.simInfo = simInfo;

        guiAgent.sendSimInfoToAll(this.simInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getProblemType()
     */
    public int getProblemType() {

        return problemType;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setProblemType(int)
     */
    public void setProblemType(int problemType) {

        this.problemType = problemType;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#enableSimStartButton()
     */
    public void enableSimStartButton() {

        simTab.enableSimStartButton();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#pauseSim(boolean)
     */
    public void pauseSim(boolean pause) {
        /*
         * simTab.pauseSim(pause);
         *
         * if (pause) {
         *
         * guiAgent.timerStop();
         *
         * } else {
         *
         * guiAgent.timerStart(); }
         */
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#refreshComsWaiting()
     */
    public void refreshComsWaiting() {

        simTab.setComsWaiting(guiAgent.getComsWaiting());
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#sendTimestamp(int)
     */
    public void sendTimestamp(int simTime) {
        guiAgent.sendTimestamp(simTime);
    }

    public boolean isSthChanged() {
        return sthChanged;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#autoSimulation()
     */
    public void autoSimulation() {
        FileDialog fd = new FileDialog(this, "Plik do zapisu wynikow",
                FileDialog.SAVE);
        fd.setDirectory(".");
        fd.setFile("wynik.xls");
        fd.setVisible(true);
        autoSimulation(fd.getDirectory() + fd.getFile());
    }

    public void autoSimulation(String file) {
        final String fileName;
        if (file.endsWith(".xls") == false)
            fileName = file + ".xls";
        else
            fileName = file;
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
                            // if(waitCount>coms) break;
                            // waitCount++;
                        } catch (InterruptedException ex) {
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

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#nextAutoSimStep()
     */
    public synchronized void nextAutoSimStep() {
        // System.out.println("autosim1 "+timestamp);
        // if(isAutoSimulation) {
        // if(simInfo.getDeadline() > timestamp) {
        // System.out.println("autosim "+timestamp);
        // try {
        // Thread.sleep(300);
        // } catch(InterruptedException ex) {}
        // nextSimStep();
        // }
        // else
        // isAutoSimulation = false;
        // }

        if (isAutoSimulation && !comsReady)
            comsReady = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#nextSimStep()
     */
    public void nextSimStep() {

        /*
         * if (guiAgent.getComsWaiting() == 0) {
         *
         * pauseSim(true);
         *
         * if (simGOD != null) {
         *
         * simGOD.completed();
         *
         * return; } }
         */
        if (timestamp >= 0 && guiAgent.isRecording() && sthChanged) {
            guiAgent.getSimmulationData(timestamp);
        } else {
            nextSimStep2();
        }

    }

    public void nextSimStep2() {
        timestamp = guiAgent.getNextTimestamp(timestamp);// timestamp++;

        // if (timestamp >= 5)
        // System.exit(0);
        simTab.setLabelDate(timestamp);

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

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getTimerDelay()
     */
    public int getTimerDelay() {

        return guiAgent.getTimerDelay();
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setTimerDelay(int)
     */
    public void setTimerDelay(int timerDelay) {

        guiAgent.setTimerDelay(timerDelay);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#displayMessage(java.lang.String)
     */
    public void displayMessage(String txt) {

        simTab.appendInfo(txt);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#displayCalendar(java.lang.String)
     */
    public void displayCalendar(String calendar) {

        if (calendar == null) {

            return;
        }

        simTab.appendInfo(calendar);
    }

    // //////// NETWORK GRAPH //////////

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getNetworkGraph()
     */
    public Graph getNetworkGraph() {

        return this.networkGraph;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setNetworkGraph(dtp.graph.Graph)
     */
    public void setNetworkGraph(Graph networkGraph) {

        this.networkGraph = networkGraph;

        guiAgent.sendGraphToEUnits(networkGraph);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#createNetworkGraph()
     */
    public Graph createNetworkGraph() {

        ArrayList<GraphPoint> points;

        points = guiAgent.getLocations();

        if (points == null)
            return null;

        return new GraphGenerator().create(points);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#generateNeighboursNetworkGraph(int, int)
     */
    public Graph generateNeighboursNetworkGraph(int howManyNeighbours,
                                                int howManyPoints) {

        ArrayList<GraphPoint> points;
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

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#generateRandomNetworkGraph(double)
     */
    public Graph generateRandomNetworkGraph(double linksRatio) {

        ArrayList<GraphPoint> points;
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

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getDepotLocation()
     */
    public Point getDepotLocation() {

        return commissionsTab.getDepotLocation();
    }

    // //////// VISUALIZATION //////////

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getVisGui()
     */
    public VisGUI getVisGui() {

        return visGui;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setVisGui(dtp.visualisation.VisGUI)
     */
    public void setVisGui(VisGUI visGui) {

        this.visGui = visGui;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#updateEUnitsInfo(dtp.jade.eunit.EUnitInfo)
     */
    public void updateEUnitsInfo(EUnitInfo eUnitInfo) {

        if (this.visGui == null) {

            return;
        }

        this.visGui.updateEUnitsInfo(eUnitInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#updateGraph(dtp.graph.Graph)
     */
    public void updateGraph(Graph graph) {

        networkGraph = graph;

        if (visGui != null) {

            visGui.updateGraph(graph);
        }
    }

    // //////// BIG SIMMULATION //////////
    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#resetEnvironment()
     */
    public void resetEnvironment() {

        setTimestamp(-1);

        setNetworkGraph(null);

        problemType = ProblemType.WITHOUT_GRAPH;

        visGui = null;

        simInfo = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getTrucksProperties()
     */
    public ArrayList<TransportElementInitialDataTruck> getTrucksProperties() {
        return trucksProperties;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setTrucksProperties(java.util.ArrayList)
     */
    public void setTrucksProperties(
            ArrayList<TransportElementInitialDataTruck> trucksProperties) {
        this.trucksProperties = trucksProperties;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#getTrailersProperties()
     */
    public ArrayList<TransportElementInitialDataTrailer> getTrailersProperties() {
        return trailersProperties;
    }

    /*
     * (non-Javadoc)
     *
     * @see dtp.gui.Gui2#setTrailersProperties(java.util.ArrayList)
     */
    public void setTrailersProperties(
            ArrayList<TransportElementInitialDataTrailer> trailersProperties) {
        this.trailersProperties = trailersProperties;
    }
}
