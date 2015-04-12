package dtp.jade.transport.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Behaviour used by transport elements to receive commission information.
 *
 * @author Michal Golacki
 */
public class GetTeamResponseBehaviour extends CyclicBehaviour {

    /**
     * Serial version
     */


    /**
     * Agent
     */
    private final TransportAgent agent;

    public GetTeamResponseBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.TEAM_OFFER_RESPONSE);
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                String response = (String) message.getContentObject();
                if (response.equals("yes"))
                    agent.response(message.getSender(), true);
                else if (response.equals("no"))
                    agent.response(message.getSender(), false);
                else
                    agent.response(message.getSender(), null);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }

    }
}
