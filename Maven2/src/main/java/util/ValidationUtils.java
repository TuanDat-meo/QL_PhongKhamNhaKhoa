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

public class ValidationUtils {
    private static final String HO_TEN_PATTERN = "^[\\p{L} .'-]+$";
    private static final String SO_DIEN_THOAI_PATTERN = "^(0|\\+84)\\d{9}$";
    private static final String CCCD_PATTERN = "^\\d{9}|\\d{12}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final String PHONE_PATTERN = "^(0|\\+84)(3|5|7|8|9)([0-9]{8})$";
    
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
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
    
    private static boolean validateField(String value, JComponent component, 
                                        String pattern, String emptyErrorMessage, 
                                        String invalidErrorMessage, String fieldName) {
        if (value == null || value.isEmpty()) {
            String message = emptyErrorMessage != null ? 
                    emptyErrorMessage : String.format(FIELD_EMPTY_ERROR, fieldName);
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(message);
            return false;
        }
        if (pattern != null && !value.matches(pattern)) {
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(invalidErrorMessage);
            return false;
        }
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
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
            }
            return false;
        }
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
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
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(NGAY_SINH_EMPTY_ERROR);
            return false;
        }
        
        if (ngaySinh.after(new Date())) {
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(NGAY_SINH_ERROR);
            return false;
        }
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
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
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
        return true;
    }
    
    public static boolean validateEmail(String email, JComponent component, JComponent errorLabel) {
        if (email == null || email.isEmpty()) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(EMAIL_EMPTY_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(EMAIL_EMPTY_ERROR);
            }
            return false;
        }
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(EMAIL_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(EMAIL_ERROR);
            }
            return false;
        }
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
        return true;
    }
    
    public static boolean validatePhoneNumber(String phone, JComponent component, JComponent errorLabel) {
        if (phone == null || phone.isEmpty()) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(PHONE_EMPTY_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(PHONE_EMPTY_ERROR);
            }
            return false;
        }
        if (!Pattern.matches(PHONE_PATTERN, phone)) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            if (errorLabel instanceof JTextField) {
                ((JTextField) errorLabel).setText(PHONE_ERROR);
            } else if (errorLabel instanceof JLabel) {
                ((JLabel) errorLabel).setText(PHONE_ERROR);
            }
            return false;
        }
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        if (errorLabel instanceof JTextField) {
            ((JTextField) errorLabel).setText("");
        } else if (errorLabel instanceof JLabel) {
            ((JLabel) errorLabel).setText("");
        }
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
}