package gui.holonstats;

import gui.common.TimestampUpdateable;
import xml.elements.SimmulationData;

import java.util.HashMap;

public class HolonStatsUpdateable extends TimestampUpdateable {
    private String[] columnNames = {
            "HolonID",
            "Summary Cost",
            "Cost",
            "Drive Time",
            "Summary Punishment",
            "Summary Time",
            "Wait Time"
    };

    @Override
    public void update(SimmulationData data) {
        // inicjujemy obiekt na porzadany typ dla wezla czasowego
        if (newRecord.getData() == null)
            newRecord.setData(new HashMap<Integer, HashMap<String, Object>>());

        // jedziemy
        @SuppressWarnings("unchecked")
        HashMap<Integer, HashMap<String, Object>> extracted
                = (HashMap<Integer, HashMap<String, Object>>) newRecord.getData();

        HashMap<String, Object> holonParams = (HashMap<String, Object>) extracted.get(data.getHolonId());
        if (holonParams == null) {
            holonParams = new HashMap<String, Object>();
            extracted.put(data.getHolonId(), holonParams);
        }

        holonParams.put(getColumnNames()[0], data.getHolonId());
        holonParams.put(getColumnNames()[1], data.getSchedule().calculateSummaryCost(simInfo));
        holonParams.put(getColumnNames()[2], data.getSchedule().calculateCost(simInfo));
        holonParams.put(getColumnNames()[3], data.getSchedule().calculateDriveTime(simInfo));
        holonParams.put(getColumnNames()[4], data.getSchedule().calculateSummaryPunishment(simInfo));
        holonParams.put(getColumnNames()[5], data.getSchedule().calculateTime(simInfo.getDepot()));
        holonParams.put(getColumnNames()[6], data.getSchedule().calculateWaitTime(simInfo.getDepot()));
    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
