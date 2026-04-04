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

        String[] cols = {"ID Điểm Cộng", "TS CCCD", "Mã ngành", "Mã tổ hợp", "Phương thức", "Điểm CC", "Điểm UTXT", "Điểm tổng", "Ghi chú", "DC Keys"};
        Object[][] data = {
                {"1", "031098001234", "CNTT", "A1", "THPT", "1.5", "0.5", "2.0", "Hộ nghèo", "DC001"},
                {"2", "031098001235", "CNTT", "A1", "THPT", "2.0", "0.5", "2.5", "Thương binh", "DC002"},
                {"3", "031098001236", "KT", "D1", "THPT", "2.5", "1.0", "3.5", "Con liệt sĩ", "DC003"}
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
        JButton addBtn = new JButton("Thêm điểm cộng");
        JButton editBtn = new JButton("Sửa điểm cộng");
        JButton deleteBtn = new JButton("Xóa điểm cộng");
        JButton refreshBtn = new JButton("Làm mới");

        styleButtonBlue(importBtn);
        styleButtonGreen(addBtn);
        styleButtonBlue(editBtn);
        styleButtonRed(deleteBtn);
        styleButtonGray(refreshBtn);

        actionPanel.add(importBtn);
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        return actionPanel;
    }

    private void styleButtonGreen(JButton b) {
        b.setFocusPainted(false);
        b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(76, 175, 80));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false); // optional: bỏ viền xám
    }

    private void styleButtonBlue(JButton b) {
        b.setFocusPainted(false);
        b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(33, 150, 243));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false); // optional: bỏ viền xám
    }

    private void styleButtonRed(JButton b) {
        b.setFocusPainted(false);
        b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(244, 67, 54));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false); // optional: bỏ viền xám

    }

    private void styleButtonGray(JButton b) {
        b.setFocusPainted(false);
        b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(158, 158, 158));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false); // optional: bỏ viền xám
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

        JTextField idField = new JTextField();
        JTextField cccdField = new JTextField();
        JTextField nganhField = new JTextField();
        JTextField tohopField = new JTextField();
        JTextField phuongthucField = new JTextField();
        JTextField diemCCField = new JTextField();
        JTextField diemUtxtField = new JTextField();
        JTextField diemTongField = new JTextField();
        JTextArea ghichuArea = new JTextArea(2, 30);
        ghichuArea.setLineWrap(true);
        ghichuArea.setWrapStyleWord(true);
        JTextField dcKeysField = new JTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: ID, CCCD
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        formPanel.add(idField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("CCCD"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        formPanel.add(cccdField, gbc);

        // Row 1: Mã ngành, Mã tổ hợp
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mã ngành"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        formPanel.add(nganhField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mã tổ hợp"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        formPanel.add(tohopField, gbc);

        // Row 2: Phương thức, Điểm CC
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Phương thức"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        formPanel.add(phuongthucField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm CC"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        formPanel.add(diemCCField, gbc);

        // Row 3: Điểm U, Điểm tổng
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm U"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        formPanel.add(diemUtxtField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm tổng"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        formPanel.add(diemTongField, gbc);

        // Row 4: Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Ghi chú"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        JScrollPane scroller = new JScrollPane(ghichuArea);
        formPanel.add(scroller, gbc);

        // Row 5: DC Keys
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("DC Keys"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        formPanel.add(dcKeysField, gbc);

        return formPanel;
    }
}

