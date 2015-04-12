package dtp.optimization;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;

import java.io.Serializable;

/**
 * @author Grzegorz
 */
public interface TrackFinder extends Serializable {

    GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint);

    Graph getGraph();

    void setGraph(Graph graph);
}
