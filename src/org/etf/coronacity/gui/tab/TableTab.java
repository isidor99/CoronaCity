package org.etf.coronacity.gui.tab;

import org.etf.coronacity.gui.model.DataTableModel;
import org.etf.coronacity.helper.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class TableTab extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(TableTab.class.getName());

    private JTable dataTable;
    private DataTableModel dataTableModel;

    public TableTab(DataTableModel dataTableModel) {

        this.dataTableModel = dataTableModel;

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);

        initComponents();
    }

    public void exportToCsv(String filename) {

        try {

            FileWriter csvWriter = new FileWriter(new File(filename));

            for (int i = 0; i < dataTable.getColumnCount(); i++)
                csvWriter.write(dataTable.getColumnName(i) + ",");

            csvWriter.write("\n");

            for (int i = 0; i < dataTable.getRowCount(); i++) {

                for (int j = 0; j < dataTable.getColumnCount(); j++)
                    csvWriter.write(dataTable.getValueAt(i, j).toString() + ",");

                csvWriter.write("\n");
            }

            csvWriter.flush();
            csvWriter.close();

        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }

    //
    // private
    //
    private void initComponents() {

        setLayout(new GridLayout(1, 1));

        dataTable = new JTable(dataTableModel) {
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        Iterator<TableColumn> iterator = dataTable.getColumnModel().getColumns().asIterator();
        while (iterator.hasNext())
            iterator.next().setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataTable.setFillsViewportHeight(true);

        add(scrollPane);
    }
}