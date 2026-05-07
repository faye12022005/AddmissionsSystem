package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;
import org.AdmissionsSystem.gui.modules.QuanLyToHopMon.EditToHopDialog;
import org.AdmissionsSystem.gui.modules.QuanLyToHopMon.AddToHopDialog;
import org.AdmissionsSystem.controller.ToHopMonController;
import org.AdmissionsSystem.models.XtTohopMonthi;

public class ToHopMonPanelSwing extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final Color PRIMARY      = new Color(19, 127, 236);
    private static final Color WHITE        = new Color(255, 255, 255);
    private static final Color BG           = new Color(246, 247, 248);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color TEXT_DARK    = new Color(15, 23, 42);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color TEXT_LIGHT   = new Color(148, 163, 184);
    private static final Color SURFACE      = new Color(241, 245, 249);
    private static final Color SUCCESS      = new Color(16, 185, 129);
    private static final Color AMBER        = new Color(245, 158, 11);
    private static final Color ERROR        = new Color(239, 68, 68);

    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> statusCb;
    private JLabel paginationInfo;
    private ToHopMonController controller;

    // ── Pagination Variables ─────────────────────────────────
    private static final int ITEMS_PER_PAGE = 6;
    private int currentPage = 1;
    private int totalPages = 1;
    private java.util.List<JButton> pageButtons = new java.util.ArrayList<>();
    private JPanel paginationPanel;  // Reference to pagination panel for rebuilding

    // ── Data Model ───────────────────────────────────────────
    public static class ToHop {
        private final String ma, ten, mon1, mon2, mon3, status;
        public ToHop(String ma, String ten, String mon1, String mon2, String mon3, String status) {
            this.ma = ma; this.ten = ten;
            this.mon1 = mon1; this.mon2 = mon2; this.mon3 = mon3;
            this.status = status;
        }
        public String getMa()   { return ma; }
        public String getTen()  { return ten; }
        public String getMon1() { return mon1; }
        public String getMon2() { return mon2; }
        public String getMon3() { return mon3; }
        public String getStatus() { return status; }
    }

    private java.util.List<ToHop> data = new java.util.ArrayList<>();
    private java.util.List<ToHop> filteredData = new java.util.ArrayList<>();

    public ToHopMonPanelSwing() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // Initialize controller
        this.controller = new ToHopMonController();

        // Load data from database
        loadDataFromDatabase();

        // Build UI
        JPanel mainPanel = buildMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // Apply filter after UI is built
        refreshFiltered();
    }

    /**
     * Tải dữ liệu từ cơ sở dữ liệu thông qua Controller
     */
    private void loadDataFromDatabase() {
        try {
            data.clear();
            resetPagination();  // Reset pagination when loading new data
            java.util.List<XtTohopMonthi> dbData = controller.taiDuLieu();
            for (XtTohopMonthi tohopMonthi : dbData) {
                data.add(new ToHop(
                    tohopMonthi.getMatohop(),
                    tohopMonthi.getTentohop() != null ? tohopMonthi.getTentohop() : "",
                    tohopMonthi.getMon1(),
                    tohopMonthi.getMon2(),
                    tohopMonthi.getMon3(),
                    "Đang hoạt động" // Status từ DB nếu có
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Deprecated
    private void loadInitialData() {
        data.clear();
        data.add(new ToHop("A00", "Khối A00", "Toán", "Vật lý", "Hóa học", "Đang hoạt động"));
        data.add(new ToHop("A01", "Khối A01", "Toán", "Vật lý", "Tiếng Anh", "Đang hoạt động"));
        data.add(new ToHop("B00", "Khối B00", "Toán", "Hóa học", "Sinh học", "Đang hoạt động"));
        data.add(new ToHop("D01", "Khối D01", "Ngữ văn", "Toán", "Tiếng Anh", "Tạm ngưng"));
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel buildMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        panel.add(buildPageHeader());
        panel.add(Box.createVerticalStrut(24));

        panel.add(buildStatsGrid());
        panel.add(Box.createVerticalStrut(24));

        panel.add(buildListControl());
        panel.add(Box.createVerticalStrut(24));

        panel.add(buildTableCard());
        panel.add(Box.createVerticalStrut(24));

        panel.add(buildFooter());

        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  1. PAGE HEADER
    // ══════════════════════════════════════════════════════════
    private JPanel buildPageHeader() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(BG);

        JLabel title = new JLabel("Tổ hợp môn xét tuyển");
        title.setFont(new Font("System", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);
        titleBlock.add(title);

        titleBlock.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Quản lý và cấu hình danh sách các khối thi cho kỳ tuyển sinh.");
        subtitle.setFont(new Font("System", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        titleBlock.add(subtitle);

        row.add(titleBlock);
        row.add(Box.createHorizontalStrut(20));

        // Button group
        JPanel btnGroup = new JPanel();
        btnGroup.setLayout(new BoxLayout(btnGroup, BoxLayout.X_AXIS));
        btnGroup.setBackground(BG);
        btnGroup.setMaximumSize(new Dimension(500, 80));

        Dimension btnSize = new Dimension(180, 40);

        JButton importBtn = new JButton("📄  Import Excel");
        importBtn.setFont(new Font("System", Font.PLAIN, 13));
        importBtn.setBackground(SUCCESS);
        importBtn.setForeground(WHITE);
        importBtn.setOpaque(true);
        importBtn.setContentAreaFilled(true);
        importBtn.setBorderPainted(false);
        importBtn.setFocusPainted(false);
        importBtn.setMaximumSize(btnSize);;
        importBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        importBtn.addMouseListener(createHoverListener(importBtn, new Color(11, 165, 122), SUCCESS));

        JButton addBtn = new JButton("＋  Thêm tổ hợp");
        addBtn.setFont(new Font("System", Font.BOLD, 13));
        addBtn.setBackground(PRIMARY);
        addBtn.setForeground(WHITE);
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setMaximumSize(btnSize);
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addBtn.addActionListener(e -> handleAddToHop());
        addBtn.addMouseListener(createHoverListener(addBtn, new Color(15, 111, 212), PRIMARY));

        btnGroup.add(importBtn);
        btnGroup.add(Box.createHorizontalStrut(10));
        btnGroup.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnGroup.add(addBtn);

        row.add(btnGroup);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  2. STATS GRID (3 thẻ)
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(1, 3, 16, 0));
        grid.setBackground(BG);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        grid.add(buildStatCard("🔷", new Color(19, 127, 236, 25), PRIMARY, "Tổng số tổ hợp", "42", 1.0, PRIMARY, null));
        grid.add(buildStatCard("✅", new Color(220, 252, 231), SUCCESS, "Đang sử dụng", "38", 0.904, SUCCESS, null));
        grid.add(buildStatCard("🕐", new Color(254, 243, 199), AMBER, "Mới cập nhật", "12", -1, null, "Cập nhật lần cuối 2 giờ trước"));

        return grid;
    }

    private JPanel buildStatCard(String icon, Color iconBg, Color iconColor, String label, String value, double barRatio, Color barColor, String note) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, BORDER));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setPreferredSize(new Dimension(300, 150));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("System", Font.PLAIN, 24));
        iconLabel.setPreferredSize(new Dimension(40, 40));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(12));

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("System", Font.PLAIN, 12));
        labelLbl.setForeground(TEXT_MUTED);
        card.add(labelLbl);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("System", Font.BOLD, 30));
        valueLbl.setForeground(TEXT_DARK);
        card.add(valueLbl);

        if (barRatio > 0 && barColor != null) {
            card.add(Box.createVerticalStrut(14));
            JPanel barPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int width = (int) (getWidth() * barRatio);
                    g.setColor(barColor);
                    g.fillRoundRect(0, 0, width, 5, 3, 3);
                }
            };
            barPanel.setBackground(BORDER);
            barPanel.setPreferredSize(new Dimension(200, 5));
            barPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
            card.add(barPanel);
        } else if (note != null) {
            card.add(Box.createVerticalStrut(10));
            JLabel noteLabel = new JLabel(note);
            noteLabel.setFont(new Font("System", Font.PLAIN, 11));
            noteLabel.setForeground(TEXT_LIGHT);
            card.add(noteLabel);
        }

        card.add(Box.createVerticalGlue());
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  3. LIST CONTROL BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildListControl() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBackground(WHITE);
        bar.setBorder(BorderFactory.createLineBorder(BORDER));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel filterBox = new JPanel();
        filterBox.setLayout(new BoxLayout(filterBox, BoxLayout.X_AXIS));
        filterBox.setBackground(SURFACE);
        filterBox.setBorder(BorderFactory.createLineBorder(BORDER));
        filterBox.setMaximumSize(new Dimension(250, 40));
        filterBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel statusLbl = new JLabel("TRẠNG THÁI:");
        statusLbl.setFont(new Font("System", Font.BOLD, 10));
        statusLbl.setForeground(TEXT_MUTED);
        filterBox.add(statusLbl);
        filterBox.add(Box.createHorizontalStrut(8));

        statusCb = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động", "Tạm ngưng"});
        statusCb.setBackground(WHITE);
        statusCb.setForeground(TEXT_DARK);
        statusCb.setFont(new Font("System", Font.PLAIN, 12));
        statusCb.setBorder(BorderFactory.createEmptyBorder());
        statusCb.addActionListener(e -> applyStatusFilter());
        filterBox.add(statusCb);

        bar.add(filterBox);
        bar.add(Box.createHorizontalGlue());

        JButton filterBtn = buildIconToolBtn("☰");
        JButton sortBtn = buildIconToolBtn("↕");

        bar.add(filterBtn);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(sortBtn);

        return bar;
    }

    private JButton buildIconToolBtn(String icon) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("System", Font.PLAIN, 14));
        btn.setBackground(WHITE);
        btn.setForeground(TEXT_MUTED);
        btn.setPreferredSize(new Dimension(34, 34));
        btn.setMaximumSize(new Dimension(34, 34));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.addMouseListener(createHoverListener(btn, SURFACE, WHITE));
        return btn;
    }

    // ══════════════════════════════════════════════════════════
    //  4. TABLE CARD
    // ══════════════════════════════════════════════════════════
    private JPanel buildTableCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Build table
        String[] columnNames = {"MÃ TỔ HỢP", "TÊN TỔ HỢP", "MÔN 1", "MÔN 2", "MÔN 3"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("System", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("System", Font.BOLD, 11));
        header.setReorderingAllowed(false);

        // Add table rows
        refreshTableModel();

        // Add double-click listener to open edit dialog and right-click to delete
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int visualRow = table.rowAtPoint(e.getPoint());
                if (visualRow < 0) return;
                
                // Calculate actual index in filteredData based on current page and visual row
                int actualIndex = (currentPage - 1) * ITEMS_PER_PAGE + visualRow;
                if (actualIndex < 0 || actualIndex >= filteredData.size()) return;
                
                ToHop selected = filteredData.get(actualIndex);
                
                if (e.getClickCount() == 2) {
                    // Double-click to edit
                    handleEditToHop(selected);
                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    // Right-click to delete
                    table.setRowSelectionInterval(visualRow, visualRow);
                    handleDeleteToHop(selected);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.setPreferredSize(new Dimension(900, 270));
        scrollTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));

        card.add(scrollTable);
        card.add(new JSeparator());
        card.add(buildPagination());

        return card;
    }

    private void refreshTableModel() {
        tableModel.setRowCount(0);
        
        // Calculate total pages
        totalPages = (int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        // Validate current page
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        
        // Calculate start and end index for current page
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredData.size());
        
        // Add rows for current page only
        for (int i = startIndex; i < endIndex; i++) {
            ToHop item = filteredData.get(i);
            Object[] row = {item.getMa(), item.getTen(), item.getMon1(), item.getMon2(), item.getMon3()};
            tableModel.addRow(row);
        }
        
        updatePaginationInfo();
    }

    // ── Pagination ──────────────────────────────────────────
    private JPanel buildPagination() {
        paginationPanel = new JPanel();
        paginationPanel.setLayout(new BoxLayout(paginationPanel, BoxLayout.X_AXIS));
        paginationPanel.setBackground(new Color(248, 250, 252));
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        paginationInfo = new JLabel();
        paginationInfo.setFont(new Font("System", Font.PLAIN, 11));
        paginationInfo.setForeground(TEXT_MUTED);
        paginationPanel.add(paginationInfo);
        paginationPanel.add(Box.createHorizontalGlue());

        rebuildPaginationButtons();

        updatePaginationInfo();
        return paginationPanel;
    }

    /**
     * Rebuild pagination buttons based on current page and total pages
     */
    private void rebuildPaginationButtons() {
        if (paginationPanel == null) return;
        
        // Remove all existing buttons from pagination panel
        java.awt.Component[] components = paginationPanel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            java.awt.Component c = components[i];
            if (c instanceof JButton || (c instanceof Box && i > 1)) {
                paginationPanel.remove(i);
            }
        }
        pageButtons.clear();

        // Add "Trước" (Previous) button
        JButton prevBtn = new JButton("‹");
        prevBtn.setFont(new Font("System", Font.PLAIN, 12));
        prevBtn.setBackground(WHITE);
        prevBtn.setForeground(TEXT_MUTED);
        prevBtn.setPreferredSize(new Dimension(32, 32));
        prevBtn.setMaximumSize(new Dimension(32, 32));
        prevBtn.setBorder(BorderFactory.createEmptyBorder());
        prevBtn.setFocusPainted(false);
        prevBtn.setEnabled(currentPage > 1);
        prevBtn.addActionListener(e -> goToPreviousPage());
        if (currentPage > 1) {
            prevBtn.addMouseListener(createHoverListener(prevBtn, SURFACE, WHITE));
        }
        paginationPanel.add(prevBtn);
        paginationPanel.add(Box.createHorizontalStrut(2));

        // Calculate page range to display (max 5 page buttons)
        int maxDisplayPages = 5;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + maxDisplayPages - 1);
        if (endPage - startPage + 1 < maxDisplayPages) {
            startPage = Math.max(1, endPage - maxDisplayPages + 1);
        }

        // Add "..." if there are pages before the range
        if (startPage > 1) {
            JButton ellipsis = new JButton("...");
            ellipsis.setFont(new Font("System", Font.PLAIN, 12));
            ellipsis.setBackground(WHITE);
            ellipsis.setForeground(TEXT_MUTED);
            ellipsis.setPreferredSize(new Dimension(32, 32));
            ellipsis.setMaximumSize(new Dimension(32, 32));
            ellipsis.setBorder(BorderFactory.createEmptyBorder());
            ellipsis.setFocusPainted(false);
            ellipsis.setEnabled(false);
            paginationPanel.add(ellipsis);
            paginationPanel.add(Box.createHorizontalStrut(2));
        }

        // Add page number buttons
        for (int i = startPage; i <= endPage; i++) {
            final int pageNum = i;
            boolean active = (i == currentPage);
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("System", active ? Font.BOLD : Font.PLAIN, 12));
            btn.setBackground(active ? PRIMARY : WHITE);
            btn.setForeground(active ? WHITE : TEXT_MUTED);
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setMaximumSize(new Dimension(32, 32));
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);
            btn.addActionListener(e -> goToPage(pageNum));
            if (!active) {
                btn.addMouseListener(createHoverListener(btn, SURFACE, WHITE));
            }
            paginationPanel.add(btn);
            paginationPanel.add(Box.createHorizontalStrut(2));
            pageButtons.add(btn);
        }

        // Add "..." if there are pages after the range
        if (endPage < totalPages) {
            JButton ellipsis = new JButton("...");
            ellipsis.setFont(new Font("System", Font.PLAIN, 12));
            ellipsis.setBackground(WHITE);
            ellipsis.setForeground(TEXT_MUTED);
            ellipsis.setPreferredSize(new Dimension(32, 32));
            ellipsis.setMaximumSize(new Dimension(32, 32));
            ellipsis.setBorder(BorderFactory.createEmptyBorder());
            ellipsis.setFocusPainted(false);
            ellipsis.setEnabled(false);
            paginationPanel.add(ellipsis);
            paginationPanel.add(Box.createHorizontalStrut(2));
        }

        // Add "Sau" (Next) button
        JButton nextBtn = new JButton("›");
        nextBtn.setFont(new Font("System", Font.PLAIN, 12));
        nextBtn.setBackground(WHITE);
        nextBtn.setForeground(TEXT_MUTED);
        nextBtn.setPreferredSize(new Dimension(32, 32));
        nextBtn.setMaximumSize(new Dimension(32, 32));
        nextBtn.setBorder(BorderFactory.createEmptyBorder());
        nextBtn.setFocusPainted(false);
        nextBtn.setEnabled(currentPage < totalPages);
        nextBtn.addActionListener(e -> goToNextPage());
        if (currentPage < totalPages) {
            nextBtn.addMouseListener(createHoverListener(nextBtn, SURFACE, WHITE));
        }
        paginationPanel.add(nextBtn);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // ══════════════════════════════════════════════════════════
    //  5. FOOTER
    // ══════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel text = new JLabel("© 2024 Hệ thống Quản lý Tuyển sinh. Phát triển bởi Đội ngũ Công nghệ thông tin.");
        text.setFont(new Font("System", Font.PLAIN, 10));
        text.setForeground(TEXT_LIGHT);
        footer.add(Box.createHorizontalGlue());
        footer.add(text);
        footer.add(Box.createHorizontalGlue());

        return footer;
    }

    // ══════════════════════════════════════════════════════════
    //  HANDLERS
    // ══════════════════════════════════════════════════════════
    /**
     * Xử lý sự kiện thêm tổ hợp môn mới
     */
    private void handleAddToHop() {
        AddToHopDialog dialog = new AddToHopDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                // Kiểm tra dữ liệu nhập vào
                String code = dialog.getCode();
                String name = dialog.getNameValue();
                String subj1 = dialog.getSubj1();
                String subj2 = dialog.getSubj2();
                String subj3 = dialog.getSubj3();
                
                if (code == null || code.trim().isEmpty()) {
                    throw new IllegalArgumentException("Mã tổ hợp không được để trống!");
                }
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên tổ hợp không được để trống!");
                }
                if (subj1 == null || subj1.trim().isEmpty() ||
                    subj2 == null || subj2.trim().isEmpty() ||
                    subj3 == null || subj3.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tất cả các môn học không được để trống!");
                }
                
                // Tạo model từ dữ liệu dialog
                XtTohopMonthi newTohop = new XtTohopMonthi();
                newTohop.setMatohop(code);
                newTohop.setTentohop(name);
                newTohop.setMon1(subj1);
                newTohop.setMon2(subj2);
                newTohop.setMon3(subj3);

                // Gọi Controller để xử lý thêm
                controller.xuLySuKienThem(newTohop);

                // Reload dữ liệu từ database
                loadDataFromDatabase();
                applyStatusFilter();

                JOptionPane.showMessageDialog(this, 
                    "Thêm tổ hợp môn thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi dữ liệu: " + e.getMessage(), 
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi thêm tổ hợp môn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện chỉnh sửa tổ hợp môn
     * @param selected tổ hợp môn được chọn
     */
    private void handleEditToHop(ToHop selected) {
        EditToHopDialog dialog = new EditToHopDialog(
            (Frame) SwingUtilities.getWindowAncestor(table),
            selected.getMa(),
            selected.getTen(),
            selected.getMon1(),
            selected.getMon2(),
            selected.getMon3(),
            selected.getStatus()
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                // Kiểm tra dữ liệu nhập vào
                String name = dialog.getNameValue();
                String subj1 = dialog.getSubj1();
                String subj2 = dialog.getSubj2();
                String subj3 = dialog.getSubj3();
                
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên tổ hợp không được để trống!");
                }
                if (subj1 == null || subj1.trim().isEmpty() ||
                    subj2 == null || subj2.trim().isEmpty() ||
                    subj3 == null || subj3.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tất cả các môn học không được để trống!");
                }
                
                // Tạo object cập nhật
                XtTohopMonthi updatedTohop = new XtTohopMonthi();
                updatedTohop.setMatohop(dialog.getCode());
                updatedTohop.setTentohop(name);
                updatedTohop.setMon1(subj1);
                updatedTohop.setMon2(subj2);
                updatedTohop.setMon3(subj3);
                
                // Gọi controller để cập nhật
                controller.xuLySuKienCapNhat(selected.getMa(), updatedTohop);
                
                // Reload dữ liệu
                loadDataFromDatabase();
                applyStatusFilter();
                
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Cập nhật tổ hợp môn thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Lỗi dữ liệu: " + e.getMessage(),
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Lỗi khi cập nhật: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện xóa tổ hợp môn
     * @param selected tổ hợp môn được chọn
     */
    private void handleDeleteToHop(ToHop selected) {
        int option = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(table),
            "Bạn có chắc muốn xóa tổ hợp: " + selected.getMa() + " - " + selected.getTen() + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                // Gọi controller để xóa
                controller.xuLySuKienXoa(selected.getMa());
                
                // Reload dữ liệu
                loadDataFromDatabase();
                applyStatusFilter();
                
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Xóa tổ hợp môn thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Lỗi dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(table),
                    "Lỗi khi xóa: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyStatusFilter() {
        String status = (String) statusCb.getSelectedItem();
        filteredData.clear();
        for (ToHop item : data) {
            if ("Tất cả trạng thái".equals(status) || item.getStatus().equals(status)) {
                filteredData.add(item);
            }
        }
        currentPage = 1;  // Reset to first page when filter changes
        totalPages = (int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        refreshTableModel();
        rebuildPaginationButtons();
    }

    private void refreshFiltered() {
        applyStatusFilter();
    }

    private void updatePaginationInfo() {
        if (paginationInfo == null) return;
        
        int visible = filteredData.size();
        int total = data.size();
        
        if (visible == 0) {
            paginationInfo.setText("Không có dữ liệu phù hợp");
        } else {
            int startIdx = (currentPage - 1) * ITEMS_PER_PAGE + 1;
            int endIdx = Math.min(currentPage * ITEMS_PER_PAGE, visible);
            paginationInfo.setText(String.format("Hiển thị %d-%d trong số %d tổ hợp | Trang %d/%d", 
                startIdx, endIdx, visible, currentPage, totalPages));
        }
    }

    /**
     * Chuyển đến trang trước đó
     */
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTableModel();
            rebuildPaginationButtons();
        }
    }

    /**
     * Chuyển đến trang tiếp theo
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            refreshTableModel();
            rebuildPaginationButtons();
        }
    }

    /**
     * Chuyển đến trang cụ thể
     * @param pageNum số trang
     */
    private void goToPage(int pageNum) {
        if (pageNum >= 1 && pageNum <= totalPages) {
            currentPage = pageNum;
            refreshTableModel();
            rebuildPaginationButtons();
        }
    }

    /**
     * Reset pagination về trang đầu tiên
     */
    private void resetPagination() {
        currentPage = 1;
        totalPages = 1;
    }

    private java.awt.event.MouseListener createHoverListener(JButton btn, Color enterColor, Color exitColor) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(enterColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(exitColor);
            }
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Tổ hợp môn xét tuyển");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(980, 900);
            frame.setLocationRelativeTo(null);
            frame.add(new ToHopMonPanelSwing());
            frame.setVisible(true);
        });
    }
}
