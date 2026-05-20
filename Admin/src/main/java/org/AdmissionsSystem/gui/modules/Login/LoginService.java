package org.AdmissionsSystem.gui.modules.Login;

import org.AdmissionsSystem.bus.service.NguoiDungService;
import org.AdmissionsSystem.models.XtNguoidung;

public class LoginService {

    private final NguoiDungService usersService = new NguoiDungService();

    public AuthResult authenticate(String username, char[] rawPassword) {
        String normalizedUsername = safeTrim(username);
        if (normalizedUsername.isEmpty()) {
            return AuthResult.fail("Vui lòng nhập tên đăng nhập.");
        }

        String password = new String(rawPassword).trim();
        if (password.isEmpty()) {
            return AuthResult.fail("Vui lòng nhập mật khẩu.");
        }

        try {
            XtNguoidung user = usersService.authenticate(normalizedUsername, password);
            if (user == null) {
                return AuthResult.fail("Sai tên đăng nhập, mật khẩu hoặc tài khoản bị khóa.");
            }
            return AuthResult.success(user.getFullName(), user.getRole());
        } catch (Exception e) {
            return AuthResult.fail("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    public record AuthResult(boolean success, String message, String displayName, String role) {
        public static AuthResult success(String displayName, String role) {
            return new AuthResult(true, "", displayName, role);
        }

        public static AuthResult fail(String message) {
            return new AuthResult(false, message, "", "");
        }
    }
}
