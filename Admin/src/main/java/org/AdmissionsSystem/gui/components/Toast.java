package org.AdmissionsSystem.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;

public final class Toast {
	private Toast() {
	}

	public static void showToast(Component parent, String message, boolean isError) {
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		if (owner == null) {
			return;
		}

		JWindow toast = new JWindow(owner);
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		label.setOpaque(true);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		label.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
		label.setBackground(isError ? new Color(180, 35, 24) : new Color(20, 130, 76));

		toast.add(label);
		toast.pack();

		Point ownerOnScreen = owner.getLocationOnScreen();
		int x = ownerOnScreen.x + owner.getWidth() - toast.getWidth() - 24;
		int y = ownerOnScreen.y + owner.getHeight() - toast.getHeight() - 36;
		toast.setLocation(x, y);
		toast.setAlwaysOnTop(true);
		toast.setVisible(true);

		Timer timer = new Timer(2200, e -> toast.dispose());
		timer.setRepeats(false);
		timer.start();
	}
}
