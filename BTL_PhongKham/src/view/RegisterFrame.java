package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.LineBorder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connect.connectMySQL;

public class RegisterFrame extends JFrame {
    private JTextField txtHoTen, txtEmail, txtSoDienThoai;
    private JPasswordField txtMatKhau, txtNhapLaiMatKhau;
    private JComboBox<String> cboGioiTinh;
    private JDateChooser dateChooserNgaySinh;
    private JButton btnDangKy;

    public RegisterFrame() {
        setTitle("Đăng ký");
        setSize(300, 350); // Nhỏ hơn
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14)); // Nhỏ hơn
        lblTitle.setForeground(new Color(50, 50, 150));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(7, 1, 5, 5)); // Nhỏ hơn
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30)); // Giảm lề
        panelForm.setBackground(Color.WHITE);

        txtHoTen = new JTextField();
        addPlaceholder(txtHoTen, "Họ và tên");
        txtEmail = new JTextField();
        addPlaceholder(txtEmail, "Email");
        txtSoDienThoai = new JTextField();
        addPlaceholder(txtSoDienThoai, "Số điện thoại");

        txtMatKhau = new JPasswordField();
        addPasswordPlaceholder(txtMatKhau, "Mật khẩu");
        txtNhapLaiMatKhau = new JPasswordField();
        addPasswordPlaceholder(txtNhapLaiMatKhau, "Nhập lại mật khẩu");

     // Ngày sinh - Hiển thị ngày hiện tại khi mở form
        dateChooserNgaySinh = new JDateChooser();
        dateChooserNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateChooserNgaySinh.setDate(new java.util.Date()); // Gán ngày hiện tại
        dateChooserNgaySinh.setPreferredSize(new Dimension(120, 24)); 


        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cboGioiTinh.setFont(new Font("Arial", Font.PLAIN, 12)); // Nhỏ hơn

        panelForm.add(txtHoTen);
        panelForm.add(txtEmail);
        panelForm.add(txtSoDienThoai);
        panelForm.add(dateChooserNgaySinh);
        panelForm.add(cboGioiTinh);
        panelForm.add(txtMatKhau);
        panelForm.add(txtNhapLaiMatKhau);
        add(panelForm, BorderLayout.CENTER);

        btnDangKy = new JButton("Đăng ký");
        btnDangKy.setFont(new Font("Arial", Font.BOLD, 12)); // Nhỏ hơn
        btnDangKy.setBackground(new Color(50, 150, 250));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.addActionListener(e -> dangKyTaiKhoan());

        JPanel panelButton = new JPanel();
        panelButton.setBackground(Color.WHITE);
        panelButton.add(btnDangKy);
        add(panelButton, BorderLayout.SOUTH);

        setVisible(true);
        panelButton.requestFocusInWindow();
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setPreferredSize(new Dimension(120, 24)); // Giảm chiều cao
        field.setFont(new Font("Arial", Font.PLAIN, 12));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void addPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.setPreferredSize(new Dimension(120, 24)); // Giảm chiều cao
        field.setFont(new Font("Arial", Font.PLAIN, 12));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }

    private void dangKyTaiKhoan() {
        String hoTen = txtHoTen.getText().equals("Họ và tên") ? "" : txtHoTen.getText();
        String email = txtEmail.getText().equals("Email") ? "" : txtEmail.getText();
        String soDienThoai = txtSoDienThoai.getText().equals("Số điện thoại") ? "" : txtSoDienThoai.getText();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String ngaySinh = dateChooserNgaySinh.getDate() != null ? dateFormat.format(dateChooserNgaySinh.getDate()) : "";
        String gioiTinh = (String) cboGioiTinh.getSelectedItem();
        String matKhau1 = new String(txtMatKhau.getPassword());
        String matKhau2 = new String(txtNhapLaiMatKhau.getPassword());

        // Kiểm tra tên không được để trống
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra độ dài họ tên
        if (hoTen.length() > 100) {
            JOptionPane.showMessageDialog(this, "Họ tên không được vượt quá 100 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra định dạng tên (chỉ chứa chữ cái và dấu cách)
        if (!hoTen.matches("^[\\p{L}\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Tên không được chứa số hoặc ký tự đặc biệt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra độ dài email
        if (email.length() > 254) {
            JOptionPane.showMessageDialog(this, "Email không được vượt quá 254 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            JOptionPane.showMessageDialog(this, "Email phải có định dạng @gmail.com!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra email đã tồn tại
        if (kiemTraEmailTonTai(email)) {
            JOptionPane.showMessageDialog(this, "Email này đã được đăng ký!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra số điện thoại
        if (!soDienThoai.matches("^[0-9]+$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được nhập số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra độ dài số điện thoại
        if (soDienThoai.length() < 10) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có ít nhất 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (soDienThoai.length() > 11) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được quá 11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (kiemTraSoDienThoaiTonTai(soDienThoai)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được đăng ký!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra mật khẩu mạnh
        if (!matKhau1.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            JOptionPane.showMessageDialog(this, 
                "Mật khẩu phải có ít nhất:\n" +
                "- 8 ký tự\n" +
                "- 1 chữ hoa\n" +
                "- 1 chữ thường\n" +
                "- 1 số\n" +
                "- 1 ký tự đặc biệt (@#$%^&+=)", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!matKhau1.equals(matKhau2)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lưu thông tin vào database
        try {
            String sql = "INSERT INTO NguoiDung (hoTen, email, soDienThoai, ngaySinh, gioiTinh, matKhau) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = connect.connectMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                // Chuyển đổi ngày sinh từ String sang java.sql.Date
                java.util.Date utilDate = dateFormat.parse(ngaySinh);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                pstmt.setString(1, hoTen);
                pstmt.setString(2, email);
                pstmt.setString(3, soDienThoai);
                pstmt.setDate(4, sqlDate);
                pstmt.setString(5, gioiTinh);
                pstmt.setString(6, matKhau1);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    this.setVisible(false); // Ẩn form đăng ký sau khi đăng ký thành công
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng ký thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin đăng ký: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức kiểm tra email đã tồn tại
    private boolean kiemTraEmailTonTai(String email) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE email = ?";
        try (Connection conn = connect.connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra email!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Phương thức kiểm tra số điện thoại đã tồn tại
    private boolean kiemTraSoDienThoaiTonTai(String soDienThoai) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE soDienThoai = ?";
        try (Connection conn = connect.connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soDienThoai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
