package org.AdmissionsSystem.bus.service.QuanLiDiem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.AdmissionsSystem.dao.BangQuyDoiDao;
import org.AdmissionsSystem.dao.DiemCongDao;
import org.AdmissionsSystem.dao.NganhHocDao;
import org.AdmissionsSystem.dao.NganhToHopDao;
import org.AdmissionsSystem.dao.QuanLiDiemDao;
import org.AdmissionsSystem.dao.QuanLiDiemVsatDao;
import org.AdmissionsSystem.dao.ThiSinhDao;
import org.AdmissionsSystem.dao.XtNguyenvongxettuyenDao;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import org.AdmissionsSystem.util.QuyDoiDiemUtil;
import org.AdmissionsSystem.util.TinhDiemThxtUtil;

public class DanhSachDiemXetTuyenService {
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final Set<String> PT_THPT_VSAT = Set.of("PT3", "PT4");
	private static final String PT_DGNL = "PT2";
	private static boolean quyDoiInitialized;

	private final XtNguyenvongxettuyenDao nguyenVongDao = new XtNguyenvongxettuyenDao();
	private final NganhToHopDao nganhToHopDao = new NganhToHopDao();
	private final NganhHocDao nganhHocDao = new NganhHocDao();
	private final DiemCongDao diemCongDao = new DiemCongDao();
	private final ThiSinhDao thiSinhDao = new ThiSinhDao();
	private final QuanLiDiemDao diemDao = new QuanLiDiemDao();
	private final QuanLiDiemVsatDao vsatDao = new QuanLiDiemVsatDao();
	private final BangQuyDoiDao bangQuyDoiDao = new BangQuyDoiDao();

	public PagedResult<SummaryRecord> queryPage(String cccdKeyword, int page, int pageSize) {
		ensureQuyDoiCache();
		int safePage = Math.max(1, page);
		int safePageSize = Math.max(1, pageSize);

		List<XtNguyenvongxettuyen> rows = nguyenVongDao.findPageByCccd(cccdKeyword, safePage, safePageSize);
		long total = nguyenVongDao.countByCccd(cccdKeyword);

		AggregationContext context = new AggregationContext();
		List<SummaryRecord> results = new ArrayList<>();
		for (XtNguyenvongxettuyen row : rows) {
			if (row == null) {
				continue;
			}
			ChiTietRecord detail = buildChiTiet(row, context);
			results.add(detail.summary());
		}

		return new PagedResult<>(results, total);
	}

	public ChiTietRecord getChiTiet(int idnv) {
		ensureQuyDoiCache();
		XtNguyenvongxettuyen entity = nguyenVongDao.findById(idnv);
		if (entity == null) {
			return null;
		}
		return buildChiTiet(entity, new AggregationContext());
	}

	private ChiTietRecord buildChiTiet(XtNguyenvongxettuyen nv, AggregationContext context) {
		String cccd = safeText(nv.getNnCccd());
		String maNganh = safeText(nv.getNvManganh());
		int thuTu = nv.getNvTt() == null ? 0 : nv.getNvTt();

		XtThisinhxettuyen25 thiSinh = context.getThiSinh(cccd);
		String soBaoDanh = thiSinh == null ? "" : safeText(thiSinh.getSobaodanh());
		String hoTen = soBaoDanh;

		XtNganh nganh = context.getNganh(maNganh);
		String tenNganh = nganh == null ? "" : safeText(nganh.getTennganh());
		String nguyenVongLabel = tenNganh.isEmpty() ? maNganh : (maNganh + " - " + tenNganh);

		List<XtNganhTohop> toHops = context.getToHop(maNganh);
		XtDiemthixettuyen diemThpt = context.getDiemThpt(cccd);
		List<XtDiemcongxetuyen> diemCongRows = context.getDiemCongRows(cccd);

		boolean allowThpt = allowByFlag(nganh == null ? null : nganh.getNThpt());
		boolean allowVsat = allowByFlag(nganh == null ? null : nganh.getNVsat());
		boolean allowDgnl = allowByFlag(nganh == null ? null : nganh.getNDgnl());

		Map<String, VsatSubjectRecord> vsatBest = Map.of();
		if (allowVsat) {
			List<XtDiemVsat> vsatRows = context.getVsatRows(cccd);
			vsatBest = buildVsatBest(vsatRows);
		}

		List<ToHopRecord> toHopRecords = new ArrayList<>();
		ToHopRecord bestCombo = null;
		if (allowThpt || allowVsat) {
			Map<String, BigDecimal> subjectScores = buildSubjectScores(toHops, diemThpt, vsatBest, allowThpt,
					allowVsat);
			Set<String> phuongThucFilter = buildPhuongThucFilter(allowThpt, allowVsat);
			for (XtNganhTohop toHop : toHops) {
				ToHopRecord record = buildToHopRecord(toHop, maNganh, subjectScores, diemCongRows, phuongThucFilter);
				toHopRecords.add(record);
				if (bestCombo == null || record.diemToHop().compareTo(bestCombo.diemToHop()) > 0) {
					bestCombo = record;
				}
			}
		}

		DgnlRecord dgnlRecord = null;
		if (allowDgnl) {
			dgnlRecord = buildDgnlRecord(diemThpt, maNganh, nganh, toHops, diemCongRows);
		}

		BigDecimal bestThm = bestCombo == null ? ZERO : bestCombo.diemToHop();
		BigDecimal bestDiemCong = bestCombo == null ? ZERO : bestCombo.diemCong();
		BigDecimal bestDiemUuTien = bestCombo == null ? ZERO : bestCombo.diemUuTien();
		BigDecimal bestDiemXetTuyen = bestCombo == null ? ZERO : bestCombo.diemXetTuyen();
		String bestNguon = bestCombo == null ? "" : bestCombo.maToHop();

		if (dgnlRecord != null && dgnlRecord.diemQuyDoi().compareTo(bestThm) > 0) {
			bestThm = dgnlRecord.diemQuyDoi();
			bestDiemCong = dgnlRecord.diemCong();
			bestDiemUuTien = dgnlRecord.diemUuTien();
			bestDiemXetTuyen = dgnlRecord.diemXetTuyen();
			bestNguon = "ĐGNL";
		}

		SummaryRecord summary = new SummaryRecord(
				nv.getIdnv(),
				cccd,
				hoTen,
				soBaoDanh,
				maNganh,
				tenNganh,
				thuTu,
				nguyenVongLabel,
				bestThm,
				bestDiemCong,
				bestDiemUuTien,
				bestDiemXetTuyen);

		return new ChiTietRecord(summary, toHopRecords, dgnlRecord, bestNguon, bestThm, vsatBest, allowDgnl);
	}

	private ToHopRecord buildToHopRecord(XtNganhTohop toHop, String maNganh, Map<String, BigDecimal> subjectScores,
			List<XtDiemcongxetuyen> diemCongRows, Set<String> phuongThucFilter) {
		String maToHop = safeText(toHop.getMatohop());
		String mon1 = normalizeMonCode(toHop.getThMon1());
		String mon2 = normalizeMonCode(toHop.getThMon2());
		String mon3 = normalizeMonCode(toHop.getThMon3());

		BigDecimal diem1 = scoreByMon(subjectScores, mon1);
		BigDecimal diem2 = scoreByMon(subjectScores, mon2);
		BigDecimal diem3 = scoreByMon(subjectScores, mon3);

		int hs1 = toHop.getHsmon1() == null ? 1 : toHop.getHsmon1();
		int hs2 = toHop.getHsmon2() == null ? 1 : toHop.getHsmon2();
		int hs3 = toHop.getHsmon3() == null ? 1 : toHop.getHsmon3();

		double tong = TinhDiemThxtUtil.tinhDTHXT(
				safeScore(diem1), hs1,
				safeScore(diem2), hs2,
				safeScore(diem3), hs3);
		BigDecimal diemToHop = toScore(tong);

		DiemCongInfo diemCong = findDiemCong(diemCongRows, maNganh, maToHop, phuongThucFilter);
		BigDecimal diemXetTuyen = diemToHop.add(diemCong.diemCong()).add(diemCong.diemUuTien());

		return new ToHopRecord(maToHop, mon1, diem1, hs1, mon2, diem2, hs2, mon3, diem3, hs3,
				diemToHop, diemCong.diemCong(), diemCong.diemUuTien(), diemXetTuyen);
	}

	private DgnlRecord buildDgnlRecord(XtDiemthixettuyen diemThpt, String maNganh, XtNganh nganh,
			List<XtNganhTohop> toHops,
			List<XtDiemcongxetuyen> diemCongRows) {
		if (diemThpt == null || diemThpt.getNl1() == null) {
			return null;
		}

		BigDecimal diemDgnl = diemThpt.getNl1();
		if (diemDgnl.compareTo(ZERO) <= 0) {
			return null;
		}

		String toHop = resolveToHopForDgnl(nganh, toHops);
		BigDecimal quyDoi = convertDgnl(diemDgnl, toHop);
		DiemCongInfo diemCong = findDiemCong(diemCongRows, maNganh, null,
				Set.of(PT_DGNL));
		BigDecimal diemXetTuyen = quyDoi.add(diemCong.diemCong()).add(diemCong.diemUuTien());

		return new DgnlRecord(diemDgnl, quyDoi, diemCong.diemCong(), diemCong.diemUuTien(), diemXetTuyen, toHop);
	}

	private Map<String, VsatSubjectRecord> buildVsatBest(List<XtDiemVsat> rows) {
		Map<String, VsatSubjectRecord> best = new HashMap<>();
		if (rows == null) {
			return best;
		}

		for (XtDiemVsat row : rows) {
			if (row == null) {
				continue;
			}
			mergeVsat(best, "TO", row.getToanVsat(), row.getDotThi());
			mergeVsat(best, "VA", row.getVanVsat(), row.getDotThi());
			mergeVsat(best, "N1", row.getAnhVsat(), row.getDotThi());
			mergeVsat(best, "LI", row.getLyVsat(), row.getDotThi());
			mergeVsat(best, "HO", row.getHoaVsat(), row.getDotThi());
			mergeVsat(best, "SI", row.getSinhVsat(), row.getDotThi());
			mergeVsat(best, "SU", row.getSuVsat(), row.getDotThi());
			mergeVsat(best, "DI", row.getDiaVsat(), row.getDotThi());
		}
		return best;
	}

	private void mergeVsat(Map<String, VsatSubjectRecord> best, String mon, BigDecimal diem, String dotThi) {
		if (diem == null) {
			return;
		}
		VsatSubjectRecord current = best.get(mon);
		if (current == null || diem.compareTo(current.diemGoc()) > 0) {
			BigDecimal quyDoi = convertVsat(diem, mon);
			best.put(mon, new VsatSubjectRecord(mon, diem, quyDoi, safeText(dotThi)));
		}
	}

	private Map<String, BigDecimal> buildSubjectScores(List<XtNganhTohop> toHops, XtDiemthixettuyen diemThpt,
			Map<String, VsatSubjectRecord> vsatBest, boolean allowThpt, boolean allowVsat) {
		Set<String> subjects = new LinkedHashSet<>();
		for (XtNganhTohop toHop : toHops) {
			String mon1 = normalizeMonCode(toHop.getThMon1());
			String mon2 = normalizeMonCode(toHop.getThMon2());
			String mon3 = normalizeMonCode(toHop.getThMon3());
			if (!mon1.isEmpty()) {
				subjects.add(mon1);
			}
			if (!mon2.isEmpty()) {
				subjects.add(mon2);
			}
			if (!mon3.isEmpty()) {
				subjects.add(mon3);
			}
		}

		Map<String, BigDecimal> subjectScores = new HashMap<>();
		for (String mon : subjects) {
			BigDecimal thpt = allowThpt ? getThptScore(diemThpt, mon) : null;
			BigDecimal vsat = null;
			if (allowVsat) {
				VsatSubjectRecord bestVsat = vsatBest.get(mon);
				if (bestVsat != null) {
					vsat = bestVsat.diemQuyDoi();
				}
			}
			subjectScores.put(mon, maxScore(thpt, vsat));
		}
		return subjectScores;
	}

	private Set<String> buildPhuongThucFilter(boolean allowThpt, boolean allowVsat) {
		if (allowThpt && allowVsat) {
			return PT_THPT_VSAT;
		}
		if (allowThpt) {
			return Set.of("PT4");
		}
		if (allowVsat) {
			return Set.of("PT3");
		}
		return Set.of();
	}

	private boolean allowByFlag(String value) {
		return !"N".equalsIgnoreCase(value == null ? "" : value.trim());
	}

	private BigDecimal getThptScore(XtDiemthixettuyen diem, String mon) {
		if (diem == null) {
			return null;
		}
		return switch (normalizeMonCode(mon)) {
			case "TO" -> diem.getTo();
			case "LI" -> diem.getLi();
			case "HO" -> diem.getHo();
			case "SI" -> diem.getSi();
			case "SU" -> diem.getSu();
			case "DI" -> diem.getDi();
			case "VA" -> diem.getVa();
			case "GDCD" -> diem.getGdcd();
			case "N1" -> diem.getN1Cc();
			case "CNCN" -> diem.getCncn();
			case "CNNN" -> diem.getCnnn();
			case "TI" -> diem.getTi();
			case "KTPL" -> diem.getKtpl();
			case "NK1" -> diem.getNk1();
			case "NK2" -> diem.getNk2();
			case "NK3" -> diem.getNk3();
			case "NK4" -> diem.getNk4();
			case "NK5" -> diem.getNk5();
			case "NK6" -> diem.getNk6();
			default -> null;
		};
	}

	private DiemCongInfo findDiemCong(List<XtDiemcongxetuyen> rows, String maNganh, String maToHop,
			Set<String> phuongThucFilter) {
		BigDecimal diemCong = ZERO;
		BigDecimal diemUuTien = ZERO;
		if (rows == null) {
			return new DiemCongInfo(diemCong, diemUuTien);
		}

		String manganhKey = safeText(maNganh).toLowerCase(Locale.ROOT);
		String toHopKey = safeText(maToHop).toLowerCase(Locale.ROOT);

		for (XtDiemcongxetuyen row : rows) {
			if (row == null) {
				continue;
			}
			String rowNganh = safeText(row.getManganh()).toLowerCase(Locale.ROOT);
			if (!manganhKey.isEmpty() && !manganhKey.equals(rowNganh)) {
				continue;
			}
			String rowToHop = safeText(row.getMatohop()).toLowerCase(Locale.ROOT);
			if (!toHopKey.isEmpty() && !rowToHop.isEmpty() && !toHopKey.equals(rowToHop)) {
				continue;
			}

			String phuongThuc = safeText(row.getPhuongthuc()).toUpperCase(Locale.ROOT);
			if (phuongThucFilter != null && !phuongThucFilter.contains(phuongThuc)) {
				continue;
			}

			diemCong = maxScore(diemCong, row.getDiemcc());
			diemUuTien = maxScore(diemUuTien, row.getDiemutxt());
		}
		return new DiemCongInfo(diemCong, diemUuTien);
	}

	private BigDecimal scoreByMon(Map<String, BigDecimal> subjectScores, String mon) {
		if (mon == null || subjectScores == null) {
			return ZERO;
		}
		return subjectScores.getOrDefault(mon, ZERO);
	}

	private BigDecimal maxScore(BigDecimal a, BigDecimal b) {
		BigDecimal left = a == null ? ZERO : a;
		BigDecimal right = b == null ? ZERO : b;
		return left.compareTo(right) >= 0 ? left : right;
	}

	private BigDecimal convertVsat(BigDecimal diem, String mon) {
		if (diem == null || mon == null || mon.isEmpty()) {
			return ZERO;
		}
		try {
			return QuyDoiDiemUtil.quyDoiVsat(diem.floatValue(), mon);
		} catch (Exception ex) {
			return ZERO;
		}
	}

	private BigDecimal convertDgnl(BigDecimal diem, String toHop) {
		if (diem == null || toHop == null || toHop.isEmpty()) {
			return ZERO;
		}
		try {
			return QuyDoiDiemUtil.quyDoiDgnl(diem.floatValue(), toHop);
		} catch (Exception ex) {
			return ZERO;
		}
	}

	private String resolveToHopForDgnl(XtNganh nganh, List<XtNganhTohop> toHops) {
		String toHop = nganh == null ? "" : safeText(nganh.getNTohopgoc());
		if (!toHop.isEmpty()) {
			return toHop;
		}
		if (toHops != null && !toHops.isEmpty()) {
			return safeText(toHops.get(0).getMatohop());
		}
		return "A00";
	}

	private double safeScore(BigDecimal value) {
		if (value == null) {
			return 0.0;
		}
		double v = value.doubleValue();
		if (v < 0) {
			return 0.0;
		}
		if (v > 10) {
			return 10.0;
		}
		return v;
	}

	private BigDecimal toScore(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
	}

	private String normalizeMonCode(String mon) {
		if (mon == null) {
			return "";
		}
		String code = mon.trim().toUpperCase(Locale.ROOT);
		return switch (code) {
			case "TOAN" -> "TO";
			case "VAN" -> "VA";
			case "ANH" -> "N1";
			case "N1_CC", "N1_THI" -> "N1";
			case "LY" -> "LI";
			case "HOA" -> "HO";
			case "SINH" -> "SI";
			case "DIA" -> "DI";
			case "TIN" -> "TI";
			default -> code;
		};
	}

	private String safeText(String value) {
		return value == null ? "" : value.trim();
	}

	private void ensureQuyDoiCache() {
		if (quyDoiInitialized) {
			return;
		}
		synchronized (DanhSachDiemXetTuyenService.class) {
			if (quyDoiInitialized) {
				return;
			}
			QuyDoiDiemUtil.init(bangQuyDoiDao.findAll());
			quyDoiInitialized = true;
		}
	}

	private class AggregationContext {
		private final Map<String, XtThisinhxettuyen25> thiSinhByCccd = new HashMap<>();
		private final Map<String, XtDiemthixettuyen> diemByCccd = new HashMap<>();
		private final Map<String, List<XtDiemVsat>> vsatByCccd = new HashMap<>();
		private final Map<String, XtNganh> nganhByMa = new HashMap<>();
		private final Map<String, List<XtNganhTohop>> toHopByNganh = new HashMap<>();
		private final Map<String, List<XtDiemcongxetuyen>> diemCongByCccd = new HashMap<>();

		XtThisinhxettuyen25 getThiSinh(String cccd) {
			if (cccd.isEmpty()) {
				return null;
			}
			return thiSinhByCccd.computeIfAbsent(cccd, thiSinhDao::findByCccd);
		}

		XtDiemthixettuyen getDiemThpt(String cccd) {
			if (cccd.isEmpty()) {
				return null;
			}
			return diemByCccd.computeIfAbsent(cccd, diemDao::findByCccd);
		}

		List<XtDiemVsat> getVsatRows(String cccd) {
			if (cccd.isEmpty()) {
				return List.of();
			}
			return vsatByCccd.computeIfAbsent(cccd, vsatDao::findByCccd);
		}

		XtNganh getNganh(String maNganh) {
			if (maNganh.isEmpty()) {
				return null;
			}
			return nganhByMa.computeIfAbsent(maNganh, nganhHocDao::findByMaNganh);
		}

		List<XtNganhTohop> getToHop(String maNganh) {
			if (maNganh.isEmpty()) {
				return List.of();
			}
			return toHopByNganh.computeIfAbsent(maNganh, nganhToHopDao::findByMaNganh);
		}

		List<XtDiemcongxetuyen> getDiemCongRows(String cccd) {
			if (cccd.isEmpty()) {
				return List.of();
			}
			return diemCongByCccd.computeIfAbsent(cccd, diemCongDao::findByCccd);
		}
	}

	public record SummaryRecord(
			int idnv,
			String cccd,
			String hoTen,
			String soBaoDanh,
			String maNganh,
			String tenNganh,
			int thuTuNguyenVong,
			String nguyenVong,
			BigDecimal diemToHopCaoNhat,
			BigDecimal diemCong,
			BigDecimal diemUuTien,
			BigDecimal diemXetTuyen) {
	}

	public record ToHopRecord(
			String maToHop,
			String mon1,
			BigDecimal diemMon1,
			int heSo1,
			String mon2,
			BigDecimal diemMon2,
			int heSo2,
			String mon3,
			BigDecimal diemMon3,
			int heSo3,
			BigDecimal diemToHop,
			BigDecimal diemCong,
			BigDecimal diemUuTien,
			BigDecimal diemXetTuyen) {
	}

	public record DgnlRecord(
			BigDecimal diemGoc,
			BigDecimal diemQuyDoi,
			BigDecimal diemCong,
			BigDecimal diemUuTien,
			BigDecimal diemXetTuyen,
			String toHopQuyDoi) {
	}

	public record VsatSubjectRecord(
			String mon,
			BigDecimal diemGoc,
			BigDecimal diemQuyDoi,
			String dotThi) {
	}

	public record ChiTietRecord(
			SummaryRecord summary,
			List<ToHopRecord> toHopRecords,
			DgnlRecord dgnlRecord,
			String nguonDiemToHopCaoNhat,
			BigDecimal diemToHopCaoNhat,
			Map<String, VsatSubjectRecord> vsatBest,
			boolean allowDgnl) {
		public ChiTietRecord {
			Objects.requireNonNull(summary, "summary");
			toHopRecords = toHopRecords == null ? List.of() : List.copyOf(toHopRecords);
			vsatBest = vsatBest == null ? Map.of() : Map.copyOf(vsatBest);
		}
	}

	private record DiemCongInfo(BigDecimal diemCong, BigDecimal diemUuTien) {
		private DiemCongInfo {
			diemCong = diemCong == null ? ZERO : diemCong;
			diemUuTien = diemUuTien == null ? ZERO : diemUuTien;
		}
	}
}
