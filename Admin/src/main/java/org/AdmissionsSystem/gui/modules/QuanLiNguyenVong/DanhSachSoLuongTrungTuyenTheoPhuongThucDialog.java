package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DanhSachSoLuongTrungTuyenTheoPhuongThucDialog extends JDialog {

    private static final Color PRIMARY = new Color(0x13, 0x7f, 0xec);
    private static final Color WHITE = Color.WHITE;
    private static final Color BG_LIGHT = new Color(0xf6, 0xf7, 0xf8);
    private static final Color TEXT_DARK = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED = new Color(0x94, 0xa3, 0xb8);
    private static final Color TEXT_SLATE = new Color(0x47, 0x55, 0x69);
    private static final Color BORDER_COLOR = new Color(0xcb, 0xd5, 0xe1);

    private static final Font FONT_BOLD_18 = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_BOLD_12 = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_PLAIN_13 = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_BOLD_10 = new Font("SansSerif", Font.BOLD, 10);

    private static final String[] COL_NAMES = {
        "STT", "MÃ NGÀNH", "TÊN NGÀNH", "PHƯƠNG THỨC", "SỐ LƯỢNG"
    };

    private static final int RECORDS_PER_PAGE = 10;

    private final List<NguyenVongPanel.NguyenVong> allRows;
    private final List<CountRow> filteredRows = new ArrayList<>();
    private final List<CountRow> pageRows = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JLabel paginationInfo;
    private JPanel paginationBtnGroup;
    private JLabel subtitleLabel;
    private JComboBox<ComboItem> nganhCombo;
    private JComboBox<ComboItem> phuongThucCombo;
    private int currentPage = 1;

    public DanhSachSoLuongTrungTuyenTheoPhuongThucDialog(Frame owner,
                                                         List<NguyenVongPanel.NguyenVong> rows) {
        super(owner, "So luong trung tuyen theo phuong thuc theo nganh", true);
        this.allRows = rows == null ? new ArrayList<>() : new ArrayList<>(rows);
        initUi();
    }

    private void initUi() {
        setSize(980, 640);
        setMinimumSize(new Dimension(860, 560));
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

        JLabel title = new JLabel("Số lượng trúng tuyển theo phương thức");
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

        nganhCombo = new JComboBox<>();
        nganhCombo.setFont(FONT_PLAIN_12);
        nganhCombo.setBackground(new Color(0xf8, 0xfa, 0xfc));
        nganhCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(9, BORDER_COLOR),
            new EmptyBorder(4, 6, 4, 6)
        ));

        phuongThucCombo = new JComboBox<>();
        phuongThucCombo.setFont(FONT_PLAIN_12);
        phuongThucCombo.setBackground(new Color(0xf8, 0xfa, 0xfc));
        phuongThucCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(9, BORDER_COLOR),
            new EmptyBorder(4, 6, 4, 6)
        ));

        buildFilterValues();

        nganhCombo.addActionListener(e -> {
            currentPage = 1;
            applyFilter();
        });
        phuongThucCombo.addActionListener(e -> {
            currentPage = 1;
            applyFilter();
        });

        left.add(new JLabel("Nganh:"));
        left.add(nganhCombo);
        left.add(Box.createHorizontalStrut(6));
        left.add(new JLabel("Phuong thuc:"));
        left.add(phuongThucCombo);

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

    private void buildFilterValues() {
        nganhCombo.removeAllItems();
        phuongThucCombo.removeAllItems();
        nganhCombo.addItem(new ComboItem("", "Tat ca nganh"));
        phuongThucCombo.addItem(new ComboItem("", "Tat ca phuong thuc"));

        Map<String, String> nganhMap = new LinkedHashMap<>();
        Map<String, String> phuongThucMap = new LinkedHashMap<>();
        for (NguyenVongPanel.NguyenVong row : allRows) {
            String maNganh = safeText(row.getMaNganh());
            if (!maNganh.isEmpty()) {
                String label = maNganh;
                String tenNganh = safeText(row.getTenNganh());
                if (!tenNganh.isEmpty()) {
                    label = maNganh + " - " + tenNganh;
                }
                nganhMap.putIfAbsent(maNganh, label);
            }
            String phuongThuc = safeText(row.getPhuongThuc());
            if (!phuongThuc.isEmpty()) {
                phuongThucMap.putIfAbsent(phuongThuc, phuongThuc);
            }
        }

        for (Map.Entry<String, String> entry : nganhMap.entrySet()) {
            nganhCombo.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, String> entry : phuongThucMap.entrySet()) {
            phuongThucCombo.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
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
        table.setRowHeight(44);
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

        int[] widths = {50, 110, 260, 150, 90};
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

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
        String selectedPt = "";
        ComboItem nganhItem = (ComboItem) nganhCombo.getSelectedItem();
        ComboItem ptItem = (ComboItem) phuongThucCombo.getSelectedItem();
        if (nganhItem != null) {
            selectedMa = safeText(nganhItem.value);
        }
        if (ptItem != null) {
            selectedPt = safeText(ptItem.value);
        }

        Map<String, CountRow> countMap = new LinkedHashMap<>();
        int totalPassed = 0;
        for (NguyenVongPanel.NguyenVong row : allRows) {
            if (!"trungtuyen".equalsIgnoreCase(stripAccent(row.getTrangThai()))) {
                continue;
            }
            if (!selectedMa.isEmpty() && !safeText(row.getMaNganh()).equalsIgnoreCase(selectedMa)) {
                continue;
            }
            if (!selectedPt.isEmpty() && !safeText(row.getPhuongThuc()).equalsIgnoreCase(selectedPt)) {
                continue;
            }

            String maNganh = safeText(row.getMaNganh());
            String tenNganh = safeText(row.getTenNganh());
            String phuongThuc = safeText(row.getPhuongThuc());
            String key = maNganh + "|" + phuongThuc;

            CountRow item = countMap.get(key);
            if (item == null) {
                item = new CountRow(maNganh, tenNganh, phuongThuc, 0);
                countMap.put(key, item);
            }
            item.count++;
            totalPassed++;
        }

        filteredRows.addAll(countMap.values());
        filteredRows.sort(Comparator.comparing(CountRow::getMaNganh, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(CountRow::getPhuongThuc, String.CASE_INSENSITIVE_ORDER));

        subtitleLabel.setText("Tổng trúng tuyển: " + totalPassed);
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
            CountRow row = filteredRows.get(i);
            pageRows.add(row);
            tableModel.addRow(new Object[] {
                String.valueOf(i + 1),
                row.getMaNganh(),
                row.getTenNganh(),
                row.getPhuongThuc(),
                String.valueOf(row.getCount())
            });
        }
    }

    private String getPaginationText() {
        int total = filteredRows.size();
        if (total == 0) {
            return "Hiển thị 0-0 trong số 0 dòng";
        }
        int startRecord = (currentPage - 1) * RECORDS_PER_PAGE + 1;
        int endRecord = Math.min(currentPage * RECORDS_PER_PAGE, total);
        return "Hiển thị " + startRecord + "-" + endRecord + " trong số " + total + " dòng";
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

    private static class CountRow {
        private final String maNganh;
        private final String tenNganh;
        private final String phuongThuc;
        private int count;

        private CountRow(String maNganh, String tenNganh, String phuongThuc, int count) {
            this.maNganh = maNganh;
            this.tenNganh = tenNganh;
            this.phuongThuc = phuongThuc;
            this.count = count;
        }

        public String getMaNganh() { return maNganh; }
        public String getTenNganh() { return tenNganh; }
        public String getPhuongThuc() { return phuongThuc; }
        public int getCount() { return count; }
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
