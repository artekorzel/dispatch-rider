package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import machineLearning.MLAlgorithm;

public class GetMLTableBehaviour extends CyclicBehaviour {

    private final DistributorAgent agent;

    public GetMLTableBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.MLTable.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            MLAlgorithm table = agent.getMLAlgorithm();
            agent.send(msg.getSender(), table, MessageType.MLTable);
        } else {
            block();
        }
    }
}
