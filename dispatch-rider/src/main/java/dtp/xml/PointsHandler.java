package dtp.xml;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Grzegorz A SAX content handler that creates hashmap of Points objects
 *         and theirs ID's
 */
public class PointsHandler extends DefaultHandler {

    // private GraphMap graphMap;
    private final Graph graph;
    private boolean pointTag;
    private boolean idTag;
    private boolean nameTag;
    private boolean positionTag;
    private boolean XTag;
    private boolean YTag;
    private boolean baseTag;
    private int tmpId;
    private String tmpName;
    private int tmpX;
    private int tmpY;
    private boolean tmpIsBase;

    public PointsHandler() {
        pointTag = false;
        idTag = false;
        nameTag = false;
        positionTag = false;
        XTag = false;
        YTag = false;
        baseTag = false;

        // graphMap = new GraphMap();
        this.graph = new Graph();
    }

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
        } else if (qualifiedName.equals("name")) {
            nameTag = true;
        } else if (qualifiedName.equals("position")) {
            positionTag = true;
        } else if (qualifiedName.equals("x")) {
            XTag = true;
        } else if (qualifiedName.equals("y")) {
            YTag = true;
        } else if (qualifiedName.equals("isbase")) {
            baseTag = true;
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
            // graphMap.addPoint(new Integer(tmpId),new
            // Point(tmpX,tmpY,tmpName,tmpIsBase));
            graph.putPoint(new Integer(tmpId), new GraphPoint(tmpX, tmpY,
                    tmpName, tmpIsBase, tmpId));
        } else if (qualifiedName.equals("id")) {
            idTag = false;
        } else if (qualifiedName.equals("name")) {
            nameTag = false;
        } else if (qualifiedName.equals("position")) {
            positionTag = false;
        } else if (qualifiedName.equals("x")) {
            XTag = false;
        } else if (qualifiedName.equals("y")) {
            YTag = false;
        } else if (qualifiedName.equals("isbase")) {
            baseTag = false;
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

        if ((pointTag) && (nameTag)) {
            tmpName = new String(chars, startIndex, endIndex).trim();
        }

        if ((pointTag) && (positionTag) && (XTag)) {
            tmpX = (int) Double.parseDouble(new String(chars, startIndex,
                    endIndex).trim());
        }

        if ((pointTag) && (positionTag) && (YTag)) {
            tmpY = (int) Double.parseDouble(new String(chars, startIndex,
                    endIndex).trim());
        }

        if ((pointTag) && (baseTag)) {
            String str = new String(chars, startIndex, endIndex).trim();
            if (str.equals("true")) {
                tmpIsBase = true;
            } else
                tmpIsBase = false;
        }
    }
}
