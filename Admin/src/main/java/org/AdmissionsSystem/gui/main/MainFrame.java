package org.AdmissionsSystem.gui.main;

import javax.swing.*;

import org.AdmissionsSystem.gui.common.Searchable;
import org.AdmissionsSystem.gui.modules.Dashboard.DashboardPanel;
import org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi.BangQuyDoiPanel;
import org.AdmissionsSystem.gui.modules.QuanLiDiem.DiemThiSinhPanel;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.DiemCongPanel;
import org.AdmissionsSystem.gui.modules.QuanLiNguyenVong.NguyenVongPanel;
import org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh.NganhToHopPanel;
import org.AdmissionsSystem.gui.modules.QuanLyNguoiDung.UsersPanel;
import org.AdmissionsSystem.gui.modules.QuanLyThiSinh.ThisinhPanel;
import org.AdmissionsSystem.gui.modules.QuanLyToHopMon.ToHopMonPanel;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.NganhHocPanel;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel centerPanel = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("Hệ thống quản lý tuyển sinh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel();
        final HeaderPanel header = new HeaderPanel();

        // create screens
        centerPanel.add(new DashboardPanel(), "dashboard");
        centerPanel.add(new UsersPanel(), "users");
        centerPanel.add(new ThisinhPanel(), "thisinh");
        centerPanel.add(new NganhHocPanel(), "nganh");
        centerPanel.add(new ToHopMonPanel(), "tohop");
        centerPanel.add(new NganhToHopPanel(), "nganh_tohop");
        centerPanel.add(new DiemThisinhPanel(), "diem_thisinh");
        centerPanel.add(new DiemCongPanel(), "diem_cong");
        centerPanel.add(new NguyenVongPanel(), "nguyenvong");
        centerPanel.add(new BangQuyDoiPanel(), "bang_quydoi");

        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // wire sidebar buttons to cards - find all JButtons under the sidebar in order
        java.util.List<JButton> buttons = new java.util.ArrayList<>();
        collectButtons(sidebar, buttons);
        String[] keys = {"dashboard","users","thisinh","nganh","tohop","nganh_tohop","diem_thisinh","diem_cong","nguyenvong","bang_quydoi"};        for (int i = 0; i < buttons.size() && i < keys.length; i++) {
            String card = keys[i];
            JButton btn = buttons.get(i);
            btn.addActionListener(e -> {
                cardLayout.show(centerPanel, card);
                // set per-page placeholder
                switch (card) {
                    case "thisinh":
                        header.setPageSearchPlaceholder("tìm kiếm thí sinh (mã, tên)");
                        break;
                    case "users":
                        header.setPageSearchPlaceholder("tìm kiếm người dùng (tên, email)");
                        break;
                    case "nganh":
                        header.setPageSearchPlaceholder("tìm kiếm ngành / tổ hợp");
                        break;
                    case "tohop":
                        header.setPageSearchPlaceholder("tìm kiếm môn học / tổ hợp môn");
                        break;
                    case "diem_thisinh":
                        header.setPageSearchPlaceholder("tìm kiếm bằng mã thí sinh hoặc tên");
                        break;
                    default:
                        header.setPageSearchPlaceholder("");
                        break;
                }
            });
        }

        // forward per-page search actions to the currently visible page if it
        // implements Searchable
        header.addPageSearchListener(e -> {
            Component current = getCurrentCardComponent();
            if (current instanceof Searchable) {
                ((Searchable) current).onSearch(header.getPageSearchText());
            }
        });

        // show diem_cong panel by default
        cardLayout.show(centerPanel, "diem_cong");
        header.setPageSearchPlaceholder("tìm kiếm điểm cộng");
    }

    private Component getCurrentCardComponent() {
        for (Component c : centerPanel.getComponents()) {
            if (c.isVisible())
                return c;
        }
        return null;
    }

    private void collectButtons(Component c, java.util.List<JButton> out) {
        if (c instanceof JButton)
            out.add((JButton) c);
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents())
                collectButtons(child, out);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
