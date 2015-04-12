package algorithm.STLike;

public enum ExchangeAlgorithmType {

    SimulatedTrading(SimulatedTrading.class);

    private final Class<? extends ExchangeAlgorithm> typeClass;

    ExchangeAlgorithmType(Class<? extends ExchangeAlgorithm> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends ExchangeAlgorithm> typeClass() {
        return typeClass;
    }
}
