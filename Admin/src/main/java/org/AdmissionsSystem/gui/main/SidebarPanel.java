package org.AdmissionsSystem.gui.main;

import javax.swing.*;
import org.AdmissionsSystem.gui.components.SidebarMenuButton;
import org.AdmissionsSystem.gui.components.SidebarMenuButton.MenuState;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
	private SidebarMenuButton selectedButton;
	private final String displayName;
	private final String role;
	private final Runnable logoutAction;

	public SidebarPanel() {
		this("Lê Minh Anh", "Admin", () -> {});
	}

	public SidebarPanel(String displayName, String role, Runnable logoutAction) {
		this.displayName = safeText(displayName, "Người dùng");
		this.role = safeText(role, "User");
		this.logoutAction = logoutAction;
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(220, 0));
		setBackground(Color.WHITE);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(true);
		wrapper.setBackground(Color.WHITE);
		wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(203, 213, 225))); // Visible slate-300 divider

		JPanel titleBox = new JPanel(new BorderLayout());
		titleBox.setOpaque(false);
		titleBox.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
		JLabel logo = new JLabel();
		logo.setOpaque(true);
		logo.setBackground(new Color(10, 102, 204));
		logo.setForeground(Color.WHITE);
		logo.setPreferredSize(new Dimension(50, 50));
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		Icon logoIcon = loadPngIcon("/icons/image.png", 49, 49);
		if (logoIcon != null) {
			logo.setIcon(logoIcon);
		} else {
			logo.setText("📘");
		}

		JPanel nameBox = new JPanel(new GridLayout(2, 1));
		nameBox.setOpaque(false);
		JLabel name = new JLabel("TUYỂN SINH SGU");
		name.setFont(new Font("Segoe UI", Font.BOLD, 13));
		name.setForeground(new Color(10, 102, 204));
		JLabel sub = new JLabel("QUẢN TRỊ VIÊN");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		sub.setForeground(new Color(110, 120, 140));
		nameBox.add(name);
		nameBox.add(sub);

		JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		brandRow.setOpaque(false);
		brandRow.add(logo);
		brandRow.add(nameBox);
		titleBox.add(brandRow, BorderLayout.CENTER);

		// menu
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		menu.setOpaque(false);
		menu.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
		List<SidebarMenuButton> menuButtons = new ArrayList<>();

		String[] items = {"Tổng quan","Người dùng","Thí sinh","Ngành học","Tổ hợp","Ngành-Tổ hợp","Điểm thi","Điểm cộng","Nguyện vọng","Bảng quy đổi"};
		String[] icons = {"📊", "👥", "👨‍🎓", "📚", "📋", "📋", "📝", "➕", "📋", "🔄"};
		boolean isAdmin = "admin".equalsIgnoreCase(this.role);
		for (int i = 0; i < items.length; i++) {
			// Ẩn menu "Người dùng" nếu không phải admin
			if (i == 1 && !isAdmin) {
				continue;
			}
			SidebarMenuButton b = new SidebarMenuButton(icons[i] + "  " + items[i]);
			b.setAlignmentX(Component.LEFT_ALIGNMENT);
			b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
			b.setMenuState(MenuState.INACTIVE);
			final SidebarMenuButton current = b;
			b.addActionListener(e -> setActiveButton(current, menuButtons));
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if (current != selectedButton) {
						current.setMenuState(MenuState.HOVER);
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (current != selectedButton) {
						current.setMenuState(MenuState.INACTIVE);
					}
				}
			});
			menuButtons.add(b);
			menu.add(b);
			menu.add(Box.createRigidArea(new Dimension(0, 6)));
		}
		if (!menuButtons.isEmpty()) {
			int defaultIndex = 0;
			setActiveButton(menuButtons.get(defaultIndex), menuButtons);
		}

		// bottom profile
		JPanel profile = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
		profile.setOpaque(false);

		JPanel userBox = new JPanel(new GridLayout(2, 1));
		userBox.setOpaque(false);
		JLabel userName = new JLabel(this.displayName);
		userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel roleLabel = new JLabel(this.role);
		roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		roleLabel.setForeground(new Color(120, 130, 150));
		userBox.add(userName);
		userBox.add(roleLabel);
		profile.add(userBox);

		// Bottom container for profile + logout
		JPanel bottomContainer = new JPanel();
		bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
		bottomContainer.setOpaque(false);
		// Add top border as a horizontal separator
		bottomContainer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
			BorderFactory.createEmptyBorder(15, 0, 15, 0)
		));

		// Logout button
		SidebarMenuButton btnLogout = new SidebarMenuButton("Đăng xuất");
		btnLogout.setForeground(new Color(220, 38, 38)); 
		btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		
		btnLogout.addActionListener(e -> {
			if (this.logoutAction != null) {
				this.logoutAction.run();
			}
		});

		btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btnLogout.setMenuState(org.AdmissionsSystem.gui.components.SidebarMenuButton.MenuState.HOVER);
				btnLogout.setForeground(new Color(220, 38, 38)); // Keep it red
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btnLogout.setMenuState(org.AdmissionsSystem.gui.components.SidebarMenuButton.MenuState.INACTIVE);
				btnLogout.setForeground(new Color(220, 38, 38)); // Keep it red
			}
		});

		bottomContainer.add(profile);
		bottomContainer.add(Box.createRigidArea(new Dimension(0, 4)));
		JPanel logoutWrapper = new JPanel(new BorderLayout());
		logoutWrapper.setOpaque(false);
		logoutWrapper.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		logoutWrapper.add(btnLogout, BorderLayout.CENTER);
		bottomContainer.add(logoutWrapper);

		wrapper.add(titleBox, BorderLayout.NORTH);
		wrapper.add(menu, BorderLayout.CENTER);
		wrapper.add(bottomContainer, BorderLayout.SOUTH);
		add(wrapper, BorderLayout.CENTER);
	}

	private void setActiveButton(SidebarMenuButton active, List<SidebarMenuButton> buttons) {
		for (SidebarMenuButton button : buttons) {
			if (button == active) {
				button.setMenuState(MenuState.ACTIVE);
			} else {
				button.setMenuState(MenuState.INACTIVE);
			}
		}
		selectedButton = active;
	}

	private Icon loadPngIcon(String resourcePath, int width, int height) {
		try {
			java.net.URL url = SidebarPanel.class.getResource(resourcePath);
			if (url == null) {
				return null;
			}
			Image image = new ImageIcon(url).getImage();
			Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);
		} catch (Exception ex) {
			return null;
		}
	}

	private String safeText(String value, String fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}
		return value.trim();
	}
}
