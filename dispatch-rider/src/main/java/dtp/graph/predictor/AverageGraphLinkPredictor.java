package dtp.graph.predictor;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import measure.MeasureHelper;

import java.util.LinkedList;
import java.util.List;

public class AverageGraphLinkPredictor extends GraphLinkPredictor {

	@Override
	public double getCost(GraphLink link) {
		if (history.size() == 0)
			return link.getCost();
		GraphLink gLink;
		List<Double> values = new LinkedList<>();
		for (Graph graph : history) {
			gLink = getLink(graph, link);
			values.add(gLink.getCost());
		}
		return MeasureHelper.average(values);
	}

}
