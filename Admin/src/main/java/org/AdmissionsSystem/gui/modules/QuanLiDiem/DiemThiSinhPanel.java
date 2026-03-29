package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Searchable;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.Toast;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DiemThiSinhPanel extends JPanel implements Searchable {
    private final DiemController controller = new DiemController();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "ID", "CCCD", "Số báo danh", "Họ và tên", "Loại điểm", "Môn", "Điểm" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JComboBox<String> cboLoaiDiemFilter = new JComboBox<>(buildFilterValues(DiemService.LOAI_DIEM));
    private final JComboBox<String> cboMonFilter = new JComboBox<>(buildFilterValues(DiemService.MON_HOC));
    private final JLabel lblTongBanGhi = new JLabel();
    private final DecimalFormat scoreFormat = new DecimalFormat("0.##");

    private final JTable table;

    private String currentSearchText = "";
    private List<DiemService.DiemRecord> currentRows = new ArrayList<>();

    public DiemThiSinhPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Style.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildHeader(), BorderLayout.NORTH);

        CustomTable customTable = new CustomTable(tableModel);
        table = customTable.getTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(64);
        add(customTable, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        footer.setOpaque(false);
        footer.add(lblTongBanGhi);
        add(footer, BorderLayout.SOUTH);

        scoreFormat.setGroupingUsed(false);
        reloadTable();
    }

    private JPanel buildHeader() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Quản lý điểm thí sinh");
        title.setFont(Style.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel(new BorderLayout(8, 8));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        leftActions.setOpaque(false);

        JButton btnThem = new JButton("Thêm mới");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnImport = new JButton("Import Excel");
        JButton btnThongKe = new JButton("Thống kê");
        JButton btnLamMoi = new JButton("Làm mới");

        Style.styleButton(btnThem);
        Style.styleButton(btnSua);
        Style.styleButton(btnXoa);
        Style.styleButton(btnImport);
        Style.styleButton(btnThongKe);
        Style.styleButton(btnLamMoi);

        leftActions.add(btnThem);
        leftActions.add(btnSua);
        leftActions.add(btnXoa);
        leftActions.add(btnImport);
        leftActions.add(btnThongKe);
        leftActions.add(btnLamMoi);

        JPanel rightFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        rightFilters.setOpaque(false);
        rightFilters.add(new JLabel("Loại điểm:"));
        rightFilters.add(cboLoaiDiemFilter);
        rightFilters.add(Box.createHorizontalStrut(8));
        rightFilters.add(new JLabel("Môn:"));
        rightFilters.add(cboMonFilter);

        actions.add(leftActions, BorderLayout.WEST);
        actions.add(rightFilters, BorderLayout.EAST);

        btnThem.addActionListener(e -> onAdd());
        btnSua.addActionListener(e -> onEdit());
        btnXoa.addActionListener(e -> onDelete());
        btnImport.addActionListener(e -> onImportExcel());
        btnThongKe.addActionListener(e -> onShowStatistics());
        btnLamMoi.addActionListener(e -> onRefresh());

        cboLoaiDiemFilter.addActionListener(e -> reloadTable());
        cboMonFilter.addActionListener(e -> reloadTable());

        wrapper.add(actions, BorderLayout.CENTER);
        return wrapper;
    }

    @Override
    public void onSearch(String query) {
        currentSearchText = query == null ? "" : query.trim();
        reloadTable();
    }

    private void onAdd() {
        DiemService.DiemRecordInput input = DiemModal.showDialog(this, "Thêm điểm thí sinh", null);
        if (input == null) {
            return;
        }

        try {
            controller.them(input);
            reloadTable();
            Toast.showToast(this, "Đã thêm điểm thí sinh.", false);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onEdit() {
        Integer selectedId = selectedId();
        if (selectedId == null) {
            showWarning("Vui lòng chọn một bản ghi để sửa.");
            return;
        }

        Optional<DiemService.DiemRecord> existing = controller.getById(selectedId);
        if (existing.isEmpty()) {
            showWarning("Không tìm thấy bản ghi cần sửa.");
            reloadTable();
            return;
        }

        DiemService.DiemRecordInput updatedInput = DiemModal.showDialog(this, "Cập nhật điểm thí sinh", existing.get());
        if (updatedInput == null) {
            return;
        }

        try {
            Optional<DiemService.DiemRecord> updated = controller.capNhat(selectedId, updatedInput);
            if (updated.isEmpty()) {
                showWarning("Bản ghi đã bị thay đổi, vui lòng tải lại danh sách.");
                reloadTable();
                return;
            }

            reloadTable();
            Toast.showToast(this, "Đã cập nhật thông tin điểm.", false);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onDelete() {
        Integer selectedId = selectedId();
        if (selectedId == null) {
            showWarning("Vui lòng chọn một bản ghi để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa bản ghi điểm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (!controller.xoa(selectedId)) {
            showWarning("Không thể xóa bản ghi. Dữ liệu có thể đã thay đổi.");
            reloadTable();
            return;
        }

        reloadTable();
        Toast.showToast(this, "Đã xóa bản ghi điểm.", false);
    }

    private void onImportExcel() {
        try {
            int importedRows = controller.importExcel(this);
            if (importedRows == 0) {
                Toast.showToast(this, "Không có dữ liệu nào được import.", true);
                return;
            }

            reloadTable();
            Toast.showToast(this, "Import thành công " + importedRows + " bản ghi.", false);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (IOException ex) {
            showError("Không thể đọc file import: " + ex.getMessage());
        }
    }

    private void onShowStatistics() {
        Map<String, Double> byLoai = controller.thongKeTheoLoai(currentRows);
        Map<String, Double> byMon = controller.thongKeTheoMon(currentRows);

        String statsText = controller.buildThongKeText("Điểm trung bình theo loại điểm", byLoai)
                + "\n\n"
                + controller.buildThongKeText("Điểm trung bình theo môn", byMon);

        JTextArea textArea = new JTextArea(statsText, 16, 42);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Thống kê điểm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onRefresh() {
        currentSearchText = "";
        cboLoaiDiemFilter.setSelectedItem(DiemService.ALL_OPTION);
        cboMonFilter.setSelectedItem(DiemService.ALL_OPTION);
        reloadTable();
    }

    private void reloadTable() {
        String selectedLoai = selectedFilterValue(cboLoaiDiemFilter);
        String selectedMon = selectedFilterValue(cboMonFilter);

        currentRows = controller.getDanhSach(currentSearchText, selectedLoai, selectedMon);

        tableModel.setRowCount(0);
        for (DiemService.DiemRecord row : currentRows) {
            tableModel.addRow(new Object[] {
                    row.id(),
                    row.cccd(),
                    row.soBaoDanh(),
                    row.hoTen(),
                    row.loaiDiem(),
                    row.mon(),
                    scoreFormat.format(row.diem())
            });
        }

        lblTongBanGhi.setText("Tổng bản ghi: " + currentRows.size());
    }

    private Integer selectedId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Object value = tableModel.getValueAt(modelRow, 0);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String selectedFilterValue(JComboBox<String> comboBox) {
        Object value = comboBox.getSelectedItem();
        return value == null ? DiemService.ALL_OPTION : value.toString();
    }

    private String[] buildFilterValues(List<String> options) {
        List<String> values = new ArrayList<>();
        values.add(DiemService.ALL_OPTION);
        values.addAll(options);
        return values.toArray(new String[0]);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
