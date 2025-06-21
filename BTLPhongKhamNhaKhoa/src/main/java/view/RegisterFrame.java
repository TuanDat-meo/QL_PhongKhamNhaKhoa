package view;

import javax.swing.*;
import javax.swing.border.*;

import controller.NguoiDungController;
import image.imageResize;
import model.NguoiDung;
import util.ValidationUtils;
import view.DoanhThuUI.NotificationType;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {
    // UI components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton, backToLoginButton;
    private JPanel mainPanel;
    private JButton togglePasswordButton;
    private JButton toggleConfirmPasswordButton;
    private JLabel nameErrorLabel;
    private JLabel emailErrorLabel;
    private JLabel phoneErrorLabel;
    private JLabel passwordErrorLabel;
    private JLabel confirmPasswordErrorLabel;
    
    // Constants
    private static final int RADIUS = 15;
    private static final String NAME_PLACEHOLDER = "Full name";
    private static final String EMAIL_PLACEHOLDER = "Email address";
    private static final String PHONE_PLACEHOLDER = "Phone number";
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private static final String CONFIRM_PASSWORD_PLACEHOLDER = "Confirm password";
    
    // Colors
    private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);
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
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font ERROR_FONT = new Font("Segoe UI", Font.ITALIC, 11);
    
    public RegisterFrame() {
        setupWindow();
        initializeComponents();
        setupActions();
        setupValidation();
        setVisible(true);
        mainPanel.requestFocusInWindow();
    }
    
    private void setupWindow() {
        setTitle("Register");
        setSize(400, 700); // Larger height for more fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("It's quick and easy.", SwingConstants.CENTER);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        nameField = createTextField(NAME_PLACEHOLDER);
        emailField = createTextField(EMAIL_PLACEHOLDER);
        phoneField = createTextField(PHONE_PLACEHOLDER);
        passwordField = createPasswordField(PASSWORD_PLACEHOLDER);
        confirmPasswordField = createPasswordField(CONFIRM_PASSWORD_PLACEHOLDER);
        
        nameErrorLabel = new JLabel("");
        nameErrorLabel.setFont(ERROR_FONT);
        nameErrorLabel.setForeground(ERROR_COLOR);
        
        emailErrorLabel = new JLabel("");
        emailErrorLabel.setFont(ERROR_FONT);
        emailErrorLabel.setForeground(ERROR_COLOR);
        
        phoneErrorLabel = new JLabel("");
        phoneErrorLabel.setFont(ERROR_FONT);
        phoneErrorLabel.setForeground(ERROR_COLOR);
        
        passwordErrorLabel = new JLabel("");
        passwordErrorLabel.setFont(ERROR_FONT);
        passwordErrorLabel.setForeground(ERROR_COLOR);
        
        confirmPasswordErrorLabel = new JLabel("");
        confirmPasswordErrorLabel.setFont(ERROR_FONT);
        confirmPasswordErrorLabel.setForeground(ERROR_COLOR);
        
        registerButton = createButton("Sign Up", ACCENT_COLOR, ACCENT_DARK_COLOR);
        backToLoginButton = new JButton("Already have an account?") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw the text
                g2d.setColor(PRIMARY_COLOR);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                // Draw underline
                g2d.drawLine(x, y + 2, x + (int) r.getWidth(), y + 2);
                
                g2d.dispose();
            }
        };
        backToLoginButton.setFont(BUTTON_FONT);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.setForeground(PRIMARY_COLOR);
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        
        int leftMargin = 60;
        int centerWidth = 280;
        int componentHeight = 40;
        
        titleLabel.setBounds((getWidth() - centerWidth)/2, 50, centerWidth, 40);
        subtitleLabel.setBounds((getWidth() - centerWidth)/2, 90, centerWidth, 25);
        
        separator.setBounds(leftMargin, 130, centerWidth, 1);
        
        nameField.setBounds(leftMargin, 150, centerWidth, componentHeight);
        nameErrorLabel.setBounds(leftMargin + 5, 190, centerWidth, 15);
        
        emailField.setBounds(leftMargin, 210, centerWidth, componentHeight);
        emailErrorLabel.setBounds(leftMargin + 5, 250, centerWidth, 15);
        
        phoneField.setBounds(leftMargin, 270, centerWidth, componentHeight);
        phoneErrorLabel.setBounds(leftMargin + 5, 310, centerWidth, 15);
        
        int passwordFieldWidth = 230;
        int eyeButtonWidth = centerWidth - passwordFieldWidth;
        
        passwordField.setBounds(leftMargin, 330, passwordFieldWidth, componentHeight);
        passwordErrorLabel.setBounds(leftMargin + 5, 370, centerWidth, 15);
        
        confirmPasswordField.setBounds(leftMargin, 390, passwordFieldWidth, componentHeight);
        confirmPasswordErrorLabel.setBounds(leftMargin + 5, 430, centerWidth, 15);
        
        registerButton.setBounds(leftMargin, 465, centerWidth, componentHeight);
        
        JSeparator bottomSeparator = new JSeparator();
        bottomSeparator.setForeground(BORDER_COLOR);
        bottomSeparator.setBounds(leftMargin, 525, centerWidth, 1);
        
        backToLoginButton.setBounds(leftMargin, 550, centerWidth, componentHeight);
        
        mainPanel.add(titleLabel);
        mainPanel.add(subtitleLabel);
        mainPanel.add(separator);
        mainPanel.add(nameField);
        mainPanel.add(nameErrorLabel);
        mainPanel.add(emailField);
        mainPanel.add(emailErrorLabel);
        mainPanel.add(phoneField);
        mainPanel.add(phoneErrorLabel);
        mainPanel.add(passwordField);
        mainPanel.add(passwordErrorLabel);
        mainPanel.add(confirmPasswordField);
        mainPanel.add(confirmPasswordErrorLabel);
        mainPanel.add(registerButton);
        mainPanel.add(bottomSeparator);
        mainPanel.add(backToLoginButton);
        
        // Toggle password buttons
        togglePasswordButton = createEyeButton();
        togglePasswordButton.setBounds(leftMargin + passwordFieldWidth, 330, eyeButtonWidth, componentHeight);
        togglePasswordButton.setToolTipText("Show/Hide password");
        
        toggleConfirmPasswordButton = createEyeButton();
        toggleConfirmPasswordButton.setBounds(leftMargin + passwordFieldWidth, 390, eyeButtonWidth, componentHeight);
        toggleConfirmPasswordButton.setToolTipText("Show/Hide password");
        
        mainPanel.add(togglePasswordButton);
        mainPanel.add(toggleConfirmPasswordButton);
        
        setContentPane(mainPanel);
    }
    
    private JButton createEyeButton() {
        JButton button = new JButton() {
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
        
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupActions() {
        // Set tab order
        nameField.addActionListener(e -> emailField.requestFocusInWindow());
        emailField.addActionListener(e -> phoneField.requestFocusInWindow());
        phoneField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocusInWindow());
        confirmPasswordField.addActionListener(e -> registerButton.doClick());
        
        // Toggle password visibility
        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pass = String.valueOf(passwordField.getPassword());
                if (pass.equals(PASSWORD_PLACEHOLDER)) {
                    return;
                }
                
                JButton button = (JButton) e.getSource();
                boolean eyeOpen = false;
                try {
                    eyeOpen = (boolean) button.getClass().getMethod("isEyeOpen").invoke(button);
                } catch (Exception ex) {
                    // Fallback if reflection fails
                    eyeOpen = passwordField.getEchoChar() == 0;
                }
                
                if (!eyeOpen) {
                    passwordField.setEchoChar((char) 0);
                    try {
                        button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, true);
                    } catch (Exception ex) {
                        // Ignore if reflection fails
                    }
                    button.setToolTipText("Hide password");
                } else {
                    passwordField.setEchoChar('●');
                    try {
                        button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, false);
                    } catch (Exception ex) {
                        // Ignore if reflection fails
                    }
                    button.setToolTipText("Show password");
                }
                button.repaint();
            }
        });
        
        toggleConfirmPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pass = String.valueOf(confirmPasswordField.getPassword());
                if (pass.equals(CONFIRM_PASSWORD_PLACEHOLDER)) {
                    return;
                }
                
                JButton button = (JButton) e.getSource();
                boolean eyeOpen = false;
                try {
                    eyeOpen = (boolean) button.getClass().getMethod("isEyeOpen").invoke(button);
                } catch (Exception ex) {
                    // Fallback if reflection fails
                    eyeOpen = confirmPasswordField.getEchoChar() == 0;
                }
                
                if (!eyeOpen) {
                    confirmPasswordField.setEchoChar((char) 0);
                    try {
                        button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, true);
                    } catch (Exception ex) {
                        // Ignore if reflection fails
                    }
                    button.setToolTipText("Hide password");
                } else {
                    confirmPasswordField.setEchoChar('●');
                    try {
                        button.getClass().getMethod("setEyeOpen", boolean.class).invoke(button, false);
                    } catch (Exception ex) {
                        // Ignore if reflection fails
                    }
                    button.setToolTipText("Show password");
                }
                button.repaint();
            }
        });
        
        // Registration action
        registerButton.addActionListener(e -> {
            if (validateAllFields()) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = String.valueOf(passwordField.getPassword()).trim();
                
                // Check if the email already exists
                if (NguoiDungController.isEmailExists(email)) {
                    showNotification("Email already registered!", NotificationType.WARNING);
                    return;
                }
                
                if (NguoiDungController.isPhoneExists(phone)) {
                    showNotification("Phone number already registered!", NotificationType.WARNING);
                    return;
                }
                boolean success = NguoiDungController.registerUser(name, email, phone, password);
                
                if (success) {
                    showNotification("Registration successful!", NotificationType.SUCCESS);
                    // Open login frame after successful registration
                    SwingUtilities.invokeLater(() -> {
                        new LoginFrame();
                        dispose(); // Close registration window
                    });
                } else {
                    showNotification("Registration failed. Please try again.", NotificationType.ERROR);
                }
            }
        });
        
        backToLoginButton.addActionListener(e -> {
            // Open login frame
            SwingUtilities.invokeLater(() -> {
                new LoginFrame();
                dispose(); // Close registration window
            });
        });
    }
    
    private void setupValidation() {
        // Validate name field
        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateNameField();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateNameField();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateNameField();
            }
        });
        
        // Validate email field
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
        
        // Validate phone field
        phoneField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validatePhoneField();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validatePhoneField();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validatePhoneField();
            }
        });
        
        // Validate password field
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
                validateConfirmPasswordField(); // Check match when password changes
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
                validateConfirmPasswordField(); // Check match when password changes
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validatePasswordField();
                validateConfirmPasswordField(); // Check match when password changes
            }
        });
        
        // Validate confirm password field
        confirmPasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateConfirmPasswordField();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateConfirmPasswordField();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateConfirmPasswordField();
            }
        });
    }
    
    private void validateNameField() {
        String name = nameField.getText().trim();
        if (!name.equals(NAME_PLACEHOLDER) && !name.isEmpty()) {
            if (name.length() < 2 || name.length() > 50) {
                setError(nameField, nameErrorLabel, "Name must be between 2 and 50 characters");
                return;
            }
            
            if (!Pattern.matches("^[\\p{L} .'-]+$", name)) {
                setError(nameField, nameErrorLabel, "Name can only contain letters and spaces");
                return;
            }
            
            clearError(nameField, nameErrorLabel);
        } else {
            clearError(nameField, nameErrorLabel);
        }
    }
    
    private void validateEmailField() {
        String email = emailField.getText().trim();
        if (!email.equals(EMAIL_PLACEHOLDER) && !email.isEmpty()) {
            ValidationUtils.validateEmail(email, emailField, emailErrorLabel);
        } else {
            ValidationUtils.clearValidationError(emailField, emailErrorLabel);
        }
    }
    
    private void validatePhoneField() {
        String phone = phoneField.getText().trim();
        if (!phone.equals(PHONE_PLACEHOLDER) && !phone.isEmpty()) {
            ValidationUtils.validatePhoneNumber(phone, phoneField, phoneErrorLabel);
        } else {
            ValidationUtils.clearValidationError(phoneField, phoneErrorLabel);
        }
    }
    
    private void validatePasswordField() {
        String password = String.valueOf(passwordField.getPassword());
        if (!password.equals(PASSWORD_PLACEHOLDER) && !password.isEmpty()) {
            ValidationUtils.validatePassword(password, passwordField, passwordErrorLabel);
        } else {
            ValidationUtils.clearValidationError(passwordField, passwordErrorLabel);
        }
    }
    
    private void validateConfirmPasswordField() {
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        String password = String.valueOf(passwordField.getPassword());
        
        if (!confirmPassword.equals(CONFIRM_PASSWORD_PLACEHOLDER) && !confirmPassword.isEmpty()) {
            ValidationUtils.validateConfirmPassword(password, confirmPassword, confirmPasswordField, confirmPasswordErrorLabel);
        } else {
            ValidationUtils.clearValidationError(confirmPasswordField, confirmPasswordErrorLabel);
        }
    }
    
    private boolean validateAllFields() {
        boolean isValid = true;
        
        // Clear placeholder texts for validation
        String name = nameField.getText().trim();
        if (name.equals(NAME_PLACEHOLDER)) {
            nameField.setText("");
        }
        
        String email = emailField.getText().trim();
        if (email.equals(EMAIL_PLACEHOLDER)) {
            emailField.setText("");
        }
        
        String phone = phoneField.getText().trim();
        if (phone.equals(PHONE_PLACEHOLDER)) {
            phoneField.setText("");
        }
        
        String password = String.valueOf(passwordField.getPassword());
        if (password.equals(PASSWORD_PLACEHOLDER)) {
            passwordField.setText("");
        }
        
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        if (confirmPassword.equals(CONFIRM_PASSWORD_PLACEHOLDER)) {
            confirmPasswordField.setText("");
        }
        
        // Validate all fields
        validateNameField();
        validateEmailField();
        validatePhoneField();
        validatePasswordField();
        validateConfirmPasswordField();
        
        // Check for errors
        if (!nameErrorLabel.getText().isEmpty()) isValid = false;
        if (!emailErrorLabel.getText().isEmpty()) isValid = false;
        if (!phoneErrorLabel.getText().isEmpty()) isValid = false;
        if (!passwordErrorLabel.getText().isEmpty()) isValid = false;
        if (!confirmPasswordErrorLabel.getText().isEmpty()) isValid = false;
        
        // Check for empty fields
        if (nameField.getText().trim().isEmpty()) {
            setError(nameField, nameErrorLabel, "Name is required");
            isValid = false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            setError(emailField, emailErrorLabel, "Email is required");
            isValid = false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            setError(phoneField, phoneErrorLabel, "Phone number is required");
            isValid = false;
        }
        
        if (String.valueOf(passwordField.getPassword()).trim().isEmpty()) {
            setError(passwordField, passwordErrorLabel, "Password is required");
            isValid = false;
        }
        
        if (String.valueOf(confirmPasswordField.getPassword()).trim().isEmpty()) {
            setError(confirmPasswordField, confirmPasswordErrorLabel, "Please confirm your password");
            isValid = false;
        }
        
        // Restore placeholder texts if fields are empty
        if (nameField.getText().isEmpty()) {
            nameField.setText(NAME_PLACEHOLDER);
            nameField.setForeground(LIGHT_TEXT_COLOR);
        }
        
        if (emailField.getText().isEmpty()) {
            emailField.setText(EMAIL_PLACEHOLDER);
            emailField.setForeground(LIGHT_TEXT_COLOR);
        }
        
        if (phoneField.getText().isEmpty()) {
            phoneField.setText(PHONE_PLACEHOLDER);
            phoneField.setForeground(LIGHT_TEXT_COLOR);
        }
        
        if (String.valueOf(passwordField.getPassword()).isEmpty()) {
            passwordField.setText(PASSWORD_PLACEHOLDER);
            passwordField.setEchoChar((char) 0);
            passwordField.setForeground(LIGHT_TEXT_COLOR);
        }
        
        if (String.valueOf(confirmPasswordField.getPassword()).isEmpty()) {
            confirmPasswordField.setText(CONFIRM_PASSWORD_PLACEHOLDER);
            confirmPasswordField.setEchoChar((char) 0);
            confirmPasswordField.setForeground(LIGHT_TEXT_COLOR);
        }
        
        return isValid;
    }
    
    private void setError(JComponent field, JLabel errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ERROR_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    private void clearError(JComponent field, JLabel errorLabel) {
        errorLabel.setText("");
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
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
}