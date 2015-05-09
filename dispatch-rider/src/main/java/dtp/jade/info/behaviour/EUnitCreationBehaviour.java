package dtp.jade.info.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;

public class EUnitCreationBehaviour extends CyclicBehaviour {


    private static Logger logger = Logger.getLogger(EUnitCreationBehaviour.class);

    private InfoAgent infoAgent;

    /**
     * Constructs a new behaviour and allows to access the Info Agent from Action method
     *
     * @param agent - Info Agent
     */
    public EUnitCreationBehaviour(InfoAgent agent) {

        this.infoAgent = agent;
    }

    /**
     * Creates a new eunit agent.
     */
    public void action() {

        /* -------- RECIEVING REQUESTS FOR EUNIT CREATION ------- */
        MessageTemplate template = MessageTemplate.MatchConversationId(CommunicationHelper.EXECUTION_UNIT_CREATION.name());
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
            EUnitInitialData initialData = null;
            try {
                initialData = (EUnitInitialData) msg.getContentObject();
            } catch (UnreadableException ignored) {
            }
            AgentContainer container = myAgent.getContainerController();
            AgentController newAgent;
            AgentInfoPOJO newAgentInfo = new AgentInfoPOJO();

            /* -------- SETTING EUNIT AGENT NAME ------- */
            newAgentInfo.setName("EUnitAgent#" + this.infoAgent.getEUnitAgentsNo());

            /* -------- EUNIT AGENT CREATION ------- */
            try {
                newAgent = container.createNewAgent(newAgentInfo.getName(), ExecutionUnitAgent.class.getName(), null);
                newAgent.start();
            } catch (StaleProxyException e) {
                logger.error(this.infoAgent.getLocalName() + " - StaleProxyException " + e.getMessage());
            }

            /* -------- WAITING FOR AID FROM NEW EUNIT AGENT ------- */
            MessageTemplate template2 = MessageTemplate.MatchConversationId(CommunicationHelper.EXECUTION_UNIT_AID.name());
            // TODO czekanie okreslona ilosc czasu
            ACLMessage msg2 = myAgent.blockingReceive(template2, 1000);
            AID aid = null;
            try {
                aid = (AID) msg2.getContentObject();
                /* -------- SETTING EUNIT AGENT AID ------- */
                newAgentInfo.setAID(aid);
            } catch (UnreadableException e) {
                logger.error(this.infoAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

            /* -------- ADDING EUNIT AGENT INFO TO EUNITS INFO LIST ------- */
            this.infoAgent.addEUnitAgentInfo();

            /* -------- INITIALIZE EUNIT AGENT BY EUNIT INFO DATA ------- */
            infoAgent.send(aid, initialData, CommunicationHelper.EUNIT_INITIAL_DATA);
        } else {
            block();
        }
    }
}
