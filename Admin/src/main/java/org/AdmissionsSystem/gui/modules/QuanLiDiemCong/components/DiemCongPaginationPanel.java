package org.AdmissionsSystem.gui.modules.QuanLiDiemCong.components;

import org.AdmissionsSystem.gui.common.Style;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DiemCongPaginationPanel extends JPanel {
    private final JLabel pageInfoLabel;
    private final JButton prevBtn;
    private final JButton nextBtn;
    private final JComboBox<Integer> pageSizeCombo;
    private Runnable onPrev;
    private Runnable onNext;
    private Consumer<Integer> onPageSizeChange;

    public DiemCongPaginationPanel(int initialPageSize) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        pageInfoLabel = new JLabel("Trang 1 / 1 (0 dòng)");
        pageInfoLabel.setFont(Style.TABLE_FONT);

        prevBtn = new JButton("< Trang trước");
        nextBtn = new JButton("Trang sau >");
        Style.styleFunctionButton(prevBtn, Style.BTN_FILTER_RESET);
        Style.styleFunctionButton(nextBtn, Style.BTN_FILTER_RESET);
        prevBtn.addActionListener(e -> {
            if (onPrev != null) onPrev.run();
        });
        nextBtn.addActionListener(e -> {
            if (onNext != null) onNext.run();
        });

        Integer[] sizes = {10, 20, 50, 100};
        pageSizeCombo = new JComboBox<>(sizes);
        pageSizeCombo.setSelectedItem(initialPageSize);
        pageSizeCombo.addActionListener(e -> {
            if (onPageSizeChange != null) {
                onPageSizeChange.accept((Integer) pageSizeCombo.getSelectedItem());
            }
        });

        add(pageInfoLabel);
        add(Box.createHorizontalGlue());
        add(new JLabel("Kích thước trang:"));
        add(Box.createRigidArea(new Dimension(8, 0)));
        add(pageSizeCombo);
        add(Box.createRigidArea(new Dimension(16, 0)));
        add(prevBtn);
        add(Box.createRigidArea(new Dimension(8, 0)));
        add(nextBtn);
    }

    public void setPageInfo(int currentPage, int totalPages, int totalRows) {
        pageInfoLabel.setText("Trang " + currentPage + " / " + totalPages + " (" + totalRows + " dòng)");
    }

    public void setNavigationEnabled(boolean prevEnabled, boolean nextEnabled) {
        prevBtn.setEnabled(prevEnabled);
        nextBtn.setEnabled(nextEnabled);
    }

    public void setOnPrev(Runnable callback) {
        this.onPrev = callback;
    }

    public void setOnNext(Runnable callback) {
        this.onNext = callback;
    }

    public void setOnPageSizeChange(Consumer<Integer> callback) {
        this.onPageSizeChange = callback;
    }
}
