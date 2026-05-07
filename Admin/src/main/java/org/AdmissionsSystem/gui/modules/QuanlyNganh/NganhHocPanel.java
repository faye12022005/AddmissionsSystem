package org.AdmissionsSystem.gui.modules.QuanlyNganh;
import org.AdmissionsSystem.bus.controller.NganhHocController;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocFormPanel;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocPaginationPanel;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocSearchPanel;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.components.NganhHocTable;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.mapper.NganhHocRowMapper;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.service.NganhHocCsvService;
import org.AdmissionsSystem.models.XtNganh;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
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

    private List<XtNganh> filteredRows = new ArrayList<>();
    private List<XtNganh> currentPageRows = new ArrayList<>();
    private final NganhHocController nganhHocController = new NganhHocController();

    private final DefaultTableModel tableModel;
    private final NganhHocTable tableView;
    private final NganhHocSearchPanel searchPanel;
    private final NganhHocFormPanel formPanel;
    private final NganhHocPaginationPanel paginationPanel;
    private final NganhHocCsvService csvService = new NganhHocCsvService();
    private final NganhHocRowMapper rowMapper = new NganhHocRowMapper();

    private int currentPage = 1;
    private int pageSize = 20;

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
    formPanel = new NganhHocFormPanel();
    paginationPanel = new NganhHocPaginationPanel(pageSize);

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(buildCrudPanel(), BorderLayout.NORTH);
        body.add(tableView, BorderLayout.CENTER);
    body.add(paginationPanel, BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);

        bindEvents();
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
        Style.styleFunctionButton(btnResetFilter, Style.BTN_FILTER_RESET);
        Style.styleFunctionButton(btnImport, Style.BTN_IMPORT);
        Style.styleFunctionButton(btnExport, Style.BTN_EXPORT);
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
        Style.styleFunctionButton(btnAdd, Style.BTN_ADD);
        Style.styleFunctionButton(btnUpdate, Style.BTN_UPDATE);
        Style.styleFunctionButton(btnDelete, Style.BTN_DELETE);
        Style.styleFunctionButton(btnClear, Style.BTN_CLEAR);

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

        wrapper.add(topRow, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
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
        if (row < 0 || row >= currentPageRows.size()) {
            return;
        }

        XtNganh selectedRow = currentPageRows.get(row);
        formPanel.setFormData(selectedRow);
        selectedMaNganh = selectedRow.getManganh();
    }

    private void onAdd() {
        try {
            XtNganh model = formPanel.collectFormData();
            nganhHocController.add(model);
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
            XtNganh model = formPanel.collectFormData();
            nganhHocController.update(selectedMaNganh, model);
            selectedMaNganh = model.getManganh();
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
        try {
            nganhHocController.deleteByMaNganh(selectedMaNganh);
            applyFilter(searchPanel.getSearchText());
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void applyFilter(String keyword) {
        filteredRows = nganhHocController.search(keyword);
        currentPage = 1;
        loadPage();
    }

    private void loadPage() {
        tableModel.setRowCount(0);
        currentPageRows = new ArrayList<>();
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
            XtNganh model = filteredRows.get(i);
            currentPageRows.add(model);
            tableModel.addRow(rowMapper.toRow(model));
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
            nganhHocController.upsert(rowMapper.fromRow(imported));
        }

        applyFilter(searchPanel.getSearchText());
        JOptionPane.showMessageDialog(this, "Import thành công " + importedRows.size() + " dòng.");
    }

    private void onExportCsv() {
        List<Object[]> source = new ArrayList<>();
        for (XtNganh model : filteredRows) {
            source.add(rowMapper.toRow(model));
        }
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
}


