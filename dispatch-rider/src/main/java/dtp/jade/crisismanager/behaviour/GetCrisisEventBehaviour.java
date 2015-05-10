package dtp.jade.crisismanager.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetCrisisEventBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetCrisisEventBehaviour.class);

    private CrisisManagerAgent crisisManagerAgent;

    public GetCrisisEventBehaviour(CrisisManagerAgent agent) {

        this.crisisManagerAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.CRISIS_EVENT.name());
        ACLMessage msg = myAgent.receive(template);

        CrisisEvent crisisEvent;

        if (msg != null) {

            try {
                crisisEvent = (CrisisEvent) msg.getContentObject();
                crisisManagerAgent.addCrisisEvent(crisisEvent);
            } catch (UnreadableException e) {
                logger.error(crisisManagerAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
