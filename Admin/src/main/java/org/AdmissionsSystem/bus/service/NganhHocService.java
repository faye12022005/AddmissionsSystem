package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.AdmissionsSystem.dao.NganhHocDao;
import org.AdmissionsSystem.models.XtNganh;

public class NganhHocService {

    private final NganhHocDao dao = new NganhHocDao();

    public List<XtNganh> getAll() {
        return dao.findAll();
    }

    public List<XtNganh> search(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            return getAll();
        }

        List<XtNganh> filtered = new ArrayList<>();
        for (XtNganh model : getAll()) {
            if (matchesKeyword(model, q)) {
                filtered.add(model);
            }
        }
        return filtered;
    }

    public void add(XtNganh model) {
        validateRequiredFields(model);
        String ma = asText(model.getManganh());
        if (dao.findByMaNganh(ma) != null) {
            throw new IllegalArgumentException("Mã ngành đã tồn tại.");
        }

        XtNganh entity = copyModel(model, new XtNganh());
        entity.setIdnganh(dao.getNextId());
        dao.save(entity);
    }

    public void update(String selectedMaNganh, XtNganh model) {
        validateRequiredFields(model);
        XtNganh existing = dao.findByMaNganh(selectedMaNganh);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi cần cập nhật.");
        }

        String newMa = asText(model.getManganh());
        XtNganh duplicate = dao.findByMaNganh(newMa);
        if (duplicate != null && !duplicate.getIdnganh().equals(existing.getIdnganh())) {
            throw new IllegalArgumentException("Mã ngành mới đã tồn tại.");
        }

        copyModel(model, existing);
        dao.update(existing);
    }

    public void deleteByMaNganh(String maNganh) {
        XtNganh existing = dao.findByMaNganh(maNganh);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi cần xóa.");
        }
        dao.delete(existing);
    }

    public void upsert(XtNganh model) {
        validateRequiredFields(model);
        String ma = asText(model.getManganh());
        XtNganh existing = dao.findByMaNganh(ma);
        if (existing == null) {
            XtNganh entity = copyModel(model, new XtNganh());
            entity.setIdnganh(dao.getNextId());
            dao.save(entity);
            return;
        }
        copyModel(model, existing);
        dao.update(existing);
    }

    private boolean matchesKeyword(XtNganh model, String keyword) {
        return asText(model.getManganh()).toLowerCase(Locale.ROOT).contains(keyword)
                || asText(model.getTennganh()).toLowerCase(Locale.ROOT).contains(keyword)
                || asText(model.getNTohopgoc()).toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void validateRequiredFields(XtNganh model) {
        if (model == null) {
            throw new IllegalArgumentException("Dữ liệu ngành học không hợp lệ.");
        }
        if (asText(model.getManganh()).isEmpty() || asText(model.getTennganh()).isEmpty()) {
            throw new IllegalArgumentException("Mã ngành và Tên ngành là bắt buộc.");
        }
        if (model.getNChitieu() == null) {
            throw new IllegalArgumentException("Chỉ tiêu là bắt buộc.");
        }
    }

    private XtNganh copyModel(XtNganh source, XtNganh target) {
        target.setManganh(asText(source.getManganh()));
        target.setTennganh(asText(source.getTennganh()));
        String toHop = asText(source.getNTohopgoc());
        target.setNTohopgoc(toHop.isEmpty() ? null : toHop);
        
        target.setNChitieu(nvlInt(source.getNChitieu())); // Required, default to 0 if null but should be validated
        target.setNDiemsan(source.getNDiemsan());
        target.setNDiemtrungtuyen(source.getNDiemtrungtuyen());
        target.setNTuyenthang(normalizeYn(source.getNTuyenthang()));
        target.setNDgnl(normalizeYn(source.getNDgnl()));
        target.setNThpt(normalizeYn(source.getNThpt()));
        target.setNVsat(normalizeYn(source.getNVsat()));
        target.setSlXtt(source.getSlXtt());
        target.setSlDgnl(source.getSlDgnl());
        target.setSlVsat(source.getSlVsat());
        target.setSlThpt(source.getSlThpt());
        return target;
    }

    private Integer parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private BigDecimal nvlBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer nvlInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeYn(String value) {
        if (value == null) {
            return "N";
        }
        String normalized = value.trim();
        if ("Y".equalsIgnoreCase(normalized) || "true".equalsIgnoreCase(normalized) || "1".equals(normalized)) {
            return "Y";
        }
        return "N";
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
