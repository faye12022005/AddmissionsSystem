package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.controller.QuanLiDiemController;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.Toast;
import org.AdmissionsSystem.bus.service.QuanLiDiem.PagedResult;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemService;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiemThptDgnlPanel extends JPanel implements DiemTabActions {
	private static final int DEFAULT_PAGE_SIZE = 20;

	private static final String[] COLUMN_NAMES = {
			"ID",
			"CCCD",
			"Số báo danh",
			// "Phương thức",
			"Toán",
			"Lý",
			"Hóa",
			"Sinh",
			"Sử",
			"Địa",
			"Văn",
			"GDCD",
			"N1_THI",
			"N1_CC",
			"CNCN",
			"CNNN",
			"Tin học",
			"KTPL",
			"NL1",
			"NK1",
			"NK2",
			"NK3",
			"NK4",
			"NK5",
			"NK6"
	};

	private final QuanLiDiemController controller;
	private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JComboBox<String> cboLoaiDiemFilter = new JComboBox<>(buildFilterValues());
	private final DecimalFormat scoreFormat = new DecimalFormat("0.##");
	private final DiemPaginationPanel paginationPanel = new DiemPaginationPanel(DEFAULT_PAGE_SIZE);
	private final JTable table;

	private int currentPage = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private String currentSearchText = "";
	private List<QuanLiDiemService.DiemRecord> currentPageRows = new ArrayList<>();
	private long totalRows;

	public DiemThptDgnlPanel(QuanLiDiemController controller) {
		this.controller = controller;

		setLayout(new BorderLayout(8, 8));
		setOpaque(false);

		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		filterPanel.setOpaque(false);
		filterPanel.add(new JLabel("Phương thức:"));
		filterPanel.add(cboLoaiDiemFilter);

		add(filterPanel, BorderLayout.NORTH);

		CustomTable customTable = new CustomTable(tableModel);
		table = customTable.getTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (table.getColumnModel().getColumnCount() > 0) {
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(1).setPreferredWidth(120);
			table.getColumnModel().getColumn(2).setPreferredWidth(120);
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
		paginationPanel.setOnPageJump(page -> {
			int totalPages = getTotalPages();
			int nextPage = Math.min(Math.max(1, page), totalPages);
			if (nextPage != currentPage) {
				currentPage = nextPage;
				loadPage();
			}
		});

		cboLoaiDiemFilter.addActionListener(e -> reloadTable());

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
		QuanLiDiemService.DiemInput input = DiemModal.showDialog(this, "Thêm điểm thí sinh", null);
		if (input == null) {
			return;
		}

		try {
			controller.them(input);
			reloadTable();
			Toast.showToast(this, "Đã thêm điểm thí sinh.", false);
		} catch (IllegalArgumentException ex) {
			Toast.showToast(this, ex.getMessage(), true);
		}
	}

	@Override
	public void onEdit() {
		QuanLiDiemService.DiemRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để sửa.", true);
			return;
		}

		QuanLiDiemService.DiemInput updatedInput = DiemModal.showDialog(this, "Cập nhật điểm thí sinh", selected);
		if (updatedInput == null) {
			return;
		}

		try {
			Optional<QuanLiDiemService.DiemRecord> updated = controller.capNhat(selected, updatedInput);
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
		QuanLiDiemService.DiemRecord selected = selectedRecord();
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

		if (!controller.xoa(selected)) {
			Toast.showToast(this, "Không thể xóa bản ghi. Dữ liệu có thể đã thay đổi.", true);
			reloadTable();
			return;
		}

		reloadTable();
		Toast.showToast(this, "Đã xóa bản ghi điểm.", false);
	}

	@Override
	public void onView() {
		QuanLiDiemService.DiemRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để xem chi tiết.", true);
			return;
		}

		QuanLiDiemService.DiemInput updatedInput = DiemModal.showDialog(this, "Chi tiết điểm thí sinh", selected);
		if (updatedInput == null) {
			return;
		}

		try {
			Optional<QuanLiDiemService.DiemRecord> updated = controller.capNhat(selected, updatedInput);
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
			QuanLiDiemService.ImportPreview preview = controller.previewImport(this);
			if (preview == null || preview.totalCount() == 0) {
				Toast.showToast(this, "Không có dữ liệu nào được import.", true);
				return;
			}

			List<Object[]> previewRows = new ArrayList<>();
			for (QuanLiDiemService.DiemInput input : preview.validRows()) {
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
					new String[] { "Dòng", "CCCD", "Số báo danh", "Lỗi" },
					errorRows);

			if (!confirmed) {
				return;
			}

			int importedRows = controller.commitImport(preview);
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
		cboLoaiDiemFilter.setSelectedItem(QuanLiDiemService.ALL_OPTION);
		currentPage = 1;
		reloadTable();
	}

	private void reloadTable() {
		currentPage = 1;
		loadPage();
	}

	private void loadPage() {
		tableModel.setRowCount(0);
		currentPageRows = new ArrayList<>();

		String selectedLoai = selectedFilterValue();
		PagedResult<QuanLiDiemService.DiemRecord> pageResult = controller.getDanhSachPage(
				currentSearchText,
				selectedLoai,
				currentPage,
				pageSize);
		totalRows = pageResult.totalRows();

		int totalPages = getTotalPages();
		if (currentPage > totalPages) {
			currentPage = totalPages;
			pageResult = controller.getDanhSachPage(currentSearchText, selectedLoai, currentPage, pageSize);
			totalRows = pageResult.totalRows();
		}

		List<QuanLiDiemService.DiemRecord> rows = pageResult.rows();
		if (rows == null || rows.isEmpty()) {
			paginationPanel.setPageInfo(currentPage, totalPages, totalRows);
			paginationPanel.setNavigationEnabled(false, false);
			return;
		}

		for (QuanLiDiemService.DiemRecord row : rows) {
			currentPageRows.add(row);
			tableModel.addRow(toRow(row));
		}

		paginationPanel.setPageInfo(currentPage, totalPages, totalRows);
		paginationPanel.setNavigationEnabled(currentPage > 1, currentPage < totalPages);
	}

	private int getTotalPages() {
		if (totalRows <= 0) {
			return 1;
		}
		return (int) Math.ceil(totalRows * 1.0 / pageSize);
	}

	private QuanLiDiemService.DiemRecord selectedRecord() {
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

	private Object[] toRow(QuanLiDiemService.DiemRecord row) {
		return new Object[] {
				row.id(),
				row.cccd(),
				row.soBaoDanh(),
				// row.loaiDiem(),
				formatScore(row.to()),
				formatScore(row.li()),
				formatScore(row.ho()),
				formatScore(row.si()),
				formatScore(row.su()),
				formatScore(row.di()),
				formatScore(row.va()),
				formatScore(row.gdcd()),
				formatScore(row.n1Thi()),
				formatScore(row.n1Cc()),
				formatScore(row.cncn()),
				formatScore(row.cnnn()),
				formatScore(row.ti()),
				formatScore(row.ktpl()),
				formatScore(row.nl1()),
				formatScore(row.nk1()),
				formatScore(row.nk2()),
				formatScore(row.nk3()),
				formatScore(row.nk4()),
				formatScore(row.nk5()),
				formatScore(row.nk6())
		};
	}

	private Object[] toPreviewRow(QuanLiDiemService.DiemInput row) {
		return new Object[] {
				"",
				row.cccd(),
				row.soBaoDanh(),
				// row.loaiDiem(),
				formatScore(row.to()),
				formatScore(row.li()),
				formatScore(row.ho()),
				formatScore(row.si()),
				formatScore(row.su()),
				formatScore(row.di()),
				formatScore(row.va()),
				formatScore(row.gdcd()),
				formatScore(row.n1Thi()),
				formatScore(row.n1Cc()),
				formatScore(row.cncn()),
				formatScore(row.cnnn()),
				formatScore(row.ti()),
				formatScore(row.ktpl()),
				formatScore(row.nl1()),
				formatScore(row.nk1()),
				formatScore(row.nk2()),
				formatScore(row.nk3()),
				formatScore(row.nk4()),
				formatScore(row.nk5()),
				formatScore(row.nk6())
		};
	}

	private List<Object[]> buildErrorRows(List<QuanLiDiemService.ImportError> errors) {
		List<Object[]> rows = new ArrayList<>();
		if (errors == null) {
			return rows;
		}
		for (QuanLiDiemService.ImportError error : errors) {
			rows.add(new Object[] {
					error.rowNumber(),
					error.cccd(),
					error.soBaoDanh(),
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

	private String selectedFilterValue() {
		Object selected = cboLoaiDiemFilter.getSelectedItem();
		if (selected == null) {
			return QuanLiDiemService.ALL_OPTION;
		}
		return selected.toString();
	}

	private String[] buildFilterValues() {
		List<String> values = new ArrayList<>();
		values.add(QuanLiDiemService.ALL_OPTION);
		values.addAll(QuanLiDiemService.LOAI_DIEM);
		return values.toArray(new String[0]);
	}
}
