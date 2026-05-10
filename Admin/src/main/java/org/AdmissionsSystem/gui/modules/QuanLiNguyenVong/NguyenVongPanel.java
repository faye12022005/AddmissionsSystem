package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.AdmissionsSystem.bus.controller.XtNguyenvongxettuyenController;
import org.AdmissionsSystem.bus.service.NganhHocService;
import org.AdmissionsSystem.bus.service.ThiSinhService;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;

public class NguyenVongPanel extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final Color PRIMARY      = new Color(0x13, 0x7f, 0xec);
    private static final Color BG_LIGHT     = new Color(0xf6, 0xf7, 0xf8);
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED   = new Color(0x94, 0xa3, 0xb8);
    private static final Color TEXT_SLATE   = new Color(0x47, 0x55, 0x69);
    private static final Color BORDER_COLOR = new Color(0xcb, 0xd5, 0xe1);
    private static final Color SURFACE      = new Color(0xf1, 0xf5, 0xf9);
    private static final Color SUCCESS_BG   = new Color(0xf0, 0xfd, 0xf4);
    private static final Color SUCCESS_FG   = new Color(0x16, 0xa3, 0x4a);
    private static final Color WARN_BG      = new Color(0xff, 0xfb, 0xeb);
    private static final Color WARN_FG      = new Color(0xd9, 0x77, 0x06);
    private static final Color DANGER_FG    = new Color(0xdc, 0x26, 0x26);

    // ── Font ─────────────────────────────────────────────────
    private static final Font FONT_BOLD_26  = new Font("SansSerif", Font.BOLD,  26);
    private static final Font FONT_BOLD_24  = new Font("SansSerif", Font.BOLD,  24);
    private static final Font FONT_BOLD_14  = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FONT_BOLD_13  = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FONT_BOLD_12  = new Font("SansSerif", Font.BOLD,  12);
    private static final Font FONT_BOLD_11  = new Font("SansSerif", Font.BOLD,  11);
    private static final Font FONT_BOLD_10  = new Font("SansSerif", Font.BOLD,  10);
    private static final Font FONT_PLAIN_13 = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_PLAIN_11 = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_PLAIN_10 = new Font("SansSerif", Font.PLAIN, 10);

    // ── Data Model ───────────────────────────────────────────
    private static final String[] COL_NAMES = {
        "THỨ TỰ", "THÍ SINH", "CCCD", "MÃ NGÀNH", "TỔ HỢP", "PHƯƠNG THỨC", "ĐIỂM XT", "TRẠNG THÁI", "HÀNH ĐỘNG"
    };
    
    private static final int RECORDS_PER_PAGE = 5;
    private int currentPage = 1;
    private DefaultTableModel tableModel;
    private JLabel paginationInfo;
    private JPanel paginationBtnGroup;
    private final XtNguyenvongxettuyenController controller = new XtNguyenvongxettuyenController();
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final NganhHocService nganhHocService = new NganhHocService();
    private final List<NguyenVong> allRows = new ArrayList<>();
    private final List<NguyenVong> pageRows = new ArrayList<>();
    private static final DecimalFormat SCORE_FMT = new DecimalFormat("0.00");

    // ════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════
    public NguyenVongPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);

        loadData();

        JScrollPane scroll = new JScrollPane(buildInnerPanel());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Inner panel (chứa tất cả nội dung) ──────────────────
    private JPanel buildInnerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        panel.add(buildPageTitle());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildStatsGrid());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildFilters());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildTable());
        return panel;
    }

    // ════════════════════════════════════════════════════════
    //  1. PAGE TITLE
    // ════════════════════════════════════════════════════════
    private JPanel buildPageTitle() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left: tiêu đề
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Danh sách Nguyện vọng Thí sinh");
        title.setFont(FONT_BOLD_26);
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Kỳ xét tuyển đại học chính quy - Năm học 2024-2025");
        subtitle.setFont(FONT_PLAIN_13);
        subtitle.setForeground(TEXT_MUTED);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        // Right: các nút
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        JButton importBtn = makeOutlineButton("⤓ Import Excel");
        JButton addBtn    = makeOutlineButton("＋ Thêm");
        JButton runBtn    = makePrimaryButton("▶  Chạy xét tuyển hệ thống");

        importBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Chức năng Import Excel chưa được triển khai.", "Import Excel",
            JOptionPane.INFORMATION_MESSAGE));

        addBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Chức năng Thêm chưa được triển khai.", "Thêm Nguyện vọng",
            JOptionPane.INFORMATION_MESSAGE));

        btnGroup.add(importBtn);
        btnGroup.add(addBtn);
        btnGroup.add(runBtn);

        row.add(titleBlock, BorderLayout.CENTER);
        row.add(btnGroup,   BorderLayout.EAST);
        return row;
    }

    // ════════════════════════════════════════════════════════
    //  2. STATS GRID (4 thẻ)
    // ════════════════════════════════════════════════════════
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        int total = allRows.size();
        int waiting = demTheoTrangThai("Đang chờ");
        int passed = demTheoTrangThai("Trúng tuyển");
        int failed = demTheoTrangThai("Đã trượt");

        Object[][] stats = {
            {"👥", new Color(0xbf,0xdb,0xfe), new Color(0x1e,0x40,0xaf), "Tổng nguyện vọng", formatCount(total)},
            {"⏳", new Color(0xff,0xed,0xd5), new Color(0xb4,0x53,0x09), "Đang chờ xử lý",   formatCount(waiting)},
            {"✅", new Color(0xbb,0xf7,0xd0), new Color(0x15,0x80,0x3d), "Đã trúng tuyển",    formatCount(passed)},
            {"❌", new Color(0xfe,0xca,0xca), new Color(0xdc,0x26,0x26), "Đã trượt",           formatCount(failed)},
        };

        for (Object[] s : stats)
            grid.add(buildStatCard((String)s[0], (Color)s[1], (Color)s[2], (String)s[3], (String)s[4]));

        return grid;
    }

    private JPanel buildStatCard(String icon, Color bgColor, Color iconColor,
                                  String label, String value) {
        RoundedPanel card = new RoundedPanel(14, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(14, BORDER_COLOR),
            new EmptyBorder(18, 18, 18, 18)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Icon box
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(bgColor);
        iconLbl.setBorder(new EmptyBorder(6, 8, 6, 8));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(FONT_PLAIN_12);
        labelLbl.setForeground(TEXT_MUTED);
        labelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_BOLD_24);
        valueLbl.setForeground(TEXT_DARK);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(labelLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLbl);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  3. FILTERS
    // ════════════════════════════════════════════════════════
    private JPanel buildFilters() {
        RoundedPanel card = new RoundedPanel(12, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.weighty = 1;

        JComboBox<String> nganh = makeCombo("Tất cả các ngành", "Khoa học máy tính", "Kỹ thuật phần mềm");
        JComboBox<String> diem  = makeCombo("Mọi mức điểm", "Dưới 20 điểm", "20 - 25 điểm", "Trên 25 điểm");
        JComboBox<String> sort  = makeCombo("Thứ tự ưu tiên", "Điểm từ cao xuống thấp", "Mới nhất trước");

        gbc.weightx = 1; gbc.gridx = 0; row.add(nganh, gbc);
        gbc.gridx = 1; row.add(diem, gbc);
        gbc.gridx = 2; row.add(sort, gbc);

        gbc.weightx = 0; gbc.insets = new Insets(0, 0, 0, 6);
        gbc.gridx = 3; row.add(makeFilterBtn("Lọc kết quả", PRIMARY, WHITE), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 4; row.add(makeResetBtn("Đặt lại"), gbc);

        card.add(row);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  4. TABLE
    // ════════════════════════════════════════════════════════
    private JPanel buildTable() {
        RoundedPanel card = new RoundedPanel(12, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setLayout(new BorderLayout());

        // ── TableModel ──
        tableModel = new DefaultTableModel(COL_NAMES, COL_NAMES.length) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshTableData(1);

        JTable table = new JTable(tableModel);
        table.setRowHeight(52);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xf1, 0xf5, 0xf9));
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(0xef, 0xf6, 0xff));
        table.setSelectionForeground(TEXT_DARK);
        table.setFont(FONT_PLAIN_13);
        table.setFocusable(false);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0xf8, 0xfa, 0xfc));
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_BOLD_10);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));

        // Column widths
        int[] widths = {70, 190, 110, 90, 80, 110, 90, 120, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // ── Custom Renderers ──
        // Thứ tự
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(sel ? t.getSelectionBackground() : WHITE);

                boolean first = row == 0;

                JLabel badge = new JLabel(val.toString(), SwingConstants.CENTER);
                badge.setFont(FONT_BOLD_11);
                badge.setPreferredSize(new Dimension(30, 30));
                badge.setOpaque(true);
                badge.setBackground(first ? PRIMARY : SURFACE);
                badge.setForeground(first ? WHITE : TEXT_SLATE);
                badge.setBorder(new RoundedBorder(15, first ? PRIMARY : SURFACE));

                p.add(badge);
                return p;
            }
        });

        // Thí sinh (tên + SBD)
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(sel ? t.getSelectionBackground() : WHITE);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(0, 0, 0, 10);
                // Avatar tròn
                JPanel avatar = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0xe2, 0xe8, 0xf0));
                        g2.fillOval(0, 0, 32, 32);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(32, 32));
                avatar.setOpaque(false);

                String[] parts = val.toString().split("\n");
                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setOpaque(false);
                JLabel name = new JLabel(parts.length > 0 ? parts[0] : "");
                name.setFont(FONT_BOLD_12);
                name.setForeground(TEXT_DARK);
                JLabel sbd = new JLabel(parts.length > 1 ? parts[1] : "");
                sbd.setFont(FONT_PLAIN_10);
                sbd.setForeground(TEXT_MUTED);
                info.add(name);
                info.add(sbd);

                p.add(avatar, gbc);
                gbc.gridx = 1;
                p.add(info, gbc);
                return p;
            }
        });

        // Tổng điểm
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setText(val.toString());
                setFont(FONT_BOLD_14);
                setForeground(PRIMARY);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(sel ? t.getSelectionBackground() : WHITE);
                return this;
            }
        });

        // Trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                String status = val.toString();
                boolean trung = status.equals("Trúng tuyển");
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(sel ? t.getSelectionBackground() : WHITE);
                JLabel badge = new JLabel("● " + status);
                badge.setFont(FONT_BOLD_10);
                badge.setForeground(trung ? SUCCESS_FG : WARN_FG);
                badge.setOpaque(true);
                badge.setBackground(trung ? SUCCESS_BG : WARN_BG);
                badge.setBorder(new EmptyBorder(4, 10, 4, 10));
                p.add(badge);
                return p;
            }
        });

        // Hành động
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(sel ? t.getSelectionBackground() : WHITE);
                JLabel edit = new JLabel("✏");
                JLabel del  = new JLabel("🗑");
                edit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                del.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                edit.setForeground(TEXT_MUTED);
                del.setForeground(TEXT_MUTED);
                edit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
                actions.setOpaque(false);

                actions.add(edit);
                actions.add(del);
                                        
                p.add(actions);
                return p;
            }
        });

        // Sự kiện cho nút chỉnh sửa và double-click
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0) {
                    // Single click: kiểm tra nếu nhấn vào cột HÀNH ĐỘNG (cột 8)
                    if (e.getClickCount() == 1 && col == 8) {
                        // Lấy vị trí x của click
                        Rectangle cellRect = table.getCellRect(row, col, false);
                        int clickX = e.getX() - cellRect.x;
                        
                        // Nếu click vào khu vực edit button (phía trái, khoảng 20-40px)
                        if (clickX > 10 && clickX < 40) {
                            openChiTietNguyenVong(row);
                        }
                    }
                    // Double-click: mở chi tiết
                    else if (e.getClickCount() == 2) {
                        openChiTietNguyenVong(row);
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(WHITE);

        updateTableHeight(table, tableScroll);

        card.add(tableScroll, BorderLayout.CENTER);
        card.add(buildPagination(), BorderLayout.SOUTH);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  PAGINATION
    // ════════════════════════════════════════════════════════
    private JPanel buildPagination() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(0xf8, 0xfa, 0xfc));
        row.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(10, 16, 10, 16)
        ));

        paginationInfo = new JLabel(getPaginationText());
        paginationInfo.setFont(FONT_PLAIN_11);
        paginationInfo.setForeground(TEXT_MUTED);

        paginationBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        paginationBtnGroup.setOpaque(false);
        
        updatePaginationButtons();

        row.add(paginationInfo,      BorderLayout.WEST);
        row.add(paginationBtnGroup, BorderLayout.EAST);
        return row;
    }

    private void updateTableHeight(JTable table, JScrollPane scroll) {

        int rowCount = table.getRowCount();

        int rowHeight = table.getRowHeight();
        int headerHeight = table.getTableHeader().getPreferredSize().height;

        int height = rowCount * rowHeight + headerHeight;

        scroll.setPreferredSize(
            new Dimension(scroll.getPreferredSize().width, height)
        );

        scroll.revalidate();
    }
    
    private void updatePaginationButtons() {
        paginationBtnGroup.removeAll();
        int totalPages = (allRows.size() + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        
        // Previous button
        JButton prevBtn = new JButton("‹") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        prevBtn.setMargin(new Insets(0, 0, 0, 0));
        prevBtn.setPreferredSize(new Dimension(32, 32));
        prevBtn.setFont(FONT_PLAIN_12);
        prevBtn.setForeground(TEXT_SLATE);
        prevBtn.setBackground(new Color(0, 0, 0, 0));
        prevBtn.setContentAreaFilled(false);
        prevBtn.setBorderPainted(false);
        prevBtn.setFocusPainted(false);
        prevBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        prevBtn.setEnabled(currentPage > 1);
        prevBtn.addActionListener(e -> goToPage(currentPage - 1));
        paginationBtnGroup.add(prevBtn);
        
        // Page buttons
        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, currentPage + 1);
        if (startPage > 1) {
            addPageButton(1, false);
            if (startPage > 2) {
                JLabel dots = new JLabel("...");
                dots.setFont(FONT_PLAIN_12);
                dots.setForeground(TEXT_MUTED);
                dots.setBorder(new EmptyBorder(0, 4, 0, 4));
                paginationBtnGroup.add(dots);
            }
        }
        for (int p = startPage; p <= endPage; p++) {
            addPageButton(p, p == currentPage);
        }
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                JLabel dots = new JLabel("...");
                dots.setFont(FONT_PLAIN_12);
                dots.setForeground(TEXT_MUTED);
                dots.setBorder(new EmptyBorder(0, 4, 0, 4));
                paginationBtnGroup.add(dots);
            }
            addPageButton(totalPages, false);
        }
        
        // Next button
        JButton nextBtn = new JButton("›") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        nextBtn.setMargin(new Insets(0, 0, 0, 0));
        nextBtn.setPreferredSize(new Dimension(32, 32));
        nextBtn.setFont(FONT_PLAIN_12);
        nextBtn.setForeground(TEXT_SLATE);
        nextBtn.setBackground(new Color(0, 0, 0, 0));
        nextBtn.setContentAreaFilled(false);
        nextBtn.setBorderPainted(false);
        nextBtn.setFocusPainted(false);
        nextBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextBtn.setEnabled(currentPage < totalPages);
        nextBtn.addActionListener(e -> goToPage(currentPage + 1));
        paginationBtnGroup.add(nextBtn);
        
        paginationBtnGroup.revalidate();
        paginationBtnGroup.repaint();
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
        int totalPages = (allRows.size() + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        if (pageNum < 1 || pageNum > totalPages) return;
        
        currentPage = pageNum;
        refreshTableData(pageNum);
        updatePaginationButtons();
        paginationInfo.setText(getPaginationText());
    }
    
    private void refreshTableData(int pageNum) {
        tableModel.setRowCount(0);
        pageRows.clear();
        int startIdx = (pageNum - 1) * RECORDS_PER_PAGE;
        int endIdx = Math.min(startIdx + RECORDS_PER_PAGE, allRows.size());
        for (int i = startIdx; i < endIdx; i++) {
            NguyenVong row = allRows.get(i);
            pageRows.add(row);
            tableModel.addRow(new Object[] {
                row.getThuTu(),
                row.getTenThiSinh() + "\nSBD: " + row.getSbd(),
                row.getCccd(),
                row.getMaNganh(),
                row.getToHop(),
                row.getPhuongThuc(),
                row.getTongDiem(),
                row.getTrangThai(),
                ""
            });
        }
    }
    
    private String getPaginationText() {
        int total = allRows.size();
        if (total == 0) {
            return "Hiển thị 0-0 trong số 0 bản ghi";
        }
        int startRecord = (currentPage - 1) * RECORDS_PER_PAGE + 1;
        int endRecord = Math.min(currentPage * RECORDS_PER_PAGE, total);
        return "Hiển thị " + startRecord + "-" + endRecord + " trong số " + total + " bản ghi";
    }

    // ════════════════════════════════════════════════════════
    //  HELPER: Button builders
    // ════════════════════════════════════════════════════════
    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x0f, 0x6f, 0xd4) : PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD_13);
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xf8, 0xfa, 0xfc) : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_PLAIN_13);
        btn.setForeground(TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 14, 9, 14));
        return btn;
    }

    private JButton makeFilterBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), getModel().isRollover() ? 40 : 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD_13);
        btn.setForeground(PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private JButton makeResetBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_PLAIN_13);
        btn.setForeground(TEXT_SLATE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // ════════════════════════════════════════════════════════
    //  HELPER: Mở ChiTietNguyenVong dialog
    // ════════════════════════════════════════════════════════
    private void openChiTietNguyenVong(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= pageRows.size()) return;

        NguyenVong data = pageRows.get(rowIndex);
        
        // Lấy Frame chứa panel này
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        
        // Mở dialog
        ChiTietNguyenVong dialog = new ChiTietNguyenVong(parentFrame, data);
        dialog.setVisible(true);
    }

    private JComboBox<String> makeCombo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_PLAIN_12);
        cb.setBackground(new Color(0xf8, 0xfa, 0xfc));
        cb.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(9, BORDER_COLOR),
            new EmptyBorder(4, 6, 4, 6)
        ));
        return cb;
    }

    // ════════════════════════════════════════════════════════
    //  HELPER: Custom components
    // ════════════════════════════════════════════════════════

    /** Panel với góc bo tròn */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius; this.bg = bg;
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

    /** Border bo tròn */
    static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius * 2, radius * 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════
    //  INNER CLASS: NguyenVong (Data Model)
    // ════════════════════════════════════════════════════════
    public static class NguyenVong {
        private final String thuTu;
        private final String tenThiSinh;
        private final String sbd;
        private final String cccd;
        private final String maNganh;
        private final String tenNganh;
        private final String toHop;
        private final String phuongThuc;
        private final String tongDiem;
        private final String trangThai;
        private final String ngaySinh;
        private final BigDecimal diemThxt;
        private final BigDecimal diemCong;
        private final BigDecimal diemUtqd;
        private final BigDecimal diemXettuyen;

        public NguyenVong(String thuTu, String tenThiSinh, String sbd, String cccd,
                          String maNganh, String tenNganh, String toHop, String phuongThuc,
                          String tongDiem, String trangThai, String ngaySinh,
                          BigDecimal diemThxt, BigDecimal diemCong, BigDecimal diemUtqd, BigDecimal diemXettuyen) {
            this.thuTu = thuTu;
            this.tenThiSinh = tenThiSinh;
            this.sbd = sbd;
            this.cccd = cccd;
            this.maNganh = maNganh;
            this.tenNganh = tenNganh;
            this.toHop = toHop;
            this.phuongThuc = phuongThuc;
            this.tongDiem = tongDiem;
            this.trangThai = trangThai;
            this.ngaySinh = ngaySinh;
            this.diemThxt = diemThxt;
            this.diemCong = diemCong;
            this.diemUtqd = diemUtqd;
            this.diemXettuyen = diemXettuyen;
        }

        public String getThuTu() { return thuTu; }
        public String getTenThiSinh() { return tenThiSinh; }
        public String getSbd() { return sbd; }
        public String getCccd() { return cccd; }
        public String getMaNganh() { return maNganh; }
        public String getTenNganh() { return tenNganh; }
        public String getToHop() { return toHop; }
        public String getPhuongThuc() { return phuongThuc; }
        public String getTongDiem() { return tongDiem; }
        public String getTrangThai() { return trangThai; }
        public String getNgaySinh() { return ngaySinh; }
        public BigDecimal getDiemThxt() { return diemThxt; }
        public BigDecimal getDiemCong() { return diemCong; }
        public BigDecimal getDiemUtqd() { return diemUtqd; }
        public BigDecimal getDiemXettuyen() { return diemXettuyen; }
    }

    private void loadData() {
        allRows.clear();
        try {
            List<XtNguyenvongxettuyen> data = controller.taiDuLieu();
            Map<String, XtNganh> nganhMap = new HashMap<>();
            for (XtNganh nganh : nganhHocService.getAll()) {
                if (nganh.getManganh() != null) {
                    nganhMap.put(nganh.getManganh().trim().toLowerCase(Locale.ROOT), nganh);
                }
            }

            for (XtNguyenvongxettuyen nv : data) {
                String cccd = safeText(nv.getNnCccd());
                XtThisinhxettuyen25 ts = thiSinhService.findByCccd(cccd);
                String tenThiSinh = buildTenThiSinh(ts, cccd);
                String sbd = ts != null ? safeText(ts.getSobaodanh()) : "";
                String ngaySinh = ts != null ? safeText(ts.getNgaySinh()) : "";
                XtNganh nganh = nganhMap.get(safeText(nv.getNvManganh()).toLowerCase(Locale.ROOT));
                String tenNganh = nganh != null ? safeText(nganh.getTennganh()) : "";
                String toHop = safeText(nv.getTtThm());
                String phuongThuc = safeText(nv.getTtPhuongthuc());

                BigDecimal diemThxt = nv.getDiemThxt();
                BigDecimal diemCong = nv.getDiemCong();
                BigDecimal diemUtqd = nv.getDiemUtqd();
                BigDecimal diemXettuyen = nv.getDiemXettuyen();
                String tongDiem = formatScore(diemXettuyen != null ? diemXettuyen : diemThxt);
                String trangThai = mapTrangThai(nv.getNvKetqua());

                allRows.add(new NguyenVong(
                    formatThuTu(nv.getNvTt()),
                    tenThiSinh,
                    sbd,
                    cccd,
                    safeText(nv.getNvManganh()),
                    tenNganh,
                    toHop,
                    phuongThuc,
                    tongDiem,
                    trangThai,
                    ngaySinh,
                    diemThxt,
                    diemCong,
                    diemUtqd,
                    diemXettuyen
                ));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu nguyện vọng.\n" + ex.getMessage(),
                "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildTenThiSinh(XtThisinhxettuyen25 ts, String cccd) {
        if (ts == null) {
            return cccd.isEmpty() ? "(Chưa có thông tin)" : "Thí sinh " + cccd;
        }
        String ho = safeText(ts.getHo());
        String ten = safeText(ts.getTen());
        String full = (ho + " " + ten).trim();
        return full.isEmpty() ? ("Thí sinh " + cccd) : full;
    }

    private String formatThuTu(Integer thuTu) {
        if (thuTu == null) return "--";
        return String.format("%02d", thuTu);
    }

    private String safeText(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String formatScore(BigDecimal value) {
        if (value == null) return "--";
        return SCORE_FMT.format(value);
    }

    private String mapTrangThai(String raw) {
        String v = safeText(raw).toLowerCase(Locale.ROOT);
        if (v.isEmpty()) return "Đang chờ";
        if (v.contains("trung")) return "Trúng tuyển";
        if (v.contains("dat")) return "Trúng tuyển";
        if (v.contains("khong") || v.contains("truot")) return "Đã trượt";
        return raw.trim();
    }

    private int demTheoTrangThai(String trangThai) {
        int count = 0;
        for (NguyenVong row : allRows) {
            if (trangThai.equals(row.getTrangThai())) {
                count++;
            }
        }
        return count;
    }

    private String formatCount(int value) {
        return String.format("%,d", value);
    }

    // ════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ thống Tuyển sinh - Quản lý Nguyện vọng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 820);
            frame.setLocationRelativeTo(null);
            frame.add(new NguyenVongPanel());
            frame.setVisible(true);
        });
    }
}