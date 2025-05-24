package view;

import javax.swing.*;

import java.awt.*;

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
            JOptionPane.showMessageDialog(this, "OTP sent to your email/phone.");
            new EnterOTPFrame(this); // Mở cửa sổ nhập OTP
        });

        panel.add(sendOTPButton, gbc);

        add(panel);
        setVisible(true);
    }
}
