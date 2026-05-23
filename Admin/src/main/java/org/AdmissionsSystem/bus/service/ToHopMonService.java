package org.AdmissionsSystem.bus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.AdmissionsSystem.dao.ToHopMonDao;
import org.AdmissionsSystem.models.XtTohopMonthi;

public class ToHopMonService {

    private final ToHopMonDao dao = new ToHopMonDao();

    private static final String[] IMPORT_COLUMNS = {
        "matohop",
        "tentohop",
        "mon1",
        "mon2",
        "mon3"
    };

    private static final Map<String, String> IMPORT_ALIASES = buildImportAliases();

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

    public ImportPreview previewImport(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return new ImportPreview(List.of(), List.of());
        }

        List<ToHopInput> validRows = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        Set<String> seenMa = new HashSet<>();

        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            int rowNumber = i + 2;

            String maToHop = asText(rowValue(row, 0));
            String tenToHop = asText(rowValue(row, 1));
            String mon1 = asText(rowValue(row, 2));
            String mon2 = asText(rowValue(row, 3));
            String mon3 = asText(rowValue(row, 4));

            List<String> errorMessages = new ArrayList<>();
            if (isBlank(maToHop)) {
                errorMessages.add("Mã tổ hợp không được để trống");
            }
            if (isBlank(mon1) || isBlank(mon2) || isBlank(mon3)) {
                errorMessages.add("Các môn học không được để trống");
            }

            String normalizedMa = normalizeKey(maToHop);
            if (!isBlank(normalizedMa)) {
                if (seenMa.contains(normalizedMa)) {
                    errorMessages.add("Mã tổ hợp bị trùng trong file import");
                } else {
                    seenMa.add(normalizedMa);
                }
            }

            if (!isBlank(maToHop) && dao.timTheoMaToHop(maToHop) != null) {
                errorMessages.add("Mã tổ hợp đã tồn tại trong hệ thống");
            }

            if (errorMessages.isEmpty()) {
                validRows.add(new ToHopInput(
                    safeText(maToHop),
                    safeText(tenToHop),
                    safeText(mon1),
                    safeText(mon2),
                    safeText(mon3)
                ));
            } else {
                String message = String.join("; ", errorMessages);
                errors.add(new ImportError(rowNumber, maToHop, message));
            }
        }

        return new ImportPreview(validRows, errors);
    }

    public int importRows(List<ToHopInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return 0;
        }
        int imported = 0;
        for (ToHopInput input : inputs) {
            XtTohopMonthi entity = new XtTohopMonthi();
            entity.setMatohop(input.maToHop());
            entity.setTentohop(input.tenToHop());
            entity.setMon1(input.mon1());
            entity.setMon2(input.mon2());
            entity.setMon3(input.mon3());
            add(entity);
            imported++;
        }
        return imported;
    }

    public String[] getImportColumns() {
        return IMPORT_COLUMNS.clone();
    }

    public Map<String, String> getImportAliases() {
        return IMPORT_ALIASES;
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

    private Object rowValue(Object[] row, int index) {
        if (row == null || index < 0 || index >= row.length) {
            return null;
        }
        return row[index];
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }

    private static Map<String, String> buildImportAliases() {
        Map<String, String> map = new HashMap<>();
        map.put("ma to hop", "matohop");
        map.put("ma tohop", "matohop");
        map.put("ma", "matohop");
        map.put("ten to hop", "tentohop");
        map.put("ten tohop", "tentohop");
        map.put("ten", "tentohop");
        map.put("mon 1", "mon1");
        map.put("mon 2", "mon2");
        map.put("mon 3", "mon3");
        map.put("m1", "mon1");
        map.put("m2", "mon2");
        map.put("m3", "mon3");
        return Collections.unmodifiableMap(map);
    }

    public record ToHopInput(
        String maToHop,
        String tenToHop,
        String mon1,
        String mon2,
        String mon3) {
    }

    public record ImportError(
        int rowNumber,
        String maToHop,
        String message) {
    }

    public record ImportPreview(
        List<ToHopInput> validRows,
        List<ImportError> errors) {

        public int validCount() {
            return validRows == null ? 0 : validRows.size();
        }

        public int errorCount() {
            return errors == null ? 0 : errors.size();
        }

        public int totalCount() {
            return validCount() + errorCount();
        }
    }
}
