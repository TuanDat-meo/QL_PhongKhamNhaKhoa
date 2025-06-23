package view;

import javax.swing.*;
import javax.swing.border.*;

import controller.NguoiDungController;
import image.imageResize;
import model.NguoiDung;
import view.DoanhThuUI.NotificationType;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.net.URL;
import java.util.regex.Pattern;

public class LoginFrame extends JFrame {
    // UI components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccountButton;
    private JLabel forgotPasswordLabel;
    private JPanel mainPanel;
    private JButton togglePasswordButton;
    private JLabel emailErrorLabel;
    private JLabel passwordErrorLabel;
    
    // Constants
    private static final int RADIUS = 15;
    private static final String PASSWORD_PLACEHOLDER = "Enter password";
    private static final String EMAIL_PLACEHOLDER = "Email or phone number";
    
    // Email validation pattern - RFC 5322 compliant
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    
    // Phone number pattern - simple version for Vietnamese phone numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(0|\\+84)(3|5|7|8|9)([0-9]{8})$");
    
    // Password requirements
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    // Colors
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color PRIMARY_COLOR = new Color(24, 119, 242);
    private static final Color PRIMARY_DARK_COLOR = new Color(12, 80, 170);
    private static final Color ACCENT_COLOR = new Color(66, 183, 42);
    private static final Color ACCENT_DARK_COLOR = new Color(36, 140, 22);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color LIGHT_TEXT_COLOR = new Color(110, 119, 128);
    private static final Color FIELD_BACKGROUND = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(226, 230, 234);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);    
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font ERROR_FONT = new Font("Segoe UI", Font.ITALIC, 12);
    
    public LoginFrame() {
        setupWindow();
        initializeComponents();
        setupCardLayout();
        setupActions();
        setupValidation();
        setVisible(true);
        mainPanel.requestFocusInWindow();
    }    
    private void setupWindow() {
        setTitle("Login");
        setSize(380, 580); // Increased height to accommodate error messages
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false); 
    }    
    private void initializeComponents() {
        // Main panel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        
        JLabel titleLabel = new JLabel("LOG IN", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        emailField = new RoundedTextField(RADIUS);
        emailField.setFont(FIELD_FONT);
        emailField.setForeground(LIGHT_TEXT_COLOR);
        emailField.setText(EMAIL_PLACEHOLDER);
        emailField.setBackground(FIELD_BACKGROUND);
        emailField.putClientProperty("JComponent.roundRect", true);
        emailField.putClientProperty("JTextField.placeholderText", EMAIL_PLACEHOLDER);
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailField.getText().equals(EMAIL_PLACEHOLDER)) {
                    emailField.setText("");
                    emailField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText(EMAIL_PLACEHOLDER);
                    emailField.setForeground(LIGHT_TEXT_COLOR);
                }
            }
        });
        passwordField = new RoundedPasswordField(RADIUS);
        passwordField.setFont(FIELD_FONT);
        passwordField.setForeground(LIGHT_TEXT_COLOR);
        passwordField.setText(PASSWORD_PLACEHOLDER);
        passwordField.setEchoChar((char) 0); // Show the placeholder text
        passwordField.setBackground(FIELD_BACKGROUND);
        passwordField.putClientProperty("JComponent.roundRect", true);
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    passwordField.setText("");
                    passwordField.setForeground(TEXT_COLOR);
                    passwordField.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(PASSWORD_PLACEHOLDER);
                    passwordField.setForeground(LIGHT_TEXT_COLOR);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
        
        emailErrorLabel = new JLabel("");
        emailErrorLabel.setFont(ERROR_FONT);
        emailErrorLabel.setForeground(ERROR_COLOR);
        
        passwordErrorLabel = new JLabel("");
        passwordErrorLabel.setFont(ERROR_FONT);
        passwordErrorLabel.setForeground(ERROR_COLOR);
        
        loginButton = createButton("Log in", PRIMARY_COLOR, PRIMARY_DARK_COLOR);
        createAccountButton = createButton("Create new account", ACCENT_COLOR, ACCENT_DARK_COLOR);
        
        forgotPasswordLabel = new JLabel("Forgotten password?", SwingConstants.CENTER);
        forgotPasswordLabel.setFont(SMALL_FONT);
        forgotPasswordLabel.setForeground(PRIMARY_COLOR);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        
        togglePasswordButton = new JButton() {
            private boolean eyeOpen = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BACKGROUND);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, RADIUS, RADIUS));
                drawEyeIcon(g2, getWidth()/2, getHeight()/2, eyeOpen);
                g2.dispose();
            }
            private void drawEyeIcon(Graphics2D g, int x, int y, boolean open) {
                int eyeWidth = 20;
                int eyeHeight = 12;
                g.setColor(TEXT_COLOR);
                g.setStroke(new BasicStroke(2));
                g.drawOval(x - eyeWidth/2, y - eyeHeight/2, eyeWidth, eyeHeight);
                g.fillOval(x - 3, y - 3, 6, 6);
                if (!open) {
                    g.drawLine(x - eyeWidth/2 - 3, y - eyeHeight/2 - 3, 
                               x + eyeWidth/2 + 3, y + eyeHeight/2 + 3);
                }
            }
            public void setEyeOpen(boolean open) {
                this.eyeOpen = open;
                repaint();
            }
            public boolean isEyeOpen() {
                return eyeOpen;
            }
        };
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordButton.setToolTipText("Hiển thị/Ẩn mật khẩu");
        
        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    return; 
                }                
                if (passwordField.getEchoChar() == '●') {
                    passwordField.setEchoChar((char) 0);
                    ((JButton)e.getSource()).putClientProperty("eyeOpen", true);
                    if (togglePasswordButton instanceof JButton) {
                        try {
                            JButton button = (JButton) togglePasswordButton;
                            if (button.getClass().getMethod("setEyeOpen", boolean.class) != null) {
                                button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, true);
                            }
                        } catch (Exception ex) {                            
                        }
                    }
                    togglePasswordButton.setToolTipText("Ẩn mật khẩu");
                } else {
                    passwordField.setEchoChar('●');
                    ((JButton)e.getSource()).putClientProperty("eyeOpen", false);
                    if (togglePasswordButton instanceof JButton) {
                        try {
                            JButton button = (JButton) togglePasswordButton;
                            if (button.getClass().getMethod("setEyeOpen", boolean.class) != null) {
                                button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, false);
                            }
                        } catch (Exception ex) {
                        }
                    }
                    togglePasswordButton.setToolTipText("Hiển thị mật khẩu");
                }
                togglePasswordButton.repaint();
            }
        });
    }    
    private JButton createTextEyeButton() {
        return new JButton();
    }    
    private void setupCardLayout() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        int panelWidth = 380; // Giá trị cố định bằng với setSize của JFrame
        int componentWidth = 280;
        int x = (panelWidth - componentWidth) / 2;
        int componentHeight = 42;
        int titleHeight = 38;
        int gapTitleToInput = 24;
        int y = 50; // Đặt LOG IN cách top 50px, có thể chỉnh lại cho vừa ý
        JLabel titleLabel = new JLabel("LOG IN", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBounds(0, y, panelWidth, titleHeight);
        mainPanel.add(titleLabel);
        y += titleHeight + gapTitleToInput;
        emailField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        emailField.setBounds(x, y, componentWidth, componentHeight);
        mainPanel.add(emailField);
        y += componentHeight + 2;
        emailErrorLabel.setBounds(x, y, componentWidth, 18);
        mainPanel.add(emailErrorLabel);
        y += 22;
        int toggleButtonWidth = 35;
        int passwordFieldWidth = componentWidth - toggleButtonWidth;
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setBounds(x, y, passwordFieldWidth, componentHeight);
        togglePasswordButton.setBounds(x + passwordFieldWidth, y, toggleButtonWidth, componentHeight);
        mainPanel.add(passwordField);
        mainPanel.add(togglePasswordButton);
        y += componentHeight + 2;
        passwordErrorLabel.setBounds(x, y, componentWidth, 18);
        mainPanel.add(passwordErrorLabel);
        y += 28;
        loginButton.setBounds(x, y, componentWidth, componentHeight);
        mainPanel.add(loginButton);
        y += componentHeight + 8;
        forgotPasswordLabel.setBounds(x, y, componentWidth, 18);
        mainPanel.add(forgotPasswordLabel);
        y += 28;
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setBounds(x, y, componentWidth, 1);
        mainPanel.add(separator);
        y += 18;
        createAccountButton.setBounds(x, y, componentWidth, componentHeight);
        mainPanel.add(createAccountButton);
        mainPanel.revalidate();
        mainPanel.repaint();
        setContentPane(mainPanel);
    }    
    private void setupValidation() {
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateEmailField();
            }            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateEmailField();
            }            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateEmailField();
            }
        });        
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
            }            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
            }            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
            }
        });
    }    
    private void validateEmailField() {
        String email = emailField.getText().trim();
        if (!email.equals(EMAIL_PLACEHOLDER) && !email.isEmpty()) {
            if (!isValidEmailOrPhone(email)) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                emailErrorLabel.setText("Email hoặc số điện thoại không hợp lệ");
            } else {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                emailErrorLabel.setText("");
            }
        } else {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            emailErrorLabel.setText("");
        }
    }    
    private void validatePasswordField() {
        String password = String.valueOf(passwordField.getPassword());
        if (!password.equals(PASSWORD_PLACEHOLDER) && !password.isEmpty()) {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                passwordErrorLabel.setText("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
            } else {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                passwordErrorLabel.setText("");
            }
        } else {
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            passwordErrorLabel.setText("");
        }
    }    
    private boolean isValidEmailOrPhone(String input) {
        return EMAIL_PATTERN.matcher(input).matches() || 
               PHONE_PATTERN.matcher(input).matches();
    }    
    private void setupActions() {
        emailField.addActionListener(e -> passwordField.requestFocusInWindow());
        
        passwordField.addActionListener(e -> loginButton.doClick());
        
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setText("<html><u>Forgotten password?</u></html>");
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setText("Forgotten password?");
            }            
            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordFrame();
            }
        });
        createAccountButton.addActionListener(e -> {
            this.setVisible(false);
            new RegisterFrame(this);
        });        
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = String.valueOf(passwordField.getPassword()).trim();
            
            if (email.equals(EMAIL_PLACEHOLDER)) {
                email = "";
            }
            if (password.equals(PASSWORD_PLACEHOLDER)) {
                password = "";
            }
            
            boolean isValid = true;
            
            if (email.isEmpty()) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                emailErrorLabel.setText("Vui lòng nhập email hoặc số điện thoại");
                isValid = false;
            } else if (!isValidEmailOrPhone(email)) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                emailErrorLabel.setText("Email hoặc số điện thoại không hợp lệ");
                isValid = false;
            }            
            if (password.isEmpty()) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                passwordErrorLabel.setText("Vui lòng nhập mật khẩu");
                isValid = false;
            } else if (password.length() < MIN_PASSWORD_LENGTH) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                passwordErrorLabel.setText("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
                isValid = false;
            }            
            if (!isValid) {
                return; 
            }
            
            NguoiDung loggedInUser = NguoiDungController.checkLoginAndGetUser(email, password);
            if (loggedInUser != null) {
                showNotification("Đăng nhập thành công!", NotificationType.SUCCESS);
                
                // Kiểm tra vai trò người dùng
                String vaiTro = loggedInUser.getVaiTro();
                
                // Nếu vai trò là null hoặc "khach_hang", mở GiaoDienKhachHang
                if (vaiTro == null || vaiTro.equals("khach_hang")) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            new GiaoDienKhachHang(loggedInUser).setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showNotification("Lỗi khởi tạo giao diện: " + ex.getMessage(), NotificationType.ERROR);
                        }
                    });
                } else {
                    // Vai trò khác (nhân viên, quản trị viên) mở GiaoDienChinh
                    try {
                        new GiaoDienChinh(loggedInUser).setVisible(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showNotification("Lỗi khởi tạo giao diện: " + ex.getMessage(), NotificationType.ERROR);
                    }
                }
                dispose();
            } else {
                showNotification("Email hoặc mật khẩu không đúng!", NotificationType.WARNING);
            }
        });
    }    
    private void showNotification(String message, NotificationType type) {
        JDialog toastDialog = new JDialog();
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);

        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(type.color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel titleLabel = new JLabel(type.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        toastPanel.add(titleLabel);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);
        toastDialog.add(toastPanel);
        toastDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
            screenSize.width - toastDialog.getWidth() - 20,
            screenSize.height - toastDialog.getHeight() - 60
        );
        toastDialog.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                toastDialog.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }    
    public enum NotificationType {
        SUCCESS(new Color(86, 156, 104), "Thành công"),
        WARNING(new Color(237, 187, 85), "Cảnh báo"),
        ERROR(new Color(192, 80, 77), "Lỗi");        
        private final Color color;
        private final String title;        
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }    
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setForeground(LIGHT_TEXT_COLOR);
        field.setText(placeholder);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(LIGHT_TEXT_COLOR);
                }
            }
        });
        return field;
    }    
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(FIELD_FONT);
        field.setForeground(LIGHT_TEXT_COLOR);
        field.setText(placeholder);
        field.setEchoChar((char) 0); // Show the placeholder text
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.putClientProperty("JComponent.roundRect", true);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                    field.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(LIGHT_TEXT_COLOR);
                    field.setEchoChar((char) 0);
                }
            }
        });
        return field;
    }    
    private JButton createButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(hoverColor);
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }    
    private void showErrorMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Lỗi");
        dialog.setBackground(BACKGROUND_COLOR);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }    
    private void showSuccessMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog("Thành công");
        
        dialog.setBackground(BACKGROUND_COLOR);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.put("TextField.arc", RADIUS);
            UIManager.put("Button.arc", RADIUS);
            UIManager.put("Component.arc", RADIUS);
            UIManager.put("TextComponent.arc", RADIUS);
            NguoiDungController.migrateAllPlaintextPasswords();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}

// Thêm class RoundedTextField và RoundedPasswordField để bo góc lớn cho input
class RoundedTextField extends JTextField {
    private int radius;
    public RoundedTextField(int radius) {
        super();
        setOpaque(false);
        this.radius = radius;
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(new Color(226, 230, 234)); // BORDER_COLOR
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
    @Override
    public void setBorder(Border border) {
        // Không set border để tránh bị đè lên custom
    }
    @Override
    public Insets getInsets() {
        return new Insets(10, 15, 10, 15);
    }
}
class RoundedPasswordField extends JPasswordField {
    private int radius;
    public RoundedPasswordField(int radius) {
        super();
        setOpaque(false);
        this.radius = radius;
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(new Color(226, 230, 234)); // BORDER_COLOR
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
    @Override
    public void setBorder(Border border) {
        // Không set border để tránh bị đè lên custom
    }
    @Override
    public Insets getInsets() {
        return new Insets(10, 15, 10, 15);
    }
}