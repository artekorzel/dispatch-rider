package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author KONY
 */
public class GetSTBeginBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent executionUnitAgent;

    public GetSTBeginBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.ST_BEGIN.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            executionUnitAgent.send(msg.getSender(), "", CommunicationHelper.ST_BEGIN);
        } else {

            block();
        }
    }
}
