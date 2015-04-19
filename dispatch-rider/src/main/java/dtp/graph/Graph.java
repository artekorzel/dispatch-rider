package dtp.graph;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Graph implements Serializable {
    private static final double costMul = 0.5, costPow = 1, costSum = 0, freeSum = 0;
    private static Logger logger = Logger.getLogger(Graph.class);
    private final ArrayList<GraphPoint> points;
    private final HashMap<Integer, GraphPoint> map;
    private final ArrayList<GraphLink> links;
    private final boolean complete;

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
     * Returns point with given coordinates or null if such doesn't exist.
     * Required by mapedit.
     *
     * @return point with given coordinates or null
     */
    public GraphPoint getPointByCoordinates(double x, double y) {

        for (GraphPoint pt : points) {
            if (pt.getX() == x && pt.getY() == y)
                return pt;
        }

        if (complete) {

            GraphPoint result = new GraphPoint(x, y, "pt_" + x + "_" + y);

            for (GraphPoint pt : points) {
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

    public Collection<GraphPoint> getCollectionOfPoints() {
        return this.points;
    }

    public ArrayList<GraphLink> getCollectionOfLinks() {
        return this.links;
    }

    public GraphPoint getPointById(Integer id) {
        return this.map.get(id);
    }

    public double getCostMul() {
        return costMul;
    }

    public double getCostPow() {
        return costPow;
    }

    public double getCostSum() {
        return costSum;
    }

    public double getFreeSum() {
        return freeSum;
    }

    public boolean isFullyTraversable() {
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

}
