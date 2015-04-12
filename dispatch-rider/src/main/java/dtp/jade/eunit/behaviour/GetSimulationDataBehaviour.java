package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetSimulationDataBehaviour extends CyclicBehaviour {

    private final ExecutionUnitAgent eunitAgent;

    public GetSimulationDataBehaviour(ExecutionUnitAgent agent) {
        this.eunitAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIMULATION_DATA);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            eunitAgent.sendSimmulationData(msg.getSender());
        } else {
            block();
        }
    }
}
