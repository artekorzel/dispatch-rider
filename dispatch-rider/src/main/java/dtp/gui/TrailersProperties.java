package dtp.gui;

import dtp.jade.transport.TransportElementInitialDataTrailer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TrailersProperties extends JFrame {

    public TrailersProperties(SimLogic gui) {
        super("Trailer properties");

        List<TransportElementInitialDataTrailer> trailerProperties = gui.getTrailersProperties();

        String data[][] = new String[trailerProperties.size()][5];
        int index = 0;

        for (TransportElementInitialDataTrailer trailerData : trailerProperties) {
            data[index][0] = String.valueOf(trailerData.getMass());
            data[index][1] = String.valueOf(trailerData.getCapacity_());
            data[index][2] = String.valueOf(trailerData.getCargoType());
            data[index][3] = String.valueOf(trailerData.getUniversality());
            data[index][4] = String.valueOf(trailerData.getConnectorType());
            index++;
        }

        String fields[] = {"Mass [kg]", "Capacity [int]", "Cargo type [int]", "Universality [1-4]", "Connector type [int]"};

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
