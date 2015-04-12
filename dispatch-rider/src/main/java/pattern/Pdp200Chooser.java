package pattern;

import java.util.HashMap;
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
        if (pattern2 > 286) return false;
        return true;
        /*String fileName=patternCalculator.getFileName();
        if(fileName.charAt(fileName.length()-6)!='_') {
            if(fileName.charAt(fileName.length()-7)=='1') return false;
            else return true;
        } else {
            if(fileName.charAt(fileName.length()-9)=='1') return false;
            else return true;
        }*/
    }

    public Map<String, Object> getConfiguration() {
        if (getTimeWindowsType() == false) return pdp_200_1();
        else return pdp_200_2();
    }

    private Map<String, Object> pdp_200_1() {
        Map<String, Object> result = new HashMap<String, Object>();
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
        Map<String, Object> result = new HashMap<String, Object>();
        result = initResultMap(brut1_dist);
        return result;
    }
}
