package machineLearning.clustering;

import dtp.jade.transport.Calculator;
import dtp.xml.ParseException;
import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

public class RUtils {
    public static final String CELLS_NAME = "cells";
    public static final String ROWS_NAME = "rNames";
    public static final String COLS_NAME = "cNames";

    public static final String GLOBAL_CENTRES_NAME = "globalCentres";
    public static final String HOLON_CENTRES_NAME = "holonCentres";

    public static final String GLOBAL_TREE_NAME = "dtreeGlobal";
    public static final String HOLON_TREE_NAME = "dtreeHolon";

    private static final String STATE = "State";

    public static final String[] REQUIRED_PACKAGES = {"fpc", "clue", "rpart"};

    private static final Logger logger = Logger.getLogger(RUtils.class);

    private static Rengine rengine = null;

    public synchronized Rengine start() {
        if (rengine == null) {
            if (!Rengine.versionCheck()) {
                logger.error("** Version mismatch - Java files don't match library version.");
                System.exit(1);
            }
            rengine = new Rengine(new String[]{}, false, new TextConsole());

            if (!rengine.waitForR()) {
                logger.error("Cannot load R");
                return null;
            }

            // load required packages
            for (String lib : REQUIRED_PACKAGES) {
                rengine.eval("library(" + lib + ")");
            }
        }

        return rengine;

    }

    public REXP kmeans(double[] values, String[] rnames, String[] cnames,
                       boolean usePam, String minClusCountExpr, String maxClusCountExpr) {
        // evaluate min and max clus count
        minClusCountExpr = minClusCountExpr.replaceAll("N",
                String.valueOf(rnames.length));
        maxClusCountExpr = maxClusCountExpr.replaceAll("N",
                String.valueOf(rnames.length));

        double minClusCount = Calculator.calculate(minClusCountExpr);
        double maxClusCount = Calculator.calculate(maxClusCountExpr);

        logger.info("MIN clusters count: " + minClusCount
                + " MAX clusters count: " + maxClusCount);

        // calculate

        logger.info("values:\n" + Arrays.toString(values));
        rengine.assign(CELLS_NAME, values);
        rengine.assign(ROWS_NAME, rnames);
        rengine.assign(COLS_NAME, cnames);

        String matrixCmd = "x <- matrix(" + CELLS_NAME + ", nrow="
                + rnames.length + ", ncol=" + cnames.length
                + ", byrow=TRUE, dimnames=list(" + ROWS_NAME + "," + COLS_NAME
                + "))";
        logger.info("Creating matrix command: " + matrixCmd);

        REXP result = rengine.eval(matrixCmd);

        String kmeansCmd = "km <- pamk(x," + ((int) minClusCount) + ":"
                + ((int) maxClusCount) + ",usepam="
                + String.valueOf(usePam).toUpperCase() + ")";
        logger.info("Kmeans command: " + kmeansCmd);
        REXP km = rengine.eval(kmeansCmd);

        // rengine.eval("print(km$nc)");

        logger.info("Optimal number of clusters : "
                + ((REXP) km.asVector().get(1)).asInt());

        return km;

    }

    public REXP kmeans(double[] values, String[] rnames, String[] cnames) {
        return kmeans(values, rnames, cnames, true, "2", "sqrt(N)");
    }

    public List<Map<String, Double>> getCentres(REXP result, String[] cnames) {
        List<Map<String, Double>> centres = new LinkedList<Map<String, Double>>();

        logger.info("getting centres from:\n" + result.getContent());

        double[] values = ((REXP) ((REXP) result.asVector().get(0)).asVector()
                .get(0)).asDoubleArray();

        logger.info("all centres:\n" + Arrays.toString(values));

        int measuresCount = cnames.length;
        int clustersCount = values.length / cnames.length;
        logger.info("Created clusters count: " + clustersCount);

        for (int i = 0; i < clustersCount; i++) {
            Map<String, Double> c = new HashMap<String, Double>();
            int measureNumber = i;
            for (int name = 0; name < measuresCount; name++) {
                c.put(cnames[name], values[measureNumber]);
                measureNumber += clustersCount;
            }
            centres.add(c);
        }

        logger.info("clusters:\n" + centres);

        return centres;
    }

    public Map<String, List<List<Double>>> getStatesAssignment(
            REXP clusteringResult, String[] stateNames) {
        Map<String, List<List<Double>>> assignment = new HashMap<String, List<List<Double>>>();

        RVector pamkAsVector = ((REXP) clusteringResult.asVector().get(0))
                .asVector();

        int vectorsCount = pamkAsVector.size();

        logger.debug("Has " + vectorsCount + " vectors inside");

        for (int i = 0; i < vectorsCount; i++) {
            logger.debug("vector #"
                    + i
                    + " "
                    + Arrays.toString(((REXP) pamkAsVector.get(i))
                    .asDoubleArray()));
        }

        // element is a number of point which is set as cluster
        // size is number of the states
        int[] clusNumber = ((REXP) pamkAsVector.get(1)).asIntArray();

        if (clusNumber.length != stateNames.length) {
            logger.error("States count doesn't match, returning");
            return null;
        }

        // element says to which cluster was assigned point number index in the
        // table
        int[] assignmentToState = ((REXP) pamkAsVector.get(2)).asIntArray();

        // all points from the clusters
        double[][] points = ((REXP) pamkAsVector.get(9)).asDoubleMatrix();

        // i - is the index in the points table
        // assignmentToState[i] - is the mapping to the state
        // stateNames[assignmentToState[i]] - is the name of the state
        for (int i = 0; i < assignmentToState.length; i++) {
            double[] nextPoint = points[i];
            String whichState = stateNames[assignmentToState[i] - 1];

            List<List<Double>> actualList = assignment.get(whichState);
            if (actualList == null) {
                actualList = new ArrayList<List<Double>>();
                assignment.put(whichState, actualList);
            }

            List<Double> nextP = new ArrayList<Double>();
            // poor
            for (double p : nextPoint) {
                nextP.add(p);
            }
            actualList.add(nextP);

        }

        return assignment;
    }

    public void buildCentres(Map<String, Map<String, Double>> holonCentres,
                             Map<String, Map<String, Double>> globalCentres) {
        buildCentres(holonCentres, HOLON_CENTRES_NAME);
        buildCentres(globalCentres, GLOBAL_CENTRES_NAME);
    }

    private void buildCentres(Map<String, Map<String, Double>> centres,
                              String centresStructureName) {
        String cmd = null;

        final String centresArrayName = "centresArray";
        final String centresMatrixName = "centresMatrix";

        int statesCount = centres.size();
        int measuresCount = centres.values().iterator().next().size();

        double[] centerVals = new double[statesCount * measuresCount];

        String[] mNames = centres.values().iterator().next().keySet()
                .toArray(new String[]{});
        String[] cNames = centres.keySet().toArray(new String[]{});

        int centersIndex = 0;

        for (String measure : mNames) {
            // add value for this measure for eache cluster
            for (String cluster : cNames) {
                centerVals[centersIndex] = centres.get(cluster).get(measure);
                centersIndex++;
            }
        }

        logger.info("Centers: " + Arrays.toString(centerVals));

        // create kmeans object and assign
        rengine.assign(centresArrayName, centerVals);
        cmd = centresMatrixName + " <- matrix(" + centresArrayName + ",nrow="
                + centerVals.length / measuresCount + ",ncol=" + measuresCount
                + ")";
        System.out.println("Centers matrix cmd: " + cmd);
        rengine.eval(cmd);
        cmd = centresStructureName + " <- list(centers = " + centresMatrixName
                + ")";
        System.out.println("Centers list cmd: " + cmd);
        rengine.eval(cmd);
        cmd = "class(" + centresStructureName + ") <- \"kmeans\"";
        System.out.println("Kmeans from list cmd: " + cmd);
        rengine.eval(cmd);

        rengine.eval("print(" + centresStructureName + ")");

    }

    public String predictStateByCentres(double[] point, String[] measureName,
                                        String[] clusterNames, String centresStructureName) {
        String cmd = "";
        final String pointMatrixName = "pmatrix";
        final String newPointName = "newPoint";

        // first assing point as matrix
        rengine.assign(newPointName, point);
        cmd = pointMatrixName + " <- matrix(" + newPointName + ",nrow=1,ncol="
                + point.length + ")";
        System.out.println("Matrix from new point cmd: " + cmd);
        rengine.eval(cmd);

        rengine.eval("print(pmatrix)");

        // predict cluster number
        cmd = "cl_predict(" + centresStructureName + "," + pointMatrixName
                + ")";
        System.out.println("Predict cmd: " + cmd);
        REXP predict = rengine.eval(cmd);

        System.out.println("Cluster index: " + predict.asInt());

        return clusterNames[predict.asInt() - 1];

    }

    public void buildDecisionTrees(ClusTableObservations globalObservations,
                                   ClusTableObservations holonObservations) {
        buildDecisionTree(globalObservations, GLOBAL_TREE_NAME);
        buildDecisionTree(holonObservations, HOLON_TREE_NAME);
    }

    private void buildDecisionTree(ClusTableObservations observations,
                                   String treeName) {
        int rows = observations.getObservationsAsList().size();
        int cols = observations.getObservationsAsList().get(0).getMeasure()
                .size() + 1;

        double[] values = new double[rows * cols];
        int i = 0;
        for (ClusTableObservation obs : observations.getObservationsAsList()) {
            for (String key : obs.getMeasure().keySet()) {
                values[i] = obs.getMeasure().get(key);
                i++;
            }
            values[i] = Double.valueOf(obs.getStateName().replace("S", ""));
            i++;
        }

        String[] cNames = new String[cols];
        int j = 0;
        for (String key : observations.getObservationsAsList().get(0)
                .getMeasure().keySet()) {
            cNames[j] = key;
            j++;
        }
        cNames[j] = STATE;

        rengine.assign(CELLS_NAME, values);
        rengine.assign(COLS_NAME, cNames);

        String matrixCmd = "obs <- matrix(" + CELLS_NAME + ", nrow=" + rows
                + ", ncol=" + cols + ", byrow=TRUE, dimnames=list(1:" + rows
                + "," + COLS_NAME + "))";

        REXP result = rengine.eval(matrixCmd);

        String dataFrameCmd = "obsDF <- data.frame(obs)";
        REXP resultDF = rengine.eval(dataFrameCmd);

        System.out.println("Data frame for " + treeName);
        rengine.eval("print(obsDF)");

        StringBuilder formula = new StringBuilder();
        formula.append(STATE + " ~ ");
        int k = 0;
        for (k = 0; k < cols - 2; k++) {
            formula.append(cNames[k]).append(" + ");
        }
        formula.append(cNames[k]);

        String deciSionTreeCommand = treeName + " <- rpart(" + formula
                + ",data=obsDF,minsplit=1, minbucket=1)";
        REXP tree = rengine.eval(deciSionTreeCommand);

        rengine.eval("print(" + treeName + ")");

//		rengine.eval("X11()");
//		rengine.eval("plot(" + treeName + ")");
//		rengine.eval("text(" + treeName + ",use.n=TRUE,all=TRUE,cex=0.8)");
//
//		rengine.eval("X11()");
//		rengine.eval("plot(" + treeName + ")");
//		rengine.eval("text(" + treeName + ",use.n=TRUE,all=TRUE,cex=0.8)");

    }

    public String predictStateByTree(double[] point, String[] measureNames,
                                     String[] clusterNames, String treeStructureName) {

        String cmd = null;
        final String testDataName = "testData";
        final String toPredict = "toPredict";
        final String toPredictDataFrame = "toPredictDataFrame";

        int nrow = 1;
        int ncol = point.length;

        rengine.assign(COLS_NAME, measureNames);

        rengine.assign(testDataName, point);

        cmd = toPredict + " <- matrix(" + testDataName + ", nrow=" + nrow
                + ", ncol=" + ncol + ", byrow=TRUE, dimnames=list(1:" + nrow
                + "," + COLS_NAME + "))";

        System.out.println("Test data matrix cmd: " + cmd);
        rengine.eval(cmd);

        cmd = toPredictDataFrame + " <- data.frame(" + toPredict + ")";
        rengine.eval(cmd);

        final String treePredict = "treePredict";

        cmd = treePredict + " <- predict(" + treeStructureName + ","
                + toPredictDataFrame + ",type=\"vector\")";
        System.out.print("Predict command: " + cmd);
        REXP predictionResult = rengine.eval(cmd);

        rengine.eval("print(" + treePredict + ")");

        int predictedState = (int) (predictionResult.asDoubleArray()[0]);

        System.out.println("Tree predict result: "
                + clusterNames[predictedState]);

        return clusterNames[predictedState];

    }

    public void end() {
        if (rengine != null) {
            rengine.end();
        }
    }

    class TextConsole implements RMainLoopCallbacks {
        public void rWriteConsole(Rengine re, String text, int oType) {
            System.out.print(text);
        }

        public void rBusy(Rengine re, int which) {
            System.out.println("rBusy(" + which + ")");
        }

        public String rReadConsole(Rengine re, String prompt, int addToHistory) {
            System.out.print(prompt);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        System.in));
                String s = br.readLine();
                return (s == null || s.length() == 0) ? s : s + "\n";
            } catch (Exception e) {
                System.out.println("jriReadConsole exception: "
                        + e.getMessage());
            }
            return null;
        }

        public void rShowMessage(Rengine re, String message) {
            System.out.println("rShowMessage \"" + message + "\"");
        }

        public String rChooseFile(Rengine re, int newFile) {
            FileDialog fd = new FileDialog(new Frame(),
                    (newFile == 0) ? "Select a file" : "Select a new file",
                    (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
            fd.show();
            String res = null;
            if (fd.getDirectory() != null)
                res = fd.getDirectory();
            if (fd.getFile() != null)
                res = (res == null) ? fd.getFile() : (res + fd.getFile());
            return res;
        }

        public void rFlushConsole(Rengine re) {
        }

        public void rLoadHistory(Rengine re, String filename) {
        }

        public void rSaveHistory(Rengine re, String filename) {
        }
    }

    public static void main(String[] args) throws ParseException {

        // FOR TEST PURPOSES
        RUtils rUtils = new RUtils();
        rengine = rUtils.start();

        // init clustering
        Clustering clustering = new Clustering();
        clustering.init("clustable.xml");

        String[] clusterNames = {"S0", "S1"};
        String[] measureName = {"M1", "M2", "M3"};
        double[] point = new double[]{4.483691224846486, 211.48925374026493,
                59.33064516129032};

        String result = null;
        // test predict
        if (clustering.isUseTrees()) {
            // test predict by tree
            result = rUtils.predictStateByTree(point, measureName,
                    clusterNames, GLOBAL_TREE_NAME);

            log(result);

        } else {

            // test predict by centres
            result = rUtils.predictStateByCentres(point, measureName,
                    clusterNames, GLOBAL_CENTRES_NAME);

            log(result);

        }

        while (true) {
        }
        // System.exit(0);
    }

    private static void log(Object o) {
        System.out.println(o.toString());
    }

}
