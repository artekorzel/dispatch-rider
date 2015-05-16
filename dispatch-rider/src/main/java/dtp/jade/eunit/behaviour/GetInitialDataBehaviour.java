package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetInitialDataBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetInitialDataBehaviour.class);

    private ExecutionUnitAgent eUnit;

    public GetInitialDataBehaviour(ExecutionUnitAgent eUnit) {
        this.eUnit = eUnit;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.EUNIT_INITIAL_DATA.name());
        ACLMessage msg = eUnit.receive(template);
        if (msg != null) {
            try {
                EUnitInitialData initialData = (EUnitInitialData) msg.getContentObject();
                eUnit.setInitialData(initialData);
            } catch (UnreadableException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            block();
        }

    }

}
