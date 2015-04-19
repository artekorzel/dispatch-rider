package dtp.graph.predictor;

import dtp.graph.GraphLink;

public class MovingAverageGraphLinkPredictor extends GraphLinkPredictor {

    @Override
    public double getCost(GraphLink link) {
        if (history.size() == 0)
            return link.getCost();

        double alpha = 1 - 2.0 / (history.size() + 1);

        double counter = 0.0;
        double denominator = 0.0;
        int power = 0;
        double weight;
        for (int i = history.size() - 1; i >= 0; i--) {
            weight = Math.pow(alpha, power);
            counter += weight * getLink(history.get(i), link).getCost();
            denominator += weight;
        }

        return counter / denominator;
    }

}
