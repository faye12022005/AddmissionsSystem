package org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class BangQuyDoiPanel extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final Color PRIMARY      = new Color(0x13, 0x7f, 0xec);
    private static final Color WHITE        = new Color(0xff, 0xff, 0xff);
    private static final Color BG           = new Color(0xf6, 0xf7, 0xf8);
    private static final Color BORDER       = new Color(0xe2, 0xe8, 0xf0);
    private static final Color TEXT_DARK    = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED   = new Color(0x64, 0x74, 0x8b);
    private static final Color TEXT_LIGHT   = new Color(0x94, 0xa3, 0xb8);
    private static final Color SURFACE      = new Color(0xf1, 0xf5, 0xf9);
    private static final Color ERROR        = new Color(0xef, 0x44, 0x44);
    private static final Color SUCCESS      = new Color(0x10, 0xb9, 0x81);

    // ── Data Model ───────────────────────────────────────────
    public static class QuyTac {
        private final String id, loai, phuongThuc, toHop, mon, khoangDiem, quyDoi;
        public QuyTac(String id, String loai, String phuongThuc,
                      String toHop, String mon, String khoangDiem, String quyDoi) {
            this.id = id; this.loai = loai; this.phuongThuc = phuongThuc;
            this.toHop = toHop; this.mon = mon;
            this.khoangDiem = khoangDiem; this.quyDoi = quyDoi;
        }
        public String getId()         { return id; }
        public String getLoai()       { return loai; }
        public String getPhuongThuc() { return phuongThuc; }
        public String getToHop()      { return toHop; }
        public String getMon()        { return mon; }
        public String getKhoangDiem() { return khoangDiem; }
        public String getQuyDoi()     { return quyDoi; }
    }

    private static final String[] COLS = {
        "ID", "LOẠI", "PHƯƠNG THỨC", "TỔ HỢP", "MÔN", "KHOẢNG ĐIỂM", "QUY ĐỔI", "HÀNH ĐỘNG"
    };

    private static final QuyTac[] DATA = new QuyTac[] {
        new QuyTac("QD-001", "Chứng chỉ",    "IELTS",      "-", "Tiếng Anh", "8.0 - 9.0",  "10.0"),
        new QuyTac("QD-002", "Chứng chỉ",    "TOEFL iBT",  "-", "Tiếng Anh", "95 - 120",   "10.0"),
        new QuyTac("QD-003", "Điểm ưu tiên", "Đối tượng",  "-", "-",         "Nhóm 1",     "+2.0"),
        new QuyTac("QD-004", "Chứng chỉ",    "VSTEP",      "-", "Tiếng Anh", "8.5 - 10.0", "9.0")
    };

    public BangQuyDoiPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel content = buildMainPanel();
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Bảng quy đổi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 780);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangQuyDoiPanel());
            frame.setVisible(true);
        });
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel buildMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(32, 32, 32, 32));

        panel.add(buildPageHeader());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildFilterBar());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildTable());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildStatsRow());
        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  1. PAGE HEADER
    // ══════════════════════════════════════════════════════════
    private JPanel buildPageHeader() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Quản lý Bảng quy đổi");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Cấu hình quy tắc đổi điểm chứng chỉ và điểm ưu tiên cho kỳ tuyển sinh 2024");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        JButton importBtn = makeOutlineButton("📄  Import Excel");

        row.add(titleBlock, BorderLayout.CENTER);
        row.add(importBtn, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  2. FILTER BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildFilterBar() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(18, 20, 18, 20)));

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.weighty = 1;

        JPanel f1 = buildFilterGroup("LOẠI QUY ĐỔI",  new String[]{"Tất cả loại", "Chứng chỉ", "Điểm ưu tiên"});
        JPanel f2 = buildFilterGroup("PHƯƠNG THỨC",    new String[]{"Chọn phương thức", "IELTS", "TOEFL iBT", "VSTEP"});
        JPanel f3 = buildFilterGroup("TỔ HỢP",         new String[]{"Tất cả tổ hợp", "A00", "D01"});
        JPanel f4 = buildFilterGroup("MÔN",            new String[]{"Tất cả môn", "Toán", "Tiếng Anh"});

        gbc.weightx = 1; gbc.gridx = 0; row.add(f1, gbc);
        gbc.gridx = 1; row.add(f2, gbc);
        gbc.gridx = 2; row.add(f3, gbc);
        gbc.gridx = 3; row.add(f4, gbc);

        JButton addBtn = makePrimaryButton("＋  Thêm mới");
        gbc.weightx = 0; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 4; row.add(addBtn, gbc);

        card.add(row, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFilterGroup(String label, String[] options) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_LIGHT);

        JComboBox<String> cb = new JComboBox<>(options);
        cb.setSelectedIndex(0);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cb.setPreferredSize(new Dimension(180, 36));
        cb.setBackground(SURFACE);
        cb.setBorder(new EmptyBorder(4, 8, 4, 8));

        group.add(lbl);
        group.add(Box.createVerticalStrut(6));
        group.add(cb);
        return group;
    }

    // ══════════════════════════════════════════════════════════
    //  3. TABLE
    // ══════════════════════════════════════════════════════════
    private JPanel buildTable() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout());

        JTable table = new JTable(new AbstractTableModel() {
            @Override public int getRowCount() { return DATA.length; }
            @Override public int getColumnCount() { return COLS.length; }
            @Override public String getColumnName(int c) { return COLS[c]; }
            @Override public Object getValueAt(int r, int c) {
                QuyTac q = DATA[r];
                return switch (c) {
                    case 0 -> q.getId();
                    case 1 -> q.getLoai();
                    case 2 -> q.getPhuongThuc();
                    case 3 -> q.getToHop();
                    case 4 -> q.getMon();
                    case 5 -> q.getKhoangDiem();
                    case 6 -> q.getQuyDoi();
                    default -> "";
                };
            }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        table.setRowHeight(42);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(SURFACE);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(0xef, 0xf6, 0xff));
        table.setSelectionForeground(TEXT_DARK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0xf8, 0xfa, 0xfc));
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 10));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        int[] widths = {80, 110, 140, 80, 110, 140, 90, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Col ID
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setForeground(PRIMARY);
                return lbl;
            }
        });

        // Col Loại (badge)
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
                p.setOpaque(true);
                p.setBackground(s ? t.getSelectionBackground() : WHITE);
                if (v == null) return p;
                boolean isCC = "Chứng chỉ".equals(v.toString());
                JLabel badge = new JLabel(v.toString());
                badge.setFont(new Font("SansSerif", Font.BOLD, 10));
                badge.setBorder(new EmptyBorder(2, 8, 2, 8));
                badge.setOpaque(true);
                badge.setBackground(isCC ? new Color(0xef, 0xf6, 0xff) : new Color(0xff, 0xfb, 0xeb));
                badge.setForeground(isCC ? new Color(0x25, 0x63, 0xeb) : new Color(0xd9, 0x77, 0x06));
                p.add(badge);
                return p;
            }
        });

        // Default renderers for other cols
        DefaultTableCellRenderer textCol = new DefaultTableCellRenderer();
        textCol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textCol.setForeground(TEXT_DARK);
        table.getColumnModel().getColumn(2).setCellRenderer(textCol);

        DefaultTableCellRenderer lightCol = new DefaultTableCellRenderer();
        lightCol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lightCol.setForeground(TEXT_LIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(lightCol);

        table.getColumnModel().getColumn(4).setCellRenderer(textCol);

        DefaultTableCellRenderer boldCol = new DefaultTableCellRenderer();
        boldCol.setFont(new Font("SansSerif", Font.BOLD, 13));
        boldCol.setForeground(TEXT_DARK);
        table.getColumnModel().getColumn(5).setCellRenderer(boldCol);

        // Col Quy đổi
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                String val = v == null ? "" : v.toString();
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setForeground(val.startsWith("+") ? SUCCESS : PRIMARY);
                return lbl;
            }
        });

        // Col Hành động
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
                p.setOpaque(true);
                p.setBackground(s ? t.getSelectionBackground() : WHITE);
                p.add(makeActionButton("✏", new Color(0xef, 0xf6, 0xff)));
                p.add(makeActionButton("🗑", new Color(0xff, 0xf1, 0xf2)));
                return p;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(WHITE);

        card.add(tableScroll, BorderLayout.CENTER);
        card.add(new JSeparator(), BorderLayout.SOUTH);
        
        JPanel pagination = buildPagination();
        card.add(pagination, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPagination() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel info = new JLabel("Hiển thị 1 - 4 trong tổng số 42 quy tắc");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(TEXT_MUTED);

        JPanel pages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pages.setOpaque(false);
        String[] items = {"‹", "1", "2", "3", "...", "12", "›"};
        for (String p : items) {
            if ("...".equals(p)) {
                JLabel dots = new JLabel("...");
                dots.setForeground(TEXT_LIGHT);
                pages.add(dots);
            } else {
                JButton btn = new JButton(p);
                btn.setPreferredSize(new Dimension(32, 32));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(true);
                boolean active = "1".equals(p);
                btn.setBackground(active ? PRIMARY : WHITE);
                btn.setForeground(active ? WHITE : TEXT_MUTED);
                btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 12));
                if (!active) {
                    btn.addMouseListener(new HoverAdapter(btn, WHITE, SURFACE));
                }
                pages.add(btn);
            }
        }

        row.add(info, BorderLayout.WEST);
        row.add(pages, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  4. STATS ROW
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        row.add(buildInfoCard("📋", new Color(0xef, 0xf6, 0xff),
            "TỔNG QUY TẮC", "42", "+3 từ tuần trước", SUCCESS));

        row.add(buildInfoCard("🔄", new Color(0xff, 0xfb, 0xeb),
            "CẬP NHẬT CUỐI", "15:30, 20/10/2023", "bởi Admin: Nguyen Minh", TEXT_MUTED));

        row.add(buildLogicCard());
        return row;
    }

    private JPanel buildInfoCard(String icon, Color iconBg,
                                 String label, String value, String sub, Color subColor) {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(20, 20, 20, 20)));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setBackground(iconBg);
        iconBox.setBorder(new RoundedBorder(12, iconBg));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
        iconBox.add(iconLbl);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_LIGHT);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, value.length() < 5 ? 24 : 15));
        val.setForeground(TEXT_DARK);
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        subLbl.setForeground(subColor);

        info.add(lbl);
        info.add(Box.createVerticalStrut(2));
        info.add(val);
        info.add(Box.createVerticalStrut(2));
        info.add(subLbl);

        card.add(iconBox, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLogicCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PRIMARY);
        card.setBorder(new CompoundBorder(new RoundedBorder(14, PRIMARY), new EmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Kiểm tra Logic");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(WHITE);

        JLabel desc = new JLabel("<html>Hệ thống phát hiện 2 quy tắc<br/>có thể bị trùng lặp khoảng điểm.</html>");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(new Color(255, 255, 255, 217));

        JButton detailBtn = makeGhostButton("Xem chi tiết");

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);
        card.add(Box.createVerticalGlue());
        card.add(detailBtn);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════
    private JPanel makeCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(new RoundedBorder(14, BORDER), new EmptyBorder(0, 0, 0, 0)));
        return card;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setBorder(new CompoundBorder(new RoundedBorder(10, BORDER), new EmptyBorder(8, 18, 8, 18)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new HoverAdapter(btn, WHITE, new Color(0xf8, 0xfa, 0xfc)));
        return btn;
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(PRIMARY);
        btn.setForeground(WHITE);
        btn.setBorder(new CompoundBorder(new RoundedBorder(10, PRIMARY), new EmptyBorder(8, 18, 8, 18)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new HoverAdapter(btn, PRIMARY, new Color(0x0f, 0x6f, 0xd4)));
        return btn;
    }

    private JButton makeGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 255, 255, 51));
        btn.setForeground(WHITE);
        btn.setBorder(new CompoundBorder(new RoundedBorder(8, new Color(255, 255, 255, 51)), new EmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new HoverAdapter(btn, new Color(255, 255, 255, 51), new Color(255, 255, 255, 82)));
        return btn;
    }

    private JButton makeActionButton(String icon, Color hoverBg) {
        JButton btn = new JButton(icon);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new HoverAdapter(btn, new Color(0, 0, 0, 0), hoverBg));
        return btn;
    }

    private static class HoverAdapter extends MouseAdapter {
        private final AbstractButton button;
        private final Color normal;
        private final Color hover;

        HoverAdapter(AbstractButton button, Color normal, Color hover) {
            this.button = button;
            this.normal = normal;
            this.hover = hover;
        }

        @Override public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
        @Override public void mouseExited(MouseEvent e)  { button.setBackground(normal); }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }
    }
}

