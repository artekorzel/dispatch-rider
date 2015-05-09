package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimEndBehaviour extends CyclicBehaviour {

    private final DistributorAgent distributorAgent;

    public SimEndBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }


    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.SIM_END.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            distributorAgent.simEnd();

        } else {

            block();
        }
    }
}
