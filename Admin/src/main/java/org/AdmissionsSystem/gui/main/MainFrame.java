package org.AdmissionsSystem.gui.main;

import javax.swing.*;

import org.AdmissionsSystem.config.AppConfig;
import org.AdmissionsSystem.gui.modules.Login.LoginFrame;
import org.AdmissionsSystem.gui.modules.Dashboard.DashboardPanel;
import org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi.BangQuyDoiPanel;
import org.AdmissionsSystem.gui.modules.QuanLiDiem.DiemThiSinhPanel;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.DiemCongPanel;
import org.AdmissionsSystem.gui.modules.QuanLiNguyenVong.NguyenVongPanel;
import org.AdmissionsSystem.gui.modules.QuanLyDanhSachNganh.NganhToHopPanel;
import org.AdmissionsSystem.gui.modules.QuanLyNguoiDung.UsersPanel;
import org.AdmissionsSystem.gui.modules.QuanLyThiSinh.ThisinhPanel;
import org.AdmissionsSystem.gui.modules.QuanLyToHopMon.ToHopMonPanelSwing;
import org.AdmissionsSystem.gui.modules.QuanlyNganh.NganhHocPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerPanel = new JPanel(cardLayout);
    private final LoginFrame loginFrame;
    private final HeaderPanel header;
    private final Map<String, Supplier<JPanel>> panelFactories = new LinkedHashMap<>();
    private final Map<String, JPanel> loadedPanels = new HashMap<>();

    public MainFrame() {
        this(null, "Lê Minh Anh", "Admin");
    }

    public MainFrame(LoginFrame loginFrame, String displayName, String role) {
        this.loginFrame = loginFrame;
        this.header = new HeaderPanel();
        setTitle("Hệ thống quản lý tuyển sinh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        centerPanel.setBackground(new Color(248, 250, 252));
        registerPanelFactories();

        SidebarPanel sidebar = new SidebarPanel(displayName, role, this::logoutToLogin);

        // right content area: just center content
        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        // Wire sidebar buttons to card panels using client property
        // SidebarPanel stores the original menu index on each button
        String[] allKeys = { "dashboard", "users", "thisinh", "nganh", "tohop", "nganh_tohop", "diem_thisinh",
                "diem_cong", "nguyenvong", "bang_quydoi" };
        java.util.List<JButton> buttons = new java.util.ArrayList<>();
        collectButtons(sidebar, buttons);

        // Assign card keys: sidebar may skip some items (e.g. "users" for non-admin)
        // We use the button text to determine which card to show
        String[] menuLabels = { "Tổng quan", "Người dùng", "Thí sinh", "Ngành học", "Tổ hợp", "Ngành-Tổ hợp",
                "Điểm thi", "Điểm cộng", "Nguyện vọng", "Bảng quy đổi" };
        for (JButton btn : buttons) {
            String text = btn.getText();
            String card = null;
            // Duyệt ngược từ cuối danh sách để ưu tiên các nhãn dài/chi tiết hơn (vd:
            // Ngành-Tổ hợp trước Tổ hợp)
            for (int j = menuLabels.length - 1; j >= 0; j--) {
                if (text.contains(menuLabels[j])) {
                    card = allKeys[j];
                    break;
                }
            }
            if (card == null)
                continue;
            final String finalCard = card;
            btn.addActionListener(e -> {
                showCard(finalCard);
            });
        }

        // chỉ tải dashboard lúc đầu, các màn còn lại tải khi user mở tab
        showCard("dashboard");
    }

    public void showCard(String card) {
        ensurePanelLoaded(card);
        cardLayout.show(centerPanel, card);
        updateSearchPlaceholder(card);
    }

    private void registerPanelFactories() {
        panelFactories.put("dashboard", DashboardPanel::new);
        panelFactories.put("users", UsersPanel::new);
        panelFactories.put("thisinh", ThisinhPanel::new);
        panelFactories.put("nganh", NganhHocPanel::new);
        panelFactories.put("tohop", ToHopMonPanelSwing::new);
        panelFactories.put("nganh_tohop", NganhToHopPanel::new);
        panelFactories.put("diem_thisinh", DiemThiSinhPanel::new);
        panelFactories.put("diem_cong", DiemCongPanel::new);
        panelFactories.put("nguyenvong", NguyenVongPanel::new);
        panelFactories.put("bang_quydoi", BangQuyDoiPanel::new);
    }

    private void ensurePanelLoaded(String card) {
        if (card == null || loadedPanels.containsKey(card)) {
            return;
        }

        Supplier<JPanel> factory = panelFactories.get(card);
        if (factory == null) {
            return;
        }

        JPanel panel = factory.get();
        loadedPanels.put(card, panel);
        centerPanel.add(panel, card);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void updateSearchPlaceholder(String card) {
        switch (card) {
            case "thisinh":
                header.setPageSearchPlaceholder("Tìm kiếm thí sinh (mã, tên)");
                break;
            case "users":
                header.setPageSearchPlaceholder("Tìm kiếm người dùng (tên, email)");
                break;
            case "nganh":
                header.setPageSearchPlaceholder("Tìm kiếm ngành / tổ hợp");
                break;
            case "tohop":
                header.setPageSearchPlaceholder("Tìm kiếm môn học / tổ hợp môn");
                break;
            default:
                header.setPageSearchPlaceholder("");
                break;
        }
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

    private void logoutToLogin() {
        dispose();
        if (loginFrame != null) {
            loginFrame.showLoginScreen();
        }
    }

    public static void main(String[] args) {
        System.out.println(AppConfig.getDbHost());
        System.out.println(AppConfig.getDbPort());
        System.out.println(AppConfig.getDbName());
        System.out.println(AppConfig.getDbUser());
        System.out.println(AppConfig.getDbPassword());
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LoginFrame().setVisible(true);
        });
    }
}
