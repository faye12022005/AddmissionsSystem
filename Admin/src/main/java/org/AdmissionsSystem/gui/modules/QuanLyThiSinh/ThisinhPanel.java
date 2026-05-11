package org.AdmissionsSystem.gui.modules.QuanLyThiSinh;

import org.AdmissionsSystem.bus.service.ThiSinhService;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.ImportExcel;
import org.AdmissionsSystem.gui.components.SearchPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ThisinhPanel extends JPanel {

    private static final String[] COLS = {
            "CCCD", "SBD", "Họ", "Tên", "Ngày sinh", "Điện thoại",
            "Giới tính", "Email", "Nơi sinh", "Đối tượng", "Khu vực"
    };

    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final List<Object[]> allRows = new ArrayList<>();
    private List<Object[]> filteredRows = new ArrayList<>();
    private String currentKeyword = "";

    private final DefaultTableModel tableModel;
    private final CustomTable customTable;
    private final SearchPanel searchPanel;

    private final JLabel lblPageInfo = new JLabel("Trang 1/1", SwingConstants.RIGHT);

    private int currentPage = 1;
    private final int pageSize = 20;

    private final JTextField txtCccd = new JTextField(12);
    private final JTextField txtSbd = new JTextField(10);
    private final JTextField txtHo = new JTextField(15);
    private final JTextField txtTen = new JTextField(10);
    private final JTextField txtNgaySinh = new JTextField(10);
    private final JTextField txtDienThoai = new JTextField(12);
    private final JComboBox<String> cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ", "Khác" });
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtNoiSinh = new JTextField(15);
    private final JTextField txtDoiTuong = new JTextField(10);
    private final JTextField txtKhuVuc = new JTextField(10);

    private String selectedCccd;

    public ThisinhPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Thí Sinh");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customTable = new CustomTable(tableModel);
        searchPanel = new SearchPanel(350, "Nhập CCCD hoặc Họ Tên...", "Tìm kiếm");

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        body.add(buildCrudPanel(), BorderLayout.NORTH);
        body.add(customTable, BorderLayout.CENTER);
        body.add(buildPaginationPanel(), BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);

        bindEvents();
        loadFromDb("");
    }

    private void applyStyle(JButton btn) {
        Style.styleFunctionButton(btn);
    }

    private void applyStyle(JButton btn, Color color) {
        Style.styleFunctionButton(btn, color);
    }

    private JPanel buildCrudPanel() {
        // Wrapper chính chứa toàn bộ hàng nút
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // --- NHÓM BÊN TRÁI: Tìm kiếm, Import, Export, Xóa lọc ---
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftGroup.setOpaque(false);

        JButton btnImport = new JButton("Import");
        JButton btnExport = new JButton("Export");
        JButton btnResetFilter = new JButton("Xóa lọc");

        applyStyle(btnImport, Style.BTN_IMPORT);
        applyStyle(btnExport, Style.BTN_EXPORT);
        applyStyle(btnResetFilter, Style.BTN_FILTER_RESET);

        btnImport.addActionListener(e -> onImportExcel());
        btnExport.addActionListener(e -> onExportExcel());
        btnResetFilter.addActionListener(e -> {
            searchPanel.setSearchText("");
            applyFilter("");
        });

        leftGroup.add(searchPanel);
        leftGroup.add(btnImport);
        leftGroup.add(btnExport);
        leftGroup.add(btnResetFilter);

        // --- NHÓM BÊN PHẢI: Thêm, Cập nhật, Xóa, Làm mới ---
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightGroup.setOpaque(false);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");

        applyStyle(btnAdd, Style.BTN_ADD);
        applyStyle(btnUpdate, Style.BTN_UPDATE);
        applyStyle(btnDelete, Style.BTN_DELETE);
        applyStyle(btnClear, Style.BTN_CLEAR);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());

        rightGroup.add(btnAdd);
        rightGroup.add(btnUpdate);
        rightGroup.add(btnDelete);
        rightGroup.add(btnClear);

        // Đưa nhóm trái vào WEST, nhóm phải vào EAST của BorderLayout
        topRow.add(leftGroup, BorderLayout.WEST);
        topRow.add(rightGroup, BorderLayout.EAST);

        // --- Bọc lại để kết hợp với Form nhập liệu bên dưới ---
        JPanel wrapper = new JPanel(new BorderLayout(6, 6));
        wrapper.setOpaque(false);
        wrapper.add(topRow, BorderLayout.NORTH);

        // --- Form nhập liệu (Giữ nguyên phần GridBagLayout của bạn) ---
        JPanel formRow = new JPanel(new GridBagLayout());
        formRow.setOpaque(false);
        formRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addField(formRow, gbc, r, "CCCD (*)", txtCccd, 0);
        addField(formRow, gbc, r, "SBD", txtSbd, 2);
        addField(formRow, gbc, r, "Họ (*)", txtHo, 4);
        addField(formRow, gbc, r, "Tên (*)", txtTen, 6);
        r++;
        addField(formRow, gbc, r, "Ngày sinh", txtNgaySinh, 0);
        addField(formRow, gbc, r, "Điện thoại", txtDienThoai, 2);
        gbc.gridx = 4;
        gbc.gridy = r;
        formRow.add(new JLabel("Giới tính"), gbc);
        gbc.gridx = 5;
        formRow.add(cboGioiTinh, gbc);
        addField(formRow, gbc, r, "Nơi sinh", txtNoiSinh, 6);
        r++;
        addField(formRow, gbc, r, "Email", txtEmail, 0);
        addField(formRow, gbc, r, "Đối tượng", txtDoiTuong, 2);
        addField(formRow, gbc, r, "Khu vực", txtKhuVuc, 4);

        wrapper.add(formRow, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildPaginationPanel() {
        JPanel pager = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        pager.setOpaque(false);

        JButton btnPrev = new JButton("< Trang trước");
        JButton btnNext = new JButton("Trang sau >");

        Style.stylePaginationButton(btnPrev);
        Style.stylePaginationButton(btnNext);
        Style.stylePaginationInfoLabel(lblPageInfo);

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPage();
            }
        });

        pager.add(btnPrev);
        pager.add(lblPageInfo);
        pager.add(btnNext);

        return pager;
    }

    private void bindEvents() {
        customTable.getTable().getSelectionModel().addListSelectionListener(this::onTableRowSelected);
        searchPanel.addActionListener(e -> applyFilter(searchPanel.getSearchText()));
    }

    private void onTableRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int row = customTable.getTable().getSelectedRow();
        if (row < 0) {
            return;
        }

        txtCccd.setText(asText(tableModel.getValueAt(row, 0)));
        txtSbd.setText(asText(tableModel.getValueAt(row, 1)));
        txtHo.setText(asText(tableModel.getValueAt(row, 2)));
        txtTen.setText(asText(tableModel.getValueAt(row, 3)));
        txtNgaySinh.setText(asText(tableModel.getValueAt(row, 4)));
        txtDienThoai.setText(asText(tableModel.getValueAt(row, 5)));
        cboGioiTinh.setSelectedItem(asText(tableModel.getValueAt(row, 6)));
        txtEmail.setText(asText(tableModel.getValueAt(row, 7)));
        txtNoiSinh.setText(asText(tableModel.getValueAt(row, 8)));
        txtDoiTuong.setText(asText(tableModel.getValueAt(row, 9)));
        txtKhuVuc.setText(asText(tableModel.getValueAt(row, 10)));

        selectedCccd = txtCccd.getText().trim();
    }

    private void onAdd() {
        try {
            XtThisinhxettuyen25 entity = collectFormEntity();
            thiSinhService.add(entity);
            loadFromDb(currentKeyword);
            clearForm();
            ToastThiSinh.showSuccess(this, "Thêm thí sinh mới thành công!");
        } catch (IllegalArgumentException ex) {
            ToastThiSinh.showError(this, ex.getMessage());
        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi thêm thí sinh: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedCccd == null || selectedCccd.isBlank()) {
            ToastThiSinh.showError(this, "Vui lòng chọn thí sinh cần cập nhật từ bảng.");
            return;
        }
        try {
            XtThisinhxettuyen25 existing = thiSinhService.findByCccd(selectedCccd);
            if (existing == null) {
                ToastThiSinh.showError(this, "Không tìm thấy dữ liệu để cập nhật.");
                return;
            }
            XtThisinhxettuyen25 entity = collectFormEntity();
            entity.setIdthisinh(existing.getIdthisinh());
            thiSinhService.update(entity);
            selectedCccd = entity.getCccd();
            loadFromDb(currentKeyword);
            ToastThiSinh.showSuccess(this, "Cập nhật thông tin thành công!");
        } catch (IllegalArgumentException ex) {
            ToastThiSinh.showError(this, ex.getMessage());
        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedCccd == null || selectedCccd.isBlank()) {
            ToastThiSinh.showError(this, "Vui lòng chọn dòng cần xóa trước.");
            return;
        }
        try {
            XtThisinhxettuyen25 existing = thiSinhService.findByCccd(selectedCccd);
            if (existing == null) {
                ToastThiSinh.showError(this, "Không tìm thấy bản ghi cần xóa.");
                return;
            }
            String hoTen = asText(existing.getHo()) + " " + asText(existing.getTen());
            if (ToastThiSinh.showConfirmDelete(this, hoTen)) {
                thiSinhService.delete(existing.getIdthisinh());
                loadFromDb(currentKeyword);
                clearForm();
                ToastThiSinh.showSuccess(this, "Đã xóa thí sinh thành công.");
            }
        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi xóa: " + ex.getMessage());
        }
    }

    private void loadFromDb(String keyword) {
        currentKeyword = keyword == null ? "" : keyword.trim();
        try {
            List<XtThisinhxettuyen25> entities = thiSinhService.search(currentKeyword);
            allRows.clear();
            for (XtThisinhxettuyen25 ts : entities) {
                allRows.add(entityToRow(ts));
            }
            filteredRows = new ArrayList<>(allRows);
            currentPage = 1;
            loadPage();
        } catch (Exception e) {
            ToastThiSinh.showError(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private Object[] entityToRow(XtThisinhxettuyen25 ts) {
        return new Object[]{
            asText(ts.getCccd()), asText(ts.getSobaodanh()), asText(ts.getHo()), asText(ts.getTen()),
            asText(ts.getNgaySinh()), asText(ts.getDienThoai()), asText(ts.getGioiTinh()),
            asText(ts.getEmail()), asText(ts.getNoiSinh()), asText(ts.getDoiTuong()), asText(ts.getKhuVuc())
        };
    }

    private XtThisinhxettuyen25 collectFormEntity() {
        String cccd = txtCccd.getText().trim();
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        if (cccd.isEmpty() || ho.isEmpty() || ten.isEmpty()) {
            throw new IllegalArgumentException("CCCD, Họ và Tên là bắt buộc.");
        }
        XtThisinhxettuyen25 ts = new XtThisinhxettuyen25();
        ts.setCccd(cccd);
        // Sử dụng toNull để chuyển "" thành null trước khi lưu
        ts.setSobaodanh(toNull(txtSbd.getText()));
        ts.setHo(ho);
        ts.setTen(ten);
        ts.setNgaySinh(toNull(txtNgaySinh.getText()));
        ts.setDienThoai(toNull(txtDienThoai.getText()));
        ts.setGioiTinh(cboGioiTinh.getSelectedItem() != null ? cboGioiTinh.getSelectedItem().toString() : "Nam");
        ts.setEmail(toNull(txtEmail.getText()));
        ts.setNoiSinh(toNull(txtNoiSinh.getText()));
        ts.setDoiTuong(toNull(txtDoiTuong.getText()));
        ts.setKhuVuc(toNull(txtKhuVuc.getText()));
        return ts;
    }

    private String toNull(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void applyFilter(String keyword) {
        loadFromDb(keyword);
    }

    private void loadPage() {
        tableModel.setRowCount(0);

        if (filteredRows.isEmpty()) {
            lblPageInfo.setText("Trang 1/1");
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

        lblPageInfo.setText("Trang " + currentPage + "/" + totalPages + " - Tổng " + filteredRows.size() + " bản ghi");
    }

    private int getTotalPages() {
        if (filteredRows.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil(filteredRows.size() * 1.0 / pageSize);
    }

    private void clearForm() {
        txtCccd.setText("");
        txtSbd.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtNgaySinh.setText("");
        txtDienThoai.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtEmail.setText("");
        txtNoiSinh.setText("");
        txtDoiTuong.setText("");
        txtKhuVuc.setText("");

        selectedCccd = null;
        customTable.getTable().clearSelection();
    }

    private void onImportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file CSV Thí Sinh");
        chooser.setFileFilter(new FileNameExtensionFilter("Data file (*.csv)", "csv"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path path = chooser.getSelectedFile().toPath();
        List<Object[]> importedRows = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Loại bỏ ký tự BOM nếu có ở đầu file
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }

                String[] values = line.split(",", -1);
                if (values.length >= COLS.length) {
                    Object[] row = new Object[COLS.length];
                    System.arraycopy(values, 0, row, 0, COLS.length);
                    importedRows.add(row);
                }
            }
        } catch (IOException ex) {
            ToastThiSinh.showError(this, "Không thể đọc file: " + ex.getMessage());
            return;
        }

        if (importedRows.isEmpty()) {
            ToastThiSinh.showError(this, "File không hợp lệ hoặc không có dữ liệu.");
            return;
        }

        // Convert to entities and batch import via service
        try {
            List<XtThisinhxettuyen25> entities = new ArrayList<>();
            for (Object[] imported : importedRows) {
                XtThisinhxettuyen25 ts = new XtThisinhxettuyen25();
                ts.setCccd(toNull(asText(imported[0])));
                ts.setSobaodanh(toNull(asText(imported[1])));
                ts.setHo(toNull(asText(imported[2])));
                ts.setTen(toNull(asText(imported[3])));
                ts.setNgaySinh(toNull(asText(imported[4])));
                ts.setDienThoai(toNull(asText(imported[5])));
                ts.setGioiTinh(toNull(asText(imported[6])));
                ts.setEmail(toNull(asText(imported[7])));
                ts.setNoiSinh(toNull(asText(imported[8])));
                ts.setDoiTuong(toNull(asText(imported[9])));
                ts.setKhuVuc(toNull(asText(imported[10])));
                entities.add(ts);
            }
            thiSinhService.importBatch(entities);
            loadFromDb(currentKeyword);
            ToastThiSinh.showSuccess(this, "Import thành công " + importedRows.size() + " dòng dữ liệu!");
        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi import: " + ex.getMessage());
        }
    }

    private void onExportExcel() {
        List<Object[]> source = filteredRows.isEmpty() ? allRows : filteredRows;
        if (source.isEmpty()) {
            ToastThiSinh.showError(this, "Không có dữ liệu để xuất file.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file Thí Sinh");
        chooser.setFileFilter(new FileNameExtensionFilter("Data file (*.csv)", "csv"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path out = chooser.getSelectedFile().toPath();
        if (!out.toString().toLowerCase().endsWith(".csv")) {
            out = Path.of(out.toString() + ".csv");
        }

        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            // Ghi ký tự BOM để Excel mở lên không bị lỗi font tiếng Việt
            bw.write("\uFEFF");

            // Ghi tiêu đề cột
            bw.write(String.join(",", COLS));
            bw.newLine();

            // Ghi dữ liệu thí sinh
            for (Object[] row : source) {
                String[] strRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    // Thay thế dấu phẩy trong dữ liệu bằng khoảng trắng để tránh lệch cột CSV
                    strRow[i] = asText(row[i]).replace(",", " ");
                }
                bw.write(String.join(",", strRow));
                bw.newLine();
            }

            ToastThiSinh.showSuccess(this, "Xuất danh sách (" + source.size() + " dòng) ra file CSV thành công!");
        } catch (IOException ex) {
            ToastThiSinh.showError(this, "Không thể ghi file: " + ex.getMessage());
        }
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = col + 1;
        gbc.weightx = 0.4;
        panel.add(field, gbc);
        gbc.weightx = 0;
    }

    private String asText(Object value) {
        return value == null ? "-" : value.toString();
    }
}