package measure.visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MeasureVisualizationControl extends JPanel {


    private final MeasureVisualizationPanel measurePanel;
    private final Set<String> aids = new TreeSet<>();
    private JPanel panel;

    public MeasureVisualizationControl(MeasureVisualizationPanel measurePanel) {
        this.measurePanel = measurePanel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    public void register(String holon) {
        if (aids.contains(holon))
            return;
        aids.add(holon);
        JCheckBox box = new JCheckBox(holon);
        box.setMargin(new Insets(10, 10, 0, 20));
        box.setSelected(true);
        box.addActionListener(new BoxListener(box, holon));
        panel.add(box);
        panel.revalidate();
        revalidate();
    }

    private class BoxListener implements ActionListener {

        private final JCheckBox box;
        private final String holon;

        public BoxListener(JCheckBox box, String holon) {
            this.box = box;
            this.holon = holon;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (box.isSelected()) {
                measurePanel.show(holon);
            } else {
                measurePanel.hide(holon);
            }
        }
    }
}