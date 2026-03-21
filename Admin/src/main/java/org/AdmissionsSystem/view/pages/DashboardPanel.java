package org.AdmissionsSystem.view.pages;

import org.AdmissionsSystem.view.common.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Dashboard Tổng quan");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Metric","Value"};
        Object[][] data = {{"Tổng số thí sinh", "1,245"}, {"Đã nhập học", "520"}, {"Đã trúng tuyển", "320"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.view.components.CustomTable ct = new org.AdmissionsSystem.view.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}
