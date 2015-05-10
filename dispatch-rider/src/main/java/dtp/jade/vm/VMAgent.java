package dtp.jade.vm;

import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.transport.driver.DriverAgent;
import dtp.jade.transport.trailer.TrailerAgent;
import dtp.jade.transport.truck.TruckAgent;
import dtp.jade.vm.behaviour.AgentCreationBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.log4j.Logger;

public class VMAgent extends BaseAgent {

    private static Logger logger = Logger.getLogger(VMAgent.class);

    @Override
    protected void setup() {
        logger.info(this.getLocalName() + " VMAgent - Hello World!");

        registerServices();

        addBehaviour(new AgentCreationBehaviour(this, CommunicationHelper.DRIVER_CREATION, DriverAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, CommunicationHelper.TRUCK_CREATION, TruckAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, CommunicationHelper.TRAILER_CREATION, TrailerAgent.class));
        addBehaviour(new AgentCreationBehaviour(this, CommunicationHelper.EXECUTION_UNIT_CREATION, ExecutionUnitAgent.class));

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
            logger.error(this.getLocalName() + " - FIPAException " + fe.getMessage());
        }
    }
}
