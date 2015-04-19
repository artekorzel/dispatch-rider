package dtp.jade.crisismanager.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

    private CrisisManagerAgent crisisManagerAgent;

    public EndOfSimulationBehaviour(CrisisManagerAgent agent) {

        this.crisisManagerAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_END);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            crisisManagerAgent.simEnd();
        } else {

            block();
        }
    }

}
