package org.AdmissionsSystem.service;

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

    public XtNganh fromRow(Object[] row) {
        XtNganh model = new XtNganh();
        model.setManganh(asText(rowValue(row, 0)));
        model.setTennganh(asText(rowValue(row, 1)));
        model.setNTohopgoc(asText(rowValue(row, 2)));
        model.setNChitieu(parseInt(rowValue(row, 3)));
        model.setNDiemsan(parseBigDecimal(rowValue(row, 4)));
        model.setNDiemtrungtuyen(parseBigDecimal(rowValue(row, 5)));
        model.setNTuyenthang(normalizeYn(asText(rowValue(row, 6))));
        model.setNDgnl(normalizeYn(asText(rowValue(row, 7))));
        model.setNThpt(normalizeYn(asText(rowValue(row, 8))));
        model.setNVsat(normalizeYn(asText(rowValue(row, 9))));
        model.setSlXtt(parseInt(rowValue(row, 10)));
        model.setSlDgnl(parseInt(rowValue(row, 11)));
        model.setSlVsat(parseInt(rowValue(row, 12)));
        model.setSlThpt(String.valueOf(parseInt(rowValue(row, 13))));
        return model;
    }

    public Object[] toRow(XtNganh model) {
        return new Object[] {
                asText(model.getManganh()),
                asText(model.getTennganh()),
                asText(model.getNTohopgoc()),
                nvlInt(model.getNChitieu()),
                nvlBigDecimal(model.getNDiemsan()),
                nvlBigDecimal(model.getNDiemtrungtuyen()),
                normalizeYn(model.getNTuyenthang()),
                normalizeYn(model.getNDgnl()),
                normalizeYn(model.getNThpt()),
                normalizeYn(model.getNVsat()),
                nvlInt(model.getSlXtt()),
                nvlInt(model.getSlDgnl()),
                nvlInt(model.getSlVsat()),
                parseIntSafe(model.getSlThpt())
        };
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
        if (asText(model.getManganh()).isEmpty() || asText(model.getTennganh()).isEmpty() || asText(model.getNTohopgoc()).isEmpty()) {
            throw new IllegalArgumentException("Mã ngành, Tên ngành, Tổ hợp gốc là bắt buộc.");
        }
    }

    private XtNganh copyModel(XtNganh source, XtNganh target) {
        target.setManganh(asText(source.getManganh()));
        target.setTennganh(asText(source.getTennganh()));
        target.setNTohopgoc(asText(source.getNTohopgoc()));
        target.setNChitieu(nvlInt(source.getNChitieu()));
        target.setNDiemsan(nvlBigDecimal(source.getNDiemsan()));
        target.setNDiemtrungtuyen(nvlBigDecimal(source.getNDiemtrungtuyen()));
        target.setNTuyenthang(normalizeYn(source.getNTuyenthang()));
        target.setNDgnl(normalizeYn(source.getNDgnl()));
        target.setNThpt(normalizeYn(source.getNThpt()));
        target.setNVsat(normalizeYn(source.getNVsat()));
        target.setSlXtt(nvlInt(source.getSlXtt()));
        target.setSlDgnl(nvlInt(source.getSlDgnl()));
        target.setSlVsat(nvlInt(source.getSlVsat()));
        target.setSlThpt(String.valueOf(parseIntSafe(source.getSlThpt())));
        return target;
    }

    private Object rowValue(Object[] row, int index) {
        if (row == null || index < 0 || index >= row.length) {
            return null;
        }
        return row[index];
    }

    private Integer parseInt(Object value) {
        String text = asText(value);
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
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

    private BigDecimal parseBigDecimal(Object value) {
        String text = asText(value);
        if (text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(text);
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
