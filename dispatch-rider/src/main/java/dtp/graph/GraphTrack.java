package dtp.graph;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphTrack implements Serializable {

    private static Logger logger = Logger.getLogger(GraphTrack.class);

    private List<GraphPoint> points;
    private boolean possible;

    /**
     * Creates an empty track, where possible==false
     */
    public GraphTrack() {

        super();
        possible = false;
        points = new ArrayList<>();
    }

    /**
     * Track object constructor. Creates non-optimal track between points 'from'
     * and 'to'.
     */
    public GraphTrack(GraphPoint from, GraphPoint to) {
        try {
            possible = false;
            ArrayList<GraphPoint> markedList;
            points = new ArrayList<>();
            markedList = new ArrayList<>();
            GraphPoint pt;
            points.add(from);
            markedList.add(from);
            int linkNumber, linkNumberIt, size;
            while (!points.isEmpty() && !possible) {
                pt = points.get(points.size() - 1);
                if (pt.equals(to))
                    possible = true;
                else {
                    size = pt.getLinksOutSize();
                    linkNumber = (int) Math.round(Math.random() * size);
                    linkNumberIt = linkNumber;
                    boolean isMarked = true;
                    GraphLink lnk = null;

                    // find link to unmarked point
                    while (isMarked && linkNumberIt < size) {
                        lnk = pt.getLinkOutByNumber(linkNumberIt);
                        if (!markedList.contains(lnk.getEndPoint()))
                            isMarked = false;
                        linkNumberIt++;
                    }
                    linkNumberIt = 0;
                    while (isMarked && linkNumberIt < linkNumber) {
                        lnk = pt.getLinkOutByNumber(linkNumberIt);
                        if (!markedList.contains(lnk.getEndPoint()))
                            isMarked = false;
                        linkNumberIt++;
                    }

                    if (isMarked)
                        points.remove(pt);
                    else {
                        markedList.add(lnk.getEndPoint());
                        points.add(lnk.getEndPoint());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public boolean isPossible() {
        return possible;
    }

    public void setPossible(boolean possible) {
        this.possible = possible;
    }

    public GraphPoint getFirst() {
        if (points.isEmpty())
            return null;
        else
            return points.get(0);
    }

    public GraphPoint get(int i) {
        if (points.isEmpty())
            return null;
        else
            return points.get(i);
    }

    public int size() {
        return points.size();
    }

    public GraphPoint getLast() {
        if (points.isEmpty())
            return null;
        else
            return points.get(points.size() - 1);
    }

    public double getCost() {

        if (points.isEmpty()) {

            return 0;

        } else if (points.size() == 2 && points.get(0).equals(points.get(1))) {

            return 0;

        } else {

            double cost = 0;
            try {
                Iterator<GraphPoint> it = points.iterator();
                GraphPoint pt1, pt2;
                GraphLink lnk;
                pt2 = it.next();
                while (cost != Double.MAX_VALUE && it.hasNext()) {
                    pt1 = pt2;
                    pt2 = it.next();
                    lnk = pt1.getLinkTo(pt2);
                    if (lnk != null)
                        cost += lnk.getCost();
                    else
                        cost = Double.MAX_VALUE;
                }
            } catch (Exception ex) {
                logger.error(ex);
            }

            return cost;
        }
    }

    public double getDist() {

        if (points.isEmpty()) {

            return 0;

        } else if (points.size() == 2 && points.get(0).equals(points.get(1))) {

            return 0;

        } else {

            double dist = 0;
            try {
                Iterator<GraphPoint> it = points.iterator();
                GraphPoint pt1, pt2;
                GraphLink lnk;
                pt2 = it.next();
                while (dist != Double.MAX_VALUE && it.hasNext()) {
                    pt1 = pt2;
                    pt2 = it.next();
                    lnk = pt1.getLinkTo(pt2);
                    if (lnk != null)
                        dist += lnk.distance();
                    else
                        dist = Double.MAX_VALUE;
                }
            } catch (Exception ex) {
                logger.error(ex);
            }

            return dist;
        }
    }

    /**
     * Adds a new point to current track.
     *
     * @param point new point to be added
     */
    public void addPoint(GraphPoint point) {

        this.points.add(point);
    }

    /**
     * Adds a new point to current track at concrete position
     *
     * @param point    new point to be added
     */
    public void addPointAtPosition(int position, GraphPoint point) {

        this.points.add(position, point);
    }

    /**
     * Checks if the track contains given point
     *
     * @return true if the track contains aPoint, false otherwise
     */
    public boolean contains(GraphPoint aPoint) {
        for (GraphPoint point : points)
            if (point == aPoint)
                return true;
        return false;
    }

    public Point2D getCurrentLocation(double startTime, double endTime,
                                      int timestamp) {

        if (timestamp < startTime || timestamp > endTime) {

            System.out.println("GraphTrack.getCurrentLocation() -> "
                    + "timestamp < startTime || timestamp > endTime");
            return null;
        }

        GraphPoint point1, point2;
        GraphLink link;
        double curCost, tmpCost1, tmpCost2, tmpCostSum;
        double factor;
        int iter;

        curCost = ((timestamp - startTime) / (endTime - startTime)) * getCost();
        tmpCostSum = 0;
        tmpCost1 = 0;
        iter = 0;

        while (true) {

            point1 = get(iter);
            point2 = get(iter + 1);

            link = point1.getLinkTo(point2);
            tmpCost2 = link.getCost();
            tmpCostSum += tmpCost2;

            if (tmpCostSum >= curCost) {

                factor = (curCost - tmpCost1) / (tmpCost2 - tmpCost1);

                return new Point2D.Double(factor
                        * (point2.getX() - point1.getX()) + point1.getX(),
                        factor * (point2.getY() - point1.getY())
                                + point1.getY());
            }

            tmpCost1 = tmpCost2;
            iter++;
        }
    }

    public void print() {

        boolean isFirst = true;

        if (getFirst() == null)
            System.out.println("Track beginning is null!");
        if (getLast() == null)
            System.out.println("Track end is null!");
        if (!isPossible())
            System.out.println("There is no connection from "
                    + getFirst().getName() + " to " + getLast().getName());
        else
            try {

                // drukuj jednoelementowa trase
                if (points.size() == 1) {

                    System.out.print(getFirst().toString());
                    System.out.print(" <=> ");
                    System.out.print(getFirst().toString());
                    System.out.println();

                    return;
                }

                for (GraphPoint point : this.points) {

                    if (isFirst) {
                        isFirst = false;
                    } else {
                        System.out.print(" -> ");
                    }

                    System.out.print(point.toString());
                }

                System.out.println();

            } catch (Exception ex) {
                logger.error(ex);
            }
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

        boolean isFirst = true;

        if (getFirst() == null)
            str.append("Track beginning is null!\n");

        if (getLast() == null)
            str.append("Track end is null!\n");
        if (!isPossible())
            str.append("There is no connection from ").append(getFirst().getName()).append(" to ").append(getLast().getName()).append("\n");
        else {

            try {

                // drukuj jednoelementowa trase
                if (points.size() == 1) {

                    str.append(getFirst().toString());
                    str.append(" <=> ");
                    str.append(getFirst().toString());
                    str.append("\n");

                    return str.toString();
                }

                for (GraphPoint point : this.points) {

                    if (isFirst) {
                        isFirst = false;
                    } else {
                        str.append(" -> ");
                    }

                    str.append(point.toString());
                }

                str.append("\n");

            } catch (Exception ex) {
                logger.error(ex);
            }
        }

        return str.toString();
    }

    public GraphTrack Clone() {

        GraphTrack t = new GraphTrack();
        t.possible = this.possible;
        t.points = new ArrayList<>();
        for (GraphPoint point : this.points) t.points.add(point);
        return t;
    }
}
