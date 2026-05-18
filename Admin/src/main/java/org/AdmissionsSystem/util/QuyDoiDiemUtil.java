package org.AdmissionsSystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import org.AdmissionsSystem.models.XtBangquydoi;

public final class QuyDoiDiemUtil {

    private static final int SCALE = 3;
    private static final Map<String, List<BangQuyDoiEntry>> VSAT_CACHE = new HashMap<>();
    private static final Map<String, List<BangQuyDoiEntry>> DGNL_CACHE = new HashMap<>();

    private QuyDoiDiemUtil() {
    }

    /**
     * Khởi tạo cache từ dữ liệu bảng quy đổi. Gọi một lần khi ứng dụng khởi động.
     */
    public static void init(List<XtBangquydoi> allRows) {
        VSAT_CACHE.clear();
        DGNL_CACHE.clear();
        for (XtBangquydoi row : allRows) {
            String phuongThuc = row.getDPhuongthuc();
            if ("VSAT".equalsIgnoreCase(phuongThuc)) {
                String mon = row.getDMon();
                String key = mon == null ? null : mon.trim().toUpperCase(Locale.ROOT);
                if (key == null || key.isEmpty()) {
                    continue;
                }
                VSAT_CACHE.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new BangQuyDoiEntry(row));
            } else if ("DGNL".equalsIgnoreCase(phuongThuc)) {
                String toHop = row.getDTohop();
                String key = toHop == null ? null : toHop.trim().toUpperCase(Locale.ROOT);
                if (key == null || key.isEmpty()) {
                    continue;
                }
                DGNL_CACHE.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new BangQuyDoiEntry(row));
            }
        }
        // Sắp xếp theo a tăng dần để dễ tìm kiếm
        for (List<BangQuyDoiEntry> list : VSAT_CACHE.values()) {
            list.sort(Comparator.comparing(e -> e.a));
        }
        for (List<BangQuyDoiEntry> list : DGNL_CACHE.values()) {
            list.sort(Comparator.comparing(e -> e.a));
        }
    }

    /**
     * Quy đổi điểm V-SAT (thang 450) sang thang 10.
     * 
     * @param diem điểm gốc (float)
     * @param mon  mã môn (TO, VA, LI, HO, SI, SU, DI, N1)
     * @return điểm thang 10 (BigDecimal)
     */
    public static BigDecimal quyDoiVsat(float diem, String mon) {
        String key = mon.toUpperCase();
        List<BangQuyDoiEntry> entries = VSAT_CACHE.get(key);
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("Không có dữ liệu quy đổi cho môn " + mon);
        }
        BigDecimal x = BigDecimal.valueOf(diem);
        BangQuyDoiEntry rule = findEntry(entries, x);
        return interpolate(x, rule);
    }

    /**
     * Quy đổi điểm ĐGNL (thang 1200) sang thang 30.
     * 
     * @param diem  điểm gốc
     * @param toHop mã tổ hợp (A00, A01, B00, C00, C01, D01...)
     * @return điểm thang 30
     */
    public static BigDecimal quyDoiDgnl(float diem, String toHop) {
        String key = toHop.toUpperCase();
        List<BangQuyDoiEntry> entries = DGNL_CACHE.get(key);
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("Không có dữ liệu quy đổi cho tổ hợp " + toHop);
        }
        BigDecimal x = BigDecimal.valueOf(diem);
        BangQuyDoiEntry rule = findEntry(entries, x);
        return interpolate(x, rule);
    }

    // Tìm khoảng chứa x, nếu nằm ngoài thì lấy khoảng đầu hoặc cuối
    private static BangQuyDoiEntry findEntry(List<BangQuyDoiEntry> entries, BigDecimal x) {
        // Nếu x < a của khoảng đầu tiên
        BangQuyDoiEntry first = entries.get(0);
        if (x.compareTo(first.a) <= 0) {
            return first;
        }
        // Nếu x > b của khoảng cuối cùng
        BangQuyDoiEntry last = entries.get(entries.size() - 1);
        if (x.compareTo(last.b) >= 0) {
            return last;
        }
        // Tìm khoảng phù hợp
        for (BangQuyDoiEntry e : entries) {
            if (x.compareTo(e.a) > 0 && x.compareTo(e.b) <= 0) {
                return e;
            }
        }
        // Fallback: khoảng gần nhất (theo a)
        return entries.stream()
                .min(Comparator.comparing(e -> e.a.subtract(x).abs()))
                .orElse(first);
    }

    private static BigDecimal interpolate(BigDecimal x, BangQuyDoiEntry rule) {
        // y = c + (x - a) * (d - c) / (b - a)
        BigDecimal range = rule.b.subtract(rule.a);
        if (range.compareTo(BigDecimal.ZERO) == 0) {
            return rule.c;
        }
        BigDecimal ratio = x.subtract(rule.a)
                .multiply(rule.d.subtract(rule.c))
                .divide(range, SCALE + 4, RoundingMode.HALF_UP);
        return rule.c.add(ratio).setScale(SCALE, RoundingMode.HALF_UP);
    }

    // Lớp nội bộ lưu thông tin khoảng quy đổi
    private static class BangQuyDoiEntry {
        final BigDecimal a, b, c, d;

        BangQuyDoiEntry(XtBangquydoi row) {
            this.a = row.getDDiema();
            this.b = row.getDDiemb();
            this.c = row.getDDiemc();
            this.d = row.getDDiemd();
        }
    }
}