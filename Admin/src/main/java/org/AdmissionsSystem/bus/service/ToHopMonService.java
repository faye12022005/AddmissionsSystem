package org.AdmissionsSystem.bus.service;

import java.util.List;
import java.util.Locale;
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
        return dao.findByMaToHop(maToHop);
    }

    public List<XtTohopMonthi> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public void add(XtTohopMonthi entity) {
        validateRequired(entity);
        if (dao.findByMaToHop(entity.getMatohop()) != null) {
            throw new IllegalArgumentException("Mã tổ hợp đã tồn tại.");
        }
        if (entity.getIdtohop() == null || entity.getIdtohop() == 0) {
            entity.setIdtohop(dao.getNextId());
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

    public void upsert(XtTohopMonthi entity) {
        validateRequired(entity);
        XtTohopMonthi existing = dao.findByMaToHop(entity.getMatohop());
        if (existing == null) {
            if (entity.getIdtohop() == null || entity.getIdtohop() == 0) {
                entity.setIdtohop(dao.getNextId());
            }
            dao.save(entity);
        } else {
            entity.setIdtohop(existing.getIdtohop());
            update(entity);
        }
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
    }
}
