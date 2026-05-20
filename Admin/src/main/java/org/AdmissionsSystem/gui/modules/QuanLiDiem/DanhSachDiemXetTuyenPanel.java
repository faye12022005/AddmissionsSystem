package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.bus.controller.QuanLiDiemController;
import org.AdmissionsSystem.bus.service.QuanLiDiem.DanhSachDiemXetTuyenService;
import org.AdmissionsSystem.bus.service.QuanLiDiem.PagedResult;
import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;
import org.AdmissionsSystem.gui.components.SearchPanel;
import org.AdmissionsSystem.gui.components.Toast;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DanhSachDiemXetTuyenPanel extends JPanel {
	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final String[] COLUMN_NAMES = {
			"CCCD",
			"Họ tên",
			"Nguyện vọng",
			"Điểm tổ hợp môn cao nhất",
			"Điểm cộng",
			"Điểm ưu tiên",
			"Điểm xét tuyển"
	};

	private final QuanLiDiemController controller;
	private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final SearchPanel searchPanel = new SearchPanel(320, "Tìm theo CCCD", "Tìm");
	private final JButton btnView = new JButton("Xem chi tiết");
	private final JButton btnRefresh = new JButton("Làm mới");
	private final DiemPaginationPanel paginationPanel = new DiemPaginationPanel(DEFAULT_PAGE_SIZE);
	private final DecimalFormat scoreFormat = new DecimalFormat("0.##");
	private final JTable table;

	private JPanel loadingOverlay;
	private SwingWorker<PagedResult<DanhSachDiemXetTuyenService.SummaryRecord>, Void> loadWorker;
	private SwingWorker<DanhSachDiemXetTuyenService.ChiTietRecord, Void> detailWorker;

	private int currentPage = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private long totalRows;
	private String currentSearch = "";
	private List<DanhSachDiemXetTuyenService.SummaryRecord> currentRows = new ArrayList<>();

	public DanhSachDiemXetTuyenPanel(QuanLiDiemController controller) {
		this.controller = controller;

		setLayout(new BorderLayout(8, 8));
		setOpaque(false);

		scoreFormat.setGroupingUsed(false);

		JPanel toolbar = buildToolbar();
		add(toolbar, BorderLayout.NORTH);

		CustomTable customTable = new CustomTable(tableModel);
		table = customTable.getTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		if (table.getColumnModel().getColumnCount() > 0) {
			table.getColumnModel().getColumn(0).setPreferredWidth(140); // CCCD
			table.getColumnModel().getColumn(1).setPreferredWidth(170); // Họ tên
			table.getColumnModel().getColumn(2).setPreferredWidth(460); // Nguyện vọng
			table.getColumnModel().getColumn(3).setPreferredWidth(180); // Điểm tổ hợp môn cao nhất
			table.getColumnModel().getColumn(4).setPreferredWidth(130); // Điểm cộng
			table.getColumnModel().getColumn(5).setPreferredWidth(130); // Điểm ưu tiên
			table.getColumnModel().getColumn(6).setPreferredWidth(150); // Điểm xét tuyển
		}

		loadingOverlay = buildLoadingOverlay();
		loadingOverlay.setVisible(false);

		JPanel tableWrapper = new JPanel();
		tableWrapper.setOpaque(false);
		tableWrapper.setLayout(new OverlayLayout(tableWrapper));
		customTable.setAlignmentX(0.5f);
		customTable.setAlignmentY(0.5f);
		loadingOverlay.setAlignmentX(0.5f);
		loadingOverlay.setAlignmentY(0.5f);
		tableWrapper.add(customTable);
		tableWrapper.add(loadingOverlay);

		add(tableWrapper, BorderLayout.CENTER);

		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);
		footer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		footer.add(paginationPanel, BorderLayout.EAST);
		add(footer, BorderLayout.SOUTH);

		bindEvents();
		loadPage();
	}

	private JPanel buildToolbar() {
		JPanel toolbar = new JPanel(new BorderLayout(8, 8));
		toolbar.setOpaque(false);
		toolbar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		leftActions.setOpaque(false);

		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
		rightActions.setOpaque(false);

		Style.styleFunctionButton(btnView, Style.BTN_UPDATE, Color.WHITE);
		Style.styleFunctionButton(btnRefresh, Style.BTN_CLEAR, Color.WHITE);
		btnView.setEnabled(false);

		leftActions.add(btnView);
		leftActions.add(btnRefresh);
		rightActions.add(searchPanel);

		toolbar.add(leftActions, BorderLayout.WEST);
		toolbar.add(rightActions, BorderLayout.EAST);
		return toolbar;
	}

	private void bindEvents() {
		searchPanel.addActionListener(e -> {
			currentSearch = searchPanel.getSearchText();
			currentPage = 1;
			loadPage();
		});

		btnRefresh.addActionListener(e -> {
			currentSearch = "";
			searchPanel.setSearchText("");
			currentPage = 1;
			loadPage();
		});

		btnView.addActionListener(e -> onViewDetail());

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				btnView.setEnabled(selectedRecord() != null);
			}
		});

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
	}

	private void loadPage() {
		if (loadWorker != null && !loadWorker.isDone()) {
			loadWorker.cancel(true);
		}
		setLoading(true);

		loadWorker = new SwingWorker<>() {
			@Override
			protected PagedResult<DanhSachDiemXetTuyenService.SummaryRecord> doInBackground() {
				return controller.getDanhSachXetTuyenPage(currentSearch, currentPage, pageSize);
			}

			@Override
			protected void done() {
				setLoading(false);
				if (isCancelled()) {
					return;
				}
				try {
					PagedResult<DanhSachDiemXetTuyenService.SummaryRecord> result = get();
					currentRows = result.rows();
					totalRows = result.totalRows();
					reloadTable();
				} catch (Exception ex) {
					Toast.showToast(DanhSachDiemXetTuyenPanel.this, "Không thể tải dữ liệu.", true);
				}
			}
		};
		loadWorker.execute();
	}

	private void reloadTable() {
		tableModel.setRowCount(0);
		if (currentRows == null || currentRows.isEmpty()) {
			paginationPanel.setPageInfo(1, 1, 0);
			paginationPanel.setNavigationEnabled(false, false);
			btnView.setEnabled(false);
			return;
		}

		for (DanhSachDiemXetTuyenService.SummaryRecord row : currentRows) {
			tableModel.addRow(new Object[] {
					row.cccd(),
					row.hoTen(),
					row.nguyenVong(),
					formatScore(row.diemToHopCaoNhat()),
					formatScore(row.diemCong()),
					formatScore(row.diemUuTien()),
					formatScore(row.diemXetTuyen())
			});
		}

		paginationPanel.setPageInfo(currentPage, getTotalPages(), totalRows);
		paginationPanel.setNavigationEnabled(currentPage > 1, currentPage < getTotalPages());
		btnView.setEnabled(selectedRecord() != null);
	}

	private int getTotalPages() {
		if (totalRows <= 0) {
			return 1;
		}
		return (int) Math.ceil(totalRows * 1.0 / pageSize);
	}

	private DanhSachDiemXetTuyenService.SummaryRecord selectedRecord() {
		int row = table.getSelectedRow();
		if (row < 0 || currentRows == null || currentRows.isEmpty()) {
			return null;
		}
		int modelRow = table.convertRowIndexToModel(row);
		if (modelRow < 0 || modelRow >= currentRows.size()) {
			return null;
		}
		return currentRows.get(modelRow);
	}

	private void onViewDetail() {
		DanhSachDiemXetTuyenService.SummaryRecord selected = selectedRecord();
		if (selected == null) {
			Toast.showToast(this, "Vui lòng chọn một bản ghi để xem chi tiết.", true);
			return;
		}

		if (detailWorker != null && !detailWorker.isDone()) {
			detailWorker.cancel(true);
		}

		setLoading(true);
		detailWorker = new SwingWorker<>() {
			@Override
			protected DanhSachDiemXetTuyenService.ChiTietRecord doInBackground() {
				return controller.getChiTietXetTuyen(selected.idnv());
			}

			@Override
			protected void done() {
				setLoading(false);
				if (isCancelled()) {
					return;
				}
				try {
					DanhSachDiemXetTuyenService.ChiTietRecord detail = get();
					if (detail == null) {
						Toast.showToast(DanhSachDiemXetTuyenPanel.this, "Không tìm thấy dữ liệu chi tiết.", true);
						return;
					}
					ChiTietDiemXetTuyenDialog.showDialog(
							SwingUtilities.getWindowAncestor(DanhSachDiemXetTuyenPanel.this),
							detail);
				} catch (Exception ex) {
					Toast.showToast(DanhSachDiemXetTuyenPanel.this, "Không thể tải chi tiết.", true);
				}
			}
		};
		detailWorker.execute();
	}

	private JPanel buildLoadingOverlay() {
		JPanel overlay = new JPanel(new GridBagLayout());
		overlay.setOpaque(true);
		overlay.setBackground(new Color(15, 23, 42, 120));

		JLabel label = new JLabel("Đang tải dữ liệu...");
		label.setForeground(Color.WHITE);
		label.setFont(Style.PANEL_TITLE_FONT);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		overlay.add(label, gbc);

		return overlay;
	}

	private void setLoading(boolean loading) {
		loadingOverlay.setVisible(loading);
		btnView.setEnabled(!loading && selectedRecord() != null);
		btnRefresh.setEnabled(!loading);
	}

	private String formatScore(BigDecimal value) {
		if (value == null) {
			return "0";
		}
		return scoreFormat.format(value);
	}
}
