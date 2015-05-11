package measure;

import jade.core.AID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * It holds measure values for each holon
 */
public class Measure implements Serializable {

    private String name;

    private Map<String, Double> values = new HashMap<>();

    private int timestamp;
    private int comId;

    public void put(AID aid, Double value) {
        values.put(aid.getLocalName(), value);
    }

    public void put(String aidLocalName, Double value) {
        values.put(aidLocalName, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValues(Map<String, Double> values) {
        this.values.putAll(values);
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }
}
