package dtp.jade.eunit.behaviour;

import algorithm.Schedule;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetComplexSTScheduleChangedBehaviour extends CyclicBehaviour {


    private ExecutionUnitAgent agent;

    public GetComplexSTScheduleChangedBehaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLONS_NEW_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                agent.setNewSchedule((Schedule) msg.getContentObject(), msg.getSender());
            } catch (UnreadableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } else {
            block();
        }
    }
}
