package dtp.jade.info.behaviour;

import dtp.jade.MessageType;
import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportType;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class TrailerCreationBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(TrailerCreationBehaviour.class);

    private InfoAgent agent;

    public TrailerCreationBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRAILER_CREATION.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            String agentName = "Trailer #" + agent.getTrailerAgentsNo();
            try {
                TransportElementInitialData initial = (TransportElementInitialData) message.getContentObject();

                agent.sendString(agent.getNextVMAgent(), agentName, MessageType.TRAILER_CREATION);
                logger.info(agent.getName() + " - " + agentName + " created");

                MessageTemplate template2 = MessageTemplate
                        .MatchConversationId(MessageType.TRANSPORT_TRAILER_AID.name());
                ACLMessage msg2 = myAgent.blockingReceive(template2);
                AID aid = (AID) msg2.getContentObject();
                AgentInfoPOJO agentInfo = new AgentInfoPOJO();
                agentInfo.setName(agentName);
                agentInfo.setAID(aid);
                agent.addTrailerAgentInfo();

                agent.addTransportAgentData(new TransportAgentData(initial, aid), TransportType.TRAILER);
                agent.send(aid, initial, MessageType.TRANSPORT_INITIAL_DATA);
            } catch (UnreadableException e) {
                logger.error(e);
            }

            agent.send(message.getSender(), "", MessageType.TRANSPORT_AGENT_CREATED);
        } else {
            block();
        }

    }

}
