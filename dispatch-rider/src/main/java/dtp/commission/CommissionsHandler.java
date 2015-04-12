package dtp.commission;

import algorithm.Schedule;
import dtp.jade.distributor.DistributorAgent;
import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.*;

public class CommissionsHandler {

    private static Map<AID, Point2D.Double> eunitsPosition = new HashMap<AID, Point2D.Double>();
    HashSet<CommissionHandler> commissions;
    int id;

    public CommissionsHandler() {

        commissions = new HashSet<CommissionHandler>();
        id = 0;
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {

        // update commission's ID
        commissionHandler.getCommission().setID(id);
        // add com handler to the set
        commissions.add(commissionHandler);
        id++;

        System.out.println("com added: incomingTime = " + commissionHandler.getIncomeTime());
        commissionHandler.getCommission().printCommision();
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
        for (int i = 0; i < count; i++)
            comsNew[i] = coms[i];

        return comsNew;
    }

    private boolean hasEunitMoved(AID aid, Point2D.Double currentLocation, boolean change, int timestamp) {
        if (eunitsPosition.get(aid).equals(currentLocation))
            return false;
        else {
            if (change)
                eunitsPosition.put(aid, currentLocation);
            return true;
        }
    }

    public boolean isAnyEUnitAtNode(int timestamp, boolean change) {
        Map<AID, Schedule> eunits = DistributorAgent.getEUnits();
        boolean result = false;

        if (eunits != null && eunits.size() > 0) {

            List<Point2D.Double> commissionsPositions = new LinkedList<Point2D.Double>();

            for (AID aid : eunits.keySet()) {
                if (eunitsPosition.get(aid) == null)
                    eunitsPosition.put(aid, new Point2D.Double(0, 0));

                Schedule schedule = eunits.get(aid);
                for (Commission commission : schedule.getCommissions()) {
                    commissionsPositions.add(new Point2D.Double(commission.getPickupX(), commission.getPickupY()));
                    commissionsPositions.add(new Point2D.Double(commission.getDeliveryX(), commission.getDeliveryY()));
                }
                Point2D.Double currentLocation = schedule.getCurrentLocation();

                if (commissionsPositions.contains(currentLocation) && hasEunitMoved(aid, currentLocation, change, timestamp)) {
                    /*if(aid.getName().contains("EUnitAgent#0"))
	    				System.err.println(timestamp + " " + change + " " + currentLocation);*/
                    result = true;
                }
                commissionsPositions = new LinkedList<Point2D.Double>();
            }
        }
        return result;
    }

    public CommissionHandler[] getCommissionHandlers() {

        CommissionHandler[] coms;

        coms = new CommissionHandler[commissions.size()];

        return commissions.toArray(coms);
    }

    public int getComsSize() {

        return commissions.size();
    }
}
