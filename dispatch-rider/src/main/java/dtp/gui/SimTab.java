package dtp.gui;

import dtp.graph.Graph;
import dtp.jade.ProblemType;
import dtp.jade.gui.GUIAgent;
import dtp.util.DirectoriesResolver;
import dtp.visualisation.VisGUI;
import dtp.xml.GraphParser;
import dtp.xml.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */

/**
 * @author kony.pl
 */
public class SimTab extends JPanel {

    private SimLogic gui;

    private GUIAgent guiAgent;

    // //////// GUI components //////////

    private JLabel labelDate;

    private JLabel labelSimSpeed;

    private JLabel labelSimSpeedValue;

    private JLabel labelCommissionsLeft;

    private JSlider sliderSimSpeed;

    private JButton buttonSimStart;

    private JButton buttonSimPause;

    private JButton buttonNextSimStep;

    private JScrollPane scrollPaneInfo;

    private JTextArea textAreaInfo;

    // Show things ...

    private JButton buttonShowCalendars;

    private JButton buttonShowStats;

    private JButton buttonVisStart;

    // Problem type

    private JLabel labelProblemType;

    // graph type button group

    // // WITHOUT_GRAPH

    private JRadioButton radioProblemTypeWithoutGraph;

    // // WITH_GRAPH

    private JRadioButton radioProblemTypeWithGraph;


    // // // Generate

    private JRadioButton radioGraphGenerate;

    // // // // With neighbours

    private JRadioButton radioGraphGenNeighbours;

    private JLabel labelGraphGenNeighboursNumber;

    private JTextField textGraphGenNeighboursNumber;

    private JLabel labelGraphGenLinksToNeighbours;

    private JTextField textGraphGenLinksToNeighbours;

    private JButton buttonGraphGenNeighboursGen;

    // // // // Random

    private JRadioButton radioGraphGenRandom;

    private JLabel labelGraphGenRandLinksRatio;

    private JTextField textGraphGenRandLinksRatio;

    private JButton buttonGraphGenRandGen;

    // // // From file

    private JRadioButton radioGraphFromFile;

    private JLabel labelFilepath;

    private JTextField textNetworkGraphFilename;

    private JButton buttonNetworkGraphBrowseFiles;

    private JButton buttonSetNetworkGraph;

    // checkbox for autoscrolling

    private JCheckBox checkBoxAutoScroll;

    public SimTab(SimLogic gui, GUIAgent guiAgent) {

        this.gui = gui;
        this.guiAgent = guiAgent;
        initGui();
    }

    private void initGui() {

        this.setPreferredSize(new java.awt.Dimension(889, 581));
        this.setLayout(null);

        labelDate = new JLabel();
        add(labelDate);
        labelDate.setText("\"" + String.valueOf(gui.getTimestamp()) + "\"");
        labelDate.setBounds(315, 28, 63, 35);
        labelDate.setFont(new java.awt.Font("Tahoma", 0, 20));
        labelDate.setEnabled(false);

        labelSimSpeed = new JLabel();
        add(labelSimSpeed);
        labelSimSpeed.setText("Sim step:");
        labelSimSpeed.setBounds(630, 35, 49, 21);
        labelSimSpeed.setEnabled(false);

        labelSimSpeedValue = new JLabel();
        add(labelSimSpeedValue);
        labelSimSpeedValue.setBounds(679, 35, 49, 21);
        labelSimSpeedValue.setText("1 per 1s");
        labelSimSpeedValue.setEnabled(false);

        labelCommissionsLeft = new JLabel();
        this.add(labelCommissionsLeft);
        labelCommissionsLeft.setText("Coms waiting: 0");
        labelCommissionsLeft.setBounds(525, 35, 91, 21);

        textNetworkGraphFilename = new JTextField();
        add(textNetworkGraphFilename);
        textNetworkGraphFilename.setText(DirectoriesResolver.getNetworksDir() + File.separator + "testMap.xml");
        textNetworkGraphFilename.setBounds(84, 551, 551, 21);
        textNetworkGraphFilename.setEditable(false);

        sliderSimSpeed = new JSlider();
        add(sliderSimSpeed);
        sliderSimSpeed.setBounds(735, 35, 147, 21);
        sliderSimSpeed.setMinimum(1);
        sliderSimSpeed.setMaximum(10);
        sliderSimSpeed.setValue(1);
        sliderSimSpeed.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt) {
                sliderSimSpeedStateChanged();
            }
        });
        sliderSimSpeed.setEnabled(false);

        scrollPaneInfo = new JScrollPane();
        add(scrollPaneInfo);
        scrollPaneInfo.setBounds(14, 77, 861, 350);
        {
            textAreaInfo = new JTextArea();
            scrollPaneInfo.setViewportView(textAreaInfo);
            textAreaInfo.setBounds(14, 70, 861, 364);
        }

        buttonSimStart = new JButton();
        add(buttonSimStart);
        buttonSimStart.setBounds(14, 35, 91, 21);
        buttonSimStart.setText("Start");
        buttonSimStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonSimStartMouseClicked();
            }
        });

        buttonSimStart.setEnabled(false);

        buttonSimPause = new JButton();
        add(buttonSimPause);
        buttonSimPause.setText("AutoSim");
        buttonSimPause.setBounds(105, 35, 91, 21);
        buttonSimPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonSimPauseMouseClicked();
            }
        });
        buttonSimPause.setEnabled(false);

        buttonNextSimStep = new JButton();
        add(buttonNextSimStep);
        buttonNextSimStep.setText("Next step");
        buttonNextSimStep.setBounds(196, 35, 91, 21);
        buttonNextSimStep.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonNextSimStepActionPerformed();
            }
        });
        buttonNextSimStep.setEnabled(false);

        buttonShowCalendars = new JButton();
        add(buttonShowCalendars);
        buttonShowCalendars.setText("Show calendars");
        buttonShowCalendars.setBounds(538, 433, 110, 21);
        buttonShowCalendars.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonShowCalendarsActionPerformed();
            }
        });
        buttonShowCalendars.setEnabled(false);

        buttonShowStats = new JButton();
        this.add(buttonShowStats);
        buttonShowStats.setText("Show stats");
        buttonShowStats.setBounds(653, 433, 110, 21);
        buttonShowStats.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buttonShowStatsMouseClicked();
            }
        });
        buttonShowStats.setEnabled(false);

        buttonVisStart = new JButton();
        add(buttonVisStart);
        buttonVisStart.setText("Show map");
        buttonVisStart.setBounds(768, 433, 110, 21);
        buttonVisStart.setEnabled(false);
        buttonVisStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buttonVisStartMouseClicked();
            }
        });

        buttonSetNetworkGraph = new JButton();
        add(buttonSetNetworkGraph);
        buttonSetNetworkGraph.setText("Set network graph");
        buttonSetNetworkGraph.setBounds(768, 551, 110, 21);
        buttonSetNetworkGraph.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buttonSetNetworkGraphMouseClicked();
            }
        });

        buttonNetworkGraphBrowseFiles = new JButton();
        add(buttonNetworkGraphBrowseFiles);
        buttonNetworkGraphBrowseFiles.setText("Browse...");
        buttonNetworkGraphBrowseFiles.setBounds(647, 551, 110, 21);
        buttonNetworkGraphBrowseFiles.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buttonNetworkGraphBrowseFilesMouseClicked();
            }
        });

        labelProblemType = new JLabel();
        this.add(labelProblemType);
        labelProblemType.setText("Network graph");
        labelProblemType.setBounds(12, 436, 77, 21);


        class GraphRadioButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("withGraph"))
                    radioProblemTypeWithGraphMouseClicked();
                else if (e.getActionCommand().equals("withoutGraph"))
                    radioProblemTypeWithoutGraphMouseClicked();
                else
                    appendInfo("cos zle z radiobuttonami od grafu");
            }
        }
        GraphRadioButtonListener graphRadioButtonListener = new GraphRadioButtonListener();

        radioProblemTypeWithoutGraph = new JRadioButton();
        this.add(radioProblemTypeWithoutGraph);
        radioProblemTypeWithoutGraph.setText("NO");
        radioProblemTypeWithoutGraph.setBounds(101, 436, 42, 21);
        radioProblemTypeWithoutGraph.setActionCommand("withoutGraph");
        radioProblemTypeWithoutGraph.addActionListener(graphRadioButtonListener);

        radioProblemTypeWithGraph = new JRadioButton();
        this.add(radioProblemTypeWithGraph);
        radioProblemTypeWithGraph.setText("YES");
        radioProblemTypeWithGraph.setBounds(148, 436, 44, 21);
        radioProblemTypeWithGraph.setActionCommand("withGraph");
        radioProblemTypeWithGraph.addActionListener(graphRadioButtonListener);

        ButtonGroup buttonGroupGraphType = new ButtonGroup();
        buttonGroupGraphType.add(radioProblemTypeWithoutGraph);
        buttonGroupGraphType.add(radioProblemTypeWithGraph);


        class GraphGenerateListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("generate"))
                    radioGraphGenerateMouseClicked();
                else if (e.getActionCommand().equals("fromFile"))
                    radioGraphFromFileMouseClicked();
                else
                    appendInfo("cos zle z radiobuttonami od grafu (generate/from file)");
            }
        }
        GraphGenerateListener graphGenerateListener = new GraphGenerateListener();
        {
            radioGraphGenerate = new JRadioButton();
            this.add(radioGraphGenerate);
            radioGraphGenerate.setEnabled(false);
            radioGraphGenerate.setText("Generate");
            radioGraphGenerate.setSelected(true);
            radioGraphGenerate.setBounds(12, 461, 89, 21);
            radioGraphGenerate.setActionCommand("generate");
            radioGraphGenerate.addActionListener(graphGenerateListener);
        }


        {
            radioGraphFromFile = new JRadioButton();
            this.add(radioGraphFromFile);
            radioGraphFromFile.setEnabled(false);
            radioGraphFromFile.setText("Read from file");
            radioGraphFromFile.setSelected(false);
            radioGraphFromFile.setBounds(12, 527, 89, 21);
            radioGraphFromFile.setActionCommand("fromFile");
            radioGraphFromFile.addActionListener(graphGenerateListener);

        }
        ButtonGroup buttonGroupGenerate = new ButtonGroup();
        buttonGroupGenerate.add(radioGraphGenerate);
        buttonGroupGenerate.add(radioGraphFromFile);

        {
            labelFilepath = new JLabel();
            this.add(labelFilepath);
            labelFilepath.setText("File:");
            labelFilepath.setEnabled(false);
            labelFilepath.setBounds(52, 551, 26, 21);
        }
        {
            radioGraphGenNeighbours = new JRadioButton();
            this.add(radioGraphGenNeighbours);
            radioGraphGenNeighbours.setEnabled(false);
            radioGraphGenNeighbours.setText("With neighbours");
            radioGraphGenNeighbours.setSelected(true);
            radioGraphGenNeighbours.setBounds(52, 481, 103, 21);
            radioGraphGenNeighbours.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    radioGraphGenNeighboursMouseClicked();
                }
            });
        }
        {
            radioGraphGenRandom = new JRadioButton();
            this.add(radioGraphGenRandom);
            radioGraphGenRandom.setEnabled(false);
            radioGraphGenRandom.setText("Random");
            radioGraphGenRandom.setSelected(false);
            radioGraphGenRandom.setBounds(52, 504, 103, 21);
            radioGraphGenRandom.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    radioGraphGenRandomMouseClicked();
                }
            });
        }
        {
            labelGraphGenNeighboursNumber = new JLabel();
            this.add(labelGraphGenNeighboursNumber);
            labelGraphGenNeighboursNumber.setText("neighbours number:");
            labelGraphGenNeighboursNumber.setEnabled(false);
            labelGraphGenNeighboursNumber.setBounds(172, 481, 102, 21);
        }
        {
            textGraphGenNeighboursNumber = new JTextField();
            this.add(textGraphGenNeighboursNumber);
            textGraphGenNeighboursNumber.setText("10");
            textGraphGenNeighboursNumber.setEnabled(false);
            textGraphGenNeighboursNumber.setBounds(274, 483, 26, 17);
        }
        {
            labelGraphGenLinksToNeighbours = new JLabel();
            this.add(labelGraphGenLinksToNeighbours);
            labelGraphGenLinksToNeighbours.setText("links to neighbours:");
            labelGraphGenLinksToNeighbours.setEnabled(false);
            labelGraphGenLinksToNeighbours.setBounds(312, 481, 102, 21);
        }
        {
            textGraphGenLinksToNeighbours = new JTextField();
            this.add(textGraphGenLinksToNeighbours);
            textGraphGenLinksToNeighbours.setText("2");
            textGraphGenLinksToNeighbours.setEnabled(false);
            textGraphGenLinksToNeighbours.setBounds(414, 483, 26, 17);
        }
        {
            labelGraphGenRandLinksRatio = new JLabel();
            this.add(labelGraphGenRandLinksRatio);
            labelGraphGenRandLinksRatio.setText("links ratio (0, 1]:");
            labelGraphGenRandLinksRatio.setEnabled(false);
            labelGraphGenRandLinksRatio.setBounds(172, 504, 83, 21);
        }
        {
            textGraphGenRandLinksRatio = new JTextField();
            this.add(textGraphGenRandLinksRatio);
            textGraphGenRandLinksRatio.setText("0.02");
            textGraphGenRandLinksRatio.setEnabled(false);
            textGraphGenRandLinksRatio.setBounds(267, 506, 33, 17);
        }
        {
            buttonGraphGenNeighboursGen = new JButton();
            this.add(buttonGraphGenNeighboursGen);
            buttonGraphGenNeighboursGen.setEnabled(false);
            buttonGraphGenNeighboursGen.setText("Generate");
            buttonGraphGenNeighboursGen.setBounds(452, 481, 110, 21);
            buttonGraphGenNeighboursGen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buttonGraphGenNeighboursGenMouseClicked();
                }
            });
        }
        {
            buttonGraphGenRandGen = new JButton();
            this.add(buttonGraphGenRandGen);
            buttonGraphGenRandGen.setEnabled(false);
            buttonGraphGenRandGen.setText("Generate");
            buttonGraphGenRandGen.setBounds(452, 504, 110, 21);
            buttonGraphGenRandGen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buttonGraphGenRandGenMouseClicked();
                }
            });
        }

        if (gui.getProblemType() == ProblemType.WITHOUT_GRAPH) {

            radioProblemTypeWithoutGraph.setSelected(true);

            textNetworkGraphFilename.setEnabled(false);
            buttonSetNetworkGraph.setEnabled(false);
            buttonNetworkGraphBrowseFiles.setEnabled(false);

        } else {

            radioProblemTypeWithGraph.setSelected(true);

            textNetworkGraphFilename.setEnabled(true);
            buttonSetNetworkGraph.setEnabled(true);
            buttonNetworkGraphBrowseFiles.setEnabled(true);
        }
        {
            checkBoxAutoScroll = new JCheckBox("AutoScroll");
            checkBoxAutoScroll.setSelected(true);
            this.add(checkBoxAutoScroll);
            checkBoxAutoScroll.setBounds(800, 35, 91, 21);

        }

        labelCommissionsLeft.setVisible(false);
        labelSimSpeed.setVisible(false);
        sliderSimSpeed.setVisible(false);
        labelSimSpeedValue.setVisible(false);

        //run/paused po lwej u gory
        buttonSimPause.setVisible(true);

    }

    private void sliderSimSpeedStateChanged() {

        labelSimSpeedValue.setText("1 per " + String.valueOf(sliderSimSpeed.getValue()) + "s");
        gui.setTimerDelay(1000 * sliderSimSpeed.getValue());
    }

    private void buttonSimStartMouseClicked() {

        if (gui.getProblemType() == ProblemType.WITH_GRAPH && gui.getNetworkGraph() == null)
            return;

        buttonSetNetworkGraph.setEnabled(false);
        buttonNetworkGraphBrowseFiles.setEnabled(false);
        buttonSimStart.setEnabled(false);

        labelProblemType.setEnabled(false);
        radioProblemTypeWithoutGraph.setEnabled(false);
        radioProblemTypeWithGraph.setEnabled(false);

        sliderSimSpeed.setEnabled(true);
        labelSimSpeedValue.setEnabled(true);
        labelDate.setEnabled(true);
        labelSimSpeed.setEnabled(true);
        buttonSimPause.setEnabled(true);
        buttonNextSimStep.setEnabled(true);
        buttonShowCalendars.setEnabled(true);
        buttonShowStats.setEnabled(true);

        radioGraphGenerate.setEnabled(false);
        radioGraphGenNeighbours.setEnabled(false);
        labelGraphGenNeighboursNumber.setEnabled(false);
        textGraphGenNeighboursNumber.setEnabled(false);
        labelGraphGenLinksToNeighbours.setEnabled(false);
        textGraphGenLinksToNeighbours.setEnabled(false);
        buttonGraphGenNeighboursGen.setEnabled(false);

        radioGraphGenRandom.setEnabled(false);
        labelGraphGenRandLinksRatio.setEnabled(false);
        textGraphGenRandLinksRatio.setEnabled(false);
        buttonGraphGenRandGen.setEnabled(false);

        radioGraphFromFile.setEnabled(false);
        labelFilepath.setEnabled(false);
        textNetworkGraphFilename.setEnabled(false);
        buttonNetworkGraphBrowseFiles.setEnabled(false);
        buttonSetNetworkGraph.setEnabled(false);

        gui.simStart();
        gui.displayMessage("GUI - simulation started...");
    }

    private void buttonSimPauseMouseClicked() {
        buttonNextSimStep.setEnabled(false);
        gui.autoSimulation();


        /*
        if (guiAgent.isTimerRunning()) {

            pauseSim(true);

        } else {

            pauseSim(false);
        }
        */
    }

    private void buttonNextSimStepActionPerformed() {
        gui.nextSimStep();
        buttonSimPause.setEnabled(false);
    }

    private void buttonShowCalendarsActionPerformed() {
        guiAgent.askForEUnitsCalendars();
    }

    private void buttonShowStatsMouseClicked() {

        guiAgent.askForEUnitsCalendarStats();
    }

    private void buttonVisStartMouseClicked() {
        VisGUI visGUI;
        Graph graph = null;

        if (gui.getProblemType() == ProblemType.WITHOUT_GRAPH) {

            graph = gui.createNetworkGraph();

        } else if (gui.getProblemType() == ProblemType.WITH_GRAPH) {

            graph = gui.getNetworkGraph();
        }

        if (graph == null) {

            if (gui.getProblemType() == ProblemType.WITHOUT_GRAPH)
                gui.displayMessage("GUI - add some commissions to display map");

            else if (gui.getProblemType() == ProblemType.WITH_GRAPH)
                gui.displayMessage("GUI - upload or generate network graph " + "to display map");

            return;
        }

        visGUI = new VisGUI(graph, gui.getProblemType());
        visGUI.setVisible(true);
        gui.setVisGui(visGUI);
    }

    private void radioProblemTypeWithoutGraphMouseClicked() {

        radioProblemTypeWithoutGraph.setSelected(true);
        radioProblemTypeWithGraph.setSelected(false);

        radioGraphGenerate.setEnabled(false);
        radioGraphGenNeighbours.setEnabled(false);
        labelGraphGenNeighboursNumber.setEnabled(false);
        textGraphGenNeighboursNumber.setEnabled(false);
        labelGraphGenLinksToNeighbours.setEnabled(false);
        textGraphGenLinksToNeighbours.setEnabled(false);
        buttonGraphGenNeighboursGen.setEnabled(false);

        radioGraphGenRandom.setEnabled(false);
        labelGraphGenRandLinksRatio.setEnabled(false);
        textGraphGenRandLinksRatio.setEnabled(false);
        buttonGraphGenRandGen.setEnabled(false);

        radioGraphFromFile.setEnabled(false);
        labelFilepath.setEnabled(false);
        textNetworkGraphFilename.setEnabled(false);
        buttonNetworkGraphBrowseFiles.setEnabled(false);
        buttonSetNetworkGraph.setEnabled(false);

        gui.setProblemType(ProblemType.WITHOUT_GRAPH);

        if (gui.getNetworkGraph() != null) {

            gui.setNetworkGraph(null);
            gui.displayMessage("GUI - network graph set to null");
        }

        if (gui.getSimInfo() != null) {

            enableSimStartButton();
        }
    }

    private void radioProblemTypeWithGraphMouseClicked() {

        radioProblemTypeWithGraph.setSelected(true);
        radioProblemTypeWithoutGraph.setSelected(false);

        radioGraphGenerate.setEnabled(true);
        radioGraphFromFile.setEnabled(true);

        if (radioGraphGenerate.isSelected()) {

            radioGraphGenNeighbours.setEnabled(true);
            if (radioGraphGenNeighbours.isSelected()) {
                labelGraphGenNeighboursNumber.setEnabled(true);
                textGraphGenNeighboursNumber.setEnabled(true);
                labelGraphGenLinksToNeighbours.setEnabled(true);
                textGraphGenLinksToNeighbours.setEnabled(true);
                buttonGraphGenNeighboursGen.setEnabled(true);
            }

            radioGraphGenRandom.setEnabled(true);
            if (radioGraphGenRandom.isSelected()) {
                labelGraphGenRandLinksRatio.setEnabled(true);
                textGraphGenRandLinksRatio.setEnabled(true);
                buttonGraphGenRandGen.setEnabled(true);
            }

            radioGraphFromFile.setEnabled(true);

            labelFilepath.setEnabled(false);
            textNetworkGraphFilename.setEnabled(false);
            buttonNetworkGraphBrowseFiles.setEnabled(false);
            buttonSetNetworkGraph.setEnabled(false);

        } else if (radioGraphFromFile.isSelected()) {

            radioGraphGenerate.setEnabled(true);

            radioGraphGenNeighbours.setEnabled(false);
            labelGraphGenNeighboursNumber.setEnabled(false);
            textGraphGenNeighboursNumber.setEnabled(false);
            labelGraphGenLinksToNeighbours.setEnabled(false);
            textGraphGenLinksToNeighbours.setEnabled(false);
            buttonGraphGenNeighboursGen.setEnabled(false);

            radioGraphGenRandom.setEnabled(false);

            labelGraphGenRandLinksRatio.setEnabled(false);
            textGraphGenRandLinksRatio.setEnabled(false);
            buttonGraphGenRandGen.setEnabled(false);

            radioGraphFromFile.setEnabled(true);
            labelFilepath.setEnabled(true);
            textNetworkGraphFilename.setEnabled(true);
            buttonNetworkGraphBrowseFiles.setEnabled(true);
            buttonSetNetworkGraph.setEnabled(true);
        }

        gui.setProblemType(ProblemType.WITH_GRAPH);

        if (gui.getNetworkGraph() == null) {

            disableSimStartButton();

        }
    }

    private void radioGraphGenerateMouseClicked() {

        radioGraphGenerate.setSelected(true);
        radioGraphFromFile.setSelected(false);

        radioGraphGenerate.setEnabled(true);

        radioGraphGenNeighbours.setEnabled(true);
        if (radioGraphGenNeighbours.isSelected()) {
            labelGraphGenNeighboursNumber.setEnabled(true);
            textGraphGenNeighboursNumber.setEnabled(true);
            labelGraphGenLinksToNeighbours.setEnabled(true);
            textGraphGenLinksToNeighbours.setEnabled(true);
            buttonGraphGenNeighboursGen.setEnabled(true);
        }

        radioGraphGenRandom.setEnabled(true);
        if (radioGraphGenRandom.isSelected()) {
            labelGraphGenRandLinksRatio.setEnabled(true);
            textGraphGenRandLinksRatio.setEnabled(true);
            buttonGraphGenRandGen.setEnabled(true);
        }

        radioGraphFromFile.setEnabled(true);

        labelFilepath.setEnabled(false);
        textNetworkGraphFilename.setEnabled(false);
        buttonNetworkGraphBrowseFiles.setEnabled(false);
        buttonSetNetworkGraph.setEnabled(false);
    }

    private void radioGraphGenNeighboursMouseClicked() {

        radioGraphGenNeighbours.setSelected(true);
        radioGraphGenRandom.setSelected(false);

        labelGraphGenNeighboursNumber.setEnabled(true);
        textGraphGenNeighboursNumber.setEnabled(true);
        labelGraphGenLinksToNeighbours.setEnabled(true);
        textGraphGenLinksToNeighbours.setEnabled(true);
        buttonGraphGenNeighboursGen.setEnabled(true);

        labelGraphGenRandLinksRatio.setEnabled(false);
        textGraphGenRandLinksRatio.setEnabled(false);
        buttonGraphGenRandGen.setEnabled(false);
    }

    private void buttonGraphGenNeighboursGenMouseClicked() {

        Graph graph;

        graph = gui.generateNeighboursNetworkGraph(Integer.valueOf(textGraphGenNeighboursNumber.getText()), Integer
                .valueOf(textGraphGenLinksToNeighbours.getText()));

        if (graph == null) {

            gui.displayMessage("GUI - network graph generated is null");
            return;
        }

        gui.displayMessage("GUI - network graph generated (points = " + graph.getPointsSize() + ", links = "
                + graph.getLinksSize() + ")");
        gui.displayMessage("GUI - is graph consistant = " + graph.isConsistant());

        buttonVisStart.setEnabled(true);

        if (gui.getSimInfo() != null) {

            buttonSimStart.setEnabled(true);
        }
    }

    private void radioGraphGenRandomMouseClicked() {

        radioGraphGenNeighbours.setSelected(false);
        radioGraphGenRandom.setSelected(true);

        labelGraphGenNeighboursNumber.setEnabled(false);
        textGraphGenNeighboursNumber.setEnabled(false);
        labelGraphGenLinksToNeighbours.setEnabled(false);
        textGraphGenLinksToNeighbours.setEnabled(false);
        buttonGraphGenNeighboursGen.setEnabled(false);

        labelGraphGenRandLinksRatio.setEnabled(true);
        textGraphGenRandLinksRatio.setEnabled(true);
        buttonGraphGenRandGen.setEnabled(true);
    }

    private void buttonGraphGenRandGenMouseClicked() {

        Graph graph;

        graph = gui.generateRandomNetworkGraph(Double.valueOf(textGraphGenRandLinksRatio.getText()));

        if (graph == null) {

            gui.displayMessage("GUI - network graph generated is null");
            return;
        }

        gui.displayMessage("GUI - network graph generated (points = " + graph.getPointsSize() + ", links = "
                + graph.getLinksSize() + ")");
        gui.displayMessage("GUI - is graph consistant = " + graph.isConsistant());

        buttonVisStart.setEnabled(true);

        if (gui.getSimInfo() != null) {

            buttonSimStart.setEnabled(true);
        }
    }

    private void radioGraphFromFileMouseClicked() {

        radioGraphGenerate.setSelected(false);
        radioGraphFromFile.setSelected(true);

        radioGraphGenerate.setEnabled(true);

        radioGraphGenNeighbours.setEnabled(false);
        labelGraphGenNeighboursNumber.setEnabled(false);
        textGraphGenNeighboursNumber.setEnabled(false);
        labelGraphGenLinksToNeighbours.setEnabled(false);
        textGraphGenLinksToNeighbours.setEnabled(false);
        buttonGraphGenNeighboursGen.setEnabled(false);

        radioGraphGenRandom.setEnabled(false);

        labelGraphGenRandLinksRatio.setEnabled(false);
        textGraphGenRandLinksRatio.setEnabled(false);
        buttonGraphGenRandGen.setEnabled(false);

        radioGraphFromFile.setEnabled(true);
        labelFilepath.setEnabled(true);
        textNetworkGraphFilename.setEnabled(true);
        buttonNetworkGraphBrowseFiles.setEnabled(true);
        buttonSetNetworkGraph.setEnabled(true);
    }

    private void buttonNetworkGraphBrowseFilesMouseClicked() {

        JFileChooser chooser = new JFileChooser(DirectoriesResolver.getNetworksDir());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            textNetworkGraphFilename.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void buttonSetNetworkGraphMouseClicked() {

        buttonSetNetworkGraph.setEnabled(false);

        try {
            gui.setNetworkGraph(new GraphParser().parse(textNetworkGraphFilename.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        gui.displayMessage("GUI - network graph uploaded [" + textNetworkGraphFilename.getText() + "]");

        buttonVisStart.setEnabled(true);
        buttonSetNetworkGraph.setEnabled(true);

        if (gui.getSimInfo() != null) {

            buttonSimStart.setEnabled(true);
        }
    }

    public void enableSimStartButton() {

        buttonSimStart.setEnabled(true);
    }

    public void disableSimStartButton() {

        buttonSimStart.setEnabled(false);
    }

    public void setLabelDate() {

        // jezeli time <0 to ustaw go w ""
        if (gui.getTimestamp() < 0) {
            labelDate.setText("\"" + String.valueOf(gui.getTimestamp()) + "\"");
        } else {
            labelDate.setText(String.valueOf(gui.getTimestamp()));
        }
    }

    public void setComsWaiting(int comsWaiting) {
        labelCommissionsLeft.setText("Coms waiting: " + comsWaiting);
    }

    public void appendInfo(String info) {
        try {
            textAreaInfo.append(info + "\n");
            if (checkBoxAutoScroll.isSelected()) {
                JScrollBar scroll = scrollPaneInfo.getVerticalScrollBar();
                if (scroll != null) {
                    scroll.setValue(scroll.getMaximum());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
