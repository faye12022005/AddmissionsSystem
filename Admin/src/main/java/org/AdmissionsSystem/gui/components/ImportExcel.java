package org.AdmissionsSystem.gui.components;

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

	public List<Object[]> chooseAndRead(Component parent, String dialogTitle, String[] targetColumns)
			throws IOException {
		return chooseAndRead(parent, dialogTitle, targetColumns, Map.of());
	}

	public List<Object[]> chooseAndRead(Component parent,
			String dialogTitle,
			String[] targetColumns,
			Map<String, String> headerAliases) throws IOException {
		if (targetColumns == null || targetColumns.length == 0) {
			throw new IllegalArgumentException("Danh sách cột import không được để trống.");
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(dialogTitle == null || dialogTitle.isBlank() ? "Chọn file import" : dialogTitle);
		chooser.setFileFilter(new FileNameExtensionFilter("Excel/CSV (*.xlsx, *.xls, *.csv)", "xlsx", "xls", "csv"));

		int result = chooser.showOpenDialog(parent);
		if (result != JFileChooser.APPROVE_OPTION) {
			return List.of();
		}

		File selectedFile = chooser.getSelectedFile();
		String extension = extensionOf(selectedFile.getName());

		return switch (extension) {
			case "xlsx", "xls" -> readExcel(selectedFile, targetColumns, headerAliases);
			case "csv" -> readCsv(selectedFile, targetColumns, headerAliases);
			default ->
				throw new IllegalArgumentException("Định dạng file không hỗ trợ. Vui lòng chọn .xlsx, .xls hoặc .csv.");
		};
	}

	private List<Object[]> readExcel(File file, String[] targetColumns, Map<String, String> headerAliases)
			throws IOException {
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

			List<String> headers = new ArrayList<>();
			for (int i = Math.max(0, headerRow.getFirstCellNum()); i < headerRow.getLastCellNum(); i++) {
				headers.add(formatter.formatCellValue(headerRow.getCell(i)));
			}

			int[] targetIndexByColumn = resolveColumnIndexes(headers, targetColumns, headerAliases);

			List<Object[]> rows = new ArrayList<>();
			for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}

				Object[] mapped = new Object[targetColumns.length];
				boolean hasAnyValue = false;

				for (int i = 0; i < targetColumns.length; i++) {
					int sourceIndex = targetIndexByColumn[i];
					String value = sourceIndex >= 0 ? formatter.formatCellValue(row.getCell(sourceIndex)).trim() : "";
					mapped[i] = value;
					if (!value.isBlank()) {
						hasAnyValue = true;
					}
				}

				if (hasAnyValue) {
					rows.add(mapped);
				}
			}

			return rows;
		}
	}

	private List<Object[]> readCsv(File file, String[] targetColumns, Map<String, String> headerAliases)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			List<String> lines = reader.lines().toList();
			if (lines.isEmpty()) {
				return List.of();
			}

			String firstLine = removeBom(lines.getFirst());
			char delimiter = detectDelimiter(firstLine);
			List<String> headers = splitCsvLine(firstLine, delimiter);
			int[] targetIndexByColumn = resolveColumnIndexes(headers, targetColumns, headerAliases);

			List<Object[]> rows = new ArrayList<>();
			for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
				String line = lines.get(lineIndex);
				if (line == null || line.isBlank()) {
					continue;
				}

				List<String> values = splitCsvLine(line, delimiter);
				Object[] mapped = new Object[targetColumns.length];
				boolean hasAnyValue = false;

				for (int i = 0; i < targetColumns.length; i++) {
					int sourceIndex = targetIndexByColumn[i];
					String value = sourceIndex >= 0 && sourceIndex < values.size() ? values.get(sourceIndex).trim()
							: "";
					mapped[i] = value;
					if (!value.isBlank()) {
						hasAnyValue = true;
					}
				}

				if (hasAnyValue) {
					rows.add(mapped);
				}
			}

			return rows;
		}
	}

	private int[] resolveColumnIndexes(List<String> sourceHeaders,
			String[] targetColumns,
			Map<String, String> headerAliases) {
		Map<String, Integer> sourceByCanonical = new HashMap<>();
		for (int i = 0; i < sourceHeaders.size(); i++) {
			String canonical = normalize(sourceHeaders.get(i));
			if (!canonical.isBlank()) {
				sourceByCanonical.putIfAbsent(canonical, i);
			}
		}

		Map<String, String> aliasToTargetCanonical = new HashMap<>();
		if (headerAliases != null) {
			for (Map.Entry<String, String> entry : headerAliases.entrySet()) {
				String alias = normalize(entry.getKey());
				String target = normalize(entry.getValue());
				if (!alias.isBlank() && !target.isBlank()) {
					aliasToTargetCanonical.put(alias, target);
				}
			}
		}

		Map<String, Integer> resolvedByTargetCanonical = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> sourceEntry : sourceByCanonical.entrySet()) {
			String sourceCanonical = sourceEntry.getKey();
			int sourceIndex = sourceEntry.getValue();

			String targetCanonical = aliasToTargetCanonical.getOrDefault(sourceCanonical, sourceCanonical);
			resolvedByTargetCanonical.putIfAbsent(targetCanonical, sourceIndex);
		}

		int[] targetIndexByColumn = new int[targetColumns.length];
		List<String> missingColumns = new ArrayList<>();

		for (int i = 0; i < targetColumns.length; i++) {
			String targetCanonical = normalize(targetColumns[i]);
			Integer sourceIndex = resolvedByTargetCanonical.get(targetCanonical);
			if (sourceIndex == null) {
				targetIndexByColumn[i] = -1;
				missingColumns.add(targetColumns[i]);
			} else {
				targetIndexByColumn[i] = sourceIndex;
			}
		}

		if (!missingColumns.isEmpty()) {
			throw new IllegalArgumentException("Thiếu cột trong file import: " + String.join(", ", missingColumns));
		}

		return targetIndexByColumn;
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

	private String removeBom(String text) {
		if (text != null && text.startsWith("\uFEFF")) {
			return text.substring(1);
		}
		return text;
	}

	// Tìm đến hàm normalize ở cuối file và sửa lại như sau:
	private String normalize(String text) {
		if (text == null) {
			return "";
		}
		return Normalizer.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replace('đ', 'd')
				.replace('Đ', 'D')
				.toLowerCase(Locale.ROOT)
				// .replace("_", "") <-- XÓA HOẶC COMMENT DÒNG NÀY
				// .replace("-", "") <-- XÓA HOẶC COMMENT DÒNG NÀY
				.replace(" ", "")
				.trim();
	}

}
