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
    
    // Popup menu components
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

    private static final Color COLOR_SELECTED = new Color(59, 130, 246);     // Blue selection
    private static final Color COLOR_BOOKED = new Color(220, 252, 231);      // Light mint green for booked
    private static final Color COLOR_HOVER = new Color(241, 245, 249);       // Light hover effect
    
    // Professional Typography
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private Color primaryColor = new Color(41, 128, 185); // Modern blue
    private Color secondaryColor = new Color(245, 248, 250);
    private Color successColor = new Color(86, 156, 104);
    private Color errorBorderColor = new Color(231, 76, 60); // Red for error borders
    private Color normalBorderColor = new Color(200, 200, 200); // Normal border color
    private Color headerTextColor = Color.WHITE; // Header text color
    private int cornerRadius = 10;
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);

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
            loadData();
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0)); // Reduced spacing
        filterPanel.setBackground(BG_SECONDARY);
        
        // Compact date picker
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dateChooser.setPreferredSize(new Dimension(90, 26)); // Smaller
        dateChooser.setBackground(BG_CARD);
        dateChooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        // Compact combos
        cbBacSi = createCompactComboBox(110); // Reduced width
        loadBacSiList();
        
        cbPhongKham = createCompactComboBox(90); // Reduced width
        loadPhongKhamList();
        
        // Compact search field
        txtTimKiem = createCompactTextField(110); // Reduced width
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(3, 6, 3, 6) // Reduced padding
        ));
        txtTimKiem.setToolTipText("Tìm kiếm...");
        
        // Compact search button
        btnTimKiem = createCompactButton("Tìm", ACCENT_PRIMARY);
        btnTimKiem.setPreferredSize(new Dimension(45, 26));
        
        filterPanel.add(dateChooser);
        filterPanel.add(cbBacSi);
        filterPanel.add(cbPhongKham);
        filterPanel.add(txtTimKiem);
        filterPanel.add(btnTimKiem);
        
        btnTimKiem.addActionListener(e -> applySearch());
        
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
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); // Reduced spacing
        legendPanel.setBackground(BG_SECONDARY);
        
        String[] labels = {"Buổi sáng", "Buổi chiều", "Đã chọn", "Đã đặt"};
        Color[] colors = {COLOR_MORNING, COLOR_AFTERNOON, COLOR_SELECTED, COLOR_BOOKED};
        
        for (int i = 0; i < labels.length; i++) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); // Reduced spacing
            item.setBackground(BG_SECONDARY);
            
            // Color indicator square
            JPanel colorSquare = new JPanel();
            colorSquare.setPreferredSize(new Dimension(14, 14)); // Smaller square
            colorSquare.setBackground(colors[i]);
            colorSquare.setBorder(BorderFactory.createLineBorder(BORDER_STRONG, 1));
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
            label.setForeground(TEXT_SECONDARY);
            
            item.add(colorSquare);
            item.add(label);
            legendPanel.add(item);
        }
        
        return legendPanel;
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

    // Updated createCompactRoundedButton method
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

    // Updated createCompactButton method
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

    // Updated createCompactNavButton method
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

    // Updated createCompactActionPanel method
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

    // Additional helper method for lighter colors
    private Color lightenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], Math.max(0.0f, hsb[1] - 0.1f), Math.min(1.0f, hsb[2] + 0.1f));
    }

    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }

    // Helper methods for color manipulation
    private Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
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
                panel.setBorder(BorderFactory.createLineBorder(ACCENT_PRIMARY, 2));
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
                    // Time column - compact display
                    JLabel timeLabel = new JLabel(value.toString());
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Smaller font
                    timeLabel.setHorizontalAlignment(JLabel.CENTER);
                    timeLabel.setVerticalAlignment(JLabel.CENTER);
                    timeLabel.setForeground(TEXT_PRIMARY);
                    panel.add(timeLabel, BorderLayout.CENTER);
                } else {
                    // Appointment cell - compact layout
                    String[] lines = value.toString().split("\n");
                    
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setOpaque(false);
                    contentPanel.setBorder(new EmptyBorder(3, 6, 3, 6)); // Reduced padding
                    
                    if (lines.length >= 1) {
                        JLabel nameLabel = new JLabel("BN: " + lines[0]);
                        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Smaller font
                        nameLabel.setForeground(TEXT_PRIMARY);
                        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        contentPanel.add(nameLabel);                                           
                        
                        if (lines.length >= 2) {
                            JLabel roomLabel = new JLabel("P: " + lines[1]); // Shortened "Phòng" to "P"
                            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9)); // Smaller font
                            roomLabel.setForeground(TEXT_SECONDARY);
                            roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            contentPanel.add(Box.createVerticalStrut(2)); // Reduced spacing
                            contentPanel.add(roomLabel);
                        }
                        
                        panel.setBackground(COLOR_BOOKED);
                        
                        // Success indicator bar
                        JPanel indicator = new JPanel();
                        indicator.setPreferredSize(new Dimension(3, panel.getHeight())); // Thinner indicator
                        indicator.setBackground(SUCCESS_COLOR);
                        panel.add(indicator, BorderLayout.WEST);
                    }
                    
                    panel.add(contentPanel, BorderLayout.CENTER);
                }
            } else if (column > 0) {
                // Empty cell with selection indicator
                if (row == selectedRow && column == selectedColumn) {
                    JLabel plusLabel = new JLabel("+");
                    plusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Smaller plus
                    plusLabel.setForeground(ACCENT_PRIMARY);
                    plusLabel.setHorizontalAlignment(JLabel.CENTER);
                    plusLabel.setVerticalAlignment(JLabel.CENTER);
                    panel.add(plusLabel, BorderLayout.CENTER);
                }
            }

            return panel;
        }
    }
    private class ModernScheduleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new BorderLayout(0, 0));
            
            // Background colors based on selection and time
            if (row == selectedRow && column == selectedColumn) {
                panel.setBackground(COLOR_SELECTED);
                panel.setBorder(BorderFactory.createLineBorder(ACCENT_PRIMARY, 3));
            } else {
                if (column == 0) {
                    panel.setBackground(TABLE_HEADER_BG);
                    panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, BORDER_STRONG));
                } else if (row < MORNING_SLOTS_COUNT) { // Buổi sáng
                    panel.setBackground(COLOR_MORNING);
                    panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                } else { // Buổi chiều
                    panel.setBackground(COLOR_AFTERNOON);
                    panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                }
            }
            
            if (value != null && !value.toString().isEmpty()) {
                if (column == 0) {
                    // Time column
                    JLabel timeLabel = new JLabel(value.toString());
                    timeLabel.setFont(FONT_HEADER);
                    timeLabel.setHorizontalAlignment(JLabel.CENTER);
                    timeLabel.setVerticalAlignment(JLabel.CENTER);
                    timeLabel.setForeground(TEXT_PRIMARY);
                    panel.add(timeLabel, BorderLayout.CENTER);
                } else {
                    // Appointment cell
                    String[] lines = value.toString().split("\n");
                    
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setOpaque(false);
                    contentPanel.setBorder(new EmptyBorder(6, 10, 6, 10));
                    
                    if (lines.length >= 1) {
                        JLabel nameLabel = new JLabel("BN: " + lines[0]);
                        nameLabel.setFont(FONT_BOLD);
                        nameLabel.setForeground(TEXT_PRIMARY);
                        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        contentPanel.add(nameLabel);                                           
                        
                        if (lines.length >= 2) {
                            JLabel roomLabel = new JLabel("Phòng: " + lines[1]);
                            roomLabel.setFont(FONT_SMALL);
                            roomLabel.setForeground(TEXT_SECONDARY);
                            roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            contentPanel.add(Box.createVerticalStrut(4));
                            contentPanel.add(roomLabel);
                        }
                        
                        panel.setBackground(COLOR_BOOKED);
                        
                        // Success indicator bar
                        JPanel indicator = new JPanel();
                        indicator.setPreferredSize(new Dimension(4, panel.getHeight()));
                        indicator.setBackground(SUCCESS_COLOR);
                        panel.add(indicator, BorderLayout.WEST);
                    }
                    
                    panel.add(contentPanel, BorderLayout.CENTER);
                }
            } else if (column > 0) {
                // Empty cell with selection indicator
                if (row == selectedRow && column == selectedColumn) {
                    JLabel plusLabel = new JLabel("+");
                    plusLabel.setFont(new Font("Inter", Font.BOLD, 20));
                    plusLabel.setForeground(ACCENT_PRIMARY);
                    plusLabel.setHorizontalAlignment(JLabel.CENTER);
                    plusLabel.setVerticalAlignment(JLabel.CENTER);
                    panel.add(plusLabel, BorderLayout.CENTER);
                }
            }

            return panel;
        }
    }
    // Keep all existing logic methods unchanged
    private void updateWeekLabel() {
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startDate = new Date(cal.getTimeInMillis());
        
        cal.add(Calendar.DATE, 6);
        Date endDate = new Date(cal.getTimeInMillis());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        weekRangeLabel.setText(dateFormat.format(startDate) + " - " + dateFormat.format(endDate));
    }

    private void navigateWeek(int days) {
        currentCalendar.add(Calendar.DATE, days);
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
        // Clear current data - đơn giản hóa
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        String searchText = txtTimKiem.getText().trim().toLowerCase();
        List<LichHen> dsLichHen = controller.getAllLichHen();
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
        // Clear current data - không cần skip header rows nữa
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        List<LichHen> dsLichHen = new ArrayList<>();
        try {
            try {
                dsLichHen = controller.getAllLichHen();
            } catch (IllegalArgumentException e) {
                if (currentUser != null) {
                    try {
                        if ("PATIENT".equalsIgnoreCase(currentUser.getVaiTro()) || 
                            "BỆNH NHÂN".equalsIgnoreCase(currentUser.getVaiTro())) {
                            dsLichHen = controller.getLichHenByUserId(currentUser.getIdNguoiDung());
                        } else if ("DOCTOR".equalsIgnoreCase(currentUser.getVaiTro()) || 
                                  "BÁC SĨ".equalsIgnoreCase(currentUser.getVaiTro())) {
                            dsLichHen = controller.layLichHenTheoBacSi(currentUser.getIdNguoiDung());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAppointmentsToTable(dsLichHen);
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
        btnThem.addActionListener(e -> themLichHen());
        btnCapNhat.addActionListener(e -> capNhatLichHen());
        btnXoa.addActionListener(e -> xoaLichHen());
        
        // Add Enter key listener for search field
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    applySearch();
                }
            }
        });
        
        lichHenTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = lichHenTable.rowAtPoint(e.getPoint());
                selectedColumn = lichHenTable.columnAtPoint(e.getPoint());
                if (selectedColumn > 0 && selectedRow >= 0) {
                    lichHenTable.repaint();
                }
            }
        });
        lichHenTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = lichHenTable.getSelectedRow();
                    int col = lichHenTable.getSelectedColumn();
                    
                    if (col > 0 && tableModel.getValueAt(row, col) == null) {
                        themLichHen();
                    }
                }
            }
        });
    }
    private void themLichHen() {
        JOptionPane.showMessageDialog(this, "Chức năng thêm lịch hẹn đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }   
    private void resetFilters(boolean showNotification) {
        dateChooser.setDate(new java.util.Date());
        cbBacSi.setSelectedIndex(0);
        cbPhongKham.setSelectedIndex(0);
        txtTimKiem.setText("");
        
        currentCalendar = Calendar.getInstance();
        updateWeekLabel();
        loadData();
        
        if (showNotification) {
            JOptionPane.showMessageDialog(
                this,
                "Đã xóa tất cả bộ lọc.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void capNhatLichHen() {
        int row = lichHenTable.getSelectedRow();
        int col = lichHenTable.getSelectedColumn();
        
        if (row < 0 || col <= 0) {
            showWarningDialog("Vui lòng chọn một lịch hẹn để cập nhật!");
            return;
        }
        
        JOptionPane.showMessageDialog(this, "Chức năng cập nhật lịch hẹn đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }    
    private void xoaLichHen() {
        int row = lichHenTable.getSelectedRow();
        int col = lichHenTable.getSelectedColumn();
        
        if (row < 0 || col <= 0) {
            showWarningDialog("Vui lòng chọn một lịch hẹn để xóa!");
            return;
        }        
        int option = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa lịch hẹn này không?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);       
        if (option == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Chức năng xóa lịch hẹn đang được phát triển!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }    
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(
            this, message, "Lưu ý", JOptionPane.WARNING_MESSAGE
        );
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