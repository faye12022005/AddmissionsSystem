package org.AdmissionsSystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.AdmissionsSystem.models.XtBangquydoi;
import org.hibernate.Session;

/**
 * Tiện ích quy đổi điểm V-SAT và ĐGNL dựa trên bảng quy đổi bách phân vị.
 *
 * <p>
 * Entity {@link XtBangquydoi} đang map bảng quy đổi trong database. Mỗi dòng
 * quy đổi có khoảng điểm đầu vào [d_diema, d_diemb] và khoảng điểm sau quy đổi
 * [d_diemc, d_diemd].
 * </p>
 */
public final class QuyDoiDiemUtil {

    private static final int RESULT_SCALE = 3;
    private static final String DGNL = "dgnl";
    private static final String VSAT = "vsat";

    private QuyDoiDiemUtil() {
    }

    /**
     * Quy đổi điểm ĐGNL sang thang 30 theo tổ hợp.
     *
     * @param diem điểm ĐGNL gốc của thí sinh
     * @param toHop mã tổ hợp cần tra trong bảng quy đổi, ví dụ "A01"
     * @return điểm ĐGNL đã quy đổi sang thang 30, làm tròn 3 chữ số thập phân
     */
    public static float quyDoiDgnl(float diem, String toHop) {
        BigDecimal score = toBigDecimal(diem, "Điểm ĐGNL");
        XtBangquydoi rule = findRule(score, DGNL, requireText(toHop, "Tổ hợp"), null);
        return interpolate(score, rule).floatValue();
    }

    /**
     * Quy đổi điểm một môn V-SAT sang thang 10 theo mã môn.
     *
     * @param diem điểm V-SAT gốc của môn thi
     * @param tenMon mã môn cần tra trong bảng quy đổi, ví dụ "VA"
     * @return điểm môn V-SAT đã quy đổi sang thang 10, làm tròn 3 chữ số thập phân
     */
    public static float quyDoiVsat(float diem, String tenMon) {
        BigDecimal score = toBigDecimal(diem, "Điểm VSAT");
        XtBangquydoi rule = findRule(score, VSAT, null, requireText(tenMon, "Tên môn"));
        return interpolate(score, rule).floatValue();
    }

    private static XtBangquydoi findRule(BigDecimal score, String phuongThuc, String toHop, String mon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lọc trước theo khoảng điểm để giảm số dòng cần so khớp trong Java.
            List<XtBangquydoi> candidates = session.createQuery(
                    "FROM XtBangquydoi WHERE dDiema <= :score AND dDiemb >= :score", XtBangquydoi.class)
                    .setParameter("score", score)
                    .list();

            // Sau đó khớp phương thức, tổ hợp hoặc môn. Phương thức/tổ hợp vẫn
            // chuẩn hóa để tránh lệch dữ liệu kiểu "ĐGNL" với "DGNL"; riêng mã
            // môn VSAT là mã 2 ký tự như "VA", nên chỉ cần trim và ignore-case.
            return candidates.stream()
                    .filter(rule -> matches(rule.getDPhuongthuc(), phuongThuc))
                    .filter(rule -> toHop == null || matches(rule.getDTohop(), toHop))
                    .filter(rule -> mon == null || matchesSubjectCode(rule.getDMon(), mon))
                    .min(Comparator.comparing(QuyDoiDiemUtil::phanViAsInt)
                            .thenComparing(rule -> rule.getIdqd() == null ? Integer.MAX_VALUE : rule.getIdqd()))
                    .orElseThrow(() -> new IllegalArgumentException(buildNotFoundMessage(score, phuongThuc, toHop, mon)));
        }
    }

    private static BigDecimal interpolate(BigDecimal score, XtBangquydoi rule) {
        BigDecimal a = requireNumber(rule.getDDiema(), "d_diema");
        BigDecimal b = requireNumber(rule.getDDiemb(), "d_diemb");
        BigDecimal c = requireNumber(rule.getDDiemc(), "d_diemc");
        BigDecimal d = requireNumber(rule.getDDiemd(), "d_diemd");

        BigDecimal inputRange = b.subtract(a);
        if (inputRange.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Khoảng điểm quy đổi không hợp lệ: d_diema bằng d_diemb.");
        }

        BigDecimal outputRange = d.subtract(c);
        // Công thức quy đổi trong file:
        // y = c + (x - a) * (d - c) / (b - a)
        // Với x là điểm gốc, y là điểm sau quy đổi.
        return score.subtract(a)
                .multiply(outputRange)
                .divide(inputRange, RESULT_SCALE + 4, RoundingMode.HALF_UP)
                .add(c)
                .setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal toBigDecimal(float value, String label) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(label + " không hợp lệ.");
        }
        return new BigDecimal(Float.toString(value));
    }

    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " không được để trống.");
        }
        return value.trim();
    }

    private static BigDecimal requireNumber(BigDecimal value, String column) {
        if (value == null) {
            throw new IllegalArgumentException("Thiếu dữ liệu cột " + column + " trong bảng quy đổi.");
        }
        return value;
    }

    private static boolean matches(String actual, String expected) {
        return normalize(actual).equals(normalize(expected));
    }

    private static boolean matchesSubjectCode(String actual, String expected) {
        return actual != null && expected != null && actual.trim().equalsIgnoreCase(expected.trim());
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        // Chuẩn hóa text để so khớp ổn định giữa dữ liệu có dấu/không dấu.
        String normalized = Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^a-z0-9]", "");
    }

    private static int phanViAsInt(XtBangquydoi rule) {
        try {
            return rule.getDPhanvi() == null ? Integer.MAX_VALUE : Integer.parseInt(rule.getDPhanvi().trim());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private static String buildNotFoundMessage(BigDecimal score, String phuongThuc, String toHop, String mon) {
        String target = toHop != null ? "tổ hợp " + toHop : "môn " + mon;
        return "Không tìm thấy dòng quy đổi " + phuongThuc.toUpperCase(Locale.ROOT)
                + " cho " + target + " với điểm " + score + ".";
    }
}
