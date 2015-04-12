package dtp.jade.gui;

import dtp.commission.CommissionsHandler;
import dtp.gui.GuiImpl;
import dtp.jade.CommunicationHelper;
import dtp.jade.gui.behaviour.*;
import dtp.jade.test.behaviour.GetTransportAgentConfirmationBehaviour;
import dtp.jade.test.behaviour.GetTransportAgentCreatedBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class GUIAgentImpl extends GUIAgent {


    private int transportAgentsCreated;
    private int level;
    private int transportAgentsCount;

    protected void setup() {

        logger.info(this.getLocalName() + " - Hello World!");

        /* -------- INITIALIZATION SECTION ------- */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        /* -------- SERVICES SECTION ------- */
        registerServices();

        /* -------- BEHAVIOURS SECTION ------- */
        this.addBehaviour(new GetSimInfoRequestBehaviour(this));
        this.addBehaviour(new GetMessageBehaviour(this));
        this.addBehaviour(new GetCalendarBehaviour(this));
        this.addBehaviour(new GetCalendarStatsBehaviour(this));
        this.addBehaviour(new GetEUnitInfoBehaviour(this));
        this.addBehaviour(new GetNooneListBehaviour(this));
        this.addBehaviour(new GetGraphUpdateBehaviour(this));
        this.addBehaviour(new GetCalenderStatsToFileBehaviour(this));
        this.addBehaviour(new SimInfoReceivedBehaviour(this));
        this.addBehaviour(new GetTransportAgentCreatedBehaviour(this));
        this.addBehaviour(new GetConfirmOfTimeStampBehaviour(this));
        this.addBehaviour(new GetTransportAgentConfirmationBehaviour(this));
        addBehaviour(new GetTransportAgentConfirmationBehaviour(this));
        addBehaviour(new GetSimulationDataBehaviour(this));
        this.addBehaviour(new GetUndeliveredCommissionBehaviour(this));

        /* -------- INTERFACE CREATION SECTION ------- */

        gui = new GuiImpl(this);
        /* -------- TIME TASK PERFORMER SECTION ------- */
        timerTaskPerformer = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {

                gui.nextSimStep();

                // w simGOD timer startowany jest zeby zapisac statystyki, zaraz
                // potem tzreba go zatrzynamc
            }
        };
        timerDelay = 200;
        timer = new Timer(timerDelay, timerTaskPerformer);
        /* -------- COMMISSIONS HANDLER SECTION ------- */
        commissionsHandler = new CommissionsHandler();
        try {
            FileReader fr = new FileReader(new File("simulationConfig" + File.separator + "holonic.properties"));
            BufferedReader br = new BufferedReader(fr);
            String initialC = br.readLine();
            @SuppressWarnings("unused")
            int initialCap = Integer.parseInt(initialC.substring(initialC.indexOf("=") + 1));
            String mod = br.readLine();
            @SuppressWarnings("unused")
            String mode = mod.substring(mod.indexOf("=") + 1);
            String dep = br.readLine();
            @SuppressWarnings("unused")
            int depots = Integer.parseInt(dep.substring(dep.indexOf("=") + 1));
            String eUnits = br.readLine();
            @SuppressWarnings("unused")
            int eUnitsCount = Integer.parseInt(eUnits.substring(eUnits.indexOf("=") + 1));
            /* -------- EUNITS CREATION SECTION ------- */
            /*for (int i = 0; i < eUnitsCount; i++) {
                EUnitInitialData data = new EUnitInitialData(HolonCreationAuction.FULL,0, HolonReorganizeAuction.FULL,0);
                data.setDepot(0);
                createNewEUnit(data);
            }*/

            try {
                transportAgentsCreated = 0;
                level = 1;
                agentsCount = loadDriversProperties("simulationConfig" + File.separator + "drivers.properties");
            } catch (FileNotFoundException e) {
                logger.fatal("properties file not found", e);

            } catch (IOException e) {
                logger.fatal("reading properties file failed", e);
            }

        } catch (FileNotFoundException e) {
            logger.fatal("properties file not found", e);

        } catch (IOException e) {
            logger.fatal("reading properties file failed", e);
        }

    }

    public synchronized void transportAgentCreated() {
        transportAgentsCreated++;
        if (transportAgentsCreated == agentsCount) {
            switch (level) {
                case 1:
                    level = 2;
                    try {
                        transportAgentsCreated = 0;
                        agentsCount = loadTrailersProperties("simulationConfig" + File.separator + "trailers.properties");
                    } catch (IOException e) {
                        logger.fatal("reading properties file failed", e);
                    }
                    break;
                case 2:
                    try {
                        level = 3;
                        transportAgentsCreated = 0;
                        agentsCount = loadTrucksProperties("simulationConfig" + File.separator + "trucks.properties");

                    } catch (IOException e) {
                        logger.fatal("reading properties file failed", e);
                    }
                    break;
                case 3:
                    level = 0;
                    next();
                    break;
            }
        }
    }

    private void next() {
        AID[] aids = CommunicationHelper.findAgentByServiceName(this, "AgentCreationService");
        transportAgentsCount = CommunicationHelper.findAgentByServiceName(this, "TransportUnitService").length;
        if (aids.length == 1) {

            ACLMessage cfp = new ACLMessage(CommunicationHelper.AGENTS_DATA);
            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject("");
            } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
            }
            send(cfp);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public synchronized void transportAgentConfirmationOfReceivingAgentsData() {
        transportAgentsCount--;
        if (transportAgentsCount == -1) {
            gui.setVisible(true);

        }
    }
}
