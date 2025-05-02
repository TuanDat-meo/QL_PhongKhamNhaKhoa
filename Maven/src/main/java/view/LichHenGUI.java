package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import controller.LichHenController;
import model.KhoVatTu;
import model.LichHen;
import util.RoundedPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LichHenGUI extends JPanel {
    private JPanel calendarPanel;
    private JLabel weekLabel;
    private JButton prevWeekButton, nextWeekButton;
    private LichHenController qlLichHen;
    private LocalDate currentWeekStart;
    private JDateChooser dateChooser;
    private JPanel searchResultsPanel;
    private JButton addAppointmentButton;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemChinhSua;
    private JMenuItem menuItemXoa;
    private JSplitPane splitPane; // Added split pane for better layout management
    private JScrollPane calendarScrollPane;
    private JScrollPane searchScrollPane;
    private JPanel searchResultsContainer; // Container for search results header and content
    
    // Colors for UI styling
    private Color accentColor = new Color(192, 80, 77); // For delete button
    private Color borderColor = new Color(222, 226, 230); // Border color
    private Color primaryColor = new Color(41, 128, 185);    // Màu xanh dương hiện đại
    private Color secondaryColor = new Color(245, 248, 250); // Màu nền nhẹ nhàng
    private Color headerBgColor = new Color(41, 128, 185);   // Màu nền cho header
    private Color headerTextColor = Color.WHITE;             // Màu chữ header
    private int cornerRadius = 10;
    // Font for buttons
    private Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
    
    public LichHenGUI() {
        qlLichHen = new LichHenController();
        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        initialize();
        setupPopupMenu();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(secondaryColor);
        
        // ========== 1. HEADER PANEL ==========
        // Header với nền màu và chữ trắng - hiện đại hơn
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(headerTextColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // 1.1 Panel dòng đầu tiên - có tiêu đề và nút thêm lịch hẹn
        JPanel headerTopRow = new JPanel(new BorderLayout(10, 0));
        headerTopRow.setOpaque(false);
        
        // Tiêu đề chính với font đẹp
        JLabel titleLabel = new JLabel("LỊCH HẸN KHÁM BỆNH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        
        // Nút thêm lịch hẹn - thiết kế hiện đại với góc bo tròn
        addAppointmentButton = createRoundedButton("+ Thêm lịch hẹn", primaryColor, headerTextColor, cornerRadius);
        addAppointmentButton.addActionListener(e -> addAppointment());
        
        headerTopRow.add(titleLabel, BorderLayout.WEST);
        headerTopRow.add(addAppointmentButton, BorderLayout.EAST);
        
        headerPanel.add(headerTopRow);
        headerPanel.add(Box.createVerticalStrut(1));
       
        // ========== 2. TOOLBAR PANEL ==========
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        
        // 2.1 Panel chứa các nút chuyển tuần và ngày
        JPanel navigationRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        navigationRow.setOpaque(false);
        
        // Tạo các nút điều hướng với góc bo tròn
        prevWeekButton = createRoundedButton("<", primaryColor, Color.WHITE, cornerRadius);
        prevWeekButton.addActionListener(e -> changeWeek(-1));
        
        nextWeekButton = createRoundedButton(">", primaryColor, Color.WHITE, cornerRadius);
        nextWeekButton.addActionListener(e -> changeWeek(1));
        
        // Label hiển thị tuần
        weekLabel = new JLabel(getFormattedWeek(), SwingConstants.LEFT);
        weekLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        weekLabel.setForeground(primaryColor);
        weekLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        
        // Chọn ngày với thiết kế đẹp hơn
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        dateChooser.setPreferredSize(new Dimension(100, 25));
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        
        // Tùy chỉnh text field trong date chooser
        JTextField dateField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateField.setHorizontalAlignment(JTextField.CENTER);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateField.setBorder(BorderFactory.createEmptyBorder());
        
        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                LocalDate selectedDate = ((java.util.Date) evt.getNewValue()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                currentWeekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);
                updateCalendar();
            }
        });
        
        // Button hiện ngày hôm nay với góc bo tròn
        JButton todayButton = createRoundedButton("Hôm nay", primaryColor, headerTextColor, cornerRadius);
        todayButton.addActionListener(e -> {
            currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
            updateCalendar();
        });
        
        navigationRow.add(prevWeekButton);
        navigationRow.add(weekLabel);
        navigationRow.add(nextWeekButton);
        navigationRow.add(Box.createHorizontalStrut(10));
        navigationRow.add(dateChooser);
        navigationRow.add(Box.createHorizontalStrut(5));
        navigationRow.add(todayButton);
        
        // 2.2 Tìm kiếm được thiết kế lại với bo góc
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchRow.setOpaque(false);

        // Custom border color for search field
        Color searchBorderColor = new Color(41, 128, 185); // Use primary color for border

        // Tạo search field với button tìm kiếm và bo góc
        JPanel searchFieldPanel = new RoundedPanel(cornerRadius, new BorderLayout());
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        searchFieldPanel.setPreferredSize(new Dimension(230, 28));

        // Add a border effect by creating a custom panel with border
        RoundedPanel searchOuterPanel = new RoundedPanel(cornerRadius + 2, new BorderLayout());
        searchOuterPanel.setBackground(searchBorderColor);
        searchOuterPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        searchOuterPanel.add(searchFieldPanel);

        JTextField searchField = new JTextField(18);
        searchField.setOpaque(false); // nền trong
        searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); // vẫn hiển thị viền
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setOpaque(false);

        // Nút tìm kiếm với góc bo tròn
        JButton searchButton = createRoundedButton("Tìm", primaryColor, Color.WHITE, cornerRadius);
        searchButton.addActionListener(e -> performSearch(searchField.getText()));

        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(searchButton, BorderLayout.EAST);
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch(searchField.getText());
                }
            }
        });
        
        searchRow.add(searchFieldPanel);
        
        // Panel chứa 2 hàng của toolbar
        JPanel toolbarContent = new JPanel(new BorderLayout());
        toolbarContent.setOpaque(false);
        toolbarContent.add(navigationRow, BorderLayout.WEST);
        toolbarContent.add(searchRow, BorderLayout.EAST);
        
        toolbarPanel.add(toolbarContent);
        
        // Thêm các panel vào mainPanel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.CENTER);
        
        // Thêm mainPanel vào top của giao diện
        add(mainPanel, BorderLayout.NORTH);
        
        // ========== 3. CONTENT AREA ==========
        // Cấu hình calendar panel
        calendarPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        calendarPanel.setBackground(secondaryColor);
        
        // Panel kết quả tìm kiếm
        searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
        searchResultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchResultsPanel.setBackground(secondaryColor);
        
        // Container for search results with header
        searchResultsContainer = new JPanel(new BorderLayout());
        searchResultsContainer.setBackground(secondaryColor);
        searchResultsContainer.add(searchResultsPanel, BorderLayout.CENTER);
        
        // Create scroll panes
        calendarScrollPane = new JScrollPane(calendarPanel);
        calendarScrollPane.setBorder(BorderFactory.createEmptyBorder());
        calendarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        calendarScrollPane.setBackground(secondaryColor);
        
        searchScrollPane = new JScrollPane(searchResultsContainer);
        searchScrollPane.setBorder(BorderFactory.createEmptyBorder());
        searchScrollPane.setBackground(secondaryColor);
        
        // Create split pane to manage the space between calendar and search results
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, calendarScrollPane, searchScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(400); // Default divider location
        splitPane.setResizeWeight(0.7); // Calendar gets 70% of resize weight
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Set minimum sizes to prevent components from disappearing
        calendarScrollPane.setMinimumSize(new Dimension(0, 200));
        searchScrollPane.setMinimumSize(new Dimension(0, 100));
        
        // Add component listener to adjust split pane when the frame is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustSplitPane();
            }
        });
        
        // Cập nhật lịch
        updateCalendar();
    }
    
     private void adjustSplitPane() {
        if (searchResultsPanel.getComponentCount() == 0) {
            // No search results, give all space to calendar
            splitPane.setDividerLocation(1.0);
        } else {
            // Search results exist, allocate space proportionally
            int totalHeight = getHeight() - 100; // Accounting for header
            int calendarHeight = (int)(totalHeight * 0.7);
            int searchHeight = totalHeight - calendarHeight;
            
            // Ensure minimum heights
            if (calendarHeight < 200) calendarHeight = 200;
            if (searchHeight < 100) searchHeight = 100;
            
            splitPane.setDividerLocation(calendarHeight);
        }
    }

    // Thay thế phương thức tạo nút tròn bằng phương thức tạo nút hình chữ nhật
    private JButton createRectangleButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(color));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        // Kích thước nút
        button.setPreferredSize(new Dimension(30, 24));
        
        return button;
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
    
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    
    private void performSearch(String query) {
        searchResultsContainer.removeAll();
        searchResultsPanel.removeAll();

        if (query.isEmpty()) {
            adjustSplitPane(); // Adjust to hide search results area
            splitPane.revalidate();
            splitPane.repaint();
            updateCalendar();
            return;
        }

        // Create a header panel for search results
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 248, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        JLabel resultsHeader = new JLabel("Kết quả tìm kiếm cho: \"" + query + "\"");
        resultsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultsHeader.setForeground(primaryColor);
        
        // Add close button to header
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(new Color(108, 117, 125));
        closeButton.setBackground(null);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            searchResultsContainer.removeAll();
            searchResultsPanel.removeAll();
            adjustSplitPane();
            splitPane.revalidate();
            splitPane.repaint();
            updateCalendar();
        });
        
        headerPanel.add(resultsHeader, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        searchResultsContainer.add(headerPanel, BorderLayout.NORTH);
        searchResultsContainer.add(searchResultsPanel, BorderLayout.CENTER);

        // Filter appointments based on search query
        List<LichHen> searchResults = new ArrayList<>();
        for (LichHen lichHen : qlLichHen.getAllLichHen()) {
            if (lichHen.getHoTenBacSi().toLowerCase().contains(query.toLowerCase()) ||
                    lichHen.getHoTenBenhNhan().toLowerCase().contains(query.toLowerCase()) ||
                    lichHen.getTenPhong().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(lichHen);
            }
        }

        if (searchResults.isEmpty()) {
            JPanel noResultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            noResultsPanel.setBackground(Color.WHITE);
            noResultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel noResultsLabel = new JLabel("Không tìm thấy kết quả nào phù hợp.");
            noResultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noResultsPanel.add(noResultsLabel);
            
            searchResultsPanel.add(noResultsPanel);
        } else {
            // Sort results by date and time
            searchResults.sort(Comparator.comparing(LichHen::getNgayHen)
                                        .thenComparing(LichHen::getGioHen));
            
            // Create a container for all result items
            JPanel resultsContainer = new JPanel();
            resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
            resultsContainer.setBackground(Color.WHITE);
            
            // Format and display each result
            for (LichHen lichHen : searchResults) {
                JPanel resultPanel = createSearchResultPanel(lichHen);
                resultsContainer.add(resultPanel);
                // Add a separator except after the last item
                if (searchResults.indexOf(lichHen) < searchResults.size() - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    separator.setForeground(new Color(240, 240, 240));
                    resultsContainer.add(separator);
                }
            }
            
            searchResultsPanel.add(resultsContainer);
        }

        // Update calendar to highlight search results
        updateCalendarWithSearchResults(searchResults);
        
        // Adjust the split pane to make search results visible
        adjustSplitPane();
        splitPane.revalidate();
        splitPane.repaint();
    }
    
    private JPanel createSearchResultPanel(LichHen lichHen) {
        JPanel resultPanel = new JPanel(new BorderLayout(10, 0));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        resultPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status indicator (left side)
        JPanel statusIndicator = new JPanel();
        statusIndicator.setPreferredSize(new Dimension(8, 30));
        statusIndicator.setBackground(getColorByStatus(lichHen.getTrangThai()));
        resultPanel.add(statusIndicator, BorderLayout.WEST);
        
        // Main content (center)
        JPanel contentPanel = new JPanel(new GridLayout(2, 1));
        contentPanel.setOpaque(false);
        
        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(lichHen.getNgayHen());
        String formattedTime = timeFormat.format(lichHen.getGioHen());
        
        // First row: Patient name and appointment date/time
        JPanel firstRow = new JPanel(new BorderLayout());
        firstRow.setOpaque(false);
        
        JLabel patientLabel = new JLabel(lichHen.getHoTenBenhNhan());
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        patientLabel.setForeground(new Color(44, 62, 80));
        
        JLabel dateTimeLabel = new JLabel(formattedDate + " lúc " + formattedTime);
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setForeground(new Color(108, 117, 125));
        
        firstRow.add(patientLabel, BorderLayout.WEST);
        firstRow.add(dateTimeLabel, BorderLayout.EAST);
        
        // Second row: Doctor name and room
        JPanel secondRow = new JPanel(new BorderLayout());
        secondRow.setOpaque(false);
        
        JLabel doctorLabel = new JLabel("BS: " + lichHen.getHoTenBacSi());
        doctorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        doctorLabel.setForeground(new Color(108, 117, 125));
        
        JLabel roomLabel = new JLabel("Phòng: " + lichHen.getTenPhong());
        roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roomLabel.setForeground(new Color(108, 117, 125));
        
        secondRow.add(doctorLabel, BorderLayout.WEST);
        secondRow.add(roomLabel, BorderLayout.EAST);
        
        contentPanel.add(firstRow);
        contentPanel.add(secondRow);
        
        resultPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Status label (right side)
        JLabel statusLabel = new JLabel(lichHen.getTrangThai());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(getStatusTextColor(lichHen.getTrangThai()));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getStatusTextColor(lichHen.getTrangThai()), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        resultPanel.add(statusLabel, BorderLayout.EAST);
        
        // Add interaction behavior
        resultPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAppointmentDetails(lichHen);
                highlightAppointment(lichHen);
                scrollToAppointment(lichHen);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                resultPanel.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                resultPanel.setBackground(Color.WHITE);
            }
        });
        
        return resultPanel;
    }

    private Color getStatusTextColor(String status) {
        switch (status) {
            case "Chờ xác nhận":
                return new Color(255, 193, 7);
            case "Đã xác nhận":
                return new Color(40, 167, 69);
            case "Đã hủy":
                return new Color(220, 53, 69);
            default:
                return Color.GRAY;
        }
    }
    private void updateCalendarWithSearchResults(List<LichHen> searchResults) {
        calendarPanel.removeAll();
        LocalDate date = currentWeekStart;
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

        for (int i = 0; i < 7; i++) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel dayLabel = new JLabel(days[i] + " (" + date.format(DateTimeFormatter.ofPattern("d/M")) + ")", SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setBackground(new Color(173, 216, 230));
            dayPanel.add(dayLabel, BorderLayout.NORTH);

            JPanel morningPanel = new JPanel(new GridLayout(0, 1));
            morningPanel.setBorder(BorderFactory.createTitledBorder("Sáng"));
            JPanel afternoonPanel = new JPanel(new GridLayout(0, 1));
            afternoonPanel.setBorder(BorderFactory.createTitledBorder("Chiều"));

            // Lọc các lịch hẹn theo ngày
            for (LichHen lichHen : searchResults) {
                if (Date.valueOf(date).equals(lichHen.getNgayHen())) {
                    JTextArea eventText = new JTextArea(
                            "BS:" + lichHen.getHoTenBacSi() + "\n" +
                            "BN:" + lichHen.getHoTenBenhNhan() + "\n" +
                            "Phòng:" + lichHen.getTenPhong() + "\n" +
                            "Giờ:" + lichHen.getGioHen().toString()
                    );
                    eventText.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showPopupMenu(evt, lichHen);
                        }
                    });
                    eventText.setFont(new Font("Arial", Font.PLAIN, 10));
                    eventText.setEditable(false);
                    eventText.setOpaque(true);
                    eventText.setBackground(getColorByStatus(lichHen.getTrangThai()));

                    eventText.setName(lichHen.getHoTenBenhNhan());
                    dayPanel.setName(lichHen.getHoTenBenhNhan());

                    LocalTime gioHen = lichHen.getGioHen().toLocalTime();
                    if (gioHen.isBefore(LocalTime.of(12, 0))) {
                        morningPanel.add(eventText);
                    } else {
                        afternoonPanel.add(eventText);
                    }
                }
            }

            JPanel contentPanel = new JPanel(new GridLayout(2, 1));
            contentPanel.add(morningPanel);
            contentPanel.add(afternoonPanel);
            dayPanel.add(contentPanel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            date = date.plusDays(1);
        }

        weekLabel.setText(getFormattedWeek());
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }


    private void highlightAppointment(LichHen lichHen) {
        for (Component comp : calendarPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getName() != null && panel.getName().equals(lichHen.getHoTenBenhNhan())) {
                    panel.setBackground(Color.YELLOW);
                    break;
                }
            }
        }
    }

    private void scrollToAppointment(LichHen lichHen) {
        for (Component comp : calendarPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getName() != null && panel.getName().equals(lichHen.getHoTenBenhNhan())) {
                    JScrollPane scrollPane = (JScrollPane) calendarPanel.getParent();
                    int panelY = panel.getY();
                    scrollPane.getVerticalScrollBar().setValue(panelY);
                    break;
                }
            }
        }
    }
    private void showAppointmentDetails(LichHen lichHen) {
        // Tạo một cửa sổ chi tiết hoặc panel mới để hiển thị thông tin
        JFrame detailFrame = new JFrame("Chi tiết lịch hẹn");
        detailFrame.setSize(280, 300); // Điều chỉnh kích thước cho phù hợp
        detailFrame.setLayout(new BorderLayout());

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(6, 2, 5, 5)); 
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Thông tin lịch hẹn", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), 
            Color.BLACK
        ));
        String hoTenBacSi = lichHen.getHoTenBacSi();
        String hoTenBenhNhan = lichHen.getHoTenBenhNhan();
        java.sql.Date ngayHen = lichHen.getNgayHen();
        java.sql.Time gioHen = lichHen.getGioHen();
        String tenPhongKham = lichHen.getTenPhong();
        String trangThai = lichHen.getTrangThai();
        String moTa = lichHen.getMoTa();

        // Định dạng giờ hẹn
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String gioHenString = timeFormat.format(gioHen); // Chuyển đối tượng Time thành chuỗi giờ

        detailPanel.add(new JLabel("Tên bác sĩ:"));
        detailPanel.add(new JLabel(hoTenBacSi));
        detailPanel.add(new JLabel("Tên bệnh nhân:"));
        detailPanel.add(new JLabel(hoTenBenhNhan));
        detailPanel.add(new JLabel("Ngày hẹn:"));
        detailPanel.add(new JLabel(ngayHen.toString())); // Hiển thị ngày hẹn
        detailPanel.add(new JLabel("Giờ hẹn (HH:mm):"));
        detailPanel.add(new JLabel(gioHenString)); // Hiển thị giờ hẹn
        detailPanel.add(new JLabel("Tên phòng khám:"));
        detailPanel.add(new JLabel(tenPhongKham));
        detailPanel.add(new JLabel("Trạng thái:"));
        detailPanel.add(new JLabel(trangThai)); 
        detailFrame.add(detailPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailFrame.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        detailFrame.add(buttonPanel, BorderLayout.SOUTH);

        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setLocationRelativeTo(this);
        detailFrame.setVisible(true);
    }
    private String getFormattedWeek() {
        LocalDate endOfWeek = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return currentWeekStart.format(formatter) + " - " + endOfWeek.format(formatter);
    }
    private void changeWeek(int delta) {
        currentWeekStart = currentWeekStart.plusWeeks(delta);
        updateCalendar();
    }
    private void updateCalendar() {
        calendarPanel.removeAll();
        LocalDate date = currentWeekStart;
        LocalDate today = LocalDate.now();
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

        // Keep the same color scheme
        Color headerBgColor = new Color(41, 128, 185);      // Bright blue for headers
        Color todayHeaderBgColor = new Color(52, 152, 219); // Lighter blue for today's header
        Color headerTextColor = Color.WHITE;                // White text for headers
        Color morningHeaderColor = new Color(241, 196, 15); // Warm yellow for morning
        Color afternoonHeaderColor = new Color(230, 126, 34); // Warm orange for afternoon
        Color borderColor = new Color(189, 195, 199);       // Light gray borders
        Color panelBgColor = new Color(250, 250, 250);      // Almost white background

        for (int i = 0; i < 7; i++) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBackground(panelBgColor);
            dayPanel.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

            // Create day header - keeping the same style
            boolean isToday = date.equals(today);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(isToday ? todayHeaderBgColor : headerBgColor);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            // Format day header exactly as shown in the screenshot
            String formattedDate = date.format(DateTimeFormatter.ofPattern("d/M"));
            JLabel dayLabel = new JLabel(days[i] + " (" + formattedDate + ")", SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            dayLabel.setForeground(headerTextColor);
            
            if (isToday) {
                // Add bullet indicator for today
                JLabel todayIndicator = new JLabel("•");
                todayIndicator.setFont(new Font("Segoe UI", Font.BOLD, 16));
                todayIndicator.setForeground(Color.WHITE);
                headerPanel.add(todayIndicator, BorderLayout.WEST);
            }
            
            headerPanel.add(dayLabel, BorderLayout.CENTER);
            dayPanel.add(headerPanel, BorderLayout.NORTH);

            // Morning panel with consistent styling
            JPanel morningPanel = new JPanel(new BorderLayout());
            morningPanel.setBackground(panelBgColor);
            
            // Morning header - use full width label to match screenshot
            JLabel morningLabel = new JLabel("Sáng", SwingConstants.LEFT);
            morningLabel.setOpaque(true);
            morningLabel.setBackground(morningHeaderColor);
            morningLabel.setForeground(Color.WHITE);
            morningLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            morningLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            morningPanel.add(morningLabel, BorderLayout.NORTH);
            
            // Morning content area
            JPanel morningContentPanel = new JPanel();
            morningContentPanel.setLayout(new BoxLayout(morningContentPanel, BoxLayout.Y_AXIS));
            morningContentPanel.setBackground(panelBgColor);
            morningContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            morningPanel.add(morningContentPanel, BorderLayout.CENTER);
            
            // Afternoon panel with consistent styling
            JPanel afternoonPanel = new JPanel(new BorderLayout());
            afternoonPanel.setBackground(panelBgColor);
            
            // Afternoon header - use full width label to match screenshot
            JLabel afternoonLabel = new JLabel("Chiều", SwingConstants.LEFT);
            afternoonLabel.setOpaque(true);
            afternoonLabel.setBackground(afternoonHeaderColor);
            afternoonLabel.setForeground(Color.WHITE);
            afternoonLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            afternoonLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            afternoonPanel.add(afternoonLabel, BorderLayout.NORTH);
            
            // Afternoon content area
            JPanel afternoonContentPanel = new JPanel();
            afternoonContentPanel.setLayout(new BoxLayout(afternoonContentPanel, BoxLayout.Y_AXIS));
            afternoonContentPanel.setBackground(panelBgColor);
            afternoonContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            afternoonPanel.add(afternoonContentPanel, BorderLayout.CENTER);

            // Get appointments for this day
            List<LichHen> lichHenList = qlLichHen.getLichHenByDate(Date.valueOf(date));

            if (lichHenList != null && !lichHenList.isEmpty()) {
                lichHenList.sort(Comparator.comparing(LichHen::getGioHen));

                for (LichHen info : lichHenList) {
                    // Improved appointment panel to display content more clearly
                    JPanel appointmentPanel = new JPanel();
                    appointmentPanel.setLayout(new BorderLayout());
                    appointmentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                    appointmentPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                    appointmentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    appointmentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
                    appointmentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Create a panel for all content with better layout
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new GridLayout(4, 1, 0, 2));
                    contentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                    
                    // Time label - make more prominent
                    JLabel timeLabel = new JLabel(info.getGioHen().toString().substring(0, 5) + ":00");
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    timeLabel.setForeground(new Color(44, 62, 80));
                    
                    // Patient label - improved display
                    JLabel patientLabel = new JLabel("BN: " + info.getHoTenBenhNhan());
                    patientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    patientLabel.setForeground(new Color(44, 62, 80));
                    
                    // Doctor label
                    JLabel doctorLabel = new JLabel("BS: " + info.getHoTenBacSi());
                    doctorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    doctorLabel.setForeground(new Color(44, 62, 80));
                    
                    // Room label
                    JLabel roomLabel = new JLabel("Phòng: " + info.getTenPhong());
                    roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    roomLabel.setForeground(new Color(44, 62, 80));
                    
                    contentPanel.add(timeLabel);
                    contentPanel.add(patientLabel);
                    contentPanel.add(doctorLabel);
                    contentPanel.add(roomLabel);
                    
                    appointmentPanel.add(contentPanel, BorderLayout.CENTER);
                    
                    // Add mouse listener for popup menu and hover effects
                    appointmentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showPopupMenu(evt, info);
                        }
                        
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            appointmentPanel.setBackground(brightenColor(getColorByStatus(info.getTrangThai())));
                            contentPanel.setBackground(brightenColor(getColorByStatus(info.getTrangThai())));
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            appointmentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                            contentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                        }
                    });
                    
                    appointmentPanel.setName(info.getHoTenBenhNhan());
                    
                    // Add appointment to appropriate panel based on time
                    LocalTime gioHen = info.getGioHen().toLocalTime();
                    if (gioHen.isBefore(LocalTime.of(12, 0))) {
                        morningContentPanel.add(appointmentPanel);
                        morningContentPanel.add(Box.createVerticalStrut(5));
                    } else {
                        afternoonContentPanel.add(appointmentPanel);
                        afternoonContentPanel.add(Box.createVerticalStrut(5));
                    }
                }
            }

            // Add placeholders for empty sections
            if (morningContentPanel.getComponentCount() == 0) {
                JLabel placeholder = new JLabel("Không có lịch hẹn", SwingConstants.CENTER);
                placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                placeholder.setForeground(new Color(150, 150, 150));
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                morningContentPanel.add(placeholder);
            }
            
            if (afternoonContentPanel.getComponentCount() == 0) {
                JLabel placeholder = new JLabel("Không có lịch hẹn", SwingConstants.CENTER);
                placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                placeholder.setForeground(new Color(150, 150, 150));
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                afternoonContentPanel.add(placeholder);
            }

            // Create a panel to hold morning and afternoon sections with equal spacing
            JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 0));
            contentPanel.setBackground(panelBgColor);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            contentPanel.add(morningPanel);
            contentPanel.add(afternoonPanel);
            
            dayPanel.add(contentPanel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            date = date.plusDays(1);
        }

        weekLabel.setText(getFormattedWeek());
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemChinhSua = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoa = createStyledMenuItem("Xóa");
        
        menuItemXoa.setForeground(accentColor); // Đặt màu đỏ cho nút xóa
        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemChinhSua);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoa);
    }
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("Arial", Font.PLAIN, 12));
        menuItem.setBackground(Color.WHITE);
        menuItem.setPreferredSize(new Dimension(120, 28));
        return menuItem;
    }
    private void showPopupMenu(MouseEvent evt, LichHen lichHen) {
        menuItemXemChiTiet.addActionListener(e -> showAppointmentDetails(lichHen));
        menuItemChinhSua.addActionListener(e -> editAppointment(lichHen));
        menuItemXoa.addActionListener(e -> deleteAppointment(lichHen));
        
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
    private void showDetails(LichHen lichHen) {
        JOptionPane.showMessageDialog(this,
                "Bác sĩ: " + lichHen.getHoTenBacSi() + "\n" +
                        "Bệnh nhân: " + lichHen.getHoTenBenhNhan() + "\n" +
                        "Phòng khám: " + lichHen.getTenPhong() + "\n" +
                        "Giờ hẹn: " + lichHen.getGioHen().toString() + "\n" +
                        "Trạng thái: " + lichHen.getTrangThai(),
                "Chi tiết lịch hẹn",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void addAppointment() {
        // Tạo panel chính với BorderLayout để tổ chức các thành phần
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Tạo tiêu đề với màu sắc nổi bật
        JLabel titleLabel = new JLabel("Thêm Lịch Hẹn Mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chính để nhập thông tin với GridBagLayout cho bố cục linh hoạt
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        // Lấy danh sách dữ liệu
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();
        List<String> phongKhamList = qlLichHen.danhSachPhongKham();

        // Chuẩn bị các components với style nhất quán
        JComboBox<String> comboBacSi = createStyledComboBox(bacSiList.toArray(new String[0]));
        JComboBox<String> comboBenhNhan = createStyledComboBox(benhNhanList.toArray(new String[0]));
        JComboBox<String> comboPhongKham = createStyledComboBox(phongKhamList.toArray(new String[0]));

        // DateChooser với style nhất quán
        JDateChooser dateChooserNgayHen = createStyledDateChooser();
        dateChooserNgayHen.setDate(Calendar.getInstance().getTime());

        // Tạo các trường nhập liệu với style
        JTextField txtGioHen = createStyledTextField("08:00");
        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        JComboBox<String> statusComboBox = createStyledComboBox(statuses);
        JTextField txtMoTa = createStyledTextField("");

        // Thông tin giờ làm việc
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255)); // Màu nền nhẹ
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 221, 242), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel lblWorkingHoursInfo = new JLabel("<html><b>Giờ làm việc:</b><br/>Sáng: 7:30 - 12:00<br/>Chiều: 13:00 - 17:00<br/>Lịch hẹn cách nhau ít nhất 30 phút</html>");
        lblWorkingHoursInfo.setForeground(new Color(70, 130, 180));
        lblWorkingHoursInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(lblWorkingHoursInfo, BorderLayout.CENTER);

        // Thêm các thành phần vào form với GridBagLayout
        // Cột nhãn
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createStyledLabel("Tên bác sĩ:"), gbc);

        gbc.gridy = 1;
        formPanel.add(createStyledLabel("Tên bệnh nhân:"), gbc);

        gbc.gridy = 2;
        formPanel.add(createStyledLabel("Ngày hẹn:"), gbc);

        gbc.gridy = 3;
        formPanel.add(createStyledLabel("Giờ hẹn (HH:mm):"), gbc);

        gbc.gridy = 4;
        formPanel.add(createStyledLabel("Phòng khám:"), gbc);

        gbc.gridy = 5;
        formPanel.add(createStyledLabel("Trạng thái:"), gbc);

        gbc.gridy = 6;
        formPanel.add(createStyledLabel("Mô tả:"), gbc);

        // Cột điều khiển nhập liệu
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(comboBacSi, gbc);

        gbc.gridy = 1;
        formPanel.add(comboBenhNhan, gbc);

        gbc.gridy = 2;
        formPanel.add(dateChooserNgayHen, gbc);

        gbc.gridy = 3;
        formPanel.add(txtGioHen, gbc);

        gbc.gridy = 4;
        formPanel.add(comboPhongKham, gbc);

        gbc.gridy = 5;
        formPanel.add(statusComboBox, gbc);

        gbc.gridy = 6;
        formPanel.add(txtMoTa, gbc);

        // Thêm thông tin giờ làm việc ở dưới cùng
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 8, 5);
        formPanel.add(infoPanel, gbc);

        // Thêm formPanel vào phần trung tâm của mainPanel
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Tạo panel nút với FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Tạo các nút với màu sắc phù hợp
        JButton cancelButton = createRoundedButton("Hủy", Color.LIGHT_GRAY, Color.BLACK, cornerRadius);
        JButton saveButton = createRoundedButton("Lưu", primaryColor, Color.WHITE, cornerRadius);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị dialog với panel chính
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm Lịch Hẹn", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Xử lý sự kiện nút hủy
        cancelButton.addActionListener(e -> dialog.dispose());

        // Xử lý sự kiện nút lưu
        saveButton.addActionListener(e -> {
            try {
                // Lấy thông tin từ form
                String hoTenBacSi = ((String) comboBacSi.getSelectedItem()).trim();
                String hoTenBenhNhan = ((String) comboBenhNhan.getSelectedItem()).trim();
                String tenPhongKham = ((String) comboPhongKham.getSelectedItem()).trim();

                int idBacSi = qlLichHen.getBacSiIdFromName(hoTenBacSi);
                int idBenhNhan = qlLichHen.getBenhNhanIdFromName(hoTenBenhNhan);
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhongKham);

                if (idBacSi == -1 || idBenhNhan == -1 || idPhongKham == -1) {
                    showErrorMessage(dialog, "Tên bác sĩ, bệnh nhân hoặc phòng khám không hợp lệ.");
                    return;
                }

                java.util.Date ngayHenDate = dateChooserNgayHen.getDate();
                java.sql.Date ngayHen = new java.sql.Date(ngayHenDate.getTime());
                LocalTime gioHen = LocalTime.parse(txtGioHen.getText());
                java.sql.Time timeGioHen = java.sql.Time.valueOf(gioHen);
                
                String selectedStatus = (String) statusComboBox.getSelectedItem();
                LichHen.TrangThaiLichHen trangThaiEnum;
                switch(selectedStatus) {
                    case "Chờ xác nhận":
                        trangThaiEnum = LichHen.TrangThaiLichHen.CHO_XAC_NHAN;
                        break;
                    case "Đã xác nhận":
                        trangThaiEnum = LichHen.TrangThaiLichHen.DA_XAC_NHAN;
                        break;
                    case "Đã hủy":
                        trangThaiEnum = LichHen.TrangThaiLichHen.DA_HUY;
                        break;
                    default:
                        trangThaiEnum = LichHen.TrangThaiLichHen.CHO_XAC_NHAN;
                }
                
                String moTa = txtMoTa.getText();
                
                // Kiểm tra thời gian hợp lệ
                String validationError = util.TimeValidator.validateAppointmentTime(
                    ngayHen, 
                    timeGioHen, 
                    qlLichHen.getAllLichHen(), 
                    idBacSi, 
                    idPhongKham
                );

                if (validationError != null) {
                    showErrorMessage(dialog, validationError);
                    return;
                }
                
                // Tạo đối tượng lịch hẹn mới
                LichHen lichHen = new LichHen(0, idBacSi, hoTenBacSi, idBenhNhan, hoTenBenhNhan, 
                        ngayHen, idPhongKham, tenPhongKham, timeGioHen, trangThaiEnum, moTa);

                // Lưu lịch hẹn
                qlLichHen.datLichHen(lichHen);

                // Hiển thị thông báo thành công và đóng dialog
                showInfoMessage(dialog, "Thêm lịch hẹn thành công!");
                dialog.dispose();
                
                // Cập nhật lịch
                updateCalendar();
            } catch (NumberFormatException ex) {
                showErrorMessage(dialog, "Lỗi nhập liệu! Kiểm tra lại định dạng giờ (HH:mm).");
            } catch (DateTimeParseException ex) {
                showErrorMessage(dialog, "Lỗi định dạng giờ (HH:mm).");
            } catch (Exception ex) {
                showErrorMessage(dialog, "Lỗi nhập liệu! Kiểm tra lại định dạng.");
                ex.printStackTrace();
            }
        });

        dialog.setVisible(true);
    }

    private void editAppointment(LichHen lichHen) {
        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Tạo tiêu đề với màu sắc nổi bật
        JLabel titleLabel = new JLabel("Chỉnh Sửa Lịch Hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chính để nhập thông tin với GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        // Lấy danh sách và tạo map để lưu ID
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();
        List<String> phongKhamList = qlLichHen.danhSachPhongKham();

        // Chuẩn bị các components với style nhất quán
        JComboBox<String> comboBacSi = createStyledComboBox(bacSiList.toArray(new String[0]));
        comboBacSi.setSelectedItem(lichHen.getHoTenBacSi());
        
        JComboBox<String> comboBenhNhan = createStyledComboBox(benhNhanList.toArray(new String[0]));
        comboBenhNhan.setSelectedItem(lichHen.getHoTenBenhNhan());
        
        JComboBox<String> comboPhongKham = createStyledComboBox(phongKhamList.toArray(new String[0]));
        comboPhongKham.setSelectedItem(lichHen.getTenPhong());

        // DateChooser với style nhất quán
        JDateChooser dateChooserNgayHen = createStyledDateChooser();
        dateChooserNgayHen.setDateFormatString("yyyy-MM-dd");
        dateChooserNgayHen.setDate(java.sql.Date.valueOf(lichHen.getNgayHen().toLocalDate()));

        // Tạo các trường nhập liệu với style
        JTextField txtGioHen = createStyledTextField(lichHen.getGioHen().toString());
        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        JComboBox<String> statusComboBox = createStyledComboBox(statuses);
        statusComboBox.setSelectedItem(lichHen.getTrangThai());
        JTextField txtMoTa = createStyledTextField(lichHen.getMoTa());

        // Thông tin giờ làm việc
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255)); // Màu nền nhẹ
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 221, 242), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel lblWorkingHoursInfo = new JLabel("<html><b>Giờ làm việc:</b><br/>Sáng: 7:30 - 12:00<br/>Chiều: 13:00 - 17:00<br/>Lịch hẹn cách nhau ít nhất 30 phút</html>");
        lblWorkingHoursInfo.setForeground(new Color(70, 130, 180));
        lblWorkingHoursInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(lblWorkingHoursInfo, BorderLayout.CENTER);

        // ID của lịch hẹn - chỉ hiển thị
        JTextField txtIdLichHen = createStyledTextField(String.valueOf(lichHen.getIdLichHen()));
        txtIdLichHen.setEditable(false);
        txtIdLichHen.setBackground(new Color(245, 245, 245));

        // Thêm các thành phần vào form với GridBagLayout
        // Cột nhãn
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createStyledLabel("ID Lịch hẹn:"), gbc);

        gbc.gridy = 1;
        formPanel.add(createStyledLabel("Bác sĩ:"), gbc);

        gbc.gridy = 2;
        formPanel.add(createStyledLabel("Bệnh nhân:"), gbc);

        gbc.gridy = 3;
        formPanel.add(createStyledLabel("Ngày hẹn:"), gbc);

        gbc.gridy = 4;
        formPanel.add(createStyledLabel("Giờ hẹn (HH:mm):"), gbc);

        gbc.gridy = 5;
        formPanel.add(createStyledLabel("Phòng khám:"), gbc);

        gbc.gridy = 6;
        formPanel.add(createStyledLabel("Trạng thái:"), gbc);

        gbc.gridy = 7;
        formPanel.add(createStyledLabel("Mô tả:"), gbc);

        // Cột điều khiển nhập liệu
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(txtIdLichHen, gbc);

        gbc.gridy = 1;
        formPanel.add(comboBacSi, gbc);

        gbc.gridy = 2;
        formPanel.add(comboBenhNhan, gbc);

        gbc.gridy = 3;
        formPanel.add(dateChooserNgayHen, gbc);

        gbc.gridy = 4;
        formPanel.add(txtGioHen, gbc);

        gbc.gridy = 5;
        formPanel.add(comboPhongKham, gbc);

        gbc.gridy = 6;
        formPanel.add(statusComboBox, gbc);

        gbc.gridy = 7;
        formPanel.add(txtMoTa, gbc);

        // Thêm thông tin giờ làm việc ở dưới cùng
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 8, 5);
        formPanel.add(infoPanel, gbc);

        // Thêm formPanel vào phần trung tâm của mainPanel
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Tạo panel nút với FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Tạo các nút với màu sắc phù hợp
        JButton cancelButton = createRoundedButton("Hủy", Color.LIGHT_GRAY, Color.BLACK, cornerRadius);
        JButton saveButton = createRoundedButton("Lưu thay đổi", primaryColor, Color.WHITE, cornerRadius);
        JButton deleteButton = createRoundedButton("Xóa lịch hẹn", accentColor, Color.WHITE, cornerRadius);

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị dialog với panel chính
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Lịch Hẹn", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Xử lý sự kiện nút hủy
        cancelButton.addActionListener(e -> dialog.dispose());

        // Xử lý sự kiện nút xóa
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc chắn muốn xóa lịch hẹn này?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    qlLichHen.deleteLichHen(lichHen.getIdLichHen());
                    showInfoMessage(dialog, "Xóa lịch hẹn thành công!");
                    dialog.dispose();
                    updateCalendar();
                } catch (Exception ex) {
                    showErrorMessage(dialog, "Lỗi khi xóa lịch hẹn.");
                    ex.printStackTrace();
                }
            }
        });
        saveButton.addActionListener(e -> {
            try {
                // Lấy thông tin từ form
                String hoTenBacSi = ((String) comboBacSi.getSelectedItem()).trim();
                String hoTenBenhNhan = ((String) comboBenhNhan.getSelectedItem()).trim();
                String tenPhong = ((String) comboPhongKham.getSelectedItem()).trim();
                
                int idBacSi = qlLichHen.getBacSiIdFromName(hoTenBacSi);
                int idBenhNhan = qlLichHen.getBenhNhanIdFromName(hoTenBenhNhan);
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhong);

                if (idBacSi == -1 || idBenhNhan == -1 || idPhongKham == -1) {
                    showErrorMessage(dialog, "Tên bác sĩ, bệnh nhân hoặc phòng khám không tồn tại!");
                    return;
                }

                java.util.Date selectedDate = dateChooserNgayHen.getDate();
                java.sql.Date ngayHen;
                if (selectedDate != null) {
                    ngayHen = new java.sql.Date(selectedDate.getTime());
                } else {
                    throw new IllegalArgumentException("Ngày hẹn không hợp lệ!");
                }

                LocalTime newGioHen = LocalTime.parse(txtGioHen.getText());
                java.sql.Time timeGioHen = java.sql.Time.valueOf(newGioHen);
                
                // Tạo lịch hẹn tạm thời để kiểm tra
                LichHen tempLichHen = new LichHen();
                tempLichHen.setIdLichHen(lichHen.getIdLichHen());
                tempLichHen.setIdBacSi(idBacSi);
                tempLichHen.setHoTenBacSi(hoTenBacSi);
                tempLichHen.setIdBenhNhan(idBenhNhan);
                tempLichHen.setHoTenBenhNhan(hoTenBenhNhan);
                tempLichHen.setNgayHen(ngayHen);
                tempLichHen.setIdPhongKham(idPhongKham);
                tempLichHen.setTenPhong(tenPhong);
                tempLichHen.setGioHen(timeGioHen);
                tempLichHen.setTrangThai((String) statusComboBox.getSelectedItem());
                tempLichHen.setMoTa(txtMoTa.getText());
                
                // Lọc danh sách lịch hẹn để loại bỏ lịch hẹn hiện tại
                List<LichHen> otherAppointments = new ArrayList<>();
                for (LichHen appointment : qlLichHen.getAllLichHen()) {
                    if (appointment.getIdLichHen() != lichHen.getIdLichHen()) {
                        otherAppointments.add(appointment);
                    }
                }
                
                // Kiểm tra thời gian hợp lệ
                String validationError = util.TimeValidator.validateAppointmentTime(
                    ngayHen, 
                    timeGioHen, 
                    otherAppointments, 
                    idBacSi, 
                    idPhongKham
                );
                
                if (validationError != null) {
                    showErrorMessage(dialog, validationError);
                    return;
                }
                
                // Cập nhật đối tượng lịch hẹn
                lichHen.setIdBacSi(idBacSi);
                lichHen.setHoTenBacSi(hoTenBacSi);
                lichHen.setIdBenhNhan(idBenhNhan);
                lichHen.setHoTenBenhNhan(hoTenBenhNhan);
                lichHen.setNgayHen(ngayHen);
                lichHen.setIdPhongKham(idPhongKham);
                lichHen.setTenPhong(tenPhong);
                lichHen.setGioHen(timeGioHen);
                lichHen.setTrangThai((String) statusComboBox.getSelectedItem());
                lichHen.setMoTa(txtMoTa.getText());

                // Lưu lịch hẹn
                qlLichHen.updateLichHen(lichHen);

                // Hiển thị thông báo thành công và đóng dialog
                showInfoMessage(dialog, "Cập nhật lịch hẹn thành công!");
                dialog.dispose();
                
                // Cập nhật lịch
                updateCalendar();
            } catch (Exception ex) {
                showErrorMessage(dialog, "Lỗi nhập liệu! Kiểm tra lại định dạng.");
                ex.printStackTrace();
            }
        });

        dialog.setVisible(true);
    }

    // Các phương thức tiện ích để tạo components có style nhất quán

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JTextField createStyledTextField(String defaultText) {
        JTextField textField = new JTextField(defaultText);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return comboBox;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        
        // Customize the text field inside the date chooser
        JTextField dateField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateField.setHorizontalAlignment(JTextField.CENTER);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateField.setBorder(BorderFactory.createEmptyBorder());
        
        return dateChooser;
    }
    private void showErrorMessage(Component parent, String message) {
        // Tạo panel chứa thông báo
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(Color.WHITE);
        
        // Icon cảnh báo
        JLabel iconLabel = new JLabel();
        panel.add(iconLabel, BorderLayout.WEST);
        
        // Nội dung thông báo
        JLabel msgLabel = new JLabel("<html>" + message + "</html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(new Color(220, 20, 60)); // Màu đỏ đậm
        panel.add(msgLabel, BorderLayout.CENTER);
        
        // Tạo và hiển thị dialog
        JDialog errorDialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Lỗi");
        errorDialog.setContentPane(panel);
        
        // Nút OK
        JButton okButton = createRoundedButton("OK", accentColor, Color.WHITE, cornerRadius);
        okButton.addActionListener(e -> errorDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Cấu hình dialog
        errorDialog.pack();
        errorDialog.setSize(Math.max(errorDialog.getWidth(), 350), errorDialog.getHeight());
        errorDialog.setLocationRelativeTo(parent);
        errorDialog.setResizable(false);
        errorDialog.setVisible(true);
    }
    private void showInfoMessage(Component parent, String message) {
        // Tạo panel chứa thông báo
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel();
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel msgLabel = new JLabel("<html>" + message + "</html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(new Color(0, 100, 150)); // Màu xanh đậm
        panel.add(msgLabel, BorderLayout.CENTER);
        
        JDialog infoDialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Thông báo");
        infoDialog.setContentPane(panel);
        
        JButton okButton = createRoundedButton("OK", primaryColor, Color.WHITE, cornerRadius);
        okButton.addActionListener(e -> infoDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        infoDialog.pack();
        infoDialog.setSize(Math.max(infoDialog.getWidth(), 350), infoDialog.getHeight());
        infoDialog.setLocationRelativeTo(parent);
        infoDialog.setResizable(false);
        infoDialog.setVisible(true);
    }
    private void deleteAppointment(LichHen lichHen) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lịch hẹn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            qlLichHen.deleteLichHen(lichHen.getIdLichHen());

            JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            updateCalendar();
        }
    }
    private Color brightenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], Math.max(0.0f, hsb[1] - 0.1f), Math.min(1.0f, hsb[2] + 0.1f));
    }
    private Color getColorByStatus(String status) {
        switch (status) {
            case "Chờ xác nhận":
                return new Color(255, 255, 102);
            case "Đã xác nhận":
                return new Color(144, 238, 144);
            case "Đã hủy":
                return new Color(255, 99, 71);
            default:
                return Color.WHITE;
        }
    }
}