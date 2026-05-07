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
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(buildToolbar(), BorderLayout.NORTH);

        CustomTable ct = new CustomTable(tableModel);
        table = ct.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });
        body.add(ct, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        loadData("");
    }

    private JPanel buildToolbar() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        // Form panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(true);
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông tin chi tiết",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(36, 56, 102)
        ));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row1.setOpaque(false);
        tfId.setEditable(false);
        row1.add(new JLabel("ID:")); row1.add(tfId);
        row1.add(new JLabel("Mã ngành:")); row1.add(tfMaNganh);
        row1.add(new JLabel("Mã tổ hợp:")); row1.add(tfMaToHop);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row2.setOpaque(false);
        row2.add(new JLabel("Môn 1:")); row2.add(tfMon1);
        row2.add(new JLabel("HS1:")); row2.add(tfHs1);
        row2.add(new JLabel("Môn 2:")); row2.add(tfMon2);
        row2.add(new JLabel("HS2:")); row2.add(tfHs2);
        row2.add(new JLabel("Môn 3:")); row2.add(tfMon3);
        row2.add(new JLabel("HS3:")); row2.add(tfHs3);
        row2.add(new JLabel("Độ lệch:")); row2.add(tfDolech);

        form.add(row1);
        form.add(row2);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(cbN1); checkPanel.add(cbTO); checkPanel.add(cbLI);
        checkPanel.add(cbHO); checkPanel.add(cbSI); checkPanel.add(cbVA);
        checkPanel.add(cbSU); checkPanel.add(cbDI); checkPanel.add(cbTI);
        checkPanel.add(cbKHAC); checkPanel.add(cbKTPL);

        // Action buttons
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

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(tfSearch);
        JButton btnSearch = new JButton("Tìm");
        Style.styleButton(btnSearch);
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        searchPanel.add(btnSearch);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(actions, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        wrapper.add(form, BorderLayout.NORTH);
        wrapper.add(checkPanel, BorderLayout.CENTER);
        wrapper.add(top, BorderLayout.SOUTH);
        return wrapper;
    }

    private void loadData(String keyword) {
        List<XtNganhTohop> rows = service.search(keyword);
        tableModel.setRowCount(0);
        for (XtNganhTohop r : rows) {
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
    }

    private String toYN(Boolean b) {
        return (b != null && b) ? "Y" : "N";
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int modelRow = table.convertRowIndexToModel(row);
        tfId.setText(str(tableModel.getValueAt(modelRow, 0)));
        tfMaNganh.setText(str(tableModel.getValueAt(modelRow, 1)));
        tfMaToHop.setText(str(tableModel.getValueAt(modelRow, 2)));
        tfMon1.setText(str(tableModel.getValueAt(modelRow, 3)));
        tfHs1.setText(str(tableModel.getValueAt(modelRow, 4)));
        tfMon2.setText(str(tableModel.getValueAt(modelRow, 5)));
        tfHs2.setText(str(tableModel.getValueAt(modelRow, 6)));
        tfMon3.setText(str(tableModel.getValueAt(modelRow, 7)));
        tfHs3.setText(str(tableModel.getValueAt(modelRow, 8)));
        
        cbN1.setSelected("Y".equals(tableModel.getValueAt(modelRow, 9)));
        cbTO.setSelected("Y".equals(tableModel.getValueAt(modelRow, 10)));
        cbLI.setSelected("Y".equals(tableModel.getValueAt(modelRow, 11)));
        cbHO.setSelected("Y".equals(tableModel.getValueAt(modelRow, 12)));
        cbSI.setSelected("Y".equals(tableModel.getValueAt(modelRow, 13)));
        cbVA.setSelected("Y".equals(tableModel.getValueAt(modelRow, 14)));
        cbSU.setSelected("Y".equals(tableModel.getValueAt(modelRow, 15)));
        cbDI.setSelected("Y".equals(tableModel.getValueAt(modelRow, 16)));
        cbTI.setSelected("Y".equals(tableModel.getValueAt(modelRow, 17)));
        cbKHAC.setSelected("Y".equals(tableModel.getValueAt(modelRow, 18)));
        cbKTPL.setSelected("Y".equals(tableModel.getValueAt(modelRow, 19)));
        tfDolech.setText(str(tableModel.getValueAt(modelRow, 20)));
    }

    private void onAdd() {
        try {
            XtNganhTohop entity = collectForm();
            service.add(entity);
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
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
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
        if (!dl.isEmpty()) {
            e.setDolech(new java.math.BigDecimal(dl));
        }
        
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
        try { return s == null || s.isBlank() ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
