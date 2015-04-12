package adapter;

public enum AdapterType {

    MitrovicMinic(MitrovicMinic.class),
    Pankratz(Pankratz.class),
    Standard(Standard.class);

    private final Class<? extends Adapter> typeClass;

    AdapterType(Class<? extends Adapter> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends Adapter> typeClass() {
        return typeClass;
    }
}
