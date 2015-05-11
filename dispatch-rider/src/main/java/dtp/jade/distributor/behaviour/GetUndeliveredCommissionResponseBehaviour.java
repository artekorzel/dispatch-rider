package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetUndeliveredCommissionResponseBehaviour extends CyclicBehaviour {

    private final DistributorAgent agent;

    public GetUndeliveredCommissionResponseBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.UNDELIVERED_COMMISSION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.undeliveredCommissionResponse();

        } else {
            block();
        }
    }
}
