package org.AdmissionsSystem.bus.controller;

import org.AdmissionsSystem.bus.service.UsersService;
import org.AdmissionsSystem.gui.modules.QuanLyNguoiDung.UsersPanel;
import org.AdmissionsSystem.models.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersController {
    private final UsersPanel view;
    private final UsersService service = new UsersService();
    private List<UserViewModel> filteredUsers = new ArrayList<>();

    public UsersController(UsersPanel view) {
        this.view = view;
    }

    public void loadInitialData() {
        try {
            List<Users> entities = service.getAll();
            filteredUsers = toViewModels(entities);
            view.renderUsers(filteredUsers);
        } catch (Exception e) {
            view.showWarning("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public void onRowSelected(int selectedIndex) {
        view.setAddMode(false);
        UserViewModel selected = getSelectedFromFiltered(selectedIndex);
        view.fillEditor(selected);
    }

    public void batDauThem() {
        view.fillEditor(null);
        view.setAddMode(true);
    }

    public void hoanTatThem() {
        String username = view.getInputUsername();
        String hoTen = safeTrim(view.getInputHoTen());
        String email = safeTrim(view.getInputEmail());

        if (username.isEmpty()) { view.showWarning("Tên đăng nhập không được để trống."); return; }
        if (hoTen.isEmpty()) { view.showWarning("Họ tên không được để trống."); return; }
        if (!isValidEmail(email)) { view.showWarning("Email không hợp lệ."); return; }

        String password = view.askNewPassword();
        if (password == null) return;
        if (password.length() < 6) { view.showWarning("Mật khẩu phải có ít nhất 6 ký tự."); return; }

        try {
            Users newUser = new Users();
            newUser.setUsername(username);
            newUser.setFullName(hoTen);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole(view.getInputVaiTro());
            newUser.setStatus(view.getInputEnabled() ? "Enable" : "Disable");

            service.add(newUser);
            view.setAddMode(false);
            loadInitialData();
            view.showInfo("Đã thêm người dùng mới thành công.");
        } catch (Exception e) {
            view.showWarning("Lỗi thêm người dùng: " + e.getMessage());
        }
    }

    public void suaThongTin() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần sửa."); return; }

        String hoTen = safeTrim(view.getInputHoTen());
        String email = safeTrim(view.getInputEmail());
        if (hoTen.isEmpty()) { view.showWarning("Họ tên không được để trống."); return; }
        if (!isValidEmail(email)) { view.showWarning("Email không hợp lệ."); return; }

        try {
            Users entity = service.findById(selected.getMaNguoiDung());
            if (entity == null) { view.showWarning("Người dùng không tồn tại."); return; }
            entity.setFullName(hoTen);
            entity.setEmail(email);
            entity.setRole(view.getInputVaiTro());
            entity.setStatus(view.getInputEnabled() ? "Enable" : "Disable");
            service.update(entity);
            loadInitialData();
            view.showInfo("Đã cập nhật thông tin người dùng.");
        } catch (Exception e) {
            view.showWarning("Lỗi cập nhật: " + e.getMessage());
        }
    }

    public void doiMatKhau() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần đổi mật khẩu."); return; }

        String newPassword = view.askNewPassword();
        if (newPassword == null) return;
        if (newPassword.length() < 6) { view.showWarning("Mật khẩu mới phải có ít nhất 6 ký tự."); return; }

        try {
            service.changePassword(selected.getMaNguoiDung(), newPassword);
            view.showInfo("Đã đổi mật khẩu cho tài khoản: " + selected.getTenDangNhap());
        } catch (Exception e) {
            view.showWarning("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }

    public void doiQuyen() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần đổi quyền."); return; }

        try {
            service.toggleRole(selected.getMaNguoiDung());
            loadInitialData();
            view.showInfo("Đã đổi quyền thành công.");
        } catch (Exception e) {
            view.showWarning("Lỗi đổi quyền: " + e.getMessage());
        }
    }

    public void batTatNguoiDung() {
        UserViewModel selected = getSelectedFromFiltered(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần thay đổi trạng thái."); return; }

        try {
            service.toggleStatus(selected.getMaNguoiDung());
            loadInitialData();
            Users updated = service.findById(selected.getMaNguoiDung());
            view.showInfo("Trạng thái hiện tại: " + (updated != null ? updated.getStatus() : "N/A"));
        } catch (Exception e) {
            view.showWarning("Lỗi thay đổi trạng thái: " + e.getMessage());
        }
    }

    public void lamMoiDanhSach() {
        loadInitialData();
    }

    public void search(String query) {
        try {
            List<Users> results = service.search(query);
            filteredUsers = toViewModels(results);
            view.renderUsers(filteredUsers);
            if (filteredUsers.isEmpty()) {
                view.showWarning("Không tìm thấy người dùng phù hợp với từ khóa: " + query);
            }
        } catch (Exception e) {
            view.showWarning("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    private List<UserViewModel> toViewModels(List<Users> entities) {
        List<UserViewModel> viewModels = new ArrayList<>();
        for (Users u : entities) {
            viewModels.add(new UserViewModel(
                u.getId(), u.getUsername(), u.getFullName(),
                u.getEmail(), u.getRole(),
                "Enable".equalsIgnoreCase(u.getStatus()), u.getPassword()
            ));
        }
        return viewModels;
    }

    private UserViewModel getSelectedFromFiltered(int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= filteredUsers.size()) return null;
        return filteredUsers.get(selectedIndex);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf('.');
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // ViewModel class kept for UI compatibility
    public static class UserViewModel {
        private String maNguoiDung;
        private String tenDangNhap;
        private String hoTen;
        private String email;
        private String vaiTro;
        private boolean enabled;
        private String password;

        public UserViewModel(String maNguoiDung, String tenDangNhap, String hoTen, String email, String vaiTro, boolean enabled, String password) {
            this.maNguoiDung = maNguoiDung; this.tenDangNhap = tenDangNhap;
            this.hoTen = hoTen; this.email = email; this.vaiTro = vaiTro;
            this.enabled = enabled; this.password = password;
        }

        public String getMaNguoiDung() { return maNguoiDung; }
        public String getTenDangNhap() { return tenDangNhap; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getVaiTro() { return vaiTro; }
        public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
