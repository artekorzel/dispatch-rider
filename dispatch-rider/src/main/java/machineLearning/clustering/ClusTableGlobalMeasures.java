package machineLearning.clustering;

import jade.core.AID;
import machineLearning.aggregator.AggregatorsManager;
import measure.Measure;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ClusTableGlobalMeasures extends ClusTableMeasures {

    private static Logger logger = Logger.getLogger(ClusTableGlobalMeasures.class);

    private AggregatorsManager aggregatorManager = new AggregatorsManager();

    public static void main(String[] args) {
        ClusTableGlobalMeasures mes = new ClusTableGlobalMeasures();
        mes.addMeasure("M1", "avg(WaitTime)");
        Map<String, Measure> measures = new HashMap<String, Measure>();

        Measure m1 = new Measure();
        m1.put("a1", 12.0);
        m1.put("a2", 8.0);

        measures.put("WaitTime", m1);

        logger.info(mes.getCurrentMeasuresVector(measures));


    }

    @Override
    public Map<String, Double> getCurrentMeasuresVector(
            Map<String, Measure> measures, AID aid) {
        aggregatorManager.setMeasures(measures);
        Map<String, Double> result = new TreeMap<String, Double>();

        String sValue = null;
        for (String measure : this.values.keySet()) {
            sValue = aggregatorManager.insertAggregateValues(this.values.get(measure));
            result.put(measure, Double.valueOf(sValue));
        }
        aggregatorManager.aggregationFinished();

        return result;
    }

}
