package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportCommission;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTransportCommisionBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportCommisionBehaviour.class);

    private TransportAgent agent;

    public GetTransportCommisionBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.TRANSPORT_COMMISSION.name());
        ACLMessage message = myAgent.receive(template);

        TransportCommission transportCommision = null;

        if (message != null) {
            try {
                //logger.info("Got commision from e unit");
                transportCommision = (TransportCommission) message.getContentObject();
                agent.checkNewCommission(transportCommision);
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UndeadableException - " + e.getMessage());
            }
        } else {
            block();
        }

    }
}
