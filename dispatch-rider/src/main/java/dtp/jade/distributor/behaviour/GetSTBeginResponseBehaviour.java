package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetSTBeginResponseBehaviour extends CyclicBehaviour {

    private final DistributorAgent agent;

    public GetSTBeginResponseBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.ST_BEGIN);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.STBeginResponse();

        } else {
            block();
        }
    }
}
