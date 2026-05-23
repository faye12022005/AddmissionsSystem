package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.controller.QuanLiDiemController;
import org.AdmissionsSystem.gui.common.Style;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class DiemThiSinhPanel extends JPanel {
    private final JTabbedPane mainTabs = new JTabbedPane();
    private final QuanLyDiemTabPanel quanLyDiemTab;
    private ThongKeDiemPanel thongKeTab;
    private DanhSachDiemXetTuyenPanel danhSachTab;
    private final JPanel thongKePlaceholder = new JPanel(new BorderLayout());
    private final JPanel danhSachPlaceholder = new JPanel(new BorderLayout());

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

        JLabel placeholderText = new JLabel("Mở tab 'Thống kê điểm' để tải dữ liệu thống kê.", SwingConstants.CENTER);
        placeholderText.setFont(Style.BUTTON_FONT);
        thongKePlaceholder.setOpaque(false);
        thongKePlaceholder.add(placeholderText, BorderLayout.CENTER);

        JLabel danhSachText = new JLabel("Mở tab 'Danh sách điểm xét tuyển theo nguyện vọng' để tải dữ liệu.",
                SwingConstants.CENTER);
        danhSachText.setFont(Style.BUTTON_FONT);
        danhSachPlaceholder.setOpaque(false);
        danhSachPlaceholder.add(danhSachText, BorderLayout.CENTER);

        mainTabs.addTab("Quản lí điểm", quanLyDiemTab);
        mainTabs.addTab("Thống kê điểm", thongKePlaceholder);
        mainTabs.addTab("Danh sách điểm xét tuyển theo nguyện vọng", danhSachPlaceholder);

        mainTabs.addChangeListener(e -> {
            if (mainTabs.getSelectedIndex() == 1 && thongKeTab == null) {
                thongKeTab = new ThongKeDiemPanel();
                mainTabs.setComponentAt(1, thongKeTab);
            }
            if (mainTabs.getSelectedIndex() == 2 && danhSachTab == null) {
                danhSachTab = new DanhSachDiemXetTuyenPanel(controller);
                mainTabs.setComponentAt(2, danhSachTab);
            }
        });

        add(mainTabs, BorderLayout.CENTER);
    }

}
