package dtp.jade.test.behaviour;

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

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_AGENT_CREATED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            guiAgent.transportAgentCreated();
            logger.info("new TransportAgent was created ");
        } else {
            block();
        }
    }
}
