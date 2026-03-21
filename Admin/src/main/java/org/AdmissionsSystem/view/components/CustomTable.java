package org.AdmissionsSystem.view.components;

import org.AdmissionsSystem.view.common.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class CustomTable extends JScrollPane {
    private JTable table;

    public CustomTable(TableModel model) {
        table = new JTable(model);
        init();
        setViewportView(table);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        getViewport().setBackground(Style.SURFACE);
    }

    private void init() {
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setRowHeight(44);
        table.setFont(Style.TABLE_FONT);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(Color.WHITE);
            header.setForeground(new Color(60,70,90));
            header.setFont(Style.BUTTON_FONT.deriveFont(Font.BOLD));
            header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,230,230)));
        }

        // alternate row colors and padding renderer
        table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
    }

    public JTable getTable() { return table; }

    private static class AlternateRowRenderer extends JLabel implements TableCellRenderer {
        public AlternateRowRenderer() { setOpaque(true); setBorder(BorderFactory.createEmptyBorder(6,8,6,8)); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value==null?"":value.toString());
            setFont(Style.TABLE_FONT);
            if (isSelected) {
                setBackground(Style.PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(row%2==0?Color.WHITE:new Color(247,249,252));
                setForeground(new Color(40,50,70));
            }
            return this;
        }
    }
}
