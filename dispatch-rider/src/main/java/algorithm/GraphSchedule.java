package algorithm;

import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.optimization.TrackFinder;
import dtp.simulation.SimInfo;
import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphSchedule extends Schedule {

    private final TrackFinder trackFinder;
    private final GraphLinkPredictor linkPredictor;

    private Point2D.Double nextLocation;
    /**
     * used to store how long we have to wait in location
     */
    private double waitTime = 0.0;
    private boolean waiting = false;
    private int previousId;
    /**
     * drive time in current track
     */
    private double currentDriveTime = 0.0;
    /**
     * timestamp when we change current link
     */
    private int changePointTimestamp;
    private Map<Point2D.Double, Map<Point2D.Double, List<Double>>> cache = new HashMap<Point2D.Double, Map<Point2D.Double, List<Double>>>();
    private Map<Point2D.Double, Map<Point2D.Double, List<Double>>> tmpCache;
    private GraphLink currentLink;
    private GraphTrack currentTrack;
    /**
     * distance which was driven on current link
     */
    private double partDistance = 0.0;
    private int previousGraphChangeTimestamp;
    private Graph changes;
    private Boolean updateAfterArrival;
    private GraphLink changedLink;

    public GraphSchedule(Algorithm algorithm, TrackFinder trackFinder,
                         GraphLinkPredictor predictor) {
        super(algorithm);
        this.trackFinder = trackFinder;
        this.linkPredictor = predictor;
    }

    public GraphSchedule(Algorithm algorithm, int currentCommission,
                         double creationTime, TrackFinder trackFinder,
                         GraphLinkPredictor predictor) {
        super(algorithm, currentCommission, creationTime);
        this.trackFinder = trackFinder;
        this.linkPredictor = predictor;
    }

    public TrackFinder getTrackFinder() {
        return trackFinder;
    }

    public GraphLinkPredictor getLinkPredictor() {
        return linkPredictor;
    }

    @Override
    protected double calculateDistance(Point2D.Double point1,
                                       Point2D.Double point2) {
        return GraphHelper.calculateDistance(point1, point2, trackFinder);
    }

    @Override
    public void setCurrentCommission(Commission currentCommission,
                                     Point2D.Double depot) {

        this.currentCommission = currentCommission;
    }

    /**
     * This method have to be invoked before time calculating. It copy cache to
     * tmpCache
     */
    @Override
    protected void beginTimeCalculating() {
        tmpCache = new HashMap<Point2D.Double, Map<Point2D.Double, List<Double>>>();
        Map<Point2D.Double, List<Double>> tmp;
        Map<Point2D.Double, List<Double>> copyTmp;
        List<Double> values;
        List<Double> copyValues;
        for (Point2D.Double key : cache.keySet()) {
            tmp = cache.get(key);
            copyTmp = new HashMap<Point2D.Double, List<Double>>();
            for (Point2D.Double key2 : tmp.keySet()) {
                values = tmp.get(key2);
                copyValues = new LinkedList<Double>();
                for (double val : values)
                    copyValues.add(val);
                copyTmp.put(key2, copyValues);
            }
            tmpCache.put(key, copyTmp);
        }
    }

    private boolean checkIfCurrentLink(GraphTrack track) {
        GraphPoint p1 = track.get(0);
        GraphPoint p2;
        for (int i = 1; i < track.size(); i++) {
            p2 = track.get(i);
            if (p1.equals(currentLink.getStartPoint())
                    && p2.equals(currentLink.getEndPoint()))
                return true;
            p1 = p2;
        }
        return false;
    }

    @Override
    protected double calculateTime(Point2D.Double point1,
                                   Point2D.Double point2, Point2D.Double depot) {
        return calculateTime(point1, point2, depot, true);
    }

    protected double calculateTime(Point2D.Double point1,
                                   Point2D.Double point2, Point2D.Double depot, boolean predictive) {

        Map<Point2D.Double, List<Double>> previous = tmpCache.get(point1);

        if (previous == null) {
            double time = currentDriveTime;

            time += previousGraphChangeTimestamp - changePointTimestamp;

            double dist = getLastLinkDist(depot);

            GraphPoint gp = trackFinder.getGraph()
                    .getPointByCoordinates(point1);
            GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(
                    point2);
            GraphTrack track = trackFinder.findTrack(gp, gp2);

            if (checkIfCurrentLink(track) == false) {
                if (predictive && linkPredictor != null) {
                    return getTrackCost(track);
                }
                return track.getCost();
            }

            if (partDistance > dist)
                partDistance = 0;

            GraphLink link = getLink(trackFinder.getGraph(), currentLink);

            if (predictive && linkPredictor != null) {
                time += linkPredictor.getCost(link) * (dist - partDistance)
                        / dist;
            } else {
                time += link.getCost() * (dist - partDistance) / dist;
            }
            gp = getLink(trackFinder.getGraph(), currentLink).getEndPoint();
            gp2 = trackFinder.getGraph().getPointByCoordinates(point2);
            track = trackFinder.findTrack(gp, gp2);
            if (predictive && linkPredictor != null) {
                time += getTrackCost(track);
            } else {
                time += track.getCost();
            }
            // if (point1.getX() == 85.0 && point1.getY() == 35.0
            // && point2.getX() == 88.0 && point2.getY() == 35.0) {
            // System.out.println("PREVIOUS is null");
            // System.out.println("cur drive time :" + currentDriveTime);
            // System.out.println("previousGraphChangeTimestamp "
            // + previousGraphChangeTimestamp);
            // System.out.println("changePointTimestamp "
            // + changePointTimestamp);
            // System.out
            // .println("add "
            // + (previousGraphChangeTimestamp - changePointTimestamp));
            // System.out.println("add "
            // + getLink(trackFinder.getGraph(), currentLink)
            // .getCost() * (dist - partDistance) / dist);
            // System.out.println(getLink(trackFinder.getGraph(), currentLink)
            // .getCost());
            // System.out.println(dist);
            // System.out.println(partDistance);
            // System.out.println("track cost " + track.getCost());
            // System.out.println("result " + time);
            // }

            return time;
        }

        List<Double> values = previous.get(point2);
        if (values != null && values.size() > 0) {
            // if (point1.getX() == 85.0 && point1.getY() == 35.0
            // && point2.getX() == 88.0 && point2.getY() == 35.0) {
            // System.out.println("FROM VALUES");
            // System.out.println("result " + values.get(0));
            // }
            if (values.get(0) < 0) {
                System.out.println("VALUES is NULL");
                System.exit(0);
            }
            return values.remove(0);
        }

        double time = currentDriveTime;
        time += previousGraphChangeTimestamp - changePointTimestamp;

        double dist = getLastLinkDist(depot);

        GraphPoint gp = trackFinder.getGraph().getPointByCoordinates(point1);
        GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(point2);
        GraphTrack track = trackFinder.findTrack(gp, gp2);

        if (checkIfCurrentLink(track) == false) {
            if (predictive && linkPredictor != null) {
                return getTrackCost(track);
            }
            return track.getCost();
        }

        if (partDistance > dist)
            partDistance = 0;

        GraphLink link = getLink(trackFinder.getGraph(), currentLink);

        if (predictive && linkPredictor != null) {
            time += linkPredictor.getCost(link) * (dist - partDistance) / dist;
        } else {
            time += link.getCost() * (dist - partDistance) / dist;
        }
        gp = getLink(trackFinder.getGraph(), currentLink).getEndPoint();
        gp2 = trackFinder.getGraph().getPointByCoordinates(point2);
        track = trackFinder.findTrack(gp, gp2);
        if (predictive && linkPredictor != null) {
            time += getTrackCost(track);
        } else {
            time += track.getCost();
        }
        // if (point1.getX() == 85.0 && point1.getY() == 35.0
        // && point2.getX() == 88.0 && point2.getY() == 35.0) {
        // System.out.println("OTHER");
        // System.out.println("cur drive time :" + currentDriveTime);
        // System.out.println("add "
        // + (previousGraphChangeTimestamp - changePointTimestamp));
        // System.out.println("add "
        // + getLink(trackFinder.getGraph(), currentLink).getCost()
        // * (dist - partDistance) / dist);
        // System.out.println("track cost " + track.getCost());
        // System.out.println("result " + time);
        // }

        return time;
    }

    @Override
    public Schedule createSchedule(Algorithm algorithm) {
        return new GraphSchedule(algorithm, trackFinder, linkPredictor);
    }

    @Override
    public Schedule createSchedule(Algorithm algorithm, int currentCommission,
                                   double creationTime) {
        return new GraphSchedule(algorithm, currentCommission, creationTime,
                trackFinder, linkPredictor);
    }

    @Override
    protected Schedule copySpecificFields(Schedule result) {
        GraphSchedule graph = (GraphSchedule) result;
        graph.nextLocation = this.nextLocation;
        graph.waitTime = this.waitTime;
        graph.waiting = this.waiting;
        graph.previousId = this.previousId;

        graph.cache = this.cache;
        graph.changePointTimestamp = this.changePointTimestamp;
        graph.currentDriveTime = this.currentDriveTime;
        graph.currentLink = this.currentLink;
        graph.currentTrack = this.currentTrack;
        graph.partDistance = this.partDistance;
        graph.previousGraphChangeTimestamp = this.previousGraphChangeTimestamp;
        graph.changedLink = this.changedLink;
        graph.changes = this.changes;
        return graph;
    }

    /**
     * This method is necessary, because in graph every method which get points,
     * links etc based on references, so if graph changed it won't work.
     */
    private GraphLink getLink(Graph graph, GraphLink link) {

        GraphPoint point1 = link.getStartPoint();
        GraphPoint point2 = link.getEndPoint();
        GraphPoint gp = graph.getPointByCoordinates(point1.getX(),
                point1.getY());
        GraphPoint gp2 = graph.getPointByCoordinates(point2.getX(),
                point2.getY());
        return gp.getLinkTo(gp2);
    }

    private double getLastLinkDist(Point2D.Double depot) {
        if (currentLink == null) {
            currentCommission = getCommission(0);
            GraphPoint gp1 = trackFinder.getGraph()
                    .getPointByCoordinates(depot);
            GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(
                    new Point2D.Double(currentCommission.getPickupX(),
                            currentCommission.getPickupY()));
            currentTrack = trackFinder.findTrack(gp1, gp2);
            currentLink = currentTrack.get(0).getLinkTo(currentTrack.get(1));
            currentDriveTime = 0.0;
        }

        GraphPoint point1 = currentLink.getStartPoint();
        GraphPoint point2 = currentLink.getEndPoint();

        double dist = Helper.calculateDistance(new Point2D.Double(
                point1.getX(), point1.getY()), new Point2D.Double(
                point2.getX(), point2.getY()));

        return dist;
    }

    public void changeGraph(Graph graph, int timestamp, Point2D.Double depot,
                            Boolean updateAfterArrival) {

        this.updateAfterArrival = updateAfterArrival;
        if (updateAfterArrival != null && updateAfterArrival) {
            changes = graph;
            return;
        }

        double oldCost = getLink(trackFinder.getGraph(), currentLink).getCost();
        double newCost = getLink(graph, currentLink).getCost();

        if (oldCost != newCost) {
            int timeDiff = timestamp - previousGraphChangeTimestamp;
            previousGraphChangeTimestamp = timestamp;

            double dist = getLastLinkDist(depot);

            partDistance += timeDiff * dist / oldCost;

            // System.out.println("part disr - " + partDistance);

        }

        linkPredictor.addGraphToHistory(trackFinder.getGraph());

        trackFinder.setGraph(graph);
        currentLink = getLink(graph, currentLink);
        nextLocation = new Point2D.Double(currentLink.getEndPoint().getX(),
                currentLink.getEndPoint().getY());

    }

    private Point2D.Double calculateCurLocation(GraphLink curLink,
                                                double timeDiff) {

        if (curLink == null) {
            partDistance = 0;
            return currentLocation;
        }
        Point2D.Double startPoint = new Point2D.Double(curLink.getStartPoint()
                .getX(), curLink.getStartPoint().getY());
        Point2D.Double endPoint = new Point2D.Double(curLink.getEndPoint()
                .getX(), curLink.getEndPoint().getY());
        double totalDist = Helper.calculateDistance(startPoint, endPoint);

        if (totalDist != 0 && timeDiff != 0) {

            double dist = (totalDist / curLink.getCost()) * timeDiff;
            partDistance = dist;

            if (dist != 0) {
                double newX = startPoint.getX() + dist
                        * (endPoint.getX() - startPoint.getX()) / totalDist;
                double newY = startPoint.getY() + dist
                        * (endPoint.getY() - startPoint.getY()) / totalDist;

                return new Point2D.Double(newX, newY);
            }
        }
        partDistance = 0;
        return currentLocation;
    }

    public GraphLink getCurLink() {
        return currentLink;
    }

    @Override
    public void updateCurrentLocation(int timestamp, Point2D.Double depot,
                                      AID aid) {
        this.beginTimeCalculating();

        if (currentCommission == null) {

            currentLocation = depot;

            currentCommission = getCommission(0);

            nextLocation = new Point2D.Double(currentCommission.getPickupX(),
                    currentCommission.getPickupY());
            GraphPoint gp1 = trackFinder.getGraph()
                    .getPointByCoordinates(depot);
            GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(
                    new Point2D.Double(currentCommission.getPickupX(),
                            currentCommission.getPickupY()));
            // System.out.println(gp1 + " " + gp2);
            currentTrack = trackFinder.findTrack(gp1, gp2);
            currentLink = currentTrack.get(0).getLinkTo(currentTrack.get(1));
            previousGraphChangeTimestamp = 0;
            currentDriveTime = 0.0;
            return;
        }

        double departureTime;
        double driveTime;
        if (currentCommission.isPickup()) {
            departureTime = calculateTimeToDeparture(depot,
                    currentCommission.getPickUpId(), aid);
            driveTime = calculateTimeToDriveToCommission(depot,
                    currentCommission.getPickUpId(), aid);
        } else {
            departureTime = calculateTimeToDeparture(depot,
                    currentCommission.getDeliveryId(), aid);
            driveTime = calculateTimeToDriveToCommission(depot,
                    currentCommission.getDeliveryId(), aid);
        }

        if (departureTime <= timestamp) {

            // if (aid.getLocalName().contains("EUnitAgent#0")) {
            // System.out.println("?????????????????????????????????????");
            // // System.exit(0);
            // }
            Point2D.Double start;
            Point2D.Double end;

            start = new Point2D.Double(currentTrack.get(0).getX(), currentTrack
                    .get(0).getY());
            end = new Point2D.Double(currentTrack.getLast().getX(),
                    currentTrack.getLast().getY());

            double time = calculateTime(start, end, depot, false);

            Map<Point2D.Double, List<Double>> tmp = cache.get(start);
            if (tmp == null) {
                tmp = new HashMap<Point2D.Double, List<Double>>();
                cache.put(start, tmp);
            }
            List<Double> values = tmp.get(end);
            if (values == null) {
                values = new LinkedList<Double>();
                tmp.put(end, values);
            }
            values.add(time);

            int curCommissionIndex = this.getIndexOf(currentCommission,
                    currentCommission.isPickup());
            Commission nextCommission;
            // TODO sprawdza� co z ostatnim
            if (curCommissionIndex + 1 == this.size()) {
                return;
            }
            nextCommission = getCommission(curCommissionIndex + 1);

            if (currentCommission.isPickup()) {
                start = new Point2D.Double(currentCommission.getPickupX(),
                        currentCommission.getPickupY());
            } else {
                start = new Point2D.Double(currentCommission.getDeliveryX(),
                        currentCommission.getDeliveryY());
            }

            if (nextCommission.isPickup()) {
                end = new Point2D.Double(nextCommission.getPickupX(),
                        nextCommission.getPickupY());
            } else {
                end = new Point2D.Double(nextCommission.getDeliveryX(),
                        nextCommission.getDeliveryY());
            }

            // if (aid.getLocalName().contains("EUnitAgent#0")) {
            // System.out.println("cur com: " + currentCommission);
            // System.out.println("cur com is pickup: "
            // + currentCommission.isPickup());
            // System.out.println("next com: " + nextCommission);
            // System.out.println("nex com is pickup: "
            // + nextCommission.isPickup());
            // System.out.println(currentTrack);
            // System.out.println(currentLink);
            // System.out.println("start: " + start);
            // System.out.println("end: " + end);
            // System.out.println(departureTime + " < " + timestamp);
            // System.out
            // .println("END pf ???????????????????????????????????????????????");
            // }

            if (start.equals(end)) {
                currentCommission = nextCommission;
                currentLocation = new Point2D.Double(start.getX(), start.getY());
                partDistance = 0;
                return;
            }
            GraphPoint gp1 = trackFinder.getGraph()
                    .getPointByCoordinates(start);
            GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(end);
            currentTrack = trackFinder.findTrack(gp1, gp2);
            currentLink = currentTrack.get(0).getLinkTo(currentTrack.get(1));
            partDistance = 0;
            checkIfLinkChanged(currentLink);
            previousGraphChangeTimestamp = timestamp;
            changePointTimestamp = timestamp;

            currentLocation = calculateCurLocation(currentLink, timestamp
                    - departureTime);
            nextLocation = new Point2D.Double(currentLink.getEndPoint().getX(),
                    currentLink.getEndPoint().getY());

            currentDriveTime = 0.0;

            currentCommission = nextCommission;
        } else {

            Point2D.Double point2;

            if (currentCommission.isPickup()) {
                point2 = new Point2D.Double(currentCommission.getPickupX(),
                        currentCommission.getPickupY());
            } else {
                point2 = new Point2D.Double(currentCommission.getDeliveryX(),
                        currentCommission.getDeliveryY());
            }
            GraphPoint gp = getLink(trackFinder.getGraph(), currentLink)
                    .getEndPoint();
            GraphPoint gp2 = trackFinder.getGraph().getPointByCoordinates(
                    point2);

            GraphTrack track = null;

            // if (aid.getLocalName().contains("EUnitAgent#0")) {
            // System.out.println("DriveTime before change " + driveTime);
            // }

            if (gp.getX() != gp2.getX() || gp.getY() != gp2.getY()) {
                track = trackFinder.findTrack(gp, gp2);

                // if (aid.getLocalName().contains("EUnitAgent#0")) {
                // System.out.println("Track cost " + track.getCost());
                // }
                driveTime -= track.getCost();
            }
            // if (currentCommission.isPickup())
            // driveTime -= currentCommission.getPickUpServiceTime();
            // else
            // driveTime -= currentCommission.getDeliveryServiceTime();

            // if (aid.getLocalName().contains("EUnitAgent#0")) {
            // System.out.println("cur com: " + currentCommission);
            // System.out.println("cur location: " + currentLocation);
            // System.out.println("cur com is pickup: "
            // + currentCommission.isPickup());
            // System.out.println(currentTrack);
            // System.out.println(currentLink);
            // System.out.println("gp: " + gp);
            // System.out.println("gp2: " + gp2);
            // System.out.println("track " + track);
            // if (track != null)
            // System.out.println("track cost: " + track.getCost());
            // System.out.println(driveTime + " < " + timestamp);
            // }

            if (driveTime < timestamp) {
                gp = getLink(trackFinder.getGraph(), currentLink).getEndPoint();
                int index = 0;
                while (!currentTrack.get(index).equals(gp)) {
                    index++;
                }

                if (index + 1 == currentTrack.size()) {
                    gp = getLink(trackFinder.getGraph(), currentLink)
                            .getEndPoint();
                    currentLocation = new Point2D.Double(gp.getX(), gp.getY());
                    partDistance = 0;
                    return;
                }

                gp = getLink(trackFinder.getGraph(), currentLink)
                        .getStartPoint();
                gp2 = getLink(trackFinder.getGraph(), currentLink)
                        .getEndPoint();

                // if (aid.getLocalName().contains("EUnitAgent#0")) {
                // System.out.println("CUR DRIVETIME: " + currentDriveTime);
                // System.out.println("ADD "
                // + calculateTime(
                // new Point2D.Double(gp.getX(), gp.getY()),
                // new Point2D.Double(gp2.getX(), gp2.getY()),
                // depot));
                // System.out.println("ADD: " + (timestamp - driveTime));
                // }

                currentDriveTime = calculateTime(new Point2D.Double(gp.getX(),
                                gp.getY()), new Point2D.Double(gp2.getX(), gp2.getY()),
                        depot, false);

                currentDriveTime += timestamp - driveTime;
                currentLink = gp2.getLinkTo(currentTrack.get(index + 1));
                partDistance = 0;
                checkIfLinkChanged(currentLink);
                previousGraphChangeTimestamp = timestamp;
                changePointTimestamp = timestamp;
                currentLocation = calculateCurLocation(currentLink, timestamp
                        - driveTime);

                // if (aid.getLocalName().contains("EUnitAgent#0")) {
                // System.out.println("PART DIST: " + partDistance);
                // }

                if (currentLink != null)
                    nextLocation = new Point2D.Double(currentLink.getEndPoint()
                            .getX(), currentLink.getEndPoint().getY());
            } else {
                Point2D.Double startPoint = new Point2D.Double(currentLink
                        .getStartPoint().getX(), currentLink.getStartPoint()
                        .getY());
                Point2D.Double endPoint = new Point2D.Double(currentLink
                        .getEndPoint().getX(), currentLink.getEndPoint().getY());
                double totalDist = Helper.calculateDistance(startPoint,
                        endPoint);

                if (totalDist != 0) {
                    double dTime = currentLink.getCost()
                            * Helper.calculateDistance(currentLocation,
                            endPoint) / totalDist;
                    // if (aid.getLocalName().contains("EUnitAgent#0")) {
                    // System.out.println("DTIME: " + dTime);
                    // }

                    if (dTime != 0) {
                        double newX = currentLocation.getX()
                                + (endPoint.getX() - currentLocation.getX())
                                / dTime;
                        double newY = currentLocation.getY()
                                + (endPoint.getY() - currentLocation.getY())
                                / dTime;
                        currentLocation = new Point2D.Double(newX, newY);
                    }
                }
            }
        }

    }

    @Override
    public void initSchedule(Schedule schedule) {
        schedule.copySpecificFields(this);
        this.currentCommission = schedule.currentCommission;
        this.currentLocation = schedule.currentLocation;

    }

    private double calculateTimeToDeparture(Point2D.Double depot,
                                            int comOriginalId, AID aid) {
        this.beginTimeCalculating();
        double time = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);

            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot,
                        false);
                // if (aid.getLocalName().contains("EUnitAgent#0"))
                if (driveTime < 0) {
                    System.out.println(currentLocation + " -> " + nextLocation
                            + " = " + driveTime);

                    System.out.println(currentDriveTime);
                    System.out.println(previousGraphChangeTimestamp);
                    System.out.println(changePointTimestamp);
                    System.out.println("dist " + getLastLinkDist(depot));

                    GraphPoint gp = trackFinder.getGraph()
                            .getPointByCoordinates(currentLocation);
                    GraphPoint gp2 = trackFinder.getGraph()
                            .getPointByCoordinates(nextLocation);
                    GraphTrack track = trackFinder.findTrack(gp, gp2);

                    System.out.println(checkIfCurrentLink(track));
                    System.out.println("part " + partDistance);

                    GraphLink link = getLink(trackFinder.getGraph(),
                            currentLink);

                    System.out.println(link.getCost());

                    System.exit(0);

                }
                if (time + driveTime < com.getPickupTime1())
                    time = com.getPickupTime1();
                else
                    time += driveTime;

                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot,
                        false);
                // if (aid.getLocalName().contains("EUnitAgent#0"))
                if (driveTime < 0) {
                    System.out.println(currentLocation + " -> " + nextLocation
                            + " = " + driveTime);

                    System.out.println(currentDriveTime);
                    System.out.println(previousGraphChangeTimestamp);
                    System.out.println(changePointTimestamp);
                    System.out.println("dist " + getLastLinkDist(depot));

                    GraphPoint gp = trackFinder.getGraph()
                            .getPointByCoordinates(currentLocation);
                    GraphPoint gp2 = trackFinder.getGraph()
                            .getPointByCoordinates(nextLocation);
                    GraphTrack track = trackFinder.findTrack(gp, gp2);

                    System.out.println(checkIfCurrentLink(track));
                    System.out.println("part " + partDistance);

                    GraphLink link = getLink(trackFinder.getGraph(),
                            currentLink);

                    System.out.println(link.getCost());

                    System.exit(0);
                }

                if (time + driveTime < com.getDeliveryTime1())
                    time = com.getDeliveryTime1();
                else
                    time += driveTime;

                time += com.getDeliveryServiceTime();
            }

            if (com.isPickup()) {
                if (comOriginalId == com.getPickUpId())
                    break;
            } else {
                if (comOriginalId == com.getDeliveryId())
                    break;
            }

            currentLocation = nextLocation;
        }
        return time;
    }

    private double calculateTimeToDriveToCommission(Point2D.Double depot,
                                                    int comOriginalId, AID aid) {
        this.beginTimeCalculating();
        double time = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        Commission com;
        double driveTime;
        for (int i = 0; i < commissions.size(); i++) {
            com = commissions.get(i);

            if (types.get(i)) {
                nextLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
                driveTime = calculateTime(currentLocation, nextLocation, depot,
                        false);
                // if (aid.getLocalName().contains("EUnitAgent#0"))
                // System.out.println(currentLocation + " -> " + nextLocation
                // + " = " + driveTime);

                if (com.getPickUpId() == comOriginalId) {
                    time += driveTime;
                    break;
                } else {
                    if (time + driveTime < com.getPickupTime1())
                        time = com.getPickupTime1();
                    else
                        time += driveTime;
                }
                time += com.getPickUpServiceTime();
            } else {
                nextLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
                driveTime = calculateTime(currentLocation, nextLocation, depot,
                        false);
                // if (aid.getLocalName().contains("EUnitAgent#0"))
                // System.out.println(currentLocation + " -> " + nextLocation
                // + " = " + driveTime);

                if (com.getDeliveryId() == comOriginalId) {
                    time += driveTime;
                    break;
                } else {
                    if (time + driveTime < com.getDeliveryTime1())
                        time = com.getDeliveryTime1();
                    else
                        time += driveTime;
                }
                time += com.getDeliveryServiceTime();
            }

            currentLocation = nextLocation;
        }
        return time;
    }

    public void find(GraphPoint point1, GraphPoint point2) {
        GraphTrack t = trackFinder.findTrack(point1, point2);
        System.out.print(t);
        GraphLink link;
        GraphPoint start = point1;
        GraphPoint end;
        for (int i = 1; i < t.size(); i++) {
            end = t.get(i);
            link = start.getLinkTo(end);
            System.out.println(start + " " + end + " --> " + link.getCost());
            start = end;
        }
        System.out.println(t.getCost());
    }

    @Override
    public double calculateDriveTime(SimInfo info) {
        beginTimeCalculating();
        double result = 0.0;
        Point2D.Double currentLocation = info.getDepot();

        for (int i = 0; i < size(); i++) {
            if (types.get(i)) {
                result += calculateTime(currentLocation, new Point2D.Double(
                        commissions.get(i).getPickupX(), commissions.get(i)
                        .getPickupY()), info.getDepot(), false);
                currentLocation = new Point2D.Double(commissions.get(i)
                        .getPickupX(), commissions.get(i).getPickupY());
            } else {
                result += calculateTime(currentLocation, new Point2D.Double(
                        commissions.get(i).getDeliveryX(), commissions.get(i)
                        .getDeliveryY()), info.getDepot(), false);
                currentLocation = new Point2D.Double(commissions.get(i)
                        .getDeliveryX(), commissions.get(i).getDeliveryY());
            }
        }
        result += calculateTime(currentLocation, info.getDepot(),
                info.getDepot(), false);
        return result;
    }

    private void checkIfLinkChanged(GraphLink link) {
        if (updateAfterArrival == null || changes == null)
            return;
        double oldCost = getLink(trackFinder.getGraph(), link).getCost();
        double newCost = getLink(changes, link).getCost();
        if (oldCost != newCost) {
            changedLink = getLink(changes, link);
            currentLink = getLink(changes, link);
        }
    }

    public GraphLink getChangeLink() {
        return changedLink;
    }

    public void resetChangeLink() {
        changedLink = null;
    }

    public void insertGraphChanges(List<GraphLink> links) {
        GraphPoint sPoint;
        GraphPoint ePoint;
        GraphLink link;
        Graph graph = trackFinder.getGraph();
        for (GraphLink clink : links) {
            sPoint = graph.getPointByCoordinates(clink.getStartPoint().getX(),
                    clink.getStartPoint().getY());
            ePoint = graph.getPointByCoordinates(clink.getEndPoint().getX(),
                    clink.getEndPoint().getY());
            link = sPoint.getLinkTo(ePoint);
            link.setCost((int) clink.getCost());
        }
        trackFinder.setGraph(graph);

        currentLink = getLink(graph, currentLink);
        nextLocation = new Point2D.Double(currentLink.getEndPoint().getX(),
                currentLink.getEndPoint().getY());

    }

    private double getTrackCost(GraphTrack track) {
        double result = 0.0;
        GraphPoint gp1;
        GraphPoint gp2;
        GraphLink link;
        for (int i = 0; i < track.size() - 1; i++) {
            gp1 = track.get(i);
            gp2 = track.get(i + 1);
            link = gp1.getLinkTo(gp2);
            result += linkPredictor.getCost(link);
        }
        return result;
    }
}
