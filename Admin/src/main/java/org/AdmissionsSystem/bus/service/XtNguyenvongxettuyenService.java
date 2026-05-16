package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.AdmissionsSystem.dao.NganhHocDao;
import org.AdmissionsSystem.dao.NganhToHopDao;
import org.AdmissionsSystem.dao.ThiSinhDao;
import org.AdmissionsSystem.dao.XtNguyenvongxettuyenDao;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;

public class XtNguyenvongxettuyenService {

    private final XtNguyenvongxettuyenDao dao = new XtNguyenvongxettuyenDao();
    private final NganhHocDao nganhDao = new NganhHocDao();
    private final NganhToHopDao nganhToHopDao = new NganhToHopDao();
    private final ThiSinhDao thiSinhDao = new ThiSinhDao();

    /**
     * Lấy tất cả nguyện vọng xét tuyển
     * @return Danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> layTatCa() {
        return dao.layTatCaNguyenVong();
    }

    /**
     * Tìm kiếm nguyện vọng theo từ khóa
     * @param keyword Từ khóa tìm kiếm (CCCD, mã ngành, kết quả, v.v.)
     * @return Danh sách nguyện vọng khớp với từ khóa
     */
    public List<XtNguyenvongxettuyen> timKiem(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        if (q.isEmpty()) {
            return layTatCa();
        }

        List<XtNguyenvongxettuyen> filtered = new ArrayList<>();
        for (XtNguyenvongxettuyen model : layTatCa()) {
            if (khopKeyword(model, q)) {
                filtered.add(model);
            }
        }
        return filtered;
    }

    /**
     * Kiểm tra xem nguyện vọng có khớp với từ khóa tìm kiếm hay không
     * @param model Nguyện vọng cần kiểm tra
     * @param keyword Từ khóa tìm kiếm
     * @return true nếu khớp, false nếu không khớp
     */
    private boolean khopKeyword(XtNguyenvongxettuyen model, String keyword) {
        String cccd = layChuoi(model.getNnCccd()).toLowerCase(Locale.ROOT);
        String manganh = layChuoi(model.getNvManganh()).toLowerCase(Locale.ROOT);
        String ketqua = layChuoi(model.getNvKetqua()).toLowerCase(Locale.ROOT);
        String phuongthuc = layChuoi(model.getTtPhuongthuc()).toLowerCase(Locale.ROOT);

        return cccd.contains(keyword) || manganh.contains(keyword) || ketqua.contains(keyword) || phuongthuc.contains(keyword);
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
     * Thêm mới nguyện vọng xét tuyển
     * @param model nguyện vọng cần thêm
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public void them(XtNguyenvongxettuyen model) {
        kiemTraThongTinBatBuoc(model);
        
        XtNguyenvongxettuyen entity = saochepModel(model, new XtNguyenvongxettuyen());
        entity.setIdnv(dao.layIdTiepTheo());
        dao.themNguyenVong(entity);
    }

    /**
     * Cập nhật nguyện vọng xét tuyển theo ID
     * @param idnv ID nguyện vọng cũ
     * @param model nguyện vọng với thông tin cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ hoặc không tìm thấy nguyện vọng cần cập nhật
     */
    public void sua(Integer idnv, XtNguyenvongxettuyen model) {
        kiemTraThongTinBatBuoc(model);

        XtNguyenvongxettuyen existing = dao.timTheoId(idnv);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần cập nhật.");
        }

        saochepModel(model, existing);
        dao.capNhatNguyenVong(existing);
    }

    /**
     * Kiểm tra thông tin bắt buộc của nguyện vọng
     * @param model nguyện vọng cần kiểm tra
     * @throws IllegalArgumentException nếu có thông tin bắt buộc bị thiếu
     */
    private void kiemTraThongTinBatBuoc(XtNguyenvongxettuyen model) {
        if (model == null) {
            throw new IllegalArgumentException("Nguyện vọng không được null.");
        }

        String cccd = layChuoi(model.getNnCccd());
        String manganh = layChuoi(model.getNvManganh());
        Integer thutua = model.getNvTt();

        if (cccd.isEmpty()) {
            throw new IllegalArgumentException("CCCD thí sinh không được rỗng.");
        }

        if (manganh.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành không được rỗng.");
        }

        if (thutua == null || thutua <= 0) {
            throw new IllegalArgumentException("Thứ tự nguyện vọng phải lớn hơn 0.");
        }
    }

    /**
     * Sao chép dữ liệu từ model này sang model khác
     * @param source model nguồn
     * @param target model đích
     * @return model đích sau khi đã sao chép dữ liệu
     */
    private XtNguyenvongxettuyen saochepModel(XtNguyenvongxettuyen source, XtNguyenvongxettuyen target) {
        if (source.getNnCccd() != null) target.setNnCccd(source.getNnCccd());
        if (source.getNvManganh() != null) target.setNvManganh(source.getNvManganh());
        if (source.getNvTt() != null) target.setNvTt(source.getNvTt());
        if (source.getDiemThxt() != null) target.setDiemThxt(source.getDiemThxt());
        if (source.getDiemUtqd() != null) target.setDiemUtqd(source.getDiemUtqd());
        if (source.getDiemCong() != null) target.setDiemCong(source.getDiemCong());
        if (source.getDiemXettuyen() != null) target.setDiemXettuyen(source.getDiemXettuyen());
        if (source.getNvKetqua() != null) target.setNvKetqua(source.getNvKetqua());
        if (source.getNvKeys() != null) target.setNvKeys(source.getNvKeys());
        if (source.getTtPhuongthuc() != null) target.setTtPhuongthuc(source.getTtPhuongthuc());
        if (source.getTtThm() != null) target.setTtThm(source.getTtThm());
        return target;
    }

    /**
     * Xóa nguyện vọng theo ID
     * @param idnv ID nguyện vọng cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy nguyện vọng cần xóa
     */
    public void xoa(Integer idnv) {
        XtNguyenvongxettuyen existing = dao.timTheoId(idnv);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần xóa.");
        }

        dao.xoaNguyenVong(existing.getIdnv());
    }

    /**
     * Lấy nguyện vọng theo ID
     * @param idnv ID của nguyện vọng
     * @return nguyện vọng nếu tìm thấy, null nếu không
     */
    public XtNguyenvongxettuyen layTheoId(Integer idnv) {
        return dao.timTheoId(idnv);
    }

    /**
     * Lấy danh sách nguyện vọng của thí sinh theo CCCD
     * @param nnCccd CCCD thí sinh
     * @return danh sách nguyện vọng của thí sinh
     */
    public List<XtNguyenvongxettuyen> layTheoCccd(String nnCccd) {
        return dao.timTheoCccd(nnCccd);
    }

    /**
     * Lấy danh sách nguyện vọng theo mã ngành
     * @param nvManganh mã ngành
     * @return danh sách nguyện vọng của ngành
     */
    public List<XtNguyenvongxettuyen> layTheoMaNganh(String nvManganh) {
        return dao.timTheoMaNganh(nvManganh);
    }

    /**
     * Kiểm tra nguyện vọng có tồn tại theo ID
     * @param idnv ID nguyện vọng
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idnv) {
        return dao.kiemTraTonTai(idnv);
    }

    /**
     * Lấy tổng số nguyện vọng
     * @return tổng số nguyện vọng
     */
    public long demTatCa() {
        return dao.demTatCa();
    }

    public int tinhDiemXetTuyenAll() {
        List<XtNguyenvongxettuyen> all = dao.layTatCaNguyenVong();
        if (all.isEmpty()) {
            return 0;
        }

        int updated = 0;
        for (XtNguyenvongxettuyen nv : all) {
            BigDecimal diemThxt = nvlBigDecimal(nv.getDiemThxt());
            BigDecimal diemCong = nvlBigDecimal(nv.getDiemCong());
            BigDecimal diemUtqd = nvlBigDecimal(nv.getDiemUtqd());

            BigDecimal diemXet = diemThxt.add(diemCong).add(diemUtqd);
            if (diemXet.compareTo(new BigDecimal("30")) > 0) {
                diemXet = new BigDecimal("30");
            }

            nv.setDiemXettuyen(diemXet);
            dao.capNhatNguyenVong(nv);
            updated++;
        }
        return updated;
    }

    public XetTuyenResult chayXetTuyenHeThong() {
        // 1) Tải toàn bộ nguyện vọng và chuẩn bị dữ liệu nền (ngành, thí sinh).
        List<XtNguyenvongxettuyen> allNguyenVong = dao.layTatCaNguyenVong();
        if (allNguyenVong.isEmpty()) {
            return new XetTuyenResult(0, 0, 0, 0, 0);
        }

        Map<String, XtNganh> nganhByKey = new HashMap<>();
        for (XtNganh nganh : nganhDao.findAll()) {
            String key = normalizeKey(nganh.getManganh());
            if (!key.isEmpty()) {
                nganhByKey.put(key, nganh);
            }
        }

        Map<String, BigDecimal> dolechTheoNganhVaTohop = napBangDolechTuDb();

        Map<String, Boolean> daTrungTuyen = new HashMap<>();
        for (XtThisinhxettuyen25 ts : thiSinhDao.findAll()) {
            String cccd = safeText(ts.getCccd());
            if (!cccd.isEmpty()) {
                daTrungTuyen.put(cccd, false);
            }
        }

        int total = allNguyenVong.size();
        int rejectedByDiemSan = 0;
        int rejectedByMissingNganh = 0;

        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            nv.setNvKetqua(null);
        }

        List<XtNguyenvongxettuyen> eligible = new ArrayList<>();
        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            // 2) Lọc bỏ các nguyện vọng không đạt điểm sàn hoặc thiếu ngành.
            String maNganh = safeText(nv.getNvManganh());
            String key = normalizeKey(maNganh);
            XtNganh nganh = nganhByKey.get(key);

            if (nganh == null) {
                nv.setNvKetqua("Rớt - không tìm thấy ngành");
                rejectedByMissingNganh++;
                continue;
            }

            BigDecimal diemSan = nvlBigDecimal(nganh.getNDiemsan());
            BigDecimal diemXet = resolveDiemXetTuyen(nv, nganhByKey, dolechTheoNganhVaTohop);
            if (diemXet.compareTo(diemSan) < 0) {
                nv.setNvKetqua("Rớt - không đạt điểm sàn");
                rejectedByDiemSan++;
                continue;
            }

            eligible.add(nv);
            String cccd = safeText(nv.getNnCccd());
            if (!cccd.isEmpty()) {
                daTrungTuyen.putIfAbsent(cccd, false);
            }
        }

        // 3) Gom nguyện vọng theo thí sinh và sắp xếp theo thứ tự ưu tiên.
        Map<String, List<XtNguyenvongxettuyen>> nvTheoThiSinh = eligible.stream()
                .collect(Collectors.groupingBy(nv -> safeText(nv.getNnCccd())));
        for (List<XtNguyenvongxettuyen> list : nvTheoThiSinh.values()) {
            list.sort(Comparator.comparingInt(this::nvThuTuSafe));
        }

        int maxNguyenVong = eligible.stream()
                .mapToInt(this::nvThuTuSafe)
                .max()
                .orElse(0);

        Map<String, Integer> daTrungTheoNganh = new HashMap<>();

        for (int tt = 1; tt <= maxNguyenVong; tt++) {
            // 4) Duyệt theo mức nguyện vọng: lấy NV mức tt của các thí sinh chưa trúng.
            List<XtNguyenvongxettuyen> nvMucHienTai = new ArrayList<>();
            for (Map.Entry<String, List<XtNguyenvongxettuyen>> entry : nvTheoThiSinh.entrySet()) {
                String cccd = entry.getKey();
                if (Boolean.TRUE.equals(daTrungTuyen.get(cccd))) {
                    continue;
                }
                List<XtNguyenvongxettuyen> list = entry.getValue();
                if (tt <= list.size()) {
                    nvMucHienTai.add(list.get(tt - 1));
                }
            }

            // 5) Nhóm theo ngành và xét tuyển theo chỉ tiêu + điểm xét tuyển giảm dần.
            Map<String, List<XtNguyenvongxettuyen>> nvTheoNganh = nvMucHienTai.stream()
                    .collect(Collectors.groupingBy(nv -> normalizeKey(nv.getNvManganh())));

            for (Map.Entry<String, List<XtNguyenvongxettuyen>> entry : nvTheoNganh.entrySet()) {
                String key = entry.getKey();
                XtNganh nganh = nganhByKey.get(key);
                if (nganh == null) {
                    for (XtNguyenvongxettuyen nv : entry.getValue()) {
                        nv.setNvKetqua("Rớt - không tìm thấy ngành");
                        rejectedByMissingNganh++;
                    }
                    continue;
                }

                int chiTieu = nvlInt(nganh.getNChitieu());
                int daTrung = daTrungTheoNganh.getOrDefault(key, 0);
                int chiTieuConLai = chiTieu - daTrung;
                if (chiTieuConLai <= 0) {
                    continue;
                }

                List<XtNguyenvongxettuyen> dsNganh = entry.getValue();
                dsNganh.sort((a, b) -> resolveDiemXetTuyen(b, nganhByKey, dolechTheoNganhVaTohop)
                    .compareTo(resolveDiemXetTuyen(a, nganhByKey, dolechTheoNganhVaTohop)));

                int count = 0;
                for (XtNguyenvongxettuyen nv : dsNganh) {
                    if (count >= chiTieuConLai) {
                        break;
                    }
                    nv.setNvKetqua("Trúng tuyển");
                    String cccd = safeText(nv.getNnCccd());
                    if (!cccd.isEmpty()) {
                        daTrungTuyen.put(cccd, true);
                    }
                    count++;
                }
                if (count > 0) {
                    daTrungTheoNganh.put(key, daTrung + count);
                }
            }
        }

        // 6) Gán kết quả cho các nguyện vọng còn lại và ghi DB.
        int passed = 0;
        int failed = 0;
        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            if (nv.getNvKetqua() == null || nv.getNvKetqua().isBlank()) {
                nv.setNvKetqua("Rớt");
            }
            if (nv.getNvKetqua().toLowerCase(Locale.ROOT).contains("trúng")) {
                passed++;
            } else {
                failed++;
            }
            dao.capNhatNguyenVong(nv);
        }

        // 7) Cập nhật điểm chuẩn ngành (min điểm trúng tuyển hoặc điểm sàn).
        for (XtNganh nganh : nganhByKey.values()) {
            BigDecimal diemChuan = null;
            String key = normalizeKey(nganh.getManganh());
            for (XtNguyenvongxettuyen nv : allNguyenVong) {
                if (!key.equals(normalizeKey(nv.getNvManganh()))) {
                    continue;
                }
                if (!safeText(nv.getNvKetqua()).toLowerCase(Locale.ROOT).contains("trúng")) {
                    continue;
                }
                BigDecimal score = resolveDiemXetTuyen(nv, nganhByKey, dolechTheoNganhVaTohop);
                if (diemChuan == null || score.compareTo(diemChuan) < 0) {
                    diemChuan = score;
                }
            }
            if (diemChuan == null) {
                diemChuan = nvlBigDecimal(nganh.getNDiemsan());
            }
            nganh.setNDiemtrungtuyen(diemChuan);
            nganhDao.update(nganh);
        }

        return new XetTuyenResult(total, passed, failed, rejectedByDiemSan, rejectedByMissingNganh);
    }

    private int nvThuTuSafe(XtNguyenvongxettuyen nv) {
        if (nv == null || nv.getNvTt() == null || nv.getNvTt() <= 0) {
            return Integer.MAX_VALUE;
        }
        return nv.getNvTt();
    }

    private BigDecimal resolveDiemXetTuyen(
            XtNguyenvongxettuyen nv,
            Map<String, XtNganh> nganhByKey,
            Map<String, BigDecimal> dolechTheoNganhVaTohop) {
        if (nv == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal base = nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : nv.getDiemThxt();
        if (base == null) {
            base = BigDecimal.ZERO;
        }
        String maNganh = normalizeKey(nv.getNvManganh());
        String toHopDangDung = normalizeKey(nv.getTtThm());
        BigDecimal dolech = traDolechTuBangNganhTohop(maNganh, toHopDangDung, nganhByKey, dolechTheoNganhVaTohop);
        return base.subtract(dolech);
    }

    /**
     * Nạp map khóa {@code manganh|matohop} (chữ thường) → {@code dolech} từ {@code xt_nganh_tohop}.
     */
    private Map<String, BigDecimal> napBangDolechTuDb() {
        Map<String, BigDecimal> map = new HashMap<>();
        for (XtNganhTohop row : nganhToHopDao.findAll()) {
            String mag = normalizeKey(row.getManganh());
            String th = normalizeKey(row.getMatohop());
            if (mag.isEmpty() || th.isEmpty()) {
                continue;
            }
            if (row.getDolech() != null) {
                map.put(mag + "|" + th, row.getDolech());
            }
        }
        return map;
    }

    /**
     * Độ lệch khi thí sinh dùng {@code matohop} đăng ký cho ngành: đọc từ DB (bảng {@code xt_nganh_tohop}).
     * Trùng tổ hợp với tổ hợp gốc ngành → 0. Không có bản ghi → 0.
     */
    private BigDecimal traDolechTuBangNganhTohop(
            String maNganh,
            String matohop,
            Map<String, XtNganh> nganhByKey,
            Map<String, BigDecimal> dolechTheoNganhVaTohop) {
        if (maNganh.isEmpty() || matohop.isEmpty()) {
            return BigDecimal.ZERO;
        }
        if (nganhByKey != null && !nganhByKey.isEmpty()) {
            XtNganh nganh = nganhByKey.get(maNganh);
            if (nganh != null && matohop.equals(normalizeKey(nganh.getNTohopgoc()))) {
                return BigDecimal.ZERO;
            }
        }
        if (dolechTheoNganhVaTohop == null || dolechTheoNganhVaTohop.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return nvlBigDecimal(dolechTheoNganhVaTohop.get(maNganh + "|" + matohop));
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeKey(String value) {
        return safeText(value).toLowerCase(Locale.ROOT);
    }

    private BigDecimal nvlBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int nvlInt(Integer value) {
        return value == null ? 0 : value;
    }

    public record XetTuyenResult(
            int total,
            int passed,
            int failed,
            int rejectedByDiemSan,
            int rejectedByMissingNganh) {
    }
}
