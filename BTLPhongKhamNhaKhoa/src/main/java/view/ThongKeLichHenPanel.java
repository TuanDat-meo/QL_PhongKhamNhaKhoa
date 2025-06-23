package view;

import controller.ThongKeLichHenController;
import model.LichHen;
import util.ExportManager;
import view.BenhNhanUI.NotificationType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import com.toedter.calendar.JDateChooser;

public class ThongKeLichHenPanel extends JPanel implements ExportManager.MessageCallback {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final int PADDING = 20;
    
    private JPanel headerPanel;
    private JPanel filterPanel;
    private JPanel contentPanel;
    private JPanel tablePanel;
    private JPanel footerPanel;
    private JComboBox<String> timePeriodComboBox;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JButton applyButton;
    private JButton exportButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JLabel totalAppointmentsLabel;
    private JLabel completedAppointmentsLabel;
    private JLabel cancelledAppointmentsLabel;
    
    private ThongKeLichHenController controller;
    private ExportManager exportManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public ThongKeLichHenPanel() {
        controller = new ThongKeLichHenController();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Giảm lề để tiết kiệm không gian
        
        initComponents();
        exportManager = new ExportManager(this, tableModel, this);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDateChooser.setDate(cal.getTime());
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDateChooser.setDate(cal.getTime());
        
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Giảm lề để tiết kiệm không gian
        
        createFilterPanel();
        createContentPanel();
        createFooterPanel();
        
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDateChooser.setDate(cal.getTime());
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDateChooser.setDate(cal.getTime());
        
        loadData();
    }
    
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ LỊCH HẸN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
    }
    
    private void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagLayout layout = new GridBagLayout();
        filterPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel periodLabel = new JLabel("Thời gian:");
        periodLabel.setFont(NORMAL_FONT);
        
        String[] timePeriods = {"Tùy chỉnh", "Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay"};
        timePeriodComboBox = new JComboBox<>(timePeriods);
        timePeriodComboBox.setFont(NORMAL_FONT);
        timePeriodComboBox.setSelectedIndex(2);
        timePeriodComboBox.setPreferredSize(new Dimension(150, 30)); // Đặt chiều cao đồng bộ
        timePeriodComboBox.setMaximumSize(new Dimension(150, 30));
        
        JLabel fromLabel = new JLabel("Từ ngày:");
        fromLabel.setFont(NORMAL_FONT);
        
        startDateChooser = new JDateChooser();
        startDateChooser.setFont(NORMAL_FONT);
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        startDateChooser.setPreferredSize(new Dimension(120, 30)); // Đặt chiều cao đồng bộ
        startDateChooser.setMaximumSize(new Dimension(120, 30));
        
        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(NORMAL_FONT);
        
        endDateChooser = new JDateChooser();
        endDateChooser.setFont(NORMAL_FONT);
        endDateChooser.setDateFormatString("dd/MM/yyyy");
        endDateChooser.setPreferredSize(new Dimension(120, 30)); // Đặt chiều cao đồng bộ
        endDateChooser.setMaximumSize(new Dimension(120, 30));
        
        applyButton = createButton("Thống kê", PRIMARY_COLOR, BUTTON_TEXT_COLOR); // Đổi tên thành "Thống kê"
        exportButton = createButton("Xuất báo cáo", SECONDARY_COLOR, BUTTON_TEXT_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        filterPanel.add(periodLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        filterPanel.add(timePeriodComboBox, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.1;
        filterPanel.add(fromLabel, gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 0.2;
        filterPanel.add(startDateChooser, gbc);
        
        gbc.gridx = 4;
        gbc.weightx = 0.1;
        filterPanel.add(toLabel, gbc);
        
        gbc.gridx = 5;
        gbc.weightx = 0.2;
        filterPanel.add(endDateChooser, gbc);
        
        gbc.gridx = 6;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(0, 20, 0, 5);
        filterPanel.add(applyButton, gbc);
        
        gbc.gridx = 7;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(0, 5, 0, 10);
        filterPanel.add(exportButton, gbc);
        
        timePeriodComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateRange();
            }
        });
        
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportManager.showExportOptions(PRIMARY_COLOR, SECONDARY_COLOR, BUTTON_TEXT_COLOR);
            }
        });
    }
    
    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout(0, PADDING));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel summaryPanel = createSummaryPanel();
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        contentPanel.add(filterPanel, BorderLayout.SOUTH); // Đặt filterPanel dưới bảng dữ liệu
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        summaryPanel.setBackground(BACKGROUND_COLOR);
        summaryPanel.setBorder(new EmptyBorder(0, 0, PADDING, 0));
        
        JPanel totalCard = createSummaryCard("Tổng lịch hẹn", "0", new Color(41, 128, 185));
        JPanel completedCard = createSummaryCard("Hoàn thành", "0", new Color(46, 204, 113));
        JPanel cancelledCard = createSummaryCard("Đã hủy", "0", new Color(231, 76, 60));
        
        summaryPanel.add(totalCard);
        summaryPanel.add(Box.createHorizontalStrut(PADDING));
        summaryPanel.add(completedCard);
        summaryPanel.add(Box.createHorizontalStrut(PADDING));
        summaryPanel.add(cancelledCard);
        
        return summaryPanel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1), // Viền đen
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        if (title.equals("Tổng lịch hẹn")) {
            totalAppointmentsLabel = valueLabel;
        } else if (title.equals("Hoàn thành")) {
            completedAppointmentsLabel = valueLabel;
        } else if (title.equals("Đã hủy")) {
            cancelledAppointmentsLabel = valueLabel;
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        card.setPreferredSize(new Dimension(200, 100));
        card.setMaximumSize(new Dimension(300, 100));
        
        return card;
    }
    
    private void createTablePanel() {
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        String[] columnNames = {"ID", "Bệnh nhân", "Bác sĩ", "Ngày hẹn", "Giờ hẹn", "Phòng khám", "Trạng thái", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dataTable = new JTable(tableModel);
        setupTable(dataTable);
        
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setPreferredSize(new Dimension(0, 500)); // Chiều cao bảng 500px
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.getTableHeader().setFont(NORMAL_FONT);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Căn giữa tất cả các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        
        // Tùy chỉnh tiêu đề bảng
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR); // Màu nền giống nút Thống kê
        header.setForeground(Color.WHITE); // Chữ trắng
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font đồng bộ
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);
    }
    
    private void createFooterPanel() {
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(PADDING, 0, 0, 0));
    }
    
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(bgColor));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    
    private void updateDateRange() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        
        switch (timePeriodComboBox.getSelectedIndex()) {
            case 0:
                break;
            case 1:
                startDateChooser.setDate(currentDate);
                endDateChooser.setDate(currentDate);
                break;
            case 2:
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                startDateChooser.setDate(cal.getTime());
                
                cal.add(Calendar.DAY_OF_WEEK, 6);
                endDateChooser.setDate(cal.getTime());
                break;
            case 3:
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDateChooser.setDate(cal.getTime());
                
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDateChooser.setDate(cal.getTime());
                break;
            case 4:
                int currentMonth = cal.get(Calendar.MONTH);
                int currentQuarter = currentMonth / 3;
                
                cal.set(Calendar.MONTH, currentQuarter * 3);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDateChooser.setDate(cal.getTime());
                
                cal.set(Calendar.MONTH, currentQuarter * 3 + 2);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDateChooser.setDate(cal.getTime());
                break;
            case 5:
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDateChooser.setDate(cal.getTime());
                
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                endDateChooser.setDate(cal.getTime());
                break;
        }
    }
    
    private void loadData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        if (startDate == null || endDate == null) {
            showNotification("Vui lòng chọn khoảng thời gian", NotificationType.WARNING);
            return;
        }
        
        if (startDate.after(endDate)) {
            showNotification("Ngày bắt đầu phải trước ngày kết thúc", NotificationType.ERROR);
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            loadAppointmentStatistics(startDate, endDate);
            loadAppointmentTable(startDate, endDate);
        } catch (Exception ex) {
            showNotification("Lỗi khi tải dữ liệu: " + ex.getMessage(), NotificationType.ERROR);
            ex.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void loadAppointmentStatistics(Date startDate, Date endDate) {
        Map<String, Integer> appointmentsByStatus = controller.thongKeLichHenTheoTrangThai(startDate, endDate);
        
        int totalAppointments = 0;
        for (Integer count : appointmentsByStatus.values()) {
            totalAppointments += count;
        }
        
        int completedAppointments = appointmentsByStatus.getOrDefault("Hoàn thành", 0);
        int cancelledAppointments = appointmentsByStatus.getOrDefault("Đã hủy", 0);
        
        totalAppointmentsLabel.setText(String.valueOf(totalAppointments));
        completedAppointmentsLabel.setText(String.valueOf(completedAppointments));
        cancelledAppointmentsLabel.setText(String.valueOf(cancelledAppointments));
    }
    
    private void loadAppointmentTable(Date startDate, Date endDate) {
        tableModel.setRowCount(0);
        
        java.util.List<LichHen> appointments = controller.layDanhSachLichHen(startDate, endDate);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        for (LichHen appointment : appointments) {
            Object[] rowData = {
                appointment.getIdLichHen(),
                appointment.getHoTenBenhNhan(),
                appointment.getHoTenBacSi(),
                dateFormat.format(appointment.getNgayHen()),
                timeFormat.format(appointment.getGioHen()),
                appointment.getTenPhong(),
                appointment.getTrangThai(),
                appointment.getMoTa()
            };
            tableModel.addRow(rowData);
        }
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
    
    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        showNotification(message, NotificationType.ERROR);
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        NotificationType type;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                type = NotificationType.ERROR;
                break;
            case JOptionPane.WARNING_MESSAGE:
                type = NotificationType.WARNING;
                break;
            default:
                type = NotificationType.SUCCESS;
                break;
        }
        showNotification(message, type);
    }
}