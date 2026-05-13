package org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.service.NganhToHopService;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.models.XtNganhTohop;

import java.awt.*;
import java.util.List;

public class NganhToHopPanel extends JPanel {

    private static final String[] COLS = {
            "ID", "Mã ngành", "Mã tổ hợp", "Môn 1", "HS1", "Môn 2", "HS2", "Môn 3", "HS3",
            "N1", "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "KHAC", "KTPL", "Độ lệch"
    };

    private final NganhToHopService service = new NganhToHopService();
    private final DefaultTableModel tableModel;

    // Form fields
    private final JTextField tfId = new JTextField(8);
    private final JTextField tfMaNganh = new JTextField(12);
    private final JTextField tfMaToHop = new JTextField(12);
    private final JTextField tfMon1 = new JTextField(10);
    private final JTextField tfHs1 = new JTextField(4);
    private final JTextField tfMon2 = new JTextField(10);
    private final JTextField tfHs2 = new JTextField(4);
    private final JTextField tfMon3 = new JTextField(10);
    private final JTextField tfHs3 = new JTextField(4);
    private final JTextField tfDolech = new JTextField(6);
    private final JTextField tfSearch = new JTextField(18);

    private final JCheckBox cbN1 = new JCheckBox("N1");
    private final JCheckBox cbTO = new JCheckBox("TO");
    private final JCheckBox cbLI = new JCheckBox("LI");
    private final JCheckBox cbHO = new JCheckBox("HO");
    private final JCheckBox cbSI = new JCheckBox("SI");
    private final JCheckBox cbVA = new JCheckBox("VA");
    private final JCheckBox cbSU = new JCheckBox("SU");
    private final JCheckBox cbDI = new JCheckBox("DI");
    private final JCheckBox cbTI = new JCheckBox("TI");
    private final JCheckBox cbKHAC = new JCheckBox("KHAC");
    private final JCheckBox cbKTPL = new JCheckBox("KTPL");

    private JTable table;

    public NganhToHopPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Ngành & Tổ hợp");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(buildToolbar(), BorderLayout.NORTH);

        CustomTable ct = new CustomTable(tableModel);
        table = ct.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                onRowSelected();
        });
        body.add(ct, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        loadData("");
    }

    private JPanel buildToolbar() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        // ── Form panel dùng GridBagLayout ──────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông tin chi tiết",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(36, 56, 102)));

        GridBagConstraints lc = new GridBagConstraints(); // label constraint
        lc.insets = new Insets(4, 8, 4, 2);
        lc.anchor = GridBagConstraints.WEST;
        lc.fill = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints(); // field constraint
        fc.insets = new Insets(4, 2, 4, 6);
        fc.anchor = GridBagConstraints.WEST;
        fc.fill = GridBagConstraints.HORIZONTAL;

        // ── Hàng 0: ID | Mã ngành | Mã tổ hợp ────────────────────────────
        int row = 0;
        tfId.setEditable(false);

        lc.gridx = 0;
        lc.gridy = row;
        form.add(new JLabel("ID:"), lc);
        fc.gridx = 1;
        fc.gridy = row;
        form.add(tfId, fc);

        lc.gridx = 2;
        lc.gridy = row;
        form.add(new JLabel("Môn 1:"), lc);
        fc.gridx = 3;
        fc.gridy = row;
        form.add(tfMon1, fc);

        lc.gridx = 4;
        lc.gridy = row;
        form.add(new JLabel("HS1:"), lc);
        fc.gridx = 5;
        fc.gridy = row;
        form.add(tfHs1, fc);

        // ── Hàng 1: Môn 1/HS1 | Môn 2/HS2 | Môn 3/HS3 | Độ lệch ─────────
        row = 1;

        lc.gridx = 0;
        lc.gridy = row;
        form.add(new JLabel("Mã ngành:"), lc);
        fc.gridx = 1;
        fc.gridy = row;
        form.add(tfMaNganh, fc);

        lc.gridx = 2;
        lc.gridy = row;
        form.add(new JLabel("Môn 2:"), lc);
        fc.gridx = 3;
        fc.gridy = row;
        form.add(tfMon2, fc);

        lc.gridx = 4;
        lc.gridy = row;
        form.add(new JLabel("HS2:"), lc);
        fc.gridx = 5;
        fc.gridy = row;
        form.add(tfHs2, fc);

        // ── Hàng 2: Môn 3/HS3 | Độ lệch ──────────────────────────────────
        row = 2;

        lc.gridx = 0;
        lc.gridy = row;
        form.add(new JLabel("Mã tổ hợp:"), lc);
        fc.gridx = 1;
        fc.gridy = row;
        form.add(tfMaToHop, fc);

        lc.gridx = 2;
        lc.gridy = row;
        form.add(new JLabel("Môn 3:"), lc);
        fc.gridx = 3;
        fc.gridy = row;
        form.add(tfMon3, fc);

        lc.gridx = 4;
        lc.gridy = row;
        form.add(new JLabel("HS3:"), lc);
        fc.gridx = 5;
        fc.gridy = row;
        form.add(tfHs3, fc);

        lc.gridx = 6;
        lc.gridy = row;
        form.add(new JLabel("Độ lệch:"), lc);
        fc.gridx = 7;
        fc.gridy = row;
        form.add(tfDolech, fc);

        // ── Hàng 3: Checkboxes (span toàn bộ cột) ─────────────────────────
        row = 3;
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(cbN1);
        checkPanel.add(cbTO);
        checkPanel.add(cbLI);
        checkPanel.add(cbHO);
        checkPanel.add(cbSI);
        checkPanel.add(cbVA);
        checkPanel.add(cbSU);
        checkPanel.add(cbDI);
        checkPanel.add(cbTI);
        checkPanel.add(cbKHAC);
        checkPanel.add(cbKTPL);

        GridBagConstraints spanC = new GridBagConstraints();
        spanC.gridx = 0;
        spanC.gridy = row;
        spanC.gridwidth = GridBagConstraints.REMAINDER;
        spanC.fill = GridBagConstraints.HORIZONTAL;
        spanC.insets = new Insets(4, 6, 4, 6);
        form.add(checkPanel, spanC);

        // ── Action buttons ─────────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        actions.setOpaque(false);
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");
        Style.styleButton(btnAdd);
        Style.styleButton(btnUpdate);
        Style.styleButton(btnDelete);
        Style.styleButton(btnClear);
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        // ── Search ─────────────────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        searchPanel.add(lblSearch);
        searchPanel.add(tfSearch);
        JButton btnSearch = new JButton("Tìm");
        Style.styleButton(btnSearch);
        // Match search field and label font and height to button
        lblSearch.setFont(btnSearch.getFont());
        tfSearch.setFont(btnSearch.getFont());
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        Dimension btnSize = btnSearch.getPreferredSize();
        tfSearch.setPreferredSize(new Dimension(tfSearch.getPreferredSize().width, btnSize.height));
        searchPanel.add(btnSearch);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(actions, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        wrapper.add(form, BorderLayout.NORTH);
        wrapper.add(top, BorderLayout.SOUTH);
        return wrapper;
    }

    // ── Data / form helpers ────────────────────────────────────────────────

    private void loadData(String keyword) {
        List<XtNganhTohop> rows = service.search(keyword);
        tableModel.setRowCount(0);
        for (XtNganhTohop r : rows) {
            tableModel.addRow(new Object[] {
                    r.getId(), r.getManganh(), r.getMatohop(),
                    r.getThMon1(), r.getHsmon1(),
                    r.getThMon2(), r.getHsmon2(),
                    r.getThMon3(), r.getHsmon3(),
                    toYN(r.getN1()), toYN(r.getTo()), toYN(r.getLi()), toYN(r.getHo()),
                    toYN(r.getSi()), toYN(r.getVa()), toYN(r.getSu()), toYN(r.getDi()),
                    toYN(r.getTi()), toYN(r.getKhac()), toYN(r.getKtpl()),
                    r.getDolech()
            });
        }
    }

    private String toYN(Boolean b) {
        return (b != null && b) ? "Y" : "N";
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        int m = table.convertRowIndexToModel(row);
        tfId.setText(str(tableModel.getValueAt(m, 0)));
        tfMaNganh.setText(str(tableModel.getValueAt(m, 1)));
        tfMaToHop.setText(str(tableModel.getValueAt(m, 2)));
        tfMon1.setText(str(tableModel.getValueAt(m, 3)));
        tfHs1.setText(str(tableModel.getValueAt(m, 4)));
        tfMon2.setText(str(tableModel.getValueAt(m, 5)));
        tfHs2.setText(str(tableModel.getValueAt(m, 6)));
        tfMon3.setText(str(tableModel.getValueAt(m, 7)));
        tfHs3.setText(str(tableModel.getValueAt(m, 8)));
        cbN1.setSelected("Y".equals(tableModel.getValueAt(m, 9)));
        cbTO.setSelected("Y".equals(tableModel.getValueAt(m, 10)));
        cbLI.setSelected("Y".equals(tableModel.getValueAt(m, 11)));
        cbHO.setSelected("Y".equals(tableModel.getValueAt(m, 12)));
        cbSI.setSelected("Y".equals(tableModel.getValueAt(m, 13)));
        cbVA.setSelected("Y".equals(tableModel.getValueAt(m, 14)));
        cbSU.setSelected("Y".equals(tableModel.getValueAt(m, 15)));
        cbDI.setSelected("Y".equals(tableModel.getValueAt(m, 16)));
        cbTI.setSelected("Y".equals(tableModel.getValueAt(m, 17)));
        cbKHAC.setSelected("Y".equals(tableModel.getValueAt(m, 18)));
        cbKTPL.setSelected("Y".equals(tableModel.getValueAt(m, 19)));
        tfDolech.setText(str(tableModel.getValueAt(m, 20)));
    }

    private void onAdd() {
        try {
            service.add(collectForm());
            loadData(tfSearch.getText().trim());
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        if (tfId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần cập nhật.");
            return;
        }
        try {
            XtNganhTohop entity = collectForm();
            entity.setId(Integer.parseInt(tfId.getText().trim()));
            service.update(entity);
            loadData(tfSearch.getText().trim());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (tfId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        try {
            service.delete(Integer.parseInt(tfId.getText().trim()));
            loadData(tfSearch.getText().trim());
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private XtNganhTohop collectForm() {
        XtNganhTohop e = new XtNganhTohop();
        e.setManganh(tfMaNganh.getText().trim());
        e.setMatohop(tfMaToHop.getText().trim());
        e.setThMon1(tfMon1.getText().trim());
        e.setHsmon1(parseIntSafe(tfHs1.getText()));
        e.setThMon2(tfMon2.getText().trim());
        e.setHsmon2(parseIntSafe(tfHs2.getText()));
        e.setThMon3(tfMon3.getText().trim());
        e.setHsmon3(parseIntSafe(tfHs3.getText()));
        e.setN1(cbN1.isSelected());
        e.setTo(cbTO.isSelected());
        e.setLi(cbLI.isSelected());
        e.setHo(cbHO.isSelected());
        e.setSi(cbSI.isSelected());
        e.setVa(cbVA.isSelected());
        e.setSu(cbSU.isSelected());
        e.setDi(cbDI.isSelected());
        e.setTi(cbTI.isSelected());
        e.setKhac(cbKHAC.isSelected());
        e.setKtpl(cbKTPL.isSelected());
        String dl = tfDolech.getText().trim();
        if (!dl.isEmpty())
            e.setDolech(new java.math.BigDecimal(dl));
        return e;
    }

    private void clearForm() {
        tfId.setText("");
        tfMaNganh.setText("");
        tfMaToHop.setText("");
        tfMon1.setText("");
        tfHs1.setText("");
        tfMon2.setText("");
        tfHs2.setText("");
        tfMon3.setText("");
        tfHs3.setText("");
        tfDolech.setText("");
        cbN1.setSelected(false);
        cbTO.setSelected(false);
        cbLI.setSelected(false);
        cbHO.setSelected(false);
        cbSI.setSelected(false);
        cbVA.setSelected(false);
        cbSU.setSelected(false);
        cbDI.setSelected(false);
        cbTI.setSelected(false);
        cbKHAC.setSelected(false);
        cbKTPL.setSelected(false);
        table.clearSelection();
    }

    private String str(Object v) {
        return v == null ? "" : v.toString();
    }

    private Integer parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}