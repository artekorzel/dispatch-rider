package machineLearning;

public class MLAlgorithmFactory {

    public static MLAlgorithm createAlgorithm(String name) {
        try {
            return MLAlgorithmType.valueOf(name).typeClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();//FIXME
        }
        return null;
    }
}
