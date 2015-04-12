package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.ExecutionUnitAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetInitialDataBehaviour extends CyclicBehaviour {

    /**
     *
     */

    private ExecutionUnitAgent eUnit;

    public GetInitialDataBehaviour(ExecutionUnitAgent eUnit) {
        this.eUnit = eUnit;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_INITIAL_DATA);
        ACLMessage msg = eUnit.receive(template);
        if (msg != null) {
            try {
                EUnitInitialData initialData = (EUnitInitialData) msg.getContentObject();
                eUnit.setInitialData(initialData);
            } catch (UnreadableException e) {
            }
        } else {
            block();
        }

    }

}
