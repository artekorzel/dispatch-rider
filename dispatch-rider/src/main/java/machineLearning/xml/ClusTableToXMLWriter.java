package machineLearning.xml;

import machineLearning.clustering.*;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
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

public class ClusTableToXMLWriter {
	public static final String ROOT_NAME = "ClusTable";

	private static Document dom;

	public static void writeToXML(String fileName, Clustering table)
			throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		dom = db.newDocument();
		dom.setXmlVersion("1.0");
		Element clusTable = dom.createElement(ROOT_NAME);
		clusTable.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		clusTable.setAttribute("xsi:noNamespaceSchemaLocation",
				table.getSchema());
		clusTable.setAttribute("learning", String.valueOf(table.isLearning()));
		clusTable.setAttribute("useTrees", String.valueOf(table.isUseTrees()));
		clusTable.setAttribute("minClustCount", table.getMinClusCount());
		clusTable.setAttribute("maxClustCount", table.getMaxClusCount());
		clusTable.setAttribute("usepam", String.valueOf(table.isUsePam()));
		clusTable.setAttribute("overwriteConf",
				String.valueOf(table.isOverwriteConf()));

		appendStructure(clusTable, table);

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
		clusTable.appendChild(content);
		dom.appendChild(clusTable);

		appendObservations(clusTable, table);

		saveDocument(fileName);

	}

	private static void appendObservations(Element clusTable, Clustering table) {
		Element observations = dom.createElement("observations");
		clusTable.appendChild(observations);

		Element globalObservations = dom.createElement("globalObservations");
		observations.appendChild(globalObservations);

		Element holonObservations = dom.createElement("holonObservations");
		observations.appendChild(holonObservations);

		Element observation;
		for (ClusTableObservation obs : table.getGlobalObservations()
				.getObservationsAsList()) {
			observation = dom.createElement("observation");
			observation.setAttribute("state", obs.getStateName());

			Element measure;
			for (String key : obs.getMeasure().keySet()) {
				measure = dom.createElement("measure");
				measure.setAttribute("name", key);
				measure.setAttribute("value", obs.getMeasure().get(key)
						.toString());
				observation.appendChild(measure);
			}

			globalObservations.appendChild(observation);
		}

		for (ClusTableObservation obs : table.getHolonObservations()
				.getObservationsAsList()) {
			observation = dom.createElement("observation");
			observation.setAttribute("state", obs.getStateName());

			Element measure;
			for (String key : obs.getMeasure().keySet()) {
				measure = dom.createElement("measure");
				measure.setAttribute("name", key);
				measure.setAttribute("value", obs.getMeasure().get(key)
						.toString());
				observation.appendChild(measure);
			}

			holonObservations.appendChild(observation);
		}

	}

	private static void appendStructure(Element mlTable, Clustering table) {
		Element structure = dom.createElement("structure");
		mlTable.appendChild(structure);

		// globals
		ClusTableGlobalMeasures gm = table.getGlobalMeasures();
		if (gm != null) {
			Element globalMeasuers = dom.createElement("globalMeasures");
			structure.appendChild(globalMeasuers);
			addGlobalMeasurements(gm, globalMeasuers);
		}

		ClusTableStates gs = table.getGlobalStates();
		if (gs != null) {
			Element globalStates = dom.createElement("globalStates");
			structure.appendChild(globalStates);
			addGlobalStates(gs, globalStates);
		}

		if (table.getGlobalActionsFunction() != null) {
			Element globalActions = dom.createElement("globalActions");
			structure.appendChild(globalActions);
			addGlobalActions(table, globalActions);
		}
		// holons
		ClusTableHolonMeasures hm = table.getHolonMeasures();
		if (hm != null) {
			Element holonMeasuers = dom.createElement("holonMeasures");
			structure.appendChild(holonMeasuers);
			addHolonMeasurements(hm, holonMeasuers);
		}

		ClusTableStates hs = table.getHolonStates();
		if (hs != null) {
			Element holonStates = dom.createElement("holonStates");
			structure.appendChild(holonStates);
			addHolonStates(hs, holonStates);
		}

		if (table.getHolonActionsFunction() != null) {
			Element holonActions = dom.createElement("holonActions");
			structure.appendChild(holonActions);
			addHolonActions(table, holonActions);
		}

	}

	private static void addGlobalMeasurements(
			ClusTableGlobalMeasures gMeasurements, Element measurements) {

		Map<String, String> usedMeasurements = gMeasurements.getValues();

		for (String mName : usedMeasurements.keySet()) {
			String mValue = usedMeasurements.get(mName);

			Element measure = dom.createElement("measure");
			measure.setAttribute("name", mName);
			measure.setAttribute("value", mValue);

			measurements.appendChild(measure);
		}

	}

	private static void addHolonMeasurements(
			ClusTableHolonMeasures hMeasurements, Element measurements) {
		Map<String, String> usedMeasurements = hMeasurements.getValues();

		for (String mName : usedMeasurements.keySet()) {
			String mValue = usedMeasurements.get(mName);

			Element measure = dom.createElement("measure");
			measure.setAttribute("name", mName);
			measure.setAttribute("value", mValue);

			measurements.appendChild(measure);
		}
	}

	private static void addGlobalStates(ClusTableStates gstates,
			Element statesEl) {
		statesEl.setAttribute("k", String.valueOf(gstates.getK()));

		Map<String, Map<String, Double>> states = gstates.getValues();

		for (String nextState : states.keySet()) {
			Element s = dom.createElement("state");
			s.setAttribute("name", nextState);

			statesEl.appendChild(s);

			Map<String, Double> measures = states.get(nextState);
			for (String nextM : measures.keySet()) {
				Element m = dom.createElement("measure");

				m.setAttribute("name", nextM);
				m.setAttribute("value", String.valueOf(measures.get(nextM)));

				s.appendChild(m);
			}
		}

	}

	private static void addHolonStates(ClusTableStates hstates, Element statesEl) {
		statesEl.setAttribute("k", String.valueOf(hstates.getK()));

		Map<String, Map<String, Double>> states = hstates.getValues();

		for (String nextState : states.keySet()) {
			Element s = dom.createElement("state");
			s.setAttribute("name", nextState);

			statesEl.appendChild(s);

			Map<String, Double> measures = states.get(nextState);
			for (String nextM : measures.keySet()) {
				Element m = dom.createElement("measure");

				m.setAttribute("name", nextM);
				m.setAttribute("value", String.valueOf(measures.get(nextM)));

				s.appendChild(m);
			}
		}
	}

	private static void addGlobalActions(Clustering table, Element actionsEl) {
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

	private static void addHolonActions(Clustering table, Element actionsEl) {
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

	private static void addContent(ClusTableStates states, Element contentEl) {
		Map<String, List<ClusTableCell>> rows = states.getRows();
		Element state;
		Element action;
		for (String stateName : rows.keySet()) {
			state = dom.createElement("state");
			state.setAttribute("name", stateName);
			for (ClusTableCell cell : rows.get(stateName)) {
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

}
