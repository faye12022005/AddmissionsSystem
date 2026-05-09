package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DiemModal extends JDialog {
	private static final BigDecimal THPT_MAX = BigDecimal.TEN;
	private static final BigDecimal DGNL_MAX = BigDecimal.valueOf(1200);

	private final JTextField txtId = new JTextField();
	private final JTextField txtCccd = new JTextField();
	private final JTextField txtSoBaoDanh = new JTextField();
	private final JComboBox<String> cboLoaiDiem = new JComboBox<>(QuanLiDiemService.LOAI_DIEM.toArray(new String[0]));

	private final JTextField txtToan = new JTextField();
	private final JTextField txtLy = new JTextField();
	private final JTextField txtHoa = new JTextField();
	private final JTextField txtSinh = new JTextField();
	private final JTextField txtSu = new JTextField();
	private final JTextField txtDia = new JTextField();
	private final JTextField txtVan = new JTextField();
	private final JTextField txtGdcd = new JTextField();
	private final JTextField txtN1Thi = new JTextField();
	private final JTextField txtN1Cc = new JTextField();
	private final JTextField txtCncn = new JTextField();
	private final JTextField txtCnnn = new JTextField();
	private final JTextField txtTin = new JTextField();
	private final JTextField txtKtpl = new JTextField();
	private final JTextField txtNl1 = new JTextField();
	private final JTextField txtNk1 = new JTextField();
	private final JTextField txtNk2 = new JTextField();
	private final JTextField txtNk3 = new JTextField();
	private final JTextField txtNk4 = new JTextField();
	private final JTextField txtNk5 = new JTextField();
	private final JTextField txtNk6 = new JTextField();

	private final List<FieldBinding> bindings = new ArrayList<>();
	private final DecimalFormat scoreFormat = new DecimalFormat("0.##");
	private final FieldBinding idBinding;
	private final boolean isViewMode;

	private QuanLiDiemService.DiemInput result;

	private DiemModal(Window owner, String title, QuanLiDiemService.DiemRecord existing) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(860, 620);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(8, 8));

		scoreFormat.setGroupingUsed(false);

		// Determine if this is VIEW mode (existing record and title contains "Chi tiết"
		// or similar)
		isViewMode = title.toLowerCase().contains("chi tiết") || title.toLowerCase().contains("view")
				|| title.toLowerCase().contains("detail");

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

		txtId.setEditable(false);
		txtId.setBackground(new Color(245, 246, 250));

		idBinding = createBinding("ID", txtId);
		bindings.add(idBinding);

		GridBagConstraints gbcId = new GridBagConstraints();
		gbcId.gridx = 0;
		gbcId.gridy = 0;
		gbcId.weightx = 1;
		gbcId.fill = GridBagConstraints.HORIZONTAL;
		gbcId.insets = new Insets(6, 0, 6, 12);
		formPanel.add(idBinding.container, gbcId);

		// Hide ID field for CREATE and UPDATE modes (not VIEW mode)
		if (!isViewMode) {
			idBinding.container.setVisible(false);
		}

		int startIndex = isViewMode ? 1 : 1; // Both cases start at index 1, but with different grid positions
		addField(formPanel, startIndex, "CCCD", txtCccd);
		addField(formPanel, startIndex + 1, "Số báo danh", txtSoBaoDanh);
		addField(formPanel, startIndex + 2, "Phương thức", cboLoaiDiem);

		addField(formPanel, startIndex + 3, "Toán", txtToan);
		addField(formPanel, startIndex + 4, "Lý", txtLy);
		addField(formPanel, startIndex + 5, "Hóa", txtHoa);
		addField(formPanel, startIndex + 6, "Sinh", txtSinh);
		addField(formPanel, startIndex + 7, "Sử", txtSu);
		addField(formPanel, startIndex + 8, "Địa", txtDia);
		addField(formPanel, startIndex + 9, "Văn", txtVan);
		addField(formPanel, startIndex + 10, "GDCD", txtGdcd);
		addField(formPanel, startIndex + 11, "N1_THI", txtN1Thi);
		addField(formPanel, startIndex + 12, "N1_CC", txtN1Cc);
		addField(formPanel, startIndex + 13, "CNCN", txtCncn);
		addField(formPanel, startIndex + 14, "CNNN", txtCnnn);
		addField(formPanel, startIndex + 15, "Tin học", txtTin);
		addField(formPanel, startIndex + 16, "KTPL", txtKtpl);
		addField(formPanel, startIndex + 17, "NL1", txtNl1);
		addField(formPanel, startIndex + 18, "NK1", txtNk1);
		addField(formPanel, startIndex + 19, "NK2", txtNk2);
		addField(formPanel, startIndex + 20, "NK3", txtNk3);
		addField(formPanel, startIndex + 21, "NK4", txtNk4);
		addField(formPanel, startIndex + 22, "NK5", txtNk5);
		addField(formPanel, startIndex + 23, "NK6", txtNk6);

		JScrollPane scrollPane = new JScrollPane(formPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));
		JButton btnHuy = new JButton(isViewMode ? "Đóng" : "Hủy");
		JButton btnLuu = new JButton("Lưu");

		Style.styleButton(btnLuu);

		btnHuy.addActionListener(e -> dispose());
		btnLuu.addActionListener(e -> onSave());

		buttonPanel.add(btnHuy);

		// Only show Save button in CREATE/UPDATE modes
		if (!isViewMode) {
			buttonPanel.add(btnLuu);
		}

		add(buttonPanel, BorderLayout.SOUTH);

		if (existing != null) {
			fillFromExisting(existing);

			// Make all fields read-only in VIEW mode
			if (isViewMode) {
				setFieldsEditable(false);
			}
		}
	}

	public static QuanLiDiemService.DiemInput showDialog(Component parent, String title,
			QuanLiDiemService.DiemRecord existing) {
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		DiemModal modal = new DiemModal(owner, title, existing);
		modal.setVisible(true);
		return modal.result;
	}

	private void addField(JPanel panel, int index, String label, JComponent input) {
		FieldBinding binding = createBinding(label, input);
		bindings.add(binding);

		int adjustedIndex = index - 1; // Adjust index since ID field is handled separately
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = adjustedIndex % 2;
		gbc.gridy = adjustedIndex / 2 + (isViewMode ? 1 : 1); // +1 to account for ID field row
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, gbc.gridx == 0 ? 0 : 12, 6, gbc.gridx == 0 ? 12 : 0);
		panel.add(binding.container, gbc);
	}

	private void setFieldsEditable(boolean editable) {
		txtCccd.setEditable(editable);
		txtSoBaoDanh.setEditable(editable);
		cboLoaiDiem.setEnabled(editable);
		txtToan.setEditable(editable);
		txtLy.setEditable(editable);
		txtHoa.setEditable(editable);
		txtSinh.setEditable(editable);
		txtSu.setEditable(editable);
		txtDia.setEditable(editable);
		txtVan.setEditable(editable);
		txtGdcd.setEditable(editable);
		txtN1Thi.setEditable(editable);
		txtN1Cc.setEditable(editable);
		txtCncn.setEditable(editable);
		txtCnnn.setEditable(editable);
		txtTin.setEditable(editable);
		txtKtpl.setEditable(editable);
		txtNl1.setEditable(editable);
		txtNk1.setEditable(editable);
		txtNk2.setEditable(editable);
		txtNk3.setEditable(editable);
		txtNk4.setEditable(editable);
		txtNk5.setEditable(editable);
		txtNk6.setEditable(editable);
	}

	private FieldBinding createBinding(String label, JComponent input) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setOpaque(false);

		JLabel lb = new JLabel(label);
		lb.setFont(Style.TABLE_FONT.deriveFont(Font.PLAIN, 13f));

		JLabel errorLabel = new JLabel(" ");
		errorLabel.setForeground(new Color(220, 38, 38));
		errorLabel.setFont(Style.TABLE_FONT.deriveFont(Font.PLAIN, 11f));

		input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		wrapper.add(lb);
		wrapper.add(Box.createVerticalStrut(4));
		wrapper.add(input);
		wrapper.add(Box.createVerticalStrut(2));
		wrapper.add(errorLabel);

		return new FieldBinding(label, input, errorLabel, wrapper);
	}

	private void fillFromExisting(QuanLiDiemService.DiemRecord existing) {
		txtId.setText(String.valueOf(existing.id()));
		txtCccd.setText(existing.cccd());
		txtSoBaoDanh.setText(existing.soBaoDanh());
		cboLoaiDiem.setSelectedItem(existing.loaiDiem());

		txtToan.setText(formatScore(existing.to()));
		txtLy.setText(formatScore(existing.li()));
		txtHoa.setText(formatScore(existing.ho()));
		txtSinh.setText(formatScore(existing.si()));
		txtSu.setText(formatScore(existing.su()));
		txtDia.setText(formatScore(existing.di()));
		txtVan.setText(formatScore(existing.va()));
		txtGdcd.setText(formatScore(existing.gdcd()));
		txtN1Thi.setText(formatScore(existing.n1Thi()));
		txtN1Cc.setText(formatScore(existing.n1Cc()));
		txtCncn.setText(formatScore(existing.cncn()));
		txtCnnn.setText(formatScore(existing.cnnn()));
		txtTin.setText(formatScore(existing.ti()));
		txtKtpl.setText(formatScore(existing.ktpl()));
		txtNl1.setText(formatScore(existing.nl1()));
		txtNk1.setText(formatScore(existing.nk1()));
		txtNk2.setText(formatScore(existing.nk2()));
		txtNk3.setText(formatScore(existing.nk3()));
		txtNk4.setText(formatScore(existing.nk4()));
		txtNk5.setText(formatScore(existing.nk5()));
		txtNk6.setText(formatScore(existing.nk6()));
	}

	private String formatScore(BigDecimal value) {
		if (value == null) {
			return "";
		}
		return scoreFormat.format(value);
	}

	private void onSave() {
		clearErrors();
		boolean hasError = false;

		String cccd = txtCccd.getText() == null ? "" : txtCccd.getText().trim();
		if (cccd.isBlank()) {
			setError("CCCD", "CCCD không được để trống");
			hasError = true;
		} else if (!cccd.matches("\\d{9,12}")) {
			setError("CCCD", "CCCD phải có 9-12 chữ số");
			hasError = true;
		}

		String soBaoDanh = txtSoBaoDanh.getText() == null ? "" : txtSoBaoDanh.getText().trim();
		if (soBaoDanh.isBlank()) {
			setError("Số báo danh", "Số báo danh không được để trống");
			hasError = true;
		}

		String loaiDiem = cboLoaiDiem.getSelectedItem() == null ? "" : cboLoaiDiem.getSelectedItem().toString();
		if (loaiDiem.isBlank()) {
			setError("Phương thức", "Vui lòng chọn phương thức");
			hasError = true;
		}

		BigDecimal toan = parseScore("Toán", txtToan, THPT_MAX);
		BigDecimal ly = parseScore("Lý", txtLy, THPT_MAX);
		BigDecimal hoa = parseScore("Hóa", txtHoa, THPT_MAX);
		BigDecimal sinh = parseScore("Sinh", txtSinh, THPT_MAX);
		BigDecimal su = parseScore("Sử", txtSu, THPT_MAX);
		BigDecimal dia = parseScore("Địa", txtDia, THPT_MAX);
		BigDecimal van = parseScore("Văn", txtVan, THPT_MAX);
		BigDecimal gdcd = parseScore("GDCD", txtGdcd, THPT_MAX);
		BigDecimal n1Thi = parseScore("N1_THI", txtN1Thi, THPT_MAX);
		BigDecimal n1Cc = parseScore("N1_CC", txtN1Cc, THPT_MAX);
		BigDecimal cncn = parseScore("CNCN", txtCncn, THPT_MAX);
		BigDecimal cnnn = parseScore("CNNN", txtCnnn, THPT_MAX);
		BigDecimal ti = parseScore("Tin học", txtTin, THPT_MAX);
		BigDecimal ktpl = parseScore("KTPL", txtKtpl, THPT_MAX);
		BigDecimal nl1 = parseScore("NL1", txtNl1, DGNL_MAX);
		BigDecimal nk1 = parseScore("NK1", txtNk1, THPT_MAX);
		BigDecimal nk2 = parseScore("NK2", txtNk2, THPT_MAX);
		BigDecimal nk3 = parseScore("NK3", txtNk3, THPT_MAX);
		BigDecimal nk4 = parseScore("NK4", txtNk4, THPT_MAX);
		BigDecimal nk5 = parseScore("NK5", txtNk5, THPT_MAX);
		BigDecimal nk6 = parseScore("NK6", txtNk6, THPT_MAX);

		if (!hasError && !hasAnyScore(toan, ly, hoa, sinh, su, dia, van, gdcd, n1Thi, n1Cc, cncn, cnnn, ti, ktpl,
				nl1, nk1, nk2, nk3, nk4, nk5, nk6)) {
			setError("Toán", "Cần nhập ít nhất một điểm môn");
			hasError = true;
		}

		if (hasError || hasAnyError()) {
			return;
		}

		result = new QuanLiDiemService.DiemInput(
				cccd,
				soBaoDanh,
				loaiDiem,
				toan,
				ly,
				hoa,
				sinh,
				su,
				dia,
				van,
				gdcd,
				n1Thi,
				n1Cc,
				cncn,
				cnnn,
				ti,
				ktpl,
				nl1,
				nk1,
				nk2,
				nk3,
				nk4,
				nk5,
				nk6);
		dispose();
	}

	private BigDecimal parseScore(String label, JTextField field, BigDecimal max) {
		String raw = field.getText() == null ? "" : field.getText().trim();
		if (raw.isBlank()) {
			return null;
		}
		String normalized = raw.replace(',', '.');
		try {
			BigDecimal value = new BigDecimal(normalized);
			if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(max) > 0) {
				setError(label, "Điểm phải nằm trong khoảng 0 đến " + max.stripTrailingZeros().toPlainString());
				return null;
			}
			return value;
		} catch (NumberFormatException ex) {
			setError(label, "Điểm không hợp lệ");
			return null;
		}
	}

	private boolean hasAnyScore(BigDecimal... values) {
		for (BigDecimal value : values) {
			if (value != null) {
				return true;
			}
		}
		return false;
	}

	private void clearErrors() {
		for (FieldBinding binding : bindings) {
			binding.errorLabel.setText(" ");
		}
	}

	private boolean hasAnyError() {
		for (FieldBinding binding : bindings) {
			if (binding.errorLabel.getText() != null && !binding.errorLabel.getText().isBlank()) {
				if (!" ".equals(binding.errorLabel.getText())) {
					return true;
				}
			}
		}
		return false;
	}

	private void setError(String label, String message) {
		for (FieldBinding binding : bindings) {
			if (binding.label.equalsIgnoreCase(label)) {
				binding.errorLabel.setText(message);
				return;
			}
		}
	}

	private static class FieldBinding {
		private final String label;
		private final JLabel errorLabel;
		private final JPanel container;

		private FieldBinding(String label, JComponent input, JLabel errorLabel, JPanel container) {
			this.label = label;
			this.errorLabel = errorLabel;
			this.container = container;
		}
	}
}