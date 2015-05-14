package dtp.jade.simulation.behaviour;

import dtp.jade.MessageType;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetMessageBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetMessageBehaviour.class);

    private SimulationAgent agent;

    public GetMessageBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    public void action() {

        /*-------- RECIEVING REQUESTS FOR DISPLAYING MESSAGE IN GUI -------*/
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.GUI_MESSAGE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            /*-------- DISPLAYING MESSAGE -------*/
            try {

                String message = (String) msg.getContentObject();

                // TODO temporary
                if (message.equals("DistributorAgent - NEXT_SIMSTEP")) {
                    agent.nextAutoSimStep();
                    // wykorzystywane do sim GOD
                    // startuje timer, zeby ten zrobil nextSimstep i statystyki
                    // zaraz potem timer trzeba zatrzymac
                }

            } catch (UnreadableException e) {
                logger.error(this.agent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {

            block();
        }
    }
}
