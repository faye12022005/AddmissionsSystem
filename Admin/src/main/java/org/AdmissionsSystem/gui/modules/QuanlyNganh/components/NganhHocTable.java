package org.AdmissionsSystem.gui.modules.QuanlyNganh.components;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.AdmissionsSystem.gui.components.CustomTable;

public class NganhHocTable extends CustomTable {
    public NganhHocTable(TableModel model) {
        super(model);
        configure();
    }

    private void configure() {
        JTable table = getTable();
        // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // if (table.getColumnModel().getColumnCount() > 1) {
        //     table.getColumnModel().getColumn(1).setMinWidth(180);
        // }    
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
         
        // table.setPreferredScrollableViewportSize(new Dimension(960, 420));
    }
}
