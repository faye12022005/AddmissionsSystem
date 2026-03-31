package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToHopMonPanel extends JPanel {

    public ToHopMonPanel() {
        // Thiết lập layout chính và khoảng cách lề (Padding)
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 249, 250)); // Nền xám nhạt như bản mẫu
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // --- 1. Header: Tiêu đề và Các nút chức năng ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Bên trái: Tiêu đề & Mô tả
        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 5));
        titleArea.setOpaque(false);
        JLabel title = new JLabel("Tổ hợp môn xét tuyển");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Quản lý và cấu hình danh sách các khối thi cho kỳ tuyển sinh.");
        subtitle.setForeground(Color.GRAY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleArea.add(title);
        titleArea.add(subtitle);

        // Bên phải: Import & Thêm mới
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsRow.setOpaque(false);
        JButton importBtn = createStyledButton("📥 Import Excel", new Color(255, 255, 255), Color.BLACK);
        JButton addBtn = createStyledButton("+ Thêm tổ hợp", new Color(24, 119, 242), Color.WHITE);
        actionsRow.add(importBtn);
        actionsRow.add(addBtn);

        headerPanel.add(titleArea, BorderLayout.WEST);
        headerPanel.add(actionsRow, BorderLayout.EAST);

        // --- 2. Thống kê (Stat Cards) ---
        JPanel statRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statRow.setOpaque(false);
        statRow.add(createStatCard("Tổng số tổ hợp", "42", new Color(24, 119, 242)));
        statRow.add(createStatCard("Đang sử dụng", "38", new Color(34, 197, 94)));
        statRow.add(createStatCard("Mới cập nhật", "12", new Color(234, 179, 8)));

        // --- 3. Khu vực Bảng và Bộ lọc ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setOpaque(false);

        // Cấu hình bảng dữ liệu theo yêu cầu mới
        String[] columns = {"MÃ TỔ HỢP", "TÊN TỔ HỢP", "MÔN 1", "MÔN 2", "MÔN 3", "HÀNH ĐỘNG"};
        Object[][] data = {
            {"A00", "Khối A00", "Toán", "Vật lý", "Hóa học", ""},
            {"A01", "Khối A01", "Toán", "Vật lý", "Tiếng Anh", ""},
            {"B00", "Khối B00", "Toán", "Hóa học", "Sinh học", ""},
            {"D01", "Khối D01", "Ngữ văn", "Toán", "Tiếng Anh", ""}
        };

        // move model and table to fields so editors can update them
        this.model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // only action column editable
            }
        };

        this.table = new JTable(this.model);
        setupTableStyle(this.table);

        // set up renderer/editor for action column
        this.table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        this.table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonsEditor(new JCheckBox()));
        this.table.getColumnModel().getColumn(5).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Wire add button to open AddToHopDialog and append new row
        addBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            AddToHopDialog dlg = new AddToHopDialog(owner);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                Object[] newRow = new Object[]{dlg.getCode(), dlg.getNameValue(), dlg.getSubj1(), dlg.getSubj2(), dlg.getSubj3(), ""};
                model.addRow(newRow);
            }
        });

        // Ghép các thành phần vào Panel chính
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 24));
        centerWrapper.setOpaque(false);
        centerWrapper.add(statRow, BorderLayout.NORTH);
        centerWrapper.add(mainContent, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JTable table;
    private DefaultTableModel model;

    // Hàm tạo Button đẹp
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return btn;
    }

    // Hàm tạo Thẻ thống kê
    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(100, 4));
        bar.setBackground(accentColor);
        
        card.add(l, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);
        return card;
    }

    // Cấu hình style cho bảng
    private void setupTableStyle(JTable table) {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(240, 247, 255));
        table.setSelectionForeground(Color.BLACK);
    }

    // Renderer để hiển thị 2 nút Sửa/Xóa trong bảng
    class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            JButton edit = new JButton("Sửa");
            edit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            JButton del = new JButton("Xóa");
            del.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            del.setForeground(Color.RED);
            
            this.add(edit);
            this.add(del);
            return this;
        }
    }

    // Editor để thực hiện hành động khi nhấn nút
    class ActionButtonsEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton editBtn, deleteBtn;
        private int currentRow = -1;
        public ActionButtonsEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
            editBtn = new JButton("Sửa");
            deleteBtn = new JButton("Xóa");
            
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentRow >= 0 && currentRow < model.getRowCount()) {
                    // get existing values
                    String code = (String) model.getValueAt(currentRow, 0);
                    String name = (String) model.getValueAt(currentRow, 1);
                    String s1 = (String) model.getValueAt(currentRow, 2);
                    String s2 = (String) model.getValueAt(currentRow, 3);
                    String s3 = (String) model.getValueAt(currentRow, 4);
                    Frame owner = (Frame) SwingUtilities.getWindowAncestor(panel);
                    EditToHopDialog dlg = new EditToHopDialog(owner, code, name, s1, s2, s3, null);
                    dlg.setVisible(true);
                    if (dlg.isSaved()) {
                        model.setValueAt(dlg.getNameValue(), currentRow, 1);
                        model.setValueAt(dlg.getSubj1(), currentRow, 2);
                        model.setValueAt(dlg.getSubj2(), currentRow, 3);
                        model.setValueAt(dlg.getSubj3(), currentRow, 4);
                    }
                }
            });
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentRow >= 0 && currentRow < model.getRowCount()) {
                    int confirm = JOptionPane.showConfirmDialog(panel, "Bạn có chắc muốn xóa?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        model.removeRow(currentRow);
                    }
                }
            });
            
            panel.add(editBtn);
            panel.add(deleteBtn);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            this.currentRow = row;
            return panel;
        }
    }
}