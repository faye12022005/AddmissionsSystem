package org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh;

import org.AdmissionsSystem.gui.common.Style;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.util.function.IntConsumer;

public class NganhToHopPaginationPanel extends JPanel {
    private final JButton btnPrev = new JButton("<");
    private final JButton btnNext = new JButton(">");
    private final JLabel lblPageInfo = new JLabel("Trang 1/1", SwingConstants.RIGHT);
    private final JComboBox<Integer> cboPageSize = new JComboBox<>(new Integer[]{10, 20, 50, 100});

    public NganhToHopPaginationPanel(int initialPageSize) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        setOpaque(false);

        Style.stylePaginationInfoLabel(lblPageInfo);
        Style.stylePaginationCombo(cboPageSize);
        Style.stylePaginationButton(btnPrev);
        Style.stylePaginationButton(btnNext);

        cboPageSize.setSelectedItem(initialPageSize);

        add(cboPageSize);
        add(btnPrev);
        add(btnNext);
        add(lblPageInfo);
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

    public void setPageInfo(int currentPage, int totalPages, int totalRows) {
        lblPageInfo.setText("Trang " + currentPage + "/" + totalPages + " - Tổng " + totalRows + " bản ghi");
    }

    public void setNavigationEnabled(boolean canPrev, boolean canNext) {
        btnPrev.setEnabled(canPrev);
        btnNext.setEnabled(canNext);
    }
}
