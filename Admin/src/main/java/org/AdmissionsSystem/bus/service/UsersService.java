package org.AdmissionsSystem.bus.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.AdmissionsSystem.dao.UsersDao;
import org.AdmissionsSystem.models.Users;

public class UsersService {

    private final UsersDao dao = new UsersDao();

    public List<Users> getAll() {
        return dao.findAll();
    }

    public Users findById(String id) {
        return dao.findById(id);
    }

    public Users findByUsername(String username) {
        return dao.findByUsername(username);
    }

    public List<Users> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public void add(Users user) {
        validateRequired(user);
        if (dao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        if (user.getStatus() == null) {
            user.setStatus("Enable");
        }
        dao.save(user);
    }

    public void update(Users user) {
        validateRequired(user);
        Users existing = dao.findById(user.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng cần cập nhật.");
        }
        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setSdt(user.getSdt());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
        dao.update(existing);
    }

    public void delete(String id) {
        Users existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng cần xóa.");
        }
        dao.delete(existing);
    }

    public void changePassword(String userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        Users existing = dao.findById(userId);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng.");
        }
        existing.setPassword(newPassword);
        dao.update(existing);
    }

    public void toggleRole(String userId) {
        Users existing = dao.findById(userId);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng.");
        }
        String newRole = "admin".equalsIgnoreCase(existing.getRole()) ? "user" : "admin";
        existing.setRole(newRole);
        dao.update(existing);
    }

    public void toggleStatus(String userId) {
        Users existing = dao.findById(userId);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng.");
        }
        String newStatus = "Enable".equalsIgnoreCase(existing.getStatus()) ? "Disable" : "Enable";
        existing.setStatus(newStatus);
        dao.update(existing);
    }

    public Users authenticate(String username, String password) {
        Users user = dao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!"Enable".equalsIgnoreCase(user.getStatus())) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    public long count() {
        return dao.count();
    }

    private void validateRequired(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("Dữ liệu người dùng không hợp lệ.");
        }
        if (isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (isBlank(user.getFullName())) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
