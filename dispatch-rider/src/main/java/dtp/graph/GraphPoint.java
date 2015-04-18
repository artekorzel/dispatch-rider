package dtp.graph;

/**
 * @author Szymon Borgosz
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphPoint implements Serializable {

    private final double x, y;
    private final List<GraphLink> linksOut;
    private String name;
    private boolean isBase;
    private int id;
    private double timeWindowBegin, timeWindowEnd;
    private boolean isPickup;

    public GraphPoint(double xVal, double yVal) {
        this.linksOut = new ArrayList<>();
        this.x = xVal;
        this.y = yVal;
        // zaokraglij do dwoch miejsc po przecinku
        double xValRound = ((int) (100 * xVal)) / 100d;
        double yValRound = ((int) (100 * yVal)) / 100d;
        this.name = "pt_" + xValRound + "_" + yValRound;
        this.isBase = false;
    }

    public GraphPoint(double xVal, double yVal, String name) {
        this.linksOut = new ArrayList<>();
        this.x = xVal;
        this.y = yVal;
        this.name = name;
        this.isBase = false;
    }

    public GraphPoint(double xVal, double yVal, String name, boolean isBase, int id) {
        this.linksOut = new ArrayList<>();
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
        return x == pt.getX() && y == pt.getY()
                && name.equalsIgnoreCase(pt.getName());
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

    /**
     * Returns link (if one exists) from this point to target
     */
    public GraphLink getLinkTo(GraphPoint target) {

        GraphLink result;
        for (GraphLink aLinksOut : this.linksOut) {
            result = aLinksOut;
            if (target.getX() == result.getEndPoint().getX()
                    && target.getY() == result.getEndPoint().getY())
                return result;
        }

        return null;
    }

    public void addElementToListOut(GraphLink link) {

        this.linksOut.add(link);
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

    public Iterator<GraphLink> getLinksOutIterator() {

        return this.linksOut.iterator();
    }

    public int getLinksOutSize() {
        return linksOut.size();
    }

    public boolean hasSameCoordinates(GraphPoint other) {

        return (this.getX() == other.getX() && this.getY() == other.getY());
    }

    @Override
    public String toString() {

        return this.name + " [" + this.x + ", " + this.y + "]";
    }
}
