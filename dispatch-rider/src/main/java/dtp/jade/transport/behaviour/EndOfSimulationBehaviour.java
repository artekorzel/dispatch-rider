package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

    private TransportAgent agent;

    public EndOfSimulationBehaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.SIM_END.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.doDelete();
        } else {
            block();
        }
    }
}
