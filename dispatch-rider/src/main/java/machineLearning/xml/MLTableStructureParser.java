package machineLearning.xml;

import dtp.xml.ParseException;
import dtp.xml.ValidatorErrorHandler;
import machineLearning.qlearning.MLTableCell;
import machineLearning.qlearning.MLTableGlobalStates;
import machineLearning.qlearning.MLTableHolonStates;
import machineLearning.qlearning.QLearning;
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

public class MLTableStructureParser {

    private static Logger logger = Logger.getLogger(MLTableStructureParser.class);

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

    private static void parseGlobalStates(QLearning table,
                                          Element states) throws ParseException {
        NodeList statesList = states.getElementsByTagName("state");
        Element state;
        MLTableGlobalStates mlStates = new MLTableGlobalStates();
        mlStates.setK(attributeToDouble(states, "k"));
        for (int i = 0; i < statesList.getLength(); i++) {
            state = (Element) statesList.item(i);
            mlStates.addState(state.getAttribute("name"),
                    state.getAttribute("value"));
        }
        table.setGlobalStates(mlStates);
    }

    private static void parseHolonStates(QLearning table,
                                         Element states) throws ParseException {
        NodeList statesList = states.getElementsByTagName("state");
        Element state;
        MLTableHolonStates mlStates = new MLTableHolonStates();
        mlStates.setK(attributeToDouble(states, "k"));
        for (int i = 0; i < statesList.getLength(); i++) {
            state = (Element) statesList.item(i);
            mlStates.addState(state.getAttribute("name"),
                    state.getAttribute("value"));
        }
        table.setHolonStates(mlStates);
    }

    private static void parseGlobalActions(QLearning table,
                                           Element actions) throws ParseException {
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

    private static void parseHolonActions(QLearning table,
                                          Element actions) throws ParseException {
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

    private static void parseStructure(QLearning table, Element element)
            throws ParseException {

        Element globalStates = (Element) element.getElementsByTagName(
                "globalStates").item(0);
        Element holonStates = (Element) element.getElementsByTagName(
                "holonStates").item(0);
        Element globalActions = (Element) element.getElementsByTagName(
                "globalActions").item(0);
        Element holonActions = (Element) element.getElementsByTagName(
                "holonActions").item(0);

        boolean isGlobalStatesPresent = false;
        if (globalStates != null) {
            isGlobalStatesPresent = true;
            parseGlobalStates(table, globalStates);
            if (globalActions != null) {
                parseGlobalActions(table, globalActions);
            } else {
                throw new ParseException("There's no global actions definition");
            }
        }
        if (holonStates != null) {
            parseHolonStates(table, holonStates);
            if (holonActions != null) {
                parseHolonActions(table, holonActions);
            } else {
                throw new ParseException("There's no holon actions definition");
            }
        } else if (!isGlobalStatesPresent) {
            throw new ParseException("There's no states definition part");
        }
    }

    private static boolean[] parseContent(QLearning table,
                                          Element contentEl) {
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

    private static Map<String, List<MLTableCell>> readContent(Element content) {
        Map<String, List<MLTableCell>> rows = new HashMap<>();
        NodeList nodes = content.getElementsByTagName("state");
        NodeList cells;
        Element state;
        Element cell;
        String stateName;
        MLTableCell tableCell;
        List<MLTableCell> cellList;
        for (int i = 0; i < nodes.getLength(); i++) {
            state = (Element) nodes.item(i);
            stateName = state.getAttribute("name");
            cells = state.getElementsByTagName("action");
            cellList = new ArrayList<>();
            for (int j = 0; j < cells.getLength(); j++) {
                cell = (Element) cells.item(j);
                tableCell = new MLTableCell();
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

    public static void parse(String filename, QLearning table)
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

            Element root = (Element) document.getElementsByTagName("MLTable")
                    .item(0);
            table.setSchema(root.getAttribute("xsi:noNamespaceSchemaLocation"));
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
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static void main(String args[]) {
        try {
            QLearning table = new QLearning();
            table.init("table.xml");
            MLTableToXMLWriter.writeToXML("proba.xml", table);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
