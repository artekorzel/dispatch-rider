package machineLearning.qlearning;

import dtp.jade.transport.Calculator;
import jade.core.AID;
import measure.Measure;

import java.util.Map;

public class MLTableHolonStates extends MLTableStates {


    @Override
    public String getCurrentState(Map<String, Measure> measures, AID aid) {
        aggregatorManager.setMeasures(measures);
        String fun;
        for (String state : values.keySet()) {
            fun = aggregatorManager.insertAggregateValues(values.get(state));
            fun = insertHolonMeasures(fun, measures, aid);
            if (Calculator.calculateBoolExpr(fun)) {
                aggregatorManager.aggregationFinished();
                return state;
            }
        }
        aggregatorManager.aggregationFinished();
        throw new IllegalStateException("No state found");
    }

    private String insertHolonMeasures(String fun,
                                       Map<String, Measure> measures, AID aid) {

        for (String measure : measures.keySet()) {
            if (measures.get(measure).getValues().get(aid) != null)
                fun = fun.replace(measure, measures.get(measure).getValues()
                        .get(aid).toString());
        }

        return fun;
    }
}
