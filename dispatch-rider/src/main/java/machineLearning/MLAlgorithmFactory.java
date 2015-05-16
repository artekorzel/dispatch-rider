package machineLearning;

import org.apache.log4j.Logger;

public class MLAlgorithmFactory {

    private static Logger logger = Logger.getLogger(MLAlgorithmFactory.class);

    public static MLAlgorithm createAlgorithm(String name) {
        try {
            return MLAlgorithmType.valueOf(name).typeClass().newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
