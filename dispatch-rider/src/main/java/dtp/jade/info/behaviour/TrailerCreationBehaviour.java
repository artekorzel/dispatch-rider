package dtp.jade.info.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportType;
import dtp.jade.transport.trailer.TrailerAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;

public class TrailerCreationBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(TrailerCreationBehaviour.class);

    private InfoAgent agent;

    public TrailerCreationBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.TRAILER_CREATION.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            AgentContainer container = agent.getContainerController();
            AgentController controller;
            AgentInfoPOJO agentInfo = new AgentInfoPOJO();

            agentInfo.setName("Trailer #" + agent.getTrailerAgentsNo());
            try {
                TransportElementInitialData initial = (TransportElementInitialData) message.getContentObject();
                controller = container.createNewAgent(agentInfo.getName(), TrailerAgent.class.getName(), null);
                controller.start();

                logger.info(agent.getName() + " - " + agentInfo.getName() + " created");

                MessageTemplate template2 = MessageTemplate
                        .MatchConversationId(CommunicationHelper.TRANSPORT_TRAILER_AID.name());
                ACLMessage msg2 = myAgent.blockingReceive(template2, 1000);
                AID aid;
                try {
                    aid = (AID) msg2.getContentObject();
                    agentInfo.setAID(aid);
                    agent.addTrailerAgentInfo();

                    //TODO sprawdzic, czy initial sie nie zmienia
                    agent.addTransportAgentData(new TransportAgentData(initial, aid), TransportType.TRAILER);
                    agent.send(aid, initial, CommunicationHelper.TRANSPORT_INITIAL_DATA);

                } catch (UnreadableException e) {
                    logger.error(this.agent.getLocalName() + " - UnreadableException " + e.getMessage());
                }

            } catch (StaleProxyException | UnreadableException e) {
                logger.error(e);
            }

            agent.send(message.getSender(), "", CommunicationHelper.TRANSPORT_AGENT_CREATED);
        } else {
            block();
        }

    }

}
