package org.AdmissionsSystem.gui.modules.QuanlyNganh.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NganhHocCsvService {
    public List<Object[]> readRows(Path path, String[] headers) throws IOException {
        List<Object[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (lineNo == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> cells = parseCsvLine(line);
                if (lineNo == 1 && isHeaderRow(cells, headers)) {
                    continue;
                }
                if (cells.size() < headers.length) {
                    throw new IllegalArgumentException("Dòng " + lineNo + " thiếu cột dữ liệu.");
                }
                rows.add(toRow(cells, lineNo));
            }
        }
        return rows;
    }

    public void writeRows(Path out, String[] headers, List<Object[]> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            writer.write(String.join(",", headers));
            writer.newLine();

            for (Object[] row : rows) {
                writer.write(toCsvLine(row));
                writer.newLine();
            }
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuote = !inQuote;
                }
            } else if (c == ',' && !inQuote) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private boolean isHeaderRow(List<String> cells, String[] headers) {
        if (cells.isEmpty() || headers.length == 0) {
            return false;
        }
        return headers[0].equalsIgnoreCase(cells.get(0).trim());
    }

    private Object[] toRow(List<String> cells, int lineNo) {
        try {
            return new Object[]{
                    text(cells, 0),
                    text(cells, 1),
                    text(cells, 2),
                    Integer.parseInt(text(cells, 3)),
                    Double.parseDouble(text(cells, 4)),
                    Double.parseDouble(text(cells, 5)),
                    yn(text(cells, 6)),
                    yn(text(cells, 7)),
                    yn(text(cells, 8)),
                    yn(text(cells, 9)),
                    Integer.parseInt(text(cells, 10)),
                    Integer.parseInt(text(cells, 11)),
                    Integer.parseInt(text(cells, 12)),
                    Integer.parseInt(text(cells, 13))
            };
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Dòng " + lineNo + " có giá trị số không hợp lệ.");
        }
    }

    private String text(List<String> cells, int index) {
        return cells.get(index).trim();
    }

    private String yn(String value) {
        if ("Y".equalsIgnoreCase(value) || "N".equalsIgnoreCase(value)) {
            return value.toUpperCase();
        }
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return "Y";
        }
        return "N";
    }

    private String toCsvLine(Object[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escapeCsv(asText(row[i])));
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }
}
