package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.NganhToHopDao;
import org.AdmissionsSystem.models.XtNganhTohop;

public class NganhToHopService {

    private final NganhToHopDao dao = new NganhToHopDao();

    public List<XtNganhTohop> getAll() {
        return dao.findAll();
    }

    public XtNganhTohop findById(int id) {
        return dao.findById(id);
    }

    public List<XtNganhTohop> findByMaNganh(String maNganh) {
        return dao.findByMaNganh(maNganh);
    }

    public List<XtNganhTohop> findByMaToHop(String maToHop) {
        return dao.findByMaToHop(maToHop);
    }

    public List<XtNganhTohop> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public void add(XtNganhTohop entity) {
        validateRequired(entity);
        // Auto-generate tbKeys if not set
        if (entity.getTbKeys() == null || entity.getTbKeys().isEmpty()) {
            entity.setTbKeys(entity.getManganh() + "_" + entity.getMatohop());
        }
        dao.save(entity);
    }

    public void update(XtNganhTohop entity) {
        validateRequired(entity);
        XtNganhTohop existing = dao.findById(entity.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy liên kết ngành-tổ hợp cần cập nhật.");
        }
        dao.update(entity);
    }

    public void delete(int id) {
        XtNganhTohop existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy liên kết ngành-tổ hợp cần xóa.");
        }
        dao.delete(existing);
    }

    public void upsert(XtNganhTohop entity) {
        validateRequired(entity);
        String key = (entity.getManganh() + "_" + entity.getMatohop()).trim();
        entity.setTbKeys(key);
        XtNganhTohop existing = dao.findByKeys(key);
        if (existing == null) {
            dao.save(entity);
        } else {
            entity.setId(existing.getId());
            dao.update(entity);
        }
    }

    public long count() {
        return dao.count();
    }

    private void validateRequired(XtNganhTohop entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu ngành-tổ hợp không hợp lệ.");
        }
        if (entity.getManganh() == null || entity.getManganh().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ngành không được để trống.");
        }
        if (entity.getMatohop() == null || entity.getMatohop().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tổ hợp không được để trống.");
        }
        if (entity.getThMon1() == null || entity.getThMon1().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên môn 1 không được để trống.");
        }
        if (entity.getThMon2() == null || entity.getThMon2().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên môn 2 không được để trống.");
        }
        if (entity.getThMon3() == null || entity.getThMon3().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên môn 3 không được sé trống.");
        }
        if (entity.getHsmon1() == null) {
            throw new IllegalArgumentException("Hệ số môn 1 không được để trống.");
        }
        if (entity.getHsmon2() == null) {
            throw new IllegalArgumentException("Hệ số môn 2 không được để trống.");
        }
        if (entity.getHsmon3() == null) {
            throw new IllegalArgumentException("Hệ số môn 3 không được để trống.");
        }
    }
}
