package algorithm.STLike;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.simulation.SimInfo;
import jade.core.AID;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SimulatedTrading extends ExchangeAlgorithm {

    private String chooseWorstCommission = "time";
    private int maxFullSTDepth = 1;
    private boolean firstComplexSTResultOnly = true;

    @Override
    public Map<AID, Schedule> doExchangesAfterComAdded(Set<AID> aids,
                                                       Map<AID, Schedule> holons, AID holon, SimInfo info, int timestamp) {

        if (parameters.containsKey("chooseWorstCommission"))
            chooseWorstCommission = parameters.get("chooseWorstCommission");

        return algorithm.simulatedTrading.SimulatedTrading.fullSimulatedTrading(holons, holon, 1,
                info, new HashSet<Integer>(), chooseWorstCommission, timestamp);
    }

    @Override
    public Map<AID, Schedule> doExchangesWhenCantAddCom(Set<AID> aids,
                                                        Map<AID, Schedule> holons, Commission com, SimInfo info,
                                                        int timestamp) {

        if (parameters.containsKey("maxFullSTDepth"))
            maxFullSTDepth = Integer.parseInt(parameters.get("maxFullSTDepth"));
        if (parameters.containsKey("firstComplexSTResultOnly"))
            firstComplexSTResultOnly = Boolean.parseBoolean(parameters
                    .get("firstComplexSTResultOnly"));

        return algorithm.simulatedTrading.SimulatedTrading.complexSimmulatedTrading(aids, holons, com,
                maxFullSTDepth, new TreeSet<Integer>(), timestamp, info,
                firstComplexSTResultOnly);
    }

}
