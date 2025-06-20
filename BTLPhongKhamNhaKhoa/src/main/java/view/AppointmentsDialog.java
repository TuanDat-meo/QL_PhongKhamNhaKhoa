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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    
    private JDialog currentChildDialog = null;    
    private Color primaryColor = new Color(79, 129, 189); 
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
    // Updated selection colors to match medical records table
    private Color tableSelectionBackground = new Color(230, 244, 255);
    private Color tableSelectionForeground = new Color(33, 37, 41);
   
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
        setFocusableWindowState(false); // Disable initial focus
        
        initializeComponents(appointments);
        setupPopupMenu();
        setupEventHandlers();        
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Set focus back to normal after initialization
        SwingUtilities.invokeLater(() -> setFocusableWindowState(true));
    }
    
    // Phương thức để đóng dialog con hiện tại
    private void closeCurrentChildDialog() {
        if (currentChildDialog != null && currentChildDialog.isDisplayable()) {
            currentChildDialog.dispose();
            currentChildDialog = null;
        }
    }
    
    // Phương thức để hiển thị dialog con mới
    private void showChildDialog(JDialog dialog) {
        closeCurrentChildDialog(); // Đóng dialog con hiện tại trước
        currentChildDialog = dialog;
        dialog.setVisible(true);
    }
    
    // Override dispose để đảm bảo đóng tất cả dialog con
    @Override
    public void dispose() {
        closeCurrentChildDialog();
        super.dispose();
    }

    // Custom message dialog method
    private void showCustomMessageDialog(String title, String message, int messageType) {
        JDialog messageDialog = new JDialog(this, title, true);
        messageDialog.setSize(400, 180);
        messageDialog.setLocationRelativeTo(this);
        messageDialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);
        
        // Add icon based on message type
        JLabel iconLabel = new JLabel();
        switch (messageType) {
            case JOptionPane.INFORMATION_MESSAGE:
                iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                break;
            case JOptionPane.WARNING_MESSAGE:
                iconLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
                break;
            case JOptionPane.ERROR_MESSAGE:
                iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                break;
            default:
                iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        }
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        messagePanel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        Color buttonColor;
        switch (messageType) {
            case JOptionPane.INFORMATION_MESSAGE:
                buttonColor = successColor;
                break;
            case JOptionPane.WARNING_MESSAGE:
                buttonColor = warningColor;
                break;
            case JOptionPane.ERROR_MESSAGE:
                buttonColor = accentColor;
                break;
            default:
                buttonColor = primaryColor;
        }
        
        JButton okButton = createRoundedButton("OK", buttonColor, Color.WHITE, 8, false);
        okButton.addActionListener(e -> messageDialog.dispose());
        
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        messageDialog.setContentPane(panel);
        messageDialog.setVisible(true);
    }
    
    // New rounded button method
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
        
        // Use different padding based on button type
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Smaller padding for "Edit"
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Normal padding for "Close"
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
    
    // Updated darkenColor method using HSB
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
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
            showCustomMessageDialog("Thông báo",
                "Vui lòng chọn một lịch hẹn để cập nhật trạng thái!",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get current data - Fix the casting issues
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Fix: Handle date and time properly - check the actual data type first
        Object dateObj = tableModel.getValueAt(selectedRow, 3);
        Object timeObj = tableModel.getValueAt(selectedRow, 4);
        
        String appointmentDate;
        String appointmentTime;
        
        // Convert date object to string
        if (dateObj instanceof java.sql.Date) {
            java.sql.Date sqlDate = (java.sql.Date) dateObj;
            // Format the date as needed
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            appointmentDate = dateFormat.format(sqlDate);
        } else if (dateObj instanceof java.util.Date) {
            java.util.Date utilDate = (java.util.Date) dateObj;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            appointmentDate = dateFormat.format(utilDate);
        } else {
            appointmentDate = dateObj != null ? dateObj.toString() : "";
        }
        
        // Convert time object to string
        if (timeObj instanceof java.sql.Time) {
            java.sql.Time sqlTime = (java.sql.Time) timeObj;
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            appointmentTime = timeFormat.format(sqlTime);
        } else if (timeObj instanceof java.util.Date) {
            java.util.Date utilDate = (java.util.Date) timeObj;
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            appointmentTime = timeFormat.format(utilDate);
        } else {
            appointmentTime = timeObj != null ? timeObj.toString() : "";
        }
        
        // Status options
        String[] statusOptions = {"Chờ xác nhận", "Đã xác nhận", "Đã hoàn thành", "Đã hủy"};
        
        // Create custom dialog
        JDialog dialog = new JDialog(this, "Cập Nhật Trạng Thái Lịch Hẹn", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setFocusableWindowState(false); // Disable initial focus
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(backgroundColor);
        
        // Header Panel - Updated color scheme
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(primaryColor); // Changed from blue to primary color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Cập Nhật Trạng Thái");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(panelColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        // Simplified info display
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBackground(panelColor);
        
        infoPanel.add(new JLabel("BN: " + patientName));
        infoPanel.add(new JLabel("BS: " + doctorName));
        infoPanel.add(new JLabel("Ngày: " + appointmentDate));
        infoPanel.add(new JLabel("Giờ: " + appointmentTime));
        
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Current Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setBackground(panelColor);
        statusPanel.add(new JLabel("Hiện tại: "));
        JLabel currentStatusLabel = new JLabel(currentStatus);
        currentStatusLabel.setForeground(getStatusColor(currentStatus));
        statusPanel.add(currentStatusLabel);
        
        contentPanel.add(statusPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // New Status Selection
        JPanel newStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        newStatusPanel.setBackground(panelColor);
        newStatusPanel.add(new JLabel("Đổi thành: "));
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(currentStatus);
        statusCombo.setPreferredSize(new Dimension(150, 30));
        statusCombo.setFocusable(false); // Disable focus
        newStatusPanel.add(statusCombo);
        
        contentPanel.add(newStatusPanel);
        
        // Button Panel - Updated to use rounded buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Cancel Button - Updated color
        JButton cancelButton = createRoundedButton("Hủy", new Color(148, 163, 184), Color.WHITE, 8, true);
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Update Button - Updated color
        JButton updateButton = createRoundedButton("Cập Nhật", warningColor, Color.WHITE, 8, true);
        updateButton.setPreferredSize(new Dimension(80, 35));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        
        // Assemble dialog
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainContainer);
        
        // Update button action
        updateButton.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            
            if (newStatus != null && !newStatus.equals(currentStatus)) {
                // Create confirmation dialog similar to xoaBenhNhan method
                JDialog confirmDialog = new JDialog();
                confirmDialog.setTitle("Xác nhận cập nhật");
                confirmDialog.setModal(true);
                confirmDialog.setSize(400, 200);
                confirmDialog.setLocationRelativeTo(dialog);
                
                JPanel panel = new JPanel(new BorderLayout(10, 15));
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
                messagePanel.setBackground(Color.WHITE);
                
                JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn thay đổi trạng thái từ<br><b>" + 
                    currentStatus + "</b> thành <b>" + newStatus + "</b>?</html>");
                messageLabel.setFont(regularFont);
                messagePanel.add(messageLabel, BorderLayout.CENTER);
                
                panel.add(messagePanel, BorderLayout.CENTER);
                
                JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                confirmButtonPanel.setBackground(Color.WHITE);
                
                JButton confirmCancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8, false);
                confirmCancelButton.addActionListener(cancelEvent -> confirmDialog.dispose());
                
                JButton confirmUpdateButton = createRoundedButton("Cập Nhật", warningColor, Color.WHITE, 8, false);
                confirmUpdateButton.addActionListener(confirmEvent -> {
                    confirmDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    confirmUpdateButton.setEnabled(false);
                    confirmUpdateButton.setText("Đang cập nhật...");
                    
                    try {
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
                            
                            confirmDialog.dispose();
                            dialog.dispose();
                            
                            appointmentsTable.repaint();
                            
                            // Show success message with custom dialog
                            showCustomMessageDialog("Cập nhật thành công",
                                "Trạng thái lịch hẹn đã được cập nhật thành công!",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                        } else {
                            showCustomMessageDialog("Lỗi cập nhật",
                                "Không thể cập nhật trạng thái lịch hẹn.\nVui lòng thử lại sau.",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        showCustomMessageDialog("Lỗi hệ thống",
                            "Đã xảy ra lỗi khi cập nhật trạng thái:\n" + ex.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } finally {
                        confirmDialog.setCursor(Cursor.getDefaultCursor());
                        confirmUpdateButton.setEnabled(true);
                        confirmUpdateButton.setText("Cập Nhật");
                    }
                });
                
                confirmButtonPanel.add(confirmCancelButton);
                confirmButtonPanel.add(confirmUpdateButton);
                panel.add(confirmButtonPanel, BorderLayout.SOUTH);
                
                confirmDialog.setContentPane(panel);
                confirmDialog.setVisible(true);
                
            } else {
                showCustomMessageDialog("Thông báo",
                    "Vui lòng chọn trạng thái khác với trạng thái hiện tại.",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Set focus back to normal after showing
        SwingUtilities.invokeLater(() -> dialog.setFocusableWindowState(true));
        
        // Sử dụng phương thức showChildDialog để quản lý dialog
        showChildDialog(dialog);
    }
    private Color getStatusColor(String status) {
        switch (status) {
            case "Đã xác nhận":
                return new Color(40, 167, 69);
            case "Chờ xác nhận":
                return new Color(255, 133, 27);
            case "Đã hoàn thành":
                return new Color(111, 66, 193);
            case "Đã hủy":
                return new Color(220, 53, 69);
            default:
                return new Color(33, 37, 41);
        }
    }  
    private void styleTable() {
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(35);
        appointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        appointmentsTable.setShowGrid(false);
        appointmentsTable.setIntercellSpacing(new Dimension(0, 1));
        // Updated selection colors to match medical records table
        appointmentsTable.setSelectionBackground(tableSelectionBackground);
        appointmentsTable.setSelectionForeground(tableSelectionForeground);
        appointmentsTable.setFont(tableFont);
        appointmentsTable.setFocusable(false); // Disable table focus
        
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
                    } else {
                        // When selected, use the same selection foreground color as medical records table
                        setForeground(tableSelectionForeground);
                    }
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(tableFont);
                    if (!isSelected) {
                        setForeground(textColor);
                    } else {
                        // When selected, use the same selection foreground color as medical records table
                        setForeground(tableSelectionForeground);
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
        
        // Refresh Button - Updated to use rounded button
        JButton refreshButton = createRoundedButton("Làm Mới", successColor, buttonTextColor, 10, false);
        refreshButton.setPreferredSize(new Dimension(100, 32));
        refreshButton.addActionListener(e -> refreshTable());
        
        // Close Button - Updated to use rounded button
        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10, false);
        closeButton.setPreferredSize(new Dimension(100, 32));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        return buttonPanel;
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
class AppointmentDetailsDialog extends JDialog {
    private HoSoBenhAnController hoSoController;
    private LichHen appointment;    
    private Color primaryColor = new Color(79, 129, 189); 
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = new Color(33, 37, 41);
    private Color panelColor = new Color(255, 255, 255);
    private Color buttonTextColor = Color.WHITE;
    private Color tableStripeColor = new Color(245, 247, 250);
    private Color borderColor = new Color(222, 226, 230);
    
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    
    // Biến để theo dõi các dialog con đang mở
    private List<JDialog> activeChildDialogs = new ArrayList<>();
    private JPopupMenu activePopupMenu = null;
    
    public AppointmentDetailsDialog(Dialog parent, LichHen appointment, 
            HoSoBenhAnController hoSoController) {
        super(parent, "Chi Tiết Lịch Hẹn - " + appointment.getHoTenBenhNhan(), true);
        this.appointment = appointment;
        this.hoSoController = hoSoController;
        setSize(580, 520);
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
        
        // Thêm ChangeListener để đóng các dialog con khi chuyển tab
        tabbedPane.addChangeListener(e -> {
            closeAllActiveElements();
        });
        
        return tabbedPane;
    }
    
    // Phương thức đóng tất cả dialog con và popup menu đang active
    private void closeAllActiveElements() {
        // Đóng tất cả dialog con
        for (JDialog dialog : activeChildDialogs) {
            if (dialog != null && dialog.isDisplayable()) {
                dialog.dispose();
            }
        }
        activeChildDialogs.clear();
        
        // Đóng popup menu nếu đang hiển thị
        if (activePopupMenu != null && activePopupMenu.isVisible()) {
            activePopupMenu.setVisible(false);
        }
        activePopupMenu = null;
    }
    
    // Override dispose để đảm bảo đóng tất cả dialog con khi đóng dialog chính
    @Override
    public void dispose() {
        closeAllActiveElements();
        super.dispose();
    }
    
    private JPanel createCloseButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        // Sử dụng rounded button
        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 8, false);
        closeButton.setPreferredSize(new Dimension(80, 35));
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
        
        // Thêm mouse listener cho việc nhấn đúp và chuột phải
        medicalTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Nhấn đúp để xem chi tiết
                    int selectedRow = medicalTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int recordId = (Integer) medicalTableModel.getValueAt(selectedRow, 0);
                        showMedicalRecordDetails(recordId);
                    }
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMedicalRecordPopup(e, medicalTable, medicalTableModel);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMedicalRecordPopup(e, medicalTable, medicalTableModel);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(520, 250));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Thông tin hướng dẫn
        JPanel bottomPanel = createMedicalRecordsBottomPanel(patientRecords.size(), patientId);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }    
 
    // Cải tiến phương thức showMedicalRecordPopup để quản lý popup menu
    private void showMedicalRecordPopup(MouseEvent e, JTable medicalTable, DefaultTableModel medicalTableModel) {
        // Đóng popup menu cũ nếu có
        if (activePopupMenu != null && activePopupMenu.isVisible()) {
            activePopupMenu.setVisible(false);
        }
        
        int row = medicalTable.rowAtPoint(e.getPoint());
        if (row >= 0 && row < medicalTable.getRowCount()) {
            medicalTable.setRowSelectionInterval(row, row);
            
            // Lấy thông tin hồ sơ được chọn
            int recordId = (Integer) medicalTableModel.getValueAt(row, 0);
            
            // Tạo popup menu với thiết kế mới
            JPopupMenu medicalPopup = new JPopupMenu();
            medicalPopup.setBorder(new LineBorder(borderColor, 1));
            medicalPopup.setBackground(panelColor);
            
            // Menu item: Xem Chi Tiết
            JMenuItem viewDetailsItem = createMenuItem("Xem Chi Tiết Hồ Sơ");
            viewDetailsItem.addActionListener(actionEvent -> {
                showMedicalRecordDetails(recordId);
                medicalPopup.setVisible(false); // Đóng popup sau khi chọn
            });
            medicalPopup.add(viewDetailsItem);
            
            // Lưu reference để có thể đóng sau này
            activePopupMenu = medicalPopup;
            
            // Hiển thị popup menu
            medicalPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    // Phương thức tạo menu item với style thống nhất
    private JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setForeground(textColor);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        menuItem.setOpaque(true);
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm hover effect
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
    private JPanel createMedicalRecordsBottomPanel(int recordCount, int patientId) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        // Thông tin bệnh nhân và hướng dẫn
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        JLabel recordCountLabel = new JLabel(String.format(
            "Hiển thị %d hồ sơ bệnh án của bệnh nhân: %s (ID: %d)", 
            recordCount, appointment.getHoTenBenhNhan(), patientId));
        recordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordCountLabel.setForeground(new Color(40, 167, 69));
        
        JLabel instructionLabel = new JLabel("Nhấn đúp vào dòng để xem chi tiết hoặc nhấn chuột phải để hiển thị menu");
        instructionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(108, 117, 125));
        
        infoPanel.add(recordCountLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(instructionLabel);
        
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        
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
            // Ẩn dialog hiện tại trước khi hiển thị dialog chi tiết
            this.setVisible(false);
            
            // Create a modern details dialog
            JDialog detailsDialog = new JDialog((Dialog)this.getOwner(), "Chi Tiết Hồ Sơ Bệnh Án", true);
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
            
            // Tạo button đóng với logic quay lại dialog trước
            JButton closeDetailButton = createRoundedButton("Đóng", primaryColor, Color.WHITE, 8, false);
            closeDetailButton.setPreferredSize(new Dimension(100, 38));
            closeDetailButton.addActionListener(e -> {
                detailsDialog.dispose();
                // Hiển thị lại dialog chính sau khi đóng dialog chi tiết
                this.setVisible(true);
            });
            
            buttonPanel.add(closeDetailButton);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            detailsDialog.setContentPane(mainPanel);
            
            // Đặt hành động khi đóng dialog bằng nút X
            detailsDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    detailsDialog.dispose();
                    // Hiển thị lại dialog chính
                    AppointmentDetailsDialog.this.setVisible(true);
                }
            });
            
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
        
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Padding nhỏ hơn
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding bình thường
        }        
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
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
    }
}