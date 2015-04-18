package dtp.jade.info;

import jade.core.AID;

/**
 * Represents an info about any type of agent as a POJO objects.
 *
 * @author Grzegorz
 */
public class AgentInfoPOJO {

    private String name;
    private AID aid;

    public AgentInfoPOJO() {
        this.name = null;
        this.aid = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AID getAID() {
        return this.aid;
    }

    public void setAID(AID aid) {
        this.aid = aid;
    }

}
