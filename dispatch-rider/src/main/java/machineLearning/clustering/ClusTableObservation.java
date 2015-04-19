package machineLearning.clustering;

import jade.util.leap.Serializable;

import java.util.Map;
import java.util.TreeMap;

public class ClusTableObservation implements Serializable {
	
	private String stateName;
	// maesure name -> value, e.g. M1 -> 17.37
	private Map<String, Double> measure = new TreeMap<>();

	public ClusTableObservation(String stateName) {
		super();
		this.stateName = stateName;
	}

	public void addMeasureElement(String measureName, double value) {
		measure.put(measureName, value);
	}

	public String getStateName() {
		return stateName;
	}

	public Map<String, Double> getMeasure() {
		return measure;
	}
}
