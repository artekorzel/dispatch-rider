package dtp.jade.vm.behaviour;

import dtp.jade.MessageType;
import dtp.jade.vm.VMAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentCreationBehaviour extends CyclicBehaviour {

    private VMAgent agent;
    private MessageType messageType;
    private Class<? extends Agent> agentClass;

    public AgentCreationBehaviour(VMAgent agent, MessageType messageType, Class<? extends Agent> agentClass) {
        this.messageType = messageType;
        this.agentClass = agentClass;
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(messageType.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            String agentName = message.getContent();
            agent.createAgent(agentName, agentClass);
        } else {
            block();
        }
    }
}
