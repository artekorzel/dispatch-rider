package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetComplexSTScheduleBehaviour extends CyclicBehaviour {


    private ExecutionUnitAgent agent;

    public GetComplexSTScheduleBehaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLONS_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.sendSchedule(msg.getSender(), false);

        } else {
            block();
        }
    }
}
