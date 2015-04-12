package dtp.gui;

import dtp.jade.gui.GUIAgent;

import javax.swing.*;
import java.awt.*;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class GuiImpl extends SimLogic {

    // //////// GUI components //////////

    private JTabbedPane mainPane;

    {
        try {
            javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GuiImpl(GUIAgent agent) {

        super(agent);

        initGUI();

        displayMessage("Hello :)");
    }

    private void initGUI() {

        try {

            this.setTitle("Dispatch Rider");
            this.setSize(910, 640);

            {
                mainPane = new JTabbedPane();
                getContentPane().add(mainPane, BorderLayout.CENTER);
                mainPane.setPreferredSize(new java.awt.Dimension(471, 314));
                mainPane.setSize(591, 500);
                {
                    mainPane.addTab("Simmulation", null, simTab, null);
                    mainPane.addTab("Commissions", null, commissionsTab, null);
                    mainPane.addTab("Crisis Management", null, crisisTab, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
