package org.AdmissionsSystem.gui.modules.QuanLyNguoiDung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class UsersPanel extends JPanel {
    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Người dùng");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID","Tên","Vai trò"};
        Object[][] data = {{"U01","Lê Minh Anh","Admin"}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.gui.components.CustomTable ct = new org.AdmissionsSystem.gui.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

