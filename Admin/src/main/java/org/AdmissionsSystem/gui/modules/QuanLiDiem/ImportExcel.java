package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ImportExcel {
	private static final String COL_CCCD = "cccd";
	private static final String COL_SBD = "sobaodanh";
	private static final String COL_HO_TEN = "hoten";
	private static final String COL_LOAI_DIEM = "loaidiem";
	private static final String COL_MON = "mon";
	private static final String COL_DIEM = "diem";

	private static final List<String> REQUIRED_COLUMNS = List.of(
			COL_CCCD,
			COL_SBD,
			COL_HO_TEN,
			COL_LOAI_DIEM,
			COL_MON,
			COL_DIEM);

	public List<DiemService.DiemRecordInput> chooseAndRead(Component parent) throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Chọn file Excel điểm thí sinh");
		chooser.setFileFilter(new FileNameExtensionFilter("Excel/CSV (*.xlsx, *.xls, *.csv)", "xlsx", "xls", "csv"));

		int result = chooser.showOpenDialog(parent);
		if (result != JFileChooser.APPROVE_OPTION) {
			return List.of();
		}

		File selectedFile = chooser.getSelectedFile();
		String ext = extensionOf(selectedFile.getName());

		if ("csv".equals(ext)) {
			return readCsvFile(selectedFile);
		}
		if ("xlsx".equals(ext) || "xls".equals(ext)) {
			return readExcelFile(selectedFile);
		}

		throw new IllegalArgumentException("Định dạng file không hỗ trợ. Vui lòng chọn file .xlsx, .xls hoặc .csv.");
	}

	private List<DiemService.DiemRecordInput> readExcelFile(File file) throws IOException {
		try (Workbook workbook = WorkbookFactory.create(file)) {
			if (workbook.getNumberOfSheets() == 0) {
				return List.of();
			}

			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				return List.of();
			}

			DataFormatter formatter = new DataFormatter();
			Row headerRow = sheet.getRow(sheet.getFirstRowNum());
			if (headerRow == null) {
				throw new IllegalArgumentException("Không tìm thấy dòng tiêu đề trong file Excel.");
			}

			Map<String, Integer> columnMap = parseHeaderRow(headerRow, formatter);
			validateRequiredColumns(columnMap);

			List<DiemService.DiemRecordInput> rows = new ArrayList<>();
			for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}

				String cccd = getExcelCell(row, columnMap.get(COL_CCCD), formatter);
				String sbd = getExcelCell(row, columnMap.get(COL_SBD), formatter);
				String hoTen = getExcelCell(row, columnMap.get(COL_HO_TEN), formatter);
				String loaiDiem = getExcelCell(row, columnMap.get(COL_LOAI_DIEM), formatter);
				String mon = getExcelCell(row, columnMap.get(COL_MON), formatter);
				String diemRaw = getExcelCell(row, columnMap.get(COL_DIEM), formatter);

				if (isBlankRow(cccd, sbd, hoTen, loaiDiem, mon, diemRaw)) {
					continue;
				}

				double diem = parseScore(diemRaw, rowIndex + 1);
				rows.add(new DiemService.DiemRecordInput(cccd, sbd, hoTen, loaiDiem, mon, diem));
			}

			return rows;
		}
	}

	private List<DiemService.DiemRecordInput> readCsvFile(File file) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			List<String> lines = reader.lines().toList();
			if (lines.isEmpty()) {
				return List.of();
			}

			char delimiter = detectDelimiter(lines.getFirst());
			List<String> headerValues = splitCsvLine(lines.getFirst(), delimiter);

			Map<String, Integer> columnMap = parseHeaderValues(headerValues);
			validateRequiredColumns(columnMap);

			List<DiemService.DiemRecordInput> rows = new ArrayList<>();
			for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
				String line = lines.get(lineIndex);
				if (line == null || line.trim().isEmpty()) {
					continue;
				}

				List<String> values = splitCsvLine(line, delimiter);
				String cccd = getCsvValue(values, columnMap.get(COL_CCCD));
				String sbd = getCsvValue(values, columnMap.get(COL_SBD));
				String hoTen = getCsvValue(values, columnMap.get(COL_HO_TEN));
				String loaiDiem = getCsvValue(values, columnMap.get(COL_LOAI_DIEM));
				String mon = getCsvValue(values, columnMap.get(COL_MON));
				String diemRaw = getCsvValue(values, columnMap.get(COL_DIEM));

				if (isBlankRow(cccd, sbd, hoTen, loaiDiem, mon, diemRaw)) {
					continue;
				}

				double diem = parseScore(diemRaw, lineIndex + 1);
				rows.add(new DiemService.DiemRecordInput(cccd, sbd, hoTen, loaiDiem, mon, diem));
			}

			return rows;
		}
	}

	private Map<String, Integer> parseHeaderRow(Row headerRow, DataFormatter formatter) {
		Map<String, String> rawHeaders = new LinkedHashMap<>();
		for (int i = headerRow.getFirstCellNum(); i < headerRow.getLastCellNum(); i++) {
			if (i < 0) {
				continue;
			}
			String raw = formatter.formatCellValue(headerRow.getCell(i));
			rawHeaders.put(String.valueOf(i), raw);
		}

		Map<String, Integer> map = new HashMap<>();
		for (Map.Entry<String, String> entry : rawHeaders.entrySet()) {
			int index = Integer.parseInt(entry.getKey());
			String canonical = canonicalHeader(entry.getValue());
			if (canonical != null) {
				map.putIfAbsent(canonical, index);
			}
		}

		return map;
	}

	private Map<String, Integer> parseHeaderValues(List<String> headerValues) {
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < headerValues.size(); i++) {
			String canonical = canonicalHeader(headerValues.get(i));
			if (canonical != null) {
				map.putIfAbsent(canonical, i);
			}
		}
		return map;
	}

	private void validateRequiredColumns(Map<String, Integer> columnMap) {
		List<String> missing = new ArrayList<>();
		for (String required : REQUIRED_COLUMNS) {
			if (!columnMap.containsKey(required)) {
				missing.add(required);
			}
		}

		if (!missing.isEmpty()) {
			throw new IllegalArgumentException("Thiếu cột bắt buộc trong file import: " + String.join(", ", missing));
		}
	}

	private String canonicalHeader(String rawHeader) {
		if (rawHeader == null) {
			return null;
		}

		String normalized = normalize(rawHeader)
				.replace("_", "")
				.replace("-", "")
				.replace(" ", "");

		return switch (normalized) {
			case "cccd", "socccd", "cancuoc", "cancuoccongdan" -> COL_CCCD;
			case "sobaodanh", "sbd", "mahoso", "mathisinh" -> COL_SBD;
			case "hoten", "ten", "hovaten", "ten thisinh", "tenthisinh" -> COL_HO_TEN;
			case "loaidiem", "phuongthuc", "dphuongthuc", "loai" -> COL_LOAI_DIEM;
			case "mon", "monthi", "dmon", "tenmon" -> COL_MON;
			case "diem", "d" -> COL_DIEM;
			default -> null;
		};
	}

	private String getExcelCell(Row row, Integer colIndex, DataFormatter formatter) {
		if (colIndex == null || colIndex < 0) {
			return "";
		}
		return formatter.formatCellValue(row.getCell(colIndex)).trim();
	}

	private String getCsvValue(List<String> values, Integer colIndex) {
		if (colIndex == null || colIndex < 0 || colIndex >= values.size()) {
			return "";
		}
		return values.get(colIndex).trim();
	}

	private boolean isBlankRow(String... values) {
		for (String value : values) {
			if (value != null && !value.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private double parseScore(String scoreRaw, int rowNumber) {
		if (scoreRaw == null || scoreRaw.isBlank()) {
			throw new IllegalArgumentException("Thiếu điểm ở dòng " + rowNumber + '.');
		}

		String normalized = scoreRaw.trim().replace(',', '.');
		try {
			return Double.parseDouble(normalized);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Điểm không hợp lệ ở dòng " + rowNumber + ": " + scoreRaw);
		}
	}

	private char detectDelimiter(String headerLine) {
		return headerLine != null && headerLine.contains(";") ? ';' : ',';
	}

	private List<String> splitCsvLine(String line, char delimiter) {
		List<String> tokens = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '"') {
				boolean escapedQuote = inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"';
				if (escapedQuote) {
					current.append('"');
					i++;
				} else {
					inQuotes = !inQuotes;
				}
				continue;
			}

			if (c == delimiter && !inQuotes) {
				tokens.add(current.toString().trim());
				current.setLength(0);
				continue;
			}

			current.append(c);
		}

		tokens.add(current.toString().trim());
		return tokens;
	}

	private String extensionOf(String fileName) {
		String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
		int lastDot = lower.lastIndexOf('.');
		if (lastDot < 0 || lastDot == lower.length() - 1) {
			return "";
		}
		return lower.substring(lastDot + 1);
	}

	private String normalize(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replace('đ', 'd')
				.replace('Đ', 'D')
				.toLowerCase(Locale.ROOT)
				.trim();
	}
}
