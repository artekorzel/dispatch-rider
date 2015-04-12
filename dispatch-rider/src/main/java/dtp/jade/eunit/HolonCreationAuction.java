package dtp.jade.eunit;

import dtp.commission.Commission;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportOffer;
import org.apache.log4j.Logger;

import java.util.*;

class OfferWithCost implements Comparable<OfferWithCost> {
    private final TransportOffer offer;
    private final double cost;

    public OfferWithCost(TransportOffer offer, double cost) {
        this.offer = offer;
        this.cost = cost;
    }

    public TransportOffer getOffer() {
        return offer;
    }

    public double getCost() {
        return cost;
    }

    public int compareTo(OfferWithCost offer) {
        if (cost > offer.getCost())
            return 1;
        if (cost == offer.getCost())
            return 0;
        return -1;
    }
}

public class HolonCreationAuction {

    public static final int FULL = 0;
    public static final int REPRESENTATIVES = 1;
    public static final int BEST_REPRESENTATIVES = 2; // metoda przyrostowa
    public static final int NEGOTIATION = 3;
    // MODIFICATION BY LP
    /* public static final int K_REPRESENTATIVES=3; */
    public static final int GENETIC = 4;
    // end of modification

    private static Logger logger = Logger.getLogger(HolonCreationAuction.class);
    private final Map<Integer, List<TransportOffer>> driversMap = new HashMap<Integer, List<TransportOffer>>();
    private final Map<Integer, List<TransportOffer>> trucksMap = new HashMap<Integer, List<TransportOffer>>();
    private final Map<Integer, List<TransportOffer>> trailersMap = new HashMap<Integer, List<TransportOffer>>();
    private final int organizationType;
    private final int organizationTypeParam;
    private Commission commission;
    private int offersReceived = 0;
    private int commissionsSent = 0;
    private int driverNum = 0;
    private int trailerNum = 0;
    private int truckNum = 0;
    private List<TransportOffer> drivers = new ArrayList<TransportOffer>();

    // MODIFICATION BY LP
    /*
     * private List<TransportOffer> copyOfDrivers = new
     * ArrayList<TransportOffer>();
     *
     * private List<TransportOffer> copyOfTrucks= new
     * ArrayList<TransportOffer>();
     *
     * private List<TransportOffer> copyOfTrailers = new
     * ArrayList<TransportOffer>();
     */
    // END OF MODIFICATION
    private List<TransportOffer> trucks = new ArrayList<TransportOffer>();
    private List<TransportOffer> trailers = new ArrayList<TransportOffer>();

    public HolonCreationAuction(int commissions, int type, int param) {
        organizationType = type;
        organizationTypeParam = param;
        commissionsSent = commissions;
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
        Collections.sort(drivers);
        Collections.sort(trailers);
        Collections.sort(trucks);
        TransportOffer[] result = null;
        switch (organizationType) {
            case FULL: {
                result = full();
                return result;
            }
            case REPRESENTATIVES: {
                result = representatives();
                return result;
            }
            case BEST_REPRESENTATIVES: {
                result = bestRep();
                return result;
            }
            // MODIFICATION BY LP
        /*
         * case K_REPRESENTATIVES: { result=K_representatives(); return result;
         * }
         */
            case GENETIC: {
                result = genetic();
                return result;
            }
            // end of modification
            case NEGOTIATION: {
                result = negotiation();
                return result;
            }
            default:
                return null;
        }
    }

    private TransportOffer[] negotiation() {

        TransportOffer[] team = new TransportOffer[3];
        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        }

        Random rand = new Random();
        int i = 0;
        List<TransportOffer> negotiationTrailers = new LinkedList<TransportOffer>();
        for (TransportOffer offer : trailers)
            negotiationTrailers.add(offer);

        int el = 0;
        int maxMessageCount = organizationTypeParam;
        if (organizationTypeParam > trucks.size())
            maxMessageCount = trucks.size();
        TransportOffer trailer = null;
        double cost = 0;
        double dist = (commission.getPickupX() - commission.getDeliveryX())
                * (commission.getPickupX() - commission.getDeliveryX())
                + (commission.getPickupY() - commission.getDeliveryY())
                * (commission.getPickupY() - commission.getDeliveryY());
        double minimalCost = Double.MAX_VALUE;
        TransportElementInitialDataTruck truckData;
        TransportElementInitialDataTrailer trailerData;
        List<NegotiationResult> results = new LinkedList<NegotiationResult>();

        for (TransportOffer truck : trucks) {
            truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();
            if (truck.getTransportElementData() == null)
                continue;
            minimalCost = Double.MAX_VALUE;

            if (negotiationTrailers.size() == 0)
                break;
            for (i = 0; i < maxMessageCount; i++) {
                el = rand.nextInt(negotiationTrailers.size());
                trailer = negotiationTrailers.get(el);
                trailerData = (TransportElementInitialDataTrailer) trailer
                        .getTransportElementData();
                if (trailerData == null)
                    continue;

                if (truckData.getConnectorType() != trailerData
                        .getConnectorType())
                    continue;

                if (truckData.getPower() < trailerData.getMass()
                        + trailer.getMaxLoad()) // commission.getLoad())
                    continue;

                cost = 0.01
                        * dist
                        * (4 - truckData.getComfort())
                        + (dist / 100)
                        * truckData.getFuelConsumption()
                        * (((double) (trailerData.getMass() + commission
                        .getLoad())) / truckData.getPower());

                if (cost < minimalCost) {
                    minimalCost = cost;
                }
            }

            negotiationTrailers.remove(negotiationTrailers.get(el));
            // dodanie wyniku negocjacji
            results.add(new NegotiationResult(truck, trailer, minimalCost));

        }

        Collections.sort(results);

        NegotiationResult bestResult = results.get(0);
        team[0] = drivers.get(0);
        team[1] = bestResult.truck;
        team[2] = bestResult.trailer;

        if (team[1].getTransportElementData() == null)
            return null;
        if (team[2].getTransportElementData() == null)
            return null;

        if (team[1] == null) {
            return null;
        }
        return team;
    }

    private TransportOffer[] full() {

        TransportOffer[] team = new TransportOffer[3];
        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        }

        double minimalCost = Double.MAX_VALUE;

        for (TransportOffer truck : trucks) {
            for (TransportOffer trailer : trailers) {
                if (truck.getTransportElementData() == null
                        || trailer.getTransportElementData() == null)
                    continue;

                TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck
                        .getTransportElementData();
                TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer
                        .getTransportElementData();

                if (truckData.getConnectorType() != trailerData
                        .getConnectorType())
                    continue;

                if (truckData.getPower() < trailerData.getMass()
                        + trailer.getMaxLoad()) // commission.getLoad())
                    continue;

                double dist = (commission.getPickupX() - commission
                        .getDeliveryX())
                        * (commission.getPickupX() - commission.getDeliveryX())
                        + (commission.getPickupY() - commission.getDeliveryY())
                        * (commission.getPickupY() - commission.getDeliveryY());
                double newCost = 0.01
                        * dist
                        * (4 - truckData.getComfort())
                        + (dist / 100)
                        * truckData.getFuelConsumption()
                        * (((double) (trailerData.getMass() + commission
                        .getLoad())) / truckData.getPower());

                if (newCost < minimalCost) {
                    team[1] = truck;
                    team[2] = trailer;
                    minimalCost = newCost;
                }
            }
        }
        team[0] = drivers.get(0);

        if (team[1] == null) {
            return null;
        }

        return team;
    }

    private TransportOffer[] bestRep() {
        TransportOffer[] team = new TransportOffer[3];
        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        }

        TransportOffer trailer = trailers.get(0);
        TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer
                .getTransportElementData();

        int i = 1;
        while (trailerData == null && i < trailers.size()) {
            trailer = trailers.get(i);
            i++;
            trailerData = (TransportElementInitialDataTrailer) trailer
                    .getTransportElementData();
        }
        if (trailerData == null && i == trailers.size())
            return null;

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

        trailerData = (TransportElementInitialDataTrailer) trailer
                .getTransportElementData();
        if (trailerData == null)
            return null;

        logger.info("Trailer Size = " + trailer.getRatio());
        if (trailer.getRatio() < 0) {
            return null;
        }
        TransportOffer truck = trucks.get(0);
        TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck
                .getTransportElementData();
        i = 1;
        while (truckData == null && i < trucks.size()) {
            truck = trucks.get(i);
            i++;
            truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();
        }
        if (truckData == null && i == trucks.size())
            return null;

        for (TransportOffer item : trucks) {

            truckData = (TransportElementInitialDataTruck) item
                    .getTransportElementData();
            if (truckData == null)
                continue;
            if (item.getRatio() > 0) {
                if (truck.getRatio() > 0) {
                    if (truck.getRatio() > item.getRatio()
                            && truck.getMaxLoad() >= trailer.getMaxLoad()) {
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

        // System.out.println("New team: " + team[0].getAid().getLocalName() +
        // " " + team[1].getAid().getLocalName() + " " +
        // team[2].getAid().getLocalName());

        return team;
    }

    private TransportOffer[] representatives() {

        TransportOffer[] team = new TransportOffer[3];
        if (drivers.size() <= 0 || trailers.size() <= 0 || trucks.size() <= 0) {
            return null;
        }

        HashMap<Integer, LinkedList<OfferWithCost>> truckOffers = new HashMap<Integer, LinkedList<OfferWithCost>>();
        HashMap<Integer, LinkedList<OfferWithCost>> trailerOffers = new HashMap<Integer, LinkedList<OfferWithCost>>();
        double cost = 0;
        double dist = (commission.getPickupX() - commission.getDeliveryX())
                * (commission.getPickupX() - commission.getDeliveryX())
                + (commission.getPickupY() - commission.getDeliveryY())
                * (commission.getPickupY() - commission.getDeliveryY());

        TreeSet<Integer> truckConnectorTypes = new TreeSet<Integer>();
        for (TransportOffer truck : trucks) {
            if (truck.getTransportElementData() == null)
                continue;

            TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();

            truckConnectorTypes.add(truckData.getConnectorType());
        }

        TreeSet<Integer> trailersConnectorTypes = new TreeSet<Integer>();
        for (TransportOffer trailer : trailers) {
            if (trailer.getTransportElementData() == null)
                continue;

            TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer
                    .getTransportElementData();

            trailersConnectorTypes.add(trailerData.getConnectorType());
        }

        TreeSet<Integer> bothConnectorTypes = new TreeSet<Integer>();
        for (Integer el : truckConnectorTypes) {
            if (trailersConnectorTypes.contains(el))
                bothConnectorTypes.add(el);
        }

        for (int connector : bothConnectorTypes) {
            truckOffers.put(connector, new LinkedList<OfferWithCost>());
            trailerOffers.put(connector, new LinkedList<OfferWithCost>());
        }

        for (TransportOffer truck : trucks) {
            if (truck.getTransportElementData() == null)
                continue;

            TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truck
                    .getTransportElementData();

            cost = 0.01 * dist * (4 - truckData.getComfort()) + (dist / 100)
                    * truckData.getFuelConsumption()
                    * (commission.getLoad() / truckData.getPower());

            if (bothConnectorTypes.contains(truckData.getConnectorType())) {
                truckOffers.get(truckData.getConnectorType()).add(
                        new OfferWithCost(truck, cost));
            }
        }

        for (TransportOffer trailer : trailers) {
            if (trailer.getTransportElementData() == null)
                continue;

            TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailer
                    .getTransportElementData();

            if (bothConnectorTypes.contains(trailerData.getConnectorType())) {
                trailerOffers.get(trailerData.getConnectorType()).add(
                        new OfferWithCost(trailer, trailerData.getMass()));
            }
        }

        for (int connector : bothConnectorTypes) {
            Collections.sort(truckOffers.get(connector));
            Collections.sort(trailerOffers.get(connector));
        }

        double minimalCost = Double.MAX_VALUE;

        for (int connector : bothConnectorTypes) {
            int truckLimit = truckOffers.get(connector).size()
                    * organizationTypeParam / 100;
            if (truckLimit == 0)
                truckLimit = 1;
            for (int i = 0; i < truckLimit; i++) {
                int trailerLimit = trailerOffers.get(connector).size()
                        * organizationTypeParam / 100;
                if (trailerLimit == 0)
                    trailerLimit = 1;
                for (int j = 0; j < trailerLimit; j++) {
                    TransportElementInitialDataTruck truckData = (TransportElementInitialDataTruck) truckOffers
                            .get(connector).get(i).getOffer()
                            .getTransportElementData();
                    TransportElementInitialDataTrailer trailerData = (TransportElementInitialDataTrailer) trailerOffers
                            .get(connector).get(j).getOffer()
                            .getTransportElementData();

                    if (truckData.getPower() < trailerData.getMass()
                            + trailerOffers.get(connector).get(j).getOffer()
                            .getMaxLoad()) // commission.getLoad())
                        continue;

                    if (truckOffers.get(connector).get(i).getCost()
                            + trailerOffers.get(connector).get(j).getCost() < minimalCost) {
                        minimalCost = truckOffers.get(connector).get(i)
                                .getCost()
                                + trailerOffers.get(connector).get(j).getCost();
                        team[1] = truckOffers.get(connector).get(i).getOffer();
                        team[2] = trailerOffers.get(connector).get(j)
                                .getOffer();
                    }
                }
            }
        }

        team[0] = drivers.get(0);

        if (team[1] == null) {
            return null;
        }

        // System.out.println("New team: " + team[0].getAid().getLocalName() +
        // " " + team[1].getAid().getLocalName() + " " +
        // team[2].getAid().getLocalName());

        return team;
    }

    // MODIFICATIONS BY LP
    private void sort(Double[] arrayCost) {
        if (arrayCost.length >= 2) {
            int m = arrayCost.length / 2;
            Double[] t1 = new Double[m];
            Double[] t2 = new Double[arrayCost.length - m];

            for (int i = 0; i < arrayCost.length; i++) {
                if (i < m) {
                    t1[i] = arrayCost[i];
                } else {
                    t2[i - m] = arrayCost[i];
                }
            }

            sort(t1);
            sort(t2);

            int j = 0, k = 0;

            for (int i = 0; i < arrayCost.length; i++) {
                // System.out.println("arrayCost.length = "+arrayCost.length+"\nt1.length = "+t1.length+"\nt2.length = "+t2.length);
                if (j < t1.length && k < t2.length) {
                    /*
                     * System.out.println("t1[0] = "+t1[0]+"\nt2[0] = "+t2[0]);
                     * System.out.println("t1[j] = "+t1[j]+"\nt2[k] = "+t2[k]);
                     */
                    // System.out.println("t1[j].doubleValue() = "+t1[j].doubleValue()+"\nt2[k].doubleValue() = "+t2[k].doubleValue());
                    if (t1[j].doubleValue() < t2[k].doubleValue()) {
                        arrayCost[i] = t1[j++];
                    } else {
                        arrayCost[i] = t2[k++];
                    }
                } else if (j >= t1.length) {
                    arrayCost[i] = t2[k++];
                } else {
                    arrayCost[i] = t1[j++];
                }
            }
        }
    }

    private void calculateCost(List<TransportOffer[]> ArrayHolon,
                               Double[] arrayCost) {
        double sum = 0;
        for (int i = 0; i < arrayCost.length; i++) {
            arrayCost[i] = (0.0);
        }

        TransportElementInitialDataTruck truckData;
        TransportElementInitialDataTrailer trailerData;

        int j = 0;
        for (TransportOffer[] team : ArrayHolon) {
            truckData = (TransportElementInitialDataTruck) team[1]
                    .getTransportElementData();
            trailerData = (TransportElementInitialDataTrailer) team[2]
                    .getTransportElementData();

            double dist = (commission.getPickupX() - commission.getDeliveryX())
                    * (commission.getPickupX() - commission.getDeliveryX())
                    + (commission.getPickupY() - commission.getDeliveryY())
                    * (commission.getPickupY() - commission.getDeliveryY());
            double Cost = 0.01
                    * dist
                    * (4 - truckData.getComfort())
                    + (dist / 100)
                    * truckData.getFuelConsumption()
                    * (((double) (trailerData.getMass() + commission.getLoad())) / truckData
                    .getPower());
            sum = sum + Cost;
            arrayCost[j] = Cost;
            j++;
        }

        for (int i = 0; i < ArrayHolon.size(); i++) {
            arrayCost[i] = arrayCost[i] / sum;
        }
        sort(arrayCost);
    }

    private void initialiseArrayDouble(List<TransportOffer[]> ArrayHolon,
                                       List<Double> ArrayDouble) {
        ArrayDouble.clear();
        TransportElementInitialDataTruck truckData;
        TransportElementInitialDataTrailer trailerData;

        for (TransportOffer[] team : ArrayHolon) {
            truckData = (TransportElementInitialDataTruck) team[1]
                    .getTransportElementData();
            trailerData = (TransportElementInitialDataTrailer) team[2]
                    .getTransportElementData();

            double dist = (commission.getPickupX() - commission.getDeliveryX())
                    * (commission.getPickupX() - commission.getDeliveryX())
                    + (commission.getPickupY() - commission.getDeliveryY())
                    * (commission.getPickupY() - commission.getDeliveryY());
            double Cost = 0.01
                    * dist
                    * (4 - truckData.getComfort())
                    + (dist / 100)
                    * truckData.getFuelConsumption()
                    * (((double) (trailerData.getMass() + commission.getLoad())) / truckData
                    .getPower());
            ArrayDouble.add(Cost);
        }
    }

    private void initialiseCostNumber(List<Double> CostNumber,
                                      List<Double> ArrayDouble) {
        CostNumber.clear();
        for (int i = 0; i < ArrayDouble.size(); i++) {
            CostNumber.add(ArrayDouble.get(i));
        }
    }

    private void initialiseCostNumber(List<Double> CostNumber,
                                      Double[] arrayDouble) {
        CostNumber.clear();
        for (int i = 0; i < arrayDouble.length; i++) {
            CostNumber.add(arrayDouble[i]);
        }
    }

    private void normalized(double sum, List<Double> ArrayDouble,
                            List<Double> TransitArray) {
        if (sum != 0) {
            for (Double it : ArrayDouble) {
                // System.out.println("Before : "+it);
                // it=it/sum;
                TransitArray.add(it / sum);
                // System.out.println("After : "+it+"\n");
            }
            ArrayDouble.clear();
            for (Double it : TransitArray) {
                ArrayDouble.add(it);
            }
            TransitArray.clear();
        } else {
            System.out
                    .println("The parameter sum is equal to 0. Impossible division.");
        }
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

        /* INITIALISATION STEP */
        List<TransportOffer[]> ArrayHolon = new ArrayList<TransportOffer[]>();
        List<Double> ArrayDouble = new ArrayList<Double>();
        // HashMap <Double, Integer> CostNumber = new HashMap<Double,Integer>();

        List<Double> CostNumber = new ArrayList<Double>();
        List<Double> TransitArray = new ArrayList<Double>();

        List<TransportOffer[]> ArrayHolonSave = new ArrayList<TransportOffer[]>();

        /* initialize the array */
        double sum = 0;
        if (drivers != null && trucks != null && trailers != null) {
            TransportOffer[] team = new TransportOffer[3];
            for (TransportOffer driver : drivers) {
                for (TransportOffer truck : trucks) {
                    for (TransportOffer trailer : trailers) {
                        team[0] = driver;
                        team[1] = truck;
                        team[2] = trailer;

                        truckData = (TransportElementInitialDataTruck) team[1]
                                .getTransportElementData();
                        trailerData = (TransportElementInitialDataTrailer) team[2]
                                .getTransportElementData();

                        double dist = (commission.getPickupX() - commission
                                .getDeliveryX())
                                * (commission.getPickupX() - commission
                                .getDeliveryX())
                                + (commission.getPickupY() - commission
                                .getDeliveryY())
                                * (commission.getPickupY() - commission
                                .getDeliveryY());
                        double Cost = 0.01
                                * dist
                                * (4 - truckData.getComfort())
                                + (dist / 100)
                                * truckData.getFuelConsumption()
                                * (((double) (trailerData.getMass() + commission
                                .getLoad())) / truckData.getPower());
                        sum = sum + Cost;
                        /*
                         * ADD HERE COMPATIBILITY OPTIONS WITH TRUCKS AND
                         * TRAILERS
                         */
                        ArrayHolon.add(team);
                        ArrayDouble.add(Cost);
                    }
                }
            }
        }
        initialiseCostNumber(CostNumber, ArrayDouble);
        normalized(sum, CostNumber, TransitArray);

        /*
         * int z=0; for(Double it : CostNumber) {
         * System.out.println("number "+z+" : "+it); z++; } try {
         * Thread.sleep(500000); } catch (Exception e) {}
         */

        while (ArrayHolon.size() >= 2) {
            /* SELECTION STEP */
            /* sort the normalized costs */
            Double[] arrayCost = new Double[CostNumber.size()];
            for (int i = 0; i < CostNumber.size(); i++) {
                arrayCost[i] = CostNumber.get(i);
            }
            sort(arrayCost);

            int size = ArrayHolon.size();
            double R = java.lang.Math.random();

            while (ArrayHolonSave.size() < (size * 80 / 100)) {
                /*
                 * calculate the accumulated normalized values and select the
                 * good values
                 */
                double accumulatedNormalizedValue = 0;
                for (int i = 0; i < CostNumber.size(); i++) {
                    accumulatedNormalizedValue = accumulatedNormalizedValue
                            + arrayCost[i];
                    if (accumulatedNormalizedValue > R) {
                        if (arrayCost[i] == null) {
                            System.out
                                    .println("arrayCost[i]==null !!!!!!!!!!!!!!");
                        }
                        if (CostNumber.contains(arrayCost[i])) {
                            int temp = CostNumber.indexOf(arrayCost[i]);
                            ArrayHolonSave.add(ArrayHolon.get(temp));
                            ArrayHolon.remove(temp);
                            CostNumber.remove(temp);
                            break;
                        } else {
                            System.out.println("CostNumber doesn't contain "
                                    + arrayCost[i] + "\n");
                            break;
                        }
                    }
                }
                calculateCost(ArrayHolon, arrayCost);
                initialiseCostNumber(CostNumber, arrayCost);
            }

            /* fill the taboo list with the not used holons */
            for (TransportOffer[] it : ArrayHolon) {
                tabooList.add(it);
            }

            ArrayHolon.clear();
            ArrayDouble.clear();
            for (TransportOffer[] it : ArrayHolonSave) {
                ArrayHolon.add(it);
            }
            initialiseArrayDouble(ArrayHolon, ArrayDouble);
            initialiseCostNumber(CostNumber, ArrayDouble);
            ArrayHolonSave.clear();

            /* REPRODUCTION STEP */
            for (int j = 0; j < ArrayHolon.size(); j++) {
                /* mutations */
                mutationTruck(ArrayHolon.get(j), trucks, tabooList);
                mutationTrailer(ArrayHolon.get(j), trailers, tabooList);
                /* crossover */
                for (int k = j + 1; k < ArrayHolon.size(); k++) {
                    crossOver(ArrayHolon.get(j), ArrayHolon.get(k), tabooList);
                }
            }
        }
        if ((ArrayHolon.get(0))[0] == null || (ArrayHolon.get(0))[1] == null
                || (ArrayHolon.get(0))[2] == null) {
            System.out.println("Problem Creation\n");
            return null;
        } else {
            return ArrayHolon.get(0);
        }

    }

    /*
     * @param : takes 1 team and a list of trucks as parameters Perform mutation
     * of the truck.
     */
    private void mutationTruck(TransportOffer[] team,
                               List<TransportOffer> trucks, List<TransportOffer[]> tabooList) {
        double mutation = java.lang.Math.random();
        /* rate of mutation defined bellow 0.1% */
        if (mutation < 0.001) {
            /* select a random number in the size of LinkedList */
            int i = 0;
            TransportOffer[] temp = new TransportOffer[3];
            do {
                do {
                    i = (int) java.lang.Math.abs(java.lang.Math.random()
                            * trucks.size());
                } while (i > trucks.size());
                temp[0] = team[0];
                temp[1] = trucks.get(i);
                temp[2] = team[2];
            } while (tabooList.contains(temp));
            team[1] = trucks.get(i);

        }

    }

    /*
     * @param : takes 1 team and a list of trailers as parameters Perform
     * mutation of the trailer.
     */
    private void mutationTrailer(TransportOffer[] team,
                                 List<TransportOffer> trailers, List<TransportOffer[]> tabooList) {
        double mutation = java.lang.Math.random();
        /* rate of mutation defined bellow 0.1% */
        if (mutation <= 0.001) {
            /* select a random number in the size of LinkedList */
            int i = 0;
            TransportOffer[] temp = new TransportOffer[3];
            do {
                do {
                    i = (int) java.lang.Math.abs(java.lang.Math.random()
                            * trailers.size());
                } while (i > trailers.size());
                temp[0] = team[0];
                temp[1] = team[1];
                temp[2] = trailers.get(i);
            } while (tabooList.contains(temp));
            team[2] = trailers.get(i);
        }

    }

    /*
     * @param : takes 2 teams in parameter Perform a crossover between 2 teams.
     */
    private void crossOver(TransportOffer[] team1, TransportOffer[] team2,
                           List<TransportOffer[]> tabooList) {
        double crossover = java.lang.Math.random();
        /* rate of mutation is defined bellow 70% */
        if (crossover <= 0.7) {
            double whichKindOfCrossOver = java.lang.Math.random();
            /* if the condition is verified do a one-point-crossover */
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

    public Map<Integer, TransportOffer[]> getBestTeams() {
        Set<Integer> driverDepotSet = driversMap.keySet();
        Set<Integer> truckDepotSet = driversMap.keySet();
        Set<Integer> trailerDepotSet = driversMap.keySet();

        driverDepotSet.retainAll(truckDepotSet);
        driverDepotSet.retainAll(trailerDepotSet);

        for (Integer depot : driverDepotSet) {
            logger.info(depot + " &&&&&&&&&&&&&&&&&&&");
        }

        Map<Integer, TransportOffer[]> result = new HashMap<Integer, TransportOffer[]>();

        for (Integer depot : driverDepotSet) {
            List<TransportOffer> driverList = driversMap.get(depot);
            List<TransportOffer> truckList = trucksMap.get(depot);
            List<TransportOffer> trailerList = trailersMap.get(depot);
            drivers = driverList;
            trucks = truckList;
            trailers = trailerList;
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
                    + depot);
            TransportOffer[] bestTeam = getBestTeam();
            // TODO
            // System.out.println(depot+" "+bestTeam[0].getAid()+" "+bestTeam[1].getAid()+" "+bestTeam[2].getAid());
            result.put(depot, bestTeam);
        }
        return result;

    }

    // END OF MODIFICATIONS

    public synchronized void addOfffer(TransportOffer offer) {
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
    }

    public synchronized boolean addOffer(TransportOffer offer) {
        offersReceived++;
        if (offer.getRatio() != -1) {
            switch (offer.getOfferType()) {
                case DRIVER:
                    driverNum++;
                    List<TransportOffer> list = driversMap.get(new Integer(offer
                            .getDepot()));
                    if (list == null) {
                        list = new ArrayList<TransportOffer>();
                        driversMap.put(new Integer(offer.getDepot()), list);
                    }
                    list.add(offer);
                    break;
                case TRUCK:
                    truckNum++;
                    List<TransportOffer> list2 = trucksMap.get(new Integer(offer
                            .getDepot()));
                    if (list2 == null) {
                        list2 = new ArrayList<TransportOffer>();
                        trucksMap.put(new Integer(offer.getDepot()), list2);
                    }
                    list2.add(offer);
                    break;
                case TRAILER:
                    trailerNum++;
                    List<TransportOffer> list3 = trailersMap.get(new Integer(offer
                            .getDepot()));
                    if (list3 == null) {
                        list3 = new ArrayList<TransportOffer>();
                        trailersMap.put(new Integer(offer.getDepot()), list3);
                    }
                    list3.add(offer);
                    break;
                default:
                    logger.info("SOMETHING WENT WRONG.......");
                    break;
            }
        }
        if (offersReceived < commissionsSent) {
            return false;
        } else {
            logger.info("THE NUMBERS: " + driverNum + " " + truckNum + " "
                    + trailerNum);
            logger.info("TRAILER KEYSET: " + trailersMap.keySet());
            return true;
        }
    }

    public boolean isFinished() {
        return commissionsSent == offersReceived;

    }

    private class NegotiationResult implements Comparable<NegotiationResult> {
        TransportOffer truck;
        TransportOffer trailer;
        double cost;

        public NegotiationResult(TransportOffer truck, TransportOffer trailer,
                                 double cost) {
            this.truck = truck;
            this.trailer = trailer;
            this.cost = cost;
        }

        public int compareTo(NegotiationResult res) {
            if (cost == res.cost)
                return 0;
            if (cost > res.cost)
                return 1;
            return -1;
        }
    }

}
