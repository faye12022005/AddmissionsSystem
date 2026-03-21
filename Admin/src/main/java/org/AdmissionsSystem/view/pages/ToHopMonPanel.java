package org.AdmissionsSystem.view.pages;

import org.AdmissionsSystem.view.common.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ToHopMonPanel extends JPanel {
    public ToHopMonPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Tổ hợp môn");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Mã","Tổ hợp","Mô tả"};
        Object[][] data = {{"A00","Toán-Lý-Hóa","Kỹ thuật"},{"C00","Văn-Sử-Địa","Xã hội"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.view.components.CustomTable ct = new org.AdmissionsSystem.view.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

