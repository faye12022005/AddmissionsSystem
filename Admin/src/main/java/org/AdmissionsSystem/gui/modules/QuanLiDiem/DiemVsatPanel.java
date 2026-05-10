package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.controller.QuanLiDiemController;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.Toast;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemVSATService;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiemVsatPanel extends JPanel implements DiemTabActions {
	private static final int DEFAULT_PAGE_SIZE = 20;

	private static final String[] COLUMN_NAMES = {
			"ID",
			"CCCD",
			"Họ tên",
			"Đợt thi",
			"Toán",
			"Văn",
			"Anh",
			"Lý",
			"Hóa",
			"Sinh",
			"Sử",
			"Địa"
	};

	private final QuanLiDiemController controller;
	private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final DiemPaginationPanel paginationPanel = new DiemPaginationPanel(DEFAULT_PAGE_SIZE);
	private final JTable table;
	private final DecimalFormat scoreFormat = new DecimalFormat("0.##");

	private int currentPage = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private String currentSearchText = "";
	private List<QuanLiDiemVSATService.VsatRecord> currentRows = new ArrayList<>();
	private List<QuanLiDiemVSATService.VsatRecord> currentPageRows = new ArrayList<>();

	public DiemVsatPanel(QuanLiDiemController controller) {
		this.controller = controller;

		setLayout(new BorderLayout(8, 8));
		setOpaque(false);

		CustomTable customTable = new CustomTable(tableModel);
		table = customTable.getTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (table.getColumnModel().getColumnCount() > 0) {
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(1).setPreferredWidth(120);
			table.getColumnModel().getColumn(2).setPreferredWidth(160);
			table.getColumnModel().getColumn(3).setPreferredWidth(120);
		}

		add(customTable, BorderLayout.CENTER);

		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);
		footer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		footer.add(paginationPanel, BorderLayout.EAST);
		add(footer, BorderLayout.SOUTH);

		paginationPanel.setOnPageSizeChange(selected -> {
			pageSize = selected;
			currentPage = 1;
			loadPage();
		});
		paginationPanel.setOnPrev(() -> {
			if (currentPage > 1) {
				currentPage--;
				loadPage();
			}
		});
		paginationPanel.setOnNext(() -> {
			if (currentPage < getTotalPages()) {
				currentPage++;
				loadPage();
			}
		});

		scoreFormat.setGroupingUsed(false);
		reloadTable();
	}

	@Override
	public void onSearch(String query) {
		currentSearchText = query == null ? "" : query.trim();
		reloadTable();
	}

	@Override
	public void onAdd() {
		QuanLiDiemVSATService.VsatInput input = DiemVsatModal.showDialog(this, "Thêm điểm VSAT", null);
		if (input == null) {
			return;
		}

		try {
			controller.themVsat(input);
			reloadTable();
			Toast.showToast(this, "Đã thêm điểm VSAT.", false);
		} catch (IllegalArgumentException ex) {
			Toast.showToast(this, ex.getMessage(), true);
		}
	}

	@Override
	public void onEdit() {
		QuanLiDiemVSATService.VsatRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để sửa.", true);
			return;
		}

		QuanLiDiemVSATService.VsatInput updatedInput = DiemVsatModal.showDialog(this, "Cập nhật điểm VSAT", selected);
		if (updatedInput == null) {
			return;
		}

		try {
			Optional<QuanLiDiemVSATService.VsatRecord> updated = controller.capNhatVsat(selected, updatedInput);
			if (updated.isEmpty()) {
				Toast.showToast(this, "Bản ghi đã bị thay đổi, vui lòng tải lại danh sách.", true);
				reloadTable();
				return;
			}

			reloadTable();
			Toast.showToast(this, "Đã cập nhật thông tin điểm.", false);
		} catch (IllegalArgumentException ex) {
			Toast.showToast(this, ex.getMessage(), true);
		}
	}

	@Override
	public void onDelete() {
		QuanLiDiemVSATService.VsatRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để xóa.", true);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(
				this,
				"Bạn có chắc chắn muốn xóa bản ghi điểm này?",
				"Xác nhận xóa",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		if (!controller.xoaVsat(selected)) {
			Toast.showToast(this, "Không thể xóa bản ghi. Dữ liệu có thể đã thay đổi.", true);
			reloadTable();
			return;
		}

		reloadTable();
		Toast.showToast(this, "Đã xóa bản ghi điểm.", false);
	}

	@Override
	public void onView() {
		QuanLiDiemVSATService.VsatRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để xem chi tiết.", true);
			return;
		}

		QuanLiDiemVSATService.VsatInput updatedInput = DiemVsatModal.showDialog(this, "Chi tiết điểm VSAT", selected);
		if (updatedInput == null) {
			return;
		}

		try {
			Optional<QuanLiDiemVSATService.VsatRecord> updated = controller.capNhatVsat(selected, updatedInput);
			if (updated.isEmpty()) {
				Toast.showToast(this, "Bản ghi đã bị thay đổi, vui lòng tải lại danh sách.", true);
				reloadTable();
				return;
			}

			reloadTable();
			Toast.showToast(this, "Đã cập nhật thông tin điểm.", false);
		} catch (IllegalArgumentException ex) {
			Toast.showToast(this, ex.getMessage(), true);
		}
	}

	@Override
	public void onImport() {
		try {
			QuanLiDiemVSATService.ImportPreview preview = controller.previewImportVsat(this);
			if (preview == null || preview.totalCount() == 0) {
				Toast.showToast(this, "Không có dữ liệu nào được import.", true);
				return;
			}

			List<Object[]> previewRows = new ArrayList<>();
			for (QuanLiDiemVSATService.VsatInput input : preview.validRows()) {
				previewRows.add(toPreviewRow(input));
			}

			List<Object[]> errorRows = buildErrorRows(preview.errors());
			String summary = buildSummary(preview.totalCount(), preview.validCount(), preview.errorCount());

			boolean confirmed = ImportPreviewDialog.showDialog(
					this,
					"Xem trước dữ liệu import",
					COLUMN_NAMES,
					previewRows,
					summary,
					"Dòng lỗi import",
					new String[] { "Dòng", "CCCD", "Đợt thi", "Lỗi" },
					errorRows);

			if (!confirmed) {
				return;
			}

			int importedRows = controller.commitImportVsat(preview);
			if (importedRows == 0) {
				Toast.showToast(this, "Không có bản ghi hợp lệ để import.", true);
				return;
			}

			reloadTable();
			String message = "Import thành công " + importedRows + " bản ghi.";
			if (preview.errorCount() > 0) {
				message += " Bỏ qua " + preview.errorCount() + " dòng lỗi.";
			}
			Toast.showToast(this, message, false);
		} catch (IllegalArgumentException ex) {
			Toast.showToast(this, ex.getMessage(), true);
		} catch (Exception ex) {
			Toast.showToast(this, "Không thể đọc file import: " + ex.getMessage(), true);
		}
	}

	@Override
	public void onRefresh() {
		currentSearchText = "";
		currentPage = 1;
		reloadTable();
	}

	private void reloadTable() {
		currentRows = controller.getDanhSachVsat(currentSearchText);
		currentPage = 1;
		loadPage();
	}

	private void loadPage() {
		tableModel.setRowCount(0);
		currentPageRows = new ArrayList<>();

		if (currentRows.isEmpty()) {
			paginationPanel.setPageInfo(1, 1, 0);
			paginationPanel.setNavigationEnabled(false, false);
			return;
		}

		int totalPages = getTotalPages();
		if (currentPage > totalPages) {
			currentPage = totalPages;
		}

		int from = (currentPage - 1) * pageSize;
		int to = Math.min(from + pageSize, currentRows.size());

		for (int i = from; i < to; i++) {
			QuanLiDiemVSATService.VsatRecord row = currentRows.get(i);
			currentPageRows.add(row);
			tableModel.addRow(toRow(row));
		}

		paginationPanel.setPageInfo(currentPage, totalPages, currentRows.size());
		paginationPanel.setNavigationEnabled(currentPage > 1, currentPage < totalPages);
	}

	private int getTotalPages() {
		if (currentRows.isEmpty()) {
			return 1;
		}
		return (int) Math.ceil(currentRows.size() * 1.0 / pageSize);
	}

	private QuanLiDiemVSATService.VsatRecord selectedRecord() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return null;
		}

		int modelRow = table.convertRowIndexToModel(selectedRow);
		if (modelRow < 0 || modelRow >= currentPageRows.size()) {
			return null;
		}
		return currentPageRows.get(modelRow);
	}

	private Object[] toRow(QuanLiDiemVSATService.VsatRecord row) {
		return new Object[] {
				row.id(),
				row.cccd(),
				row.hoTen(),
				row.dotThi(),
				formatScore(row.toan()),
				formatScore(row.van()),
				formatScore(row.anh()),
				formatScore(row.ly()),
				formatScore(row.hoa()),
				formatScore(row.sinh()),
				formatScore(row.su()),
				formatScore(row.dia())
		};
	}

	private Object[] toPreviewRow(QuanLiDiemVSATService.VsatInput row) {
		return new Object[] {
				"",
				row.cccd(),
				"",
				row.dotThi(),
				formatScore(row.toan()),
				formatScore(row.van()),
				formatScore(row.anh()),
				formatScore(row.ly()),
				formatScore(row.hoa()),
				formatScore(row.sinh()),
				formatScore(row.su()),
				formatScore(row.dia())
		};
	}

	private List<Object[]> buildErrorRows(List<QuanLiDiemVSATService.ImportError> errors) {
		List<Object[]> rows = new ArrayList<>();
		if (errors == null) {
			return rows;
		}
		for (QuanLiDiemVSATService.ImportError error : errors) {
			rows.add(new Object[] {
					error.rowNumber(),
					error.cccd(),
					error.dotThi(),
					error.message()
			});
		}
		return rows;
	}

	private String buildSummary(int total, int valid, int invalid) {
		return "Tổng: " + total + " | Hợp lệ: " + valid + " | Lỗi: " + invalid;
	}

	private String formatScore(BigDecimal value) {
		if (value == null) {
			return "";
		}
		return scoreFormat.format(value);
	}
}
