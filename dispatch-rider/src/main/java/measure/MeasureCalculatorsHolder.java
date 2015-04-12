package measure;

import dtp.commission.Commission;
import dtp.simmulation.SimInfo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for MeasureCalculators. It uses reflection to create calculators by
 * their name. It simplifies adding new calculators (more is described in
 * MeasureCalculator class)
 */
public class MeasureCalculatorsHolder implements Serializable {


    private final List<MeasureCalculator> calculators = new LinkedList<>();
    private final List<String> visualizationMeasuresNames = new LinkedList<>();
    protected int timestamp;
    private int timeGap;

    public void addVisualizationMeasuresNames(String name) {
        visualizationMeasuresNames.add(name);
    }

    public List<String> getVisualizationMeasuresNames() {
        return visualizationMeasuresNames;
    }

    public void addCalculator(MeasureCalculatorType calculatorType) throws IllegalArgumentException {
        try {
            MeasureCalculator calculator = calculatorType.typeClass().newInstance();
            calculators.add(calculator);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong measure name: " + calculatorType);
        }
    }

    public List<MeasureCalculator> getCalculators() {
        return calculators;
    }

    public int getTimeGap() {
        return timeGap;
    }

    public void setTimeGap(int timeGap) {
        this.timeGap = timeGap;
    }

    public void setSimInfo(SimInfo info) {
        for (MeasureCalculator calc : calculators)
            calc.setSimInfo(info);
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
        for (MeasureCalculator calc : calculators)
            calc.setTimestamp(timestamp);
    }

    public void setCommissions(List<Commission> commissions) {
        for (MeasureCalculator calc : calculators)
            calc.setCommissions(commissions);
    }
}
