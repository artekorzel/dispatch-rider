package dtp.jade.test.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetTransportAgentConfirmationBehaviour extends CyclicBehaviour {



    private GUIAgent agent;

    public GetTransportAgentConfirmationBehaviour(GUIAgent agent) {

        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_AGENT_CONFIRMATION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            agent.transportAgentConfirmationOfReceivingAgentsData();
        } else {
            block();
        }
    }
}
