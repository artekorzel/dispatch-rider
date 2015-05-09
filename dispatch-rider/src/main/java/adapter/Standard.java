package adapter;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.simulation.SimInfo;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Standard implements Adapter {

    private static Logger logger = Logger.getLogger(Standard.class);
    private List<CommissionHandler> commissions = new LinkedList<>();
    private SimInfo simInfo;

    public Standard(String fileName) {
        Commission[] commissions = TxtFileReader.getCommissions(fileName);
        int incomeTime[] = TxtFileReader.getIncomeTimes(fileName + ".income_times", commissions.length);
        if (incomeTime == null) {
            logger.error("Brak pliku .income_times");
            return;
        }

        for (int i = 0; i < commissions.length; i++)
            this.commissions.add(new CommissionHandler(commissions[i], incomeTime[i]));

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
