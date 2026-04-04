package org.AdmissionsSystem.gui.modules.QuanLiDiemCong;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;

import java.awt.*;

public class DiemCongPanel extends JPanel {
    public DiemCongPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Điểm cộng");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        title.setFont(Style.TITLE_FONT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(createActionPanel(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Mã", "Mô tả", "Điểm"};
        Object[][] data = {
                {"DC01", "Hộ nghèo", "1.0"},
                {"DC02", "Thương binh", "2.0"},
                {"DC03", "Con liệt sĩ", "2.5"}
        };
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), BorderLayout.NORTH);

        CustomTable table = new CustomTable(model);
        contentPanel.add(table, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JButton importBtn = new JButton("Import danh sách");
        JButton addBtn = new JButton("Thêm");
        JButton editBtn = new JButton("Sửa");
        JButton deleteBtn = new JButton("Xóa");
        JButton refreshBtn = new JButton("Làm mới");

        Style.styleButton(importBtn);
        Style.styleButton(addBtn);
        Style.styleButton(editBtn);
        Style.styleButton(deleteBtn);
        Style.styleButton(refreshBtn);

        actionPanel.add(importBtn);
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        return actionPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 8, 10, 8),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 235)),
                        "Thông tin điểm cộng",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        Style.BUTTON_FONT.deriveFont(Font.BOLD),
                        new Color(60, 70, 90)
                )
        ));

        JTextField maField = new JTextField();
        JTextField diemField = new JTextField();
        JTextField moTaField = new JTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mã điểm cộng"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(maField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        formPanel.add(diemField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        formPanel.add(moTaField, gbc);

        return formPanel;
    }
}

