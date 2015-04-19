package machineLearning.clustering;

import jade.core.AID;
import measure.Measure;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public abstract class ClusTableMeasures implements Serializable {

    /**
     * Mapuje nazwe measurmentu na jego measurment, np. S1 ->
     * avg(AverageMinDistBetweenAllCommissions)
     */
    protected Map<String, String> values = new TreeMap<String, String>();

    public void addMeasure(String key, String value) {
        values.put(key, value);
    }

    public Map<String, Double> getCurrentMeasuresVector(
            Map<String, Measure> measures) {
        return getCurrentMeasuresVector(measures, null);
    }

    public abstract Map<String, Double> getCurrentMeasuresVector(
            Map<String, Measure> measures, AID aid);

    public Map<String, String> getValues() {
        return values;
    }
}
