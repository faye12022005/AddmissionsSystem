package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.service.QuanLiDiem.DanhSachDiemXetTuyenService.ChiTietRecord;
import org.AdmissionsSystem.bus.service.QuanLiDiem.DanhSachDiemXetTuyenService.DgnlRecord;
import org.AdmissionsSystem.bus.service.QuanLiDiem.DanhSachDiemXetTuyenService.SummaryRecord;
import org.AdmissionsSystem.bus.service.QuanLiDiem.DanhSachDiemXetTuyenService.ToHopRecord;
import org.AdmissionsSystem.gui.common.Style;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChiTietDiemXetTuyenDialog extends JDialog {
	private final DecimalFormat numberFormat = new DecimalFormat("0.##");

	public static void showDialog(Window owner, ChiTietRecord detail) {
		ChiTietDiemXetTuyenDialog dialog = new ChiTietDiemXetTuyenDialog(owner, detail);
		dialog.setVisible(true);
	}

	public ChiTietDiemXetTuyenDialog(Window owner, ChiTietRecord detail) {
		super(owner, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Chi tiết điểm xét tuyển");
		setPreferredSize(new Dimension(900, 680));

		numberFormat.setGroupingUsed(false);

		SummaryRecord summary = detail.summary();
		String titleText = "Chi tiết điểm xét tuyển của thí sinh " + summary.hoTen()
				+ " - CCCD: " + summary.cccd();
		JLabel title = new JLabel(titleText);
		title.setFont(Style.PANEL_TITLE_FONT);
		title.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

		JPanel content = new JPanel();
		content.setLayout(new javax.swing.BoxLayout(content, javax.swing.BoxLayout.Y_AXIS));
		content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		content.setOpaque(false);

		content.add(buildThongTinChung(summary));
		content.add(buildToHopSection(detail));
		if (detail.allowDgnl()) {
			content.add(buildDgnlSection(detail.dgnlRecord()));
		}
		content.add(buildHighestToHopSection(detail));

		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JButton btnClose = new JButton("Đóng");
		Style.styleFunctionButton(btnClose, Style.BTN_CLEAR, java.awt.Color.WHITE);
		btnClose.addActionListener(e -> dispose());

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
		footer.setOpaque(false);
		footer.add(btnClose);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(title, BorderLayout.NORTH);
		wrapper.add(scrollPane, BorderLayout.CENTER);
		wrapper.add(footer, BorderLayout.SOUTH);

		setContentPane(wrapper);
		pack();
		setLocationRelativeTo(owner);
	}

	private JPanel buildThongTinChung(SummaryRecord summary) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		panel.setBorder(sectionBorder("Thông tin chung"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 6, 4, 12);

		addInfoRow(panel, gbc, "CCCD:", summary.cccd());
		addInfoRow(panel, gbc, "Họ tên:", summary.hoTen());
		addInfoRow(panel, gbc, "Số báo danh:", summary.soBaoDanh());
		addInfoRow(panel, gbc, "Nguyện vọng:", summary.nguyenVong());
		addInfoRow(panel, gbc, "Thứ tự NV:", String.valueOf(summary.thuTuNguyenVong()));

		return panel;
	}

	private JPanel buildToHopSection(ChiTietRecord detail) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(sectionBorder("Điểm tổ hợp môn"));
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

		List<ToHopRecord> records = detail.toHopRecords();
		if (records.isEmpty()) {
			panel.add(new JLabel("Không có tổ hợp môn."));
			return panel;
		}

		List<ToHopRecord> sorted = new ArrayList<>(records);
		sorted.sort(Comparator.comparing(ToHopRecord::maToHop));

		for (int i = 0; i < sorted.size(); i++) {
			ToHopRecord record = sorted.get(i);
			panel.add(buildToHopDetail(record));
			if (i < sorted.size() - 1) {
				panel.add(new JSeparator(SwingConstants.HORIZONTAL));
			}
		}

		return panel;
	}

	private JPanel buildToHopDetail(ToHopRecord record) {
		JPanel panel = new JPanel(new GridLayout(0, 2, 8, 4));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		panel.add(label("Tổ hợp:"));
		panel.add(value(record.maToHop()));

		panel.add(label("Môn 1:"));
		panel.add(value(monLine(record.mon1(), record.diemMon1(), record.heSo1())));
		panel.add(label("Môn 2:"));
		panel.add(value(monLine(record.mon2(), record.diemMon2(), record.heSo2())));
		panel.add(label("Môn 3:"));
		panel.add(value(monLine(record.mon3(), record.diemMon3(), record.heSo3())));

		panel.add(label("Điểm tổ hợp:"));
		panel.add(value(formatScore(record.diemToHop())));
		panel.add(label("Điểm cộng:"));
		panel.add(value(formatScore(record.diemCong())));
		panel.add(label("Điểm ưu tiên:"));
		panel.add(value(formatScore(record.diemUuTien())));
		panel.add(label("Điểm xét tuyển:"));
		panel.add(value(formatScore(record.diemXetTuyen())));

		return panel;
	}

	private JPanel buildDgnlSection(DgnlRecord record) {
		JPanel panel = new JPanel(new GridLayout(0, 2, 8, 4));
		panel.setOpaque(false);
		panel.setBorder(sectionBorder("Điểm ĐGNL"));

		if (record == null) {
			panel.add(label("Thông tin:"));
			panel.add(value("Không có điểm ĐGNL"));
			return panel;
		}

		panel.add(label("Điểm ĐGNL (gốc):"));
		panel.add(value(formatScore(record.diemGoc())));
		panel.add(label("Tổ hợp quy đổi:"));
		panel.add(value(record.toHopQuyDoi()));
		panel.add(label("Điểm quy đổi:"));
		panel.add(value(formatScore(record.diemQuyDoi())));
		panel.add(label("Điểm cộng:"));
		panel.add(value(formatScore(record.diemCong())));
		panel.add(label("Điểm ưu tiên:"));
		panel.add(value(formatScore(record.diemUuTien())));
		panel.add(label("Điểm xét tuyển:"));
		panel.add(value(formatScore(record.diemXetTuyen())));

		return panel;
	}

	private JPanel buildHighestToHopSection(ChiTietRecord detail) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		panel.setOpaque(false);
		panel.setBorder(sectionBorder("Điểm tổ hợp môn cao nhất"));

		String nguon = detail.nguonDiemToHopCaoNhat();
		String display = (nguon == null || nguon.isBlank() ? "" : nguon + " - ")
				+ formatScore(detail.diemToHopCaoNhat());
		JLabel label = new JLabel(display);
		label.setFont(Style.BUTTON_FONT.deriveFont(java.awt.Font.BOLD));
		panel.add(label);

		return panel;
	}

	private TitledBorder sectionBorder(String title) {
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleFont(Style.PANEL_TITLE_FONT.deriveFont(14f));
		return border;
	}

	private void addInfoRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
		JLabel lbl = new JLabel(label);
		lbl.setFont(Style.TABLE_FONT);
		JLabel val = new JLabel(value == null ? "" : value);
		val.setFont(Style.TABLE_FONT);

		gbc.gridx = 0;
		panel.add(lbl, gbc);
		gbc.gridx = 1;
		panel.add(val, gbc);
		gbc.gridy++;
	}

	private JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setFont(Style.TABLE_FONT);
		return label;
	}

	private JLabel value(String text) {
		JLabel label = new JLabel(text == null ? "" : text);
		label.setFont(Style.TABLE_FONT);
		return label;
	}

	private String monLine(String mon, BigDecimal diem, int heSo) {
		return mon + " - " + formatScore(diem) + " (HS " + heSo + ")";
	}

	private String formatScore(BigDecimal value) {
		if (value == null) {
			return "0";
		}
		return numberFormat.format(value);
	}
}