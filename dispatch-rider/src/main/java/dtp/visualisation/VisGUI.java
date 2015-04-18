package dtp.visualisation;

import dtp.graph.Graph;
import dtp.jade.eunit.EUnitInfo;

import javax.swing.*;
import java.awt.*;

public class VisGUI extends JFrame {


    private final int problemType;
    // panel do rysowania
    private VisPanel aVisPanel = null;

    public VisGUI(Graph aGraph, int problemType) {
        super();

        try {
            this.setSize(662, 522);
            setTitle("Dispatch Rider - Network graph");
            JPanel outerPanel = new JPanel();
            getContentPane().add(outerPanel, BorderLayout.CENTER);
            outerPanel.setSize(660, 510);
            outerPanel.setPreferredSize(new Dimension(654, 510));
            aVisPanel = new VisPanel(aGraph, this);
            aVisPanel.setSize(640, 480);
            aVisPanel.setBackground(Color.white);
            aVisPanel.setPreferredSize(new Dimension(640, 480));
            aVisPanel.setOpaque(true);
            outerPanel.add(aVisPanel);
            aVisPanel.repaint();
        } catch (Exception ignored) {
        }

        this.problemType = problemType;
    }

    public Dimension getPanelDimension() {
        return new Dimension(640, 480);
    }

    public int getProblemType() {
        return problemType;
    }

    public void updateGraph(Graph graph) {
        aVisPanel.updateGraph(graph);
    }

    public void updateEUnitsInfo(EUnitInfo eUnitInfo) {
        aVisPanel.updateEunitInfo(eUnitInfo);
    }
}
