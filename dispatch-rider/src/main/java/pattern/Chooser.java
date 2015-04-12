package pattern;

import algorithm.BruteForceAlgorithm;
import algorithm.BruteForceAlgorithm2;

import java.util.HashMap;
import java.util.Map;

public abstract class Chooser {

    protected final Object brut1[] = {8, new BruteForceAlgorithm(), false, false};
    protected final Object brut2[] = {8, new BruteForceAlgorithm2(), false, false};
    protected final Object brut1_dist[] = {8, new BruteForceAlgorithm(), false, true};
    protected final Object brut2_dist[] = {8, new BruteForceAlgorithm2(), false, true};
    private final String paramesNames[] = {"STDepth", "algorithm", "chooseWorstCommissionByGlobalTime", "dist"};
    protected PatternCalculator patternCalculator;

    public Chooser(PatternCalculator patternCalculator) {
        this.patternCalculator = patternCalculator;
    }

    protected Map<String, Object> initResultMap(Object params[]) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < paramesNames.length; i++) {
            result.put(paramesNames[i], params[i]);
        }
        return result;
    }

    protected abstract boolean getTimeWindowsType();

    public abstract Map<String, Object> getConfiguration();
}
