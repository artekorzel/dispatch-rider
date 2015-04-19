package dtp.commission;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KONY
 */
public class TxtFileReader {

    /**
     * Zwraca tablice zlecen przeczytanych z pliku zgodnego z konwencja
     * http://www.sintef.no/static/am/opti/projects/top/vrp/format_pdp.htm
     *
     * @param fileName nazwa pliku z definicja problemu
     * @return tablica zlecen
     */
    public static Commission[] getCommissions(String fileName) {

        return combineCommissions(read(fileName));
    }

    /**
     * @param fileName nazwa pliku z definicja problemu
     * @return ladownosc pojazdu
     */
    public static int getTruckCapacity(String fileName) {

        BufferedReader in;

        String line = "";
        String[] lineParts;

        try {

            in = new BufferedReader(new FileReader(fileName));

        } catch (FileNotFoundException e) {

            System.out.println("TxtFileReader.read() -> no such file: "
                    + fileName);
            return -1;
        }

        try {
            line = in.readLine();

        } catch (IOException e) {

            System.out.println("TxtFileReader.read() -> IOException occured");
        }

        lineParts = line.split("\t");

        return new Integer(lineParts[1]);
    }

    /**
     * @param fileName nazwa pliku z definicja problemu
     * @return lokacja bazy transportowej
     */
    public static Point2D.Double getDepot(String fileName) {

        BufferedReader in;

        String line = "";
        String[] lineParts;

        try {

            in = new BufferedReader(new FileReader(fileName));

        } catch (FileNotFoundException e) {

            System.out.println("TxtFileReader.read() -> no such file: "
                    + fileName);
            return null;
        }

        try {
            line = in.readLine();
            line = in.readLine(); // that's where it is

        } catch (IOException e) {

            System.out.println("TxtFileReader.read() -> IOException occured");
        }

        lineParts = line.split("\t");

        return new Point2D.Double(Integer.parseInt(lineParts[1]),
                Integer.parseInt(lineParts[2]));
    }

    /**
     * @param fileName nazwa pliku z definicja problemu
     * @return deadline (wszystkie zlecenia musza zostac zrealizowane przed tym
     * czasem)
     */
    public static int getDeadline(String fileName) {

        BufferedReader in;

        String line = "";
        String[] lineParts;

        try {

            in = new BufferedReader(new FileReader(fileName));

        } catch (FileNotFoundException e) {

            System.out.println("TxtFileReader.read() -> no such file: "
                    + fileName);
            return -1;
        }

        try {
            line = in.readLine();
            line = in.readLine(); // that's where it is

        } catch (IOException e) {

            System.out.println("TxtFileReader.read() -> IOException occured");
        }

        lineParts = line.split("\t");

        return new Integer(lineParts[5]);
    }

    /**
     * @param filename nazwa pliku z definicja problemu
     * @return odleglosc najdalszego punktu zaladunku od bazy
     */
    public static double getFarthestPickupLocation(String filename) {

        Commission[] commissions;
        Point2D.Double depot;

        double tempDistance;
        double farthestPickupLocVal = Double.MIN_VALUE;

        commissions = getCommissions(filename);
        depot = getDepot(filename);

        for (Commission commission : commissions) {
            tempDistance = Point.distance(depot.getX(), depot.getY(),
                    commission.getPickupX(), commission.getPickupY());

            if (tempDistance > farthestPickupLocVal) {

                farthestPickupLocVal = tempDistance;
            }
        }

        if (farthestPickupLocVal != Double.MAX_VALUE)
            return farthestPickupLocVal;
        else {
            System.out.println("TxtFileReader.getFarthestPickupLocation -> "
                    + "no farthest pickup location :(");
            return -1;
        }
    }

    /**
     * Czyta plik zapisany zgodnie z konwencja
     * http://www.sintef.no/static/am/opti/projects/top/vrp/format_pdp.htm
     *
     * @param fileName nazwa pliku z definicja problemu
     * @return tablica zlecen czesciowych
     */
    private static CommissionPart[] read(String fileName) {

        BufferedReader in;

        String line;
        String lineParts[];

        CommissionPart comParts[];
        int partsNo = 0;

        int counter = 0;

        try {
            System.out.println(new File(fileName).getAbsolutePath());
            in = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();//FIXME
            return null;
        }

        try {
            while (in.readLine() != null) {
                partsNo++;
            }
        } catch (IOException e) {
            e.printStackTrace();//FIXME
            return null;
        }

        try {
            in = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("TxtFileReader.read() -> no such file: "
                    + fileName);
            return null;
        }

        // zagodnie z konwencja pierwsze dwie linie nie reprezentuja zlecenia
        // zlecenia sa numerowane od 1
        comParts = new CommissionPart[partsNo - 1];

        try {
            while ((line = in.readLine()) != null) {
                lineParts = line.split("\t");

                // pierwsza linia informuje o ilosci dostepnych pojazdow oraz
                // ich
                // ladownosci
                if (counter > 1) {
                    if (lineParts.length < 9 || lineParts.length > 10) {
                        System.out.println("TxtFileReader -> wrong file format");
                    } else {
                        comParts[counter - 1] = new CommissionPart(
                                Integer.parseInt(lineParts[0]),
                                Integer.parseInt(lineParts[1]),
                                Integer.parseInt(lineParts[2]),
                                Integer.parseInt(lineParts[4]),
                                Integer.parseInt(lineParts[5]),
                                Integer.parseInt(lineParts[6]),
                                Integer.parseInt(lineParts[3]),
                                Integer.parseInt(lineParts[7]),
                                Integer.parseInt(lineParts[8]));

                        try {
                            if (lineParts.length > 9) {
                                Map<String, Double> params = new HashMap<>();
                                String[] parts;
                                for (String param : lineParts[9].split(";")) {
                                    parts = param.trim().split("=");
                                    if (parts.length != 2)
                                        throw new IllegalArgumentException();
                                    params.put(parts[0], new Double(
                                            parts[1]));
                                }
                                comParts[counter - 1]
                                        .setPunishmentFunParams(params);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();//FIXME
                            System.exit(0);
                        }
                    }
                }
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();//FIXME
            return null;
        }

        return comParts;
    }

    /**
     * Laczy czesciowe zlecenia w pary, powstaje pelne zlecenie (Commission)
     *
     * @param comParts tablica zlecen czesciowych
     * @return tablica zlecen transportowych
     */
    private static Commission[] combineCommissions(CommissionPart[] comParts) {

        if (comParts == null)
            return null;

        /**
         * zlecenia sa numerowane od 1; pierszy el tablicy bedzie zawsze pusty;
         * tablica jest indeksowana numerami zlecen do odbioru (bedzie wiec
         * wypelniona tylko w polowie)
         */
        Commission[] commissionsTmp = new Commission[comParts.length];
        for (int i = 0; i < commissionsTmp.length; i++) {

            commissionsTmp[i] = new Commission();
        }

        for (int i = 1; i < comParts.length; i++) {

            // pickup order
            if (comParts[i].pickup == 0) {

                commissionsTmp[i].setPickUpId(comParts[i].id);
                commissionsTmp[i].setPickupX(comParts[i].x);
                commissionsTmp[i].setPickupY(comParts[i].y);
                commissionsTmp[i].setPickupTime1(comParts[i].time1);
                commissionsTmp[i].setPickupTime2(comParts[i].time2);
                commissionsTmp[i].setLoad(comParts[i].load);
                commissionsTmp[i].setPickUpServiceTime(comParts[i].serviceTime);
                commissionsTmp[i].setPunishmentFunParamsPickup(comParts[i]
                        .getPunishmentFunParams());
            }
            // delivery order
            else {

                commissionsTmp[comParts[i].pickup]
                        .setDeliveryId(comParts[i].id);
                commissionsTmp[comParts[i].pickup].setDeliveryX(comParts[i].x);
                commissionsTmp[comParts[i].pickup].setDeliveryY(comParts[i].y);
                commissionsTmp[comParts[i].pickup]
                        .setDeliveryTime1(comParts[i].time1);
                commissionsTmp[comParts[i].pickup]
                        .setDeliveryTime2(comParts[i].time2);
                commissionsTmp[comParts[i].pickup]
                        .setDeliveryServiceTime(comParts[i].serviceTime);
                commissionsTmp[comParts[i].pickup]
                        .setPunishmentFunParamsDelivery(comParts[i]
                                .getPunishmentFunParams());
            }
        }

        // pozbywanie sie pustych elementow z tablicy
        Commission[] commissions = new Commission[(commissionsTmp.length - 1) / 2];
        int counter = 0;

        for (Commission aCommissionsTmp : commissionsTmp) {

            if (aCommissionsTmp.getLoad() >= 0) {

                commissions[counter] = aCommissionsTmp;
                commissions[counter].setID(counter);

                counter++;
            }
        }

        return commissions;
    }

    public static int[] getIncomeTimes(String fileName, int size) {
        int ret[] = new int[size];

        BufferedReader in;

        String line;
        int counter = 0;

        try {
            in = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e1) {
            System.out.println("TxtFileReader.read() -> no such file: "
                    + fileName);
            return null;
        }

        try {
            while ((line = in.readLine()) != null) {
                ret[counter++] = Integer.parseInt(line);
            }
        } catch (NumberFormatException e) {
            System.out
                    .println("TxtFileReader.read() -> NumberFormatException occured");
            return null;

        } catch (IOException e) {
            System.out.println("TxtFileReader.read() -> IOException occured");
            return null;
        }

        return ret;
    }
}

/**
 * funkcja pomocnicza, obiekty reprezentuja zlecenia czesciowe (tylko pickup lub
 * tylko delivery)
 *
 * @author KONY
 */
class CommissionPart {

    public int x;

    public int y;

    public int time1;

    public int time2;

    public int serviceTime;

    public int load;

    public int pickup;

    public int delivery;

    public int id;

    private Map<String, Double> punishmentFunParams = new HashMap<>();

    CommissionPart(int id, int x, int y, int time1, int time2, int serviceTime,
                   int load, int pickup, int delivery) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.time1 = time1;
        this.time2 = time2;
        this.serviceTime = serviceTime;
        this.load = load;
        this.pickup = pickup;
        this.delivery = delivery;
    }

    public Map<String, Double> getPunishmentFunParams() {
        return punishmentFunParams;
    }

    public void setPunishmentFunParams(Map<String, Double> punishmentFunParams) {
        this.punishmentFunParams = punishmentFunParams;
    }
}
