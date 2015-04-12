package dtp.jade.eunit.behaviour;

import algorithm.Schedule;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetChangeScheduleBehaviour extends CyclicBehaviour {


    private ExecutionUnitAgent eunitAgent;

    public GetChangeScheduleBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.CHANGE_SCHEDULE);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                eunitAgent.changeSchedule((Schedule) msg.getContentObject(), msg.getSender());
            } catch (UnreadableException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            block();
        }
    }
}
