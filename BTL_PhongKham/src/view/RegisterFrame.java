package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.LineBorder;
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

        if (!matKhau1.equals(matKhau2)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
