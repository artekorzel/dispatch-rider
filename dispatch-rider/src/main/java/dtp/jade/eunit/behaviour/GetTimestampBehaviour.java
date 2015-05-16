package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTimestampBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger
            .getLogger(GetTimestampBehaviour.class);

    private final ExecutionUnitAgent eunitAgent;

    public GetTimestampBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
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
                eunitAgent.setCurrentTimestamp(time);
            } catch (UnreadableException e) {
                logger.error(this.eunitAgent.getLocalName() + " - IOException ", e);
            }

            logger.info(myAgent.getLocalName() + "\t- got time stamp ["
                    + time + "]");

            eunitAgent.send(msg.getSender(), "", MessageType.TIME_STAMP_CONFIRM);
        } else {
            block();
        }
    }
}
