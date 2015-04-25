package dtp.xml;

import algorithm.STLike.ExchangeAlgorithmsFactory;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.jade.crisismanager.crisisevents.*;
import dtp.jade.gui.DefaultAgentsData;
import dtp.jade.gui.TestConfiguration;
import machineLearning.MLAlgorithm;
import machineLearning.MLAlgorithmFactory;
import machineLearning.qlearning.QLearning;
import measure.MeasureCalculatorType;
import measure.MeasureCalculatorsHolder;
import measure.printer.PrintersHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigurationParser {

    /**
     * Converts XML boolean attribute to Java boolean type
     *
     * @param value represent boolean attribute as returned by
     *              org.w3c.dom.Element.getAttrbute()
     * @return true if value is "true" or "1"
     */
    private static boolean stringToBoolean(String value) {
        return "true".equals(value) || "1".equals(value);
    }

    private static int attributeToInt(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return Integer.valueOf(attr.getTextContent().trim());
    }

    private static double attributeToDobule(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return Double.valueOf(attr.getTextContent().trim());
    }

    private static TestConfiguration parseTest(Element element)
            throws ParseException {
        TestConfiguration configuration = new TestConfiguration();

        Element commissions = (Element) element.getElementsByTagName(
                "commissions").item(0);
        Element agents = (Element) element
                .getElementsByTagName("defaultAgents").item(0);
        Element results = (Element) element.getElementsByTagName("results")
                .item(0);
        Element configDir = (Element) element.getElementsByTagName(
                "configuration").item(0);
        Element events = (Element) element.getElementsByTagName("events").item(
                0);
        Element graph = (Element) element.getElementsByTagName("roadGraph")
                .item(0);

        Element measures = (Element) element.getElementsByTagName("measures")
                .item(0);

        Element punishment = (Element) element.getElementsByTagName(
                "punishment").item(0);

        Element ml = (Element) element.getElementsByTagName("machineLearning")
                .item(0);

        Element mlAlgorithm = (Element) element.getElementsByTagName(
                "mlAlgorithm").item(0);

        if (graph != null) {
            String graphFile = graph.getTextContent().trim();
            Graph graphObj = new GraphParser().parse(graphFile.trim());
            String trackFinder = graph.getAttribute("trackFinder");
            String predictor = graph.getAttribute("predictor");
            String historySize = graph.getAttribute("historySize");
            boolean graphST = stringToBoolean(graph.getAttribute("ST"));
            configuration.setSTAfterGraphChange(graphST);
            configuration.setGraph(graphObj, trackFinder, predictor,
                    Integer.parseInt(historySize));
            String graphChanges = graph.getAttribute("graphChanges");
            String changeTime = graph.getAttribute("changeTime");
            String notificationTime = graph.getAttribute("notificationTime");
            configuration.setGraphChangeTime(changeTime);
            if (notificationTime != null && notificationTime.length() > 0) {
                configuration.setGraphChangeFreq(Integer
                        .parseInt(notificationTime));
            }
            if (graphChanges != null && graphChanges.length() > 0) {
                GraphChangesConfiguration graphConf = GraphChangesParser
                        .parse(graphChanges);
                graphConf.setGraph(graphObj);
                configuration.setGraphChangesConf(graphConf);
            }
        }

        if (measures != null) {
            configuration = parseMeasures(measures, configuration);
        }

        if (punishment != null) {
            configuration = parsePunishment(punishment, configuration);
        }

        if (ml != null) {
            configuration = parseMachineLearningPart(configuration, ml);
        }

        if (mlAlgorithm != null) {
            configuration = parseMLAlgorithmPart(configuration, mlAlgorithm);
        }

        configuration.setBrute2Sorter(commissions
                .getAttribute("BruteForceAlgorithm2Sorter"));
        configuration.setCommissions(commissions.getTextContent().trim());
        configuration.setConfChange(stringToBoolean(commissions
                .getAttribute("confChange")));
        configuration.setAutoConfigure(stringToBoolean(commissions
                .getAttribute("autoConfig")));
        configuration.setRecording(stringToBoolean(commissions
                .getAttribute("recording")));
        configuration.setSTTimeGap(Integer.parseInt(commissions
                .getAttribute("STTimeGap")));
        configuration.setSTCommissionGap(Integer.parseInt(commissions
                .getAttribute("STCommissionGap")));
        configuration.setAdapter(commissions.getAttribute("dynamic"));
        configuration
                .setDist(stringToBoolean(commissions.getAttribute("dist")));
        // only to maintain compatibility with older configurations
        String worstCommissionChoose = commissions
                .getAttribute("worstCommissionByGlobalTime");
        if (worstCommissionChoose != null) {
            boolean time = stringToBoolean(worstCommissionChoose);
            if (time) {
                configuration.setWorstCommissionChoose("time");
            } else {
                configuration.setWorstCommissionChoose("wTime");
            }
        } else {
            configuration.setWorstCommissionChoose(commissions
                    .getAttribute("chooseWorstCommission"));
        }
        configuration.setAlgorithm(commissions.getAttribute("algorithm"));
        configuration.setPackageSending(stringToBoolean(commissions
                .getAttribute("packageSending")));
        configuration.setChoosingByCost(stringToBoolean(commissions
                .getAttribute("choosingByCost")));
        configuration.setSimulatedTrading(Integer.parseInt(commissions
                .getAttribute("simulatedTrading")));
        int STDepth = Integer.parseInt(commissions.getAttribute("STDepth"));
        if (STDepth == 0) {
            System.err.println("STDepth nie moze byc rowne 0");
            System.exit(0);
        }
        configuration.setSTDepth(STDepth);
        configuration.setFirstComplexSTResultOnly(stringToBoolean(commissions
                .getAttribute("firstComplexSTResultOnly")));
        configuration.setDefaultAgentsData(parseAgents(agents));
        configuration.setConfigurationDirectory(configDir.getTextContent().trim());
        configuration.setResults(results.getTextContent().trim());
        configuration.setEvents(parseEvents(events));

        configuration = parseExchangeAlgorithms(configuration, element);

        return configuration;
    }

    private static DefaultAgentsData parseAgents(Element element)
            throws ParseException {
        if (element == null)
            return null;

        int power = -1;
        int reliability = -1;
        int comfort = -1;
        int fuelConsumption = -1;
        int mass = -1;
        int capacity = -1;
        int cargoType = -1;
        int universality = -1;

        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                if ("truck".equals(name)) {
                    power = attributeToInt(node, "power");
                    reliability = attributeToInt(node, "reliability");
                    comfort = attributeToInt(node, "comfort");
                    fuelConsumption = attributeToInt(node, "fuelConsumption");
                } else if ("trailer".equals(name)) {
                    mass = attributeToInt(node, "mass");
                    capacity = attributeToInt(node, "capacity");
                    cargoType = attributeToInt(node, "cargoType");
                    universality = attributeToInt(node, "universality");
                } else {
                    throw new ParseException("Unrecognized event " + name);
                }
            }
        }
        return new DefaultAgentsData(power, reliability, comfort,
                fuelConsumption, mass, capacity, cargoType, universality);
    }

    private static List<CrisisEvent> parseEvents(Element element)
            throws ParseException {
        List<CrisisEvent> events = new LinkedList<>();

        if (element == null)
            return events;

        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            CrisisEvent event;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                if ("commissionWithdrawal".equals(name))
                    event = parseCommissionWithdrawal(node);
                else if ("commissionDelay".equals(name)) {
                    event = parseCommissionDelay(node);
                } else if ("vehicleFailure".equals(name)) {
                    event = parseVehicleFailure(node);
                } else if ("trafficJam".equals(name)) {
                    event = parseTrafficJamEvent(node);
                } else if ("roadTrafficExclusion".equals(name)) {
                    event = parseRoadTrafficExclusionEvent(node);
                } else {
                    throw new ParseException("Unrecognized event " + name);
                }
                event.setEventID(events.size());
                events.add(event);
            }
        }

        return events;
    }

    private static CommissionWithdrawalEvent parseCommissionWithdrawal(Node node)
            throws ParseException {
        CommissionWithdrawalEvent event = new CommissionWithdrawalEvent();
        event.setEventTime(attributeToInt(node, "time"));
        event.setCommissionID(attributeToInt(node, "commission"));
        return event;
    }

    private static CommissionDelayEvent parseCommissionDelay(Node node)
            throws ParseException {
        CommissionDelayEvent event = new CommissionDelayEvent();
        event.setEventTime(attributeToInt(node, "time"));
        event.setCommissionID(attributeToInt(node, "commission"));
        event.setDelay(attributeToInt(node, "delay"));
        return event;
    }

    private static EUnitFailureEvent parseVehicleFailure(Node node)
            throws ParseException {
        EUnitFailureEvent event = new EUnitFailureEvent();
        event.setEventTime(attributeToInt(node, "time"));
        event.setEUnitID(attributeToInt(node, "vehicle"));
        event.setFailureDuration(attributeToDobule(node, "duration"));
        return event;
    }

    private static TrafficJamEvent parseTrafficJamEvent(Node node)
            throws ParseException {
        TrafficJamEvent event = new TrafficJamEvent();
        event.setEventTime(attributeToInt(node, "time"));
        event.setStartPoint(new Point2D.Double(
                attributeToDobule(node, "startX"), attributeToDobule(node,
                "startY")));
        event.setEndPoint(new Point2D.Double(attributeToDobule(node, "endX"),
                attributeToDobule(node, "endY")));
        event.setJamCost(attributeToDobule(node, "cost"));
        return event;
    }

    private static RoadTrafficExclusionEvent parseRoadTrafficExclusionEvent(
            Node node) throws ParseException {
        RoadTrafficExclusionEvent event = new RoadTrafficExclusionEvent();
        event.setEventTime(attributeToInt(node, "time"));
        event.setStartPoint(new Point2D.Double(
                attributeToDobule(node, "startX"), attributeToDobule(node,
                "startY")));
        event.setEndPoint(new Point2D.Double(attributeToDobule(node, "endX"),
                attributeToDobule(node, "endY")));
        return event;
    }

    /**
     * @param filename name of XML file to read
     * @return new TestConfiguration representing contents of given file
     * @throws ParseException
     */
    public static List<TestConfiguration> parse(String filename)
            throws ParseException {
        List<TestConfiguration> tests = new LinkedList<>();

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

            NodeList nodes = document.getElementsByTagName("test");

            for (int i = 0; i < nodes.getLength(); ++i) {
                tests.add(parseTest((Element) nodes.item(i)));
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }

        return tests;
    }

    public static TestConfiguration parseMeasures(Element measures,
                                                  TestConfiguration conf) {
        String formats[] = measures.getAttribute("formats").split(" ");
        PrintersHolder printersHolder = new PrintersHolder();

        for (String extension : formats)
            printersHolder.addPrinter(extension);

        MeasureCalculatorsHolder calculatorsHolder = new MeasureCalculatorsHolder();
        calculatorsHolder.setTimeGap(Integer.parseInt(measures
                .getAttribute("timeGap")));

        NodeList measureList = measures.getElementsByTagName("measure");
        Element measure;
        boolean visualize;
        for (int i = 0; i < measureList.getLength(); i++) {
            measure = (Element) measureList.item(i);
            calculatorsHolder.addCalculator(MeasureCalculatorType.valueOf(measure.getTextContent().trim()));
            visualize = stringToBoolean(measure.getAttribute("visualize"));
            if (visualize) {
                calculatorsHolder.addVisualizationMeasuresNames(measure
                        .getTextContent().trim());
            }
        }

        conf.setCalculatorsHolder(calculatorsHolder);
        conf.setPrintersHolder(printersHolder);

        return conf;
    }

    public static TestConfiguration parsePunishment(Element punishment,
                                                    TestConfiguration conf) {
        String fun = punishment.getAttribute("function");
        int holons = Integer.parseInt(punishment.getAttribute("holons"));
        conf.setHolons(holons);
        String defaultValues = punishment.getAttribute("default");
        String delayLimit = punishment.getAttribute("delayLimit");
        if (delayLimit != null && delayLimit.length() > 0)
            conf.setDelayLimit(new Double(delayLimit));
        Map<String, Double> defaults = new HashMap<>();
        String[] parts;
        try {
            if (defaultValues != null && defaultValues.length() > 0)
                for (String value : defaultValues.split(";")) {
                    parts = value.split("=");
                    if (parts.length != 2)
                        throw new IllegalArgumentException();
                    defaults.put(parts[0].trim(), new Double(parts[1].trim()));
                }
            conf.setPunishmentFunction(fun);
            conf.setDefaultPunishmentFunValues(defaults);
        } catch (Exception e) {
            e.printStackTrace();//FIXME
        }
        return conf;
    }

    private static TestConfiguration parseMachineLearningPart(
            TestConfiguration conf, Element ml) throws ParseException {
        boolean exploration = stringToBoolean(ml.getAttribute("exploration"));

        String path = ml.getTextContent().trim();
        conf.setMlTableFileName(path);
        String params = ml.getAttribute("params");

        QLearning alg = new QLearning();
        alg.init(path);

        if (params != null && params.length() > 0) {
            String[] parts = params.split(";");
            String[] paramsParts;
            Map<String, Double> paramsMap = new HashMap<>();
            for (String param : parts) {
                paramsParts = param.trim().split("=");
                paramsMap.put(paramsParts[0], new Double(paramsParts[1]));
            }
            alg.setDefaultParams(paramsMap);
        }

        conf.setMlAlgorithm(alg);

        conf.setExploration(exploration);
        return conf;
    }

    private static TestConfiguration parseMLAlgorithmPart(
            TestConfiguration conf, Element ml) throws ParseException {

        boolean exploration = stringToBoolean(ml.getAttribute("exploration"));
        String algName = ml.getAttribute("algorithm");

        String path = ml.getAttribute("file");
        conf.setMlTableFileName(path);

        NodeList params = ml.getElementsByTagName("param");
        Element param;
        Map<String, String> parameters = new HashMap<>();
        for (int i = 0; i < params.getLength(); i++) {
            param = (Element) params.item(i);
            parameters.put(param.getAttribute("name"), param.getAttribute("value"));
        }

        MLAlgorithm alg = MLAlgorithmFactory.createAlgorithm(algName);
        if (alg == null) {
            throw new ParseException("MLAlgorithm " + algName + "not found");
        }
        alg.init(path);
        alg.setAlgorithmParameters(parameters);

        conf.setMlAlgorithm(alg);

        conf.setExploration(exploration);
        return conf;
    }

    private static TestConfiguration parseExchangeAlgorithms(TestConfiguration conf, Element root) {

        ExchangeAlgorithmsFactory factory = new ExchangeAlgorithmsFactory();
        if (conf.getSimulatedTrading() > 0) {
            Map<String, String> params = new HashMap<>();
            params.put("chooseWorstCommission", conf.getWorstCommissionChoose());
            factory.setAlgAfterComAdd("SimulatedTrading", params);
            params = new HashMap<>();
            params.put("maxFullSTDepth", Integer.toString(conf.getSTDepth()));
            params.put("firstComplexSTResultOnly", Boolean.toString(conf.isFirstComplexSTResultOnly()));
            factory.setAlgWhenCantAdd("SimulatedTrading", params);
        }

        NodeList list = root.getElementsByTagName("exchangeAlgorithmAfterComAdd");
        Element algEl;
        Object[] params;

        if (list != null && list.getLength() > 0) {
            algEl = (Element) list.item(0);
            params = parseExchangeAlgParams(algEl);
            factory.setAlgAfterComAdd((String) params[0], (Map<String, String>) params[1]);
        }
        list = root.getElementsByTagName("exchangeAlgorithmWhenCantAddCom");
        if (list != null && list.getLength() > 0) {
            algEl = (Element) list.item(0);
            params = parseExchangeAlgParams(algEl);
            factory.setAlgWhenCantAdd((String) params[0], (Map<String, String>) params[1]);
        }
        conf.setExchangeAlgFactory(factory);
        return conf;
    }

    private static Object[] parseExchangeAlgParams(Element algEl) {

        String name = algEl.getAttribute("name");
        Map<String, String> params = new HashMap<>();
        NodeList list = algEl.getElementsByTagName("param");
        String paramName;
        String paramValue;
        Element paramEl;
        if (list != null)
            for (int i = 0; i < list.getLength(); i++) {
                paramEl = (Element) list.item(i);
                paramName = paramEl.getAttribute("name");
                paramValue = paramEl.getAttribute("value");
                params.put(paramName, paramValue);
            }

        return new Object[]{name, params};
    }
}
