package dtp.gui;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.jade.gui.GUIAgent;
import dtp.simulation.SimInfo;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
public class CommissionsTab {

    private SimLogic gui;

    private GUIAgent guiAgent;

    private List<CommissionHandler> listCommissions;

    private double depotX;
    private double depotY;
    private double deadline = 1500;
    private double maxLoad = 200;

    public CommissionsTab(SimLogic gui, GUIAgent guiAgent) {
        this.gui = gui;
        this.guiAgent = guiAgent;
        listCommissions = new ArrayList<>();
    }

    public void setConstraintsTestMode() {
        setSimConstrains(depotX, depotY, deadline, maxLoad, true);
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

        for (int i = 0; i < commissions.length; i++) {
            addCommissionHandler(new CommissionHandler(commissions[i], incomeTime[i]));
        }

        gui.refreshComsWaiting();

        // set sim constraints read from .txt file
        Point2D.Double depot = TxtFileReader.getDepot(filename);
        depotX = depot.getX();
        depotY = depot.getY();
        deadline = TxtFileReader.getDeadline(filename);
        maxLoad = TxtFileReader.getTruckCapacity(filename);

        gui.displayMessage("GUI - simulation constrains " + "read form .txt file ["
                + filename + "]");
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        listCommissions.add(commissionHandler);
        guiAgent.addCommissionHandler(commissionHandler);
        gui.displayMessage("GUI - commission added " + commissionHandler.toString());
    }

    public void setSimConstrains(double depotX, double depotY, double deadline, double maxLoad, boolean testMode) {

        SimInfo simConstrains;

        simConstrains = new SimInfo(new Point2D.Double(depotX, depotY), deadline, maxLoad);
        gui.setSimInfo(simConstrains);
        this.depotX = depotX;
        this.depotY = depotY;
        this.deadline = deadline;
        this.maxLoad = maxLoad;

        if (!testMode) {
            setConstraints();
        }
    }

    public void setConstraints() {
        gui.displayMessage("GUI - constrains set: depot = (" + depotX + ", " + depotY + ") deadline = "
                + deadline + " capacity = " + maxLoad);
    }

    public int getCommissionsCount() {
        return listCommissions.size();
    }

    public int newCommissions() {
        int newCommissions = 0;

        for (CommissionHandler commission : listCommissions) {
            if (commission.getIncomeTime() == gui.getTimestamp())
                newCommissions++;
        }
        return newCommissions;
    }

    public void refreshComsWaiting() {

        gui.refreshComsWaiting();
    }

    public Point getDepotLocation() {
        return new Point((int) depotX, (int) depotY);
    }
}
