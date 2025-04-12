package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class GiaoDienChinh extends JFrame {
    private JPanel contentPanel;
    private String[] menuItems = {
            "Quản lý Bệnh Nhân", "Quản lý Doanh Thu", "Quản lý Hóa Đơn",
            "Quản lý Hồ Sơ", "Quản lý Kho Vật Tư", "Quản lý Lịch Hẹn",
            "Quản lý Lương", "Quản lý Nhà Cung Cấp", "Quản lý Thanh Toán", "Thống Kê","Thông tin Người Dùng"
    };

    public GiaoDienChinh() {
        setTitle("Phần mềm Quản lý Phòng Khám Nha Khoa");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== HEADER ====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(224, 242, 241));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel logoLabel = new JLabel("Nha Khoa SmileCare");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(logoLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ==== DANH SÁCH MENU ====
        JList<String> menuList = new JList<>(menuItems);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setBackground(new Color(60, 75, 85));
        menuList.setForeground(Color.WHITE);
        menuList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        menuList.setFixedCellHeight(35); // Giảm chiều cao mỗi mục xuống 35
        menuList.setBorder(new EmptyBorder(10, 10, 10, 10));
        menuList.setCellRenderer(new MenuRenderer());

        // ==== NÚT ĐĂNG XUẤT ====
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(255, 0, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(220, 45));
        logoutButton.addActionListener(e -> logout());

        // ==== PANEL MENU BÊN TRÁI ====
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setPreferredSize(new Dimension(200, getHeight())); // Thu hẹp thanh menu
        menuPanel.setBackground(new Color(38, 50, 56));

        JLabel menuLabel = new JLabel("MENU", JLabel.LEFT);
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        menuPanel.add(menuLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(menuList);
        scrollPane.setBorder(null);
        menuPanel.add(scrollPane, BorderLayout.CENTER);

        menuPanel.add(logoutButton, BorderLayout.SOUTH);

        // ==== CONTENT PANEL ====
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // ==== GIAO DIỆN CÁC CHỨC NĂNG ====
        BenhNhanUI benhNhanPanel = new BenhNhanUI();
        contentPanel.add(benhNhanPanel, "Quản lý Bệnh Nhân");

        LichHenGUI lichHenPanel = new LichHenGUI();
        contentPanel.add(lichHenPanel, "Quản lý Lịch Hẹn");

        ThongTinNguoiDungUI thongTinNguoiDungPanel = new ThongTinNguoiDungUI();
        contentPanel.add(thongTinNguoiDungPanel, "Thông tin Người Dùng");

        // ==== CHUYỂN ĐỔI GIAO DIỆN ====
        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = menuList.getSelectedValue();
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, selectedItem);
            }
        });

        menuList.setSelectedIndex(0);
    }

    // ==== PHƯƠNG THỨC ĐĂNG XUẤT ====
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Đóng giao diện hiện tại
            new LoginFrame(); // Mở lại màn hình đăng nhập
        }
    }

    // ==== CUSTOM RENDERER MENU ====
    private static class MenuRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(10, 15, 10, 10));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (isSelected) {
                label.setBackground(new Color(45, 59, 69));
                label.setForeground(Color.CYAN);
            }
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienChinh().setVisible(true));
    }
}
