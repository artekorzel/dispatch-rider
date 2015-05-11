package measure.visualization;

import gui.main.SingletonGUI;
import jade.core.AID;
import measure.Measure;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MeasuresVisualizationRunner {

    private final Map<String, MeasureVisualizationPanel> panels = new HashMap<>();
    private final Map<String, MeasureVisualizationControl> controls = new HashMap<>();

    public MeasuresVisualizationRunner(String[] measureNames) {
        init(measureNames);
    }

    private void init(String[] names) {
        for (final String name : names) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JPanel panelToMainFrame = new JPanel();
                    panelToMainFrame.setLayout(new BorderLayout());
                    MeasureVisualizationPanel panel = new MeasureVisualizationPanel(name);
                    panels.put(name, panel);
                    panelToMainFrame.add(panel, BorderLayout.CENTER);
                    SingletonGUI.getInstance().addPanel(name, panelToMainFrame);
                    MeasureVisualizationControl control = new MeasureVisualizationControl(panel);
                    panelToMainFrame.add(control, BorderLayout.EAST);
                    controls.put(name, control);
                }
            });
        }
    }

    public void update(Measure measure) {
        MeasureVisualizationPanel panel = panels.get(measure.getName());
        if (panel != null) {
            panel.updateChart(measure);
        }
    }

    public synchronized void setCurrentHolons(AID[] aids) {
        for (MeasureVisualizationControl control : controls.values()) {
            control.register(MeasureVisualizationPanel.avgSeriesName);
        }
        for (AID aid : aids) {
            for (MeasureVisualizationControl control : controls.values()) {
                control.register(aid.getLocalName());
            }
        }
    }
}
