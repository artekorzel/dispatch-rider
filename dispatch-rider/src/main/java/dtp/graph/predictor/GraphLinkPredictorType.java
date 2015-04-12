package dtp.graph.predictor;

public enum GraphLinkPredictorType {
    Standard(StandardGraphLinkPredictor.class);

    private final Class<? extends GraphLinkPredictor> typeClass;

    GraphLinkPredictorType(Class<? extends GraphLinkPredictor> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends GraphLinkPredictor> typeClass() {
        return typeClass;
    }
}
