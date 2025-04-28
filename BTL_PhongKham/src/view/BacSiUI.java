package view;

import controller.BacSiController;
import model.BacSi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BacSiUI extends JPanel {
    // Color scheme based on BenhNhanUI
    private Color primaryColor = new Color(79, 129, 189);     // Professional blue
    private Color secondaryColor = new Color(141, 180, 226);  // Lighter blue
    private Color accentColor = new Color(192, 80, 77);       // Refined red for delete
    private Color successColor = new Color(86, 156, 104);     // Elegant green for add
    private Color warningColor = new Color(237, 187, 85);     // Softer yellow for edit
    private Color backgroundColor = new Color(248, 249, 250); // Extremely light gray background
    private Color textColor = new Color(33, 37, 41);          // Near-black text
    private Color panelColor = new Color(255, 255, 255);      // White panels
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189); // Match primary color
    private Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private Color borderColor = new Color(222, 226, 230);     // Light gray borders
    
    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    
    private BacSiController bacSiController;
    private JTable bacSiTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton exportButton;
    private int currentBacSiId = -1;
    
    private JFrame parentFrame;
    
    public BacSiUI() {
        bacSiController = new BacSiController();
        
        setBackground(backgroundColor);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadBacSiData();
    }
    
    private void initComponents() {
        // Header panel (Title and Search)
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("QUẢN LÝ BÁC SĨ");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        searchField = new JTextField(18);
        searchField.setFont(regularFont);
        searchField.setBorder(new LineBorder(borderColor, 1));
        
        searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(buttonFont);
        searchButton.setForeground(buttonTextColor);
        searchButton.setBackground(primaryColor);
        searchButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchBacSi());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Table panel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create table with simplified columns matching the image
	     String[] columns = {
	         "ID", "Họ Tên", "Chuyên Khoa", "Bằng Cấp", "Kinh Nghiệm", "Phòng Khám", "Email", "Số Điện Thoại"
	     };
	
	     tableModel = new DefaultTableModel(columns, 0) {
	         @Override
	         public boolean isCellEditable(int row, int column) {
	             return false;
	         }
	     };
	
	     bacSiTable = new JTable(tableModel) {
	         @Override
	         public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
	             Component comp = super.prepareRenderer(renderer, row, column);
	             // Add alternating row colors
	             if (!comp.getBackground().equals(getSelectionBackground())) {
	                 comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
	             }
	             return comp;
	         }
	     };
	
	     bacSiTable.setFont(tableFont);
	     bacSiTable.setRowHeight(40);
	     bacSiTable.setShowGrid(false);
	     bacSiTable.setIntercellSpacing(new Dimension(0, 0));
	     bacSiTable.setSelectionBackground(new Color(229, 243, 255));
	     bacSiTable.setSelectionForeground(textColor);
	     bacSiTable.setFocusable(false);
	     bacSiTable.setAutoCreateRowSorter(true);
	     bacSiTable.setBorder(null);
	
	     // Style table header
	     JTableHeader header = bacSiTable.getTableHeader();
	     header.setFont(tableHeaderFont);
	     header.setBackground(tableHeaderColor);
	     header.setForeground(Color.WHITE);
	     header.setPreferredSize(new Dimension(header.getWidth(), 45));
	     header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
	     header.setReorderingAllowed(false);
	
	     // Set column widths - proportional to the reference image
	     TableColumnModel columnModel = bacSiTable.getColumnModel();
	     columnModel.getColumn(0).setPreferredWidth(50);   // ID
	     columnModel.getColumn(1).setPreferredWidth(180);  // Họ Tên
	     columnModel.getColumn(2).setPreferredWidth(150);  // Chuyên Khoa
	     columnModel.getColumn(3).setPreferredWidth(100);  // Bằng Cấp
	     columnModel.getColumn(4).setPreferredWidth(80);   // Kinh Nghiệm
	     columnModel.getColumn(5).setPreferredWidth(120);  // Phòng Khám
	     columnModel.getColumn(6).setPreferredWidth(180);  // Email
	     columnModel.getColumn(7).setPreferredWidth(120);  // Số Điện Thoại
	        
        
        // Alternating row colors
        bacSiTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (value == null || value.toString().trim().isEmpty()) {
                        component.setBackground(Color.RED); // Nền trắng cho ô trống
                    } else {
                        component.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor); // Nền xen kẽ
                    }
                }
                
                if (column == 0) {
                    ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    ((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return component;
            }
        });
        
        bacSiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = bacSiTable.getSelectedRow();
                if (selectedRow >= 0) {
                    currentBacSiId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    if (e.getClickCount() == 2) {
                        // Double click to show details
                        BacSi selectedBacSi = bacSiController.getBacSiById(currentBacSiId);
                        if (selectedBacSi != null) {
                            showBacSiDetails(selectedBacSi);
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // Right click to show popup menu
                        showRowPopupMenu(e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bacSiTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        exportButton = new JButton("Xuất file");
        exportButton.setFont(buttonFont);
        exportButton.setForeground(buttonTextColor);
        exportButton.setBackground(warningColor);
        exportButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportData());
        
        addButton = new JButton("Thêm mới");
        addButton.setFont(buttonFont);
        addButton.setForeground(buttonTextColor);
        addButton.setBackground(successColor);
        addButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddBacSiDialog());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(addButton);
        
        // Add everything to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void showRowPopupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        BacSi selectedBacSi = bacSiController.getBacSiById(currentBacSiId);
        
        if (selectedBacSi != null) {
            JMenuItem viewDetailsItem = createMenuItem("Xem Chi Tiết");
            viewDetailsItem.addActionListener(e -> showBacSiDetails(selectedBacSi));
            popupMenu.add(viewDetailsItem);
            
            JMenuItem editItem = createMenuItem("Chỉnh Sửa");
            editItem.addActionListener(e -> showEditBacSiDialog());
            popupMenu.add(editItem);
            
            JMenuItem deleteItem = createMenuItem("Xóa");
            deleteItem.setForeground(accentColor);
            deleteItem.addActionListener(e -> deleteBacSi());
            popupMenu.add(deleteItem);
            
            popupMenu.addSeparator();
            
            JMenuItem appointmentsItem = createMenuItem("Xem Lịch Hẹn");
            appointmentsItem.addActionListener(e -> showAppointments());
            popupMenu.add(appointmentsItem);
            
            JMenuItem treatmentsItem = createMenuItem("Xem Điều Trị");
            treatmentsItem.addActionListener(e -> showTreatments());
            popupMenu.add(treatmentsItem);
            
            popupMenu.show(bacSiTable, x, y);
        }
    }
    
    private JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setForeground(textColor);
        menuItem.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        return menuItem;
    }
    
    private void showBacSiDetails(BacSi bacSi) {
        JDialog detailsDialog = new JDialog(parentFrame, "Chi Tiết Bác Sĩ", true);
        detailsDialog.setSize(500, 450);
        detailsDialog.setLocationRelativeTo(parentFrame);
        detailsDialog.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel nameLabel = new JLabel(bacSi.getHoTenBacSi());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Chuyên khoa: " + bacSi.getChuyenKhoa());
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel namePanelWrapper = new JPanel(new BorderLayout());
        namePanelWrapper.setBackground(primaryColor);
        namePanelWrapper.add(nameLabel, BorderLayout.NORTH);
        namePanelWrapper.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(namePanelWrapper, BorderLayout.CENTER);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);
        
        addDetailField(detailsPanel, "ID:", String.valueOf(bacSi.getIdBacSi()));
        addDetailField(detailsPanel, "Chuyên Khoa:", bacSi.getChuyenKhoa());
        addDetailField(detailsPanel, "Bằng Cấp:", bacSi.getBangCap());
        addDetailField(detailsPanel, "Kinh Nghiệm:", bacSi.getKinhNghiem() + " năm");
        addDetailField(detailsPanel, "Phòng Khám:", bacSi.getTenPhong());
        addDetailField(detailsPanel, "Email:", bacSi.getEmailNguoiDung());
        addDetailField(detailsPanel, "Số Điện Thoại:", bacSi.getSoDienThoaiNguoiDung());
        
        detailsPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton editButton = new JButton("Chỉnh Sửa");
        editButton.setFont(buttonFont);
        editButton.setForeground(buttonTextColor);
        editButton.setBackground(warningColor);
        editButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        editButton.addActionListener(e -> {
            detailsDialog.dispose();
            showEditBacSiDialog();
        });
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(buttonFont);
        closeButton.setForeground(textColor);
        closeButton.setBackground(Color.WHITE);
        closeButton.setBorder(new LineBorder(borderColor, 1));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
        // Add components to dialog
        detailsDialog.add(headerPanel, BorderLayout.NORTH);
        detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(15, 0));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(450, 40));
        fieldPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        fieldPanel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setPreferredSize(new Dimension(120, 30));
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(textColor);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(regularFont);
        valueComponent.setForeground(textColor);
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(valueComponent, BorderLayout.CENTER);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(450, 1));
        
        panel.add(fieldPanel);
        panel.add(separator);
    }
    
    public void loadBacSiData() {
        tableModel.setRowCount(0);
        List<BacSi> bacSiList = bacSiController.getAllBacSi();
        
        for (BacSi bacSi : bacSiList) {
            Object[] rowData = {
                bacSi.getIdBacSi(),
                bacSi.getHoTenBacSi(),
                bacSi.getChuyenKhoa(),
                bacSi.getBangCap(),
                bacSi.getKinhNghiem() + " năm",
                bacSi.getTenPhong(),
                bacSi.getEmailNguoiDung(),
                bacSi.getSoDienThoaiNguoiDung()
            };
            tableModel.addRow(rowData);
        }
        
        currentBacSiId = -1;
    }
    
    private void searchBacSi() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBacSiData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<BacSi> bacSiList = bacSiController.searchBacSi(searchTerm);
        
        for (BacSi bacSi : bacSiList) {
            Object[] rowData = {
                bacSi.getIdBacSi(),
                bacSi.getHoTenBacSi(),
                bacSi.getChuyenKhoa(),
                bacSi.getBangCap(),
                bacSi.getKinhNghiem() + " năm",
                bacSi.getTenPhong(),
                bacSi.getEmailNguoiDung(),
                bacSi.getSoDienThoaiNguoiDung()
            };
            tableModel.addRow(rowData);
        }
        
        currentBacSiId = -1;
    }
    
    private void showAddBacSiDialog() {
        BacSiDialog dialog = new BacSiDialog(parentFrame, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    public void focusOnDoctor(int doctorId) {
        try {
            // Use the correct table variable (bacSiTable instead of tableBacSi)
            int rowCount = bacSiTable.getRowCount();
            
            // Find the row containing the doctor with the corresponding ID
            for (int i = 0; i < rowCount; i++) {
                // The first column in the table contains the doctor ID
                int currentDoctorId = (Integer) bacSiTable.getValueAt(i, 0);
                
                if (currentDoctorId == doctorId) {
                    // Select the found row
                    bacSiTable.setRowSelectionInterval(i, i);
                    
                    // Scroll to the selected row
                    Rectangle rect = bacSiTable.getCellRect(i, 0, true);
                    bacSiTable.scrollRectToVisible(rect);
                    
                    // Optionally, we could add highlighting effects here
                    break;
                }
            }
            
            // If the doctor is not found in the current table data, we could show a message
            // or reload data from the database
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể hiển thị thông tin bác sĩ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showEditBacSiDialog() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Vui lòng chọn một bác sĩ để chỉnh sửa.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BacSi bacSi = bacSiController.getBacSiById(currentBacSiId);
        if (bacSi == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Không thể tìm thấy thông tin bác sĩ.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BacSiDialog dialog = new BacSiDialog(parentFrame, bacSi);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    
    public void deleteBacSi() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Vui lòng chọn một bác sĩ để xóa.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BacSi currentDoctor = bacSiController.getBacSiById(currentBacSiId);
        if (currentDoctor == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Không thể tìm thấy thông tin bác sĩ.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(parentFrame, 
            "Bạn có chắc chắn muốn xóa bác sĩ này?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = bacSiController.deleteBacSi(currentBacSiId);
            if (success) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Đã xóa bác sĩ thành công.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBacSiData();
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Không thể xóa bác sĩ. Bác sĩ này có thể có lịch hẹn hoặc điều trị hiện tại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportData() {
        // Implementation for exporting data to file
        JOptionPane.showMessageDialog(parentFrame, 
            "Chức năng xuất file sẽ được triển khai sau.", 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAppointments() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Vui lòng chọn một bác sĩ để xem lịch hẹn.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<model.LichHen> appointments = bacSiController.getFutureAppointments(currentBacSiId);
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Bác sĩ này không có lịch hẹn nào trong tương lai.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        AppointmentsDialog dialog = new AppointmentsDialog(parentFrame, appointments);
        dialog.setVisible(true);
    }
    
    private void showTreatments() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Vui lòng chọn một bác sĩ để xem điều trị.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<model.DieuTri> treatments = bacSiController.getDieuTriByBacSi(currentBacSiId);
        
        if (treatments.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Bác sĩ này không có điều trị nào.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        TreatmentsDialog dialog = new TreatmentsDialog(parentFrame, treatments);
        dialog.setVisible(true);
    }
    
    // Set parent frame reference
    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    // Custom rounded panel with shadow
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean shadowEnabled;
        
        public RoundedPanel(int radius, boolean shadow) {
            super();
            this.cornerRadius = radius;
            this.shadowEnabled = shadow;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow if enabled
            if (shadowEnabled) {
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 10 - i * 2));
                    g2d.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, cornerRadius, cornerRadius);
                }
            }
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
}