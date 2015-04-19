package machineLearning.clustering;

import jade.util.leap.Serializable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClusTableObservations implements Serializable {
    private Map<String, List<ClusTableObservation>> observations = new TreeMap<String, List<ClusTableObservation>>();

    public void addObservation(ClusTableObservation observation) {
        List<ClusTableObservation> observationsList = observations
                .get(observation.getStateName());
        if (observationsList == null) {
            observationsList = new LinkedList<>();
            observations.put(observation.getStateName(), observationsList);
        }
        observationsList.add(observation);
    }

    public List<ClusTableObservation> getObservationsAsList() {
        LinkedList<ClusTableObservation> obsers = new LinkedList<>();
        for (List<ClusTableObservation> obs : observations.values()) {
            obsers.addAll(obs);
        }
        return obsers;
    }

    public void clean() {
        this.observations.clear();
    }

}
