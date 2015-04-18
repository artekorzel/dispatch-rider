package dtp.jade.eunit;

import dtp.commission.Commission;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportOffer;
import org.apache.log4j.Logger;

import java.util.*;

public class HolonReorganizeAuction {

    public static final int FULL = 0;
    public static final int REPRESENTATIVES = 1;
    public static final int BEST_REPRESENTATIVES = 2; //metoda przyrostowa
    //MODIFICATION BY LP
    /*public static final int K_REPRESENTATIVES=3;*/
    public static final int GENETIC = 4;
    //end of modification

    private static Logger logger = Logger.getLogger(HolonReorganizeAuction.class);

    private int reorganizationType;
    private int reorganizationTypeParam;

    private Commission commission;

    private int offersReceived = 0;

    private int commissionsSent = 0;

    private int loadRequired;
    private TransportOffer driverBackup;
    private TransportOffer truckBackup;
    private TransportOffer trailerBackup;

    private List<TransportOffer> drivers = new ArrayList<TransportOffer>();

    private List<TransportOffer> trucks = new ArrayList<TransportOffer>();

    private List<TransportOffer> trailers = new ArrayList<TransportOffer>();


    private long reorganizationTime;

    public HolonReorganizeAuction(int commissions) {
        commissionsSent = commissions;
    }

    public void setReorganizationType(int type, int param) {
        reorganizationType = type;
        reorganizationTypeParam = param;
    }

    /**
     * @return the commission
     */
    public Commission getCommission() {
        return commission;
    }

    /**
     * @param commission the commission to set
     */
    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public TransportOffer[] getBestTeam() {
        long begin = Calendar.getInstance().getTimeInMillis();
        TransportOffer[] result = null;
        switch (reorganizationType) {
            case FULL: {
                result = full();
                reorganizationTime = Calendar.getInstance().getTimeInMillis() - begin;
                return result;
            }
            case REPRESENTATIVES: {
                result = representatives();
                reorganizationTime = Calendar.getInstance().getTimeInMillis() - begin;
                return result;
            }
            case BEST_REPRESENTATIVES: {
                result = bestRep();
                reorganizationTime = Calendar.getInstance().getTimeInMillis() - begin;
                return result;
            }
            //MODIFICATION BY LP
            /*case K_REPRESENTATIVES: {
                result=K_representatives();
                reorganizationTime=Calendar.getInstance().getTimeInMillis()-begin;
                return result;
            }*/
            case GENETIC: {
                result = genetic();
                reorganizationTime = Calendar.getInstance().getTimeInMillis() - begin;
                return result;
            }
            //end of modification
            default:
                return null;
        }
    }

    public long getReorganizationTime() {
        return reorganizationTime;
    }

    private TransportOffer[] full() {

        TransportOffer[] team = new TransportOffer[3];

        System.out.println("Old team: " + driverBackup.getAid().getLocalName() + " " + truckBackup.getAid().getLocalName() + " " + trailerBackup.getAid().getLocalName());

        double minimalCost = Double.MAX_VALUE;
        if (!trucks.contains(truckBackup)) {
            trucks.add(truckBackup);
        }
        if (!trailers.contains(trailerBackup) && ((TransportElementInitialDataTrailer) (trailerBackup.getTransportElementData())).getCapacity_() >= getLoadRequired()) {
            trailers.add(trailerBackup);
        }

        if (trailers.size() <= 0 || trucks.size() <= 0) {
            team[0] = driverBackup;
            team[1] = truckBackup;
            team[2] = trailerBackup;
            return team;
        }

        for (TransportOffer truck : trucks) {
            for (TransportOffer trailer : trailers) {
                if (truck.getTransportElementData() == null || trailer.getTransportElementData() == null)
                    continue;

                TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck.getTransportElementData();
                TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer.getTransportElementData();

                if (truckData.getConnectorType() != trailerData.getConnectorType())
                    continue;

                if (truckData.getPower() < trailerData.getMass() + trailerData.getCapacity_())
                    continue;

                double dist = (commission.getPickupX() - commission.getDeliveryX()) * (commission.getPickupX() - commission.getDeliveryX()) + (commission.getPickupY() - commission.getDeliveryY()) * (commission.getPickupY() - commission.getDeliveryY());
                double newCost = 0.01 * dist * (4 - truckData.getComfort()) + (dist / 100) * ((double) truckData.getFuelConsumption()) * (((double) (trailerData.getMass() + commission.getLoad())) / truckData.getPower());

                if (newCost < minimalCost) {
                    team[1] = truck;
                    team[2] = trailer;
                    minimalCost = newCost;
                }
            }
        }
        team[0] = driverBackup;

        if (team[1] == null) {
            team[0] = driverBackup;
            team[1] = truckBackup;
            team[2] = trailerBackup;
        }

        System.out.println("New team: " + team[0].getAid().getLocalName() + " " + team[1].getAid().getLocalName() + " " + team[2].getAid().getLocalName());
        
/*        TransportOffer trailer = trailers.get(0);
        for (TransportOffer item : trailers) {
            if (item.getRatio() > 0) {
                if (trailer.getRatio() > 0) {
                    if (trailer.getRatio() > item.getRatio()) {
                        trailer = item;
                    }
                } else {
                    trailer = item;
                }
            }
        }
        logger.info("Trailer Size = " + trailer.getRatio());
        if (trailer.getRatio() < 0) {
            return null;
        }
        TransportOffer truck = trucks.get(0);
        for (TransportOffer item : trucks) {
            if (item.getRatio() > 0) {
                if (truck.getRatio() > 0) {
                    if (truck.getRatio() > item.getRatio() && truck.getMaxLoad() >= trailer.getMaxLoad()) {
                        truck = item;
                    }
                } else if (truck.getMaxLoad() >= trailer.getMaxLoad()) {
                    truck = item;
                }
            }
        }
        logger.info("Truck Size = " + truck.getRatio());
        if (truck.getMaxLoad() < trailer.getMaxLoad() || truck.getRatio() < 0) {
            return null;
        }
        for (TransportOffer item : drivers) {
            if (item.getRatio() > 0) {
                team[0] = item;
                team[1] = truck;
                team[2] = trailer;
            }
        }
 */

        return team;
    }


    private TransportOffer[] bestRep() {
        TransportOffer[] team = new TransportOffer[3];

        System.out.println("Old team: " + driverBackup.getAid().getLocalName() + " " + truckBackup.getAid().getLocalName() + " " + trailerBackup.getAid().getLocalName());

        if (!trucks.contains(truckBackup)) {
            trucks.add(truckBackup);
        }
        if (!trailers.contains(trailerBackup) && ((TransportElementInitialDataTrailer) (trailerBackup.getTransportElementData())).getCapacity_() >= getLoadRequired()) {
            trailers.add(trailerBackup);
        }

        if (trailers.size() <= 0 || trucks.size() <= 0) {
            team[0] = driverBackup;
            team[1] = truckBackup;
            team[2] = trailerBackup;
            return team;
        }

        TransportOffer trailer = trailers.get(0);
        for (TransportOffer item : trailers) {
            if (item.getRatio() > 0) {
                if (trailer.getRatio() > 0) {
                    if (trailer.getRatio() > item.getRatio()) {
                        trailer = item;
                    }
                } else {
                    trailer = item;
                }
            }
        }
        logger.info("Trailer Size = " + trailer.getRatio());
        if (trailer.getRatio() < 0) {
            return null;
        }
        TransportOffer truck = trucks.get(0);
        for (TransportOffer item : trucks) {
            if (item.getRatio() > 0) {
                if (truck.getRatio() > 0) {
                    if (truck.getRatio() > item.getRatio() && truck.getMaxLoad() >= trailer.getMaxLoad()) {
                        truck = item;
                    }
                } else if (truck.getMaxLoad() >= trailer.getMaxLoad()) {
                    truck = item;
                }
            }
        }
        logger.info("Truck Size = " + truck.getRatio());
        if (truck.getMaxLoad() < trailer.getMaxLoad() || truck.getRatio() < 0) {
            return null;
        }
        for (TransportOffer item : drivers) {
            if (item.getRatio() > 0) {
                team[0] = item;
                team[1] = truck;
                team[2] = trailer;
            }
        }

        System.out.println("New team: " + team[0].getAid().getLocalName() + " " + team[1].getAid().getLocalName() + " " + team[2].getAid().getLocalName());

        return team;
    }


    private TransportOffer[] representatives() {

        TransportOffer[] team = new TransportOffer[3];

        System.out.println("Old team: " + driverBackup.getAid().getLocalName() + " " + truckBackup.getAid().getLocalName() + " " + trailerBackup.getAid().getLocalName());

        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        }


        HashMap<Integer, LinkedList<OfferWithCost>> truckOffers = new HashMap<Integer, LinkedList<OfferWithCost>>();
        HashMap<Integer, LinkedList<OfferWithCost>> trailerOffers = new HashMap<Integer, LinkedList<OfferWithCost>>();
        double cost = 0;
        double dist = (commission.getPickupX() - commission.getDeliveryX()) * (commission.getPickupX() - commission.getDeliveryX()) + (commission.getPickupY() - commission.getDeliveryY()) * (commission.getPickupY() - commission.getDeliveryY());

        TreeSet<Integer> truckConnectorTypes = new TreeSet<Integer>();
        for (TransportOffer truck : trucks) {
            if (truck.getTransportElementData() == null) continue;

            TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck.getTransportElementData();

            truckConnectorTypes.add(truckData.getConnectorType());
        }

        TreeSet<Integer> trailersConnectorTypes = new TreeSet<Integer>();
        for (TransportOffer trailer : trailers) {
            if (trailer.getTransportElementData() == null) continue;

            TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer.getTransportElementData();

            trailersConnectorTypes.add(trailerData.getConnectorType());
        }

        TreeSet<Integer> bothConnectorTypes = new TreeSet<Integer>();
        for (Integer el : truckConnectorTypes) {
            if (trailersConnectorTypes.contains(el)) bothConnectorTypes.add(el);
        }

        for (int connector : bothConnectorTypes) {
            truckOffers.put(connector, new LinkedList<OfferWithCost>());
            trailerOffers.put(connector, new LinkedList<OfferWithCost>());
        }

        for (TransportOffer truck : trucks) {
            if (truck.getTransportElementData() == null) continue;

            TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck.getTransportElementData();

            cost = 0.01 * dist * (4 - truckData.getComfort()) + (dist / 100) * ((double) truckData.getFuelConsumption()) * (commission.getLoad() / truckData.getPower());

            if (bothConnectorTypes.contains(truckData.getConnectorType())) {
                truckOffers.get(truckData.getConnectorType()).add(new OfferWithCost(truck, cost));
            }
        }

        for (TransportOffer trailer : trailers) {
            if (trailer.getTransportElementData() == null) continue;

            TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer.getTransportElementData();

            if (bothConnectorTypes.contains(trailerData.getConnectorType())) {
                trailerOffers.get(trailerData.getConnectorType()).add(new OfferWithCost(trailer, trailerData.getMass()));
            }
        }


        for (int connector : bothConnectorTypes) {
            Collections.sort(truckOffers.get(connector));
            Collections.sort(trailerOffers.get(connector));
        }

        double minimalCost = Double.MAX_VALUE;

        for (int connector : bothConnectorTypes) {
            int truckLimit = truckOffers.get(connector).size() * reorganizationTypeParam / 100;
            if (truckLimit == 0) truckLimit = 1;
            for (int i = 0; i < truckLimit; i++) {
                int trailerLimit = trailerOffers.get(connector).size() * reorganizationTypeParam / 100;
                if (trailerLimit == 0) trailerLimit = 1;
                for (int j = 0; j < trailerLimit; j++) {
                    TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truckOffers.get(connector).get(i).getOffer().getTransportElementData();
                    TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailerOffers.get(connector).get(j).getOffer().getTransportElementData();


                    if (truckData.getPower() < trailerData.getMass() + trailerOffers.get(connector).get(j).getOffer().getMaxLoad()) // commission.getLoad())
                        continue;

                    if (truckOffers.get(connector).get(i).getCost() + trailerOffers.get(connector).get(j).getCost() < minimalCost) {
                        minimalCost = truckOffers.get(connector).get(i).getCost() + trailerOffers.get(connector).get(j).getCost();
                        team[1] = truckOffers.get(connector).get(i).getOffer();
                        team[2] = trailerOffers.get(connector).get(j).getOffer();
                    }
                }
            }
        }

        team[0] = drivers.get(0);

        if (team[1] == null) {
            return null;
        }

        System.out.println("New team: " + team[0].getAid().getLocalName() + " " + team[1].getAid().getLocalName() + " " + team[2].getAid().getLocalName());

        return team;
    }

    //MODIFICATIONS BY LP
    private TransportOffer[] full2(List<TransportOffer> copyOfDrivers, List<TransportOffer> copyOfTrucks, List<TransportOffer> copyOfTrailers) {


        TransportOffer[] team = new TransportOffer[3];
        if (copyOfDrivers.size() <= 0 || copyOfTrailers.size() <= 0 || copyOfTrucks.size() <= 0) {
            return null;
        }

        double minimalCost = Double.MAX_VALUE;

        for (TransportOffer truck : copyOfTrucks) {
            for (TransportOffer trailer : copyOfTrailers) {
                if (truck.getTransportElementData() == null || trailer.getTransportElementData() == null)
                    continue;

                TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck.getTransportElementData();
                TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer.getTransportElementData();

                if (truckData.getConnectorType() != trailerData.getConnectorType())
                    continue;

                if (truckData.getPower() < trailerData.getMass() + trailer.getMaxLoad())
                    continue;

                double dist = (commission.getPickupX() - commission.getDeliveryX()) * (commission.getPickupX() - commission.getDeliveryX()) + (commission.getPickupY() - commission.getDeliveryY()) * (commission.getPickupY() - commission.getDeliveryY());
                double newCost = 0.01 * dist * (4 - truckData.getComfort()) + (dist / 100) * ((double) truckData.getFuelConsumption()) * (((double) (trailerData.getMass() + commission.getLoad())) / truckData.getPower());
                if (newCost < minimalCost) {
                    team[1] = truck;
                    team[2] = trailer;
                    minimalCost = newCost;
                }
            }
        }
        team[0] = copyOfDrivers.get(0);
        if (copyOfDrivers.contains(team[0])) {
            copyOfDrivers.remove(team[0]);
        } else {
            System.out.println("driver remove problem\n");
        }
        if (copyOfTrucks.contains(team[1])) {
            copyOfTrucks.remove(team[1]);
        } else {
            System.out.println("truck remove problem\n");
        }
        if (copyOfTrailers.contains(team[2])) {
            copyOfTrailers.remove(team[2]);
        } else {
            System.out.println("trailer remove problem\n");
        }

        if (team[0] == null || team[1] == null || team[2] == null) {
            System.out.println("Null problem\n");
            return null;
        }

        return team;
    }

    private TransportOffer[] genetic() {
        List<TransportOffer> copyOfDrivers = new ArrayList<TransportOffer>();
        List<TransportOffer> copyOfTrucks = new ArrayList<TransportOffer>();
        List<TransportOffer> copyOfTrailers = new ArrayList<TransportOffer>();

        List<TransportOffer[]> tabooList = new ArrayList<TransportOffer[]>();

        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        } else {

            for (TransportOffer it : drivers) {
                copyOfDrivers.add(it);
            }
            for (TransportOffer it : trucks) {
                copyOfTrucks.add(it);
            }
            for (TransportOffer it : trailers) {
                copyOfTrailers.add(it);
            }
        }


        TransportElementInitialDataTruck truckData;
        TransportElementInitialDataTrailer trailerData;

        /*INITIALISATION STEP*/
        List<TransportOffer[]> Array = new ArrayList<TransportOffer[]>();

        /*initialize the array*/
        int min;
        if (drivers != null && trucks != null && trailers != null) {
            min = trucks.size();
            if (min > trailers.size()) {
                min = trailers.size();
            }
            if (min > drivers.size()) {
                min = drivers.size();
            }
        } else {
            min = 0;
        }

        for (int i = 0; i < min; i++) {
            Array.add(full2(copyOfDrivers, copyOfTrucks, copyOfTrailers)/*full()*/);
        }

        while (Array.size() >= 2) {
            /*SELECTION STEP */
            /*we have to keep X% of the best teams built */
            int size = Array.size();

            while ((double) Array.size() > (double) (size * (80 / 100)) && Array.size() > 1) {
                double firstCost = 0;
                int counter = 0;
                /*delete the worst entry of the HashMap */
                for (int j = 0; j < Array.size(); j++) {
                    TransportOffer[] offerArray = new TransportOffer[3];
                    offerArray = Array.get(j);

                    if ((offerArray[0] != null) && (offerArray[1] != null) && (offerArray[2] != null)) {
                        truckData = (TransportElementInitialDataTruck) offerArray[1].getTransportElementData();
                        trailerData = (TransportElementInitialDataTrailer) offerArray[2].getTransportElementData();

                        double dist = (commission.getPickupX() - commission.getDeliveryX()) * (commission.getPickupX() - commission.getDeliveryX()) + (commission.getPickupY() - commission.getDeliveryY()) * (commission.getPickupY() - commission.getDeliveryY());
                        double Cost = 0.01 * dist * (4 - truckData.getComfort()) + (dist / 100) * ((double) truckData.getFuelConsumption()) * (((double) (trailerData.getMass() + commission.getLoad())) / truckData.getPower());

                        if (Cost > firstCost) {
                            firstCost = Cost;
                            counter = j;
                        }
                    } else {
                        counter = 0;
                    }
                }
                tabooList.add(Array.get(counter));
                Array.remove(counter);
            }


            /*REPRODUCTION STEP */
            for (int j = 0; j < Array.size(); j++) {
                /*mutations*/
                mutationTruck(Array.get(j), trucks, tabooList);
                mutationTrailer(Array.get(j), trailers, tabooList);
                /*crossover*/
                for (int k = j + 1; k < Array.size(); k++) {
                    crossOver(Array.get(j), Array.get(k), tabooList);
                }
            }
        }
        if ((Array.get(0))[0] == null || (Array.get(0))[1] == null || (Array.get(0))[2] == null) {
            System.out.println("Problem Creation\n");
            return null;
        } else {
            return Array.get(0);
        }

    }

    /*
     * @param : takes 1 team and a list of trucks as parameters
     * Perform mutation of the truck.
     */
    private void mutationTruck(TransportOffer[] team, List<TransportOffer> trucks, List<TransportOffer[]> tabooList) {
        double mutation = java.lang.Math.random();
        /*rate of mutation defined bellow 0.1% */
        if (mutation < 0.001) {
            /*select a random number in the size of LinkedList*/
            int i = 0;
            TransportOffer[] temp = new TransportOffer[3];
            do {
                do {
                    i = (int) java.lang.Math.abs(java.lang.Math.random() * trucks.size());
                } while (i > trucks.size());
                temp[0] = team[0];
                temp[1] = trucks.get(i);
                temp[2] = team[2];
            } while (tabooList.contains(temp));
            team[1] = trucks.get(i);

        }

    }

    /*
     * @param : takes 1 team and a list of trailers as parameters
     * Perform mutation of the trailer.
     */
    private void mutationTrailer(TransportOffer[] team, List<TransportOffer> trailers, List<TransportOffer[]> tabooList) {
        double mutation = java.lang.Math.random();
        /*rate of mutation defined bellow 0.1% */
        if (mutation <= 0.001) {
            /*select a random number in the size of LinkedList*/
            int i = 0;
            TransportOffer[] temp = new TransportOffer[3];
            do {
                do {
                    i = (int) java.lang.Math.abs(java.lang.Math.random() * trailers.size());
                } while (i > trailers.size());
                temp[0] = team[0];
                temp[1] = team[1];
                temp[2] = trailers.get(i);
            } while (tabooList.contains(temp));
            team[2] = trailers.get(i);
        }

    }

    /*
     * @param : takes 2 teams in parameter
     * Perform a crossover between 2 teams.
     */
    private void crossOver(TransportOffer[] team1, TransportOffer[] team2, List<TransportOffer[]> tabooList) {
        double crossover = java.lang.Math.random();
        /*rate of mutation is defined bellow 70% */
        if (crossover <= 0.7) {
            double whichKindOfCrossOver = java.lang.Math.random();
            /*if the condition is verified do a one-point-crossover */
            if (whichKindOfCrossOver <= 0.5) {
                TransportOffer tempTruck = team1[1];
                TransportOffer tempTrailer = team1[2];
                TransportOffer[] temp1 = new TransportOffer[3];
                TransportOffer[] temp2 = new TransportOffer[3];
                temp1[0] = team1[0];
                temp1[1] = team2[1];
                temp1[2] = team2[2];
                temp2[0] = team2[0];
                temp2[1] = tempTruck;
                temp2[2] = tempTrailer;
                if (!tabooList.contains(temp1) && !tabooList.contains(temp2)) {
                    team1[1] = team2[1];
                    team1[2] = team2[2];
                    team2[1] = tempTruck;
                    team2[2] = tempTrailer;
                }
            /* else do a two-points-crossover */
            } else {
                TransportOffer temp = team1[1];
                TransportOffer[] temp1 = new TransportOffer[3];
                TransportOffer[] temp2 = new TransportOffer[3];
                temp1[0] = team1[0];
                temp1[1] = team2[1];
                temp1[2] = team1[2];
                temp2[0] = team2[0];
                temp2[1] = temp;
                temp2[2] = team2[2];
                if (!tabooList.contains(temp1) && !tabooList.contains(temp2)) {
                    team1[1] = team2[1];
                    team2[1] = temp;
                }
            }
        }
    }
    //END OF MODIFICATIONS

    public synchronized boolean addOfffer(TransportOffer offer) {
        offersReceived++;
        switch (offer.getOfferType()) {
            case DRIVER:
                drivers.add(offer);
                break;
            case TRUCK:
                trucks.add(offer);
                break;
            case TRAILER:
                trailers.add(offer);
                break;
            default:
                System.err.println("SOMETHING WENT WRONG.......");
                break;
        }
        return offersReceived >= commissionsSent;
    }

    public int getLoadRequired() {
        return loadRequired;
    }
}
