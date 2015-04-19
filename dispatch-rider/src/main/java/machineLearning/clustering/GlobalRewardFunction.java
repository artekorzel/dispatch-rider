package machineLearning.clustering;

import jade.core.AID;
import measure.Measure;

import java.util.Map;

public class GlobalRewardFunction extends RewardFunction {

    public GlobalRewardFunction(String function) {
        super(function);
    }

    @Override
    protected String insertMeasures(String fun, Map<String, Measure> measures,
                                    AID aid) {
        aggregatorManager.setMeasures(measures);

        fun = aggregatorManager.insertAggregateValues(fun);

        aggregatorManager.aggregationFinished();
        return fun;
    }

}
