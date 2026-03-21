package org.AdmissionsSystem.view.pages;

import org.AdmissionsSystem.view.common.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        org.AdmissionsSystem.view.components.CustomTable ct = new org.AdmissionsSystem.view.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

