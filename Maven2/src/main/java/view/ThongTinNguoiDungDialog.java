package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import controller.NguoiDungController;
import model.NguoiDung;

public class ThongTinNguoiDungDialog extends JDialog {
    // UI components for displaying user info
    private JLabel idValueLabel;
    private JTextField hoTenField;
    private JTextField emailField;
    private JTextField soDienThoaiField;
    private JTextField ngaySinhField;
    private JComboBox<String> gioiTinhComboBox;
    private JLabel vaiTroValueLabel;
    
    // Buttons for actions
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private NguoiDungController nguoiDungController;
    private int userId;
    private NguoiDung currentUser;
    private boolean editMode = false;

    public ThongTinNguoiDungDialog(Frame parent, int userId, NguoiDungController controller) {
        super(parent, "Thông Tin Người Dùng", true);
        this.userId = userId;
        this.nguoiDungController = controller;
        
        initializeUI();
        loadUserData(userId);
        
        // Cấu hình dialog
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 247, 250));
        
        // Panel tiêu đề - bỏ màu nền, giữ màu chữ
        JPanel headerPanel = new JPanel(new BorderLayout());
        // Bỏ màu nền cho header
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("THÔNG TIN NGƯỜI DÙNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        // Giữ màu chữ
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel nội dung - giảm padding để nội dung vừa khung hơn
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        // Panel thông tin
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 10); // Giảm insets để tiết kiệm không gian
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Định dạng chung cho các label tiêu đề
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Color labelColor = new Color(52, 73, 94);
        
        // ID (không thể chỉnh sửa)
        addLabelField(infoPanel, gbc, "ID:", labelFont, labelColor, 0);
        
        // Họ và tên
        addEditableField(infoPanel, gbc, "Họ và Tên:", labelFont, labelColor, 1);
        
        // Email
        addEditableField(infoPanel, gbc, "Email:", labelFont, labelColor, 2);
        
        // Số điện thoại
        addEditableField(infoPanel, gbc, "Số Điện Thoại:", labelFont, labelColor, 3);
        
        // Ngày sinh
        addEditableField(infoPanel, gbc, "Ngày Sinh:", labelFont, labelColor, 4);
        
        // Giới tính
        addGenderComboBox(infoPanel, gbc, "Giới Tính:", labelFont, labelColor, 5);
        
        // Vai trò (chỉ hiển thị, không chỉnh sửa)
        addReadOnlyField(infoPanel, gbc, "Vai Trò:", labelFont, labelColor, 6);
        
        // Thêm panel thông tin vào panel nội dung
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Panel chứa các nút - giảm padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Nút "Chỉnh sửa"
        editButton = createButton("Chỉnh sửa", new Color(41, 128, 185));
        editButton.addActionListener(e -> toggleEditMode(true));
        
        // Nút "Lưu"
        saveButton = createButton("Lưu", new Color(46, 204, 113));
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveUserData());
        
        // Nút "Hủy"
        cancelButton = createButton("Hủy", new Color(231, 76, 60));
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelEdit());
        
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Thêm panel nút vào panel nội dung
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm các panel vào panel chính
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Thêm panel chính vào dialog với viền tạo bóng đổ
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel);
        // Điều chỉnh kích thước cho vừa vặn
        setPreferredSize(new Dimension(430, 480));
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(95, 32)); // Giảm kích thước nút
        
        return button;
    }
    
    private void addLabelField(JPanel panel, GridBagConstraints gbc, String labelText, 
                              Font labelFont, Color labelColor, int row) {
        // Label tiêu đề
        JLabel titleLabel = new JLabel(labelText);
        titleLabel.setFont(labelFont);
        titleLabel.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(titleLabel, gbc);
        
        // Label giá trị cho ID (không thể chỉnh sửa)
        idValueLabel = new JLabel();
        idValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idValueLabel.setForeground(new Color(44, 62, 80));
        idValueLabel.setBorder(new CompoundBorder(
                new EmptyBorder(6, 8, 6, 8), // Giảm padding
                new MatteBorder(0, 0, 1, 0, new Color(189, 195, 199))
        ));
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(idValueLabel, gbc);
    }
    
    private void addEditableField(JPanel panel, GridBagConstraints gbc, String labelText, 
                                Font labelFont, Color labelColor, int row) {
        // Label tiêu đề
        JLabel titleLabel = new JLabel(labelText);
        titleLabel.setFont(labelFont);
        titleLabel.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(titleLabel, gbc);
        
        // Text field cho giá trị
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setEditable(false);
        textField.setBorder(new CompoundBorder(
                new EmptyBorder(6, 8, 6, 8), // Giảm padding
                new MatteBorder(0, 0, 1, 0, new Color(189, 195, 199))
        ));
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(textField, gbc);
        
        // Lưu reference đến các text field
        switch (row) {
            case 1: hoTenField = textField; break;
            case 2: emailField = textField; break;
            case 3: soDienThoaiField = textField; break;
            case 4: ngaySinhField = textField; break;
        }
    }
    
    private void addReadOnlyField(JPanel panel, GridBagConstraints gbc, String labelText, 
                                Font labelFont, Color labelColor, int row) {
        // Label tiêu đề
        JLabel titleLabel = new JLabel(labelText);
        titleLabel.setFont(labelFont);
        titleLabel.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(titleLabel, gbc);
        
        // Label giá trị cho vai trò (không thể chỉnh sửa)
        vaiTroValueLabel = new JLabel();
        vaiTroValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vaiTroValueLabel.setForeground(new Color(44, 62, 80));
        vaiTroValueLabel.setBorder(new CompoundBorder(
                new EmptyBorder(6, 8, 6, 8), // Giảm padding
                new MatteBorder(0, 0, 1, 0, new Color(189, 195, 199))
        ));
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(vaiTroValueLabel, gbc);
    }
    
    private void addGenderComboBox(JPanel panel, GridBagConstraints gbc, String labelText, 
                               Font labelFont, Color labelColor, int row) {
        // Label tiêu đề
        JLabel titleLabel = new JLabel(labelText);
        titleLabel.setFont(labelFont);
        titleLabel.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(titleLabel, gbc);
        
        // ComboBox cho giới tính
        String[] genders = {"Nam", "Nữ", "Khác"};
        gioiTinhComboBox = new JComboBox<>(genders);
        gioiTinhComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gioiTinhComboBox.setEnabled(false);
        gioiTinhComboBox.setBorder(new CompoundBorder(
                new EmptyBorder(4, 4, 4, 4), // Giảm padding
                new MatteBorder(0, 0, 1, 0, new Color(189, 195, 199))
        ));
        gioiTinhComboBox.setBackground(Color.WHITE);
        gioiTinhComboBox.setFocusable(false);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(gioiTinhComboBox, gbc);
    }

    public void loadUserData(int userId) {
        try {
            currentUser = nguoiDungController.getNguoiDungById(userId);
            if (currentUser != null) {
                // Hiển thị thông tin người dùng
                idValueLabel.setText(String.valueOf(currentUser.getIdNguoiDung()));
                hoTenField.setText(currentUser.getHoTen());
                emailField.setText(currentUser.getEmail());
                soDienThoaiField.setText(currentUser.getSoDienThoai());
                
                // Xử lý ngày sinh
                if (currentUser.getNgaySinh() != null) {
                    ngaySinhField.setText(dateFormat.format(currentUser.getNgaySinh()));
                } else {
                    ngaySinhField.setText("");
                }
                
                // Xử lý giới tính
                if (currentUser.getGioiTinh() != null) {
                    String gender = currentUser.getGioiTinh();
                    for (int i = 0; i < gioiTinhComboBox.getItemCount(); i++) {
                        if (gioiTinhComboBox.getItemAt(i).equalsIgnoreCase(gender)) {
                            gioiTinhComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                // Hiển thị vai trò (chỉ đọc)
                vaiTroValueLabel.setText(currentUser.getVaiTro());
                
                // Cập nhật lại kích thước dialog sau khi đặt dữ liệu
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy người dùng với ID: " + userId, 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                dispose(); // Đóng dialog nếu không tìm thấy dữ liệu
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose(); // Đóng dialog nếu có lỗi
        }
    }
    
    private void toggleEditMode(boolean enabled) {
        // Bật/tắt chế độ chỉnh sửa
        editMode = enabled;
        
        // Cập nhật trạng thái các thành phần UI
        hoTenField.setEditable(enabled);
        emailField.setEditable(enabled);
        soDienThoaiField.setEditable(enabled);
        ngaySinhField.setEditable(enabled);
        gioiTinhComboBox.setEnabled(enabled);
        
        // Cập nhật trạng thái các nút
        editButton.setEnabled(!enabled);
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
        
        // Thay đổi nền cho text field khi ở chế độ chỉnh sửa
        Color bgColor = enabled ? new Color(240, 248, 255) : Color.WHITE;
        
        hoTenField.setBackground(bgColor);
        emailField.setBackground(bgColor);
        soDienThoaiField.setBackground(bgColor);
        ngaySinhField.setBackground(bgColor);
        
        // Nếu bật chế độ chỉnh sửa, focus vào field đầu tiên
        if (enabled) {
            hoTenField.requestFocus();
        }
    }
    
    private void saveUserData() {
        // Kiểm tra và lấy dữ liệu từ các trường
        String hoTen = hoTenField.getText().trim();
        String email = emailField.getText().trim();
        String soDienThoai = soDienThoaiField.getText().trim();
        String ngaySinhStr = ngaySinhField.getText().trim();
        String gioiTinh = (String) gioiTinhComboBox.getSelectedItem();
        
        // Kiểm tra dữ liệu
        if (hoTen.isEmpty() || email.isEmpty() || soDienThoai.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin bắt buộc (Họ tên, Email, Số điện thoại)");
            return;
        }
        
        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            showErrorMessage("Định dạng email không hợp lệ");
            return;
        }
        
        // Kiểm tra định dạng số điện thoại
        if (!isValidPhoneNumber(soDienThoai)) {
            showErrorMessage("Số điện thoại không hợp lệ");
            return;
        }
        
        // Cập nhật dữ liệu người dùng
        currentUser.setHoTen(hoTen);
        currentUser.setEmail(email);
        currentUser.setSoDienThoai(soDienThoai);
        currentUser.setGioiTinh(gioiTinh);
        
        // Xử lý ngày sinh
        if (!ngaySinhStr.isEmpty()) {
            try {
                Date ngaySinh = dateFormat.parse(ngaySinhStr);
                currentUser.setNgaySinh(new java.sql.Date(ngaySinh.getTime()));
            } catch (ParseException e) {
                showErrorMessage("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
                return;
            }
        } else {
            currentUser.setNgaySinh(null);
        }
        
        // Lưu dữ liệu vào cơ sở dữ liệu
        try {
            nguoiDungController.updateUser(currentUser);
            
            // Hiển thị thông báo thành công với giao diện đẹp hơn
            showSuccessMessage("Cập nhật thông tin người dùng thành công");
            
            // Tắt chế độ chỉnh sửa sau khi lưu thành công
            toggleEditMode(false);
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
    
    private boolean isValidPhoneNumber(String phone) {
        String regex = "^[0-9]{10,11}$";
        return phone.matches(regex);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Thành công",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelEdit() {
        // Hiển thị lại dữ liệu gốc
        loadUserData(userId);
        
        // Tắt chế độ chỉnh sửa
        toggleEditMode(false);
    }

    // Phương thức để hiển thị dialog
    public static void showDialog(Frame parent, int userId, NguoiDungController controller) {
        ThongTinNguoiDungDialog dialog = new ThongTinNguoiDungDialog(parent, userId, controller);
        dialog.setVisible(true);
    }
}