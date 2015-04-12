package dtp.graph.predictor;

import dtp.graph.GraphLink;

public class StandardGraphLinkPredictor extends GraphLinkPredictor {

    @Override
    public double getCost(GraphLink link) {
        return link.getCost();
    }

}
