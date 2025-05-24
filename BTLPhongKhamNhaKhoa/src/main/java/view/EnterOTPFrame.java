package view;

import javax.swing.*;

import java.awt.*;

public class EnterOTPFrame extends JFrame {
    private JTextField otpField;
    private JButton verifyButton;
    private ForgotPasswordFrame parentFrame;

    public EnterOTPFrame(ForgotPasswordFrame parent) {
        this.parentFrame = parent;
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
            if (otpField.getText().equals("123456")) { // Giả lập OTP
                JOptionPane.showMessageDialog(this, "OTP verified successfully.");
                new ResetPasswordFrame(this); // Mở cửa sổ đổi mật khẩu
            } else {
                JOptionPane.showMessageDialog(this, "Invalid OTP. Try again.");
            }
        });

        panel.add(verifyButton, gbc);

        add(panel);
        setVisible(true);
    }
}
