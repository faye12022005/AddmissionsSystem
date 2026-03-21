package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class DiemThisinhPanel extends JPanel {
    public DiemThisinhPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Điểm Thí sinh");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID","Họ và tên","Môn","Điểm"};
        Object[][] data = {{"TS-001","Nguyễn A","Toán",8.5},{"TS-002","Trần B","Văn",7.0}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.gui.components.CustomTable ct = new org.AdmissionsSystem.gui.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

