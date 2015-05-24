package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetMessageBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetMessageBehaviour.class);

    private SimulationAgent agent;

    public GetMessageBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        /*-------- RECIEVING REQUESTS FOR DISPLAYING MESSAGE IN GUI -------*/
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.NEXT_SIMSTEP.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.nextAutoSimStep();
        } else {
            block();
        }
    }
}
