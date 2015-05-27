package adapter;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Standard implements Adapter {

    private List<CommissionHandler> commissions = new LinkedList<>();
    private SimInfo simInfo;

    public Standard(String fileName) {
        Commission[] commissions = TxtFileReader.getCommissions(fileName);
        int incomeTime[] = TxtFileReader.getIncomeTimes(fileName + ".income_times", commissions.length);

        for (int i = 0; i < commissions.length; i++)
            if (incomeTime == null) {
                this.commissions.add(new CommissionHandler(commissions[i], 0));
            } else {
                this.commissions.add(new CommissionHandler(commissions[i], incomeTime[i]));
            }

        int depotX = (int) TxtFileReader.getDepot(fileName).getX();
        int depotY = (int) TxtFileReader.getDepot(fileName).getY();

        double deadline = TxtFileReader.getDeadline(fileName);
        Point2D.Double depot = new Point2D.Double(depotX, depotY);
        double maxLoad = TxtFileReader.getTruckCapacity(fileName);

        simInfo = new SimInfo(depot, deadline, maxLoad);
    }

    public List<CommissionHandler> readCommissions() {
        return commissions;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }
}
