package view;

import controller.ThongKeDoanhThuController;
import util.ExportManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class ThongKeDoanhThuPanel extends JPanel implements ExportManager.MessageCallback {

    // Controller
    private ThongKeDoanhThuController controller;
    
    // Date formatters
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Main panels
    private JPanel pnlOptions;
    private JPanel pnlChart;
    private JPanel pnlTable;
    
    // Option components
    private JComboBox<String> cboLoaiThongKe;
    private JPanel pnlNgay;
    private JPanel pnlTuan;
    private JPanel pnlThang;
    private JPanel pnlNam;
    
    // Panel Ngày
    private JLabel lblTuNgay;
    private JLabel lblDenNgay;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JButton btnThongKeNgay;
    
    // Panel Tuần
    private JComboBox<String> cboTuan;
    private JComboBox<Integer> cboNamTuan;
    private JButton btnThongKeTuan;
    
    // Panel Tháng
    private JComboBox<String> cboThang;
    private JComboBox<Integer> cboNamThang;
    private JButton btnThongKeThang;
    
    // Panel Năm
    private JComboBox<Integer> cboNam;
    private JButton btnThongKeNam;
    
    // Table
    private JTable tblThongKe;
    private DefaultTableModel tableModel;
    
    // Chart container
    private JPanel pnlBieuDoContainer;
    
    // Export button
    private JButton btnExport;
    private ExportManager exportManager;
    // Theme colors
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color secondaryColor = new Color(52, 152, 219);
    private final Color buttonTextColor = Color.WHITE;
    
    public ThongKeDoanhThuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        initComponents();
        initLayouts();
        initEvents();
        
        // Create controller
        controller = new ThongKeDoanhThuController(this);
        
        // Initialize ExportManager
        exportManager = new ExportManager(this, tableModel, this);
        
        // Default setting
        cboLoaiThongKe.setSelectedIndex(0);
        showOptionPanel(0);
    }
    
    private void initComponents() {
        // Option panel
        pnlOptions = new JPanel();
        pnlOptions.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tùy chọn thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));
        
        String[] loaiThongKe = {"Theo ngày", "Theo tuần", "Theo tháng", "Theo năm"};
        cboLoaiThongKe = new JComboBox<>(loaiThongKe);
        
        // Ngày panel
        pnlNgay = new JPanel();
        lblTuNgay = new JLabel("Từ ngày:");
        lblDenNgay = new JLabel("Đến ngày:");
        txtTuNgay = new JTextField(10);
        txtDenNgay = new JTextField(10);
        btnThongKeNgay = new JButton("Thống kê");
        
        // Tuần panel
        pnlTuan = new JPanel();
        cboTuan = new JComboBox<>();
        // Populate weeks
        for (int i = 1; i <= 53; i++) {
            cboTuan.addItem("Tuần " + i);
        }
        
        cboNamTuan = new JComboBox<>();
        populateYears(cboNamTuan);
        btnThongKeTuan = new JButton("Thống kê");
        
        // Tháng panel
        pnlThang = new JPanel();
        cboThang = new JComboBox<>();
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                         "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        for (String month : months) {
            cboThang.addItem(month);
        }
        
        cboNamThang = new JComboBox<>();
        populateYears(cboNamThang);
        btnThongKeThang = new JButton("Thống kê");
        
        // Năm panel
        pnlNam = new JPanel();
        cboNam = new JComboBox<>();
        populateYears(cboNam);
        btnThongKeNam = new JButton("Thống kê");
        
        // Chart panel
        pnlChart = new JPanel();
        pnlChart.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Biểu đồ thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));
        
        pnlBieuDoContainer = new JPanel();
        pnlBieuDoContainer.setPreferredSize(new Dimension(600, 350));
        pnlBieuDoContainer.setLayout(new BorderLayout());
        
        JLabel lblNoData = new JLabel("Chọn loại thống kê và nhấn Thống kê để xem biểu đồ", JLabel.CENTER);
        lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
        pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
        
        // Table panel
        pnlTable = new JPanel();
        pnlTable.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dữ liệu thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));

        String[] columnNames = {"Thời gian", "Doanh thu"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblThongKe = new JTable(tableModel);
        tblThongKe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblThongKe.setRowHeight(25);
        tblThongKe.getTableHeader().setReorderingAllowed(false);

        
        // Export button
        btnExport = new JButton("Xuất dữ liệu");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setBackground(primaryColor);
        btnExport.setForeground(buttonTextColor);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        btnExport.setOpaque(true);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void populateYears(JComboBox<Integer> comboBox) {
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            comboBox.addItem(i);
        }
        comboBox.setSelectedItem(currentYear);
    }
    
    private void initLayouts() {
        // Options panel layout
        pnlOptions.setLayout(new BorderLayout());
        JPanel pnlLoaiThongKe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLoaiThongKe.add(new JLabel("Loại thống kê:"));
        pnlLoaiThongKe.add(cboLoaiThongKe);
        pnlOptions.add(pnlLoaiThongKe, BorderLayout.NORTH);
        
        // Configure sub panels
        configureNgayPanel();
        configureTuanPanel();
        configureThangPanel();
        configureNamPanel();
        
        // Chart panel layout
        pnlChart.setLayout(new BorderLayout());
        pnlChart.add(pnlBieuDoContainer, BorderLayout.CENTER);
        
        // Table panel layout
        pnlTable.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        JPanel pnlTableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTableActions.add(btnExport);
        
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        pnlTable.add(pnlTableActions, BorderLayout.SOUTH);
        
        // Main panel layout
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(pnlChart, BorderLayout.CENTER);
        centerPanel.add(pnlTable, BorderLayout.SOUTH);
        
        add(pnlOptions, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void configureNgayPanel() {
        pnlNgay.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Set today's date as default
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusDays(7);
        
        txtTuNgay.setText(dateFormat.format(Date.valueOf(oneWeekAgo)));
        txtDenNgay.setText(dateFormat.format(Date.valueOf(today)));
        
        pnlNgay.add(lblTuNgay);
        pnlNgay.add(txtTuNgay);
        pnlNgay.add(lblDenNgay);
        pnlNgay.add(txtDenNgay);
        pnlNgay.add(btnThongKeNgay);
    }
    
    private void configureTuanPanel() {
        pnlTuan.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Get current week
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        cboTuan.setSelectedIndex(currentWeek - 1);
        
        pnlTuan.add(new JLabel("Tuần:"));
        pnlTuan.add(cboTuan);
        pnlTuan.add(new JLabel("Năm:"));
        pnlTuan.add(cboNamTuan);
        pnlTuan.add(btnThongKeTuan);
    }
    
    private void configureThangPanel() {
        pnlThang.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Get current month
        int currentMonth = LocalDate.now().getMonthValue();
        cboThang.setSelectedIndex(currentMonth - 1);
        
        pnlThang.add(new JLabel("Tháng:"));
        pnlThang.add(cboThang);
        pnlThang.add(new JLabel("Năm:"));
        pnlThang.add(cboNamThang);
        pnlThang.add(btnThongKeThang);
    }
    
    private void configureNamPanel() {
        pnlNam.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        pnlNam.add(new JLabel("Năm:"));
        pnlNam.add(cboNam);
        pnlNam.add(btnThongKeNam);
    }
    
    private void initEvents() {
        cboLoaiThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = cboLoaiThongKe.getSelectedIndex();
                showOptionPanel(selectedIndex);
            }
        });
        
        btnThongKeNgay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoNgay();
            }
        });
        
        btnThongKeTuan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoTuan();
            }
        });
        
        btnThongKeThang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoThang();
            }
        });
        
        btnThongKeNam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoNam();
            }
        });
        
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() > 0) {
                    exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
                } else {
                    showErrorMessage("Không có dữ liệu", "Không có dữ liệu thống kê để xuất.");
                }
            }
        });
        
        // Add hover effect for export button
        btnExport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExport.setBackground(darkenColor(primaryColor));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExport.setBackground(primaryColor);
            }
        });
    }
    
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    
    private void showOptionPanel(int index) {
        // Remove all option panels
        if (pnlOptions.getComponentCount() > 1) {
            pnlOptions.remove(1);
        }
        
        // Show selected panel
        switch (index) {
            case 0: // Ngày
                pnlOptions.add(pnlNgay, BorderLayout.CENTER);
                break;
            case 1: // Tuần
                pnlOptions.add(pnlTuan, BorderLayout.CENTER);
                break;
            case 2: // Tháng
                pnlOptions.add(pnlThang, BorderLayout.CENTER);
                break;
            case 3: // Năm
                pnlOptions.add(pnlNam, BorderLayout.CENTER);
                break;
        }
        
        pnlOptions.revalidate();
        pnlOptions.repaint();
    }
    
    private void thongKeTheoNgay() {
        try {
            java.util.Date tuNgayUtil = dateFormat.parse(txtTuNgay.getText());
            java.util.Date denNgayUtil = dateFormat.parse(txtDenNgay.getText());
            
            Date tuNgay = new Date(tuNgayUtil.getTime());
            Date denNgay = new Date(denNgayUtil.getTime());
            
            if (tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            controller.thongKeDoanhThuTheoNgay(tuNgay, denNgay);
            
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập theo định dạng dd/MM/yyyy", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void thongKeTheoTuan() {
        int selectedWeek = cboTuan.getSelectedIndex() + 1;
        int selectedYear = (int) cboNamTuan.getSelectedItem();
        
        // Calculate start and end date of the selected week
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.WEEK_OF_YEAR, selectedWeek);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        
        Date tuNgay = new Date(calendar.getTimeInMillis());
        
        calendar.add(Calendar.DAY_OF_WEEK, 6); // Sunday
        Date denNgay = new Date(calendar.getTimeInMillis());
        
        controller.thongKeDoanhThuTheoTuanTong(tuNgay, denNgay);
    }
    
    private void thongKeTheoThang() {
        int selectedMonth = cboThang.getSelectedIndex() + 1;
        int selectedYear = (int) cboNamThang.getSelectedItem();
        
        controller.thongKeDoanhThuTheoThang(selectedYear, selectedMonth);
    }
    
    private void thongKeTheoNam() {
        int selectedYear = (int) cboNam.getSelectedItem();
        
        controller.thongKeDoanhThuTheoNam(selectedYear);
    }
    
    // Method called by controller to display results
    public void hienThiThongKe(List<Object[]> dataThongKe) {
        // Clear table
        tableModel.setRowCount(0);
        
        // Add data to table
        for (Object[] row : dataThongKe) {
            tableModel.addRow(row);
        }
        
        // Update chart
        updateChart(dataThongKe);
    }
    
    // Method to display detailed results (for methods that support it)
    public void hienThiThongKeChiTiet(List<Object[]> dataThongKe) {
        // Similar to hienThiThongKe, but could display more detailed information
        hienThiThongKe(dataThongKe);
    }
    
    // Update chart with data
    private void updateChart(List<Object[]> dataThongKe) {
        if (dataThongKe == null || dataThongKe.isEmpty()) {
            JLabel lblNoData = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
            pnlBieuDoContainer.removeAll();
            pnlBieuDoContainer.setLayout(new BorderLayout());
            pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
            pnlBieuDoContainer.revalidate();
            pnlBieuDoContainer.repaint();
            return;
        }
        
        // Convert data to a format suitable for charts
        Map<String, Integer> chartData = new LinkedHashMap<>();
        
        for (Object[] row : dataThongKe) {
            String label = row[0].toString();
            double value = 0;
            
            if (row[1] instanceof Number) {
                value = ((Number) row[1]).doubleValue();
            } else if (row[1] instanceof String) {
                try {
                    value = Double.parseDouble(row[1].toString().replaceAll("[^\\d.]", ""));
                } catch (NumberFormatException e) {
                    value = 0;
                }
            }
            
            chartData.put(label, (int) value);
        }
        
        // Determine which type of chart to show based on the current selection
        int selectedIndex = cboLoaiThongKe.getSelectedIndex();
        String chartTitle = "Biểu đồ doanh thu " + cboLoaiThongKe.getSelectedItem().toString().toLowerCase();
        
        hienThiBieuDoCot(chartData, chartTitle);
    }
    
    private int calculateScale(int maxValue) {
        int scale = 1;
        
        if (maxValue <= 10) {
            scale = 1;
        } else if (maxValue <= 50) {
            scale = 5;
        } else if (maxValue <= 100) {
            scale = 10;
        } else if (maxValue <= 500) {
            scale = 50;
        } else if (maxValue <= 1000) {
            scale = 100;
        } else if (maxValue <= 5000) {
            scale = 500;
        } else if (maxValue <= 10000) {
            scale = 1000;
        } else if (maxValue <= 50000) {
            scale = 5000;
        } else if (maxValue <= 100000) {
            scale = 10000;
        } else if (maxValue <= 500000) {
            scale = 50000;
        } else if (maxValue <= 1000000) {
            scale = 100000;
        } else if (maxValue <= 5000000) {
            scale = 500000;
        } else {
            scale = 1000000;
        }
        
        return scale;
    }
    
    private void drawMultilineLabel(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        
        if (fm.stringWidth(text) <= maxWidth) {
            g2d.drawString(text, x - fm.stringWidth(text) / 2, y);
            return;
        }
        
        // Tạo dòng ngắn hơn nếu text quá dài
        String[] words = text.split(" ");
        if (words.length == 1) {
            // Nếu chỉ có một từ, hiển thị dạng rút gọn
            if (text.length() > 10) {
                text = text.substring(0, 7) + "...";
            }
            g2d.drawString(text, x - fm.stringWidth(text) / 2, y);
            return;
        }
        
        // Chia thành nhiều dòng
        StringBuilder currentLine = new StringBuilder();
        int lineCount = 0;
        
        for (String word : words) {
            String testLine = currentLine.toString() + word + " ";
            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                if (lineCount == 0) {
                    g2d.drawString(currentLine.toString().trim(), x - fm.stringWidth(currentLine.toString().trim()) / 2, y);
                    lineCount++;
                    currentLine = new StringBuilder(word).append(" ");
                } else {
                    // Chỉ hiển thị tối đa 2 dòng
                    String shortLine = currentLine.toString().trim();
                    if (shortLine.length() > 7) {
                        shortLine = shortLine.substring(0, 7) + "...";
                    }
                    g2d.drawString(shortLine, x - fm.stringWidth(shortLine) / 2, y + fm.getHeight());
                    break;
                }
            }
        }
        
        if (lineCount == 0) {
            g2d.drawString(currentLine.toString().trim(), x - fm.stringWidth(currentLine.toString().trim()) / 2, y);
        } else if (lineCount == 1 && currentLine.length() > 0) {
            String lastLine = currentLine.toString().trim();
            if (lastLine.length() > 7) {
                lastLine = lastLine.substring(0, 7) + "...";
            }
            g2d.drawString(lastLine, x - fm.stringWidth(lastLine) / 2, y + fm.getHeight());
        }
    }
    
    private void hienThiBieuDoCot(Map<String, Integer> data, String title) {
        pnlBieuDoContainer.removeAll();
        
        if (data == null || data.isEmpty()) {
            JLabel lblNoData = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
            pnlBieuDoContainer.setLayout(new BorderLayout());
            pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
            pnlBieuDoContainer.revalidate();
            pnlBieuDoContainer.repaint();
            return;
        }
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                int marginTop = 40;
                int marginBottom = 70; // Tăng marginBottom để có thêm không gian cho nhãn
                int marginLeft = 80;   // Tăng marginLeft để có thêm không gian cho các số lớn
                int marginRight = 20;
                
                int chartWidth = width - marginLeft - marginRight;
                int chartHeight = height - marginTop - marginBottom;
                
                int numBars = data.size();
                int barWidth = Math.min(60, (chartWidth) / (numBars * 2));
                int spacing = barWidth / 2;
                
                // Tìm giá trị lớn nhất trong dữ liệu
                int maxValue = 1;
                for (Integer value : data.values()) {
                    maxValue = Math.max(maxValue, value);
                }
                
                int yAxisScale = calculateScale(maxValue);
                int numYDivisions = maxValue / yAxisScale + (maxValue % yAxisScale > 0 ? 1 : 0);
                
                // Vẽ trục tung
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, marginTop, marginLeft, height - marginBottom);
                
                // Vẽ các mức chia trục tung
                for (int i = 0; i <= numYDivisions; i++) {
                    int y = height - marginBottom - (i * yAxisScale * chartHeight / (numYDivisions * yAxisScale));
                    if (y >= marginTop) {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.drawLine(marginLeft, y, width - marginRight, y);
                        
                        g2d.setColor(Color.BLACK);
                        String yLabel = formatNumber(i * yAxisScale);
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelWidth = fm.stringWidth(yLabel);
                        g2d.drawString(yLabel, marginLeft - labelWidth - 5, y + fm.getAscent() / 2);
                    }
                }
                
                // Vẽ trục hoành
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom);
                
                int x = marginLeft + spacing;
                int i = 0;
                
                // Vẽ các cột
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int value = entry.getValue();
                    
                    double ratio = (double) chartHeight / (numYDivisions * yAxisScale);
                    int barHeight = (int) (value * ratio);
                    
                    g2d.setColor(new Color(41, 128, 185));
                    int barX = x + i * (barWidth + spacing);
                    int barY = height - marginBottom - barHeight;
                    
                    g2d.fillRect(barX, barY, barWidth, barHeight);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(barX, barY, barWidth, barHeight);
                    
                    // Vẽ giá trị trên cột
                    String valueText = formatNumber(value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    int textX = barX + (barWidth - textWidth) / 2;
                    int textY = barY - 5;
                    
                    if (textY < marginTop) {
                        textY = barY + 15;
                        g2d.setColor(Color.WHITE);
                    } else {
                        g2d.setColor(Color.BLACK);
                    }
                    
                    g2d.drawString(valueText, textX, textY);
                    
                    // Vẽ nhãn dưới cột
                    String label = entry.getKey();
                    drawMultilineLabel(g2d, label, barX + barWidth / 2, height - marginBottom + 15, barWidth + spacing);
                    
                    i++;
                }
                
                // Vẽ tiêu đề
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
            }
        };
        
        chart.setPreferredSize(new Dimension(600, 350));
        pnlBieuDoContainer.setLayout(new BorderLayout());
        pnlBieuDoContainer.add(chart, BorderLayout.CENTER);
        
        pnlBieuDoContainer.revalidate();
        pnlBieuDoContainer.repaint();
    }
    
    private void hienThiBieuDoTop10(List<Object[]> data, String title, int labelIndex, int valueIndex) {
        pnlBieuDoContainer.removeAll();
        
        if (data == null || data.isEmpty()) {
            JLabel lblNoData = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
            pnlBieuDoContainer.setLayout(new BorderLayout());
            pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
            pnlBieuDoContainer.revalidate();
            pnlBieuDoContainer.repaint();
            return;
        }
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                int marginTop = 40;
                int marginBottom = 70; // Tăng marginBottom để có thêm không gian cho nhãn
                int marginLeft = 80;   // Tăng marginLeft để có thêm không gian cho các số lớn
                int marginRight = 20;
                
                int chartWidth = width - marginLeft - marginRight;
                int chartHeight = height - marginTop - marginBottom;
                
                // Giới hạn số lượng item hiển thị là 10
                int numItems = Math.min(10, data.size());
                int barWidth = Math.min(60, (chartWidth) / (numItems * 2));
                int spacing = barWidth / 2;
                
                // Tìm giá trị lớn nhất trong dữ liệu
                int maxValue = 1;
                for (int i = 0; i < numItems; i++) {
                    Object[] item = data.get(i);
                    int value = 0;
                    
                    if (item[valueIndex] instanceof Number) {
                        value = ((Number) item[valueIndex]).intValue();
                    } else if (item[valueIndex] instanceof String) {
                        try {
                            value = (int) Double.parseDouble(item[valueIndex].toString().replaceAll("[^\\d.]", ""));
                        } catch (NumberFormatException e) {
                            value = 0;
                        }
                    }
                    
                    maxValue = Math.max(maxValue, value);
                }
                
                int yAxisScale = calculateScale(maxValue);
                int numYDivisions = maxValue / yAxisScale + (maxValue % yAxisScale > 0 ? 1 : 0);
                
                // Vẽ trục tung
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, marginTop, marginLeft, height - marginBottom);
                
                // Vẽ các mức chia trục tung
                for (int i = 0; i <= numYDivisions; i++) {
                    int y = height - marginBottom - (i * yAxisScale * chartHeight / (numYDivisions * yAxisScale));
                    if (y >= marginTop) {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.drawLine(marginLeft, y, width - marginRight, y);
                        
                        g2d.setColor(Color.BLACK);
                        String yLabel = formatNumber(i * yAxisScale);
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelWidth = fm.stringWidth(yLabel);
                        g2d.drawString(yLabel, marginLeft - labelWidth - 5, y + fm.getAscent() / 2);
                    }
                }
                
                // Vẽ trục hoành
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom);
                
                int x = marginLeft + spacing;
                
                // Vẽ các cột
                for (int i = 0; i < numItems; i++) {
                    Object[] item = data.get(i);
                    String label = item[labelIndex].toString();
                    int value = 0;
                    
                    if (item[valueIndex] instanceof Number) {
                        value = ((Number) item[valueIndex]).intValue();
                    } else if (item[valueIndex] instanceof String) {
                        try {
                            value = (int) Double.parseDouble(item[valueIndex].toString().replaceAll("[^\\d.]", ""));
                        } catch (NumberFormatException e) {
                            value = 0;
                        }
                    }
                    
                    double ratio = (double) chartHeight / (numYDivisions * yAxisScale);
                    int barHeight = (int) (value * ratio);
                    
                    g2d.setColor(new Color(41, 128, 185));
                    int barX = x + i * (barWidth + spacing);
                    int barY = height - marginBottom - barHeight;
                    
                    g2d.fillRect(barX, barY, barWidth, barHeight);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(barX, barY, barWidth, barHeight);
                    
                    // Vẽ giá trị trên cột
                    String valueText = formatNumber(value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    int textX = barX + (barWidth - textWidth) / 2;
                    int textY = barY - 5;
                    
                    if (textY < marginTop) {
                        textY = barY + 15;
                        g2d.setColor(Color.WHITE);
                    } else {
                        g2d.setColor(Color.BLACK);
                    }
                    
                    g2d.drawString(valueText, textX, textY);
                    
                    // Vẽ nhãn dưới cột
                    drawMultilineLabel(g2d, label, barX + barWidth / 2, height - marginBottom + 15, barWidth + spacing);
                }
                
                // Vẽ tiêu đề
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
            }
        };
        
        chart.setPreferredSize(new Dimension(600, 350));
        pnlBieuDoContainer.setLayout(new BorderLayout());
        pnlBieuDoContainer.add(chart, BorderLayout.CENTER);
        
        pnlBieuDoContainer.revalidate();
        pnlBieuDoContainer.repaint();
    }
    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
	@Override
	public void showSuccessToast(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMessage(String title, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessage(String message, String title, int messageType) {
		// TODO Auto-generated method stub
		
	}
}