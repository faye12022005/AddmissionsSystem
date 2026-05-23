package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.AdmissionsSystem.dao.DiemThiDao;
import org.AdmissionsSystem.models.XtDiemthixettuyen;

public class DiemThiService {

    private final DiemThiDao dao = new DiemThiDao();
    private Map<String, XtDiemthixettuyen> cacheByCccd = new HashMap<>();

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
        // Luôn luôn để d_phuongthuc là null, không lưu phương thức
        entity.setDPhuongthuc(null);
        dao.save(entity);
    }

    public void update(XtDiemthixettuyen entity) {
        // Luôn luôn để d_phuongthuc là null, không lưu phương thức
        entity.setDPhuongthuc(null);
        dao.update(entity);
    }

    public void delete(int id) {
        XtDiemthixettuyen entity = new XtDiemthixettuyen();
        entity.setIddiemthi(id);
        dao.delete(entity);
    }

    public void upsertByCccd(XtDiemthixettuyen entity) {
        XtDiemthixettuyen existing = dao.findByCccd(entity.getCccd());
        if (existing == null) {
            if (entity.getIddiemthi() == null || entity.getIddiemthi() == 0) {
                entity.setIddiemthi(dao.getNextId());
            }
            dao.save(entity);
        } else {
            entity.setIddiemthi(existing.getIddiemthi());
            // Luôn luôn để d_phuongthuc là null, không lưu phương thức
            entity.setDPhuongthuc(null);
            dao.update(entity);
        }
    }

    public long count() {
        return dao.count();
    }

    /**
     * Tải toàn bộ dữ liệu vào cache (nên gọi một lần khi khởi động)
     */
    public void loadCache() {
        cacheByCccd.clear();
        for (XtDiemthixettuyen dt : getAll()) {
            if (dt.getCccd() != null && !dt.getCccd().trim().isEmpty()) {
                cacheByCccd.put(normalizeKey(dt.getCccd()), dt);
            }
        }
    }

    /**
     * Lấy điểm thi THPT theo CCCD và mã môn (dạng "TO", "LI", ...)
     * @return BigDecimal điểm (thang 10), null nếu không có
     */
    public BigDecimal getDiemThpt(String cccd, String monCode) {
        XtDiemthixettuyen dt = cacheByCccd.get(normalizeKey(cccd));
        if (dt == null) return null;
        String code = normalizeSubject(monCode);
        switch (code) {
            case "to": return dt.getTo();
            case "li": return dt.getLi();
            case "ho": return dt.getHo();
            case "si": return dt.getSi();
            case "su": return dt.getSu();
            case "di": return dt.getDi();
            case "va": return dt.getVa();
            case "ti": return dt.getTi();
            case "gdcd": return dt.getGdcd();
            case "ktpl": return dt.getKtpl();
            case "cncn": return dt.getCncn();
            case "cnnn": return dt.getCnnn();
            // case "th": return dt.getTh(); // chưa có cột, comment lại
            default: return null;
        }
    }

    /**
     * Lấy điểm ĐGNL (thang 1200) gốc
     */
    public BigDecimal getDiemDgnlRaw(String cccd) {
        XtDiemthixettuyen dt = cacheByCccd.get(normalizeKey(cccd));
        return dt != null ? dt.getNl1() : null;
    }

    /**
     * Trả về danh sách các môn hợp lệ cho phương thức THPT (dùng cho "môn còn lại")
     */
    public List<String> getListMonThpt() {
        return Arrays.asList("TO", "LI", "HO", "SI", "SU", "DI", "VA", "TI", "KTPL", "GDCD", "CNCN", "CNNN");
    }

    /**
     * Kiểm tra thí sinh có đủ điểm cho một tổ hợp cụ thể (các môn không null)
     * @param cccd
     * @param monCodes danh sách 3 mã môn
     * @return true nếu cả 3 môn đều có điểm
     */
    public boolean hasEnoughPoints(String cccd, List<String> monCodes) {
        for (String mon : monCodes) {
            if (getDiemThpt(cccd, mon) == null) return false;
        }
        return true;
    }

    /**
     * Tính điểm tổ hợp (chưa nhân hệ số, trả về tổng có trọng số)
     * Công thức: (d1*w1 + d2*w2 + d3*w3) / (w1+w2+w3) * 3
     */
    public BigDecimal tinhDiemToHop(String cccd, List<String> monCodes, List<Integer> heSo) {
        if (monCodes.size() != 3 || heSo.size() != 3) return BigDecimal.ZERO;
        BigDecimal tong = BigDecimal.ZERO;
        int tongHeSo = 0;
        for (int i = 0; i < 3; i++) {
            BigDecimal diem = getDiemThpt(cccd, monCodes.get(i));
            if (diem == null) return BigDecimal.ZERO;
            int hs = heSo.get(i);
            tong = tong.add(diem.multiply(BigDecimal.valueOf(hs)));
            tongHeSo += hs;
        }
        if (tongHeSo == 0) return BigDecimal.ZERO;
        BigDecimal trungBinh = tong.divide(BigDecimal.valueOf(tongHeSo), 4, RoundingMode.HALF_UP);
        return trungBinh.multiply(BigDecimal.valueOf(3)).setScale(3, RoundingMode.HALF_UP);
    }

    // Hàm hỗ trợ chuẩn hóa
    private String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizeSubject(String mon) {
        if (mon == null) return "";
        String m = mon.trim().toUpperCase();
        switch (m) {
            case "TOAN": return "TO";
            case "LY": return "LI";
            case "HOA": return "HO";
            case "SINH": return "SI";
            case "SU": return "SU";
            case "DIA": return "DI";
            case "VAN": return "VA";
            case "NGUVAN": return "VA";
            case "TIENGANH": return "TI";
            case "KTPL": return "KTPL";
            case "GDCD": return "GDCD";
            case "CONGNGHECONGNGHIEP": return "CNCN";
            case "CONGNGHENONGNGHIEP": return "CNNN";
            // case "TINHOC": return "TH";
            default: return m;
        }
    }

}
