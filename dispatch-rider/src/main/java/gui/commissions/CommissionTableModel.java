package gui.commissions;

import dtp.simulation.SimInfo;
import gui.common.Updateable;
import xml.elements.SimulationData;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

public class CommissionTableModel extends AbstractTableModel implements Updateable {


    CommisionUpdateable updateable = new CommisionUpdateable();

    @Override
    public int getColumnCount() {
        return updateable.getColumnNames().length;
    }

    public String getColumnName(int col) {
        return updateable.getColumnNames()[col];
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getRowCount() {
        if (updateable.visualisedRecord.getData() == null) return 0;
        return ((HashMap<Object, Object>) updateable.visualisedRecord.getData()).size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (updateable.visualisedRecord.getData() == null) return 0;
        @SuppressWarnings("unchecked")
        HashMap<Integer, HashMap<String, Object>> extracted = (HashMap<Integer, HashMap<String, Object>>) updateable.visualisedRecord.getData();
        return extracted.get(extracted.keySet().toArray()[rowIndex]).get(updateable.getColumnNames()[columnIndex]);
    }

    @Override
    public void newTimestampUpdate(int val) {
        updateable.newTimestampUpdate(val);
    }

    @Override
    public int getDrawnTimestamp() {
        return updateable.getDrawnTimestamp();
    }

    @Override
    public void setDrawnTimestamp(int val) {
        updateable.setDrawnTimestamp(val);
    }

    @Override
    public void update(SimulationData data) {
        updateable.update(data);

    }

    @Override
    public void setSimInfo(SimInfo simInfo) {
        // TODO Auto-generated method stub

    }
}
