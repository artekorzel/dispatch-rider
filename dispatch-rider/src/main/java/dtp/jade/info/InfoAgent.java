package dtp.jade.info;

import dtp.jade.AgentsService;
import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.info.behaviour.*;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportAgentsMessage;
import dtp.jade.transport.TransportType;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.log4j.Logger;

import java.util.*;

public class InfoAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(InfoAgent.class);

    private int driverAgentsNo;
    private int truckAgentsNo;
    private int trailerAgentsNo;
    private int eunitAgentsNo;

    private Map<TransportType, List<TransportAgentData>> agents;
    private int nextVMAgentIndex = 0;

    protected void setup() {

        agents = new HashMap<>();

        logger.info(this.getLocalName() + " - Hello World!");

        /*-------- SERVICES SECTION -------*/
        registerServices();

        /*-------- BEHAVIOURS SECTION -------*/
        addBehaviour(new EUnitCreationBehaviour(this));
        addBehaviour(new DriverCreationBehaviour(this));
        addBehaviour(new TruckCreationBehaviour(this));
        addBehaviour(new TrailerCreationBehaviour(this));
        addBehaviour(new EndOfSimulationBehaviour(this));
        addBehaviour(new GetAgentsDataBehaviour(this));

        logger.info("InfoAgent - end of initialization");
    }

    private void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /*-------- AGENT CREATION SERVICE SECTION -------*/
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AgentCreationService");
        sd.setName("AgentCreationService");
        dfd.addServices(sd);
        logger.info(this.getLocalName() + " - registering AgentCreationService");

        /*-------- REGISTRATION SECTION -------*/
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException ", fe);
        }
    }

    public void addTransportAgentData(TransportAgentData data, TransportType type) {
        List<TransportAgentData> transportData = agents.get(type);
        if (transportData == null) {
            transportData = new LinkedList<>();
            agents.put(type, transportData);
        }
        transportData.add(data);
    }

    public int getDriverAgentsNo() {
        return this.driverAgentsNo;
    }

    public void addDriverAgentInfo() {
        this.driverAgentsNo++;
    }

    public int getTruckAgentsNo() {
        return this.truckAgentsNo;
    }

    public void addTruckAgentInfo() {
        this.truckAgentsNo++;
    }

    public int getTrailerAgentsNo() {
        return this.trailerAgentsNo;
    }

    public void addTrailerAgentInfo() {
        this.trailerAgentsNo++;
    }

    public int getEUnitAgentsNo() {
        return this.eunitAgentsNo;
    }

    public void addEUnitAgentInfo() {
        this.eunitAgentsNo++;
    }

    public AID getNextVMAgent() {
        AID[] vmAgents = AgentsService.findAgentByServiceName(this, "VMAgentCreationService");
        Arrays.sort(vmAgents);
        return vmAgents[nextVMAgentIndex++ % vmAgents.length];
    }

    public void simEnd() {
        eunitAgentsNo = 0;
        driverAgentsNo = 0;
        trailerAgentsNo = 0;
        truckAgentsNo = 0;
        agents = new HashMap<>();
    }

    public void sendDataToAgents() {
        AID[] aids = AgentsService.findAgentByServiceName(this, "TransportUnitService");

        logger.info("InfoAgent - sending agents data to agents");
        send(aids, new TransportAgentsMessage(agents), MessageType.AGENTS_DATA_FOR_TRANSPORTUNITS);

        aids = AgentsService.findAgentByServiceName(this, "CommissionService");

        logger.info("InfoAgent - sending agents data to Distributor");
        send(aids, new TransportAgentsMessage(agents), MessageType.AGENTS_DATA_FOR_TRANSPORTUNITS);
    }
}
