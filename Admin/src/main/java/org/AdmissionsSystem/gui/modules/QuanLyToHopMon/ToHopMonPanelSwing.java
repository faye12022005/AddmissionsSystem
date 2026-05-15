package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import org.AdmissionsSystem.bus.controller.ToHopMonController;
import org.AdmissionsSystem.bus.service.ToHopMonService;
import org.AdmissionsSystem.gui.modules.QuanLiDiem.ImportPreviewDialog;
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

    // ── Font ─────────────────────────────────────────────────
    private static final Font FONT_BOLD_13  = new Font("System", Font.BOLD, 13);
    private static final Font FONT_BOLD_11  = new Font("System", Font.BOLD, 11);
    private static final Font FONT_PLAIN_13 = new Font("System", Font.PLAIN, 13);
    private static final Font FONT_PLAIN_12 = new Font("System", Font.PLAIN, 12);
    private static final Font FONT_PLAIN_11 = new Font("System", Font.PLAIN, 11);

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane tableScroll;
    private JLabel paginationInfo;
    private JPanel paginationBtnGroup;
    private ToHopMonController controller;

    // ── Pagination Variables ─────────────────────────────────
    private static final int ITEMS_PER_PAGE = 9;
    private int currentPage = 1;
    private int totalPages = 1;

    // ── Data Model ───────────────────────────────────────────
    public static class ToHop {
        private final Integer id;
        private final String ma, ten, mon1, mon2, mon3, status;
        public ToHop(Integer id, String ma, String ten, String mon1, String mon2, String mon3, String status) {
            this.id = id;
            this.ma = ma; this.ten = ten;
            this.mon1 = mon1; this.mon2 = mon2; this.mon3 = mon3;
            this.status = status;
        }
        public Integer getId()  { return id; }
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
                    tohopMonthi.getIdtohop(),
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
        importBtn.addActionListener(e -> handleImportExcel());
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
    //  4. TABLE CARD
    // ══════════════════════════════════════════════════════════
    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(12, WHITE);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(12, BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setLayout(new BorderLayout());

        // Build table
        String[] columnNames = {"MÃ TỔ HỢP", "TÊN TỔ HỢP", "MÔN 1", "MÔN 2", "MÔN 3"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(52);
        table.setFont(FONT_PLAIN_13);
        table.setForeground(TEXT_DARK);
        table.setGridColor(new Color(0xf1, 0xf5, 0xf9));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(0xef, 0xf6, 0xff));
        table.setSelectionForeground(TEXT_DARK);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0xf8, 0xfa, 0xfc));
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_BOLD_11);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));

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

        tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(WHITE);
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        updateTableHeight(table, tableScroll);

        card.add(tableScroll, BorderLayout.CENTER);
        card.add(buildPagination(), BorderLayout.SOUTH);

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
        
        updateTableHeight(table, tableScroll);
    }

    private JPanel buildPagination() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(new Color(0xf8, 0xfa, 0xfc));
        row.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(4, 8, 4, 8)  // giảm padding
        ));

        paginationInfo = new JLabel(getPaginationText());
        paginationInfo.setFont(FONT_PLAIN_11);
        paginationInfo.setForeground(TEXT_MUTED);

        paginationBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        paginationBtnGroup.setOpaque(false);
        updatePaginationButtons();

        row.add(paginationInfo);
        row.add(Box.createHorizontalGlue()); // đẩy buttons sang phải, không tạo khoảng trống thừa
        row.add(paginationBtnGroup);

        return row;
    }

    private void updatePaginationButtons() {
        if (paginationBtnGroup == null) return;
        paginationBtnGroup.removeAll();

        int total = filteredData.size();
        totalPages = (total + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        if (totalPages == 0) totalPages = 1;

        JButton prevBtn = new JButton("‹") {
            @Override
            protected void paintComponent(Graphics g) {
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
        prevBtn.setForeground(TEXT_MUTED);
        prevBtn.setBackground(new Color(0, 0, 0, 0));
        prevBtn.setContentAreaFilled(false);
        prevBtn.setBorderPainted(false);
        prevBtn.setFocusPainted(false);
        prevBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        prevBtn.setEnabled(currentPage > 1);
        prevBtn.addActionListener(e -> goToPage(currentPage - 1));
        paginationBtnGroup.add(prevBtn);

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

        JButton nextBtn = new JButton("›") {
            @Override
            protected void paintComponent(Graphics g) {
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
        nextBtn.setForeground(TEXT_MUTED);
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
            @Override
            protected void paintComponent(Graphics g) {
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
        btn.setFont(active ? FONT_BOLD_11 : FONT_PLAIN_12);
        btn.setForeground(active ? WHITE : TEXT_MUTED);
        btn.setBackground(active ? PRIMARY : new Color(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        int page = pageNum;
        btn.addActionListener(e -> goToPage(page));
        if (!active) {
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(0xe2, 0xe8, 0xf0));
                    btn.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(0, 0, 0, 0));
                    btn.repaint();
                }
            });
        }
        paginationBtnGroup.add(btn);
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
                refreshFiltered();

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

    private void handleImportExcel() {
        try {
            ToHopMonService.ImportPreview preview = controller.previewImport(this);
            if (preview == null || preview.totalCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Không có dữ liệu nào được import.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            java.util.List<Object[]> previewRows = new java.util.ArrayList<>();
            for (ToHopMonService.ToHopInput input : preview.validRows()) {
                previewRows.add(toPreviewRow(input));
            }

            java.util.List<Object[]> errorRows = buildErrorRows(preview.errors());
            String summary = buildSummary(preview.totalCount(), preview.validCount(), preview.errorCount());

            boolean confirmed = ImportPreviewDialog.showDialog(
                this,
                "Xem trước dữ liệu import",
                new String[] {"Mã tổ hợp", "Tên tổ hợp", "Môn 1", "Môn 2", "Môn 3"},
                previewRows,
                summary,
                "Dòng lỗi import",
                new String[] {"Dòng", "Mã tổ hợp", "Lỗi"},
                errorRows);

            if (!confirmed) {
                return;
            }

            int importedRows = controller.commitImport(preview);
            if (importedRows == 0) {
                JOptionPane.showMessageDialog(this,
                    "Không có bản ghi hợp lệ để import.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            loadDataFromDatabase();
            refreshFiltered();

            String message = "Import thành công " + importedRows + " bản ghi.";
            if (preview.errorCount() > 0) {
                message += " Bỏ qua " + preview.errorCount() + " dòng lỗi.";
            }
            JOptionPane.showMessageDialog(this,
                message,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể đọc file import: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
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

        if (dialog.isDeleted()) {
            try {
                controller.xuLySuKienXoaTheoId(selected.getId());

                loadDataFromDatabase();
                refreshFiltered();

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
            return;
        }

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
                updatedTohop.setIdtohop(selected.getId());
                updatedTohop.setMatohop(dialog.getCode());
                updatedTohop.setTentohop(name);
                updatedTohop.setMon1(subj1);
                updatedTohop.setMon2(subj2);
                updatedTohop.setMon3(subj3);
                
                // Gọi controller để cập nhật
                controller.xuLySuKienCapNhat(updatedTohop);
                
                // Reload dữ liệu
                loadDataFromDatabase();
                refreshFiltered();
                
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
                controller.xuLySuKienXoaTheoId(selected.getId());
                
                // Reload dữ liệu
                loadDataFromDatabase();
                refreshFiltered();
                
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

    private Object[] toPreviewRow(ToHopMonService.ToHopInput row) {
        return new Object[] {
            row.maToHop(),
            row.tenToHop(),
            row.mon1(),
            row.mon2(),
            row.mon3()
        };
    }

    private java.util.List<Object[]> buildErrorRows(java.util.List<ToHopMonService.ImportError> errors) {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        if (errors == null) {
            return rows;
        }

        for (ToHopMonService.ImportError error : errors) {
            rows.add(new Object[] { error.rowNumber(), error.maToHop(), error.message() });
        }
        return rows;
    }

    private String buildSummary(int total, int valid, int error) {
        return "Tổng: " + total + " | Hợp lệ: " + valid + " | Lỗi: " + error;
    }

    private void refreshFiltered() {
        filteredData.clear();
        filteredData.addAll(data);
        currentPage = 1;
        totalPages = (int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        refreshTableModel();
        updatePaginationButtons();
        updatePaginationInfo();
    }

    private void updatePaginationInfo() {
        if (paginationInfo == null) return;
        paginationInfo.setText(getPaginationText());
    }

    /**
     * Chuyển đến trang trước đó
     */
    private void goToPreviousPage() {
        goToPage(currentPage - 1);
    }

    /**
     * Chuyển đến trang tiếp theo
     */
    private void goToNextPage() {
        goToPage(currentPage + 1);
    }

    /**
     * Chuyển đến trang cụ thể
     * @param pageNum số trang
     */
    private void goToPage(int pageNum) {
        if (pageNum < 1 || pageNum > totalPages) return;

        currentPage = pageNum;
        refreshTableModel();
        updatePaginationButtons();
        updatePaginationInfo();
    }

    /**
     * Reset pagination về trang đầu tiên
     */
    private void resetPagination() {
        currentPage = 1;
        totalPages = 1;
    }

    private void updateTableHeight(JTable table, JScrollPane scroll) {
        if (table == null || scroll == null) return;

        int rowCount = table.getRowCount();
        int rowHeight = table.getRowHeight();
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int height = rowCount * rowHeight + headerHeight;

        scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, height));
        scroll.setMinimumSize(new Dimension(0, height));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        scroll.revalidate();
    }

    private String getPaginationText() {
        int total = filteredData.size();
        if (total == 0) {
            return "Hiển thị 0-0 trong số 0 bản ghi";
        }
        int startRecord = (currentPage - 1) * ITEMS_PER_PAGE + 1;
        int endRecord = Math.min(currentPage * ITEMS_PER_PAGE, total);
        return "Hiển thị " + startRecord + "-" + endRecord + " trong số " + total + " bản ghi";
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

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
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

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
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
