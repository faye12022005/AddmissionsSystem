package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemVSATService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public class DiemVsatModal extends JDialog {
	private static final BigDecimal VSAT_MAX = BigDecimal.valueOf(150);

	private final JTextField txtId = new JTextField();
	private final JTextField txtCccd = new JTextField();
	private final JTextField txtDotThi = new JTextField();

	private final JTextField txtToan = new JTextField();
	private final JTextField txtVan = new JTextField();
	private final JTextField txtAnh = new JTextField();
	private final JTextField txtLy = new JTextField();
	private final JTextField txtHoa = new JTextField();
	private final JTextField txtSinh = new JTextField();
	private final JTextField txtSu = new JTextField();
	private final JTextField txtDia = new JTextField();

	private final List<FieldBinding> bindings = new ArrayList<>();
	private final DecimalFormat scoreFormat = new DecimalFormat("0.##");
	private final FieldBinding idBinding;
	private final boolean isViewMode;

	private QuanLiDiemVSATService.VsatInput result;

	private DiemVsatModal(Window owner, String title, QuanLiDiemVSATService.VsatRecord existing) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(760, 520);
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

		// --- Section: Thông tin cá nhân ---
		JPanel personalInfoPanel = createSectionPanel("Thông tin cá nhân");
		GridBagConstraints gbcSection = new GridBagConstraints();
		gbcSection.gridx = 0;
		gbcSection.gridy = 0;
		gbcSection.gridwidth = 2;
		gbcSection.weightx = 1;
		gbcSection.fill = GridBagConstraints.HORIZONTAL;
		gbcSection.insets = new Insets(6, 0, 6, 0);
		formPanel.add(personalInfoPanel, gbcSection);

		// Add ID field to personal info section if in view mode
		if (isViewMode) {
			addFieldToPanel(personalInfoPanel, "ID", txtId);
		}
		addFieldToPanel(personalInfoPanel, "CCCD", txtCccd);
		addFieldToPanel(personalInfoPanel, "Đợt thi", txtDotThi);

		// --- Section: Điểm VSAT ---
		JPanel vsatScoresPanel = createSectionPanel("Điểm VSAT");
		gbcSection = new GridBagConstraints();
		gbcSection.gridx = 0;
		gbcSection.gridy = 1;
		gbcSection.gridwidth = 2;
		gbcSection.weightx = 1;
		gbcSection.fill = GridBagConstraints.HORIZONTAL;
		gbcSection.insets = new Insets(12, 0, 6, 0);
		formPanel.add(vsatScoresPanel, gbcSection);

		addFieldToPanel(vsatScoresPanel, "Toán", txtToan);
		addFieldToPanel(vsatScoresPanel, "Văn", txtVan);
		addFieldToPanel(vsatScoresPanel, "Anh", txtAnh);
		addFieldToPanel(vsatScoresPanel, "Lý", txtLy);
		addFieldToPanel(vsatScoresPanel, "Hóa", txtHoa);
		addFieldToPanel(vsatScoresPanel, "Sinh", txtSinh);
		addFieldToPanel(vsatScoresPanel, "Sử", txtSu);
		addFieldToPanel(vsatScoresPanel, "Địa", txtDia);

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

	public static QuanLiDiemVSATService.VsatInput showDialog(Component parent, String title,
			QuanLiDiemVSATService.VsatRecord existing) {
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		DiemVsatModal modal = new DiemVsatModal(owner, title, existing);
		modal.setVisible(true);
		return modal.result;
	}

	private JPanel createSectionPanel(String title) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
				title,
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.TOP,
				Style.TABLE_FONT.deriveFont(Font.BOLD, 13f)));
		panel.setOpaque(false);
		return panel;
	}

	private void addFieldToPanel(JPanel panel, String label, JComponent input) {
		FieldBinding binding = createBinding(label, input);
		bindings.add(binding);

		int componentCount = panel.getComponentCount();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = componentCount % 2;
		gbc.gridy = componentCount / 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, gbc.gridx == 0 ? 0 : 8, 4, gbc.gridx == 0 ? 8 : 0);
		panel.add(binding.container, gbc);
	}

	private void setFieldsEditable(boolean editable) {
		txtCccd.setEditable(editable);
		txtDotThi.setEditable(editable);
		txtToan.setEditable(editable);
		txtVan.setEditable(editable);
		txtAnh.setEditable(editable);
		txtLy.setEditable(editable);
		txtHoa.setEditable(editable);
		txtSinh.setEditable(editable);
		txtSu.setEditable(editable);
		txtDia.setEditable(editable);
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

	private void fillFromExisting(QuanLiDiemVSATService.VsatRecord existing) {
		txtId.setText(String.valueOf(existing.id()));
		txtCccd.setText(existing.cccd());
		txtDotThi.setText(existing.dotThi());

		txtToan.setText(formatScore(existing.toan()));
		txtVan.setText(formatScore(existing.van()));
		txtAnh.setText(formatScore(existing.anh()));
		txtLy.setText(formatScore(existing.ly()));
		txtHoa.setText(formatScore(existing.hoa()));
		txtSinh.setText(formatScore(existing.sinh()));
		txtSu.setText(formatScore(existing.su()));
		txtDia.setText(formatScore(existing.dia()));
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

		String dotThi = txtDotThi.getText() == null ? "" : txtDotThi.getText().trim();
		if (dotThi.isBlank()) {
			setError("Đợt thi", "Đợt thi không được để trống");
			hasError = true;
		}

		BigDecimal toan = parseScore("Toán", txtToan, VSAT_MAX);
		BigDecimal van = parseScore("Văn", txtVan, VSAT_MAX);
		BigDecimal anh = parseScore("Anh", txtAnh, VSAT_MAX);
		BigDecimal ly = parseScore("Lý", txtLy, VSAT_MAX);
		BigDecimal hoa = parseScore("Hóa", txtHoa, VSAT_MAX);
		BigDecimal sinh = parseScore("Sinh", txtSinh, VSAT_MAX);
		BigDecimal su = parseScore("Sử", txtSu, VSAT_MAX);
		BigDecimal dia = parseScore("Địa", txtDia, VSAT_MAX);

		if (!hasError && !hasAnyScore(toan, van, anh, ly, hoa, sinh, su, dia)) {
			setError("Toán", "Cần nhập ít nhất một điểm môn");
			hasError = true;
		}

		if (hasError || hasAnyError()) {
			return;
		}

		result = new QuanLiDiemVSATService.VsatInput(
				cccd,
				dotThi,
				toan,
				van,
				anh,
				ly,
				hoa,
				sinh,
				su,
				dia);
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