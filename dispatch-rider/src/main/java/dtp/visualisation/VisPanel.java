package dtp.visualisation;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.jade.ProblemType;
import dtp.jade.eunit.EUnitInfo;
import dtp.util.AgentIDResolver;
import jade.core.AID;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

public class VisPanel extends JPanel {


    private static Logger logger = Logger.getLogger(VisPanel.class);
    private Graph aGraph;
    private VisGUI aVisGUI;
    private int ray = 12;
    private HashMap<GraphPoint, Ellipse2D> points;
    private HashMap<GraphLink, Line2D> links;
    private boolean printPointNames;

    private HashMap<AID, EUnitInfo> eUnits;

    public VisPanel(Graph aGraph, VisGUI aVisGUI) {

        this.points = new HashMap<GraphPoint, Ellipse2D>();
        this.links = new HashMap<GraphLink, Line2D>();
        this.aGraph = aGraph;
        this.aVisGUI = aVisGUI;
        this.setPrintPointNames(true);
        this.eUnits = new HashMap<AID, EUnitInfo>();
        MouseHandler mh = new MouseHandler();
        addMouseListener(mh);
        addMouseMotionListener(mh);
        setGraphRepresentation();
    }

    public boolean getPrintPointNames() {
        return printPointNames;
    }

    public void setPrintPointNames(boolean printPointNames) {
        this.printPointNames = printPointNames;
    }

    private void setGraphRepresentation() {

        double xFactor = xFactor();
        double yFactor = yFactor();
        double xMin = aGraph.getXmin();
        double yMin = aGraph.getYmin();

        // represents point as circles

        Iterator<GraphPoint> pit = aGraph.getPointsIterator();
        GraphPoint pt = null;

        int ray = (int) (this.ray / 2);

        // odsuniecie punktow od brzegow ekranu
        final int SPACE = 20;

        while (pit.hasNext()) {
            pt = pit.next();
            Ellipse2D e2d = new Ellipse2D.Double(SPACE + xFactor * (pt.getX() - xMin) - ray / 2, SPACE + yFactor
                    * (pt.getY() - yMin) - ray / 2, ray, ray);
            points.put(pt, e2d);
        }

        // represent links as lines

        if (aVisGUI.getProblemType() == ProblemType.WITH_GRAPH) {

            Iterator<GraphLink> lit = aGraph.getLinksIterator();
            GraphLink ln = null;
            GraphPoint pt1 = null, pt2 = null;

            while (lit.hasNext()) {

                ln = lit.next();
                pt1 = ln.getStartPoint();
                pt2 = ln.getEndPoint();
                Line2D line = new Line2D.Double(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY());
                links.put(ln, line);
                ln = pt2.getLinkTo(pt1);
                links.put(ln, line);
            }
        }
    }

    public void updateGraph(Graph graph) {

        aGraph = graph;
        setGraphRepresentation();

        this.repaint();
    }

    public void updateEunitInfo(EUnitInfo eUnitInfo) {

        eUnits.remove(eUnitInfo.getAID());
        eUnits.put(eUnitInfo.getAID(), eUnitInfo);

        this.repaint();
    }

    public void updateMany(EUnitInfo[] eUnitInfos) {

        for (int i = 0; i < eUnitInfos.length; i++) {

            eUnits.remove(eUnitInfos[i].getAID());
            eUnits.put(eUnitInfos[i].getAID(), eUnitInfos[i]);
        }

        this.repaint();
    }

    public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;
        super.paint(g);

        double xFactor = xFactor();
        double yFactor = yFactor();
        double xMin = aGraph.getXmin();
        double yMin = aGraph.getYmin();

        // odsuniecie punktow od brzegow ekranu
        final int SPACE = 20;

        g2D.setColor(Color.WHITE);
        Rectangle2D r2d = new Rectangle2D.Double(0, 0, 640, 480);
        g2D.draw(r2d);
        g2D.fill(r2d);

        g2D.setColor(Color.BLACK);
        // draw points as black circles
        Iterator<GraphPoint> pit = aGraph.getPointsIterator();
        GraphPoint pt = null;
        while (pit.hasNext()) {

            pt = pit.next();
            Ellipse2D e2d = points.get(pt);

            g2D.setColor(Color.BLACK);
            g2D.draw(e2d);
            g2D.fill(e2d);

            // draw points names as gray strings
            if (getPrintPointNames()) {

                g2D.setColor(Color.LIGHT_GRAY);
                g2D.setFont(new Font(null, Font.ITALIC, 10));
                g2D.drawString(pt.getName(), (int) (SPACE + xFactor * (pt.getX() - xMin)) + ray, (int) (SPACE + yFactor
                        * (pt.getY() - yMin))
                        + ray);
            }
        }

        // draw links as lines
        Iterator<GraphLink> lit = aGraph.getLinksIterator();
        GraphLink ln = null;
        GraphPoint pt1 = null, pt2 = null;

        while (lit.hasNext()) {

            ln = lit.next();
            pt1 = ln.getStartPoint();
            pt2 = ln.getEndPoint();

            // +/- 1 - zapobiega nakladaniu sie linii

            if (pt1.getX() < pt2.getX()) {

                g2D.setColor(Color.BLUE.darker());
                g2D.drawLine(SPACE + (int) (xFactor * (pt1.getX() - xMin)) - 1, SPACE
                                + (int) (yFactor * (pt1.getY() - yMin) - 1), SPACE + (int) (xFactor * (pt2.getX() - xMin) - 1),
                        SPACE + (int) (yFactor * (pt2.getY() - yMin)) - 1);

            } else {

                g2D.setColor(Color.RED.darker());
                g2D.drawLine(SPACE + (int) (xFactor * (pt1.getX() - xMin) + 1), SPACE
                                + (int) (yFactor * (pt1.getY() - yMin) + 1), SPACE + (int) (xFactor * (pt2.getX() - xMin) + 1),
                        SPACE + (int) (yFactor * (pt2.getY() - yMin)) + 1);
            }
        }

        // draw EUnits as green circles
        Iterator<AID> git = eUnits.keySet().iterator();
        EUnitInfo eUnitInfo;
        g2D.setColor(Color.GREEN.darker());

        try {

            while (git.hasNext()) {

                eUnitInfo = eUnits.get(git.next());

                g2D.drawOval((int) (SPACE + xFactor * (eUnitInfo.getCurrentLocation().getX() - xMin)) - 5,
                        (int) (SPACE + yFactor * (eUnitInfo.getCurrentLocation().getY() - yMin)) - 5, 10, 10);
                g2D.setFont(new Font(null, Font.BOLD, 12));
                g2D.drawString("#" + AgentIDResolver.getEUnitIDFromName(eUnitInfo.getAID().getLocalName()),
                        (int) (SPACE + xFactor * (eUnitInfo.getCurrentLocation().getX() - xMin)),
                        (int) (SPACE + yFactor * (eUnitInfo.getCurrentLocation().getY() - yMin)));
            }

        } catch (ConcurrentModificationException e) {

            logger.error("ConcurrentModificationException occured");
        }
    }

    // o ile nalezy pomnozyc wspolrzedna aby graf wyswietlal sie przy
    // wykorzystaniu pelnej powierzchni panelu
    private double xFactor() {

        // odsuniecie punktow od brzegow ekranu
        final int SPACE = 100;

        int xMin = (int) aGraph.getXmin();
        int xMax = (int) aGraph.getXmax();

        return (aVisGUI.getPanelDimension().getWidth() - SPACE) / (xMax - xMin);
    }

    // o ile nalezy pomnozyc wspolrzedna aby graf wyswietlal sie przy
    // wykorzystaniu pelnej powierzchni panelu
    private double yFactor() {

        // odsuniecie punktow od brzegow ekranu
        final int SPACE = 50;

        int yMin = (int) aGraph.getYmin();
        int yMax = (int) aGraph.getYmax();

        return (aVisGUI.getPanelDimension().getHeight() - SPACE) / (yMax - yMin);
    }

    public void mouseMoved(MouseEvent ev) {
    }

    public void mouseDragged(MouseEvent ev) {
    }

    private class MouseHandler extends MouseAdapter implements MouseMotionListener {

        public void mouseClicked(MouseEvent e) {

            repaint();
        }
    }
}
