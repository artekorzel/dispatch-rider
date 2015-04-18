package dtp.graph;

import java.util.*;

public class GraphGenerator {

    public Graph create(List<GraphPoint> points) {

        Graph graph;
        Iterator<GraphPoint> pit;

        graph = new Graph();

        pit = points.iterator();
        while (pit.hasNext())
            graph.addPoint(pit.next());

        return graph;
    }

    public Graph generateRandom(List<GraphPoint> points, int howManyLinks) {

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

    public Graph generateWithNeighbours(List<GraphPoint> points, int howManyNeighbours, int howManyPoints) {

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

    private void setLinksIn(Graph graph, GraphPoint point, List<GraphPoint> sources) {
        for (GraphPoint source : sources) {
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
            source.addElementToListOut(link);
        }
    }

    private void setLinksOut(Graph graph, GraphPoint point, List<GraphPoint> targets) {
        for (GraphPoint target : targets) {
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
        }
    }

    private List<GraphPoint> getRandomPoints(List<GraphPoint> points, int howMany) {

        List<GraphPoint> newPoints;
        GraphPoint point;
        Random rand;

        if (howMany > points.size())
            return points;

        newPoints = new ArrayList<>();
        rand = new Random(System.nanoTime());

        while (newPoints.size() < howMany) {

            point = points.get(rand.nextInt(points.size()));

            if (!newPoints.contains(point))
                newPoints.add(point);
        }

        return newPoints;
    }

    private List<GraphPoint> getClosePoints(GraphPoint point, List<GraphPoint> points,
                                            int howManyNeighbours, int howManyPoints) {

        List<GraphPoint> neighbours;
        neighbours = getNeighbours(point, points, howManyNeighbours);
        return getRandomPoints(neighbours, howManyPoints);
    }

    private List<GraphPoint> getNeighbours(GraphPoint point, List<GraphPoint> points, int howMany) {

        HashMap<String, Double> neighboursMap;
        Iterator<String> iterString;
        List<GraphPoint> neighboursList;
        Iterator<GraphPoint> iterPoint;
        GraphPoint tmpPoint;
        double distance;
        String tmpMaxDist;
        int count;
        int index;

        neighboursMap = new HashMap<>();
        iterPoint = points.iterator();
        count = 0;
        index = 0;

        while (count < Math.min(howMany, points.size() - 1)) {
            distance = distance(points.get(index).getX(), points.get(index).getY(), point.getX(), point.getY());

            if (distance != 0) {

                neighboursMap.put(points.get(index).getName(), distance);
                count++;
            }

            index++;
        }

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
        neighboursList = new ArrayList<>();
        iterString = neighboursMap.keySet().iterator();

        while (iterString.hasNext()) {
            neighboursList.add(getPointByName(points, iterString.next()));
        }

        return neighboursList;
    }

    private GraphPoint getPointByName(List<GraphPoint> points, String name) {

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
            dist = points.get(key);

            if (dist > points.get(keyWithMaxDist))
                keyWithMaxDist = key;
        }

        return keyWithMaxDist;
    }
}
