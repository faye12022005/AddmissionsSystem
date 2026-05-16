package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import java.awt.FlowLayout;
import java.util.function.IntConsumer;

public class DiemPaginationPanel extends JPanel {
	private final JButton btnPrev = new JButton("<");
	private final JButton btnNext = new JButton(">");
	private final JLabel lblPageJump = new JLabel("Trang:");
	private final JSpinner spnPage = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
	private final JLabel lblPageInfo = new JLabel("Trang 1/1", SwingConstants.RIGHT);
	private final JComboBox<Integer> cboPageSize = new JComboBox<>(new Integer[] { 10, 20, 50, 100, 200, 500, 1000 });
	private boolean isUpdatingPage;
	private IntConsumer pageJumpListener;

	public DiemPaginationPanel(int initialPageSize) {
		setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 6));
		setOpaque(false);

		Style.stylePaginationInfoLabel(lblPageInfo);
		Style.stylePaginationCombo(cboPageSize);
		Style.stylePaginationButton(btnPrev);
		Style.stylePaginationButton(btnNext);
		Style.stylePaginationInfoLabel(lblPageJump);
		stylePageSpinner();

		cboPageSize.setSelectedItem(initialPageSize);

		add(cboPageSize);
		add(btnPrev);
		add(btnNext);
		add(lblPageJump);
		add(spnPage);
		add(lblPageInfo);

		ChangeListener changeListener = e -> {
			if (isUpdatingPage || pageJumpListener == null) {
				return;
			}
			Object value = spnPage.getValue();
			if (value instanceof Integer page) {
				pageJumpListener.accept(page);
			}
		};
		spnPage.addChangeListener(changeListener);
	}

	public void setOnPageSizeChange(IntConsumer callback) {
		cboPageSize.addActionListener(e -> {
			Integer selected = (Integer) cboPageSize.getSelectedItem();
			if (selected != null) {
				callback.accept(selected);
			}
		});
	}

	public void setOnPrev(Runnable callback) {
		btnPrev.addActionListener(e -> callback.run());
	}

	public void setOnNext(Runnable callback) {
		btnNext.addActionListener(e -> callback.run());
	}

	public void setOnPageJump(IntConsumer callback) {
		this.pageJumpListener = callback;
	}

	public void setPageInfo(int currentPage, int totalPages, long totalRows) {
		lblPageInfo.setText("Trang " + currentPage + "/" + totalPages + " - Tổng " + totalRows + " bản ghi");
		updateSpinner(currentPage, totalPages);
	}

	public void setNavigationEnabled(boolean canPrev, boolean canNext) {
		btnPrev.setEnabled(canPrev);
		btnNext.setEnabled(canNext);
	}

	private void updateSpinner(int currentPage, int totalPages) {
		int safeTotal = Math.max(1, totalPages);
		int safePage = Math.min(Math.max(1, currentPage), safeTotal);
		isUpdatingPage = true;
		SpinnerNumberModel model = (SpinnerNumberModel) spnPage.getModel();
		model.setMinimum(1);
		model.setMaximum(safeTotal);
		model.setValue(safePage);
		spnPage.setEnabled(safeTotal > 1);
		isUpdatingPage = false;
	}

	private void stylePageSpinner() {
		spnPage.setFont(Style.BUTTON_FONT);
		spnPage.setBorder(BorderFactory.createLineBorder(Style.BORDER_SOFT));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnPage, "#");
		JFormattedTextField field = editor.getTextField();
		field.setFont(Style.BUTTON_FONT);
		field.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		field.setColumns(3);
		spnPage.setEditor(editor);
	}
}
