package util;

import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;

public class ValidationUtils {
    private static final String HO_TEN_PATTERN = "^[\\p{L} .'-]+$";
    private static final String SO_DIEN_THOAI_PATTERN = "^(0|\\+84)\\d{9}$";
    private static final String CCCD_PATTERN = "^\\d{9}|\\d{12}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_PATTERN = "^0\\d{9}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color BORDER_COLOR = new Color(226, 230, 234);
    
    private static final String FIELD_EMPTY_ERROR = "%s không được để trống";
    private static final String HO_TEN_ERROR = "Họ tên chỉ được chứa chữ cái";
    private static final String SO_DIEN_THOAI_ERROR = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0 hoặc +84";
    private static final String CCCD_ERROR = "CCCD phải có 9 hoặc 12 chữ số";
    private static final String DIA_CHI_ERROR = "Địa chỉ phải nhập đầy đủ";
    private static final String NGAY_SINH_ERROR = "Ngày sinh không hợp lệ";
    private static final String NGAY_SINH_EMPTY_ERROR = "Ngày sinh chưa được chọn";
    private static final String EMAIL_PHONE_ERROR = "Email hoặc số điện thoại không hợp lệ";
    private static final String EMAIL_PHONE_EMPTY_ERROR = "Vui lòng nhập email hoặc số điện thoại";
    private static final String PASSWORD_ERROR = "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự";
    private static final String PASSWORD_EMPTY_ERROR = "Vui lòng nhập mật khẩu";
    private static final String EMAIL_ERROR = "Email không hợp lệ";
    private static final String EMAIL_EMPTY_ERROR = "Email không được để trống";
    private static final String PHONE_ERROR = "Số điện thoại không hợp lệ";
    private static final String PHONE_EMPTY_ERROR = "Số điện thoại không được để trống";
    
    // Icons for validation state - These should be added to your project resources
    private static ImageIcon successIcon;
    private static ImageIcon errorIcon;
    
    static {
        try {
            // Initialize icons - replace with actual paths to your icons
            // These could be replaced with actual paths to icons in your project
            successIcon = new ImageIcon(ValidationUtils.class.getResource("/resources/icons/check.png"));
            errorIcon = new ImageIcon(ValidationUtils.class.getResource("/resources/icons/error.png"));
            
            // If icons can't be loaded, they will be null and won't be displayed
        } catch (Exception e) {
            // Handle icon loading errors silently
            successIcon = null;
            errorIcon = null;
        }
    }
    
    // New method to create validation feedback components
    public static JPanel createValidationPanel(JComponent inputComponent, String fieldName, boolean required) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        
        // Create label for field name
        JLabel nameLabel = new JLabel(fieldName + (required ? " *" : ""));
        nameLabel.setForeground(new Color(33, 37, 41)); // Dark text color
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Create label for validation state
        JLabel validationLabel = new JLabel();
        validationLabel.setPreferredSize(new Dimension(20, 20));
        
        // Add document listener to input component
        if (inputComponent instanceof JTextField) {
            JTextField textField = (JTextField) inputComponent;
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateValidationState();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateValidationState();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateValidationState();  
                }
                
                private void updateValidationState() {
                    // This will be implemented based on specific field validation
                    validationLabel.setIcon(null); // Reset icon during typing
                }
            });
        }
        
        panel.add(nameLabel);
        panel.add(inputComponent);
        panel.add(validationLabel);
        
        return panel;
    }
    
    private static void setValidationState(JComponent component, JLabel validationLabel, boolean isValid, String errorMessage) {
        Border normalBorder = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        );
        
        Border errorBorder = new CompoundBorder(
            new LineBorder(ERROR_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        );
        
        Border successBorder = new CompoundBorder(
            new LineBorder(SUCCESS_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        );
        
        if (isValid) {
            component.setBorder(successBorder);
            component.setToolTipText(null);
            if (validationLabel != null) {
                validationLabel.setIcon(successIcon);
                validationLabel.setToolTipText("Hợp lệ");
            }
        } else {
            component.setBorder(errorBorder);
            component.setToolTipText(errorMessage);
            if (validationLabel != null) {
                validationLabel.setIcon(errorIcon);
                validationLabel.setToolTipText(errorMessage);
            }
        }
    }
    
    // Method to attach real-time validation listeners
    public static void attachValidationListeners(JTextField textField, JLabel validationLabel, 
                                               ValidationFunction validationFunction, String fieldName) {
        
        // Add focus listener for validation on focus lost
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Optional: Clear error styling on focus gain
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                boolean isValid = validationFunction.validate(textField.getText().trim());
                String errorMessage = isValid ? null : getErrorMessage(textField);
                setValidationState(textField, validationLabel, isValid, errorMessage);
            }
        });
        
        // Add document listener for real-time validation feedback
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateOnChange();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateOnChange();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateOnChange();
            }
            
            private void validateOnChange() {
                boolean isValid = validationFunction.validate(textField.getText().trim());
                String errorMessage = isValid ? null : getErrorMessage(textField);
                setValidationState(textField, validationLabel, isValid, errorMessage);
            }
        });
    }
    
    // Functional interface for validation functions
    @FunctionalInterface
    public interface ValidationFunction {
        boolean validate(String input);
    }
    
    private static boolean validateField(String value, JComponent component, 
                                        String pattern, String emptyErrorMessage, 
                                        String invalidErrorMessage, String fieldName) {
        if (value == null || value.isEmpty()) {
            String message = emptyErrorMessage != null ? 
                    emptyErrorMessage : String.format(FIELD_EMPTY_ERROR, fieldName);
            component.setBorder(BorderFactory.createLineBorder(ERROR_COLOR));
            component.setToolTipText(message);
            return false;
        }
        if (pattern != null && !value.matches(pattern)) {
            component.setBorder(BorderFactory.createLineBorder(ERROR_COLOR));
            component.setToolTipText(invalidErrorMessage);
            return false;
        }
        component.setBorder(BorderFactory.createLineBorder(SUCCESS_COLOR));
        component.setToolTipText(null);
        return true;
    }
    
    private static boolean validateLoginField(String value, JComponent component, 
                                             String pattern, String emptyErrorMessage, 
                                             String invalidErrorMessage, JComponent errorLabel) {
        
        if (value == null || value.isEmpty()) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(emptyErrorMessage);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(emptyErrorMessage);
            }
            return false;
        }
        if (pattern != null && !Pattern.matches(pattern, value)) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(invalidErrorMessage);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(invalidErrorMessage);
            }
            return false;
        }
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SUCCESS_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
        return true;
    }
    
    public static boolean validateHoTen(String hoTen, JComponent component) {
        return validateField(hoTen, component, HO_TEN_PATTERN, 
                "Họ tên không được để trống", HO_TEN_ERROR, "Họ tên");
    }
    
    public static boolean validateSoDienThoai(String soDienThoai, JComponent component) {
        return validateField(soDienThoai, component, SO_DIEN_THOAI_PATTERN, 
                "Số điện thoại không được để trống", SO_DIEN_THOAI_ERROR, "Số điện thoại");
    }
    
    public static boolean validateCCCD(String cccd, JComponent component) {
        return validateField(cccd, component, CCCD_PATTERN, 
                "CCCD không được để trống", CCCD_ERROR, "CCCD");
    }
    
    public static boolean validateDiaChi(String diaChi, JComponent component) {
        return validateField(diaChi, component, null, 
                "Địa chỉ không được để trống", DIA_CHI_ERROR, "Địa chỉ");
    }
    
    public static boolean validateNgaySinh(Date ngaySinh, JComponent component) {
        if (ngaySinh == null) {
            component.setBorder(BorderFactory.createLineBorder(ERROR_COLOR));
            component.setToolTipText(NGAY_SINH_EMPTY_ERROR);
            return false;
        }
        
        if (ngaySinh.after(new Date())) {
            component.setBorder(BorderFactory.createLineBorder(ERROR_COLOR));
            component.setToolTipText(NGAY_SINH_ERROR);
            return false;
        }
        component.setBorder(BorderFactory.createLineBorder(SUCCESS_COLOR));
        component.setToolTipText(null);
        return true;
    }
    
    public static boolean validateEmailOrPhone(String input, JComponent component, JComponent errorLabel) {
        String emailPattern = EMAIL_PATTERN;
        String phonePattern = PHONE_PATTERN;
        
        String combinedPattern = "(" + emailPattern + ")|(" + phonePattern + ")";
        
        return validateLoginField(input, component, combinedPattern, 
                EMAIL_PHONE_EMPTY_ERROR, EMAIL_PHONE_ERROR, errorLabel);
    }
    
    public static boolean isValidEmailOrPhone(String input) {
        return Pattern.matches(EMAIL_PATTERN, input) || 
               Pattern.matches(PHONE_PATTERN, input);
    }
    
    public static boolean validatePassword(String password, JComponent component, JComponent errorLabel) {
        if (password == null || password.isEmpty()) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(PASSWORD_EMPTY_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(PASSWORD_EMPTY_ERROR);
            }
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(PASSWORD_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(PASSWORD_ERROR);
            }
            return false;
        }
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SUCCESS_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
        return true;
    }
    
    public static boolean validateEmail(String email, JComponent field, JLabel errorLabel) {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            setError(field, errorLabel, "Invalid email format");
            return false;
        }
        setSuccess(field, errorLabel);
        return true;
    }
    
    public static boolean validatePhoneNumber(String phone, JComponent field, JLabel errorLabel) {
        if (!Pattern.matches(PHONE_PATTERN, phone)) {
            setError(field, errorLabel, "Phone number must start with 0 and have 10 digits");
            return false;
        }
        setSuccess(field, errorLabel);
        return true;
    }
    
    public static boolean validatePassword(String password, JComponent field, JLabel errorLabel) {
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            setError(field, errorLabel, "Password must be at least 8 characters and contain uppercase, lowercase, number and special character");
            return false;
        }
        setSuccess(field, errorLabel);
        return true;
    }
    
    public static boolean validateRequired(String value, JComponent component, String fieldName) {
        return validateField(value, component, null, null, null, fieldName);
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("&", "&amp;");
    }
    
    public static String getErrorMessage(JComponent component) {
        return component.getToolTipText();
    }
    
    public static void clearValidationError(JComponent component) {
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        component.setToolTipText(null);
    }
    
    public static void clearValidationError(JComponent component, JComponent errorLabel) {
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
    }
    
    public static void clearLoginValidationError(JComponent component, JComponent errorLabel) {
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
    }
    
    public static void resetValidationErrors(JComponent... components) {
        for (JComponent component : components) {
            clearValidationError(component);
        }
    }
    
    // New methods for creating styled validation labels
    public static JLabel createValidationStatusLabel() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(24, 24));
        return label;
    }
    
    // New methods for creating error message labels
    public static JLabel createErrorMessageLabel() {
        JLabel label = new JLabel();
        label.setForeground(ERROR_COLOR);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        return label;
    }

    public static void setError(JComponent field, JLabel errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ERROR_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    public static void setSuccess(JComponent field, JLabel errorLabel) {
        errorLabel.setText("");
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SUCCESS_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    public static void clearValidationError(JComponent field, JLabel errorLabel) {
        errorLabel.setText("");
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    public static void validateConfirmPassword(String password, String confirmPassword, JComponent field, JLabel errorLabel) {
        if (!confirmPassword.equals(password)) {
            setError(field, errorLabel, "Passwords do not match");
            return;
        }
        setSuccess(field, errorLabel);
    }
}