package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiemService {
	public static final String ALL_OPTION = "Tất cả";
	public static final List<String> LOAI_DIEM = List.of("THPT", "VSAT", "ĐGNL");
	public static final List<String> MON_HOC = List.of(
			"Toán",
			"Ngữ văn",
			"Vật lý",
			"Hóa học",
			"Sinh học",
			"Lịch sử",
			"Địa lý",
			"Tiếng Anh",
			"Năng lực tổng hợp",
			"Năng khiếu 1",
			"Năng khiếu 2");

	private final List<DiemRecord> records = new ArrayList<>();
	private int nextId = 1;

	public DiemService() {
		importRows(MockDiemData.createSeedData());
	}

	public List<DiemRecord> getAll() {
		return sortByNewestFirst(records);
	}

	public List<DiemRecord> query(String searchText, String loaiDiem, String mon) {
		String normalizedSearch = normalize(searchText);
		String selectedLoaiDiem = normalizeFilter(loaiDiem);
		String selectedMon = normalizeFilter(mon);

		return records.stream()
				.filter(r -> matchesSearch(r, normalizedSearch))
				.filter(r -> selectedLoaiDiem == null || selectedLoaiDiem.equalsIgnoreCase(r.loaiDiem()))
				.filter(r -> selectedMon == null || selectedMon.equalsIgnoreCase(r.mon()))
				.sorted((a, b) -> Integer.compare(b.id(), a.id()))
				.toList();
	}

	public Optional<DiemRecord> findById(int id) {
		return records.stream().filter(r -> r.id() == id).findFirst();
	}

	public DiemRecord add(DiemRecordInput input) {
		DiemRecordInput sanitized = sanitizeAndValidate(input);
		DiemRecord created = new DiemRecord(
				nextId++,
				sanitized.cccd(),
				sanitized.soBaoDanh(),
				sanitized.hoTen(),
				sanitized.loaiDiem(),
				sanitized.mon(),
				sanitized.diem());
		records.add(created);
		return created;
	}

	public Optional<DiemRecord> update(int id, DiemRecordInput input) {
		DiemRecordInput sanitized = sanitizeAndValidate(input);

		for (int i = 0; i < records.size(); i++) {
			DiemRecord current = records.get(i);
			if (current.id() == id) {
				DiemRecord updated = new DiemRecord(
						id,
						sanitized.cccd(),
						sanitized.soBaoDanh(),
						sanitized.hoTen(),
						sanitized.loaiDiem(),
						sanitized.mon(),
						sanitized.diem());
				records.set(i, updated);
				return Optional.of(updated);
			}
		}

		return Optional.empty();
	}

	public boolean delete(int id) {
		return records.removeIf(r -> r.id() == id);
	}

	public int importRows(Collection<DiemRecordInput> inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return 0;
		}

		int imported = 0;
		for (DiemRecordInput input : inputs) {
			add(input);
			imported++;
		}
		return imported;
	}

	public Map<String, Double> averageByLoaiDiem(List<DiemRecord> source) {
		List<DiemRecord> effectiveSource = source == null ? Collections.emptyList() : source;
		Map<String, Double> averages = averageBy(effectiveSource, DiemRecord::loaiDiem);

		LinkedHashMap<String, Double> ordered = new LinkedHashMap<>();
		for (String loai : LOAI_DIEM) {
			if (averages.containsKey(loai)) {
				ordered.put(loai, averages.get(loai));
			}
		}
		return ordered;
	}

	public Map<String, Double> averageByMon(List<DiemRecord> source) {
		List<DiemRecord> effectiveSource = source == null ? Collections.emptyList() : source;
		Map<String, Double> averages = averageBy(effectiveSource, DiemRecord::mon);

		LinkedHashMap<String, Double> ordered = new LinkedHashMap<>();
		for (String mon : MON_HOC) {
			if (averages.containsKey(mon)) {
				ordered.put(mon, averages.get(mon));
			}
		}
		return ordered;
	}

	private Map<String, Double> averageBy(List<DiemRecord> source,
			java.util.function.Function<DiemRecord, String> classifier) {
		return source.stream()
				.collect(Collectors.groupingBy(classifier, LinkedHashMap::new,
						Collectors.averagingDouble(DiemRecord::diem)));
	}

	private List<DiemRecord> sortByNewestFirst(List<DiemRecord> source) {
		return source.stream()
				.sorted((a, b) -> Integer.compare(b.id(), a.id()))
				.toList();
	}

	private boolean matchesSearch(DiemRecord r, String normalizedSearch) {
		if (normalizedSearch.isBlank()) {
			return true;
		}
		return normalize(r.cccd()).contains(normalizedSearch)
				|| normalize(r.soBaoDanh()).contains(normalizedSearch)
				|| normalize(r.hoTen()).contains(normalizedSearch)
				|| normalize(r.loaiDiem()).contains(normalizedSearch)
				|| normalize(r.mon()).contains(normalizedSearch);
	}

	private String normalizeFilter(String filterValue) {
		if (filterValue == null || filterValue.isBlank() || ALL_OPTION.equalsIgnoreCase(filterValue)) {
			return null;
		}
		return filterValue.trim();
	}

	private DiemRecordInput sanitizeAndValidate(DiemRecordInput input) {
		Objects.requireNonNull(input, "Dữ liệu điểm không được để trống.");

		String cccd = safeTrim(input.cccd());
		String soBaoDanh = safeTrim(input.soBaoDanh());
		String hoTen = safeTrim(input.hoTen());
		String loaiDiem = safeTrim(input.loaiDiem());
		String mon = safeTrim(input.mon());
		double diem = input.diem();

		if (cccd.isBlank()) {
			throw new IllegalArgumentException("CCCD không được để trống.");
		}
		if (!cccd.matches("\\d{9,12}")) {
			throw new IllegalArgumentException("CCCD phải có 9-12 chữ số.");
		}
		if (soBaoDanh.isBlank()) {
			throw new IllegalArgumentException("Số báo danh không được để trống.");
		}
		if (hoTen.isBlank()) {
			throw new IllegalArgumentException("Họ tên không được để trống.");
		}
		if (!LOAI_DIEM.contains(loaiDiem)) {
			throw new IllegalArgumentException("Loại điểm không hợp lệ.");
		}
		if (!MON_HOC.contains(mon)) {
			throw new IllegalArgumentException("Môn không hợp lệ.");
		}
		if (Double.isNaN(diem) || diem < 0 || diem > 1200) {
			throw new IllegalArgumentException("Điểm phải nằm trong khoảng từ 0 đến 1200.");
		}

		return new DiemRecordInput(cccd, soBaoDanh, hoTen, loaiDiem, mon, diem);
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}

	private String normalize(String value) {
		String source = value == null ? "" : value;
		String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replace('đ', 'd')
				.replace('Đ', 'D')
				.toLowerCase(Locale.ROOT);
		return normalized.trim();
	}

	public record DiemRecord(
			int id,
			String cccd,
			String soBaoDanh,
			String hoTen,
			String loaiDiem,
			String mon,
			double diem) {
	}

	public record DiemRecordInput(
			String cccd,
			String soBaoDanh,
			String hoTen,
			String loaiDiem,
			String mon,
			double diem) {
	}
}
