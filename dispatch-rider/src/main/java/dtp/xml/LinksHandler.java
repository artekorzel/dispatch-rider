package dtp.xml;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Grzegorz A SAX content handler that creates whole graph structure of
 *         Points and Links objects
 */
public class LinksHandler extends DefaultHandler {

    // private GraphMap graphMap;
    private final Graph graph;
    private boolean pointTag;
    private boolean idTag;
    private boolean routeTag;
    private boolean id_rTag;
    // private boolean timeTag;
    private boolean costTag;
    private Integer tmpId;
    private Integer tmpId_r;
    private int tmpCost;

    // public LinksHandler(GraphMap graphMap) {
    public LinksHandler(Graph graph) {
        pointTag = false;
        idTag = false;
        routeTag = false;
        id_rTag = false;
        costTag = false;
        // timeTag = false;

        // this.graphMap = graphMap;
        this.graph = graph;
    }

    // public GraphMap getGraphMap() {
    // return this.graphMap;
    // }

    public Graph getGraph() {

        return this.graph;
    }

    /**
     * Sets boolean variables depending on which tag is considered at the
     * momement
     */
    @Override
    public void startElement(String namespaceUri, String localName,
                             String qualifiedName, Attributes attributes) throws SAXException {

        if (qualifiedName.equals("point")) {
            pointTag = true;
        } else if (qualifiedName.equals("id")) {
            idTag = true;
        } else if (qualifiedName.equals("route")) {
            routeTag = true;
        } else if (qualifiedName.equals("id_r")) {
            id_rTag = true;
        } else if (qualifiedName.equals("cost")) {
            costTag = true;
        } else if (qualifiedName.equals("time")) {
            // timeTag = true;
        }
    }

    /**
     * Unsets boolean variables depending on which closing tag is considered at
     * the momement
     */
    @Override
    public void endElement(String namespaceUri, String localName,
                           String qualifiedName) throws SAXException {

        if (qualifiedName.equals("point")) {
            pointTag = false;
        } else if (qualifiedName.equals("id")) {
            idTag = false;
        } else if (qualifiedName.equals("route")) {
            routeTag = false;
            // Point startPoint = (Point) graphMap.getPointById(tmpId);
            GraphPoint startPoint = graph.getPointById(tmpId);

            // Point endPoint = (Point) graphMap.getPointById(tmpId_r);
            GraphPoint endPoint = graph.getPointById(tmpId_r);

            // graphMap.putLink(new Link(startPoint,endPoint,tmpTime,tmpCost));
            graph.addLink(new GraphLink(startPoint, endPoint, tmpCost));

        } else if (qualifiedName.equals("id_r")) {
            id_rTag = false;
        } else if (qualifiedName.equals("cost")) {
            costTag = false;
        } else if (qualifiedName.equals("time")) {
            // timeTag = false;
        }
    }

    /**
     * Reads values of concrete tags depending on location where the parser is
     * at the moment
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if ((pointTag) && (idTag)) {
            tmpId = Integer.parseInt(new String(chars, startIndex, endIndex)
                    .trim());
        }

        if ((pointTag) && (routeTag) && (id_rTag)) {
            tmpId_r = Integer.parseInt(new String(chars, startIndex, endIndex)
                    .trim());
        }

        if ((pointTag) && (routeTag) && (costTag)) {
            tmpCost = (int) Double.parseDouble(new String(chars, startIndex,
                    endIndex).trim());
        }

        // if ((pointTag)&&(routeTag)&&(timeTag)) {
        // tmpTime = Integer.parseInt(new String(chars, startIndex,
        // endIndex).trim());
        // }
    }
}
