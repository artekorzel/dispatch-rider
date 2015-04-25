package dtp.logic;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.jade.gui.GUIAgent;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CommissionsLogic {

    private SimLogic simLogic;
    private GUIAgent guiAgent;
    private List<CommissionHandler> listCommissions;

    private double depotX;
    private double depotY;
    private double deadline = 1500;
    private double maxLoad = 200;

    public CommissionsLogic(SimLogic simLogic, GUIAgent guiAgent) {
        this.simLogic = simLogic;
        this.guiAgent = guiAgent;
        listCommissions = new ArrayList<>();
    }

    public void setConstraintsTestMode() {
        setSimConstrains(depotX, depotY, deadline, maxLoad);
    }

    public void addCommissionGroup(String filename, boolean dynamic) {
        Commission[] commissions = TxtFileReader.getCommissions(filename.trim());

        int incomeTime[] = new int[commissions.length];
        if (dynamic) {
            incomeTime = TxtFileReader.getIncomeTimes(filename + ".income_times", commissions.length);
            if (incomeTime == null) {
                return;
            }
        }

        for (int i = 0; i < commissions.length; i++) {
            addCommissionHandler(new CommissionHandler(commissions[i], incomeTime[i]));
        }

        simLogic.refreshComsWaiting();

        // set sim constraints read from .txt file
        Point2D.Double depot = TxtFileReader.getDepot(filename);
        depotX = depot.getX();
        depotY = depot.getY();
        deadline = TxtFileReader.getDeadline(filename);
        maxLoad = TxtFileReader.getTruckCapacity(filename);
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        listCommissions.add(commissionHandler);
        guiAgent.addCommissionHandler(commissionHandler);
    }

    public void setSimConstrains(double depotX, double depotY, double deadline, double maxLoad) {
        SimInfo simConstrains = new SimInfo(new Point2D.Double(depotX, depotY), deadline, maxLoad);
        simLogic.setSimInfo(simConstrains);
        this.depotX = depotX;
        this.depotY = depotY;
        this.deadline = deadline;
        this.maxLoad = maxLoad;
    }

    public int getCommissionsCount() {
        return listCommissions.size();
    }

    public int newCommissions() {
        int newCommissions = 0;

        for (CommissionHandler commission : listCommissions) {
            if (commission.getIncomeTime() == simLogic.getTimestamp())
                newCommissions++;
        }
        return newCommissions;
    }
}
