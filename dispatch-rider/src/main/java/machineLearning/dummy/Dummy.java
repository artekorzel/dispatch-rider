package machineLearning.dummy;

import algorithm.Schedule;
import dtp.simulation.SimInfo;
import jade.core.AID;
import machineLearning.MLAlgorithm;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.eval.NotImplementedException;

import java.util.Map;

public class Dummy extends MLAlgorithm {

    private static Logger logger = Logger.getLogger(Dummy.class);

    @Override
    public GlobalConfiguration getGlobalConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration) {
        return null;
    }

    @Override
    public Map<AID, HolonConfiguration> getHolonsConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration) {
        return null;
    }

    @Override
    public void setAlgorithmParameters(Map<String, String> parameters) {
        logger.info(parameters);
        throw new NotImplementedException("Implement me c(;");
    }

    @Override
    public void save(String fileName, String saveFileName) throws Exception {
    }

    @Override
    public void init(String fileName) {
    }
}
