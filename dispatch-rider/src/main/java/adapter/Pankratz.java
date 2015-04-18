package adapter;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.simulation.SimInfo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Pankratz implements Adapter {
    private final List<CommissionHandler> handler;
    private final SimInfo simInfo;

    public Pankratz(String fileName) throws Exception {
        handler = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        reader.readLine();
        reader.readLine();
        int maxLoad = Integer.parseInt(reader.readLine().split("\t")[1]);
        int deadline = Integer.parseInt(reader.readLine().split("\t")[1]);
        reader.readLine();
        String parts[] = reader.readLine().split("\t");
        Point2D.Double depot = new Point2D.Double(Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]));
        simInfo = new SimInfo(depot, deadline, maxLoad);
        Map<String, Point2D.Double> nodes = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).equals("DEMAND_SECTION:")) {
            parts = line.split("\t");
            nodes.put(parts[0], new Point2D.Double(Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])));
        }
        int comId = 1;
        Commission com;
        double pickupX;
        double pickupY;
        double pickupTime1;
        double pickupTime2;
        double deliveryX;
        double deliveryY;
        double deliveryTime1;
        double deliveryTime2;
        Point2D.Double point;

        while (!(line = reader.readLine()).equals("EOF")) {
            parts = line.split("\t");

            point = nodes.get(parts[1]);
            pickupX = point.getX();
            pickupY = point.getY();
            pickupTime1 = Double.parseDouble(parts[3]);
            pickupTime2 = Double.parseDouble(parts[4]);
            point = nodes.get(parts[2]);
            deliveryX = point.getX();
            deliveryY = point.getY();
            deliveryTime1 = Double.parseDouble(parts[5]);
            deliveryTime2 = Double.parseDouble(parts[6]);

            com = new Commission(Integer.parseInt(parts[0]), comId, pickupX,
                    pickupY, pickupTime1, pickupTime2, comId + 1, deliveryX,
                    deliveryY, deliveryTime1, deliveryTime2,
                    Integer.parseInt(parts[9]), Integer.parseInt(parts[8]),
                    Integer.parseInt(parts[8]));
            comId += 2;
            handler.add(new CommissionHandler(com, Integer.parseInt(parts[10])));
        }
    }

    public List<CommissionHandler> readCommissions() {
        return handler;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }
}
