package org.AdmissionsSystem.gui.modules.QuanLiDiemCong;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.controller.DiemCongController;
import org.AdmissionsSystem.bus.service.DiemCongService;
import org.AdmissionsSystem.bus.service.NganhHocService;
import org.AdmissionsSystem.bus.service.NguyenVongService;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.Toast;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components.DiemCongPaginationPanel;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components.DiemCongSearchPanel;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiemCongPanel extends JPanel {

    private static final String KHONG_CO = "không có";

    private static final String[] COLS = {
        "ID Điểm Cộng", "TS CCCD", "Mã ngành", "Mã tổ hợp",
        "Phương thức", "Điểm CC", "Điểm UTXT", "Điểm tổng", "Nguyện vọng", "Thứ tự NV"
    };

    private final DiemCongController controller = new DiemCongController();
    private final NganhHocService nganhHocService = new NganhHocService();
    private final NguyenVongService nguyenVongService = new NguyenVongService();
    private final DefaultTableModel tableModel;
    private JTable table;
    private final DiemCongSearchPanel searchPanel;
    private final DiemCongPaginationPanel paginationPanel;
    private int pageSize = 20;
    private int currentPage = 1;
    private List<XtDiemcongxetuyen> filteredRows = new ArrayList<>();
    private final Map<String, List<XtNguyenvongxettuyen>> nguyenVongCacheByCccd = new HashMap<>();
    private final Map<String, String> tenNganhByMaNganh = new HashMap<>();

    // Form fields
    private final JTextField idField = new JTextField();
    private final JTextField cccdField = new JTextField();
    private final JTextField nganhField = new JTextField();
    private final JTextField tohopField = new JTextField();
    private final JTextField phuongthucField = new JTextField();
    private final JTextField diemCCField = new JTextField();
    private final JTextField diemUtxtField = new JTextField();
    private final JTextField diemTongField = new JTextField();
    private final JTextField nguyenVongField = new JTextField();
    private final JTextField thuTuNvField = new JTextField();
    private String currentGhichu = "";
    private String currentDcKeys = "";

    public DiemCongPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Điểm cộng");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        title.setFont(Style.TITLE_FONT);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        searchPanel = new DiemCongSearchPanel();
        paginationPanel = new DiemCongPaginationPanel(pageSize);

        // NORTH panel: title + action buttons + form
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(title, BorderLayout.NORTH);
        
        JPanel actionAndFormPanel = new JPanel(new BorderLayout());
        actionAndFormPanel.setOpaque(false);
        actionAndFormPanel.add(createActionPanel(), BorderLayout.NORTH);
        actionAndFormPanel.add(createFormPanel(), BorderLayout.CENTER);
        
        northPanel.add(actionAndFormPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        // CENTER panel: table only
        CustomTable ct = new CustomTable(tableModel);
        table = ct.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });
        add(ct, BorderLayout.CENTER);

        // SOUTH panel: pagination
        add(paginationPanel, BorderLayout.SOUTH);

        bindEvents();
        applyFilterByCccd("");
    }

    private JPanel createActionPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(12, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setOpaque(false);

        JButton addBtn = new JButton("Thêm điểm cộng");
        JButton editBtn = new JButton("Sửa điểm cộng");
        JButton deleteBtn = new JButton("Xóa điểm cộng");
        JButton importDccBtn = new JButton("Import ĐCC");
        JButton importUtxtBtn = new JButton("Import UTXT");
        JButton refreshBtn = new JButton("Làm mới");

        styleButtonGreen(addBtn);
        styleButtonBlue(editBtn);
        styleButtonRed(deleteBtn);
        styleButtonBlue(importDccBtn);
        styleButtonBlue(importUtxtBtn);
        styleButtonGray(refreshBtn);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        importDccBtn.addActionListener(e -> onImportDcc());
        importUtxtBtn.addActionListener(e -> onImportUtxt());
        refreshBtn.addActionListener(e -> {
            clearForm();
            searchPanel.setSearchText("");
            applyFilterByCccd("");
        });

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(importDccBtn);
        actionPanel.add(importUtxtBtn);
        actionPanel.add(refreshBtn);

        wrapper.add(searchPanel, BorderLayout.WEST);
        wrapper.add(actionPanel, BorderLayout.EAST);
        return wrapper;
    }

    private void bindEvents() {
        searchPanel.addActionListener(e -> applyFilterByCccd(searchPanel.getSearchText()));

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

    private void applyFilterByCccd(String cccdKeyword) {
        filteredRows = controller.searchByCccd(cccdKeyword);
        filteredRows.sort(Comparator
                .comparing((XtDiemcongxetuyen row) -> safe(row.getTsCccd()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(row -> safe(row.getManganh()), String.CASE_INSENSITIVE_ORDER));
        nguyenVongCacheByCccd.clear();
        tenNganhByMaNganh.clear();
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
            XtDiemcongxetuyen r = filteredRows.get(i);
            NguyenVongInfo nvInfo = resolveNguyenVongInfo(r);
            tableModel.addRow(new Object[]{
                r.getIddiemcong(), r.getTsCccd(), r.getManganh(), r.getMatohop(),
                r.getPhuongthuc(), bd(r.getDiemcc()), bd(r.getDiemutxt()),
                bd(r.getDiemtong()), nvInfo.nguyenVong, nvInfo.thuTuNv
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

    private void onRowSelected() {
        XtDiemcongxetuyen selected = findSelectedEntity();
        if (selected == null) return;

        idField.setText(str(selected.getIddiemcong()));
        cccdField.setText(str(selected.getTsCccd()));
        nganhField.setText(str(selected.getManganh()));
        tohopField.setText(str(selected.getMatohop()));
        phuongthucField.setText(str(selected.getPhuongthuc()));
        diemCCField.setText(bd(selected.getDiemcc()));
        diemUtxtField.setText(bd(selected.getDiemutxt()));
        diemTongField.setText(bd(selected.getDiemtong()));

        currentGhichu = str(selected.getGhichu());
        currentDcKeys = str(selected.getDcKeys());
        NguyenVongInfo nvInfo = resolveNguyenVongInfo(selected);
        nguyenVongField.setText(nvInfo.nguyenVong);
        thuTuNvField.setText(nvInfo.thuTuNv);
    }

    private void onAdd() {
        try {
            // Validation
            if (cccdField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập TS CCCD.");
                return;
            }
            if (nganhField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã ngành.");
                return;
            }
            if (tohopField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã tổ hợp.");
                return;
            }
            if (diemCCField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Điểm CC.");
                return;
            }
            
            XtDiemcongxetuyen entity = collectForm();
            controller.add(entity);
            applyFilterByCccd(searchPanel.getSearchText());
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
        // Validation
        if (cccdField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập TS CCCD.");
            return;
        }
        if (nganhField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã ngành.");
            return;
        }
        if (tohopField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã tổ hợp.");
            return;
        }
        if (diemCCField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Điểm CC.");
            return;
        }
        try {
            XtDiemcongxetuyen entity = collectForm();
            entity.setIddiemcong(Integer.parseInt(idField.getText().trim()));
                controller.update(entity);
            applyFilterByCccd(searchPanel.getSearchText());
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
            controller.delete(Integer.parseInt(idField.getText().trim()));
            applyFilterByCccd(searchPanel.getSearchText());
            clearForm();
            Toast.showToast(this, "Đã xóa điểm cộng.", false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onImportDcc() {
        try {
            DiemCongService.ImportResult result = controller.importDcc(this);
            if (result.totalRows() == 0) {
                return;
            }
            applyFilterByCccd(searchPanel.getSearchText());
            showImportSummary("ĐCC", result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi import ĐCC", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onImportUtxt() {
        try {
            DiemCongService.ImportResult result = controller.importUtxt(this);
            if (result.totalRows() == 0) {
                return;
            }
            applyFilterByCccd(searchPanel.getSearchText());
            showImportSummary("UTXT", result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi import UTXT", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showImportSummary(String mode, DiemCongService.ImportResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Import ").append(mode).append(" hoàn tất.\n")
                .append("- Tổng dòng file: ").append(result.totalRows()).append('\n')
                .append("- Dòng xử lý hợp lệ: ").append(result.processedRows()).append('\n')
                .append("- Bản ghi điểm thi cập nhật: ").append(result.updatedDiemThiRows()).append('\n')
                .append("- Bản ghi điểm cộng cập nhật: ").append(result.updatedDiemCongRows());

        List<String> errors = result.errors();
        if (errors != null && !errors.isEmpty()) {
            sb.append("\n\nCó ").append(errors.size()).append(" dòng lỗi. 5 lỗi đầu:\n");
            int max = Math.min(5, errors.size());
            for (int i = 0; i < max; i++) {
                sb.append(i + 1).append(". ").append(errors.get(i)).append('\n');
            }
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Kết quả import " + mode,
                JOptionPane.INFORMATION_MESSAGE);
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
        e.setGhichu(currentGhichu);
        e.setDcKeys(currentDcKeys);
        return e;
    }

    private void clearForm() {
        idField.setText(""); cccdField.setText(""); nganhField.setText("");
        tohopField.setText(""); phuongthucField.setText("");
        diemCCField.setText(""); diemUtxtField.setText(""); diemTongField.setText("");
        nguyenVongField.setText(""); thuTuNvField.setText("");
        currentGhichu = ""; currentDcKeys = "";
        table.clearSelection();
    }

    private String str(Object v) { return v == null ? "" : v.toString(); }
    private String bd(BigDecimal v) { return v == null ? "" : v.toPlainString(); }
    private String safe(String v) { return v == null ? "" : v.trim(); }
    private BigDecimal parseBD(String s) {
        try { return s == null || s.isBlank() ? null : new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private XtDiemcongxetuyen findSelectedEntity() {
        int row = table.getSelectedRow();
        if (row < 0) return null;

        int modelRow = table.convertRowIndexToModel(row);
        Integer selectedId = parseInteger(str(tableModel.getValueAt(modelRow, 0)));
        if (selectedId != null) {
            for (XtDiemcongxetuyen entity : filteredRows) {
                if (entity != null && selectedId.equals(entity.getIddiemcong())) {
                    return entity;
                }
            }
        }

        int absoluteIndex = (currentPage - 1) * pageSize + modelRow;
        if (absoluteIndex >= 0 && absoluteIndex < filteredRows.size()) {
            return filteredRows.get(absoluteIndex);
        }
        return null;
    }

    private Integer parseInteger(String value) {
        try { return Integer.parseInt(value); }
        catch (Exception ex) { return null; }
    }

    private NguyenVongInfo resolveNguyenVongInfo(XtDiemcongxetuyen diemCong) {
        if (diemCong == null) return new NguyenVongInfo(KHONG_CO, KHONG_CO);
        String cccd = safe(diemCong.getTsCccd());
        if (cccd.isEmpty()) return new NguyenVongInfo(KHONG_CO, KHONG_CO);

        String cacheKey = cccd.toLowerCase(Locale.ROOT);
        List<XtNguyenvongxettuyen> wishes = nguyenVongCacheByCccd.computeIfAbsent(cacheKey,
                k -> nguyenVongService.findByCccd(cccd));
        if (wishes == null || wishes.isEmpty()) return new NguyenVongInfo(KHONG_CO, KHONG_CO);

        String maNganh = normalizeKey(diemCong.getManganh());
        XtNguyenvongxettuyen matchedNv = null;
        int bestOrder = Integer.MAX_VALUE;
        for (XtNguyenvongxettuyen nv : wishes) {
            if (nv == null) continue;
            if (!maNganh.equals(normalizeKey(nv.getNvManganh()))) continue;
            int order = uuTienThuTu(nv.getNvTt());
            if (matchedNv == null || order < bestOrder) {
                matchedNv = nv;
                bestOrder = order;
            }
        }
        if (matchedNv == null) return new NguyenVongInfo(KHONG_CO, KHONG_CO);
        return toNguyenVongInfo(matchedNv);
    }

    private int uuTienThuTu(Integer nvTt) {
        return nvTt == null ? Integer.MAX_VALUE : nvTt;
    }

    private NguyenVongInfo toNguyenVongInfo(XtNguyenvongxettuyen nv) {
        String maNganh = safe(nv.getNvManganh());
        String tenNganh = resolveTenNganh(maNganh);
        String nguyenVong = tenNganh.isEmpty() ? KHONG_CO : tenNganh;
        String thuTuNv = nv.getNvTt() == null ? KHONG_CO : String.valueOf(nv.getNvTt());
        return new NguyenVongInfo(nguyenVong, thuTuNv);
    }

    private String normalizeKey(String value) {
        return safe(value).toLowerCase(Locale.ROOT);
    }

    private String resolveTenNganh(String maNganh) {
        String key = normalizeKey(maNganh);
        if (key.isEmpty()) return "";
        if (tenNganhByMaNganh.isEmpty()) {
            for (XtNganh nganh : nganhHocService.getAll()) {
                if (nganh == null) continue;
                String ma = normalizeKey(nganh.getManganh());
                if (!ma.isEmpty()) {
                    tenNganhByMaNganh.put(ma, safe(nganh.getTennganh()));
                }
            }
        }
        return safe(tenNganhByMaNganh.get(key));
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

        idField.setEditable(false);
        nguyenVongField.setEditable(false);
        thuTuNvField.setEditable(false);

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
        formPanel.add(new JLabel("Nguyện vọng"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        formPanel.add(nguyenVongField, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Thứ tự NV"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        formPanel.add(thuTuNvField, gbc);

        return formPanel;
    }

    private static class NguyenVongInfo {
        private final String nguyenVong;
        private final String thuTuNv;

        private NguyenVongInfo(String nguyenVong, String thuTuNv) {
            this.nguyenVong = nguyenVong;
            this.thuTuNv = thuTuNv;
        }
    }
}
