package org.AdmissionsSystem.bus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.AdmissionsSystem.dao.DiemCongDao;
import org.AdmissionsSystem.dao.DiemThiDao;
import org.AdmissionsSystem.dao.DiemVsatDao;
import org.AdmissionsSystem.dao.NganhHocDao;
import org.AdmissionsSystem.dao.NganhToHopDao;
import org.AdmissionsSystem.dao.ThiSinhDao;
import org.AdmissionsSystem.dao.UutienDao;
import org.AdmissionsSystem.dao.XtNguyenvongxettuyenDao;
import org.AdmissionsSystem.models.XtBangquydoi;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import org.AdmissionsSystem.models.XtUutien;
import org.AdmissionsSystem.util.QuyDoiDiemUtil;

public class XtNguyenvongxettuyenService {

    private final XtNguyenvongxettuyenDao dao = new XtNguyenvongxettuyenDao();
    private final NganhHocDao nganhDao = new NganhHocDao();
    private final NganhToHopDao nganhToHopDao = new NganhToHopDao();
    private final ThiSinhDao thiSinhDao = new ThiSinhDao();
    private final DiemThiDao diemThiDao = new DiemThiDao();
    private final DiemVsatDao diemVsatDao = new DiemVsatDao();
    private final DiemCongDao diemCongDao = new DiemCongDao();
    private final UutienDao uutienDao = new UutienDao();
    private final BangQuyDoiService bangQuyDoiService = new BangQuyDoiService();

    private static final BigDecimal DIEM_TOI_DA = new BigDecimal("30");
    private static final BigDecimal DIEM_MON_TOI_DA = new BigDecimal("10");
    private static final BigDecimal DIEM_UT_MOC = new BigDecimal("22.5");
    private static final BigDecimal DIEM_UT_MAU = new BigDecimal("7.5");
    private static final int SCALE = 3;
    private static final String KQ_TRUNG_TUYEN = "Trúng Tuyển";
    private static final String KQ_ROT = "Rớt";
    private static final String KQ_ROT_DU_CHI_TIEU = "Rớt - Ngành đã đủ chỉ tiêu";
    private static final String KQ_ROT_DAU_NV_KHAC = "Rớt - Đã đậu nguyện vọng khác";
    private static final String KQ_DUOI_SAN = "Dưới Sàn";

    /**
     * Lấy tất cả nguyện vọng xét tuyển
     * @return Danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> layTatCa() {
        return dao.layTatCaNguyenVong();
    }

    public List<XtNguyenvongxettuyen> layTheoTrang(int page, int pageSize) {
        return dao.layNguyenVongTheoTrang(page, pageSize);
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

    public void initQuyDoiCache() {
        List<XtBangquydoi> all = bangQuyDoiService.getAll();
        QuyDoiDiemUtil.init(all);
    }

    public int tinhDiemXetTuyenAll() {
        initQuyDoiCache();
        List<XtNguyenvongxettuyen> all = dao.layTatCaNguyenVong();
        if (all.isEmpty()) {
            return 0;
        }

        Map<String, XtNganh> nganhByKey = nganhDao.findAll().stream()
                .filter(nganh -> nganh != null && !normalizeKey(nganh.getManganh()).isEmpty())
                .collect(Collectors.toMap(
                        nganh -> normalizeKey(nganh.getManganh()),
                        nganh -> nganh,
                        (a, b) -> a));

        Map<String, List<XtNganhTohop>> toHopByNganh = nganhToHopDao.findAll().stream()
                .filter(row -> row != null && !normalizeKey(row.getManganh()).isEmpty())
                .collect(Collectors.groupingBy(row -> normalizeKey(row.getManganh())));

        Map<String, XtDiemthixettuyen> diemThptByCccd = buildDiemThiByPhuongThuc("THPT");
        Map<String, XtDiemthixettuyen> diemDgnlByCccd = buildDiemThiByPhuongThuc("DGNL");
        Map<String, XtDiemVsat> diemVsatByCccd = buildBestVsatByCccd();
        Map<String, List<XtDiemcongxetuyen>> diemCongByCccd = buildDiemCongByCccd();
        Map<String, List<XtUutien>> uutienByCccd = buildUutienByCccd();
        Map<String, XtThisinhxettuyen25> thiSinhByCccd = thiSinhDao.findAll().stream()
                .filter(ts -> ts != null && !normalizeKey(ts.getCccd()).isEmpty())
                .collect(Collectors.toMap(
                        ts -> normalizeKey(ts.getCccd()),
                        ts -> ts,
                        (a, b) -> a));

        int updated = 0;
        for (XtNguyenvongxettuyen nv : all) {
            ScoreResult best = tinhDiemChoNguyenVong(
                    nv,
                    nganhByKey,
                    toHopByNganh,
                    diemThptByCccd,
                    diemDgnlByCccd,
                    diemVsatByCccd,
                    diemCongByCccd,
                    uutienByCccd,
                    thiSinhByCccd);

            nv.setDiemThxt(best.diemThxt());
            nv.setDiemCong(best.diemCong());
            nv.setDiemUtqd(best.diemUtqd());
            nv.setDiemXettuyen(best.diemXettuyen());
            nv.setTtPhuongthuc(best.phuongThuc());
            nv.setTtThm(best.toHop());
            updated++;
        }
        dao.capNhatNguyenVongHangLoat(all);
        return updated;
    }

    public XetTuyenResult chayXetTuyenHeThong() {
        // Luôn tính lại ĐXT trước khi xét tuyển để đảm bảo dữ liệu mới nhất được ghi DB.
        tinhDiemXetTuyenAll();

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

        Map<String, List<XtUutien>> uutienByCccd = buildUutienByCccd();
        Set<String> uuTienCccdSet = uutienByCccd.keySet();

        int total = allNguyenVong.size();
        int rejectedByDiemSan = 0;
        int rejectedByMissingNganh = 0;

        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            nv.setNvKetqua(null);
        }

        // 2) Lọc bỏ các nguyện vọng không đạt điểm sàn hoặc thiếu ngành.
        List<XtNguyenvongxettuyen> eligible = new ArrayList<>();
        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            String maNganh = safeText(nv.getNvManganh());
            String key = normalizeKey(maNganh);
            XtNganh nganh = nganhByKey.get(key);

            if (nganh == null) {
                nv.setNvKetqua(KQ_ROT);
                rejectedByMissingNganh++;
                continue;
            }

            BigDecimal diemSan = nvlBigDecimal(nganh.getNDiemsan());
            BigDecimal diemXet = resolveDiemXetTuyen(nv);
            if (diemXet.compareTo(diemSan) < 0) {
                nv.setNvKetqua(KQ_DUOI_SAN);
                rejectedByDiemSan++;
                continue;
            }

            eligible.add(nv);
        }

        Map<String, Integer> chiTieuConLai = new HashMap<>();
        for (Map.Entry<String, XtNganh> entry : nganhByKey.entrySet()) {
            chiTieuConLai.put(entry.getKey(), nvlInt(entry.getValue().getNChitieu()));
        }

        // 3) Chia theo thí sinh để đảm bảo mỗi thí sinh được xét tuần tự các nguyện vọng.
        Map<String, List<XtNguyenvongxettuyen>> eligibleByThiSinh = eligible.stream()
                .collect(Collectors.groupingBy(this::resolveThiSinhKey));

        // 4) Xét nhóm có trong xt_uutien trước, rồi mới tới nhóm còn lại.
        List<String> uuTienOrder = sapXepThuTuThiSinh(eligibleByThiSinh, uuTienCccdSet, true);
        List<String> thuongOrder = sapXepThuTuThiSinh(eligibleByThiSinh, uuTienCccdSet, false);

        Set<String> daTrungTuyen = new HashSet<>();
        xetTheoThuTuThiSinh(uuTienOrder, eligibleByThiSinh, chiTieuConLai, daTrungTuyen);
        xetTheoThuTuThiSinh(thuongOrder, eligibleByThiSinh, chiTieuConLai, daTrungTuyen);

        // 5) Safety check: mỗi CCCD chỉ được trúng tối đa 1 nguyện vọng.
        // Nếu phát sinh >1 bản ghi trúng cho cùng CCCD, giữ lại NV thứ tự nhỏ nhất.
        damBaoMotThiSinhMotKetQuaTrung(allNguyenVong);

        // 6) Phân loại lý do rớt cho các nguyện vọng đủ điều kiện nhưng không đậu.
        phanLoaiLyDoRotSauXetTuyen(allNguyenVong);

        // 7) Chuẩn hóa kết quả và ghi DB.
        int passed = 0;
        int failed = 0;
        for (XtNguyenvongxettuyen nv : allNguyenVong) {
            if (nv.getNvKetqua() == null || nv.getNvKetqua().isBlank()) {
                nv.setNvKetqua(KQ_ROT);
            }
            nv.setNvKetqua(chuanHoaGiaTriKetQua(nv.getNvKetqua()));
            if (laKetQuaTrungTuyen(nv.getNvKetqua())) {
                passed++;
            } else {
                failed++;
            }
        }
        int expectedNvUpdates = (int) allNguyenVong.stream()
                .filter(nv -> nv != null && nv.getIdnv() != null)
                .count();
        int updatedNv = dao.capNhatKetQuaHangLoat(allNguyenVong);
        if (updatedNv < expectedNvUpdates) {
            throw new IllegalStateException(
                    "Không cập nhật đủ nv_ketqua. Dự kiến: " + expectedNvUpdates + ", thực tế: " + updatedNv);
        }

        // 8) Cập nhật điểm chuẩn ngành (min điểm trúng tuyển hoặc điểm sàn).
        List<XtNganh> nganhCanCapNhat = new ArrayList<>();
        for (XtNganh nganh : nganhByKey.values()) {
            BigDecimal diemChuan = null;
            String key = normalizeKey(nganh.getManganh());
            for (XtNguyenvongxettuyen nv : allNguyenVong) {
                if (!key.equals(normalizeKey(nv.getNvManganh()))) {
                    continue;
                }
                if (!laKetQuaTrungTuyen(nv.getNvKetqua())) {
                    continue;
                }
                BigDecimal score = resolveDiemXetTuyen(nv);
                if (diemChuan == null || score.compareTo(diemChuan) < 0) {
                    diemChuan = score;
                }
            }
            if (diemChuan == null) {
                diemChuan = nvlBigDecimal(nganh.getNDiemsan());
            }
            nganh.setNDiemtrungtuyen(diemChuan);
            nganhCanCapNhat.add(nganh);
        }
        nganhDao.capNhatNganhHangLoat(nganhCanCapNhat);

        return new XetTuyenResult(total, passed, failed, rejectedByDiemSan, rejectedByMissingNganh);
    }

    private List<String> sapXepThuTuThiSinh(
            Map<String, List<XtNguyenvongxettuyen>> eligibleByThiSinh,
            Set<String> uuTienCccdSet,
            boolean onlyUuTien) {
        return eligibleByThiSinh.keySet().stream()
                .filter(key -> uuTienCccdSet.contains(key) == onlyUuTien)
                .sorted((a, b) -> {
                    BigDecimal diemA = diemTotNhatCuaThiSinh(eligibleByThiSinh.getOrDefault(a, List.of()));
                    BigDecimal diemB = diemTotNhatCuaThiSinh(eligibleByThiSinh.getOrDefault(b, List.of()));
                    int cmp = diemB.compareTo(diemA);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return a.compareTo(b);
                })
                .collect(Collectors.toList());
    }

    private BigDecimal diemTotNhatCuaThiSinh(List<XtNguyenvongxettuyen> dsNv) {
        BigDecimal best = BigDecimal.ZERO;
        for (XtNguyenvongxettuyen nv : dsNv) {
            BigDecimal score = resolveDiemXetTuyen(nv);
            if (score.compareTo(best) > 0) {
                best = score;
            }
        }
        return best;
    }

    private void xetTheoThuTuThiSinh(
            List<String> thuTuThiSinh,
            Map<String, List<XtNguyenvongxettuyen>> eligibleByThiSinh,
            Map<String, Integer> chiTieuConLai,
            Set<String> daTrungTuyen) {
        for (String thiSinhKey : thuTuThiSinh) {
            if (daTrungTuyen.contains(thiSinhKey)) {
                continue;
            }
            List<XtNguyenvongxettuyen> dsNv = new ArrayList<>(eligibleByThiSinh.getOrDefault(thiSinhKey, List.of()));
            dsNv.sort((a, b) -> {
                int cmpNv = Integer.compare(nvThuTuSafe(a), nvThuTuSafe(b));
                if (cmpNv != 0) {
                    return cmpNv;
                }
                return resolveDiemXetTuyen(b).compareTo(resolveDiemXetTuyen(a));
            });

            for (XtNguyenvongxettuyen nv : dsNv) {
                String keyNganh = normalizeKey(nv.getNvManganh());
                int conLai = chiTieuConLai.getOrDefault(keyNganh, 0);
                if (conLai <= 0) {
                    continue;
                }
                nv.setNvKetqua(KQ_TRUNG_TUYEN);
                chiTieuConLai.put(keyNganh, conLai - 1);
                daTrungTuyen.add(thiSinhKey);
                break;
            }
        }
    }

    private void damBaoMotThiSinhMotKetQuaTrung(List<XtNguyenvongxettuyen> allNguyenVong) {
        if (allNguyenVong == null || allNguyenVong.isEmpty()) {
            return;
        }

        Map<String, List<XtNguyenvongxettuyen>> byCccd = allNguyenVong.stream()
                .filter(nv -> nv != null)
                .filter(nv -> !normalizeKey(nv.getNnCccd()).isEmpty())
                .collect(Collectors.groupingBy(nv -> normalizeKey(nv.getNnCccd())));

        for (List<XtNguyenvongxettuyen> dsNv : byCccd.values()) {
            List<XtNguyenvongxettuyen> dsTrung = dsNv.stream()
                    .filter(nv -> laKetQuaTrungTuyen(nv.getNvKetqua()))
                    .sorted((a, b) -> {
                        int cmpNv = Integer.compare(nvThuTuSafe(a), nvThuTuSafe(b));
                        if (cmpNv != 0) {
                            return cmpNv;
                        }
                        return resolveDiemXetTuyen(b).compareTo(resolveDiemXetTuyen(a));
                    })
                    .collect(Collectors.toList());

            if (dsTrung.size() <= 1) {
                continue;
            }

            for (int i = 1; i < dsTrung.size(); i++) {
                dsTrung.get(i).setNvKetqua(KQ_ROT);
            }
        }
    }

    private void phanLoaiLyDoRotSauXetTuyen(List<XtNguyenvongxettuyen> allNguyenVong) {
        if (allNguyenVong == null || allNguyenVong.isEmpty()) {
            return;
        }

        Map<String, List<XtNguyenvongxettuyen>> byThiSinh = allNguyenVong.stream()
                .filter(nv -> nv != null)
                .collect(Collectors.groupingBy(this::resolveThiSinhKey));

        for (List<XtNguyenvongxettuyen> dsNv : byThiSinh.values()) {
            boolean hasPassed = dsNv.stream()
                    .anyMatch(nv -> laKetQuaTrungTuyen(nv.getNvKetqua()));

            for (XtNguyenvongxettuyen nv : dsNv) {
                String ketQua = safeText(nv.getNvKetqua());
                if (!ketQua.isEmpty()) {
                    continue;
                }
                nv.setNvKetqua(hasPassed ? KQ_ROT_DAU_NV_KHAC : KQ_ROT_DU_CHI_TIEU);
            }
        }
    }

    private String resolveThiSinhKey(XtNguyenvongxettuyen nv) {
        if (nv == null) {
            return "";
        }
        String cccd = normalizeKey(nv.getNnCccd());
        if (!cccd.isEmpty()) {
            return cccd;
        }
        Integer idnv = nv.getIdnv();
        return idnv == null ? "" : "__idnv_" + idnv;
    }

    private int nvThuTuSafe(XtNguyenvongxettuyen nv) {
        if (nv == null || nv.getNvTt() == null || nv.getNvTt() <= 0) {
            return Integer.MAX_VALUE;
        }
        return nv.getNvTt();
    }

    private BigDecimal resolveDiemXetTuyen(XtNguyenvongxettuyen nv) {
        if (nv == null) {
            return BigDecimal.ZERO;
        }
        if (nv.getDiemXettuyen() != null) {
            return nv.getDiemXettuyen();
        }
        BigDecimal diemThxt = nvlBigDecimal(nv.getDiemThxt());
        BigDecimal diemCong = nvlBigDecimal(nv.getDiemCong());
        BigDecimal diemUtqd = nvlBigDecimal(nv.getDiemUtqd());
        return diemThxt.add(diemCong).add(diemUtqd);
    }

    private ScoreResult tinhDiemChoNguyenVong(
            XtNguyenvongxettuyen nv,
            Map<String, XtNganh> nganhByKey,
            Map<String, List<XtNganhTohop>> toHopByNganh,
            Map<String, XtDiemthixettuyen> diemThptByCccd,
            Map<String, XtDiemthixettuyen> diemDgnlByCccd,
            Map<String, XtDiemVsat> diemVsatByCccd,
            Map<String, List<XtDiemcongxetuyen>> diemCongByCccd,
            Map<String, List<XtUutien>> uutienByCccd,
            Map<String, XtThisinhxettuyen25> thiSinhByCccd) {
        if (nv == null) {
            return ScoreResult.empty();
        }

        String cccdKey = normalizeKey(nv.getNnCccd());
        String maNganhKey = normalizeKey(nv.getNvManganh());
        XtNganh nganh = nganhByKey.get(maNganhKey);
        if (nganh == null) {
            return ScoreResult.empty();
        }

        List<XtNganhTohop> toHops = toHopByNganh.getOrDefault(maNganhKey, List.of());
        XtThisinhxettuyen25 thiSinh = thiSinhByCccd.get(cccdKey);
        XtUutien uuTien = resolveUutien(nv, uutienByCccd);
        boolean isTeacherTraining = laNganhSuPham(maNganhKey);

        ScoreResult best = ScoreResult.empty();
        BigDecimal bestScore = BigDecimal.ZERO;

        // PT4 (THPT) - chỉ xét nếu ngành cho phép phương thức THPT
        XtDiemthixettuyen diemThpt = diemThptByCccd.get(cccdKey);
        boolean allowThpt = "Y".equalsIgnoreCase(safeText(nganh.getNThpt()));
        if (allowThpt && diemThpt != null) {
            for (XtNganhTohop th : toHops) {
                ScoreResult candidate = tinhTheoToHop(
                        nv, th, "THPT", diemThpt, null, diemCongByCccd, thiSinh, uuTien, false);
                if (candidate.diemXettuyen().compareTo(bestScore) > 0) {
                    bestScore = candidate.diemXettuyen();
                    best = candidate;
                }
            }
        }

        // PT3 (V-SAT) - xét nếu ngành cho phép và thí sinh có điểm
        XtDiemVsat diemVsat = diemVsatByCccd.get(cccdKey);
        boolean allowVsat = "Y".equalsIgnoreCase(safeText(nganh.getNVsat()));
        if (!isTeacherTraining && allowVsat && diemVsat != null) {
            for (XtNganhTohop th : toHops) {
                ScoreResult candidate = tinhTheoToHop(
                        nv, th, "VSAT", null, diemVsat, diemCongByCccd, thiSinh, uuTien, true);
                if (candidate.diemXettuyen().compareTo(bestScore) > 0) {
                    bestScore = candidate.diemXettuyen();
                    best = candidate;
                }
            }
        }

        // PT2 (ĐGNL) - xét nếu ngành cho phép và thí sinh có điểm
        XtDiemthixettuyen diemDgnl = diemDgnlByCccd.get(cccdKey);
        boolean allowDgnl = "Y".equalsIgnoreCase(safeText(nganh.getNDgnl()));
        if (!isTeacherTraining && allowDgnl && diemDgnl != null) {
            String toHopDgnl = safeText(nganh.getNTohopgoc());
            if (toHopDgnl.isEmpty()) toHopDgnl = safeText(nv.getTtThm());
            if (!toHopDgnl.isEmpty()) {
                BigDecimal raw = pickDgnlRaw(diemDgnl);
                if (raw != null) {
                    // Nếu thiếu bản phân vị cho tổ hợp, bỏ qua DGNL và xét các phương thức còn lại.
                    BigDecimal dthxt = quyDoiDgnlAnToan(raw.floatValue(), toHopDgnl);
                    if (dthxt != null) {
                        dthxt = applyDiemUuTienThxtOnly(dthxt, uuTien);
                        BigDecimal dolech = BigDecimal.ZERO;
                        BigDecimal dcXet = resolveDiemCong(cccdKey, nv.getNvManganh(), toHopDgnl, "DGNL", diemCongByCccd);
                        BigDecimal dthgxt = dthxt.subtract(dolech);
                        BigDecimal duut = tinhDiemUuTien(dthgxt, dcXet, thiSinh);
                        BigDecimal dxt = capDiem(dthxt.add(dcXet).add(duut));
                        BigDecimal diemCongLuu = tongChuaHeSoCongUuTien(dthxt, duut);
                        ScoreResult candidate = new ScoreResult(dthxt, diemCongLuu, duut, dxt, "DGNL", toHopDgnl);
                        if (candidate.diemXettuyen().compareTo(bestScore) > 0) {
                            bestScore = candidate.diemXettuyen();
                            best = candidate;
                        }
                    }
                }
            }
        }

        return best;
    }

    private ScoreResult tinhTheoToHop(
        XtNguyenvongxettuyen nv,
        XtNganhTohop th,
        String phuongThuc,
        XtDiemthixettuyen diemThpt,
        XtDiemVsat diemVsat,
        Map<String, List<XtDiemcongxetuyen>> diemCongByCccd,
        XtThisinhxettuyen25 thiSinh,
        XtUutien uuTien,
        boolean laVsat) {
        if (th == null) return ScoreResult.empty();

        // Lấy danh sách các môn hợp lệ cho "môn còn lại" dựa trên phương thức
        List<String> danhSachMonConLai = getDanhSachMonConLai(phuongThuc);
        
        // Tạo các bộ môn từ tổ hợp gốc, thay thế 'KHAC' bằng tất cả các môn trong danh sách
        List<List<String>> cacToHop = generateCombinations(th, danhSachMonConLai);
        
        ScoreResult best = ScoreResult.empty();
        BigDecimal bestScore = BigDecimal.ZERO;
        
        for (List<String> combo : cacToHop) {
            String mon1 = combo.get(0);
            String mon2 = combo.get(1);
            String mon3 = combo.get(2);
            
            BigDecimal d1 = laVsat ? getMonDiemVsat(mon1, diemVsat) : getMonDiemThpt(mon1, diemThpt);
            BigDecimal d2 = laVsat ? getMonDiemVsat(mon2, diemVsat) : getMonDiemThpt(mon2, diemThpt);
            BigDecimal d3 = laVsat ? getMonDiemVsat(mon3, diemVsat) : getMonDiemThpt(mon3, diemThpt);
            
            if (d1 == null || d2 == null || d3 == null) continue;

            MonCongResult monCong = applyUuTienTheoMon(mon1, mon2, mon3, d1, d2, d3, uuTien);
            d1 = monCong.d1();
            d2 = monCong.d2();
            d3 = monCong.d3();
             
            // Tính điểm như cũ...
            int hs1 = normalizeHeSo(th.getHsmon1());
            int hs2 = normalizeHeSo(th.getHsmon2());
            int hs3 = normalizeHeSo(th.getHsmon3());
            int w = hs1 + hs2 + hs3;
            BigDecimal tong = d1.multiply(BigDecimal.valueOf(hs1))
                    .add(d2.multiply(BigDecimal.valueOf(hs2)))
                    .add(d3.multiply(BigDecimal.valueOf(hs3)));
            BigDecimal dthxt = tong.divide(BigDecimal.valueOf(w), SCALE+4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(3)).setScale(SCALE, RoundingMode.HALF_UP);
            if (!monCong.appliedDcGiai()) {
                dthxt = applyDiemUuTienThxtOnly(dthxt, uuTien);
            }
            BigDecimal dolech = nvlBigDecimal(th.getDolech());
            BigDecimal dthgxt = dthxt.subtract(dolech);
            String toHop = safeText(th.getMatohop());
            BigDecimal dcXet = resolveDiemCong(normalizeKey(nv.getNnCccd()), nv.getNvManganh(), toHop, phuongThuc, diemCongByCccd);
            BigDecimal duut = tinhDiemUuTien(dthgxt, dcXet, thiSinh);
            BigDecimal dxt = capDiem(dthxt.add(dcXet).add(duut));
            BigDecimal tong3MonChuaHeSo = d1.add(d2).add(d3).setScale(SCALE, RoundingMode.HALF_UP);
            BigDecimal diemCongLuu = tongChuaHeSoCongUuTien(tong3MonChuaHeSo, duut);
            
            if (dxt.compareTo(bestScore) > 0) {
                bestScore = dxt;
                best = new ScoreResult(dthxt, diemCongLuu, duut, dxt, phuongThuc, toHop);
            }
        }
        return best;
    }

    // Hàm lấy danh sách môn hợp lệ theo phương thức
    private List<String> getDanhSachMonConLai(String phuongThuc) {
        if ("THPT".equalsIgnoreCase(phuongThuc)) {
            return List.of("TO", "LI", "HO", "SI", "SU", "DI", "VA", "TI", "KTPL", "CNCN", "CNNN");
        } else if ("VSAT".equalsIgnoreCase(phuongThuc)) {
            return List.of("TO", "VA", "LI", "HO", "SI", "SU", "DI", "TI");
        }
        return List.of(); // DGNL không có tổ hợp môn
    }

    // Hàm sinh tổ hợp từ template (thay thế 'KHAC' bằng các môn)
    private List<List<String>> generateCombinations(XtNganhTohop th, List<String> monList) {
        String mon1 = safeText(th.getThMon1());
        String mon2 = safeText(th.getThMon2());
        String mon3 = safeText(th.getThMon3());
        
        List<String> candidates1 = isKhac(mon1) ? monList : List.of(mon1);
        List<String> candidates2 = isKhac(mon2) ? monList : List.of(mon2);
        List<String> candidates3 = isKhac(mon3) ? monList : List.of(mon3);
        
        List<List<String>> result = new ArrayList<>();
        for (String m1 : candidates1) {
            for (String m2 : candidates2) {
                if (m1.equals(m2)) continue;
                for (String m3 : candidates3) {
                    if (m3.equals(m1) || m3.equals(m2)) continue;
                    result.add(List.of(m1, m2, m3));
                }
            }
        }
        return result;
    }

    private boolean isKhac(String mon) {
        return mon == null || mon.trim().isEmpty() || "KHAC".equalsIgnoreCase(mon.trim());
    }

    private MonCongResult applyUuTienTheoMon(
            String mon1,
            String mon2,
            String mon3,
            BigDecimal d1,
            BigDecimal d2,
            BigDecimal d3,
            XtUutien uuTien) {
        if (uuTien == null) {
            return new MonCongResult(d1, d2, d3, false);
        }

        String maMonUuTien = normalizeSubject(uuTien.getMaMon());
        BigDecimal dcGiai = nvlBigDecimal(uuTien.getDcGiai());

        boolean applied = false;
        if (!maMonUuTien.isEmpty()) {
            String mon1Key = normalizeSubject(mon1);
            String mon2Key = normalizeSubject(mon2);
            String mon3Key = normalizeSubject(mon3);
            if (maMonUuTien.equals(mon1Key)) {
                d1 = capDiemMon(d1.add(dcGiai));
                applied = true;
            } else if (maMonUuTien.equals(mon2Key)) {
                d2 = capDiemMon(d2.add(dcGiai));
                applied = true;
            } else if (maMonUuTien.equals(mon3Key)) {
                d3 = capDiemMon(d3.add(dcGiai));
                applied = true;
            }
        }

        return new MonCongResult(d1, d2, d3, applied);
    }

    private BigDecimal applyDiemUuTienThxtOnly(BigDecimal dthxt, XtUutien uuTien) {
        if (uuTien == null || dthxt == null) {
            return dthxt;
        }
        BigDecimal dcThxt = nvlBigDecimal(uuTien.getDcThxt());
        return dthxt.add(dcThxt).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal capDiemMon(BigDecimal diemMon) {
        if (diemMon == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = diemMon;
        if (result.compareTo(DIEM_MON_TOI_DA) > 0) {
            result = DIEM_MON_TOI_DA;
        }
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }
        return result.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal tongChuaHeSoCongUuTien(BigDecimal tongChuaHeSo, BigDecimal diemUuTien) {
        BigDecimal tong = nvlBigDecimal(tongChuaHeSo).add(nvlBigDecimal(diemUuTien));
        if (tong.compareTo(BigDecimal.ZERO) < 0) {
            tong = BigDecimal.ZERO;
        }
        return tong.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private XtUutien resolveUutien(
            XtNguyenvongxettuyen nv,
            Map<String, List<XtUutien>> uutienByCccd) {
        if (nv == null || uutienByCccd == null) {
            return null;
        }
        String cccdKey = normalizeKey(nv.getNnCccd());
        if (cccdKey.isEmpty()) {
            return null;
        }
        List<XtUutien> rows = uutienByCccd.getOrDefault(cccdKey, List.of());
        if (rows.isEmpty()) {
            return null;
        }

        XtUutien exact = null;
        XtUutien fallback = null;
        int nvTt = nvThuTuSafe(nv);
        for (XtUutien row : rows) {
            if (row == null) {
                continue;
            }
            if (!matchUutienNganh(row, nv.getNvManganh())) {
                continue;
            }
            int rowTt = row.getTtNv() == null ? 1 : row.getTtNv();
            if (rowTt == nvTt && exact == null) {
                exact = row;
            }
            if (fallback == null) {
                fallback = row;
            }
        }
        return exact != null ? exact : fallback;
    }

    private boolean matchUutienNganh(XtUutien row, String maNganhNv) {
        String maNganhUuTien = normalizeKey(row.getMaNganh());
        if (maNganhUuTien.isEmpty()) {
            return true;
        }
        return maNganhUuTien.equals(normalizeKey(maNganhNv));
    }

    private BigDecimal tinhDiemUuTien(BigDecimal dthgxt, BigDecimal diemCong, XtThisinhxettuyen25 thiSinh) {
        BigDecimal mucUuTien = tinhMucUuTien(thiSinh);
        BigDecimal t = dthgxt.add(diemCong);
        if (t.compareTo(DIEM_UT_MOC) < 0) {
            return mucUuTien;
        }
        BigDecimal duut = mucUuTien.multiply(DIEM_TOI_DA.subtract(t))
                .divide(DIEM_UT_MAU, SCALE + 4, RoundingMode.HALF_UP);
        if (duut.compareTo(BigDecimal.ZERO) < 0) {
            duut = BigDecimal.ZERO;
        }
        return duut.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal tinhMucUuTien(XtThisinhxettuyen25 thiSinh) {
        if (thiSinh == null) return BigDecimal.ZERO;
        String rawKhuVuc = normalizeText(thiSinh.getKhuVuc());
        String doiTuong = normalizeText(thiSinh.getDoiTuong());

        // Hỗ trợ nhiều dạng mã khu vực: '1','2','3','2NT' hoặc 'kv1','kv2','kv3','kv2nt'
        String khuVuc = switch (rawKhuVuc) {
            case "1", "kv1" -> "kv1";
            case "2", "kv2" -> "kv2";
            case "2nt", "2-nt", "kv2-nt", "kv2nt" -> "kv2nt";
            case "3", "kv3" -> "kv3";
            default -> rawKhuVuc;
        };

        BigDecimal kv = switch (khuVuc) {
            case "kv1" -> new BigDecimal("0.75");
            case "kv2nt", "kv2-nt" -> new BigDecimal("0.5");
            case "kv2" -> new BigDecimal("0.25");
            case "kv3" -> BigDecimal.ZERO;
            default -> BigDecimal.ZERO;
        };

        BigDecimal dt = switch (doiTuong) {
            case "dt1" -> new BigDecimal("2.0");
            case "dt2" -> new BigDecimal("1.0");
            case "dt3" -> new BigDecimal("0.5");
            case "dt4" -> new BigDecimal("0.25");
            default -> BigDecimal.ZERO;
        };

        return kv.add(dt).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private boolean isPhuongThucChoPhep(String value) {
        if (value == null) return false;
        // Chuẩn hóa: "1", "true", "yes" -> cho phép
        String v = value.trim().toLowerCase();
        return v.equals("1") || v.equals("true") || v.equals("yes");
    }

    private BigDecimal resolveDiemCong(
            String cccdKey,
            String maNganh,
            String maToHop,
            String phuongThuc,
            Map<String, List<XtDiemcongxetuyen>> diemCongByCccd) {
        if (diemCongByCccd == null || cccdKey.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<XtDiemcongxetuyen> rows = diemCongByCccd.getOrDefault(cccdKey, List.of());
        for (XtDiemcongxetuyen row : rows) {
            if (!normalizeKey(row.getManganh()).equals(normalizeKey(maNganh))) {
                continue;
            }
            if (!normalizeKey(row.getMatohop()).equals(normalizeKey(maToHop))) {
                continue;
            }
            String rowPhuongThuc = normalizeText(row.getPhuongthuc());
            if (!rowPhuongThuc.isEmpty() && !rowPhuongThuc.equalsIgnoreCase(normalizeText(phuongThuc))) {
                continue;
            }
            BigDecimal tong = row.getDiemtong();
            if (tong == null) {
                tong = nvlBigDecimal(row.getDiemcc()).add(nvlBigDecimal(row.getDiemutxt()));
            }
            return capDiemCong(tong);
        }
        return BigDecimal.ZERO;
    }

    private int normalizeHeSo(Integer value) {
        int safe = nvlInt(value);
        return safe <= 0 ? 1 : safe;
    }

    private BigDecimal capDiemCong(BigDecimal diemCong) {
        if (diemCong == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = diemCong;
        if (result.compareTo(new BigDecimal("3")) > 0) {
            result = new BigDecimal("3");
        }
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }
        return result.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal capDiem(BigDecimal diem) {
        if (diem == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = diem;
        if (result.compareTo(DIEM_TOI_DA) > 0) {
            result = DIEM_TOI_DA;
        }
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }
        return result.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private Map<String, XtDiemthixettuyen> buildDiemThiByPhuongThuc(String phuongThuc) {
        Map<String, XtDiemthixettuyen> map = new HashMap<>();
        for (XtDiemthixettuyen row : diemThiDao.findAll()) {
            if (row == null) {
                continue;
            }
            if (!matchPhuongThuc(row, phuongThuc)) {
                continue;
            }
            String key = normalizeKey(row.getCccd());
            if (key.isEmpty()) {
                continue;
            }
            XtDiemthixettuyen existing = map.get(key);
            if (existing == null || shouldReplaceDiemThi(existing, row, phuongThuc)) {
                map.put(key, row);
            }
        }
        return map;
    }

    private boolean matchPhuongThuc(XtDiemthixettuyen row, String phuongThuc) {
        if (row == null) {
            return false;
        }
        String target = normalizeText(phuongThuc);
        String method = normalizeText(row.getDPhuongthuc());
        if (!method.isEmpty()) {
            return method.equalsIgnoreCase(target);
        }
        if (target.equals("dgnl")) {
            return isPositive(row.getNl1());
        }
        if (target.equals("thpt")) {
            return hasAnyThptScore(row);
        }
        return false;
    }

    private boolean hasAnyThptScore(XtDiemthixettuyen row) {
        return isPositive(row.getTo())
                || isPositive(row.getLi())
                || isPositive(row.getHo())
                || isPositive(row.getSi())
                || isPositive(row.getSu())
                || isPositive(row.getDi())
                || isPositive(row.getVa())
                || isPositive(row.getGdcd())
                || isPositive(row.getTi())
                || isPositive(row.getKtpl())
                || isPositive(row.getCncn())
                || isPositive(row.getCnnn())
                || isPositive(row.getNk1())
                || isPositive(row.getNk2())
                || isPositive(row.getNk3())
                || isPositive(row.getNk4())
                || isPositive(row.getNk5())
                || isPositive(row.getNk6());
    }

    private boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean shouldReplaceDiemThi(XtDiemthixettuyen existing, XtDiemthixettuyen candidate, String phuongThuc) {
        if (existing == null) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        BigDecimal existingScore = tongDiem(existing, phuongThuc);
        BigDecimal candidateScore = tongDiem(candidate, phuongThuc);
        return candidateScore.compareTo(existingScore) > 0;
    }

    private BigDecimal tongDiem(XtDiemthixettuyen row, String phuongThuc) {
        if (row == null) {
            return BigDecimal.ZERO;
        }
        String method = normalizeText(phuongThuc);
        if (method.equals("dgnl")) {
            BigDecimal raw = pickDgnlRaw(row);
            return raw == null ? BigDecimal.ZERO : raw;
        }
        return sumBigDecimal(
                row.getTo(),
                row.getLi(),
                row.getHo(),
                row.getSi(),
                row.getSu(),
                row.getDi(),
                row.getVa(),
                row.getGdcd(),
                row.getTi(),
                row.getKtpl());
    }

    private BigDecimal pickDgnlRaw(XtDiemthixettuyen row) {
        if (row == null) {
            return null;
        }
        return isPositive(row.getNl1()) ? row.getNl1() : null;
    }

    private Map<String, XtDiemVsat> buildBestVsatByCccd() {
        Map<String, XtDiemVsat> map = new HashMap<>();
        for (XtDiemVsat row : diemVsatDao.findAll()) {
            if (row == null) {
                continue;
            }
            if (!hasAnyVsatScore(row)) {
                continue;
            }
            String key = normalizeKey(row.getCccd());
            if (key.isEmpty()) {
                continue;
            }
            XtDiemVsat existing = map.get(key);
            if (existing == null || tongDiemVsat(row).compareTo(tongDiemVsat(existing)) > 0) {
                map.put(key, row);
            }
        }
        return map;
    }

    private Map<String, List<XtDiemcongxetuyen>> buildDiemCongByCccd() {
        return diemCongDao.findAll().stream()
                .filter(row -> row != null && !normalizeKey(row.getTsCccd()).isEmpty())
                .collect(Collectors.groupingBy(row -> normalizeKey(row.getTsCccd())));
    }

    private Map<String, List<XtUutien>> buildUutienByCccd() {
        return uutienDao.findAll().stream()
                .filter(row -> row != null && !normalizeKey(row.getCccd()).isEmpty())
                .collect(Collectors.groupingBy(row -> normalizeKey(row.getCccd())));
    }

    private BigDecimal tongDiemVsat(XtDiemVsat row) {
        if (row == null) {
            return BigDecimal.ZERO;
        }
        return sumBigDecimal(
                row.getToanVsat(),
                row.getVanVsat(),
                row.getAnhVsat(),
                row.getLyVsat(),
                row.getHoaVsat(),
                row.getSinhVsat(),
                row.getSuVsat(),
                row.getDiaVsat());
    }

    private boolean hasAnyVsatScore(XtDiemVsat row) {
        if (row == null) {
            return false;
        }
        return isPositive(row.getToanVsat())
                || isPositive(row.getVanVsat())
                || isPositive(row.getAnhVsat())
                || isPositive(row.getLyVsat())
                || isPositive(row.getHoaVsat())
                || isPositive(row.getSinhVsat())
                || isPositive(row.getSuVsat())
                || isPositive(row.getDiaVsat());
    }

    private BigDecimal quyDoiDgnlAnToan(float raw, String toHop) {
        try {
            return QuyDoiDiemUtil.quyDoiDgnl(raw, toHop);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private BigDecimal getMonDiemThpt(String mon, XtDiemthixettuyen diem) {
        if (diem == null) return null;
        String code = normalizeSubject(mon);
        switch (code) {
            case "to": return diem.getTo();
            case "li": return diem.getLi();
            case "ho": return diem.getHo();
            case "si": return diem.getSi();
            case "su": return diem.getSu();
            case "di": return diem.getDi();
            case "va": return diem.getVa();
            case "gdcd": return diem.getGdcd();
            case "ti": return diem.getTi();           // Tin học
            case "ktpl": return diem.getKtpl();
            case "cncn": return diem.getCncn();
            case "cnnn": return diem.getCnnn();
            case "nk1": return diem.getNk1();
            case "nk2": return diem.getNk2();
            case "nk3": return diem.getNk3();
            case "nk4": return diem.getNk4();
            case "nk5": return diem.getNk5();
            case "nk6": return diem.getNk6();
            case "n1": {
                // Tiếng Anh: ưu tiên N1_CC, nếu null thì lấy N1_THI
                BigDecimal cc = diem.getN1Cc();
                return (cc != null && cc.compareTo(BigDecimal.ZERO) > 0) ? cc : diem.getN1Thi();
            }
            default: return null;
        }
    }

    private BigDecimal getMonDiemVsat(String mon, XtDiemVsat diem) {
        if (diem == null) return null;
        String code = normalizeSubject(mon);
        BigDecimal raw = switch (code) {
            case "to" -> diem.getToanVsat();
            case "va" -> diem.getVanVsat();
            case "ti" -> diem.getAnhVsat();
            case "li" -> diem.getLyVsat();
            case "ho" -> diem.getHoaVsat();
            case "si" -> diem.getSinhVsat();
            case "su" -> diem.getSuVsat();
            case "di" -> diem.getDiaVsat();
            default -> null;
        };
        if (raw == null) return null;
        try {
            // Sửa: trực tiếp nhận BigDecimal
            return QuyDoiDiemUtil.quyDoiVsat(raw.floatValue(), code.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private BigDecimal sumBigDecimal(BigDecimal... values) {
        BigDecimal sum = BigDecimal.ZERO;
        if (values == null) {
            return sum;
        }
        for (BigDecimal value : values) {
            if (value != null) {
                sum = sum.add(value);
            }
        }
        return sum;
    }

    private BigDecimal toBigDecimal(float value) {
        return new BigDecimal(Float.toString(value)).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private String normalizeSubject(String mon) {
        String normalized = normalizeText(mon);
        return switch (normalized) {
            case "toan" -> "to";
            case "ly" -> "li";
            case "hoa" -> "ho";
            case "sinh" -> "si";
            case "su" -> "su";
            case "dia" -> "di";
            case "van" -> "va";
            case "nguvan" -> "va";
            case "tienganh" -> "n1";      // Tiếng Anh → lấy từ N1_THI/N1_CC
            case "tinhoc" -> "ti";       
            case "gdcd" -> "gdcd";
            case "ktpl" -> "ktpl";
            case "cncn" -> "cncn";
            case "cnnn" -> "cnnn";
            case "nk1" -> "nk1";
            case "nk2" -> "nk2";
            case "nk3" -> "nk3";
            case "nk4" -> "nk4";
            case "nk5" -> "nk5";
            case "nk6" -> "nk6";
            default -> normalized;
        };
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^a-z0-9]", "");
    }

    @SafeVarargs
    private <T> T firstNonNull(T... values) {
        if (values == null) {
            return null;
        }
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeKey(String value) {
        return safeText(value).toLowerCase(Locale.ROOT);
    }

    private boolean laKetQuaTrungTuyen(String ketQua) {
        String normalized = normalizeText(ketQua);
        return normalized.equals("trungtuyen")
                || normalized.equals("trung")
                || normalized.equals("dat");
    }

    private String chuanHoaGiaTriKetQua(String ketQua) {
        String normalized = normalizeText(ketQua);
        if (normalized.equals("trungtuyen") || normalized.equals("trung") || normalized.equals("dat")) {
            return KQ_TRUNG_TUYEN;
        }
        if (normalized.equals("duoisan") || normalized.contains("diemsan")) {
            return KQ_DUOI_SAN;
        }
        if (normalized.contains("chitieu") || normalized.contains("duchitieu")) {
            return KQ_ROT_DU_CHI_TIEU;
        }
        if (normalized.contains("daunguyenvongkhac") || normalized.contains("nguyenvongkhac")) {
            return KQ_ROT_DAU_NV_KHAC;
        }
        return KQ_ROT;
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

    private record MonCongResult(
            BigDecimal d1,
            BigDecimal d2,
            BigDecimal d3,
            boolean appliedDcGiai) {
    }

    private record ScoreResult(
            BigDecimal diemThxt,
            BigDecimal diemCong,
            BigDecimal diemUtqd,
            BigDecimal diemXettuyen,
            String phuongThuc,
            String toHop) {

        private static ScoreResult empty() {
            return new ScoreResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "", "");
        }
    }

    private boolean laNganhSuPham(String maNganh) {
        if (maNganh == null) return false;
        // Quản lý giáo dục (7140114) không thuộc nhóm sư phạm
        if (maNganh.equals("7140114")) return false;
        // Các ngành sư phạm có mã bắt đầu bằng "71402"
        return maNganh.startsWith("71402");
    }
}
