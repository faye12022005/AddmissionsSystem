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
import org.AdmissionsSystem.dao.QuanLiDiemVsatDao;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.AdmissionsSystem.bus.service.ThiSinhService;

public class QuanLiDiemVSATService {
	private static final String[] IMPORT_COLUMNS = {
			"cccd",
			"dot_thi",
			"toan_vsat",
			"van_vsat",
			"anh_vsat",
			"ly_vsat",
			"hoa_vsat",
			"sinh_vsat",
			"su_vsat",
			"dia_vsat"
	};

	private static final Map<String, String> IMPORT_ALIASES = buildImportAliases();

	private final QuanLiDiemVsatDao vsatDao = new QuanLiDiemVsatDao();
	private final ThiSinhService thiSinhService = new ThiSinhService();
	private final Map<String, String> hoTenCache = new HashMap<>();

	public List<VsatRecord> query(String searchText) {
		String normalizedSearch = normalizeText(searchText);
		return vsatDao.findAll().stream()
				.map(this::toRecord)
				.filter(record -> matchesSearch(record, normalizedSearch))
				.sorted((a, b) -> Integer.compare(b.id(), a.id()))
				.toList();
	}

	public VsatRecord add(VsatInput input) {
		return upsert(input, false);
	}

	public Optional<VsatRecord> update(VsatRecord existing, VsatInput input) {
		if (existing == null) {
			return Optional.empty();
		}

		VsatInput sanitized = sanitizeAndValidate(input, true);
		XtDiemVsat entity = vsatDao.findById(existing.id());
		if (entity == null) {
			return Optional.empty();
		}

		applyInput(entity, sanitized);
		vsatDao.update(entity);
		return Optional.of(toRecord(entity));
	}

	public boolean delete(VsatRecord existing) {
		if (existing == null) {
			return false;
		}
		XtDiemVsat entity = vsatDao.findById(existing.id());
		if (entity == null) {
			return false;
		}
		vsatDao.delete(entity);
		return true;
	}

	public ImportPreview previewImport(List<Object[]> rows) {
		if (rows == null || rows.isEmpty()) {
			return new ImportPreview(List.of(), List.of());
		}

		List<VsatInput> validRows = new ArrayList<>();
		List<ImportError> errors = new ArrayList<>();

		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			int rowNumber = i + 2;

			String cccd = asText(rowValue(row, 0));
			String dotThi = asText(rowValue(row, 1));
			String toanRaw = asText(rowValue(row, 2));
			String vanRaw = asText(rowValue(row, 3));
			String anhRaw = asText(rowValue(row, 4));
			String lyRaw = asText(rowValue(row, 5));
			String hoaRaw = asText(rowValue(row, 6));
			String sinhRaw = asText(rowValue(row, 7));
			String suRaw = asText(rowValue(row, 8));
			String diaRaw = asText(rowValue(row, 9));

			List<String> errorMessages = new ArrayList<>();
			if (isBlank(cccd)) {
				errorMessages.add("CCCD không được để trống");
			} else if (!cccd.matches("TS_\\d{4,}")) {
				errorMessages.add("CCCD phải theo định dạng TS_0001");
			}
			if (isBlank(dotThi)) {
				errorMessages.add("Đợt thi không được để trống");
			}

			BigDecimal toan = parseScore(toanRaw, "Toán", errorMessages);
			BigDecimal van = parseScore(vanRaw, "Văn", errorMessages);
			BigDecimal anh = parseScore(anhRaw, "Anh", errorMessages);
			BigDecimal ly = parseScore(lyRaw, "Lý", errorMessages);
			BigDecimal hoa = parseScore(hoaRaw, "Hóa", errorMessages);
			BigDecimal sinh = parseScore(sinhRaw, "Sinh", errorMessages);
			BigDecimal su = parseScore(suRaw, "Sử", errorMessages);
			BigDecimal dia = parseScore(diaRaw, "Địa", errorMessages);

			if (errorMessages.isEmpty()) {
				validRows.add(new VsatInput(cccd, dotThi, toan, van, anh, ly, hoa, sinh, su, dia));
			} else {
				String message = String.join("; ", errorMessages);
				errors.add(new ImportError(rowNumber, cccd, dotThi, message));
			}
		}

		return new ImportPreview(validRows, errors);
	}

	public int importRows(List<VsatInput> inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return 0;
		}
		int imported = 0;
		for (VsatInput input : inputs) {
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

	private VsatRecord upsert(VsatInput input, boolean allowOverwrite) {
		VsatInput sanitized = sanitizeAndValidate(input, false);
		XtDiemVsat entity = vsatDao.findByCccdAndDotThi(sanitized.cccd(), sanitized.dotThi());
		if (entity != null && !allowOverwrite) {
			throw new IllegalArgumentException("Dữ liệu VSAT đã tồn tại cho CCCD và đợt thi này.");
		}

		if (entity == null) {
			entity = new XtDiemVsat();
		}

		applyInput(entity, sanitized);
		if (entity.getIdVsat() == null) {
			vsatDao.save(entity);
		} else {
			vsatDao.update(entity);
		}
		return toRecord(entity);
	}

	private void applyInput(XtDiemVsat entity, VsatInput input) {
		entity.setCccd(input.cccd());
		entity.setDotThi(input.dotThi());
		entity.setToanVsat(input.toan());
		entity.setVanVsat(input.van());
		entity.setAnhVsat(input.anh());
		entity.setLyVsat(input.ly());
		entity.setHoaVsat(input.hoa());
		entity.setSinhVsat(input.sinh());
		entity.setSuVsat(input.su());
		entity.setDiaVsat(input.dia());
	}

	private VsatInput sanitizeAndValidate(VsatInput input, boolean allowEmptyScores) {
		Objects.requireNonNull(input, "Dữ liệu VSAT không được để trống.");

		String cccd = safeText(input.cccd());
		String dotThi = safeText(input.dotThi());

		if (cccd.isBlank()) {
			throw new IllegalArgumentException("CCCD không được để trống.");
		}
		if (!cccd.matches("TS_\\d{4,}")) {
			throw new IllegalArgumentException("CCCD phải theo định dạng TS_0001.");
		}
		if (dotThi.isBlank()) {
			throw new IllegalArgumentException("Đợt thi không được để trống.");
		}

		List<String> errors = new ArrayList<>();
		BigDecimal toan = validateScore(input.toan(), "Toán", errors);
		BigDecimal van = validateScore(input.van(), "Văn", errors);
		BigDecimal anh = validateScore(input.anh(), "Anh", errors);
		BigDecimal ly = validateScore(input.ly(), "Lý", errors);
		BigDecimal hoa = validateScore(input.hoa(), "Hóa", errors);
		BigDecimal sinh = validateScore(input.sinh(), "Sinh", errors);
		BigDecimal su = validateScore(input.su(), "Sử", errors);
		BigDecimal dia = validateScore(input.dia(), "Địa", errors);

		if (!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("; ", errors));
		}

		if (!allowEmptyScores
				&& toan == null
				&& van == null
				&& anh == null
				&& ly == null
				&& hoa == null
				&& sinh == null
				&& su == null
				&& dia == null) {
			throw new IllegalArgumentException("Cần nhập ít nhất một điểm môn.");
		}

		return new VsatInput(cccd, dotThi, toan, van, anh, ly, hoa, sinh, su, dia);
	}

	private BigDecimal validateScore(BigDecimal score, String label, List<String> errors) {
		if (score == null) {
			return null;
		}
		if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(150)) > 0) {
			errors.add("Điểm " + label + " phải nằm trong khoảng từ 0 đến 150");
		}
		return score;
	}

	private BigDecimal parseScore(String rawScore, String label, List<String> errors) {
		if (isBlank(rawScore)) {
			return null;
		}
		String normalized = rawScore.trim().replace(',', '.');
		try {
			BigDecimal score = new BigDecimal(normalized);
			if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(150)) > 0) {
				errors.add("Điểm " + label + " phải nằm trong khoảng từ 0 đến 150");
				return null;
			}
			return score;
		} catch (NumberFormatException ex) {
			errors.add("Điểm " + label + " không hợp lệ");
			return null;
		}
	}

	private VsatRecord toRecord(XtDiemVsat entity) {
		String cccd = safeText(entity.getCccd());
		String dotThi = safeText(entity.getDotThi());
		String hoTen = resolveHoTen(cccd);
		return new VsatRecord(
				entity.getIdVsat(),
				cccd,
				hoTen,
				dotThi,
				entity.getToanVsat(),
				entity.getVanVsat(),
				entity.getAnhVsat(),
				entity.getLyVsat(),
				entity.getHoaVsat(),
				entity.getSinhVsat(),
				entity.getSuVsat(),
				entity.getDiaVsat());
	}

	private String resolveHoTen(String cccd) {
		String key = safeText(cccd);
		if (hoTenCache.containsKey(key)) {
			return hoTenCache.get(key);
		}
		String hoTen = thiSinhService.resolveHoTen(cccd, "");
		hoTenCache.put(key, hoTen);
		return hoTen;
	}

	private boolean matchesSearch(VsatRecord record, String normalizedSearch) {
		if (normalizedSearch.isBlank()) {
			return true;
		}
		return normalizeText(record.cccd()).contains(normalizedSearch)
				|| normalizeText(record.hoTen()).contains(normalizedSearch)
				|| normalizeText(record.dotThi()).contains(normalizedSearch);
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

	private static Map<String, String> buildImportAliases() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("can cuoc", "cccd");
		map.put("can cuoc cong dan", "cccd");
		map.put("so cccd", "cccd");
		map.put("dot thi", "dot_thi");
		map.put("dot", "dot_thi");
		map.put("toan", "toan_vsat");
		map.put("toan vsat", "toan_vsat");
		map.put("van", "van_vsat");
		map.put("van vsat", "van_vsat");
		map.put("anh", "anh_vsat");
		map.put("anh vsat", "anh_vsat");
		map.put("ly", "ly_vsat");
		map.put("ly vsat", "ly_vsat");
		map.put("hoa", "hoa_vsat");
		map.put("hoa vsat", "hoa_vsat");
		map.put("sinh", "sinh_vsat");
		map.put("sinh vsat", "sinh_vsat");
		map.put("su", "su_vsat");
		map.put("su vsat", "su_vsat");
		map.put("dia", "dia_vsat");
		map.put("dia vsat", "dia_vsat");
		return Collections.unmodifiableMap(map);
	}

	public record VsatRecord(
			int id,
			String cccd,
			String hoTen,
			String dotThi,
			BigDecimal toan,
			BigDecimal van,
			BigDecimal anh,
			BigDecimal ly,
			BigDecimal hoa,
			BigDecimal sinh,
			BigDecimal su,
			BigDecimal dia) {
	}

	public record VsatInput(
			String cccd,
			String dotThi,
			BigDecimal toan,
			BigDecimal van,
			BigDecimal anh,
			BigDecimal ly,
			BigDecimal hoa,
			BigDecimal sinh,
			BigDecimal su,
			BigDecimal dia) {
	}

	public record ImportError(
			int rowNumber,
			String cccd,
			String dotThi,
			String message) {
	}

	public record ImportPreview(
			List<VsatInput> validRows,
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
