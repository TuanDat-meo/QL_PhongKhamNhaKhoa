package view;

import javax.swing.*;

import controller.NguoiDungController;
import image.imageResize;
import model.NguoiDung;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailOrPhoneField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccountButton;
    private JLabel forgotPasswordLabel;
    private final String passwordPlaceholder = "Enter password";
    public LoginFrame() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Fixed: Use the class member variable instead of declaring a new local variable
        emailOrPhoneField = new JTextField(20);
        addPlaceholder(emailOrPhoneField, "Email address or phone number");
        panel.add(emailOrPhoneField, gbc);
     

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(20);
     // Enter trong emailOrPhoneField thì chuyển focus sang passwordField
        emailOrPhoneField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocusInWindow();
            }
        });

        // Enter trong passwordField thì tự động click nút login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        addPasswordPlaceholder(passwordField, passwordPlaceholder);
        ImageIcon eyeOpenIcon = new ImageIcon(getClass().getResource("/image/eyeOpen.jpg"));
        ImageIcon eyeClosedIcon = new ImageIcon(getClass().getResource("/image/eyeClose.jpg"));
        ImageIcon eyeOpenIconResize = imageResize.resizeImageIcon(eyeOpenIcon, 40, 30);
        ImageIcon eyeClosedIconResize = imageResize.resizeImageIcon(eyeClosedIcon, 40, 30);
        JButton toggleButton = new JButton(eyeClosedIconResize);
        toggleButton.setPreferredSize(new Dimension(40, 30));
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(passwordPlaceholder)) {
                    return; 
                }
                if (passwordField.getEchoChar() == '●') {
                    passwordField.setEchoChar((char) 0);
                    toggleButton.setIcon(eyeOpenIconResize);
                } else {
                    passwordField.setEchoChar('●');
                    toggleButton.setIcon(eyeClosedIconResize);
                }
            }
        });
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(toggleButton, BorderLayout.EAST);
        panel.add(passwordPanel, gbc);
        // Login Button
        gbc.gridwidth = 2;
        gbc.gridy = 2;
        loginButton = new JButton("Log in");
        loginButton.setBackground(new Color(24, 119, 242));
        loginButton.setForeground(Color.WHITE);
        panel.add(loginButton, gbc);
     // Trong LoginFrame.java, trong sự kiện của loginButton
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailOrPhoneField.getText().trim();
                String password = String.valueOf(passwordField.getPassword()).trim();
                if (email.equals("Email address or phone number")) {
                    email = "";
                }
                // Nếu password vẫn là placeholder thì coi như trống
                if (password.equals(passwordPlaceholder)) {
                    password = "";
                }
                // Kiểm tra nếu để trống
                if (email.isEmpty() && password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập email và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return; // Dừng lại nếu chưa nhập đủ
                }else if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập email!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }else if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                
                // Thay đổi phần này để lấy đối tượng NguoiDung từ controller
                NguoiDung loggedInUser = NguoiDungController.checkLoginAndGetUser(email, password);
                if (loggedInUser != null) {
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        new GiaoDienChinh(loggedInUser).setVisible(true);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    dispose(); // Đóng cửa sổ đăng nhập
                } else {
                    JOptionPane.showMessageDialog(null, "Email hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        forgotPasswordLabel = new JLabel("Forgotten password?");
        forgotPasswordLabel.setForeground(new Color(24, 119, 242));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Biểu tượng tay
        // Bắt sự kiện click để mở trang quên mật khẩu
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ForgotPasswordFrame(); // Mở cửa sổ quên mật khẩu
            }
        });
        panel.add(forgotPasswordLabel, gbc);
        // Create New Account Button
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        createAccountButton = new JButton("Create new account");
        createAccountButton.setBackground(new Color(66, 183, 42));
        createAccountButton.setForeground(Color.WHITE);
        // Xử lý sự kiện khi nhấn nút "Create new account"
        createAccountButton.addActionListener(e -> {
            RegisterFrame registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
        });
        panel.add(createAccountButton, gbc);
        add(panel);
        setVisible(true);
        panel.requestFocusInWindow();
    }
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
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
        field.setEchoChar((char) 0); // Hiển thị văn bản bình thường thay vì ●
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('●'); // Chuyển về chế độ ẩn mật khẩu
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0); // Hiện lại placeholder
                }
            }
        });
    }
    public static void main(String[] args) {
        new LoginFrame();
    }
}