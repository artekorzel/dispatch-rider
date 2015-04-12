package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import machineLearning.MLAlgorithm;
import org.apache.log4j.Logger;

/**
 * @author KONY
 */
public class GetMLTableBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(GetMLTableBehaviour.class);

    private final GUIAgent guiAgent;

    public GetMLTableBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.MLTable);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                guiAgent.saveMLAlgorithm((MLAlgorithm) msg.getContentObject());
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName()
                        + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
