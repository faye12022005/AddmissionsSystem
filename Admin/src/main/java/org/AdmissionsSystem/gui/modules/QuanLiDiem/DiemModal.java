package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class DiemModal extends JDialog {
	private final JTextField txtCccd = new JTextField();
	private final JTextField txtSoBaoDanh = new JTextField();
	private final JTextField txtHoTen = new JTextField();
	private final JComboBox<String> cboLoaiDiem = new JComboBox<>(DiemService.LOAI_DIEM.toArray(new String[0]));
	private final JComboBox<String> cboMon = new JComboBox<>(DiemService.MON_HOC.toArray(new String[0]));
	private final JSpinner spnDiem = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1200.0, 0.25));

	private DiemService.DiemRecordInput result;

	private DiemModal(Window owner, String title, DiemService.DiemRecord existing) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(460, 360);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(8, 8));

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));

		addRow(formPanel, 0, "CCCD", txtCccd);
		addRow(formPanel, 1, "Số báo danh", txtSoBaoDanh);
		addRow(formPanel, 2, "Họ và tên", txtHoTen);
		addRow(formPanel, 3, "Loại điểm", cboLoaiDiem);
		addRow(formPanel, 4, "Môn", cboMon);
		addRow(formPanel, 5, "Điểm", spnDiem);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
		JButton btnHuy = new JButton("Hủy");
		JButton btnLuu = new JButton("Lưu");

		Style.styleButton(btnLuu);

		btnHuy.addActionListener(e -> dispose());
		btnLuu.addActionListener(e -> onSave());

		buttonPanel.add(btnHuy);
		buttonPanel.add(btnLuu);

		add(formPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		if (existing != null) {
			fillFromExisting(existing);
		}
	}

	public static DiemService.DiemRecordInput showDialog(Component parent, String title,
			DiemService.DiemRecord existing) {
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		DiemModal modal = new DiemModal(owner, title, existing);
		modal.setVisible(true);
		return modal.result;
	}

	private void addRow(JPanel panel, int row, String label, JComponent field) {
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = 0;
		gbcLabel.gridy = row;
		gbcLabel.anchor = GridBagConstraints.WEST;
		gbcLabel.insets = new Insets(6, 0, 6, 10);

		GridBagConstraints gbcField = new GridBagConstraints();
		gbcField.gridx = 1;
		gbcField.gridy = row;
		gbcField.weightx = 1;
		gbcField.fill = GridBagConstraints.HORIZONTAL;
		gbcField.insets = new Insets(6, 0, 6, 0);

		JLabel lb = new JLabel(label + ':');
		panel.add(lb, gbcLabel);
		panel.add(field, gbcField);
	}

	private void fillFromExisting(DiemService.DiemRecord existing) {
		txtCccd.setText(existing.cccd());
		txtSoBaoDanh.setText(existing.soBaoDanh());
		txtHoTen.setText(existing.hoTen());
		cboLoaiDiem.setSelectedItem(existing.loaiDiem());
		cboMon.setSelectedItem(existing.mon());
		spnDiem.setValue(existing.diem());
	}

	private void onSave() {
		String cccd = txtCccd.getText() == null ? "" : txtCccd.getText().trim();
		String soBaoDanh = txtSoBaoDanh.getText() == null ? "" : txtSoBaoDanh.getText().trim();
		String hoTen = txtHoTen.getText() == null ? "" : txtHoTen.getText().trim();
		String loaiDiem = (String) cboLoaiDiem.getSelectedItem();
		String mon = (String) cboMon.getSelectedItem();
		double diem = ((Number) spnDiem.getValue()).doubleValue();

		if (cccd.isBlank() || soBaoDanh.isBlank() || hoTen.isBlank()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ CCCD, số báo danh và họ tên.", "Thiếu thông tin",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!cccd.matches("\\d{9,12}")) {
			JOptionPane.showMessageDialog(this, "CCCD phải có 9-12 chữ số.", "Dữ liệu không hợp lệ",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (diem < 0 || diem > 1200) {
			JOptionPane.showMessageDialog(this, "Điểm phải nằm trong khoảng 0 đến 1200.", "Dữ liệu không hợp lệ",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		result = new DiemService.DiemRecordInput(cccd, soBaoDanh, hoTen, loaiDiem, mon, diem);
		dispose();
	}
}
