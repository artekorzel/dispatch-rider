package pattern;

import dtp.jade.gui.TestConfiguration;
import org.apache.log4j.Logger;

import java.util.Map;

public class ConfigurationChooser {

    private static Logger logger = Logger.getLogger(ConfigurationChooser.class);

    private PatternCalculator patternCalculator;

    private int getProblemSize() {
        double result = patternCalculator.getCommissions().size() * 2;
        int size = Integer.toString((int) result).length();
        result = (int) (result / (Math.pow(10, size - 1)));
        result *= Math.pow(10, size - 1);
        return (int) result;
    }

    public Map<String, Object> getConfiguration(TestConfiguration conf) {
        patternCalculator = new PatternCalculator(conf);
        Map<String, Object> result = null;
        switch (getProblemSize()) {
            case 100:
                result = new Pdp100Chooser(patternCalculator).getConfiguration();
                break;
            case 200:
                result = new Pdp200Chooser(patternCalculator).getConfiguration();
                break;
            default:
                logger.error("Brak wzorca dla tego problemu");
                System.exit(0);
        }
        return result;
    }
}
