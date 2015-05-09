package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import dtp.simulation.SimInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetSimInfoBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetSimInfoBehaviour.class);

    private final DistributorAgent agent;

    public GetSimInfoBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO);
        ACLMessage msg = myAgent.receive(template);

        SimInfo simConstrains;

        if (msg != null) {

            try {
                simConstrains = (SimInfo) msg.getContentObject();
                agent.setSimInfo(simConstrains);
            } catch (UnreadableException e1) {
                logger.error(e1);
            }
        } else {
            block();
        }
    }
}
