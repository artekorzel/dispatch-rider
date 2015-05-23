package dtp.logic;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.jade.gui.TestConfiguration;
import dtp.jade.simulation.SimulationAgent;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CommissionsLogic {

    private SimLogic simLogic;
    private SimulationAgent simulationAgent;
    private List<CommissionHandler> listCommissions;

    private double depotX;
    private double depotY;
    private double deadline = 1500;
    private double maxLoad = 200;

    public CommissionsLogic(SimLogic simLogic, SimulationAgent simulationAgent) {
        this.simLogic = simLogic;
        this.simulationAgent = simulationAgent;
        listCommissions = new ArrayList<>();
    }

    public void setConstraintsTestMode() {
        setSimConstraints(depotX, depotY, deadline, maxLoad);
    }

    public void addCommissionGroup(TestConfiguration configuration) {

        Commission[] commissions = configuration.getCommissions();
        int[] incomeTime = configuration.getIncomeTime();
        if(incomeTime == null) {
             return;
        }

        for (int i = 0; i < commissions.length; i++) {
            addCommissionHandler(new CommissionHandler(commissions[i], incomeTime[i]));
        }

        simLogic.refreshComsWaiting();

        // set sim constraints read from .txt file
        Point2D.Double depot = configuration.getDepot();
        depotX = depot.getX();
        depotY = depot.getY();
        deadline = configuration.getDeadline();
        maxLoad = configuration.getMaxLoad();
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {
        listCommissions.add(commissionHandler);
        simulationAgent.addCommissionHandler(commissionHandler);
    }

    public void setSimConstraints(double depotX, double depotY, double deadline, double maxLoad) {
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
