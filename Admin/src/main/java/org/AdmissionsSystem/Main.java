package org.AdmissionsSystem;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.AdmissionsSystem.gui.main.MainFrame;

import com.formdev.flatlaf.FlatLightLaf; // Or FlatDarkLaf for dark mode
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		// 1. Setup FlatLaf BEFORE invokeLater or at the very start of it
		try {
			// This replaces the old UIManager.setLookAndFeel line
			FlatLightLaf.setup();
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}

		SwingUtilities.invokeLater(() -> {
			MainFrame frame = new MainFrame(null, "Demo", "Admin");
			frame.setVisible(true);
			frame.showCard("diem_thisinh");

			JOptionPane.showMessageDialog(
					frame,
					"Demo Quản lý điểm thí sinh:\n"
							+ "- Thêm/Sửa/Xóa điểm\n"
							+ "- Import Excel\n"
							+ "- Thống kê theo loại điểm và môn",
					"Hướng dẫn demo",
					JOptionPane.INFORMATION_MESSAGE);
		});
	}
}
