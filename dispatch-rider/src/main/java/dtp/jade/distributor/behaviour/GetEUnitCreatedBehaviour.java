package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetEUnitCreatedBehaviour extends CyclicBehaviour {


    private DistributorAgent agent;

    public GetEUnitCreatedBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.EXECUTION_UNIT_CREATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.eUnitCreated();
        } else {
            block();
        }
    }
}
