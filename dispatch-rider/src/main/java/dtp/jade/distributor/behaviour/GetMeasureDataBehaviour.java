package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetMeasureDataBehaviour extends CyclicBehaviour {

    private final DistributorAgent distributorAgent;

    public GetMeasureDataBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.MEASURE_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            distributorAgent.send(msg.getSender(), distributorAgent.getMeasureData(), MessageType.MEASURE_DATA);
        } else {
            block();
        }
    }
}
