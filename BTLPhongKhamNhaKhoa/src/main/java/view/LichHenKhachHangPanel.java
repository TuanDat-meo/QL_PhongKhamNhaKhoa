package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.sql.Date;
import java.sql.SQLException;
import com.toedter.calendar.JDateChooser;
import controller.LichHenController;
import controller.NguoiDungController;
import controller.BenhNhanController;
import model.LichHen;
import model.NguoiDung;

public class LichHenKhachHangPanel extends JPanel {
    private LichHenController controller;
    private BenhNhanController benhNhanController;
    private NguoiDungController nguoiDungController;
    private JTable lichHenTable;
    private DefaultTableModel tableModel;
    private JLabel weekRangeLabel;
    private JComboBox<String> cbBacSi;
    private JComboBox<String> cbPhongKham;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JDateChooser dateChooser;
    private Calendar currentCalendar;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    private int currentUserId = -1; 
    private NguoiDung currentUser = null; 
    private Map<JComponent, Timer> fieldErrorTimers = new HashMap<>();
    private Map<JComponent, JLabel> fieldErrorLabels = new HashMap<>();
    private JPanel errorPanel;
    private Color errorBorderColor = Color.RED;
    private JLabel errorLabel;
    private JPopupMenu popupMenuLichHen;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemSuaLichHen;
    private JMenuItem menuItemXoaLichHen;   
    
    // Enhanced Color Palette - Softer and More Professional
    private static final Color BG_PRIMARY = new Color(248, 250, 252);        // Softer background
    private static final Color BG_SECONDARY = new Color(241, 245, 249);      // Lighter secondary
    private static final Color BG_CARD = new Color(255, 255, 255);           // Pure white cards
    private static final Color ACCENT_PRIMARY = new Color(59, 130, 246);     // Modern blue
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Fresh green
    private static final Color DANGER_COLOR = new Color(239, 68, 68);        // Modern red
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);         // Slate text
    private static final Color TEXT_SECONDARY = new Color(71, 85, 105);      // Medium slate
    private static final Color BORDER_COLOR = new Color(226, 232, 240);      // Soft border
    private static final Color BORDER_STRONG = new Color(148, 163, 184);     // Stronger border
    
    // Improved Table Colors - Better Contrast and Readability
    private static final Color TABLE_HEADER_BG = new Color(248, 250, 252);
    private static final Color COLOR_MORNING = new Color(224, 244, 255); // light sky blue
    private static final Color COLOR_AFTERNOON = new Color(255, 242, 215); // vàng kem sáng

    private static final Color COLOR_SELECTED = new Color(79, 172, 254);     // Blue selection
    private static final Color COLOR_BOOKED = new Color(220, 252, 231);      // Light mint green for booked
    private static final Color COLOR_HOVER = new Color(241, 245, 249);       // Light hover effect
    
    // Professional Typography
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private Color primaryColor = new Color(41, 128, 185); // Modern blue
    private Color successColor = new Color(86, 156, 104);
    private Color headerTextColor = Color.WHITE; // Header text color
    private int cornerRadius = 10;
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
    private static final Color STATUS_PENDING = new Color(255, 193, 7);     // Amber/Warning - Chờ xác nhận
    private static final Color STATUS_CONFIRMED = new Color(40, 167, 69);   // Success Green - Đã xác nhận  
    private static final Color STATUS_CANCELLED = new Color(220, 53, 69);   // Danger Red - Đã hủy
    private static final Color STATUS_DEFAULT = new Color(248, 249, 250);   // Light Gray - Mặc định

    // Sử dụng Map để tối ưu performance thay vì switch-case
    private static final Map<String, Color> STATUS_COLOR_MAP = Map.of(
        "Chờ xác nhận", STATUS_PENDING,
        "Đã xác nhận", STATUS_CONFIRMED,
        "Đã hủy", STATUS_CANCELLED
    );
    private final String[] daysOfWeek = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
    
    private final String[] timeSlots = {
    	    "07:30", "08:00", "08:30", "09:00", "09:30", 
    	    "10:00", "10:30", "11:00", "11:30", "12:00",  // Buổi sáng: 10 slots
    	    "13:00", "13:30", "14:00", "14:30", "15:00", 
    	    "15:30", "16:00", "16:30", "17:00"           // Buổi chiều: 9 slots
    	};
    
    private static final int MORNING_SLOTS_COUNT = 10; // 07:30 - 12:00 (10 slots)
    private Map<String, LichHen> appointmentMap = new HashMap<>();

    public LichHenKhachHangPanel(NguoiDung user) {
        this.currentUser = user;
        controller = new LichHenController();
        benhNhanController = new BenhNhanController();
        nguoiDungController = new NguoiDungController();
        currentCalendar = Calendar.getInstance();
        setupUI();
        setupPopupMenu();
        updateUIBasedOnUserRole();
        loadData();
        setupEventListeners();
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        try {
            NguoiDungController userController = new NguoiDungController();
            this.currentUser = userController.getNguoiDungById(userId);
            updateUIBasedOnUserRole();
            resetFilters(true); // Reset filters và load data mới
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Không thể tải thông tin người dùng: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void setupPopupMenu() {
        popupMenuLichHen = new JPopupMenu();
        popupMenuLichHen.setBorder(new LineBorder(BORDER_STRONG, 1));
        popupMenuLichHen.setBackground(BG_CARD);

        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSuaLichHen = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoaLichHen = createStyledMenuItem("Xóa");

        menuItemXoaLichHen.setForeground(DANGER_COLOR);

        popupMenuLichHen.add(menuItemXemChiTiet);
        popupMenuLichHen.addSeparator();
        popupMenuLichHen.add(menuItemSuaLichHen);
        popupMenuLichHen.addSeparator();
        popupMenuLichHen.add(menuItemXoaLichHen);
    }

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(FONT_BODY);
        menuItem.setForeground(TEXT_PRIMARY);
        menuItem.setBackground(BG_CARD);
        menuItem.setBorder(new EmptyBorder(10, 16, 10, 16));
        menuItem.setOpaque(true);
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(COLOR_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(BG_CARD);
            }
        });
        
        return menuItem;
    }
    
    private void updateUIBasedOnUserRole() {
        if (currentUser == null) return;
        
        String role = currentUser.getVaiTro();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isStaff = "STAFF".equalsIgnoreCase(role) || "NHÂN VIÊN".equalsIgnoreCase(role);
        
        if (!isAdmin && !isStaff) {
            btnThem.setText("Đặt lịch");
            btnCapNhat.setText("Sửa");
        } else {
            btnThem.setText("Thêm");
            btnCapNhat.setText("Sửa");
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JPanel headerPanel = createModernHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel footerPanel = createFooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    private JPanel createModernHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 0));
        headerPanel.setBackground(BG_SECONDARY);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(12, 20, 12, 20) // Reduced padding from 16 to 12
        ));
        
        // Single compact row layout
        JPanel mainRow = new JPanel(new BorderLayout());
        mainRow.setBackground(BG_SECONDARY);
        
        // Left: Compact title
        JPanel titleSection = createCompactTitleSection();
        
        // Center: Navigation
        JPanel navigationSection = createCompactNavigationSection();
        
        // Right: Filters
        JPanel filterSection = createInlineFilterSection();
        
        mainRow.add(titleSection, BorderLayout.WEST);
        mainRow.add(navigationSection, BorderLayout.CENTER);
        mainRow.add(filterSection, BorderLayout.EAST);
        
        headerPanel.add(mainRow, BorderLayout.CENTER);
        
        return headerPanel;
    }
    private JPanel createCompactTitleSection() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BG_SECONDARY);
        
        JLabel titleLabel = new JLabel("Lịch hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Reduced from 18 to 16
        titleLabel.setForeground(TEXT_PRIMARY);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    private JPanel createCompactNavigationSection() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0)); // Reduced spacing
        navPanel.setBackground(BG_SECONDARY);
        
        JButton btnPrevWeek = createCompactNavButton("‹", "Tuần trước");
        weekRangeLabel = new JLabel();
        weekRangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Smaller font
        weekRangeLabel.setForeground(TEXT_PRIMARY);
        weekRangeLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(4, 10, 4, 10) // Reduced padding
        ));
        weekRangeLabel.setBackground(BG_CARD);
        weekRangeLabel.setOpaque(true);
        
        JButton btnNextWeek = createCompactNavButton("›", "Tuần sau");
        JButton btnToday = createCompactNavButton("Hôm nay", null);
        btnToday.setPreferredSize(new Dimension(65, 26)); // Smaller size
        
        navPanel.add(btnPrevWeek);
        navPanel.add(weekRangeLabel);
        navPanel.add(btnNextWeek);
        navPanel.add(Box.createHorizontalStrut(8));
        navPanel.add(btnToday);
        
        btnPrevWeek.addActionListener(e -> navigateWeek(-7));
        btnNextWeek.addActionListener(e -> navigateWeek(7));
        btnToday.addActionListener(e -> {
            currentCalendar = Calendar.getInstance();
            updateWeekLabel();
            loadData();
        });
        
        updateWeekLabel();
        return navPanel;
    }    
    private JPanel createInlineFilterSection() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setBackground(BG_SECONDARY);

        // Create date chooser following the pattern
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date());
        dateChooser.setPreferredSize(new Dimension(100, 26));
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        // Create combo boxes following the pattern
        cbBacSi = createCompactComboBox(110);
        cbBacSi.setPreferredSize(new Dimension(110, 26));
        cbBacSi.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadBacSiList();

        cbPhongKham = createCompactComboBox(110);
        cbPhongKham.setPreferredSize(new Dimension(110, 26));
        cbPhongKham.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadPhongKhamList();

        // Create search text field following the pattern
        txtTimKiem = createCompactTextField(140);
        txtTimKiem.setPreferredSize(new Dimension(140, 26));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(3, 6, 3, 6)));
        txtTimKiem.setToolTipText("Tìm kiếm...");

        // Create search button following the pattern
        btnTimKiem = createCompactButton("Tìm", ACCENT_PRIMARY);
        btnTimKiem.setPreferredSize(new Dimension(45, 26));
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTimKiem.addActionListener(e -> applySearch());

        filterPanel.add(dateChooser);
        filterPanel.add(cbBacSi);
        filterPanel.add(cbPhongKham);
        filterPanel.add(txtTimKiem);
        filterPanel.add(btnTimKiem);

        return filterPanel;
    }
    private JTextField createCompactTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        field.setPreferredSize(new Dimension(width, 26)); // Reduced height
        field.setBackground(BG_CARD);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        return field;
    }
    private JComboBox<String> createCompactComboBox(int width) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        combo.setPreferredSize(new Dimension(width, 26)); // Reduced height
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return combo;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
        
        createScheduleTable();
        
        JScrollPane scrollPane = new JScrollPane(lichHenTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_STRONG, 1));
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setBackground(BG_CARD);
        
        // Style scrollbars
        scrollPane.getVerticalScrollBar().setBackground(BG_CARD);
        scrollPane.getHorizontalScrollBar().setBackground(BG_CARD);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout(20, 0)); // Reduced spacing
        footerPanel.setBackground(BG_SECONDARY);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_STRONG),
            new EmptyBorder(15, 24, 15, 24) // Reduced padding
        ));
        
        JPanel legendPanel = createCompactLegendPanel();
        JPanel actionPanel = createCompactActionPanel();
        
        footerPanel.add(legendPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    private JPanel createCompactLegendPanel() {
        JPanel legendPanel = new JPanel(new BorderLayout());
        legendPanel.setBackground(BG_SECONDARY);
        
        // Tạo 2 hàng: 1 hàng cho màu thời gian, 1 hàng cho màu trạng thái
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topRow.setBackground(BG_SECONDARY);
        
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bottomRow.setBackground(BG_SECONDARY);
        
        // Hàng 1: Màu thời gian và trạng thái cơ bản
        String[] timeLabels = {"Buổi sáng", "Buổi chiều", "Đã chọn", "Đã đặt"};
        Color[] timeColors = {COLOR_MORNING, COLOR_AFTERNOON, COLOR_SELECTED, COLOR_BOOKED};
        
        for (int i = 0; i < timeLabels.length; i++) {
            JPanel item = createLegendItem(timeLabels[i], timeColors[i]);
            topRow.add(item);
        }
        
        // Hàng 2: Màu trạng thái lịch hẹn
        String[] statusLabels = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        Color[] statusColors = {STATUS_PENDING, STATUS_CONFIRMED, STATUS_CANCELLED};
        
        for (int i = 0; i < statusLabels.length; i++) {
            JPanel item = createLegendItem(statusLabels[i], statusColors[i]);
            bottomRow.add(item);
        }
        
        // Thêm các hàng vào panel chính
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(BG_SECONDARY);
        
        containerPanel.add(topRow);
        containerPanel.add(Box.createVerticalStrut(8)); // Khoảng cách giữa 2 hàng
        containerPanel.add(bottomRow);
        
        legendPanel.add(containerPanel, BorderLayout.WEST);
        
        return legendPanel;
    }
    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setBackground(BG_SECONDARY);
        
        // Color indicator square
        JPanel colorSquare = new JPanel();
        colorSquare.setPreferredSize(new Dimension(14, 14));
        colorSquare.setBackground(color);
        colorSquare.setBorder(BorderFactory.createLineBorder(BORDER_STRONG, 1));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelComponent.setForeground(TEXT_SECONDARY);
        
        item.add(colorSquare);
        item.add(labelComponent);
        
        return item;
    }
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    private JButton createCompactRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(95, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
    private JButton createCompactButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6); // Smaller radius for compact buttons
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(45, 26));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(originalColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    private JButton createCompactNavButton(String text, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        
        button.setFont(text.length() > 2 ? new Font("Segoe UI", Font.BOLD, 10) : new Font("Segoe UI", Font.BOLD, 12));
        if (tooltip != null) button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(text.length() <= 2 ? 26 : 65, 26));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBackground(BG_CARD);
        button.setForeground(TEXT_PRIMARY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BG_CARD);
            }
        });
        
        return button;
    }
    private JPanel createCompactActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(BG_SECONDARY);
        
        // Use the updated createRoundedButton method with proper parameters
        btnThem = createRoundedButton("Thêm lịch", successColor, headerTextColor, cornerRadius);
        btnCapNhat = createRoundedButton("Cập nhật", primaryColor, headerTextColor, cornerRadius);
        btnXoa = createRoundedButton("Xóa", DANGER_COLOR, headerTextColor, cornerRadius);
        
        // Set consistent sizes for action buttons
        Dimension buttonSize = new Dimension(95, 35);
        btnThem.setPreferredSize(buttonSize);
        btnCapNhat.setPreferredSize(buttonSize);
        btnXoa.setPreferredSize(buttonSize);
        
        actionPanel.add(btnThem);
        actionPanel.add(btnCapNhat);
        actionPanel.add(btnXoa);
        
        return actionPanel;
    }
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    private void createScheduleTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns
        tableModel.addColumn("Giờ");
        for (String day : daysOfWeek) {
            tableModel.addColumn(day);
        }
        
        // Add all time slots
        for (int i = 0; i < timeSlots.length; i++) {
            Object[] rowData = new Object[8];
            rowData[0] = timeSlots[i];
            tableModel.addRow(rowData);
        }
        
        lichHenTable = new JTable(tableModel);
        lichHenTable.setRowHeight(38); // Reduced from 45 to 38
        lichHenTable.setShowVerticalLines(true);
        lichHenTable.setShowHorizontalLines(true);
        lichHenTable.setGridColor(BORDER_COLOR);
        lichHenTable.getTableHeader().setReorderingAllowed(false);
        lichHenTable.getTableHeader().setResizingAllowed(false);
        lichHenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lichHenTable.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
        lichHenTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lichHenTable.setRowSelectionAllowed(false);
        lichHenTable.setCellSelectionEnabled(true);
        lichHenTable.setIntercellSpacing(new Dimension(1, 1));
        lichHenTable.setBackground(BG_CARD);
        lichHenTable.setForeground(TEXT_PRIMARY);
        
        // Header styling
        JTableHeader header = lichHenTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Smaller header font
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Reduced height
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_STRONG));
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setVerticalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(TABLE_HEADER_BG);
        headerRenderer.setForeground(TEXT_PRIMARY);
        
        lichHenTable.setDefaultRenderer(Object.class, new OptimizedScheduleTableCellRenderer());
        
        // Column widths - optimize for space
        lichHenTable.getColumnModel().getColumn(0).setMaxWidth(80); // Reduced from 100
        lichHenTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        
        for (int i = 1; i < lichHenTable.getColumnCount(); i++) {
            lichHenTable.getColumnModel().getColumn(i).setPreferredWidth(130); // Reduced from 150
        }
    }
    private class OptimizedScheduleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new BorderLayout(0, 0));
            
            // Background colors based on selection and time
            if (row == selectedRow && column == selectedColumn) {
                panel.setBackground(COLOR_SELECTED);
                panel.setBorder(BorderFactory.createLineBorder(new Color(37, 99, 235), 3));
            } else {
                if (column == 0) {
                    panel.setBackground(TABLE_HEADER_BG);
                    panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, BORDER_STRONG));
                } else if (row < MORNING_SLOTS_COUNT) {
                    panel.setBackground(COLOR_MORNING);
                    panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                } else {
                    panel.setBackground(COLOR_AFTERNOON);
                    panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                }
            }
            
            if (value != null && !value.toString().isEmpty()) {
                if (column == 0) {
                    // Time column
                    JLabel timeLabel = new JLabel(value.toString());
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    timeLabel.setHorizontalAlignment(JLabel.CENTER);
                    timeLabel.setVerticalAlignment(JLabel.CENTER);
                    timeLabel.setForeground(TEXT_PRIMARY);
                    panel.add(timeLabel, BorderLayout.CENTER);
                } else {
                    // Appointment cell - sử dụng màu theo trạng thái
                    String key = row + "-" + column;
                    LichHen lichHen = appointmentMap.get(key);
                    
                    String[] lines = value.toString().split("\n");
                    
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setOpaque(false);
                    contentPanel.setBorder(new EmptyBorder(3, 6, 3, 6));
                    
                    if (lines.length >= 1) {
                        JLabel nameLabel = new JLabel("BN: " + lines[0]);
                        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        nameLabel.setForeground(TEXT_PRIMARY);
                        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        contentPanel.add(nameLabel);                                           
                        
                        if (lines.length >= 2) {
                            JLabel roomLabel = new JLabel("P: " + lines[1]);
                            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                            roomLabel.setForeground(TEXT_SECONDARY);
                            roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            contentPanel.add(Box.createVerticalStrut(2));
                            contentPanel.add(roomLabel);
                        }
                        
                        // *** QUAN TRỌNG: Sử dụng màu theo trạng thái thay vì màu cố định ***
                        Color appointmentBgColor;
                        Color indicatorColor;
                        
                        if (lichHen != null && lichHen.getTrangThai() != null) {
                            // Lấy màu theo trạng thái lịch hẹn
                            appointmentBgColor = getColorByStatus(lichHen.getTrangThai());
                            indicatorColor = appointmentBgColor.darker();
                            
                            // Thêm status badge nhỏ
                            JLabel statusBadge = new JLabel(lichHen.getTrangThai());
                            statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 8));
                            statusBadge.setForeground(getTextColorByStatus(lichHen.getTrangThai()));
                            statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
                            contentPanel.add(Box.createVerticalStrut(1));
                            contentPanel.add(statusBadge);
                        } else {
                            // Fallback cho trường hợp không có trạng thái
                            appointmentBgColor = COLOR_BOOKED;
                            indicatorColor = SUCCESS_COLOR;
                        }
                        
                        // Xử lý màu nền khi ô được chọn
                        if (row == selectedRow && column == selectedColumn) {
                            // Làm sáng màu nền khi được chọn
                            int red = Math.min(255, appointmentBgColor.getRed() + 30);
                            int green = Math.min(255, appointmentBgColor.getGreen() + 30);
                            int blue = Math.min(255, appointmentBgColor.getBlue() + 30);
                            panel.setBackground(new Color(red, green, blue));
                        } else {
                            panel.setBackground(appointmentBgColor);
                        }
                        
                        // Status indicator bar bên trái
                        JPanel indicator = new JPanel();
                        indicator.setPreferredSize(new Dimension(4, panel.getHeight()));
                        indicator.setBackground(indicatorColor);
                        panel.add(indicator, BorderLayout.WEST);
                    }
                    
                    panel.add(contentPanel, BorderLayout.CENTER);
                }
            } else if (column > 0) {
                // Empty cell with selection indicator
                if (row == selectedRow && column == selectedColumn) {
                    JLabel plusLabel = new JLabel("+");
                    plusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    plusLabel.setForeground(Color.WHITE);
                    plusLabel.setHorizontalAlignment(JLabel.CENTER);
                    plusLabel.setVerticalAlignment(JLabel.CENTER);
                    panel.add(plusLabel, BorderLayout.CENTER);
                }
            }

            return panel;
        }
    }    
    private void updateWeekLabel() {
        Calendar startOfWeek = (Calendar) currentCalendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        weekRangeLabel.setText(sdf.format(startOfWeek.getTime()) + " - " + sdf.format(endOfWeek.getTime()));
    }
    private void navigateWeek(int days) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, days);
        updateWeekLabel();
        loadData();
    }
    private void applySearch() {
        try {
            String searchText = txtTimKiem.getText().trim();
            java.util.Date selectedDate = dateChooser.getDate();
            String selectedDoctor = cbBacSi.getSelectedIndex() > 0 ? cbBacSi.getSelectedItem().toString() : null;
            String selectedRoom = cbPhongKham.getSelectedIndex() > 0 ? cbPhongKham.getSelectedItem().toString() : null;
            
            if (searchText.isEmpty() && selectedDoctor == null && selectedRoom == null) {
                resetFilters(false);
                return;
            }
            
            if (selectedDate != null) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime(selectedDate);
                currentCalendar = selectedCalendar;
                updateWeekLabel();
            }
            
            filterAndLoadData(selectedDoctor, selectedRoom);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAndLoadData(String doctor, String room) {
        // Clear current data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        String searchText = txtTimKiem.getText().trim().toLowerCase();
        List<LichHen> dsLichHen = new ArrayList<>();
        
        // Lấy dữ liệu theo role của user trước
        try {
            if (currentUser != null) {
                String role = currentUser.getVaiTro();
                String userName = currentUser.getHoTen();
                
                if ("ADMIN".equalsIgnoreCase(role) || "STAFF".equalsIgnoreCase(role) || "NHÂN VIÊN".equalsIgnoreCase(role)) {
                    dsLichHen = controller.getAllLichHen();
                } else if ("PATIENT".equalsIgnoreCase(role) || "BỆNH NHÂN".equalsIgnoreCase(role)) {
                    dsLichHen = getLichHenByPatientName(userName);
                } else if ("DOCTOR".equalsIgnoreCase(role) || "BÁC SĨ".equalsIgnoreCase(role)) {
                    dsLichHen = getLichHenByDoctorName(userName);
                } else {
                    dsLichHen = getLichHenByUserName(userName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            dsLichHen = new ArrayList<>();
        }
        
        List<LichHen> filteredList = new ArrayList<>();
        
        for (LichHen lichHen : dsLichHen) {
            boolean includeRecord = true;
            
            if (doctor != null && !doctor.isEmpty() && !lichHen.getHoTenBacSi().equals(doctor)) {
                includeRecord = false;
            }
            
            if (room != null && !room.isEmpty() && !lichHen.getTenPhong().equals(room)) {
                includeRecord = false;
            }
            
            if (!searchText.isEmpty()) {
                boolean matchesSearch = 
                    lichHen.getHoTenBenhNhan().toLowerCase().contains(searchText) ||
                    lichHen.getHoTenBacSi().toLowerCase().contains(searchText) ||
                    lichHen.getTenPhong().toLowerCase().contains(searchText);
                
                if (!matchesSearch) {
                    includeRecord = false;
                }
            }
            
            if (includeRecord) {
                filteredList.add(lichHen);
            }
        }
        
        // Load filtered data into table
        loadAppointmentsToTable(filteredList);
    }
    private void loadData() {
        // Clear current data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        List<LichHen> dsLichHen = new ArrayList<>();
        try {
            if (currentUser != null) {
                String role = currentUser.getVaiTro();
                String userName = currentUser.getHoTen(); // Lấy tên người dùng hiện tại
                
                if ("ADMIN".equalsIgnoreCase(role) || "STAFF".equalsIgnoreCase(role) || "NHÂN VIÊN".equalsIgnoreCase(role)) {
                    // Admin và Staff có thể xem tất cả lịch hẹn
                    dsLichHen = controller.getAllLichHen();
                } else if ("PATIENT".equalsIgnoreCase(role) || "BỆNH NHÂN".equalsIgnoreCase(role)) {
                    // Bệnh nhân chỉ xem lịch hẹn của chính mình
                    dsLichHen = getLichHenByPatientName(userName);
                } else if ("DOCTOR".equalsIgnoreCase(role) || "BÁC SĨ".equalsIgnoreCase(role)) {
                    // Bác sĩ chỉ xem lịch hẹn mà họ phụ trách
                    dsLichHen = getLichHenByDoctorName(userName);
                } else {
                    // Mặc định: chỉ xem lịch hẹn của chính mình (theo tên)
                    dsLichHen = getLichHenByUserName(userName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể tải dữ liệu lịch hẹn: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        loadAppointmentsToTable(dsLichHen);
    }

    // Thêm các phương thức helper để lấy lịch hẹn theo tên người dùng
    private List<LichHen> getLichHenByPatientName(String patientName) {
        try {
            List<LichHen> allAppointments = controller.getAllLichHen();
            List<LichHen> userAppointments = new ArrayList<>();
            
            for (LichHen lichHen : allAppointments) {
                if (lichHen.getHoTenBenhNhan() != null && 
                    lichHen.getHoTenBenhNhan().equalsIgnoreCase(patientName)) {
                    userAppointments.add(lichHen);
                }
            }
            return userAppointments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<LichHen> getLichHenByDoctorName(String doctorName) {
        try {
            List<LichHen> allAppointments = controller.getAllLichHen();
            List<LichHen> doctorAppointments = new ArrayList<>();
            
            for (LichHen lichHen : allAppointments) {
                if (lichHen.getHoTenBacSi() != null && 
                    lichHen.getHoTenBacSi().equalsIgnoreCase(doctorName)) {
                    doctorAppointments.add(lichHen);
                }
            }
            return doctorAppointments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<LichHen> getLichHenByUserName(String userName) {
        try {
            List<LichHen> allAppointments = controller.getAllLichHen();
            List<LichHen> userAppointments = new ArrayList<>();
            
            for (LichHen lichHen : allAppointments) {
                // Kiểm tra cả bệnh nhân và bác sĩ
                if ((lichHen.getHoTenBenhNhan() != null && lichHen.getHoTenBenhNhan().equalsIgnoreCase(userName)) ||
                    (lichHen.getHoTenBacSi() != null && lichHen.getHoTenBacSi().equalsIgnoreCase(userName))) {
                    userAppointments.add(lichHen);
                }
            }
            return userAppointments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    private void loadAppointmentsToTable(List<LichHen> dsLichHen) {
        // Clear current data - đơn giản hóa
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        Calendar startOfWeek = (Calendar) currentCalendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        startOfWeek.set(Calendar.HOUR_OF_DAY, 0);
        startOfWeek.set(Calendar.MINUTE, 0);
        startOfWeek.set(Calendar.SECOND, 0);

        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DATE, 6);
        endOfWeek.set(Calendar.HOUR_OF_DAY, 23);
        endOfWeek.set(Calendar.MINUTE, 59);
        endOfWeek.set(Calendar.SECOND, 59);

        for (LichHen lichHen : dsLichHen) {
            try {
                if (lichHen.getNgayHen() == null) continue;
                
                Calendar lichHenCal = Calendar.getInstance();
                lichHenCal.setTime(lichHen.getNgayHen());

                if (lichHenCal.getTimeInMillis() >= startOfWeek.getTimeInMillis() &&
                    lichHenCal.getTimeInMillis() <= endOfWeek.getTimeInMillis()) {

                    int dayOfWeek = lichHenCal.get(Calendar.DAY_OF_WEEK);
                    int column = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;

                    String gioHen = new SimpleDateFormat("HH:mm").format(lichHen.getGioHen());
                    if (gioHen != null) {
                        for (int i = 0; i < timeSlots.length; i++) {
                            if (timeSlots[i].equals(gioHen)) {
                                // Đơn giản hóa: row index trực tiếp từ timeSlots index
                                int tableRow = i;
                                
                                String cellContent = lichHen.getHoTenBenhNhan() + "\n" + lichHen.getTenPhong();
                                tableModel.setValueAt(cellContent, tableRow, column);
                                
                                // Store appointment for quick access
                                String key = tableRow + "-" + column;
                                appointmentMap.put(key, lichHen);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý lịch hẹn: " + e.getMessage());
            }
        }
    }
    private void setupEventListeners() {
        // Setup table click events
        lichHenTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = lichHenTable.rowAtPoint(e.getPoint());
                int col = lichHenTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col > 0) {
                    selectedRow = row;
                    selectedColumn = col;
                    lichHenTable.repaint();
                    
                    Object cellValue = tableModel.getValueAt(row, col);
                    
                    // Nếu ô có dữ liệu (đã có lịch hẹn)
                    if (cellValue != null && !cellValue.toString().isEmpty()) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            showPopupMenu(e);
                        }
                    } 
                    // Nếu ô trống và click chuột trái
                    else if (SwingUtilities.isLeftMouseButton(e)) {
                        // Tính toán ngày và giờ từ vị trí ô được click
                        Calendar selectedDate = getDateFromTablePosition(col);
                        String selectedTime = timeSlots[row];
                        
                        // Kiểm tra ngày có hợp lệ không
                        if (isValidAppointmentDate(selectedDate)) {
                            // Hiển thị form đặt lịch với ngày giờ đã điền sẵn
                            showThemLichHenDialogWithDateTime(selectedDate, selectedTime);
                        } else {
                            String errorMessage = getDateErrorMessage(selectedDate);
                            JOptionPane.showMessageDialog(null, "Không thể đặt lịch", "Lỗi", JOptionPane.WARNING_MESSAGE);

                        }
                    }
                }
            }
        });
        
        // Setup button events
        btnThem.addActionListener(e -> showThemLichHenDialog());
        btnCapNhat.addActionListener(e -> showCapNhatLichHenDialog());
        btnXoa.addActionListener(e -> xoaLichHen());
        
        // Setup popup menu events
        menuItemXemChiTiet.addActionListener(e -> xemChiTietLichHen());
        menuItemSuaLichHen.addActionListener(e -> showCapNhatLichHenDialog());
        menuItemXoaLichHen.addActionListener(e -> xoaLichHen());
    }
    private Calendar getDateFromTablePosition(int column) {
        Calendar startOfWeek = (Calendar) currentCalendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        
        Calendar selectedDate = (Calendar) startOfWeek.clone();
        selectedDate.add(Calendar.DAY_OF_YEAR, column - 1);
        
        return selectedDate;
    }
    private boolean isValidAppointmentDate(Calendar selectedDate) {
        Calendar now = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar selected = (Calendar) selectedDate.clone();
        selected.set(Calendar.HOUR_OF_DAY, 0);
        selected.set(Calendar.MINUTE, 0);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);
        
        // Không được chọn ngày trong quá khứ
        if (selected.before(today)) {
            return false;
        }
        
        // Không được chọn ngày cuối tuần
        int dayOfWeek = selected.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }
        
        return true;
    }
    private String getDateErrorMessage(Calendar selectedDate) {
        Calendar now = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar selected = (Calendar) selectedDate.clone();
        selected.set(Calendar.HOUR_OF_DAY, 0);
        selected.set(Calendar.MINUTE, 0);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);
        
        if (selected.before(today)) {
            return "Không thể đặt lịch hẹn trong quá khứ. Vui lòng chọn ngày từ hôm nay trở đi.";
        }
        
        int dayOfWeek = selected.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return "Không thể đặt lịch hẹn vào cuối tuần. Vui lòng chọn ngày từ thứ 2 đến thứ 6.";
        }
        
        return "Ngày không hợp lệ. Vui lòng chọn ngày khác.";
    }
    private void showThemLichHenDialogWithDateTime(Calendar selectedDate, String selectedTime) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đặt Lịch Hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650); // Tăng chiều cao để chứa error panel
        dialog.setLocationRelativeTo(this);
        
        // Tạo form panel với ngày giờ được điền sẵn
        JPanel formPanel = createLichHenFormPanelWithDateTime(selectedDate, selectedTime);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton btnLuu = createCompactRoundedButton("Lưu", SUCCESS_COLOR, Color.WHITE);
        JButton btnHuy = createCompactRoundedButton("Hủy", DANGER_COLOR, Color.WHITE);
        
        btnLuu.addActionListener(e -> {
            if (luuLichHenMoi(formPanel)) {
                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Đặt lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private void showPopupMenu(MouseEvent e) {
        popupMenuLichHen.show(e.getComponent(), e.getX(), e.getY());
    }
    private JPanel createLichHenFormPanelWithDateTime(Calendar selectedDate, String selectedTime) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_CARD);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Đặt Lịch Hẹn Mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Bác sĩ
        panel.add(createFormField("Bác sĩ:", createBacSiComboBox(null)));
        panel.add(Box.createVerticalStrut(15));
        
        // Bệnh nhân
        panel.add(createFormField("Bệnh nhân:", createBenhNhanComboBox(null)));
        panel.add(Box.createVerticalStrut(15));
        
        // Ngày hẹn - điền sẵn ngày được chọn
        JDateChooser dateChooserForm = createDateChooser(null);
        dateChooserForm.setDate(selectedDate.getTime());
        JPanel datePanel = createFormFieldWithError("Ngày hẹn:", dateChooserForm);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Giờ hẹn - điền sẵn giờ được chọn với error handling
        JComboBox<String> timeComboBox = createTimeComboBox(null);
        timeComboBox.setSelectedItem(selectedTime);
        JPanel timePanel = createFormFieldWithTimeError("Giờ hẹn:", timeComboBox);
        panel.add(timePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Phòng khám
        panel.add(createFormField("Phòng khám:", createPhongKhamComboBox(null)));
        panel.add(Box.createVerticalStrut(15));
        
        // Trạng thái
        panel.add(createFormField("Trạng thái:", createTrangThaiComboBox(null)));
        panel.add(Box.createVerticalStrut(15));
        
        // Mô tả
        panel.add(createFormField("Mô tả:", createMoTaTextArea(null)));
        
        // Error panel
        errorPanel = createErrorPanel();
        
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(errorPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    private JPanel createFormFieldWithError(String labelText, JComponent component) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        
        // Field panel
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(BG_CARD);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 30));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(100, 25));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(component, BorderLayout.CENTER);
        
        // Error label for this field
        JLabel errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(errorBorderColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(2, 110, 2, 5)); // Indent to align with field
        errorLabel.setVisible(false);
        
        fieldErrorLabels.put(component, errorLabel);
        
        mainPanel.add(fieldPanel);
        mainPanel.add(errorLabel);
        
        // Add change listener to date chooser
        if (component instanceof JDateChooser) {
            JDateChooser dateChooser = (JDateChooser) component;
            dateChooser.addPropertyChangeListener("date", e -> {
                if (e.getNewValue() != null) {
                    validateDate(dateChooser);
                }
            });
        }
        
        return mainPanel;
    }
    private JPanel createErrorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setVisible(false);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(errorBorderColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(errorLabel, BorderLayout.CENTER);
        return panel;
    }

    private void validateDate(JDateChooser dateChooser) {
        if (dateChooser.getDate() == null) {
            clearFieldError(dateChooser);
            return;
        }
        
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(dateChooser.getDate());
        
        if (!isValidAppointmentDate(selectedDate)) {
            String errorMessage = getDateErrorMessage(selectedDate);
            showFieldError(dateChooser, errorMessage);
        } else {
            clearFieldError(dateChooser);
        }
    }

    private void showFieldError(JComponent field, String message) {
        // Set error border
        setBorderError(field, true);
        
        // Show error message for this field
        JLabel errorLabel = fieldErrorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
        
        // Auto hide after 5 seconds
        Timer timer = fieldErrorTimers.get(field);
        if (timer != null) {
            timer.stop();
        }        
        // Sử dụng javax.swing.Timer với lambda expression đúng cú pháp
        timer = new Timer(5000, e -> clearFieldError(field));
        timer.setRepeats(false);
        timer.start();
        fieldErrorTimers.put(field, timer);
    }
    private void clearFieldError(JComponent field) {
        // Khôi phục viền bình thường
        setBorderError(field, false);

        // Ẩn error label riêng
        JLabel errorLabel = fieldErrorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

        // Hủy timer nếu có
        Timer timer = fieldErrorTimers.get(field);
        if (timer != null) {
            timer.stop();
            fieldErrorTimers.remove(field);
        }

        // Kiểm tra nếu không còn lỗi nào thì ẩn error panel chung
        boolean hasAnyError = fieldErrorLabels.values().stream()
                .anyMatch(label -> label.isVisible());

        if (!hasAnyError && errorPanel != null) {
            errorPanel.setVisible(false);
        }
    }
    private void setBorderError(JComponent component, boolean isError) {
        if (component instanceof JDateChooser) {
            JDateChooser dateChooser = (JDateChooser) component;
            if (isError) {
                dateChooser.setBorder(BorderFactory.createLineBorder(errorBorderColor, 2));
            } else {
                dateChooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }
        } else {
            if (isError) {
                component.setBorder(BorderFactory.createLineBorder(errorBorderColor, 2));
            } else {
                component.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }
        }
    }
    private void showThemLichHenDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Lịch Hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650); // Tăng chiều cao để chứa error panel
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = createLichHenFormPanel(null, false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton btnLuu = createCompactRoundedButton("Lưu", SUCCESS_COLOR, Color.WHITE);
        JButton btnHuy = createCompactRoundedButton("Hủy", DANGER_COLOR, Color.WHITE);
        
        btnLuu.addActionListener(e -> {
            if (luuLichHenMoi(formPanel)) {
                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private void showCapNhatLichHenDialog() {
        LichHen lichHen = getLichHenFromSelectedCell();
        if (lichHen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch hẹn để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Lịch Hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650); // Tăng chiều cao để chứa error panel
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = createLichHenFormPanel(lichHen, true);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton btnCapNhat = createCompactRoundedButton("Cập nhật", ACCENT_PRIMARY, Color.WHITE);
        JButton btnHuy = createCompactRoundedButton("Hủy", DANGER_COLOR, Color.WHITE);
        
        btnCapNhat.addActionListener(e -> {
            if (capNhatLichHen(formPanel, lichHen)) {
                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private JPanel createLichHenFormPanel(LichHen lichHen, boolean isEdit) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_CARD);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Title
        JLabel titleLabel = new JLabel(isEdit ? "Chỉnh Sửa Lịch Hẹn" : "Thêm Lịch Hẹn Mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Bác sĩ
        panel.add(createFormField("Bác sĩ:", createBacSiComboBox(lichHen)));
        panel.add(Box.createVerticalStrut(15));
        
        // Bệnh nhân
        panel.add(createFormField("Bệnh nhân:", createBenhNhanComboBox(lichHen)));
        panel.add(Box.createVerticalStrut(15));
        
        // Ngày hẹn với error handling
        JDateChooser dateChooser = createDateChooser(lichHen);
        JPanel datePanel = createFormFieldWithError("Ngày hẹn:", dateChooser);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Giờ hẹn với error handling
        JComboBox<String> timeComboBox = createTimeComboBox(lichHen);
        JPanel timePanel = createFormFieldWithTimeError("Giờ hẹn:", timeComboBox);
        panel.add(timePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Phòng khám
        panel.add(createFormField("Phòng khám:", createPhongKhamComboBox(lichHen)));
        panel.add(Box.createVerticalStrut(15));
        
        // Trạng thái
        panel.add(createFormField("Trạng thái:", createTrangThaiComboBox(lichHen)));
        panel.add(Box.createVerticalStrut(15));
        
        // Mô tả
        panel.add(createFormField("Mô tả:", createMoTaTextArea(lichHen)));
        
        // Error panel
        errorPanel = createErrorPanel();
        
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(errorPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    private JPanel createFormFieldWithTimeError(String labelText, JComboBox<String> timeComboBox) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        
        // Field panel
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(BG_CARD);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, timeComboBox.getPreferredSize().height + 30));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(100, 25));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(timeComboBox, BorderLayout.CENTER);
        
        // Error label for this field
        JLabel errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(errorBorderColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(2, 110, 2, 5)); // Indent to align with field
        errorLabel.setVisible(false);
        
        fieldErrorLabels.put(timeComboBox, errorLabel);
        
        mainPanel.add(fieldPanel);
        mainPanel.add(errorLabel);
        
        return mainPanel;
    }
    private JPanel createFormField(String labelText, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(BG_CARD);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 30));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(100, 25));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(component, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    private Color getColorByStatus(String status) {
        return status != null ? STATUS_COLOR_MAP.getOrDefault(status, STATUS_DEFAULT) : STATUS_DEFAULT;
    }
    private Color getTextColorByStatus(String status) {
        Color bgColor = getColorByStatus(status);
        
        // Tính độ sáng để chọn màu text phù hợp
        double brightness = (bgColor.getRed() * 0.299 + bgColor.getGreen() * 0.587 + bgColor.getBlue() * 0.114) / 255;
        return brightness > 0.6 ? Color.BLACK : Color.WHITE;
    }
    private JComboBox<String> createBacSiComboBox(LichHen lichHen) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setName("cbBacSi");
        
        List<String> bacSiList = controller.danhSachBacSi();
        for (String bacSi : bacSiList) {
            combo.addItem(bacSi);
        }
        
        if (lichHen != null && lichHen.getHoTenBacSi() != null) {
            combo.setSelectedItem(lichHen.getHoTenBacSi());
        }
        
        return combo;
    }    
    private JComboBox<String> createBenhNhanComboBox(LichHen lichHen) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setName("cbBenhNhan");
        
        // Chỉ hiển thị tên người dùng hiện tại
        if (currentUser != null) {
            String currentUserName = currentUser.getHoTen();
            combo.addItem(currentUserName);
            combo.setSelectedItem(currentUserName);
            
            // Disable combo box vì chỉ có một lựa chọn
            combo.setEnabled(false);
        }
        
        // Nếu đang cập nhật lịch hẹn và có thông tin bệnh nhân khác với user hiện tại
        if (lichHen != null && lichHen.getHoTenBenhNhan() != null) {
            String lichHenPatient = lichHen.getHoTenBenhNhan();
            
            // Nếu bệnh nhân trong lịch hẹn khác với user hiện tại, thêm vào combo
            if (currentUser != null && !lichHenPatient.equals(currentUser.getHoTen())) {
                combo.addItem(lichHenPatient);
            }
            combo.setSelectedItem(lichHenPatient);
        }
        
        return combo;
    }
    private JDateChooser createDateChooser(LichHen lichHen) {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(FONT_BODY);
        dateChooser.setBackground(BG_CARD);
        dateChooser.setName("dateChooser");
        
        if (lichHen != null && lichHen.getNgayHen() != null) {
            dateChooser.setDate(lichHen.getNgayHen());
        } else {
            // Đặt ngày mặc định là ngày sắp tới gần nhất
            dateChooser.setDate(getNextAvailableDateTime());
        }
        
        // Đặt ngày tối thiểu là hôm nay
        dateChooser.setMinSelectableDate(new java.util.Date());
        
        return dateChooser;
    }
    private Date getNextAvailableDateTime() {
        Calendar now = Calendar.getInstance();
        
        // Làm tròn lên đến giờ tiếp theo
        now.add(Calendar.HOUR_OF_DAY, 1);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        // Nếu là ngoài giờ làm việc, chuyển sang ngày hôm sau 8:00
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour < 8) {
            now.set(Calendar.HOUR_OF_DAY, 8);
        } else if (hour >= 17) {
            now.add(Calendar.DAY_OF_MONTH, 1);
            now.set(Calendar.HOUR_OF_DAY, 8);
        }
        
        // Nếu là cuối tuần, chuyển sang thứ 2
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            now.add(Calendar.DAY_OF_MONTH, 2);
            now.set(Calendar.HOUR_OF_DAY, 8);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            now.add(Calendar.DAY_OF_MONTH, 1);
            now.set(Calendar.HOUR_OF_DAY, 8);
        }
        
        return new java.sql.Date(now.getTimeInMillis());
    }
    private JComboBox<String> createTimeComboBox(LichHen lichHen) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setName("cbTime");
        
        for (String time : timeSlots) {
            combo.addItem(time);
        }
        
        if (lichHen != null && lichHen.getGioHen() != null) {
            combo.setSelectedItem(lichHen.getGioHen().toString().substring(0, 5));
        } else {
            // Đặt thời gian mặc định là thời gian sắp tới gần nhất
            String nextTimeSlot = getNextAvailableTimeSlot();
            combo.setSelectedItem(nextTimeSlot);
        }
        
        // Thêm listener để validate thời gian khi thay đổi
        combo.addActionListener(e -> {
            JComboBox<?> source = (JComboBox<?>) e.getSource();
            String selectedTime = source.getSelectedItem().toString();
            
            // Tìm dateChooser trong cùng form
            Container parent = source.getParent();
            while (parent != null && !(parent instanceof JDialog)) {
                parent = parent.getParent();
            }
            
            if (parent != null) {
                JDateChooser dateChooser = findComponentByName((Container) parent, "dateChooser");
                if (dateChooser != null && dateChooser.getDate() != null) {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(dateChooser.getDate());
                    
                    if (!isValidAppointmentTime(selectedDate, selectedTime)) {
                        String timeErrorMessage = getTimeErrorMessage(selectedDate, selectedTime);
                        showFieldError(combo, timeErrorMessage);
                    } else {
                        clearFieldError(combo);
                    }
                }
            }
        });
        
        return combo;
    }

    private String getNextAvailableTimeSlot() {
        Date nextTime = getNextAvailableDateTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextTime);
        
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        
        // Làm tròn lên đến khung giờ 30 phút gần nhất
        if (minute > 0 && minute <= 30) {
            minute = 30;
        } else if (minute > 30) {
            hour++;
            minute = 0;
        }
        
        // Đảm bảo trong khung giờ làm việc
        if (hour >= 17) {
            // Chuyển sang ngày hôm sau
            cal.add(Calendar.DAY_OF_MONTH, 1);
            hour = 8;
            minute = 0;
        }
        
        return String.format("%02d:%02d", hour, minute);
    }
    private JComboBox<String> createPhongKhamComboBox(LichHen lichHen) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setName("cbPhongKham");
        
        List<String> phongKhamList = controller.danhSachPhongKham();
        for (String phongKham : phongKhamList) {
            combo.addItem(phongKham);
        }
        
        if (lichHen != null && lichHen.getTenPhong() != null) {
            combo.setSelectedItem(lichHen.getTenPhong());
        }
        
        return combo;
    }
    
    private JComboBox<String> createTrangThaiComboBox(LichHen lichHen) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setName("cbTrangThai");

        String[] trangThaiList = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy", "Hoàn thành"};
        for (String trangThai : trangThaiList) {
            combo.addItem(trangThai);
        }

        // Mặc định chọn "Chờ xác nhận"
        combo.setSelectedItem("Chờ xác nhận");
        
        // Nếu có lịch hẹn và có trạng thái thì hiển thị theo trạng thái đó
        if (lichHen != null && lichHen.getTrangThai() != null) {
            combo.setSelectedItem(lichHen.getTrangThai());
        }
        
        // Không cho phép chỉnh sửa trạng thái
        combo.setEnabled(false);

        return combo;
    }
    
    private JScrollPane createMoTaTextArea(LichHen lichHen) {
        JTextArea textArea = new JTextArea(4, 20);
        textArea.setFont(FONT_BODY);
        textArea.setBackground(BG_CARD);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setName("txtMoTa");
        textArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        if (lichHen != null && lichHen.getMoTa() != null) {
            textArea.setText(lichHen.getMoTa());
        }
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(300, 100));
        
        return scrollPane;
    }
    private boolean luuLichHenMoi(JPanel formPanel) {
        try {
            // Clear previous errors
            fieldErrorLabels.values().forEach(label -> label.setVisible(false));
            if (errorPanel != null) {
                errorPanel.setVisible(false);
            }
            
            // Get components from form panel
            JComboBox<String> cbBacSi = findComponentByName(formPanel, "cbBacSi");
            JComboBox<String> cbBenhNhan = findComponentByName(formPanel, "cbBenhNhan");
            JDateChooser dateChooser = findComponentByName(formPanel, "dateChooser");
            JComboBox<String> cbTime = findComponentByName(formPanel, "cbTime");
            JComboBox<String> cbPhongKham = findComponentByName(formPanel, "cbPhongKham");
            JComboBox<String> cbTrangThai = findComponentByName(formPanel, "cbTrangThai");
            JTextArea txtMoTa = findComponentByName(formPanel, "txtMoTa");
            
            // Validate inputs
            if (cbBacSi.getSelectedItem() == null || cbBenhNhan.getSelectedItem() == null || 
                dateChooser.getDate() == null || cbTime.getSelectedItem() == null || 
                cbPhongKham.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(dateChooser.getDate());
            
            if (!isValidAppointmentDate(selectedDate)) {
                String errorMessage = getDateErrorMessage(selectedDate);
                showFieldError(dateChooser, errorMessage);
                return false;
            }
            
            // Validate time for today với thông báo lỗi chi tiết
            String selectedTime = cbTime.getSelectedItem().toString();
            if (!isValidAppointmentTime(selectedDate, selectedTime)) {
                String timeErrorMessage = getTimeErrorMessage(selectedDate, selectedTime);
                showFieldError(cbTime, timeErrorMessage);
                return false;
            }
            
            // Kiểm tra xem có phải item mặc định không
            String selectedBacSi = cbBacSi.getSelectedItem().toString();
            String selectedBenhNhan = cbBenhNhan.getSelectedItem().toString();
            String selectedPhong = cbPhongKham.getSelectedItem().toString();
            
            if (selectedBacSi.startsWith("--") || selectedBenhNhan.startsWith("--") || selectedPhong.startsWith("--")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thông tin cụ thể!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Kiểm tra trùng lịch
            if (isTimeSlotConflict(selectedDate, selectedTime, selectedBacSi, selectedPhong)) {
                // Lấy thông tin lịch hẹn hiện tại
                LichHen existingAppointment = getExistingAppointment(selectedDate, selectedTime, selectedBacSi, selectedPhong);
                
                if (existingAppointment != null) {
                    int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Đã có lịch hẹn tại thời gian này!\n" +
                        "Bệnh nhân: " + existingAppointment.getHoTenBenhNhan() + "\n" +
                        "Bác sĩ: " + existingAppointment.getHoTenBacSi() + "\n" +
                        "Phòng: " + existingAppointment.getTenPhong() + "\n" +
                        "Thời gian: " + existingAppointment.getNgayHen() + " " + existingAppointment.getGioHen() + "\n\n" +
                        "Bạn có muốn chỉnh sửa lịch hẹn hiện tại không?",
                        "Trùng lịch hẹn",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        // Đóng dialog hiện tại và mở dialog chỉnh sửa
                        SwingUtilities.getWindowAncestor(formPanel).dispose();
                        showEditAppointmentDialog(existingAppointment);
                        return true; // Trả về true để không hiển thị thông báo lỗi
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Thời gian này đã có lịch hẹn. Vui lòng chọn thời gian khác!", 
                        "Trùng lịch", 
                        JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
            
            // Tạo lịch hẹn mới
            LichHen lichHen = new LichHen();
            
            // Set values với kiểm tra null
            Integer bacSiId = controller.getBacSiIdFromName(selectedBacSi);
            Integer benhNhanId = controller.getBenhNhanIdFromName(selectedBenhNhan);
            Integer phongKhamId = controller.getPhongKhamIdFromName(selectedPhong);
            
            if (bacSiId == null || benhNhanId == null || phongKhamId == null) {
                JOptionPane.showMessageDialog(this, "Không thể lấy ID từ tên được chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            lichHen.setIdBacSi(bacSiId);
            lichHen.setIdBenhNhan(benhNhanId);
            lichHen.setNgayHen(new java.sql.Date(dateChooser.getDate().getTime()));
            lichHen.setGioHen(java.sql.Time.valueOf(selectedTime + ":00"));
            lichHen.setIdPhongKham(phongKhamId);
            lichHen.setTrangThai(cbTrangThai.getSelectedItem().toString());
            lichHen.setMoTa(txtMoTa.getText().trim());
            
            boolean result = controller.datLichHen(lichHen);           
            
            return result;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    private String getTimeErrorMessage(Calendar selectedDate, String timeSlot) {
        Calendar now = Calendar.getInstance();
        
        if (isSameDay(selectedDate, now)) {
            try {
                String[] timeParts = timeSlot.split(":");
                int selectedHour = Integer.parseInt(timeParts[0]);
                int selectedMinute = Integer.parseInt(timeParts[1]);
                
                Calendar selectedTime = (Calendar) selectedDate.clone();
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedTime.set(Calendar.MINUTE, selectedMinute);
                selectedTime.set(Calendar.SECOND, 0);
                selectedTime.set(Calendar.MILLISECOND, 0);
                
                // Tính thời gian tối thiểu cần có (15 phút trước khung giờ)
                Calendar minimumTime = (Calendar) now.clone();
                minimumTime.add(Calendar.MINUTE, 15);
                
                if (selectedTime.before(minimumTime)) {
                    // Tìm khung giờ tiếp theo có thể đặt
                    String nextAvailableTime = getNextAvailableTimeFromNow();
                    if (nextAvailableTime != null) {
                        return String.format("Vui lòng chọn thời gian từ %s trở đi.", nextAvailableTime);
                    } else {
                        return "Hôm nay đã hết giờ làm việc. Vui lòng chọn ngày khác.";
                    }
                }
            } catch (Exception e) {
                return "Thời gian không hợp lệ.";
            }
        }
        
        return "Thời gian không hợp lệ.";
    }
    private boolean isValidAppointmentTime(Calendar selectedDate, String timeSlot) {
        Calendar now = Calendar.getInstance();
        Calendar selected = (Calendar) selectedDate.clone();
        
        // Nếu là ngày hôm nay, kiểm tra thời gian
        if (isSameDay(selected, now)) {
            try {
                String[] timeParts = timeSlot.split(":");
                int selectedHour = Integer.parseInt(timeParts[0]);
                int selectedMinute = Integer.parseInt(timeParts[1]);
                
                selected.set(Calendar.HOUR_OF_DAY, selectedHour);
                selected.set(Calendar.MINUTE, selectedMinute);
                selected.set(Calendar.SECOND, 0);
                selected.set(Calendar.MILLISECOND, 0);
                
                // Thời gian tối thiểu là 15 phút sau thời điểm hiện tại
                // Ví dụ: Hiện tại 2:10, có thể đặt 2:30 (còn 20 phút)
                // Nhưng không thể đặt 2:15 (chỉ còn 5 phút)
                Calendar minimumTime = (Calendar) now.clone();
                minimumTime.add(Calendar.MINUTE, 15);
                
                return selected.after(minimumTime);
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }

    private String getNextAvailableTimeFromNow() {
        Calendar now = Calendar.getInstance();
        
        // Thêm 15 phút buffer time
        now.add(Calendar.MINUTE, 15);
        
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        
        // Nếu đã quá giờ làm việc (17:00)
        if (currentHour >= 17) {
            return null; // Không còn khung giờ nào trong ngày
        }
        
        // Tìm khung giờ 30 phút tiếp theo
        int nextHour = currentHour;
        int nextMinute;
        
        if (currentMinute <= 30) {
            nextMinute = 30;
        } else {
            nextHour++;
            nextMinute = 0;
        }
        
        // Kiểm tra lại giờ làm việc sau khi tính toán
        if (nextHour >= 17) {
            return null;
        }
        
        return String.format("%02d:%02d", nextHour, nextMinute);
    }
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    private boolean isTimeSlotConflict(Calendar date, String timeSlot, String doctorName, String roomName) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTimeInMillis());
            java.sql.Time sqlTime = java.sql.Time.valueOf(timeSlot + ":00");
            
            return controller.checkTimeSlotConflict(sqlDate, sqlTime, doctorName, roomName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private LichHen getExistingAppointment(Calendar date, String timeSlot, String doctorName, String roomName) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTimeInMillis());
            java.sql.Time sqlTime = java.sql.Time.valueOf(timeSlot + ":00");
            
            return controller.getExistingAppointment(sqlDate, sqlTime, doctorName, roomName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void showEditAppointmentDialog(LichHen lichHen) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Lịch Hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = createLichHenFormPanel(lichHen, true);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton btnCapNhat = createCompactRoundedButton("Cập nhật", ACCENT_PRIMARY, Color.WHITE);
        JButton btnHuy = createCompactRoundedButton("Hủy", DANGER_COLOR, Color.WHITE);
        
        btnCapNhat.addActionListener(e -> {
            if (capNhatLichHen(formPanel, lichHen)) {
                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private boolean capNhatLichHen(JPanel formPanel, LichHen lichHenCu) {
        try {
            // Clear previous errors
            fieldErrorLabels.values().forEach(label -> label.setVisible(false));
            if (errorPanel != null) {
                errorPanel.setVisible(false);
            }
            
            // Get components from form panel
            JComboBox<String> cbBacSi = findComponentByName(formPanel, "cbBacSi");
            JComboBox<String> cbBenhNhan = findComponentByName(formPanel, "cbBenhNhan");
            JDateChooser dateChooser = findComponentByName(formPanel, "dateChooser");
            JComboBox<String> cbTime = findComponentByName(formPanel, "cbTime");
            JComboBox<String> cbPhongKham = findComponentByName(formPanel, "cbPhongKham");
            JComboBox<String> cbTrangThai = findComponentByName(formPanel, "cbTrangThai");
            JTextArea txtMoTa = findComponentByName(formPanel, "txtMoTa");
            
            // Validate inputs
            if (cbBacSi.getSelectedItem() == null || cbBenhNhan.getSelectedItem() == null || 
                dateChooser.getDate() == null || cbTime.getSelectedItem() == null || 
                cbPhongKham.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(dateChooser.getDate());
            
            if (!isValidAppointmentDate(selectedDate)) {
                String errorMessage = getDateErrorMessage(selectedDate);
                showFieldError(dateChooser, errorMessage);
                return false;
            }
            
            // Validate time for today với thông báo lỗi chi tiết
            String selectedTime = cbTime.getSelectedItem().toString();
            if (!isValidAppointmentTime(selectedDate, selectedTime)) {
                String timeErrorMessage = getTimeErrorMessage(selectedDate, selectedTime);
                showFieldError(cbTime, timeErrorMessage);
                return false;
            }
            
            String selectedBacSi = cbBacSi.getSelectedItem().toString();
            String selectedPhong = cbPhongKham.getSelectedItem().toString();
            
            // Kiểm tra trùng lịch (ngoại trừ lịch hẹn hiện tại)
            if (isTimeSlotConflictExcluding(selectedDate, selectedTime, selectedBacSi, selectedPhong, lichHenCu.getIdLichHen())) {
                JOptionPane.showMessageDialog(this, 
                    "Thời gian này đã có lịch hẹn khác. Vui lòng chọn thời gian khác!", 
                    "Trùng lịch", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            // Update values
            lichHenCu.setHoTenBacSi(selectedBacSi);
            lichHenCu.setHoTenBenhNhan(cbBenhNhan.getSelectedItem().toString());
            lichHenCu.setNgayHen(new java.sql.Date(dateChooser.getDate().getTime()));
            lichHenCu.setGioHen(java.sql.Time.valueOf(selectedTime + ":00"));
            lichHenCu.setTenPhong(selectedPhong);
            lichHenCu.setIdPhongKham(controller.getPhongKhamIdFromName(selectedPhong));
            lichHenCu.setTrangThai(cbTrangThai.getSelectedItem().toString());
            lichHenCu.setMoTa(txtMoTa.getText().trim());
            
            return controller.updateLichHen(lichHenCu);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    private boolean isTimeSlotConflictExcluding(Calendar date, String timeSlot, String doctorName, String roomName, int excludeId) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTimeInMillis());
            java.sql.Time sqlTime = java.sql.Time.valueOf(timeSlot + ":00");
            
            return controller.checkTimeSlotConflictExcluding(sqlDate, sqlTime, doctorName, roomName, excludeId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void xoaLichHen() {
        LichHen lichHen = getLichHenFromSelectedCell();
        if (lichHen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch hẹn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa lịch hẹn này?\n" +
            "Bệnh nhân: " + lichHen.getHoTenBenhNhan() + "\n" +
            "Ngày: " + lichHen.getNgayHen() + "\n" +
            "Giờ: " + lichHen.getGioHen(),
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteLichHen(lichHen.getIdLichHen())) {
                loadData();
                selectedRow = -1;
                selectedColumn = -1;
                JOptionPane.showMessageDialog(this, "Xóa lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void xemChiTietLichHen() {
        LichHen lichHen = getLichHenFromSelectedCell();
        if (lichHen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch hẹn để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Lịch Hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel detailPanel = createChiTietPanel(lichHen);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton btnDong = createCompactRoundedButton("Đóng", ACCENT_PRIMARY, Color.WHITE);
        btnDong.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnDong);
        
        dialog.add(detailPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private JPanel createChiTietPanel(LichHen lichHen) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Chi Tiết Lịch Hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Detail fields
        panel.add(createDetailField("Mã lịch hẹn:", String.valueOf(lichHen.getIdLichHen())));
        panel.add(createDetailField("Bác sĩ:", lichHen.getHoTenBacSi()));
        panel.add(createDetailField("Bệnh nhân:", lichHen.getHoTenBenhNhan()));
        panel.add(createDetailField("Ngày hẹn:", lichHen.getNgayHen().toString()));
        panel.add(createDetailField("Giờ hẹn:", lichHen.getGioHen().toString().substring(0, 5)));
        panel.add(createDetailField("Phòng khám:", lichHen.getTenPhong()));
        panel.add(createDetailField("Trạng thái:", lichHen.getTrangThai()));
        
        if (lichHen.getMoTa() != null && !lichHen.getMoTa().trim().isEmpty()) {
            panel.add(createDetailField("Mô tả:", lichHen.getMoTa()));
        }
        
        return panel;
    }
    
    private JPanel createDetailField(String labelText, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(BG_CARD);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fieldPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_SECONDARY);
        label.setPreferredSize(new Dimension(120, 25));
        
        JLabel valueLabel = new JLabel(value != null ? value : "N/A");
        valueLabel.setFont(FONT_BODY);
        valueLabel.setForeground(TEXT_PRIMARY);
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    private LichHen getLichHenFromSelectedCell() {
        if (selectedRow < 0 || selectedColumn <= 0) return null;
        
        String timeSlot = timeSlots[selectedRow];
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromColumn(selectedColumn));
        
        Object cellValue = tableModel.getValueAt(selectedRow, selectedColumn);
        if (cellValue == null || cellValue.toString().isEmpty()) return null;
        
        String[] lines = cellValue.toString().split("\n");
        if (lines.length < 1) return null;
        
        String patientName = lines[0].replace("BN: ", "").trim();
        
        return controller.getLichHenByDateAndTime(cal.getTime(), timeSlot, patientName);
    }    
    private Date getDateFromColumn(int column) {
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_WEEK, column - 1);
        
        // THAY ĐỔI: Trả về java.sql.Date thay vì java.util.Date
        return new java.sql.Date(cal.getTimeInMillis());
    }    
    @SuppressWarnings("unchecked")
    private <T> T findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                if (component instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) component;
                    if (scrollPane.getViewport().getView() instanceof JTextArea) {
                        return (T) scrollPane.getViewport().getView();
                    }
                }
                return (T) component;
            }
            if (component instanceof Container) {
                T found = findComponentByName((Container) component, name);
                if (found != null) return found;
            }
        }
        return null;
    }
    private void resetFilters(boolean reloadData) {
        txtTimKiem.setText("");
        cbBacSi.setSelectedIndex(0);
        cbPhongKham.setSelectedIndex(0);
        dateChooser.setDate(new java.util.Date());
        currentCalendar = Calendar.getInstance();
        updateWeekLabel();
        
        if (reloadData) {
            loadData();
        }
    }  
    private void loadBacSiList() {
        cbBacSi.removeAllItems();
        cbBacSi.addItem("-- Tất cả bác sĩ --");
        
        List<String> danhSachBacSi = controller.danhSachBacSi();
        for (String bacSi : danhSachBacSi) {
            cbBacSi.addItem(bacSi);
        }
    }    
    private void loadPhongKhamList() {
        cbPhongKham.removeAllItems();
        cbPhongKham.addItem("-- Tất cả phòng khám --");
        List<String> danhSachPhong = controller.danhSachPhongKham();
        for (String phong : danhSachPhong) {
            cbPhongKham.addItem(phong);
        }
    }
}