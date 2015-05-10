package dtp.jade.vm.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.vm.VMAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;

public class AgentCreationBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(AgentCreationBehaviour.class);

    private VMAgent agent;
    private CommunicationHelper messageType;
    private Class<? extends Agent> agentClass;

    public AgentCreationBehaviour(VMAgent agent, CommunicationHelper messageType, Class<? extends Agent> agentClass) {
        this.messageType = messageType;
        this.agentClass = agentClass;
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(messageType.name());
        ACLMessage message = agent.receive(template);

        if (message != null) {
            AgentContainer container = agent.getContainerController();
            AgentController controller;

            try {
                String agentName = message.getContent();
                controller = container.createNewAgent(agentName, agentClass.getName(), null);
                controller.start();

                logger.info(agent.getName() + " - " + agentName + " created");
            } catch (StaleProxyException e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }
}
