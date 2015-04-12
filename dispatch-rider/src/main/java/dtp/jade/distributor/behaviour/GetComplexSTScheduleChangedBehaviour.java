package dtp.jade.distributor.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.DistributorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetComplexSTScheduleChangedBehaviour extends CyclicBehaviour {


    private DistributorAgent agent;

    public GetComplexSTScheduleChangedBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLONS_NEW_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.scheduleChanged();


        } else {
            block();
        }
    }
}
