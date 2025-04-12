package util;

import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;

public class ValidationUtils {
    // Constant patterns
    private static final String HO_TEN_PATTERN = "^[\\p{L} .'-]+$";
    private static final String SO_DIEN_THOAI_PATTERN = "^(0|\\+84)\\d{9}$";
    private static final String CCCD_PATTERN = "^\\d{9}|\\d{12}$";
    
    // Validation messages
    private static final String FIELD_EMPTY_ERROR = "%s không được để trống";
    private static final String HO_TEN_ERROR = "Họ tên chỉ được chứa chữ cái";
    private static final String SO_DIEN_THOAI_ERROR = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0 hoặc +84";
    private static final String CCCD_ERROR = "CCCD phải có 9 hoặc 12 chữ số";
    private static final String DIA_CHI_ERROR = "Địa chỉ phải nhập đầy đủ";
    private static final String NGAY_SINH_ERROR = "Ngày sinh không hợp lệ";
    private static final String NGAY_SINH_EMPTY_ERROR = "Ngày sinh chưa được chọn";
    
    /**
     * Validates a field and shows error message if invalid
     * @param value Field value
     * @param component Component to highlight
     * @param pattern Regex pattern to validate
     * @param emptyErrorMessage Error message when field is empty
     * @param invalidErrorMessage Error message when field format is invalid
     * @param fieldName Name of the field for error message
     * @return true if valid, false otherwise
     */
    private static boolean validateField(String value, JComponent component, 
                                        String pattern, String emptyErrorMessage, 
                                        String invalidErrorMessage, String fieldName) {
        // Check if empty first
        if (value == null || value.isEmpty()) {
            String message = emptyErrorMessage != null ? 
                    emptyErrorMessage : String.format(FIELD_EMPTY_ERROR, fieldName);
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(message);
            return false;
        }
        
        // If not empty, check pattern if provided
        if (pattern != null && !value.matches(pattern)) {
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(invalidErrorMessage);
            return false;
        }
        
        // Valid
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        component.setToolTipText(null);
        return true;
    }
    
    /**
     * Validates Họ tên field
     */
    public static boolean validateHoTen(String hoTen, JComponent component) {
        return validateField(hoTen, component, HO_TEN_PATTERN, 
                "Họ tên không được để trống", HO_TEN_ERROR, "Họ tên");
    }
    
    /**
     * Validates Số điện thoại field
     */
    public static boolean validateSoDienThoai(String soDienThoai, JComponent component) {
        return validateField(soDienThoai, component, SO_DIEN_THOAI_PATTERN, 
                "Số điện thoại không được để trống", SO_DIEN_THOAI_ERROR, "Số điện thoại");
    }
    
    /**
     * Validates CCCD field
     */
    public static boolean validateCCCD(String cccd, JComponent component) {
        return validateField(cccd, component, CCCD_PATTERN, 
                "CCCD không được để trống", CCCD_ERROR, "CCCD");
    }
    
    /**
     * Validates Địa chỉ field
     */
    public static boolean validateDiaChi(String diaChi, JComponent component) {
        return validateField(diaChi, component, null, 
                "Địa chỉ không được để trống", DIA_CHI_ERROR, "Địa chỉ");
    }
    
    /**
     * Validates Ngày sinh
     */
    public static boolean validateNgaySinh(Date ngaySinh, JComponent component) {
        // Check if empty
        if (ngaySinh == null) {
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(NGAY_SINH_EMPTY_ERROR);
            return false;
        }
        
        // Check if valid (not in the future)
        if (ngaySinh.after(new Date())) {
            component.setBorder(BorderFactory.createLineBorder(Color.RED));
            component.setToolTipText(NGAY_SINH_ERROR);
            return false;
        }
        
        // Valid
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        component.setToolTipText(null);
        return true;
    }
    
    /**
     * Sanitizes input to prevent XSS attacks
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("&", "&amp;");
    }
    
    /**
     * Get validation error message for a specific field
     */
    public static String getErrorMessage(JComponent component) {
        return component.getToolTipText();
    }
    
    /**
     * Clear validation errors from a component
     */
    public static void clearValidationError(JComponent component) {
        component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        component.setToolTipText(null);
    }
    
    /**
     * Reset all validation errors on components
     */
    public static void resetValidationErrors(JComponent... components) {
        for (JComponent component : components) {
            clearValidationError(component);
        }
    }
}