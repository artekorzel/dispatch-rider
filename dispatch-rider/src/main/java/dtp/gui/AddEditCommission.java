package dtp.gui;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class AddEditCommission extends JFrame {

    private static Logger logger = Logger.getLogger(AddEditCommission.class);

    private final CommissionsTab commissionsTab;
    // adding new commission -> true
    // editing commission -> false
    private final boolean newCommission;
    private CommissionHandler commissionHandler = null;
    private JPanel mainPanel;

    private JLabel labelPickupLocation;

    private JLabel labelDeliveryLocation;

    private JLabel labelDeliveryTime;

    private JLabel labelIncomeTime;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JTextField txtDeliveryTime2;

    private JTextField txtDeliveryTime1;

    private JTextField txtDeliveryLocationY;

    private JTextField txtPickupTime2;

    private JTextField txtDeliveryLocationX;

    private JTextField txtPickupTime1;

    private JTextField txtPickupLocationY;

    private JTextField txtPickupLocationX;

    private JTextField txtServiceTime;

    private JTextField txtLoad;

    private JTextField txtIncomeTime;

    private JLabel labelServiceTime;

    private JLabel labelLoad;

    private JLabel labelPickupTime;

    private JList commissionsList;

    public AddEditCommission(CommissionsTab commissionsTab) {

        this.commissionsTab = commissionsTab;
        newCommission = true;

        initGUI();
        this.setVisible(true);
    }

    public AddEditCommission(CommissionsTab commissionsTab,
                             JList commissionsList, CommissionHandler comHandler) {

        Commission tempCommission = comHandler.getCommission();

        this.commissionsTab = commissionsTab;
        this.commissionsList = commissionsList;
        commissionHandler = comHandler;
        newCommission = false;

        initGUI();
        txtPickupLocationX.setText(new Double(tempCommission.getPickupX())
                .toString());
        txtPickupLocationY.setText(new Double(tempCommission.getPickupY())
                .toString());
        txtPickupTime1.setText(new Double(tempCommission.getPickupTime1())
                .toString());
        txtPickupTime2.setText(new Double(tempCommission.getPickupTime2())
                .toString());
        txtDeliveryLocationX.setText(new Double(tempCommission.getDeliveryX())
                .toString());
        txtDeliveryLocationY.setText(new Double(tempCommission.getDeliveryY())
                .toString());
        txtDeliveryTime1.setText(new Double(tempCommission.getDeliveryTime1())
                .toString());
        txtDeliveryTime2.setText(new Double(tempCommission.getDeliveryTime2())
                .toString());
        txtLoad.setText(new Integer(tempCommission.getLoad()).toString());
        // txtServiceTime.setText(new
        // Integer(tempCommission.getServiceTime()).toString());
        txtIncomeTime.setText(new Integer(comHandler.getIncomeTime())
                .toString());
        this.setVisible(true);
    }

    private void initGUI() {

        try {
            {
                mainPanel = new JPanel();
                getContentPane().add(mainPanel);
                mainPanel.setBounds(0, -7, 322, 231);
                mainPanel.setLayout(null);
                {
                    labelDeliveryLocation = new JLabel();
                    mainPanel.add(labelDeliveryLocation);
                    labelDeliveryLocation.setText("DeliveryLocation");
                    labelDeliveryLocation.setBounds(14, 56, 84, 21);
                }
                {
                    labelPickupLocation = new JLabel();
                    mainPanel.add(labelPickupLocation);
                    labelPickupLocation.setText("Pickup Location");
                    labelPickupLocation.setBounds(14, 14, 91, 21);
                }
                {
                    labelPickupTime = new JLabel();
                    mainPanel.add(labelPickupTime);
                    labelPickupTime.setText("Pickup Time");
                    labelPickupTime.setBounds(14, 35, 63, 21);
                }
                {
                    labelDeliveryTime = new JLabel();
                    mainPanel.add(labelDeliveryTime);
                    labelDeliveryTime.setText("DeliveryTime");
                    labelDeliveryTime.setBounds(14, 77, 63, 21);
                }
                {
                    labelLoad = new JLabel();
                    mainPanel.add(labelLoad);
                    labelLoad.setText("Load");
                    labelLoad.setBounds(14, 98, 63, 21);
                }
                {
                    labelServiceTime = new JLabel();
                    mainPanel.add(labelServiceTime);
                    labelServiceTime.setText("Service Time");
                    labelServiceTime.setBounds(14, 119, 63, 21);
                }
                {
                    txtPickupLocationX = new JTextField();
                    mainPanel.add(txtPickupLocationX);
                    txtPickupLocationX.setBounds(112, 14, 42, 21);
                }
                {
                    txtPickupLocationY = new JTextField();
                    mainPanel.add(txtPickupLocationY);
                    txtPickupLocationY.setBounds(161, 14, 42, 21);
                }
                {
                    txtPickupTime1 = new JTextField();
                    mainPanel.add(txtPickupTime1);
                    txtPickupTime1.setBounds(112, 35, 42, 21);
                }
                {
                    txtPickupTime2 = new JTextField();
                    mainPanel.add(txtPickupTime2);
                    txtPickupTime2.setBounds(161, 35, 42, 21);
                }
                {
                    txtDeliveryLocationX = new JTextField();
                    mainPanel.add(txtDeliveryLocationX);
                    txtDeliveryLocationX.setBounds(112, 56, 42, 21);
                }
                {
                    txtDeliveryLocationY = new JTextField();
                    mainPanel.add(txtDeliveryLocationY);
                    txtDeliveryLocationY.setBounds(161, 56, 42, 21);
                }
                {
                    txtDeliveryTime1 = new JTextField();
                    mainPanel.add(txtDeliveryTime1);
                    txtDeliveryTime1.setBounds(112, 77, 42, 21);
                }
                {
                    txtDeliveryTime2 = new JTextField();
                    mainPanel.add(txtDeliveryTime2);
                    txtDeliveryTime2.setBounds(161, 77, 42, 21);
                }
                {
                    txtLoad = new JTextField();
                    mainPanel.add(txtLoad);
                    txtLoad.setBounds(112, 98, 35, 21);
                }
                {
                    txtServiceTime = new JTextField();
                    mainPanel.add(txtServiceTime);
                    txtServiceTime.setBounds(112, 119, 35, 21);
                }
                {
                    buttonOK = new JButton();
                    mainPanel.add(buttonOK);
                    buttonOK.setText("OK");
                    buttonOK.setBounds(168, 196, 70, 28);
                    buttonOK.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            buttonOKActionPerformed(evt);
                        }
                    });
                }
                {
                    buttonCancel = new JButton();
                    mainPanel.add(buttonCancel);
                    buttonCancel.setText("Cancel");
                    buttonCancel.setBounds(245, 196, 70, 28);
                    buttonCancel.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            buttonCancelActionPerformed(evt);
                        }
                    });
                }
                {
                    labelIncomeTime = new JLabel();
                    mainPanel.add(labelIncomeTime);
                    labelIncomeTime.setText("Income Time");
                    labelIncomeTime.setBounds(14, 147, 63, 21);
                }
                {
                    txtIncomeTime = new JTextField();
                    mainPanel.add(txtIncomeTime);
                    txtIncomeTime.setBounds(112, 147, 35, 21);
                }
            }
            {
                getContentPane().setLayout(null);
            }
            {
                this.setSize(330, 251);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buttonOKActionPerformed(ActionEvent evt) {

        if (newCommission) {

            Commission tempCommission = null;
            CommissionHandler tempCommissionHandler = null;

            try {
                // tempCommission = new Commission(0,
                // Double.parseDouble(txtPickupLocationX.getText()),
                // Double.parseDouble(txtPickupLocationY.getText()),
                // Double.parseDouble(txtPickupTime1.getText()),
                // Double.parseDouble(txtPickupTime2.getText()),
                // Double.parseDouble(txtDeliveryLocationX.getText()),
                // Double.parseDouble(txtDeliveryLocationY.getText()),
                // Double.parseDouble(txtDeliveryTime1.getText()),
                // Double.parseDouble(txtDeliveryTime2.getText()),
                // new Integer(txtLoad.getText()).intValue(),
                // new Integer(txtServiceTime.getText()).intValue());

                tempCommissionHandler = new CommissionHandler(tempCommission,
                        new Integer(txtIncomeTime.getText()).intValue());

                commissionsTab.addCommissionHandler(tempCommissionHandler);

            } catch (NumberFormatException e) {

                logger.error("GUI - Add Single Commission - Number Format Exception occured");
            }
        } else {

            try {
                commissionHandler.getCommission().setPickupX(
                        Double.parseDouble(txtPickupLocationX.getText()));
                commissionHandler.getCommission().setPickupY(
                        Double.parseDouble(txtPickupLocationY.getText()));
                commissionHandler.getCommission().setPickupTime1(
                        Double.parseDouble(txtPickupTime1.getText()));
                commissionHandler.getCommission().setPickupTime2(
                        Double.parseDouble(txtPickupTime2.getText()));
                commissionHandler.getCommission().setDeliveryX(
                        Double.parseDouble(txtDeliveryLocationX.getText()));
                commissionHandler.getCommission().setDeliveryY(
                        Double.parseDouble(txtDeliveryLocationY.getText()));
                commissionHandler.getCommission().setDeliveryTime1(
                        Double.parseDouble(txtDeliveryTime1.getText()));
                commissionHandler.getCommission().setDeliveryTime2(
                        Double.parseDouble(txtDeliveryTime2.getText()));
                commissionHandler.getCommission().setLoad(
                        new Integer(txtLoad.getText()).intValue());
                // commissionHandler.getCommission().setServiceTime(new
                // Integer(txtServiceTime.getText()).intValue());
                commissionHandler.setIncomeTime(new Integer(txtIncomeTime
                        .getText()).intValue());

                commissionsList.repaint();

            } catch (NumberFormatException e) {

                logger.error("GUI - Edit Single Commission - Number Format Exception occured"
                        + e);
            }
        }

        commissionsTab.refreshComsWaiting();

        this.dispose();
    }

    private void buttonCancelActionPerformed(ActionEvent evt) {

        this.dispose();
    }
}
