package org.AdmissionsSystem.gui.main;

import javax.swing.*;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.SearchPanel;

import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {

	public HeaderPanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 72));
		setBackground(Color.WHITE);

		// Header layout: left spacer (sidebar contains logo), centered search, right icons
		setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,230,230)));

		// left: logo area (icon + app name)
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,16));
		leftPanel.setOpaque(false);
		JLabel iconBox = new JLabel("📘");
		iconBox.setOpaque(true);
		iconBox.setBackground(Style.PRIMARY);
		iconBox.setForeground(Color.WHITE);
		iconBox.setPreferredSize(new Dimension(44,44));
		iconBox.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel appLabel = new JLabel("HỆ THỐNG TUYỂN SINH");
		appLabel.setFont(Style.PANEL_TITLE_FONT);
		appLabel.setBorder(BorderFactory.createEmptyBorder(0,6,0,0));
		leftPanel.add(iconBox);
		leftPanel.add(appLabel);

		// Center: global search + per-page search
		JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 12));
		center.setOpaque(false);
		SearchPanel globalSearch = new SearchPanel(560, "Tìm kiếm thí sinh, hồ sơ hoặc mã ngành...", null);
		SearchPanel pageSearch = new SearchPanel(320, "", "Tìm");
		center.add(globalSearch);
		center.add(pageSearch);

		// Right: small icon buttons
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
		right.setOpaque(false);
		JButton bell = new JButton("🔔");
		bell.setPreferredSize(new Dimension(40,40));
		bell.setBackground(new Color(245,246,250));
		bell.setFocusPainted(false);
		bell.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

		JButton gear = new JButton("⚙️");
		gear.setPreferredSize(new Dimension(40,40));
		gear.setBackground(new Color(245,246,250));
		gear.setFocusPainted(false);
		gear.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

		JLabel avatar = new JLabel("LA");
		avatar.setOpaque(true);
		avatar.setBackground(new Color(240,240,250));
		avatar.setPreferredSize(new Dimension(36,36));
		avatar.setHorizontalAlignment(SwingConstants.CENTER);

		right.add(bell);
		right.add(gear);
		right.add(avatar);

		add(leftPanel, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
		this.pageSearch = pageSearch;
		this.globalSearch = globalSearch;
	}

	private SearchPanel pageSearch;
	private SearchPanel globalSearch;

	public void setPageSearchPlaceholder(String placeholder) {
		if (pageSearch != null) pageSearch.setPlaceholder(placeholder);
	}

	public String getPageSearchText() {
		if (pageSearch == null) return "";
		return pageSearch.getSearchText();
	}

	public void addPageSearchListener(ActionListener al) {
		if (pageSearch != null) pageSearch.addActionListener(al);
	}
}

