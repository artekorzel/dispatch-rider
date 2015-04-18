package gui.map;

import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;

import java.awt.*;

public class HolonGraphLink extends GraphLink {

    private Color color;

    public HolonGraphLink(Color color, GraphPoint begin, GraphPoint end, double cost) {
        super(begin, end, cost);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
