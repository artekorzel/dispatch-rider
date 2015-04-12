package measure.visualization;

import gui.main.SingletonGUI;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculatorsHolder;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MeasuresVisualizationRunner {

    private final Map<String, MeasureVisualizationPanel> panels = new HashMap<>();
    private final Map<String, MeasureVisualizationControl> controls = new HashMap<>();

    public MeasuresVisualizationRunner(MeasureCalculatorsHolder holder) {
        init(holder.getVisualizationMeasuresNames());
    }

    private void init(List<String> names) {
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

    public synchronized void update(Measure measure, String name) {
        MeasureVisualizationPanel panel = panels.get(name);
        if (panel != null) {
            panel.updateChart(measure);
        }
    }

    public synchronized void setCurrentHolons(Set<AID> aids) {
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
