package machineLearning.clustering;

import jade.core.AID;
import measure.Measure;
import org.apache.log4j.Logger;

import java.util.Map;

public class ClusTableGlobalStates extends ClusTableStates {

    private static final Logger logger = Logger
            .getLogger(ClusTableGlobalStates.class);

    @Override
    public String getCurrentState(Map<String, Measure> measures, AID aid) {
        Map<String, Double> currentMeasures = clusTableMeasures
                .getCurrentMeasuresVector(measures);
        String currentState = predictCurrentStateByR(currentMeasures);

        logger.info("Current global state: " + currentState);

        measurementsHistory.add(currentMeasures);

        return currentState;
    }

    @Override
    public String predictCurrentStateByRCentres(double[] point, String[] measureName, String[] clusterNames) {
        logger.info("Predict current global state by centres");
        return rutils.predictStateByCentres(point,
                clusterNames, RUtils.GLOBAL_CENTRES_NAME);
    }

    @Override
    public String predictCurrentStateByRTrees(double[] point, String[] measureName, String[] clusterNames) {
        logger.info("Predict current global state by tree");
        return rutils.predictStateByTree(point, measureName, clusterNames,
                RUtils.GLOBAL_TREE_NAME);
    }
}
