package dtp.jade.distributor.behaviour;

import algorithm.Schedule;
import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetComplexSTScheduleBehaviour extends CyclicBehaviour {

    private final DistributorAgent agent;

    public GetComplexSTScheduleBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.HOLONS_CALENDAR);
        ACLMessage msg = myAgent.receive(template);
        
        if (msg != null) {
            try {
                Schedule schedule = (Schedule) msg.getContentObject();
            	if(schedule.getRefreshCurrentLocation())
            		agent.putEUnitSchedule(msg.getSender(), schedule);
            	else
            		agent.addComplexSTSchedule(schedule, msg.getSender());
            } catch (Exception e) {
                e.printStackTrace();//FIXME
            }
        } else {
            block();
        }
    }
}
