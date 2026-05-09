package org.AdmissionsSystem.gui.modules.QuanlyNganh.mapper;

import java.math.BigDecimal;
import org.AdmissionsSystem.models.XtNganh;

public class NganhHocRowMapper {

    public XtNganh fromRow(Object[] row) {
        XtNganh model = new XtNganh();
        model.setManganh(asText(rowValue(row, 0)));
        model.setTennganh(asText(rowValue(row, 1)));
        model.setNTohopgoc(asText(rowValue(row, 2)));
        model.setNChitieu(parseInt(rowValue(row, 3)));
        model.setNDiemsan(parseBigDecimal(rowValue(row, 4)));
        model.setNDiemtrungtuyen(parseBigDecimal(rowValue(row, 5)));
        model.setNTuyenthang(normalizeYn(asText(rowValue(row, 6))));
        model.setNDgnl(normalizeYn(asText(rowValue(row, 7))));
        model.setNThpt(normalizeYn(asText(rowValue(row, 8))));
        model.setNVsat(normalizeYn(asText(rowValue(row, 9))));
        model.setSlXtt(parseInt(rowValue(row, 10)));
        model.setSlDgnl(parseInt(rowValue(row, 11)));
        model.setSlVsat(parseInt(rowValue(row, 12)));
        model.setSlThpt(String.valueOf(parseInt(rowValue(row, 13))));
        return model;
    }

    public Object[] toRow(XtNganh model) {
        return new Object[] {
                asText(model.getManganh()),
                asText(model.getTennganh()),
                asText(model.getNTohopgoc()),
                nvlInt(model.getNChitieu()),
                nvlBigDecimal(model.getNDiemsan()),
                nvlBigDecimal(model.getNDiemtrungtuyen()),
                normalizeYn(model.getNTuyenthang()),
                normalizeYn(model.getNDgnl()),
                normalizeYn(model.getNThpt()),
                normalizeYn(model.getNVsat()),
                nvlInt(model.getSlXtt()),
                nvlInt(model.getSlDgnl()),
                nvlInt(model.getSlVsat()),
                parseIntSafe(model.getSlThpt())
        };
    }

    private Object rowValue(Object[] row, int index) {
        if (row == null || index < 0 || index >= row.length) {
            return null;
        }
        return row[index];
    }

    private Integer parseInt(Object value) {
        String text = asText(value);
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    private Integer parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private BigDecimal parseBigDecimal(Object value) {
        String text = asText(value);
        if (text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(text);
    }

    private BigDecimal nvlBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer nvlInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeYn(String value) {
        if (value == null) {
            return "N";
        }
        String normalized = value.trim();
        if ("Y".equalsIgnoreCase(normalized) || "true".equalsIgnoreCase(normalized) || "1".equals(normalized)) {
            return "Y";
        }
        return "N";
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
