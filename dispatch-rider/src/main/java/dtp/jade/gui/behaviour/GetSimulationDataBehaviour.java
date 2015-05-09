package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;
import xml.elements.SimulationData;

/**
 * @author kony.pl
 */
public class GetSimulationDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetSimulationDataBehaviour.class);

    private GUIAgent guiAgent;

    public GetSimulationDataBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.SIMULATION_DATA.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                guiAgent.addSimulationData((SimulationData) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(e);
            }
        } else {

            block();
        }
    }
}
