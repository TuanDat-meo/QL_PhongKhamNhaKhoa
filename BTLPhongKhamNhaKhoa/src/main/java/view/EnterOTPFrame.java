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

    public EnterOTPFrame(ForgotPasswordFrame parent, int idNguoiDung) {
        this.parentFrame = parent;
        this.idNguoiDung = idNguoiDung;
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
        otpField = new JTextField("Enter OTP", 10);
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
                new ResetPasswordFrame(this, idNguoiDung); // Mở cửa sổ đổi mật khẩu, truyền idNguoiDung
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or expired OTP. Hãy kiểm tra lại mã OTP, idNguoiDung, loại OTP và thời gian hết hạn trong database!");
            }
        });

        panel.add(verifyButton, gbc);

        add(panel);
        setVisible(true);
    }
}
