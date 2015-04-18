package gui.main;

import dtp.graph.Graph;
import dtp.jade.gui.CalendarStatsHolder;
import dtp.simulation.SimInfo;
import gui.commissions.CommissionTableModel;
import gui.holon.HolonTableModel;
import gui.holonstats.HolonStatsTableModel;
import gui.map.MapHolder;
import gui.parameters.DRParams;
import gui.parameters.ParametersTableModel;
import gui.path.PathTableModel;
import xml.elements.SimulationData;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * @author Jakub Tyrcha, Tomasz Put
 *         <p/>
 *         Podstawowa klasa modulu GUI, reprezentujaca glowne okno,
 *         spinajaca wszystkie zakladki, przekazujaca im dane do wyswietlenia,
 *         obslugujaca slider czasowy
 */
public class WindowGUI implements ChangeListener, ActionListener {
    JComboBox holonList;
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JSlider timestampSlider;
    private JPanel mapPanel;
    private List<JPanel> measurePanels = new LinkedList<>();
    private JTable holonTable, commissionTable, holonStatsTable, paramsTable, pathTable;
    private MapHolder mapHolder;

    private Vector<Integer> timestamps = new Vector<>();

    private CalendarStatsHolder statsHolder;

    protected WindowGUI() {

        JPanel mainPane = new JPanel();
        tabbedPane = new JTabbedPane();
        frame = new JFrame("Dispatch Rider");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //slider
        timestampSlider = new JSlider();
        timestampSlider.addChangeListener(this);

        // mapa
        mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());
        mapPanel.setPreferredSize(new Dimension(900, 600));
        mapHolder = new MapHolder();

        // tabela holonw
        JPanel holonPanel = new JPanel();
        holonPanel.setLayout(new BorderLayout());
        holonTable = new JTable(new HolonTableModel());
        holonTable.setAutoCreateRowSorter(true);
        holonTable.setFillsViewportHeight(true);
        holonPanel.add(new JScrollPane(holonTable), BorderLayout.CENTER);

        // trasy
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout());
        pathTable = new JTable(new PathTableModel());
        pathTable.setAutoCreateRowSorter(true);
        pathTable.setFillsViewportHeight(true);
        pathPanel.add(new JScrollPane(pathTable), BorderLayout.CENTER);
        holonList = new JComboBox();
        holonList.addActionListener(this);
        pathPanel.add(holonList, BorderLayout.NORTH);

        //tabela zlecen
        JPanel commissionPanel = new JPanel();
        commissionPanel.setLayout(new BorderLayout());
        commissionTable = new JTable(new CommissionTableModel());
        commissionTable.setAutoCreateRowSorter(true);
        commissionTable.setFillsViewportHeight(true);
        commissionPanel.add(new JScrollPane(commissionTable), BorderLayout.CENTER);

        // tabela statystyk holonw
        JPanel holonStatsPanel = new JPanel();
        holonStatsPanel.setLayout(new BorderLayout());
        holonStatsTable = new JTable(new HolonStatsTableModel());
        holonStatsTable.setAutoCreateRowSorter(true);
        holonStatsTable.setFillsViewportHeight(true);
        holonStatsPanel.add(new JScrollPane(holonStatsTable), BorderLayout.CENTER);

        // parametry
        JPanel paramsPanel = new JPanel();
        paramsPanel.setLayout(new BorderLayout());
        paramsTable = new JTable(new ParametersTableModel());
        paramsTable.setAutoCreateRowSorter(true);
        paramsTable.setFillsViewportHeight(true);
        paramsPanel.add(new JScrollPane(paramsTable), BorderLayout.CENTER);

        // ustawienie zakadek
        tabbedPane.addTab("Mapa", mapPanel);
        tabbedPane.addTab("Holony", holonPanel);
        tabbedPane.addTab("Trasy", pathPanel);
        tabbedPane.addTab("Zlecenia", commissionPanel);
        tabbedPane.addTab("Statystyki holonów", holonStatsPanel);
        tabbedPane.addTab("Parametry algorytmu", paramsPanel);

        frame.getContentPane().add(mainPane);
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.add(tabbedPane);
        mainPane.add(timestampSlider);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * wyswietlanie wartosci zgodnie z ustawieniem na sliderze
     */
    public void stateChanged(ChangeEvent e) {

        //int val = Arrays.binarySearch(timestamps.toArray(), timestampSlider.getValue());

        if (timestamps.size() == 0) return;

        int i = 1;
        while (i < timestamps.size() && timestamps.get(i) <= timestampSlider.getValue())
            i++;
        int val = timestamps.get(i - 1);
        timestampSlider.setValue(val);

        ((HolonTableModel) holonTable.getModel()).setDrawnTimestamp(val);
        ((CommissionTableModel) commissionTable.getModel()).setDrawnTimestamp(val);
        ((HolonStatsTableModel) holonStatsTable.getModel()).setDrawnTimestamp(val);
        ((PathTableModel) pathTable.getModel()).setDrawnTimestamp(val);

        holonTable.repaint();
        commissionTable.repaint();
        holonStatsTable.repaint();
        pathTable.repaint();

        if (val == timestamps.get(timestamps.size() - 1) && val != 0) {
            mapHolder.paintWholePaths(statsHolder);
        } else {
            mapHolder.setDrawnTimestamp(val);
            mapHolder.repaint();
        }

        mapPanel.updateUI();
    }

    private void resetList() {
        if (((PathTableModel) pathTable.getModel()).getHolonIds() == null) return;
        if (holonList.getItemCount() == ((PathTableModel) pathTable.getModel()).getHolonIds().size()) return;
        holonList.removeAllItems();
        for (Integer i : ((PathTableModel) pathTable.getModel()).getHolonIds()) {
            holonList.addItem(i);
        }
    }

    /**
     * poprawne wyswietlanie slidera
     */
    private void regenerateSlider() {
        timestampSlider.setMinimum(timestamps.firstElement());
        timestampSlider.setMaximum(timestamps.lastElement());
        timestampSlider.setPaintTicks(true);
        timestampSlider.setPaintLabels(true);
        timestampSlider.setSnapToTicks(true);
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        for (Integer i : timestamps) {
            table.put(i, new JLabel(i.toString()));
        }

        timestampSlider.setLabelTable(table);
    }

    public void addPanel(String name, JPanel panel) {
        tabbedPane.addTab(name, panel);
        measurePanels.add(panel);
        frame.pack();
    }

    /**
     * Aktualizacja gui o przychodzace dane
     *
     */
    public void update(SimulationData data) {
        if (data == null) {
            return;
        }
        HolonTableModel model = (HolonTableModel) holonTable.getModel();
        model.update(data);
        holonTable.repaint();

        ((CommissionTableModel) commissionTable.getModel()).update(data);
        commissionTable.repaint();

        ((HolonStatsTableModel) holonStatsTable.getModel()).update(data);
        holonStatsTable.repaint();

        ((PathTableModel) pathTable.getModel()).update(data);
        pathTable.repaint();

        mapHolder.update(data);

        for (JPanel panel : measurePanels) {
            panel.repaint();
        }
    }

    /**
     * Metoda wywolywana z kazdym nowym timestamp symulacji,
     * informuje o nadejsciu nowego kroku czasowego
     *
     */
    public void newTimestamp(int val) {
        HolonTableModel model = (HolonTableModel) holonTable.getModel();
        model.newTimestampUpdate(val);
        mapHolder.newTimestampUpdate(val);

        timestamps.add(val);
        timestampSlider.setValue(model.getDrawnTimestamp());
        mapHolder.repaint();
        holonTable.repaint();

        ((CommissionTableModel) commissionTable.getModel()).newTimestampUpdate(val);
        commissionTable.repaint();

        ((HolonStatsTableModel) holonStatsTable.getModel()).newTimestampUpdate(val);
        holonStatsTable.repaint();

        ((PathTableModel) pathTable.getModel()).newTimestampUpdate(val);
        pathTable.repaint();

        regenerateSlider();
        resetList();


        for (JPanel panel : measurePanels) {
            panel.repaint();
        }
    }

    /**
     * Metoda wywolywana w celu przekazaniu do GUI grafu do wyswietlenia
     *
     */
    public void update(Graph graph) {
        if (graph == null) {
            System.out.println("null w grafie");
        }
    }

    /**
     * Metoda sluzaca do przekazania do gui informacji o parametrach algorytmu
     *
     */
    public void update(DRParams params) {
        ParametersTableModel model = (ParametersTableModel) paramsTable.getModel();
        model.update(params);
    }

    /**
     * Metoda wywolywana tylko raz, majaca na celu przekazanie do gui danych z poczatku symulacji
     *
     */
    public void update(SimInfo info) {
        if (info == null) {
            System.out.println("null w siminfo");
            return;
        }
        try {
            HolonTableModel model = (HolonTableModel) holonTable.getModel();
            model.setSimInfo(info);
            mapHolder.setSimInfo(info);
            mapPanel.add(mapHolder.getMap(), BorderLayout.CENTER);
            ((HolonStatsTableModel) holonStatsTable.getModel()).setSimInfo(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        if (cb.getSelectedItem() == null) return;
        int holonId = (Integer) cb.getSelectedItem();
        ((PathTableModel) pathTable.getModel()).setHolon(holonId);
        pathTable.repaint();
    }

    public void setHolonStatsHolder(CalendarStatsHolder statsHolder) {
        this.statsHolder = statsHolder;
    }
}
