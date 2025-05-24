package view;

import javax.swing.*;

import java.awt.*;

public class ResetPasswordFrame extends JFrame {
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton resetButton;
    private JCheckBox showPasswordCheckBox;
    private EnterOTPFrame parentFrame;

    public ResetPasswordFrame(EnterOTPFrame parent) {
        this.parentFrame = parent;
        setTitle("Reset Password");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Tạo panel chính
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Khoảng cách viền

        // Tiêu đề
        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15)); // Khoảng cách

        // Trường nhập mật khẩu mới
        newPasswordField = createPasswordField("New Password");
        panel.add(createLabeledField("New Password:", newPasswordField));

        // Trường nhập lại mật khẩu
        confirmPasswordField = createPasswordField("Confirm Password");
        panel.add(createLabeledField("Confirm Password:", confirmPasswordField));

        // Checkbox hiện mật khẩu
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheckBox.addActionListener(e -> {
            boolean isChecked = showPasswordCheckBox.isSelected();
            newPasswordField.setEchoChar(isChecked ? (char) 0 : '•');
            confirmPasswordField.setEchoChar(isChecked ? (char) 0 : '•');
        });
        panel.add(showPasswordCheckBox);
        panel.add(Box.createVerticalStrut(10));

        // Nút đặt lại mật khẩu
        resetButton = new JButton("Reset Password");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(24, 119, 242));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton.addActionListener(e -> {
            String newPassword = String.valueOf(newPasswordField.getPassword());
            String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

            if (newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully. Returning to login.");
                this.dispose();
                parentFrame.dispose(); // Đóng tất cả cửa sổ trước đó
            } else {
                JOptionPane.showMessageDialog(this, "Passwords do not match. Try again.");
            }
        });

        panel.add(resetButton);
        panel.add(Box.createVerticalStrut(15));

        add(panel);
        setVisible(true);
    }

    private JPanel createLabeledField(String labelText, JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0)); // Tạo khoảng cách
        return panel;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        return passwordField;
    }
}
