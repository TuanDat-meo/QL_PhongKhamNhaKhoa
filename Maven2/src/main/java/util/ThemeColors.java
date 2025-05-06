package util;

import java.awt.Color;
import java.awt.Font;

/**
 * Class quản lý màu sắc và font cho toàn bộ ứng dụng
 */
public class ThemeColors {
    // Background colors
    public static final Color BG_PRIMARY = new Color(245, 247, 250);      // Light gray-blue background
    public static final Color BG_SECONDARY = new Color(255, 255, 255);    // White
    public static final Color BG_ACCENT = new Color(232, 240, 254);       // Very light blue
    
    // Main colors
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);    // Medium blue
    public static final Color PRIMARY_DARK = new Color(21, 101, 192);     // Darker blue
    public static final Color PRIMARY_LIGHT = new Color(66, 165, 245);    // Lighter blue
    public static final Color SECONDARY_COLOR = new Color(66, 66, 66);    // Dark gray
    public static final Color ACCENT_COLOR = new Color(211, 47, 47);      // Red
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);     // Green
    public static final Color WARNING_COLOR = new Color(237, 108, 2);     // Orange
    
    // Text colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);       // Nearly black
    public static final Color TEXT_SECONDARY = new Color(97, 97, 97);     // Medium gray
    public static final Color TEXT_LIGHT = new Color(158, 158, 158);      // Light gray
    
    // Border colors
    public static final Color BORDER_COLOR = new Color(224, 224, 224);    // Light gray
    public static final Color DIVIDER_COLOR = new Color(238, 238, 238);   // Very light gray
    
    // Calendar specific colors
    public static final Color COLOR_MORNING = new Color(232, 245, 253);   // Light blue for morning
    public static final Color COLOR_AFTERNOON = new Color(255, 243, 224); // Light orange for afternoon
    public static final Color COLOR_SELECTED = new Color(187, 222, 251);  // Highlighted blue when selected
    public static final Color COLOR_BOOKED = new Color(224, 242, 241);    // Mint green for booked
    public static final Color COLOR_OWN_BOOKED = new Color(200, 230, 201); // Different green for own appointments
    
    // Table colors
    public static final Color TABLE_HEADER_BG = PRIMARY_COLOR;            // Header background
    public static final Color TABLE_HEADER_FG = Color.WHITE;              // Header text
    public static final Color TABLE_ROW_ALT = new Color(250, 250, 250);   // Alternate row color
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
}