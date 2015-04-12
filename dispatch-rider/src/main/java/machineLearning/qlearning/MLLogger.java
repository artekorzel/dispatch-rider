package machineLearning.qlearning;

import jade.core.AID;
import measure.Measure;

import java.io.FileWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MLLogger implements Serializable {


    private List<String> logs;

    public void init() {
        logs = new LinkedList<String>();
    }

    public void log(MLTableStates states, RewardFunction function,
                    Map<String, Double> parameters, Map<String, Double> prevParameters,
                    Map<String, Measure> measures, AID aid, double factor,
                    String currentState, MLTableCell previousCell, Double value) {

        if (parameters.get("holonsCount") > prevParameters.get("holonsCount"))
            logs.add("Dodanie nowego pojazdu - " + value.toString());
        else {
            Measure measure = measures.get("GivenCommissionsNumber");
            for (String holon : measure.getValues().keySet()) {
                if (measure.getValues().get(holon) > 0) {
                    logs.add("Po wymianie zlece� - " + value.toString());
                    break;
                }
            }
        }
    }

    public void save(String fileName) throws Exception {
        FileWriter fw = new FileWriter(fileName, false);
        for (String log : logs) {
            fw.write(log + "\n");
        }
        fw.flush();
        fw.close();
    }
}
