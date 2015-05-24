package machineLearning.clustering;

import jade.core.AID;
import measure.Measure;
import org.apache.log4j.Logger;

import java.util.Map;

public class ClusTableHolonStates extends ClusTableStates {
    private static final Logger logger = Logger
            .getLogger(ClusTableHolonStates.class);

    @Override
    public String getCurrentState(Map<String, Measure> measures, AID aid) {
        Map<String, Double> currentMeasures = clusTableMeasures
                .getCurrentMeasuresVector(measures, aid);

        String currentState = predictCurrentStateByR(currentMeasures);

        logger.info("Current holon state: " + currentState);

        measurementsHistory.add(currentMeasures);

        return currentState;
    }

    @Override
    public String predictCurrentStateByRCentres(double[] point, String[] measureName, String[] clusterNames) {
        logger.info("Predict current holon state by centres");
        return rutils.predictStateByCentres(point,
                clusterNames, RUtils.HOLON_CENTRES_NAME);
    }

    @Override
    public String predictCurrentStateByRTrees(double[] point, String[] measureName, String[] clusterNames) {
        logger.info("Predict current holon state by tree");
        return rutils.predictStateByTree(point, measureName, clusterNames,
                RUtils.HOLON_TREE_NAME);
    }
}
