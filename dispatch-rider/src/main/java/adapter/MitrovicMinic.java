package adapter;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.simmulation.SimInfo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class MitrovicMinic implements Adapter {
    private final List<CommissionHandler> handler;
    private final SimInfo simInfo;

    public MitrovicMinic(String fileName) throws Exception {
        handler = new LinkedList<CommissionHandler>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        for (int i = 0; i < 2; i++)
            reader.readLine();
        int comsSize = Integer.parseInt(reader.readLine().split(" ")[4]);
        for (int i = 3; i < 6; i++)
            reader.readLine();
        String parts[] = new String[12];
        int comId = 1;
        Commission com;
        int c;
        double pickupX;
        double pickupY;
        double pickupTime1;
        double pickupTime2;
        double deliveryX;
        double deliveryY;
        double deliveryTime1;
        double deliveryTime2;
        for (int j = 0; j < comsSize; j++) {
            c = 0;
            for (String part : reader.readLine().split(" ")) {
                if (part.length() > 0) {
                    parts[c++] = part;
                }
            }
            pickupX = Double.parseDouble(parts[3]);
            pickupY = Double.parseDouble(parts[4]);
            pickupTime1 = Double.parseDouble(parts[5]);
            pickupTime2 = Double.parseDouble(parts[6]);
            deliveryX = Double.parseDouble(parts[8]);
            deliveryY = Double.parseDouble(parts[9]);
            deliveryTime1 = Double.parseDouble(parts[10]);
            deliveryTime2 = Double.parseDouble(parts[11]);
            if (parts.length < 12)
                continue;
            com = new Commission(Integer.parseInt(parts[0]), comId, pickupX,
                    pickupY, pickupTime1, pickupTime2, comId + 1, deliveryX,
                    deliveryY, deliveryTime1, deliveryTime2, 0,
                    Integer.parseInt(parts[2]), Integer.parseInt(parts[2]));
            comId += 2;
            handler.add(new CommissionHandler(com, Integer.parseInt(parts[1])));
        }
        // baza jest stala (20,30)
        // deadline jest 10h = 600 min
        // nie ma okreslonego maxLoad dlatego przyjeto 100 (nie ma to znaczenia
        // bo zlecenia maja ladownosci 0)
        simInfo = new SimInfo(new Point2D.Double(20, 30), 600, 100);
    }

    public List<CommissionHandler> readCommissions() {
        return handler;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }
}
