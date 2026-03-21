package org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class BangQuyDoiPanel extends JPanel {
    public BangQuyDoiPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Bảng quy đổi");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Thang","Diem quy doi"};
        Object[][] data = {{"A","10"},{"B","8"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.gui.components.CustomTable ct = new org.AdmissionsSystem.gui.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

