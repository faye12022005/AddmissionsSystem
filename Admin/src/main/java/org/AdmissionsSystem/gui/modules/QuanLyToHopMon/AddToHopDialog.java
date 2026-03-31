package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AddToHopDialog extends JDialog {
    private final JTextField codeField = new JTextField(20);
    private final JTextField nameField = new JTextField(30);
    private final JTextField subj1 = new JTextField(12);
    private final JTextField subj2 = new JTextField(12);
    private final JTextField subj3 = new JTextField(12);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Hoạt động","Tạm ngưng"});
    private boolean saved = false;

    public AddToHopDialog(Frame owner) {
        super(owner, "Thêm tổ hợp môn", true);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Mã tổ hợp:"), gbc);
        gbc.gridx = 1; fields.add(codeField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Tên tổ hợp:"), gbc);
        gbc.gridx = 1; fields.add(nameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Môn 1:"), gbc);
        gbc.gridx = 1; fields.add(subj1, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Môn 2:"), gbc);
        gbc.gridx = 1; fields.add(subj2, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Môn 3:"), gbc);
        gbc.gridx = 1; fields.add(subj3, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; fields.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; fields.add(statusBox, gbc);

        panel.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");
        btnRow.add(cancel);
        btnRow.add(save);

        save.addActionListener(e -> {
            saved = true;
            setVisible(false);
        });
        cancel.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });

        panel.add(btnRow, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public boolean isSaved() { return saved; }
    public String getCode() { return codeField.getText().trim(); }
    public String getNameValue() { return nameField.getText().trim(); }
    public String getSubj1() { return subj1.getText().trim(); }
    public String getSubj2() { return subj2.getText().trim(); }
    public String getSubj3() { return subj3.getText().trim(); }
    public String getStatus() { return (String) statusBox.getSelectedItem(); }
}
