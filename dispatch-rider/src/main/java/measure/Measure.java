package measure;

import jade.core.AID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * It holds measure values for each holon
 */
public class Measure implements Serializable {


    // private final Map<AID, Double> values = new HashMap<AID, Double>();
    private final Map<String, Double> values = new HashMap<String, Double>();

    private int timestamp;
    private int comId;

    public void put(AID aid, Double value) {
        values.put(aid.getLocalName(), value);
    }

    public void put(String aidLocalName, Double value) {
        values.put(aidLocalName, value);
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Set by Distributor
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getComId() {
        return comId;
    }

    /**
     * Set by Distributor
     */
    public void setComId(int comId) {
        this.comId = comId;
    }
}
