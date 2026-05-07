package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.ToHopMonDao;
import org.AdmissionsSystem.models.XtTohopMonthi;

public class ToHopMonService {

    private final ToHopMonDao dao = new ToHopMonDao();

    public List<XtTohopMonthi> getAll() {
        return dao.findAll();
    }

    public XtTohopMonthi findById(int id) {
        return dao.findById(id);
    }

    public XtTohopMonthi findByMaToHop(String maToHop) {
        return dao.timTheoMaToHop(maToHop);
    }

    public List<XtTohopMonthi> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public void add(XtTohopMonthi entity) {
        validateRequired(entity);
        if (dao.timTheoMaToHop(entity.getMatohop()) != null) {
            throw new IllegalArgumentException("Mã tổ hợp đã tồn tại.");
        }
        // ID is auto-increment in DB, but if needed manual:
        if (entity.getIdtohop() == null || entity.getIdtohop() == 0) {
            entity.setIdtohop(dao.layIdTiepTheo());
        }
        dao.save(entity);
    }

    public void update(XtTohopMonthi entity) {
        validateRequired(entity);
        XtTohopMonthi existing = dao.findById(entity.getIdtohop());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy tổ hợp cần cập nhật.");
        }
        existing.setMatohop(entity.getMatohop());
        existing.setTentohop(entity.getTentohop());
        existing.setMon1(entity.getMon1());
        existing.setMon2(entity.getMon2());
        existing.setMon3(entity.getMon3());
        dao.update(existing);
    }

    public void delete(int id) {
        XtTohopMonthi existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy tổ hợp cần xóa.");
        }
        dao.delete(existing);
    }

    public long count() {
        return dao.count();
    }

    private void validateRequired(XtTohopMonthi entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu tổ hợp không hợp lệ.");
        }
        if (entity.getMatohop() == null || entity.getMatohop().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tổ hợp không được để trống.");
        }
        if (entity.getMon1() == null || entity.getMon1().trim().isEmpty() ||
            entity.getMon2() == null || entity.getMon2().trim().isEmpty() ||
            entity.getMon3() == null || entity.getMon3().trim().isEmpty()) {
            throw new IllegalArgumentException("Các môn học không được để trống.");
        }
    }
}
