package dtp.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class GraphGenerator {

    /**
     * Tworzy dtp.graph.Graph na podstawie listy punktow (dtp.graph.GraphPoint). W grafie nie ma linkow.
     *
     * @param points       lista punktow
     * @param howManyLinks do/z ilu punktow maja wchodzic/wychodzic linki [0, 1]
     * @return wygenerowany graf
     */
    public Graph create(ArrayList<GraphPoint> points) {

        Graph graph;
        Iterator<GraphPoint> pit;

        graph = new Graph();

        pit = points.iterator();
        while (pit.hasNext())
            graph.addPoint(pit.next());

        return graph;
    }

    /**
     * Generuje dtp.graph.Graph na podstawie listy punktow (dtp.graph.GraphPoint)
     *
     * @param points       lista punktow
     * @param howManyLinks do/z ilu punktow maja wchodzic/wychodzic linki
     * @return wygenerowany graf
     */
    public Graph generateRandom(ArrayList<GraphPoint> points, int howManyLinks) {

        Graph graph;
        Iterator<GraphPoint> pit;

        graph = new Graph();

        pit = points.iterator();
        while (pit.hasNext())
            graph.addPoint(pit.next());

        pit = points.iterator();
        while (pit.hasNext()) {

            GraphPoint point = pit.next();

            setLinksIn(graph, point, getRandomPoints(points, howManyLinks));
            setLinksOut(graph, point, getRandomPoints(points, howManyLinks));

        }

        return graph;
    }

    public Graph generateWithNeighbours(ArrayList<GraphPoint> points, int howManyNeighbours, int howManyPoints) {

        Graph graph;
        Iterator<GraphPoint> pit;

        graph = new Graph();

        pit = points.iterator();
        while (pit.hasNext())
            graph.addPoint(pit.next());

        pit = points.iterator();
        while (pit.hasNext()) {

            GraphPoint point = pit.next();

            setLinksIn(graph, point, getClosePoints(point, points, howManyNeighbours, howManyPoints));
            setLinksOut(graph, point, getClosePoints(point, points, howManyNeighbours, howManyPoints));
        }

        return graph;
    }

    private void setLinksIn(Graph graph, GraphPoint point, ArrayList<GraphPoint> sources) {

        Iterator<GraphPoint> pit = sources.iterator();
        while (pit.hasNext()) {
            GraphPoint source = pit.next();

            // nie dodawaj linku do siebie samego
            if (point.hasSameCoordinates(source))
                continue;

            // nie dodawaj linku jezeli jest juz w grafie link pomiedzy takimi
            // dwoma lokacjami
            if (graph.containsLink(source, point))
                continue;

            int time = (int) (distance(point.getX(), point.getY(), source.getX(), source.getY()));
            GraphLink link = new GraphLink(source, point, time);
            graph.addLink(link);
            point.addElementToListIn(link);
            source.addElementToListOut(link);
        }
    }

    private void setLinksOut(Graph graph, GraphPoint point, ArrayList<GraphPoint> targets) {

        Iterator<GraphPoint> pit = targets.iterator();
        while (pit.hasNext()) {
            GraphPoint target = pit.next();

            // nie dodawaj linku do siebie samego
            if (point.hasSameCoordinates(target))
                continue;

            // nie dodawaj linku jezeli jest juz w grafie link pomiedzy takimi
            // dwoma lokacjami
            if (graph.containsLink(point, target))
                continue;

            int time = (int) (distance(point.getX(), point.getY(), target.getX(), target.getY()));
            GraphLink link = new GraphLink(point, target, time);
            graph.addLink(link);
            point.addElementToListOut(link);
            target.addElementToListIn(link);
        }
    }

    /**
     * @param points        pula z ktorej losowane beda punkty
     * @param howManyPoints ile punktow ma zostac wylosowanych [0, 1]
     * @return
     */
    private ArrayList<GraphPoint> getRandomPoints(ArrayList<GraphPoint> points, int howMany) {

        ArrayList<GraphPoint> newPoints;
        GraphPoint point;
        Random rand;

        if (howMany > points.size())
            return points;

        newPoints = new ArrayList<GraphPoint>();
        rand = new Random(System.nanoTime());

        while (newPoints.size() < howMany) {

            point = points.get(rand.nextInt(points.size()));

            if (!newPoints.contains(point))
                newPoints.add(point);
        }

        return newPoints;
    }

    // wylosuj nowManyPoints sposrod howManyNeighbours najblizszych punktow
    private ArrayList<GraphPoint> getClosePoints(GraphPoint point, ArrayList<GraphPoint> points, int howManyNeighbours,
                                                 int howManyPoints) {

        ArrayList<GraphPoint> neighbours;

        neighbours = getNeighbours(point, points, howManyNeighbours);

        return getRandomPoints(neighbours, howManyPoints);
    }

    private ArrayList<GraphPoint> getNeighbours(GraphPoint point, ArrayList<GraphPoint> points, int howMany) {

        HashMap<String, Double> neighboursMap;
        Iterator<String> iterString;
        ArrayList<GraphPoint> neighboursList;
        Iterator<GraphPoint> iterPoint;
        GraphPoint tmpPoint;
        double distance;
        String tmpMaxDist;
        int count;
        int index;

        neighboursMap = new HashMap<String, Double>();
        iterPoint = points.iterator();
        count = 0;
        index = 0;

        // losowe punkty na start, bez point
        while (count < Math.min(howMany, points.size() - 1)) {

            distance = distance(points.get(index).getX(), points.get(index).getY(), point.getX(), point.getY());

            if (distance != 0) {

                neighboursMap.put(points.get(index).getName(), distance);
                count++;
            }

            index++;
        }

        // neighbours ma zawierac howMany najblizszych punktow
        while (iterPoint.hasNext()) {

            tmpPoint = iterPoint.next();
            distance = distance(point.getX(), point.getY(), tmpPoint.getX(), tmpPoint.getY());

            tmpMaxDist = maxDistance(neighboursMap);
            if (distance < neighboursMap.get(tmpMaxDist) && distance != 0) {

                neighboursMap.remove(tmpMaxDist);
                neighboursMap.put(tmpPoint.getName(), distance);
            }
        }

        // przepisz do ArrayList
        neighboursList = new ArrayList<GraphPoint>();
        iterString = neighboursMap.keySet().iterator();

        while (iterString.hasNext()) {

            neighboursList.add(getPointByName(points, iterString.next()));
        }

        return neighboursList;
    }

    private GraphPoint getPointByName(ArrayList<GraphPoint> points, String name) {

        Iterator<GraphPoint> iter;
        GraphPoint tmpPoint;

        iter = points.iterator();

        while (iter.hasNext()) {

            tmpPoint = iter.next();

            if (tmpPoint.getName().equals(name)) {

                return tmpPoint;
            }
        }

        return null;
    }

    private double distance(double ax, double ay, double bx, double by) {

        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    private String maxDistance(HashMap<String, Double> points) {

        Iterator<String> iter;
        String key;
        String keyWithMaxDist;
        double dist;

        iter = points.keySet().iterator();
        keyWithMaxDist = iter.next();

        while (iter.hasNext()) {

            key = iter.next();
            dist = points.get(key).doubleValue();

            if (dist > points.get(keyWithMaxDist).doubleValue())
                keyWithMaxDist = key;
        }

        return keyWithMaxDist;
    }
}
