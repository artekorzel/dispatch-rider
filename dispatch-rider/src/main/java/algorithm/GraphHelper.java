package algorithm;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.optimization.TrackFinder;

import java.awt.geom.Point2D;

public class GraphHelper {

    private static GraphPoint getGraphPoint(Point2D.Double point,
                                            TrackFinder finder) {
        Graph graph = finder.getGraph();
        return graph.getPointByCoordinates(point.getX(), point.getY());
    }

    public static double calculateDistance(Point2D.Double com1,
                                           Point2D.Double com2, TrackFinder trackFinder) {
        if (com1 == null || com2 == null)
            return 0.0;
        GraphTrack track = trackFinder.findTrack(
                getGraphPoint(com1, trackFinder),
                getGraphPoint(com2, trackFinder));

        return track.getDist();
    }

    public static double calculateTime(Point2D.Double com1,
                                       Point2D.Double com2, TrackFinder trackFinder) {
        if (com1 == null || com2 == null)
            return 0.0;
        GraphTrack track = trackFinder.findTrack(
                getGraphPoint(com1, trackFinder),
                getGraphPoint(com2, trackFinder));

        return track.getCost();
    }
}
