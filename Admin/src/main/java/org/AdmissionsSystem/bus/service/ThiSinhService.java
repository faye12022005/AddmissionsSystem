package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.ThiSinhDao;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;

public class ThiSinhService {

    private final ThiSinhDao dao = new ThiSinhDao();

    public List<XtThisinhxettuyen25> getAll() {
        return dao.findAll();
    }

    public XtThisinhxettuyen25 findById(int id) {
        return dao.findById(id);
    }

    public XtThisinhxettuyen25 findByCccd(String cccd) {
        return dao.findByCccd(cccd);
    }

    public List<XtThisinhxettuyen25> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public List<XtThisinhxettuyen25> findAllPaginated(int page, int pageSize) {
        return dao.findAllPaginated(page, pageSize);
    }

    public List<XtThisinhxettuyen25> searchPaginated(String keyword, int page, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllPaginated(page, pageSize);
        }
        return dao.searchPaginated(keyword, page, pageSize);
    }

    public long countByKeyword(String keyword) {
        return dao.countByKeyword(keyword);
    }

    public void add(XtThisinhxettuyen25 entity) {
        validateRequired(entity);
        if (entity.getCccd() != null && !entity.getCccd().isEmpty()) {
            XtThisinhxettuyen25 existing = dao.findByCccd(entity.getCccd());
            if (existing != null) {
                throw new IllegalArgumentException("CCCD đã tồn tại trong hệ thống.");
            }
        }
        if (entity.getIdthisinh() == null || entity.getIdthisinh() == 0) {
            entity.setIdthisinh(dao.getNextId());
        }
        dao.save(entity);
    }

    public void update(XtThisinhxettuyen25 entity) {
        validateRequired(entity);
        XtThisinhxettuyen25 existing = dao.findById(entity.getIdthisinh());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy thí sinh cần cập nhật.");
        }
        existing.setCccd(entity.getCccd());
        existing.setSobaodanh(entity.getSobaodanh());
        existing.setHo(entity.getHo());
        existing.setTen(entity.getTen());
        existing.setNgaySinh(entity.getNgaySinh());
        existing.setDienThoai(entity.getDienThoai());
        existing.setGioiTinh(entity.getGioiTinh());
        existing.setEmail(entity.getEmail());
        existing.setNoiSinh(entity.getNoiSinh());
        existing.setDoiTuong(entity.getDoiTuong());
        existing.setKhuVuc(entity.getKhuVuc());
        existing.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
        dao.update(existing);
    }

    public void delete(int id) {
        XtThisinhxettuyen25 existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy thí sinh cần xóa.");
        }
        dao.delete(existing);
    }

    public void importBatch(List<XtThisinhxettuyen25> entities) {
        for (XtThisinhxettuyen25 entity : entities) {
            if (entity.getCccd() != null && !entity.getCccd().isEmpty()) {
                XtThisinhxettuyen25 existing = dao.findByCccd(entity.getCccd());
                if (existing != null) {
                    // Update existing record
                    entity.setIdthisinh(existing.getIdthisinh());
                    update(entity);
                    continue;
                }
            }
            if (entity.getIdthisinh() == null || entity.getIdthisinh() == 0) {
                entity.setIdthisinh(dao.getNextId());
            }
            dao.save(entity);
        }
    }

    public long count() {
        return dao.count();
    }

    private void validateRequired(XtThisinhxettuyen25 entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu thí sinh không hợp lệ.");
        }
        
        // CCCD Validation
        String cccd = entity.getCccd();
        if (cccd != null && !cccd.isEmpty()) {
            if (!cccd.matches("\\d{9}") && !cccd.matches("\\d{12}")) {
                throw new IllegalArgumentException("CCCD phải có 9 hoặc 12 chữ số.");
            }
        } else {
            throw new IllegalArgumentException("CCCD không được để trống.");
        }

        // Email Validation
        String email = entity.getEmail();
        if (email != null && !email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email không đúng định dạng.");
            }
        }

        // Phone Validation
        String phone = entity.getDienThoai();
        if (phone != null && !phone.isEmpty()) {
            if (!phone.matches("\\d{10}")) {
                throw new IllegalArgumentException("Số điện thoại phải có 10 chữ số.");
            }
        }
    }
}
