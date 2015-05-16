package dtp.optimization;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import org.apache.log4j.Logger;

/**
 * @author Szymon Borgosz
 */
public class SimulatedAnnealing implements TrackFinder {

    private static Logger logger = Logger.getLogger(SimulatedAnnealing.class);

    /**
     * quantity of temperatures
     */
    private int imax;

    /**
     * quantity of iterations for a single temperature value
     */
    private int kmax;

    /**
     * temperature is increased alfa-times (c=c*alfa)
     */
    private double alfa;

    /**
     * initial temperature: 0 <= c <= 1
     */
    private double c;

    public SimulatedAnnealing(int kmax, int imax, double c, double alfa) {
        super();
        this.kmax = kmax;
        this.imax = imax;
        this.alfa = alfa;
        this.c = c;
    }

    public SimulatedAnnealing() {
        super();
        this.kmax = 3;
        this.imax = 3;
        this.alfa = 0.5;
        this.c = 0.5;
    }

    public GraphTrack permute(GraphTrack pi) {
        return new GraphTrack(pi.getFirst(), pi.getLast());
    }

    private double probabilityC(GraphTrack pi, GraphTrack piChanged,
                                double temperature) {
        double cost = pi.getCost();
        double costChanged = piChanged.getCost();
        if (costChanged != 0)
            return (cost / costChanged) * temperature;
        else
            return 1;
    }

    public GraphTrack optimize(GraphTrack pi) {
        try {
            GraphTrack piChanged;
            int i = 0;
            int k;
            do {
                k = 0;
                kmax = 5;
                do {
                    piChanged = permute(pi);
                    if (piChanged.getCost() - pi.getCost() < 0
                            || Math.random() < probabilityC(pi, piChanged, c))
                        pi = piChanged;
                    k++;
                } while (k < kmax);
                c *= alfa;
                i++;
            } while (i < imax);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return pi;
    }

    public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {
        GraphTrack tr = new GraphTrack(startPoint, endPoint);
        return optimize(tr);
    }

    public Graph getGraph() {
        throw new IllegalStateException("class don't have graph");
    }

    public void setGraph(Graph graph) {
        // do nothing
    }
}
