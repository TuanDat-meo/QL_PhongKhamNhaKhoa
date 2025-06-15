package view;

import model.LichHen;
import model.HoSoBenhAn;
import controller.HoSoBenhAnController;
import controller.LichHenController; // Add this import

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AppointmentsDialog extends JDialog {
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private HoSoBenhAnController hoSoController;
    private LichHenController lichHenController; // Add this field
    private List<LichHen> originalAppointments;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemCapNhat;
    private Color primaryColor = new Color(79, 129, 189);
    private Color secondaryColor = new Color(141, 180, 226);
    private Color accentColor = new Color(192, 80, 77);
    private Color successColor = new Color(86, 156, 104);
    private Color warningColor = new Color(237, 187, 85);
    private Color completedColor = new Color(111, 66, 193); // New color for completed status
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = new Color(33, 37, 41);
    private Color panelColor = new Color(255, 255, 255);
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189);
    private Color tableStripeColor = new Color(245, 247, 250);
    private Color borderColor = new Color(222, 226, 230);
   
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font errorFont = new Font("Segoe UI", Font.ITALIC, 11);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    
    public AppointmentsDialog(JFrame parent, List<LichHen> appointments) {
        super(parent, "Quản Lý Lịch Hẹn", true);
        this.hoSoController = new HoSoBenhAnController();
        this.lichHenController = new LichHenController(); // Initialize controller
        this.originalAppointments = appointments;
        
        setSize(800, 480);
        setLocationRelativeTo(parent);
        
        initializeComponents(appointments);
        setupPopupMenu();
        setupEventHandlers();        
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents(List<LichHen> appointments) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(backgroundColor);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel(appointments);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }    
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        JLabel titleLabel = new JLabel("Quản Lý Lịch Hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Nhấp chuột phải để xem thêm tùy chọn");
        subtitleLabel.setFont(regularFont);
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }        
    
    private JPanel createTablePanel(List<LichHen> appointments) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(backgroundColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        String[] columns = {
            "ID", "Bệnh Nhân", "Bác Sĩ", "Ngày Hẹn", "Giờ Hẹn", "Phòng", "Trạng Thái", "Lý Do"
        };        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (LichHen appointment : appointments) {
            Object[] rowData = {
                appointment.getIdLichHen(),
                appointment.getHoTenBenhNhan(),
                appointment.getHoTenBacSi(),
                appointment.getNgayHen(),
                appointment.getGioHen(),
                appointment.getTenPhong(),
                appointment.getTrangThai(),
                appointment.getMoTa()
            };
            tableModel.addRow(rowData);
        }        
        
        appointmentsTable = new JTable(tableModel);
        styleTable();        
        
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.getViewport().setBackground(panelColor);
        scrollPane.setBackground(panelColor);
        scrollPane.setPreferredSize(new Dimension(760, 300));
        
        JLabel instructionLabel = new JLabel("Nhấp đúp để xem chi tiết hoặc nhấp chuột phải để hiển thị menu");
        instructionLabel.setFont(errorFont);
        instructionLabel.setForeground(new Color(108, 117, 125));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(instructionLabel, BorderLayout.SOUTH);
        
        return tablePanel;
    }    
    
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        popupMenu.setBackground(panelColor);
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết", new ImageIcon());
        menuItemCapNhat = createStyledMenuItem("Cập Nhật Trạng Thái", new ImageIcon());
        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemCapNhat);
        
        // Event handlers for menu items
        menuItemXemChiTiet.addActionListener(e -> {
            if (appointmentsTable.getSelectedRow() != -1) {
                viewSelectedAppointmentDetails();
            }
        });
        
        menuItemCapNhat.addActionListener(e -> {
            if (appointmentsTable.getSelectedRow() != -1) {
                updateAppointmentStatus();
            }
        });
        
        // Add mouse listener for popup menu
        appointmentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedAppointmentDetails();
                }
            }
            
            private void showPopupMenu(MouseEvent e) {
                int row = appointmentsTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < appointmentsTable.getRowCount()) {
                    appointmentsTable.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    private JMenuItem createStyledMenuItem(String text, Icon icon) {
        JMenuItem menuItem = new JMenuItem(text, icon);
        menuItem.setFont(regularFont);
        menuItem.setForeground(textColor);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        menuItem.setOpaque(true);
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(panelColor);
            }
        });
        
        return menuItem;
    }
    
    private void updateAppointmentStatus() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lịch hẹn để cập nhật trạng thái!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get current data
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Status options - Updated to match the enum exactly
        String[] statusOptions = {"Chờ xác nhận", "Đã xác nhận", "Đã hoàn thành", "Đã hủy"};
        
        // Create custom dialog for status selection
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Bệnh nhân: " + patientName), gbc);
        
        gbc.gridy = 1;
        panel.add(new JLabel("Trạng thái hiện tại: " + currentStatus), gbc);
        
        gbc.gridy = 2;
        panel.add(new JLabel("Chọn trạng thái mới:"), gbc);
        
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(currentStatus);
        statusCombo.setPreferredSize(new Dimension(200, 25));
        
        gbc.gridy = 3;
        panel.add(statusCombo, gbc);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Cập Nhật Trạng Thái Lịch Hẹn",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusCombo.getSelectedItem();
            
            if (newStatus != null && !newStatus.equals(currentStatus)) {
                // Show loading cursor
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                try {
                    // Update in database
                    boolean success = lichHenController.capNhatTrangThaiLichHen(appointmentId, newStatus);
                    
                    if (success) {
                        // Update table model
                        tableModel.setValueAt(newStatus, selectedRow, 6);
                        
                        // Update original appointments list
                        for (LichHen appointment : originalAppointments) {
                            if (appointment.getIdLichHen() == appointmentId) {
                                appointment.setTrangThai(newStatus);
                                break;
                            }
                        }
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            String.format("Trạng thái lịch hẹn đã được cập nhật thành công!\n\nBệnh nhân: %s\nTrạng thái cũ: %s\nTrạng thái mới: %s", 
                                patientName, currentStatus, newStatus),
                            "Cập nhật thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh table to show changes
                        appointmentsTable.repaint();
                        
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật trạng thái lịch hẹn.\nVui lòng kiểm tra kết nối cơ sở dữ liệu và thử lại.",
                            "Lỗi cập nhật",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Đã xảy ra lỗi khi cập nhật trạng thái:\n" + ex.getMessage(),
                        "Lỗi hệ thống",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    // Reset cursor
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Trạng thái không thay đổi.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void styleTable() {
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(35);
        appointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        appointmentsTable.setShowGrid(false);
        appointmentsTable.setIntercellSpacing(new Dimension(0, 1));
        appointmentsTable.setSelectionBackground(secondaryColor);
        appointmentsTable.setSelectionForeground(buttonTextColor);
        appointmentsTable.setFont(tableFont);
        
        JTableHeader header = appointmentsTable.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(buttonTextColor);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor));
        
        appointmentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? panelColor : tableStripeColor);
                }
                
                // Special styling for status column
                if (column == 6 && value != null) { // Status column
                    String status = value.toString();
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(getFont().deriveFont(Font.BOLD, 11f));
                    
                    if (!isSelected) {
                        switch (status) {
                            case "Đã xác nhận":
                                setForeground(successColor);
                                break;
                            case "Chờ xác nhận":
                                setForeground(warningColor);
                                break;
                            case "Đã hoàn thành":
                                setForeground(completedColor);
                                break;
                            case "Đã hủy":
                                setForeground(accentColor);
                                break;
                            default:
                                setForeground(textColor);
                        }
                    }
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(tableFont);
                    if (!isSelected) {
                        setForeground(textColor);
                    }
                }
                
                return c;
            }
        });
        
        // Set column widths
        appointmentsTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        appointmentsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Patient
        appointmentsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Doctor
        appointmentsTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Date
        appointmentsTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Time
        appointmentsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Room
        appointmentsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        appointmentsTable.getColumnModel().getColumn(7).setPreferredWidth(140); // Description
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Refresh Button
        JButton refreshButton = createStyledButton("Làm Mới", successColor, buttonTextColor);
        refreshButton.addActionListener(e -> refreshTable());
        
        // Close Button
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125), buttonTextColor);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(buttonFont);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 32));
        
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(originalColor, 0.9f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * factor), 0),
            Math.max((int)(color.getGreen() * factor), 0),
            Math.max((int)(color.getBlue() * factor), 0),
            color.getAlpha()
        );
    }
    
    private void refreshTable() {
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Reload fresh data from database if needed
            // For now, we'll use the original appointments list
            // In a real application, you might want to reload from database
            
            // Repopulate with fresh data
            for (LichHen appointment : originalAppointments) {
                Object[] rowData = {
                    appointment.getIdLichHen(),
                    appointment.getHoTenBenhNhan(),
                    appointment.getHoTenBacSi(),
                    appointment.getNgayHen(),
                    appointment.getGioHen(),
                    appointment.getTenPhong(),
                    appointment.getTrangThai(),
                    appointment.getMoTa()
                };
                tableModel.addRow(rowData);
            }
            
            tableModel.fireTableDataChanged();
            
        } finally {
            // Reset cursor
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void setupEventHandlers() {
        // The popup menu handles double-click events
        // No additional setup needed since popup menu is already configured
    }
    
    private void viewSelectedAppointmentDetails() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lịch hẹn để xem chi tiết!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get appointment info
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Find the corresponding LichHen object
        LichHen selectedAppointment = null;
        for (LichHen appointment : originalAppointments) {
            if (appointment.getIdLichHen() == appointmentId) {
                selectedAppointment = appointment;
                break;
            }
        }
        
        if (selectedAppointment != null) {
            // Create and show details dialog
            AppointmentDetailsDialog detailsDialog = new AppointmentDetailsDialog(
                this, selectedAppointment, hoSoController);
            detailsDialog.setVisible(true);
        }
    }
}

// Separate dialog for showing appointment and medical record details with updated colors
class AppointmentDetailsDialog extends JDialog {
    private HoSoBenhAnController hoSoController;
    private LichHen appointment;    
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = new Color(33, 37, 41);
    private Color panelColor = new Color(255, 255, 255);
    private Color buttonTextColor = Color.WHITE;
    private Color tableStripeColor = new Color(245, 247, 250);
    private Color borderColor = new Color(222, 226, 230);
    
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    
    public AppointmentDetailsDialog(Dialog parent, LichHen appointment, 
            HoSoBenhAnController hoSoController) {
        super(parent, "Chi Tiết Lịch Hẹn - " + appointment.getHoTenBenhNhan(), true);
        this.appointment = appointment;
        this.hoSoController = hoSoController;
        setSize(580, 480);
        setLocationRelativeTo(parent);        
        initializeDetailsComponents();        
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }    
    
    private void initializeDetailsComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(backgroundColor);        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);        
        // Tabbed pane for different sections
        JTabbedPane tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);        
        // Close button
        JPanel buttonPanel = createCloseButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);        
        setContentPane(mainPanel);
    }    
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Chi Tiết Lịch Hẹn");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel = new JLabel(String.format("Bệnh nhân: %s | Bác sĩ: %s", 
            appointment.getHoTenBenhNhan(), appointment.getHoTenBacSi()));
        infoLabel.setFont(regularFont);
        infoLabel.setForeground(new Color(108, 117, 125));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(infoLabel);
        
        return headerPanel;
    }
    
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Appointment info tab
        JPanel appointmentPanel = createAppointmentInfoPanel();
        tabbedPane.addTab("Thông Tin Lịch Hẹn", appointmentPanel);
        
        // Medical records tab
        JPanel medicalRecordsPanel = createMedicalRecordsPanel();
        tabbedPane.addTab("Hồ Sơ Bệnh Án", medicalRecordsPanel);
        
        return tabbedPane;
    }
    
    private JPanel createCloseButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(buttonTextColor);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(buttonFont);
        closeButton.setPreferredSize(new Dimension(80, 32));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        return buttonPanel;
    }
    
    private JPanel createAppointmentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Create info display panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(tableStripeColor);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 12);
               
        // Add appointment details
        addInfoRow(infoPanel, "ID Lịch Hẹn:", String.valueOf(appointment.getIdLichHen()), gbc, 0);
        addInfoRow(infoPanel, "Bệnh Nhân:", appointment.getHoTenBenhNhan(), gbc, 1);
        addInfoRow(infoPanel, "Bác Sĩ:", appointment.getHoTenBacSi(), gbc, 2);
        addInfoRow(infoPanel, "Ngày Hẹn:", String.valueOf(appointment.getNgayHen()), gbc, 3);
        addInfoRow(infoPanel, "Giờ Hẹn:", String.valueOf(appointment.getGioHen()), gbc, 4);
        addInfoRow(infoPanel, "Phòng:", appointment.getTenPhong(), gbc, 5);
        addInfoRow(infoPanel, "Trạng Thái:", appointment.getTrangThai(), gbc, 6);
        addInfoRow(infoPanel, "Lý Do:", appointment.getMoTa(), gbc, 7);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelComponent.setForeground(new Color(73, 80, 87));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        
        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(regularFont);
        valueComponent.setForeground(textColor);
        panel.add(valueComponent, gbc);
    }
    
    private JPanel createMedicalRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        int patientId = appointment.getIdBenhNhan();
        
        if (patientId == 0) {
            patientId = hoSoController.timIdBenhNhanBangTen(appointment.getHoTenBenhNhan());
            if (patientId == 0) {
                return createErrorPanel("Không tìm thấy ID bệnh nhân hợp lệ");
            }
        }
        List<HoSoBenhAn> patientRecords = hoSoController.layHoSoBenhAnTheoIdBenhNhan(patientId);
        
        if (patientRecords.isEmpty()) {
            return createEmptyRecordsPanel(patientId);
        } else {
            return createRecordsTablePanel(patientRecords, patientId);
        }
    }
    
    private JPanel createErrorPanel(String errorMessage) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorLabel = new JLabel(String.format(
            "<html><center><h3>Lỗi dữ liệu</h3>" +
            "<p>%s</p><br>" +
            "<p><b>Thông tin debug:</b></p>" +
            "<p>Appointment ID: %s</p>" +
            "<p>Tên bệnh nhân: %s</p>" +
            "<p>ID bệnh nhân: %d</p>" +
            "</center></html>", 
            errorMessage,
            appointment.getIdLichHen(),
            appointment.getHoTenBenhNhan(),
            appointment.getIdBenhNhan()));
        
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
    }
    
    private JPanel createEmptyRecordsPanel(int patientId) {
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setBackground(Color.WHITE);
        
        JLabel noRecordsLabel = new JLabel(String.format(
            "<html><center>" +
            "<h3>Không có hồ sơ bệnh án</h3>" +
            "<p>Bệnh nhân: <b>%s</b></p>" +
            "<p>ID bệnh nhân: <b>%d</b></p><br>" +
            "<p><small>Hệ thống đã tìm kiếm trực tiếp trong database</small></p>" +
            "</center></html>", 
            appointment.getHoTenBenhNhan(), 
            patientId));
        
        noRecordsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noRecordsLabel.setForeground(new Color(108, 117, 125));
        noRecordsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        emptyPanel.add(noRecordsLabel, BorderLayout.CENTER);
        return emptyPanel;
    }

    private JPanel createRecordsTablePanel(List<HoSoBenhAn> patientRecords, int patientId) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        // Tạo bảng hiển thị hồ sơ
        String[] columns = {"ID Hồ Sơ", "Chuẩn Đoán", "Ngày Tạo", "Trạng Thái", "Ghi Chú"};
        DefaultTableModel medicalTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (HoSoBenhAn record : patientRecords) {
            Object[] rowData = {
                record.getIdHoSo(),
                record.getChuanDoan(),
                record.getNgayTao(),
                record.getTrangThai(),
                record.getGhiChu()
            };
            medicalTableModel.addRow(rowData);
        }
        
        JTable medicalTable = new JTable(medicalTableModel);
        styleMedicalTable(medicalTable);
        
        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(520, 250));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Thông tin và nút
        JPanel bottomPanel = createBottomPanel(patientRecords.size(), patientId, medicalTable, medicalTableModel);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private JPanel createBottomPanel(int recordCount, int patientId, JTable medicalTable, DefaultTableModel medicalTableModel) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        // Thông tin bệnh nhân
        JLabel infoLabel = new JLabel(String.format(
            "Hiển thị %d hồ sơ bệnh án của bệnh nhân: %s (ID: %d)", 
            recordCount, appointment.getHoTenBenhNhan(), patientId));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(40, 167, 69));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Nút xem chi tiết
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewRecordButton = new JButton("Xem Chi Tiết Hồ Sơ");
        viewRecordButton.setBackground(new Color(0, 123, 255));
        viewRecordButton.setForeground(Color.WHITE);
        viewRecordButton.setFocusPainted(false);
        viewRecordButton.setBorderPainted(false);
        viewRecordButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewRecordButton.setPreferredSize(new Dimension(150, 38));
        viewRecordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        viewRecordButton.addActionListener(e -> {
            int selectedRow = medicalTable.getSelectedRow();
            if (selectedRow != -1) {
                int recordId = (Integer) medicalTableModel.getValueAt(selectedRow, 0);
                showMedicalRecordDetails(recordId);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một hồ sơ để xem chi tiết!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(viewRecordButton);
        
        bottomPanel.add(infoLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void styleMedicalTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(40);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 244, 255));
        table.setSelectionForeground(new Color(33, 37, 41));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(218, 220, 224)));
        
        // Custom cell renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    setForeground(new Color(33, 37, 41));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setHorizontalAlignment(SwingConstants.LEFT);
                
                return c;
            }
        });
    }
    
    private void showMedicalRecordDetails(int recordId) {
        HoSoBenhAn record = hoSoController.timKiemHoSoBenhAnTheoId(recordId);
        if (record != null) {
            // Create a modern details dialog
            JDialog detailsDialog = new JDialog(this, "Chi Tiết Hồ Sơ Bệnh Án", true);
            detailsDialog.setSize(600, 500);
            detailsDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(250, 251, 252));
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(new Color(255, 255, 255));
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 232, 236)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
            ));
            
            JLabel titleLabel = new JLabel("Chi Tiết Hồ Sơ Bệnh Án");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(new Color(33, 37, 41));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel subtitleLabel = new JLabel("ID Hồ Sơ: " + record.getIdHoSo());
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitleLabel.setForeground(new Color(108, 117, 125));
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            headerPanel.add(titleLabel);
            headerPanel.add(Box.createVerticalStrut(8));
            headerPanel.add(subtitleLabel);
            
            // Content panel
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(12, 0, 12, 20);
            
            // Add record details
            addDetailRow(contentPanel, "ID Hồ Sơ:", String.valueOf(record.getIdHoSo()), gbc, 0);
            addDetailRow(contentPanel, "ID Bệnh Nhân:", String.valueOf(record.getIdBenhNhan()), gbc, 1);
            addDetailRow(contentPanel, "Chuẩn Đoán:", record.getChuanDoan(), gbc, 2);
            addDetailRow(contentPanel, "Ngày Tạo:", String.valueOf(record.getNgayTao()), gbc, 3);
            addDetailRow(contentPanel, "Trạng Thái:", record.getTrangThai(), gbc, 4);
            addDetailRow(contentPanel, "Ghi Chú:", record.getGhiChu(), gbc, 5);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(new Color(255, 255, 255));
            buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 232, 236)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            
            JButton closeDetailButton = new JButton("Đóng");
            closeDetailButton.setBackground(new Color(108, 117, 125));
            closeDetailButton.setForeground(Color.WHITE);
            closeDetailButton.setFocusPainted(false);
            closeDetailButton.setBorderPainted(false);
            closeDetailButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            closeDetailButton.setPreferredSize(new Dimension(100, 38));
            closeDetailButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeDetailButton.addActionListener(e -> detailsDialog.dispose());
            
            buttonPanel.add(closeDetailButton);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            detailsDialog.setContentPane(mainPanel);
            detailsDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy hồ sơ bệnh án!",
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addDetailRow(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(73, 80, 87));
        labelComponent.setPreferredSize(new Dimension(120, 25));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        
        // Handle long text with text area for better display
        if (value != null && value.length() > 50) {
            JTextArea valueArea = new JTextArea(value);
            valueArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valueArea.setForeground(new Color(33, 37, 41));
            valueArea.setBackground(new Color(248, 249, 250));
            valueArea.setEditable(false);
            valueArea.setLineWrap(true);
            valueArea.setWrapStyleWord(true);
            valueArea.setRows(2);
            valueArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            JScrollPane scrollPane = new JScrollPane(valueArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1));
            scrollPane.setPreferredSize(new Dimension(350, 50));
            panel.add(scrollPane, gbc);
        } else {
            JLabel valueComponent = new JLabel(value != null ? value : "N/A");
            valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valueComponent.setForeground(new Color(33, 37, 41));
            panel.add(valueComponent, gbc);
        }
    }
}