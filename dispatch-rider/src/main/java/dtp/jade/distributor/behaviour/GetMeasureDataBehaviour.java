package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GetMeasureDataBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger
            .getLogger(GetMeasureDataBehaviour.class);

    private final DistributorAgent distributorAgent;

    public GetMeasureDataBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.MEASURE_DATA);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                ACLMessage response = new ACLMessage(
                        CommunicationHelper.MEASURE_DATA);
                response.addReceiver(msg.getSender());

                response.setContentObject(distributorAgent.getMeasureData());

                distributorAgent.send(response);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }

        } else {

            block();
        }
    }
}
