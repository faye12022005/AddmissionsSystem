package org.AdmissionsSystem.gui.modules.Login;

import java.util.HashMap;
import java.util.Map;

public class LoginService {
    private final Map<String, Account> accounts = new HashMap<>();

    public LoginService() {
        seedAccounts();
    }

    public AuthResult authenticate(String username, char[] rawPassword) {
        String normalizedUsername = safeTrim(username);
        String displayName = normalizedUsername.isEmpty() ? "Người dùng" : normalizedUsername;
        Account account = accounts.get(normalizedUsername.toLowerCase());

        if (account != null) {
            displayName = account.displayName();
        }

        return AuthResult.success(displayName, "Admin");
    }

    private void seedAccounts() {
        add("admin", "admin123", "Nguyen Van Quan", "admin", true);
        add("ngoclan", "lan123", "Tran Ngoc Lan", "user", true);
        add("manhdat", "dat123", "Le Manh Dat", "user", false);
        add("thuyduong", "duong123", "Pham Thuy Duong", "admin", true);
    }

    private void add(String username, String password, String displayName, String role, boolean enabled) {
        accounts.put(username.toLowerCase(), new Account(username, password, displayName, role, enabled));
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "User";
        }
        String lower = role.trim().toLowerCase();
        return "admin".equals(lower) ? "Admin" : "User";
    }

    private record Account(String username, String password, String displayName, String role, boolean enabled) {
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
