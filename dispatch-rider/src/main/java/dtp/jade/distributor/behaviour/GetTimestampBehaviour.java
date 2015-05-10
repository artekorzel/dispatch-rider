package dtp.jade.distributor.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTimestampBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger
            .getLogger(GetTimestampBehaviour.class);

    private final dtp.jade.distributor.DistributorAgent distributorAgent;

    public GetTimestampBehaviour(DistributorAgent agent) {
        distributorAgent = agent;
    }

    @Override
    public void action() {

        /*-------- RECIEVING CURRENT TIME STAMP -------*/
        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.TIME_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            Integer time = null;
            try {
                time = (Integer) msg.getContentObject();
            } catch (UnreadableException e) {
                logger.error(this.distributorAgent.getLocalName()
                        + " - IOException " + e.getMessage());
            }

            logger.info(myAgent.getLocalName() + "\t- got time stamp [" + time + "]");

            distributorAgent.nextSimstep(time);
            distributorAgent.send(msg.getSender(), "", MessageType.TIME_STAMP_CONFIRM);
        } else {
            block();
        }
    }
}
