package pattern;

import algorithm.Helper;
import dtp.commission.Commission;
import dtp.commission.TxtFileReader;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PatternCalculator {
    private final String benchmarksPath = "benchmarks";
    private final String pdp_100[] = {"lc101.txt", "lc102.txt", "lc103.txt",
            "lc104.txt", "lc105.txt", "lr101.txt", "lr102.txt", "lr103.txt",
            "lr104.txt", "lr105.txt", "lrc101.txt", "lrc102.txt", "lrc103.txt",
            "lrc104.txt", "lrc105.txt"};
    private final String pdp_100_2[] = {"lc201.txt", "lc202.txt", "lc203.txt",
            "lc204.txt", "lc205.txt", "lr201.txt", "lr202.txt", "lr203.txt",
            "lr204.txt", "lr205.txt", "lrc201.txt", "lrc202.txt", "lrc203.txt",
            "lrc204.txt", "lrc205.txt"};
    private final String pdp_200[] = {"LC1_2_1.txt", "LC1_2_2.txt",
            "LC1_2_3.txt", "LC1_2_4.txt", "LC1_2_5.txt", "LR1_2_1.txt",
            "LR1_2_2.txt", "LR1_2_3.txt", "LR1_2_4.txt", "LR1_2_5.txt",
            "LRC1_2_1.txt", "LRC1_2_2.txt", "LRC1_2_3.txt", "LRC1_2_4.txt",
            "LRC1_2_5.txt"};
    private final String pdp_200_2[] = {"LC2_2_1.txt", "LC2_2_2.txt",
            "LC2_2_3.txt", "LC2_2_4.txt", "LC2_2_5.txt", "LR2_2_1.txt",
            "LR2_2_2.txt", "LR2_2_3.txt", "LR2_2_4.txt", "LR2_2_5.txt",
            "LRC2_2_1.txt", "LRC2_2_2.txt", "LRC2_2_3.txt", "LRC2_2_4.txt",
            "LRC2_2_5.txt"};

    private Map<String, String[]> tests;
    private Map<String, String> paths;

    private List<Commission> commissions;
    private Point2D.Double depot;
    private String fileName;

    public PatternCalculator() {
        tests = new TreeMap<>();
        paths = new TreeMap<>();

        tests.put("pdp_100", pdp_100);
        paths.put("pdp_100", "pdp_100");

        tests.put("pdp_100_2", pdp_100_2);
        paths.put("pdp_100_2", "pdp_100");

        tests.put("pdp_200", pdp_200);
        paths.put("pdp_200", "pdp_200");

        tests.put("pdp_200_2", pdp_200_2);
        paths.put("pdp_200_2", "pdp_200");
    }

    public PatternCalculator(String fileName) {
        initNextTest(fileName);
        this.fileName = fileName;
    }

    public PatternCalculator(List<Commission> commissions) {
        this.commissions = commissions;
    }

    public static void main(String args[]) {
        try {
            new PatternCalculator().calculate("patterns.xls");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public List<Commission> getCommissions() {
        return commissions;
    }

    private void initNextTest(String fileName) {
        Commission[] commissions = TxtFileReader.getCommissions(fileName);
        this.commissions = new LinkedList<>();
        Collections.addAll(this.commissions, commissions);

        int depotX;
        int depotY;

        depotX = (int) TxtFileReader.getDepot(fileName).getX();
        depotY = (int) TxtFileReader.getDepot(fileName).getY();

        this.depot = new Point2D.Double(depotX, depotY);
    }

    private double average(List<Double> values) {
        double result = 0.0;
        for (Double v : values)
            result += v;
        result /= values.size();
        return result;
    }

    private double standardDeviation(List<Double> values) {
        double result = 0.0;
        double avg = average(values);
        for (Double v : values)
            result += Math.pow(v - avg, 2);
        result = Math.sqrt(result);
        return result;
    }

    /* Srednia z ilosci ladunkow w kazdym zleceniu */
    public Double pattern1() {
        List<Double> values = new LinkedList<>();
        for (Commission com : commissions) {
            values.add((double) com.getLoad());
        }
        return average(values);
    }

    /* Odchylenie standardowe z ilosci ladunkow w kazdym zleceniu */
    public Double pattern2() {
        List<Double> values = new LinkedList<>();
        for (Commission com : commissions) {
            values.add((double) com.getLoad());
        }
        return standardDeviation(values);
    }

    /* Srednia z odleglosci miedzy parami zaladunku, wyladunku oraz baza */
    public Double pattern3() {
        List<Double> values = new LinkedList<>();
        double dist;
        for (Commission com : commissions) {
            dist = 0.0;
            dist += Helper.calculateDistance(depot,
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getPickupX(), com.getPickupY()),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()),
                    depot);
            values.add(dist);
        }
        return average(values);
    }

    /*
     * Odchylenie standardowe z odleglosci miedzy parami zaladunku, wyladunku
     * oraz baza
     */
    public Double pattern4() {
        List<Double> values = new LinkedList<>();
        double dist;
        for (Commission com : commissions) {
            dist = 0.0;
            dist += Helper.calculateDistance(depot,
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getPickupX(), com.getPickupY()),
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            dist += Helper.calculateDistance(
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()),
                    depot);
            values.add(dist);
        }
        return standardDeviation(values);
    }

    /* Srednia dlugosc okien czasowych */
    public Double pattern5() {
        List<Double> values = new LinkedList<>();
        for (Commission com : commissions) {
            values.add(com.getPickupTime2() - com.getPickupTime1());
            values.add(com.getDeliveryTime2() - com.getDeliveryTime1());
        }
        return average(values);
    }

    private Point2D.Double getNearestLocation(Point2D.Double location,
                                              Integer comId) {
        Point2D.Double nearestLocation = null;
        double bestDistance = Double.MAX_VALUE;
        double dist;
        for (Commission com : commissions) {
            dist = Helper.calculateDistance(location,
                    new Point2D.Double(com.getPickupX(), com.getPickupY()));
            if (comId != com.getPickUpId() && dist < bestDistance) {
                bestDistance = dist;
                nearestLocation = new Point2D.Double(com.getPickupX(),
                        com.getPickupY());
            }
            dist = Helper.calculateDistance(location,
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY()));
            if (comId != com.getDeliveryId() && dist < bestDistance) {
                bestDistance = dist;
                nearestLocation = new Point2D.Double(com.getDeliveryX(),
                        com.getDeliveryY());
            }
        }
        return nearestLocation;
    }

    /*
     * Srednia z najmniejszych odleglosci miedzy poszczegolnymi punktami
     * zaladunku/wyladunku (liczone dla kazdego punktu)
     */
    public Double pattern6() {
        List<Double> values = new LinkedList<>();
        Point2D.Double location;
        for (Commission com : commissions) {
            location = new Point2D.Double(com.getPickupX(), com.getPickupY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(location, com.getPickUpId())));
            location = new Point2D.Double(com.getDeliveryX(),
                    com.getDeliveryY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(location, com.getDeliveryId())));
        }
        return average(values);
    }

    /*
     * Odchylenie standardowe z najmniejszych odleglosci miedzy poszczegolnymi
     * punktami zaladunku/wyladunku (liczone dla kazdego punktu)
     */
    public Double pattern7() {
        List<Double> values = new LinkedList<>();
        Point2D.Double location;
        for (Commission com : commissions) {
            location = new Point2D.Double(com.getPickupX(), com.getPickupY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(location, com.getPickUpId())));
            location = new Point2D.Double(com.getDeliveryX(),
                    com.getDeliveryY());
            values.add(Helper.calculateDistance(location,
                    getNearestLocation(location, com.getDeliveryId())));
        }
        return standardDeviation(values);
    }

    /* Odlegosc srodka ciezkosci od bazy */
    public Double pattern8() {
        List<Double> x = new LinkedList<>();
        List<Double> y = new LinkedList<>();
        for (Commission com : commissions) {
            x.add(com.getPickupX());
            x.add(com.getDeliveryX());
            y.add(com.getPickupY());
            y.add(com.getDeliveryY());
        }
        return Helper.calculateDistance(depot, new Point2D.Double(average(x),
                average(y)));
    }

    private double getCommissionsBetweenTimeWindow(double time1, double time2,
                                                   int id) {
        int result = 0;
        for (Commission com : commissions) {
            if (com.getPickUpId() != id) {
                if (com.getPickupTime1() >= time1
                        && com.getPickupTime2() <= time2)
                    result++;
            }
            if (com.getDeliveryId() != id) {
                if (com.getDeliveryTime1() >= time1
                        && com.getDeliveryTime2() <= time2)
                    result++;
            }
        }
        return result;
    }

    /* Wskaznik do odroznienia problemow z waskimi i szerokimi oknami */
    public Double pattern9() {
        List<Double> values = new LinkedList<>();
        for (Commission com : commissions) {
            values.add(getCommissionsBetweenTimeWindow(com.getPickupTime1(),
                    com.getPickupTime2(), com.getPickUpId()));
            values.add(getCommissionsBetweenTimeWindow(com.getDeliveryTime1(),
                    com.getDeliveryTime2(), com.getDeliveryId()));
        }
        return average(values);
    }

    /* Max okno czasowe */
    public Double pattern10() {
        Double max = 0.0;
        double time;
        for (Commission com : commissions) {
            time = com.getPickupTime2() - com.getPickupTime1();
            if (time > max)
                max = time;
            time = com.getDeliveryTime2() - com.getDeliveryTime1();
            if (time > max)
                max = time;
        }
        return max;
    }

    /* Min okno czasowe */
    public Double pattern11() {
        Double min = Double.MAX_VALUE;
        double time;
        for (Commission com : commissions) {
            time = com.getPickupTime2() - com.getPickupTime1();
            if (time < min)
                min = time;
            time = com.getDeliveryTime2() - com.getDeliveryTime1();
            if (time < min)
                min = time;
        }
        return min;
    }

    /*
     * Srednia z wiekszych okien czasowych (pickup lub delivery) w ramach
     * ka�dego zlecenia
     */
    public Double pattern12() {
        List<Double> values = new LinkedList<>();
        double time;
        double time2;
        for (Commission com : commissions) {
            time = com.getPickupTime2() - com.getPickupTime1();
            time2 = com.getDeliveryTime2() - com.getDeliveryTime1();
            values.add(Math.max(time, time2));
        }
        return average(values);
    }

    /*
     * Srednia z mniejszych okien czasowych (pickup lub delivery) w ramach
     * ka�dego zlecenia
     */
    public Double pattern13() {
        List<Double> values = new LinkedList<>();
        double time;
        double time2;
        for (Commission com : commissions) {
            time = com.getPickupTime2() - com.getPickupTime1();
            time2 = com.getDeliveryTime2() - com.getDeliveryTime1();
            values.add(Math.min(time, time2));
        }
        return average(values);
    }

    /* Odchylenie standardowe z dystansow zlecen od bazy */
    public Double pattern14() {
        List<Double> values = new LinkedList<>();
        for (Commission com : commissions) {
            values.add(Helper.calculateDistance(depot,
                    new Point2D.Double(com.getPickupX(), com.getPickupY())));
            values.add(Helper.calculateDistance(depot,
                    new Point2D.Double(com.getDeliveryX(), com.getDeliveryY())));
        }
        return standardDeviation(values);
    }

    public void calculate(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedWriter wr = new BufferedWriter(new FileWriter(file));

        wr.write("wskaznik1 - Srednia z ilosci ladunkow w kazdym zleceniu");
        wr.newLine();
        wr.write("wskaznik2 - Odchylenie standardowe z ilosci ladunkow w kazdym zleceniu");
        wr.newLine();
        wr.write("wskaznik3 - Srednia z odleglosci miedzy parami zaladunku, wyladunku oraz baza");
        wr.newLine();
        wr.write("wskaznik4 - Odchylenie standardowe z odleglosci miedzy parami zaladunku, wyladunku oraz baza");
        wr.newLine();
        wr.write("wskaznik5 - Srednia dlugosc okien czasowych");
        wr.newLine();
        wr.write("wskaznik6 - Srednia z najmniejszych odleglosci miedzy poszczegolnymi punktami zaladunku/wyladunku (liczone dla kazdego punktu)");
        wr.newLine();
        wr.write("wskaznik7 - Odchylenie standardowe z najmniejszych odleglosci miedzy poszczegolnymi punktami zaladunku/wyladunku (liczone dla kazdego punktu)");
        wr.newLine();
        wr.write("wskaznik8 - Odlegosc srodka ciezkosci od bazy");
        wr.newLine();
        wr.write("wskaznik9 - Wskaznik do odroznienia problemow z waskimi i szerokimi oknami");
        wr.newLine();
        wr.write("wskaznik10 - Max okno czasowe");
        wr.newLine();
        wr.write("wskaznik11 - Min okno czasowe");
        wr.newLine();
        wr.write("wskaznik12 - Srednia z wiekszych okien czasowych (pickup lub delivery) w ramach ka�dego zlecenia");
        wr.newLine();
        wr.write("wskaznik13 - Srednia z mniejszych okien czasowych (pickup lub delivery) w ramach ka�dego zlecenia");
        wr.newLine();
        wr.write("wskaznik14 - Odchylenie standardowe z dystansow zlecen od bazy");
        wr.newLine();
        wr.newLine();
        wr.flush();

        String name;
        for (String key : tests.keySet()) {
            wr.write(key);
            wr.newLine();
            wr.write("\twskaznik1\twskaznik2\twskaznik3\twskaznik4\twskaznik5\twskaznik6\twskaznik7\twskaznik8\twskaznik9\twskaznik10\twskaznik11\twskaznik12\twskaznik13\twskaznik14");
            wr.newLine();
            for (String f : tests.get(key)) {
                name = benchmarksPath + File.separator + paths.get(key)
                        + File.separator + f;
                initNextTest(name);

                wr.write(f + "\t");
                wr.write(pattern1().toString() + "\t");
                wr.write(pattern2().toString() + "\t");
                wr.write(pattern3().toString() + "\t");
                wr.write(pattern4().toString() + "\t");
                wr.write(pattern5().toString() + "\t");
                wr.write(pattern6().toString() + "\t");
                wr.write(pattern7().toString() + "\t");
                wr.write(pattern8().toString() + "\t");
                wr.write(pattern9().toString() + "\t");
                wr.write(pattern10().toString() + "\t");
                wr.write(pattern11().toString() + "\t");
                wr.write(pattern12().toString() + "\t");
                wr.write(pattern13().toString() + "\t");
                wr.write(pattern14().toString());
                wr.newLine();
                wr.flush();
            }
            wr.newLine();
            wr.newLine();
            wr.flush();
        }
    }

}
