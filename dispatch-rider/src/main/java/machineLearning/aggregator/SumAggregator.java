package machineLearning.aggregator;

import measure.Measure;

public class SumAggregator extends MLAggregator {

    @Override
    protected Double aggregate(String name) {
        if (values.containsKey(name))
            return values.get(name);
        Measure measure = measures.get(name);
        Double result = 0.0;
        for (double value : measure.getValues().values()) {
            result += value;
        }
        if (measure.getValues().size() == 0) {
            values.put(name, 0.0);
            return 0.0;
        }
        values.put(name, result);
        return result;
    }

    @Override
    protected String getName() {
        return "sum";
    }

}
