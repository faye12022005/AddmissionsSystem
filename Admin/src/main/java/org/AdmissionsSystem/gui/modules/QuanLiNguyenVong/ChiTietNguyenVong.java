package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ChiTietNguyenVong extends JDialog {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final Color PRIMARY    = new Color(0x13, 0x7f, 0xec);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BORDER_C   = new Color(0xe2, 0xe8, 0xf0);
    private static final Color TEXT_DARK  = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8b);
    private static final Color TEXT_SLATE = new Color(0x33, 0x41, 0x55);
    private static final Color SURFACE_L  = new Color(0xf8, 0xfa, 0xfc);
    private static final Color SURFACE_M  = new Color(0xf1, 0xf5, 0xf9);
    private static final Color ERROR_C    = new Color(0xef, 0x44, 0x44);
    private static final Color ERROR_BG   = new Color(0xfe, 0xe2, 0xe2);
    private static final Color SUCCESS_BG = new Color(0xf0, 0xfd, 0xf4);
    private static final Color SUCCESS_FG = new Color(0x16, 0xa3, 0x4a);
    private static final Color DOT_GREEN  = new Color(0x22, 0xc5, 0x5e);
    private static final Color AVATAR_BG  = new Color(0xcb, 0xd5, 0xe1);

    // ── Font – tất cả dùng cùng family, size nhất quán ──────
    private static final Font FB_20 = new Font("SansSerif", Font.BOLD,  20);
    private static final Font FB_18 = new Font("SansSerif", Font.BOLD,  18);
    private static final Font FB_16 = new Font("SansSerif", Font.BOLD,  16);
    private static final Font FB_15 = new Font("SansSerif", Font.BOLD,  15);
    private static final Font FB_14 = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FB_13 = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FB_12 = new Font("SansSerif", Font.BOLD,  12);
    private static final Font FB_11 = new Font("SansSerif", Font.BOLD,  11);
    private static final Font FB_10 = new Font("SansSerif", Font.BOLD,  10);
    private static final Font FB_9  = new Font("SansSerif", Font.BOLD,   9);
    private static final Font FB_28 = new Font("SansSerif", Font.BOLD,  28);
    private static final Font FB_26 = new Font("SansSerif", Font.BOLD,  26);
    private static final Font FP_14 = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FP_13 = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FP_12 = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FP_11 = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FP_10 = new Font("SansSerif", Font.PLAIN, 10);

    // ── Padding nhất quán ────────────────────────────────────
    private static final int PAD = 28;   // padding cột
    private static final int GAP = 14;   // khoảng cách giữa các phần

    // ── Data ─────────────────────────────────────────────────
    private final String tenThiSinh, sbd, maNganh, tenNganh, thuTu, tongDiem, trangThai;

    // ════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════
    public ChiTietNguyenVong(Frame owner, NguyenVongPanel.NguyenVong data) {
        super(owner, "Chi tiết Nguyện vọng", true);
        tenThiSinh = data != null ? data.getTenThiSinh() : "Nguyễn Văn An";
        sbd        = data != null ? data.getSbd()        : "2400015";
        maNganh    = data != null ? data.getMaNganh()    : "7480101";
        tenNganh   = data != null ? data.getTenNganh()   : "Khoa học máy tính";
        thuTu      = data != null ? data.getThuTu()      : "01";
        tongDiem   = data != null ? data.getTongDiem()   : "28.25";
        trangThai  = data != null ? data.getTrangThai()  : "Trúng tuyển";
        initUI();
    }

    public ChiTietNguyenVong(Frame owner) { this(owner, null); }

    private void initUI() {
        setSize(1100, 600);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(getOwner());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ════════════════════════════════════════════════════════
    //  HEADER
    // ════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout(12, 0));
        h.setBackground(WHITE);
        h.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
            new EmptyBorder(16, 24, 16, 24)
        ));

        // Icon + tiêu đề
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(40, 40));
        JLabel starLbl = new JLabel("★");
        starLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        starLbl.setForeground(PRIMARY);
        iconBox.add(starLbl);

        JLabel titleLbl = new JLabel("Chi tiết Nguyện vọng");
        titleLbl.setFont(FB_18);
        titleLbl.setForeground(TEXT_DARK);

        left.add(iconBox);
        left.add(titleLbl);

        // Nút đóng
        JButton closeBtn = new JButton("✕") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? SURFACE_M : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setFont(FB_15);
        closeBtn.setForeground(TEXT_MUTED);
        closeBtn.setPreferredSize(new Dimension(36, 36));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { closeBtn.setForeground(TEXT_DARK); closeBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { closeBtn.setForeground(TEXT_MUTED); closeBtn.repaint(); }
        });
        closeBtn.addActionListener(e -> dispose());

        h.add(left,     BorderLayout.WEST);
        h.add(closeBtn, BorderLayout.EAST);
        return h;
    }

    // ════════════════════════════════════════════════════════
    //  BODY – dùng tỷ lệ cột: 28% | 34% | 38%
    // ════════════════════════════════════════════════════════
    private JPanel buildBody() {
        // Dùng GridBagLayout để kiểm soát tỷ lệ cột chính xác
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.gridy   = 0;
        gbc.weighty = 1.0;

        // Cột 1: 28%
        gbc.gridx   = 0;
        gbc.weightx = 0.28;
        JPanel col1 = buildCol1();
        col1.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));
        body.add(col1, gbc);

        // Cột 2: 34%
        gbc.gridx   = 1;
        gbc.weightx = 0.34;
        JPanel col2 = buildCol2();
        col2.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));
        body.add(col2, gbc);

        // Cột 3: 38%
        gbc.gridx   = 2;
        gbc.weightx = 0.38;
        body.add(buildCol3(), gbc);

        return body;
    }

    // ════════════════════════════════════════════════════════
    //  CỘT 1 – Thông tin thí sinh
    // ════════════════════════════════════════════════════════
    private JPanel buildCol1() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(WHITE);
        col.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));

        // ── Avatar + tên ────────────────────────────────────
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        avatarRow.setOpaque(false);
        avatarRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));

        // Avatar vẽ tay
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AVATAR_BG);
                g2.fillRoundRect(0, 0, 72, 72, 14, 14);
                // icon người
                g2.setFont(new Font("SansSerif", Font.BOLD, 28));
                g2.setColor(new Color(0x94, 0xa3, 0xb8));
                g2.drawString("☺", 20, 50);
                // badge online
                g2.setColor(WHITE);    g2.fillOval(51, 51, 18, 18);
                g2.setColor(DOT_GREEN); g2.fillOval(54, 54, 12, 12);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(72, 72));
        avatar.setOpaque(false);

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);
        JLabel nameL = new JLabel(tenThiSinh);
        nameL.setFont(FB_16);
        nameL.setForeground(TEXT_DARK);
        JLabel typeL = new JLabel("Thí sinh tự do");
        typeL.setFont(FP_13);
        typeL.setForeground(TEXT_MUTED);
        nameBlock.add(nameL);
        nameBlock.add(Box.createVerticalStrut(5));
        nameBlock.add(typeL);

        avatarRow.add(avatar);
        avatarRow.add(nameBlock);

        // ── Divider ─────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // ── Info rows ───────────────────────────────────────
        JPanel infoBlock = new JPanel();
        infoBlock.setLayout(new BoxLayout(infoBlock, BoxLayout.Y_AXIS));
        infoBlock.setOpaque(false);
        infoBlock.add(buildInfoRow("SỐ BÁO DANH", sbd, true));
        infoBlock.add(Box.createVerticalStrut(16));
        infoBlock.add(buildInfoRow("CCCD", "012345678901", false));
        infoBlock.add(Box.createVerticalStrut(16));
        infoBlock.add(buildInfoRow("NGÀY SINH", "15/04/2006", false));

        col.add(avatarRow);
        col.add(Box.createVerticalStrut(GAP + 4));
        col.add(sep);
        col.add(Box.createVerticalStrut(GAP + 4));
        col.add(infoBlock);
        col.add(Box.createVerticalGlue());
        return col;
    }

    /** Một dòng label – value, full width */
    private JPanel buildInfoRow(String label, String value, boolean highlight) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FB_10);
        lbl.setForeground(TEXT_MUTED);

        if (highlight) {
            // Badge xanh nhạt
            JLabel val = new JLabel(value) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 22));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            val.setFont(FB_13);
            val.setForeground(PRIMARY);
            val.setOpaque(false);
            val.setBorder(new EmptyBorder(3, 10, 3, 10));
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
        } else {
            JLabel val = new JLabel(value);
            val.setFont(FP_13);
            val.setForeground(TEXT_DARK);
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
        }
        return row;
    }

    // ════════════════════════════════════════════════════════
    //  CỘT 2 – Thông tin đăng ký
    // ════════════════════════════════════════════════════════
    private JPanel buildCol2() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(SURFACE_L);
        col.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));

        // Tiêu đề section
        JLabel secT = new JLabel("THÔNG TIN ĐĂNG KÝ");
        secT.setFont(FB_10);
        secT.setForeground(PRIMARY);
        secT.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Ngành học ───────────────────────────────────────
        JPanel nganhBlock = new JPanel();
        nganhBlock.setLayout(new BoxLayout(nganhBlock, BoxLayout.Y_AXIS));
        nganhBlock.setOpaque(false);
        nganhBlock.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nganhCap = new JLabel("NGÀNH HỌC");
        nganhCap.setFont(FB_10);
        nganhCap.setForeground(TEXT_MUTED);

        JLabel nganhVal = new JLabel(tenNganh + " (" + maNganh + ")");
        nganhVal.setFont(FB_15);
        nganhVal.setForeground(TEXT_DARK);

        JLabel truongLbl = new JLabel("Trường Đại học Bách Khoa - ĐHQG TP.HCM");
        truongLbl.setFont(FP_12);
        truongLbl.setForeground(TEXT_MUTED);

        nganhBlock.add(nganhCap);
        nganhBlock.add(Box.createVerticalStrut(5));
        nganhBlock.add(nganhVal);
        nganhBlock.add(Box.createVerticalStrut(3));
        nganhBlock.add(truongLbl);

        // ── Tổ hợp + Thứ tự ─────────────────────────────────
        // Dùng GridLayout 2 cột bên trong panel có max-height
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 24, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Trái: Tổ hợp
        JPanel toHopPnl = new JPanel();
        toHopPnl.setLayout(new BoxLayout(toHopPnl, BoxLayout.Y_AXIS));
        toHopPnl.setOpaque(false);

        JLabel thCap = new JLabel("TỔ HỢP XÉT TUYỂN");
        thCap.setFont(FB_10);
        thCap.setForeground(TEXT_MUTED);

        // Badge A00 nền tối
        JLabel badgeA00 = new JLabel("  A00  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0f, 0x17, 0x2a));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badgeA00.setFont(FB_13);
        badgeA00.setForeground(WHITE);
        badgeA00.setOpaque(false);
        badgeA00.setMaximumSize(new Dimension(62, 28));

        JLabel monLbl = new JLabel("Toán, Lý, Hóa");
        monLbl.setFont(FP_11);
        monLbl.setForeground(TEXT_MUTED);

        toHopPnl.add(thCap);
        toHopPnl.add(Box.createVerticalStrut(7));
        toHopPnl.add(badgeA00);
        toHopPnl.add(Box.createVerticalStrut(5));
        toHopPnl.add(monLbl);

        // Phải: Thứ tự ưu tiên
        JPanel thuTuPnl = new JPanel();
        thuTuPnl.setLayout(new BoxLayout(thuTuPnl, BoxLayout.Y_AXIS));
        thuTuPnl.setOpaque(false);

        JLabel ttCap = new JLabel("THỨ TỰ ƯU TIÊN");
        ttCap.setFont(FB_10);
        ttCap.setForeground(TEXT_MUTED);

        JLabel ttVal = new JLabel(thuTu);
        ttVal.setFont(FB_28);
        ttVal.setForeground(PRIMARY);

        thuTuPnl.add(ttCap);
        thuTuPnl.add(Box.createVerticalStrut(5));
        thuTuPnl.add(ttVal);

        bottomRow.add(toHopPnl);
        bottomRow.add(thuTuPnl);

        col.add(secT);
        col.add(Box.createVerticalStrut(GAP + 4));
        col.add(nganhBlock);
        col.add(Box.createVerticalStrut(GAP + 4));
        col.add(bottomRow);
        col.add(Box.createVerticalGlue());
        return col;
    }

    // ════════════════════════════════════════════════════════
    //  CỘT 3 – Kết quả xét tuyển
    // ════════════════════════════════════════════════════════
    private JPanel buildCol3() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(WHITE);
        col.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));

        boolean trung = "Trúng tuyển".equals(trangThai);

        // ── Tiêu đề + badge trạng thái ──────────────────────
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel secT = new JLabel("KẾT QUẢ CHI TIẾT");
        secT.setFont(FB_10);
        secT.setForeground(PRIMARY);

        // Badge trạng thái
        JLabel statusBadge = new JLabel("  ● " + trangThai + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trung ? SUCCESS_BG : new Color(0xff, 0xfb, 0xeb));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBadge.setFont(FB_12);
        statusBadge.setForeground(trung ? SUCCESS_FG : new Color(0xd9, 0x77, 0x06));
        statusBadge.setOpaque(false);
        statusBadge.setBorder(new EmptyBorder(4, 4, 4, 4));

        titleRow.add(secT,        BorderLayout.WEST);
        titleRow.add(statusBadge, BorderLayout.EAST);

        // ── 3 ô điểm – GridLayout để BẰNG NHAU ─────────────
        JPanel scoresRow = new JPanel(new GridLayout(1, 3, 12, 0));
        scoresRow.setOpaque(false);
        scoresRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoresRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scoresRow.add(buildScoreCard("TOÁN",    "9.0"));
        scoresRow.add(buildScoreCard("VẬT LÝ",  "9.5"));
        scoresRow.add(buildScoreCard("HÓA HỌC", "9.0")); // fix bị cắt chữ

        // ── Điểm ưu tiên ────────────────────────────────────
        JPanel uuRow = new JPanel(new BorderLayout());
        uuRow.setOpaque(false);
        uuRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        uuRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel uuLbl = new JLabel("Điểm ưu tiên");
        uuLbl.setFont(FP_14);
        uuLbl.setForeground(TEXT_MUTED);
        JLabel uuVal = new JLabel("+0.75");
        uuVal.setFont(FB_14);
        uuVal.setForeground(TEXT_DARK);
        uuRow.add(uuLbl, BorderLayout.WEST);
        uuRow.add(uuVal, BorderLayout.EAST);

        // ── Banner tổng điểm ─────────────────────────────────
        JPanel totalBox = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        totalBox.setOpaque(false);
        totalBox.setBorder(new EmptyBorder(16, 20, 16, 20));
        totalBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        totalBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel totalLbl = new JLabel("TỔNG ĐIỂM XÉT TUYỂN");
        totalLbl.setFont(FB_11);
        totalLbl.setForeground(new Color(255, 255, 255, 210));

        JLabel totalVal = new JLabel(tongDiem);
        totalVal.setFont(FB_26);
        totalVal.setForeground(WHITE);

        totalBox.add(totalLbl, BorderLayout.WEST);
        totalBox.add(totalVal, BorderLayout.EAST);

        col.add(titleRow);
        col.add(Box.createVerticalStrut(GAP));
        col.add(scoresRow);
        col.add(Box.createVerticalStrut(GAP));
        col.add(uuRow);
        col.add(Box.createVerticalStrut(GAP));
        col.add(totalBox);
        col.add(Box.createVerticalGlue());
        return col;
    }

    /** Ô điểm từng môn */
    private JPanel buildScoreCard(String subject, String score) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE_M);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 6, 12, 6));

        JLabel subL = new JLabel(subject, SwingConstants.CENTER);
        subL.setFont(FB_10);
        subL.setForeground(TEXT_MUTED);
        subL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreL = new JLabel(score, SwingConstants.CENTER);
        scoreL.setFont(new Font("SansSerif", Font.BOLD, 22));
        scoreL.setForeground(TEXT_DARK);
        scoreL.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(subL);
        card.add(Box.createVerticalStrut(5));
        card.add(scoreL);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  FOOTER
    // ════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SURFACE_L);
        footer.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C),
            new EmptyBorder(14, 24, 14, 24)
        ));

        // Nút Xóa hồ sơ
        JButton deleteBtn = new JButton("🗑  Xóa hồ sơ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ERROR_BG : SURFACE_L);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        deleteBtn.setFont(FB_13);
        deleteBtn.setForeground(ERROR_C);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setBorder(new EmptyBorder(10, 16, 10, 16));
        deleteBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hồ sơ này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) dispose();
        });

        // Nhóm nút bên phải
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightGroup.setOpaque(false);

        JButton editBtn = makeOutlineBtn("Chỉnh sửa");
        editBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Chức năng Chỉnh sửa chưa triển khai.", "Chỉnh sửa",
            JOptionPane.INFORMATION_MESSAGE));

        JButton approveBtn = makePrimaryBtn("Duyệt hồ sơ");
        approveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Đã duyệt hồ sơ thành công!", "Duyệt hồ sơ",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        rightGroup.add(editBtn);
        rightGroup.add(approveBtn);

        footer.add(deleteBtn,  BorderLayout.WEST);
        footer.add(rightGroup, BorderLayout.EAST);
        return footer;
    }

    // ════════════════════════════════════════════════════════
    //  HELPER – Button builders
    // ════════════════════════════════════════════════════════
    private JButton makeOutlineBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xf8, 0xfa, 0xfc) : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FB_13);
        btn.setForeground(TEXT_SLATE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        return btn;
    }

    private JButton makePrimaryBtn(String text) {
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
        btn.setFont(FB_13);
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 30, 10, 30));
        return btn;
    }

    // ════════════════════════════════════════════════════════
    //  MAIN – test độc lập
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChiTietNguyenVong dlg = new ChiTietNguyenVong(null);
            dlg.setVisible(true);
        });
    }
}