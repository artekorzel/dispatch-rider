package dtp.visualisation;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class InfoPopup extends JFrame {


    private JPanel popupPanel;
    private JButton closeButton;
    private JScrollPane choiceScrollPane;
    private JList choiceList;
    private JTextPane popupTextPane;

    // private Graph aGraph;

    // public InfoPopup(Graph aGraph, HolonInfo holon) {
    public InfoPopup(Graph aGraph) {

        initGUI();
        /*
         * choiceList.setModel(new DefaultComboBoxModel(new String[] { holon .getTrack().getFirst().getName() + " - " +
         * holon.getTrack().getLast().getName() }));
         *//*
            * String text=""; text += "kierowcy: \n"; Iterator<DriverInfo> dit = holon.getDriverIterator();
            * if(!dit.hasNext()) text += "brak\n"; while(dit.hasNext()){ DriverInfo di = dit.next(); text += " "
            * +di.getName() + " " + di.getSurname() + " (id: " + di.getId() + ")\n"; } text += "ciagnik: ";
            * if(holon.getTruck() == null) text += "brak\n"; else text += "id:" + holon.getTruck().getId(); text +=
            * "naczepa: "; if(holon.getSemitrailer() == null) text += "brak\n"; else text += "id:" +
            * holon.getSemitrailer().getId();
            * 
            * text += "przesylki: \n"; Iterator<CargoInfo> cit = holon.getParcelIterator(); if(!cit.hasNext()) text +=
            * "brak \n"; while(cit.hasNext()){ CargoInfo ci = cit.next(); text += "\"" + ci.getName() + "\" z: " +
            * ci.getFrom().getName() + " do: " + ci.getTo().getName(); }
            * 
            * text += "trasa: \n"; Track aTrack = holon.getTrack(); int numberOfPoints; if(aTrack==null) text +=
            * " brak\n"; else{ numberOfPoints = aTrack.getNumberOfPoints(); for(int i=0 ; i<numberOfPoints ; i++) text
            * += " " + aTrack.get(i).getName() + "\n"; }
            */
        // popupTextPane.setText(text);
    }

    public InfoPopup(Graph aGraph, GraphLink ln) {
        initGUI();
        choiceList.setModel(new DefaultComboBoxModel(new String[]{
                ln.getStartPoint().getName() + " - " + ln.getEndPoint().getName(),
                ln.getEndPoint().getName() + " - " + ln.getStartPoint().getName()}));
        setInfoText(ln);
    }

    public InfoPopup(Graph aGraph, GraphPoint pt) {
        initGUI();
        choiceList.setModel(new DefaultComboBoxModel(new String[]{pt.getName()}));
        String text = "punkt: " + pt.getName() + " [" + pt.getX() + ";" + pt.getY() + "]\n";
        popupTextPane.setText(text);
    }

    private void setInfoText(GraphLink ln) {
        String text = "";
        text += "��cze: \n";
        text += ln.getStartPoint().getName() + " <-> " + ln.getEndPoint().getName() + "\n";
        text += "koszt:" + ln.getCost() + "\n";

        popupTextPane.setText(text);
    }

    private void initGUI() {
        try {
            {
                this.setPreferredSize(new java.awt.Dimension(641, 382));
                this.setTitle("Szczegoly");
                this.setResizable(false);
                {
                    popupPanel = new JPanel();
                    getContentPane().add(popupPanel, BorderLayout.CENTER);
                    popupPanel.setSize(300, 150);
                    popupPanel.setPreferredSize(new java.awt.Dimension(630, 357));
                    popupPanel.setLayout(null);
                    {
                        choiceScrollPane = new JScrollPane();
                        popupPanel.add(choiceScrollPane);
                        choiceScrollPane.setBounds(7, 14, 217, 294);
                        {

                            // ListModel choiceListModel = new
                            // DefaultComboBoxModel(
                            // new String[] { "Item One", "Item Two" });
                            choiceList = new JList();
                            choiceScrollPane.setViewportView(choiceList);
                            // choiceList.setModel(choiceListModel);
                            choiceList.setBounds(122, 53, 214, 298);
                            choiceList.setPreferredSize(new java.awt.Dimension(217, 294));
                            choiceList.addListSelectionListener(new ListSelectionListener() {

                                public void valueChanged(ListSelectionEvent evt) {
                                    choiceListValueChanged(evt);
                                }
                            });
                        }
                    }
                    {
                        popupTextPane = new JTextPane();
                        popupPanel.add(popupTextPane);
                        popupTextPane.setText("");
                        popupTextPane.setEditable(false);
                        popupTextPane.setDragEnabled(true);
                        popupTextPane.setBounds(231, 14, 392, 294);
                    }
                    {
                        closeButton = new JButton();
                        popupPanel.add(closeButton);
                        closeButton.setText("Zamknij");
                        closeButton.setBounds(525, 322, 98, 21);
                        closeButton.addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent evt) {
                                closeButtonMouseClicked(evt);
                            }
                        });
                    }
                }
                this.setSize(641, 382);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeButtonMouseClicked(MouseEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void choiceListValueChanged(ListSelectionEvent evt) {
        // System.out.println("choiceList.valueChanged, event src=" +
        // choiceList.getSelectedValue().toString());
        // setInfoText(ln);
    }
}
