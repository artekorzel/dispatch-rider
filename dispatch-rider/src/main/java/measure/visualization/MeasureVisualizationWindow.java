package measure.visualization;

import measure.Measure;

import javax.swing.*;
import java.awt.*;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MeasureVisualizationWindow extends JFrame {


    // private static final String PREFERRED_LOOK_AND_FEEL = null;

    private MeasureVisualizationPanel panel;

    public MeasureVisualizationWindow(String name) {
        initComponents(name);
    }

    public MeasureVisualizationWindow() {
        initComponents("NumberOfCommissions");
    }

    private void initComponents(String name) {
        setLayout(new BorderLayout());
        setSize(400, 300);

        panel = new MeasureVisualizationPanel(name);

        this.add(panel, BorderLayout.CENTER);

    }

    public void update(Measure measure, String name) {
        panel.updateChart(measure);
    }

    public synchronized void hide(String holon) {
        panel.hide(holon);
    }

    public synchronized void show(String holon) {
        panel.show(holon);
    }

}
