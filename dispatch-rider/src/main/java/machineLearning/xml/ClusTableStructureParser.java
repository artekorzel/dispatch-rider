package machineLearning.xml;

import dtp.xml.ParseException;
import dtp.xml.ValidatorErrorHandler;
import machineLearning.clustering.*;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusTableStructureParser {

    private static Logger logger = Logger.getLogger(ClusTableStructureParser.class);

    /**
     * Converts XML boolean attribute to Java boolean type
     *
     * @param value represent boolean attribute as returned by
     *              org.w3c.dom.Element.getAttrbute()
     * @return true if value is "true" or "1"
     */
    private static Boolean stringToBoolean(String value) {
        if ("true".equals(value))
            return true;
        else if ("1".equals(value))
            return true;
        else if ("null".equals(value))
            return null;
        return false;
    }

    private static Boolean attributeToBoolean(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        if (attr.getTextContent().trim().equals("null"))
            return null;
        return stringToBoolean(attr.getTextContent().trim());
    }

    private static Integer attributeToInt(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        if (attr.getTextContent().trim().equals("-1"))
            return null;
        return Integer.valueOf(attr.getTextContent().trim());
    }

    private static Double attributeToDouble(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return Double.valueOf(attr.getTextContent().trim());
    }

    private static String attributeToString(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        if (attr.getTextContent().trim().equals("null"))
            return null;
        return attr.getTextContent().trim();
    }

    private static void parseGlobalStates(Clustering table, Element states)
            throws ParseException {
        NodeList statesList = states.getElementsByTagName("state");
        Element state;
        ClusTableGlobalStates mlStates = new ClusTableGlobalStates();
        mlStates.setK(attributeToDouble(states, "k"));
        for (int i = 0; i < statesList.getLength(); i++) {
            state = (Element) statesList.item(i);

            NodeList measuresList = state.getElementsByTagName("measure");

            Map<String, Double> measures = new HashMap<>();
            Element measure;
            for (int j = 0; j < measuresList.getLength(); j++) {
                measure = (Element) measuresList.item(j);
                measures.put(measure.getAttribute("name"),
                        Double.valueOf(measure.getAttribute("value")));
            }

            mlStates.addState(state.getAttribute("name"), measures);
        }
        mlStates.setMeasures(table.getGlobalMeasures());
        mlStates.setUseTrees(table.isUseTrees());
        table.setGlobalStates(mlStates);
    }

    private static void parseHolonStates(Clustering table, Element states)
            throws ParseException {
        NodeList statesList = states.getElementsByTagName("state");
        Element state;
        ClusTableHolonStates mlStates = new ClusTableHolonStates();
        mlStates.setK(attributeToDouble(states, "k"));
        for (int i = 0; i < statesList.getLength(); i++) {
            state = (Element) statesList.item(i);

            NodeList measuresList = state.getElementsByTagName("measure");

            Map<String, Double> measures = new HashMap<>();
            Element measure;
            for (int j = 0; j < measuresList.getLength(); j++) {
                measure = (Element) measuresList.item(j);
                measures.put(measure.getAttribute("name"),
                        Double.valueOf(measure.getAttribute("value")));
            }

            mlStates.addState(state.getAttribute("name"), measures);
        }
        mlStates.setMeasures(table.getHolonMeasures());
        mlStates.setUseTrees(table.isUseTrees());
        table.setHolonStates(mlStates);
    }

    private static void parseGlobalActions(Clustering table, Element actions)
            throws ParseException {
        table.setGlobalActionsFunction(actions.getAttribute("function"));
        table.setGlobalFactor(attributeToDouble(actions, "factor"));
        table.setGlobalDeterministic(attributeToBoolean(actions,
                "deterministic"));
        NodeList actionsList = actions.getElementsByTagName("action");
        Element action;
        GlobalConfiguration conf;
        for (int i = 0; i < actionsList.getLength(); i++) {
            action = (Element) actionsList.item(i);
            conf = new GlobalConfiguration();
            conf.setChooseWorstCommission(attributeToString(action,
                    "chooseWorstCommission"));
            conf.setChoosingByCost(attributeToBoolean(action, "choosingByCost"));
            conf.setSimulatedTrading(attributeToInt(action,
                    "simulatedTrading"));
            conf.setSTDepth(attributeToInt(action, "STDepth"));
            conf.setType(attributeToBoolean(action, "sendingType"));
            table.addGlobalAction(attributeToString(action, "name"), conf);
        }
    }

    private static void parseHolonActions(Clustering table, Element actions)
            throws ParseException {
        table.setHolonActionsFunction(actions.getAttribute("function"));
        table.setHolonsFactor(attributeToDouble(actions, "factor"));
        table.setHolonDeterministic(attributeToBoolean(actions, "deterministic"));
        NodeList actionsList = actions.getElementsByTagName("action");
        Element action;
        HolonConfiguration conf;
        for (int i = 0; i < actionsList.getLength(); i++) {
            action = (Element) actionsList.item(i);
            conf = new HolonConfiguration();
            conf.setAlgorithm(attributeToString(action, "algorithm"));
            conf.setDist(attributeToBoolean(action, "newCommissionCostByDist"));
            conf.setSimulatedTrading(attributeToBoolean(action,
                    "simulatedTrading"));
            table.addHolonAction(attributeToString(action, "name"), conf);
        }
    }

    private static void parseStructure(Clustering table, Element element)
            throws ParseException {

        Element globalMeasures = (Element) element.getElementsByTagName(
                "globalMeasures").item(0);
        Element holonMeasures = (Element) element.getElementsByTagName(
                "holonMeasures").item(0);
        Element globalStates = (Element) element.getElementsByTagName(
                "globalStates").item(0);
        Element holonStates = (Element) element.getElementsByTagName(
                "holonStates").item(0);
        Element globalActions = (Element) element.getElementsByTagName(
                "globalActions").item(0);
        Element holonActions = (Element) element.getElementsByTagName(
                "holonActions").item(0);

        boolean isGlobalStatesPresent = false;

        if (globalMeasures != null) {

            parseGlobalMeasures(table, globalMeasures);

            if (globalStates != null) {
                isGlobalStatesPresent = true;
                parseGlobalStates(table, globalStates);
                if (globalActions != null) {
                    parseGlobalActions(table, globalActions);
                } else {
                    throw new ParseException(
                            "There's no global actions definition");
                }
            }
        }

        if (holonMeasures != null) {

            parseHolonMeasures(table, holonMeasures);

            if (holonStates != null) {
                parseHolonStates(table, holonStates);
                if (holonActions != null) {
                    parseHolonActions(table, holonActions);
                } else {
                    throw new ParseException(
                            "There's no holon actions definition");
                }
            } else if (!isGlobalStatesPresent) {
                throw new ParseException("There's no states definition part");
            }
        }

    }

    private static void parseHolonMeasures(Clustering table,
                                           Element holonMeasures) {

        NodeList measuresList = holonMeasures.getElementsByTagName("measure");
        Element measure;
        ClusTableHolonMeasures clusMeasures = new ClusTableHolonMeasures();

        for (int i = 0; i < measuresList.getLength(); i++) {
            measure = (Element) measuresList.item(i);
            clusMeasures.addMeasure(measure.getAttribute("name"),
                    measure.getAttribute("value"));

        }
        table.setHolonMeasures(clusMeasures);

    }

    private static void parseGlobalMeasures(Clustering table,
                                            Element globalMeasures) {
        NodeList measuresList = globalMeasures.getElementsByTagName("measure");
        Element measure;
        ClusTableGlobalMeasures clusMeasures = new ClusTableGlobalMeasures();

        for (int i = 0; i < measuresList.getLength(); i++) {
            measure = (Element) measuresList.item(i);
            clusMeasures.addMeasure(measure.getAttribute("name"),
                    measure.getAttribute("value"));

        }
        table.setGlobalMeasures(clusMeasures);

    }

    private static void parseObservations(Clustering table, Element observations)
            throws ParseException {
        NodeList globalObservations = observations
                .getElementsByTagName("globalObservations");
        Element globalEle = (Element) globalObservations.item(0);
        ClusTableObservations clusGlobalObservations = parseObservations(globalEle);
        table.setGlobalObservations(clusGlobalObservations);

        NodeList holonObservations = observations
                .getElementsByTagName("holonObservations");
        Element holonEle = (Element) holonObservations.item(0);
        ClusTableObservations clusHolonObservations = parseObservations(holonEle);
        table.setHolonObservations(clusHolonObservations);
    }

    private static ClusTableObservations parseObservations(Element globalEle)
            throws ParseException {
        ClusTableObservations ctos = new ClusTableObservations();
        NodeList obsList = globalEle.getElementsByTagName("observation");

        Element obs;
        String stateName;
        ClusTableObservation cto;
        for (int i = 0; i < obsList.getLength(); i++) {
            obs = (Element) obsList.item(i);
            stateName = attributeToString(obs, "state");
            cto = new ClusTableObservation(stateName);

            NodeList measuresList = obs.getElementsByTagName("measure");
            Element mes;
            for (int j = 0; j < measuresList.getLength(); j++) {
                mes = (Element) measuresList.item(j);
                cto.addMeasureElement(attributeToString(mes, "name"),
                        attributeToDouble(mes, "value"));
            }
            ctos.addObservation(cto);

        }

        return ctos;
    }

    private static boolean[] parseContent(Clustering table, Element contentEl) {
        Element globalTableContent = (Element) contentEl.getElementsByTagName(
                "globalTableContent").item(0);
        Element holonTableContent = (Element) contentEl.getElementsByTagName(
                "holonTableContent").item(0);
        boolean added[] = new boolean[2];
        if (globalTableContent != null) {
            table.getGlobalStates().setRows(readContent(globalTableContent));
            added[0] = true;
        } else
            added[0] = false;
        if (holonTableContent != null) {
            table.getHolonStates().setRows(readContent(holonTableContent));
            added[1] = true;
        } else
            added[1] = false;
        return added;
    }

    private static Map<String, List<ClusTableCell>> readContent(Element content) {
        Map<String, List<ClusTableCell>> rows = new HashMap<>();
        NodeList nodes = content.getElementsByTagName("state");
        NodeList cells;
        Element state;
        Element cell;
        String stateName;
        ClusTableCell tableCell;
        List<ClusTableCell> cellList;
        for (int i = 0; i < nodes.getLength(); i++) {
            state = (Element) nodes.item(i);
            stateName = state.getAttribute("name");
            cells = state.getElementsByTagName("action");
            cellList = new ArrayList<>();
            for (int j = 0; j < cells.getLength(); j++) {
                cell = (Element) cells.item(j);
                tableCell = new ClusTableCell();
                tableCell.setState(stateName);
                tableCell.setAction(cell.getAttribute("name"));
                tableCell.setUseCount(Integer.parseInt(cell
                        .getAttribute("useCount")));
                tableCell.setValue(Double.parseDouble(cell
                        .getAttribute("value")));
                cellList.add(tableCell);
            }
            rows.put(stateName, cellList);
        }
        return rows;
    }

    public static void parse(String filename, Clustering table)
            throws ParseException {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);

			/* Use embedded location of XML schema to validate document */
            factory.setAttribute(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ValidatorErrorHandler());
            Document document = builder.parse(filename);

            Element root = (Element) document.getElementsByTagName("ClusTable")
                    .item(0);
            table.setSchema(root.getAttribute("xsi:noNamespaceSchemaLocation"));

            table.setLearning(attributeToBoolean(root, "learning"));
            table.setUseTrees(attributeToBoolean(root, "useTrees"));
            table.setMinClusCount(attributeToString(root, "minClustCount"));
            table.setMaxClusCount(attributeToString(root, "maxClustCount"));
            table.setUsePam(attributeToBoolean(root, "usepam"));
            table.setOverwriteConf(attributeToBoolean(root, "overwriteConf"));

            Element structure = (Element) root
                    .getElementsByTagName("structure").item(0);
            parseStructure(table, structure);

            Element contentEl = (Element) root.getElementsByTagName("content")
                    .item(0);
            if (contentEl != null) {
                boolean added[] = parseContent(table, contentEl);
                if (!added[0])
                    table.init(table.getGlobalStates(),
                            table.getGlobalActions());
                if (!added[1])
                    table.init(table.getHolonStates(), table.getHolonActions());
            } else {
                table.init(table.getGlobalStates(), table.getGlobalActions());
                table.init(table.getHolonStates(), table.getHolonActions());
            }

            Element obs = (Element) root.getElementsByTagName("observations")
                    .item(0);
            parseObservations(table, obs);

        } catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static void main(String args[]) {
        try {
            Clustering table = new Clustering();
            table.init("clustable.xml");

            logger.info(table.isLearning());
            logger.info("UseTrees: " + table.isUseTrees());
            logger.info(table.getGlobalObservations()
                    .getObservationsAsList().get(0).getMeasure());
            ClusTableToXMLWriter.writeToXML("proba.xml", table);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
