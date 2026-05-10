package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.controller.QuanLiDiemController;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.SearchPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

public class QuanLyDiemTabPanel extends JPanel {
	private final DiemThptDgnlPanel thptPanel;
	private final DiemVsatPanel vsatPanel;
	private final JTabbedPane subTabs = new JTabbedPane();
	private final SearchPanel searchPanel = new SearchPanel(360, "Tìm theo CCCD, SBD, họ tên", "Tìm");
	private String currentSearchText = "";

	public QuanLyDiemTabPanel(QuanLiDiemController controller) {
		setLayout(new BorderLayout(8, 8));
		setOpaque(false);

		JPanel toolbar = new JPanel(new BorderLayout(8, 8));
		toolbar.setOpaque(false);
		toolbar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		leftActions.setOpaque(false);

		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
		rightActions.setOpaque(false);

		JButton btnImport = new JButton("Import Excel");
		JButton btnView = new JButton("Xem chi tiết");
		JButton btnEdit = new JButton("Sửa");
		JButton btnDelete = new JButton("Xóa");
		JButton btnAdd = new JButton("Thêm mới");
		JButton btnRefresh = new JButton("Làm mới");

		Style.styleFunctionButton(btnImport, Style.BTN_IMPORT, Color.WHITE);
		Style.styleFunctionButton(btnView, Style.BTN_UPDATE, Color.WHITE);
		Style.styleFunctionButton(btnEdit, Style.BTN_UPDATE, Color.WHITE);
		Style.styleFunctionButton(btnDelete, Style.BTN_DELETE, Color.WHITE);
		Style.styleFunctionButton(btnAdd, Style.BTN_ADD, Color.WHITE);
		Style.styleFunctionButton(btnRefresh, Style.BTN_CLEAR, Color.WHITE);

		leftActions.add(btnImport);
		leftActions.add(btnView);
		leftActions.add(btnEdit);
		leftActions.add(btnDelete);
		leftActions.add(btnAdd);
		leftActions.add(btnRefresh);

		rightActions.add(searchPanel);

		toolbar.add(leftActions, BorderLayout.WEST);
		toolbar.add(rightActions, BorderLayout.EAST);

		add(toolbar, BorderLayout.NORTH);

		thptPanel = new DiemThptDgnlPanel(controller);
		vsatPanel = new DiemVsatPanel(controller);

		subTabs.addTab("THPT & ĐGNL", thptPanel);
		subTabs.addTab("VSAT", vsatPanel);

		add(subTabs, BorderLayout.CENTER);

		btnImport.addActionListener(e -> getActiveTab().onImport());
		btnView.addActionListener(e -> getActiveTab().onView());
		btnEdit.addActionListener(e -> getActiveTab().onEdit());
		btnDelete.addActionListener(e -> getActiveTab().onDelete());
		btnAdd.addActionListener(e -> getActiveTab().onAdd());
		btnRefresh.addActionListener(e -> {
			currentSearchText = "";
			searchPanel.setSearchText("");
			getActiveTab().onRefresh();
		});

		searchPanel.addActionListener(e -> {
			currentSearchText = searchPanel.getSearchText();
			getActiveTab().onSearch(currentSearchText);
		});

		subTabs.addChangeListener(e -> getActiveTab().onSearch(currentSearchText));
	}

	private DiemTabActions getActiveTab() {
		int index = subTabs.getSelectedIndex();
		if (index == 1) {
			return vsatPanel;
		}
		return thptPanel;
	}
}
