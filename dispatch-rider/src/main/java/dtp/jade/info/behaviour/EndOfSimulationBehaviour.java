package dtp.jade.info.behaviour;

import dtp.jade.MessageType;
import dtp.jade.info.InfoAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

    private InfoAgent agent;

    public EndOfSimulationBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIM_END.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            agent.simEnd();
        } else {
            block();
        }

    }

}
