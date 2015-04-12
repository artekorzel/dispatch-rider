package pattern;

import java.util.Map;

public class ConfigurationChooser {

    private PatternCalculator patternCalculator;

    public static void main(String args[]) {
        ConfigurationChooser chooser = new ConfigurationChooser();
        System.out.println(chooser.getConfiguration("benchmarks\\pdp_100\\lrc108.txt"));
    }

    private int getProblemSize() {
        double result = patternCalculator.getCommissions().size() * 2;
        int size = Integer.toString((int) result).length();
        result = (int) (result / (Math.pow(10, size - 1)));
        result *= Math.pow(10, size - 1);
        return (int) result;
    }

    public Map<String, Object> getConfiguration(String file) {
        patternCalculator = new PatternCalculator(file);
        Map<String, Object> result = null;
        switch (getProblemSize()) {
            case 100:
                result = new Pdp100Chooser(patternCalculator).getConfiguration();
                break;
            case 200:
                result = new Pdp200Chooser(patternCalculator).getConfiguration();
                break;
            default:
                System.out.println("Brak wzorca dla tego problemu");
                System.exit(0);
        }
        return result;
    }
}
