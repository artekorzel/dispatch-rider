package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetNooneRequestBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetNooneRequestBehaviour.class);

    private final DistributorAgent distributorAgent;

    public GetNooneRequestBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DISTRIBUTOR_SHOW_NOONE_LIST);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - got nooneList request behaviour");
            distributorAgent.sendNooneList();

        } else {

            block();
        }
    }
}
