package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent executionUnitAgent;

    public EndOfSimulationBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.SIM_END.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            executionUnitAgent.doDelete();

        } else {
            block();
        }
    }

}
