package machineLearning.qlearning;

import dtp.jade.transport.Calculator;
import jade.core.AID;
import measure.Measure;

import java.util.Map;

public class MLTableGlobalStates extends MLTableStates {



    @Override
    public String getCurrentState(Map<String, Measure> measures, AID aid) {
        aggregatorManager.setMeasures(measures);
        String fun;
        for (String state : values.keySet()) {
            fun = aggregatorManager.insertAggregateValues(values.get(state));
            if (Calculator.calculateBoolExpr(fun) == true) {
                aggregatorManager.aggregationFinished();
                return state;
            }
        }
        aggregatorManager.aggregationFinished();
        throw new IllegalStateException("No state found");
    }
}
