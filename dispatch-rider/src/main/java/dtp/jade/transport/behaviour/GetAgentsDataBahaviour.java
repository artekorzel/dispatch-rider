package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportAgentsMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Behaviour for receiving initial data for transport element.
 *
 * @author Michal Golacki
 */
public class GetAgentsDataBahaviour extends CyclicBehaviour {

    /**
     *
     */


    /**
     * Agent
     */
    private TransportAgent agent;

    public GetAgentsDataBahaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
        ACLMessage message = agent.receive(template);

        if (message != null) {
            try {
                TransportAgentsMessage agents = (TransportAgentsMessage) message.getContentObject();
                agent.setAgentsData(agents.getAgents());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        } else {
            block();
        }

    }

}
