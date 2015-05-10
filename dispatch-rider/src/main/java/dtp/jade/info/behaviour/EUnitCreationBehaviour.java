package dtp.jade.info.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class EUnitCreationBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(EUnitCreationBehaviour.class);

    private InfoAgent infoAgent;

    public EUnitCreationBehaviour(InfoAgent agent) {
        this.infoAgent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.EXECUTION_UNIT_CREATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            String agentName = "EUnitAgent#" + this.infoAgent.getEUnitAgentsNo();
            try {
                EUnitInitialData initialData = (EUnitInitialData) msg.getContentObject();

                infoAgent.sendString(infoAgent.getNextVMAgent(), agentName, CommunicationHelper.EXECUTION_UNIT_CREATION);
                logger.info(infoAgent.getName() + " - " + agentName + " created");

                MessageTemplate template2 = MessageTemplate.MatchConversationId(CommunicationHelper.EXECUTION_UNIT_AID.name());
                ACLMessage msg2 = myAgent.blockingReceive(template2);
                AID aid = (AID) msg2.getContentObject();
                AgentInfoPOJO newAgentInfo = new AgentInfoPOJO();
                newAgentInfo.setName(agentName);
                newAgentInfo.setAID(aid);
                infoAgent.addEUnitAgentInfo();

                infoAgent.send(aid, initialData, CommunicationHelper.EUNIT_INITIAL_DATA);
            } catch (UnreadableException e) {
                logger.error(this.infoAgent.getLocalName() + " - UnreadableException ", e);
            }
        } else {
            block();
        }
    }
}
