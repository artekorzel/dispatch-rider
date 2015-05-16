package dtp.optimization;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Track optimization based on Dijkstra's algorithm.
 *
 * @author Imploz
 */
public class Dijkstra implements TrackFinder {


    private static Logger logger = Logger.getLogger(Dijkstra.class);

    private Graph graph;

    public Dijkstra(Graph world) {
        super();
        this.graph = world;
    }


    private int getWeight(int cost) {
        return (int) ((Math.pow(graph.getCostMul() * cost + graph.getCostSum(),
                graph.getCostPow()) + graph.getFreeSum())// *
                // distance(startPoint,endPoint)
        );
    }

    public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {
        try {
            GraphTrack result = new GraphTrack();
            HashMap<GraphPoint, Integer> d = new HashMap<GraphPoint, Integer>();
            HashMap<GraphPoint, GraphPoint> precedessor = new HashMap<GraphPoint, GraphPoint>();
            ArrayList<GraphPoint> pointList = new ArrayList<GraphPoint>();
            pointList.addAll(graph.getCollectionOfPoints());

            for (GraphPoint tmp : pointList) {
                d.put(tmp, Integer.MAX_VALUE);
                precedessor.put(tmp, null);
            }
            d.put(startPoint, 0);
            HashSet<GraphPoint> S = new HashSet<GraphPoint>(), Q = new HashSet<GraphPoint>();
            Q.addAll(pointList);
            while (!Q.isEmpty()) {

                // get point with least d[point]
                Iterator<GraphPoint> pit = Q.iterator();
                GraphPoint least = null;
                while (pit.hasNext()) {
                    GraphPoint tmp = pit.next();
                    if (least == null || d.get(tmp) < d.get(least))
                        least = tmp;
                }
                Q.remove(least);

                S.add(least);

                Iterator<GraphLink> lit = least.getLinksOutIterator();
                while (lit.hasNext()) {
                    GraphLink ln = lit.next();
                    GraphPoint pt = ln.getEndPoint();
                    int weight = getWeight((int) ln.getCost());
                    if (d.get(pt) > d.get(least) + weight) {
                        d.put(pt, d.get(least) + weight);
                        precedessor.put(pt, least);
                    }
                }
            }

            if (precedessor.get(endPoint) == null)
                return result;

            GraphPoint pt = endPoint;
            while (pt != startPoint) {
                result.addPointAtPosition(0, pt);
                pt = precedessor.get(pt);
            }
            result.addPointAtPosition(0, pt); // pt==startPoint

            result.setPossible(true);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph world) {
        this.graph = world;
    }

}
