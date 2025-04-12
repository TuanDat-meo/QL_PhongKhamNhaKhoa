package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SuaDoanhThuDialog extends JDialog {
    private JTextField txtIdDoanhThu;
    private JDateChooser dateChooserThangNam;
    private JTextField txtTongDoanhThu;
    private JTextField txtIdHoaDon;
    private JButton btnSua;
    private JButton btnHuy;
    private DoanhThuUI mainUI;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    
    // Định nghĩa các màu sắc chính
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color FIELD_BG_COLOR = Color.WHITE;
    private final Color BUTTON_SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color BUTTON_CANCEL_COLOR = new Color(192, 57, 43);

    public SuaDoanhThuDialog(JFrame parent, Object[] data, DoanhThuUI mainUI) {
        super(parent, "Sửa Thông Tin Doanh Thu", true);
        this.mainUI = mainUI;
        
        // Thiết lập giao diện chính
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Banner phía trên
        JPanel headerPanel = createHeaderPanel();
        
        // Panel chính chứa form
        JPanel formPanel = createFormPanel(data);
        
        // Panel chứa các nút
        JPanel buttonPanel = createButtonPanel();
        
        // Thêm các panel vào dialog
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Tạo các ActionListener
        setupEventListeners(parent);
        
        // Thiết lập cửa sổ
        setSize(400, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Cập Nhật Thông Tin Doanh Thu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createFormPanel(Object[] data) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Thiết lập font và màu sắc cho các label
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        
        // ID Doanh Thu
        createFormRow(mainPanel, gbc, 0, "ID Doanh Thu:", labelFont);
        txtIdDoanhThu = createTextField(data[0].toString(), false);
        txtIdDoanhThu.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        mainPanel.add(txtIdDoanhThu, gbc);
        
        // ID Hóa Đơn
        createFormRow(mainPanel, gbc, 1, "ID Hóa Đơn:", labelFont);
        txtIdHoaDon = createTextField(data[1].toString(), true);
        gbc.gridx = 1;
        mainPanel.add(txtIdHoaDon, gbc);
        
        // Tháng/Năm
        createFormRow(mainPanel, gbc, 2, "Tháng/Năm:", labelFont);
        dateChooserThangNam = new JDateChooser();
        dateChooserThangNam.setPreferredSize(new Dimension(200, 30));
        dateChooserThangNam.setDateFormatString("MM/yyyy");
        dateChooserThangNam.setBorder(new CompoundBorder(
                new LineBorder(new Color(204, 204, 204), 1, true),
                new EmptyBorder(2, 5, 2, 5)));
        try {
            dateChooserThangNam.setDate(monthYearFormat.parse(data[3].toString()));
        } catch (ParseException e) {
            dateChooserThangNam.setDate(new Date());
            e.printStackTrace();
        }
        gbc.gridx = 1;
        mainPanel.add(dateChooserThangNam, gbc);
        
        // Tổng Thu
        createFormRow(mainPanel, gbc, 3, "Tổng Thu:", labelFont);
        txtTongDoanhThu = createTextField(data[4].toString(), true);
        gbc.gridx = 1;
        mainPanel.add(txtTongDoanhThu, gbc);
        
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
    
    private JTextField createTextField(String text, boolean editable) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(FIELD_BG_COLOR);
        textField.setBorder(new CompoundBorder(
                new LineBorder(new Color(204, 204, 204), 1, true),
                new EmptyBorder(5, 7, 5, 7)));
        textField.setEnabled(editable);
        textField.setPreferredSize(new Dimension(200, 30));
        return textField;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        btnSua = createButton("Cập Nhật", BUTTON_SUCCESS_COLOR, Color.WHITE);
        btnHuy = createButton("Hủy", BUTTON_CANCEL_COLOR, Color.WHITE);
        
        buttonPanel.add(btnSua);
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
    
    private void setupEventListeners(JFrame parent) {
        // ActionListener cho nút Sửa
        btnSua.addActionListener(e -> {
            String idDoanhThuStr = txtIdDoanhThu.getText();
            Date thangNam = dateChooserThangNam.getDate();
            String tongDoanhThuStr = txtTongDoanhThu.getText();
            String idHoaDonStr = txtIdHoaDon.getText();
            
            if (!idDoanhThuStr.isEmpty() && thangNam != null && !tongDoanhThuStr.isEmpty() && !idHoaDonStr.isEmpty()) {
                try {
                    int idDoanhThu = Integer.parseInt(idDoanhThuStr);
                    double tongDoanhThu = Double.parseDouble(tongDoanhThuStr);
                    int idHoaDon = Integer.parseInt(idHoaDonStr);
                    
                    mainUI.getDoanhThuController().suaDoanhThu(idDoanhThu, thangNam, tongDoanhThu, idHoaDon);
                    mainUI.getDoanhThuController().loadDoanhThuData();
                    dispose();
                    
                    // Hiển thị thông báo thành công với giao diện đẹp hơn
                    JOptionPane.showMessageDialog(
                        parent, 
                        "Cập nhật doanh thu thành công!", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (NumberFormatException ex) {
                    // Hiển thị thông báo lỗi với giao diện đẹp hơn
                    JOptionPane.showMessageDialog(
                        this, 
                        "Vui lòng nhập đúng định dạng số:\n- ID Hóa Đơn: số nguyên\n- Tổng Thu: số thực", 
                        "Lỗi Định Dạng", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // Hiển thị thông báo thiếu thông tin với giao diện đẹp hơn
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
        getRootPane().setDefaultButton(btnSua);
    }
}