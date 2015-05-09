package dtp.jade.info;

import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.info.behaviour.*;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportAgentsMessage;
import dtp.jade.transport.TransportType;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfoAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(InfoAgent.class);

    private int driverAgentsNo;
    private int truckAgentsNo;
    private int trailerAgentsNo;
    private int eunitAgentsNo;

    private Map<TransportType, List<TransportAgentData>> agents;

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

    void registerServices() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        /*-------- AGENT CREATION SERVICE SECTION -------*/
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("AgentCreationService");
        sd1.setName("AgentCreationService");
        dfd.addServices(sd1);
        logger.info(this.getLocalName() + " - registering AgentCreationService");

        /*-------- AGENT DELETION SERVICE SECTION -------*/
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("AgentDeletionService");
        sd2.setName("AgentDeletionService");
        dfd.addServices(sd2);
        logger.info(this.getLocalName() + " - registering AgentDeletionService");

        /*-------- INFO AGENT SERVICE SECTION -------*/
        ServiceDescription sd3 = new ServiceDescription();
        sd3.setType("InfoAgentService");
        sd3.setName("InfoAgentService");
        dfd.addServices(sd3);
        logger.info(this.getLocalName() + " - registering AgentAgentService");

        /*-------- REGISTRATION SECTION -------*/
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException " + fe.getMessage());
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

    public void simEnd() {
        eunitAgentsNo = 0;
        driverAgentsNo = 0;
        trailerAgentsNo = 0;
        truckAgentsNo = 0;
        agents = new HashMap<>();
    }

    public void sendDataToAgents() {
        ACLMessage cfp;
        AID[] aids = CommunicationHelper.findAgentByServiceName(this, "TransportUnitService");

        logger.info("InfoAgent - sending agents data to agents");
        for (AID aid : aids) {
            cfp = new ACLMessage(CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
            cfp.addReceiver(aid);
            try {
                cfp.setContentObject(new TransportAgentsMessage(agents));
            } catch (IOException e) {
                logger.error(getLocalName() + " - IOException " + e.getMessage());
            }
            send(cfp);
        }

        aids = CommunicationHelper.findAgentByServiceName(this, "CommissionService");

        logger.info("InfoAgent - sending agents data to Distributor");
        for (AID aid : aids) {

            cfp = new ACLMessage(CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
            cfp.addReceiver(aid);
            try {
                cfp.setContentObject(new TransportAgentsMessage(agents));
            } catch (IOException e) {
                logger.error(getLocalName() + " - IOException " + e.getMessage());
            }
            send(cfp);
        }
    }
}
