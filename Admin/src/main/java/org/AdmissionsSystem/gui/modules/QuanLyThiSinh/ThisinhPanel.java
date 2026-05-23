package org.AdmissionsSystem.gui.modules.QuanLyThiSinh;

import org.AdmissionsSystem.bus.service.ThiSinhService;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThisinhPanel extends JPanel {

    private static final String[] COLS = {
            "ID",
            "CCCD",
            "SBD",
            "Họ",
            "Tên",
            "Ngày sinh",
            "Giới tính",
            "Thao tác"
    };

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ThiSinhService thiSinhService = new ThiSinhService();
    private String currentKeyword = "";
    private long totalRecords = 0;
    private int lastRequestId = 0;
    private JButton btnPrevPage;
    private JButton btnNextPage;
    private JComboBox<Integer> cboPageSize;

    private final DefaultTableModel tableModel;
    private final CustomTable customTable;
    private final SearchPanel searchPanel;
    private final JTextField txtIdthisinh = new JTextField(6);

    private static final String[] KV_KEYS = { "1", "2", "2NT", "3" };

    private final JLabel lblPageInfo = new JLabel("Trang 1/1", SwingConstants.RIGHT);
    private final JLabel lblStatsTotalValue = new JLabel("0", SwingConstants.CENTER);
    private final Map<Integer, JLabel> lblStatsDoiTuongGrid = new LinkedHashMap<>();
    private final Map<String, JLabel> lblStatsKhuVucGrid = new LinkedHashMap<>();
    private Map<String, Long> statsDoiTuongCounts = java.util.Collections.emptyMap();
    private Map<String, Long> statsKhuVucCounts = java.util.Collections.emptyMap();

    private int currentPage = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;

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
        // render action column as a button-like cell and handle clicks
        JTable _table = customTable.getTable();
        // set a renderer for the action column after table created
        int actionCol = tableModel.getColumnCount() - 1;
        _table.getColumnModel().getColumn(actionCol).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            private final javax.swing.JButton btn = new javax.swing.JButton("Xem chi tiết");

            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                btn.setFont(Style.TABLE_FONT);
                btn.setBackground(new java.awt.Color(245, 246, 250));
                btn.setForeground(new java.awt.Color(60, 70, 90));
                return btn;
            }
        });
        _table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = _table.rowAtPoint(e.getPoint());
                int viewCol = _table.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol < 0)
                    return;
                int modelCol = _table.convertColumnIndexToModel(viewCol);
                if (modelCol == actionCol) {
                    int modelRow = _table.convertRowIndexToModel(viewRow);
                    String cccd = asText(tableModel.getValueAt(modelRow, 1));
                    ThisinhDetailDialog.showDialog(javax.swing.SwingUtilities.getWindowAncestor(ThisinhPanel.this),
                            cccd);
                }
            }
        });
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

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setOpaque(false);
        middle.add(buildStatsPanel(), BorderLayout.NORTH);
        middle.add(formRow, BorderLayout.CENTER);

        wrapper.add(middle, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStatsPanel() {
        JPanel statsWrapper = new JPanel(new GridBagLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setPreferredSize(new Dimension(0, 132));

        lblStatsTotalValue.setFont(Style.TITLE_FONT.deriveFont(20f));
        lblStatsTotalValue.setForeground(new Color(22, 163, 74));

        JPanel totalCard = createStatsCard("Tổng thí sinh", lblStatsTotalValue);
        totalCard.setPreferredSize(new Dimension(220, 132));
        totalCard.setMinimumSize(new Dimension(220, 132));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        statsWrapper.add(totalCard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 8);
        statsWrapper.add(createStatsCard("Theo đối tượng", createDoiTuongStatsGrid()), gbc);

        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        statsWrapper.add(createStatsCard("Theo khu vực", createKhuVucStatsGrid()), gbc);
        return statsWrapper;
    }

    private JPanel createStatsCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Style.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        titleLabel.setForeground(new Color(60, 70, 90));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDoiTuongStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 4));
        grid.setOpaque(false);

        for (int dt = 1; dt <= 7; dt++) {
            JLabel valueLabel = createStatsValueLabel();
            lblStatsDoiTuongGrid.put(dt, valueLabel);
            grid.add(createStatsGridCell("DT " + dt, valueLabel));
        }

        grid.add(createEmptyStatsGridCell());
        grid.add(createEmptyStatsGridCell());
        return grid;
    }

    private JPanel createKhuVucStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 6, 4));
        grid.setOpaque(false);

        for (String kv : KV_KEYS) {
            JLabel valueLabel = createStatsValueLabel();
            lblStatsKhuVucGrid.put(kv, valueLabel);
            grid.add(createStatsGridCell("KV " + kv, valueLabel));
        }
        return grid;
    }

    private JPanel createStatsGridCell(String title, JLabel valueLabel) {
        JPanel cell = new JPanel(new BorderLayout(4, 0));
        cell.setOpaque(true);
        cell.setBackground(new Color(248, 250, 253));
        cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Style.TABLE_FONT.deriveFont(Font.BOLD, 11f));
        titleLabel.setForeground(new Color(71, 85, 105));

        cell.add(titleLabel, BorderLayout.WEST);
        cell.add(valueLabel, BorderLayout.EAST);
        return cell;
    }

    private JPanel createEmptyStatsGridCell() {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        return cell;
    }

    private JLabel createStatsValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.RIGHT);
        label.setFont(Style.TABLE_FONT.deriveFont(Font.BOLD, 12f));
        label.setForeground(new Color(37, 99, 235));
        return label;
    }

    private JPanel buildPaginationPanel() {
        JPanel pager = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pager.setOpaque(false);

        cboPageSize = new JComboBox<>(new Integer[] { 10, 20, 50, 100 });
        cboPageSize.setSelectedItem(pageSize);
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");

        Style.stylePaginationCombo(cboPageSize);
        Style.stylePaginationButton(btnPrevPage);
        Style.stylePaginationButton(btnNextPage);
        Style.stylePaginationInfoLabel(lblPageInfo);
        lblPageInfo.setText("Trang 1/1 - Tổng 0 bản ghi");

        cboPageSize.addActionListener(e -> {
            Integer selected = (Integer) cboPageSize.getSelectedItem();
            if (selected == null) {
                return;
            }
            pageSize = selected;
            currentPage = 1;
            loadPageAsync();
        });

        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPageAsync();
            }
        });

        btnNextPage.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPageAsync();
            }
        });

        pager.add(cboPageSize);
        pager.add(btnPrevPage);
        pager.add(btnNextPage);
        pager.add(lblPageInfo);

        return pager;
    }

    private void bindEvents() {
        customTable.getTable().getSelectionModel().addListSelectionListener(this::onTableRowSelected);
        searchPanel.addActionListener(e -> applyFilter(searchPanel.getSearchText()));
    }

    private void onTableRowSelected(ListSelectionEvent e) {

        if (e.getValueIsAdjusting())
            return;

        int row = customTable.getTable().getSelectedRow();
        if (row < 0)
            return;

        // lấy CCCD từ table (cột 1)
        String cccd = asText(tableModel.getValueAt(row, 1));

        try {
            XtThisinhxettuyen25 ts = thiSinhService.findByCccd(cccd);

            if (ts == null) {
                ToastThiSinh.showError(this, "Không tìm thấy thí sinh!");
                return;
            }

            // ===== FILL FULL 12 FIELD =====
            txtIdthisinh.setText(asText(ts.getIdthisinh()));
            txtCccd.setText(asText(ts.getCccd()));
            txtSbd.setText(asText(ts.getSobaodanh()));
            txtHo.setText(asText(ts.getHo()));
            txtTen.setText(asText(ts.getTen()));
            txtNgaySinh.setText(asText(ts.getNgaySinh()));
            txtDienThoai.setText(asText(ts.getDienThoai()));
            cboGioiTinh.setSelectedItem(asText(ts.getGioiTinh()));
            txtEmail.setText(asText(ts.getEmail()));
            txtNoiSinh.setText(asText(ts.getNoiSinh()));
            txtDoiTuong.setText(asText(ts.getDoiTuong()));
            txtKhuVuc.setText(asText(ts.getKhuVuc()));

            selectedCccd = ts.getCccd();

        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi load chi tiết: " + ex.getMessage());
        }
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
        currentPage = 1;
        loadPageAsync();
    }

    private Object[] entityToRow(XtThisinhxettuyen25 ts) {

        return new Object[] {
                ts.getIdthisinh(),
                asText(ts.getCccd()),
                asText(ts.getSobaodanh()),
                asText(ts.getHo()),
                asText(ts.getTen()),
                asText(ts.getNgaySinh()),
                asText(ts.getGioiTinh()),
                "Xem chi tiết"
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
        if (s == null)
            return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void applyFilter(String keyword) {
        loadFromDb(keyword);
    }

    private void loadPageAsync() {
        final int requestId = ++lastRequestId;
        final int pageToLoad = currentPage;
        final int pageSizeToLoad = pageSize;
        final String keyword = currentKeyword;
        setLoadingState(true);

        new SwingWorker<PageResult, Void>() {
            @Override
            protected PageResult doInBackground() {
                List<XtThisinhxettuyen25> entities = thiSinhService.searchPaginated(keyword, pageToLoad, pageSizeToLoad);
                long total = thiSinhService.countByKeyword(keyword);
                Map<String, Long> doiTuongCounts = thiSinhService.countByDoiTuong(keyword);
                Map<String, Long> khuVucCounts = thiSinhService.countByKhuVuc(keyword);
                List<Object[]> rows = new ArrayList<>(entities.size());
                for (XtThisinhxettuyen25 ts : entities) {
                    rows.add(entityToRow(ts));
                }
                return new PageResult(rows, total, doiTuongCounts, khuVucCounts);
            }

            @Override
            protected void done() {
                try {
                    if (requestId != lastRequestId) {
                        return;
                    }

                    PageResult result = get();
                    totalRecords = result.totalRecords();

                    tableModel.setRowCount(0);
                    for (Object[] row : result.rows()) {
                        tableModel.addRow(row);
                    }

                    int totalPages = getTotalPages();
                    lblPageInfo.setText("Trang " + currentPage + "/" + totalPages + " - Tổng " + totalRecords + " bản ghi");
                    updateStatsPanel(result.totalRecords(), result.doiTuongCounts(), result.khuVucCounts());
                } catch (Exception e) {
                    ToastThiSinh.showError(ThisinhPanel.this, "Lỗi tải dữ liệu: " + e.getMessage());
                } finally {
                    setLoadingState(false);
                }
            }
        }.execute();
    }

    private int getTotalPages() {
        if (totalRecords <= 0) {
            return 1;
        }
        return (int) Math.ceil(totalRecords * 1.0 / pageSize);
    }

    private void setLoadingState(boolean loading) {
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        if (cboPageSize != null) {
            cboPageSize.setEnabled(!loading);
        }
        if (btnPrevPage != null) {
            btnPrevPage.setEnabled(!loading && currentPage > 1);
        }
        if (btnNextPage != null) {
            btnNextPage.setEnabled(!loading && currentPage < getTotalPages());
        }
    }

    private void updateStatsPanel(long total, Map<String, Long> doiTuongCounts, Map<String, Long> khuVucCounts) {
        lblStatsTotalValue.setText(String.format("%,d", total));
        statsDoiTuongCounts = doiTuongCounts == null ? java.util.Collections.emptyMap() : doiTuongCounts;
        statsKhuVucCounts = khuVucCounts == null ? java.util.Collections.emptyMap() : khuVucCounts;
        updateDoiTuongStatsGrid();
        updateKhuVucStatsGrid();
    }

    private void updateDoiTuongStatsGrid() {
        for (int dt = 1; dt <= 7; dt++) {
            JLabel label = lblStatsDoiTuongGrid.get(dt);
            if (label == null) {
                continue;
            }
            long value = 0L;
            if (statsDoiTuongCounts != null) {
                value = statsDoiTuongCounts.getOrDefault(String.valueOf(dt), 0L);
            }
            label.setText(String.format("%,d", value));
        }
    }

    private void updateKhuVucStatsGrid() {
        for (String kv : KV_KEYS) {
            JLabel label = lblStatsKhuVucGrid.get(kv);
            if (label == null) {
                continue;
            }
            long value = 0L;
            if (statsKhuVucCounts != null) {
                value = statsKhuVucCounts.getOrDefault(kv, 0L);
            }
            label.setText(String.format("%,d", value));
        }
    }

    private record PageResult(
            List<Object[]> rows,
            long totalRecords,
            Map<String, Long> doiTuongCounts,
            Map<String, Long> khuVucCounts) {
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
        chooser.setDialogTitle("Chọn file Excel Thí Sinh");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path filePath = chooser.getSelectedFile().toPath();

        List<XtThisinhxettuyen25> entities = new ArrayList<>();

        try (var fis = Files.newInputStream(filePath);
                var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis)) {

            var sheet = workbook.getSheetAt(0);

            // bỏ dòng header (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                var row = sheet.getRow(i);
                if (row == null)
                    continue;

                XtThisinhxettuyen25 ts = new XtThisinhxettuyen25();

                ts.setCccd(getCell(row, 0));
                ts.setSobaodanh(getCell(row, 1));
                ts.setHo(getCell(row, 2));
                ts.setTen(getCell(row, 3));
                ts.setNgaySinh(getCell(row, 4));
                ts.setDienThoai(getCell(row, 5));
                ts.setGioiTinh(getCell(row, 6));
                ts.setEmail(getCell(row, 7));
                ts.setNoiSinh(getCell(row, 8));
                ts.setDoiTuong(getCell(row, 9));
                ts.setKhuVuc(getCell(row, 10));

                entities.add(ts);
            }

            if (entities.isEmpty()) {
                ToastThiSinh.showError(this, "File Excel không có dữ liệu!");
                return;
            }

            thiSinhService.importBatch(entities);

            loadFromDb(currentKeyword);

            ToastThiSinh.showSuccess(
                    this,
                    "Import Excel thành công " + entities.size() + " thí sinh!");

        } catch (Exception ex) {
            ToastThiSinh.showError(this, "Lỗi import Excel: " + ex.getMessage());
        }
    }

    private void onExportExcel() {

        List<XtThisinhxettuyen25> entities;

        try {
            entities = thiSinhService.search(currentKeyword);
        } catch (Exception ex) {
            ToastThiSinh.showError(this,
                    "Không thể tải dữ liệu export: " + ex.getMessage());
            return;
        }

        if (entities == null || entities.isEmpty()) {
            ToastThiSinh.showError(this,
                    "Không có dữ liệu để xuất file.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file Excel Thí Sinh");
        chooser.setFileFilter(
                new FileNameExtensionFilter("Excel file (*.xlsx)", "xlsx"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path out = chooser.getSelectedFile().toPath();

        if (!out.toString().toLowerCase().endsWith(".xlsx")) {
            out = Path.of(out.toString() + ".xlsx");
        }

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            var sheet = workbook.createSheet("ThiSinh");

            // ===== HEADER =====
            String[] headers = {
                    "ID", "CCCD", "SBD", "Họ", "Tên",
                    "Ngày sinh", "Điện thoại", "Giới tính",
                    "Email", "Nơi sinh", "Đối tượng", "Khu vực"
            };

            var headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // ===== DATA =====
            int rowIndex = 1;

            for (XtThisinhxettuyen25 ts : entities) {

                var row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(asText(ts.getIdthisinh()));
                row.createCell(1).setCellValue(asText(ts.getCccd()));
                row.createCell(2).setCellValue(asText(ts.getSobaodanh()));
                row.createCell(3).setCellValue(asText(ts.getHo()));
                row.createCell(4).setCellValue(asText(ts.getTen()));
                row.createCell(5).setCellValue(asText(ts.getNgaySinh()));
                row.createCell(6).setCellValue(asText(ts.getDienThoai()));
                row.createCell(7).setCellValue(asText(ts.getGioiTinh()));
                row.createCell(8).setCellValue(asText(ts.getEmail()));
                row.createCell(9).setCellValue(asText(ts.getNoiSinh()));
                row.createCell(10).setCellValue(asText(ts.getDoiTuong()));
                row.createCell(11).setCellValue(asText(ts.getKhuVuc()));
            }

            // auto resize cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ghi file
            try (var fos = Files.newOutputStream(out)) {
                workbook.write(fos);
            }

            ToastThiSinh.showSuccess(
                    this,
                    "Xuất Excel thành công " + entities.size() + " thí sinh!");

        } catch (Exception ex) {
            ToastThiSinh.showError(this,
                    "Không thể ghi file Excel: " + ex.getMessage());
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

    private String safeCsv(Object value) {

        if (value == null) {
            return "";
        }

        return value.toString()
                .replace(",", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private String getCell(org.apache.poi.ss.usermodel.Row row, int col) {
        try {
            var cell = row.getCell(col);
            if (cell == null)
                return null;

            cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
            String value = cell.getStringCellValue();

            return (value == null || value.trim().isEmpty()) ? null : value.trim();

        } catch (Exception e) {
            return null;
        }
    }
}
