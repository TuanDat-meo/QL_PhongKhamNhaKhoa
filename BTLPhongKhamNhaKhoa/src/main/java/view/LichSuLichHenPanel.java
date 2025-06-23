package view;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.sql.Date;
import java.sql.SQLException;
import controller.LichHenController;
import controller.BenhNhanController;
import controller.NguoiDungController;
import model.LichHen;
import model.NguoiDung;
import model.BenhNhan;

public class LichSuLichHenPanel extends JPanel {
    private LichHenController controller;
    private BenhNhanController benhNhanController;
    private NguoiDungController nguoiDungController;
    private NguoiDung currentUser;
    private int currentUserId;
    private JTabbedPane tabbedPane;
    private JPanel lichSuPanel;
    private JPanel sapToiPanel;
    private JTable lichSuTable;
    private JTable sapToiTable;
    private DefaultTableModel lichSuTableModel;
    private DefaultTableModel sapToiTableModel;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    
    private Color warningColor = new Color(237, 187, 85);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color buttonTextColor = Color.WHITE;
    private Color accentColor = new Color(192, 80, 77);
    private Color borderColor = new Color(222, 226, 230);
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(245, 248, 250);
    private Color successColor = new Color(86, 156, 104);
    private Color headerTextColor = Color.WHITE;
    private Color nearestAppointmentColor = new Color(255, 248, 220); // Màu highlight cho lịch hẹn gần nhất
    private Color nearestAppointmentBorderColor = new Color(255, 193, 7); // Màu viền cho lịch hẹn gần nhất
    
    private Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    
    private JPanel errorPanel;
    private JLabel errorLabel;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public LichSuLichHenPanel(NguoiDung user) {
        this.currentUser = user;
        this.currentUserId = user != null ? user.getIdNguoiDung() : -1;
        controller = new LichHenController();
        benhNhanController = new BenhNhanController();
        nguoiDungController = new NguoiDungController();
        
        setupUI();
        loadData();
    }

    public LichSuLichHenPanel(int userId) {
        this.currentUserId = userId;
        controller = new LichHenController();
        benhNhanController = new BenhNhanController();
        nguoiDungController = new NguoiDungController();
        
        setCurrentUserId(userId);
        setupUI();
        loadData();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        try {
            NguoiDungController userController = new NguoiDungController();
            this.currentUser = userController.getNguoiDungById(userId);
            if (this.currentUser != null) {
                updateUIForCurrentUser();
                loadData();
            } else {
                showErrorMessage("Không tìm thấy thông tin người dùng!");
            }
        } catch (SQLException e) {
            showErrorMessage("Không thể tải thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUIForCurrentUser() {
        if (currentUser != null) {
            Component[] components = ((JPanel) getComponent(0)).getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Quản lý lịch hẹn")) {
                    ((JLabel) comp).setText("Quản lý lịch hẹn - " + currentUser.getHoTen());
                    break;
                }
            }
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        createErrorPanel();
        add(errorPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        String userName = (currentUser != null) ? currentUser.getHoTen() : "Người dùng";
        JLabel titleLabel = new JLabel("Quản lý lịch hẹn - " + userName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("Làm mới", primaryColor);
        refreshButton.addActionListener(e -> {
            loadData();
            showSuccessMessage("Đã cập nhật dữ liệu!");
        });
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(headerFont);
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setForeground(primaryColor);

        lichSuPanel = createLichSuPanel();
        sapToiPanel = createSapToiPanel();

        tabbedPane.addTab("Lịch hẹn sắp tới", sapToiPanel);
        tabbedPane.addTab("Lịch sử đặt lịch", lichSuPanel);

        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });
    }

    private JPanel createLichSuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columnNames = {"ID", "Bác sĩ", "Ngày hẹn", "Giờ hẹn", "Phòng khám", "Trạng thái", "Mô tả"};
        lichSuTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lichSuTable = new JTable(lichSuTableModel);
        setupTable(lichSuTable);

        JScrollPane scrollPane = new JScrollPane(lichSuTable);
        styleScrollPane(scrollPane);

        JPanel infoPanel = createInfoPanel("Hiển thị tất cả lịch hẹn đã qua hoặc đã hoàn thành");
        
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSapToiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columnNames = {"ID", "Bác sĩ", "Ngày hẹn", "Giờ hẹn", "Phòng khám", "Trạng thái", "Mô tả", "Thao tác"};
        sapToiTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        sapToiTable = new JTable(sapToiTableModel);
        setupTable(sapToiTable);
        
        sapToiTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
        sapToiTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(sapToiTable);
        styleScrollPane(scrollPane);

        JPanel infoPanel = createSapToiInfoPanel();
        
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel(String message) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(secondaryColor);
        infoPanel.setBorder(new CompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel infoLabel = new JLabel(message);
        infoLabel.setFont(regularFont);
        infoLabel.setForeground(primaryColor);
        infoPanel.add(infoLabel);

        return infoPanel;
    }

    private JPanel createSapToiInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(secondaryColor);
        infoPanel.setBorder(new CompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel infoLabel = new JLabel("Lịch hẹn sắp tới - Bạn có thể hủy lịch hẹn nếu cần thiết (Lịch hẹn gần nhất được đánh dấu màu vàng)");
        infoLabel.setFont(regularFont);
        infoLabel.setForeground(primaryColor);
        infoPanel.add(infoLabel, BorderLayout.WEST);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendPanel.setBackground(secondaryColor);
        
        JLabel legendLabel = new JLabel("Đã xác nhận | Chờ xác nhận | Đã hủy");
        legendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        legendLabel.setForeground(Color.GRAY);
        legendPanel.add(legendLabel);
        
        infoPanel.add(legendPanel, BorderLayout.EAST);

        return infoPanel;
    }

    private void setupTable(JTable table) {
        table.setFont(regularFont);
        table.setRowHeight(45);
        table.setSelectionBackground(primaryColor.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(borderColor);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(primaryColor);
        header.setForeground(headerTextColor);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Sử dụng renderer tùy chỉnh để highlight lịch hẹn gần nhất
        if (table == sapToiTable) {
            table.setDefaultRenderer(Object.class, new SapToiTableCellRenderer());
        } else {
            table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        }

        if (table.getColumnCount() >= 7) {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
            table.getColumnModel().getColumn(4).setPreferredWidth(120);
            table.getColumnModel().getColumn(5).setPreferredWidth(120);
            table.getColumnModel().getColumn(6).setPreferredWidth(200);
            
            if (table.getColumnCount() == 8) {
                table.getColumnModel().getColumn(7).setPreferredWidth(100);
            }
        }
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new LineBorder(borderColor, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
    }

    private void createErrorPanel() {
        errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.setBackground(backgroundColor);
        errorPanel.setVisible(false);

        errorLabel = new JLabel();
        errorLabel.setFont(regularFont);
        errorLabel.setForeground(accentColor);
        errorPanel.add(errorLabel);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(buttonTextColor);
        button.setBackground(backgroundColor);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void loadData() {
        if (currentUser == null) {
            showErrorMessage("Không có thông tin người dùng!");
            return;
        }

        try {
            List<LichHen> userLichHen = getUserAppointments();
            
            lichSuTableModel.setRowCount(0);
            sapToiTableModel.setRowCount(0);
            
            if (userLichHen.isEmpty()) {
                showErrorMessage("Không tìm thấy lịch hẹn nào cho tài khoản này.");
                return;
            }
            
            java.util.Date today = new java.util.Date();
            
            // Danh sách lịch hẹn sắp tới để sắp xếp
            List<LichHen> upcomingAppointments = new ArrayList<>();
            
            for (LichHen lichHen : userLichHen) {
                Object[] rowData = {
                    lichHen.getIdLichHen(),
                    lichHen.getHoTenBacSi() != null ? lichHen.getHoTenBacSi() : "",
                    lichHen.getNgayHen() != null ? dateFormat.format(lichHen.getNgayHen()) : "",
                    lichHen.getGioHen() != null ? timeFormat.format(lichHen.getGioHen()) : "",
                    lichHen.getTenPhong() != null ? lichHen.getTenPhong() : "",
                    lichHen.getTrangThai() != null ? lichHen.getTrangThai() : "",
                    lichHen.getMoTa() != null ? lichHen.getMoTa() : ""
                };
                
                if (lichHen.getNgayHen() != null && 
                    (lichHen.getNgayHen().before(today) || 
                    "Đã hoàn thành".equals(lichHen.getTrangThai()) || 
                    "Đã hủy".equals(lichHen.getTrangThai()))) {
                    lichSuTableModel.addRow(rowData);
                } else {
                    upcomingAppointments.add(lichHen);
                }
            }
            upcomingAppointments.sort((l1, l2) -> {
                java.util.Date dateTime1 = combineDateTime(l1.getNgayHen(), l1.getGioHen());
                java.util.Date dateTime2 = combineDateTime(l2.getNgayHen(), l2.getGioHen());
                
                if (dateTime1 == null && dateTime2 == null) return 0;
                if (dateTime1 == null) return 1;
                if (dateTime2 == null) return -1;
                int result = dateTime1.compareTo(dateTime2);
                if (result == 0) {
                    String status1 = l1.getTrangThai() != null ? l1.getTrangThai() : "";
                    String status2 = l2.getTrangThai() != null ? l2.getTrangThai() : "";
                    
                    if ("Đã xác nhận".equals(status1) && !"Đã xác nhận".equals(status2)) {
                        return -1;
                    } else if (!"Đã xác nhận".equals(status1) && "Đã xác nhận".equals(status2)) {
                        return 1;
                    }
                }
                
                return result;
            });
            
            // Thêm lịch hẹn sắp tới đã sắp xếp vào bảng
            for (LichHen lichHen : upcomingAppointments) {
                Object[] rowData = {
                    lichHen.getIdLichHen(),
                    lichHen.getHoTenBacSi() != null ? lichHen.getHoTenBacSi() : "",
                    lichHen.getNgayHen() != null ? dateFormat.format(lichHen.getNgayHen()) : "",
                    lichHen.getGioHen() != null ? timeFormat.format(lichHen.getGioHen()) : "",
                    lichHen.getTenPhong() != null ? lichHen.getTenPhong() : "",
                    lichHen.getTrangThai() != null ? lichHen.getTrangThai() : "",
                    lichHen.getMoTa() != null ? lichHen.getMoTa() : ""
                };
                
                Object[] sapToiRowData = Arrays.copyOf(rowData, rowData.length + 1);
                if ("Chờ xác nhận".equals(lichHen.getTrangThai()) || 
                    "Đã xác nhận".equals(lichHen.getTrangThai())) {
                    sapToiRowData[sapToiRowData.length - 1] = "Hủy lịch";
                } else {
                    sapToiRowData[sapToiRowData.length - 1] = "";
                }
                sapToiTableModel.addRow(sapToiRowData);
            }
            
            tabbedPane.setTitleAt(0, "Lịch hẹn sắp tới (" + sapToiTableModel.getRowCount() + ")");
            tabbedPane.setTitleAt(1, "Lịch sử đặt lịch (" + lichSuTableModel.getRowCount() + ")");
            
            // Làm mới bảng để cập nhật highlight
            sapToiTable.repaint();
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private List<LichHen> getUserAppointments() {
        List<LichHen> userLichHen = new ArrayList<>();
        
        if (currentUser == null) {
            return userLichHen;
        }
        
        try {
            List<BenhNhan> matchingPatients = findMatchingPatients();
            
            if (matchingPatients.isEmpty()) {
                return userLichHen;
            }          
            List<LichHen> allLichHen = controller.getAllLichHen();
            
            for (LichHen lichHen : allLichHen) {
                if (isAppointmentBelongsToUser(lichHen, matchingPatients)) {
                    userLichHen.add(lichHen);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return userLichHen;
    }
    private List<BenhNhan> findMatchingPatients() {
        List<BenhNhan> matchingPatients = new ArrayList<>();
        
        try {
            List<BenhNhan> allBenhNhan = benhNhanController.getAllBenhNhan();
            
            for (BenhNhan benhNhan : allBenhNhan) {
                if (isPatientMatchesUser(benhNhan, currentUser)) {
                    matchingPatients.add(benhNhan);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return matchingPatients;
    }
    private boolean isPatientMatchesUser(BenhNhan benhNhan, NguoiDung user) {
        // Kiểm tra số điện thoại (ưu tiên cao nhất)
        if (isPhoneNumberMatch(benhNhan.getSoDienThoai(), user.getSoDienThoai())) {
            return true;
        }
        
        // Kiểm tra họ tên
        if (isNameMatch(benhNhan.getHoTen(), user.getHoTen())) {
            // Nếu có ngày sinh, phải khớp cả ngày sinh
            if (benhNhan.getNgaySinh() != null && user.getNgaySinh() != null) {
                if (benhNhan.getNgaySinh().equals(user.getNgaySinh())) {
                    return true;
                }
            } else {
                // Nếu không có ngày sinh, chỉ cần khớp tên
                return true;
            }
        }
        
        return false;
    }
    private boolean isPhoneNumberMatch(String phone1, String phone2) {
        if (phone1 == null || phone2 == null) return false;
        
        String cleanPhone1 = phone1.replaceAll("[\\s\\-\\(\\)\\+]", "").trim();
        String cleanPhone2 = phone2.replaceAll("[\\s\\-\\(\\)\\+]", "").trim();
        
        if (cleanPhone1.isEmpty() || cleanPhone2.isEmpty()) return false;
        
        return cleanPhone1.equals(cleanPhone2);
    }
    private boolean isNameMatch(String name1, String name2) {
        if (name1 == null || name2 == null) return false;
        
        String cleanName1 = name1.trim().toLowerCase().replaceAll("\\s+", " ");
        String cleanName2 = name2.trim().toLowerCase().replaceAll("\\s+", " ");
        
        if (cleanName1.isEmpty() || cleanName2.isEmpty()) return false;
        
        return cleanName1.equals(cleanName2);
    }
    // Kiểm tra xem lịch hẹn có thuộc về người dùng không     
    private boolean isAppointmentBelongsToUser(LichHen lichHen, List<BenhNhan> matchingPatients) {
        // Kiểm tra qua ID bệnh nhân
        if (lichHen.getIdBenhNhan() > 0) {
            for (BenhNhan patient : matchingPatients) {
                if (patient.getIdBenhNhan() == lichHen.getIdBenhNhan()) {
                    return true;
                }
            }
        }        
        // Kiểm tra qua tên bệnh nhân trong lịch hẹn
        if (lichHen.getHoTenBenhNhan() != null && !lichHen.getHoTenBenhNhan().trim().isEmpty()) {
            for (BenhNhan patient : matchingPatients) {
                if (isNameMatch(lichHen.getHoTenBenhNhan(), patient.getHoTen())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    private void handleCancelAppointment(int lichHenId) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn hủy lịch hẹn này?\n" +
            "Lưu ý: Bạn chỉ có thể hủy lịch hẹn trước 24 giờ.",
            "Xác nhận hủy lịch",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                LichHen lichHen = controller.getLichHenById(lichHenId);
                if (lichHen == null) {
                    showErrorMessage("Không tìm thấy lịch hẹn!");
                    return;
                }
                
                java.util.Date now = new java.util.Date();
                java.util.Date appointmentDateTime = combineDateTime(lichHen.getNgayHen(), lichHen.getGioHen());
                long timeDiff = appointmentDateTime.getTime() - now.getTime();
                long hoursDiff = timeDiff / (1000 * 60 * 60);
                
                if (hoursDiff < 24) {
                    showErrorMessage("Không thể hủy lịch hẹn trong vòng 24 giờ trước cuộc hẹn!");
                    return;
                }
                
                boolean success = controller.capNhatTrangThaiLichHen(lichHenId, "Đã hủy");
                if (success) {
                	showSuccessToast("Đã hủy lịch hẹn thành công!");   
                    loadData();
                } else {
                    showErrorMessage("Không thể hủy lịch hẹn. Vui lòng thử lại!");
                }
            } catch (Exception e) {
                showErrorMessage("Lỗi khi hủy lịch hẹn: " + e.getMessage());
                e.printStackTrace();
            }
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
    private java.util.Date combineDateTime(java.sql.Date date, java.sql.Time time) {
        if (date == null || time == null) {
            return null;
        }
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        
        java.util.Calendar timeCal = java.util.Calendar.getInstance();
        timeCal.setTime(time);
        
        cal.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY));
        cal.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE));
        cal.set(java.util.Calendar.SECOND, timeCal.get(java.util.Calendar.SECOND));
        
        return cal.getTime();
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorPanel.setVisible(true);
        
        Timer timer = new Timer(5000, e -> errorPanel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 249, 250));
                }
            }
            
            if (column == 5) {
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "Đã xác nhận":
                        c.setForeground(successColor);
                        break;
                    case "Chờ xác nhận":
                        c.setForeground(warningColor);
                        break;
                    case "Đã hủy":
                        c.setForeground(accentColor);
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        break;
                }
            } else {
                c.setForeground(Color.BLACK);
            }
            
            return c;
        }
    }    
    private class SapToiTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Reset border trước khi set lại
            setBorder(null);
            
            if (!isSelected) {
                // Highlight lịch hẹn gần nhất (hàng đầu tiên) với màu vàng cho toàn bộ hàng
                if (row == 0 && sapToiTableModel.getRowCount() > 0) {
                    c.setBackground(nearestAppointmentColor);
                    // Tạo border cho toàn bộ hàng
                    if (column == 0) {
                        // Cột đầu tiên: border trái, trên, dưới
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(2, 2, 2, 0, nearestAppointmentBorderColor),
                            BorderFactory.createEmptyBorder(5, 8, 5, 5)
                        ));
                    } else if (column == table.getColumnCount() - 1) {
                        // Cột cuối: border phải, trên, dưới
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(2, 0, 2, 2, nearestAppointmentBorderColor),
                            BorderFactory.createEmptyBorder(5, 5, 5, 8)
                        ));
                    } else {
                        // Các cột giữa: chỉ border trên, dưới
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(2, 0, 2, 0, nearestAppointmentBorderColor),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                        ));
                    }
                } else {
                    // Màu xen kẽ cho các hàng khác
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                    // Padding bình thường cho các hàng khác
                    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                }
            } else {
                // Khi hàng được chọn, vẫn giữ padding
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            }
            
            // Màu chữ theo trạng thái
            if (column == 5) { // Cột trạng thái
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "Đã xác nhận":
                        c.setForeground(successColor);
                        break;
                    case "Chờ xác nhận":
                        c.setForeground(warningColor);
                        break;
                    case "Đã hủy":
                        c.setForeground(accentColor);
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        break;
                }
            } else {
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
    }

    // Thêm cải tiến cho ButtonRenderer để phù hợp với highlight
    private class ButtonRenderer implements javax.swing.table.TableCellRenderer {
        private JButton button;

        public ButtonRenderer() {
            button = new JButton();
            button.setOpaque(true);
            button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null && !value.toString().isEmpty()) {
                button.setText(value.toString());
                button.setForeground(Color.WHITE);
                // Nếu là hàng đầu tiên (lịch hẹn gần nhất), điều chỉnh màu button
                if (row == 0 && sapToiTableModel.getRowCount() > 0) {
                    button.setBackground(accentColor.darker()); // Màu đậm hơn để nổi bật trên nền vàng
                    // Border phải để khớp với highlight
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(2, 0, 2, 2, nearestAppointmentBorderColor),
                        BorderFactory.createEmptyBorder(3, 5, 3, 8)
                    ));
                } else {
                    button.setBackground(accentColor);
                    button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                }
            } else {
                button.setText("");
                if (row == 0 && sapToiTableModel.getRowCount() > 0) {
                    button.setBackground(nearestAppointmentColor);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(2, 0, 2, 2, nearestAppointmentBorderColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 8)
                    ));
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                }
            }
            return button;
        }
    }
    private void setupTableForSmoothHighlight(JTable table) {
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setDoubleBuffered(true);
    }
    private class ButtonEditor extends javax.swing.DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value != null && !value.toString().isEmpty()) {
                label = value.toString();
                button.setText(label);
                button.setBackground(accentColor);
                button.setForeground(Color.WHITE);
                currentRow = row;
                isPushed = true;
            } else {
                button.setText("");
                button.setBackground(Color.LIGHT_GRAY);
                isPushed = false;
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && "Hủy lịch".equals(label)) {
                int lichHenId = (Integer) sapToiTableModel.getValueAt(currentRow, 0);
                handleCancelAppointment(lichHenId);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}