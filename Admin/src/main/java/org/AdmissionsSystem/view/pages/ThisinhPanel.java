package org.AdmissionsSystem.view.pages;

import org.AdmissionsSystem.view.common.Style;
import org.AdmissionsSystem.view.common.Searchable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ThisinhPanel extends JPanel implements Searchable {
    public ThisinhPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Thí sinh");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID","Họ và tên","CCCD","Ngày sinh","Giới tính","Trạng thái"};
        Object[][] data = {{"TS-2024-001","Nguyễn Văn An","031098001234","12/05/2006","Nam","Đã nhập học"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.view.components.CustomTable ct = new org.AdmissionsSystem.view.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }

    @Override
    public void onSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }
        // Basic stub: show search term. Replace with actual filter logic later.
        JOptionPane.showMessageDialog(this, "Tìm kiếm thí sinh với: " + query);
    }
}
