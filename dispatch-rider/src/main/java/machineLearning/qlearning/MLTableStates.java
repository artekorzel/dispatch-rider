package machineLearning.qlearning;

import jade.core.AID;
import machineLearning.aggregator.AggregatorsManager;
import measure.Measure;

import java.io.Serializable;
import java.util.*;

/**
 * Stores states definitions, ad MLTable rows (cells)
 */
public abstract class MLTableStates implements Serializable {


    /**
     * states definitions
     */
    protected final Map<String, String> values = new HashMap<>();
    protected final AggregatorsManager aggregatorManager = new AggregatorsManager();
    private final Random rand = new Random(Calendar.getInstance()
            .getTimeInMillis());
    protected Map<String, List<MLTableCell>> rows = new HashMap<>();
    /**
     * Constant in formula to calculate probability of choosing action, its
     * value is assigned in ml table xml file - look at documentation to see it
     * meaning
     */
    private double k;

    public void addState(String name, String value) {
        values.put(name, value);
    }

    public int size() {
        return values.size();
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public Map<String, List<MLTableCell>> getRows() {
        return rows;
    }

    public void setRows(Map<String, List<MLTableCell>> rows) {
        this.rows = rows;
    }

    private void recalculateProbabilities(String state) {
        List<MLTableCell> cells = rows.get(state);
        double sum = 0.0;
        for (MLTableCell cell : cells) {
            sum += Math.pow(k, cell.getValue());
        }
        for (MLTableCell cell : cells) {
            cell.setProbability(Math.pow(k, cell.getValue()) / sum);
        }
    }

    public String getActionWithMaxValue(String state) {
        List<MLTableCell> cells = rows.get(state);
        double max = 0.0;
        String action = null;
        for (MLTableCell cell : cells)
            if (max < cell.getValue()) {
                max = cell.getValue();
                action = cell.getAction();
            }
        return action;
    }

    public double getMaxActionValue(String state) {
        List<MLTableCell> cells = rows.get(state);
        double max = 0.0;
        for (MLTableCell cell : cells)
            if (max < cell.getValue())
                max = cell.getValue();
        return max;
    }

    public String getAction(String state) {
        recalculateProbabilities(state);
        double val = rand.nextDouble();
        double sum = 0.0;
        for (MLTableCell cell : rows.get(state)) {
            sum += cell.getProbability();
            if (sum >= val)
                return cell.getAction();
        }
        throw new IllegalStateException("No action found");
    }

    public MLTableCell getCell(String state, String action) {
        for (MLTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action))
                return cell;
        }
        throw new IllegalStateException("No cell found");
    }

    public void updateCellValue(String state, String action, Double value) {
        for (MLTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action)) {
                cell.setValue(value);
                return;
            }
        }
        throw new IllegalStateException("No cell found");
    }

    public void updateCellUseCount(String state, String action, int value) {
        for (MLTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action)) {
                cell.setUseCount(value);
                return;
            }
        }
        throw new IllegalStateException("No cell found");
    }

    public String getCurrentState(Map<String, Measure> measures) {
        return getCurrentState(measures, null);
    }

    public abstract String getCurrentState(Map<String, Measure> measures,
                                           AID aid);
}
