package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
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
import model.LichHen;
import model.NguoiDung;
import util.RoundedButtonUI;
import util.ShadowBorder;

public class LichHenKhachHangPanel extends JPanel {
    private LichHenController controller;
    private JTable lichHenTable;
    private DefaultTableModel tableModel;
    private JLabel weekRangeLabel;
    private JComboBox<String> cbBacSi;
    private JComboBox<String> cbPhongKham;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnReset;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnChiTiet;
    private JDateChooser dateChooser;
    private Calendar currentCalendar;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    private int currentUserId = -1; 
    private NguoiDung currentUser = null; 
    // Để lưu trữ thông tin lịch hẹn đã chọn
    private LichHen selectedAppointment = null;
    
    private final String[] daysOfWeek = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
    private final String[] timeSlots = {
        "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    };
    private final Color BG_PRIMARY = new Color(245, 247, 250);      // Light gray-blue background
    private final Color BG_SECONDARY = new Color(255, 255, 255);    // White
    private final Color BG_ACCENT = new Color(232, 240, 254);       // Very light blue
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);    // Medium blue
    private final Color PRIMARY_DARK = new Color(21, 101, 192);     // Darker blue
    private final Color PRIMARY_LIGHT = new Color(66, 165, 245);    // Lighter blue
    private final Color SECONDARY_COLOR = new Color(66, 66, 66);    // Dark gray
    private final Color ACCENT_COLOR = new Color(211, 47, 47);      // Red
    private final Color SUCCESS_COLOR = new Color(46, 125, 50);     // Green
    private final Color WARNING_COLOR = new Color(237, 108, 2);     // Orange
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);       // Nearly black
    private final Color TEXT_SECONDARY = new Color(97, 97, 97);     // Medium gray
    private final Color TEXT_LIGHT = new Color(158, 158, 158);      // Light gray
    private final Color BORDER_COLOR = new Color(224, 224, 224);    // Light gray
    private final Color DIVIDER_COLOR = new Color(238, 238, 238);   // Very light gray
    private final Color COLOR_MORNING = new Color(232, 245, 253);   // Light blue for morning
    private final Color COLOR_AFTERNOON = new Color(255, 243, 224); // Light orange for afternoon
    private final Color COLOR_SELECTED = new Color(187, 222, 251);  // Highlighted blue when selected
    private final Color COLOR_BOOKED = new Color(224, 242, 241);    // Mint green for booked
    private final Color COLOR_OWN_BOOKED = new Color(200, 230, 201); // Different green for own appointments
    private final Color TABLE_HEADER_BG = new Color(25, 118, 210);  // Header background
    private final Color TABLE_HEADER_FG = Color.WHITE;              // Header text
    private final Color TABLE_ROW_ALT = new Color(250, 250, 250);   // Alternate row color
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // Map để lưu trữ thông tin lịch hẹn theo vị trí ô trên bảng
    private Map<String, LichHen> appointmentMap = new HashMap<>();

    public LichHenKhachHangPanel(NguoiDung user) {
        this.currentUser = user;
        controller = new LichHenController();
        currentCalendar = Calendar.getInstance();
        setupUI();
        updateUIBasedOnUserRole();
        loadData();
        setupEventListeners();
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        
        try {
            // Tải thông tin người dùng từ database
            NguoiDungController userController = new NguoiDungController();
            this.currentUser = userController.getNguoiDungById(userId);
            
            // Cập nhật giao diện dựa trên vai trò người dùng
            updateUIBasedOnUserRole();
            
            // Load dữ liệu lịch hẹn
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Không thể tải thông tin người dùng: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateUIBasedOnUserRole() {
        if (currentUser == null) return;
        
        // Kiểm tra vai trò người dùng
        String role = currentUser.getVaiTro();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isStaff = "STAFF".equalsIgnoreCase(role) || "NHÂN VIÊN".equalsIgnoreCase(role);
        
        // Nếu là người dùng thông thường, hạn chế quyền
        if (!isAdmin && !isStaff) {
            // Nút Xóa vẫn hiển thị nhưng sẽ kiểm tra quyền khi thực hiện xóa
            btnXoa.setVisible(true);
            
            // Nút Thêm chỉ thêm cho chính mình
            btnThem.setText("Đặt lịch hẹn mới");
            
            // Nút Cập nhật chỉ cập nhật lịch của mình
            btnCapNhat.setText("Cập nhật lịch hẹn");
        } else {
            // Admin hoặc nhân viên có đầy đủ quyền
            btnXoa.setVisible(true);
            btnThem.setText("Thêm lịch hẹn");
            btnCapNhat.setText("Cập nhật");
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel headerPanel = createCompactHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel footerPanel = createFooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCompactHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(BG_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(BG_PRIMARY);
        
        JPanel titleArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleArea.setBackground(BG_PRIMARY);
        
        JLabel calendarIcon = new JLabel(); 
        calendarIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        calendarIcon.setForeground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("LỊCH HẸN");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        titleArea.add(calendarIcon);
        titleArea.add(titleLabel);
        
        // Navigation controls in the center
        JPanel navigationArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navigationArea.setBackground(BG_PRIMARY);
        
        JButton btnPrevWeek = createIconButton("<", "Tuần trước");
        weekRangeLabel = new JLabel();
        weekRangeLabel.setFont(FONT_BOLD);
        weekRangeLabel.setForeground(PRIMARY_COLOR);
        weekRangeLabel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(8, 15, 8, 15)
        ));
        weekRangeLabel.setBackground(BG_SECONDARY);
        weekRangeLabel.setOpaque(true);
        
        JButton btnNextWeek = createIconButton(">", "Tuần sau");
        
        navigationArea.add(btnPrevWeek);
        navigationArea.add(weekRangeLabel);
        navigationArea.add(btnNextWeek);
        
        // Today button on the right
        JPanel todayArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        todayArea.setBackground(BG_PRIMARY);
        
        JButton btnToday = createTextButton("Hôm nay", PRIMARY_LIGHT);
        btnToday.setPreferredSize(new Dimension(110, 36));
        todayArea.add(btnToday);
        
        btnPrevWeek.addActionListener(e -> navigateWeek(-7));
        btnNextWeek.addActionListener(e -> navigateWeek(7));
        btnToday.addActionListener(e -> {
            currentCalendar = Calendar.getInstance();
            updateWeekLabel();
            loadData();
        });
        
        updateWeekLabel();
        
        topBar.add(titleArea, BorderLayout.WEST);
        topBar.add(navigationArea, BorderLayout.CENTER);
        topBar.add(todayArea, BorderLayout.EAST);
        
        // Filter panel with improved layout
        JPanel filterPanel = createFilterPanel();
        
        headerPanel.add(topBar, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(BG_SECONDARY);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Use a GridBagLayout for more precise control over filter components
        JPanel filtersContainer = new JPanel(new GridBagLayout());
        filtersContainer.setBackground(BG_SECONDARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 15); // Space between components
        
        // Date filter - using proper JDateChooser from toedter calendar
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        datePanel.setBackground(BG_SECONDARY);
        JLabel dateLabel = new JLabel("Ngày:");
        dateLabel.setFont(FONT_BOLD);
        
        // Create a JDateChooser from the com.toedter.calendar package
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        dateChooser.setFont(FONT_REGULAR);
        dateChooser.setPreferredSize(new Dimension(130, 34));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        
        datePanel.add(dateLabel, BorderLayout.NORTH);
        datePanel.add(dateChooser, BorderLayout.CENTER);
        
        // Doctor filter
        JPanel doctorPanel = new JPanel(new BorderLayout(5, 5));
        doctorPanel.setBackground(BG_SECONDARY);
        JLabel doctorLabel = new JLabel("Bác sĩ:");
        doctorLabel.setFont(FONT_BOLD);
        cbBacSi = new JComboBox<>();
        cbBacSi.setFont(FONT_REGULAR);
        cbBacSi.setPreferredSize(new Dimension(180, 34));
        styleComboBox(cbBacSi);
        loadBacSiList();
        doctorPanel.add(doctorLabel, BorderLayout.NORTH);
        doctorPanel.add(cbBacSi, BorderLayout.CENTER);
        
        // Room filter
        JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
        roomPanel.setBackground(BG_SECONDARY);
        JLabel roomLabel = new JLabel("Phòng khám:");
        roomLabel.setFont(FONT_BOLD);
        cbPhongKham = new JComboBox<>();
        cbPhongKham.setFont(FONT_REGULAR);
        cbPhongKham.setPreferredSize(new Dimension(180, 34));
        styleComboBox(cbPhongKham);
        loadPhongKhamList();
        roomPanel.add(roomLabel, BorderLayout.NORTH);
        roomPanel.add(cbPhongKham, BorderLayout.CENTER);
        
        // Search panel with larger space
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(BG_SECONDARY);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(FONT_BOLD);
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        
        JPanel searchInputPanel = new JPanel(new BorderLayout(8, 0)); // Added spacing between components
        searchInputPanel.setBackground(BG_SECONDARY);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setFont(FONT_REGULAR);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        
        // Button panel with both search and reset
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 0)); // Added spacing between buttons
        buttonPanel.setBackground(BG_SECONDARY);
        
        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setFont(FONT_BUTTON);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setBorderPainted(false);
        btnTimKiem.setBackground(PRIMARY_COLOR);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTimKiem.setPreferredSize(new Dimension(80, 34));
        
        btnReset = new JButton("Đặt lại");
        btnReset.setFont(FONT_BUTTON);
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setBackground(TEXT_SECONDARY);
        btnReset.setForeground(Color.WHITE);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setPreferredSize(new Dimension(80, 34));
        
        // Add hover effects
        btnTimKiem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTimKiem.setBackground(PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnTimKiem.setBackground(PRIMARY_COLOR);
            }
        });
        
        btnReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnReset.setBackground(TEXT_SECONDARY.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnReset.setBackground(TEXT_SECONDARY);
            }
        });
        
        buttonPanel.add(btnTimKiem);
        buttonPanel.add(btnReset);
        
        searchInputPanel.add(txtTimKiem, BorderLayout.CENTER);
        searchInputPanel.add(buttonPanel, BorderLayout.EAST);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        
        // Add components to grid with adjusted weights
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.12; // Reduce date chooser width
        filtersContainer.add(datePanel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.20; // Reduce doctor dropdown width
        filtersContainer.add(doctorPanel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.20; // Reduce room dropdown width
        filtersContainer.add(roomPanel, gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 0.48; // Increase search field width
        gbc.insets = new Insets(0, 0, 0, 0); // Remove right margin for last component
        filtersContainer.add(searchPanel, gbc);
        
        filterPanel.add(filtersContainer, BorderLayout.CENTER);
        
        btnTimKiem.addActionListener(e -> applySearch());
        btnReset.addActionListener(e -> resetFilters());
        
        return filterPanel;
    }
    private JPanel createSearchButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_SECONDARY);
        
        // Main search button
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(FONT_BUTTON);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setBorderPainted(false);
        btnTimKiem.setBackground(PRIMARY_COLOR);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setPreferredSize(new Dimension(120, 32));
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Create a custom icon for the dropdown button
        JButton dropdownButton = new JButton("\u21B3");  // Unicode arrow
        dropdownButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dropdownButton.setFocusPainted(false);
        dropdownButton.setBorderPainted(false);
        dropdownButton.setBackground(PRIMARY_COLOR.darker());
        dropdownButton.setForeground(Color.WHITE);
        dropdownButton.setPreferredSize(new Dimension(32, 32));
        dropdownButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect for search button
        btnTimKiem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTimKiem.setBackground(PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnTimKiem.setBackground(PRIMARY_COLOR);
            }
        });
        
        // Hover effect for dropdown button
        dropdownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dropdownButton.setBackground(PRIMARY_COLOR.darker().darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                dropdownButton.setBackground(PRIMARY_COLOR.darker());
            }
        });
        
        // Action listener for the main search button
        btnTimKiem.addActionListener(e -> applySearch());
        
        // Action listener for the dropdown button to show the popup menu
        dropdownButton.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(BG_SECONDARY);
            
            JMenuItem refreshItem = new JMenuItem("Làm mới bộ lọc");
            refreshItem.setFont(FONT_REGULAR);
            refreshItem.setForeground(TEXT_PRIMARY);
            refreshItem.setBackground(BG_SECONDARY);
            refreshItem.addActionListener(evt -> resetFilters());
            
            menu.add(refreshItem);
            menu.show(dropdownButton, 0, dropdownButton.getHeight());
        });
        
        // Add rounded corners using a border layout
        JPanel roundedPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        roundedPanel.setOpaque(false);
        roundedPanel.add(btnTimKiem, BorderLayout.CENTER);
        roundedPanel.add(dropdownButton, BorderLayout.EAST);
        
        panel.add(roundedPanel, BorderLayout.CENTER);
        
        return panel;
    }
 // Modified search button functionality
    private void applySearch() {
        try {
            // Get values from filters
            String searchText = txtTimKiem.getText().trim();
            java.util.Date selectedDate = dateChooser.getDate();
            String selectedDoctor = cbBacSi.getSelectedIndex() > 0 ? cbBacSi.getSelectedItem().toString() : null;
            String selectedRoom = cbPhongKham.getSelectedIndex() > 0 ? cbPhongKham.getSelectedItem().toString() : null;
            
            // If all filters are empty/default, just reset
            if (searchText.isEmpty() && selectedDoctor == null && selectedRoom == null) {
                resetFilters(false); // Reset without showing notification
                return;
            }
            
            // Update the calendar if a date is selected
            if (selectedDate != null) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime(selectedDate);
                currentCalendar = selectedCalendar;
                updateWeekLabel();
            }
            
            // Apply filter and load data
            filterAndLoadData(selectedDoctor, selectedRoom);
            
            // Show a concise notification about what was filtered
            StringBuilder message = new StringBuilder("Đã lọc theo: ");
            boolean hasFilter = false;
            
            if (selectedDoctor != null) {
                message.append("BS ").append(selectedDoctor);
                hasFilter = true;
            }
            
            if (selectedRoom != null) {
                if (hasFilter) message.append(", ");
                message.append("Phòng ").append(selectedRoom);
                hasFilter = true;
            }
            
            if (!searchText.isEmpty()) {
                if (hasFilter) message.append(", ");
                message.append("Từ khóa \"").append(searchText).append("\"");
                hasFilter = true;
            }
            
            // Only show notification for actual filters
            if (hasFilter) {
                JOptionPane.showMessageDialog(
                    this, 
                    message.toString(), 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, 
                "Lỗi khi áp dụng bộ lọc: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void filterAndLoadData(String doctor, String room) {
        // Clear existing data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        String searchText = txtTimKiem.getText().trim().toLowerCase();
        List<LichHen> dsLichHen = controller.getAllLichHen();
        List<LichHen> filteredList = new ArrayList<>();
        
        // Apply filters
        for (LichHen lichHen : dsLichHen) {
            boolean includeRecord = true;
            
            // Filter by doctor if specified
            if (doctor != null && !doctor.isEmpty() && !lichHen.getHoTenBacSi().equals(doctor)) {
                includeRecord = false;
            }
            
            // Filter by room if specified
            if (room != null && !room.isEmpty() && !lichHen.getTenPhong().equals(room)) {
                includeRecord = false;
            }
            
            // Filter by search text if specified
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
        
        // Get the start and end of the displayed week
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
        
        // Populate the table with filtered appointments
        for (LichHen lichHen : filteredList) {
            Calendar lichHenCal = Calendar.getInstance();
            lichHenCal.setTime(lichHen.getNgayHen());
            
            if (lichHenCal.getTimeInMillis() >= startOfWeek.getTimeInMillis() && 
                lichHenCal.getTimeInMillis() <= endOfWeek.getTimeInMillis()) {
                
                int dayOfWeek = lichHenCal.get(Calendar.DAY_OF_WEEK);
                int column;
                
                if (dayOfWeek == Calendar.SUNDAY) {
                    column = 7;
                } else {
                    column = dayOfWeek - 1;
                }
                
                String gioHen = new SimpleDateFormat("HH:mm").format(lichHen.getGioHen());
                int row = -1;
                for (int i = 0; i < timeSlots.length; i++) {
                    if (timeSlots[i].equals(gioHen)) {
                        row = i;
                        break;
                    }
                }
                
                if (row >= 0 && column > 0) {
                    String cellInfo = lichHen.getHoTenBenhNhan() + "\n" + lichHen.getTenPhong();
                    
                    // Show doctor name if filtering by room
                    if (room != null && !room.isEmpty()) {
                        cellInfo = lichHen.getHoTenBenhNhan() + "\nBS: " + lichHen.getHoTenBacSi();
                    }
                    
                    tableModel.setValueAt(cellInfo, row, column);
                }
            }
        }
        
        lichHenTable.repaint();
    }
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(BG_SECONDARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(TEXT_PRIMARY);
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return c;
            }
        });
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(BG_PRIMARY);
        
        createScheduleTable();
        
        JScrollPane scrollPane = new JScrollPane(lichHenTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(BG_SECONDARY);
        
        // Make the scroll pane the focal point of the UI
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
        footerPanel.setBackground(BG_PRIMARY);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel legendPanel = createLegendPanel();
        JPanel actionPanel = createActionPanel();
        
        footerPanel.add(legendPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legendPanel.setBackground(BG_PRIMARY);        
        String[] labels = {"Buổi sáng", "Buổi chiều", "Đã chọn", "Đã đặt"};
        Color[] colors = {COLOR_MORNING, COLOR_AFTERNOON, COLOR_SELECTED, COLOR_BOOKED};
        for (int i = 0; i < labels.length; i++) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            item.setBackground(BG_PRIMARY);            
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBackground(colors[i]);
            colorBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(FONT_SMALL);
            label.setForeground(TEXT_SECONDARY);            
            item.add(colorBox);
            item.add(label);
            legendPanel.add(item);
        }        
        return legendPanel;
    }
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setBackground(BG_PRIMARY);
        
        btnThem = createTextButton("Thêm lịch hẹn", SUCCESS_COLOR);
        btnCapNhat = createTextButton("Cập nhật", PRIMARY_COLOR);
        btnXoa = createTextButton("Xóa", ACCENT_COLOR);
        
        actionPanel.add(btnThem);
        actionPanel.add(btnCapNhat);
        actionPanel.add(btnXoa);
        
        return actionPanel;
    }    
    private JButton createTextButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (bgColor != null) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setBorder(new EmptyBorder(0, 0, 0, 0));
        } else {
            button.setBackground(BG_SECONDARY);
            button.setForeground(TEXT_PRIMARY);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        }
        button.setUI(new RoundedButtonUI(10));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor != null) {
                    button.setBackground(bgColor.darker());
                } else {
                    button.setBackground(BG_ACCENT);
                }
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                if (bgColor != null) {
                    button.setBackground(bgColor);
                } else {
                    button.setBackground(BG_SECONDARY);
                }
            }
        });        
        return button;
    }    
    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(36, 36));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(BG_SECONDARY);
        button.setForeground(PRIMARY_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setUI(new RoundedButtonUI(18));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BG_ACCENT);
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BG_SECONDARY);
            }
        });
        
        return button;
    }
    private void createScheduleTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };        
        tableModel.addColumn("Giờ");
        for (String day : daysOfWeek) {
            tableModel.addColumn(day);
        }        
        for (String timeSlot : timeSlots) {
            Object[] rowData = new Object[8];
            rowData[0] = timeSlot;
            tableModel.addRow(rowData);
        }        
        lichHenTable = new JTable(tableModel);
        lichHenTable.setRowHeight(60);
        lichHenTable.setShowVerticalLines(true);
        lichHenTable.setShowHorizontalLines(true);
        lichHenTable.setGridColor(BORDER_COLOR);
        lichHenTable.getTableHeader().setReorderingAllowed(false);
        lichHenTable.getTableHeader().setResizingAllowed(false);
        lichHenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lichHenTable.setFont(FONT_REGULAR);
        lichHenTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lichHenTable.setRowSelectionAllowed(false);
        lichHenTable.setCellSelectionEnabled(true);
        lichHenTable.setIntercellSpacing(new Dimension(1, 1));
        
        JTableHeader header = lichHenTable.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setVerticalAlignment(JLabel.CENTER);
        
        lichHenTable.setDefaultRenderer(Object.class, new ScheduleTableCellRenderer());
        
        lichHenTable.getColumnModel().getColumn(0).setMaxWidth(80);
        lichHenTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        
        for (int i = 1; i < lichHenTable.getColumnCount(); i++) {
            lichHenTable.getColumnModel().getColumn(i).setPreferredWidth(160);
        }
    }
    
    private class ScheduleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            if (row == selectedRow && column == selectedColumn) {
                panel.setBackground(COLOR_SELECTED); // Selected cell
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            } else {
                if (column == 0) {
                    panel.setBackground(BG_ACCENT); // Time column
                    panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
                } else if (row < 10) {
                    panel.setBackground(COLOR_MORNING); // Morning
                    if (row % 2 == 0) {
                        panel.setBackground(panel.getBackground().brighter());
                    }
                } else {
                    panel.setBackground(COLOR_AFTERNOON); // Afternoon
                    if (row % 2 == 0) {
                        panel.setBackground(panel.getBackground().brighter());
                    }
                }
            }
            if (value != null) {
                if (column == 0) {
                    // Time column formatting
                    JLabel timeLabel = new JLabel(value.toString());
                    timeLabel.setFont(FONT_BOLD);
                    timeLabel.setHorizontalAlignment(JLabel.CENTER);
                    timeLabel.setForeground(PRIMARY_COLOR);
                    panel.add(timeLabel, BorderLayout.CENTER);
                } else {
                    JPanel contentPanel = new JPanel(new BorderLayout(3, 3));
                    contentPanel.setOpaque(false);
                    
                    String[] lines = value.toString().split("\n");
                    
                    if (lines.length >= 1) {
                        JLabel nameLabel = new JLabel(lines[0]);
                        nameLabel.setFont(FONT_BOLD);
                        nameLabel.setForeground(TEXT_PRIMARY);
                        contentPanel.add(nameLabel, BorderLayout.NORTH);
                        
                        if (lines.length >= 2) {
                            JLabel roomLabel = new JLabel(lines[1]);
                            roomLabel.setFont(FONT_SMALL);
                            roomLabel.setForeground(TEXT_SECONDARY);
                            contentPanel.add(roomLabel, BorderLayout.CENTER);
                        }                        
                        panel.setBackground(COLOR_BOOKED);
                        
                        JPanel indicator = new JPanel();
                        indicator.setPreferredSize(new Dimension(5, panel.getHeight()));
                        indicator.setBackground(SUCCESS_COLOR);
                        panel.add(indicator, BorderLayout.WEST);
                    }                    
                    panel.add(contentPanel, BorderLayout.CENTER);
                }
            } else if (column > 0) {
                if (row == selectedRow && column == selectedColumn) {
                    JLabel plusLabel = new JLabel("+");
                    plusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    plusLabel.setForeground(PRIMARY_COLOR);
                    plusLabel.setHorizontalAlignment(JLabel.CENTER);
                    panel.add(plusLabel, BorderLayout.CENTER);
                }
            }

            return panel;
        }
    }    
    private void updateWeekLabel() {
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startDate = new Date(cal.getTimeInMillis());
        
        cal.add(Calendar.DATE, 6);
        Date endDate = new Date(cal.getTimeInMillis());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        weekRangeLabel.setText("Tuần từ " + dateFormat.format(startDate) + " đến " + dateFormat.format(endDate));
    }    
    private void navigateWeek(int days) {
        currentCalendar.add(Calendar.DATE, days);
        updateWeekLabel();
        loadData();
    }    
    private void loadData() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }
        
        List<LichHen> dsLichHen = controller.getAllLichHen();
        
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
            Calendar lichHenCal = Calendar.getInstance();
            lichHenCal.setTime(lichHen.getNgayHen());
            
            if (lichHenCal.getTimeInMillis() >= startOfWeek.getTimeInMillis() && 
                lichHenCal.getTimeInMillis() <= endOfWeek.getTimeInMillis()) {
                
                int dayOfWeek = lichHenCal.get(Calendar.DAY_OF_WEEK);
                int column;                
                if (dayOfWeek == Calendar.SUNDAY) {
                    column = 7;
                } else {
                    column = dayOfWeek - 1;
                }                
                String gioHen = new SimpleDateFormat("HH:mm").format(lichHen.getGioHen());
                int row = -1;
                for (int i = 0; i < timeSlots.length; i++) {
                    if (timeSlots[i].equals(gioHen)) {
                        row = i;
                        break;
                    }
                }                
                if (row >= 0 && column > 0) {
                    String cellInfo = lichHen.getHoTenBenhNhan() + "\n" + lichHen.getTenPhong();
                    
                    tableModel.setValueAt(cellInfo, row, column);
                }
            }
        }        
        lichHenTable.repaint();
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
    private void timKiemLichHen() {
        JOptionPane.showMessageDialog(this, "Chức năng tìm kiếm đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }    
    private void resetFilters() {
        resetFilters(true);
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
    private void themLichHen() {
        JOptionPane.showMessageDialog(this, "Chức năng thêm lịch hẹn đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
    private void showAppointmentDetails(Calendar date, String timeSlot) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel dateLabel = new JLabel("Ngày: " + dateFormat.format(date.getTime()));
        JLabel timeLabel = new JLabel("Giờ: " + timeSlot);
        dateLabel.setFont(FONT_BOLD);
        timeLabel.setFont(FONT_BOLD);        
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(timeLabel);
        
        Object value = lichHenTable.getValueAt(
                getTimeSlotRow(timeSlot), 
                getDayOfWeekColumn(date.get(Calendar.DAY_OF_WEEK)));
        if (value != null) {
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JSeparator());
            panel.add(Box.createVerticalStrut(10));
            
            String[] lines = ((String)value).split("\n");
            JLabel patientLabel = new JLabel("Bệnh nhân: " + lines[0]);
            patientLabel.setFont(FONT_REGULAR);
            panel.add(patientLabel);            
            if (lines.length > 1) {
                JLabel roomLabel = new JLabel("Phòng khám: " + lines[1]);
                roomLabel.setFont(FONT_REGULAR);
                panel.add(Box.createVerticalStrut(5));
                panel.add(roomLabel);
            }
        } else {
            panel.add(Box.createVerticalStrut(10));
            JLabel availableLabel = new JLabel("Khung giờ này đang trống!");
            availableLabel.setFont(FONT_REGULAR);
            panel.add(availableLabel);
        }
        
        JOptionPane.showMessageDialog(this, panel, "Chi tiết lịch hẹn", JOptionPane.INFORMATION_MESSAGE);
    }    
    private int getTimeSlotRow(String timeSlot) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equals(timeSlot)) {
                return i;
            }
        }
        return -1;
    }    
    private int getDayOfWeekColumn(int dayOfWeek) {
        if (dayOfWeek == Calendar.SUNDAY) {
            return 7;
        } else {
            return dayOfWeek - 1;
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
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Lịch Hẹn Khách Hàng");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1000, 700);
//            frame.setLocationRelativeTo(null);
//            
//            LichHenKhachHangPanel panel = new LichHenKhachHangPanel();
//            frame.getContentPane().add(panel);
//            
//            frame.setVisible(true);
//        });
//    }
}