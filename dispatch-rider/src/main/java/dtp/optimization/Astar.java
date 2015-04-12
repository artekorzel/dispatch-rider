package dtp.optimization;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Astar implements TrackFinder {

    // heuristics ids
    public final static int MANHATTAN = 1;

    private static Logger logger = Logger.getLogger(Astar.class);
    private Graph graph;
    private int heuristicType;

    public Astar(Graph world, int heuristicType) {
        super();
        this.graph = world;
        this.heuristicType = heuristicType;
    }

    public Astar(Graph world) {
        super();
        this.graph = world;
        this.heuristicType = Astar.MANHATTAN;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph world) {
        this.graph = world;
    }

    public int getHeuristicNumber() {
        return heuristicType;
    }

    public void setHeuristicNumber(int heuristicNumber) {
        this.heuristicType = heuristicNumber;
    }

    private double distance(GraphPoint startPoint, GraphPoint endPoint) {
        return Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2)
                + Math.pow(endPoint.getY() - startPoint.getY(), 2));
    }

    private int heuristics(GraphPoint startPoint, GraphPoint endPoint,
                           int heuristicNumber) {
        int result = -1;

        if (startPoint.equals(endPoint))
            result = 0;
        else if (heuristicNumber == Astar.MANHATTAN) {
            ArrayList<GraphLink> list = graph.getCollectionOfLinks();
            Iterator<GraphLink> it = list.iterator();
            int costs = 0, counter = 0;
            while (it.hasNext()) {
                GraphLink ln = it.next();
                costs += (long) (ln.getCost() * distance(startPoint, endPoint));
                counter++;
            }

            int cost = 0;

            if (counter != 0) {
                cost = costs / counter;
            }

            result = (int) ((Math.pow(
                    graph.getCostMul() * cost + graph.getCostSum(),
                    graph.getCostPow()) + graph.getFreeSum())// *
                    // distance(startPoint,endPoint)
            );
        }

        return result;
    }

    public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {

        try {

            HashSet<GraphPoint> visited = new HashSet<GraphPoint>();
            PriorityWeightQueue q = new PriorityWeightQueue();
            GraphTrack initial = new GraphTrack();
            initial.addPoint(startPoint);
            initial.setPossible(true);
            q.add(initial, heuristics(startPoint, endPoint, heuristicType));
            while (!q.isEmpty()) {
                QueueNode qn = q.get();
                GraphTrack p = (GraphTrack) (qn.getElement());
                p.setPossible(true);

                GraphPoint x = p.getLast();
                if (visited.contains(x))
                    continue;
                if (x.equals(endPoint)) {
                    return p;
                }
                visited.add(x);
                Iterator<GraphLink> it = x.getLinksOutIterator();
                while (it.hasNext()) {
                    GraphLink ln = it.next();
                    GraphTrack tmpTrack = p.Clone();
                    tmpTrack.addPoint(ln.getEndPoint());
                    q.add(tmpTrack,
                            heuristics(x, ln.getEndPoint(), heuristicType)
                                    + heuristics(ln.getEndPoint(), endPoint,
                                    heuristicType));
                }
            }

        } catch (Exception ex) {
            logger.error("Astar: ");
            logger.error(ex);
            return null;
        }
        return new GraphTrack(); // impossible
    }
}
