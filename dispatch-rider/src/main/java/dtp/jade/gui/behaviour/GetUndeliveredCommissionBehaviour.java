package dtp.jade.gui.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetUndeliveredCommissionBehaviour extends CyclicBehaviour {



    private static Logger logger = Logger
            .getLogger(GetUndeliveredCommissionBehaviour.class);

    private final GUIAgent guiAgent;

    public GetUndeliveredCommissionBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchPerformative(CommunicationHelper.UNDELIVERIED_COMMISSION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                guiAgent.addUndeliveredCommission((NewTeamData) msg
                        .getContentObject());

                ACLMessage response = new ACLMessage(
                        CommunicationHelper.UNDELIVERIED_COMMISSION);
                response.addReceiver(msg.getSender());
                response.setContentObject("");
                guiAgent.send(response);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        } else {
            block();
        }
    }

}
