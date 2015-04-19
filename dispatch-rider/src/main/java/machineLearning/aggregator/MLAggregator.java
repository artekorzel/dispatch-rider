package machineLearning.aggregator;

import measure.Measure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all aggregators, which are used in ML RewardFunction. To
 * create new aggregator you have to make new class which will be: created in
 * machineLearning.aggregator package; extended MLAggregator (this class); class
 * name have to end with Aggregator.Then you can use this aggregator in ML
 * reward function, using downcase letters of class name without Aggregator. For
 * example for AvgAggregator, you use avg
 */
public abstract class MLAggregator implements Serializable {


    protected Map<String, Measure> measures;
    protected Map<String, Double> values = new HashMap<>();

    public void setMeasures(Map<String, Measure> measures) {
        this.measures = measures;
    }

    protected abstract Double aggregate(String name);

    protected abstract String getName();

    public void aggregationFinished() {
        values = new HashMap<>();
    }

    private String getCheckedName() {
        String name = getName();
        if (name.endsWith("("))
            return name;
        return name + "(";
    }

    private int[] getSubstringPos(String fun, String name, int begin) {
        int startIndex = fun.indexOf(name, begin);
        int endIndex = fun.indexOf(")", startIndex);
        return new int[]{startIndex, endIndex};
    }

    private String replace(String fun, String name) {
        int begin = 0;
        int[] pos;
        Double value;
        while (begin >= 0 && begin < fun.length()) {
            pos = getSubstringPos(fun, name, begin);
            if (pos[0] == -1 || pos[1] == -1)
                break;
            value = aggregate(fun.substring(pos[0] + name.length(), pos[1]));
            fun = fun.substring(0, pos[0]) + value.toString()
                    + fun.substring(pos[1] + 1);
            begin = pos[0] + value.toString().length();
        }
        return fun;
    }

    public String replace(String fun) {
        return replace(fun, getCheckedName());
    }
}
