package dtp.jade.info.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportType;
import dtp.jade.transport.driver.DriverAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class DriverCreationBehaviour extends CyclicBehaviour {

    /**
     *
     */


    private static Logger logger = Logger.getLogger(DriverCreationBehaviour.class);

    private InfoAgent agent;

    public DriverCreationBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DRIVER_CREATION);
        ACLMessage message = agent.receive(template);

        if (message != null) {
            AgentContainer container = agent.getContainerController();
            AgentController controller = null;
            AgentInfoPOJO agentInfo = new AgentInfoPOJO();

            agentInfo.setName("Driver #" + agent.getDriverAgentsNo());
            try {
                TransportElementInitialData initial = (TransportElementInitialData) message.getContentObject();
                controller = container.createNewAgent(agentInfo.getName(), DriverAgent.class.getName(), null);
                controller.start();

                logger.info(agent.getName() + " - " + agentInfo.getName() + " created");
                agentInfo.setAgentController(controller);

                MessageTemplate template2 = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_DRIVER_AID);
                ACLMessage msg2 = myAgent.blockingReceive(template2, 1000);
                AID aid = null;
                try {
                    aid = (AID) msg2.getContentObject();
                    agentInfo.setAID(aid);
                    agent.addDriverAgentInfo(agentInfo);

                    //TODO sprawdzic, czy initial sie nie zmienia
                    agent.addTransportAgentData(new TransportAgentData(initial, aid), TransportType.DRIVER);
                    agent.send(aid, initial, CommunicationHelper.TRANSPORT_INITIAL_DATA);

                } catch (UnreadableException e) {
                    logger.error(this.agent.getLocalName() + " - UnreadableException " + e.getMessage());
                }

            } catch (StaleProxyException e) {
                e.printStackTrace();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            AID[] aids = CommunicationHelper.findAgentByServiceName(agent, "GUIService");
            ACLMessage cfp = new ACLMessage(CommunicationHelper.TRANSPORT_AGENT_CREATED);

            cfp.addReceiver(aids[0]);
            try {
                cfp.setContentObject("");
                agent.send(cfp);
            } catch (IOException e) {
                logger.error("DriverCreationBehaviour - IOException " + e.getMessage());
            }

        } else {
            block();
        }

    }
}
