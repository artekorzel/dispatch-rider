package dtp.xml;

import dtp.graph.GraphChangesConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GraphChangesParser {

    /**
     * Converts XML boolean attribute to Java boolean type
     *
     * @param value represent boolean attribute as returned by
     *              org.w3c.dom.Element.getAttrbute()
     * @return true if value is "true" or "1"
     */
    private static boolean stringToBoolean(String value) {
        if ("true".equals(value))
            return true;
        else if ("1".equals(value))
            return true;
        else
            return false;
    }

    private static int attributeToInt(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return Integer.valueOf(attr.getTextContent());
    }

    private static double attributeToDobule(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return Double.valueOf(attr.getTextContent());
    }

    private static boolean attributeToBool(Node node, String attribute)
            throws ParseException {
        Node attr = node.getAttributes().getNamedItem(attribute);
        if (attr == null)
            throw new ParseException("No such attribute " + attribute);
        return stringToBoolean(attr.getTextContent());
    }

    public static GraphChangesConfiguration parse(String filename)
            throws ParseException {

        GraphChangesConfiguration conf = new GraphChangesConfiguration();
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
            builder.setErrorHandler(new SimpleErrorHandler());
            Document document = builder.parse(filename);

            Element changes = (Element) document.getElementsByTagName(
                    "graphChanges").item(0);
            NodeList changeList = changes.getElementsByTagName("change");

            Element change;
            Element link;
            int timestamp;
            NodeList links;
            for (int i = 0; i < changeList.getLength(); i++) {
                change = (Element) changeList.item(i);
                timestamp = attributeToInt(change, "time");
                links = change.getElementsByTagName("link");
                for (int j = 0; j < links.getLength(); j++) {
                    link = (Element) links.item(j);
                    conf.addChange(attributeToInt(link, "sPoint"),
                            attributeToInt(link, "ePoint"),
                            attributeToDobule(link, "cost"),
                            attributeToBool(link, "both"), timestamp);
                }
            }

        } catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
        return conf;
    }

    public static class SimpleErrorHandler implements ErrorHandler {

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;

        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}
