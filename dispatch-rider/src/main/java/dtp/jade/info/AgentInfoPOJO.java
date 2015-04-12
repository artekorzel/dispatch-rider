package dtp.jade.info;

import jade.core.AID;
import jade.wrapper.AgentController;

/**
 * Represents an info about any type of agent as a POJO objects.
 *
 * @author Grzegorz
 */
public class AgentInfoPOJO {

    private String name;
    private AgentController agentController;
    private AID aid;

    public AgentInfoPOJO() {
        this.name = null;
        this.agentController = null;
        this.aid = null;
    }

    public AgentInfoPOJO(String name, AgentController controller, AID aid) {
        this.name = name;
        this.agentController = controller;
        this.aid = aid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgentController getAgentController() {
        return this.agentController;
    }

    public void setAgentController(AgentController controller) {
        this.agentController = controller;
    }

    public AID getAID() {
        return this.aid;
    }

    public void setAID(AID aid) {
        this.aid = aid;
    }

}
