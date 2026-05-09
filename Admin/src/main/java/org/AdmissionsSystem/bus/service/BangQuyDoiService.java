package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.BangQuyDoiDao;
import org.AdmissionsSystem.models.XtBangquydoi;

public class BangQuyDoiService {

    private final BangQuyDoiDao dao = new BangQuyDoiDao();

    public List<XtBangquydoi> getAll() { return dao.findAll(); }
    public XtBangquydoi findById(int id) { return dao.findById(id); }
    public List<XtBangquydoi> findByPhuongThuc(String pt) { return dao.findByPhuongThuc(pt); }
    public XtBangquydoi findByMaQuyDoi(String ma) { return dao.findByMaQuyDoi(ma); }

    public List<XtBangquydoi> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAll();
        return dao.search(keyword);
    }

    public void add(XtBangquydoi entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu bảng quy đổi không hợp lệ.");
        if (entity.getIdqd() == null || entity.getIdqd() == 0) entity.setIdqd(dao.getNextId());
        dao.save(entity);
    }

    public void update(XtBangquydoi entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu bảng quy đổi không hợp lệ.");
        XtBangquydoi existing = dao.findById(entity.getIdqd());
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy bản ghi quy đổi.");
        dao.update(entity);
    }

    public void delete(int id) {
        XtBangquydoi existing = dao.findById(id);
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy bản ghi quy đổi cần xóa.");
        dao.delete(existing);
    }

    public long count() { return dao.count(); }
}
