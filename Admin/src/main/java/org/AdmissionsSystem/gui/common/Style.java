package org.AdmissionsSystem.gui.common;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Style {
    private static final String HOVER_STYLE_INSTALLED_KEY = "style.hover.installed";

    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color ACCENT = new Color(20, 184, 166);
    public static final Color PRIMARY_SOFT = new Color(239, 246, 255);
    public static final Color BORDER_SOFT = new Color(214, 223, 236);
    public static final Color FUNCTION_BG = new Color(59, 130, 246);
    public static final Color FUNCTION_BORDER = new Color(37, 99, 235);
    public static final Color BTN_ADD = new Color(22, 163, 74);
    public static final Color BTN_UPDATE = new Color(59, 130, 246);
    public static final Color BTN_DELETE = new Color(239, 68, 68);
    public static final Color BTN_CLEAR = new Color(100, 116, 139);
    public static final Color BTN_IMPORT = new Color(6, 182, 212);
    public static final Color BTN_EXPORT = new Color(99, 102, 241);
    public static final Color BTN_FILTER_RESET = new Color(71, 85, 105);
    public static final Color PAGINATION_TEXT = new Color(51, 65, 85);
    public static final Color SIDEBAR_BG = new Color(30, 64, 175);
    public static final Color SIDEBAR_LIGHT = new Color(248, 250, 252);
    public static final Color SELECT_BG = new Color(219, 234, 254);
    public static final Color SURFACE = new Color(248, 250, 252);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BUTTON_FONT = new Font("Arial Unicode MS", Font.PLAIN, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void styleButton(JButton b) {
        applyButtonBase(b);
        b.setFont(BUTTON_FONT.deriveFont(Font.BOLD));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(darken(PRIMARY, 0.18f), 1, true),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        installHoverStyle(b, PRIMARY, darken(PRIMARY, 0.08f), Color.WHITE, Color.WHITE);
    }


    public static void styleFunctionButton(JButton b) {
        styleFunctionButton(b, FUNCTION_BG, Color.WHITE);
    }

    public static void styleFunctionButton(JButton b, Color backgroundColor) {
        styleFunctionButton(b, backgroundColor, Color.WHITE);
    }

    public static void styleFunctionButton(JButton b, Color backgroundColor, Color textColor) {
        applyButtonBase(b);
        b.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        b.setBackground(backgroundColor);
        b.setForeground(textColor);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(darken(backgroundColor, 0.18f), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.setMargin(new Insets(0, 0, 0, 0));
        Dimension pref = b.getPreferredSize();
        b.setPreferredSize(new Dimension(pref.width + 2, 30));
        installHoverStyle(b, backgroundColor, darken(backgroundColor, 0.08f), textColor, textColor);
    }

    public static void stylePaginationButton(JButton b) {
        applyButtonBase(b);
        b.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 12.5f));
        b.setBackground(Color.WHITE);
        b.setForeground(PAGINATION_TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SOFT, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        b.setMargin(new Insets(0, 0, 0, 0));
        Dimension pref = b.getPreferredSize();
        b.setPreferredSize(new Dimension(Math.max(30, pref.width + 3), 33));
        installHoverStyle(b, Color.WHITE, PRIMARY_SOFT, PAGINATION_TEXT, PRIMARY);
    }

    public static void stylePaginationInfoLabel(JLabel label) {
        label.setFont(TABLE_FONT.deriveFont(Font.BOLD));
        label.setForeground(new Color(70, 84, 104));
    }

    public static void stylePaginationCombo(JComboBox<?> comboBox) {
        comboBox.setFont(BUTTON_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_SOFT));
    }

    public static void styleSidebarButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(BUTTON_FONT);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setForeground(new Color(36, 56, 102));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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

    private static void applyButtonBase(JButton b) {
        // Force a stable button UI so platform LAF does not repaint gray background.
        b.setUI(new BasicButtonUI());
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static void installHoverStyle(JButton b,
                                          Color normalBg,
                                          Color hoverBg,
                                          Color normalFg,
                                          Color hoverFg) {
        b.setBackground(normalBg);
        b.setForeground(normalFg);

        if (Boolean.TRUE.equals(b.getClientProperty(HOVER_STYLE_INSTALLED_KEY))) {
            return;
        }

        b.putClientProperty(HOVER_STYLE_INSTALLED_KEY, true);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!b.isEnabled()) {
                    return;
                }
                b.setBackground(hoverBg);
                b.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(normalBg);
                b.setForeground(normalFg);
            }
        });
    }

    private static Color darken(Color color, float factor) {
        int r = Math.max(0, Math.round(color.getRed() * (1 - factor)));
        int g = Math.max(0, Math.round(color.getGreen() * (1 - factor)));
        int b = Math.max(0, Math.round(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }


}
