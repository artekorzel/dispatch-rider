package machineLearning.dummy;

import algorithm.Schedule;
import dtp.simmulation.SimInfo;
import jade.core.AID;
import machineLearning.MLAlgorithm;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import org.apache.poi.ss.formula.eval.NotImplementedException;

import java.util.Map;

public class Dummy extends MLAlgorithm {

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
        System.out.println(parameters);
        throw new NotImplementedException("Implement me c(;");
    }

    @Override
    public void save(String fileName, String saveFileName) throws Exception {
    }

    @Override
    public void init(String fileName) {
    }
}
