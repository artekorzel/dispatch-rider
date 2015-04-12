package dtp.gui;

import dtp.jade.crisismanager.crisisevents.*;
import dtp.jade.gui.GUIAgent;

import javax.swing.*;
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
public class CrisisManagementTab extends JPanel {

    private int crisisEventsNo;

    private GUIAgent guiAgent;

    // //////// GUI components //////////

    // /// Commission Withdrawal /////

    private JLabel labelComWithdrawalTitle;

    private JLabel labelComWithdrawalComID;

    private JTextField textComWithdrawalComID;

    private JLabel labelComWithdrawalEventTime;

    private JTextField textComWithdrawalEventTime;

    private JButton butComWithdrawalAddEvent;

    // /// Commission Delay /////

    private JLabel labelComDelayTitle;

    private JLabel labelComDelayComID;

    private JTextField textComDelayComID;

    private JLabel labelComDelayDelay;

    private JTextField textComDelayDelay;

    private JLabel labelComDelayEventTime;

    private JTextField textComDelayEventTime;

    private JButton butComDelayAddEvent;

    // /// Execution Unit Failure /////

    private JLabel labelEUnitFailure;

    private JLabel labelEUnitFailureEUnitID;

    private JTextField textEUnitFailureEUnitID;

    private JLabel labelEUnitFailureDurationTime;

    private JTextField textEUnitFailureDurationTime;

    private JLabel labelEUnitFailureEventTime;

    private JTextField textEUnitFailureEventTime;

    private JButton butEUnitFailureAddEvent;

    // /// Traffic Jam /////

    private JLabel labelTrafficJam;

    private JLabel labelTrafficJamStartLocation;

    private JTextField textTrafficJamStartLocationX;

    private JTextField textTrafficJamStartLocationY;

    private JLabel labelTrafficJamEndLocation;

    private JTextField textTrafficJamEndLocationX;

    private JTextField textTrafficJamEndLocationY;

    private JLabel labelTrafficJamEventTime;

    private JLabel labelTrafficJamJamCost;

    private JTextField textTrafficJamEventTime;

    private JButton butTrafficJamAddEvent;

    // /// Road Traffic Exlusion /////

    private JLabel labelRoadTrafficExclusion;

    private JLabel labelRoadTrafficExclusionStartLocation;

    private JTextField textRoadTrafficExclusionStartLocationX;

    private JTextField textRoadTrafficExclusionStartLocationY;
    private JTextField textTrafficJamJamCost;

    private JLabel labelRoadTrafficExclusionEndLocation;

    private JTextField textRoadTrafficExclusionEndLocationX;

    private JTextField textRoadTrafficExclusionEndLocationY;

    private JLabel labelRoadTrafficExclusionEventTime;

    private JTextField textRoadTrafficExclusionEventTime;

    private JButton butRoadTrafficExclusionAddEvent;

    // /// Events list /////

    private JLabel labelCrisisEventsList;

    private JList listContingencies;

    // //////// GUI components END //////////

    public CrisisManagementTab(SimLogic gui, GUIAgent guiAgent) {

        crisisEventsNo = 0;

        this.guiAgent = guiAgent;

        initGui();
    }

    private void initGui() {

        setLayout(null);
        this.setPreferredSize(new java.awt.Dimension(889, 581));
        this.setSize(889, 581);
        {
            labelComDelayTitle = new JLabel();
            this.add(labelComDelayTitle);
            labelComDelayTitle.setText("Commission Delay");
            labelComDelayTitle.setBounds(12, 60, 92, 14);
        }

        butComWithdrawalAddEvent = new JButton();
        add(butComWithdrawalAddEvent);
        butComWithdrawalAddEvent.setText("Add Commission Withdrawal event");
        butComWithdrawalAddEvent.setBounds(630, 31, 250, 21);
        butComWithdrawalAddEvent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butComWithdrawalAddEventMouseClicked();
            }
        });

        listContingencies = new JList();
        add(listContingencies);
        listContingencies.setBounds(12, 326, 865, 243);
        {
            labelComWithdrawalTitle = new JLabel();
            this.add(labelComWithdrawalTitle);
            labelComWithdrawalTitle.setText("Commission Withdrawal");
            labelComWithdrawalTitle.setBounds(12, 10, 122, 14);
        }
        {
            labelComWithdrawalComID = new JLabel();
            this.add(labelComWithdrawalComID);
            labelComWithdrawalComID.setText("Commission ID:");
            labelComWithdrawalComID.setBounds(49, 33, 73, 14);
        }
        {
            textComWithdrawalComID = new JTextField("0");
            this.add(textComWithdrawalComID);
            textComWithdrawalComID.setBounds(134, 30, 34, 21);
        }
        {
            labelComWithdrawalEventTime = new JLabel();
            this.add(labelComWithdrawalEventTime);
            labelComWithdrawalEventTime.setText("Event time:");
            labelComWithdrawalEventTime.setBounds(525, 34, 55, 14);
        }
        {
            textComWithdrawalEventTime = new JTextField("0");
            this.add(textComWithdrawalEventTime);
            textComWithdrawalEventTime.setBounds(585, 31, 34, 21);
        }
        {
            labelCrisisEventsList = new JLabel();
            this.add(labelCrisisEventsList);
            labelCrisisEventsList.setText("Crisis events:");
            labelCrisisEventsList.setBounds(12, 306, 66, 14);
        }
        {
            labelComDelayComID = new JLabel();
            this.add(labelComDelayComID);
            labelComDelayComID.setText("Commission ID:");
            labelComDelayComID.setBounds(49, 83, 73, 14);
        }
        {
            textComDelayComID = new JTextField("0");
            this.add(textComDelayComID);
            textComDelayComID.setBounds(134, 80, 34, 21);
        }
        {
            labelComDelayDelay = new JLabel();
            this.add(labelComDelayDelay);
            labelComDelayDelay.setText("Delay:");
            labelComDelayDelay.setBounds(180, 83, 37, 14);
        }
        {
            textComDelayDelay = new JTextField("0");
            this.add(textComDelayDelay);
            textComDelayDelay.setBounds(223, 80, 34, 21);
        }
        {
            butComDelayAddEvent = new JButton();
            this.add(butComDelayAddEvent);
            butComDelayAddEvent.setText("Add Commission Delay event");
            butComDelayAddEvent.setBounds(630, 81, 250, 21);
            butComDelayAddEvent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    butComDelayAddEventMouseClicked();
                }
            });
        }
        {
            labelComDelayEventTime = new JLabel();
            this.add(labelComDelayEventTime);
            labelComDelayEventTime.setText("Event time:");
            labelComDelayEventTime.setBounds(526, 84, 55, 14);
        }
        {
            textComDelayEventTime = new JTextField("0");
            this.add(textComDelayEventTime);
            textComDelayEventTime.setBounds(585, 81, 34, 21);
        }
        {
            butEUnitFailureAddEvent = new JButton();
            this.add(butEUnitFailureAddEvent);
            butEUnitFailureAddEvent.setText("Add Vehicle Failure event");
            butEUnitFailureAddEvent.setBounds(630, 131, 250, 21);
            butEUnitFailureAddEvent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    butEUnitFailureAddEventMouseClicked();
                }
            });
        }
        {
            textEUnitFailureEventTime = new JTextField();
            this.add(textEUnitFailureEventTime);
            textEUnitFailureEventTime.setText("0");
            textEUnitFailureEventTime.setBounds(585, 131, 34, 21);
        }
        {
            labelEUnitFailureEventTime = new JLabel();
            this.add(labelEUnitFailureEventTime);
            labelEUnitFailureEventTime.setText("Event time:");
            labelEUnitFailureEventTime.setBounds(526, 134, 55, 14);
        }
        {
            textEUnitFailureDurationTime = new JTextField();
            this.add(textEUnitFailureDurationTime);
            textEUnitFailureDurationTime.setText("0");
            textEUnitFailureDurationTime.setBounds(260, 130, 34, 21);
        }
        {
            labelEUnitFailureDurationTime = new JLabel();
            this.add(labelEUnitFailureDurationTime);
            labelEUnitFailureDurationTime.setText("Duration of failure:");
            labelEUnitFailureDurationTime.setBounds(160, 133, 94, 14);
        }
        {
            textEUnitFailureEUnitID = new JTextField();
            this.add(textEUnitFailureEUnitID);
            textEUnitFailureEUnitID.setText("0");
            textEUnitFailureEUnitID.setBounds(110, 130, 34, 21);
        }
        {
            labelEUnitFailureEUnitID = new JLabel();
            this.add(labelEUnitFailureEUnitID);
            labelEUnitFailureEUnitID.setText("Vehicle ID:");
            labelEUnitFailureEUnitID.setBounds(49, 133, 55, 14);
        }
        {
            labelEUnitFailure = new JLabel();
            this.add(labelEUnitFailure);
            labelEUnitFailure.setText("Vehicle Failure");
            labelEUnitFailure.setBounds(12, 110, 73, 14);
        }
        {
            butTrafficJamAddEvent = new JButton();
            this.add(butTrafficJamAddEvent);
            butTrafficJamAddEvent.setText("Add Traffic Jam event");
            butTrafficJamAddEvent.setBounds(630, 181, 250, 21);
            butTrafficJamAddEvent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    butTrafficJamAddEventMouseClicked();
                }
            });
        }
        {
            textTrafficJamEventTime = new JTextField();
            this.add(textTrafficJamEventTime);
            textTrafficJamEventTime.setText("0");
            textTrafficJamEventTime.setBounds(585, 181, 34, 21);
        }
        {
            labelTrafficJamEventTime = new JLabel();
            this.add(labelTrafficJamEventTime);
            labelTrafficJamEventTime.setText("Event time:");
            labelTrafficJamEventTime.setBounds(526, 183, 55, 14);
        }
        {
            labelTrafficJamStartLocation = new JLabel();
            this.add(labelTrafficJamStartLocation);
            labelTrafficJamStartLocation.setText("Start location (x, y):");
            labelTrafficJamStartLocation.setBounds(49, 183, 102, 14);
        }
        {
            labelTrafficJam = new JLabel();
            this.add(labelTrafficJam);
            labelTrafficJam.setText("Traffic Jam");
            labelTrafficJam.setBounds(12, 160, 59, 14);
        }
        {
            butRoadTrafficExclusionAddEvent = new JButton();
            this.add(butRoadTrafficExclusionAddEvent);
            butRoadTrafficExclusionAddEvent.setText("Add Road Traffic Exclusion event");
            butRoadTrafficExclusionAddEvent.setBounds(630, 231, 250, 21);
            butRoadTrafficExclusionAddEvent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    butRoadTrafficExclusionAddEventMouseClicked();
                }
            });
        }
        {
            textRoadTrafficExclusionEventTime = new JTextField();
            this.add(textRoadTrafficExclusionEventTime);
            textRoadTrafficExclusionEventTime.setText("0");
            textRoadTrafficExclusionEventTime.setBounds(585, 231, 34, 21);
        }
        {
            labelRoadTrafficExclusionEventTime = new JLabel();
            this.add(labelRoadTrafficExclusionEventTime);
            labelRoadTrafficExclusionEventTime.setText("Event time:");
            labelRoadTrafficExclusionEventTime.setBounds(526, 233, 55, 14);
        }
        {
            labelRoadTrafficExclusion = new JLabel();
            this.add(labelRoadTrafficExclusion);
            labelRoadTrafficExclusion.setText("Road Traffic Exclusion");
            labelRoadTrafficExclusion.setBounds(12, 210, 115, 14);
        }
        {
            textTrafficJamStartLocationY = new JTextField("0");
            this.add(textTrafficJamStartLocationY);
            textTrafficJamStartLocationY.setBounds(190, 180, 34, 21);
        }
        {
            textTrafficJamStartLocationX = new JTextField("0");
            this.add(textTrafficJamStartLocationX);
            textTrafficJamStartLocationX.setBounds(155, 180, 34, 21);
        }
        {
            labelTrafficJamEndLocation = new JLabel();
            this.add(labelTrafficJamEndLocation);
            labelTrafficJamEndLocation.setText("End location (x, y):");
            labelTrafficJamEndLocation.setBounds(235, 183, 96, 14);
        }
        {
            textTrafficJamEndLocationY = new JTextField("0");
            this.add(textTrafficJamEndLocationY);
            textTrafficJamEndLocationY.setBounds(376, 180, 34, 21);
        }
        {
            textTrafficJamEndLocationX = new JTextField("0");
            this.add(textTrafficJamEndLocationX);
            textTrafficJamEndLocationX.setBounds(338, 180, 34, 21);
        }
        {
            labelRoadTrafficExclusionStartLocation = new JLabel();
            this.add(labelRoadTrafficExclusionStartLocation);
            labelRoadTrafficExclusionStartLocation.setText("Start location (x, y):");
            labelRoadTrafficExclusionStartLocation.setBounds(49, 233, 102, 14);
        }
        {
            textRoadTrafficExclusionStartLocationY = new JTextField("0");
            this.add(textRoadTrafficExclusionStartLocationY);
            textRoadTrafficExclusionStartLocationY.setBounds(190, 230, 34, 21);
        }
        {
            textRoadTrafficExclusionStartLocationX = new JTextField("0");
            this.add(textRoadTrafficExclusionStartLocationX);
            textRoadTrafficExclusionStartLocationX.setBounds(155, 230, 34, 21);
        }
        {
            labelRoadTrafficExclusionEndLocation = new JLabel();
            this.add(labelRoadTrafficExclusionEndLocation);
            labelRoadTrafficExclusionEndLocation.setText("End location (x, y):");
            labelRoadTrafficExclusionEndLocation.setBounds(235, 233, 96, 14);
        }
        {
            textRoadTrafficExclusionEndLocationY = new JTextField("0");
            this.add(textRoadTrafficExclusionEndLocationY);
            textRoadTrafficExclusionEndLocationY.setBounds(376, 230, 34, 21);
        }
        {
            textRoadTrafficExclusionEndLocationX = new JTextField("0");
            this.add(textRoadTrafficExclusionEndLocationX);
            textRoadTrafficExclusionEndLocationX.setBounds(338, 230, 34, 21);
        }
        {
            textTrafficJamJamCost = new JTextField();
            this.add(textTrafficJamJamCost);
            textTrafficJamJamCost.setText("0");
            textTrafficJamJamCost.setBounds(470, 181, 34, 21);
        }
        {
            labelTrafficJamJamCost = new JLabel();
            this.add(labelTrafficJamJamCost);
            labelTrafficJamJamCost.setText("Jam cost:");
            labelTrafficJamJamCost.setBounds(420, 183, 52, 14);
        }
    }

    private void butComWithdrawalAddEventMouseClicked() {

        CommissionWithdrawalEvent comWithdrawalEvent = new CommissionWithdrawalEvent();

        comWithdrawalEvent.setEventID(newEventID());

        try {

            comWithdrawalEvent.setCommissionID(Integer.valueOf(textComWithdrawalComID.getText()));
            comWithdrawalEvent.setEventTime(Integer.valueOf(textComWithdrawalEventTime.getText()));

        } catch (NumberFormatException e) {

            textComWithdrawalComID.setText("!!!");
            textComWithdrawalEventTime.setText("!!!");

            return;
        }

        textComWithdrawalComID.setText("");
        textComWithdrawalEventTime.setText("");

        addContingencyToList(comWithdrawalEvent);
        guiAgent.sendCrisisEvent(comWithdrawalEvent);
    }

    private void butComDelayAddEventMouseClicked() {

        CommissionDelayEvent comDelayEvent = new CommissionDelayEvent();

        comDelayEvent.setEventID(newEventID());

        try {

            comDelayEvent.setCommissionID(Integer.valueOf(textComDelayComID.getText()));
            comDelayEvent.setDelay(Integer.valueOf(textComDelayDelay.getText()));
            comDelayEvent.setEventTime(Integer.valueOf(textComDelayEventTime.getText()));

        } catch (NumberFormatException e) {

            textComDelayComID.setText("!!!");
            textComDelayEventTime.setText("!!!");

            return;
        }

        textComDelayComID.setText("");
        textComDelayDelay.setText("");
        textComDelayEventTime.setText("");

        addContingencyToList(comDelayEvent);
        guiAgent.sendCrisisEvent(comDelayEvent);
    }

    private void butEUnitFailureAddEventMouseClicked() {

        EUnitFailureEvent eUnitFailureEvent = new EUnitFailureEvent();

        eUnitFailureEvent.setEventID(newEventID());

        try {

            eUnitFailureEvent.setEUnitID(Integer.valueOf(textEUnitFailureEUnitID.getText()));
            eUnitFailureEvent.setFailureDuration(Double.valueOf(textEUnitFailureDurationTime.getText()));
            eUnitFailureEvent.setEventTime(Integer.valueOf(textEUnitFailureEventTime.getText()));

        } catch (NumberFormatException e) {

            textEUnitFailureEUnitID.setText("!!!");
            textEUnitFailureDurationTime.setText("!!!");
            textEUnitFailureEventTime.setText("!!!");

            return;
        }

        textEUnitFailureEUnitID.setText("");
        textEUnitFailureDurationTime.setText("");
        textEUnitFailureEventTime.setText("");

        addContingencyToList(eUnitFailureEvent);
        guiAgent.sendCrisisEvent(eUnitFailureEvent);
    }

    private void butTrafficJamAddEventMouseClicked() {

        TrafficJamEvent trafficJamEvent = new TrafficJamEvent();

        trafficJamEvent.setEventID(newEventID());

        try {

            trafficJamEvent.setStartPoint(new Point2D.Double(Double.valueOf(textTrafficJamStartLocationX.getText()),
                    Double.valueOf(textTrafficJamStartLocationY.getText())));
            trafficJamEvent.setEndPoint(new Point2D.Double(Double.valueOf(textTrafficJamEndLocationX.getText()), Double
                    .valueOf(textTrafficJamEndLocationY.getText())));
            trafficJamEvent.setJamCost(Double.valueOf(textTrafficJamJamCost.getText()));
            trafficJamEvent.setEventTime(Integer.valueOf(textTrafficJamEventTime.getText()));

        } catch (NumberFormatException e) {

            textTrafficJamStartLocationX.setText("!!!");
            textTrafficJamStartLocationY.setText("!!!");
            textTrafficJamEndLocationX.setText("!!!");
            textTrafficJamEndLocationY.setText("!!!");
            textTrafficJamJamCost.setText("!!!");
            textTrafficJamEventTime.setText("!!!");

            return;
        }

        textTrafficJamStartLocationX.setText("");
        textTrafficJamStartLocationY.setText("");
        textTrafficJamEndLocationX.setText("");
        textTrafficJamEndLocationY.setText("");
        textTrafficJamJamCost.setText("");
        textTrafficJamEventTime.setText("");

        addContingencyToList(trafficJamEvent);
        guiAgent.sendCrisisEvent(trafficJamEvent);
    }

    private void butRoadTrafficExclusionAddEventMouseClicked() {

        RoadTrafficExclusionEvent roadTrafficExclusionEvent = new RoadTrafficExclusionEvent();

        roadTrafficExclusionEvent.setEventID(newEventID());

        try {

            roadTrafficExclusionEvent.setStartPoint(new Point2D.Double(Double
                    .valueOf(textRoadTrafficExclusionStartLocationX.getText()), Double
                    .valueOf(textRoadTrafficExclusionStartLocationY.getText())));
            roadTrafficExclusionEvent.setEndPoint(new Point2D.Double(Double
                    .valueOf(textRoadTrafficExclusionEndLocationX.getText()), Double
                    .valueOf(textRoadTrafficExclusionEndLocationY.getText())));
            roadTrafficExclusionEvent.setEventTime(Integer.valueOf(textRoadTrafficExclusionEventTime.getText()));

        } catch (NumberFormatException e) {

            textRoadTrafficExclusionStartLocationX.setText("!!!");
            textRoadTrafficExclusionStartLocationY.setText("!!!");
            textRoadTrafficExclusionEndLocationX.setText("!!!");
            textRoadTrafficExclusionEndLocationY.setText("!!!");
            textRoadTrafficExclusionEventTime.setText("!!!");

            return;
        }

        textRoadTrafficExclusionStartLocationX.setText("");
        textRoadTrafficExclusionStartLocationY.setText("");
        textRoadTrafficExclusionEndLocationX.setText("");
        textRoadTrafficExclusionEndLocationY.setText("");
        textRoadTrafficExclusionEventTime.setText("");

        addContingencyToList(roadTrafficExclusionEvent);
        guiAgent.sendCrisisEvent(roadTrafficExclusionEvent);
    }

    private void addContingencyToList(CrisisEvent crisisEvent) {

        ListModel listModel = listContingencies.getModel();
        String[] contingencies = new String[listModel.getSize() + 1];
        for (int i = 0; i < listModel.getSize(); i++)
            contingencies[i] = (String) listModel.getElementAt(i);
        contingencies[listModel.getSize()] = crisisEvent.toString();
        listContingencies.setModel(new DefaultComboBoxModel(contingencies));
    }

    private int newEventID() {

        crisisEventsNo++;

        return crisisEventsNo - 1;
    }

}
