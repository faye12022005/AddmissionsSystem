package org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class NganhToHopPanel extends JPanel {
    public NganhToHopPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Ngành & Tổ hợp");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Mã ngành","Tên ngành","Tổ hợp"};
        Object[][] data = {{"CNTT","Công nghệ thông tin","A00,B00"},{"KT","Kinh tế","C00"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.gui.components.CustomTable ct = new org.AdmissionsSystem.gui.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

