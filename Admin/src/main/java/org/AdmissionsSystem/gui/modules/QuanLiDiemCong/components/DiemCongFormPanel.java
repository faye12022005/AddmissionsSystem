package org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components;

import org.AdmissionsSystem.gui.common.Style;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DiemCongFormPanel extends JPanel {
    private final JTextField idField;
    private final JTextField cccdField;
    private final JTextField nganhField;
    private final JTextField tohopField;
    private final JTextField phuongthucField;
    private final JTextField diemCCField;
    private final JTextField diemUtxtField;
    private final JTextField diemTongField;
    private final JTextArea ghichuArea;
    private final JTextField dcKeysField;

    public DiemCongFormPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 8, 10, 8),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 235)),
                        "Thông tin điểm cộng",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        Style.BUTTON_FONT.deriveFont(Font.BOLD),
                        new Color(60, 70, 90)
                )
        ));

        idField = new JTextField();
        cccdField = new JTextField();
        nganhField = new JTextField();
        tohopField = new JTextField();
        phuongthucField = new JTextField();
        diemCCField = new JTextField();
        diemUtxtField = new JTextField();
        diemTongField = new JTextField();
        ghichuArea = new JTextArea(2, 30);
        ghichuArea.setLineWrap(true);
        ghichuArea.setWrapStyleWord(true);
        dcKeysField = new JTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: ID, CCCD
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(idField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        add(new JLabel("CCCD"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        add(cccdField, gbc);

        // Row 1: Mã ngành, Mã tổ hợp
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("Mã ngành"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(nganhField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        add(new JLabel("Mã tổ hợp"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        add(tohopField, gbc);

        // Row 2: Phương thức, Điểm CC
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        add(new JLabel("Phương thức"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(phuongthucField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        add(new JLabel("Điểm CC"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        add(diemCCField, gbc);

        // Row 3: Điểm U, Điểm tổng
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        add(new JLabel("Điểm U"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(diemUtxtField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        add(new JLabel("Điểm tổng"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        add(diemTongField, gbc);

        // Row 4: Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        add(new JLabel("Ghi chú"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        JScrollPane scroller = new JScrollPane(ghichuArea);
        add(scroller, gbc);

        // Row 5: DC Keys
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("DC Keys"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        add(dcKeysField, gbc);
    }

    public Object[] collectFormData() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String cccd = cccdField.getText().trim();
            String nganh = nganhField.getText().trim();
            String tohop = tohopField.getText().trim();
            String phuongthuc = phuongthucField.getText().trim();
            double diemCC = Double.parseDouble(diemCCField.getText().trim());
            double diemUtxt = Double.parseDouble(diemUtxtField.getText().trim());
            double diemTong = Double.parseDouble(diemTongField.getText().trim());
            String ghichu = ghichuArea.getText().trim();
            String dcKeys = dcKeysField.getText().trim();

            if (cccd.isEmpty() || nganh.isEmpty() || tohop.isEmpty() || phuongthuc.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin bắt buộc.");
            }

            return new Object[]{id, cccd, nganh, tohop, phuongthuc, diemCC, diemUtxt, diemTong, ghichu, dcKeys};
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Vui lòng kiểm tra định dạng số.");
        }
    }

    public void setFormDataFromRow(Object[] row) {
        if (row == null || row.length < 10) return;

        idField.setText(asText(row[0]));
        cccdField.setText(asText(row[1]));
        nganhField.setText(asText(row[2]));
        tohopField.setText(asText(row[3]));
        phuongthucField.setText(asText(row[4]));
        diemCCField.setText(asText(row[5]));
        diemUtxtField.setText(asText(row[6]));
        diemTongField.setText(asText(row[7]));
        ghichuArea.setText(asText(row[8]));
        dcKeysField.setText(asText(row[9]));
    }

    public void clearForm() {
        idField.setText("");
        cccdField.setText("");
        nganhField.setText("");
        tohopField.setText("");
        phuongthucField.setText("");
        diemCCField.setText("");
        diemUtxtField.setText("");
        diemTongField.setText("");
        ghichuArea.setText("");
        dcKeysField.setText("");
    }

    public int getId() {
        try {
            return Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }
}
