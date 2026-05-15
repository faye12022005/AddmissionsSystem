package org.AdmissionsSystem.bus.service.QuanLiDiem;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.AdmissionsSystem.dao.QuanLiDiemDao;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.AdmissionsSystem.bus.service.ThiSinhService;

public class QuanLiDiemService {
	public static final String ALL_OPTION = "Tất cả";
	public static final List<String> LOAI_DIEM = List.of("THPT", "ĐGNL");

	private static final BigDecimal THPT_MAX = BigDecimal.TEN;
	private static final BigDecimal DGNL_MAX = BigDecimal.valueOf(1200);

	private static final String[] IMPORT_COLUMNS = {
			"cccd",
			"sobaodanh",
			"d_phuongthuc",
			"TO",
			"LI",
			"HO",
			"SI",
			"SU",
			"DI",
			"VA",
			"GDCD",
			"N1_THI",
			"N1_CC",
			"CNCN",
			"CNNN",
			"TI",
			"KTPL",
			"NL1",
			"NK1",
			"NK2",
			"NK3",
			"NK4",
			"NK5",
			"NK6"
	};

	private static final Map<String, String> IMPORT_ALIASES = buildImportAliases();
	private static final Map<String, String> PHUONG_THUC_BY_NORMALIZED = buildPhuongThucMap();
	private static final Map<String, String> LABEL_BY_CODE = buildLabelByCode();

	private final QuanLiDiemDao diemDao = new QuanLiDiemDao();
	private final ThiSinhService thiSinhService = new ThiSinhService();
	private final Map<String, String> hoTenCache = new HashMap<>();

	public List<DiemRecord> query(String searchText, String loaiDiem) {
		String normalizedSearch = normalizeText(searchText);
		String filterLoai = normalizeLoaiDiemFilter(loaiDiem);

		List<DiemRecord> rows = new ArrayList<>();
		for (XtDiemthixettuyen entity : diemDao.findAll()) {
			if (entity == null) {
				continue;
			}
			DiemRecord record = toRecord(entity);
			if (!matchesSearch(record, normalizedSearch)) {
				continue;
			}
			if (filterLoai != null && !filterLoai.equalsIgnoreCase(record.loaiDiem())) {
				continue;
			}
			rows.add(record);
		}

		rows.sort((a, b) -> Integer.compare(b.id(), a.id()));
		return rows;
	}

	public DiemRecord add(DiemInput input) {
		return upsert(input, false);
	}

	public Optional<DiemRecord> update(DiemRecord existing, DiemInput input) {
		if (existing == null) {
			return Optional.empty();
		}

		DiemInput sanitized = sanitizeAndValidate(input, false);
		XtDiemthixettuyen entity = diemDao.findById(existing.id());
		if (entity == null) {
			return Optional.empty();
		}

		applyInput(entity, sanitized);
		diemDao.update(entity);
		return Optional.of(toRecord(entity));
	}

	public boolean delete(DiemRecord existing) {
		if (existing == null) {
			return false;
		}
		XtDiemthixettuyen entity = diemDao.findById(existing.id());
		if (entity == null) {
			return false;
		}
		diemDao.delete(entity);
		return true;
	}

	public ImportPreview previewImport(List<Object[]> rows) {
		if (rows == null || rows.isEmpty()) {
			return new ImportPreview(List.of(), List.of());
		}

		List<DiemInput> validRows = new ArrayList<>();
		List<ImportError> errors = new ArrayList<>();

		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			int rowNumber = i + 2;

			String cccd = asText(rowValue(row, 0));
			String soBaoDanh = asText(rowValue(row, 1));
			String loaiRaw = asText(rowValue(row, 2));

			List<String> errorMessages = new ArrayList<>();
			if (isBlank(cccd)) {
				errorMessages.add("CCCD không được để trống");
			} else if (!cccd.matches("TS_\\d{4,}")) {
				errorMessages.add("CCCD phải theo định dạng TS_0001");
			}

			if (isBlank(soBaoDanh)) {
				errorMessages.add("Số báo danh không được để trống");
			}

			String loaiDiem = resolveLoaiDiemLabel(loaiRaw);
			if (loaiDiem == null) {
				errorMessages.add("Phương thức không hợp lệ");
			}

			BigDecimal to = parseScore(asText(rowValue(row, 3)), "Toán", THPT_MAX, errorMessages);
			BigDecimal li = parseScore(asText(rowValue(row, 4)), "Lý", THPT_MAX, errorMessages);
			BigDecimal ho = parseScore(asText(rowValue(row, 5)), "Hóa", THPT_MAX, errorMessages);
			BigDecimal si = parseScore(asText(rowValue(row, 6)), "Sinh", THPT_MAX, errorMessages);
			BigDecimal su = parseScore(asText(rowValue(row, 7)), "Sử", THPT_MAX, errorMessages);
			BigDecimal di = parseScore(asText(rowValue(row, 8)), "Địa", THPT_MAX, errorMessages);
			BigDecimal va = parseScore(asText(rowValue(row, 9)), "Văn", THPT_MAX, errorMessages);
			BigDecimal gdcd = parseScore(asText(rowValue(row, 10)), "GDCD", THPT_MAX, errorMessages);
			BigDecimal n1Thi = parseScore(asText(rowValue(row, 11)), "N1_THI", THPT_MAX, errorMessages);
			BigDecimal n1Cc = parseScore(asText(rowValue(row, 12)), "N1_CC", THPT_MAX, errorMessages);
			BigDecimal cncn = parseScore(asText(rowValue(row, 13)), "CNCN", THPT_MAX, errorMessages);
			BigDecimal cnnn = parseScore(asText(rowValue(row, 14)), "CNNN", THPT_MAX, errorMessages);
			BigDecimal ti = parseScore(asText(rowValue(row, 15)), "Tin học", THPT_MAX, errorMessages);
			BigDecimal ktpl = parseScore(asText(rowValue(row, 16)), "KTPL", THPT_MAX, errorMessages);
			BigDecimal nl1 = parseScore(asText(rowValue(row, 17)), "NL1", DGNL_MAX, errorMessages);
			BigDecimal nk1 = parseScore(asText(rowValue(row, 18)), "NK1", THPT_MAX, errorMessages);
			BigDecimal nk2 = parseScore(asText(rowValue(row, 19)), "NK2", THPT_MAX, errorMessages);
			BigDecimal nk3 = parseScore(asText(rowValue(row, 20)), "NK3", THPT_MAX, errorMessages);
			BigDecimal nk4 = parseScore(asText(rowValue(row, 21)), "NK4", THPT_MAX, errorMessages);
			BigDecimal nk5 = parseScore(asText(rowValue(row, 22)), "NK5", THPT_MAX, errorMessages);
			BigDecimal nk6 = parseScore(asText(rowValue(row, 23)), "NK6", THPT_MAX, errorMessages);

			if (errorMessages.isEmpty()) {
				validRows.add(new DiemInput(
						cccd,
						soBaoDanh,
						loaiDiem,
						to,
						li,
						ho,
						si,
						su,
						di,
						va,
						gdcd,
						n1Thi,
						n1Cc,
						cncn,
						cnnn,
						ti,
						ktpl,
						nl1,
						nk1,
						nk2,
						nk3,
						nk4,
						nk5,
						nk6));
			} else {
				String message = String.join("; ", errorMessages);
				errors.add(new ImportError(rowNumber, cccd, soBaoDanh, message));
			}
		}

		return new ImportPreview(validRows, errors);
	}

	public int importRows(List<DiemInput> inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return 0;
		}
		int imported = 0;
		for (DiemInput input : inputs) {
			upsert(input, true);
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

	private DiemRecord upsert(DiemInput input, boolean allowOverwrite) {
		DiemInput sanitized = sanitizeAndValidate(input, false);
		XtDiemthixettuyen entity = findByCandidate(sanitized.cccd(), sanitized.soBaoDanh());
		if (entity != null && !allowOverwrite) {
			throw new IllegalArgumentException("Thí sinh đã có điểm. Vui lòng dùng chức năng Sửa.");
		}

		if (entity == null) {
			entity = new XtDiemthixettuyen();
		}

		applyInput(entity, sanitized);
		if (entity.getIddiemthi() == null) {
			diemDao.save(entity);
		} else {
			diemDao.update(entity);
		}
		return toRecord(entity);
	}

	private void applyInput(XtDiemthixettuyen entity, DiemInput input) {
		String phuongThucCode = resolvePhuongThucCode(input.loaiDiem());
		if (phuongThucCode == null) {
			throw new IllegalArgumentException("Phương thức không hợp lệ.");
		}

		entity.setCccd(input.cccd());
		entity.setSobaodanh(input.soBaoDanh());
		entity.setDPhuongthuc(phuongThucCode);
		entity.setTo(input.to());
		entity.setLi(input.li());
		entity.setHo(input.ho());
		entity.setSi(input.si());
		entity.setSu(input.su());
		entity.setDi(input.di());
		entity.setVa(input.va());
		entity.setGdcd(input.gdcd());
		entity.setN1Thi(input.n1Thi());
		entity.setN1Cc(input.n1Cc());
		entity.setCncn(input.cncn());
		entity.setCnnn(input.cnnn());
		entity.setTi(input.ti());
		entity.setKtpl(input.ktpl());
		entity.setNl1(input.nl1());
		entity.setNk1(input.nk1());
		entity.setNk2(input.nk2());
		entity.setNk3(input.nk3());
		entity.setNk4(input.nk4());
		entity.setNk5(input.nk5());
		entity.setNk6(input.nk6());
	}

	private DiemInput sanitizeAndValidate(DiemInput input, boolean allowEmptyScores) {
		Objects.requireNonNull(input, "Dữ liệu điểm không được để trống.");

		String cccd = safeText(input.cccd());
		String soBaoDanh = safeText(input.soBaoDanh());
		String loaiDiem = resolveLoaiDiemLabel(input.loaiDiem());

		if (cccd.isBlank()) {
			throw new IllegalArgumentException("CCCD không được để trống.");
		}
		if (!cccd.matches("TS_\\d{4,}")) {
			throw new IllegalArgumentException("CCCD phải theo định dạng TS_0001.");
		}
		if (soBaoDanh.isBlank()) {
			throw new IllegalArgumentException("Số báo danh không được để trống.");
		}
		if (loaiDiem == null) {
			throw new IllegalArgumentException("Phương thức không hợp lệ.");
		}

		List<String> errors = new ArrayList<>();
		BigDecimal to = validateScore(input.to(), "Toán", THPT_MAX, errors);
		BigDecimal li = validateScore(input.li(), "Lý", THPT_MAX, errors);
		BigDecimal ho = validateScore(input.ho(), "Hóa", THPT_MAX, errors);
		BigDecimal si = validateScore(input.si(), "Sinh", THPT_MAX, errors);
		BigDecimal su = validateScore(input.su(), "Sử", THPT_MAX, errors);
		BigDecimal di = validateScore(input.di(), "Địa", THPT_MAX, errors);
		BigDecimal va = validateScore(input.va(), "Văn", THPT_MAX, errors);
		BigDecimal gdcd = validateScore(input.gdcd(), "GDCD", THPT_MAX, errors);
		BigDecimal n1Thi = validateScore(input.n1Thi(), "N1_THI", THPT_MAX, errors);
		BigDecimal n1Cc = validateScore(input.n1Cc(), "N1_CC", THPT_MAX, errors);
		BigDecimal cncn = validateScore(input.cncn(), "CNCN", THPT_MAX, errors);
		BigDecimal cnnn = validateScore(input.cnnn(), "CNNN", THPT_MAX, errors);
		BigDecimal ti = validateScore(input.ti(), "Tin học", THPT_MAX, errors);
		BigDecimal ktpl = validateScore(input.ktpl(), "KTPL", THPT_MAX, errors);
		BigDecimal nl1 = validateScore(input.nl1(), "NL1", DGNL_MAX, errors);
		BigDecimal nk1 = validateScore(input.nk1(), "NK1", THPT_MAX, errors);
		BigDecimal nk2 = validateScore(input.nk2(), "NK2", THPT_MAX, errors);
		BigDecimal nk3 = validateScore(input.nk3(), "NK3", THPT_MAX, errors);
		BigDecimal nk4 = validateScore(input.nk4(), "NK4", THPT_MAX, errors);
		BigDecimal nk5 = validateScore(input.nk5(), "NK5", THPT_MAX, errors);
		BigDecimal nk6 = validateScore(input.nk6(), "NK6", THPT_MAX, errors);

		if (!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("; ", errors));
		}

		if (!allowEmptyScores
				&& to == null
				&& li == null
				&& ho == null
				&& si == null
				&& su == null
				&& di == null
				&& va == null
				&& gdcd == null
				&& n1Thi == null
				&& n1Cc == null
				&& cncn == null
				&& cnnn == null
				&& ti == null
				&& ktpl == null
				&& nl1 == null
				&& nk1 == null
				&& nk2 == null
				&& nk3 == null
				&& nk4 == null
				&& nk5 == null
				&& nk6 == null) {
			throw new IllegalArgumentException("Cần nhập ít nhất một điểm môn.");
		}

		return new DiemInput(
				cccd,
				soBaoDanh,
				loaiDiem,
				to,
				li,
				ho,
				si,
				su,
				di,
				va,
				gdcd,
				n1Thi,
				n1Cc,
				cncn,
				cnnn,
				ti,
				ktpl,
				nl1,
				nk1,
				nk2,
				nk3,
				nk4,
				nk5,
				nk6);
	}

	private BigDecimal validateScore(BigDecimal score, String label, BigDecimal max, List<String> errors) {
		if (score == null) {
			return null;
		}
		if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(max) > 0) {
			errors.add("Điểm " + label + " phải nằm trong khoảng từ 0 đến " + max.stripTrailingZeros());
		}
		return score;
	}

	private BigDecimal parseScore(String rawScore, String label, BigDecimal max, List<String> errors) {
		if (isBlank(rawScore)) {
			return null;
		}
		String normalized = rawScore.trim().replace(',', '.');
		try {
			BigDecimal score = new BigDecimal(normalized);
			if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(max) > 0) {
				errors.add("Điểm " + label + " phải nằm trong khoảng từ 0 đến " + max.stripTrailingZeros());
				return null;
			}
			return score;
		} catch (NumberFormatException ex) {
			errors.add("Điểm " + label + " không hợp lệ");
			return null;
		}
	}

	private DiemRecord toRecord(XtDiemthixettuyen entity) {
		String cccd = safeText(entity.getCccd());
		String soBaoDanh = safeText(entity.getSobaodanh());
		String hoTen = resolveHoTen(cccd, soBaoDanh);
		String loaiDiem = displayLoaiDiem(entity.getDPhuongthuc());

		return new DiemRecord(
				entity.getIddiemthi(),
				cccd,
				soBaoDanh,
				hoTen,
				loaiDiem,
				entity.getTo(),
				entity.getLi(),
				entity.getHo(),
				entity.getSi(),
				entity.getSu(),
				entity.getDi(),
				entity.getVa(),
				entity.getGdcd(),
				entity.getN1Thi(),
				entity.getN1Cc(),
				entity.getCncn(),
				entity.getCnnn(),
				entity.getTi(),
				entity.getKtpl(),
				entity.getNl1(),
				entity.getNk1(),
				entity.getNk2(),
				entity.getNk3(),
				entity.getNk4(),
				entity.getNk5(),
				entity.getNk6());
	}

	private XtDiemthixettuyen findByCandidate(String cccd, String soBaoDanh) {
		XtDiemthixettuyen entity = diemDao.findByCccd(cccd);
		if (entity == null && !isBlank(soBaoDanh)) {
			entity = diemDao.findBySoBaoDanh(soBaoDanh);
		}
		return entity;
	}

	private String resolveHoTen(String cccd, String soBaoDanh) {
		String key = safeText(cccd) + "|" + safeText(soBaoDanh);
		if (hoTenCache.containsKey(key)) {
			return hoTenCache.get(key);
		}
		String hoTen = thiSinhService.resolveHoTen(cccd, soBaoDanh);
		hoTenCache.put(key, hoTen);
		return hoTen;
	}

	private String displayLoaiDiem(String raw) {
		if (raw == null) {
			return "";
		}
		String normalized = normalizeKey(raw);
		String code = PHUONG_THUC_BY_NORMALIZED.getOrDefault(normalized, raw);
		return LABEL_BY_CODE.getOrDefault(code, raw);
	}

	private String resolvePhuongThucCode(String raw) {
		if (raw == null) {
			return null;
		}
		String normalized = normalizeKey(raw);
		return PHUONG_THUC_BY_NORMALIZED.get(normalized);
	}

	private String resolveLoaiDiemLabel(String raw) {
		if (raw == null) {
			return null;
		}
		String normalized = normalizeKey(raw);
		String code = PHUONG_THUC_BY_NORMALIZED.get(normalized);
		return code == null ? null : LABEL_BY_CODE.getOrDefault(code, raw);
	}

	private String normalizeLoaiDiemFilter(String loaiDiem) {
		if (loaiDiem == null || loaiDiem.isBlank() || ALL_OPTION.equalsIgnoreCase(loaiDiem)) {
			return null;
		}
		return resolveLoaiDiemLabel(loaiDiem);
	}

	private boolean matchesSearch(DiemRecord record, String normalizedSearch) {
		if (normalizedSearch.isBlank()) {
			return true;
		}
		return normalizeText(record.cccd()).contains(normalizedSearch)
				|| normalizeText(record.soBaoDanh()).contains(normalizedSearch)
				|| normalizeText(record.hoTen()).contains(normalizedSearch)
				|| normalizeText(record.loaiDiem()).contains(normalizedSearch);
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

	private String normalizeText(String value) {
		String source = value == null ? "" : value;
		return Normalizer.normalize(source, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replace('đ', 'd')
				.replace('Đ', 'D')
				.toLowerCase(Locale.ROOT)
				.trim();
	}

	private String normalizeKey(String value) {
		String normalized = normalizeText(value);
		return normalized.replace("_", "").replace("-", "").replace(" ", "");
	}

	private static Map<String, String> buildImportAliases() {
		Map<String, String> map = new HashMap<>();
		map.put("can cuoc", "cccd");
		map.put("can cuoc cong dan", "cccd");
		map.put("so cccd", "cccd");
		map.put("sbd", "sobaodanh");
		map.put("so bao danh", "sobaodanh");
		map.put("phuong thuc", "d_phuongthuc");
		map.put("loai diem", "d_phuongthuc");
		map.put("toan", "TO");
		map.put("ly", "LI");
		map.put("hoa", "HO");
		map.put("sinh", "SI");
		map.put("su", "SU");
		map.put("dia", "DI");
		map.put("van", "VA");
		map.put("gdcd", "GDCD");
		map.put("giao duc cong dan", "GDCD");
		map.put("ngoai ngu thi", "N1_THI");
		map.put("n1 thi", "N1_THI");
		map.put("ngoai ngu cc", "N1_CC");
		map.put("n1 cc", "N1_CC");
		map.put("cncn", "CNCN");
		map.put("cong nghe cong nghiep", "CNCN");
		map.put("cnnn", "CNNN");
		map.put("cong nghe nong nghiep", "CNNN");
		map.put("tin hoc", "TI");
		map.put("ktpl", "KTPL");
		map.put("kinh te phap luat", "KTPL");
		map.put("nl1", "NL1");
		map.put("nang luc 1", "NL1");
		map.put("nk1", "NK1");
		map.put("nk2", "NK2");
		map.put("nk3", "NK3");
		map.put("nk4", "NK4");
		map.put("nk5", "NK5");
		map.put("nk6", "NK6");
		map.put("nang khieu 1", "NK1");
		map.put("nang khieu 2", "NK2");
		map.put("nang khieu 3", "NK3");
		map.put("nang khieu 4", "NK4");
		map.put("nang khieu 5", "NK5");
		map.put("nang khieu 6", "NK6");
		return Collections.unmodifiableMap(map);
	}

	private static Map<String, String> buildPhuongThucMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("xtt", "1");
		map.put("xettuyenthang", "1");
		map.put("dgnl", "2");
		map.put("dgnhcm", "2");
		map.put("vsat", "3");
		map.put("thpt", "4");
		return Collections.unmodifiableMap(map);
	}

	private static Map<String, String> buildLabelByCode() {
		Map<String, String> map = new HashMap<>();
		map.put("1", "Xét tuyển thẳng");
		map.put("2", "ĐGNL");
		map.put("3", "VSAT");
		map.put("4", "THPT");
		return Collections.unmodifiableMap(map);
	}

	public record DiemRecord(
			int id,
			String cccd,
			String soBaoDanh,
			String hoTen,
			String loaiDiem,
			BigDecimal to,
			BigDecimal li,
			BigDecimal ho,
			BigDecimal si,
			BigDecimal su,
			BigDecimal di,
			BigDecimal va,
			BigDecimal gdcd,
			BigDecimal n1Thi,
			BigDecimal n1Cc,
			BigDecimal cncn,
			BigDecimal cnnn,
			BigDecimal ti,
			BigDecimal ktpl,
			BigDecimal nl1,
			BigDecimal nk1,
			BigDecimal nk2,
			BigDecimal nk3,
			BigDecimal nk4,
			BigDecimal nk5,
			BigDecimal nk6) {
	}

	public record DiemInput(
			String cccd,
			String soBaoDanh,
			String loaiDiem,
			BigDecimal to,
			BigDecimal li,
			BigDecimal ho,
			BigDecimal si,
			BigDecimal su,
			BigDecimal di,
			BigDecimal va,
			BigDecimal gdcd,
			BigDecimal n1Thi,
			BigDecimal n1Cc,
			BigDecimal cncn,
			BigDecimal cnnn,
			BigDecimal ti,
			BigDecimal ktpl,
			BigDecimal nl1,
			BigDecimal nk1,
			BigDecimal nk2,
			BigDecimal nk3,
			BigDecimal nk4,
			BigDecimal nk5,
			BigDecimal nk6) {
	}

	public record ImportError(
			int rowNumber,
			String cccd,
			String soBaoDanh,
			String message) {
	}

	public record ImportPreview(
			List<DiemInput> validRows,
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
