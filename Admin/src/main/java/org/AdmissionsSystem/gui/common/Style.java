package org.AdmissionsSystem.gui.common;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class Style {
    public static final Color PRIMARY = new Color(10, 102, 204);
    public static final Color ACCENT = new Color(0, 150, 136);
    public static final Color SIDEBAR_BG = new Color(18, 66, 135);
    public static final Color SIDEBAR_LIGHT = new Color(250, 252, 254);
    public static final Color SELECT_BG = new Color(232, 246, 255);
    public static final Color SURFACE = new Color(250, 251, 253);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BUTTON_FONT = new Font("Arial Unicode MS", Font.PLAIN, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void styleButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(BUTTON_FONT);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
    }

    public static void styleSidebarButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(BUTTON_FONT);
        b.setBackground(new Color(0,0,0,0));
        b.setForeground(new Color(36, 56, 102));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
    }

    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(44);
        JTableHeader h = table.getTableHeader();
        if (h != null) {
            h.setBackground(SURFACE);
            h.setFont(BUTTON_FONT.deriveFont(Font.BOLD));
        }
        // alternate row renderer
        table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
    }

    private static class AlternateRowRenderer extends JLabel implements TableCellRenderer {
        public AlternateRowRenderer() { setOpaque(true); setBorder(BorderFactory.createEmptyBorder(4,6,4,6)); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value==null?"":value.toString());
            setFont(TABLE_FONT);
            if (isSelected) {
                setBackground(PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(row%2==0?Color.WHITE:new Color(245,247,250));
                setForeground(Color.DARK_GRAY);
            }
            return this;
        }
    }

    public static JLabel iconLabel(String emoji) {
        JLabel l = new JLabel(emoji);
        l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        l.setForeground(Color.WHITE);
        return l;
    }
}
