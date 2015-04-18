package dtp.graph.predictor;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for predictors. To add new predictor you have to create new class
 * which: extends this class; it's name ends with GraphLinkPredictor. Then you
 * can use it in test configuration file using name of your class without
 * GraphLinkPredictor, for example for StandardGraphLinkPredictor, you use
 * 'Standard' in configuration
 */
public abstract class GraphLinkPredictor implements Serializable {

    /**
     * History of graph changes
     */
    protected final List<Graph> history = new LinkedList<Graph>();

    /**
     * Determines max length of history list
     */
    private int historyMaxSize = 4;

    public void setHistoryMaxSize(int maxSize) {
        this.historyMaxSize = maxSize;
    }

    public Graph getGraph() {
        return history.get(history.size() - 1);
    }

    public void addGraphToHistory(Graph graph) {
        if (history.size() >= historyMaxSize) {
            history.remove(0);
        }
        history.add(graph);
    }

    public GraphLink getLink(Graph graph, GraphLink link) {
        GraphPoint gp1 = link.getStartPoint();
        GraphPoint gp2 = link.getEndPoint();

        gp1 = graph.getPointByCoordinates(gp1.getX(), gp1.getY());
        gp2 = graph.getPointByCoordinates(gp2.getX(), gp2.getY());

        return gp1.getLinkTo(gp2);

    }

    public abstract double getCost(GraphLink link);
}
