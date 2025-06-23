package view;

import javax.swing.*;

import java.awt.*;
import java.sql.Timestamp;

import controller.OtpDAO;
import controller.NguoiDungController;
import model.NguoiDung;
import model.Otp;

public class ForgotPasswordFrame extends JFrame {
    private JTextField emailOrPhoneField;
    private JButton sendOTPButton;
    private LoginFrame loginFrame;

    public ForgotPasswordFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("Forgot Password");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nhập email hoặc số điện thoại
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        emailOrPhoneField = new JTextField("", 20);
        emailOrPhoneField.setForeground(Color.GRAY);
        emailOrPhoneField.setText("");
        emailOrPhoneField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailOrPhoneField.getText().equals("Enter your email or phone")) {
                    emailOrPhoneField.setText("");
                    emailOrPhoneField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (emailOrPhoneField.getText().isEmpty()) {
                    emailOrPhoneField.setForeground(Color.GRAY);
                    emailOrPhoneField.setText("Enter your email or phone");
                }
            }
        });
        panel.add(emailOrPhoneField, gbc);

        // Nút gửi OTP
        gbc.gridy = 1;
        sendOTPButton = new JButton("Send OTP");
        sendOTPButton.setBackground(new Color(24, 119, 242));
        sendOTPButton.setForeground(Color.WHITE);
        
        sendOTPButton.addActionListener(e -> {
            String input = emailOrPhoneField.getText().trim();
            NguoiDung user = new NguoiDungController().getNguoiDungByEmailOrPhone(input);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Email hoặc số điện thoại không tồn tại!");
                return;
            }
            // Sinh OTP 6 số
            String otp = String.format("%06d", (int)(Math.random() * 1000000));
            // Thời gian hết hạn 5 phút
            long now = System.currentTimeMillis();
            Timestamp expire = new Timestamp(now + 5 * 60 * 1000);
            Otp otpObj = new Otp();
            otpObj.setIdNguoiDung(user.getIdNguoiDung());
            otpObj.setMaOTP(otp);
            otpObj.setThoiGianHetHan(expire);
            otpObj.setDaSuDung(false);
            otpObj.setLoai("QuenMatKhau");
            OtpDAO.insertOtp(otpObj);
            System.out.println("[FORGOT PASSWORD] OTP for " + input + ": " + otp);
            JOptionPane.showMessageDialog(this, "OTP sent to your email/phone.");
            new EnterOTPFrame(this, user.getIdNguoiDung(), loginFrame); // Truyền loginFrame
            this.dispose();
        });

        panel.add(sendOTPButton, gbc);

        add(panel);
        setVisible(true);

        // Khi mở form, set placeholder nếu rỗng
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (emailOrPhoneField.getText().isEmpty()) {
                emailOrPhoneField.setForeground(Color.GRAY);
                emailOrPhoneField.setText("Enter your email or phone");
            }
            // Không tự động focus vào ô input, focus về frame
            this.requestFocusInWindow();
        });
    }
}
