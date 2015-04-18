package dtp.graph.predictor;

import dtp.graph.GraphLink;
import measure.MeasureHelper;

import java.util.LinkedList;
import java.util.List;

public class WeightedAverageGraphLinkPredictor extends GraphLinkPredictor {

	@Override
	public double getCost(GraphLink link) {
		if (history.size() == 0)
			return link.getCost();
		double previousValue = getLink(history.get(history.size() - 1), link)
				.getCost();
		if (history.size() == 1)
			return previousValue;
		List<Double> values = new LinkedList<>();
		for (int i = 0; i < history.size() - 1; i++)
			values.add(getLink(history.get(i), link).getCost());
		return 0.5 * previousValue + 0.5 * MeasureHelper.average(values);
	}

}
