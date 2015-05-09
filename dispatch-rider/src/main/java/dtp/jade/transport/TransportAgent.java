package dtp.jade.transport;

import dtp.commission.Commission;
import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.transport.behaviour.*;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Klasa bazowa dla elementow transportowych.
 *
 * @author Michal Golacki
 */
public abstract class TransportAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(TransportAgent.class);
    private final Lock lock = new ReentrantLock();
    protected Commission commission;
    protected Commission[] commissions;
    protected TransportAgentData[] holonParts;
    protected List<HolonPartsCost> holonPartsCostList;
    protected List<TransportAgentData> trucks;
    protected List<TransportAgentData> trailers;
    protected List<TransportAgentData> drivers;
    protected Map<TransportType, List<TransportAgentData>> agents;
    protected TransportElementInitialData initialData;
    private Set<AID> askingUnits;
    private Set<AID> confirmedUnits;
    private Set<AID> waitingUnits;
    private boolean feedbackSended;
    /**
     * Is transport element already in transport team.
     */
    private boolean booked;

    /**
     * Wyznacza wartosc funkcji kosztu Skroty uzywane w funkcji kosztu TRUCK:
     * power reliability comfort fuel TRAILER: mass capacity universality
     * <p/>
     * DISTANCE dist
     * <p/>
     * COMMISSION load pickUpServiceTime deliveryServiceTime punishment
     */
    public static double costFunctionValue(String function, double dist,
                                           TransportElementInitialData driver,
                                           TransportElementInitialDataTruck truck,
                                           TransportElementInitialDataTrailer trailer, Commission comm,
                                           Double punishment) {
        String expr = function;
        expr = expr.replace("power", new Double(truck.getPower()).toString());
        expr = expr.replace("reliability",
                new Double(truck.getReliability()).toString());
        expr = expr.replace("comfort",
                new Double(truck.getComfort()).toString());
        expr = expr.replace("fuel",
                new Double(truck.getFuelConsumption()).toString());
        expr = expr.replace("mass", new Double(trailer.getMass()).toString());
        expr = expr.replace("capacity",
                new Double(trailer.getCapacity()).toString());
        expr = expr.replace("actualLoad",
                new Double(comm.getActualLoad()).toString());
        expr = expr.replace("universality",
                new Double(trailer.getUniversality()).toString());
        expr = expr.replace("dist", new Double(dist).toString());
        expr = expr.replace("load", new Double(comm.getLoad()).toString());
        expr = expr.replace("pickUpServiceTime",
                new Double(comm.getPickUpServiceTime()).toString());
        expr = expr.replace("deliveryServiceTime",
                new Double(comm.getDeliveryServiceTime()).toString());
        if (punishment != null)
            expr = expr.replace("punishment", punishment.toString());
        else
            expr = expr.replace("punishment", "0");
        return Calculator.calculate(expr);
    }

    /**
     * Metoda wywolywana w trybie z przesylaniem zlecen jedno po drugim Inicjuje
     * srodowisko dla pozniejszej negocjacji
     *
     * @param commission
     */
    public synchronized void setCommission(Commission commission) {
        this.commission = commission;
        trucks = agents.get(TransportType.TRUCK);
        trailers = agents.get(TransportType.TRAILER);
        drivers = agents.get(TransportType.DRIVER);

        askingUnits = new TreeSet<AID>();
        confirmedUnits = new TreeSet<AID>();
        waitingUnits = new TreeSet<AID>();
        askingUnits = Collections.synchronizedSet(askingUnits);
        confirmedUnits = Collections.synchronizedSet(confirmedUnits);
        waitingUnits = Collections.synchronizedSet(waitingUnits);
        feedbackSended = false;

        if (isHolonPart() == false)
            makeHolonPartsList();
        /*
         * if(getAID().getLocalName().contains("Truck #0")) for(HolonPartsCost
         * cost:holonPartsCostList) {
         * System.out.println(getAID()+" "+cost.getAgents
         * ()[0].getAid()+" "+cost.getAgents()[1].getAid()+" "+cost.getCost());
         * }
         */

        sendReadyToStartNegotiation();
    }

    /**
     * Metoda wywolywana w trybie z przesylaniem zlecen paczkami Inicjuje
     * srodowisko dla pozniejszej negocjacji
     *
     */
    public synchronized void setCommissions(Commission[] commissions) {
        this.commissions = commissions;
        trucks = agents.get(TransportType.TRUCK);
        trailers = agents.get(TransportType.TRAILER);
        drivers = agents.get(TransportType.DRIVER);

        askingUnits = new TreeSet<AID>();
        confirmedUnits = new TreeSet<AID>();
        waitingUnits = new TreeSet<AID>();
        askingUnits = Collections.synchronizedSet(askingUnits);
        confirmedUnits = Collections.synchronizedSet(confirmedUnits);
        waitingUnits = Collections.synchronizedSet(waitingUnits);
        feedbackSended = false;

        if (isHolonPart() == false)
            makeList();
        // makeHolonPartsList();
        /*
         * for(HolonPartsCost cost:holonPartsCostList) {
         * System.out.println(getAID
         * ()+" "+cost.getAgents()[0].getAid()+" "+cost.
         * getAgents()[1].getAid()+" "+cost.getCost()); }
         */

        sendReadyToStartNegotiation();
    }

    /**
     * sprawdza czy dany kandydat na holon moze obsluzy zlecenie
     *
     * @param com
     * @param part
     * @return
     */
    protected abstract boolean canCarryCommission(Commission com,
                                                  HolonPartsCost part);

    /**
     * Inicjuje liste preferencji w trybie wysylania zlecen paczkami.
     */
    protected abstract void makeHolonPartsListFromAllAgents();

    /**
     * Tworzy liste preferencji w trybie wysylania zlecen paczkami
     */
    private synchronized void makeList() {
        Commission com;
        commission = commissions[0];
        makeHolonPartsListFromAllAgents();
        for (int i = 0; i < commissions.length; i++) {
            com = commissions[i];
            for (HolonPartsCost part : holonPartsCostList) {
                if (canCarryCommission(com, part)) {
                    part.addCommission(com);
                    part.setCost(calculateCommissionsCost(
                            part.getCommissions(), part));
                } else {
                    tryChangeCommissions(part, com);
                }
            }
        }
        Collections.sort(holonPartsCostList);
    }

    /**
     * Robi podmiane zlecenia (z listy kandydatow na holon) z nowym zleceniem
     * jesli koszt wymiany jest lepszy
     *
     * @param part
     * @param com
     */
    private synchronized void tryChangeCommissions(HolonPartsCost part,
                                                   Commission com) {

        if (part.getCommissions().size() == 0)
            return;
        double oldCost = calculateCommissionsCost(part.getCommissions(), part);
        double newCost = part.getCost();
        List<Commission> newPartList = part.getCommissions();
        for (int i = 0; i < part.getCommissionsCount(); i++) {
            List<Commission> newList = new LinkedList<Commission>();
            for (int j = 0; j < i; j++)
                newList.add(part.getCommissions().get(j));
            newList.add(com);
            for (int j = i + 1; j < part.getCommissionsCount(); j++)
                newList.add(part.getCommissions().get(j));
            newCost = calculateCommissionsCost(newList, part);

            if (newCost < oldCost) {
                oldCost = newCost;
                newPartList = newList;
            }
        }
        part.setCommissions(newPartList);
        part.setCost(oldCost);

        Collections.sort(holonPartsCostList);
    }

    /**
     * Oblicza koszt dodania zlecenia
     *
     * @param commissionsList
     * @param part
     * @return
     */
    private synchronized double calculateCommissionsCost(
            List<Commission> commissionsList, HolonPartsCost part) {
        double dist = 0;
        int load = 0;
        Commission commission = commissionsList.get(0);
        load += commission.getLoad();
        dist = (commission.getPickupX() - commission.getDeliveryX())
                * (commission.getPickupX() - commission.getDeliveryX())
                + (commission.getPickupY() - commission.getDeliveryY())
                * (commission.getPickupY() - commission.getDeliveryY());
        for (int i = 1; i < commissionsList.size(); i++) {
            dist += (commissionsList.get(i).getPickupX() - commissionsList.get(
                    i).getDeliveryX())
                    * (commissionsList.get(i).getPickupX() - commissionsList
                    .get(i).getDeliveryX())
                    + (commissionsList.get(i).getPickupY() - commissionsList
                    .get(i).getDeliveryY())
                    * (commissionsList.get(i).getPickupY() - commissionsList
                    .get(i).getDeliveryY());
            dist += (commissionsList.get(i).getPickupX() - commission
                    .getDeliveryX())
                    * (commissionsList.get(i).getPickupX() - commission
                    .getDeliveryX())
                    + (commissionsList.get(i).getPickupY() - commission
                    .getDeliveryY())
                    * (commissionsList.get(i).getPickupY() - commission
                    .getDeliveryY());
            commission = commissionsList.get(i);
            load += commission.getLoad();
        }
        Commission tmp = new Commission();
        tmp.setLoad(load);
        return getCostFunctionValue(part, dist, tmp);
    }

    /**
     * Zwraca wartosc funkcji kosztu
     *
     * @param part
     * @param dist
     * @param com
     * @return
     */
    protected abstract double getCostFunctionValue(HolonPartsCost part,
                                                   double dist, Commission com);

    /**
     * Oblicza calkowite zaladowanie kandydata na holon
     *
     * @param com
     * @param addedCommissions
     * @return
     */
    protected synchronized int calculateLoad(Commission com,
                                             List<Commission> addedCommissions) {
        int result = 0;
        for (Commission c : addedCommissions)
            result += c.getLoad();
        result += com.getLoad();
        return result;
    }

    /**
     * Potwierdzenie o gotowosci do negocjacji
     */
    private synchronized void sendReadyToStartNegotiation() {
        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {
            send(aids[0], "", CommunicationHelper.TRANSPORT_AGENT_PREPARED_TO_NEGOTIATION);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with CommissionService in the system");
        }
    }

    /**
     * Tworzy liste preferencji w trybie przesylania zlecenia po zleceniu
     */
    protected abstract void makeHolonPartsList();

    /**
     * Sprawdza czy AID odpowiada ciezarowce
     *
     * @param aid
     * @return
     */
    private boolean isTruck(AID aid) {
        return aid.getName().contains("Truck");
    }

    /**
     * Sprawdza czy AID odpowiada przyczepie
     *
     * @param aid
     * @return
     */
    private boolean isTrailer(AID aid) {
        return aid.getName().contains("Trailer");
    }

    /**
     * Sprawdza czy AID odpowiada kierowcy
     *
     * @param aid
     * @return
     */
    private boolean isDriver(AID aid) {
        return aid.getName().contains("Driver");
    }

    /**
     * Sprawdza czy agent jest czescia holonu
     */
    private synchronized boolean isHolonPart() {
        if (holonParts == null)
            return false;
        if (holonParts.length > 0)
            return true;
        return false;
    }

    /**
     * Wysyla oferte feedback do Dystrybutora
     */
    private synchronized void sendFeedback() {
        AID truck = null;
        AID trailer = null;
        AID driver = null;
        TransportElementInitialDataTrailer trailerData = null;
        TransportElementInitialDataTruck truckData = null;
        TransportElementInitialData driverData = null;
        if (isTruck(getAID())) {
            truck = getAID();
            truckData = (TransportElementInitialDataTruck) initialData;
        }
        for (AID aid : confirmedUnits) {
            if (isTruck(aid)) {
                truck = aid;
                truckData = (TransportElementInitialDataTruck) getTruck(aid)
                        .getData();
                break;
            }
        }
        if (isTrailer(getAID())) {
            trailer = getAID();
            trailerData = (TransportElementInitialDataTrailer) initialData;
        }
        for (AID aid : confirmedUnits) {
            if (isTrailer(aid)) {
                trailer = aid;
                trailerData = (TransportElementInitialDataTrailer) getTrailer(
                        aid).getData();
                break;
            }
        }
        if (isDriver(getAID())) {
            driver = getAID();
            driverData = initialData;
        }
        for (AID aid : confirmedUnits) {
            if (isDriver(aid)) {
                driver = aid;
                driverData = getDriver(aid).getData();
                break;
            }
        }

        sendFeedbackToDistributor(new NewHolonOffer(truck, trailer, driver,
                trailerData, truckData, driverData));
    }

    /**
     * rozpoczecie negocjacji
     */
    public synchronized void startNegotiation() {

        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.print("startNeg "); for(AID a:confirmedUnits)
         * System.out.print(a+" "); System.out.println();
         * System.out.println("startNegotiation "+holonPartsCostList.size()); }
         */
        lock.lock();
        if (isHolonPart()) {
            sendFeedbackToDistributor(new NewHolonOffer());
            lock.unlock();
            return;
        }
        if (holonPartsCostList.size() == 0) {
            sendFeedbackToDistributor(new NewHolonOffer());
            lock.unlock();
            return;
        }

        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            lock.unlock();
            return;
        }

        HolonPartsCost part = holonPartsCostList.get(0);

        if (waitingUnits.size() > 0) {
            for (Object aid : waitingUnits.toArray()) {
                for (TransportAgentData agent : part.getAgents()) {
                    if (agent.getAid().equals(aid)) {
                        respondToTeamOffer((AID) aid, "yes");
                    }
                }
            }
        }
        if (confirmedUnits.size() < TransportType.values().length - 1) {
            for (TransportAgentData agent : part.getAgents()) {
                if (!confirmedUnits.contains(agent.getAid()))
                    askForConnection(agent.getAid());
            }
        }
        lock.unlock();
    }

    /**
     * Zapytanie o polaczenie
     *
     * @param aid
     */
    private synchronized void askForConnection(AID aid) {
        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.println("ask "+aid); }
         */
        if (askingUnits.contains(aid))
            return;
        if (confirmedUnits.contains(aid))
            return;
        askingUnits.add(aid);
        send(aid, "", CommunicationHelper.TEAM_OFFER);
        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());
    }

    /**
     * Wolana gdy przychodzi zapytanie o polaczenie
     *
     * @param aid
     */
    public synchronized void teamOfferArrived(AID aid) {
        lock.lock();

        if (confirmedUnits == null) {
            sendResponse(aid, "no");
            lock.unlock();
            return;
        }

        if (isHolonPart()) {
            sendResponse(aid, "no");
            lock.unlock();
            return;
        }

        if (confirmedUnits.contains(aid)) {
            respondToTeamOffer(aid, "yes");
            lock.unlock();
            return;
        }
        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendResponse(aid, "no");
            lock.unlock();
            return;
        }
        if (askingUnits.contains(aid)) {
            respondToTeamOffer(aid, "yes");
            lock.unlock();
            return;
        }

        if (holonPartsCostList.size() == 0) {
            sendResponse(aid, "no");
            lock.unlock();
            return;
        }
        HolonPartsCost part = holonPartsCostList.get(0);
        for (TransportAgentData agent : part.getAgents()) {
            if (agent.getAid().equals(aid)) {
                respondToTeamOffer(aid, "yes");
                lock.unlock();
                return;
            }
        }
        if (askingUnits.size() > 0) {
            sendResponse(aid, "none");
            lock.unlock();
            return;
        }
        respondToTeamOffer(aid, "no");
        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            lock.unlock();
            return;
        }
        if (holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());

        lock.unlock();
    }

    /**
     * Wysyla odpowiedz na zapytanie o polaczenie
     *
     * @param aid
     * @param response
     */
    private synchronized void respondToTeamOffer(AID aid, String response) {
        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.println("responseToTeamOffer "+aid+" "+response); }
         */
        if (response.equals("yes")) {
            confirmedUnits.add(aid);
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<HolonPartsCost>();
            for (HolonPartsCost part : holonPartsCostList) {
                for (TransportAgentData agent : part.getAgents()) {
                    if (agent.getAid().equals(aid)) {
                        newHolonPartsCostList.add(part);
                        break;
                    }
                }
            }
            holonPartsCostList = newHolonPartsCostList;
            sendResponse(aid, "yes");
        } else {
            // AID toRemove = getPartToRemove(aid);
            // if (getAID().getName().startsWith("Truck #1")) {
            // System.out.println(Arrays.toString(waitingUnits.toArray()));
            // System.out.println(aid);
            // }
            waitingUnits.add(aid);
            // if (toRemove != null) {
            // waitingUnits.remove(toRemove);
            // sendResponse(toRemove, "no");
            // }
            // if (getAID().getName().startsWith("Truck #1")) {
            // System.out.println("toRemove " + toRemove);
            // System.out.println(Arrays.toString(waitingUnits.toArray()));
            // System.out.println();
            // }
        }

        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());

    }

    // private synchronized AID getPartToRemove(AID aid) {
    // AID theSamePart = null;
    // for (AID part : waitingUnits)
    // if (part.getName().split(" ")[0]
    // .equals(aid.getName().split(" ")[0])) {
    // theSamePart = part;
    // break;
    // }
    //
    // for (AID part : confirmedUnits)
    // if (part.getName().split(" ")[0]
    // .equals(aid.getName().split(" ")[0])) {
    // return aid;
    // }
    //
    // if (theSamePart == null)
    // return null;
    // for (HolonPartsCost parts : holonPartsCostList)
    // for (TransportAgentData agent : parts.getAgents()) {
    // if (agent.getAid().equals(theSamePart))
    // return aid;
    // if (agent.getAid().equals(aid))
    // return theSamePart;
    // }
    // return aid;
    // }

    /**
     * Wysyla odpowiedz na zapytanie o polaczenie
     *
     * @param aid
     * @param response
     */
    private synchronized void sendResponse(AID aid, String response) {
        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.println("sendResponse "+aid+" "+response); }
         */

        if (waitingUnits != null) {
            waitingUnits.remove(aid);
            if (response.equals("no")) {
                List<HolonPartsCost> newHolonPartsCostList = new LinkedList<HolonPartsCost>();
                boolean contain = false;
                for (HolonPartsCost part : holonPartsCostList) {
                    contain = false;
                    for (TransportAgentData agent : part.getAgents()) {
                        if (agent.getAid().equals(aid)) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain == false)
                        newHolonPartsCostList.add(part);
                }
                holonPartsCostList = newHolonPartsCostList;
                if (holonPartsCostList.size() == 0)
                    sendFeedbackToDistributor(new NewHolonOffer());
            }
        }
        send(aid, response, CommunicationHelper.TEAM_OFFER_RESPONSE);
        if (confirmedUnits != null
                && confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList == null || holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());
    }

    /**
     * Wolana gdy przychodzi odpoiwedz na zapytanie o polaczenie
     *
     * @param aid
     * @param response
     */
    public synchronized void response(AID aid, Boolean response) {
        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.println("response "+aid+" "+response); for(HolonPartsCost
         * part:holonPartsCostList) {
         * System.out.println(part.getAgents()[0].getAid
         * ()+" "+part.getAgents()[1].getAid()); } }
         */
        lock.lock();
        askingUnits.remove(aid);
        if (response == null) {
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<HolonPartsCost>();
            boolean present;
            for (HolonPartsCost part : holonPartsCostList) {
                present = false;
                for (TransportAgentData data : part.getAgents())
                    if (data.getAid().equals(aid)) {
                        present = true;
                        break;
                    }
                if (present == false)
                    newHolonPartsCostList.add(part);
            }
            for (HolonPartsCost part : holonPartsCostList) {
                present = false;
                for (TransportAgentData data : part.getAgents())
                    if (data.getAid().equals(aid)) {
                        present = true;
                        break;
                    }
                if (present)
                    newHolonPartsCostList.add(part);
            }
            holonPartsCostList = newHolonPartsCostList;
        } else if (response) {
            if (confirmedUnits.size() < TransportType.values().length)
                confirmedUnits.add(aid);
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<HolonPartsCost>();
            for (HolonPartsCost part : holonPartsCostList) {
                for (TransportAgentData agent : part.getAgents()) {
                    if (agent.getAid().equals(aid)) {
                        newHolonPartsCostList.add(part);
                        break;
                    }
                }
            }
            holonPartsCostList = newHolonPartsCostList;
        } else {
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<HolonPartsCost>();
            boolean contain = false;
            for (HolonPartsCost part : holonPartsCostList) {
                contain = false;
                for (TransportAgentData agent : part.getAgents()) {
                    if (agent.getAid().equals(aid)) {
                        contain = true;
                        break;
                    }
                }
                if (contain == false)
                    newHolonPartsCostList.add(part);
            }
            holonPartsCostList = newHolonPartsCostList;
        }
        /*
         * if(getAID().getName().startsWith("Trailer #2")) {
         * System.out.println("response po zmianach"); for(HolonPartsCost
         * part:holonPartsCostList) {
         * System.out.println(part.getAgents()[0].getAid
         * ()+" "+part.getAgents()[1].getAid()); } }
         */
        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            lock.unlock();
            return;
        }
        if (holonPartsCostList.size() > 0) {
            lock.unlock();
            startNegotiation();
            return;
        } else
            sendFeedbackToDistributor(new NewHolonOffer());
        lock.unlock();
    }

    /**
     * Wysyla oferte do Dystrybutora
     *
     * @param offer
     */
    public synchronized void sendFeedbackToDistributor(NewHolonOffer offer) {
        if (feedbackSended == false) {
            feedbackSended = true;
            if (waitingUnits != null)
                for (Object aid : waitingUnits.toArray()) {
                    if (!confirmedUnits.contains(aid))
                        sendResponse((AID) aid, "no");
                }
            if (confirmedUnits != null)
                for (AID aid : confirmedUnits)
                    sendResponse(aid, "yes");
            // System.out.println(getAID().getName());
            /*
             * if(getAID().getName().startsWith("Trailer #2")) {
             * System.out.println("feedback"); System.exit(0); }
             */

            AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                    "CommissionService");

            if (aids.length == 1) {
                send(aids[0], offer, CommunicationHelper.NEW_HOLON_OFFER);
            } else {
                logger.error(getLocalName()
                        + " - none or more than one agent with CommissionService in the system");
            }

        }
    }

    /**
     * Pobiera dane ciezarowki
     *
     * @param aid
     * @return
     */
    private synchronized TransportAgentData getTruck(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRUCK)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

    /**
     * Pobiera dane przyczepy
     *
     * @param aid
     * @return
     */
    private synchronized TransportAgentData getTrailer(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRAILER)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

    /**
     * Pobiera dane kierowcy
     *
     * @param aid
     * @return
     */
    private synchronized TransportAgentData getDriver(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.DRIVER)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

    /**
     * Potwierdzenie od Dystrybutora. Agent jest czescia holonu
     */
    public void confirmationFromDistributor() {
        holonParts = new TransportAgentData[3];
        int i = -1;
        for (AID aid : confirmedUnits) {
            i++;
            if (i > 2)
                break;
            if (getTruck(aid) != null) {
                holonParts[i] = getTruck(aid);
                continue;
            }
            if (getTrailer(aid) != null) {
                holonParts[i] = getTrailer(aid);
                continue;
            }
            if (getDriver(aid) != null) {
                holonParts[i] = getDriver(aid);
                continue;
            }
        }

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "CommissionService");
        send(aids[0], "", CommunicationHelper.HOLON_FEEDBACK);
    }

    /**
     * @param initialData
     */
    public void setTransportElementInitialData(
            TransportElementInitialData initialData) {
        this.initialData = initialData;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return initialData.getCapacity();
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.initialData.setCapacity(capacity);
    }

    /**
     * Zwraca typ danego agenta
     *
     * @return
     */
    protected abstract TransportType getType();

    /**
     * Odfiltrowuje z danych o agentach, dane agenta, ktory ja wywoluje
     *
     * @param map
     * @return
     */
    protected Map<TransportType, List<TransportAgentData>> filtr(
            Map<TransportType, List<TransportAgentData>> map) {
        Map<TransportType, List<TransportAgentData>> result = new HashMap<TransportType, List<TransportAgentData>>();
        for (TransportType type : map.keySet()) {
            if (!type.equals(getType()))
                result.put(type, map.get(type));
            else {
                List<TransportAgentData> data = map.get(type);
                List<TransportAgentData> newData = new LinkedList<TransportAgentData>();
                for (TransportAgentData agentData : data) {
                    if (!agentData.getAid().equals(getAID()))
                        newData.add(agentData);
                }
                result.put(getType(), newData);
            }
        }
        return result;
    }

    /**
     * @param agents
     */
    public void setAgentsData(
            Map<TransportType, List<TransportAgentData>> agents) {
        this.agents = filtr(agents);

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "GUIService");

        if (aids.length == 1) {
            send(aids[0], "", CommunicationHelper.TRANSPORT_AGENT_CONFIRMATION);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with GUIService in the system");
        }

    }

    /**
     * @param type
     * @return
     */
    public List<TransportAgentData> getData(TransportType type) {
        return agents.get(type);
    }

    /**
     * @return the defaultCapacity
     */
    public int getDefaultCapacity() {
        return this.initialData.getDefaultCapacity();
    }

    /**
     * @param defaultCapacity the defaultCapacity to set
     */
    public void setDefaultCapacity(int defaultCapacity) {
        this.initialData.setDefaultCapacity(defaultCapacity);
    }

    /**
     * @return the booked
     */
    public synchronized boolean isBooked() {
        return booked;
    }

    /**
     * @param booked the booked to set
     */
    public synchronized void setBooked(boolean booked) {
        this.booked = booked;
    }

    /**
     * Returns ratio of transport cost.
     *
     * @return ratio
     */
    public abstract double getRatio();

    /**
     * Returns type of transport element.
     *
     * @return type of element
     */
    abstract public TransportType getTransportType();

    /**
     * Method to check if commission can be accepted by this transport element
     *
     * @param commission commission to check
     */
    public abstract void checkNewCommission(TransportCommission commission);

    public abstract void checkReorganize(TransportCommission commission);

    public void sendOfferToEUnit(AID aid, TransportOffer offer) {
        send(aid, offer, CommunicationHelper.TRANSPORT_OFFER);
    }

    public void sendReorganizeOfferToEUnit(AID aid, TransportOffer offer) {
        send(aid, offer, CommunicationHelper.TRANSPORT_REORGANIZE_OFFER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new GetTransportCommisionBehaviour(this));
        addBehaviour(new GetTransportFeedbackBahaviour(this));
        addBehaviour(new GetInitialDataBehaviour(this));
        addBehaviour(new GetResetRequestBehaviour(this));
        addBehaviour(new GetTransportReorganizeBehaviour(this));
        addBehaviour(new dtp.jade.transport.behaviour.EndOfSimulationBehaviour(
                this));
        addBehaviour(new GetAgentsDataBehaviour(this));
        addBehaviour(new GetCommisionBehaviour(this));
        addBehaviour(new GetTeamOfferBehaviour(this));
        addBehaviour(new GetStartNegotiationBehaviour(this));
        addBehaviour(new GetTeamResponseBehaviour(this));
        addBehaviour(new GetConfirmationFromDistributorBehaviour(this));
        registerServices();

        askingUnits = new TreeSet<>();
        sendAidToInfoAgent();
        logger.info("Transport agent created: " + getAID());
    }

    /**
     * Services registration
     */
    private void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /* -------- EXECUTION UNIT SERVICE ------- */
        ServiceDescription sd = new ServiceDescription();
        sd.setType("TransportUnitService");
        sd.setName("TransportUnitService");
        dfd.addServices(sd);

        addSubclassServices(dfd);

        /* -------- REGISTRATION ------- */
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException "
                    + fe.getMessage());
        }

    }

    protected void addSubclassServices(DFAgentDescription dfd) {
        // Intentionally left empty to be overridden by subclasses

    }

    private void sendAidToInfoAgent() {

        AID[] aids = CommunicationHelper.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {
            CommunicationHelper messageCode;
            if (getTransportType().equals(TransportType.DRIVER)) {
                messageCode = CommunicationHelper.TRANSPORT_DRIVER_AID;
            } else if (getTransportType().equals(TransportType.TRAILER)) {
                messageCode = CommunicationHelper.TRANSPORT_TRAILER_AID;
            } else {
                messageCode = CommunicationHelper.TRANSPORT_TRUCK_AID;
            }
            send(aids[0], this.getAID(), messageCode);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public void resetAgent() {
        booked = false;
    }

    /**
     * getter
     *
     * @return the depot
     */
    public int getDepot() {
        return this.initialData.getDepot();
    }

    /**
     * setter
     *
     * @param depot the depot to set
     */
    public void setDepot(int depot) {
        this.initialData.setDepot(depot);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }
}
