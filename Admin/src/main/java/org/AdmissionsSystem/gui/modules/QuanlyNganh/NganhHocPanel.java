package org.AdmissionsSystem.gui.modules.QuanlyNganh;
import javax.swing.*;
import org.AdmissionsSystem.gui.common.Style;

import java.awt.*;
public class NganhHocPanel extends JPanel {
    public NganhHocPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        JLabel title = new JLabel("Quản lý Ngành Học");
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


