package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.simulation.SimulationAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.log4j.Logger;

public class GetUndeliveredCommissionBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetUndeliveredCommissionBehaviour.class);

    private final SimulationAgent agent;

    public GetUndeliveredCommissionBehaviour(SimulationAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.UNDELIVERED_COMMISSION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            try {
                agent.addUndeliveredCommission((NewTeamData) msg
                        .getContentObject());

                agent.send(msg.getSender(), "", MessageType.UNDELIVERED_COMMISSION);
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            block();
        }
    }

}
