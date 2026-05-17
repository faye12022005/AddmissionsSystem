package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.AdmissionsSystem.dao.*;
import org.AdmissionsSystem.models.*;

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

        /**
     * Lấy tổng điểm cộng đã được tính sẵn trong bảng xt_diemcongxetuyen.
     * @param cccd      CCCD thí sinh
     * @param maNganh   Mã ngành
     * @param maToHop   Mã tổ hợp
     * @param phuongThuc Phương thức xét tuyển (THPT/VSAT/DGNL)
     * @return điểm cộng (thang 30, tối đa 3)
     */
    public BigDecimal layDiemCongDaCo(String cccd, String maNganh, String maToHop, String phuongThuc) {
        // Tạo khóa theo đúng format trong DB (ví dụ: TS_xxx_7140231_A01)
        String key = cccd + "_" + maNganh + "_" + maToHop;
        XtDiemcongxetuyen record = dao.findByKeys(key);
        if (record == null) {
            // Nếu chưa có, có thể trả về 0 hoặc log warning
            return BigDecimal.ZERO;
        }
        return record.getDiemtong() != null ? record.getDiemtong() : BigDecimal.ZERO;
    }
}
