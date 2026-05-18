package org.AdmissionsSystem.gui.modules.Login;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.main.MainFrame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.awt.Cursor;
import javax.swing.SwingWorker;
import java.util.logging.Logger;

public class LoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());
    private static final boolean BYPASS_LOGIN_FOR_LOAD_TEST = true;
    private final LoginService loginService = new LoginService();

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JCheckBox chkShowPassword = new JCheckBox("Hiển thị mật khẩu");

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel shell = new JPanel(new GridBagLayout());
        shell.setBackground(Color.WHITE);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER_SOFT),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        GridBagConstraints shellGbc = new GridBagConstraints();
        shellGbc.gridy = 0;
        shellGbc.fill = GridBagConstraints.BOTH;
        shellGbc.weighty = 1;

        shellGbc.gridx = 0;
        shellGbc.weightx = 0.44;
        shell.add(createBrandingPanel(), shellGbc);

        shellGbc.gridx = 1;
        shellGbc.weightx = 0.56;
        shell.add(createLoginPanel(), shellGbc);
        root.add(shell, BorderLayout.CENTER);
        setContentPane(root);

        // Ensure Enter key works globally and focus is on username
        SwingUtilities.invokeLater(() -> {
            if (getRootPane() != null) {
                // Find the login button in the panel
                txtUsername.requestFocusInWindow();
            }
        });
    }

    private JPanel createBrandingPanel() {
        JPanel branding = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setPaint(new java.awt.GradientPaint(
                        0, 0, new Color(248, 250, 252),
                        getWidth(), getHeight(), new Color(226, 232, 240)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        branding.setBorder(BorderFactory.createEmptyBorder(44, 40, 44, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel logo = new JLabel(loadBrandLogo());
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 26, 0);
        branding.add(logo, gbc);

        JLabel appName = new JLabel("WELCOME", SwingConstants.CENTER);
        appName.setForeground(new Color(30, 41, 59));
        appName.setFont(new Font("Segoe UI", Font.BOLD, 30));
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        branding.add(appName, gbc);

        JLabel headline = new JLabel("Hệ thống quản lý tuyển sinh", SwingConstants.CENTER);
        headline.setForeground(new Color(30, 64, 175));
        headline.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        branding.add(headline, gbc);

        JLabel subtitle = new JLabel("<html><div style='text-align:center;'>Nền tảng tập trung để quản trị hồ sơ, điểm thi<br/>và nguyện vọng một cách trực quan.</div></html>", SwingConstants.CENTER);
        subtitle.setForeground(new Color(71, 85, 105));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 26, 0);
        branding.add(subtitle, gbc);


        return branding;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER_SOFT),
                BorderFactory.createEmptyBorder(48, 56, 42, 56)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel title = new JLabel("Đăng nhập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(title, gbc);

        JLabel desc = new JLabel("Đăng nhập để truy cập trang quản trị");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        card.add(desc, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);
        addField(card, gbc, 2, "Tên đăng nhập", txtUsername);
        addField(card, gbc, 4, "Mật khẩu", txtPassword);

        styleInput(txtUsername);
        styleInput(txtPassword);
        txtUsername.setToolTipText("Nhập tên đăng nhập");
        txtPassword.setToolTipText("Nhập mật khẩu");

        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setForeground(new Color(71, 85, 105));
        chkShowPassword.addActionListener(e -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '\u2022'));
        gbc.gridy = 6;
        gbc.insets = new Insets(8, 0, 14, 0);
        card.add(chkShowPassword, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        Style.styleFunctionButton(btnLogin, Style.BTN_UPDATE);
        btnLogin.setPreferredSize(new Dimension(0, 44));
        btnLogin.addActionListener(e -> onLogin());
        txtPassword.addActionListener(e -> onLogin());
        txtUsername.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(btnLogin);

        gbc.gridy = 7;
        gbc.insets = new Insets(4, 0, 10, 0);
        card.add(btnLogin, gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = 9;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(18, 0, 0, 0);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(51, 65, 85));
        panel.add(lbl, gbc);

        gbc.gridy = row + 1;
        field.setPreferredSize(new Dimension(320, 38));
        panel.add(field, gbc);
    }

    private void styleInput(JComponent input) {
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER_SOFT),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private javax.swing.Icon loadBrandLogo() {
        URL logoUrl = LoginFrame.class.getResource("/icons/image.png");
        if (logoUrl == null) {
            return null;
        }
        Image scaled = new javax.swing.ImageIcon(logoUrl).getImage()
                .getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(scaled);
    }


    // private void onLogin() {
    //     LoginService.AuthResult authResult = loginService.authenticate(txtUsername.getText(), txtPassword.getPassword());
    //     if (!authResult.success()) {
    //         JOptionPane.showMessageDialog(this, authResult.message(), "Đăng nhập thất bại", JOptionPane.WARNING_MESSAGE);
    //         txtPassword.selectAll();
    //         txtPassword.requestFocusInWindow();
    //         return;
    //     }

    //     // Show a loading cursor or disable button
    //     setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    //     new SwingWorker<Void, Void>() {
    //         @Override
    //         protected Void doInBackground() throws Exception {
    //             // Initialize database session factory in background thread
    //             // If DB is down, this will block and eventually throw an exception here
    //             org.AdmissionsSystem.util.HibernateUtil.getSessionFactory();
    //             return null;
    //         }

    //         @Override
    //         protected void done() {
    //             setCursor(Cursor.getDefaultCursor());
    //             try {
    //                 get(); // Check for exceptions during initialization
                    
    //                 // Now that DB is ready, create MainFrame on the EDT
    //                 MainFrame mainFrame = new MainFrame(LoginFrame.this, authResult.displayName(), authResult.role());
    //                 mainFrame.setVisible(true);
    //                 setVisible(false);
    //             } catch (Exception e) {
    //                 LOGGER.log(java.util.logging.Level.SEVERE, "Lỗi khi khởi tạo hệ thống: " + e.getMessage(), e);
    //                 String msg = "Không thể kết nối đến cơ sở dữ liệu.\n" +
    //                              "Vui lòng đảm bảo server MySQL đã được mở và thông tin cấu hình chính xác.";
    //                 JOptionPane.showMessageDialog(LoginFrame.this, msg, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
    //             }
    //         }
    //     }.execute();
    // }
private void onLogin() {
        LoginService.AuthResult authResult;
        if (BYPASS_LOGIN_FOR_LOAD_TEST) {
            authResult = LoginService.AuthResult.success("Admin Test", "ADMIN");
        } else {
            authResult = loginService.authenticate(txtUsername.getText(), txtPassword.getPassword());
            if (!authResult.success()) {
                JOptionPane.showMessageDialog(this, authResult.message(), "Đăng nhập thất bại", JOptionPane.WARNING_MESSAGE);
                txtPassword.selectAll();
                txtPassword.requestFocusInWindow();
                return;
            }
        }

        // Show a loading cursor or disable button
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Initialize database session factory in background thread
                // If DB is down, this will block and eventually throw an exception here
                org.AdmissionsSystem.util.HibernateUtil.getSessionFactory();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get(); // Check for exceptions during initialization
                    
                    // Now that DB is ready, create MainFrame on the EDT
                    MainFrame mainFrame = new MainFrame(LoginFrame.this, authResult.displayName(), authResult.role());
                    mainFrame.setVisible(true);
                    setVisible(false);
                } catch (Exception e) {
                    LOGGER.log(java.util.logging.Level.SEVERE, "Lỗi khi khởi tạo hệ thống: " + e.getMessage(), e);
                    String msg = "Không thể kết nối đến cơ sở dữ liệu.\n" +
                                 "Vui lòng đảm bảo server MySQL đã được mở và thông tin cấu hình chính xác.";
                    JOptionPane.showMessageDialog(LoginFrame.this, msg, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    public void showLoginScreen() {
        txtUsername.setText("");
        txtPassword.setText("");
        setVisible(true);
        toFront();
        requestFocus();
        txtUsername.requestFocusInWindow();
    }
}
