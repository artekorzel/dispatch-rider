package algorithm.STLike;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Map;

public class ExchangeAlgorithmsFactory implements Serializable {

    private static Logger logger = Logger.getLogger(ExchangeAlgorithmsFactory.class);

    private ExchangeAlgorithm algAfterComAdd;
    private ExchangeAlgorithm algWhenCantAdd;

    private ExchangeAlgorithm getAlgorithm(String name,
                                           Map<String, String> params) {
        try {
            ExchangeAlgorithm alg = ExchangeAlgorithmType.valueOf(name).typeClass().newInstance();
            alg.setParameters(params);
            return alg;
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }
        return null;
    }

    public void setAlgAfterComAdd(String name, Map<String, String> params) {
        this.algAfterComAdd = getAlgorithm(name, params);
    }

    public void setAlgWhenCantAdd(String name, Map<String, String> params) {
        this.algWhenCantAdd = getAlgorithm(name, params);
    }

    public ExchangeAlgorithm getAlgAfterComAdd() {
        return algAfterComAdd;
    }

    public ExchangeAlgorithm getAlgWhenCantAdd() {
        return algWhenCantAdd;
    }

}
