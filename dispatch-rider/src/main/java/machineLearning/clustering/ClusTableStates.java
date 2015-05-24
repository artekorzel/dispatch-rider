package machineLearning.clustering;

import jade.core.AID;
import machineLearning.aggregator.AggregatorsManager;
import measure.Measure;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Stores states definitions, ad MLTable rows (cells)
 */
public abstract class ClusTableStates implements Serializable {

    protected static RUtils rutils;
    /**
     * states definitions
     */
    private static Logger logger = Logger.getLogger(ClusTableStates.class);
    /**
     * State name -> measurment name -> value(center)
     */
    protected final Map<String, Map<String, Double>> values = new HashMap<>();
    protected final AggregatorsManager aggregatorManager = new AggregatorsManager();
    private final Random rand = new Random(Calendar.getInstance()
            .getTimeInMillis());
    protected Map<String, List<ClusTableCell>> rows = new HashMap<>();
    protected ClusTableMeasures clusTableMeasures;
    protected List<Map<String, Double>> measurementsHistory = new LinkedList<>();
    private boolean useTrees;
    /**
     * Constant in formula to calculate probability of choosing action, its
     * value is assigned in ml table xml file - look at documentation to see it
     * meaning
     */
    private double k;

    static {
        rutils = new RUtils();
        rutils.start();
    }

    public void addState(String name, Map<String, Double> value) {
        values.put(name, value);
    }

    public void removeStates() {
        values.clear();
    }

    public void addActionCell(String stateName, String actionName,
                              int useCount, double value) {
        List<ClusTableCell> currentActions = rows.get(stateName);

        if (currentActions == null) {
            currentActions = new LinkedList<>();
            rows.put(stateName, currentActions);
        }

        currentActions.add(new ClusTableCell(stateName, actionName, useCount,
                value));
    }

    public void addDefaultActionCell(String stateName, String actionName) {
        this.addActionCell(stateName, actionName, 0, 0.0);
    }

    public void addDefaultActionCellForEachState(Set<String> actionNames) {
        for (String state : values.keySet()) {
            for (String action : actionNames) {
                this.addDefaultActionCell(state, action);
            }
        }
    }

    public void removeActions() {
        rows.clear();
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

    public Map<String, Map<String, Double>> getValues() {
        return values;
    }

    public Map<String, List<ClusTableCell>> getRows() {
        return rows;
    }

    public void setRows(Map<String, List<ClusTableCell>> rows) {
        this.rows = rows;
    }

    public void setUseTrees(boolean useTrees) {
        this.useTrees = useTrees;
    }

    private void recalculateProbabilities(String state) {
        logger.info(state);
        List<ClusTableCell> cells = rows.get(state);
        double sum = 0.0;
        for (ClusTableCell cell : cells) {
            sum += Math.pow(k, cell.getValue());
        }
        for (ClusTableCell cell : cells) {
            cell.setProbability(Math.pow(k, cell.getValue()) / sum);
        }
    }

    public String getActionWithMaxValue(String state) {
        List<ClusTableCell> cells = rows.get(state);
        double max = 0.0;
        String action = null;
        for (ClusTableCell cell : cells)
            if (max < cell.getValue()) {
                max = cell.getValue();
                action = cell.getAction();
            }
        return action;
    }

    public double getMaxActionValue(String state) {
        List<ClusTableCell> cells = rows.get(state);
        double max = 0.0;
        for (ClusTableCell cell : cells)
            if (max < cell.getValue())
                max = cell.getValue();
        return max;
    }

    public String getAction(String state) {
        recalculateProbabilities(state);
        double val = rand.nextDouble();
        double sum = 0.0;
        for (ClusTableCell cell : rows.get(state)) {
            sum += cell.getProbability();
            if (sum >= val)
                return cell.getAction();
        }
        throw new IllegalStateException("No action found");
    }

    public ClusTableCell getCell(String state, String action) {
        for (ClusTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action))
                return cell;
        }
        throw new IllegalStateException("No cell found");
    }

    public void updateCellValue(String state, String action, Double value) {
        for (ClusTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action)) {
                cell.setValue(value);
                return;
            }
        }
        throw new IllegalStateException("No cell found");
    }

    public void updateCellUseCount(String state, String action, int value) {
        for (ClusTableCell cell : rows.get(state)) {
            if (cell.getAction().equals(action)) {
                cell.setUseCount(value);
                return;
            }
        }
        throw new IllegalStateException("No cell found");
    }

    public ClusTableMeasures getMeasures() {
        return clusTableMeasures;
    }

    public void setMeasures(ClusTableMeasures measures) {
        this.clusTableMeasures = measures;
    }

    public String getCurrentState(Map<String, Measure> measures) {
        return getCurrentState(measures, null);
    }

    public abstract String getCurrentState(Map<String, Measure> measures,
                                           AID aid);

    public List<Map<String, Double>> getMeasurementsHistory() {
        return measurementsHistory;
    }

    public String predictCurrentStateByR(Map<String, Double> measures) {
        double[] point = new double[measures.size()];

        String[] mNames = measures.keySet().toArray(new String[]{});
        String[] cNames = values.keySet().toArray(new String[]{});

        int pointIndex = 0;

        for (String measure : mNames) {
            point[pointIndex] = measures.get(measure);
            pointIndex++;
        }

        logger.info("New point: " + Arrays.toString(point));

        return useTrees ? predictCurrentStateByRTrees(point, mNames, cNames)
                : predictCurrentStateByRCentres(point, mNames, cNames);
    }

    public abstract String predictCurrentStateByRCentres(double[] point,
                                                String[] measureName, String[] clusterNames);

    public abstract String predictCurrentStateByRTrees(double[] point,
                                              String[] measureName, String[] clusterNames);

}
