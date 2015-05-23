package pattern;

import algorithm.Helper;
import dtp.commission.Commission;
import dtp.jade.gui.TestConfiguration;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PatternCalculator {

    private List<Commission> commissions;
    private Point2D.Double depot;

    public PatternCalculator(TestConfiguration conf) {
        initNextTest(conf);
    }

    public List<Commission> getCommissions() {
        return commissions;
    }

    private void initNextTest(TestConfiguration conf) {
        Commission[] commissions = conf.getCommissions();
        this.commissions = new LinkedList<>();
        Collections.addAll(this.commissions, commissions);

        int depotX;
        int depotY;

        depotX = (int) conf.getDepot().getX();
        depotY = (int) conf.getDepot().getY();

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
}
