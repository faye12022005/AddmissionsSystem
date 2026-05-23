package org.AdmissionsSystem.gui.components;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;
import java.awt.event.MouseEvent;

public class CustomTable extends JScrollPane {
    private static final Color HEADER_TEXT = new Color(60, 70, 90);
    private static final Color ROW_TEXT = new Color(40, 50, 70);
    private static final Color EVEN_ROW = Color.WHITE;
    private static final Color ODD_ROW = new Color(247, 249, 252);
    private static final Color SELECT_ROW = new Color(207, 232, 255);
    private static final Color TABLE_BORDER = new Color(220, 228, 238);
    private static final Color HEADER_BORDER = new Color(214, 223, 234);
    private static final Color HEADER_BG = new Color(249, 251, 254);

    private final JTable table;

    public CustomTable(TableModel model) {
        table = new JTable(model);
        init();
        setViewportView(table);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_BORDER, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE),
                        BorderFactory.createEmptyBorder(1,1,1,1))));
        getViewport().setBackground(Style.SURFACE);
        getViewport().setOpaque(true);
    }

    private void init() {
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(new Color(238, 242, 247));
        table.setRowHeight(42);
        table.setFont(Style.TABLE_FONT);
        table.setBackground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECT_ROW);
        table.setSelectionForeground(ROW_TEXT);
        table.setAutoCreateRowSorter(true);
        table.setRowMargin(0);

        JTableHeader header = new JTableHeader(table.getColumnModel()) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int viewColumn = columnAtPoint(e.getPoint());
                if (viewColumn < 0) {
                    return null;
                }
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                String columnName = table.getModel().getColumnName(modelColumn);
                return (columnName == null || columnName.isBlank()) ? null : columnName;
            }
        };
        table.setTableHeader(header);
        if (header != null) {
            header.setBackground(HEADER_BG);
            header.setForeground(HEADER_TEXT);
            header.setFont(Style.BUTTON_FONT.deriveFont(Font.BOLD));
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, HEADER_BORDER),
                    BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE)));
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
            header.setReorderingAllowed(false);
        }

        table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
    }

    public JTable getTable() {
        return table;
    }

    private class AlternateRowRenderer extends JLabel implements TableCellRenderer {
        public AlternateRowRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setFont(Style.TABLE_FONT);

            if (isSelected) {
                setBackground(SELECT_ROW);
                setForeground(ROW_TEXT);
            } else {
                setBackground(row % 2 == 0 ? EVEN_ROW : ODD_ROW);
                setForeground(ROW_TEXT);
            }

            return this;
        }
    }
}
