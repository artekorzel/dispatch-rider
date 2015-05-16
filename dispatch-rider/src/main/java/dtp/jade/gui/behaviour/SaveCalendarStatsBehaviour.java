package dtp.jade.gui.behaviour;

import dtp.jade.MessageType;
import dtp.jade.distributor.NewTeamData;
import dtp.jade.gui.GUIAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;
import xml.elements.SimulationData;

import java.util.List;
import java.util.TreeMap;

public class SaveCalendarStatsBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(SaveCalendarStatsBehaviour.class);

    private GUIAgent guiAgent;

    public SaveCalendarStatsBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.STATS_DATA.name());
        ACLMessage msg = myAgent.receive(template);


        if (msg != null) {
            try {
                Object[] statsData = (Object[]) msg.getContentObject();
                String commissionsCount = statsData[0].toString();
                NewTeamData[] undeliveredCommissions = (NewTeamData[]) statsData[1];
                TreeMap<Integer, List<SimulationData>> simulationData = (TreeMap<Integer, List<SimulationData>>) statsData[2];
                guiAgent.performStatsSave(commissionsCount, undeliveredCommissions, simulationData);
            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException ", e);
            }
        } else {
            block();
        }
    }
}