package dtp.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.log4j.Logger;

public class AgentsService {

    private static Logger logger = Logger.getLogger(AgentsService.class);

    public static AID[] findAgentByServiceName(Agent agent, String serviceName) {

        DFAgentDescription[] descriptions = null;
        AID[] aids;

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        template.addServices(sd);
        SearchConstraints constraints = new SearchConstraints();
        constraints.setMaxResults(1000000L);

        try {
            descriptions = DFService.search(agent, template, constraints);
        } catch (FIPAException fe) {
            logger.error(fe);
        }

        aids = new AID[descriptions.length];
        for (int i = 0; i < descriptions.length; i++) {
            aids[i] = descriptions[i].getName();
        }

        return aids;
    }

}
