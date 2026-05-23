package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.AdmissionsSystem.dao.DiemCongDao;
import org.AdmissionsSystem.dao.DiemThiDao;
import org.AdmissionsSystem.dao.NganhToHopDao;
import org.AdmissionsSystem.dao.NguyenVongDao;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;

public class DiemCongService {

    private final DiemCongDao dao = new DiemCongDao();
    private final DiemThiDao diemThiDao = new DiemThiDao();
    private final NguyenVongDao nguyenVongDao = new NguyenVongDao();
    private final NganhToHopDao nganhToHopDao = new NganhToHopDao();

    private static final String[] IMPORT_DCC_COLUMNS = {"CCCD", "Điểm Quy đổi", "Điểm cộng"};
    private static final String[] IMPORT_UTXT_COLUMNS = {"CCCD", "Điểm UTXT"};
    private static final Map<String, String> IMPORT_DCC_ALIASES = buildDccAliases();
    private static final Map<String, String> IMPORT_UTXT_ALIASES = buildUtxtAliases();

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

    public String[] getImportDccColumns() {
        return IMPORT_DCC_COLUMNS.clone();
    }

    public Map<String, String> getImportDccAliases() {
        return IMPORT_DCC_ALIASES;
    }

    public String[] getImportUtxtColumns() {
        return IMPORT_UTXT_COLUMNS.clone();
    }

    public Map<String, String> getImportUtxtAliases() {
        return IMPORT_UTXT_ALIASES;
    }

    public ImportResult importDccRows(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return ImportResult.empty();
        }

        Map<String, List<XtNganhTohop>> toHopByNganh = buildToHopByNganh();
        Map<String, Boolean> hasN1WishCache = new HashMap<>();

        int processedRows = 0;
        int updatedDiemThiRows = 0;
        int updatedDiemCongRows = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            int rowNumber = i + 2;
            Object[] row = rows.get(i);

            String cccd = safeText(readCell(row, 0));
            BigDecimal diemQuyDoi = parseFlexibleBigDecimal(readCell(row, 1));
            BigDecimal diemCong = parseFlexibleBigDecimal(readCell(row, 2));

            if (cccd.isBlank()) {
                errors.add("Dòng " + rowNumber + ": CCCD trống.");
                continue;
            }

            String cccdKey = normalizeKey(cccd);
            boolean hasN1Wish = hasN1WishCache.computeIfAbsent(
                    cccdKey,
                    key -> hasN1Wish(cccd, toHopByNganh));
            processedRows++;

            if (hasN1Wish) {
                if (diemQuyDoi == null) {
                    errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): thiếu Điểm Quy đổi (cột E).");
                    continue;
                }

                List<XtDiemthixettuyen> diemThiRows = diemThiDao.findAllByCccd(cccd);
                if (diemThiRows == null || diemThiRows.isEmpty()) {
                    errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): không tìm thấy bản ghi xt_diemthixettuyen.");
                    continue;
                }

                for (XtDiemthixettuyen diemThi : diemThiRows) {
                    BigDecimal currentEnglish = firstNonNull(diemThi.getN1Thi(), diemThi.getN1Cc(), BigDecimal.ZERO);
                    BigDecimal targetN1Cc = max(currentEnglish, diemQuyDoi);

                    if (sameNumber(diemThi.getN1Cc(), targetN1Cc)) {
                        continue;
                    }

                    diemThi.setN1Cc(targetN1Cc);
                    diemThiDao.update(diemThi);
                    updatedDiemThiRows++;
                }
            } else {
                if (diemCong == null) {
                    errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): thiếu Điểm cộng (cột F).");
                    continue;
                }

                List<XtDiemcongxetuyen> diemCongRows = dao.findByCccd(cccd);
                if (diemCongRows == null || diemCongRows.isEmpty()) {
                    errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): không tìm thấy bản ghi xt_diemcongxetuyen.");
                    continue;
                }

                for (XtDiemcongxetuyen item : diemCongRows) {
                    boolean changed = false;

                    if (!sameNumber(item.getDiemcc(), diemCong)) {
                        item.setDiemcc(diemCong);
                        changed = true;
                    }

                    BigDecimal diemTongMoi = nvl(item.getDiemcc()).add(nvl(item.getDiemutxt()));
                    if (!sameNumber(item.getDiemtong(), diemTongMoi)) {
                        item.setDiemtong(diemTongMoi);
                        changed = true;
                    }

                    if (changed) {
                        dao.update(item);
                        updatedDiemCongRows++;
                    }
                }
            }
        }

        return new ImportResult(rows.size(), processedRows, updatedDiemThiRows, updatedDiemCongRows, errors);
    }

    public ImportResult importUtxtRows(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return ImportResult.empty();
        }

        int processedRows = 0;
        int updatedDiemCongRows = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            int rowNumber = i + 2;
            Object[] row = rows.get(i);

            String cccd = safeText(readCell(row, 0));
            BigDecimal diemUtxt = parseFlexibleBigDecimal(readCell(row, 1));

            if (cccd.isBlank()) {
                errors.add("Dòng " + rowNumber + ": CCCD trống.");
                continue;
            }
            if (diemUtxt == null) {
                errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): thiếu Điểm UTXT.");
                continue;
            }

            processedRows++;
            List<XtDiemcongxetuyen> diemCongRows = dao.findByCccd(cccd);
            if (diemCongRows == null || diemCongRows.isEmpty()) {
                errors.add("Dòng " + rowNumber + " (CCCD " + cccd + "): không tìm thấy bản ghi xt_diemcongxetuyen.");
                continue;
            }

            for (XtDiemcongxetuyen item : diemCongRows) {
                boolean changed = false;

                if (!sameNumber(item.getDiemutxt(), diemUtxt)) {
                    item.setDiemutxt(diemUtxt);
                    changed = true;
                }

                BigDecimal diemTongMoi = nvl(item.getDiemcc()).add(nvl(item.getDiemutxt()));
                if (!sameNumber(item.getDiemtong(), diemTongMoi)) {
                    item.setDiemtong(diemTongMoi);
                    changed = true;
                }

                if (changed) {
                    dao.update(item);
                    updatedDiemCongRows++;
                }
            }
        }

        return new ImportResult(rows.size(), processedRows, 0, updatedDiemCongRows, errors);
    }

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

    private Map<String, List<XtNganhTohop>> buildToHopByNganh() {
        Map<String, List<XtNganhTohop>> result = new HashMap<>();
        for (XtNganhTohop row : nganhToHopDao.findAll()) {
            if (row == null) {
                continue;
            }
            String key = normalizeKey(row.getManganh());
            if (key.isBlank()) {
                continue;
            }
            result.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private boolean hasN1Wish(String cccd, Map<String, List<XtNganhTohop>> toHopByNganh) {
        List<XtNguyenvongxettuyen> wishes = nguyenVongDao.findByCccd(cccd);
        if (wishes == null || wishes.isEmpty()) {
            return false;
        }

        for (XtNguyenvongxettuyen nv : wishes) {
            if (nv == null) {
                continue;
            }
            String maNganhKey = normalizeKey(nv.getNvManganh());
            if (maNganhKey.isBlank()) {
                continue;
            }
            List<XtNganhTohop> toHopRows = toHopByNganh.getOrDefault(maNganhKey, List.of());
            for (XtNganhTohop row : toHopRows) {
                if (containsN1(row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsN1(XtNganhTohop row) {
        if (row == null) {
            return false;
        }
        if (Boolean.TRUE.equals(row.getN1())) {
            return true;
        }
        return "n1".equalsIgnoreCase(safeText(row.getThMon1()))
                || "n1".equalsIgnoreCase(safeText(row.getThMon2()))
                || "n1".equalsIgnoreCase(safeText(row.getThMon3()));
    }

    private String readCell(Object[] row, int index) {
        if (row == null || index < 0 || index >= row.length || row[index] == null) {
            return "";
        }
        return row[index].toString();
    }

    private BigDecimal parseFlexibleBigDecimal(String raw) {
        String value = safeText(raw);
        if (value.isBlank()) {
            return null;
        }
        String normalized = value.replace(" ", "").replace(",", ".");
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeKey(String value) {
        return safeText(value).toLowerCase(Locale.ROOT);
    }

    private BigDecimal max(BigDecimal a, BigDecimal b) {
        BigDecimal x = nvl(a);
        BigDecimal y = nvl(b);
        return x.compareTo(y) >= 0 ? x : y;
    }

    private BigDecimal firstNonNull(BigDecimal first, BigDecimal second, BigDecimal fallback) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return fallback;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean sameNumber(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.compareTo(b) == 0;
    }

    private static Map<String, String> buildDccAliases() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("so cccd", "CCCD");
        map.put("can cuoc", "CCCD");
        map.put("can cuoc cong dan", "CCCD");
        map.put("diem quy doi", "Điểm Quy đổi");
        map.put("diem qd", "Điểm Quy đổi");
        map.put("diem cong", "Điểm cộng");
        return map;
    }

    private static Map<String, String> buildUtxtAliases() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("so cccd", "CCCD");
        map.put("can cuoc", "CCCD");
        map.put("can cuoc cong dan", "CCCD");
        map.put("diem utxt", "Điểm UTXT");
        map.put("diem uu tien xet tuyen", "Điểm UTXT");
        map.put("diem cong", "Điểm UTXT");
        return map;
    }

    public record ImportResult(
            int totalRows,
            int processedRows,
            int updatedDiemThiRows,
            int updatedDiemCongRows,
            List<String> errors) {
        public static ImportResult empty() {
            return new ImportResult(0, 0, 0, 0, List.of());
        }
    }
}
