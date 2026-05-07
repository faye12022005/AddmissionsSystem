package org.AdmissionsSystem.gui.modules.QuanlyNganh.components;

import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.gui.common.Style;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class NganhHocDialog extends JDialog {
    private final JTextField txtMa = new JTextField(15);
    private final JTextField txtTen = new JTextField(30);
    private final JTextField txtToHopGoc = new JTextField(15);
    private final JTextField txtChiTieu = new JTextField(10);
    private final JTextField txtDiemSan = new JTextField(10);
    private final JTextField txtDiemTrungTuyen = new JTextField(10);
    private final JCheckBox chkTuyenThang = new JCheckBox("Tuyển thẳng");
    private final JCheckBox chkDGNL = new JCheckBox("Sử dụng DGNL");
    private final JCheckBox chkTHPT = new JCheckBox("Sử dụng THPT");
    private final JCheckBox chkVSAT = new JCheckBox("Sử dụng VSAT");
    private final JTextField txtSlXetTuyen = new JTextField(10);
    private final JTextField txtSlDGNL = new JTextField(10);
    private final JTextField txtSlVSAT = new JTextField(10);
    private final JTextField txtSlTHPT = new JTextField(10);

    private boolean confirmed = false;
    private final boolean isEdit;

    public NganhHocDialog(Frame owner, String title, XtNganh data) {
        super(owner, title, true);
        this.isEdit = (data != null);
        
        setLayout(new BorderLayout());
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int r = 0;
        addLabeledField(content, gbc, r++, "Mã ngành:", txtMa);
        addLabeledField(content, gbc, r++, "Tên ngành:", txtTen);
        addLabeledField(content, gbc, r++, "Tổ hợp gốc:", txtToHopGoc);
        addLabeledField(content, gbc, r++, "Chỉ tiêu:", txtChiTieu);
        addLabeledField(content, gbc, r++, "Điểm sàn:", txtDiemSan);
        addLabeledField(content, gbc, r++, "Điểm trúng tuyển:", txtDiemTrungTuyen);
        addLabeledField(content, gbc, r++, "SL xét tuyển:", txtSlXetTuyen);
        addLabeledField(content, gbc, r++, "SL DGNL:", txtSlDGNL);
        addLabeledField(content, gbc, r++, "SL VSAT:", txtSlVSAT);
        addLabeledField(content, gbc, r++, "SL THPT:", txtSlTHPT);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(chkTuyenThang);
        checkPanel.add(chkDGNL);
        checkPanel.add(chkTHPT);
        checkPanel.add(chkVSAT);
        
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        content.add(checkPanel, gbc);

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        Style.styleButton(btnSave);
        Style.styleButton(btnCancel);
        
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        if (isEdit) {
            setFormData(data);
            txtMa.setEditable(false);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void addLabeledField(JPanel p, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(field, gbc);
    }

    private void setFormData(XtNganh model) {
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

    public XtNganh collectFormData() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành và Tên ngành là bắt buộc.");
        }

        XtNganh model = new XtNganh();
        model.setManganh(ma);
        model.setTennganh(ten);
        model.setNTohopgoc(txtToHopGoc.getText().trim().isEmpty() ? null : txtToHopGoc.getText().trim());
        model.setNChitieu(parseInt(txtChiTieu, "Chỉ tiêu", true));
        model.setNDiemsan(parseBigDecimal(txtDiemSan, "Điểm sàn", false));
        model.setNDiemtrungtuyen(parseBigDecimal(txtDiemTrungTuyen, "Điểm trúng tuyển", false));
        model.setNTuyenthang(toYN(chkTuyenThang.isSelected()));
        model.setNDgnl(toYN(chkDGNL.isSelected()));
        model.setNThpt(toYN(chkTHPT.isSelected()));
        model.setNVsat(toYN(chkVSAT.isSelected()));
        model.setSlXtt(parseInt(txtSlXetTuyen, "SL xét tuyển", false));
        model.setSlDgnl(parseInt(txtSlDGNL, "SL DGNL", false));
        model.setSlVsat(parseInt(txtSlVSAT, "SL VSAT", false));
        model.setSlThpt(txtSlTHPT.getText().trim().isEmpty() ? null : txtSlTHPT.getText().trim());
        return model;
    }

    private void onSave() {
        try {
            collectFormData(); // Just to validate
            confirmed = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private Integer parseInt(JTextField field, String fieldName, boolean required) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            if (required) throw new IllegalArgumentException(fieldName + " không được để trống.");
            return null;
        }
        try { return Integer.parseInt(text); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(fieldName + " phải là số nguyên."); }
    }

    private BigDecimal parseBigDecimal(JTextField field, String fieldName, boolean required) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            if (required) throw new IllegalArgumentException(fieldName + " không được để trống.");
            return null;
        }
        try { return new BigDecimal(text); }
        catch (Exception e) { throw new IllegalArgumentException(fieldName + " phải là số thực."); }
    }

    private String asText(Object value) { return value == null ? "" : value.toString(); }
    private String toYN(boolean selected) { return selected ? "Y" : "N"; }
}
