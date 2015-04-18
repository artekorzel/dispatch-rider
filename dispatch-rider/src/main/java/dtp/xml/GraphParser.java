package dtp.xml;

import dtp.graph.Graph;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * @author Grzegorz A parser for XML documents describing transport infrastructure.
 */
public class GraphParser {

    private SAXParser parser;

    /**
     * Creates new parser and initializes its parameteres.
     *
     * @throws ParseException
     */
    public GraphParser() throws ParseException {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false); // factory.setNamespaceAware(true);
            factory.setValidating(false); // factory.setValidating(true);
            parser = factory.newSAXParser();
            // parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            // parser.setProperty(JAXP_SCHEMA_SOURCE, new File(SCHEMA_SOURCE));
        } catch (SAXException e1) {
            throw new ParseException("SAXException: " + e1.getMessage());
        } catch (ParserConfigurationException e2) {
            throw new ParseException("ParserConfigurationException: " + e2.getMessage());
        }
    }

    /**
     * Validates and parses given xml document.
     *
     * @param xmlDocument document in XML to be parsed
     * @return Graph genereted graph of Points and Links
     * @throws ParseException
     */
    public Graph parse(String xmlDocument) throws ParseException {

        try {
            // Get the encapsulated SAX XMLReader
            XMLReader xmlReader = parser.getXMLReader();

            // INITIAL PARSING

            // Creating new content handler for initial parsing
            PointsHandler pointsHandler = new PointsHandler();

            // Set the ContentHandler of the XMLReader
            xmlReader.setContentHandler(pointsHandler);

            // Set an ErrorHandler of the XMLReader
            xmlReader.setErrorHandler(new ValidatorErrorHandler());

            // Parsing of the XML document
            xmlReader.parse(xmlDocument);

            // FINAL PARSING

            // Creating new content handler for final parsing
            LinksHandler linksHandler = new LinksHandler(pointsHandler.getGraph());

            // Set the ContentHandler of the XMLReader
            xmlReader.setContentHandler(linksHandler);

            // Set an ErrorHandler of the XMLReader
            xmlReader.setErrorHandler(new ValidatorErrorHandler());

            // Parsing of the XML document
            xmlReader.parse(xmlDocument);

            // Graph g = new Graph(linksHandler.getGraphMap(), 0, 0, 0, 0);
            return linksHandler.getGraph();

        } catch (IOException e1) {

            throw new ParseException("IOException: " + e1.getMessage());
        } catch (SAXException e2) {

            throw new ParseException("SAXException: " + e2.getMessage());
        }
    }
}
