package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ChiTietNguyenVong extends JDialog {

    // ────────────────────────────── Màu sắc ──────────────────────────────
    private static final Color PRIMARY     = new Color(0x13, 0x7f, 0xec);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BORDER_LIGHT= new Color(0xe2, 0xe8, 0xf0);
    private static final Color TEXT_DARK   = new Color(0x0f, 0x17, 0x2a);
    private static final Color TEXT_MUTED  = new Color(0x64, 0x74, 0x8b);
    private static final Color TEXT_SLATE  = new Color(0x33, 0x41, 0x55);
    private static final Color SURFACE_L   = new Color(0xf8, 0xfa, 0xfc);
    private static final Color SURFACE_M   = new Color(0xf1, 0xf5, 0xf9);
    private static final Color ERROR_RED   = new Color(0xef, 0x44, 0x44);
    private static final Color ERROR_BG    = new Color(0xfe, 0xe2, 0xe2);
    private static final Color SUCCESS_BG  = new Color(0xf0, 0xfd, 0xf4);
    private static final Color SUCCESS_FG  = new Color(0x16, 0xa3, 0x4a);
    private static final Color DOT_GREEN   = new Color(0x22, 0xc5, 0x5e);
    private static final Color AVATAR_BG   = new Color(0xcb, 0xd5, 0xe1);
    private static final int SPACE_2XL = 40;
    // ────────────────────────────── Hệ thống spacing ─────────────────────
    private static final int SPACE_XS = 4;
    private static final int SPACE_SM = 8;
    private static final int SPACE_MD = 16;
    private static final int SPACE_LG = 24;
    private static final int SPACE_XL = 32;

    // ────────────────────────────── Font chữ thống nhất ──────────────────
    private static final String FONT_FAMILY = "Segoe UI, Inter, SansSerif";
    private static final Font FONT_TITLE      = new Font(FONT_FAMILY, Font.BOLD, 18);
    private static final Font FONT_SUBTITLE   = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BODY_BOLD  = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font FONT_BODY_PLAIN = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_LABEL      = new Font(FONT_FAMILY, Font.BOLD, 11);
    private static final Font FONT_LARGE_NUM  = new Font(FONT_FAMILY, Font.BOLD, 28);
    private static final Font FONT_MEDIUM_NUM = new Font(FONT_FAMILY, Font.BOLD, 22);
    private static final Font FONT_BUTTON     = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font FONT_LABEL_LARGE  = new Font(FONT_FAMILY, Font.BOLD, 12);
    private static final Font FONT_BODY_LARGE   = new Font(FONT_FAMILY, Font.PLAIN, 15);
    private static final Font FONT_BODY_BOLD_LARGE = new Font(FONT_FAMILY, Font.BOLD, 15);

    private static final DecimalFormat SCORE_FMT = new DecimalFormat("0.00");

    // ────────────────────────────── Dữ liệu ──────────────────────────────
    private final String tenThiSinh;
    private final String sbd;
    private final String cccd;
    private final String ngaySinh;
    private final String maNganh;
    private final String tenNganh;
    private final String thuTu;
    private final String tongDiem;
    private final String trangThai;
    private final String toHop;
    private final String phuongThuc;
    private final BigDecimal diemThxt;
    private final BigDecimal diemCong;
    private final BigDecimal diemUtqd;

    // ────────────────────────────── Constructor ──────────────────────────
    public ChiTietNguyenVong(Frame owner, NguyenVongPanel.NguyenVong data) {
        super(owner, "Chi tiết Nguyện vọng", true);
        tenThiSinh = (data != null) ? data.getTenThiSinh() : "Nguyễn Văn An";
        sbd        = (data != null) ? data.getSbd()        : "2400015";
        cccd       = (data != null) ? data.getCccd()       : "012345678901";
        ngaySinh   = (data != null) ? data.getNgaySinh()   : "15/04/2006";
        maNganh    = (data != null) ? data.getMaNganh()    : "7480101";
        tenNganh   = (data != null) ? data.getTenNganh()   : "Khoa học máy tính";
        thuTu      = (data != null) ? data.getThuTu()      : "01";
        tongDiem   = (data != null) ? data.getTongDiem()   : "28.25";
        trangThai  = (data != null) ? data.getTrangThai()  : "Trúng tuyển";
        toHop      = (data != null) ? data.getToHop()      : "A00";
        phuongThuc = (data != null) ? data.getPhuongThuc() : "PT4";
        diemThxt   = (data != null) ? data.getDiemThxt()   : null;
        diemCong   = (data != null) ? data.getDiemCong()   : null;
        diemUtqd   = (data != null) ? data.getDiemUtqd()   : null;
        initUI();
    }

    public ChiTietNguyenVong(Frame owner) { this(owner, null); }

    // ────────────────────────────── Khởi tạo giao diện ───────────────────
    private void initUI() {
        setSize(1100, 600);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(getOwner());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(),   BorderLayout.CENTER);
        setContentPane(root);
    }

    // ════════════════════════════════════════════════════════════════════
    //  HEADER – Tiêu đề và nút đóng
    // ════════════════════════════════════════════════════════════════════
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(SPACE_SM, 0));
        header.setBackground(WHITE);
        header.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
            new EmptyBorder(SPACE_MD, SPACE_LG, SPACE_MD, SPACE_LG)
        ));

        // Tiêu đề căn giữa
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        JLabel title = new JLabel("Chi tiết Nguyện vọng");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        center.add(title);

        header.add(center, BorderLayout.CENTER);
        return header;
    }

    // ════════════════════════════════════════════════════════════════════
    //  BODY – 3 cột với tỉ lệ 28% | 34% | 38%
    // ════════════════════════════════════════════════════════════════════
    private JPanel createBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.28;
        JPanel col1 = createCol1();
        col1.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_LIGHT));
        body.add(col1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.34;
        JPanel col2 = createCol2();
        col2.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_LIGHT));
        body.add(col2, gbc);

        gbc.gridx = 2; gbc.weightx = 0.38;
        body.add(createCol3(), gbc);
        return body;
    }

    // ════════════════════════════════════════════════════════════════════
    //  CỘT 1 – Thông tin thí sinh
    // ════════════════════════════════════════════════════════════════════
    private JPanel createCol1() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(WHITE);
        col.setBorder(new EmptyBorder(SPACE_LG, SPACE_2XL, SPACE_LG, SPACE_LG));

        col.add(createAvatarRow());
        col.add(Box.createVerticalStrut(SPACE_LG));
        col.add(new JSeparator() {{ setForeground(BORDER_LIGHT); }});
        col.add(Box.createVerticalStrut(SPACE_LG));
        col.add(createInfoBlock());
        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel createInfoBlock() {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.add(createInfoCard("SỐ BÁO DANH", sbd, true));
        block.add(Box.createVerticalStrut(SPACE_LG));
        block.add(createInfoCard("CCCD", cccd, false));
        block.add(Box.createVerticalStrut(SPACE_LG));
        block.add(createInfoCard("NGÀY SINH", ngaySinh, false));
        return block;
    }

    private JPanel createInfoCard(String label, String value, boolean highlight) {
        JPanel card = new JPanel(new BorderLayout(SPACE_MD, 0));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        if (highlight) {
            card.setBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 8));
            card.setOpaque(true);
            card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 30), 1),
                new EmptyBorder(SPACE_SM, SPACE_MD, SPACE_SM, SPACE_MD)
            ));
        } else {
            // Thêm padding trái/phải giống hệt card highlight
            card.setBorder(new EmptyBorder(SPACE_SM, SPACE_MD, SPACE_SM, SPACE_MD));
        }

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL_LARGE);
        lbl.setForeground(TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(highlight ? FONT_BODY_BOLD_LARGE : FONT_BODY_LARGE);
        val.setForeground(highlight ? PRIMARY : TEXT_DARK);

        card.add(lbl, BorderLayout.WEST);
        card.add(val, BorderLayout.EAST);
        return card;
    }

    private JPanel createAvatarRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACE_LG, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Avatar vẽ tay
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AVATAR_BG);
                g2.fillRoundRect(0, 0, 80, 80, 16, 16);
                g2.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
                g2.setColor(new Color(0x94, 0xa3, 0xb8));
                g2.drawString("☺", 22, 54);
                g2.setColor(WHITE); 
                g2.fillOval(58, 58, 18, 18);
                g2.setColor(DOT_GREEN); 
                g2.fillOval(61, 61, 12, 12);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setOpaque(false);

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);
        
        JLabel name = new JLabel(tenThiSinh);
        name.setFont(new Font(FONT_FAMILY, Font.BOLD, 18)); // chữ to hơn
        name.setForeground(TEXT_DARK);
        
        JLabel type = new JLabel("Thí sinh tự do");
        type.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14)); // to hơn
        type.setForeground(TEXT_MUTED);
        
        nameBlock.add(name);
        nameBlock.add(Box.createVerticalStrut(SPACE_XS));
        nameBlock.add(type);

        row.add(avatar);
        row.add(Box.createHorizontalStrut(SPACE_MD));
        row.add(nameBlock);
        return row;
    }

    // ════════════════════════════════════════════════════════════════════
    //  CỘT 2 – Thông tin đăng ký
    // ════════════════════════════════════════════════════════════════════
    private JPanel createCol2() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(SURFACE_L);
        col.setBorder(new EmptyBorder(SPACE_LG, SPACE_LG, SPACE_LG, SPACE_LG));

        JLabel sectionTitle = new JLabel("THÔNG TIN ĐĂNG KÝ");
        sectionTitle.setFont(FONT_LABEL_LARGE);
        sectionTitle.setForeground(PRIMARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        col.add(sectionTitle);
        col.add(Box.createVerticalStrut(SPACE_MD));
        col.add(createNganhCard());
        col.add(Box.createVerticalStrut(SPACE_LG));
        col.add(createToHopThuTuPanel());
        col.add(Box.createVerticalStrut(SPACE_LG));
        col.add(createPhuongThucCard());
        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel createNganhCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("NGÀNH HỌC");
        label.setFont(FONT_LABEL_LARGE);
        label.setForeground(TEXT_MUTED);
        JLabel value = new JLabel(tenNganh + " (" + maNganh + ")");
        value.setFont(FONT_BODY_BOLD_LARGE);
        value.setForeground(TEXT_DARK);
        JLabel school = new JLabel("Trường Đại học Sài Gòn");
        school.setFont(FONT_BODY_LARGE);
        school.setForeground(TEXT_MUTED);

        card.add(label);
        card.add(Box.createVerticalStrut(SPACE_XS));
        card.add(value);
        card.add(Box.createVerticalStrut(SPACE_XS));
        card.add(school);
        return card;
    }

    private JPanel createToHopThuTuPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, SPACE_LG, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Bên trái: Tổ hợp
        JPanel toHopCard = new JPanel();
        toHopCard.setLayout(new BoxLayout(toHopCard, BoxLayout.Y_AXIS));
        toHopCard.setBackground(WHITE);
        toHopCard.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD)
        ));
        JLabel thLabel = new JLabel("TỔ HỢP XÉT TUYỂN");
        thLabel.setFont(FONT_LABEL_LARGE);
        thLabel.setForeground(TEXT_MUTED);
        JLabel badge = new JLabel("  " + toHop + "  ");
        badge.setFont(FONT_BODY_BOLD_LARGE);
        badge.setForeground(WHITE);
        badge.setOpaque(true);
        badge.setBackground(TEXT_DARK);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        JLabel mon = new JLabel(getToHopLabel(toHop));
        mon.setFont(FONT_BODY_LARGE);
        mon.setForeground(TEXT_MUTED);

        toHopCard.add(thLabel);
        toHopCard.add(Box.createVerticalStrut(SPACE_SM));
        toHopCard.add(badge);
        toHopCard.add(Box.createVerticalStrut(SPACE_SM));
        toHopCard.add(mon);

        // Bên phải: Thứ tự ưu tiên
        JPanel thuTuCard = new JPanel();
        thuTuCard.setLayout(new BoxLayout(thuTuCard, BoxLayout.Y_AXIS));
        thuTuCard.setBackground(WHITE);
        thuTuCard.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD)
        ));
        JLabel ttLabel = new JLabel("THỨ TỰ ƯU TIÊN");
        ttLabel.setFont(FONT_LABEL_LARGE);
        ttLabel.setForeground(TEXT_MUTED);
        JLabel ttValue = new JLabel(thuTu);
        ttValue.setFont(new Font(FONT_FAMILY, Font.BOLD, 36));  // số to hơn
        ttValue.setForeground(PRIMARY);

        thuTuCard.add(ttLabel);
        thuTuCard.add(Box.createVerticalStrut(SPACE_SM));
        thuTuCard.add(ttValue);

        panel.add(toHopCard);
        panel.add(thuTuCard);
        return panel;
    }

    private JPanel createPhuongThucCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("PHƯƠNG THỨC");
        label.setFont(FONT_LABEL_LARGE);
        label.setForeground(TEXT_MUTED);
        JLabel value = new JLabel(phuongThuc.isEmpty() ? "--" : phuongThuc);
        value.setFont(FONT_BODY_BOLD_LARGE);
        value.setForeground(TEXT_DARK);
        card.add(label, BorderLayout.WEST);
        card.add(value, BorderLayout.EAST);
        return card;
    }

    // ════════════════════════════════════════════════════
    //  CỘT 3 – Kết quả xét tuyển
    // ════════════════════════════════════════════════════════════════════
    private JPanel createCol3() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(WHITE);
        col.setBorder(new EmptyBorder(SPACE_LG, SPACE_LG, SPACE_LG, SPACE_LG));

        boolean isPass = "Trúng tuyển".equals(trangThai);
        col.add(createResultHeader(isPass));
        col.add(Box.createVerticalStrut(SPACE_MD));
        col.add(createScoresRow());
        col.add(Box.createVerticalStrut(SPACE_MD));
        col.add(createToHopRow());
        col.add(Box.createVerticalStrut(SPACE_LG));
        col.add(createTotalBox());
        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel createResultHeader(boolean isPass) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("KẾT QUẢ CHI TIẾT");
        title.setFont(FONT_LABEL);
        title.setForeground(PRIMARY);

        JLabel badge = new JLabel("  ● " + trangThai + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isPass ? SUCCESS_BG : new Color(0xff, 0xfb, 0xeb));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_LABEL);
        badge.setForeground(isPass ? SUCCESS_FG : new Color(0xd9, 0x77, 0x06));
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(SPACE_XS, SPACE_SM, SPACE_XS, SPACE_SM));

        row.add(title, BorderLayout.WEST);
        row.add(badge, BorderLayout.EAST);
        return row;
    }

    private JPanel createScoresRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, SPACE_MD, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        row.add(createScoreCard("ĐTHXT", formatScore(diemThxt)));
        row.add(createScoreCard("ĐIỂM CỘNG", formatScore(diemCong)));
        row.add(createScoreCard("ĐƯT", formatScore(diemUtqd)));
        return row;
    }

    private JPanel createScoreCard(String subject, String score) {
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
        card.setBorder(new EmptyBorder(SPACE_MD, SPACE_SM, SPACE_MD, SPACE_SM));

        JLabel sub = new JLabel(subject);
        sub.setFont(FONT_LABEL);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sco = new JLabel(score);
        sco.setFont(FONT_MEDIUM_NUM);
        sco.setForeground(TEXT_DARK);
        sco.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(sub);
        card.add(Box.createVerticalStrut(SPACE_XS));
        card.add(sco);
        return card;
    }

    private JPanel createToHopRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("Tổ hợp xét tuyển");
        label.setFont(FONT_BODY_PLAIN);
        label.setForeground(TEXT_MUTED);
        JLabel value = new JLabel(toHop.isEmpty() ? "--" : toHop);
        value.setFont(FONT_BODY_BOLD);
        value.setForeground(TEXT_DARK);
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel createTotalBox() {
        JPanel box = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(SPACE_MD, SPACE_LG, SPACE_MD, SPACE_LG));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("TỔNG ĐIỂM XÉT TUYỂN");
        label.setFont(FONT_LABEL);
        label.setForeground(new Color(255, 255, 255, 210));

        JLabel value = new JLabel(tongDiem);
        value.setFont(FONT_LARGE_NUM);
        value.setForeground(WHITE);

        box.add(label, BorderLayout.WEST);
        box.add(value, BorderLayout.EAST);
        return box;
    }

    // ────────────────────────────── Helper ──────────────────────────────
    private String formatScore(BigDecimal value) {
        return (value == null) ? "--" : SCORE_FMT.format(value);
    }

    private String getToHopLabel(String code) {
        String v = (code == null) ? "" : code.trim().toUpperCase();
        switch (v) {
            case "A00": return "Toán, Lý, Hóa";
            case "A01": return "Toán, Lý, Anh";
            case "B00": return "Toán, Hóa, Sinh";
            case "C00": return "Văn, Sử, Địa";
            case "C01": return "Văn, Toán, Lý";
            case "D01": return "Văn, Toán, Anh";
            case "D07": return "Toán, Hóa, Anh";
            default:   return v.isEmpty() ? "--" : v;
        }
    }

    // ────────────────────────────── Test ────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChiTietNguyenVong(null).setVisible(true));
    }
}