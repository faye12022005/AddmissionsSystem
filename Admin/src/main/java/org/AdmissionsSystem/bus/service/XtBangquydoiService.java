package org.AdmissionsSystem.bus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.AdmissionsSystem.dao.XtBangquydoiDao;
import org.AdmissionsSystem.models.XtBangquydoi;

public class XtBangquydoiService {

    private final XtBangquydoiDao dao = new XtBangquydoiDao();

    private static final String[] IMPORT_COLUMNS = {
        "phuongthuc",
        "tohop",
        "mon",
        "diema",
        "diemb",
        "diemc",
        "diemd",
        "maquydoi",
        "phanvi"
    };

    private static final java.util.Map<String, String> IMPORT_ALIASES = buildImportAliases();

    /**
     * Lấy tất cả bảng quy đổi
     * @return Danh sách tất cả bảng quy đổi
     */
    public List<XtBangquydoi> layTatCa() {
        return dao.layTatCaBangQuydoi();
    }

    /**
     * Tìm kiếm bảng quy đổi theo từ khóa
     * @param keyword Từ khóa tìm kiếm (phương thức, tổ hợp, mã quy đổi, v.v.)
     * @return Danh sách bảng quy đổi khớp với từ khóa
     */
    public List<XtBangquydoi> timKiem(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        if (q.isEmpty()) {
            return layTatCa();
        }

        List<XtBangquydoi> filtered = new ArrayList<>();
        for (XtBangquydoi model : layTatCa()) {
            if (khopKeyword(model, q)) {
                filtered.add(model);
            }
        }
        return filtered;
    }

    /**
     * Kiểm tra xem bảng quy đổi có khớp với từ khóa tìm kiếm hay không
     * @param model Bảng quy đổi cần kiểm tra
     * @param keyword Từ khóa tìm kiếm
     * @return true nếu khớp, false nếu không khớp
     */
    private boolean khopKeyword(XtBangquydoi model, String keyword) {
        String phuongthuc = layChuoi(model.getDPhuongthuc()).toLowerCase(Locale.ROOT);
        String tohop = layChuoi(model.getDTohop()).toLowerCase(Locale.ROOT);
        String mon = layChuoi(model.getDMon()).toLowerCase(Locale.ROOT);
        String maquydoi = layChuoi(model.getDMaquydoi()).toLowerCase(Locale.ROOT);
        String phanvi = layChuoi(model.getDPhanvi()).toLowerCase(Locale.ROOT);

        return phuongthuc.contains(keyword) || tohop.contains(keyword) || mon.contains(keyword) 
               || maquydoi.contains(keyword) || phanvi.contains(keyword);
    }

    /**
     * Chuyển đổi đối tượng thành chuỗi, trim để loại bỏ khoảng trắng
     * @param obj đối tượng cần chuyển đổi
     * @return chuỗi kết quả sau khi trim
     */
    private String layChuoi(Object obj) {
        return obj != null ? obj.toString().trim() : "";
    }

    /**
     * Thêm mới bảng quy đổi
     * @param model bảng quy đổi cần thêm
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public void them(XtBangquydoi model) {
        kiemTraThongTinBatBuoc(model);
        
        String maquydoi = layChuoi(model.getDMaquydoi());
        if (dao.timTheoMaQuydoi(maquydoi) != null) {
            throw new IllegalArgumentException("Mã quy đổi đã tồn tại.");
        }

        XtBangquydoi entity = saochepModel(model, new XtBangquydoi());
        entity.setIdqd(dao.layIdTiepTheo());
        dao.themBangQuydoi(entity);
    }

    /**
     * Cập nhật bảng quy đổi theo ID
     * @param idqdCu ID bảng quy đổi cũ
     * @param model bảng quy đổi với thông tin cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ hoặc không tìm thấy bảng cần cập nhật
     */
    public void sua(Integer idqdCu, XtBangquydoi model) {
        kiemTraThongTinBatBuoc(model);

        XtBangquydoi existing = dao.timTheoId(idqdCu);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bảng quy đổi cần cập nhật.");
        }

        String maquydoiMoi = layChuoi(model.getDMaquydoi());
        XtBangquydoi duplicate = dao.timTheoMaQuydoi(maquydoiMoi);
        if (duplicate != null && !duplicate.getIdqd().equals(existing.getIdqd())) {
            throw new IllegalArgumentException("Mã quy đổi mới đã tồn tại.");
        }

        saochepModel(model, existing);
        dao.capNhatBangQuydoi(existing);
    }

    /**
     * Kiểm tra thông tin bắt buộc của bảng quy đổi
     * @param model bảng quy đổi cần kiểm tra
     * @throws IllegalArgumentException nếu có thông tin bắt buộc bị thiếu
     */
    private void kiemTraThongTinBatBuoc(XtBangquydoi model) {
        if (model == null) {
            throw new IllegalArgumentException("Bảng quy đổi không được null.");
        }

        String phuongthuc = layChuoi(model.getDPhuongthuc());
        String tohop = layChuoi(model.getDTohop());
        String mon = layChuoi(model.getDMon());
        String maquydoi = layChuoi(model.getDMaquydoi());

        if (phuongthuc.isEmpty()) {
            throw new IllegalArgumentException("Phương thức xét tuyển không được rỗng.");
        }

        if (tohop.isEmpty()) {
            throw new IllegalArgumentException("Tổ hợp môn không được rỗng.");
        }

        if (mon.isEmpty()) {
            throw new IllegalArgumentException("Môn học không được rỗng.");
        }

        if (maquydoi.isEmpty()) {
            throw new IllegalArgumentException("Mã quy đổi không được rỗng.");
        }
    }

    /**
     * Sao chép dữ liệu từ model này sang model khác
     * @param source model nguồn
     * @param target model đích
     * @return model đích sau khi đã sao chép dữ liệu
     */
    private XtBangquydoi saochepModel(XtBangquydoi source, XtBangquydoi target) {
        if (source.getDPhuongthuc() != null) target.setDPhuongthuc(source.getDPhuongthuc());
        if (source.getDTohop() != null) target.setDTohop(source.getDTohop());
        if (source.getDMon() != null) target.setDMon(source.getDMon());
        if (source.getDDiema() != null) target.setDDiema(source.getDDiema());
        if (source.getDDiemb() != null) target.setDDiemb(source.getDDiemb());
        if (source.getDDiemc() != null) target.setDDiemc(source.getDDiemc());
        if (source.getDDiemd() != null) target.setDDiemd(source.getDDiemd());
        if (source.getDMaquydoi() != null) target.setDMaquydoi(source.getDMaquydoi());
        if (source.getDPhanvi() != null) target.setDPhanvi(source.getDPhanvi());
        return target;
    }

    /**
     * Xóa bảng quy đổi theo ID
     * @param idqd ID bảng quy đổi cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy bảng cần xóa
     */
    public void xoa(Integer idqd) {
        XtBangquydoi existing = dao.timTheoId(idqd);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bảng quy đổi cần xóa.");
        }

        dao.xoaBangQuydoi(existing.getIdqd());
    }

    /**
     * Lấy bảng quy đổi theo ID
     * @param idqd ID của bảng quy đổi
     * @return bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi layTheoId(Integer idqd) {
        return dao.timTheoId(idqd);
    }

    /**
     * Lấy danh sách bảng quy đổi theo phương thức
     * @param dPhuongthuc phương thức xét tuyển
     * @return danh sách bảng quy đổi của phương thức
     */
    public List<XtBangquydoi> layTheoPhương(String dPhuongthuc) {
        return dao.timTheoPhương(dPhuongthuc);
    }

    /**
     * Lấy danh sách bảng quy đổi theo tổ hợp
     * @param dTohop tổ hợp môn
     * @return danh sách bảng quy đổi của tổ hợp
     */
    public List<XtBangquydoi> layTheoTohop(String dTohop) {
        return dao.timTheoTohop(dTohop);
    }

    /**
     * Lấy bảng quy đổi theo mã
     * @param dMaquydoi mã quy đổi
     * @return bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi layTheoMaQuydoi(String dMaquydoi) {
        return dao.timTheoMaQuydoi(dMaquydoi);
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo ID
     * @param idqd ID bảng quy đổi
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idqd) {
        return dao.kiemTraTonTai(idqd);
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo mã
     * @param dMaquydoi mã quy đổi
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTaiTheoMa(String dMaquydoi) {
        return dao.kiemTraTonTaiTheoMa(dMaquydoi);
    }

    /**
     * Lấy tổng số bảng quy đổi
     * @return tổng số bảng quy đổi
     */
    public long demTatCa() {
        return dao.demTatCa();
    }

    public ImportPreview previewImport(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return new ImportPreview(List.of(), List.of());
        }

        List<QuyDoiInput> validRows = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        java.util.Set<String> seenMa = new java.util.HashSet<>();

        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            int rowNumber = i + 2;

            String phuongThuc = asText(rowValue(row, 0));
            String toHop = asText(rowValue(row, 1));
            String mon = asText(rowValue(row, 2));
            String maQuyDoi = asText(rowValue(row, 7));
            String phanVi = asText(rowValue(row, 8));

            String diemARaw = asText(rowValue(row, 3));
            String diemBRaw = asText(rowValue(row, 4));
            String diemCRaw = asText(rowValue(row, 5));
            String diemDRaw = asText(rowValue(row, 6));

            java.math.BigDecimal diemA = parseDecimal(diemARaw);
            java.math.BigDecimal diemB = parseDecimal(diemBRaw);
            java.math.BigDecimal diemC = parseDecimal(diemCRaw);
            java.math.BigDecimal diemD = parseDecimal(diemDRaw);

            List<String> messages = new ArrayList<>();
            if (isBlank(phuongThuc)) {
                messages.add("Phương thức không được để trống");
            }
            if (isBlank(toHop)) {
                messages.add("Tổ hợp không được để trống");
            }
            if (isBlank(mon)) {
                messages.add("Môn không được để trống");
            }
            if (isBlank(maQuyDoi)) {
                messages.add("Mã quy đổi không được để trống");
            }

            if (!isBlank(diemARaw) && diemA == null) {
                messages.add("Điểm A không hợp lệ");
            }
            if (!isBlank(diemBRaw) && diemB == null) {
                messages.add("Điểm B không hợp lệ");
            }
            if (!isBlank(diemCRaw) && diemC == null) {
                messages.add("Điểm C không hợp lệ");
            }
            if (!isBlank(diemDRaw) && diemD == null) {
                messages.add("Điểm D không hợp lệ");
            }

            if ((diemA == null) != (diemB == null)) {
                messages.add("Khoảng điểm trước quy đổi phải có đủ điểm A và B");
            }
            if ((diemC == null) != (diemD == null)) {
                messages.add("Khoảng điểm sau quy đổi phải có đủ điểm C và D");
            }

            String normalizedMa = normalizeKey(maQuyDoi);
            if (!isBlank(normalizedMa)) {
                if (seenMa.contains(normalizedMa)) {
                    messages.add("Mã quy đổi bị trùng trong file import");
                } else {
                    seenMa.add(normalizedMa);
                }
            }

            if (!isBlank(maQuyDoi) && dao.timTheoMaQuydoi(maQuyDoi) != null) {
                messages.add("Mã quy đổi đã tồn tại trong hệ thống");
            }

            if (messages.isEmpty()) {
                validRows.add(new QuyDoiInput(
                    safeText(phuongThuc),
                    safeText(toHop),
                    safeText(mon),
                    diemA,
                    diemB,
                    diemC,
                    diemD,
                    safeText(maQuyDoi),
                    safeText(phanVi)
                ));
            } else {
                errors.add(new ImportError(rowNumber, maQuyDoi, String.join("; ", messages)));
            }
        }

        return new ImportPreview(validRows, errors);
    }

    public int importRows(List<QuyDoiInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return 0;
        }
        int imported = 0;
        for (QuyDoiInput input : inputs) {
            XtBangquydoi entity = new XtBangquydoi();
            entity.setDPhuongthuc(input.phuongThuc());
            entity.setDTohop(input.toHop());
            entity.setDMon(input.mon());
            entity.setDDiema(input.diemA());
            entity.setDDiemb(input.diemB());
            entity.setDDiemc(input.diemC());
            entity.setDDiemd(input.diemD());
            entity.setDMaquydoi(input.maQuyDoi());
            entity.setDPhanvi(input.phanVi());
            them(entity);
            imported++;
        }
        return imported;
    }

    public String[] getImportColumns() {
        return IMPORT_COLUMNS.clone();
    }

    public java.util.Map<String, String> getImportAliases() {
        return IMPORT_ALIASES;
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
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private java.math.BigDecimal parseDecimal(String value) {
        if (isBlank(value)) {
            return null;
        }
        String normalized = value.replace(",", "").trim();
        try {
            return new java.math.BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static java.util.Map<String, String> buildImportAliases() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("phuong thuc", "phuongthuc");
        map.put("phuongthuc", "phuongthuc");
        map.put("to hop", "tohop");
        map.put("tohop", "tohop");
        map.put("mon", "mon");
        map.put("diem a", "diema");
        map.put("diem b", "diemb");
        map.put("diem c", "diemc");
        map.put("diem d", "diemd");
        map.put("ma quy doi", "maquydoi");
        map.put("maquydoi", "maquydoi");
        map.put("phan vi", "phanvi");
        return java.util.Collections.unmodifiableMap(map);
    }

    public record QuyDoiInput(
        String phuongThuc,
        String toHop,
        String mon,
        java.math.BigDecimal diemA,
        java.math.BigDecimal diemB,
        java.math.BigDecimal diemC,
        java.math.BigDecimal diemD,
        String maQuyDoi,
        String phanVi) {
    }

    public record ImportError(
        int rowNumber,
        String maQuyDoi,
        String message) {
    }

    public record ImportPreview(
        List<QuyDoiInput> validRows,
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
