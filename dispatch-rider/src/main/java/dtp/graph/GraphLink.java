package dtp.graph;

import java.io.Serializable;

/**
 * Represents link between two points.
 *
 * @author kony.pl
 */
public class GraphLink implements Serializable, Comparable<GraphLink> {
    protected GraphPoint from;
    protected GraphPoint to;
    protected double cost;

    public GraphLink() {
        super();
    }

    public GraphLink(GraphPoint from, GraphPoint to, double cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.from.addElementToListOut(this);
    }

    @Override
    public int compareTo(GraphLink obj) {
        if (this.from.equals(obj.from) && this.to.equals(obj.to)
                && this.cost == obj.cost)
            return 0;
        return 1;
    }

    /**
     * Gets the start point of the link.
     *
     * @return reference to Point object representing start point
     */
    public GraphPoint getStartPoint() {

        return this.from;
    }

    /**
     * Gets the end point of the link.
     *
     * @return reference to Point object representing end point
     */
    public GraphPoint getEndPoint() {

        return this.to;
    }

    /**
     * Gets the cost of the link.
     *
     * @return integer value representing the cost
     */
    public double getCost() {

        return this.cost;
    }

    /**
     * Sets link's cost
     *
     * @param cost
     */
    public void setCost(int cost) {

        this.cost = cost;
    }

    /**
     * @return geometrical lenght of link
     */
    public double distance() {

        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2)
                + Math.pow(from.getY() - to.getY(), 2));
    }

    public boolean equals(GraphLink ln) {
        return this.cost == ln.getCost() && this.from.equals(ln.getStartPoint())
                && this.to.equals(ln.getEndPoint());
    }

    @Override
    public String toString() {
        return "<(" + from.getX() + "," + from.getY() + ") -> (" + to.getX()
                + "," + to.getY() + ")";
    }
}
