package gui.path;

import gui.common.TimestampUpdateable;
import xml.elements.SimulationData;

import java.util.Set;
import java.util.TreeMap;

public class PathUpdateable extends TimestampUpdateable {
    private String[] columnNames = {
            "timestamp",
            "status",
            "holon location",
            "commision ID",
            "pickup location",
            "delivery location",
            "pickup time 1",
            "pickup time 2",
            "delivery time 1",
            "delivery time 2",
            "pickup id",
            "delivery id",
            "actual load",
            "load",
            // etc
    };

    private String getStatusString(SimulationData data) {
        if (data.getLocation().x == data.getSchedule().getCurrentCommission().getPickupX()
                && data.getLocation().y == data.getSchedule().getCurrentCommission().getPickupY()
                && data.getLocation().x == data.getSchedule().getCurrentCommission().getDeliveryX()
                && data.getLocation().y == data.getSchedule().getCurrentCommission().getDeliveryY())
            return "PICKUP/DELIVERY";
        if (data.getLocation().x == data.getSchedule().getCurrentCommission().getPickupX()
                && data.getLocation().y == data.getSchedule().getCurrentCommission().getPickupY())
            return "PICKUP";
        if (data.getLocation().x == data.getSchedule().getCurrentCommission().getDeliveryX()
                && data.getLocation().y == data.getSchedule().getCurrentCommission().getDeliveryY())
            return "DELIVERY";
        return "EN ROUTE";
    }

    @Override
    public void update(SimulationData data) {
        if (newRecord.getData() == null) {
            TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>> young = new TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>>();
            newRecord.setData(young);
            if (records.size() > 1 && records.get(records.size() - 2).getData() != null) {
                @SuppressWarnings("unchecked")
                TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>> old = (TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>>) records.get(records.size() - 2).getData();
                for (int k1 : old.keySet()) {
                    young.put(k1, new TreeMap<Integer, TreeMap<String, Object>>());
                    for (int k2 : old.get(k1).keySet()) {
                        young.get(k1).put(k2, new TreeMap<String, Object>());
                        young.get(k1).get(k2).putAll(old.get(k1).get(k2));
                    }
                }
            }
            //System.err.println(newRecord.getTimestamp() + " " + young.size());
        }

        @SuppressWarnings("unchecked")
        TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>> extracted = (TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>>) newRecord.getData();

        TreeMap<Integer, TreeMap<String, Object>> holonData = (TreeMap<Integer, TreeMap<String, Object>>) extracted.get(data.getHolonId());
        if (holonData == null) {
            holonData = new TreeMap<Integer, TreeMap<String, Object>>();
            extracted.put(data.getHolonId(), holonData);
        }

        TreeMap<String, Object> params = holonData.get(newTimestamp);
        if (params == null) {
            params = new TreeMap<String, Object>();
            holonData.put(newTimestamp, params);
        }

        params.put(columnNames[0], newTimestamp);
        params.put(columnNames[1], getStatusString(data));
        params.put(columnNames[2], "(" + data.getLocation().x + ", " + data.getLocation().y + ")");
        params.put(columnNames[3], data.getSchedule().getCurrentCommission().getID());
        params.put(columnNames[4], "(" + data.getSchedule().getCurrentCommission().getPickupX() + ", " + data.getSchedule().getCurrentCommission().getPickupY() + ")");
        params.put(columnNames[5], "(" + data.getSchedule().getCurrentCommission().getDeliveryX() + ", " + data.getSchedule().getCurrentCommission().getDeliveryY() + ")");
        params.put(columnNames[6], data.getSchedule().getCurrentCommission().getPickupTime1());
        params.put(columnNames[7], data.getSchedule().getCurrentCommission().getPickupTime2());
        params.put(columnNames[8], data.getSchedule().getCurrentCommission().getDeliveryTime1());
        params.put(columnNames[9], data.getSchedule().getCurrentCommission().getDeliveryTime2());
        params.put(columnNames[10], data.getSchedule().getCurrentCommission().getDeliveryId());
        params.put(columnNames[11], data.getSchedule().getCurrentCommission().getPickUpId());
        params.put(columnNames[12], data.getSchedule().getCurrentCommission().getActualLoad());
        params.put(columnNames[13], data.getSchedule().getCurrentCommission().getLoad());

        // holon id -> timestamps so far -> string -> val
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getHolonIds() {
        if (visualisedRecord.getData() == null) return null;
        return ((TreeMap<Integer, Object>) visualisedRecord.getData()).keySet();
    }
}
