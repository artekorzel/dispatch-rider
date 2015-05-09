package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetTransportAgentCreatedBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportAgentCreatedBehaviour.class);

    private GUIAgent guiAgent;

    public GetTransportAgentCreatedBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.TRANSPORT_AGENT_CREATED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            logger.info("new TransportAgent was created ");
            guiAgent.transportAgentCreated();
        } else {
            block();
        }
    }
}
