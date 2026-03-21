package org.AdmissionsSystem.gui.modules.QuanLiDiemCong;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;

public class DiemCongPanel extends JPanel {
    public DiemCongPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Điểm cộng");
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Mã","Mô tả","Điểm"};
        Object[][] data = {{"DC01","Hộ nghèo",1.0},{"DC02","Thương binh",2.0}};
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(data, cols);
        org.AdmissionsSystem.gui.components.CustomTable ct = new org.AdmissionsSystem.gui.components.CustomTable(m);
        add(ct, BorderLayout.CENTER);
    }
}

