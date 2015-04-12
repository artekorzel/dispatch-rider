package dtp.graph;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Graph implements Serializable {
    private static Logger logger = Logger.getLogger(Graph.class);

    private final ArrayList<GraphPoint> points;
    private final HashMap<Integer, GraphPoint> map;
    private final ArrayList<GraphLink> links;
    private final boolean complete;

    private double costMul = 0.5, costPow = 1, costSum = 0, freeSum = 0;

    public Graph() {

        this.points = new ArrayList<>();
        this.links = new ArrayList<>();
        this.complete = false;
        map = new HashMap<>();
    }

    public Graph(boolean complete) {
        this.points = new ArrayList<>();
        this.links = new ArrayList<>();
        this.map = new HashMap<>();
        this.complete = complete;
    }

    public GraphPoint getDepot() {

        Iterator<GraphPoint> iter;
        GraphPoint graphPoint;

        iter = points.iterator();

        while (iter.hasNext()) {

            graphPoint = iter.next();

            if (graphPoint.isBase())
                return graphPoint;
        }

        logger.info("No depot in the graph");
        return null;
    }

    public GraphPoint getPoint(int num) {
        return this.points.get(num);
    }

    public int getPointsSize() {
        return points.size();
    }

    public Iterator<GraphPoint> getPointsIterator() {
        return this.points.iterator();
    }

    public int getLinksSize() {
        return links.size();
    }

    public Iterator<GraphLink> getLinksIterator() {
        return this.links.iterator();
    }

    public void addPoint(GraphPoint pt) {
        points.add(pt);
    }

    public void putPoint(Integer id, GraphPoint point) {
        this.map.put(id, point);
        points.add(point);
    }

    /**
     * Dodaje nowy punkt do grafu wkladajac go pomiedzy dwa punkty linka
     */
    public void addPointOnLink(GraphPoint point, GraphLink link) {

        this.map.put(map.size(), point);
        points.add(point);

        GraphLink tmpLink;

        tmpLink = new GraphLink(link.getStartPoint(), point, Point.distance(
                link.getStartPoint().getX(), link.getStartPoint().getY(),
                point.getX(), point.getY()));
        addLink(tmpLink);

        tmpLink = new GraphLink(point, link.getEndPoint(), Point.distance(point
                .getX(), point.getY(), link.getEndPoint().getX(), link
                .getEndPoint().getY()));
        addLink(tmpLink);

        if (containsLink(link.getEndPoint(), link.getStartPoint())) {

            tmpLink = new GraphLink(link.getEndPoint(), point, Point.distance(
                    link.getEndPoint().getX(), link.getEndPoint().getY(),
                    point.getX(), point.getY()));
            addLink(tmpLink);

            tmpLink = new GraphLink(point, link.getStartPoint(),
                    Point.distance(point.getX(), point.getY(), link
                            .getStartPoint().getX(), link.getStartPoint()
                            .getY()));
            addLink(tmpLink);
        }

    }

    public void addLink(GraphLink ln) {
        links.add(ln);
    }

    /**
     * Sprawdza czy do grafu dodany jest juz link laczacy takie dwa punkty (o
     * takich wspolrzednych) od point1 do point2
     */
    public boolean containsLink(GraphPoint point1, GraphPoint point2) {

        Iterator<GraphLink> iter;
        GraphLink tmpLink;
        GraphPoint startPoint, endPoint;

        iter = getLinksIterator();

        while (iter.hasNext()) {

            tmpLink = iter.next();

            startPoint = tmpLink.getStartPoint();
            endPoint = tmpLink.getEndPoint();

            if (startPoint.hasSameCoordinates(point1)
                    && endPoint.hasSameCoordinates(point2))
                return true;

        }

        return false;
    }

    /**
     * Removes given point.
     *
     * @param pt point to be removed
     * @return false if there was no such link in the links arraylist, true
     * otherwise
     */
    public boolean removePoint(GraphPoint pt) {
        if (points.contains(pt)) {
            points.remove(pt);
            pt.dispose();
            pt = null;
            return true;
        }
        return false;
    }

    /**
     * Removes given link.
     *
     * @param ln link to be removed
     * @return false if there was no such link in the links arraylist, true
     * otherwise
     */
    public boolean removeLink(GraphLink ln) {
        if (links.contains(ln)) {
            links.remove(ln);
            ln.dispose();
            ln = null;
            return true;
        }
        return false;
    }

    /**
     * Returns point with given coordinates or null if such doesn't exist.
     * Required by mapedit.
     *
     * @param x
     * @param y
     * @return point with given coordinates or null
     */
    public GraphPoint getPointByCoordinates(double x, double y) {

        Iterator<GraphPoint> it = points.iterator();
        while (it.hasNext()) {
            GraphPoint pt = it.next();
            if (pt.getX() == x && pt.getY() == y)
                return pt;
        }

        if (complete) {

            GraphPoint result = new GraphPoint(x, y, "pt_" + x + "_" + y);
            it = points.iterator();

            while (it.hasNext()) {
                GraphPoint pt = it.next();
                addLink(new GraphLink(pt, result, 0));
                addLink(new GraphLink(result, pt, 0));
            }

            addPoint(result);
            return result;

        } else {

            return null;
        }
    }

    public GraphPoint getPointByCoordinates(Point2D point) {

        return getPointByCoordinates((int) point.getX(), (int) point.getY());
    }

    public GraphLink getLinkByCoordinate(double x1, double y1, double x2,
                                         double y2) {
        for (GraphLink ln : links) {
            if (ln.getStartPoint().getX() == x1
                    && ln.getStartPoint().getY() == y1
                    && ln.getEndPoint().getX() == x2
                    && ln.getEndPoint().getY() == y2)
                return ln;
        }
        return null;
    }

    public double getXmin() {

        Iterator<GraphPoint> iter = getPointsIterator();
        double xmin = Double.MAX_VALUE;

        while (iter.hasNext()) {

            double val = iter.next().getX();
            if (val < xmin)
                xmin = val;
        }

        return xmin;
    }

    public double getXmax() {

        Iterator<GraphPoint> iter = getPointsIterator();
        double xmax = Double.MIN_VALUE;

        while (iter.hasNext()) {

            double val = iter.next().getX();
            if (val > xmax)
                xmax = val;
        }

        return xmax;
    }

    public double getYmin() {

        Iterator<GraphPoint> iter = getPointsIterator();
        double ymin = Double.MAX_VALUE;

        while (iter.hasNext()) {

            double val = iter.next().getY();
            if (val < ymin)
                ymin = val;
        }

        return ymin;
    }

    public double getYmax() {

        Iterator<GraphPoint> iter = getPointsIterator();
        double ymax = Double.MIN_VALUE;

        while (iter.hasNext()) {

            double val = iter.next().getY();
            if (val > ymax)
                ymax = val;
        }

        return ymax;
    }

    public void removeUnlinkedPoints() {
        int i = points.size();
        while (i > 0) {
            i--;
            if (points.get(i).getLinksInSize() == 0
                    && points.get(i).getLinksOutSize() == 0)
                points.remove(i);
        }
    }

    public void removePoints(ArrayList<GraphPoint> list) {
        int i = points.size();
        while (i > 0) {
            i--;
            if (list.contains(points.get(i))) {
                GraphPoint pt = points.remove(i);
                pt.dispose();
                pt = null;
            }

        }
    }

    public void removePointsExcept(ArrayList<GraphPoint> list) {
        int i = points.size();
        while (i > 0) {
            i--;
            GraphPoint pt = points.get(i);
            if (!list.contains(pt)) {
                points.remove(i);
                pt.dispose();
                pt = null;
            }

        }
    }

    public void removeLinks(ArrayList<GraphLink> list) {
        int i = links.size();
        while (i > 0) {
            i--;
            if (list.contains(links.get(i))) {
                GraphLink ln = links.remove(i);
                ln.dispose();
                ln = null;
            }

        }
    }

    public void removeLinksExcept(ArrayList<GraphLink> list) {
        try {
            int i = links.size();
            ArrayList<GraphLink> already = new ArrayList<GraphLink>();
            while (i > 0) {
                i--;
                if (!list.contains(links.get(i))) {
                    GraphLink ln = links.remove(i);
                    ln.dispose();
                    ln = null;
                } else { // removing duplicate links
                    if (already.contains(links.get(i)))
                        links.remove(i);
                    else
                        already.add(links.get(i));
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public Collection<GraphPoint> getCollectionOfPoints() {
        return this.points;
    }

    public ArrayList<GraphLink> getCollectionOfLinks() {
        return this.links;
    }

    public GraphPoint getPointById(Integer id) {
        return this.map.get(id);
    }

    public int getIdByPoint(GraphPoint pt) {
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(pt))
                return i;
        }
        return -1;
    }

    public boolean isComplete() {
        return complete;
    }

    public double costFunction(GraphTrack tr) {
        return Math.pow(costMul * tr.getCost() + costSum, costPow) + freeSum;
    }

    public double costFunction(GraphLink ln) {
        return Math.pow(costMul * ln.getCost() + costSum, costPow) + freeSum;
    }

    public double getCostMul() {
        return costMul;
    }

    public void setCostMul(double costMul) {
        this.costMul = costMul;
    }

    public double getCostPow() {
        return costPow;
    }

    public void setCostPow(double costPow) {
        this.costPow = costPow;
    }

    public double getCostSum() {
        return costSum;
    }

    public void setCostSum(double costSum) {
        this.costSum = costSum;
    }

    public double getFreeSum() {
        return freeSum;
    }

    public void setFreeSum(double freeSum) {
        this.freeSum = freeSum;
    }

    /**
     * @return true if graph is fully traversable, false otherwise
     */
    public boolean isConsistant() {
        Iterator<GraphPoint> pit = this.getPointsIterator();
        if (!pit.hasNext())
            pit = getCollectionOfPoints().iterator();
        while (pit.hasNext()) {
            GraphPoint point = pit.next();
            Iterator<GraphPoint> neighboursIt = this.getPointsIterator();
            while (neighboursIt.hasNext()) {
                GraphPoint neighbour = neighboursIt.next();
                if (!(new GraphTrack(point, neighbour)).isPossible()
                        || !(new GraphTrack(neighbour, point)).isPossible())
                    return false;
            }
        }
        return true;
    }

    public void printPoints() {

        Iterator<GraphPoint> it;
        GraphPoint point;

        System.out.println("\nLIST OF POINTS:");
        for (it = points.iterator(); it.hasNext(); ) {
            point = it.next();
            System.out.println(point.toString() + " " + point.getX() + " "
                    + point.getY());

        }
    }

    public void printLinks() {

        Iterator<GraphPoint> it;
        Iterator<GraphLink> it2;
        GraphPoint point;
        GraphLink link;

        System.out.println("\nWHOLE GRAPH:");
        for (it = points.iterator(); it.hasNext(); ) {
            point = it.next();
            System.out.println("\nPoint: " + point.toString());
            System.out.println("To: ");
            for (it2 = point.getLinksOutIterator(); it2.hasNext(); ) {
                link = it2.next();
                System.out.println("   " + link.getEndPoint().toString() + " "
                        + link.getCost());
            }
            System.out.println("From: ");
            for (it2 = point.getLinksInIterator(); it2.hasNext(); ) {
                link = it2.next();
                System.out.println("   " + link.getStartPoint().toString()
                        + " " + link.getCost());
            }
        }

        System.out.println("\nWHOLE GRAPH2:");
        for (it2 = links.iterator(); it2.hasNext(); ) {
            link = it2.next();
            System.out.println("\nLink: " + link.getStartPoint().toString()
                    + " - " + link.getEndPoint().toString());
        }

    }
}
