package machineLearning;

import algorithm.Schedule;
import dtp.simmulation.SimInfo;
import jade.core.AID;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorType;
import measure.MeasureCalculatorsHolder;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Base class for all machine learning algorithms. To add new algorithm, you
 * have to create new class, which: extends this class, will be created in
 * package 'machinelearning.<your class name (down case)>'. Then you can use it
 * in configuration file (mlAlgorithm element), by setting value of 'algorithm'
 * attribute to the name of your class (it is case sensitive!)
 */
public abstract class MLAlgorithm extends MeasureCalculatorsHolder implements Serializable {

    public MLAlgorithm() {
        for (MeasureCalculatorType calculatorType : MeasureCalculatorType.values()) {
            addCalculator(calculatorType);
        }
    }

    protected Map<String, Measure> calculateMeasures(Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules) {
        if (newSchedules != null) {
            for (AID key : oldSchedules.keySet()) {
                if (newSchedules.get(key) == null) {
                    newSchedules.put(key, oldSchedules.get(key));
                }
            }
        }
        Map<String, Measure> measures = new HashMap<>();
        for (MeasureCalculator calc : getCalculators()) {
            measures.put(calc.getName(), calc.calculateMeasure(oldSchedules, newSchedules));
        }
        return measures;
    }

    public abstract GlobalConfiguration getGlobalConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration);

    public abstract Map<AID, HolonConfiguration> getHolonsConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration);

    public abstract void setAlgorithmParameters(Map<String, String> parameters);

    /**
     * This method is invoked after simulation. You should save machine learning
     * knowledge representation (for example QLearinng table)
     *
     * @param fileName     - file from which knowledge representation was read
     * @param saveFileName - simulation result file name (you have to change it, for
     *                     example by add ending string)
     * @throws Exception
     */
    public abstract void save(String fileName, String saveFileName)
            throws Exception;

    /**
     * You should read knowledge representation here
     */
    public abstract void init(String fileName);
}
