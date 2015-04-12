package algorithm.STLike;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.simmulation.SimInfo;
import jade.core.AID;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Base class to algorithms, which move commissions between holons. Your
 * algorithm have to extends this class and be in algorithm.STLike package
 */
public abstract class ExchangeAlgorithm implements Serializable {

    /**
     * Algorithm parameters. It is assign in configuration file. Each algorithm
     * could have different parameters
     */
    protected Map<String, String> parameters;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * This method is called after commission is assign to unit with AID 'holon'
     */
    public abstract Map<AID, Schedule> doExchangesAfterComAdded(Set<AID> aids,
                                                                Map<AID, Schedule> holons, AID holon, SimInfo info, int timestamp);

    /**
     * This method is called when new commission (com) can't be added to any of
     * existing holons. If this method returns null, then new holon is created.
     */
    public abstract Map<AID, Schedule> doExchangesWhenCantAddCom(Set<AID> aids,
                                                                 Map<AID, Schedule> holons, Commission com, SimInfo info,
                                                                 int timestamp);

}
