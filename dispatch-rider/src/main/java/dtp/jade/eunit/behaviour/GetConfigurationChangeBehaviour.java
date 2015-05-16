package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import measure.configuration.HolonConfiguration;
import org.apache.log4j.Logger;

public class GetConfigurationChangeBehaviour extends CyclicBehaviour {


    private static final Logger logger = Logger
            .getLogger(dtp.jade.distributor.behaviour.GetConfigurationChangeBehaviour.class);
    private final ExecutionUnitAgent executionUnitAgent;

    public GetConfigurationChangeBehaviour(ExecutionUnitAgent agent) {

        this.executionUnitAgent = agent;
    }

    @Override
    public void action() {

        MessageTemplate template = MessageTemplate
                .MatchConversationId(MessageType.CONFIGURATION_CHANGE.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                executionUnitAgent.configurationChanged(
                        (HolonConfiguration) msg.getContentObject(),
                        msg.getSender());
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {

            block();
        }
    }
}
