package org.AdmissionsSystem.bus.service;

import java.util.List;
import org.AdmissionsSystem.dao.DiemCongDao;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;

public class DiemCongService {

    private final DiemCongDao dao = new DiemCongDao();

    public List<XtDiemcongxetuyen> getAll() {
        return dao.findAll();
    }

    public XtDiemcongxetuyen findById(int id) {
        return dao.findById(id);
    }

    public List<XtDiemcongxetuyen> findByCccd(String cccd) {
        return dao.findByCccd(cccd);
    }

    public XtDiemcongxetuyen findByKeys(String dcKeys) {
        return dao.findByKeys(dcKeys);
    }

    public List<XtDiemcongxetuyen> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.search(keyword);
    }

    public List<XtDiemcongxetuyen> searchByCccd(String cccdKeyword) {
        if (cccdKeyword == null || cccdKeyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.searchByCccd(cccdKeyword);
    }

    public void add(XtDiemcongxetuyen entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu điểm cộng không hợp lệ.");
        if (entity.getIddiemcong() == null || entity.getIddiemcong() == 0) entity.setIddiemcong(dao.getNextId());
        if (entity.getDcKeys() == null || entity.getDcKeys().isEmpty())
            entity.setDcKeys(entity.getTsCccd() + "_" + entity.getManganh() + "_" + entity.getMatohop());
        dao.save(entity);
    }

    public void update(XtDiemcongxetuyen entity) {
        if (entity == null) throw new IllegalArgumentException("Dữ liệu điểm cộng không hợp lệ.");
        XtDiemcongxetuyen existing = dao.findById(entity.getIddiemcong());
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy bản ghi điểm cộng.");
        dao.update(entity);
    }

    public void delete(int id) {
        XtDiemcongxetuyen existing = dao.findById(id);
        if (existing == null) throw new IllegalArgumentException("Không tìm thấy bản ghi điểm cộng cần xóa.");
        dao.delete(existing);
    }

    public long count() { return dao.count(); }
}
