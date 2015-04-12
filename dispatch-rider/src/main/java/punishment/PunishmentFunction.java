package punishment;

import dtp.commission.Commission;
import dtp.jade.transport.Calculator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Punishment function can be complex. You have to put it's body to constructor.
 * Punishment function format is like this:
 * formula1;conditions1?formula2;conditions2?...?formulan;conditionsn
 * <p/>
 * in conditions you can use any logic operators you use in java (you can use
 * 'true' as well). If you want example, take a look in main
 */
public class PunishmentFunction implements Serializable {


    private final Map<String, String> functions = new HashMap<String, String>();

    public PunishmentFunction(String function) {
        String parts[];
        for (String part : function.split("\\?")) {
            parts = part.split(";");
            if (parts.length == 1)
                functions.put(function, "true");
            else
                functions.put(parts[0].trim(), parts[1].trim());
        }
    }

    public static void main(String args[]) {
        PunishmentFunction function = new PunishmentFunction(
                "x;x>=0?-x;x<0?3;x==8");
        Map<String, Double> def = new HashMap<String, Double>();
        def.put("x", -1.0);
        Commission com = new Commission();
        System.out.println(function.getValue(def, com, 0, true));
    }

    public double getValue(Map<String, Double> defaults, Commission com,
                           double latency, boolean isPickup) {
        Map<String, Double> params;
        Map<String, Double> comParams;
        if (isPickup)
            comParams = com.getPunishmentFunParamsPickup();
        else
            comParams = com.getPunishmentFunParamsDelivery();
        params = new HashMap<String, Double>(comParams);
        Set<String> keys = comParams.keySet();
        for (String key : defaults.keySet())
            if (!keys.contains(key))
                params.put(key, defaults.get(key));
        params.put("latency", latency);

        String fun = null;
        for (String function : functions.keySet()) {
            if (Calculator.calculateBoolExpr(insertParams(
                    functions.get(function), params)))
                if (fun != null)
                    throw new IllegalArgumentException(
                            "Punishment function is not a function");
                else
                    fun = function;
        }

        if (fun == null)
            throw new IllegalArgumentException(
                    "Punishment function don't exists for specified parameters: "
                            + params);

        return Calculator.calculate(insertParams(fun, params));
    }

    private String insertParams(String str, Map<String, Double> params) {
        String result = String.copyValueOf(str.toCharArray());
        for (String key : params.keySet())
            result = result.replace(key, params.get(key).toString());
        return result;
    }
}
