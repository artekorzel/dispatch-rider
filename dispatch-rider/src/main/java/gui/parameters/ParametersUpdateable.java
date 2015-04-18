package gui.parameters;

import gui.common.TimestampUpdateable;
import xml.elements.SimulationData;

import java.util.HashMap;

public class ParametersUpdateable extends TimestampUpdateable {
    private String[] columnNames = {
            "timestamp",
            "choosingByCost",
            "commissionSendingType",
            "dist",
            "simulatedTradingCount",
            "chooseWorstCommission",
            "algorithm",
            "maxFullSTDepth",
            "STTimestampGap",
            "STCommissionsGap",

            // etc
    };

    public void update(SimulationData data) {
        // inicjujemy obiekt na porzadany typ dla wezla czasowego
        if (newRecord.getData() == null)
            newRecord.setData(new HashMap<String, Object>());

        // jedziemy
//        HashMap<String,Object> extracted
//            = (HashMap<String, Object>) newRecord.getData();
    }

    public void update(DRParams params) {
        // inicjujemy obiekt na porzadany typ dla wezla czasowego
        if (newRecord.getData() == null && newRecord.getTimestamp() == 0)
            newRecord.setData(new HashMap<String, Object>());
        else if (newRecord.getData() == null) {
            newRecord.setData(records.get(0).getData());
        }

        // jedziemy
        @SuppressWarnings("unchecked")
        HashMap<String, Object> extracted
                = (HashMap<String, Object>) newRecord.getData();

        extracted.put(columnNames[0], 0);
        extracted.put(columnNames[1], params.isChoosingByCost());
        extracted.put(columnNames[2], params.isCommissionSendingType());
        extracted.put(columnNames[3], params.isDist());
        extracted.put(columnNames[4], params.getSimulatedTradingCount());
        extracted.put(columnNames[5], params.getChooseWorstCommission());
        extracted.put(columnNames[6], params.getAlgorithm());
        extracted.put(columnNames[7], params.getMaxFullSTDepth());
        extracted.put(columnNames[8], params.getSTTimestampGap());
        extracted.put(columnNames[9], params.getSTCommissionsionsGap());
    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
