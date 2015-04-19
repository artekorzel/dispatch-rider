package machineLearning.clustering;

import dtp.jade.transport.Calculator;
import jade.core.AID;
import machineLearning.aggregator.AggregatorsManager;
import measure.Measure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class RewardFunction implements Serializable {

    protected final AggregatorsManager aggregatorManager = new AggregatorsManager();
    private final Map<String, String> functions = new HashMap<>();
    private final String rewardFunction;

    /**
     * @param function Function formula. You can use many variables which are
     *                 initialized in machineLearning.Helper. Apart from that if you
     *                 want to get access to previous value of variable you use '_'.
     *                 You can use constants, which are defined in test
     *                 configuration. Function formula have format:
     *                 fun1;cond1?fun2;cond2?...?funn;condn
     */
    public RewardFunction(String function) {
        rewardFunction = function;
        String parts[];
        for (String part : function.split("\\?")) {
            parts = part.split(";");
            if (parts.length == 1)
                functions.put(function, "true");
            else
                functions.put(parts[0].trim(), parts[1].trim());
        }
    }

    public String getRewardFunction() {
        return rewardFunction;
    }

    protected abstract String insertMeasures(String fun,
                                             Map<String, Measure> measures, AID aid);

    public double getValue(Map<String, Double> parameters,
                           Map<String, Double> prevParameters, Map<String, Measure> measures,
                           AID aid) {
        String fun = null;
        String expr;
        for (String function : functions.keySet()) {

            expr = insertParams(functions.get(function), prevParameters, "_");
            expr = insertParams(expr, parameters, null);
            expr = insertMeasures(expr, measures, aid);

            if (Calculator.calculateBoolExpr(expr))
                if (fun != null)
                    throw new IllegalArgumentException(
                            "Reward function is not a function");
                else
                    fun = function;
        }

        if (fun == null)
            throw new IllegalArgumentException(
                    "Reward function don't exists for specified parameters: "
                            + parameters + " and old parameters: "
                            + prevParameters);

        fun = insertParams(fun, parameters, "_");
        fun = insertParams(fun, parameters, null);
        fun = insertMeasures(fun, measures, aid);

        return Calculator.calculate(fun);
    }

    private String insertParams(String str, Map<String, Double> params,
                                String suffix) {
        String result = String.copyValueOf(str.toCharArray());
        if (suffix == null)
            suffix = "";
        for (String key : params.keySet())
            result = result.replace(suffix + key, params.get(key).toString());
        return result;
    }
}
