package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.DiemThiDao;
import org.AdmissionsSystem.models.XtDiemthixettuyen;

public class DiemThiService {

    private final DiemThiDao dao = new DiemThiDao();

    public List<XtDiemthixettuyen> getAll() {
        return dao.findAll();
    }

    public XtDiemthixettuyen findById(int id) {
        return dao.findById(id);
    }

    public XtDiemthixettuyen findByCccd(String cccd) {
        return dao.findByCccd(cccd);
    }

    public List<XtDiemthixettuyen> findByPhuongThuc(String phuongThuc) {
        return dao.findByPhuongThuc(phuongThuc);
    }

    public List<XtDiemthixettuyen> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public void add(XtDiemthixettuyen entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu điểm thi không hợp lệ.");
        }
        if (entity.getIddiemthi() == null || entity.getIddiemthi() == 0) {
            entity.setIddiemthi(dao.getNextId());
        }
        dao.save(entity);
    }

    public void update(XtDiemthixettuyen entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu điểm thi không hợp lệ.");
        }
        XtDiemthixettuyen existing = dao.findById(entity.getIddiemthi());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi điểm thi cần cập nhật.");
        }
        dao.update(entity);
    }

    public void delete(int id) {
        XtDiemthixettuyen existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi điểm thi cần xóa.");
        }
        dao.delete(existing);
    }

    public void upsertByCccd(XtDiemthixettuyen entity) {
        if (entity == null || entity.getCccd() == null) {
            throw new IllegalArgumentException("Dữ liệu điểm thi không hợp lệ.");
        }
        XtDiemthixettuyen existing = dao.findByCccd(entity.getCccd());
        if (existing == null) {
            if (entity.getIddiemthi() == null || entity.getIddiemthi() == 0) {
                entity.setIddiemthi(dao.getNextId());
            }
            dao.save(entity);
        } else {
            entity.setIddiemthi(existing.getIddiemthi());
            dao.update(entity);
        }
    }

    public long count() {
        return dao.count();
    }
}
