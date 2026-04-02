package org.AdmissionsSystem.gui.modules.QuanLyThiSinh;

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

    private final List<Object[]> allRows = new ArrayList<>();
    private List<Object[]> filteredRows = new ArrayList<>();

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
        seedData();
        applyFilter("");
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
            Object[] row = collectFormData();
            String cccd = asText(row[0]);

            if (findIndexByCccd(cccd) >= 0) {
                ToastThiSinh.showError(this, "CCCD thí sinh đã tồn tại.");
                return;
            }

            allRows.add(row);
            applyFilter(searchPanel.getSearchText());
            clearForm();
            ToastThiSinh.showSuccess(this, "Thêm thí sinh mới thành công!");
        } catch (IllegalArgumentException ex) {
            ToastThiSinh.showError(this, ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedCccd == null || selectedCccd.isBlank()) {
            ToastThiSinh.showError(this, "Vui lòng chọn thí sinh cần cập nhật từ bảng.");
            return;
        }
        try {
            Object[] row = collectFormData();
            String newCccd = asText(row[0]);

            int selectedIndex = findIndexByCccd(selectedCccd);
            if (selectedIndex < 0) {
                ToastThiSinh.showError(this, "Không tìm thấy dữ liệu để cập nhật.");
                return;
            }

            int duplicateIndex = findIndexByCccd(newCccd);
            if (duplicateIndex >= 0 && duplicateIndex != selectedIndex) {
                ToastThiSinh.showError(this, "CCCD mới đã tồn tại ở thí sinh khác.");
                return;
            }

            allRows.set(selectedIndex, row);
            selectedCccd = newCccd;
            applyFilter(searchPanel.getSearchText());
            ToastThiSinh.showSuccess(this, "Cập nhật thông tin thành công!");
        } catch (IllegalArgumentException ex) {
            ToastThiSinh.showError(this, ex.getMessage());
        }
    }

    private void onDelete() {
        // Kiểm tra xem đã chọn dòng nào chưa
        if (selectedCccd == null || selectedCccd.isBlank()) {
            ToastThiSinh.showError(this, "Vui lòng chọn dòng cần xóa trước.");
            return;
        }

        int index = findIndexByCccd(selectedCccd);
        if (index < 0) {
            ToastThiSinh.showError(this, "Không tìm thấy bản ghi cần xóa.");
            return;
        }

        // Lấy Họ + Tên thí sinh để hiện thông báo xác nhận cho rõ ràng
        String ho = asText(allRows.get(index)[2]);
        String ten = asText(allRows.get(index)[3]);
        String hoTen = ho + " " + ten;

        // Gọi hộp thoại xác nhận từ ToastThiSinh
        if (ToastThiSinh.showConfirmDelete(this, hoTen)) {
            allRows.remove(index);
            applyFilter(searchPanel.getSearchText());
            clearForm();
            ToastThiSinh.showSuccess(this, "Đã xóa thí sinh thành công.");
        }
    }

    private Object[] collectFormData() {
        String cccd = txtCccd.getText().trim();
        String sbd = txtSbd.getText().trim();
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        String ngaySinh = txtNgaySinh.getText().trim();
        String dienThoai = txtDienThoai.getText().trim();
        String gioiTinh = cboGioiTinh.getSelectedItem() != null ? cboGioiTinh.getSelectedItem().toString() : "";
        String email = txtEmail.getText().trim();
        String noiSinh = txtNoiSinh.getText().trim();
        String doiTuong = txtDoiTuong.getText().trim();
        String khuVuc = txtKhuVuc.getText().trim();

        if (cccd.isEmpty() || ho.isEmpty() || ten.isEmpty()) {
            throw new IllegalArgumentException("CCCD, Họ và Tên là bắt buộc.");
        }

        return new Object[] {
                cccd, sbd, ho, ten, ngaySinh, dienThoai, gioiTinh, email, noiSinh, doiTuong, khuVuc
        };
    }

    // Dữ liệu giả
    private void seedData() {
        allRows.clear();
        allRows.add(new Object[] { "079203123456", "TS001", "Nguyễn Văn", "An", "15/05/2005", "0901234567", "Nam",
                "an.nv@gmail.com", "TP.HCM", "01", "KV1" });
        allRows.add(new Object[] { "079203654321", "TS002", "Trần Thị", "Bích", "20/10/2005", "0912345678", "Nữ",
                "bich.tt@gmail.com", "Hà Nội", "02", "KV2" });
        allRows.add(new Object[] { "012345678910", "TS003", "Lê Hoàng", "Cường", "01/01/2004", "0987654321", "Nam",
                "cuong.lh@gmail.com", "Đà Nẵng", "01", "KV3" });
        allRows.add(new Object[] { "079205111222", "TS004", "Phạm Thái", "Dương", "14/02/2005", "0933112233", "Nam",
                "duong.pt@gmail.com", "Đồng Nai", "01", "KV2-NT" });
        allRows.add(new Object[] { "079205333444", "TS005", "Vũ Mỹ", "Duyên", "08/03/2005", "0944112233", "Nữ",
                "duyen.vm@gmail.com", "Hải Phòng", "01", "KV2" });
        allRows.add(new Object[] { "079205555666", "TS006", "Đặng Hải", "Đăng", "19/04/2005", "0955112233", "Nam",
                "dang.dh@gmail.com", "Cần Thơ", "03", "KV3" });
        allRows.add(new Object[] { "079205777888", "TS007", "Bùi Tố", "Uyên", "22/07/2005", "0966112233", "Nữ",
                "uyen.bt@gmail.com", "Bình Dương", "01", "KV2" });
        allRows.add(new Object[] { "079205999000", "TS008", "Ngô Quốc", "Bảo", "11/08/2005", "0977112233", "Nam",
                "bao.nq@gmail.com", "Vũng Tàu", "04", "KV1" });
        allRows.add(new Object[] { "079206111333", "TS009", "Lý Thu", "Thảo", "30/09/2005", "0988112233", "Nữ",
                "thao.lt@gmail.com", "Long An", "01", "KV2-NT" });
        allRows.add(new Object[] { "079206222444", "TS010", "Hồ Trọng", "Nghĩa", "25/11/2005", "0999112233", "Nam",
                "nghia.ht@gmail.com", "Tiền Giang", "01", "KV1" });
        allRows.add(new Object[] { "079206333555", "TS011", "Đỗ Kim", "Ngân", "12/12/2005", "0900112233", "Nữ",
                "ngan.dk@gmail.com", "Bến Tre", "02", "KV2" });
        allRows.add(new Object[] { "079206444666", "TS012", "Châu Gia", "Huy", "05/01/2005", "0911223344", "Nam",
                "huy.cg@gmail.com", "Đồng Tháp", "01", "KV3" });
        allRows.add(new Object[] { "079206555777", "TS013", "Dương Yến", "Nhi", "18/06/2005", "0922334455", "Nữ",
                "nhi.dy@gmail.com", "An Giang", "01", "KV2-NT" });
        allRows.add(new Object[] { "079206666888", "TS014", "Lương Minh", "Triết", "29/02/2004", "0933445566", "Nam",
                "triet.lm@gmail.com", "Kiên Giang", "05", "KV1" });
        allRows.add(new Object[] { "079206777999", "TS015", "Tạ Thanh", "Trúc", "03/05/2005", "0944556677", "Nữ",
                "truc.tt@gmail.com", "Cà Mau", "01", "KV2" });
        allRows.add(new Object[] { "079206888000", "TS016", "Vương Tuấn", "Kiệt", "14/07/2005", "0955667788", "Nam",
                "kiet.vt@gmail.com", "Tây Ninh", "01", "KV3" });
        allRows.add(new Object[] { "079206999111", "TS017", "Mai Phương", "Linh", "27/08/2005", "0966778899", "Nữ",
                "linh.mp@gmail.com", "Bình Phước", "06", "KV2-NT" });
        allRows.add(new Object[] { "079207000222", "TS018", "Lâm Phúc", "Hậu", "09/09/2005", "0977889900", "Nam",
                "hau.lp@gmail.com", "Bạc Liêu", "01", "KV1" });
        allRows.add(new Object[] { "079207111333", "TS019", "Đoàn Thị", "Mỹ", "16/10/2005", "0988990011", "Nữ",
                "my.dt@gmail.com", "Sóc Trăng", "01", "KV2" });
        allRows.add(new Object[] { "079207222444", "TS020", "Trương Bá", "Toàn", "21/11/2005", "0999001122", "Nam",
                "toan.tb@gmail.com", "Trà Vinh", "07", "KV3" });

        allRows.add(new Object[] { "079207333555", "TS021", "Phan Hoài", "Thương", "02/12/2005", "0801122334", "Nữ",
                "thuong.ph@gmail.com", "Hậu Giang", "01", "KV1" });
        allRows.add(new Object[] { "079207444666", "TS022", "Đinh Tấn", "Tài", "07/04/2005", "0812233445", "Nam",
                "tai.dt@gmail.com", "Ninh Thuận", "01", "KV2" });
        allRows.add(new Object[] { "079207555777", "TS023", "Thiều Nhật", "Mai", "13/05/2005", "0823344556", "Nữ",
                "mai.tn@gmail.com", "Bình Thuận", "01", "KV3" });
        allRows.add(new Object[] { "079207666888", "TS024", "Thái Đình", "Phong", "24/06/2005", "0834455667", "Nam",
                "phong.td@gmail.com", "Lâm Đồng", "02", "KV2-NT" });
        allRows.add(new Object[] { "079207777999", "TS025", "Tống Nhã", "Kỳ", "17/02/2005", "0845566778", "Nữ",
                "ky.tn@gmail.com", "Gia Lai", "01", "KV1" });
        allRows.add(new Object[] { "079207888000", "TS026", "Hà Trung", "Hiếu", "28/03/2005", "0856677889", "Nam",
                "hieu.ht@gmail.com", "Đắk Lắk", "01", "KV2" });
        allRows.add(new Object[] { "079207999111", "TS027", "Chu Diễm", "Quỳnh", "04/09/2005", "0867788990", "Nữ",
                "quynh.cd@gmail.com", "Đắk Nông", "01", "KV3" });
        allRows.add(new Object[] { "079208000222", "TS028", "Trịnh Xuân", "Thanh", "15/10/2005", "0878899001", "Nam",
                "thanh.tx@gmail.com", "Kon Tum", "03", "KV2-NT" });
        allRows.add(new Object[] { "079208111333", "TS029", "Vi Bích", "Ngọc", "26/11/2005", "0889900112", "Nữ",
                "ngoc.vb@gmail.com", "Thanh Hóa", "01", "KV1" });
        allRows.add(new Object[] { "079208222444", "TS030", "Lại Phi", "Hùng", "08/12/2005", "0890011223", "Nam",
                "hung.lp@gmail.com", "Nghệ An", "01", "KV2" });
    }

    private void applyFilter(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        filteredRows = new ArrayList<>();

        for (Object[] row : allRows) {
            String cccd = asText(row[0]).toLowerCase();
            String ho = asText(row[2]).toLowerCase();
            String ten = asText(row[3]).toLowerCase();
            String sbd = asText(row[1]).toLowerCase();

            if (q.isEmpty() || cccd.contains(q) || sbd.contains(q) || ho.contains(q) || ten.contains(q)
                    || (ho + " " + ten).contains(q)) {
                filteredRows.add(cloneRow(row));
            }
        }
        currentPage = 1;
        loadPage();
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

        // Cập nhật vào danh sách hiện tại
        for (Object[] imported : importedRows) {
            String cccd = asText(imported[0]);
            int index = findIndexByCccd(cccd);
            if (index >= 0) {
                allRows.set(index, imported);
            } else {
                allRows.add(imported);
            }
        }

        applyFilter(searchPanel.getSearchText());
        ToastThiSinh.showSuccess(this, "Import thành công " + importedRows.size() + " dòng dữ liệu!");
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

    private int findIndexByCccd(String cccd) {
        for (int i = 0; i < allRows.size(); i++) {
            if (asText(allRows.get(i)[0]).equalsIgnoreCase(cccd)) {
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
        return value == null ? "" : value.toString();
    }
}