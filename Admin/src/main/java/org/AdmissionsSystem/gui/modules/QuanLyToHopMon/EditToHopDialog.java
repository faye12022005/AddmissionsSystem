package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class EditToHopDialog extends JDialog {
    private final JTextField codeField = new JTextField(20);
    private final JTextField nameField = new JTextField(30);
    private final JTextField subj1 = new JTextField(12);
    private final JTextField subj2 = new JTextField(12);
    private final JTextField subj3 = new JTextField(12);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Hoạt động","Tạm ngưng"});
    private boolean saved = false;
    private boolean deleted = false;

    public EditToHopDialog(Frame owner, String code, String name, String s1, String s2, String s3, String status) {
        super(owner, "Chỉnh sửa tổ hợp môn", true);
        initUI();
        // Populate initial values
        codeField.setText(code);
        codeField.setEditable(false);  // Code không thể sửa
        nameField.setText(name != null ? name : "");
        subj1.setText(s1 != null ? s1 : "");
        subj2.setText(s2 != null ? s2 : "");
        subj3.setText(s3 != null ? s3 : "");
        if (status != null && !status.isEmpty()) {
            statusBox.setSelectedItem(status);
        } else {
            statusBox.setSelectedIndex(0);
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        
        // Mã tổ hợp (không thể sửa)
        gbc.gridx = 0; gbc.gridy = row; 
        JLabel codeLbl = new JLabel("Mã tổ hợp:");
        codeLbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(codeLbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        codeField.setFont(new Font("System", Font.PLAIN, 12));
        codeField.setEditable(false);  // Read-only
        codeField.setBackground(new Color(240, 240, 240));
        fields.add(codeField, gbc); 
        row++;

        // Tên tổ hợp
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel nameLbl = new JLabel("Tên tổ hợp:");
        nameLbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(nameLbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField.setFont(new Font("System", Font.PLAIN, 12));
        fields.add(nameField, gbc); 
        row++;

        // Môn 1
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel subj1Lbl = new JLabel("Môn 1:");
        subj1Lbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(subj1Lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        subj1.setFont(new Font("System", Font.PLAIN, 12));
        fields.add(subj1, gbc); 
        row++;

        // Môn 2
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel subj2Lbl = new JLabel("Môn 2:");
        subj2Lbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(subj2Lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        subj2.setFont(new Font("System", Font.PLAIN, 12));
        fields.add(subj2, gbc); 
        row++;

        // Môn 3
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel subj3Lbl = new JLabel("Môn 3:");
        subj3Lbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(subj3Lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        subj3.setFont(new Font("System", Font.PLAIN, 12));
        fields.add(subj3, gbc); 
        row++;

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel statusLbl = new JLabel("Trạng thái:");
        statusLbl.setFont(new Font("System", Font.BOLD, 12));
        fields.add(statusLbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        statusBox.setFont(new Font("System", Font.PLAIN, 12));
        fields.add(statusBox, gbc);

        panel.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton delete = new JButton("Xóa");
        JButton save = new JButton("Cập nhật");
        JButton cancel = new JButton("Hủy");
        
        // Styling
        delete.setFont(new Font("System", Font.PLAIN, 12));
        save.setFont(new Font("System", Font.BOLD, 12));
        cancel.setFont(new Font("System", Font.PLAIN, 12));
        delete.setPreferredSize(new Dimension(80, 32));
        save.setPreferredSize(new Dimension(100, 32));
        cancel.setPreferredSize(new Dimension(80, 32));
        
        delete.setMnemonic(KeyEvent.VK_D);
        save.setMnemonic(KeyEvent.VK_U);
        cancel.setMnemonic(KeyEvent.VK_C);
        
        delete.addActionListener(e -> onDelete());
        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> onCancel());
        
        // Close dialog on ESC
        getRootPane().registerKeyboardAction(e -> onCancel(), 
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        btnRow.add(delete);
        btnRow.add(cancel);
        btnRow.add(save);

        panel.add(btnRow, BorderLayout.SOUTH);

        setContentPane(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    private void onSave() {
        if (validateInput()) {
            saved = true;
            setVisible(false);
        }
    }

    private void onCancel() {
        saved = false;
        setVisible(false);
    }

    private void onDelete() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn xóa tổ hợp này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (option == JOptionPane.YES_OPTION) {
            deleted = true;
            saved = false;
            setVisible(false);
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tổ hợp không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (subj1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Môn 1 không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            subj1.requestFocus();
            return false;
        }
        if (subj2.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Môn 2 không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            subj2.requestFocus();
            return false;
        }
        if (subj3.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Môn 3 không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            subj3.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isSaved() { return saved; }
    public boolean isDeleted() { return deleted; }
    public String getCode() { return codeField.getText().trim(); }
    public String getNameValue() { return nameField.getText().trim(); }
    public String getSubj1() { return subj1.getText().trim(); }
    public String getSubj2() { return subj2.getText().trim(); }
    public String getSubj3() { return subj3.getText().trim(); }
    public String getStatus() { return (String) statusBox.getSelectedItem(); }
}
