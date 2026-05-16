package org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components;

import org.AdmissionsSystem.gui.common.Style;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DiemCongSearchPanel extends JPanel {
    private final JTextField searchField;
    private final JButton searchBtn;

    public DiemCongSearchPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);

        JLabel label = new JLabel("Tìm kiếm:");
        label.setFont(Style.TABLE_FONT);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.setMaximumSize(new Dimension(200, 32));
        searchField.setFont(Style.TABLE_FONT);

        searchBtn = new JButton("Tìm");
        Style.styleFunctionButton(searchBtn);

        add(label);
        add(Box.createRigidArea(new Dimension(8, 0)));
        add(searchField);
        add(Box.createRigidArea(new Dimension(8, 0)));
        add(searchBtn);
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public void setSearchText(String text) {
        searchField.setText(text);
    }

    public void addActionListener(ActionListener listener) {
        searchBtn.addActionListener(listener);
        searchField.addActionListener(listener);
    }
}
