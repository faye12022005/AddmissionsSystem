package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.util.*;
import org.AdmissionsSystem.dao.DiemVsatDao;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.AdmissionsSystem.util.QuyDoiDiemUtil;

public class DiemVsatService {

    private final DiemVsatDao dao = new DiemVsatDao();
    private final Map<String, XtDiemVsat> bestScoreCache = new HashMap<>();

    // ==================== Các hàm cơ bản ====================
    public List<XtDiemVsat> getAll() {
        return dao.findAll(); // giả sử AbstractCrudDao có findAll
    }

    public XtDiemVsat findById(int id) {
        return dao.findById(id);
    }

    public List<XtDiemVsat> findByCccd(String cccd) {
        return dao.findByCccd(cccd);
    }

    public List<XtDiemVsat> findByDotThi(String dotThi) {
        return dao.findByDotThi(dotThi);
    }

    public XtDiemVsat findByCccdAndDotThi(String cccd, String dotThi) {
        return dao.findByCccdAndDotThi(cccd, dotThi);
    }

    public void add(XtDiemVsat entity) {
        if (entity == null || entity.getCccd() == null || entity.getDotThi() == null) {
            throw new IllegalArgumentException("Dữ liệu điểm V-SAT không hợp lệ.");
        }
        dao.save(entity);
    }

    public void update(XtDiemVsat entity) {
        if (entity == null || entity.getIdVsat() == null) {
            throw new IllegalArgumentException("Dữ liệu điểm V-SAT không hợp lệ.");
        }
        XtDiemVsat existing = dao.findById(entity.getIdVsat());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi V-SAT cần cập nhật.");
        }
        dao.update(entity);
    }

    public void delete(int id) {
        XtDiemVsat existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi V-SAT cần xóa.");
        }
        dao.delete(existing);
    }

    public void upsertByCccdAndDotThi(XtDiemVsat entity) {
        if (entity == null || entity.getCccd() == null || entity.getDotThi() == null) {
            throw new IllegalArgumentException("Dữ liệu điểm V-SAT không hợp lệ.");
        }
        XtDiemVsat existing = dao.findByCccdAndDotThi(entity.getCccd(), entity.getDotThi());
        if (existing == null) {
            dao.save(entity);
        } else {
            entity.setIdVsat(existing.getIdVsat());
            dao.update(entity);
        }
    }

    public long count() {
        return dao.count();
    }

    // ==================== Hàm chuyên biệt cho xét tuyển ====================

    /**
     * Lấy điểm V-SAT tốt nhất cho từng môn (có cache).
     */
    public XtDiemVsat getBestScoresByCccd(String cccd) {
        if (bestScoreCache.containsKey(cccd)) {
            return bestScoreCache.get(cccd);
        }
        List<XtDiemVsat> all = dao.findByCccd(cccd);
        if (all.isEmpty()) {
            bestScoreCache.put(cccd, null);
            return null;
        }
        XtDiemVsat best = new XtDiemVsat();
        best.setCccd(cccd);
        best.setDotThi("TỔNG_HỢP");
        for (XtDiemVsat row : all) {
            best.setToanVsat(max(best.getToanVsat(), row.getToanVsat()));
            best.setVanVsat(max(best.getVanVsat(), row.getVanVsat()));
            best.setAnhVsat(max(best.getAnhVsat(), row.getAnhVsat()));
            best.setLyVsat(max(best.getLyVsat(), row.getLyVsat()));
            best.setHoaVsat(max(best.getHoaVsat(), row.getHoaVsat()));
            best.setSinhVsat(max(best.getSinhVsat(), row.getSinhVsat()));
            best.setSuVsat(max(best.getSuVsat(), row.getSuVsat()));
            best.setDiaVsat(max(best.getDiaVsat(), row.getDiaVsat()));
        }
        bestScoreCache.put(cccd, best);
        return best;
    }

    /**
     * Lấy điểm V-SAT đã quy đổi sang thang 10.
     * @param cccd   CCCD thí sinh
     * @param mon    Mã môn đầu vào: "TOAN", "VAN", "ANH", "LY", "HOA", "SINH", "SU", "DIA"
     * @return điểm thang 10, hoặc null nếu không có điểm hoặc quy đổi thất bại
     */
    public BigDecimal getQuyDoiDiem(String cccd, String mon) {
        XtDiemVsat best = getBestScoresByCccd(cccd);
        if (best == null) return null;
        BigDecimal raw = getRawScore(best, mon);
        if (raw == null || raw.compareTo(BigDecimal.ZERO) <= 0) return null;
        // Chuẩn hóa mã môn theo yêu cầu của QuyDoiDiemUtil
        String monCode = mapMonToCode(mon);
        try {
            BigDecimal converted = QuyDoiDiemUtil.quyDoiVsat(raw.floatValue(), monCode);
            return converted;
        } catch (IllegalArgumentException e) {
            // Log lỗi nếu cần
            return null;
        }
    }

    private String mapMonToCode(String mon) {
        if (mon == null) return null;
        switch (mon.trim().toUpperCase()) {
            case "TOAN": return "TO";
            case "VAN": return "VA";
            case "ANH": return "N1";
            case "LY": return "LI";
            case "HOA": return "HO";
            case "SINH": return "SI";
            case "SU": return "SU";
            case "DIA": return "DI";
            default: return mon;
        }
    }

    /**
     * Kiểm tra thí sinh có điểm V-SAT (bất kỳ môn nào) hay không.
     */
    public boolean hasDiem(String cccd) {
        List<XtDiemVsat> list = dao.findByCccd(cccd);
        if (list.isEmpty()) return false;
        for (XtDiemVsat row : list) {
            if (hasAnyPositiveScore(row)) return true;
        }
        return false;
    }

    /**
     * Lấy danh sách các đợt thi mà thí sinh đã tham gia.
     */
    public List<String> getDotThiByCccd(String cccd) {
        List<XtDiemVsat> list = dao.findByCccd(cccd);
        List<String> dots = new ArrayList<>();
        for (XtDiemVsat row : list) {
            if (row.getDotThi() != null && !row.getDotThi().isBlank()) {
                dots.add(row.getDotThi());
            }
        }
        return dots;
    }

    // ==================== Hàm nội bộ ====================
    private BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) >= 0 ? a : b;
    }

    private BigDecimal getRawScore(XtDiemVsat best, String mon) {
        if (best == null) return null;
        String m = mon.trim().toUpperCase();
        switch (m) {
            case "TOAN": return best.getToanVsat();
            case "VAN":  return best.getVanVsat();
            case "ANH":  return best.getAnhVsat();
            case "LY":   return best.getLyVsat();
            case "HOA":  return best.getHoaVsat();
            case "SINH": return best.getSinhVsat();
            case "SU":   return best.getSuVsat();
            case "DIA":  return best.getDiaVsat();
            default: return null;
        }
    }

    private boolean hasAnyPositiveScore(XtDiemVsat row) {
        if (row == null) return false;
        return isPositive(row.getToanVsat()) ||
               isPositive(row.getVanVsat()) ||
               isPositive(row.getAnhVsat()) ||
               isPositive(row.getLyVsat()) ||
               isPositive(row.getHoaVsat()) ||
               isPositive(row.getSinhVsat()) ||
               isPositive(row.getSuVsat()) ||
               isPositive(row.getDiaVsat());
    }

    private boolean isPositive(BigDecimal val) {
        return val != null && val.compareTo(BigDecimal.ZERO) > 0;
    }

    // Thêm phương thức refresh cache khi có dữ liệu mới
    public void clearCache() {
        bestScoreCache.clear();
    }
}