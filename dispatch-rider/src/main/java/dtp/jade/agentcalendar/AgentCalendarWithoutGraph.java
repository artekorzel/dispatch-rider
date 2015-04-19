package dtp.jade.agentcalendar;

import dtp.commission.Commission;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.List;

// TODO nie jest do konca uwzgledniany aspekt "NOW" (timestamp)

public class AgentCalendarWithoutGraph implements AgentCalendar {

    private static Logger logger = Logger.getLogger(AgentCalendarWithoutGraph.class);

    private Hashtable commissions;
    private ActionQueueWithoutGraph schedule;
    private Point2D.Double depot;
    private double maxLoad;

    public AgentCalendarWithoutGraph() {

    }

    public synchronized List<? extends CalendarAction> getSchedule() {
        return schedule;
    }

    public void setSchedule(ActionQueueWithoutGraph schedule) {
        this.schedule = schedule;
    }

    public void setCommissions(Hashtable commissions) {
        this.commissions = commissions;
    }

    public void setDepot(Point2D.Double depot) {
        this.depot = depot;
    }

    public synchronized double addCommission(Commission com, int timestamp) {
        ActionQueueWithoutGraph backupSchedulePickup;
        ActionQueueWithoutGraph backupScheduleDelivery;

        CalendarActionWithoutGraph curAction;
        CalendarActionWithoutGraph nextPDDAction;

        CalendarActionWithoutGraph backupCurActionPickup;
        CalendarActionWithoutGraph backupCurActionDelivery;
        CalendarActionWithoutGraph backupPickupAction;

        // load
        CalendarActionWithoutGraph pickupAction;
        CalendarActionWithoutGraph deliveryAction;

        ActionQueueWithoutGraph minSchedule;
        double minExtraDistance;
        double originalDistance;
        double loadDifference = 0;

        minSchedule = null;
        minExtraDistance = Double.MAX_VALUE;
        originalDistance = calculateWholeDistance();

        curAction = schedule.getLast();
        nextPDDAction = getNextPDDAction(curAction);

        do {

            int tmpIndex = schedule.indexOf(curAction);
            backupSchedulePickup = schedule.backup();
            backupCurActionPickup = backupSchedulePickup
                    .get(tmpIndex);

            boolean pickup = true;
            // pickupAction - zwraca stworzona akcje PICKUP
            pickupAction = putCom(com, pickup, curAction, nextPDDAction,
                    timestamp);

            if (pickupAction == null) {

                elbowLeft(curAction, timestamp);
                elbowRight(curAction);
                pickupAction = putCom(com, pickup, curAction, nextPDDAction,
                        timestamp);
            }

            // PICKUP OK
            if (pickupAction != null) {

                addComToHashtable(com);

                backupPickupAction = pickupAction.clone();

                curAction = getNextPDDAction(curAction); // new PICKUP

                do {

                    tmpIndex = schedule.indexOf(curAction);
                    backupScheduleDelivery = schedule.backup();
                    backupCurActionDelivery = backupScheduleDelivery
                            .get(tmpIndex);

                    pickup = false;
                    deliveryAction = putCom(com, pickup, curAction,
                            nextPDDAction, timestamp);

                    if (deliveryAction == null) {

                        elbowLeft(curAction, timestamp);
                        elbowRight(curAction);
                        deliveryAction = putCom(com, pickup, curAction,
                                nextPDDAction, timestamp);
                    }

                    if (deliveryAction != null) {

                        // update load
                        double loadOk = addLoad(com, pickupAction,
                                deliveryAction);

                        if (loadOk <= 0) {

                            // calculate extra distance
                            double extraDistance = calculateWholeDistance()
                                    - originalDistance;

                            // save schedule (if better)
                            if (extraDistance < minExtraDistance) {

                                minExtraDistance = extraDistance;
                                minSchedule = schedule.backup();
                            }
                        } else {
                            loadDifference = loadOk;
                            System.out
                                    .println("AgentCalendar -> LOAD NOT OK >> "
                                            + loadDifference);
                        }

                        schedule = backupScheduleDelivery;
                        curAction = backupCurActionDelivery;
                        pickupAction = backupPickupAction;
                    }

                    curAction = getNextPDDAction(curAction);
                    nextPDDAction = getNextPDDAction(curAction);

                } while (nextPDDAction != null); // END do DELIVERY

                schedule = backupSchedulePickup;
                curAction = backupCurActionPickup;
            }

            curAction = getNextPDDAction(curAction);
            nextPDDAction = getNextPDDAction(curAction);

        } while (nextPDDAction != null); // END do PICKUP

        if (minSchedule != null) {

            schedule = minSchedule;
            optimizeSchedule(timestamp);

            return -1;

        } else {

            removeComFromHashtable(String.valueOf(com.getID()));

            return loadDifference;
        }
    }

    public double getDistance() {
        return calculateWholeDistance();
    }

    @Override
    public String toString() {

        CalendarActionWithoutGraph tmpAction;
        StringBuilder str;

        str = new StringBuilder();

        str.append("-----------------------------------------------\n");

        for (int i = schedule.size() - 1; i >= 0; i--) {

            tmpAction = schedule.get(i);
            str.append(tmpAction.toString());
        }

        str.append("-----------------------------------------------\n");
        str.append("distance = ").append(getDistance()).append("\n");
        str.append("-----------------------------------------------\n");

        return str.toString();
    }

    @Override
    public AgentCalendarWithoutGraph clone() {

        AgentCalendarWithoutGraph tmpAgentCalendar = new AgentCalendarWithoutGraph();

        tmpAgentCalendar.setCommissions((Hashtable) commissions.clone());
        tmpAgentCalendar.setSchedule(schedule.backup());
        tmpAgentCalendar.setDepot((Point2D.Double) depot.clone());
        tmpAgentCalendar.setMaxLoad(maxLoad);

        return tmpAgentCalendar;
    }

    // sprawdz czy mozna zrobic pickup/delivery
    // pomiedzy prevAction, a nextAction
    // prevAction i nextAction to PICKUP, DELIVERY lub DEPOT
    // zwraca najwczesniejszy czas w jakim moze rozpoczac sie zlecenie
    // (uwzglednia czasy dojazdu)
    private double comFits(Commission com, boolean isPickup,
                           CalendarActionWithoutGraph prevPDDAction,
                           CalendarActionWithoutGraph nextPDDAction, int timestamp) {

        double timeDriveFromPrev;
        double timeDriveToNext;
        double timeService;
        double timeTotal;

        if (!prevPDDAction.isPDD()) {

            System.out
                    .println("AgentCalendar.comFits -> !prevActionPDD.isPDD()");
        }

        if (!nextPDDAction.isPDD()) {

            System.out
                    .println("AgentCalendar.comFits -> !nextActionPDD.isPDD()");
        }

        // dla PICKUP, DELIVERY i DEPOT source location = destination location
        if (isPickup) {

            timeDriveFromPrev = calculateTime(prevPDDAction.getSource(),
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));
            timeDriveToNext = calculateTime(new Point2D.Double(
                            com.getPickupX(), com.getPickupY()),
                    nextPDDAction.getSource());
            timeService = com.getPickUpServiceTime();
        } else {

            timeDriveFromPrev = calculateTime(prevPDDAction.getSource(),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            timeDriveToNext = calculateTime(
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()),
                    nextPDDAction.getSource());
            timeService = com.getDeliveryServiceTime();
        }

        timeTotal = timeDriveFromPrev + timeService + timeDriveToNext;

        // ograniczenie czasow dojazdu
        if (timeTotal > nextPDDAction.getStartTime()
                - prevPDDAction.getEndTime()) {

            return -1;
        }

        // wyznaczenie najwczesniejszego czasu w jakim moze rozpoczac sie
        // pickup/delivery
        double actionTime;
        double earliestTime = prevPDDAction.getEndTime() + timeDriveFromPrev;
        double latestTime = nextPDDAction.getStartTime() - timeService
                - timeDriveToNext;

        // uwzglednij timestamp
        if (earliestTime - timeDriveFromPrev < timestamp) {

            earliestTime = earliestTime + timestamp
                    - (earliestTime - timeDriveFromPrev);
        }

        // ograniczenie okien czasowych
        double timeWindow1 = 0;
        double timeWindow2 = 0;
        if (isPickup) {

            timeWindow1 = com.getPickupTime1();
            timeWindow2 = com.getPickupTime2();

        } else {

            timeWindow1 = com.getDeliveryTime1();
            timeWindow2 = com.getDeliveryTime2();

        }

        if (latestTime < earliestTime) {

            return -1;

        } else if (latestTime < timeWindow1) {

            return -1;

        } else if (earliestTime < timeWindow1 && latestTime >= timeWindow1) {

            actionTime = timeWindow1;

        } else if (earliestTime >= timeWindow1 && latestTime <= timeWindow2) {

            actionTime = earliestTime;

        } else if (earliestTime <= timeWindow2 && latestTime >= timeWindow2) {

            actionTime = earliestTime;

        } else if (earliestTime > timeWindow2) {

            return -1;

        } else if (earliestTime < timeWindow1 && latestTime > timeWindow2) {

            actionTime = timeWindow1;

        } else {

            return -1;
        }

        return actionTime;
    }

    // wklada zlecenie pomiedzy prevActionPDD a nextActionPDD
    // przy zalozeniu, ze obie sa PDD
    private CalendarActionWithoutGraph putCom(Commission com, boolean isPickup,
                                              CalendarActionWithoutGraph prevPDDAction,
                                              CalendarActionWithoutGraph nextPDDAction, int timestamp) {

        CalendarActionWithoutGraph createdPDAction;

        // czas kiedy moze rozpoczac sie PICKUP lub DELIVERY przy zalozeniu, ze
        // DRIVE rozpocznie sie teraz
        double PDTime = comFits(com, isPickup, prevPDDAction, nextPDDAction,
                timestamp);
        double time;

        if (PDTime < 0) {

            return null;
        }

        // add DRIVE action
        CalendarActionWithoutGraph newDriveToAction;
        double earliestDriveStartTime;

        if (isPickup) {

            time = calculateTime(prevPDDAction.getDestination(),
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));

            // if (timestamp <= prevPDDAction.getEndTime())
            // earliestDriveStartTime = prevPDDAction.getEndTime();
            // else
            // earliestDriveStartTime = PDTime - time;

            earliestDriveStartTime = Math.max(prevPDDAction.getEndTime(),
                    timestamp);

            newDriveToAction = new CalendarActionWithoutGraph(-1, -1, "DRIVE",
                    prevPDDAction.getDestination(), new Point2D.Double(
                    com.getPickupX(), com.getPickupY()),
                    earliestDriveStartTime, earliestDriveStartTime + time,
                    prevPDDAction.getCurrentLoad());

        } else {

            time = calculateTime(prevPDDAction.getDestination(),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));

            // if (timestamp <= prevPDDAction.getEndTime())
            // earliestDriveStartTime = prevPDDAction.getEndTime();
            // else
            // earliestDriveStartTime = PDTime - time;

            earliestDriveStartTime = Math.max(prevPDDAction.getEndTime(),
                    timestamp);

            newDriveToAction = new CalendarActionWithoutGraph(-1, -1, "DRIVE",
                    prevPDDAction.getDestination(), new Point2D.Double(
                    com.getDeliveryX(), com.getDeliveryY()),
                    earliestDriveStartTime, earliestDriveStartTime + time,
                    prevPDDAction.getCurrentLoad());
        }

        schedule.putActionAfter(newDriveToAction, prevPDDAction);

        // add WAIT action
        CalendarActionWithoutGraph newWaitBeforeAction;

        if (isPickup) {

            newWaitBeforeAction = new CalendarActionWithoutGraph(-1, -1,
                    "WAIT", new Point2D.Double(com.getPickupX(),
                    com.getPickupY()), new Point2D.Double(
                    com.getPickupX(), com.getPickupY()),
                    newDriveToAction.getEndTime(), PDTime,
                    newDriveToAction.getCurrentLoad());
        } else {

            newWaitBeforeAction = new CalendarActionWithoutGraph(-1, -1,
                    "WAIT", new Point2D.Double(com.getDeliveryX(),
                    com.getDeliveryY()), new Point2D.Double(
                    com.getDeliveryX(), com.getDeliveryY()),
                    newDriveToAction.getEndTime(), PDTime,
                    newDriveToAction.getCurrentLoad());
        }

        schedule.putActionAfter(newWaitBeforeAction, newDriveToAction);

        // add PICKUP/DELIVERY action
        CalendarActionWithoutGraph newPDAction = null;

        if (isPickup) {

            newPDAction = new CalendarActionWithoutGraph(com.getID(),
                    com.getPickUpId(), "PICKUP", new Point2D.Double(
                    com.getPickupX(), com.getPickupY()),
                    new Point2D.Double(com.getPickupX(), com.getPickupY()),
                    PDTime, PDTime + com.getPickUpServiceTime(),
                    newDriveToAction.getCurrentLoad());

            createdPDAction = newPDAction;

            schedule.putActionAfter(newPDAction, newWaitBeforeAction);

        } else {

            newPDAction = new CalendarActionWithoutGraph(com.getID(),
                    com.getDeliveryId(), "DELIVERY", new Point2D.Double(
                    com.getDeliveryX(), com.getDeliveryY()),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()),
                    PDTime, PDTime + com.getDeliveryServiceTime(),
                    newDriveToAction.getCurrentLoad());

            createdPDAction = newPDAction;

            schedule.putActionAfter(newPDAction, newWaitBeforeAction);
        }

        double oldDriveLoad;
        double oldWaitLoad;

        // wymien DRIVE i WAIT na nowe
        // usun DRIVE
        oldDriveLoad = schedule.getNextAction(newPDAction).getCurrentLoad();
        schedule.removeAction(schedule.getNextAction(newPDAction));
        // usun WAIT
        oldWaitLoad = schedule.getNextAction(newPDAction).getCurrentLoad();
        schedule.removeAction(schedule.getNextAction(newPDAction));

        CalendarActionWithoutGraph nextAction = schedule
                .getNextAction(newPDAction);

        time = calculateTime(newPDAction.getDestination(),
                nextAction.getSource());

        CalendarActionWithoutGraph newDriveNextAction = new CalendarActionWithoutGraph(
                -1, -1, "DRIVE", newPDAction.getDestination(),
                nextAction.getSource(), newPDAction.getEndTime(),
                newPDAction.getEndTime() + time, oldDriveLoad);

        schedule.putActionAfter(newDriveNextAction, newPDAction);

        CalendarActionWithoutGraph newWaitBeforeNextAction = new CalendarActionWithoutGraph(
                -1, -1, "WAIT", nextAction.getSource(), nextAction.getSource(),
                newDriveNextAction.getEndTime(), nextAction.getStartTime(),
                oldWaitLoad);

        schedule.putActionAfter(newWaitBeforeNextAction, newDriveNextAction);

        return createdPDAction;
    }

    // rozepchnij w lewo wszystkie zlecenia przed curAction
    // (lacznie z curAction)
    private void elbowLeft(CalendarActionWithoutGraph curAction, int timestamp) {

        // current PD action to elbow left
        CalendarActionWithoutGraph tmpPDDAction;
        CalendarActionWithoutGraph tmpPDDActionNext;
        CalendarActionWithoutGraph tmpPDDActionPrev;

        Commission tmpCom;

        double timeDriveFromPrev;
        double timeDriveToNext;
        double timeService;

        double earliestPossibleTime;

        if (schedule.indexOf(curAction) >= schedule.size() - 1)
            return;

        // first DEPOT
        tmpPDDActionPrev = schedule.getLast();
        // first PD
        tmpPDDAction = getNextPDDAction(tmpPDDActionPrev);
        tmpPDDActionNext = getNextPDDAction(tmpPDDAction);

        // jezeli w schedule sa tylko akcje DEPOT
        // nie mozna ich rozpychac
        if (tmpPDDActionNext == null) {

            return;
        }

        while (!tmpPDDActionPrev.equals(curAction)) {

            // dla PICKUP, DELIVERY i DEPOT source location = destination
            // location
            timeDriveFromPrev = calculateTime(tmpPDDActionPrev.getSource(),
                    tmpPDDAction.getSource());
            timeDriveToNext = calculateTime(tmpPDDAction.getSource(),
                    tmpPDDActionNext.getSource());

            timeService = tmpPDDAction.getEndTime()
                    - tmpPDDAction.getStartTime();

            // wyznaczenie najwczesniejszego i najpozniejszego czasu w jakim
            // moze rozpoczac sie pickup/delivery
            double earliestTime;

            if (timestamp > tmpPDDActionPrev.getEndTime()) {

                // TODO timeDriveFromPrev trzeba zamienic na czas dojazdu z
                // biezacej lokalizacji do miejsca PICKUP/DELIVERY
                earliestTime = timestamp + timeDriveFromPrev;
            } else {
                earliestTime = tmpPDDActionPrev.getEndTime()
                        + timeDriveFromPrev;
            }

            double latestTime = tmpPDDActionNext.getStartTime() - timeService
                    - timeDriveToNext;

            // ograniczenie okien czasowych
            tmpCom = getComFromHashtable(String.valueOf(tmpPDDAction
                    .getCommissionID()));
            double timeWindow1 = 0;
            double timeWindow2 = 0;
            switch (tmpPDDAction.getType()) {
                case "PICKUP":
                    timeWindow1 = tmpCom.getPickupTime1();
                    timeWindow2 = tmpCom.getPickupTime2();
                    break;
                case "DELIVERY":
                    timeWindow1 = tmpCom.getDeliveryTime1();
                    timeWindow2 = tmpCom.getDeliveryTime2();
                    break;
                default:
                    logger.warn("AgentCalendar.elbowLeft() -> sth wrong!");
                    break;
            }

            earliestPossibleTime = Double.MAX_VALUE;

            if (latestTime < earliestTime || earliestTime > timeWindow2) {
                return;
            } else if (earliestTime >= timeWindow1) {
                earliestPossibleTime = earliestTime;
            } else if (earliestTime < timeWindow1) {
                if (latestTime >= timeWindow1) {
                    earliestPossibleTime = timeWindow1;
                } else {
                    return;
                }
            } else {
                logger.warn("AgentCalendar.elbowLeft -> sth wrong!");
            }

            double extraTime = tmpPDDAction.getStartTime()
                    - earliestPossibleTime;

            if (extraTime > 0) {

                CalendarActionWithoutGraph tmpPrevWaitAction;
                CalendarActionWithoutGraph tmpNextDriveAction;
                CalendarActionWithoutGraph tmpNextWaitAction;

                // update tmpAction (PICKUP or DELIVERY)
                tmpPDDAction.setStartTime(earliestPossibleTime);
                tmpPDDAction.setEndTime(earliestPossibleTime + timeService);

                // update previous action (WAIT)
                tmpPrevWaitAction = schedule.getPreviousAction(tmpPDDAction);
                tmpPrevWaitAction.setEndTime(tmpPrevWaitAction.getEndTime()
                        - extraTime);

                // update next action (DRIVE)
                tmpNextDriveAction = schedule.getNextAction(tmpPDDAction);
                tmpNextDriveAction.setStartTime(tmpNextDriveAction
                        .getStartTime() - extraTime);
                tmpNextDriveAction.setEndTime(tmpNextDriveAction.getEndTime()
                        - extraTime);

                // update action after next action :) (WAIT)
                tmpNextWaitAction = schedule.getNextAction(tmpNextDriveAction);
                tmpNextWaitAction.setStartTime(tmpNextWaitAction.getStartTime()
                        - extraTime);
            }

            tmpPDDActionPrev = tmpPDDAction;
            tmpPDDAction = tmpPDDActionNext;
            tmpPDDActionNext = getNextPDDAction(tmpPDDActionNext);
        }
    }

    // rozepchnij w prawo wszystkie zlecenia po curAction
    private void elbowRight(CalendarActionWithoutGraph curAction) {

        // current PD action to elbow right
        CalendarActionWithoutGraph tmpPDDAction;
        CalendarActionWithoutGraph tmpPDDActionNext;
        CalendarActionWithoutGraph tmpPDDActionPrev;

        Commission tmpCom;

        double timeDriveFromPrev;
        double timeDriveToNext;
        double timeService;
        double timeTotal;

        double latestPossibleTime;

        if (schedule.indexOf(curAction) <= 1)
            return;

        // last DEPOT
        tmpPDDActionNext = schedule.getFirst();
        // last PD
        tmpPDDAction = getPrevPDDAction(tmpPDDActionNext);
        tmpPDDActionPrev = getPrevPDDAction(tmpPDDAction);

        // w schedule sa tylko akcje DEPOT
        // nie mozna ich rozpychac
        if (tmpPDDActionPrev == null) {

            return;
        }

        while (!tmpPDDAction.equals(curAction)) {

            // dla PICKUP, DELIVERY i DEPOT source location = destination
            // location
            timeDriveFromPrev = calculateTime(tmpPDDActionPrev.getSource(),
                    tmpPDDAction.getSource());
            timeDriveToNext = calculateTime(tmpPDDAction.getSource(),
                    tmpPDDActionNext.getSource());

            timeService = tmpPDDAction.getEndTime()
                    - tmpPDDAction.getStartTime();

            // wyznaczenie najwczesniejszego i najpozniejszego czasu w jakim
            // moze rozpoczac sie pickup/delivery
            double earliestTime = tmpPDDActionPrev.getEndTime()
                    + timeDriveFromPrev;
            double latestTime = tmpPDDActionNext.getStartTime() - timeService
                    - timeDriveToNext;

            // ograniczenie okien czasowych
            tmpCom = getComFromHashtable(String.valueOf(tmpPDDAction
                    .getCommissionID()));
            double timeWindow1 = 0;
            double timeWindow2 = 0;
            switch (tmpPDDAction.getType()) {
                case "PICKUP":
                    timeWindow1 = tmpCom.getPickupTime1();
                    timeWindow2 = tmpCom.getPickupTime2();
                    break;
                case "DELIVERY":
                    timeWindow1 = tmpCom.getDeliveryTime1();
                    timeWindow2 = tmpCom.getDeliveryTime2();
                    break;
                default:
                    logger.warn("AgentCalendar.elbowRight() -> sth wrong!");
                    break;
            }

            latestPossibleTime = Double.MIN_VALUE;
            if (latestTime < earliestTime || latestTime < timeWindow1) {
                return;
            } else if (latestTime <= timeWindow2) {
                latestPossibleTime = latestTime;
            } else if (latestTime > timeWindow2) {
                if (earliestTime <= timeWindow2) {
                    latestPossibleTime = timeWindow2;
                } else {
                    return;
                }
            } else {
                logger.warn("AgentCalendar.elbowRight -> sth wrong!");
            }

            double extraTime = latestPossibleTime - tmpPDDAction.getStartTime();

            if (extraTime > 0) {

                CalendarActionWithoutGraph tmpPrevWaitAction;
                CalendarActionWithoutGraph tmpNextDriveAction;
                CalendarActionWithoutGraph tmpNextWaitAction;

                // update tmpAction (PICKUP or DELIVERY)
                tmpPDDAction.setStartTime(latestPossibleTime);
                tmpPDDAction.setEndTime(latestPossibleTime + timeService);

                // update previous action (WAIT)
                tmpPrevWaitAction = schedule.getPreviousAction(tmpPDDAction);
                tmpPrevWaitAction.setEndTime(tmpPrevWaitAction.getEndTime()
                        + extraTime);

                // update next action (DRIVE)
                tmpNextDriveAction = schedule.getNextAction(tmpPDDAction);
                tmpNextDriveAction.setStartTime(tmpNextDriveAction
                        .getStartTime() + extraTime);
                tmpNextDriveAction.setEndTime(tmpNextDriveAction.getEndTime()
                        + extraTime);

                // update action after next action :) (WAIT)
                tmpNextWaitAction = schedule.getNextAction(tmpNextDriveAction);
                tmpNextWaitAction.setStartTime(tmpNextWaitAction.getStartTime()
                        + extraTime);
            }

            tmpPDDActionNext = tmpPDDAction;
            tmpPDDAction = tmpPDDActionPrev;
            tmpPDDActionPrev = getPrevPDDAction(tmpPDDActionPrev);
        }
    }

    // dopycha wszystkie zlecenia do lewej
    // (aby wykonaly sie jak najwczesniej)
    private void optimizeSchedule(int timestamp) {

        CalendarActionWithoutGraph tmpAction;

        tmpAction = schedule.getFirst(); // last
        // DEPOT
        tmpAction = getPrevPDDAction(tmpAction);

        if (tmpAction.getType().equals("DEPOT")) {
            return;
        }

        elbowLeft(tmpAction, timestamp);
    }

    // poprzednia akcja PICKUP, DELIVERY lub DEPOT
    private CalendarActionWithoutGraph getPrevPDDAction(
            CalendarActionWithoutGraph action) {

        CalendarActionWithoutGraph tmpAction = action;

        do {
            tmpAction = schedule.getPreviousAction(tmpAction);
            if (tmpAction == null) {
                return null;
            }

            if (tmpAction.isPDD()) {
                return tmpAction;
            }

        } while (schedule.indexOf(tmpAction) < schedule.size());

        System.out
                .println("AgentCalendar.getPrevPDDAction() -> first DEPOT action not found");
        return null;
    }

    // nastepna akcja: PICKUP, DELIVERY lub DEPOT
    private CalendarActionWithoutGraph getNextPDDAction(
            CalendarActionWithoutGraph action) {

        CalendarActionWithoutGraph tmpAction = action;

        do {
            tmpAction = schedule.getNextAction(tmpAction);

            if (tmpAction == null) {
                return null;
            }

            if (tmpAction.isPDD()) {
                return tmpAction;
            }

        } while (schedule.indexOf(tmpAction) > 0);

        System.out
                .println("AgentCalendar.getPrevPDDAction -> sth wrong! last DEPOT action not found");
        return null;
    }

    private double calculateTime(Point2D source, Point2D destination) {
        final int speed = 1;
        return calculateDistance(source, destination) / speed;
    }

    private double calculateDistance(Point2D source, Point2D destination) {
        return Point.distance(source.getX(), source.getY(), destination.getX(),
                destination.getY());
    }

    private double calculateWholeDistance() {

        CalendarActionWithoutGraph tmpAction;
        double wholeDistance;

        wholeDistance = 0;

        for (int i = schedule.size() - 1; i >= 0; i--) {

            tmpAction = schedule.get(i);

            if (tmpAction.getType().equals("DRIVE")) {
                wholeDistance += calculateDistance(tmpAction.getSource(),
                        tmpAction.getDestination());
            }
        }

        return wholeDistance;
    }

    @SuppressWarnings("unchecked")
    private void addComToHashtable(Commission com) {
        commissions.put(Integer.toString(com.getID()), com);
    }

    private Commission getComFromHashtable(String key) {
        return (Commission) commissions.get(key);
    }

    private void removeComFromHashtable(String key) {
        commissions.remove(key);
    }

    private double addLoad(Commission com,
                           CalendarActionWithoutGraph pickupAction,
                           CalendarActionWithoutGraph deliveryAction) {

        CalendarActionWithoutGraph tmpAction;
        boolean isBetween;
        double loadOK = 0;

        isBetween = false;

        for (int i = schedule.size() - 1; i >= 0; i--) {

            tmpAction = schedule.get(i);

            if (tmpAction.equals(pickupAction)) {

                isBetween = true;

            } else if (tmpAction.equals(deliveryAction)) {

                isBetween = false;
            }

            if (isBetween) {

                tmpAction.setCurrentLoad(tmpAction.getCurrentLoad()
                        + com.getLoad());
                if (tmpAction.getCurrentLoad() - maxLoad > loadOK) {
                    loadOK = tmpAction.getCurrentLoad() - maxLoad;
                }
            }
        }

        return loadOK;
    }

    public double getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(double maxLoad) {

        this.maxLoad = maxLoad;
    }
}
