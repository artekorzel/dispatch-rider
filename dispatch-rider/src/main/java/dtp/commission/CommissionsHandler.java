package dtp.commission;

import algorithm.Schedule;
import dtp.jade.distributor.DistributorAgent;
import jade.core.AID;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.util.*;

public class CommissionsHandler {

    private static Logger logger = Logger.getLogger(CommissionsHandler.class);
    private static Map<AID, Point2D.Double> eunitsPosition = new HashMap<>();
    HashSet<CommissionHandler> commissions;
    int id;

    public CommissionsHandler() {

        commissions = new HashSet<>();
        id = 0;
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {

        // update commission's ID
        commissionHandler.getCommission().setID(id);
        // add com handler to the set
        commissions.add(commissionHandler);
        id++;

        logger.info("com added: incomingTime = " + commissionHandler.getIncomeTime());
        logger.info(commissionHandler.getCommission().toString());
    }

    public void removeCommissionHandler(CommissionHandler comHandler) {

        commissions.remove(comHandler);
    }

    public CommissionHandler[] getCommissionsBeforeTime(int time) {

        CommissionHandler[] coms = new CommissionHandler[1000];
        CommissionHandler[] comsNew;
        CommissionHandler tempCom;
        Iterator<CommissionHandler> iter = commissions.iterator();
        int count = 0;

        while (iter.hasNext()) {

            tempCom = iter.next();
            if (tempCom.getIncomeTime() <= time) {

                coms[count] = tempCom;
                count++;
            }
        }

        comsNew = new CommissionHandler[count];
        System.arraycopy(coms, 0, comsNew, 0, count);

        return comsNew;
    }

    private boolean hasEUnitMoved(AID aid, Point2D.Double currentLocation, boolean change) {
        if (eunitsPosition.get(aid).equals(currentLocation))
            return false;
        else {
            if (change)
                eunitsPosition.put(aid, currentLocation);
            return true;
        }
    }

    public boolean isAnyEUnitAtNode(boolean change) {
        Map<AID, Schedule> eunits = DistributorAgent.getEUnits();
        boolean result = false;

        if (eunits != null && eunits.size() > 0) {

            List<Point2D.Double> commissionsPositions = new LinkedList<>();

            for (AID aid : eunits.keySet()) {
                if (eunitsPosition.get(aid) == null)
                    eunitsPosition.put(aid, new Point2D.Double(0, 0));

                Schedule schedule = eunits.get(aid);
                for (Commission commission : schedule.getCommissions()) {
                    commissionsPositions.add(new Point2D.Double(commission.getPickupX(), commission.getPickupY()));
                    commissionsPositions.add(new Point2D.Double(commission.getDeliveryX(), commission.getDeliveryY()));
                }
                Point2D.Double currentLocation = schedule.getCurrentLocation();

                if (commissionsPositions.contains(currentLocation) && hasEUnitMoved(aid, currentLocation, change)) {
                    /*if(aid.getName().contains("EUnitAgent#0"))
                        System.err.println(timestamp + " " + change + " " + currentLocation);*/
                    result = true;
                }
                commissionsPositions = new LinkedList<>();
            }
        }
        return result;
    }

    public int getComsSize() {
        return commissions.size();
    }
}
