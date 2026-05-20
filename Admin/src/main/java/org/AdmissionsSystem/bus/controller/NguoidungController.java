package org.AdmissionsSystem.bus.controller;

import org.AdmissionsSystem.bus.service.NguoiDungService;
import org.AdmissionsSystem.gui.modules.QuanLyNguoiDung.UsersPanel;
import org.AdmissionsSystem.models.XtNguoidung;

import java.util.ArrayList;
import java.util.List;

public class NguoidungController {
    private final UsersPanel view;
    private final NguoiDungService service = new NguoiDungService();
    private List<UserViewModel> filteredUsers = new ArrayList<>();
    private List<UserViewModel> currentPageUsers = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 20;
    private String currentKeyword = "";

    public NguoidungController(UsersPanel view) {
        this.view = view;
    }

    public void loadInitialData() {
        try {
            List<XtNguoidung> entities = service.getAll();
            filteredUsers = toViewModels(entities);
            currentKeyword = "";
            currentPage = 1;
            renderPage();
        } catch (Exception e) {
            view.showWarning("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public void onRowSelected(int selectedIndex) {
        view.setAddMode(false);
        UserViewModel selected = getSelectedFromCurrentPage(selectedIndex);
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
        String sdt = safeTrim(view.getInputSdt());

        if (username.isEmpty()) { view.showWarning("Tên đăng nhập không được để trống."); return; }
        if (hoTen.isEmpty()) { view.showWarning("Họ tên không được để trống."); return; }
        if (!isValidEmail(email)) { view.showWarning("Email không hợp lệ."); return; }
        if (!sdt.isEmpty() && !sdt.matches("\\d{10}")) { view.showWarning("Số điện thoại phải có 10 chữ số."); return; }

        String password = view.askNewPassword();
        if (password == null) return;
        if (password.length() < 6) { view.showWarning("Mật khẩu phải có ít nhất 6 ký tự."); return; }

        try {
            XtNguoidung newUser = new XtNguoidung();
            newUser.setUsername(username);
            newUser.setFullName(hoTen);
            newUser.setEmail(email);
            newUser.setSdt(sdt);
            newUser.setPassword(password);
            newUser.setRole(view.getInputVaiTro());
            newUser.setStatus(view.getInputEnabled() ? "Enable" : "Disable");

            service.add(newUser);
            view.setAddMode(false);
            reloadWithCurrentFilter();
            view.showInfo("Đã thêm người dùng mới thành công.");
        } catch (Exception e) {
            view.showWarning("Lỗi thêm người dùng: " + e.getMessage());
        }
    }

    public void suaThongTin() {
        UserViewModel selected = getSelectedFromCurrentPage(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần sửa."); return; }

        String hoTen = safeTrim(view.getInputHoTen());
        String email = safeTrim(view.getInputEmail());
        String sdt = safeTrim(view.getInputSdt());
        if (hoTen.isEmpty()) { view.showWarning("Họ tên không được để trống."); return; }
        if (!isValidEmail(email)) { view.showWarning("Email không hợp lệ."); return; }
        if (!sdt.isEmpty() && !sdt.matches("\\d{10}")) { view.showWarning("Số điện thoại phải có 10 chữ số."); return; }

        try {
            XtNguoidung entity = service.findById(selected.getMaNguoiDung());
            if (entity == null) { view.showWarning("Người dùng không tồn tại."); return; }
            entity.setFullName(hoTen);
            entity.setEmail(email);
            entity.setSdt(sdt);
            entity.setRole(view.getInputVaiTro());
            entity.setStatus(view.getInputEnabled() ? "Enable" : "Disable");
            service.update(entity);
            reloadWithCurrentFilter();
            view.showInfo("Đã cập nhật thông tin người dùng.");
        } catch (Exception e) {
            view.showWarning("Lỗi cập nhật: " + e.getMessage());
        }
    }

    public void doiMatKhau() {
        UserViewModel selected = getSelectedFromCurrentPage(view.getSelectedTableRow());
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
        UserViewModel selected = getSelectedFromCurrentPage(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần đổi quyền."); return; }

        try {
            service.toggleRole(selected.getMaNguoiDung());
            reloadWithCurrentFilter();
            view.showInfo("Đã đổi quyền thành công.");
        } catch (Exception e) {
            view.showWarning("Lỗi đổi quyền: " + e.getMessage());
        }
    }

    public void batTatNguoiDung() {
        UserViewModel selected = getSelectedFromCurrentPage(view.getSelectedTableRow());
        if (selected == null) { view.showWarning("Vui lòng chọn người dùng cần thay đổi trạng thái."); return; }

        try {
            service.toggleStatus(selected.getMaNguoiDung());
            reloadWithCurrentFilter();
            XtNguoidung updated = service.findById(selected.getMaNguoiDung());
            view.showInfo("Trạng thái hiện tại: " + (updated != null ? updated.getStatus() : "N/A"));
        } catch (Exception e) {
            view.showWarning("Lỗi thay đổi trạng thái: " + e.getMessage());
        }
    }

    public void lamMoiDanhSach() {
        currentKeyword = "";
        currentPage = 1;
        view.setSearchText("");
        reloadWithCurrentFilter();
    }

    public void search(String query) {
        try {
            currentKeyword = safeTrim(query);
            List<XtNguoidung> results = service.search(currentKeyword);
            filteredUsers = toViewModels(results);
            currentPage = 1;
            renderPage();
            if (filteredUsers.isEmpty() && !currentKeyword.isEmpty()) {
                view.showWarning("Không tìm thấy người dùng phù hợp với từ khóa: " + currentKeyword);
            }
        } catch (Exception e) {
            view.showWarning("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderPage();
        }
    }

    public void nextPage() {
        int totalPages = getTotalPages();
        if (currentPage < totalPages) {
            currentPage++;
            renderPage();
        }
    }

    public void changePageSize(int newPageSize) {
        if (newPageSize <= 0) {
            return;
        }
        pageSize = newPageSize;
        currentPage = 1;
        renderPage();
    }

    private List<UserViewModel> toViewModels(List<XtNguoidung> entities) {
        List<UserViewModel> viewModels = new ArrayList<>();
        for (XtNguoidung u : entities) {
            viewModels.add(new UserViewModel(
                u.getId(), u.getUsername(), u.getFullName(),
                u.getEmail(), u.getSdt(), u.getRole(),
                "Enable".equalsIgnoreCase(u.getStatus()), u.getPassword()
            ));
        }
        return viewModels;
    }

    private UserViewModel getSelectedFromCurrentPage(int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= currentPageUsers.size()) return null;
        return currentPageUsers.get(selectedIndex);
    }

    private void renderPage() {
        int totalRows = filteredUsers.size();
        int totalPages = getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalRows);
        if (start >= end) {
            currentPageUsers = new ArrayList<>();
        } else {
            currentPageUsers = new ArrayList<>(filteredUsers.subList(start, end));
        }

        view.renderUsers(currentPageUsers);
        view.updatePagination(currentPage, totalPages, totalRows);
    }

    private int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        return (int) Math.ceil(filteredUsers.size() / (double) pageSize);
    }

    private void reloadWithCurrentFilter() {
        List<XtNguoidung> results = service.search(currentKeyword);
        filteredUsers = toViewModels(results);
        currentPage = Math.min(currentPage, getTotalPages());
        if (currentPage <= 0) {
            currentPage = 1;
        }
        renderPage();
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
        private String sdt;
        private String vaiTro;
        private boolean enabled;
        private String password;

        public UserViewModel(String maNguoiDung, String tenDangNhap, String hoTen, String email, String sdt, String vaiTro, boolean enabled, String password) {
            this.maNguoiDung = maNguoiDung; this.tenDangNhap = tenDangNhap;
            this.hoTen = hoTen; this.email = email; this.sdt = sdt; this.vaiTro = vaiTro;
            this.enabled = enabled; this.password = password;
        }

        public String getMaNguoiDung() { return maNguoiDung; }
        public String getTenDangNhap() { return tenDangNhap; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSdt() { return sdt; }
        public String getVaiTro() { return vaiTro; }
        public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
