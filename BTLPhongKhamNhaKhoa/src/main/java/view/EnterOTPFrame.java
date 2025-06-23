package view;

import javax.swing.*;

import java.awt.*;

import controller.OtpDAO;
import model.Otp;

public class EnterOTPFrame extends JFrame {
    private JTextField otpField;
    private JButton verifyButton;
    private ForgotPasswordFrame parentFrame;
    private int idNguoiDung;
    private LoginFrame loginFrame;

    public EnterOTPFrame(ForgotPasswordFrame parent, int idNguoiDung, LoginFrame loginFrame) {
        this.parentFrame = parent;
        this.idNguoiDung = idNguoiDung;
        this.loginFrame = loginFrame;
        setTitle("Enter OTP");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nhập OTP
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        otpField = new JTextField("", 10);
        otpField.setForeground(Color.GRAY);
        otpField.setText("");
        otpField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (otpField.getText().equals("Enter OTP")) {
                    otpField.setText("");
                    otpField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (otpField.getText().isEmpty()) {
                    otpField.setForeground(Color.GRAY);
                    otpField.setText("Enter OTP");
                }
            }
        });
        panel.add(otpField, gbc);

        // Nút xác minh
        gbc.gridy = 1;
        verifyButton = new JButton("Verify OTP");
        verifyButton.setBackground(new Color(24, 119, 242));
        verifyButton.setForeground(Color.WHITE);

        verifyButton.addActionListener(e -> {
            String otpInput = otpField.getText().trim();
            System.out.println("Check OTP: idNguoiDung=" + idNguoiDung + ", maOTP=" + otpInput + ", loai=QuenMatKhau");
            Otp otp = OtpDAO.getValidOtp(idNguoiDung, otpInput, "QuenMatKhau");
            if (otp != null) {
                JOptionPane.showMessageDialog(this, "OTP verified successfully.");
                OtpDAO.markOtpUsed(otp.getIdOTP());
                new ResetPasswordFrame(this, idNguoiDung, loginFrame); // Truyền loginFrame
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or expired OTP. Hãy kiểm tra lại mã OTP, idNguoiDung, loại OTP và thời gian hết hạn trong database!");
            }
        });

        panel.add(verifyButton, gbc);

        add(panel);
        setVisible(true);

        // Khi mở form, set placeholder nếu rỗng
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (otpField.getText().isEmpty()) {
                otpField.setForeground(Color.GRAY);
                otpField.setText("Enter OTP");
            }
            // Không tự động focus vào ô input, focus về frame
            this.requestFocusInWindow();
        });
    }
}
