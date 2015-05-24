package dtp.jade.transport;

import dtp.commission.Commission;
import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
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

    public static double costFunctionValue(String function, double dist,
                                           TransportElementInitialData driver,
                                           TransportElementInitialDataTruck truck,
                                           TransportElementInitialDataTrailer trailer, Commission comm,
                                           Double punishment) {
        String expr = function;
        expr = expr.replace("power", Double.toString(truck.getPower()));
        expr = expr.replace("reliability",
                Double.toString(truck.getReliability()));
        expr = expr.replace("comfort",
                Double.toString(truck.getComfort()));
        expr = expr.replace("fuel",
                Double.toString(truck.getFuelConsumption()));
        expr = expr.replace("mass", Double.toString(trailer.getMass()));
        expr = expr.replace("capacity",
                Double.toString(trailer.getCapacity()));
        expr = expr.replace("actualLoad",
                Double.toString(comm.getActualLoad()));
        expr = expr.replace("universality",
                Double.toString(trailer.getUniversality()));
        expr = expr.replace("dist", Double.toString(dist));
        expr = expr.replace("load", Double.toString(comm.getLoad()));
        expr = expr.replace("pickUpServiceTime",
                Double.toString(comm.getPickUpServiceTime()));
        expr = expr.replace("deliveryServiceTime",
                Double.toString(comm.getDeliveryServiceTime()));
        if (punishment != null)
            expr = expr.replace("punishment", punishment.toString());
        else
            expr = expr.replace("punishment", "0");
        return Calculator.calculate(expr);
    }

    public synchronized void setCommission(Commission commission) {
        this.commission = commission;
        trucks = agents.get(TransportType.TRUCK);
        trailers = agents.get(TransportType.TRAILER);
        drivers = agents.get(TransportType.DRIVER);

        askingUnits = new TreeSet<>();
        confirmedUnits = new TreeSet<>();
        waitingUnits = new TreeSet<>();
        askingUnits = Collections.synchronizedSet(askingUnits);
        confirmedUnits = Collections.synchronizedSet(confirmedUnits);
        waitingUnits = Collections.synchronizedSet(waitingUnits);
        feedbackSended = false;

        if (!isHolonPart())
            makeHolonPartsList();

        sendReadyToStartNegotiation();
    }

    public synchronized void setCommissions(Commission[] commissions) {
        this.commissions = commissions;
        trucks = agents.get(TransportType.TRUCK);
        trailers = agents.get(TransportType.TRAILER);
        drivers = agents.get(TransportType.DRIVER);

        askingUnits = new TreeSet<>();
        confirmedUnits = new TreeSet<>();
        waitingUnits = new TreeSet<>();
        askingUnits = Collections.synchronizedSet(askingUnits);
        confirmedUnits = Collections.synchronizedSet(confirmedUnits);
        waitingUnits = Collections.synchronizedSet(waitingUnits);
        feedbackSended = false;

        if (!isHolonPart())
            makeList();

        sendReadyToStartNegotiation();
    }

    protected abstract boolean canCarryCommission(Commission com,
                                                  HolonPartsCost part);

    protected abstract void makeHolonPartsListFromAllAgents();

    private synchronized void makeList() {
        Commission com;
        commission = commissions[0];
        makeHolonPartsListFromAllAgents();
        for (Commission commission1 : commissions) {
            com = commission1;
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

    private synchronized void tryChangeCommissions(HolonPartsCost part,
                                                   Commission com) {

        if (part.getCommissions().size() == 0)
            return;
        double oldCost = calculateCommissionsCost(part.getCommissions(), part);
        double newCost;
        List<Commission> newPartList = part.getCommissions();
        for (int i = 0; i < part.getCommissionsCount(); i++) {
            List<Commission> newList = new LinkedList<>();
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

    private synchronized double calculateCommissionsCost(
            List<Commission> commissionsList, HolonPartsCost part) {
        double dist;
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

    protected abstract double getCostFunctionValue(HolonPartsCost part,
                                                   double dist, Commission com);

    protected synchronized int calculateLoad(Commission com,
                                             List<Commission> addedCommissions) {
        int result = 0;
        for (Commission c : addedCommissions)
            result += c.getLoad();
        result += com.getLoad();
        return result;
    }

    private synchronized void sendReadyToStartNegotiation() {
        AID[] aids = AgentsService.findAgentByServiceName(this,
                "CommissionService");

        if (aids.length == 1) {
            send(aids[0], "", MessageType.TRANSPORT_AGENT_PREPARED_TO_NEGOTIATION);
        } else {
            logger.error(getLocalName()
                    + " - none or more than one agent with CommissionService in the system");
        }
    }

    protected abstract void makeHolonPartsList();

    private boolean isTruck(AID aid) {
        return aid.getName().contains("Truck");
    }

    private boolean isTrailer(AID aid) {
        return aid.getName().contains("Trailer");
    }

    private boolean isDriver(AID aid) {
        return aid.getName().contains("Driver");
    }

    private synchronized boolean isHolonPart() {
        return holonParts != null && holonParts.length > 0;
    }

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

    public synchronized void startNegotiation() {
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

    private synchronized void askForConnection(AID aid) {
        if (askingUnits.contains(aid))
            return;
        if (confirmedUnits.contains(aid))
            return;
        askingUnits.add(aid);
        send(aid, "", MessageType.TEAM_OFFER);
        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());
    }

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

    private synchronized void respondToTeamOffer(AID aid, String response) {
        if (response.equals("yes")) {
            confirmedUnits.add(aid);
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<>();
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
            waitingUnits.add(aid);
        }

        if (confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());

    }

    private synchronized void sendResponse(AID aid, String response) {
        if (waitingUnits != null) {
            waitingUnits.remove(aid);
            if (response.equals("no")) {
                List<HolonPartsCost> newHolonPartsCostList = new LinkedList<>();
                boolean contain;
                for (HolonPartsCost part : holonPartsCostList) {
                    contain = false;
                    for (TransportAgentData agent : part.getAgents()) {
                        if (agent.getAid().equals(aid)) {
                            contain = true;
                            break;
                        }
                    }
                    if (!contain)
                        newHolonPartsCostList.add(part);
                }
                holonPartsCostList = newHolonPartsCostList;
                if (holonPartsCostList.size() == 0)
                    sendFeedbackToDistributor(new NewHolonOffer());
            }
        }
        send(aid, response, MessageType.TEAM_OFFER_RESPONSE);
        if (confirmedUnits != null
                && confirmedUnits.size() == TransportType.values().length - 1) {
            sendFeedback();
            return;
        }
        if (holonPartsCostList == null || holonPartsCostList.size() == 0)
            sendFeedbackToDistributor(new NewHolonOffer());
    }

    public synchronized void response(AID aid, Boolean response) {
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
                if (!present)
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
            List<HolonPartsCost> newHolonPartsCostList = new LinkedList<>();
            boolean contain;
            for (HolonPartsCost part : holonPartsCostList) {
                contain = false;
                for (TransportAgentData agent : part.getAgents()) {
                    if (agent.getAid().equals(aid)) {
                        contain = true;
                        break;
                    }
                }
                if (!contain)
                    newHolonPartsCostList.add(part);
            }
            holonPartsCostList = newHolonPartsCostList;
        }

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

    public synchronized void sendFeedbackToDistributor(NewHolonOffer offer) {
        if (!feedbackSended) {
            feedbackSended = true;
            if (waitingUnits != null)
                for (AID aid : waitingUnits.toArray(new AID[waitingUnits.size()])) {
                    if (!confirmedUnits.contains(aid))
                        sendResponse(aid, "no");
                }
            if (confirmedUnits != null)
                for (AID aid : confirmedUnits)
                    sendResponse(aid, "yes");

            AID[] aids = AgentsService.findAgentByServiceName(this,
                    "CommissionService");

            if (aids.length == 1) {
                send(aids[0], offer, MessageType.NEW_HOLON_OFFER);
            } else {
                logger.error(getLocalName()
                        + " - none or more than one agent with CommissionService in the system");
            }

        }
    }

    private synchronized TransportAgentData getTruck(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRUCK)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

    private synchronized TransportAgentData getTrailer(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.TRAILER)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

    private synchronized TransportAgentData getDriver(AID aid) {
        for (TransportAgentData agent : agents.get(TransportType.DRIVER)) {
            if (agent.getAid().equals(aid))
                return agent;
        }
        return null;
    }

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
            }
        }

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "CommissionService");
        send(aids[0], "", MessageType.HOLON_FEEDBACK);
    }

    public void setTransportElementInitialData(
            TransportElementInitialData initialData) {
        this.initialData = initialData;
    }

    public int getCapacity() {
        return initialData.getCapacity();
    }

    public void setCapacity(int capacity) {
        this.initialData.setCapacity(capacity);
    }

    protected abstract TransportType getType();

    protected Map<TransportType, List<TransportAgentData>> filtr(
            Map<TransportType, List<TransportAgentData>> map) {
        Map<TransportType, List<TransportAgentData>> result = new HashMap<>();
        for (TransportType type : map.keySet()) {
            if (!type.equals(getType()))
                result.put(type, map.get(type));
            else {
                List<TransportAgentData> data = map.get(type);
                List<TransportAgentData> newData = new LinkedList<>();
                for (TransportAgentData agentData : data) {
                    if (!agentData.getAid().equals(getAID()))
                        newData.add(agentData);
                }
                result.put(getType(), newData);
            }
        }
        return result;
    }

    public void setAgentsData(Map<TransportType, List<TransportAgentData>> agents) {
        this.agents = filtr(agents);

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "SimulationService");

        send(aids, "", MessageType.TRANSPORT_AGENT_CONFIRMATION);
    }

    public List<TransportAgentData> getData(TransportType type) {
        return agents.get(type);
    }

    public abstract double getRatio();

    abstract public TransportType getTransportType();

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new GetInitialDataBehaviour(this));
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
            logger.error(this.getLocalName() + " - FIPAException ", fe);
        }

    }

    protected void addSubclassServices(DFAgentDescription dfd) {
        // Intentionally left empty to be overridden by subclasses

    }

    private void sendAidToInfoAgent() {

        AID[] aids = AgentsService.findAgentByServiceName(this,
                "AgentCreationService");

        if (aids.length == 1) {
            MessageType messageCode;
            if (getTransportType().equals(TransportType.DRIVER)) {
                messageCode = MessageType.TRANSPORT_DRIVER_AID;
            } else if (getTransportType().equals(TransportType.TRAILER)) {
                messageCode = MessageType.TRANSPORT_TRAILER_AID;
            } else {
                messageCode = MessageType.TRANSPORT_TRUCK_AID;
            }
            send(aids[0], this.getAID(), messageCode);
        } else {
            logger.error("None or more than one Info Agent in the system");
        }
    }

    public int getDepot() {
        return this.initialData.getDepot();
    }

    public void setDepot(int depot) {
        this.initialData.setDepot(depot);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignored) {
        }
    }
}
