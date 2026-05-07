package org.AdmissionsSystem.gui.modules.QuanLiDiemCong;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.service.DiemCongService;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.Toast;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DiemCongPanel extends JPanel {

    private static final String[] COLS = {
        "ID Điểm Cộng", "TS CCCD", "Mã ngành", "Mã tổ hợp",
        "Phương thức", "Điểm CC", "Điểm UTXT", "Điểm tổng", "Ghi chú", "DC Keys"
    };

    private final DiemCongService service = new DiemCongService();
    private final DefaultTableModel tableModel;
    private JTable table;

    // Form fields
    private final JTextField idField = new JTextField();
    private final JTextField cccdField = new JTextField();
    private final JTextField nganhField = new JTextField();
    private final JTextField tohopField = new JTextField();
    private final JTextField phuongthucField = new JTextField();
    private final JTextField diemCCField = new JTextField();
    private final JTextField diemUtxtField = new JTextField();
    private final JTextField diemTongField = new JTextField();
    private final JTextArea ghichuArea = new JTextArea(2, 30);
    private final JTextField dcKeysField = new JTextField();

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

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), BorderLayout.NORTH);

        CustomTable ct = new CustomTable(tableModel);
        table = ct.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });
        contentPanel.add(ct, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        loadData();
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JButton addBtn = new JButton("Thêm điểm cộng");
        JButton editBtn = new JButton("Sửa điểm cộng");
        JButton deleteBtn = new JButton("Xóa điểm cộng");
        JButton refreshBtn = new JButton("Làm mới");

        styleButtonGreen(addBtn);
        styleButtonBlue(editBtn);
        styleButtonRed(deleteBtn);
        styleButtonGray(refreshBtn);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> { clearForm(); loadData(); });

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        return actionPanel;
    }

    private void loadData() {
        List<XtDiemcongxetuyen> rows = service.getAll();
        tableModel.setRowCount(0);
        for (XtDiemcongxetuyen r : rows) {
            tableModel.addRow(new Object[]{
                r.getIddiemcong(), r.getTsCccd(), r.getManganh(), r.getMatohop(),
                r.getPhuongthuc(), bd(r.getDiemcc()), bd(r.getDiemutxt()),
                bd(r.getDiemtong()), r.getGhichu(), r.getDcKeys()
            });
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int mr = table.convertRowIndexToModel(row);
        idField.setText(str(tableModel.getValueAt(mr, 0)));
        cccdField.setText(str(tableModel.getValueAt(mr, 1)));
        nganhField.setText(str(tableModel.getValueAt(mr, 2)));
        tohopField.setText(str(tableModel.getValueAt(mr, 3)));
        phuongthucField.setText(str(tableModel.getValueAt(mr, 4)));
        diemCCField.setText(str(tableModel.getValueAt(mr, 5)));
        diemUtxtField.setText(str(tableModel.getValueAt(mr, 6)));
        diemTongField.setText(str(tableModel.getValueAt(mr, 7)));
        ghichuArea.setText(str(tableModel.getValueAt(mr, 8)));
        dcKeysField.setText(str(tableModel.getValueAt(mr, 9)));
    }

    private void onAdd() {
        try {
            XtDiemcongxetuyen entity = collectForm();
            service.add(entity);
            loadData();
            clearForm();
            Toast.showToast(this, "Đã thêm điểm cộng.", false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        if (idField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa.");
            return;
        }
        try {
            XtDiemcongxetuyen entity = collectForm();
            entity.setIddiemcong(Integer.parseInt(idField.getText().trim()));
            service.update(entity);
            loadData();
            Toast.showToast(this, "Đã cập nhật điểm cộng.", false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (idField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            service.delete(Integer.parseInt(idField.getText().trim()));
            loadData();
            clearForm();
            Toast.showToast(this, "Đã xóa điểm cộng.", false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private XtDiemcongxetuyen collectForm() {
        XtDiemcongxetuyen e = new XtDiemcongxetuyen();
        e.setTsCccd(cccdField.getText().trim());
        e.setManganh(nganhField.getText().trim());
        e.setMatohop(tohopField.getText().trim());
        e.setPhuongthuc(phuongthucField.getText().trim());
        e.setDiemcc(parseBD(diemCCField.getText()));
        e.setDiemutxt(parseBD(diemUtxtField.getText()));
        e.setDiemtong(parseBD(diemTongField.getText()));
        e.setGhichu(ghichuArea.getText().trim());
        e.setDcKeys(dcKeysField.getText().trim());
        return e;
    }

    private void clearForm() {
        idField.setText(""); cccdField.setText(""); nganhField.setText("");
        tohopField.setText(""); phuongthucField.setText("");
        diemCCField.setText(""); diemUtxtField.setText(""); diemTongField.setText("");
        ghichuArea.setText(""); dcKeysField.setText("");
        table.clearSelection();
    }

    private String str(Object v) { return v == null ? "" : v.toString(); }
    private String bd(BigDecimal v) { return v == null ? "" : v.toPlainString(); }
    private BigDecimal parseBD(String s) {
        try { return s == null || s.isBlank() ? null : new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    // ── Styling helpers (preserved from original) ──
    private void styleButtonGreen(JButton b) {
        b.setFocusPainted(false); b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(76, 175, 80)); b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
    }

    private void styleButtonBlue(JButton b) {
        b.setFocusPainted(false); b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(33, 150, 243)); b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
    }

    private void styleButtonRed(JButton b) {
        b.setFocusPainted(false); b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(244, 67, 54)); b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
    }

    private void styleButtonGray(JButton b) {
        b.setFocusPainted(false); b.setFont(Style.BUTTON_FONT);
        b.setBackground(new Color(158, 158, 158)); b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 8, 10, 8),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 235)),
                        "Thông tin điểm cộng",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        Style.BUTTON_FONT.deriveFont(Font.BOLD),
                        new Color(60, 70, 90)
                )
        ));

        ghichuArea.setLineWrap(true);
        ghichuArea.setWrapStyleWord(true);
        idField.setEditable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("CCCD"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(cccdField, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã ngành"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(nganhField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã tổ hợp"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(tohopField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Phương thức"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(phuongthucField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm CC"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(diemCCField, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm UTXT"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(diemUtxtField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm tổng"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(diemTongField, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Ghi chú"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        formPanel.add(new JScrollPane(ghichuArea), gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("DC Keys"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        formPanel.add(dcKeysField, gbc);

        return formPanel;
    }
}
