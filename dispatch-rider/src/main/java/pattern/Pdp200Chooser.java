package pattern;

import java.util.Map;

public class Pdp200Chooser extends Chooser {

    public Pdp200Chooser(PatternCalculator patternCalculator) {
        super(patternCalculator);
    }

    protected boolean getTimeWindowsType() {
        double pattern = patternCalculator.pattern6() * patternCalculator.pattern7();
        if (pattern <= 60) return false;
        if (pattern >= 150 && pattern <= 165) return true;
        if (pattern > 240) return true;
        double pattern2 = patternCalculator.pattern14();
        return pattern2 <= 286;
    }

    public Map<String, Object> getConfiguration() {
        if (!getTimeWindowsType()) return pdp_200_1();
        else return pdp_200_2();
    }

    private Map<String, Object> pdp_200_1() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if (pattern <= 375) {
            result = initResultMap(brut1);
        } else if (pattern > 375 && pattern <= 416) {
            result = initResultMap(brut1_dist);
        } else if (pattern > 416 && pattern <= 420) {
            result = initResultMap(brut2);
        } else {
            result = initResultMap(brut2_dist);
        }
        return result;
    }

    private Map<String, Object> pdp_200_2() {
        Map<String, Object> result;
        result = initResultMap(brut1_dist);
        return result;
    }
}
