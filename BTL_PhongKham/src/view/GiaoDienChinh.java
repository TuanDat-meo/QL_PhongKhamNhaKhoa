package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.NguoiDungController;
import model.NguoiDung;

public class GiaoDienChinh extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JButton menuButton;
    private boolean isMenuVisible = true;
    private final int MENU_WIDTH = 220;
    
    // Refined color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Soft blue
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Lighter blue for hover effects
    private final Color DARK_COLOR = new Color(44, 62, 80);         // Dark slate for menu bg
    private final Color LIGHT_COLOR = new Color(236, 240, 241);     // Off-white for contrast
    private final Color ACCENT_COLOR = new Color(26, 188, 156);     // Teal accent for selected items
    
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private String[] menuItems = {
            "Quản lý Bệnh Nhân", "Quản lý Doanh Thu", "Quản lý Hóa Đơn",
            "Quản lý Hồ Sơ", "Quản lý Kho Vật Tư", "Quản lý Lịch Hẹn",
            "Quản lý Lương", "Quản lý Nhà Cung Cấp", "Thống Kê"
    };

    private JPanel mainPanel;
    private ThongTinNguoiDungUI thongTinNguoiDungPanel;
    private static NguoiDung loggedInUser;
    private NguoiDungController thongTinNguoiDungController = new NguoiDungController();

    public GiaoDienChinh(NguoiDung user) throws SQLException {
        this.loggedInUser = user;
        setTitle("Phần mềm Quản lý Phòng Khám Nha Khoa");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        thongTinNguoiDungPanel = new ThongTinNguoiDungUI(user.getIdNguoiDung(), thongTinNguoiDungController);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);

        menuPanel = createMenuPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, contentPanel);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(0);
        splitPane.setBorder(null);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        initializeContentPanels();

        createCircularMenuButton();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50)); // Reduced height
        headerPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel logoLabel = new JLabel("Nha Khoa SmileCare");
        logoLabel.setFont(HEADER_FONT);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        titlePanel.add(logoLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Xin chào, " + loggedInUser.getHoTen());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Smaller font
        userLabel.setForeground(Color.WHITE);

        // Improved profile button
        JButton profileButton = new JButton("Hồ sơ");
        profileButton.setFont(BUTTON_FONT);
        profileButton.setBackground(new Color(255, 255, 255, 80)); // Semi-transparent white
        profileButton.setForeground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 100), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        profileButton.setFocusPainted(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Button hover effect
        profileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                profileButton.setBackground(new Color(255, 255, 255, 120));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                profileButton.setBackground(new Color(255, 255, 255, 80));
            }
        });

        profileButton.addActionListener(e -> {
            showThongTinNguoiDung();
        });

        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(10)); // Add space between label and button
        userPanel.add(profileButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void showThongTinNguoiDung() {
        thongTinNguoiDungPanel.loadUserData(loggedInUser.getIdNguoiDung());
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "Thông tin Người Dùng");
    }

    private void highlightMenuItem(String menuItemText) {
        for (Component scrollComp : menuPanel.getComponents()) {
            if (scrollComp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) scrollComp;
                Component viewComp = scrollPane.getViewport().getView();

                if (viewComp instanceof JPanel) {
                    JPanel menuListPanel = (JPanel) viewComp;

                    for (Component menuItemComp : menuListPanel.getComponents()) {
                        if (menuItemComp instanceof JPanel) {
                            JPanel menuItemPanel = (JPanel) menuItemComp;

                            for (Component labelComp : menuItemPanel.getComponents()) {
                                if (labelComp instanceof JLabel) {
                                    JLabel label = (JLabel) labelComp;

                                    if (label.getText().equals(menuItemText)) {
                                        for (Component comp : menuListPanel.getComponents()) {
                                            if (comp instanceof JPanel) {
                                                comp.setBackground(DARK_COLOR);
                                                for (Component c : ((JPanel) comp).getComponents()) {
                                                    if (c instanceof JLabel) {
                                                        c.setForeground(Color.WHITE);
                                                    }
                                                }
                                            }
                                        }
                                        menuItemPanel.setBackground(ACCENT_COLOR);
                                        label.setForeground(Color.WHITE);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel createMenuPanel() {
        JPanel menuContainerPanel = new JPanel(new BorderLayout());
        menuContainerPanel.setPreferredSize(new Dimension(MENU_WIDTH, getHeight()));
        menuContainerPanel.setBackground(DARK_COLOR);

        JLabel menuHeader = new JLabel("MENU", JLabel.CENTER);
        menuHeader.setForeground(Color.WHITE);
        menuHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        menuHeader.setBorder(new EmptyBorder(15, 10, 15, 10));
        menuContainerPanel.add(menuHeader, BorderLayout.NORTH);

        JPanel menuListPanel = new JPanel();
        menuListPanel.setLayout(new BoxLayout(menuListPanel, BoxLayout.Y_AXIS));
        menuListPanel.setBackground(DARK_COLOR);

        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItemPanel = createMenuItem(menuItems[i], i);
            menuListPanel.add(menuItemPanel);
        }

        JScrollPane scrollPane = new JScrollPane(menuListPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        menuContainerPanel.add(scrollPane, BorderLayout.CENTER);

        // Enhanced logout button
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(BUTTON_FONT);
        logoutButton.setBackground(new Color(231, 76, 60, 180)); // Semi-transparent red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(MENU_WIDTH - 20, 36)); // Smaller height
        
        // Logout button hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(231, 76, 60)); // Solid on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(231, 76, 60, 180));
            }
        });
        
        logoutButton.addActionListener(e -> logout());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        logoutPanel.setBackground(DARK_COLOR);
        logoutPanel.add(logoutButton);
        menuContainerPanel.add(logoutPanel, BorderLayout.SOUTH);

        return menuContainerPanel;
    }

    private JPanel createMenuItem(String menuText, int index) {
        JPanel menuItemPanel = new JPanel();
        menuItemPanel.setLayout(new BorderLayout());
        menuItemPanel.setBackground(DARK_COLOR);
        menuItemPanel.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        menuItemPanel.setMaximumSize(new Dimension(MENU_WIDTH, 42)); // Smaller height
        menuItemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textLabel = new JLabel(menuText);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(MENU_FONT);

        menuItemPanel.add(textLabel, BorderLayout.CENTER);

        menuItemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItemPanel.setBackground(SECONDARY_COLOR);
                textLabel.setForeground(LIGHT_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!menuItemPanel.getBackground().equals(ACCENT_COLOR)) {
                    menuItemPanel.setBackground(DARK_COLOR);
                    textLabel.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, menuText);

                for (Component component : menuItemPanel.getParent().getComponents()) {
                    if (component instanceof JPanel) {
                        component.setBackground(DARK_COLOR);
                        for (Component c : ((JPanel) component).getComponents()) {
                            if (c instanceof JLabel) {
                                c.setForeground(Color.WHITE);
                            }
                        }
                    }
                }
                menuItemPanel.setBackground(ACCENT_COLOR);
                textLabel.setForeground(Color.WHITE);
            }
        });

        return menuItemPanel;
    }

    // Improved rounded border class with antialiasing
    private static class RoundedBorder implements Border {
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2d.dispose();
        }
    }

    private void createCircularMenuButton() {
        menuButton = new JButton("☰");
        menuButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Smaller font
        menuButton.setBackground(PRIMARY_COLOR);
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int buttonSize = 48; // Smaller button
        menuButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
        menuButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
        menuButton.setMaximumSize(new Dimension(buttonSize, buttonSize));

        // Add shadow effect to button
        menuButton.setBorder(new RoundedBorder(buttonSize/2, new Color(0, 0, 0, 80)));
        menuButton.setContentAreaFilled(true);
        
        // Add hover effect
        menuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuButton.setBackground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuButton.setBackground(PRIMARY_COLOR);
            }
        });

        menuButton.addActionListener(e -> toggleMenu());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(getWidth(), getHeight()));

        layeredPane.add(menuButton, JLayeredPane.PALETTE_LAYER);

        menuButton.setVisible(!isMenuVisible);
        menuButton.setBounds(15, 70, buttonSize, buttonSize);

        setGlassPane(layeredPane);
        getGlassPane().setVisible(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                menuButton.setBounds(15, 70, buttonSize, buttonSize);
            }
        });
    }

    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;

        menuPanel.setVisible(isMenuVisible);

        menuButton.setVisible(!isMenuVisible);
        menuButton.setText(isMenuVisible ? "✕" : "☰");

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(LIGHT_COLOR);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 26)); // Smaller height

        JLabel copyrightLabel = new JLabel("© 2025 Nha Khoa SmileCare. Bản quyền thuộc về công ty.");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
        copyrightLabel.setForeground(new Color(100, 100, 100));
        copyrightLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        footerPanel.add(copyrightLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("Phiên bản 1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
        versionLabel.setForeground(new Color(100, 100, 100));
        versionLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        footerPanel.add(versionLabel, BorderLayout.EAST);

        return footerPanel;
    }
    
    private void initializeContentPanels() throws SQLException {
        // Thêm các panel cho từng chức năng
        BenhNhanUI benhNhanPanel = new BenhNhanUI();
        contentPanel.add(benhNhanPanel, "Quản lý Bệnh Nhân");

        LichHenGUI lichHenPanel = new LichHenGUI();
        contentPanel.add(lichHenPanel, "Quản lý Lịch Hẹn");
        
        // Add ThongTinNguoiDungUI panel for user profile (used by header button)
        contentPanel.add(thongTinNguoiDungPanel, "Thông tin Người Dùng");
        
        // HoSoBenhAnUI for the "Quản lý Hồ Sơ" menu item
        HoSoBenhAnUI hoSoBenhAnPanel = new HoSoBenhAnUI();
        contentPanel.add(hoSoBenhAnPanel, "Quản lý Hồ Sơ");
        
        HoaDonUI hoaDonPanel = new HoaDonUI();
        contentPanel.add(hoaDonPanel, "Quản lý Hóa Đơn");
        
        DoanhThuUI doanhThuPanel = new DoanhThuUI();
        contentPanel.add(doanhThuPanel, "Quản lý Doanh Thu");
        
        NhaCungCapUI nhaCungCapPanel = new NhaCungCapUI();
        contentPanel.add(nhaCungCapPanel, "Quản lý Nhà Cung Cấp");
        
        KhoVatTuUI khoVatTuPanel = new KhoVatTuUI();
        contentPanel.add(khoVatTuPanel, "Quản lý Kho Vật Tư");
        
        LuongUI luongPanel = new LuongUI();
        contentPanel.add(luongPanel, "Quản lý Lương");
        
        // Thêm panel giả cho Thống kê
        BacSiUI thongKePanel = new BacSiUI();
        contentPanel.add(thongKePanel, "Thống Kê");

        // Set default panel
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "Quản lý Bệnh Nhân");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();

            // Create and show LoginFrame on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        null,
                        "Lỗi khởi tạo LoginFrame: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi tạo: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}