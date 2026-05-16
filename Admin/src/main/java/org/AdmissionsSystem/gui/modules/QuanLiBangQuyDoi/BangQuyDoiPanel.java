package org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.AdmissionsSystem.bus.service.BangQuyDoiService;
import org.AdmissionsSystem.models.XtBangquydoi;

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
    private final BangQuyDoiService bangQuyDoiService = new BangQuyDoiService();
    private List<XtBangquydoi> dataList = new ArrayList<>();
    private List<XtBangquydoi> filteredList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable dataTable;

    // ── Filter variables ──────────────────────────────────
    private JComboBox<String> filterPhuongThuc;
    private JComboBox<String> filterTohop;
    private JComboBox<String> filterMon;

    // ── Pagination variables ──────────────────────────────
    private static final int RECORDS_PER_PAGE = 9;
    private int currentPage = 1;
    private int totalPages = 1;
    private JPanel paginationPanel;

    public BangQuyDoiPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // Load dữ liệu từ database
        loadData();
        calculateTotalPages();

        JPanel content = buildMainPanel();
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ──────────────────────────────────────────────────────────
    // LOAD DATA FROM SERVICE
    // ──────────────────────────────────────────────────────────
    private void loadData() {
        try {
            dataList = bangQuyDoiService.getAll();
            if (dataList == null) dataList = new ArrayList<>();
            filteredList = new ArrayList<>(dataList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            dataList = new ArrayList<>();
            filteredList = new ArrayList<>();
        }
    }

    private void applyFilters() {
        String selectedPhuongThuc = (String) filterPhuongThuc.getSelectedItem();
        String selectedTohop = (String) filterTohop.getSelectedItem();
        String selectedMon = (String) filterMon.getSelectedItem();

        filteredList.clear();
        for (XtBangquydoi record : dataList) {
            boolean matchPhuongThuc = "Chọn phương thức".equals(selectedPhuongThuc) ||
                    (record.getDPhuongthuc() != null && record.getDPhuongthuc().equals(selectedPhuongThuc));
            boolean matchTohop = "Tất cả tổ hợp".equals(selectedTohop) ||
                    (record.getDTohop() != null && record.getDTohop().equals(selectedTohop));
            boolean matchMon = "Tất cả môn".equals(selectedMon) ||
                    (record.getDMon() != null && record.getDMon().equals(selectedMon));

            if (matchPhuongThuc && matchTohop && matchMon) {
                filteredList.add(record);
            }
        }
        currentPage = 1;  // Reset về trang 1
        calculateTotalPages();
        updateTable();
        updatePaginationUI();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        
        // Tính toán chỉ số start và end cho trang hiện tại
        int startIdx = (currentPage - 1) * RECORDS_PER_PAGE;
        int endIdx = Math.min(startIdx + RECORDS_PER_PAGE, filteredList.size());
        
        for (int i = startIdx; i < endIdx; i++) {
            XtBangquydoi record = filteredList.get(i);
            String khoangDiem = formatKhoangDiem(record);
            String khoangDiemSau = formatKhoangDiemSau(record);
            String phanvi = record.getDPhanvi() != null ? record.getDPhanvi() : "";
            tableModel.addRow(new Object[]{
                    record.getIdqd(),
                    record.getDPhuongthuc() != null ? record.getDPhuongthuc() : "",
                    record.getDTohop() != null ? record.getDTohop() : "-",
                    record.getDMon() != null ? record.getDMon() : "-",
                    khoangDiem,
                    khoangDiemSau,
                    phanvi,
                    ""  // Hành động (sẽ render bằng button)
            });
        }
    }

    private void calculateTotalPages() {
        totalPages = (int) Math.ceil((double) filteredList.size() / RECORDS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
    }

    private void goToPage(int pageNum) {
        if (pageNum >= 1 && pageNum <= totalPages) {
            currentPage = pageNum;
            updateTable();
            updatePaginationUI();
        }
    }

    private void updatePaginationUI() {
        if (paginationPanel == null) return;
        paginationPanel.removeAll();
        paginationPanel.add(buildPaginationContent(), BorderLayout.CENTER);
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private JPanel buildPaginationContent() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));

        int displayStart = filteredList.size() > 0 ? (currentPage - 1) * RECORDS_PER_PAGE + 1 : 0;
        int displayEnd = Math.min(currentPage * RECORDS_PER_PAGE, filteredList.size());
        int total = filteredList.size();

        JLabel info = new JLabel(String.format("Hiển thị %d - %d trong tổng số %d quy tắc", displayStart, displayEnd, total));
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(TEXT_MUTED);

        JPanel pages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pages.setOpaque(false);
        
        // Nút Previous
        JButton prevBtn = createNavButton("‹", () -> goToPage(currentPage - 1));
        prevBtn.addMouseListener(new HoverAdapter(prevBtn, WHITE, SURFACE));
        prevBtn.addActionListener(e -> goToPage(currentPage - 1));
        pages.add(prevBtn);
        
        // Các nút trang
        int visiblePages = Math.min(5, totalPages);
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + visiblePages - 1);
        
        if (endPage - startPage + 1 < visiblePages) {
            startPage = Math.max(1, endPage - visiblePages + 1);
        }
        
        // Nút "1" nếu không ở trang đầu
        if (startPage > 1) {
            JButton btn = createPageButton("1", 1);
            pages.add(btn);
            if (startPage > 2) {
                JLabel dots = new JLabel("...");
                dots.setForeground(TEXT_LIGHT);
                pages.add(dots);
            }
        }
        
        // Các trang ở giữa
        for (int p = startPage; p <= endPage; p++) {
            JButton btn = createPageButton(String.valueOf(p), p);
            pages.add(btn);
        }
        
        // Nút "..." và trang cuối nếu không ở trang cuối
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                JLabel dots = new JLabel("...");
                dots.setForeground(TEXT_LIGHT);
                pages.add(dots);
            }
            JButton btn = createPageButton(String.valueOf(totalPages), totalPages);
            pages.add(btn);
        }
        
        // Nút Next
        JButton nextBtn = createNavButton("›", () -> goToPage(currentPage + 1));
        nextBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nextBtn.setBorder(new CompoundBorder(
                new RoundedBorder(6, BORDER),
                new EmptyBorder(2, 2, 2, 2)
        ));
        nextBtn.addMouseListener(new HoverAdapter(nextBtn, WHITE, SURFACE));
        nextBtn.addActionListener(e -> goToPage(currentPage + 1));
        pages.add(nextBtn);

        row.add(info, BorderLayout.WEST);
        row.add(pages, BorderLayout.EAST);
        return row;
    }

    private JButton createNavButton(String text, Runnable action) {

        JButton btn = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                if (getModel().isRollover()) {
                    g2.setColor(new Color(0xf1, 0xf5, 0xf9));
                } else {
                    g2.setColor(WHITE);
                }

                g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    10,
                    10
                );

                g2.setColor(BORDER);

                g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    10,
                    10
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(36, 36));

        btn.setFocusPainted(false);
        btn.setFocusable(false);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setForeground(TEXT_MUTED);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> action.run());

        return btn;
    }

    private JButton createPageButton(String label, int pageNum) {

        boolean active = currentPage == pageNum;

        JButton btn = new JButton(label) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Background
                if (active) {
                    g2.setColor(PRIMARY);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0xf1, 0xf5, 0xf9));
                } else {
                    g2.setColor(WHITE);
                }

                g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    10,
                    10
                );

                // Border
                g2.setColor(active ? PRIMARY : BORDER);
                g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    10,
                    10
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(36, 36));
        btn.setMargin(new Insets(0, 0, 0, 0));

        btn.setFocusPainted(false);
        btn.setFocusable(false);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setForeground(active ? WHITE : TEXT_MUTED);

        btn.setFont(new Font(
            "SansSerif",
            active ? Font.BOLD : Font.PLAIN,
            12
        ));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final int page = pageNum;
        btn.addActionListener(e -> goToPage(page));

        return btn;
    }

    private String formatKhoangDiem(XtBangquydoi record) {
        if (record.getDDiema() != null && record.getDDiemb() != null) {
            return record.getDDiema() + " - " + record.getDDiemb();
        }
        return "";
    }

    private String formatKhoangDiemSau(XtBangquydoi record) {
        if (record.getDDiemc() != null && record.getDDiemd() != null) {
            return record.getDDiemc() + " - " + record.getDDiemd();
        }
        return "";
    }

    // ──────────────────────────────────────────────────────────
    // MAIN PANEL
    // ──────────────────────────────────────────────────────────
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

        // Lấy các giá trị unique từ database
        Set<String> uniquePhuongThuc = new HashSet<>();
        Set<String> uniqueTohop = new HashSet<>();
        Set<String> uniqueMon = new HashSet<>();

        for (XtBangquydoi record : dataList) {
            if (record.getDPhuongthuc() != null) uniquePhuongThuc.add(record.getDPhuongthuc());
            if (record.getDTohop() != null) uniqueTohop.add(record.getDTohop());
            if (record.getDMon() != null) uniqueMon.add(record.getDMon());
        }

        String[] phuongThucArray = new String[uniquePhuongThuc.size() + 1];
        phuongThucArray[0] = "Chọn phương thức";
        int idx = 1;
        for (String pt : uniquePhuongThuc) {
            phuongThucArray[idx++] = pt;
        }

        String[] tohopArray = new String[uniqueTohop.size() + 1];
        tohopArray[0] = "Tất cả tổ hợp";
        idx = 1;
        for (String th : uniqueTohop) {
            tohopArray[idx++] = th;
        }

        String[] monArray = new String[uniqueMon.size() + 1];
        monArray[0] = "Tất cả môn";
        idx = 1;
        for (String m : uniqueMon) {
            monArray[idx++] = m;
        }

        JPanel f1 = buildFilterGroup("LOẠI QUY ĐỔI", new String[]{"Tất cả loại", "Chứng chỉ", "Điểm ưu tiên"});
        JPanel f2 = buildFilterGroup("PHƯƠNG THỨC", phuongThucArray);
        JPanel f3 = buildFilterGroup("TỔ HỢP", tohopArray);
        JPanel f4 = buildFilterGroup("MÔN", monArray);

        // Lưu reference để sử dụng sau
        // Index 2 vì: [0]=Label, [1]=Strut, [2]=JComboBox
        filterPhuongThuc = (JComboBox<String>) f2.getComponent(2);
        filterTohop = (JComboBox<String>) f3.getComponent(2);
        filterMon = (JComboBox<String>) f4.getComponent(2);

        // Thêm listener cho filter
        filterPhuongThuc.addActionListener(e -> applyFilters());
        filterTohop.addActionListener(e -> applyFilters());
        filterMon.addActionListener(e -> applyFilters());

        gbc.weightx = 1; gbc.gridx = 0; row.add(f1, gbc);
        gbc.gridx = 1; row.add(f2, gbc);
        gbc.gridx = 2; row.add(f3, gbc);
        gbc.gridx = 3; row.add(f4, gbc);

        JButton addBtn = makePrimaryButton("＋  Thêm mới");
        addBtn.addActionListener(e -> handleAddNew());
        gbc.weightx = 0; 
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 4; 
        row.add(addBtn, gbc);

        card.add(row, BorderLayout.CENTER);
        return card;
    }

    private void handleAddNew() {
        JOptionPane.showMessageDialog(this, "Chức năng thêm mới sẽ được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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

        String[] COLS = {"ID", "PHƯƠNG THỨC", "TỔ HỢP", "MÔN", "KHOẢNG ĐIỂM", "KHOẢNG ĐIỂM (SAU QUY ĐỔI)", "PHÂN VỊ", "HÀNH ĐỘNG"};
        tableModel = new DefaultTableModel(new Object[0][0], COLS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        dataTable = new JTable(tableModel);

        // Cập nhật bảng với dữ liệu hiện tại
        updateTable();

        dataTable.setRowHeight(42);
        dataTable.setShowHorizontalLines(true);
        dataTable.setShowVerticalLines(false);
        dataTable.setGridColor(SURFACE);
        dataTable.setBackground(WHITE);
        dataTable.setSelectionBackground(new Color(0xef, 0xf6, 0xff));
        dataTable.setSelectionForeground(TEXT_DARK);
        dataTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dataTable.setFocusable(false);
        dataTable.setFillsViewportHeight(true);

        JTableHeader header = dataTable.getTableHeader();
        header.setBackground(new Color(0xf8, 0xfa, 0xfc));
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 10));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        int[] widths = {80, 140, 100, 110, 140, 100, 160, 120};
        for (int i = 0; i < widths.length; i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Col ID
        dataTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setForeground(PRIMARY);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        // Default renderers for other cols
        DefaultTableCellRenderer textCol = new DefaultTableCellRenderer();
        textCol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textCol.setForeground(TEXT_DARK);
        textCol.setHorizontalAlignment(SwingConstants.LEFT);
        dataTable.getColumnModel().getColumn(1).setCellRenderer(textCol);

        DefaultTableCellRenderer lightCol = new DefaultTableCellRenderer();
        lightCol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lightCol.setForeground(TEXT_LIGHT);
        lightCol.setHorizontalAlignment(SwingConstants.CENTER);
        dataTable.getColumnModel().getColumn(2).setCellRenderer(lightCol);

        DefaultTableCellRenderer monCol = new DefaultTableCellRenderer();
        monCol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        monCol.setForeground(TEXT_DARK);
        monCol.setHorizontalAlignment(SwingConstants.LEFT);
        dataTable.getColumnModel().getColumn(3).setCellRenderer(monCol);

        DefaultTableCellRenderer boldCol = new DefaultTableCellRenderer();
        boldCol.setFont(new Font("SansSerif", Font.BOLD, 13));
        boldCol.setForeground(TEXT_DARK);
        boldCol.setHorizontalAlignment(SwingConstants.LEFT);
        dataTable.getColumnModel().getColumn(4).setCellRenderer(boldCol);

        // Col Khoảng điểm sau quy đổi
        DefaultTableCellRenderer diemSauCol = new DefaultTableCellRenderer();
        diemSauCol.setFont(new Font("SansSerif", Font.BOLD, 13));
        diemSauCol.setForeground(TEXT_DARK);
        diemSauCol.setHorizontalAlignment(SwingConstants.CENTER);
        dataTable.getColumnModel().getColumn(5).setCellRenderer(diemSauCol);

        // Col Phân vị
        dataTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                String val = v == null ? "" : v.toString();
                lbl.setHorizontalAlignment(SwingConstants.LEADING);
                lbl.setForeground(val.startsWith("+") ? SUCCESS : PRIMARY);
                return lbl;
            }
        });

        // Col Hành động
        dataTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
                p.setOpaque(true);
                p.setBackground(s ? t.getSelectionBackground() : WHITE);
                int rowIdx = r;
                JButton editBtn = makeActionButton("✏", PRIMARY, new Color(0x0f, 0x6f, 0xd4));
                editBtn.addActionListener(e -> handleEdit(rowIdx));
                JButton deleteBtn = makeActionButton("🗑", new Color(0xdc, 0x26, 0x26), new Color(0xb9, 0x1f, 0x1f));
                deleteBtn.addActionListener(e -> handleDelete(rowIdx));
                p.add(editBtn);
                p.add(deleteBtn);
                return p;
            }
        });

        JScrollPane tableScroll = new JScrollPane(dataTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(tableScroll, BorderLayout.NORTH); 
        card.add(wrapper, BorderLayout.CENTER);
        
        paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setOpaque(false);
        paginationPanel.add(buildPaginationContent(), BorderLayout.CENTER);
        card.add(paginationPanel, BorderLayout.SOUTH);
        return card;
    }

    private void handleEdit(int rowIdx) {
        int actualIdx = (currentPage - 1) * RECORDS_PER_PAGE + rowIdx;
        if (actualIdx >= 0 && actualIdx < filteredList.size()) {
            XtBangquydoi record = filteredList.get(actualIdx);
            JOptionPane.showMessageDialog(this, "Sửa quy đổi ID: " + record.getIdqd(), 
                    "Sửa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDelete(int rowIdx) {
        int actualIdx = (currentPage - 1) * RECORDS_PER_PAGE + rowIdx;
        if (actualIdx >= 0 && actualIdx < filteredList.size()) {
            XtBangquydoi record = filteredList.get(actualIdx);
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", 
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    bangQuyDoiService.delete(record.getIdqd());
                    loadData();
                    calculateTotalPages();
                    applyFilters();
                    JOptionPane.showMessageDialog(this, "Xóa thành công", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
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

        Color normalBg = new Color(0x16, 0xa3, 0x4a);
        Color hoverBg  = new Color(0x15, 0x8a, 0x3d);

        JButton btn = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Hover effect
                if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(normalBg);
                }

                g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    10,
                    10
                );

                super.paintComponent(g);

                g2.dispose();
            }
        };
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);

        btn.setFont(Font.decode("SansSerif-BOLD-13"));

        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);
        btn.setFocusable(false);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(new EmptyBorder(10, 18, 10, 18));

        return btn;
    }

    private JButton makePrimaryButton(String text) {

        JButton btn = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                // màu hover
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0x0f, 0x6f, 0xd4));
                } else {
                    g2.setColor(PRIMARY);
                }

                g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    8,
                    8
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFocusPainted(false);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setForeground(WHITE);

        btn.setFont(new Font("SansSerif", Font.BOLD, 12));

        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    private JButton makeActionButton(String icon, Color normalBg, Color hoverBg) {

        JButton btn = new JButton(icon) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                // background
                if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(normalBg);
                }

                g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    8,
                    8
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(36, 36));

        btn.setFocusPainted(false);
        btn.setFocusable(false);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setForeground(WHITE);

        // Font emoji đẹp hơn
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        btn.setBorder(new EmptyBorder(4, 4, 4, 4));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
