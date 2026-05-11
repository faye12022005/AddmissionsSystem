package org.AdmissionsSystem.gui.modules.QuanLyNguoiDung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.AdmissionsSystem.bus.controller.UsersController;
import org.AdmissionsSystem.gui.common.Searchable;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;

import java.awt.*;
import java.util.List;

public class UsersPanel extends JPanel implements Searchable {
    private final UsersController controller;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtMaNguoiDung = new JTextField();
    private final JTextField txtTenDangNhap = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<String> cboVaiTro = new JComboBox<>(new String[] { "user", "admin" });
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Enable", "Disable" });

    private final JButton btnThem = new JButton("Thêm mới");
    private final JButton btnLuuThem = new JButton("Lưu người dùng");
    private final JButton btnSuaThongTin = new JButton("Lưu chỉnh sửa");
    private final JButton btnDoiMatKhau = new JButton("Đổi mật khẩu");
    private final JButton btnDoiQuyen = new JButton("Đổi quyền user/admin");
    private final JButton btnBatTat = new JButton("Enable/Disable");
    private final JButton btnLamMoi = new JButton("Làm mới");

    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(Style.SURFACE);
        this.controller = new UsersController(this);

        JLabel title = new JLabel("Quản lý Người dùng");
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        title.setFont(Style.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        String[] cols = { "Mã", "Tên đăng nhập", "Họ tên", "Email", "Vai trò", "Trạng thái" };
        this.tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        CustomTable customTable = new CustomTable(tableModel);
        this.table = customTable.getTable();
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = table.getSelectedRow();
                controller.onRowSelected(selected);
            }
        });

        JPanel actionBar = createActionBar();
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setOpaque(false);
        leftPanel.add(actionBar, BorderLayout.NORTH);
        leftPanel.add(customTable, BorderLayout.CENTER);

        JPanel editorPanel = createEditorPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, editorPanel);
        splitPane.setResizeWeight(0.72);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);

        bindActions();
        controller.loadInitialData();
    }

    private JPanel createActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBar.setOpaque(false);
        actionBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        JButton[] actions = { btnThem, btnLuuThem, btnSuaThongTin, btnDoiMatKhau, btnDoiQuyen, btnBatTat, btnLamMoi };
        for (JButton b : actions) {
            Style.styleButton(b);
            styleActionButton(b);
            actionBar.add(b);
        }
        
        btnLuuThem.setVisible(false); // Initially hidden

        return actionBar;
    }

    private void styleActionButton(JButton button) {
        Color primary = new Color(41, 98, 255);

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(primary);

        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        Dimension preferred = button.getPreferredSize();
        button.setPreferredSize(new Dimension(Math.max(preferred.width, 130), 32));
    }

    private JPanel createEditorPanel() {
        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
        editorPanel.setBackground(Color.WHITE);
        editorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 245)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel editorTitle = new JLabel("Thông tin người dùng");
        editorTitle.setFont(Style.PANEL_TITLE_FONT);
        editorTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        editorPanel.add(editorTitle);
        editorPanel.add(Box.createVerticalStrut(10));

        editorPanel.add(field("Mã người dùng", txtMaNguoiDung));
        editorPanel.add(field("Tên đăng nhập", txtTenDangNhap));
        editorPanel.add(field("Họ và tên", txtHoTen));
        editorPanel.add(field("Email", txtEmail));
        editorPanel.add(comboField("Vai trò", cboVaiTro));
        editorPanel.add(comboField("Trạng thái", cboTrangThai));

        txtMaNguoiDung.setEditable(false);
        txtTenDangNhap.setEditable(false);

        editorPanel.add(Box.createVerticalStrut(10));
        JTextArea note = new JTextArea(
                "Gợi ý: Chọn 1 dòng từ danh sách để sửa thông tin, đổi mật khẩu, đổi quyền hoặc bật/tắt tài khoản.");
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setEditable(false);
        note.setOpaque(false);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        note.setForeground(new Color(90, 100, 120));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        editorPanel.add(note);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(editorPanel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel field(String label, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(jLabel, BorderLayout.NORTH);

        textField.setPreferredSize(new Dimension(220, 34));
        panel.add(textField, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel comboField(String label, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(jLabel, BorderLayout.NORTH);

        comboBox.setPreferredSize(new Dimension(220, 34));
        panel.add(comboBox, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        return panel;
    }

    private void bindActions() {
        btnThem.addActionListener(e -> controller.batDauThem());
        btnLuuThem.addActionListener(e -> controller.hoanTatThem());
        btnSuaThongTin.addActionListener(e -> controller.suaThongTin());
        btnDoiMatKhau.addActionListener(e -> controller.doiMatKhau());
        btnDoiQuyen.addActionListener(e -> controller.doiQuyen());
        btnBatTat.addActionListener(e -> controller.batTatNguoiDung());
        btnLamMoi.addActionListener(e -> controller.lamMoiDanhSach());
    }

    public void renderUsers(List<UsersController.UserViewModel> users) {
        tableModel.setRowCount(0);
        for (UsersController.UserViewModel user : users) {
            tableModel.addRow(new Object[] {
                    user.getMaNguoiDung(),
                    user.getTenDangNhap(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getVaiTro(),
                    user.isEnabled() ? "Enable" : "Disable"
            });
        }

        if (tableModel.getRowCount() > 0 && table.getSelectedRow() < 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void fillEditor(UsersController.UserViewModel user) {
        if (user == null) {
            txtMaNguoiDung.setText("");
            txtTenDangNhap.setText("");
            txtHoTen.setText("");
            txtEmail.setText("");
            cboVaiTro.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            return;
        }

        txtMaNguoiDung.setText(user.getMaNguoiDung());
        txtTenDangNhap.setText(user.getTenDangNhap());
        txtHoTen.setText(user.getHoTen());
        txtEmail.setText(user.getEmail());
        cboVaiTro.setSelectedItem(user.getVaiTro());
        cboTrangThai.setSelectedItem(user.isEnabled() ? "Enable" : "Disable");
    }

    public int getSelectedTableRow() {
        return table.getSelectedRow();
    }

    public String getInputHoTen() {
        return txtHoTen.getText();
    }

    public String getInputEmail() {
        return txtEmail.getText();
    }

    public String getInputVaiTro() {
        Object selected = cboVaiTro.getSelectedItem();
        return selected == null ? "user" : selected.toString();
    }

    public boolean getInputEnabled() {
        Object selected = cboTrangThai.getSelectedItem();
        return selected == null || "Enable".equalsIgnoreCase(selected.toString());
    }

    public String getInputUsername() {
        return txtTenDangNhap.getText().trim();
    }

    public void setAddMode(boolean active) {
        txtTenDangNhap.setEditable(active);
        btnThem.setVisible(!active);
        btnLuuThem.setVisible(active);
        btnSuaThongTin.setEnabled(!active);
        btnDoiMatKhau.setEnabled(!active);
        btnDoiQuyen.setEnabled(!active);
        btnBatTat.setEnabled(!active);
        
        if (active) {
            txtTenDangNhap.requestFocus();
        }
    }

    public String askNewPassword() {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(
                this,
                passwordField,
                "Nhập mật khẩu mới",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String password = new String(passwordField.getPassword()).trim();
        if (password.isEmpty()) {
            showWarning("Mật khẩu không được để trống.");
            return null;
        }
        return password;
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onSearch(String query) {
        controller.search(query);
    }
}
