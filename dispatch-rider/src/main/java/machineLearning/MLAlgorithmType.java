package machineLearning;

import machineLearning.dummy.Dummy;
import machineLearning.qlearning.QLearning;

public enum MLAlgorithmType {

    Dummy(Dummy.class),
    QLearning(QLearning.class);


    private final Class<? extends MLAlgorithm> typeClass;

    MLAlgorithmType(Class<? extends MLAlgorithm> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends MLAlgorithm> typeClass() {
        return typeClass;
    }
}
