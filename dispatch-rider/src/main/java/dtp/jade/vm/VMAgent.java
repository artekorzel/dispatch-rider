package dtp.jade.vm;

import dtp.jade.BaseAgent;
import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.transport.driver.DriverAgent;
import dtp.jade.transport.trailer.TrailerAgent;
import dtp.jade.transport.truck.TruckAgent;
import dtp.jade.vm.behaviour.AgentCreationBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;

public class VMAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(VMAgent.class);

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " VMAgent - Hello World!");

        registerServices();

        addBehaviour(new AgentCreationBehaviour(this, MessageType.DRIVER_CREATION, DriverAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, MessageType.TRUCK_CREATION, TruckAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, MessageType.TRAILER_CREATION, TrailerAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, MessageType.EXECUTION_UNIT_CREATION, ExecutionUnitAgent.class));

        logger.info(this.getLocalName() + " VMAgent - end of initialization");
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("VMAgentCreationService");
        sd.setName("VMAgentCreationService");
        dfd.addServices(sd);
        logger.info(this.getLocalName() + " - registering VMAgentCreationService");

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(this.getLocalName() + " - FIPAException ", fe);
        }
    }

    public void createAgent(String agentName, Class<? extends Agent> agentClass) {
        try {
            getContainerController().createNewAgent(agentName, agentClass.getName(), null).start();
            logger.info(getName() + " - " + agentName + " created");
        } catch (StaleProxyException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
