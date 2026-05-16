package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.AdmissionsSystem.models.XtNganh;

public class DanhSachTrungTuyenTheoNganhDialog extends JDialog {

    private static final Color PRIMARY = new Color(0x13, 0x7f, 0xec);
    private static final Color WHITE = Color.WHITE;
    private static final Color BG_LIGHT = new Color(0xf6, 0xf7, 0xf8);
    private static final Color TEXT_DARK = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED = new Color(0x94, 0xa3, 0xb8);
    private static final Color TEXT_SLATE = new Color(0x47, 0x55, 0x69);
    private static final Color BORDER_COLOR = new Color(0xcb, 0xd5, 0xe1);
    private static final Color SURFACE = new Color(0xf1, 0xf5, 0xf9);

    private static final Font FONT_BOLD_18 = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_BOLD_12 = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_PLAIN_13 = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_BOLD_10 = new Font("SansSerif", Font.BOLD, 10);

    private static final DecimalFormat SCORE_FMT = new DecimalFormat("0.00");

    private static final String[] COL_NAMES = {
        "STT", "THI SINH", "CCCD", "SBD", "TO HOP", "PHUONG THUC", "DIEM XT", "NV"
    };

    private static final int RECORDS_PER_PAGE = 8;

    private final List<NguyenVongPanel.NguyenVong> allRows;
    private final List<NguyenVongPanel.NguyenVong> filteredRows = new ArrayList<>();
    private final List<NguyenVongPanel.NguyenVong> pageRows = new ArrayList<>();
    private final List<XtNganh> nganhList;

    private DefaultTableModel tableModel;
    private JLabel paginationInfo;
    private JPanel paginationBtnGroup;
    private JLabel subtitleLabel;
    private JComboBox<ComboItem> nganhCombo;
    private int currentPage = 1;

    public DanhSachTrungTuyenTheoNganhDialog(Frame owner,
                                             List<NguyenVongPanel.NguyenVong> rows,
                                             List<XtNganh> nganhList) {
        super(owner, "Danh sach trung tuyen theo nganh", true);
        this.allRows = rows == null ? new ArrayList<>() : new ArrayList<>(rows);
        this.nganhList = nganhList == null ? new ArrayList<>() : new ArrayList<>(nganhList);
        initUi();
    }

    private void initUi() {
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.setBorder(new EmptyBorder(16, 20, 20, 20));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);

        applyFilter();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Danh sach trung tuyen theo nganh");
        title.setFont(FONT_BOLD_18);
        title.setForeground(TEXT_DARK);

        subtitleLabel = new JLabel("");
        subtitleLabel.setFont(FONT_PLAIN_12);
        subtitleLabel.setForeground(TEXT_MUTED);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLabel);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(FONT_BOLD_12);
        closeBtn.setForeground(new Color(0xdc, 0x26, 0x26));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        header.add(titleBlock, BorderLayout.CENTER);
        header.add(closeBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildFilters());
        content.add(Box.createVerticalStrut(14));
        content.add(buildTable());
        return content;
    }

    private JPanel buildFilters() {
        RoundedPanel card = new RoundedPanel(12, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(12, 14, 12, 14)
        ));
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel label = new JLabel("Nganh:");
        label.setFont(FONT_PLAIN_12);
        label.setForeground(TEXT_SLATE);

        nganhCombo = new JComboBox<>();
        nganhCombo.setFont(FONT_PLAIN_12);
        nganhCombo.setBackground(new Color(0xf8, 0xfa, 0xfc));
        nganhCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(9, BORDER_COLOR),
            new EmptyBorder(4, 6, 4, 6)
        ));
        nganhCombo.addItem(new ComboItem("", "Tat ca nganh"));
        for (XtNganh nganh : nganhList) {
            String ma = safeText(nganh.getManganh());
            String ten = safeText(nganh.getTennganh());
            if (!ma.isEmpty()) {
                nganhCombo.addItem(new ComboItem(ma, ma + " - " + ten));
            }
        }
        nganhCombo.addActionListener(e -> {
            currentPage = 1;
            applyFilter();
        });

        left.add(label);
        left.add(nganhCombo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        paginationInfo = new JLabel();
        paginationInfo.setFont(FONT_PLAIN_12);
        paginationInfo.setForeground(TEXT_MUTED);
        right.add(paginationInfo);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildTable() {
        RoundedPanel card = new RoundedPanel(12, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(COL_NAMES, COL_NAMES.length) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xf1, 0xf5, 0xf9));
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(0xef, 0xf6, 0xff));
        table.setSelectionForeground(TEXT_DARK);
        table.setFont(FONT_PLAIN_13);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0xf8, 0xfa, 0xfc));
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_BOLD_10);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));

        int[] widths = {50, 220, 120, 90, 90, 120, 90, 60};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(FONT_BOLD_12);
                label.setForeground(TEXT_SLATE);
                return label;
            }
        });

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(FONT_BOLD_12);
                label.setForeground(PRIMARY);
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(WHITE);

        card.add(scroll, BorderLayout.CENTER);
        card.add(buildPagination(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPagination() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(0xf8, 0xfa, 0xfc));
        row.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(8, 14, 8, 14)
        ));

        paginationBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        paginationBtnGroup.setOpaque(false);
        updatePaginationButtons();

        row.add(paginationBtnGroup, BorderLayout.EAST);
        return row;
    }

    private void updatePaginationButtons() {
        paginationBtnGroup.removeAll();
        int totalPages = (filteredRows.size() + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        if (totalPages == 0) {
            paginationBtnGroup.revalidate();
            paginationBtnGroup.repaint();
            return;
        }

        JButton prevBtn = buildNavButton("‹", currentPage > 1);
        prevBtn.addActionListener(e -> goToPage(currentPage - 1));
        paginationBtnGroup.add(prevBtn);

        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, currentPage + 1);
        if (startPage > 1) {
            addPageButton(1, false);
            if (startPage > 2) {
                paginationBtnGroup.add(buildDots());
            }
        }
        for (int p = startPage; p <= endPage; p++) {
            addPageButton(p, p == currentPage);
        }
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                paginationBtnGroup.add(buildDots());
            }
            addPageButton(totalPages, false);
        }

        JButton nextBtn = buildNavButton("›", currentPage < totalPages);
        nextBtn.addActionListener(e -> goToPage(currentPage + 1));
        paginationBtnGroup.add(nextBtn);

        paginationBtnGroup.revalidate();
        paginationBtnGroup.repaint();
    }

    private JLabel buildDots() {
        JLabel dots = new JLabel("...");
        dots.setFont(FONT_PLAIN_12);
        dots.setForeground(TEXT_MUTED);
        dots.setBorder(new EmptyBorder(0, 4, 0, 4));
        return dots;
    }

    private JButton buildNavButton(String text, boolean enabled) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(FONT_PLAIN_12);
        btn.setForeground(TEXT_SLATE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setEnabled(enabled);
        return btn;
    }

    private void addPageButton(int pageNum, boolean active) {
        JButton btn = new JButton(String.valueOf(pageNum)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active ? PRIMARY : getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(active ? FONT_BOLD_12 : FONT_PLAIN_12);
        btn.setForeground(active ? WHITE : TEXT_SLATE);
        btn.setBackground(active ? PRIMARY : new Color(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        int page = pageNum;
        btn.addActionListener(e -> goToPage(page));
        if (!active) {
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(0xe2, 0xe8, 0xf0));
                    btn.repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(0, 0, 0, 0));
                    btn.repaint();
                }
            });
        }
        paginationBtnGroup.add(btn);
    }

    private void goToPage(int pageNum) {
        int totalPages = (filteredRows.size() + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        if (pageNum < 1 || pageNum > totalPages) {
            return;
        }
        currentPage = pageNum;
        refreshTableData(pageNum);
        updatePaginationButtons();
    }

    private void applyFilter() {
        filteredRows.clear();
        String selectedMa = "";
        String selectedLabel = "Tat ca nganh";
        ComboItem item = (ComboItem) nganhCombo.getSelectedItem();
        if (item != null) {
            selectedMa = safeText(item.value);
            selectedLabel = safeText(item.label);
        }

        for (NguyenVongPanel.NguyenVong row : allRows) {
            if (!"trungtuyen".equalsIgnoreCase(stripAccent(row.getTrangThai()))) {
                continue;
            }
            if (!selectedMa.isEmpty() && !safeText(row.getMaNganh()).equalsIgnoreCase(selectedMa)) {
                continue;
            }
            filteredRows.add(row);
        }

        subtitleLabel.setText("Nganh dang xem: " + selectedLabel);
        refreshTableData(currentPage);
        updatePaginationButtons();
        paginationInfo.setText(getPaginationText());
    }

    private void refreshTableData(int pageNum) {
        tableModel.setRowCount(0);
        pageRows.clear();
        int startIdx = (pageNum - 1) * RECORDS_PER_PAGE;
        int endIdx = Math.min(startIdx + RECORDS_PER_PAGE, filteredRows.size());
        for (int i = startIdx; i < endIdx; i++) {
            NguyenVongPanel.NguyenVong row = filteredRows.get(i);
            pageRows.add(row);
            tableModel.addRow(new Object[] {
                String.valueOf(i + 1),
                row.getTenThiSinh(),
                safeText(row.getCccd()),
                safeText(row.getSbd()),
                safeText(row.getToHop()),
                safeText(row.getPhuongThuc()),
                formatScore(resolveDiemXetTuyen(row.getDiemXettuyen(), row.getDiemThxt())),
                safeText(row.getThuTu())
            });
        }
    }

    private String getPaginationText() {
        int total = filteredRows.size();
        if (total == 0) {
            return "Hien thi 0-0 trong so 0 trung tuyen";
        }
        int startRecord = (currentPage - 1) * RECORDS_PER_PAGE + 1;
        int endRecord = Math.min(currentPage * RECORDS_PER_PAGE, total);
        return "Hien thi " + startRecord + "-" + endRecord + " trong so " + total + " trung tuyen";
    }

    private BigDecimal resolveDiemXetTuyen(BigDecimal diemXet, BigDecimal diemThxt) {
        return diemXet != null ? diemXet : diemThxt;
    }

    private String formatScore(BigDecimal value) {
        if (value == null) {
            return "--";
        }
        return SCORE_FMT.format(value);
    }

    private String safeText(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String stripAccent(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .replace(" ", "");
    }

    private static class ComboItem {
        private final String value;
        private final String label;

        private ComboItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
        }
    }

    static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }
    }
}
