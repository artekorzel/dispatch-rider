package pattern;

import java.util.Map;

public class Pdp100Chooser extends Chooser {

    public Pdp100Chooser(PatternCalculator patternCalculator) {
        super(patternCalculator);
    }

    protected boolean getTimeWindowsType() {
        double pattern = patternCalculator.pattern6() * patternCalculator.pattern7();
        if (pattern <= 50) return false;
        if (pattern > 50 && pattern <= 75) return true;
        if (pattern >= 121) return false;
        double pattern2 = patternCalculator.pattern14();
        return pattern2 < 105 || pattern2 >= 105 && pattern2 <= 131;
    }

    public Map<String, Object> getConfiguration() {
        if (!getTimeWindowsType()) return pdp_100_1();
        else return pdp_100_2();
    }

    private Map<String, Object> pdp_100_1() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern6();
        if (pattern <= 3.5) {
            result = pdp_100_1_lc();
        } else if (pattern > 3.5 && pattern <= 4.1) {
            result = pdp_100_1_lrc();
        } else {
            result = pdp_100_1_lr();
        }
        return result;
    }

    private Map<String, Object> pdp_100_1_lc() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if ((pattern > 176 && pattern <= 178) || (pattern > 182 && pattern <= 183)) {
            result = initResultMap(brut2_dist);
        } else if (pattern <= 176 || (pattern > 178 && pattern <= 179.5)) {
            result = initResultMap(brut1);
        } else if (pattern > 179.5 && pattern <= 182) {
            result = initResultMap(brut2);
        } else {
            result = initResultMap(brut1_dist);
        }
        return result;
    }

    private Map<String, Object> pdp_100_1_lr() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if (pattern <= 139) {
            result = initResultMap(brut1);
        } else if (pattern > 139 && pattern <= 141.6) {
            result = initResultMap(brut2);
        } else {
            result = initResultMap(brut1_dist);
        }
        return result;
    }

    private Map<String, Object> pdp_100_1_lrc() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if (pattern <= 161 || (pattern > 165 && pattern <= 172)) {
            result = initResultMap(brut1_dist);
        } else if (pattern > 161 && pattern <= 165) {
            result = initResultMap(brut1);
        } else if (pattern > 165 && pattern <= 176) {
            result = initResultMap(brut2_dist);
        } else {
            result = initResultMap(brut2);
        }
        return result;
    }

    private Map<String, Object> pdp_100_2() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern6();
        if (pattern <= 4.1) {
            result = pdp_100_2_lc();
        } else if (pattern > 5) {
            result = pdp_100_2_lr();
        } else {
            result = pdp_100_2_lrc();
        }
        return result;
    }

    private Map<String, Object> pdp_100_2_lc() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if (pattern <= 177 || (pattern > 180 && pattern <= 195)) {
            result = initResultMap(brut1_dist);
        } else if (pattern > 177 && pattern <= 180) {
            result = initResultMap(brut2_dist);
        } else {
            result = initResultMap(brut1);
        }
        return result;
    }

    private Map<String, Object> pdp_100_2_lrc() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();
        if (pattern <= 178 || pattern > 220) {
            result = initResultMap(brut1);
        } else {
            result = initResultMap(brut1_dist);
        }
        return result;
    }

    private Map<String, Object> pdp_100_2_lr() {
        Map<String, Object> result;
        double pattern = patternCalculator.pattern4();

        if ((pattern > 150 && pattern <= 165) || (pattern > 170.75 && pattern <= 174)) {
            result = initResultMap(brut1_dist);
        } else {
            result = initResultMap(brut1);
        }
        return result;
    }

}
