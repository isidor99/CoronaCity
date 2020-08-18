package org.etf.coronacity.gui.model;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {

    private Object[][] data;
    private String[] columnNames;

    public static final int INFECTED_COLUMNS = 9;
    public static final int RECOVERED_COLUMNS = 9;
    public static final int HOSPITAL_PERSON_COLUMNS = 9;

    public DataTableModel(Object[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
