package view;

import javax.swing.*;

import java.awt.*;

import controller.NguoiDungController;
import util.ValidationUtils;

public class ResetPasswordFrame extends JFrame {
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton resetButton;
    private JCheckBox showPasswordCheckBox;
    private EnterOTPFrame parentFrame;
    private int idNguoiDung;
    private JLabel newPasswordErrorLabel, confirmPasswordErrorLabel;

    public ResetPasswordFrame(EnterOTPFrame parent, int idNguoiDung) {
        this.parentFrame = parent;
        this.idNguoiDung = idNguoiDung;
        setTitle("Reset Password");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Tạo panel chính
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60)); // Khoảng cách viền lớn hơn

        // Tiêu đề
        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20)); // Khoảng cách

        // Trường nhập mật khẩu mới
        newPasswordField = createPasswordField("New Password");
        newPasswordErrorLabel = createErrorLabel();
        JPanel newPassPanel = createLabeledField("New Password:", newPasswordField, newPasswordErrorLabel);
        newPassPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(newPassPanel);
        panel.add(Box.createVerticalStrut(10));

        // Trường nhập lại mật khẩu
        confirmPasswordField = createPasswordField("Confirm Password");
        confirmPasswordErrorLabel = createErrorLabel();
        JPanel confirmPassPanel = createLabeledField("Confirm Password:", confirmPasswordField, confirmPasswordErrorLabel);
        confirmPassPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmPassPanel);
        panel.add(Box.createVerticalStrut(10));

        // Checkbox hiện mật khẩu
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheckBox.addActionListener(e -> {
            boolean isChecked = showPasswordCheckBox.isSelected();
            newPasswordField.setEchoChar(isChecked ? (char) 0 : '•');
            confirmPasswordField.setEchoChar(isChecked ? (char) 0 : '•');
        });
        panel.add(showPasswordCheckBox);
        panel.add(Box.createVerticalStrut(18));

        // Nút đặt lại mật khẩu
        resetButton = new JButton("Reset Password");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setBackground(new Color(24, 119, 242));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(resetButton);
        panel.add(Box.createVerticalStrut(15));

        add(panel);
        setVisible(true);

        // Xử lý validate realtime
        newPasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateNewPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateNewPassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateNewPassword(); }
        });
        confirmPasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
        });

        resetButton.addActionListener(e -> {
            boolean valid = true;
            String newPassword = String.valueOf(newPasswordField.getPassword());
            String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
            // Validate password pattern
            if (!ValidationUtils.validatePassword(newPassword, newPasswordField, newPasswordErrorLabel)) {
                valid = false;
            }
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordErrorLabel.setText("Passwords do not match");
                confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
                valid = false;
            } else {
                if (confirmPasswordErrorLabel.getText().equals("Passwords do not match"))
                    confirmPasswordErrorLabel.setText("");
                confirmPasswordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            }
            if (!valid) return;
            // Cập nhật mật khẩu vào DB
            boolean ok = new NguoiDungController().updatePassword(idNguoiDung, newPassword);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Password changed successfully. Returning to login.");
                this.dispose();
                parentFrame.dispose();
                new LoginFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.");
            }
        });
    }

    private JPanel createLabeledField(String labelText, JPasswordField field, JLabel errorLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label);
        panel.add(field);
        panel.add(errorLabel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        return passwordField;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.ITALIC, 12));
        return label;
    }

    private void validateNewPassword() {
        String newPassword = String.valueOf(newPasswordField.getPassword());
        if (!ValidationUtils.validatePassword(newPassword, newPasswordField, newPasswordErrorLabel)) {
            newPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        } else {
            newPasswordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        }
    }

    private void validateConfirmPassword() {
        String newPassword = String.valueOf(newPasswordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        if (!confirmPassword.equals(newPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match");
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        } else {
            confirmPasswordErrorLabel.setText("");
            confirmPasswordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        }
    }
}
