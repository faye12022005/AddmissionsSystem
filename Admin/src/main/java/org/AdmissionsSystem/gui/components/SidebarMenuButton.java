package org.AdmissionsSystem.gui.components;

import org.AdmissionsSystem.gui.common.Style;

import javax.swing.*;
import java.awt.*;

public class SidebarMenuButton extends JButton {
    public enum MenuState {
        INACTIVE,
        HOVER,
        ACTIVE
    }

    private static final int ARC = 14;
    private static final Color HOVER_BG = new Color(224, 242, 255);
    private static final Color ACTIVE_BG = new Color(196, 232, 255);
    private static final Color ACTIVE_TEXT = new Color(10, 102, 204);
    private static final Color INACTIVE_BG = Color.WHITE;
    private static final Color INACTIVE_TEXT = new Color(36, 56, 102);

    public SidebarMenuButton(String text) {
        super(text);
        setFocusPainted(false);
        setFont(Style.BUTTON_FONT);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMenuState(MenuState.INACTIVE);
    }

    public void setMenuState(MenuState state) {
        switch (state) {
            case ACTIVE:
                setBackground(ACTIVE_BG);
                setForeground(ACTIVE_TEXT);
                break;
            case HOVER:
                setBackground(HOVER_BG);
                setForeground(INACTIVE_TEXT);
                break;
            default:
                setBackground(INACTIVE_BG);
                setForeground(INACTIVE_TEXT);
                break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.dispose();
        super.paintComponent(g);
    }
}
