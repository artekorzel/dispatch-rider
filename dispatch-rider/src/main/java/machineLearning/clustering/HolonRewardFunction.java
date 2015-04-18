package machineLearning.clustering;

import jade.core.AID;
import measure.Measure;

import java.util.Map;

public class HolonRewardFunction extends RewardFunction {

	public HolonRewardFunction(String function) {
		super(function);
	}

	@Override
	protected String insertMeasures(String fun, Map<String, Measure> measures,
			AID aid) {
		aggregatorManager.setMeasures(measures);
		fun = aggregatorManager.insertAggregateValues(fun);
		for (String name : measures.keySet()) {

			try {
				fun = fun.replace(name, measures.get(name).getValues().get(aid)
						.toString());
			} catch (NullPointerException e) {
				fun = fun.replace(name, "0");
			}
		}
		aggregatorManager.aggregationFinished();
		return fun;
	}

}
