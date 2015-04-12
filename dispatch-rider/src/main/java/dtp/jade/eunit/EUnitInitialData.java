package dtp.jade.eunit;

import dtp.jade.distributor.NewTeamData;
import dtp.simmulation.SimInfo;

import java.io.Serializable;

public class EUnitInitialData implements Serializable {

    private SimInfo simInfo;
    private NewTeamData data;
    private int depot;

    public EUnitInitialData(SimInfo simInfo, NewTeamData data) {
        this.simInfo = simInfo;
        this.data = data;
    }

    public SimInfo getSimInfo() {
        return simInfo;
    }

    public NewTeamData getData() {
        return data;
    }

    /**
     * getter
     *
     * @return the depot
     */
    public int getDepot() {
        return depot;
    }

    /**
     * setter
     *
     * @param depot the depot to set
     */
    public void setDepot(int depot) {
        this.depot = depot;
    }
}
