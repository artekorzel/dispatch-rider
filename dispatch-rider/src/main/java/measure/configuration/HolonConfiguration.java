package measure.configuration;

import algorithm.Algorithm;
import algorithm.AlgorithmType;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * This class contains parameters, which can be changed in specified holon.
 */
public class HolonConfiguration implements Serializable {

    private static Logger logger = Logger.getLogger(HolonConfiguration.class);

    private Boolean simulatedTrading;

    /**
     * Algorithm which is responsible for inserting new commissions into
     * schedule
     */
    private Algorithm algorithm;

    /**
     * This parameters determines how cost of new commission should be
     * calculated by holon. If you set it to false, cost of commission is equal
     * to increase of time (summary time of realization all commissions in
     * schedule) . If you set it to true, first increase of distance is
     * calculated and then sum of cost functions of transport agents are used.
     */
    private Boolean dist;

    public Boolean getSimulatedTrading() {
        return simulatedTrading;
    }

    public void setSimulatedTrading(Boolean simulatedTrading) {
        this.simulatedTrading = simulatedTrading;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setAlgorithm(String algorithmName) {
        try {
            this.algorithm = AlgorithmType.valueOf(algorithmName).typeClass().newInstance();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public Boolean isDist() {
        return dist;
    }

    public void setDist(Boolean dist) {
        this.dist = dist;
    }

}
