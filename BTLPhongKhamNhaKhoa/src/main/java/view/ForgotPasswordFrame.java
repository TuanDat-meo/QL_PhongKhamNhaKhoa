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

    public ForgotPasswordFrame() {
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
        emailOrPhoneField = new JTextField("Enter your email or phone", 20);
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
            new EnterOTPFrame(this, user.getIdNguoiDung()); // Truyền idNguoiDung sang bước nhập OTP
        });

        panel.add(sendOTPButton, gbc);

        add(panel);
        setVisible(true);
    }
}
