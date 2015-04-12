package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import machineLearning.MLAlgorithm;

import java.io.IOException;

public class GetMLTableBehaviour extends CyclicBehaviour {



    private final DistributorAgent agent;

    public GetMLTableBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.MLTable);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            MLAlgorithm table = agent.getMLAlgorithm();
            ACLMessage resp = new ACLMessage(CommunicationHelper.MLTable);
            resp.addReceiver(msg.getSender());
            try {
                resp.setContentObject(table);
            } catch (IOException e) {
                e.printStackTrace();
            }
            agent.send(resp);

        } else {
            block();
        }
    }
}
