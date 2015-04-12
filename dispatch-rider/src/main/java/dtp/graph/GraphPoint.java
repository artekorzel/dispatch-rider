package dtp.graph;

/**
 * @author Szymon Borgosz
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class GraphPoint implements Serializable {

    private final double x, y;
    private final ArrayList<GraphLink> linksIn, linksOut;
    private String name;
    private boolean isBase;
    private int id;
    private double timeWindowBegin, timeWindowEnd;
    private boolean isPickup;

    public GraphPoint(double xVal, double yVal) {

        this.linksIn = new ArrayList<GraphLink>();
        this.linksOut = new ArrayList<GraphLink>();
        this.x = xVal;
        this.y = yVal;
        // zaokraglij do dwoch miejsc po przecinku
        double xValRound = ((int) (100 * xVal)) / 100d;
        double yValRound = ((int) (100 * yVal)) / 100d;
        this.name = "pt_" + xValRound + "_" + yValRound;
        this.isBase = false;
    }

    public GraphPoint(double xVal, double yVal, String name) {

        this.linksIn = new ArrayList<GraphLink>();
        this.linksOut = new ArrayList<GraphLink>();
        this.x = xVal;
        this.y = yVal;
        this.name = name;
        this.isBase = false;
    }

    public GraphPoint(double xVal, double yVal, String name, boolean isBase, int id) {

        this.linksIn = new ArrayList<GraphLink>();
        this.linksOut = new ArrayList<GraphLink>();
        this.x = xVal;
        this.y = yVal;
        this.name = name;
        this.isBase = isBase;
        this.id = id;
    }

    public GraphPoint(double xVal, double yVal, String name, boolean isBase, int id, double timeWindowBegin, double timeWindowEnd, boolean isPickup) {

        this(xVal, yVal, name, isBase, id);
        this.timeWindowBegin = timeWindowBegin;
        this.timeWindowEnd = timeWindowEnd;
        this.isPickup = isPickup;
    }

    public boolean equals(GraphPoint pt) {
        if (x == pt.getX() && y == pt.getY()
                && name.equalsIgnoreCase(pt.getName()))
            return true;
        else
            return false;
    }

    public Integer getId() {
        return id;
    }

    public double getX() {

        return this.x;
    }

    public double getY() {

        return this.y;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setAsBase(boolean b) {

        isBase = b;
    }

    public boolean isBase() {

        return isBase;
    }

    public double getTimeWindowBegin() {
        return timeWindowBegin;
    }

    public double getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public boolean isPickup() {
        return isPickup;
    }

    // //////// links IN //////////

    public void addElementToListIn(GraphLink link) {

        this.linksIn.add(link);
    }

    public void addAllToLinksIn(ArrayList<GraphLink> linksIn) {

        Iterator<GraphLink> iter;

        iter = linksIn.iterator();
        while (iter.hasNext()) {

            addElementToListIn(iter.next());
        }
    }

    /**
     * Returns link (if one exists) from this point to target
     *
     * @param target
     */
    public GraphLink getLinkTo(GraphPoint target) {

        GraphLink result;
        Iterator<GraphLink> it = this.linksOut.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            result = it.next();
            if (target.getX() == result.getEndPoint().getX()
                    && target.getY() == result.getEndPoint().getY())
                return result;
        }

        return null;
    }

    /**
     * Returns link (if one exists) by it's number in LinksIn ArrayList, if
     * number>=LinksIn.size(), the result is null
     *
     * @param number - link's number in LinksIn ArrayList
     */
    public GraphLink getLinkInByNumber(int number) {
        if (number >= this.linksIn.size())
            return null;
        else
            return this.linksIn.get(number);
    }

    public void removeElementFromListIn(GraphLink link) {

        this.linksIn.remove(link);
    }

    public Iterator<GraphLink> getLinksInIterator() {
        return this.linksIn.iterator();
    }

    public int getLinksInSize() {
        return linksIn.size();
    }

    // //////// links OUT //////////

    public void addElementToListOut(GraphLink link) {

        this.linksOut.add(link);
    }

    public void addAllToLinksOut(ArrayList<GraphLink> linksOut) {

        Iterator<GraphLink> iter;

        iter = linksOut.iterator();
        while (iter.hasNext()) {

            addElementToListOut(iter.next());
        }
    }

    /**
     * Returns link (if one exists) from source to this point
     *
     * @param source
     */
    public GraphLink getLinkFrom(GraphPoint source) {

        GraphLink result;
        Iterator<GraphLink> it = this.linksIn.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            result = it.next();
            if (source.equals(result.getStartPoint()))
                return result;
        }

        return null;
    }

    /**
     * Returns link (if one exists) by it's number in LinksOut ArrayList, if
     * number>=LinksOut.size(), the result is null
     *
     * @param number - link's number in LinksOut ArrayList
     */
    public GraphLink getLinkOutByNumber(int number) {

        if (number >= this.linksOut.size())
            return null;
        else
            return this.linksOut.get(number);
    }

    public void removeElementFromListOut(GraphLink link) {

        this.linksOut.remove(link);
    }

    public Iterator<GraphLink> getLinksOutIterator() {

        return this.linksOut.iterator();
    }

    public int getLinksOutSize() {
        return linksOut.size();
    }

    // //////// other functions //////////

    public boolean hasSameCoordinates(GraphPoint other) {

        return (this.getX() == other.getX() && this.getY() == other.getY());
    }

    @Override
    public String toString() {

        return this.name + " [" + this.x + ", " + this.y + "]";
    }

    /**
     * Removes links to and from this point. All to this point should be set too
     * null anyway.
     */
    public void dispose() {
        Iterator<GraphLink> it = linksIn.iterator();
        while (it.hasNext()) {
            GraphLink ln = it.next();
            ln.getStartPoint().removeElementFromListOut(ln);
        }

        it = linksOut.iterator();
        while (it.hasNext()) {
            GraphLink ln = it.next();
            ln.getEndPoint().removeElementFromListIn(ln);
        }
    }

    /*
     * public GraphPoint clone() {
     *
     * GraphPoint newGraphPoint; Iterator<GraphLink> iter;
     *
     * ArrayList<GraphLink> newLinksIn = new ArrayList<GraphLink>();
     * ArrayList<GraphLink> newLinksOut = new ArrayList<GraphLink>();
     *
     * iter = linksIn.iterator(); while (iter.hasNext()) {
     *
     * newLinksIn.add(iter.next().clone()); }
     *
     * iter = linksOut.iterator(); while (iter.hasNext()) {
     *
     * newLinksOut.add(iter.next().clone()); }
     *
     * newGraphPoint = new GraphPoint(x, y, new String(name), isBase);
     * newGraphPoint.addAllToLinksIn(newLinksIn);
     * newGraphPoint.addAllToLinksOut(newLinksOut);
     *
     * return newGraphPoint; }
     */
}
