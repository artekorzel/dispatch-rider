package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetConfirmOfTimeStampBehaviour extends CyclicBehaviour {
    protected static Logger logger = Logger.getLogger(GetConfirmOfTimeStampBehaviour.class);

    private SimulationAgent agent;

    public GetConfirmOfTimeStampBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TIME_STAMP_CONFIRM.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
//            logger.info(msg.getSender().getName() + " - stampConfirmed");
            agent.stampConfirmed();
        } else {
            block();
        }
    }
}
