package gui.parameters;

import dtp.simulation.SimInfo;
import gui.common.Updateable;
import xml.elements.SimmulationData;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

public class ParametersTableModel extends AbstractTableModel implements Updateable {

    /**
     *
     */


    ParametersUpdateable updateable = new ParametersUpdateable();

    @Override
    public int getColumnCount() {
        return updateable.getColumnNames().length;
    }

    public String getColumnName(int col) {
        return updateable.getColumnNames()[col];
    }

    @Override
    public int getRowCount() {
        if (updateable.visualisedRecord.getData() == null) return 0;
        //return ((HashMap<Object,Object>)updateable.visualisedRecord.getData()).size();
        return 1;
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
    public void update(SimmulationData data) {
        updateable.update(data);
    }

    @Override
    public void setSimInfo(SimInfo simInfo) {
    }

    public void update(DRParams params) {
        updateable.update(params);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (updateable.visualisedRecord.getData() == null) return 0;
        @SuppressWarnings("unchecked")
        HashMap<String, Object> extracted = (HashMap<String, Object>) updateable.visualisedRecord.getData();
        return extracted.get(extracted.keySet().toArray()[columnIndex]);
    }

}
