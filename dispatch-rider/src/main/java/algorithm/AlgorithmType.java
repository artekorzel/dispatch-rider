package algorithm;

public enum AlgorithmType {

    BruteForceAlgorithm(BruteForceAlgorithm.class),
    BruteForceAlgorithm2(BruteForceAlgorithm2.class);

    private final Class<? extends Algorithm> typeClass;

    AlgorithmType(Class<? extends Algorithm> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends Algorithm> typeClass() {
        return typeClass;
    }
}
