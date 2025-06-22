package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;
import controller.LichHenController;
import model.LichHen;
import util.RoundedPanel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    private Color warningColor = new Color(237, 187, 85);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color buttonTextColor = Color.WHITE;
    private Color accentColor = new Color(192, 80, 77); // For delete button
    private Color borderColor = new Color(222, 226, 230); // Border color
    private Color primaryColor = new Color(41, 128, 185);    // Màu xanh dương hiện đại
    private Color secondaryColor = new Color(245, 248, 250);
    private Color successColor = new Color(86, 156, 104);
    private JPanel errorPanel;
    private JLabel errorLabel;
    private Map<JComponent, JLabel> fieldErrorLabels = new HashMap<>();
    private Map<JComponent, Timer> fieldErrorTimers = new HashMap<>();
    private Color errorBorderColor = new Color(231, 76, 60); // Màu đỏ cho viền lỗi
    private Color normalBorderColor = new Color(200, 200, 200); // Màu viền bình thường
    private Color headerTextColor = Color.WHITE;             // Màu chữ header
    private int cornerRadius = 10;
    // Font for buttons
    private Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
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
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setBackground(headerTextColor);
        combinedPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(8, 20, 8, 20) // giảm padding trên-dưới
        ));
        combinedPanel.setPreferredSize(new Dimension(0, 60)); // tăng chiều cao tổng thể một chút

        // ========== 1. HEADER SECTION (LEFT SIDE) ==========
        JPanel headerSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerSection.setOpaque(false);
        headerSection.setPreferredSize(new Dimension(110, 40));

        JLabel titleLabel = new JLabel("LỊCH HẸN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);

        // Nút "Thêm" sẽ được di chuyển xuống navigationSection
        addAppointmentButton = createRoundedButton("Thêm", primaryColor, headerTextColor, cornerRadius);
        addAppointmentButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addAppointmentButton.setPreferredSize(new Dimension(65, 30));
        addAppointmentButton.addActionListener(e -> addAppointment());

        headerSection.add(titleLabel);
        // ========== 2. NAVIGATION SECTION (CENTER) ==========
        JPanel navigationSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Giảm gap từ 10 xuống 5
        navigationSection.setOpaque(false);

        prevWeekButton = createRoundedButton("‹", primaryColor, Color.WHITE, cornerRadius);
        prevWeekButton.setPreferredSize(new Dimension(35, 30));
        prevWeekButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevWeekButton.addActionListener(e -> changeWeek(-1));

        nextWeekButton = createRoundedButton("›", primaryColor, Color.WHITE, cornerRadius);
        nextWeekButton.setPreferredSize(new Dimension(35, 30));
        nextWeekButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextWeekButton.addActionListener(e -> changeWeek(1));

        weekLabel = new JLabel(getFormattedWeek(), SwingConstants.CENTER);
        weekLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        weekLabel.setForeground(primaryColor);
        weekLabel.setPreferredSize(new Dimension(200, 30));
        weekLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        weekLabel.setOpaque(true);
        weekLabel.setBackground(Color.WHITE);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        dateChooser.setPreferredSize(new Dimension(110, 30));
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        JTextField dateField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateField.setHorizontalAlignment(JTextField.CENTER);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateField.setBorder(BorderFactory.createEmptyBorder());

        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                LocalDate selectedDate = ((java.util.Date) evt.getNewValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                currentWeekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);
                updateCalendar();
            }
        });
        JButton todayButton = createRoundedButton("Hôm nay", primaryColor, headerTextColor, cornerRadius);
        todayButton.setPreferredSize(new Dimension(80, 30));
        todayButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        todayButton.addActionListener(e -> {
            currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
            updateCalendar();
        });

        // Thêm các thành phần với khoảng cách nhỏ hơn
        navigationSection.add(prevWeekButton);
        navigationSection.add(Box.createHorizontalStrut(2)); // Giảm từ 5 xuống 2
        navigationSection.add(weekLabel);
        navigationSection.add(Box.createHorizontalStrut(2)); // Giảm từ 5 xuống 2
        navigationSection.add(nextWeekButton);
        navigationSection.add(Box.createHorizontalStrut(8)); // Giảm từ 15 xuống 8
        navigationSection.add(dateChooser);
        navigationSection.add(Box.createHorizontalStrut(5)); // Giảm từ 8 xuống 5
        navigationSection.add(todayButton);
        navigationSection.add(Box.createHorizontalStrut(5)); // Giảm từ 8 xuống 5
        navigationSection.add(addAppointmentButton);

        // ========== 3. SEARCH SECTION (RIGHT SIDE) ==========
        JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        searchSection.setOpaque(false);
        searchSection.setPreferredSize(new Dimension(240, 50)); // Tăng từ 180 lên 240

        Color searchBorderColor = new Color(41, 128, 185);

        JPanel searchFieldPanel = new RoundedPanel(cornerRadius, new BorderLayout());
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 0)); // Tăng padding trở lại
        searchFieldPanel.setPreferredSize(new Dimension(220, 30)); // Tăng từ 160 lên 220

        RoundedPanel searchOuterPanel = new RoundedPanel(cornerRadius + 1, new BorderLayout());
        searchOuterPanel.setBackground(searchBorderColor);
        searchOuterPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        searchOuterPanel.add(searchFieldPanel);

        JTextField searchField = new JTextField(15); // Tăng từ 12 lên 15 columns
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Tăng font size trở lại 12
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.GRAY);
        searchField.setText("Tìm kiếm lịch hẹn...");  // Dùng lại text đầy đủ
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Tìm kiếm lịch hẹn...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm lịch hẹn...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        JButton searchButton = createRoundedButton("Tìm", primaryColor, headerTextColor, cornerRadius);
        searchButton.setPreferredSize(new Dimension(55, 28));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setMargin(new Insets(2, 8, 2, 8));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (!searchText.equals("Tìm kiếm lịch hẹn...") && !searchText.trim().isEmpty()) {
                performSearch(searchText);
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchText = searchField.getText();
                    if (!searchText.equals("Tìm kiếm lịch hẹn...") && !searchText.trim().isEmpty()) {
                        performSearch(searchText);
                    }
                }
            }
        });
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(searchButton, BorderLayout.EAST);
        searchSection.add(searchOuterPanel);
        // ========== COMBINE ALL SECTIONS ==========
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(navigationSection);

        headerWrapper.add(headerSection, BorderLayout.WEST);
        headerWrapper.add(centerWrapper, BorderLayout.CENTER);
        headerWrapper.add(searchSection, BorderLayout.EAST);

        combinedPanel.add(headerWrapper, BorderLayout.CENTER);        
        // Thêm combined panel vào mainPanel
        mainPanel.add(combinedPanel, BorderLayout.NORTH);        
        // Thêm mainPanel vào top của giao diện
        add(mainPanel, BorderLayout.NORTH);
        
        // ========== 4. CONTENT AREA ==========
        // Cấu hình calendar panel với horizontal scrolling nếu cần
        calendarPanel = new JPanel(new GridLayout(1, 7, 1, 1));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        calendarPanel.setBackground(secondaryColor);
        calendarPanel.setPreferredSize(new Dimension(850, 580));
        
        // Panel kết quả tìm kiếm
        searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
        searchResultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchResultsPanel.setBackground(secondaryColor);
        
        // Container for search results with header
        searchResultsContainer = new JPanel(new BorderLayout());
        searchResultsContainer.setBackground(secondaryColor);
        searchResultsContainer.add(searchResultsPanel, BorderLayout.CENTER);
        
        // Create scroll panes - enable horizontal scrolling for calendar
        calendarScrollPane = new JScrollPane(calendarPanel);
        calendarScrollPane.setBorder(BorderFactory.createEmptyBorder());
        calendarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        calendarScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        calendarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        calendarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        calendarScrollPane.setBackground(secondaryColor);
        
        searchScrollPane = new JScrollPane(searchResultsContainer);
        searchScrollPane.setBorder(BorderFactory.createEmptyBorder());
        searchScrollPane.setBackground(secondaryColor);
        
        // Create split pane to manage the space between calendar and search results
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, calendarScrollPane, searchScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.7);
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
    private void updateCalendar() {
        calendarPanel.removeAll();
        LocalDate date = currentWeekStart;
        LocalDate today = LocalDate.now();
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"}; // Rút gọn tên ngày

        // Keep the same color scheme
        Color headerBgColor = new Color(41, 128, 185);      // Bright blue for headers
        Color todayHeaderBgColor = new Color(52, 152, 219); // Lighter blue for today's header
        Color headerTextColor = Color.WHITE;                // White text for headers
        Color morningHeaderColor = new Color(241, 196, 15); // Warm yellow for morning
        Color afternoonHeaderColor = new Color(230, 126, 34); // Warm orange for afternoon
        Color borderColor = new Color(189, 195, 199);       // Light gray borders
        Color panelBgColor = new Color(250, 250, 250);      // Almost white background
        Color todayPanelBgColor = new Color(240, 248, 255); // Light blue background for today

        for (int i = 0; i < 7; i++) {
            boolean isToday = date.equals(today);
            
            JPanel dayPanel = new JPanel(new BorderLayout());
            // Sử dụng màu nền khác cho ngày hôm nay
            dayPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
            dayPanel.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
            dayPanel.setMinimumSize(new Dimension(120, 0)); // Đảm bảo width tối thiểu

            // Create day header - tối ưu kích thước
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(isToday ? todayHeaderBgColor : headerBgColor);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Giảm padding
            
            // Format day header với tên ngày ngắn hơn
            String formattedDate = date.format(DateTimeFormatter.ofPattern("d/M"));
            JLabel dayLabel = new JLabel("<html><center>" + days[i] + " " + formattedDate + "</center></html>", SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Giảm font size
            dayLabel.setForeground(headerTextColor);
            
            // Bỏ phần thêm dấu chấm cho ngày hôm nay
            headerPanel.add(dayLabel, BorderLayout.CENTER);
            dayPanel.add(headerPanel, BorderLayout.NORTH);

            // Morning panel với styling tối ưu
            JPanel morningPanel = new JPanel(new BorderLayout());
            morningPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
            
            // Morning header - thu gọn
            JLabel morningLabel = new JLabel("Sáng", SwingConstants.CENTER);
            morningLabel.setOpaque(true);
            morningLabel.setBackground(morningHeaderColor);
            morningLabel.setForeground(Color.WHITE);
            morningLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Font nhỏ hơn
            morningLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); // Padding nhỏ hơn
            morningPanel.add(morningLabel, BorderLayout.NORTH);
            
            // Morning content area
            JPanel morningContentPanel = new JPanel();
            morningContentPanel.setLayout(new BoxLayout(morningContentPanel, BoxLayout.Y_AXIS));
            morningContentPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
            morningContentPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Padding nhỏ
            morningPanel.add(morningContentPanel, BorderLayout.CENTER);
            
            // Afternoon panel với styling tối ưu
            JPanel afternoonPanel = new JPanel(new BorderLayout());
            afternoonPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
            
            // Afternoon header - thu gọn
            JLabel afternoonLabel = new JLabel("Chiều", SwingConstants.CENTER);
            afternoonLabel.setOpaque(true);
            afternoonLabel.setBackground(afternoonHeaderColor);
            afternoonLabel.setForeground(Color.WHITE);
            afternoonLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Font nhỏ hơn
            afternoonLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); // Padding nhỏ hơn
            afternoonPanel.add(afternoonLabel, BorderLayout.NORTH);
            
            // Afternoon content area
            JPanel afternoonContentPanel = new JPanel();
            afternoonContentPanel.setLayout(new BoxLayout(afternoonContentPanel, BoxLayout.Y_AXIS));
            afternoonContentPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
            afternoonContentPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Padding nhỏ
            afternoonPanel.add(afternoonContentPanel, BorderLayout.CENTER);

            // Get appointments for this day
            List<LichHen> lichHenList = qlLichHen.getLichHenByDate(Date.valueOf(date));

            if (lichHenList != null && !lichHenList.isEmpty()) {
                lichHenList.sort(Comparator.comparing(LichHen::getGioHen));

                for (LichHen info : lichHenList) {
                    // Tối ưu appointment panel để tiết kiệm không gian
                    JPanel appointmentPanel = new JPanel();
                    appointmentPanel.setLayout(new BorderLayout());
                    appointmentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                    appointmentPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(4, 5, 4, 5) // Giảm padding
                    ));
                    appointmentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    appointmentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Giảm chiều cao
                    appointmentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Tạo content panel với layout tối ưu
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setBackground(getColorByStatus(info.getTrangThai()));
                    
                    // Time label - compact
                    JLabel timeLabel = new JLabel(info.getGioHen().toString().substring(0, 5));
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    timeLabel.setForeground(new Color(44, 62, 80));
                    timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Patient label - rút gọn
                    String patientName = info.getHoTenBenhNhan();
                    if (patientName.length() > 15) {
                        patientName = patientName.substring(0, 12) + "...";
                    }
                    JLabel patientLabel = new JLabel(patientName);
                    patientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    patientLabel.setForeground(new Color(44, 62, 80));
                    patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Doctor label - rút gọn
                    String doctorName = info.getHoTenBacSi();
                    if (doctorName.length() > 15) {
                        doctorName = doctorName.substring(0, 12) + "...";
                    }
                    JLabel doctorLabel = new JLabel("BS:" + doctorName);
                    doctorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    doctorLabel.setForeground(new Color(44, 62, 80));
                    doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Room label - rút gọn
                    JLabel roomLabel = new JLabel("P:" + info.getTenPhong());
                    roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    roomLabel.setForeground(new Color(44, 62, 80));
                    roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
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
                        morningContentPanel.add(Box.createVerticalStrut(2)); // Giảm khoảng cách
                    } else {
                        afternoonContentPanel.add(appointmentPanel);
                        afternoonContentPanel.add(Box.createVerticalStrut(2)); // Giảm khoảng cách
                    }
                }
            }            
            // Add placeholders for empty sections - tối ưu
            if (morningContentPanel.getComponentCount() == 0) {
                JLabel placeholder = new JLabel("<html><center>Trống</center></html>", SwingConstants.CENTER);
                placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                placeholder.setForeground(new Color(150, 150, 150));
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                morningContentPanel.add(placeholder);
            }            
            if (afternoonContentPanel.getComponentCount() == 0) {
                JLabel placeholder = new JLabel("<html><center>Trống</center></html>", SwingConstants.CENTER);
                placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                placeholder.setForeground(new Color(150, 150, 150));
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                afternoonContentPanel.add(placeholder);
            }
            
            // Create a panel to hold morning and afternoon sections
            JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 1)); // Thêm gap nhỏ
            contentPanel.setBackground(isToday ? todayPanelBgColor : panelBgColor);
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
    private void showPopupMenu(MouseEvent evt, LichHen lichHen) {
        // Xóa tất cả ActionListener cũ
        for (ActionListener al : menuItemXemChiTiet.getActionListeners()) {
            menuItemXemChiTiet.removeActionListener(al);
        }
        for (ActionListener al : menuItemChinhSua.getActionListeners()) {
            menuItemChinhSua.removeActionListener(al);
        }
        for (ActionListener al : menuItemXoa.getActionListeners()) {
            menuItemXoa.removeActionListener(al);
        }
        
        // Thêm ActionListener mới
        menuItemXemChiTiet.addActionListener(e -> showAppointmentDetails(lichHen));
        menuItemChinhSua.addActionListener(e -> editAppointment(lichHen));
        menuItemXoa.addActionListener(e -> deleteAppointment(lichHen));
        
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
    private void showAppointmentDetails(LichHen lichHen) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Lịch Hẹn", true);
        dialog.setSize(650, 490); // Tăng chiều cao từ 480 lên 500
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        // ========== HEADER PANEL ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20)); // Tăng padding
        headerPanel.setPreferredSize(new Dimension(650, 60)); // Tăng chiều cao lên 60

        JLabel titleLabel = new JLabel("CHI TIẾT LỊCH HẸN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Tăng font size lên 16
        titleLabel.setForeground(Color.WHITE);

        // Lấy thông tin từ lichHen để tạo subtitle
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String ngayHenString = dateFormat.format(lichHen.getNgayHen());
        String gioHenString = timeFormat.format(lichHen.getGioHen());
        
        JLabel subtitleLabel = new JLabel("Ngày " + ngayHenString + " lúc " + gioHenString);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12)); // Tăng font size lên 12
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel titlePanelWrapper = new JPanel(new BorderLayout());
        titlePanelWrapper.setBackground(primaryColor);
        titlePanelWrapper.add(titleLabel, BorderLayout.NORTH);
        titlePanelWrapper.add(Box.createVerticalStrut(3), BorderLayout.CENTER); // Thêm khoảng cách
        titlePanelWrapper.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanelWrapper, BorderLayout.CENTER);

        // ========== MAIN CONTENT PANEL ==========
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(10, 10, 0, 10)); // Tăng padding

        // Lấy thông tin từ lichHen
        String hoTenBacSi = lichHen.getHoTenBacSi();
        String hoTenBenhNhan = lichHen.getHoTenBenhNhan();
        String tenPhongKham = lichHen.getTenPhong();
        String trangThai = lichHen.getTrangThai();
        String moTa = lichHen.getMoTa();

        // ========== TOP SECTION - 2 COLUMNS ==========
        JPanel topSection = new JPanel(new GridLayout(1, 2, 15, 0)); // Tăng gap lên 15
        topSection.setBackground(new Color(248, 249, 250));
        topSection.setPreferredSize(new Dimension(610, 140)); // Tăng chiều cao lên 140

        // Left column - Thông tin cơ bản
        JPanel leftCard = createBalancedCard("Thông Tin Cơ Bản");
        addBalancedField(leftCard, "Bác Sĩ", hoTenBacSi);
        addBalancedField(leftCard, "Bệnh Nhân", hoTenBenhNhan);
        addBalancedField(leftCard, "Phòng Khám", tenPhongKham);

        // Right column - Thông tin lịch hẹn
        JPanel rightCard = createBalancedCard("Thông Tin Lịch Hẹn");
        addBalancedField(rightCard, "Ngày Khám", ngayHenString);
        addBalancedField(rightCard, "Thời Gian", gioHenString);
        addStatusField(rightCard, "Trạng Thái", trangThai);

        topSection.add(leftCard);
        topSection.add(rightCard);

        // ========== BOTTOM SECTION - MÔ TẢ ==========
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(new Color(248, 249, 250));
        bottomSection.setPreferredSize(new Dimension(610, 160)); // Giảm xuống 160 để cân bằng

        if (moTa != null && !moTa.trim().isEmpty()) {
            JPanel descriptionCard = createDescriptionCard("Mô Tả Chi Tiết", moTa);
            bottomSection.add(descriptionCard, BorderLayout.CENTER);
        } else {
            JPanel emptyCard = createBalancedCard("Mô Tả Chi Tiết");
            JLabel emptyLabel = new JLabel("Không có mô tả");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            emptyLabel.setForeground(new Color(108, 117, 125));
            emptyLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyCard.add(emptyLabel);
            bottomSection.add(emptyCard, BorderLayout.CENTER);
        }

        // ========== ASSEMBLY MAIN CONTENT ==========
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.add(topSection, BorderLayout.NORTH);
        contentPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER); // Tăng spacing lên 15
        contentPanel.add(bottomSection, BorderLayout.SOUTH);

        // Tạo wrapper panel với khoảng cách bottom
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(248, 249, 250));
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);
        wrapperPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH); // Tăng spacing lên 10
        
        mainPanel.add(wrapperPanel, BorderLayout.CENTER);

        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 25, 15, 25) // Tăng padding đều các phía
        ));
        buttonPanel.setPreferredSize(new Dimension(650, 65)); // Tăng chiều cao lên 60
        
        // Tạo panel con để chứa buttons với spacing tốt hơn
        JPanel buttonsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsContainer.setBackground(Color.WHITE);
        
        Dimension buttonSize = new Dimension(100, 35); // Tăng chiều cao button lên 35
        
        JButton editButton = createRoundedButton("Chỉnh Sửa", warningColor, Color.WHITE, 8, true);
        editButton.setPreferredSize(buttonSize);
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        editButton.addActionListener(e -> {
            dialog.dispose();
            editAppointment(lichHen);
        });
        
        JButton closeButton = createRoundedButton("Đóng", new Color(108, 117, 125), Color.WHITE, 8, false);
        closeButton.setPreferredSize(buttonSize);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        closeButton.addActionListener(e -> dialog.dispose());

        // Thêm buttons với khoảng cách 12px giữa chúng
        buttonsContainer.add(editButton);
        buttonsContainer.add(Box.createHorizontalStrut(12)); // Khoảng cách giữa 2 button
        buttonsContainer.add(closeButton);
        
        buttonPanel.add(buttonsContainer, BorderLayout.EAST);

        // ========== FINAL ASSEMBLY ==========
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    // Helper method để tạo card với spacing cân bằng
    private JPanel createBalancedCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(12, 12, 12, 12) // Tăng padding
        ));
        
        // Header
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Tăng font size lên 13
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0)); // Tăng padding xuống 8
        
        card.add(titleLabel);
        
        return card;
    }

    // Helper method để tạo card mô tả với spacing cân bằng
    private JPanel createDescriptionCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(12, 15, 12, 15) // Tăng padding
        ));
        
        // Header
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Tăng font size lên 13
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Tăng padding xuống 10
        
        // Text area
        JTextArea textArea = new JTextArea(description);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Tăng font size lên 12
        textArea.setForeground(new Color(44, 62, 80));
        textArea.setBackground(new Color(248, 249, 250));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(8, 10, 8, 10)); // Tăng padding
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(580, 110)); // Giảm xuống 110 để cân bằng
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }

    // Helper method để thêm field với spacing cân bằng
    private void addBalancedField(JPanel card, String labelText, String valueText) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Tăng chiều cao lên 30
        fieldPanel.setBorder(new EmptyBorder(0, 0, 6, 0)); // Tăng padding xuống 6
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Tăng font size lên 10
        label.setForeground(new Color(52, 73, 94));
        label.setPreferredSize(new Dimension(80, 24)); // Tăng kích thước

        // Value
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(4, 8, 4, 8) // Tăng padding
        ));
        valuePanel.setBackground(new Color(248, 249, 250));

        JLabel valueLabel = new JLabel(valueText != null ? valueText : "Không xác định");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Tăng font size lên 10
        valueLabel.setForeground(new Color(44, 62, 80));

        valuePanel.add(valueLabel, BorderLayout.CENTER);
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(valuePanel, BorderLayout.CENTER);

        card.add(fieldPanel);
    }

    // Helper method để thêm status field với spacing cân bằng
    private void addStatusField(JPanel card, String labelText, String status) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32)); // Tăng chiều cao
        fieldPanel.setBorder(new EmptyBorder(0, 0, 6, 0)); // Tăng padding
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Tăng font size lên 10
        label.setForeground(new Color(52, 73, 94));
        label.setPreferredSize(new Dimension(80, 26)); // Tăng kích thước

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(4, 8, 4, 8)); // Tăng padding
        
        // Sử dụng method getStatusTextColor để lấy màu
        Color statusColor = getStatusTextColor(status != null ? status : "");
        Color textColor = Color.WHITE;
        
        // Điều chỉnh màu text cho trạng thái "Chờ xác nhận" (màu vàng)
        if (status != null && status.equals("Chờ xác nhận")) {
            textColor = new Color(52, 58, 64); // Màu text tối cho nền vàng
        }
        
        statusPanel.setBackground(statusColor);

        JLabel statusLabel = new JLabel(status != null ? status : "Không xác định");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Tăng font size lên 10
        statusLabel.setForeground(textColor);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(statusPanel, BorderLayout.CENTER);

        card.add(fieldPanel);
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
        gbc.insets = new Insets(6, 5, 6, 5); // Tăng khoảng cách đều
        gbc.weightx = 1.0;

        // Lấy danh sách dữ liệu
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();
        List<String> phongKhamList = qlLichHen.danhSachPhongKham();

        // Tạo searchable combobox cho bác sĩ và bệnh nhân
        JComboBox<String> comboBacSi = createSearchableComboBox(bacSiList, "Nhập tên bác sĩ...");
        comboBacSi.setPreferredSize(new Dimension(250, 35));
        
        JComboBox<String> comboBenhNhan = createSearchableComboBox(benhNhanList, "Nhập tên bệnh nhân...");
        comboBenhNhan.setPreferredSize(new Dimension(250, 35));
        
        // Phòng khám vẫn sử dụng combo thông thường
        phongKhamList.add(0, "Lựa chọn");
        JComboBox<String> comboPhongKham = createStyledComboBox(phongKhamList.toArray(new String[0]));
        comboPhongKham.setPreferredSize(new Dimension(250, 35));
        comboPhongKham.setSelectedIndex(0);

        // DateChooser với style nhất quán và kích thước lớn hơn - LOGIC MỚI CHO NGÀY MẶC ĐỊNH
        JDateChooser dateChooserNgayHen = createStyledDateChooser();
        dateChooserNgayHen.setPreferredSize(new Dimension(250, 35));
        
        // Tính toán ngày mặc định thông minh
        java.util.Date defaultDate = getNextAvailableWorkingDate();
        dateChooserNgayHen.setMinSelectableDate(new java.util.Date());
        dateChooserNgayHen.setDate(defaultDate);

        // Tạo time picker hiện đại với JSpinner - kích thước lớn hơn
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.setPreferredSize(new Dimension(250, 35));
        timePanel.setName("timePanel");
        
        // Spinner cho giờ (7-17) - kích thước lớn hơn
        SpinnerNumberModel hourModel = new SpinnerNumberModel(8, 7, 17, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        hourSpinner.setPreferredSize(new Dimension(60, 32));
        hourSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(normalBorderColor, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        hourSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Spinner cho phút (0, 15, 30, 45) - kích thước lớn hơn
        String[] minutes = {"00", "15", "30", "45"};
        SpinnerListModel minuteModel = new SpinnerListModel(minutes);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setPreferredSize(new Dimension(60, 32));
        minuteSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(normalBorderColor, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        minuteSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Labels cho time picker
        JLabel hourLabel = new JLabel("Giờ:");
        hourLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hourLabel.setForeground(new Color(100, 100, 100));
        
        JLabel minuteLabel = new JLabel("Phút:");
        minuteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        minuteLabel.setForeground(new Color(100, 100, 100));
        
        JLabel colonLabel = new JLabel(":");
        colonLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        colonLabel.setForeground(new Color(100, 100, 100));
        
        // Thêm các component vào timePanel
        timePanel.add(hourLabel);
        timePanel.add(hourSpinner);
        timePanel.add(colonLabel);
        timePanel.add(minuteLabel);
        timePanel.add(minuteSpinner);
        
        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        JComboBox<String> statusComboBox = createStyledComboBox(statuses);
        statusComboBox.setPreferredSize(new Dimension(250, 35));
        JTextField txtMoTa = createStyledTextField("");
        txtMoTa.setPreferredSize(new Dimension(250, 35));

        // Thông tin giờ làm việc - CẬP NHẬT THÔNG TIN
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 221, 242), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel lblWorkingHoursInfo = new JLabel("<html><b>Lưu ý:</b><br/>• Giờ làm việc: Sáng: 7:30-12:00, Chiều: 13:00-17:00<br/>• Lịch hẹn cách nhau ít nhất 30 phút<br/>• <b>Nghỉ thứ 7, chủ nhật</b><br/>• <b>Ngày mặc định được tự động chọn ngày làm việc gần nhất</b><br/>• <b>Có thể nhập tên để tìm kiếm bác sĩ/bệnh nhân</b></html>");
        lblWorkingHoursInfo.setForeground(new Color(70, 130, 180));
        lblWorkingHoursInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(lblWorkingHoursInfo, BorderLayout.CENTER);
        
        // Tạo error panel
        errorPanel = createErrorPanel();
        // Khởi tạo error labels cho từng field
        initializeFieldErrorLabels(comboBacSi, comboBenhNhan, comboPhongKham, dateChooserNgayHen, timePanel);
        JTextComponent bacSiTextComponent = (JTextComponent) comboBacSi.getEditor().getEditorComponent();
        JTextComponent benhNhanTextComponent = (JTextComponent) comboBenhNhan.getEditor().getEditorComponent();

        bacSiTextComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            private void validateBacSi() {
                SwingUtilities.invokeLater(() -> {
                    String currentText = bacSiTextComponent.getText().trim();
                    validateAndClearError(comboBacSi, () -> {
                        return !currentText.isEmpty() && 
                               !currentText.equals("Nhập tên bác sĩ...") && 
                               bacSiList.contains(currentText);
                    });
                });
            }
        });

        // Validation cho bệnh nhân
        benhNhanTextComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            private void validateBenhNhan() {
                SwingUtilities.invokeLater(() -> {
                    String currentText = benhNhanTextComponent.getText().trim();
                    validateAndClearError(comboBenhNhan, () -> {
                        return !currentText.isEmpty() && 
                               !currentText.equals("Nhập tên bệnh nhân...") && 
                               benhNhanList.contains(currentText);
                    });
                });
            }
        });
            
        comboPhongKham.addActionListener(e -> validateAndClearError(comboPhongKham, () -> {
            String selected = (String) comboPhongKham.getSelectedItem();
            return !"Lựa chọn".equals(selected) && selected != null && !selected.trim().isEmpty();
        }));        
        
        // THÊM VALIDATION CHO NGÀY - KIỂM TRA KHÔNG ĐƯỢC CHỌN QUÁ KHỨ VÀ NGÀY NGHỈ
        dateChooserNgayHen.addPropertyChangeListener("date", e -> validateAndClearError(dateChooserNgayHen, () -> {
            java.util.Date selectedDate = dateChooserNgayHen.getDate();
            if (selectedDate == null) return false;
            
            // Kiểm tra ngày không được ở quá khứ
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            selectedCal.set(Calendar.HOUR_OF_DAY, 0);
            selectedCal.set(Calendar.MINUTE, 0);
            selectedCal.set(Calendar.SECOND, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);
            
            if (selectedCal.before(today)) return false;
            
            // Kiểm tra không phải ngày nghỉ (thứ 7, chủ nhật)
            int dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek != Calendar.SUNDAY;
        }));        
        
        // Listeners cho time spinners - CẬP NHẬT VALIDATION THỜI GIAN
        ChangeListener timeChangeListener = e -> validateAndClearError(timePanel, () -> {
            try {
                int hour = (Integer) hourSpinner.getValue();
                String minute = (String) minuteSpinner.getValue();
                java.util.Date selectedDate = dateChooserNgayHen.getDate();
                
                if (selectedDate == null || hour < 7 || hour > 17 || minute == null) {
                    return false;
                }
                
                // Kiểm tra nếu là ngày hôm nay, thời gian phải sau thời điểm hiện tại
                Calendar today = Calendar.getInstance();
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(selectedDate);
                
                // Nếu là cùng ngày
                if (today.get(Calendar.DATE) == selectedCal.get(Calendar.DATE) &&
                    today.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    today.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)) {
                    
                    int currentHour = today.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = today.get(Calendar.MINUTE);
                    int selectedMinuteInt = Integer.parseInt(minute);
                    
                    // Thời gian phải sau thời điểm hiện tại ít nhất 15 phút
                    if (hour < currentHour || 
                        (hour == currentHour && selectedMinuteInt <= currentMinute + 15)) {
                        return false;
                    }
                }
                
                return true;
            } catch (Exception ex) {
                return false;
            }
        });
        hourSpinner.addChangeListener(timeChangeListener);
        minuteSpinner.addChangeListener(timeChangeListener);
        
        // Thêm các thành phần vào form với GridBagLayout
        int currentRow = 0;        
        // Cột nhãn và điều khiển
        addFormRow(formPanel, gbc, currentRow++, "Tên bác sĩ:", comboBacSi);
        addFormRow(formPanel, gbc, currentRow++, "Tên bệnh nhân:", comboBenhNhan);
        addFormRow(formPanel, gbc, currentRow++, "Ngày hẹn:", dateChooserNgayHen);
        addFormRow(formPanel, gbc, currentRow++, "Giờ hẹn:", timePanel);
        addFormRow(formPanel, gbc, currentRow++, "Phòng khám:", comboPhongKham);
        addFormRow(formPanel, gbc, currentRow++, "Trạng thái:", statusComboBox);
        addFormRow(formPanel, gbc, currentRow++, "Mô tả:", txtMoTa);
        
        // Thêm error panel
        gbc.gridx = 0;
        gbc.gridy = currentRow++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(errorPanel, gbc);        
        // Thêm info panel
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(10, 5, 8, 5);
        formPanel.add(infoPanel, gbc);        
        // Tạo scroll pane với kích thước lớn hơn
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(480, 520)); // Tăng kích thước để hiển thị info thêm        
        // Thêm formPanel vào phần trung tâm của mainPanel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Tạo panel nút với FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton cancelButton = createRoundedButton("Hủy", Color.LIGHT_GRAY, Color.BLACK, cornerRadius);
        cancelButton.setPreferredSize(new Dimension(90, 35));
        JButton saveButton = createRoundedButton("Lưu", primaryColor, Color.WHITE, cornerRadius);
        saveButton.setPreferredSize(new Dimension(90, 35));

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Hiển thị dialog với panel chính - kích thước lớn hơn
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm Lịch Hẹn", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(550, 640); // Tăng kích thước dialog
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Xử lý sự kiện nút hủy
        cancelButton.addActionListener(e -> {
            clearAllFieldErrors();
            dialog.dispose();
        });
        
        // Xử lý sự kiện nút lưu với validation mới - CẬP NHẬT VALIDATION
        saveButton.addActionListener(e -> {
            try {
                clearAllFieldErrors();                
                boolean hasErrors = false;                
                
                // Validate bác sĩ từ searchable combobox
                String hoTenBacSi = getSelectedValueFromSearchableCombo(comboBacSi, "Nhập tên bác sĩ...").trim();
                if ("Lựa chọn".equals(hoTenBacSi) || hoTenBacSi.equals("Nhập tên bác sĩ...") || hoTenBacSi.isEmpty()) {
                    showPersistentFieldError(comboBacSi, "Vui lòng chọn bác sĩ");
                    hasErrors = true;
                } else if (!bacSiList.contains(hoTenBacSi)) {
                    showPersistentFieldError(comboBacSi, "Tên bác sĩ không tồn tại trong hệ thống");
                    hasErrors = true;
                }
                
                // Validate bệnh nhân từ searchable combobox
                String hoTenBenhNhan = getSelectedValueFromSearchableCombo(comboBenhNhan, "Nhập tên bệnh nhân...").trim();
                if ("Lựa chọn".equals(hoTenBenhNhan) || hoTenBenhNhan.equals("Nhập tên bệnh nhân...") || hoTenBenhNhan.isEmpty()) {
                    showPersistentFieldError(comboBenhNhan, "Vui lòng chọn bệnh nhân");
                    hasErrors = true;
                } else if (!benhNhanList.contains(hoTenBenhNhan)) {
                    showPersistentFieldError(comboBenhNhan, "Tên bệnh nhân không tồn tại trong hệ thống");
                    hasErrors = true;
                }
                    
                String tenPhongKham = ((String) comboPhongKham.getSelectedItem()).trim();
                if ("Lựa chọn".equals(tenPhongKham)) {
                    showPersistentFieldError(comboPhongKham, "Vui lòng chọn phòng khám");
                    hasErrors = true;
                }                
                java.util.Date ngayHenDate = dateChooserNgayHen.getDate();
                if (ngayHenDate == null) {
                    showPersistentFieldError(dateChooserNgayHen, "Vui lòng chọn ngày hẹn");
                    hasErrors = true;
                } else {
                    // VALIDATION NGÀY KHÔNG ĐƯỢC Ở QUÁ KHỨ
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.setTime(ngayHenDate);
                    selectedCal.set(Calendar.HOUR_OF_DAY, 0);
                    selectedCal.set(Calendar.MINUTE, 0);
                    selectedCal.set(Calendar.SECOND, 0);
                    selectedCal.set(Calendar.MILLISECOND, 0);
                    
                    if (selectedCal.before(today)) {
                        showPersistentFieldError(dateChooserNgayHen, "Không thể đặt lịch hẹn trong quá khứ");
                        hasErrors = true;
                    }
                    
                    // VALIDATION NGÀY NGHỈ
                    int dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        showPersistentFieldError(dateChooserNgayHen, "Không thể đặt lịch hẹn vào ngày nghỉ (thứ 7, chủ nhật)");
                        hasErrors = true;
                    }
                }
                
                // Validate time
                int selectedHour = (Integer) hourSpinner.getValue();
                String selectedMinute = (String) minuteSpinner.getValue();
                if (selectedHour < 7 || selectedHour > 17) {
                    showPersistentFieldError(timePanel, "Giờ hẹn phải trong khoảng 7:00 - 17:00");
                    hasErrors = true;
                } else if (ngayHenDate != null) {
                    // VALIDATION THỜI GIAN KHÔNG ĐƯỢC Ở QUÁ KHỨ NÕU LÀ HÔM NAY
                    Calendar now = Calendar.getInstance();
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.setTime(ngayHenDate);
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, Integer.parseInt(selectedMinute));
                    selectedDateTime.set(Calendar.SECOND, 0);
                    selectedDateTime.set(Calendar.MILLISECOND, 0);
                    
                    // Kiểm tra nếu là ngày hôm nay và thời gian đã qua hoặc quá gần
                    if (selectedDateTime.get(Calendar.DATE) == now.get(Calendar.DATE) &&
                        selectedDateTime.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        selectedDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                        
                        // Thêm 15 phút buffer để đảm bảo có thời gian chuẩn bị
                        now.add(Calendar.MINUTE, 15);
                        
                        if (selectedDateTime.before(now) || selectedDateTime.equals(now)) {
                            showPersistentFieldError(timePanel, "Thời gian hẹn phải sau thời điểm hiện tại ít nhất 15 phút");
                            hasErrors = true;
                        }
                    }
                }
                
                if (hasErrors) {
                    return;
                }
                // Tiếp tục xử lý logic lưu dữ liệu
                int idBacSi = qlLichHen.getBacSiIdFromName(hoTenBacSi);
                int idBenhNhan = qlLichHen.getBenhNhanIdFromName(hoTenBenhNhan);
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhongKham);

                if (idBacSi == -1) {
                    showPersistentFieldError(comboBacSi, "Tên bác sĩ không hợp lệ");
                    return;
                }
                if (idBenhNhan == -1) {
                    showPersistentFieldError(comboBenhNhan, "Tên bệnh nhân không hợp lệ");
                    return;
                }
                if (idPhongKham == -1) {
                    showPersistentFieldError(comboPhongKham, "Tên phòng khám không hợp lệ");
                    return;
                }       
                java.sql.Date ngayHen = new java.sql.Date(ngayHenDate.getTime());
                String timeString = String.format("%02d:%s", selectedHour, selectedMinute);
                LocalTime gioHen = LocalTime.parse(timeString);
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
                    showPersistentFieldError(timePanel, validationError);
                    return;
                }                
                // Tạo đối tượng lịch hẹn mới
                LichHen lichHen = new LichHen(0, idBacSi, hoTenBacSi, idBenhNhan, hoTenBenhNhan, 
                        ngayHen, idPhongKham, tenPhongKham, timeGioHen, trangThaiEnum, moTa);
                // Thực hiện lưu và kiểm tra kết quả
                boolean success = qlLichHen.datLichHen(lichHen);
                if (success) {
                    // Clear tất cả errors trước khi đóng
                    clearAllFieldErrors();
                    dialog.dispose();
                    updateCalendar();
                    showSuccessToast("Thêm lịch hẹn thành công!");
                } else {
                    showPersistentFieldError(saveButton, "Không thể thêm lịch hẹn. Vui lòng thử lại");
                }
            } catch (DateTimeParseException ex) {
                showPersistentFieldError(timePanel, "Lỗi định dạng thời gian");
            } catch (Exception ex) {
                showPersistentFieldError(saveButton, "Lỗi nhập liệu! Vui lòng thử lại");
                ex.printStackTrace();
            }
        });
        
        dialog.setVisible(true);
    }
    private JComboBox<String> createSearchableComboBox(List<String> originalData, String defaultText) {
        // Create copy of original data and add default text at the beginning
        List<String> dataWithDefault = new ArrayList<>(originalData);
        dataWithDefault.add(0, defaultText);

        JComboBox<String> comboBox = createStyledComboBox(dataWithDefault.toArray(new String[0]));
        comboBox.setEditable(true);

        // Get the text component of the editor
        JTextComponent textComponent = (JTextComponent) comboBox.getEditor().getEditorComponent();

        // Store original data for filtering
        final List<String> originalItems = new ArrayList<>(originalData);

        // Flags to control behavior
        // isFilteringItems: true when the DocumentListener is actively filtering/updating the model
        final AtomicBoolean isFilteringItems = new AtomicBoolean(false);
        // isSelectingFromDropdown: true when an item is selected from the dropdown (programmatic text change)
        final AtomicBoolean isSelectingFromDropdown = new AtomicBoolean(false);
        // hasFocus: true when the text component has focus
        final AtomicBoolean hasFocus = new AtomicBoolean(false);
        // ignoreDocumentEvents: A crucial flag to prevent re-triggering filtering
        final AtomicBoolean ignoreDocumentEvents = new AtomicBoolean(false);

        // Custom Document to get better control
        class SearchDocument extends PlainDocument {
            private Timer filterTimer;

            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if (ignoreDocumentEvents.get()) { // Ignore if we are programmatically updating
                    super.insertString(offset, str, attr);
                    return;
                }
                super.insertString(offset, str, attr);
                scheduleFilter();
            }

            @Override
            public void remove(int offset, int length) throws BadLocationException {
                if (ignoreDocumentEvents.get()) { // Ignore if we are programmatically updating
                    super.remove(offset, length);
                    return;
                }
                super.remove(offset, length);
                scheduleFilter();
            }

            private void scheduleFilter() {
                if (filterTimer != null && filterTimer.isRunning()) {
                    filterTimer.stop();
                }

                filterTimer = new Timer(200, e -> {
                    if (hasFocus.get() && !isFilteringItems.get() && !isSelectingFromDropdown.get()) {
                        performFiltering();
                    }
                });
                filterTimer.setRepeats(false);
                filterTimer.start();
            }

            private void performFiltering() {
                SwingUtilities.invokeLater(() -> {
                    // Ensure no conflicting operations
                    if (isFilteringItems.get() || isSelectingFromDropdown.get()) return;

                    try {
                        String searchText = textComponent.getText().trim();

                        if (searchText.isEmpty() || searchText.equals(defaultText)) {
                            updateDropdownItems(originalItems); // Show all items if text is empty or default
                            return;
                        }

                        // Filter items
                        List<String> filteredItems = originalItems.stream()
                            .filter(item -> item.toLowerCase().contains(searchText.toLowerCase()))
                            .collect(Collectors.toList());

                        updateDropdownItems(filteredItems);

                        // Show popup if has results and focused
                        if (!filteredItems.isEmpty() && hasFocus.get()) {
                            SwingUtilities.invokeLater(() -> {
                                if (!comboBox.isPopupVisible()) {
                                    comboBox.showPopup();
                                }
                            });
                        } else if (filteredItems.isEmpty() && comboBox.isPopupVisible()) {
                            comboBox.hidePopup(); // Hide if no results
                        }

                    } catch (Exception ex) {
                        // Log or handle the error appropriately, but for filtering, often ignored
                        System.err.println("Error during filtering: " + ex.getMessage());
                    }
                });
            }

            private void updateDropdownItems(List<String> items) {
                isFilteringItems.set(true); // Indicate that we are updating items
                ignoreDocumentEvents.set(true); // Crucial: temporarily ignore document changes

                try {
                    String currentText = textComponent.getText(); // Store current text
                    int caretPos = textComponent.getCaretPosition(); // Store caret position

                    // Temporarily remove action listeners to prevent unwanted side effects
                    // when programmatically setting selected item (though not directly here)
                    ActionListener[] listeners = comboBox.getActionListeners();
                    for (ActionListener listener : listeners) {
                        comboBox.removeActionListener(listener);
                    }

                    // Clear existing items and add filtered ones
                    comboBox.removeAllItems();
                    comboBox.addItem(defaultText); // Always include default option
                    for (String item : items) {
                        comboBox.addItem(item);
                    }

                    // Restore action listeners
                    for (ActionListener listener : listeners) {
                        comboBox.addActionListener(listener);
                    }

                    // Restore text and caret position.
                    // Because ignoreDocumentEvents is true, this setText won't re-trigger filtering.
                    textComponent.setText(currentText);
                    textComponent.setCaretPosition(Math.min(caretPos, currentText.length()));

                } finally {
                    isFilteringItems.set(false); // Reset flag
                    ignoreDocumentEvents.set(false); // Reset flag
                }
            }
        }

        // Replace the default document with our custom one
        textComponent.setDocument(new SearchDocument());

        // Action listener for when an item is selected from the dropdown
        comboBox.addActionListener(e -> {
            // Only process if user is not actively filtering or if it's not a programmatic selection
            if (isFilteringItems.get() || isSelectingFromDropdown.get()) {
                return;
            }

            Object selectedItem = comboBox.getSelectedItem();
            if (selectedItem != null && !selectedItem.equals(defaultText)) {
                String selectedText = selectedItem.toString();
                String currentTextInEditor = textComponent.getText();

                // Only update if the selected item is different from what's currently in the editor
                if (!selectedText.equals(currentTextInEditor)) {
                    isSelectingFromDropdown.set(true); // Indicate programmatic change
                    ignoreDocumentEvents.set(true); // Crucial: Prevent document listener from reacting
                    try {
                        textComponent.setText(selectedText);
                        textComponent.setCaretPosition(selectedText.length());
                        comboBox.hidePopup(); // Hide the popup after selection
                    } finally {
                        isSelectingFromDropdown.set(false); // Reset flag
                        ignoreDocumentEvents.set(false); // Reset flag
                    }
                }
            }
        });

        // Focus listeners to handle default text and filtering on focus
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                hasFocus.set(true);
                String currentText = textComponent.getText();

                // Clear default text when focus is gained
                if (defaultText.equals(currentText)) {
                    isSelectingFromDropdown.set(true); // Treat as programmatic
                    ignoreDocumentEvents.set(true);
                    try {
                        textComponent.setText("");
                        textComponent.setCaretPosition(0);
                    } finally {
                        isSelectingFromDropdown.set(false);
                        ignoreDocumentEvents.set(false);
                    }
                }
                // Ensure popup is visible if items are filtered and there's text
                if (!currentText.isEmpty() && !currentText.equals(defaultText)) {
                     // Trigger a filter to ensure the popup shows relevant items
                    ((SearchDocument) textComponent.getDocument()).performFiltering();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasFocus.set(false);

                // Delay to allow dropdown selection to complete before resetting text
                Timer delayTimer = new Timer(150, event -> {
                    String currentText = textComponent.getText().trim();

                    // If text is empty or not a valid item from original list, revert to default
                    if (currentText.isEmpty() || !originalItems.contains(currentText)) {
                        isSelectingFromDropdown.set(true); // Treat as programmatic
                        ignoreDocumentEvents.set(true);
                        try {
                            textComponent.setText(defaultText);
                            textComponent.setCaretPosition(0);
                        } finally {
                            isSelectingFromDropdown.set(false);
                            ignoreDocumentEvents.set(false);
                        }
                    }
                    comboBox.hidePopup(); // Always hide popup on focus loss
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });

        return comboBox;
    }
    private String getSelectedValueFromSearchableCombo(JComboBox<String> comboBox, String defaultText) {
        JTextComponent textComponent = (JTextComponent) comboBox.getEditor().getEditorComponent();
        String currentText = textComponent.getText().trim();
        
        if (currentText.isEmpty() || currentText.equals(defaultText)) {
            return "Lựa chọn"; 
        }
        
        return currentText;
    }

    // Remove the old updateComboBoxItems methods as they're no longer needed
    private java.util.Date getNextAvailableWorkingDate() {
        Calendar cal = Calendar.getInstance();
        
        // Kiểm tra xem hôm nay có thể đặt lịch được không
        if (isTodayBookable()) {
            return cal.getTime();
        }
        
        // Nếu hôm nay không thể đặt lịch, tìm ngày làm việc tiếp theo
        cal.add(Calendar.DAY_OF_MONTH, 1); // Chuyển sang ngày mai
        
        // Tìm ngày làm việc tiếp theo (không phải thứ 7, chủ nhật)
        while (true) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            
            // Nếu là ngày làm việc (thứ 2-6)
            if (dayOfWeek != Calendar.SUNDAY) {
                return cal.getTime();
            }
            
            // Nếu là ngày nghỉ, chuyển sang ngày tiếp theo
            cal.add(Calendar.DAY_OF_MONTH, 1);
            
            // Tránh vòng lặp vô hạn (tối đa 7 ngày)
            if (cal.get(Calendar.DAY_OF_MONTH) > Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7) {
                break;
            }
        }
        
        return cal.getTime();
    }
    private boolean isTodayBookable() {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY) {
            return false;
        }
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        if (currentHour < 11 || (currentHour == 11 && currentMinute <= 30)) {
            return true;
        }
        if (currentHour >= 13 && (currentHour < 16 || (currentHour == 16 && currentMinute <= 30))) {
            return true;
        }
        return false;
    }
    private void deleteAppointment(LichHen lichHen) {
        // Tạo dialog xác nhận tùy chỉnh
        JDialog confirmDialog = new JDialog();
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setModal(true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        
        // Panel chính
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel thông điệp
        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("<html>Bạn có chắc muốn xóa lịch hẹn này?</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Nút Hủy
        JButton cancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8, false);
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        // Nút Xóa
        JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8, false);
        deleteButton.addActionListener(e -> {
            confirmDialog.dispose();
            try {
                boolean success = qlLichHen.deleteLichHen(lichHen.getIdLichHen());
                if (success) {
                    // Cập nhật lịch trước
                    updateCalendar();
                    showSuccessToast("Lịch hẹn đã được xóa thành công!");                    
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa lịch hẹn. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setContentPane(panel);
        confirmDialog.setVisible(true);
    }    
    private void clearAllFieldErrors() {
        for (JComponent field : fieldErrorLabels.keySet()) {
            clearFieldError(field);
        }
    }
    private void addFormRow(JPanel formPanel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 5, 8, 5);
        formPanel.add(createStyledLabel(labelText), gbc);
        
        // Component
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(component, gbc);
        
        // Error label (sẽ được thêm bên dưới component)
        JLabel errorLabel = fieldErrorLabels.get(component);
        if (errorLabel != null) {
            gbc.gridx = 1;
            gbc.gridy = row;
            gbc.insets = new Insets(0, 5, 8, 5); // Giảm khoảng cách trên để error label gần component hơn
            gbc.anchor = GridBagConstraints.NORTHWEST;
            
            // Tạo một panel wrapper để chứa cả component và error label
            JPanel wrapperPanel = new JPanel(new BorderLayout(0, 2));
            wrapperPanel.setBackground(Color.WHITE);
            wrapperPanel.add(component, BorderLayout.CENTER);
            wrapperPanel.add(errorLabel, BorderLayout.SOUTH);
            
            // Thay thế component bằng wrapper panel
            formPanel.remove(component);
            formPanel.add(wrapperPanel, gbc);
        }
    }
    private void initializeFieldErrorLabels(JComponent... components) {
        fieldErrorLabels.clear();
        for (JComponent component : components) {
            JLabel errorLabel = new JLabel();
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            errorLabel.setForeground(errorBorderColor);
            errorLabel.setVisible(false);
            fieldErrorLabels.put(component, errorLabel);
        }
    }
    private void showPersistentFieldError(JComponent field, String message) {
        // Đặt viền đỏ cho field
        setBorderError(field, true);
        
        // Hiển thị error label riêng cho field này
        JLabel errorLabel = fieldErrorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }        
        // Hủy timer cũ nếu có
        Timer oldTimer = fieldErrorTimers.get(field);
        if (oldTimer != null) {
            oldTimer.stop();
            fieldErrorTimers.remove(field);
        }
    }
    private void validateAndClearError(JComponent field, Supplier<Boolean> validator) {
        try {
            if (validator.get()) {
                clearFieldError(field);
            }
        } catch (Exception e) {
            // Nếu validation throw exception, không clear error
        }
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
    private void editAppointment(LichHen lichHen) {
        // Tạo panel chính với BorderLayout để tổ chức các thành phần
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Tạo tiêu đề với màu sắc nổi bật
        JLabel titleLabel = new JLabel("Chỉnh Sửa Lịch Hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chính để nhập thông tin với GridBagLayout cho bố cục linh hoạt
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5); // Tăng khoảng cách đều
        gbc.weightx = 1.0;

        // Lấy danh sách dữ liệu
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();
        List<String> phongKhamList = qlLichHen.danhSachPhongKham();

        // Tạo searchable combobox cho bác sĩ và bệnh nhân
        JComboBox<String> comboBacSi = createSearchableComboBox(bacSiList, "Nhập tên bác sĩ...");
        comboBacSi.setPreferredSize(new Dimension(250, 35));
        // Set giá trị hiện tại cho bác sĩ
        JTextComponent bacSiTextComponent = (JTextComponent) comboBacSi.getEditor().getEditorComponent();
        bacSiTextComponent.setText(lichHen.getHoTenBacSi());
        
        JComboBox<String> comboBenhNhan = createSearchableComboBox(benhNhanList, "Nhập tên bệnh nhân...");
        comboBenhNhan.setPreferredSize(new Dimension(250, 35));
        // Set giá trị hiện tại cho bệnh nhân
        JTextComponent benhNhanTextComponent = (JTextComponent) comboBenhNhan.getEditor().getEditorComponent();
        benhNhanTextComponent.setText(lichHen.getHoTenBenhNhan());
        
        // Phòng khám vẫn sử dụng combo thông thường
        phongKhamList.add(0, "Lựa chọn");
        JComboBox<String> comboPhongKham = createStyledComboBox(phongKhamList.toArray(new String[0]));
        comboPhongKham.setPreferredSize(new Dimension(250, 35));
        comboPhongKham.setSelectedItem(lichHen.getTenPhong());

        // DateChooser với style nhất quán và kích thước lớn hơn - THÊM VALIDATION NGHỈ
        JDateChooser dateChooserNgayHen = createStyledDateChooser();
        dateChooserNgayHen.setPreferredSize(new Dimension(250, 35));
        dateChooserNgayHen.setMinSelectableDate(new java.util.Date());
        dateChooserNgayHen.setDate(java.sql.Date.valueOf(lichHen.getNgayHen().toLocalDate()));

        // Tạo time picker hiện đại với JSpinner - kích thước lớn hơn
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.setPreferredSize(new Dimension(250, 35));
        timePanel.setName("timePanel");
        
        LocalTime currentTime = lichHen.getGioHen().toLocalTime();
        int currentHour = currentTime.getHour();
        int currentMinute = currentTime.getMinute();

        // Spinner cho giờ (7-17) - kích thước lớn hơn
        SpinnerNumberModel hourModel = new SpinnerNumberModel(currentHour, 7, 17, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        hourSpinner.setPreferredSize(new Dimension(60, 32));
        hourSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(normalBorderColor, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        hourSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Spinner cho phút (0, 15, 30, 45) - kích thước lớn hơn
        String[] minutes = {"00", "15", "30", "45"};
        String currentMinuteStr = String.format("%02d", currentMinute);
        // Tìm phút gần nhất trong danh sách
        String defaultMinute = "00";
        for (String minute : minutes) {
            if (minute.equals(currentMinuteStr)) {
                defaultMinute = minute;
                break;
            }
        }
        // Nếu không tìm thấy chính xác, chọn phút gần nhất
        if (!defaultMinute.equals(currentMinuteStr)) {
            if (currentMinute <= 7) defaultMinute = "00";
            else if (currentMinute <= 22) defaultMinute = "15";
            else if (currentMinute <= 37) defaultMinute = "30";
            else defaultMinute = "45";
        }

        SpinnerListModel minuteModel = new SpinnerListModel(minutes);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setValue(defaultMinute);
        minuteSpinner.setPreferredSize(new Dimension(60, 32));
        minuteSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(normalBorderColor, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        minuteSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Labels cho time picker
        JLabel hourLabel = new JLabel("Giờ:");
        hourLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hourLabel.setForeground(new Color(100, 100, 100));
        
        JLabel minuteLabel = new JLabel("Phút:");
        minuteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        minuteLabel.setForeground(new Color(100, 100, 100));
        
        JLabel colonLabel = new JLabel(":");
        colonLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        colonLabel.setForeground(new Color(100, 100, 100));
        
        // Thêm các component vào timePanel
        timePanel.add(hourLabel);
        timePanel.add(hourSpinner);
        timePanel.add(colonLabel);
        timePanel.add(minuteLabel);
        timePanel.add(minuteSpinner);
        
        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        JComboBox<String> statusComboBox = createStyledComboBox(statuses);
        statusComboBox.setPreferredSize(new Dimension(250, 35));
        statusComboBox.setSelectedItem(lichHen.getTrangThai());
        
        JTextField txtMoTa = createStyledTextField(lichHen.getMoTa());
        txtMoTa.setPreferredSize(new Dimension(250, 35));

        // ID của lịch hẹn - chỉ hiển thị
        JTextField txtIdLichHen = createStyledTextField(String.valueOf(lichHen.getIdLichHen()));
        txtIdLichHen.setPreferredSize(new Dimension(250, 35));
        txtIdLichHen.setEditable(false);
        txtIdLichHen.setBackground(new Color(245, 245, 245));

        // Thông tin giờ làm việc - CẬP NHẬT THÔNG TIN
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 221, 242), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel lblWorkingHoursInfo = new JLabel("<html><b>Lưu ý:</b><br/>• Giờ làm việc: Sáng: 7:30-12:00, Chiều: 13:00-17:00<br/>• Lịch hẹn cách nhau ít nhất 30 phút<br/>• <b>Nghỉ thứ 7, chủ nhật</b><br/>• <b>Có thể nhập tên để tìm kiếm bác sĩ/bệnh nhân</b></html>");
        lblWorkingHoursInfo.setForeground(new Color(70, 130, 180));
        lblWorkingHoursInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(lblWorkingHoursInfo, BorderLayout.CENTER);

        // Tạo error panel
        errorPanel = createErrorPanel();
        // Khởi tạo error labels cho từng field
        initializeFieldErrorLabels(comboBacSi, comboBenhNhan, comboPhongKham, dateChooserNgayHen, timePanel);

        bacSiTextComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateBacSi();
            }
            
            private void validateBacSi() {
                SwingUtilities.invokeLater(() -> {
                    String currentText = bacSiTextComponent.getText().trim();
                    validateAndClearError(comboBacSi, () -> {
                        return !currentText.isEmpty() && 
                               !currentText.equals("Nhập tên bác sĩ...") && 
                               bacSiList.contains(currentText);
                    });
                });
            }
        });

        // Validation cho bệnh nhân
        benhNhanTextComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateBenhNhan();
            }
            
            private void validateBenhNhan() {
                SwingUtilities.invokeLater(() -> {
                    String currentText = benhNhanTextComponent.getText().trim();
                    validateAndClearError(comboBenhNhan, () -> {
                        return !currentText.isEmpty() && 
                               !currentText.equals("Nhập tên bệnh nhân...") && 
                               benhNhanList.contains(currentText);
                    });
                });
            }
        });
            
        comboPhongKham.addActionListener(e -> validateAndClearError(comboPhongKham, () -> {
            String selected = (String) comboPhongKham.getSelectedItem();
            return !"Lựa chọn".equals(selected) && selected != null && !selected.trim().isEmpty();
        }));        
        
        // THÊM VALIDATION CHO NGÀY - KIỂM TRA KHÔNG ĐƯỢC CHỌN QUÁ KHỨ VÀ NGÀY NGHỈ
        dateChooserNgayHen.addPropertyChangeListener("date", e -> validateAndClearError(dateChooserNgayHen, () -> {
            java.util.Date selectedDate = dateChooserNgayHen.getDate();
            if (selectedDate == null) return false;
            
            // Kiểm tra ngày không được ở quá khứ
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            selectedCal.set(Calendar.HOUR_OF_DAY, 0);
            selectedCal.set(Calendar.MINUTE, 0);
            selectedCal.set(Calendar.SECOND, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);
            
            if (selectedCal.before(today)) return false;
            
            // Kiểm tra không phải ngày nghỉ (thứ 7, chủ nhật)
            int dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek != Calendar.SUNDAY;
        }));        
        
        // Listeners cho time spinners - CẬP NHẬT VALIDATION THỜI GIAN
        ChangeListener timeChangeListener = e -> validateAndClearError(timePanel, () -> {
            try {
                int hour = (Integer) hourSpinner.getValue();
                String minute = (String) minuteSpinner.getValue();
                java.util.Date selectedDate = dateChooserNgayHen.getDate();
                
                if (selectedDate == null || hour < 7 || hour > 17 || minute == null) {
                    return false;
                }
                
                // Kiểm tra nếu là ngày hôm nay, thời gian phải sau thời điểm hiện tại
                Calendar today = Calendar.getInstance();
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(selectedDate);
                
                // Nếu là cùng ngày
                if (today.get(Calendar.DATE) == selectedCal.get(Calendar.DATE) &&
                    today.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    today.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)) {
                    
                    int selectedMinuteInt = Integer.parseInt(minute);
                    
                    // Thời gian phải sau thời điểm hiện tại ít nhất 15 phút
                    if (hour < currentHour || 
                        (hour == currentHour && selectedMinuteInt <= currentMinute + 15)) {
                        return false;
                    }
                }
                
                return true;
            } catch (Exception ex) {
                return false;
            }
        });
        hourSpinner.addChangeListener(timeChangeListener);
        minuteSpinner.addChangeListener(timeChangeListener);

        // Thêm các thành phần vào form với GridBagLayout
        int currentRow = 0;
        
        // Cột nhãn và điều khiển
        addFormRow(formPanel, gbc, currentRow++, "ID Lịch hẹn:", txtIdLichHen);
        addFormRow(formPanel, gbc, currentRow++, "Tên bác sĩ:", comboBacSi);
        addFormRow(formPanel, gbc, currentRow++, "Tên bệnh nhân:", comboBenhNhan);
        addFormRow(formPanel, gbc, currentRow++, "Ngày hẹn:", dateChooserNgayHen);
        addFormRow(formPanel, gbc, currentRow++, "Giờ hẹn:", timePanel);
        addFormRow(formPanel, gbc, currentRow++, "Phòng khám:", comboPhongKham);
        addFormRow(formPanel, gbc, currentRow++, "Trạng thái:", statusComboBox);
        addFormRow(formPanel, gbc, currentRow++, "Mô tả:", txtMoTa);
        
        // Thêm error panel
        gbc.gridx = 0;
        gbc.gridy = currentRow++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(errorPanel, gbc);        
        // Thêm info panel
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(10, 5, 8, 5);
        formPanel.add(infoPanel, gbc);        
        // Tạo scroll pane với kích thước lớn hơn
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(480, 520)); // Tăng kích thước để hiển thị info thêm        
        // Thêm formPanel vào phần trung tâm của mainPanel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Tạo panel nút với FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton cancelButton = createRoundedButton("Hủy", Color.LIGHT_GRAY, Color.BLACK, cornerRadius);
        cancelButton.setPreferredSize(new Dimension(90, 35));
        JButton saveButton = createRoundedButton("Lưu", primaryColor, Color.WHITE, cornerRadius);
        saveButton.setPreferredSize(new Dimension(90, 35));
        JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, cornerRadius);
        deleteButton.setPreferredSize(new Dimension(90, 35));

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Hiển thị dialog với panel chính - kích thước lớn hơn
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Lịch Hẹn", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(550, 640); // Tăng kích thước dialog
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Xử lý sự kiện nút hủy
        cancelButton.addActionListener(e -> {
            clearAllFieldErrors();
            dialog.dispose();
        });

        // Xử lý sự kiện nút xóa
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc chắn muốn xóa lịch hẹn này?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean success = qlLichHen.deleteLichHen(lichHen.getIdLichHen());
                    if (success) {
                        clearAllFieldErrors();
                        dialog.dispose();                        
                        updateCalendar();                        
                        showSuccessToast("Xóa lịch hẹn thành công!");
                    } else {
                        showPersistentFieldError(deleteButton, "Không thể xóa lịch hẹn. Vui lòng thử lại");
                    }
                } catch (Exception ex) {
                    showPersistentFieldError(deleteButton, "Lỗi khi xóa lịch hẹn");
                    ex.printStackTrace();
                }
            }
        });
        
        // Xử lý sự kiện nút lưu với validation mới - CẬP NHẬT VALIDATION
        saveButton.addActionListener(e -> {
            try {
                clearAllFieldErrors();                
                boolean hasErrors = false;                
                
                // Validate bác sĩ từ searchable combobox
                String hoTenBacSi = getSelectedValueFromSearchableCombo(comboBacSi, "Nhập tên bác sĩ...").trim();
                if ("Lựa chọn".equals(hoTenBacSi) || hoTenBacSi.equals("Nhập tên bác sĩ...") || hoTenBacSi.isEmpty()) {
                    showPersistentFieldError(comboBacSi, "Vui lòng chọn bác sĩ");
                    hasErrors = true;
                } else if (!bacSiList.contains(hoTenBacSi)) {
                    showPersistentFieldError(comboBacSi, "Tên bác sĩ không tồn tại trong hệ thống");
                    hasErrors = true;
                }
                
                // Validate bệnh nhân từ searchable combobox
                String hoTenBenhNhan = getSelectedValueFromSearchableCombo(comboBenhNhan, "Nhập tên bệnh nhân...").trim();
                if ("Lựa chọn".equals(hoTenBenhNhan) || hoTenBenhNhan.equals("Nhập tên bệnh nhân...") || hoTenBenhNhan.isEmpty()) {
                    showPersistentFieldError(comboBenhNhan, "Vui lòng chọn bệnh nhân");
                    hasErrors = true;
                } else if (!benhNhanList.contains(hoTenBenhNhan)) {
                    showPersistentFieldError(comboBenhNhan, "Tên bệnh nhân không tồn tại trong hệ thống");
                    hasErrors = true;
                }
                    
                String tenPhongKham = ((String) comboPhongKham.getSelectedItem()).trim();
                if ("Lựa chọn".equals(tenPhongKham)) {
                    showPersistentFieldError(comboPhongKham, "Vui lòng chọn phòng khám");
                    hasErrors = true;
                }                
                java.util.Date ngayHenDate = dateChooserNgayHen.getDate();
                if (ngayHenDate == null) {
                    showPersistentFieldError(dateChooserNgayHen, "Vui lòng chọn ngày hẹn");
                    hasErrors = true;
                } else {
                    // VALIDATION NGÀY KHÔNG ĐƯỢC Ở QUÁ KHỨ
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.setTime(ngayHenDate);
                    selectedCal.set(Calendar.HOUR_OF_DAY, 0);
                    selectedCal.set(Calendar.MINUTE, 0);
                    selectedCal.set(Calendar.SECOND, 0);
                    selectedCal.set(Calendar.MILLISECOND, 0);
                    
                    if (selectedCal.before(today)) {
                        showPersistentFieldError(dateChooserNgayHen, "Không thể đặt lịch hẹn trong quá khứ");
                        hasErrors = true;
                    }
                    
                    // VALIDATION NGÀY NGHỈ
                    int dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        showPersistentFieldError(dateChooserNgayHen, "Không thể đặt lịch hẹn vào ngày nghỉ (thứ 7, chủ nhật)");
                        hasErrors = true;
                    }
                }
                
                // Validate time
                int selectedHour = (Integer) hourSpinner.getValue();
                String selectedMinute = (String) minuteSpinner.getValue();
                if (selectedHour < 7 || selectedHour > 17) {
                    showPersistentFieldError(timePanel, "Giờ hẹn phải trong khoảng 7:00 - 17:00");
                    hasErrors = true;
                } else if (ngayHenDate != null) {
                    // VALIDATION THỜI GIAN KHÔNG ĐƯỢC Ở QUÁ KHỨ NÕU LÀ HÔM NAY
                    Calendar now = Calendar.getInstance();
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.setTime(ngayHenDate);
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, Integer.parseInt(selectedMinute));
                    selectedDateTime.set(Calendar.SECOND, 0);
                    selectedDateTime.set(Calendar.MILLISECOND, 0);
                    
                    // Kiểm tra nếu là ngày hôm nay và thời gian đã qua hoặc quá gần
                    if (selectedDateTime.get(Calendar.DATE) == now.get(Calendar.DATE) &&
                        selectedDateTime.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        selectedDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                        
                        // Thêm 15 phút buffer để đảm bảo có thời gian chuẩn bị
                        now.add(Calendar.MINUTE, 15);
                        
                        if (selectedDateTime.before(now) || selectedDateTime.equals(now)) {
                            showPersistentFieldError(timePanel, "Thời gian hẹn phải sau thời điểm hiện tại ít nhất 15 phút");
                            hasErrors = true;
                        }
                    }
                }
                
                if (hasErrors) {
                    return;
                }

                // Tiếp tục xử lý logic lưu dữ liệu
                int idBacSi = qlLichHen.getBacSiIdFromName(hoTenBacSi);
                int idBenhNhan = qlLichHen.getBenhNhanIdFromName(hoTenBenhNhan);
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhongKham);

                if (idBacSi == -1) {
                    showPersistentFieldError(comboBacSi, "Tên bác sĩ không hợp lệ");
                    return;
                }
                if (idBenhNhan == -1) {
                    showPersistentFieldError(comboBenhNhan, "Tên bệnh nhân không hợp lệ");
                    return;
                }
                if (idPhongKham == -1) {
                    showPersistentFieldError(comboPhongKham, "Tên phòng khám không hợp lệ");
                    return;
                }
                
                java.sql.Date ngayHen = new java.sql.Date(ngayHenDate.getTime());
                String timeString = String.format("%02d:%s", selectedHour, selectedMinute);
                LocalTime gioHen = LocalTime.parse(timeString);
                java.sql.Time timeGioHen = java.sql.Time.valueOf(gioHen);
                
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
                    showPersistentFieldError(timePanel, validationError);
                    return;
                }
                
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
                
                // Cập nhật đối tượng lịch hẹn
                lichHen.setIdBacSi(idBacSi);
                lichHen.setHoTenBacSi(hoTenBacSi);
                lichHen.setIdBenhNhan(idBenhNhan);
                lichHen.setHoTenBenhNhan(hoTenBenhNhan);
                lichHen.setNgayHen(ngayHen);
                lichHen.setIdPhongKham(idPhongKham);
                lichHen.setTenPhong(tenPhongKham);
                lichHen.setGioHen(timeGioHen);
                lichHen.setTrangThai(selectedStatus);
                lichHen.setMoTa(moTa);
                
                // Thực hiện cập nhật và kiểm tra kết quả
                boolean success = qlLichHen.updateLichHen(lichHen);
                if (success) {
                    // Clear tất cả errors trước khi đóng
                    clearAllFieldErrors();
                    dialog.dispose();
                    updateCalendar();
                    showSuccessToast("Cập nhật lịch hẹn thành công!");
                } else {
                    showPersistentFieldError(saveButton, "Không thể cập nhật lịch hẹn. Vui lòng thử lại");
                }
            } catch (DateTimeParseException ex) {
                showPersistentFieldError(timePanel, "Lỗi định dạng thời gian");
            } catch (Exception ex) {
                showPersistentFieldError(saveButton, "Lỗi nhập liệu! Vui lòng thử lại");
                ex.printStackTrace();
            }
        });
        
        dialog.setVisible(true);
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
    private void setBorderError(JComponent field, boolean isError) {
        Color borderColor = isError ? errorBorderColor : normalBorderColor;
        int borderWidth = isError ? 2 : 1;
        
        if (field instanceof JComboBox) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderWidth),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderWidth),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        } else if (field instanceof JPanel && field.getName() != null && field.getName().equals("timePanel")) {
            // Xử lý riêng cho timePanel - áp dụng border cho các spinner bên trong
            Component[] components = field.getComponents();
            for (Component comp : components) {
                if (comp instanceof JSpinner) {
                    JSpinner spinner = (JSpinner) comp;
                    spinner.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, borderWidth),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)
                    ));
                }
            }
            // Không set border cho timePanel để tránh conflict với layout
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderWidth),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
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
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("Arial", Font.PLAIN, 12));
        menuItem.setBackground(Color.WHITE);
        menuItem.setPreferredSize(new Dimension(120, 28));
        return menuItem;
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
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius, boolean reducedPadding) {
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
        // Sử dụng padding khác nhau tùy theo button
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Padding nhỏ hơn cho "Chỉnh Sửa"
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding bình thường cho "Đóng"
        }

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
    public void showSuccessToast(String message) {
        JDialog toastDialog = new JDialog();
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);        
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(successColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));               
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
}