package org.AdmissionsSystem.gui.modules.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.service.*;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;

import java.awt.*;

public class DashboardPanel extends JPanel {

    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final NganhHocService nganhHocService = new NganhHocService();
    private final NguyenVongService nguyenVongService = new NguyenVongService();
    private final DiemThiService diemThiService = new DiemThiService();
    private final NguoiDungService usersService = new NguoiDungService();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Dashboard Tổng quan");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        // Load real stats from DB
        Object[][] data = loadStats();
        String[] cols = {"Metric", "Value"};
        DefaultTableModel m = new DefaultTableModel(data, cols);
        CustomTable ct = new CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }

    private Object[][] loadStats() {
        try {
            long totalThiSinh = thiSinhService.count();
            long totalNganh = nganhHocService.getAll().size();
            long totalNguyenVong = nguyenVongService.count();
            long totalDiemThi = diemThiService.count();
            long totalUsers = usersService.count();

            return new Object[][]{
                {"Tổng số thí sinh", String.format("%,d", totalThiSinh)},
                {"Tổng số ngành tuyển sinh", String.format("%,d", totalNganh)},
                {"Tổng số nguyện vọng", String.format("%,d", totalNguyenVong)},
                {"Tổng số bài thi", String.format("%,d", totalDiemThi)},
                {"Tổng số người dùng", String.format("%,d", totalUsers)},
            };
        } catch (Exception e) {
            return new Object[][]{
                {"Lỗi kết nối DB", e.getMessage()}
            };
        }
    }
}
