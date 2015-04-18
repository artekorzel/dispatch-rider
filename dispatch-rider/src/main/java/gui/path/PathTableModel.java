package gui.path;

import dtp.simulation.SimInfo;
import gui.common.Updateable;
import xml.elements.SimulationData;

import javax.swing.table.AbstractTableModel;
import java.util.Set;
import java.util.TreeMap;

public class PathTableModel extends AbstractTableModel implements Updateable {

    PathUpdateable updateable = new PathUpdateable();
    int holonID = 0;

    public void setHolon(int val) {
        holonID = val;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getRowCount() {
        if (updateable.visualisedRecord.getData() == null || holonID == -1) return 0;
        //return ((TreeMap<Object,Object>)updateable.visualisedRecord.getData()).size();
        return ((TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>>) updateable.visualisedRecord.getData()).get(holonID).size();
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
        if (updateable.visualisedRecord.getData() == null || holonID == -1) return null;
        @SuppressWarnings("unchecked")
        TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>> extracted = (TreeMap<Integer, TreeMap<Integer, TreeMap<String, Object>>>) updateable.visualisedRecord.getData();
        TreeMap<Integer, TreeMap<String, Object>> forHolon = extracted.get(holonID);
        return forHolon.get(forHolon.keySet().toArray()[rowIndex]).get(updateable.getColumnNames()[columnIndex]);
    }

    @Override
    public void newTimestampUpdate(int val) {
        updateable.newTimestampUpdate(val);

    }

    @Override
    public int getDrawnTimestamp() {
        // TODO Auto-generated method stub
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

    public Set<Integer> getHolonIds() {
        return updateable.getHolonIds();
    }

}
