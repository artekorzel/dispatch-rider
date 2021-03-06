package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetConfigurationChangeBehaviour extends CyclicBehaviour {


    private final DistributorAgent distributorAgent;

    public GetConfigurationChangeBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.CONFIGURATION_CHANGE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            distributorAgent.configurationChanged();
        } else {

            block();
        }
    }
}
