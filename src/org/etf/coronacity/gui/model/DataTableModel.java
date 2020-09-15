package org.etf.coronacity.gui.model;

import javax.swing.table.AbstractTableModel;

/*
 * Model for table
 * Contains public constants which represents number of columns for tables with data connected with
 *      infected, recovered and persons in hospital
 * This class has one constructor which expects two arguments
 *      Object[][] - two dimensional array which contains data
 *      String[] - one dimensional array which contains column names
 * Second dimension of the first argument and size of the second argument
 *      must be the same and must be equals to number of columns
 */
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
