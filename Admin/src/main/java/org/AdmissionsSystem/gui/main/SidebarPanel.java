package org.AdmissionsSystem.gui.main;

import javax.swing.*;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class SidebarPanel extends JPanel {
	public SidebarPanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(220,0));
		setBackground(Color.WHITE);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(true);
		wrapper.setBackground(Color.WHITE);
		wrapper.setBorder(BorderFactory.createMatteBorder(0,0,0,1,new Color(230,230,230)));

		JPanel titleBox = new JPanel(new GridLayout(2,1));
		titleBox.setOpaque(false);
		JLabel name = new JLabel("HỆ THỐNG TUYỂN SINH");
		name.setFont(new Font("Segoe UI", Font.BOLD, 13));
		JLabel sub = new JLabel("QUẢN TRỊ VIÊN");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		sub.setForeground(new Color(110,120,140));
		titleBox.add(name);
		titleBox.add(sub);


		// menu
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		menu.setOpaque(false);
		menu.setBorder(BorderFactory.createEmptyBorder(12,8,12,8));

		String[] items = {"Tổng quan","Người dùng","Thí sinh","Ngành học","Môn học","Điểm số","Nguyện vọng","Bảng quy đổi"};
		String[] icons = {"🏠","👥","🧾","📚","📘","🧮","⭐","🔁"};
		for (int i=0;i<items.length;i++) {
			JButton b = new JButton(icons[i]+"  "+items[i]);
			b.setAlignmentX(Component.LEFT_ALIGNMENT);
			b.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
			Style.styleSidebarButton(b);
			if (i==0) {
				b.setBackground(Style.SELECT_BG);
				b.setOpaque(true);
			}
			menu.add(b);
			menu.add(Box.createRigidArea(new Dimension(0,6)));
		}

		// bottom profile
		JPanel profile = new JPanel(new FlowLayout(FlowLayout.LEFT,12,12));
		profile.setOpaque(false);
		JLabel avatar = new JLabel("LA");
		avatar.setOpaque(true);
		avatar.setBackground(new Color(240,240,250));
		avatar.setPreferredSize(new Dimension(44,44));
		avatar.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel userBox = new JPanel(new GridLayout(2,1));
		userBox.setOpaque(false);
		JLabel userName = new JLabel("Lê Minh Anh");
		userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel roleLabel = new JLabel("Admin");
		roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		roleLabel.setForeground(new Color(120,130,150));
		userBox.add(userName);
		userBox.add(roleLabel);

		profile.add(avatar);
		profile.add(userBox);

		wrapper.add(menu, BorderLayout.CENTER);
		wrapper.add(profile, BorderLayout.SOUTH);

		add(wrapper, BorderLayout.CENTER);
	}
}


