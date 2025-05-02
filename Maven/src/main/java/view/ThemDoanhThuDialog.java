package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Date;

public class ThemDoanhThuDialog extends JDialog {
    private JDateChooser dateChooserThangNam;
    private JTextField txtTongDoanhThu;
    private JTextField txtIdHoaDon;
    private JButton btnThem;
    private JButton btnHuy;
    private DoanhThuUI mainUI;
    
    // Định nghĩa các màu sắc chính
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color FIELD_BG_COLOR = Color.WHITE;
    private final Color BUTTON_SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color BUTTON_CANCEL_COLOR = new Color(192, 57, 43);

    public ThemDoanhThuDialog(JFrame parent, DoanhThuUI mainUI) {
        super(parent, "Thêm Mới Doanh Thu", true);
        this.mainUI = mainUI;
        
        // Thiết lập giao diện chính
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Banner phía trên
        JPanel headerPanel = createHeaderPanel();
        
        // Panel chính chứa form
        JPanel formPanel = createFormPanel();
        
        // Panel chứa các nút
        JPanel buttonPanel = createButtonPanel();
        
        // Thêm các panel vào dialog
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Tạo các ActionListener
        setupEventListeners();
        
        // Thiết lập cửa sổ
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Thêm Mới Doanh Thu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Thiết lập font và màu sắc cho các label
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        
        // Tháng/Năm
        createFormRow(mainPanel, gbc, 0, "Tháng/Năm:", labelFont);
        dateChooserThangNam = new JDateChooser();
        dateChooserThangNam.setPreferredSize(new Dimension(200, 30));
        dateChooserThangNam.setDateFormatString("MM/yyyy");
        dateChooserThangNam.setDate(new Date()); // Mặc định là ngày hiện tại
        dateChooserThangNam.setBorder(new CompoundBorder(
                new LineBorder(new Color(204, 204, 204), 1, true),
                new EmptyBorder(2, 5, 2, 5)));
        gbc.gridx = 1;
        mainPanel.add(dateChooserThangNam, gbc);
        
        // Tổng Thu
        createFormRow(mainPanel, gbc, 1, "Tổng Thu:", labelFont);
        txtTongDoanhThu = createTextField("");
        gbc.gridx = 1;
        mainPanel.add(txtTongDoanhThu, gbc);
        
        // ID Hóa Đơn
        createFormRow(mainPanel, gbc, 2, "ID Hóa Đơn:", labelFont);
        txtIdHoaDon = createTextField("");
        gbc.gridx = 1;
        mainPanel.add(txtIdHoaDon, gbc);
        
        return mainPanel;
    }
    
    private void createFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Font font) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);
    }
    
    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(FIELD_BG_COLOR);
        textField.setBorder(new CompoundBorder(
                new LineBorder(new Color(204, 204, 204), 1, true),
                new EmptyBorder(5, 7, 5, 7)));
        textField.setPreferredSize(new Dimension(200, 30));
        return textField;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        btnThem = createButton("Thêm", BUTTON_SUCCESS_COLOR, Color.WHITE);
        btnHuy = createButton("Hủy", BUTTON_CANCEL_COLOR, Color.WHITE);
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnHuy);
        
        return buttonPanel;
    }
    
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover cho nút
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupEventListeners() {
        // ActionListener cho nút Thêm
        btnThem.addActionListener(e -> {
            Date thangNam = dateChooserThangNam.getDate();
            String tongDoanhThuStr = txtTongDoanhThu.getText().trim();
            String idHoaDonStr = txtIdHoaDon.getText().trim();
            
            if (thangNam != null && !tongDoanhThuStr.isEmpty() && !idHoaDonStr.isEmpty()) {
                try {
                    double tongDoanhThu = Double.parseDouble(tongDoanhThuStr);
                    int idHoaDon = Integer.parseInt(idHoaDonStr);
                    
                    mainUI.getDoanhThuController().themDoanhThu(thangNam, tongDoanhThu, idHoaDon);
                    mainUI.getDoanhThuController().loadDoanhThuData();
                    dispose();
                    
                    // Hiển thị thông báo thành công
                    JOptionPane.showMessageDialog(
                        this.getParent(),
                        "Thêm doanh thu thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (NumberFormatException ex) {
                    // Hiển thị thông báo lỗi định dạng
                    JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đúng định dạng số:\n- ID Hóa Đơn: số nguyên\n- Tổng Thu: số thực",
                        "Lỗi Định Dạng",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // Hiển thị thông báo thiếu thông tin
                JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Thiếu Thông Tin",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
        
        // ActionListener cho nút Hủy
        btnHuy.addActionListener(e -> dispose());
        
        // Cài đặt phím tắt ESC để đóng dialog
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Đặt nút mặc định khi nhấn Enter
        getRootPane().setDefaultButton(btnThem);
    }
}