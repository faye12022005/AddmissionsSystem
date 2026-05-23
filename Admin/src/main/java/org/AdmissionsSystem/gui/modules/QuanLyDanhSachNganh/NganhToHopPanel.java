package org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.service.NganhToHopService;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.models.XtNganhTohop;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class NganhToHopPanel extends JPanel {

    private static final String[] COLS = {
            "ID", "Mã ngành", "Mã tổ hợp", "Môn 1", "HS1", "Môn 2", "HS2", "Môn 3", "HS3",
            "N1", "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "KHAC", "KTPL", "Độ lệch"
    };

    // ── Màu sắc cho 2 widget ──────────────────────────────────────────────────
    private static final Color BTN_PRIMARY   = new Color(33, 102, 172);
    private static final Color BTN_DANGER    = new Color(220, 53,  69);
    private static final Color BTN_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_C      = new Color(206, 212, 218);
    private static final Color WHITE         = Color.WHITE;

    private final NganhToHopService service = new NganhToHopService();
    private final DefaultTableModel tableModel;
    private final NganhToHopPaginationPanel paginationPanel;

    // Form fields
    private final JTextField tfId      = new JTextField(8);
    private final JTextField tfMaNganh = new JTextField(12);
    private final JTextField tfMaToHop = new JTextField(12);
    private final JTextField tfMon1    = new JTextField(10);
    private final JTextField tfHs1     = new JTextField(4);
    private final JTextField tfMon2    = new JTextField(10);
    private final JTextField tfHs2     = new JTextField(4);
    private final JTextField tfMon3    = new JTextField(10);
    private final JTextField tfHs3     = new JTextField(4);
    private final JTextField tfDolech  = new JTextField(6);
    private final JTextField tfSearch  = new JTextField(18);

    private final JCheckBox cbN1   = new JCheckBox("N1");
    private final JCheckBox cbTO   = new JCheckBox("TO");
    private final JCheckBox cbLI   = new JCheckBox("LI");
    private final JCheckBox cbHO   = new JCheckBox("HO");
    private final JCheckBox cbSI   = new JCheckBox("SI");
    private final JCheckBox cbVA   = new JCheckBox("VA");
    private final JCheckBox cbSU   = new JCheckBox("SU");
    private final JCheckBox cbDI   = new JCheckBox("DI");
    private final JCheckBox cbTI   = new JCheckBox("TI");
    private final JCheckBox cbKHAC = new JCheckBox("KHAC");
    private final JCheckBox cbKTPL = new JCheckBox("KTPL");

    private JTable table;
    private List<XtNganhTohop> filteredRows = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 20;

    public NganhToHopPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Ngành & Tổ hợp");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(buildToolbar(), BorderLayout.NORTH);

        // ── Bảng dữ liệu: giữ nguyên 100% của bạn ───────────────────────────
        CustomTable ct = new CustomTable(tableModel);
        table = ct.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                onRowSelected();
        });
        paginationPanel = new NganhToHopPaginationPanel(pageSize);

        // ── Điều chỉnh độ rộng cột ───────────────────────────────────────────
        // col: 0=ID, 1=Mã ngành, 2=Mã tổ hợp, 3=Môn1, 4=HS1, 5=Môn2, 6=HS2,
        //      7=Môn3, 8=HS3, 9=N1..19=KTPL, 20=Độ lệch
        int[] widths = {
            40,   // ID
            90,   // Mã ngành
            80,   // Mã tổ hợp
            65,   // Môn 1
            40,   // HS1
            65,   // Môn 2
            40,   // HS2
            65,   // Môn 3
            40,   // HS3
            35,   // N1
            35,   // TO
            35,   // LI
            35,   // HO
            35,   // SI
            35,   // VA
            35,   // SU
            35,   // DI
            35,   // TI
            50,   // KHAC
            50,   // KTPL
            70    // Độ lệch
        };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setMinWidth(widths[i]);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        body.add(ct, BorderLayout.CENTER);
        body.add(paginationPanel, BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);

        bindPaginationEvents();
        applyFilter("");
    }

    // =========================================================================
    // buildToolbar: widget Chức năng (trái) + Hiển thị chi tiết (phải)
    // =========================================================================
    private JPanel buildToolbar() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        // Widget TRÁI: Chức năng
        gbc.gridx   = 0;
        gbc.weightx = 0;
        gbc.insets  = new Insets(0, 0, 0, 10);
        wrapper.add(buildChucNangPanel(), gbc);

        // Widget PHẢI: Hiển thị chi tiết
        gbc.gridx   = 1;
        gbc.weightx = 1;
        gbc.insets  = new Insets(0, 0, 0, 0);
        wrapper.add(buildHienThiPanel(), gbc);

        return wrapper;
    }

    // ── Widget "Chức năng" ────────────────────────────────────────────────────
    private JPanel buildChucNangPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_C),
                "Chức năng",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12), BTN_SECONDARY));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);

        // Hàng 0: 4 nút
        JButton btnAdd    = makeBtn("Thêm",     BTN_PRIMARY);
        JButton btnUpdate = makeBtn("Cập nhật", BTN_PRIMARY);
        JButton btnDelete = makeBtn("Xóa",      BTN_DANGER);
        JButton btnClear  = makeBtn("Làm mới",  BTN_SECONDARY);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());

        g.gridy = 0;
        g.gridx = 0; p.add(btnAdd,    g);
        g.gridx = 1; p.add(btnUpdate, g);
        g.gridx = 2; p.add(btnDelete, g);
        g.gridx = 3; p.add(btnClear,  g);

        // Hàng 1: Tìm kiếm
        g.gridy = 1; g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lbSearch = new JLabel("Tìm kiếm:");
        lbSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbSearch, g);

        g.gridx = 1; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setPreferredSize(new Dimension(180, 28));
        p.add(tfSearch, g);

        g.gridx = 3; g.gridwidth = 1; g.fill = GridBagConstraints.NONE;
        JButton btnSearch = makeBtn("Tìm", BTN_PRIMARY);
        btnSearch.addActionListener(e -> applyFilter(tfSearch.getText().trim()));
        p.add(btnSearch, g);

        return p;
    }

    private void bindPaginationEvents() {
        paginationPanel.setOnPageSizeChange(selected -> {
            pageSize = selected;
            currentPage = 1;
            loadPage();
        });
        paginationPanel.setOnPrev(() -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage();
            }
        });
        paginationPanel.setOnNext(() -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPage();
            }
        });
    }

    // ── Widget "Hiển thị chi tiết" ────────────────────────────────────────────
    private JPanel buildHienThiPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_C),
                "Hiển thị chi tiết",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12), BTN_SECONDARY));

        GridBagConstraints lc = new GridBagConstraints();
        lc.insets = new Insets(4, 8, 4, 2);
        lc.anchor = GridBagConstraints.WEST;
        lc.fill   = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(4, 2, 4, 6);
        fc.anchor = GridBagConstraints.WEST;
        fc.fill   = GridBagConstraints.HORIZONTAL;

        tfId.setEditable(false);

        // Hàng 0: ID | Môn 1 | HS1
        addLabelField(form, lc, fc, 0, 0, "ID:",    tfId);
        addLabelField(form, lc, fc, 0, 2, "Môn 1:", tfMon1);
        addLabelField(form, lc, fc, 0, 4, "HS1:",   tfHs1);

        // Hàng 1: Mã ngành | Môn 2 | HS2
        addLabelField(form, lc, fc, 1, 0, "Mã ngành:", tfMaNganh);
        addLabelField(form, lc, fc, 1, 2, "Môn 2:",    tfMon2);
        addLabelField(form, lc, fc, 1, 4, "HS2:",      tfHs2);

        // Hàng 2: Mã tổ hợp | Môn 3 | HS3 | Độ lệch
        addLabelField(form, lc, fc, 2, 0, "Mã tổ hợp:", tfMaToHop);
        addLabelField(form, lc, fc, 2, 2, "Môn 3:",     tfMon3);
        addLabelField(form, lc, fc, 2, 4, "HS3:",        tfHs3);
        addLabelField(form, lc, fc, 2, 6, "Độ lệch:",    tfDolech);

        // Hàng 3: Checkboxes
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkPanel.setOpaque(false);
        for (JCheckBox cb : new JCheckBox[]{cbN1,cbTO,cbLI,cbHO,cbSI,cbVA,cbSU,cbDI,cbTI,cbKHAC,cbKTPL}) {
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cb.setOpaque(false);
            checkPanel.add(cb);
        }
        GridBagConstraints spanC = new GridBagConstraints();
        spanC.gridx = 0; spanC.gridy = 3;
        spanC.gridwidth = GridBagConstraints.REMAINDER;
        spanC.fill = GridBagConstraints.HORIZONTAL;
        spanC.insets = new Insets(4, 6, 4, 6);
        form.add(checkPanel, spanC);

        return form;
    }

    // ── Layout helper ─────────────────────────────────────────────────────────
    private void addLabelField(JPanel p,
                               GridBagConstraints lc, GridBagConstraints fc,
                               int row, int colStart, String label, JTextField tf) {
        lc.gridx = colStart;     lc.gridy = row; lc.gridwidth = 1; lc.weightx = 0;
        p.add(new JLabel(label), lc);
        fc.gridx = colStart + 1; fc.gridy = row; fc.gridwidth = 1; fc.weightx = 0.3;
        p.add(tf, fc);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 32));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    // =========================================================================
    // Data / form helpers — giữ nguyên 100% của bạn
    // =========================================================================
    private void applyFilter(String keyword) {
        filteredRows = service.search(keyword);
        currentPage = 1;
        loadPage();
    }

    private void loadPage() {
        tableModel.setRowCount(0);
        if (filteredRows.isEmpty()) {
            paginationPanel.setPageInfo(1, 1, 0);
            paginationPanel.setNavigationEnabled(false, false);
            return;
        }

        int totalPages = getTotalPages();
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int from = (currentPage - 1) * pageSize;
        int to = Math.min(from + pageSize, filteredRows.size());

        for (int i = from; i < to; i++) {
            XtNganhTohop r = filteredRows.get(i);
            tableModel.addRow(new Object[]{
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

        paginationPanel.setPageInfo(currentPage, totalPages, filteredRows.size());
        paginationPanel.setNavigationEnabled(currentPage > 1, currentPage < totalPages);
    }

    private int getTotalPages() {
        if (filteredRows.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil(filteredRows.size() * 1.0 / pageSize);
    }

    private String toYN(Boolean b) {
        return (b != null && b) ? "Y" : "N";
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
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
            applyFilter(tfSearch.getText().trim());
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
            applyFilter(tfSearch.getText().trim());
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
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.delete(Integer.parseInt(tfId.getText().trim()));
            applyFilter(tfSearch.getText().trim());
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
        tfId.setText(""); tfMaNganh.setText(""); tfMaToHop.setText("");
        tfMon1.setText(""); tfHs1.setText("");
        tfMon2.setText(""); tfHs2.setText("");
        tfMon3.setText(""); tfHs3.setText("");
        tfDolech.setText("");
        cbN1.setSelected(false); cbTO.setSelected(false); cbLI.setSelected(false);
        cbHO.setSelected(false); cbSI.setSelected(false); cbVA.setSelected(false);
        cbSU.setSelected(false); cbDI.setSelected(false); cbTI.setSelected(false);
        cbKHAC.setSelected(false); cbKTPL.setSelected(false);
        table.clearSelection();
    }

    private String str(Object v) { return v == null ? "" : v.toString(); }

    private Integer parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
