package dtp.jade.info.behaviour;

import dtp.jade.MessageType;
import dtp.jade.info.InfoAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetAgentsDataBehaviour extends CyclicBehaviour {

    private InfoAgent agent;

    public GetAgentsDataBehaviour(InfoAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.AGENTS_DATA.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            agent.sendDataToAgents();
        } else {
            block();
        }

    }
}
