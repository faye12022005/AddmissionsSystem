package org.AdmissionsSystem.gui.modules.QuanLyThiSinh;

import org.AdmissionsSystem.bus.service.ThiSinhService;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemService;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemVSATService;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ThisinhDetailDialog extends JDialog {

        private ThisinhDetailDialog(Window owner, String cccd) {

                super(owner, "Thông tin chi tiết", ModalityType.APPLICATION_MODAL);

                setLayout(new BorderLayout(10, 10));
                setPreferredSize(new Dimension(950, 600));

                // =========================================================
                // HEADER
                // =========================================================

                JPanel header = new JPanel(new BorderLayout());
                header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

                JLabel lblTitle = new JLabel("Thông tin chi tiết thí sinh");
                lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

                JLabel lblCccd = new JLabel("CCCD: " + cccd);
                lblCccd.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                header.add(lblTitle, BorderLayout.NORTH);
                header.add(lblCccd, BorderLayout.SOUTH);

                add(header, BorderLayout.NORTH);

                // =========================================================
                // TABS
                // =========================================================

                JTabbedPane tabs = new JTabbedPane();

                // =========================================================
                // TAB THÔNG TIN THÍ SINH
                // =========================================================

                String[] infoCols = {
                                "ID",
                                "CCCD",
                                "SBD",
                                "Họ",
                                "Tên",
                                "Ngày sinh",
                                "Giới tính",
                                "Điện thoại",
                                "Email",
                                "Nơi sinh",
                                "Đối tượng",
                                "Khu vực"
                };

                DefaultTableModel infoModel = new DefaultTableModel(infoCols, 0) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };

                JTable infoTable = new JTable(infoModel);

                infoTable.setRowHeight(30);

                infoTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                infoTable.getTableHeader().setFont(
                                new Font("Segoe UI", Font.BOLD, 14));

                infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                try {

                        ThiSinhService thiSinhService = new ThiSinhService();

                        XtThisinhxettuyen25 ts = thiSinhService.findByCccd(cccd);

                        if (ts != null) {

                                infoModel.addRow(new Object[] {

                                                ts.getIdthisinh(),
                                                safeText(ts.getCccd()),
                                                safeText(ts.getSobaodanh()),
                                                safeText(ts.getHo()),
                                                safeText(ts.getTen()),
                                                safeText(ts.getNgaySinh()),
                                                safeText(ts.getGioiTinh()),
                                                safeText(ts.getDienThoai()),
                                                safeText(ts.getEmail()),
                                                safeText(ts.getNoiSinh()),
                                                safeText(ts.getDoiTuong()),
                                                safeText(ts.getKhuVuc())
                                });

                        } else {

                                infoModel.addRow(new Object[] {
                                                "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-"
                                });
                        }

                } catch (Exception ex) {

                        infoModel.addRow(new Object[] {
                                        "Lỗi",
                                        ex.getMessage(),
                                        "-", "-", "-", "-", "-", "-", "-", "-", "-", "-"
                        });
                }

                // width columns
                infoTable.getColumnModel().getColumn(0).setPreferredWidth(60);
                infoTable.getColumnModel().getColumn(1).setPreferredWidth(130);
                infoTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                infoTable.getColumnModel().getColumn(3).setPreferredWidth(120);
                infoTable.getColumnModel().getColumn(4).setPreferredWidth(100);
                infoTable.getColumnModel().getColumn(5).setPreferredWidth(100);
                infoTable.getColumnModel().getColumn(6).setPreferredWidth(90);
                infoTable.getColumnModel().getColumn(7).setPreferredWidth(120);
                infoTable.getColumnModel().getColumn(8).setPreferredWidth(180);
                infoTable.getColumnModel().getColumn(9).setPreferredWidth(150);
                infoTable.getColumnModel().getColumn(10).setPreferredWidth(100);
                infoTable.getColumnModel().getColumn(11).setPreferredWidth(100);

                tabs.addTab(
                                "Thông tin thí sinh",
                                new JScrollPane(infoTable));

                // =========================================================
                // TAB ĐIỂM XÉT TUYỂN
                // =========================================================

                String[] diemCols = {

                                "Toán",
                                "Lý",
                                "Hóa",
                                "Sinh",
                                "Sử",
                                "Địa",
                                "Văn",
                                "GDCD",
                                "NN Thi",
                                "NN QĐ",
                                "Tin",
                                "KTPL",
                                "CNCN",
                                "CNNN",
                                "ĐGNL",
                                "NK1",
                                "NK2",
                                "NK3",
                                "NK4",
                                "NK5",
                                "NK6"
                };

                DefaultTableModel diemModel = new DefaultTableModel(diemCols, 0) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };

                JTable diemTable = new JTable(diemModel);

                diemTable.setRowHeight(28);

                diemTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                diemTable.getTableHeader()
                                .setFont(new Font("Segoe UI", Font.BOLD, 13));

                diemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                try {

                        QuanLiDiemService diemService = new QuanLiDiemService();

                        List<QuanLiDiemService.DiemRecord> allRows = diemService.query("",
                                        QuanLiDiemService.ALL_OPTION);

                        List<QuanLiDiemService.DiemRecord> rows = allRows.stream()
                                        .filter(r -> r.cccd() != null
                                                        && r.cccd().trim().equalsIgnoreCase(cccd.trim()))
                                        .toList();

                        JScrollPane diemScroll;

                        if (rows != null && !rows.isEmpty()) {

                                for (QuanLiDiemService.DiemRecord r : rows) {

                                        diemModel.addRow(new Object[] {

                                                        safeNum(r.to()),
                                                        safeNum(r.li()),
                                                        safeNum(r.ho()),
                                                        safeNum(r.si()),
                                                        safeNum(r.su()),
                                                        safeNum(r.di()),
                                                        safeNum(r.va()),
                                                        safeNum(r.gdcd()),
                                                        safeNum(r.n1Thi()),
                                                        safeNum(r.n1Cc()),
                                                        safeNum(r.ti()),
                                                        safeNum(r.ktpl()),
                                                        safeNum(r.cncn()),
                                                        safeNum(r.cnnn()),
                                                        safeNum(r.nl1()),
                                                        safeNum(r.nk1()),
                                                        safeNum(r.nk2()),
                                                        safeNum(r.nk3()),
                                                        safeNum(r.nk4()),
                                                        safeNum(r.nk5()),
                                                        safeNum(r.nk6())
                                        });
                                }

                                diemScroll = new JScrollPane(diemTable);

                        } else {

                                JLabel emptyLabel = new JLabel("Không có dữ liệu điểm xét tuyển");

                                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

                                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

                                diemScroll = new JScrollPane(emptyLabel);
                        }

                        tabs.addTab(
                                        "Điểm xét tuyển",
                                        diemScroll);

                } catch (Exception ex) {

                        JLabel errorLabel = new JLabel("Lỗi tải dữ liệu điểm: " + ex.getMessage());

                        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

                        tabs.addTab(
                                        "Điểm xét tuyển",
                                        new JScrollPane(errorLabel));
                }

                // =========================================================
                // TAB VSAT
                // =========================================================

                String[] vsatCols = {
                                "Đợt thi",
                                "Toán",
                                "Văn",
                                "Anh",
                                "Lý",
                                "Hóa",
                                "Sinh",
                                "Sử",
                                "Địa"
                };

                DefaultTableModel vsatModel = new DefaultTableModel(vsatCols, 0) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };

                JTable vsatTable = new JTable(vsatModel);

                vsatTable.setRowHeight(28);

                vsatTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                vsatTable.getTableHeader()
                                .setFont(new Font("Segoe UI", Font.BOLD, 13));

                try {

                        QuanLiDiemVSATService vsatService = new QuanLiDiemVSATService();

                        List<QuanLiDiemVSATService.VsatRecord> allVsat = vsatService.query("");

                        List<QuanLiDiemVSATService.VsatRecord> vsatRows = allVsat.stream()
                                        .filter(r -> r.cccd() != null
                                                        && r.cccd().trim().equalsIgnoreCase(cccd.trim()))
                                        .toList();

                        JScrollPane vsatScroll;

                        if (vsatRows != null && !vsatRows.isEmpty()) {

                                for (QuanLiDiemVSATService.VsatRecord r : vsatRows) {

                                        vsatModel.addRow(new Object[] {

                                                        safeText(r.dotThi()),
                                                        safeNum(r.toan()),
                                                        safeNum(r.van()),
                                                        safeNum(r.anh()),
                                                        safeNum(r.ly()),
                                                        safeNum(r.hoa()),
                                                        safeNum(r.sinh()),
                                                        safeNum(r.su()),
                                                        safeNum(r.dia())
                                        });
                                }

                                vsatScroll = new JScrollPane(vsatTable);

                        } else {

                                JLabel emptyLabel = new JLabel("Không có dữ liệu điểm VSAT");

                                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

                                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

                                vsatScroll = new JScrollPane(emptyLabel);
                        }

                        tabs.addTab(
                                        "Điểm VSAT",
                                        vsatScroll);

                } catch (Exception ex) {

                        JLabel errorLabel = new JLabel("Lỗi tải dữ liệu VSAT: " + ex.getMessage());

                        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

                        tabs.addTab(
                                        "Điểm VSAT",
                                        new JScrollPane(errorLabel));
                }

                add(tabs, BorderLayout.CENTER);

                // =========================================================
                // FOOTER
                // =========================================================

                JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                JButton btnClose = new JButton("Đóng");

                btnClose.setFocusPainted(false);

                btnClose.addActionListener(e -> dispose());

                footer.add(btnClose);

                add(footer, BorderLayout.SOUTH);

                pack();

                setLocationRelativeTo(owner);
        }

        // =========================================================
        // HELPER
        // =========================================================

        private static String safeNum(BigDecimal num) {

                return num == null
                                ? "-"
                                : num.stripTrailingZeros().toPlainString();
        }

        private static String safeText(Object obj) {

                return obj == null
                                ? "-"
                                : obj.toString();
        }

        // =========================================================
        // SHOW DIALOG
        // =========================================================

        public static void showDialog(Window owner, String cccd) {

                ThisinhDetailDialog dialog = new ThisinhDetailDialog(owner, cccd);

                dialog.setVisible(true);
        }
}