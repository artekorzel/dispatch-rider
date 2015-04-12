package machineLearning.aggregator;

import measure.Measure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class AggregatorsManager implements Serializable {

    private final List<MLAggregator> aggregators = new LinkedList<>();

    public AggregatorsManager() {
        try {
            for (MLAggregatorType aggregatorType : MLAggregatorType.values()) {
                aggregators.add(aggregatorType.typeClass().newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
