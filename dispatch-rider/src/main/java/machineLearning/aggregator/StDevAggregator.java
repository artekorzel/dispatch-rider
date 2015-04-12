package machineLearning.aggregator;

import measure.Measure;
import measure.MeasureHelper;

import java.util.LinkedList;

public class StDevAggregator extends MLAggregator {

    @Override
    protected Double aggregate(String name) {
        if (values.containsKey(name))
            return values.get(name);
        Measure measure = measures.get(name);
        Double result = MeasureHelper.standardDeviation(new LinkedList<>(
                measure.getValues().values()));
        if (measure.getValues().size() == 0) {
            values.put(name, 0.0);
            return 0.0;
        }
        values.put(name, result);
        return result;
    }

    @Override
    protected String getName() {
        return "stdev";
    }

}
