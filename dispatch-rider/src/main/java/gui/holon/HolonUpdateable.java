package gui.holon;

import gui.common.TimestampUpdateable;
import xml.elements.SimulationData;

import java.util.HashMap;
import java.util.HashSet;

/*
 * przyk�ad jak si� poslugiwa� TimestampUpdatable
 */
public class HolonUpdateable extends TimestampUpdateable {
    private HashSet<Integer> holonIds = new HashSet<Integer>();
    private String[] columnNames = {
            "HolonID",
            "Creation time",
            "Location",
            "Driver name",
            "Truck name",
            // etc
    };

    public void update(SimulationData data) {
        // inicjujemy obiekt na porzadany typ dla wezla czasowego
        if (newRecord.getData() == null)
            newRecord.setData(new HashMap<Integer, HashMap<String, Object>>());

        // jedziemy
        @SuppressWarnings("unchecked")
        HashMap<Integer, HashMap<String, Object>> extracted
                = (HashMap<Integer, HashMap<String, Object>>) newRecord.getData();

        getHolonIds().add(data.getHolonId());
        HashMap<String, Object> holonParams = (HashMap<String, Object>) extracted.get(data.getHolonId());
        if (holonParams == null) {
            holonParams = new HashMap<String, Object>();
            extracted.put(data.getHolonId(), holonParams);
        }

        holonParams.put(getColumnNames()[0], data.getHolonId());
        holonParams.put(getColumnNames()[1], data.getHolonCreationTime());
        holonParams.put(getColumnNames()[2], "(" + data.getLocation().x + ", " + data.getLocation().y + ")");
        holonParams.put(getColumnNames()[3], data.getDriver().getAID().getLocalName());
        holonParams.put(getColumnNames()[4], data.getTruck().getAID().getLocalName());
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public HashSet<Integer> getHolonIds() {
        return holonIds;
    }
}
