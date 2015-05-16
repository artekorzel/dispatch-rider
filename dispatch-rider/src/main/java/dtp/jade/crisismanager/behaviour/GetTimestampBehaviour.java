package dtp.jade.crisismanager.behaviour;

import dtp.jade.AgentsService;
import dtp.jade.MessageType;
import dtp.jade.crisismanager.CrisisManagerAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTimestampBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTimestampBehaviour.class);

    private CrisisManagerAgent crisisManagerAgent;

    public GetTimestampBehaviour(CrisisManagerAgent agent) {

        crisisManagerAgent = agent;
    }

    public void action() {

        /*-------- RECIEVING CURRENT TIME STAMP -------*/
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TIME_CHANGED.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            Integer time = null;
            try {

                time = (Integer) msg.getContentObject();

            } catch (UnreadableException e) {

                logger.error(this.crisisManagerAgent.getLocalName() + " - IOException ", e);
            }

            logger.info(myAgent.getLocalName() + "\t- got time stamp [" + time + "]");

            crisisManagerAgent.nextSimstep(time);

            AID[] aids = AgentsService.findAgentByServiceName(crisisManagerAgent, "SimulationService");
            crisisManagerAgent.send(aids[0], "", MessageType.TIME_STAMP_CONFIRM);

        } else {

            block();
        }
    }
}
