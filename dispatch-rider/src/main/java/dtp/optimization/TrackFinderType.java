package dtp.optimization;

public enum TrackFinderType {

    Astar(Astar.class),
    Dijkstra(Dijkstra.class),
    SimulatedAnnealing(SimulatedAnnealing.class);

    private final Class<? extends TrackFinder> typeClass;

    TrackFinderType(Class<? extends TrackFinder> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends TrackFinder> typeClass() {
        return typeClass;
    }
}
