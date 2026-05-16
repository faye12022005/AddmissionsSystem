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
    private final JPanel thongKePlaceholder = new JPanel(new BorderLayout());

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

        mainTabs.addTab("Quản lí điểm", quanLyDiemTab);
        mainTabs.addTab("Thống kê điểm", thongKePlaceholder);

        mainTabs.addChangeListener(e -> {
            if (mainTabs.getSelectedIndex() == 1 && thongKeTab == null) {
                thongKeTab = new ThongKeDiemPanel();
                mainTabs.setComponentAt(1, thongKeTab);
            }
        });

        add(mainTabs, BorderLayout.CENTER);
    }

}
