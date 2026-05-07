package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.NguyenVongDao;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;

public class NguyenVongService {

    private final NguyenVongDao dao = new NguyenVongDao();

    public List<XtNguyenvongxettuyen> getAll() { return dao.findAll(); }
    public XtNguyenvongxettuyen findById(int id) { return dao.findById(id); }
    public List<XtNguyenvongxettuyen> findByCccd(String cccd) { return dao.findByCccd(cccd); }
    public List<XtNguyenvongxettuyen> findByMaNganh(String maNganh) { return dao.findByMaNganh(maNganh); }

    public List<XtNguyenvongxettuyen> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAll();
        return dao.search(keyword);
    }

    public void add(XtNguyenvongxettuyen entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu nguyện vọng không hợp lệ.");
        if (entity.getIdnv() == null || entity.getIdnv() == 0) entity.setIdnv(dao.getNextId());
        if (entity.getNvKeys() == null || entity.getNvKeys().isEmpty())
            entity.setNvKeys(entity.getNnCccd() + "_" + entity.getNvManganh() + "_" + entity.getNvTt());
        dao.save(entity);
    }

    public void update(XtNguyenvongxettuyen entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu nguyện vọng không hợp lệ.");
        XtNguyenvongxettuyen existing = dao.findById(entity.getIdnv());
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần cập nhật.");
        dao.update(entity);
    }

    public void delete(int id) {
        XtNguyenvongxettuyen existing = dao.findById(id);
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần xóa.");
        dao.delete(existing);
    }

    public long count() { return dao.count(); }
}
