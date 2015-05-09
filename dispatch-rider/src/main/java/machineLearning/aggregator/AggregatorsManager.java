package machineLearning.aggregator;

import measure.Measure;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AggregatorsManager implements Serializable {

    private static Logger logger = Logger.getLogger(AggregatorsManager.class);

    private final List<MLAggregator> aggregators = new LinkedList<>();

    public AggregatorsManager() {
        try {
            for (MLAggregatorType aggregatorType : MLAggregatorType.values()) {
                aggregators.add(aggregatorType.typeClass().newInstance());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void setMeasures(Map<String, Measure> measures) {
        for (MLAggregator aggregator : aggregators)
            aggregator.setMeasures(measures);
    }

    public void aggregationFinished() {
        for (MLAggregator aggregator : aggregators)
            aggregator.aggregationFinished();
    }

    public String insertAggregateValues(String fun) {
        for (MLAggregator aggregator : aggregators)
            fun = aggregator.replace(fun);
        return fun;
    }
}
