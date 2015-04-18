package gui.holon;


import dtp.simulation.SimInfo;
import gui.common.Updateable;
import xml.elements.SimulationData;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

public class HolonTableModel extends AbstractTableModel implements Updateable {

    HolonUpdateable updateable = new HolonUpdateable();

    @SuppressWarnings("unchecked")
    @Override
    public int getRowCount() {
        if (updateable.visualisedRecord.getData() == null) return 0;
        return ((HashMap<Object, Object>) updateable.visualisedRecord.getData()).size();
    }

    @Override
    public int getColumnCount() {
        return updateable.getColumnNames().length;
    }

    public String getColumnName(int col) {
        return updateable.getColumnNames()[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (updateable.visualisedRecord.getData() == null) return 0;
        @SuppressWarnings("unchecked")
        HashMap<Integer, HashMap<String, Object>> extracted = (HashMap<Integer, HashMap<String, Object>>) updateable.visualisedRecord.getData();
        return extracted.get(updateable.getHolonIds().toArray()[rowIndex]).get(updateable.getColumnNames()[columnIndex]);
    }

    public void setSimInfo(SimInfo simInfo) {
        updateable.setSimInfo(simInfo);
    }

    /**
     * Do aktualizowania danych w tabelach
     *
     */
    public void update(SimulationData data) {
        updateable.update(data);
    }

    /**
     * Wskazuje ze update'y przychodza juz na nowy timestamp
     *
     */
    public void newTimestampUpdate(int val) {
        updateable.newTimestampUpdate(val);
    }

    public int getDrawnTimestamp() {
        return updateable.getDrawnTimestamp();
    }

    /**
     * Wyswietla dane z wybranego timestamp
     */
    public void setDrawnTimestamp(int val) {
        updateable.setDrawnTimestamp(val);
    }
}
