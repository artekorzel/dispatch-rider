package machineLearning.xml;

import machineLearning.qlearning.MLTableCell;
import machineLearning.qlearning.MLTableStates;
import machineLearning.qlearning.QLearning;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.Map;

public class MLTableToXMLWriter {

    private static Logger logger = Logger.getLogger(MLTableToXMLWriter.class);

    private static Document dom;

    public static void writeToXML(String fileName, QLearning table)
            throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();
        dom = db.newDocument();
        dom.setXmlVersion("1.0");
        Element mlTable = dom.createElement("MLTable");
        mlTable.setAttribute("xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
        mlTable.setAttribute("xsi:noNamespaceSchemaLocation", table.getSchema());

        appendStructure(mlTable, table);
        Element content = null;
        if (table.getGlobalStates() != null) {
            content = dom.createElement("content");
            Element globalTableContent = dom
                    .createElement("globalTableContent");
            addContent(table.getGlobalStates(), globalTableContent);
            content.appendChild(globalTableContent);
        }
        if (table.getHolonStates() != null) {
            if (content == null)
                content = dom.createElement("content");
            Element holonTableContent = dom.createElement("holonTableContent");
            addContent(table.getHolonStates(), holonTableContent);
            content.appendChild(holonTableContent);
        }
        if (content == null)
            throw new IllegalStateException("content can't be null");
        mlTable.appendChild(content);
        dom.appendChild(mlTable);
        saveDocument(fileName);
    }

    private static void appendStructure(Element mlTable, QLearning table) {
        Element structure = dom.createElement("structure");
        if (table.getGlobalStates() != null) {
            Element globalStates = dom.createElement("globalStates");
            addStates(table.getGlobalStates(), globalStates);
            Element globalActions = dom.createElement("globalActions");
            addGlobalActions(table, globalActions);
            structure.appendChild(globalStates);
            structure.appendChild(globalActions);
        }
        if (table.getHolonStates() != null) {
            Element holonStates = dom.createElement("holonStates");
            addStates(table.getHolonStates(), holonStates);
            Element holonActions = dom.createElement("holonActions");
            addHolonActions(table, holonActions);
            structure.appendChild(holonStates);
            structure.appendChild(holonActions);
        }
        mlTable.appendChild(structure);
    }

    private static void addStates(MLTableStates states, Element statesEl) {
        statesEl.setAttribute("k", Double.toString(states.getK()));
        Map<String, String> values = states.getValues();
        Element state;
        for (String name : values.keySet()) {
            state = dom.createElement("state");
            state.setAttribute("name", name);
            state.setAttribute("value", values.get(name));
            statesEl.appendChild(state);
        }
    }

    private static void addGlobalActions(QLearning table, Element actionsEl) {
        actionsEl.setAttribute("function", table.getGlobalActionsFunction()
                .getRewardFunction());
        actionsEl.setAttribute("factor",
                Double.toString(table.getGlobalFactor()));

        Map<String, GlobalConfiguration> actions = table.getGlobalActions()
                .getActions();
        Element actionEl;
        GlobalConfiguration conf;
        for (String name : actions.keySet()) {
            actionEl = dom.createElement("action");
            actionEl.setAttribute("name", name);
            conf = actions.get(name);
            if (conf.isType() != null)
                actionEl.setAttribute("sendingType", conf.isType().toString());
            if (conf.isChoosingByCost() != null)
                actionEl.setAttribute("choosingByCost", conf.isChoosingByCost()
                        .toString());
            if (conf.getSimulatedTrading() != null)
                actionEl.setAttribute("simulatedTrading", conf
                        .getSimulatedTrading().toString());
            if (conf.getSTDepth() != null)
                actionEl.setAttribute("STDepth", conf.getSTDepth().toString());
            if (conf.getChooseWorstCommission() != null)
                actionEl.setAttribute("chooseWorstCommission",
                        conf.getChooseWorstCommission());
            actionsEl.appendChild(actionEl);
        }
    }

    private static void addHolonActions(QLearning table, Element actionsEl) {
        actionsEl.setAttribute("function", table.getHolonActionsFunction()
                .getRewardFunction());
        actionsEl.setAttribute("factor",
                Double.toString(table.getHolonsFactor()));

        Map<String, HolonConfiguration> actions = table.getHolonActions()
                .getActions();
        Element actionEl;
        HolonConfiguration conf;
        String[] algorithmName;
        for (String name : actions.keySet()) {
            actionEl = dom.createElement("action");
            actionEl.setAttribute("name", name);
            conf = actions.get(name);
            if (conf.isDist() != null)
                actionEl.setAttribute("newCommissionCostByDist", conf.isDist()
                        .toString());
            if (conf.getAlgorithm() != null) {
                algorithmName = conf.getAlgorithm().getClass().getName()
                        .split("\\.");
                actionEl.setAttribute("algorithm",
                        algorithmName[algorithmName.length - 1]);
            }
            if (conf.getSimulatedTrading() != null) {
                actionEl.setAttribute("simulatedTrading", conf
                        .getSimulatedTrading().toString());
            }
            actionsEl.appendChild(actionEl);
        }
    }

    private static void addContent(MLTableStates states, Element contentEl) {
        Map<String, List<MLTableCell>> rows = states.getRows();
        Element state;
        Element action;
        for (String stateName : rows.keySet()) {
            state = dom.createElement("state");
            state.setAttribute("name", stateName);
            for (MLTableCell cell : rows.get(stateName)) {
                action = dom.createElement("action");
                action.setAttribute("name", cell.getAction());
                action.setAttribute("useCount", cell.getUseCount().toString());
                action.setAttribute("value", cell.getValue().toString());
                state.appendChild(action);
            }
            contentEl.appendChild(state);
        }
    }

    private static void saveDocument(String fileName) throws Exception {

        Source source = new DOMSource(dom);

        File file = new File(fileName);
        Result result = new StreamResult(file);

        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                "4");

        xformer.transform(source, result);

    }

    public static void main(String arrgs[]) {
        try {
            MLTableToXMLWriter.writeToXML("proba.xml", null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
