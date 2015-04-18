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

        String currentState = predictCurrentStateByR(currentMeasures, aid);

        logger.info("Current holon state: " + currentState);

        measurmentsHistory.add(currentMeasures);

        return currentState;
    }
}
