package machineLearning.aggregator;

public enum MLAggregatorType {

    AvgAggregator(AvgAggregator.class),
    StDevAggregator(StDevAggregator.class),
    SumAggregator(SumAggregator.class);

    private Class<? extends MLAggregator> typeClass;

    MLAggregatorType(Class<? extends MLAggregator> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends MLAggregator> typeClass() {
        return typeClass;
    }
}
