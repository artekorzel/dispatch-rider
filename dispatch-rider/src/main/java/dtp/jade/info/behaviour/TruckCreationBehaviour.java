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

public class TruckCreationBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(TruckCreationBehaviour.class);

    private InfoAgent agent;

    public TruckCreationBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRUCK_CREATION.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            String agentName = "Truck #" + agent.getTruckAgentsNo();
            try {
                TransportElementInitialData initial = (TransportElementInitialData) message.getContentObject();

                agent.sendString(agent.getNextVMAgent(), agentName, MessageType.TRUCK_CREATION);
                logger.info(agent.getName() + " - " + agentName + " created");

                MessageTemplate template2 = MessageTemplate.MatchConversationId(MessageType.TRANSPORT_TRUCK_AID.name());
                ACLMessage msg2 = myAgent.blockingReceive(template2);
                AID aid = (AID) msg2.getContentObject();
                AgentInfoPOJO agentInfo = new AgentInfoPOJO();
                agentInfo.setName(agentName);
                agentInfo.setAID(aid);
                agent.addTruckAgentInfo();

                agent.addTransportAgentData(new TransportAgentData(initial, aid), TransportType.TRUCK);
                agent.send(aid, initial, MessageType.TRANSPORT_INITIAL_DATA);
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }

            agent.send(message.getSender(), "", MessageType.TRANSPORT_AGENT_CREATED);
        } else {
            block();
        }

    }

}
