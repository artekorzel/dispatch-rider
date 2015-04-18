package dtp.gui;

import dtp.jade.transport.TransportElementInitialDataTruck;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TruckProperties extends JFrame {

    public TruckProperties(SimLogic gui) {
        super("Truck properties");

        List<TransportElementInitialDataTruck> truckProperties = gui.getTrucksProperties();

        String data[][] = new String[truckProperties.size()][5];
        int index = 0;
        for (TransportElementInitialDataTruck truckData : truckProperties) {
            data[index][0] = String.valueOf(truckData.getPower());
            data[index][1] = String.valueOf(truckData.getReliability());
            data[index][2] = String.valueOf(truckData.getComfort());
            data[index][3] = String.valueOf(truckData.getFuelConsumption());
            data[index][4] = String.valueOf(truckData.getConnectorType());
            index++;
        }

        String fields[] = {"Power [BHP]", "Reliability [1-4]", "Comfort [1-4]", "Fuel consumption [l/100km]", "Connector type [int]"};

        JTable jTable = new JTable(data, fields);
        JScrollPane pane = new JScrollPane(jTable);
        getContentPane().add(pane);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
        setSize(800, 600);
        pack();
        setVisible(true);
    }

}
