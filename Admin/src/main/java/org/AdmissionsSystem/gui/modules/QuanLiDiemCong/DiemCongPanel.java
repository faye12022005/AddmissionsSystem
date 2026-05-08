package org.AdmissionsSystem.gui.modules.QuanLiDiemCong;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components.*;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.service.DiemCongCsvService;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DiemCongPanel extends JPanel {
    private static final String[] COLS = {
            "ID Điểm Cộng", "TS CCCD", "Mã ngành", "Mã tổ hợp", "Phương thức", "Điểm CC", "Điểm UTXT", "Điểm tổng", "Ghi chú", "DC Keys"
    };

    private final List<Object[]> allRows = new ArrayList<>();
    private List<Object[]> filteredRows = new ArrayList<>();

    private final DefaultTableModel tableModel;
    private final DiemCongTable tableView;
    private final DiemCongSearchPanel searchPanel;
    private final DiemCongFormPanel formPanel;
    private final DiemCongPaginationPanel paginationPanel;
    private final DiemCongCsvService csvService = new DiemCongCsvService();

    private int currentPage = 1;
    private int pageSize = 20;
    private int selectedDiemCongId = -1;

    public DiemCongPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Điểm cộng");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        title.setFont(Style.TITLE_FONT);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableView = new DiemCongTable(tableModel);
        searchPanel = new DiemCongSearchPanel();
        formPanel = new DiemCongFormPanel();
        paginationPanel = new DiemCongPaginationPanel(pageSize);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(createActionPanel(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tableView, BorderLayout.CENTER);
        contentPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        bindEvents();
        seedData();
        applyFilter("");
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JButton importBtn = new JButton("Import danh sách");
        JButton addBtn = new JButton("Thêm điểm cộng");
        JButton editBtn = new JButton("Sửa điểm cộng");
        JButton deleteBtn = new JButton("Xóa điểm cộng");
        JButton exportBtn = new JButton("Export CSV");
        JButton refreshBtn = new JButton("Làm mới");

        styleButtonBlue(importBtn);
        styleButtonGreen(addBtn);
        styleButtonBlue(editBtn);
        styleButtonRed(deleteBtn);
        styleButtonBlue(exportBtn);
        styleButtonGray(refreshBtn);

        importBtn.addActionListener(e -> onImportCsv());
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        exportBtn.addActionListener(e -> onExportCsv());
        refreshBtn.addActionListener(e -> clearForm());

        actionPanel.add(importBtn);
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(exportBtn);
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
        b.setBorderPainted(false);
    }

    private void bindEvents() {
        searchPanel.addActionListener(e -> applyFilter(searchPanel.getSearchText()));
        tableView.getTable().getSelectionModel().addListSelectionListener(this::onTableRowSelected);
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

    private void onTableRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int row = tableView.getTable().getSelectedRow();
        if (row < 0) {
            return;
        }

        Object[] selectedRow = new Object[COLS.length];
        for (int i = 0; i < COLS.length; i++) {
            selectedRow[i] = tableModel.getValueAt(row, i);
        }

        formPanel.setFormDataFromRow(selectedRow);
        selectedDiemCongId = formPanel.getId();
    }

    private void onAdd() {
        try {
            Object[] row = formPanel.collectFormData();
            int id = (int) row[0];
            if (findIndexById(id) >= 0) {
                JOptionPane.showMessageDialog(this, "ID điểm cộng đã tồn tại.");
                return;
            }
            allRows.add(row);
            applyFilter(searchPanel.getSearchText());
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedDiemCongId < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần cập nhật trước.");
            return;
        }
        try {
            Object[] row = formPanel.collectFormData();
            int newId = (int) row[0];

            int selectedIndex = findIndexById(selectedDiemCongId);
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi cần cập nhật.");
                return;
            }

            int duplicateIndex = findIndexById(newId);
            if (duplicateIndex >= 0 && duplicateIndex != selectedIndex) {
                JOptionPane.showMessageDialog(this, "ID điểm cộng mới đã tồn tại.");
                return;
            }

            allRows.set(selectedIndex, row);
            selectedDiemCongId = newId;
            applyFilter(searchPanel.getSearchText());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedDiemCongId < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa trước.");
            return;
        }
        int index = findIndexById(selectedDiemCongId);
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi cần xóa.");
            return;
        }
        allRows.remove(index);
        applyFilter(searchPanel.getSearchText());
        clearForm();
    }

    private void seedData() {
        allRows.clear();
        allRows.add(new Object[]{1, "031098001234", "CNTT", "A1", "THPT", 1.5, 0.5, 2.0, "Hộ nghèo", "DC001"});
        allRows.add(new Object[]{2, "031098001235", "CNTT", "A1", "THPT", 2.0, 0.5, 2.5, "Thương binh", "DC002"});
        allRows.add(new Object[]{3, "031098001236", "KT", "D1", "THPT", 2.5, 1.0, 3.5, "Con liệt sĩ", "DC003"});
    }

    private void applyFilter(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        filteredRows = new ArrayList<>();
        for (Object[] row : allRows) {
            String cccd = asText(row[1]).toLowerCase();
            String nganh = asText(row[2]).toLowerCase();
            String tohop = asText(row[3]).toLowerCase();
            if (q.isEmpty() || cccd.contains(q) || nganh.contains(q) || tohop.contains(q)) {
                filteredRows.add(cloneRow(row));
            }
        }
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
            tableModel.addRow(filteredRows.get(i));
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

    private void clearForm() {
        formPanel.clearForm();
        selectedDiemCongId = -1;
        tableView.getTable().clearSelection();
    }

    private void onImportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file CSV điểm cộng");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV file (*.csv)", "csv"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path path = chooser.getSelectedFile().toPath();
        List<Object[]> importedRows;
        try {
            importedRows = csvService.readRows(path, COLS);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể đọc file: " + ex.getMessage());
            return;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        if (importedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "File không có dữ liệu để import.");
            return;
        }

        for (Object[] imported : importedRows) {
            int id = (int) imported[0];
            int index = findIndexById(id);
            if (index >= 0) {
                allRows.set(index, imported);
            } else {
                allRows.add(imported);
            }
        }

        applyFilter(searchPanel.getSearchText());
        JOptionPane.showMessageDialog(this, "Import thành công " + importedRows.size() + " dòng.");
    }

    private void onExportCsv() {
        List<Object[]> source = filteredRows.isEmpty() ? allRows : filteredRows;
        if (source.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file CSV điểm cộng");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV file (*.csv)", "csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path out = chooser.getSelectedFile().toPath();
        if (!out.toString().toLowerCase().endsWith(".csv")) {
            out = Path.of(out.toString() + ".csv");
        }

        try {
            csvService.writeRows(out, COLS, source);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể ghi file: " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Export thành công " + source.size() + " dòng.");
    }

    private int findIndexById(int id) {
        for (int i = 0; i < allRows.size(); i++) {
            if ((int) allRows.get(i)[0] == id) {
                return i;
            }
        }
        return -1;
    }

    private Object[] cloneRow(Object[] row) {
        Object[] copy = new Object[row.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        return copy;
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }
}

