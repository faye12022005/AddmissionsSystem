package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.controller.QuanLiDiemController;
import org.AdmissionsSystem.gui.common.Style;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class DiemThiSinhPanel extends JPanel {
	private final JTabbedPane mainTabs = new JTabbedPane();
	private final QuanLyDiemTabPanel quanLyDiemTab;
	private final ThongKeDiemPanel thongKeTab;

	public DiemThiSinhPanel() {
		setLayout(new BorderLayout(8, 8));
		setBackground(Style.SURFACE);
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JLabel title = new JLabel("Quản lý điểm thí sinh");
		title.setFont(Style.TITLE_FONT);
		title.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
		add(title, BorderLayout.NORTH);

		QuanLiDiemController controller = new QuanLiDiemController();
		quanLyDiemTab = new QuanLyDiemTabPanel(controller);
		thongKeTab = new ThongKeDiemPanel();

		mainTabs.addTab("Quản lí điểm", quanLyDiemTab);
		mainTabs.addTab("Thống kê điểm", thongKeTab);

		add(mainTabs, BorderLayout.CENTER);
	}

}
