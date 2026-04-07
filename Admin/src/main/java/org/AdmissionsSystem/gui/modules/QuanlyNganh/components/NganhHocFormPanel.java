package org.AdmissionsSystem.gui.modules.QuanlyNganh.components;

import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import org.AdmissionsSystem.models.XtNganh;

public class NganhHocFormPanel extends JPanel {
    private final JTextField txtMa = new JTextField(12);
    private final JTextField txtTen = new JTextField(24);
    private final JTextField txtToHopGoc = new JTextField(10);
    private final JTextField txtChiTieu = new JTextField(6);
    private final JTextField txtDiemSan = new JTextField(6);
    private final JTextField txtDiemTrungTuyen = new JTextField(6);
    private final JCheckBox chkTuyenThang = new JCheckBox("Tuyển thẳng");
    private final JCheckBox chkDGNL = new JCheckBox("Sử dụng DGNL");
    private final JCheckBox chkTHPT = new JCheckBox("Sử dụng THPT");
    private final JCheckBox chkVSAT = new JCheckBox("Sử dụng VSAT");
    private final JTextField txtSlXetTuyen = new JTextField(8);
    private final JTextField txtSlDGNL = new JTextField(8);
    private final JTextField txtSlVSAT = new JTextField(8);
    private final JTextField txtSlTHPT = new JTextField(8);

    public NganhHocFormPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addField(gbc, r, "Mã ngành", txtMa, 0);
        addField(gbc, r, "Tên ngành", txtTen, 2);
        addField(gbc, r, "Tổ hợp gốc", txtToHopGoc, 4);
        r++;
        addField(gbc, r, "Chỉ tiêu", txtChiTieu, 0);
        addField(gbc, r, "Điểm sàn", txtDiemSan, 2);
        addField(gbc, r, "Điểm trúng tuyển", txtDiemTrungTuyen, 4);
        r++;
        addField(gbc, r, "SL xét tuyển", txtSlXetTuyen, 0);
        addField(gbc, r, "SL DGNL", txtSlDGNL, 2);
        addField(gbc, r, "SL VSAT", txtSlVSAT, 4);
        r++;
        addField(gbc, r, "SL THPT", txtSlTHPT, 0);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(chkTuyenThang);
        checkPanel.add(chkDGNL);
        checkPanel.add(chkTHPT);
        checkPanel.add(chkVSAT);

        gbc.gridx = 2;
        gbc.gridy = r;
        gbc.gridwidth = 4;
        add(checkPanel, gbc);
    }

    public XtNganh collectFormData() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String toHop = txtToHopGoc.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || toHop.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành, Tên ngành, Tổ hợp gốc là bắt buộc.");
        }

        int chiTieu = parseInt(txtChiTieu, "Chỉ tiêu");
        BigDecimal diemSan = parseBigDecimal(txtDiemSan, "Điểm sàn");
        BigDecimal diemTrungTuyen = parseBigDecimal(txtDiemTrungTuyen, "Điểm trúng tuyển");
        int slXetTuyen = parseInt(txtSlXetTuyen, "SL xét tuyển");
        int slDGNL = parseInt(txtSlDGNL, "SL DGNL");
        int slVSAT = parseInt(txtSlVSAT, "SL VSAT");
        int slTHPT = parseInt(txtSlTHPT, "SL THPT");

        XtNganh model = new XtNganh();
        model.setManganh(ma);
        model.setTennganh(ten);
        model.setNTohopgoc(toHop);
        model.setNChitieu(chiTieu);
        model.setNDiemsan(diemSan);
        model.setNDiemtrungtuyen(diemTrungTuyen);
        model.setNTuyenthang(toYN(chkTuyenThang.isSelected()));
        model.setNDgnl(toYN(chkDGNL.isSelected()));
        model.setNThpt(toYN(chkTHPT.isSelected()));
        model.setNVsat(toYN(chkVSAT.isSelected()));
        model.setSlXtt(slXetTuyen);
        model.setSlDgnl(slDGNL);
        model.setSlVsat(slVSAT);
        model.setSlThpt(String.valueOf(slTHPT));
        return model;
    }

    public void setFormData(XtNganh model) {
        if (model == null) {
            clearForm();
            return;
        }
        txtMa.setText(asText(model.getManganh()));
        txtTen.setText(asText(model.getTennganh()));
        txtToHopGoc.setText(asText(model.getNTohopgoc()));
        txtChiTieu.setText(asText(model.getNChitieu()));
        txtDiemSan.setText(asText(model.getNDiemsan()));
        txtDiemTrungTuyen.setText(asText(model.getNDiemtrungtuyen()));
        chkTuyenThang.setSelected("Y".equals(asText(model.getNTuyenthang())));
        chkDGNL.setSelected("Y".equals(asText(model.getNDgnl())));
        chkTHPT.setSelected("Y".equals(asText(model.getNThpt())));
        chkVSAT.setSelected("Y".equals(asText(model.getNVsat())));
        txtSlXetTuyen.setText(asText(model.getSlXtt()));
        txtSlDGNL.setText(asText(model.getSlDgnl()));
        txtSlVSAT.setText(asText(model.getSlVsat()));
        txtSlTHPT.setText(asText(model.getSlThpt()));
    }

    public void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtToHopGoc.setText("");
        txtChiTieu.setText("");
        txtDiemSan.setText("");
        txtDiemTrungTuyen.setText("");
        txtSlXetTuyen.setText("");
        txtSlDGNL.setText("");
        txtSlVSAT.setText("");
        txtSlTHPT.setText("");
        chkTuyenThang.setSelected(false);
        chkDGNL.setSelected(false);
        chkTHPT.setSelected(false);
        chkVSAT.setSelected(false);
    }

    public String getMaNganh() {
        return txtMa.getText().trim();
    }

    private void addField(GridBagConstraints gbc, int row, String label, JTextField field, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        add(new JLabel(label), gbc);

        gbc.gridx = col + 1;
        gbc.weightx = 0.4;
        add(field, gbc);
        gbc.weightx = 0;
    }

    private int parseInt(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private BigDecimal parseBigDecimal(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số thực.");
        }
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }

    private String toYN(boolean selected) {
        return selected ? "Y" : "N";
    }
}
