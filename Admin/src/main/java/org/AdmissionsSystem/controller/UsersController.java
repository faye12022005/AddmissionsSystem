package org.AdmissionsSystem.controller;

import org.AdmissionsSystem.gui.modules.QuanLyNguoiDung.UsersPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UsersController {
    private final UsersPanel view;
    private final List<UserViewModel> users = new ArrayList<>();
    private final List<UserViewModel> filteredUsers = new ArrayList<>();

    public UsersController(UsersPanel view) {
        this.view = view;
    }

    public void loadInitialData() {
        users.clear();
        users.add(new UserViewModel("U001", "admin", "Nguyen Van Quan", "admin@tuyensinh.vn", "admin", true, "admin123"));
        users.add(new UserViewModel("U002", "ngoclan", "Tran Ngoc Lan", "lan@tuyensinh.vn", "user", true, "lan123"));
        users.add(new UserViewModel("U003", "manhdat", "Le Manh Dat", "dat@tuyensinh.vn", "user", false, "dat123"));
        users.add(new UserViewModel("U004", "thuyduong", "Pham Thuy Duong", "duong@tuyensinh.vn", "admin", true, "duong123"));
        refreshFiltered(users);
    }

    public void onRowSelected(int selectedIndex) {
        UserViewModel selected = getSelectedFromFiltered(selectedIndex);
        view.fillEditor(selected);
    }

    public void suaThongTin() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) {
            view.showWarning("Vui lòng chọn người dùng cần sửa.");
            return;
        }

        String hoTen = safeTrim(view.getInputHoTen());
        String email = safeTrim(view.getInputEmail());
        if (hoTen.isEmpty()) {
            view.showWarning("Họ tên không được để trống.");
            return;
        }
        if (!isValidEmail(email)) {
            view.showWarning("Email không hợp lệ.");
            return;
        }

        selected.setHoTen(hoTen);
        selected.setEmail(email);
        selected.setVaiTro(view.getInputVaiTro());
        selected.setEnabled(view.getInputEnabled());

        view.renderUsers(filteredUsers);
        view.showInfo("Đã cập nhật thông tin người dùng.");
    }

    public void doiMatKhau() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) {
            view.showWarning("Vui lòng chọn người dùng cần đổi mật khẩu.");
            return;
        }

        String newPassword = view.askNewPassword();
        if (newPassword == null) {
            return;
        }

        if (newPassword.length() < 6) {
            view.showWarning("Mật khẩu mới phải có ít nhất 6 ký tự.");
            return;
        }

        selected.setPassword(newPassword);
        view.showInfo("Đã đổi mật khẩu cho tài khoản: " + selected.getTenDangNhap());
    }

    public void doiQuyen() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) {
            view.showWarning("Vui lòng chọn người dùng cần đổi quyền.");
            return;
        }

        String newRole = "user".equalsIgnoreCase(selected.getVaiTro()) ? "admin" : "user";
        selected.setVaiTro(newRole);
        view.fillEditor(selected);
        view.renderUsers(filteredUsers);
        view.showInfo("Đã đổi quyền thành: " + newRole);
    }

    public void batTatNguoiDung() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) {
            view.showWarning("Vui lòng chọn người dùng cần thay đổi trạng thái.");
            return;
        }

        selected.setEnabled(!selected.isEnabled());
        view.fillEditor(selected);
        view.renderUsers(filteredUsers);
        view.showInfo("Trạng thái hiện tại: " + (selected.isEnabled() ? "Enable" : "Disable"));
    }

    public void lamMoiDanhSach() {
        refreshFiltered(users);
    }

    public void search(String query) {
        String normalized = safeTrim(query).toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            refreshFiltered(users);
            return;
        }

        List<UserViewModel> result = new ArrayList<>();
        for (UserViewModel user : users) {
            if (containsIgnoreCase(user.getMaNguoiDung(), normalized)
                    || containsIgnoreCase(user.getTenDangNhap(), normalized)
                    || containsIgnoreCase(user.getHoTen(), normalized)
                    || containsIgnoreCase(user.getEmail(), normalized)
                    || containsIgnoreCase(user.getVaiTro(), normalized)) {
                result.add(user);
            }
        }

        refreshFiltered(result);
        if (result.isEmpty()) {
            view.showWarning("Không tìm thấy người dùng phù hợp với từ khóa: " + query);
        }
    }

    private void refreshFiltered(List<UserViewModel> source) {
        filteredUsers.clear();
        filteredUsers.addAll(source);
        view.renderUsers(filteredUsers);
        onRowSelected(view.getSelectedTableRow());
    }

    private UserViewModel getSelectedFromFiltered(int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= filteredUsers.size()) {
            return null;
        }
        return filteredUsers.get(selectedIndex);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf('.');
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean containsIgnoreCase(String text, String keywordLowerCase) {
        return Objects.toString(text, "").toLowerCase(Locale.ROOT).contains(keywordLowerCase);
    }

    public static class UserViewModel {
        private String maNguoiDung;
        private String tenDangNhap;
        private String hoTen;
        private String email;
        private String vaiTro;
        private boolean enabled;
        private String password;

        public UserViewModel(String maNguoiDung, String tenDangNhap, String hoTen, String email, String vaiTro, boolean enabled, String password) {
            this.maNguoiDung = maNguoiDung;
            this.tenDangNhap = tenDangNhap;
            this.hoTen = hoTen;
            this.email = email;
            this.vaiTro = vaiTro;
            this.enabled = enabled;
            this.password = password;
        }

        public String getMaNguoiDung() {
            return maNguoiDung;
        }

        public String getTenDangNhap() {
            return tenDangNhap;
        }

        public String getHoTen() {
            return hoTen;
        }

        public void setHoTen(String hoTen) {
            this.hoTen = hoTen;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getVaiTro() {
            return vaiTro;
        }

        public void setVaiTro(String vaiTro) {
            this.vaiTro = vaiTro;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
