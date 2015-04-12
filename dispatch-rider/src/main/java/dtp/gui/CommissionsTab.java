package dtp.gui;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.jade.ProblemType;
import dtp.jade.gui.GUIAgent;
import dtp.simulation.SimInfo;
import dtp.util.DirectoriesResolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

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
public class CommissionsTab extends JPanel {

    private SimLogic gui;

    private GUIAgent guiAgent;

    // //////// GUI components //////////

    private JCheckBox checkBoxMarkSentCommisssions;

    private JCheckBox checkBoxIsProblemDynamic;

    private JList listCommissions;

    // Constrains

    private JTextField textDepotX;

    private JTextField textDepotY;

    private JTextField textDeadline;

    private JTextField textMaxLoad;

    private JButton buttonSetConstrains;
    private Point2D.Double depot;
    private double deadline;
    private double maxLoad;

    public CommissionsTab(SimLogic gui, GUIAgent guiAgent) {

        this.gui = gui;

        this.guiAgent = guiAgent;

        initGui();
    }

    private void initGui() {

        setLayout(null);
        this.setPreferredSize(new java.awt.Dimension(945, 623));

        checkBoxMarkSentCommisssions = new JCheckBox();
        add(checkBoxMarkSentCommisssions);
        checkBoxMarkSentCommisssions.setText("Show sent commissions");
        checkBoxMarkSentCommisssions.setBounds(7, 150, 133, 21);
        checkBoxMarkSentCommisssions.setSelected(false);
        checkBoxMarkSentCommisssions.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                checkBoxMarkSentCommissionsActionPerformed();
            }
        });

        listCommissions = new JList();
        JScrollPane listCommissionsWrapper = new JScrollPane(listCommissions);
        add(listCommissionsWrapper);

        listCommissions.setBounds(0, 0, 732, 567);
        listCommissionsWrapper.setBounds(147, 7, 732, 567);
        listCommissions.setToolTipText("Zawiera list\u0119 zlece\u0144 maj\u0105cych"
                + " pojawi\u0107 si\u0119 w systemie w trakcie symulacji");

        JButton buttonAddSingleCommission = new JButton();
        add(buttonAddSingleCommission);
        buttonAddSingleCommission.setText("Add commission");
        buttonAddSingleCommission.setBounds(7, 14, 133, 21);
        buttonAddSingleCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonAddSingleCommissionActionPerformed();
            }
        });

        JButton buttonAddCommissionsGroupTxt = new JButton();
        add(buttonAddCommissionsGroupTxt);
        buttonAddCommissionsGroupTxt.setText("Add coms group");
        buttonAddCommissionsGroupTxt.setBounds(7, 42, 133, 21);
        buttonAddCommissionsGroupTxt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonAddCommissionsGroupTxtActionPerformed();
            }
        });

        checkBoxIsProblemDynamic = new JCheckBox();
        add(checkBoxIsProblemDynamic);
        checkBoxIsProblemDynamic.setText("Dynamic problem");
        checkBoxIsProblemDynamic.setBounds(17, 68, 133, 21);
        checkBoxIsProblemDynamic.setSelected(false);


        JButton buttonEditCommission = new JButton();
        add(buttonEditCommission);
        buttonEditCommission.setText("Edit");
        buttonEditCommission.setBounds(7, 94, 133, 21);
        buttonEditCommission.setToolTipText("Modyfikuje wybrane zlecenie");
        buttonEditCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonEditCommissionActionPerformed();
            }
        });

        JButton buttonRemoveCommission = new JButton();
        add(buttonRemoveCommission);
        buttonRemoveCommission.setText("Remove");
        buttonRemoveCommission.setBounds(7, 122, 133, 21);
        buttonRemoveCommission.setToolTipText("Usuwa wybrane zlecenie");
        buttonRemoveCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonRemoveCommissionActionPerformed();
            }
        });


        JButton buttonTruckProperties = new JButton();
        add(buttonTruckProperties);
        buttonTruckProperties.setText("Truck properties");
        buttonTruckProperties.setBounds(7, 422, 133, 21);
        buttonTruckProperties.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonTruckPropertiesActionPerformed();
            }
        });


        JButton buttonTrailersProperties = new JButton();
        add(buttonTrailersProperties);
        buttonTrailersProperties.setText("Trailers properties");
        buttonTrailersProperties.setBounds(7, 450, 133, 21);
        buttonTrailersProperties.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonTrailersPropertiesActionPerformed();
            }
        });

        JLabel labelDepot = new JLabel();
        this.add(labelDepot);
        labelDepot.setText("Depot: (x, y)");
        labelDepot.setBounds(21, 168, 63, 28);

        textDepotX = new JTextField();
        this.add(textDepotX);
        textDepotX.setText("0");
        textDepotX.setBounds(21, 196, 42, 21);

        textDepotY = new JTextField();
        this.add(textDepotY);
        textDepotY.setText("0");
        textDepotY.setBounds(70, 196, 42, 21);

        JLabel labelDeadline = new JLabel();
        this.add(labelDeadline);
        labelDeadline.setText("Deadline:");
        labelDeadline.setBounds(21, 217, 63, 28);

        textDeadline = new JTextField();
        this.add(textDeadline);
        textDeadline.setText("1500");
        textDeadline.setBounds(21, 245, 91, 21);

        JLabel labelMaxLoad = new JLabel();
        this.add(labelMaxLoad);
        labelMaxLoad.setText("Capacity:");
        labelMaxLoad.setBounds(21, 266, 63, 28);

        textMaxLoad = new JTextField();
        this.add(textMaxLoad);
        textMaxLoad.setText("200");
        textMaxLoad.setBounds(21, 294, 91, 21);

        buttonSetConstrains = new JButton();
        this.add(buttonSetConstrains);
        buttonSetConstrains.setText("Set constrains");
        buttonSetConstrains.setBounds(7, 322, 133, 21);
        buttonSetConstrains.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonSetConstrainsActionPerformed();
            }
        });
    }

    protected void buttonTrailersPropertiesActionPerformed() {
        new TrailersProperties(gui);
    }

    protected void buttonTruckPropertiesActionPerformed() {
        new TruckProperties(gui);
    }

    private void checkBoxMarkSentCommissionsActionPerformed() {
        if (checkBoxMarkSentCommisssions.isSelected()) {
            listCommissions.setSelectionBackground(java.awt.Color.GREEN.darker());
            markSentCommissions();
        } else {
            listCommissions.setSelectionBackground(java.awt.Color.BLUE.darker());
            listCommissions.clearSelection();
        }
    }

    private void buttonAddSingleCommissionActionPerformed() {
        new AddEditCommission(this);
    }

    public void addCommissionGroup(String filename, boolean dynamic) {
        Commission[] commissions = TxtFileReader.getCommissions(filename);

        gui.displayMessage("GUI - commissions read from .txt file [" + filename + "]");

        int incomeTime[] = new int[commissions.length];
        if (dynamic) {
            incomeTime = TxtFileReader.getIncomeTimes(filename + ".income_times", commissions.length);
            if (incomeTime == null) {
                gui.displayMessage("GUI - error reading commission's income times");
                return;
            }
        }

        double farthestPickupLocation = TxtFileReader.getFarthestPickupLocation(filename);
        int farthestPickupLocation2int = (int) farthestPickupLocation;

        gui.displayMessage("GUI - distance from depot to farthest pickup location = " + farthestPickupLocation + " ("
                + farthestPickupLocation2int + ")");

        // Random rand = new Random(System.nanoTime());

        for (int i = 0; i < commissions.length; i++) {

            // Commission tempCom;
            // int tempPickupTime1;
            //
            // tempCom = commissions[i];
            // tempPickupTime1 = tempCom.getPickupTime1();
            // if (tempPickupTime1 >= farthestPickupLocation2int) {
            //
            // incomeTime = tempPickupTime1 - farthestPickupLocation2int;
            //
            // } else {
            //
            // incomeTime = 0;
            // // incomeTime = rand.nextInt(10);
            // }

//            incomeTime = 0;

            // incomeTime = commissions[i].getID() % 10;

            addCommissionHandler(new CommissionHandler(commissions[i], incomeTime[i]));
        }

        gui.refreshComsWaiting();

        // set sim constraints read from .txt file
        Point2D.Double depot = TxtFileReader.getDepot(filename);
        textDepotX.setText(String.valueOf((int) depot.getX()));
        textDepotY.setText(String.valueOf((int) depot.getY()));
        textDeadline.setText(String.valueOf(TxtFileReader.getDeadline(filename)));

        textMaxLoad.setText(String.valueOf(TxtFileReader.getTruckCapacity(filename)));

        gui.displayMessage("GUI - simulation constrains " + "read form .txt file ["
                + filename + "]");
    }

    public void buttonAddCommissionsGroupTxtActionPerformed() {
        JFileChooser chooser = new JFileChooser(DirectoriesResolver.getTxtCommisionsDir());

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            addCommissionGroup(chooser.getSelectedFile().getAbsolutePath(), checkBoxIsProblemDynamic.isSelected());
        }
    }

    private void buttonEditCommissionActionPerformed() {
        Object[] toEdit = listCommissions.getSelectedValues();
        if (toEdit.length == 1)
            new AddEditCommission(this, listCommissions, (CommissionHandler) toEdit[0]);
    }

    private void buttonRemoveCommissionActionPerformed() {
        Object[] toRemove = listCommissions.getSelectedValues();
        for (int i = 0; i < toRemove.length; i++) {
            removeCommissionHandler(((CommissionHandler) toRemove[i]));
        }
    }

    public void buttonSetConstrainsActionPerformed() {
        try {
            Point2D.Double depot = new Point2D.Double(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
            double deadline = Double.valueOf(textDeadline.getText());
            double maxLoad = Double.valueOf(textMaxLoad.getText());
            setSimConstrains(depot, deadline, maxLoad, false);
        } catch (NumberFormatException e) {
            textDeadline.setText("         ???     ");
        }

    }

    public void setConstraintsTestMode() {
        try {

            Point2D.Double depot = new Point2D.Double(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
            double deadline = Double.valueOf(textDeadline.getText());
            double maxLoad = Double.valueOf(textMaxLoad.getText());
            setSimConstrains(depot, deadline, maxLoad, true);
        } catch (NumberFormatException e) {
            textDeadline.setText("         ???     ");
        }
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize() + 1];
        for (int i = 0; i < listModel.getSize(); i++)
            content[i] = (CommissionHandler) listModel.getElementAt(i);
        content[listModel.getSize()] = commissionHandler;
        listCommissions.setModel(new DefaultComboBoxModel<>(content));

        guiAgent.addCommissionHandler(commissionHandler);
        gui.displayMessage("GUI - commission added " + commissionHandler.toString());
    }

    public void removeCommissionHandler(CommissionHandler comHandler) {
        guiAgent.removeCommissionHandler(comHandler);

        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
        CommissionHandler[] contentNew;
        CommissionHandler temp;
        boolean removed = false;
        for (int i = 0; i < listModel.getSize(); i++) {
            temp = (CommissionHandler) listModel.getElementAt(i);
            if (temp.equals(comHandler))
                removed = true;
            else {
                if (removed) {
                    content[i - 1] = temp;
                } else {
                    content[i] = temp;
                }
            }
        }

        if (removed) {
            contentNew = new CommissionHandler[content.length - 1];
            for (int i = 0; i < content.length - 1; i++) {
                contentNew[i] = content[i];
            }
            listCommissions.setModel(new DefaultComboBoxModel<>(contentNew));
        } else
            listCommissions.setModel(new DefaultComboBoxModel(content));

    }

    public void setSimConstrains(Point2D.Double depot, double deadline, double maxLoad, boolean testMode) {

        SimInfo simConstrains;

        simConstrains = new SimInfo(depot, deadline, maxLoad);
        gui.setSimInfo(simConstrains);
        this.depot = depot;
        this.deadline = deadline;
        this.maxLoad = maxLoad;

        if (!testMode) setConstraints();
    }

    public void setConstraints() {

        textDepotX.setText(Integer.toString((int) depot.getX()));
        textDepotY.setText(Integer.toString((int) depot.getY()));
        textDeadline.setText(Integer.toString((int) deadline));
        textMaxLoad.setText(Integer.toString((int) maxLoad));

        textDepotX.setEnabled(false);
        textDepotY.setEnabled(false);
        textDeadline.setEnabled(false);
        textMaxLoad.setEnabled(false);
        buttonSetConstrains.setEnabled(false);

        gui.displayMessage("GUI - constrains set: depot = (" + depot.getX() + ", " + depot.getY() + ") deadline = "
                + deadline + " capacity = " + maxLoad);

        if (gui.getProblemType() == ProblemType.WITHOUT_GRAPH) {

            gui.enableSimStartButton();

        } else if (gui.getProblemType() == ProblemType.WITH_GRAPH && gui.getNetworkGraph() != null) {

            gui.enableSimStartButton();
        }
    }

    // Podswietla juz wyslane do Dystrybutora zadania
    // getIncomeTime() <= simTime
    public void markSentCommissions() {

        int[] indicesToSelect;
        int indicesToSelectNo = 0;

        if (checkBoxMarkSentCommisssions.isSelected()) {

            ListModel listModel = listCommissions.getModel();
            CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
            for (int i = 0; i < listModel.getSize(); i++)
                content[i] = (CommissionHandler) listModel.getElementAt(i);

            for (int i = 0; i < content.length; i++)
                if (content[i].getIncomeTime() <= gui.getTimestamp())
                    indicesToSelectNo++;

            indicesToSelect = new int[indicesToSelectNo];

            int j = 0;
            for (int i = 0; i < content.length; i++)
                if (content[i].getIncomeTime() <= gui.getTimestamp())
                    indicesToSelect[j++] = i;

            listCommissions.setSelectedIndices(indicesToSelect);
        }
    }

    public int getCommisionsCount() {
        return listCommissions.getModel().getSize();
    }

    public int newCommissions() {
        int newCommissions = 0;

        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++)
            content[i] = (CommissionHandler) listModel.getElementAt(i);

        for (int i = 0; i < content.length; i++)
            if (content[i].getIncomeTime() == gui.getTimestamp())
                newCommissions++;
        return newCommissions;
    }

    public void refreshComsWaiting() {

        gui.refreshComsWaiting();
    }

    public Point getDepotLocation() {

        return new Point(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
    }
}
