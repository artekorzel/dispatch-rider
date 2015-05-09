package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetResetRequestBehaviour extends CyclicBehaviour {

    /**
     *
     */

    private TransportAgent agent;

    public GetResetRequestBehaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.RESET.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.resetAgent();

        } else {
            block();
        }

    }

}
