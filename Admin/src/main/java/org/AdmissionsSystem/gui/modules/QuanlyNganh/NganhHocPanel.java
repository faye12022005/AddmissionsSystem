package org.AdmissionsSystem.gui.modules.QuanlyNganh;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocSearchPanel;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocTable;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.service.NganhHocCsvService;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NganhHocPanel extends JPanel {
    private static final String[] COLS = {
            "Mã ngành", "Tên ngành", "Tổ hợp gốc", "Chỉ tiêu", "Điểm sàn", "Điểm trúng tuyển",
            "Tuyển thẳng", "Sử dụng DGNL", "Sử dụng THPT", "Sử dụng VSAT",
            "SL xét tuyển", "SL DGNL", "SL VSAT", "SL THPT"
    };

    private final List<Object[]> allRows = new ArrayList<>();
    private List<Object[]> filteredRows = new ArrayList<>();

    private final DefaultTableModel tableModel;
    private final NganhHocTable tableView;
    private final NganhHocSearchPanel searchPanel;
    private final NganhHocCsvService csvService = new NganhHocCsvService();
    private final JLabel lblPageInfo = new JLabel("Trang 1/1", SwingConstants.RIGHT);
    private final JComboBox<Integer> cboPageSize = new JComboBox<>(new Integer[]{10, 20, 50, 100});

    private int currentPage = 1;
    private int pageSize = 20;

    private final JTextField txtMa = new JTextField(12);
    private final JTextField txtTen = new JTextField(24);
    private final JTextField txtToHopGoc = new JTextField(10);
    private final JTextField txtChiTieu = new JTextField(6);
    private final JTextField txtDiemSan = new JTextField(6);
    private final JTextField txtDiemTrungTuyen = new JTextField(6);
    private final JCheckBox chkTuyenThang = new JCheckBox("Tuyển thẳng");
    private final JCheckBox chkDGNL = new JCheckBox("Sử dụng DGNL");
    private final JCheckBox chkTHPT = new JCheckBox("Sử dụng THPT");
    private final JCheckBox chkVSAT = new JCheckBox("Sử dụng VSAT");
    private final JTextField txtSlXetTuyen = new JTextField(8);
    private final JTextField txtSlDGNL = new JTextField(8);
    private final JTextField txtSlVSAT = new JTextField(8);
    private final JTextField txtSlTHPT = new JTextField(8);

    private String selectedMaNganh;

    public NganhHocPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);

        JLabel title = new JLabel("Quản lý Ngành Học");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableView = new NganhHocTable(tableModel);
        searchPanel = new NganhHocSearchPanel();

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(buildCrudPanel(), BorderLayout.NORTH);
        body.add(tableView, BorderLayout.CENTER);
        body.add(buildPaginationPanel(), BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);

        bindEvents();
        seedData();
        applyFilter("");
    }

    private JPanel buildCrudPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(6, 6));
        wrapper.setOpaque(false);

        JPanel filterRow = new JPanel();
        filterRow.setLayout(new BoxLayout(filterRow, BoxLayout.X_AXIS));
        filterRow.setOpaque(false);
        JButton btnResetFilter = new JButton("Xóa lọc");
        JButton btnImport = new JButton("Import CSV");
        JButton btnExport = new JButton("Export CSV");
        Style.styleButton(btnResetFilter);
        Style.styleButton(btnImport);
        Style.styleButton(btnExport);
        btnResetFilter.addActionListener(e -> {
            searchPanel.setSearchText("");
            applyFilter("");
        });
        btnImport.addActionListener(e -> onImportCsv());
        btnExport.addActionListener(e -> onExportCsv());
        filterRow.add(searchPanel);
        filterRow.add(Box.createRigidArea(new Dimension(8, 0)));
        filterRow.add(btnImport);
        filterRow.add(Box.createRigidArea(new Dimension(8, 0)));
        filterRow.add(btnExport);
        filterRow.add(Box.createRigidArea(new Dimension(8, 0)));
        filterRow.add(btnResetFilter);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setOpaque(false);
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

        actionPanel.add(btnAdd);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        actionPanel.add(btnUpdate);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        actionPanel.add(btnDelete);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        actionPanel.add(btnClear);

        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setOpaque(false);
        topRow.add(filterRow, BorderLayout.WEST);
        topRow.add(actionPanel, BorderLayout.EAST);

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
        addField(formRow, gbc, r, "Mã ngành", txtMa, 0);
        addField(formRow, gbc, r, "Tên ngành", txtTen, 2);
        addField(formRow, gbc, r, "Tổ hợp gốc", txtToHopGoc, 4);
        r++;
        addField(formRow, gbc, r, "Chỉ tiêu", txtChiTieu, 0);
        addField(formRow, gbc, r, "Điểm sàn", txtDiemSan, 2);
        addField(formRow, gbc, r, "Điểm trúng tuyển", txtDiemTrungTuyen, 4);
        r++;
        addField(formRow, gbc, r, "SL xét tuyển", txtSlXetTuyen, 0);
        addField(formRow, gbc, r, "SL DGNL", txtSlDGNL, 2);
        addField(formRow, gbc, r, "SL VSAT", txtSlVSAT, 4);
        r++;
        addField(formRow, gbc, r, "SL THPT", txtSlTHPT, 0);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(chkTuyenThang);
        checkPanel.add(chkDGNL);
        checkPanel.add(chkTHPT);
        checkPanel.add(chkVSAT);
        gbc.gridx = 2;
        gbc.gridy = r;
        gbc.gridwidth = 4;
        formRow.add(checkPanel, gbc);

        wrapper.add(topRow, BorderLayout.NORTH);
        wrapper.add(formRow, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildPaginationPanel() {
        JPanel pager = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pager.setOpaque(false);

        JLabel lblSize = new JLabel("Số dòng/trang");
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        Style.styleButton(btnPrev);
        Style.styleButton(btnNext);

        cboPageSize.setSelectedItem(pageSize);
        cboPageSize.addActionListener(e -> {
            Integer selected = (Integer) cboPageSize.getSelectedItem();
            if (selected != null) {
                pageSize = selected;
                currentPage = 1;
                loadPage();
            }
        });

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

        pager.add(lblSize);
        pager.add(cboPageSize);
        pager.add(btnPrev);
        pager.add(btnNext);
        pager.add(lblPageInfo);
        return pager;
    }

    private void bindEvents() {
        searchPanel.addActionListener(e -> applyFilter(searchPanel.getSearchText()));
        tableView.getTable().getSelectionModel().addListSelectionListener(this::onTableRowSelected);
    }

    private void onTableRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int row = tableView.getTable().getSelectedRow();
        if (row < 0) {
            return;
        }

        txtMa.setText(asText(tableModel.getValueAt(row, 0)));
        txtTen.setText(asText(tableModel.getValueAt(row, 1)));
        txtToHopGoc.setText(asText(tableModel.getValueAt(row, 2)));
        txtChiTieu.setText(asText(tableModel.getValueAt(row, 3)));
        txtDiemSan.setText(asText(tableModel.getValueAt(row, 4)));
        txtDiemTrungTuyen.setText(asText(tableModel.getValueAt(row, 5)));
        chkTuyenThang.setSelected("Y".equals(asText(tableModel.getValueAt(row, 6))));
        chkDGNL.setSelected("Y".equals(asText(tableModel.getValueAt(row, 7))));
        chkTHPT.setSelected("Y".equals(asText(tableModel.getValueAt(row, 8))));
        chkVSAT.setSelected("Y".equals(asText(tableModel.getValueAt(row, 9))));
        txtSlXetTuyen.setText(asText(tableModel.getValueAt(row, 10)));
        txtSlDGNL.setText(asText(tableModel.getValueAt(row, 11)));
        txtSlVSAT.setText(asText(tableModel.getValueAt(row, 12)));
        txtSlTHPT.setText(asText(tableModel.getValueAt(row, 13)));

        selectedMaNganh = txtMa.getText().trim();
    }

    private void onAdd() {
        try {
            Object[] row = collectFormData();
            String ma = asText(row[0]);
            if (findIndexByMa(ma) >= 0) {
                JOptionPane.showMessageDialog(this, "Mã ngành đã tồn tại.");
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
        if (selectedMaNganh == null || selectedMaNganh.isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần cập nhật trước.");
            return;
        }
        try {
            Object[] row = collectFormData();
            String newMa = asText(row[0]);

            int selectedIndex = findIndexByMa(selectedMaNganh);
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi cần cập nhật.");
                return;
            }

            int duplicateIndex = findIndexByMa(newMa);
            if (duplicateIndex >= 0 && duplicateIndex != selectedIndex) {
                JOptionPane.showMessageDialog(this, "Mã ngành mới đã tồn tại.");
                return;
            }

            allRows.set(selectedIndex, row);
            selectedMaNganh = newMa;
            applyFilter(searchPanel.getSearchText());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedMaNganh == null || selectedMaNganh.isBlank()) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa trước.");
            return;
        }
        int index = findIndexByMa(selectedMaNganh);
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi cần xóa.");
            return;
        }
        allRows.remove(index);
        applyFilter(searchPanel.getSearchText());
        clearForm();
    }

    private Object[] collectFormData() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String toHop = txtToHopGoc.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || toHop.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành, Tên ngành, Tổ hợp gốc là bắt buộc.");
        }

        int chiTieu = parseInt(txtChiTieu, "Chỉ tiêu");
        double diemSan = parseDouble(txtDiemSan, "Điểm sàn");
        double diemTrungTuyen = parseDouble(txtDiemTrungTuyen, "Điểm trúng tuyển");
        int slXetTuyen = parseInt(txtSlXetTuyen, "SL xét tuyển");
        int slDGNL = parseInt(txtSlDGNL, "SL DGNL");
        int slVSAT = parseInt(txtSlVSAT, "SL VSAT");
        int slTHPT = parseInt(txtSlTHPT, "SL THPT");

        return new Object[]{
                ma, ten, toHop, chiTieu, diemSan, diemTrungTuyen,
                toYN(chkTuyenThang.isSelected()), toYN(chkDGNL.isSelected()),
                toYN(chkTHPT.isSelected()), toYN(chkVSAT.isSelected()),
                slXetTuyen, slDGNL, slVSAT, slTHPT
        };
    }

    private void seedData() {
        allRows.clear();
        allRows.add(new Object[]{"CNTT", "Công nghệ thông tin", "A00", 100, 20.0, 25.0, "N", "Y", "Y", "N", 1000, 200, 50, 750});
        allRows.add(new Object[]{"KT", "Kinh tế", "C00", 80, 18.0, 22.0, "N", "N", "Y", "N", 800, 0, 0, 800});
        allRows.add(new Object[]{"DTVT", "Điện tử viễn thông", "A01", 70, 19.0, 23.0, "N", "Y", "Y", "N", 650, 120, 30, 500});
        allRows.add(new Object[]{"QTKD", "Quản trị kinh doanh", "D01", 120, 18.0, 24.0, "Y", "N", "Y", "Y", 1200, 0, 100, 1100});
        allRows.add(new Object[]{"TCKT", "Tài chính kế toán", "A00", 95, 18.5, 23.5, "N", "Y", "Y", "N", 900, 140, 20, 740});
        allRows.add(new Object[]{"SPTOAN", "Sư phạm Toán", "A00", 60, 19.0, 24.0, "Y", "N", "Y", "N", 420, 0, 0, 420});
        allRows.add(new Object[]{"SPANH", "Sư phạm Anh", "D01", 55, 19.5, 24.8, "Y", "N", "Y", "Y", 410, 0, 80, 330});
    }

    private void applyFilter(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        filteredRows = new ArrayList<>();
        for (Object[] row : allRows) {
            String ma = asText(row[0]).toLowerCase();
            String ten = asText(row[1]).toLowerCase();
            String toHop = asText(row[2]).toLowerCase();
            if (q.isEmpty() || ma.contains(q) || ten.contains(q) || toHop.contains(q)) {
                filteredRows.add(cloneRow(row));
            }
        }
        currentPage = 1;
        loadPage();
    }

    private void loadPage() {
        tableModel.setRowCount(0);
        if (filteredRows.isEmpty()) {
            lblPageInfo.setText("Trang 1/1 - Tổng 0 bản ghi");
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
        txtMa.setText("");
        txtTen.setText("");
        txtToHopGoc.setText("");
        txtChiTieu.setText("");
        txtDiemSan.setText("");
        txtDiemTrungTuyen.setText("");
        txtSlXetTuyen.setText("");
        txtSlDGNL.setText("");
        txtSlVSAT.setText("");
        txtSlTHPT.setText("");
        chkTuyenThang.setSelected(false);
        chkDGNL.setSelected(false);
        chkTHPT.setSelected(false);
        chkVSAT.setSelected(false);
        selectedMaNganh = null;
        tableView.getTable().clearSelection();
    }

    private void onImportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file CSV ngành học");
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
            String ma = asText(imported[0]);
            int index = findIndexByMa(ma);
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
        chooser.setDialogTitle("Lưu file CSV ngành học");
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

    private int findIndexByMa(String maNganh) {
        for (int i = 0; i < allRows.size(); i++) {
            if (asText(allRows.get(i)[0]).equalsIgnoreCase(maNganh)) {
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

    private int parseInt(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private double parseDouble(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số thực.");
        }
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field, int col) {
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
        return value == null ? "" : value.toString();
    }

    private String toYN(boolean selected) {
        return selected ? "Y" : "N";
    }
}


