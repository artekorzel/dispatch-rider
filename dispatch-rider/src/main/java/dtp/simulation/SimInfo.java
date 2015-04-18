package dtp.simulation;

import algorithm.*;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.optimization.TrackFinder;
import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import punishment.PunishmentFunction;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Map;

/**
 * Klasa zawierajaca podstawowe informacje o symulacji
 * tj. wspolrzedne magazynu, liczba holonow
 */
public class SimInfo implements Serializable {

    private Point2D.Double depot;

    private double deadline;

    private double maxLoad;

    private MeasureCalculatorsHolder calculatorsHolder;

    private PunishmentFunction punishmentFunction;
    private Map<String, Double> defaultPunishmentFunValues;
    private Double delayLimit;
    private int holons;
    private boolean firstComplexSTResultOnly;

    private MLAlgorithm mlAlgorithm;
    private boolean exploration;

    private boolean STAfterGraphChange;

    private ExchangeAlgorithmsFactory exchangeAlgFactory;

    private Schedule scheduleCreator = new BasicSchedule(null);

    private Brute2Sorter brute2Sorter;

    public SimInfo(Point2D.Double depot, double deadline, double maxLoad) {

        this.depot = depot;
        this.deadline = deadline;
        this.maxLoad = maxLoad;
    }

    public Brute2Sorter getBrute2Sorter() {
        return brute2Sorter;
    }

    public void setBrute2Sorter(Brute2Sorter sorter) {
        this.brute2Sorter = sorter;
    }

    public ExchangeAlgorithmsFactory getExchangeAlgFactory() {
        return exchangeAlgFactory;
    }

    public void setExchangeAlgFactory(
            ExchangeAlgorithmsFactory exchangeAlgFactory) {
        this.exchangeAlgFactory = exchangeAlgFactory;
    }

    public boolean isSTAfterGraphChange() {
        return STAfterGraphChange;
    }

    public void setSTAfterGraphChange(boolean sTAfterGraphChange) {
        STAfterGraphChange = sTAfterGraphChange;
    }

    public void setTrackFinder(TrackFinder finder, GraphLinkPredictor predictor) {
        if (finder == null) {
            this.scheduleCreator = new BasicSchedule(null);
        } else {
            this.scheduleCreator = new GraphSchedule(null, finder, predictor);
        }
    }

    public MLAlgorithm getMlAlgorithm() {
        return mlAlgorithm;
    }

    public void setMlAlgorithm(MLAlgorithm mlTable) {
        this.mlAlgorithm = mlTable;
    }

    public boolean isExploration() {
        return exploration;
    }

    public void setExploration(boolean exploration) {
        this.exploration = exploration;
    }

    public Schedule getScheduleCreator() {
        return scheduleCreator;
    }

    public Schedule createSchedule(Algorithm algorithm) {
        return scheduleCreator.createSchedule(algorithm);
    }

    public Schedule createSchedule(Algorithm algorithm, int currentCommission,
                                   double creationTime) {
        return scheduleCreator.createSchedule(algorithm, currentCommission,
                creationTime);
    }

    public boolean isFirstComplexSTResultOnly() {
        return firstComplexSTResultOnly;
    }

    public void setFirstComplexSTResultOnly(boolean firstComplexSTResultOnly) {
        this.firstComplexSTResultOnly = firstComplexSTResultOnly;
    }

    public int getHolons() {
        return holons;
    }

    public void setHolons(int holons) {
        this.holons = holons;
    }

    public Double getDelayLimit() {
        return delayLimit;
    }

    public void setDelayLimit(Double delayLimit) {
        this.delayLimit = delayLimit;
    }

    public Map<String, Double> getDefaultPunishmentFunValues() {
        return defaultPunishmentFunValues;
    }

    public void setDefaultPunishmentFunValues(
            Map<String, Double> defaultPunishmentFunValues) {
        this.defaultPunishmentFunValues = defaultPunishmentFunValues;
    }

    public PunishmentFunction getPunishmentFunction() {
        return punishmentFunction;
    }

    public void setPunishmentFunction(String punishmentFunction) {
        if (punishmentFunction == null)
            return;
        this.punishmentFunction = new PunishmentFunction(punishmentFunction);
    }

    public Point2D.Double getDepot() {

        return depot;
    }

    public void setDepot(Point2D.Double depot) {

        this.depot = depot;
    }

    public double getDeadline() {

        return deadline;
    }

    public double getMaxLoad() {

        return maxLoad;
    }

    public void setMaxLoad(double maxLoad) {

        this.maxLoad = maxLoad;
    }

    public MeasureCalculatorsHolder getCalculatorsHolder() {
        return calculatorsHolder;
    }

    public void setCalculatorsHolder(MeasureCalculatorsHolder calculatorsHolder) {
        this.calculatorsHolder = calculatorsHolder;
    }

}
