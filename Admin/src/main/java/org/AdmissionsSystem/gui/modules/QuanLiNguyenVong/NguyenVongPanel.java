package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

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
        "THỨ TỰ", "THÍ SINH", "MÃ NGÀNH", "TÊN NGÀNH", "TỔNG ĐIỂM", "TRẠNG THÁI", "HÀNH ĐỘNG"
    };

    private static final Object[][] TABLE_DATA = {
        {"01", "Nguyễn Văn A\nSBD: 2400015", "7480101", "Khoa học máy tính", "28.50", "Trúng tuyển", ""},
        {"02", "Lê Thị B\nSBD: 2400288",     "7480103", "Kỹ thuật phần mềm", "26.25", "Đang chờ",    ""},
        {"03", "Trần Văn C\nSBD: 2400312",   "7480201", "Hệ thống thông tin","25.75", "Đang chờ",    ""},
        {"04", "Phạm Thị D\nSBD: 2400456",   "7480104", "Mạng máy tính",     "24.00", "Đang chờ",    ""},
        {"05", "Hoàng Văn E\nSBD: 2400589",  "7480101", "Khoa học máy tính", "22.50", "Đang chờ",    ""},
        {"06", "Vũ Thị F\nSBD: 2400671",     "7480103", "Kỹ thuật phần mềm", "20.75", "Đã trượt",    ""},
        {"07", "Đặng Văn G\nSBD: 2400710",   "7480105", "An toàn thông tin", "19.50", "Đã trượt",    ""},
    };
    
    private static final int RECORDS_PER_PAGE = 5;
    private int currentPage = 1;
    private DefaultTableModel tableModel;
    private JLabel paginationInfo;
    private JPanel paginationBtnGroup;

    // ════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════
    public NguyenVongPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);

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

        Object[][] stats = {
            {"👥", new Color(0xbf,0xdb,0xfe), new Color(0x1e,0x40,0xaf), "Tổng nguyện vọng", "12,450"},
            {"⏳", new Color(0xff,0xed,0xd5), new Color(0xb4,0x53,0x09), "Đang chờ xử lý",   "8,120"},
            {"✅", new Color(0xbb,0xf7,0xd0), new Color(0x15,0x80,0x3d), "Đã trúng tuyển",    "3,240"},
            {"❌", new Color(0xfe,0xca,0xca), new Color(0xdc,0x26,0x26), "Đã trượt",           "1,090"},
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
        int[] widths = {70, 180, 100, 190, 100, 120, 100};
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
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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
                    // Single click: kiểm tra nếu nhấn vào cột HÀNH ĐỘNG (cột 6)
                    if (e.getClickCount() == 1 && col == 6) {
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
        int totalPages = (TABLE_DATA.length + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        
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
        int totalPages = (TABLE_DATA.length + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE;
        if (pageNum < 1 || pageNum > totalPages) return;
        
        currentPage = pageNum;
        refreshTableData(pageNum);
        updatePaginationButtons();
        paginationInfo.setText(getPaginationText());
    }
    
    private void refreshTableData(int pageNum) {
        tableModel.setRowCount(0);
        int startIdx = (pageNum - 1) * RECORDS_PER_PAGE;
        int endIdx = Math.min(startIdx + RECORDS_PER_PAGE, TABLE_DATA.length);
        for (int i = startIdx; i < endIdx; i++) {
            tableModel.addRow(TABLE_DATA[i]);
        }
    }
    
    private String getPaginationText() {
        int startRecord = (currentPage - 1) * RECORDS_PER_PAGE + 1;
        int endRecord = Math.min(currentPage * RECORDS_PER_PAGE, TABLE_DATA.length);
        return "Hiển thị " + startRecord + "-" + endRecord + " trong số " + TABLE_DATA.length + " bản ghi";
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
        if (rowIndex < 0 || rowIndex >= TABLE_DATA.length) return;
        
        // Trích xuất dữ liệu từ TABLE_DATA
        Object[] rowData = TABLE_DATA[rowIndex];
        String thuTu = rowData[0].toString();
        String fullName = rowData[1].toString();
        String maNganh = rowData[2].toString();
        String tenNganh = rowData[3].toString();
        String tongDiem = rowData[4].toString();
        String trangThai = rowData[5].toString();
        
        // Parse thí sinh và SBD từ format "Tên Thí Sinh\nSBD: XXXX"
        String[] parts = fullName.split("\\n");
        String tenThiSinh = parts.length > 0 ? parts[0] : "";
        String sbd = "";
        if (parts.length > 1) {
            sbd = parts[1].replace("SBD: ", "");
        }
        
        // Tạo NguyenVong object
        NguyenVong data = new NguyenVong(thuTu, tenThiSinh, sbd, maNganh, tenNganh, tongDiem, trangThai);
        
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
        private String thuTu;
        private String tenThiSinh;
        private String sbd;
        private String maNganh;
        private String tenNganh;
        private String tongDiem;
        private String trangThai;

        public NguyenVong(String thuTu, String tenThiSinh, String sbd, String maNganh,
                         String tenNganh, String tongDiem, String trangThai) {
            this.thuTu = thuTu;
            this.tenThiSinh = tenThiSinh;
            this.sbd = sbd;
            this.maNganh = maNganh;
            this.tenNganh = tenNganh;
            this.tongDiem = tongDiem;
            this.trangThai = trangThai;
        }

        public String getThuTu() { return thuTu; }
        public String getTenThiSinh() { return tenThiSinh; }
        public String getSbd() { return sbd; }
        public String getMaNganh() { return maNganh; }
        public String getTenNganh() { return tenNganh; }
        public String getTongDiem() { return tongDiem; }
        public String getTrangThai() { return trangThai; }
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