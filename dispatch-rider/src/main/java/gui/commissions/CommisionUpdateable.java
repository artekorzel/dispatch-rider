package gui.commissions;

import dtp.commission.Commission;
import gui.common.TimestampUpdateable;
import xml.elements.SimulationData;

import java.util.HashMap;

public class CommisionUpdateable extends TimestampUpdateable {
    private String[] columnNames = {
            "CommisionID",
            "Holon ID",
            "Pickup ID",
            "Pickup X",
            "Pickup Y",
            "Pickup Time 1",
            "Pickup Time 2",
            "PickUpServiceTime",
            "Delivery ID",
            "Delivery X",
            "Delivery Y",
            "Delivery Time 1",
            "Delivery Time 2",
            "DeliveryServiceTime",
            "Load",
            "ActualLoad"
            // etc
    };

    @Override
    public void update(SimulationData data) {
        // inicjujemy obiekt na porzadany typ dla wezla czasowego
        if (newRecord.getData() == null)
            newRecord.setData(new HashMap<Integer, HashMap<String, Object>>());

        // jedziemy
        @SuppressWarnings("unchecked")
        HashMap<Integer, HashMap<String, Object>> extracted
                = (HashMap<Integer, HashMap<String, Object>>) newRecord.getData();


        for (Commission c : data.getSchedule().getCommissions()) {
            HashMap<String, Object> vars = extracted.get(c.getID());
            if (vars == null) {
                vars = new HashMap<String, Object>();
                extracted.put(c.getID(), vars);
            }

            vars.put(columnNames[0], c.getID());
            vars.put(columnNames[1], data.getHolonId());
            vars.put(columnNames[2], c.getPickUpId());
            vars.put(columnNames[3], c.getPickupX());
            vars.put(columnNames[4], c.getPickupY());
            vars.put(columnNames[5], c.getPickupTime1());
            vars.put(columnNames[6], c.getPickupTime2());
            vars.put(columnNames[7], c.getPickUpServiceTime());
            vars.put(columnNames[8], c.getDeliveryId());
            vars.put(columnNames[9], c.getDeliveryX());
            vars.put(columnNames[10], c.getDeliveryY());
            vars.put(columnNames[11], c.getDeliveryTime1());
            vars.put(columnNames[12], c.getDeliveryTime2());
            vars.put(columnNames[13], c.getDeliveryServiceTime());
            vars.put(columnNames[14], c.getLoad());
            vars.put(columnNames[15], c.getActualLoad());
        }

    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
