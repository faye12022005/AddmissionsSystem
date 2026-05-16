package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.components.CustomTable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

public class ImportPreviewDialog extends JDialog {
	private static final int DEFAULT_PAGE_SIZE = 20;

	private final DefaultTableModel tableModel;
	private final JTable table;
	private final DiemPaginationPanel paginationPanel = new DiemPaginationPanel(DEFAULT_PAGE_SIZE);
	private final List<Object[]> rows;
	private final JButton btnConfirm = new JButton("Xác nhận import");

	private int currentPage = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private boolean confirmed;

	private ImportPreviewDialog(Window owner,
			String title,
			String[] columns,
			List<Object[]> rows,
			String summaryText,
			String errorTitle,
			String[] errorColumns,
			List<Object[]> errorRows) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		this.rows = rows == null ? new ArrayList<>() : new ArrayList<>(rows);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(980, 620);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(10, 10));

		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		CustomTable customTable = new CustomTable(tableModel);
		table = customTable.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		add(customTable, BorderLayout.CENTER);

		JPanel summaryPanel = new JPanel(new BorderLayout(8, 8));
		summaryPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		summaryPanel.setOpaque(false);

		JLabel summaryLabel = new JLabel(summaryText);
		summaryLabel.setFont(Style.PANEL_TITLE_FONT);
		summaryLabel.setForeground(new Color(15, 23, 42));

		JButton btnViewErrors = new JButton("Xem dòng lỗi");
		Style.styleFunctionButton(btnViewErrors, Style.BTN_DELETE, Color.WHITE);
		btnViewErrors.setEnabled(errorRows != null && !errorRows.isEmpty());

		btnViewErrors.addActionListener(e -> showErrorDialog(errorTitle, errorColumns, errorRows));

		JPanel summaryRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		summaryRight.setOpaque(false);
		summaryRight.add(btnViewErrors);

		summaryPanel.add(summaryLabel, BorderLayout.WEST);
		summaryPanel.add(summaryRight, BorderLayout.EAST);

		add(summaryPanel, BorderLayout.NORTH);

		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);
		footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 8, 10));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.setOpaque(false);

		JButton btnCancel = new JButton("Hủy");
		Style.styleFunctionButton(btnCancel, Style.BTN_CLEAR, Color.WHITE);
		Style.styleFunctionButton(btnConfirm, Style.BTN_ADD, Color.WHITE);

		btnConfirm.setEnabled(rows != null && !rows.isEmpty());

		btnCancel.addActionListener(e -> dispose());
		btnConfirm.addActionListener(e -> {
			confirmed = true;
			dispose();
		});

		buttons.add(btnCancel);
		buttons.add(btnConfirm);

		footer.add(paginationPanel, BorderLayout.WEST);
		footer.add(buttons, BorderLayout.EAST);

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

		loadPage();
	}

	public static boolean showDialog(Component parent,
			String title,
			String[] columns,
			List<Object[]> rows,
			String summaryText,
			String errorTitle,
			String[] errorColumns,
			List<Object[]> errorRows) {
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		ImportPreviewDialog dialog = new ImportPreviewDialog(owner, title, columns, rows, summaryText,
				errorTitle, errorColumns, errorRows);
		dialog.setVisible(true);
		return dialog.confirmed;
	}

	private void loadPage() {
		tableModel.setRowCount(0);

		if (rows.isEmpty()) {
			paginationPanel.setPageInfo(1, 1, 0L);
			paginationPanel.setNavigationEnabled(false, false);
			return;
		}

		int totalPages = getTotalPages();
		if (currentPage > totalPages) {
			currentPage = totalPages;
		}

		int from = (currentPage - 1) * pageSize;
		int to = Math.min(from + pageSize, rows.size());

		for (int i = from; i < to; i++) {
			tableModel.addRow(rows.get(i));
		}

		paginationPanel.setPageInfo(currentPage, totalPages, rows.size());
		paginationPanel.setNavigationEnabled(currentPage > 1, currentPage < totalPages);
	}

	private int getTotalPages() {
		if (rows.isEmpty()) {
			return 1;
		}
		return (int) Math.ceil(rows.size() * 1.0 / pageSize);
	}

	private void showErrorDialog(String title, String[] columns, List<Object[]> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}

		DefaultTableModel errorModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		for (Object[] row : rows) {
			errorModel.addRow(row);
		}

		CustomTable errorTable = new CustomTable(errorModel);
		JTable table = errorTable.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JDialog errorDialog = new JDialog(this, title, ModalityType.APPLICATION_MODAL);
		errorDialog.setLayout(new BorderLayout(8, 8));
		errorDialog.setSize(840, 520);
		errorDialog.setLocationRelativeTo(this);

		errorDialog.add(errorTable, BorderLayout.CENTER);

		JButton btnClose = new JButton("Đóng");
		Style.styleFunctionButton(btnClose, Style.BTN_CLEAR, Color.WHITE);
		btnClose.addActionListener(e -> errorDialog.dispose());

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
		footer.add(btnClose);

		errorDialog.add(footer, BorderLayout.SOUTH);
		errorDialog.setVisible(true);
	}
}
