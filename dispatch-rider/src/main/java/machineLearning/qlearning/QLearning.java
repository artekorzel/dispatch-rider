package machineLearning.qlearning;

import algorithm.Schedule;
import dtp.simulation.SimInfo;
import dtp.xml.ParseException;
import jade.core.AID;
import machineLearning.MLAlgorithm;
import machineLearning.xml.MLTableStructureParser;
import machineLearning.xml.MLTableToXMLWriter;
import measure.Measure;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;

import java.io.Serializable;
import java.util.*;

public class QLearning extends MLAlgorithm implements Serializable {


    private final MLTableActions<GlobalConfiguration> globalActions = new MLTableGlobalActions();
    private final MLTableActions<HolonConfiguration> holonActions = new MLTableHolonActions();
    private final MLLogger logger = new MLLogger();
    private final Map<AID, MLTableCell> previousCells = new HashMap<AID, MLTableCell>();
    private final Map<AID, Map<String, Double>> previousHolonParameters = new HashMap<AID, Map<String, Double>>();
    private String schema;
    private MLTableStates globalStates;
    private MLTableStates holonStates;
    private RewardFunction globalRewardFunction;
    private double globalFactor;
    private RewardFunction holonRewardFunction;
    private double holonsFactor;
    private boolean globalDeterministic = true;
    private boolean holonDeterministic = true;
    private Map<String, Double> defaultParams;
    // choose global configuration part
    private MLTableCell previousCell;
    private Map<String, Double> previousParameters;

    public QLearning() {
        logger.init();
    }

    @Override
    public void setAlgorithmParameters(Map<String, String> parameters) {
        String params = parameters.get("params");
        if (params != null && params.length() > 0) {
            String[] parts = params.split(";");
            String[] paramsParts;
            Map<String, Double> paramsMap = new HashMap<String, Double>();
            for (String param : parts) {
                paramsParts = param.trim().split("=");
                paramsMap.put(paramsParts[0], new Double(paramsParts[1]));
            }
            setDefaultParams(paramsMap);
        }

    }

    public void setDefaultParams(Map<String, Double> defaultParams) {
        this.defaultParams = defaultParams;
    }

    public void setGlobalDeterministic(boolean globalDeterministic) {
        this.globalDeterministic = globalDeterministic;
    }

    public void setHolonDeterministic(boolean holonDeterministic) {
        this.holonDeterministic = holonDeterministic;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public double getGlobalFactor() {
        return globalFactor;
    }

    public void setGlobalFactor(double globalFactor) {
        this.globalFactor = globalFactor;
    }

    public double getHolonsFactor() {
        return holonsFactor;
    }

    public void setHolonsFactor(double holonsFactor) {
        this.holonsFactor = holonsFactor;
    }

    public void addGlobalAction(String name, GlobalConfiguration value) {
        globalActions.addAction(name, value);
    }

    public void addHolonAction(String name, HolonConfiguration value) {
        holonActions.addAction(name, value);
    }

    public MLTableStates getGlobalStates() {
        return globalStates;
    }

    public void setGlobalStates(MLTableStates globalStates) {
        this.globalStates = globalStates;
    }

    public MLTableStates getHolonStates() {
        return holonStates;
    }

    public void setHolonStates(MLTableStates holonStates) {
        this.holonStates = holonStates;
    }

    public MLTableActions<GlobalConfiguration> getGlobalActions() {
        return globalActions;
    }

    public MLTableActions<HolonConfiguration> getHolonActions() {
        return holonActions;
    }

    public RewardFunction getGlobalActionsFunction() {
        return globalRewardFunction;
    }

    public void setGlobalActionsFunction(String globalActionsFunction) {
        this.globalRewardFunction = new GlobalRewardFunction(
                globalActionsFunction);
    }

    public RewardFunction getHolonActionsFunction() {
        return holonRewardFunction;
    }

    public void setHolonActionsFunction(String holonActionsFunction) {
        this.holonRewardFunction = new HolonRewardFunction(holonActionsFunction);
    }

    public MLLogger getLogger() {
        return logger;
    }

    public void init(MLTableStates states, MLTableActions<?> actions) {
        if (states == null || actions == null)
            return;
        MLTableCell cell;
        List<MLTableCell> cells;
        for (String name : states.getValues().keySet()) {
            cells = new ArrayList<MLTableCell>();
            for (String actionName : actions.getActions().keySet()) {
                cell = new MLTableCell();
                cell.setState(name);
                cell.setAction(actionName);
                cell.setValue(0.0);
                cells.add(cell);
            }
            states.getRows().put(name, cells);
        }
    }

    private double calculateCellValue(MLTableStates states,
                                      RewardFunction function, Map<String, Double> parameters,
                                      Map<String, Double> prevParameters, Map<String, Measure> measures,
                                      AID aid, double factor, String currentState,
                                      MLTableCell previousCell, boolean deterministic) {
        double value;

        if (defaultParams != null)
            parameters.putAll(defaultParams);

        double reward = function.getValue(parameters, prevParameters, measures,
                aid);

        logger.log(parameters, prevParameters, measures,
                reward);

        if (deterministic)
            value = reward + factor * states.getMaxActionValue(currentState);
        else {
            double alpha = 1 / (1 + previousCell.getUseCount());
            value = (1 - alpha)
                    * previousCell.getValue()
                    + alpha
                    * (reward + factor * states.getMaxActionValue(currentState));
        }

        return value;
    }

    // choose holon configuration part

    @Override
    public GlobalConfiguration getGlobalConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration) {
        if (exploration)
            return getGlobalConfiguration(oldSchedules, newSchedules, info);
        else {
            if (globalStates == null)
                return null;
            Map<String, Measure> measures = calculateMeasures(oldSchedules,
                    newSchedules);
            String currentState = globalStates.getCurrentState(measures);
            String action = globalStates.getActionWithMaxValue(currentState);
            return globalActions.getActions().get(action);
        }
    }

    private GlobalConfiguration getGlobalConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info) {

        if (globalStates == null)
            return null;
        Map<String, Measure> measures = calculateMeasures(oldSchedules,
                newSchedules);
        String currentState = globalStates.getCurrentState(measures);
        String action = globalStates.getAction(currentState);
        if (previousCell == null) {
            previousCell = globalStates.getCell(currentState, action);
            // globalStates.updateCellUseCount(currentState, action,
            // previousCell.getUseCount() + 1);
            previousParameters = Helper.getParameters(oldSchedules,
                    newSchedules, info, timestamp);
            return globalActions.getActions().get(action);
        }

        Map<String, Double> parameters = Helper.getParameters(oldSchedules,
                newSchedules, info, timestamp);

        double value = calculateCellValue(globalStates, globalRewardFunction,
                parameters, previousParameters, measures, null, globalFactor,
                currentState, previousCell, globalDeterministic);

        globalStates.updateCellValue(previousCell.getState(),
                previousCell.getAction(), value);
        globalStates.updateCellUseCount(previousCell.getState(),
                previousCell.getAction(), previousCell.getUseCount() + 1);

        previousCell = globalStates.getCell(currentState, action);
        previousParameters = parameters;

        return globalActions.getActions().get(action);
    }

    @Override
    public Map<AID, HolonConfiguration> getHolonsConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info, boolean exploration) {
        if (exploration)
            return getHolonsConfiguration(oldSchedules, newSchedules, info);
        else {
            if (holonStates == null)
                return null;
            Map<AID, HolonConfiguration> configurations = new HashMap<AID, HolonConfiguration>();
            Map<String, Measure> measures = calculateMeasures(oldSchedules,
                    newSchedules);
            Set<AID> aids;
            if (newSchedules != null && newSchedules.size() > 0)
                aids = newSchedules.keySet();
            else
                aids = oldSchedules.keySet();

            for (AID holon : aids) {
                String currentState = holonStates.getCurrentState(measures,
                        holon);
                String action = holonStates.getActionWithMaxValue(currentState);
                configurations
                        .put(holon, holonActions.getActions().get(action));

            }
            return configurations;
        }

    }

    private Map<AID, HolonConfiguration> getHolonsConfiguration(
            Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
            SimInfo info) {

        if (holonStates == null)
            return null;
        Map<AID, HolonConfiguration> configurations = new HashMap<AID, HolonConfiguration>();
        Map<String, Measure> measures = calculateMeasures(oldSchedules,
                newSchedules);
        MLTableCell previousHolonCell;
        Map<AID, Map<String, Double>> holonParams = Helper.getHolonParameters(
                oldSchedules, newSchedules, info, timestamp);
        Set<AID> aids;
        if (newSchedules != null && newSchedules.size() > 0)
            aids = newSchedules.keySet();
        else
            aids = oldSchedules.keySet();
        for (AID holon : aids) {
            String currentState = holonStates.getCurrentState(measures, holon);
            String action = holonStates.getAction(currentState);
            previousHolonCell = previousCells.get(holon);
            if (previousHolonCell == null) {
                previousHolonCell = holonStates.getCell(currentState, action);
                previousCells.put(holon, previousHolonCell);
                holonStates.updateCellUseCount(currentState, action,
                        previousHolonCell.getUseCount() + 1);
                configurations
                        .put(holon, holonActions.getActions().get(action));
                previousHolonParameters.put(holon, holonParams.get(holon));
                continue;
            }
            Map<String, Double> parameters = holonParams.get(holon);

            double value = calculateCellValue(holonStates, holonRewardFunction,
                    parameters, previousHolonParameters.get(holon), measures,
                    holon, holonsFactor, currentState, previousHolonCell,
                    holonDeterministic);

            holonStates.updateCellValue(previousHolonCell.getState(),
                    previousHolonCell.getAction(), value);
            holonStates.updateCellUseCount(previousHolonCell.getState(),
                    previousHolonCell.getAction(),
                    previousHolonCell.getUseCount() + 1);

            previousHolonCell = holonStates.getCell(currentState, action);
            previousCells.put(holon, previousHolonCell);
            previousHolonParameters.put(holon, parameters);

            configurations.put(holon, holonActions.getActions().get(action));

        }
        return configurations;
    }

    @Override
    public void save(String mlTableFileName, String saveFileName)
            throws Exception {
        MLTableToXMLWriter.writeToXML(mlTableFileName, this);
        String parts[] = mlTableFileName.split("\\\\");
        MLTableToXMLWriter.writeToXML(saveFileName + "_"
                + parts[parts.length - 1], this);
        this.getLogger().save(
                saveFileName + "_" + parts[parts.length - 1] + "_log.txt");
    }

    @Override
    public void init(String fileName) {
        try {
            MLTableStructureParser.parse(fileName, this);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
