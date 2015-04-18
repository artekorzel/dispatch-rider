package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitInfo;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetEUnitInfoBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetEUnitInfoBehaviour.class);

    private GUIAgent guiAgent;

    public GetEUnitInfoBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_INFO);
        ACLMessage msg = myAgent.receive(template);

        EUnitInfo info;

        if (msg != null) {
            try {
                info = (EUnitInfo) msg.getContentObject();
                guiAgent.updateEUnitInfo(info);
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
