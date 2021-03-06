package machineLearning.qlearning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MLTableActions<T extends Serializable> implements Serializable {


    protected final Map<String, T> actions = new HashMap<>();

    public void addAction(String name, T conf) {
        actions.put(name, conf);
    }

    public int size() {
        return actions.size();
    }

    public Map<String, T> getActions() {
        return actions;
    }

}
