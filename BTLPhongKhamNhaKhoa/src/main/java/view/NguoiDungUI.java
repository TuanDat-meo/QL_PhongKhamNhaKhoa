package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;

import controller.NguoiDungController;
import model.NguoiDung;
import util.DataChangeListener;
import util.ExportManager;
import util.RoundedPanel;
import util.ExportManager.MessageCallback;
import view.BenhNhanUI.NotificationType;

public class NguoiDungUI extends JPanel implements MessageCallback, DataChangeListener {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, searchButton, exportButton;
    private NguoiDungController controller;
    private JPopupMenu popupMenu;
    private JMenuItem viewItem, editMenuItem, deleteMenuItem;
    private ExportManager exportManager;
    // Updated color scheme
    private final Color primaryColor = new Color(79, 129, 189);      // Professional blue
    private final Color secondaryColor = new Color(141, 180, 226);   // Lighter blue
    private final Color accentColor = new Color(192, 80, 77);        // Refined red for delete
    private final Color successColor = new Color(86, 156, 104);      // Elegant green for add
    private final Color warningColor = new Color(237, 187, 85);      // Softer yellow for edit
    private final Color backgroundColor = new Color(248, 249, 250);  // Extremely light gray background
    private final Color textColor = new Color(33, 37, 41);           // Near-black text
    private final Color panelColor = new Color(255, 255, 255);       // White panels
    private final Color buttonTextColor = Color.WHITE;
    private final Color tableHeaderColor = new Color(79, 129, 189);  // Match primary color
    private final Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private final Color borderColor = new Color(222, 226, 230);
    
    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

    public NguoiDungUI() {
        controller = new NguoiDungController();
        exportManager = new ExportManager(this, tableModel, this);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
        loadUserData();
    }
    
    private void initializeComponents() {
        // Header Panel with Title and Search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        setupEventListeners();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        // Title panel on the left
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("QUẢN LÝ NGƯỜI DÙNG");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        // Search panel on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        searchField = new JTextField(18);
        searchField.setFont(regularFont);
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUsers();
                }
            }
        });
        searchButton = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10, false);
        searchButton.setPreferredSize(new Dimension(120, 38));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        // Create a panel with shadow effect
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create table model
        String[] columns = {"ID", "Họ Tên", "Email", "Số điện thoại", "Vai trò"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Center the content
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }
                
                // Add alternating row colors
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                return comp;
            }
        };
        
        // Set default cell renderer to center all content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Apply center renderer to all columns
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        userTable.setFont(tableFont);
        userTable.setRowHeight(40);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setSelectionBackground(new Color(229, 243, 255));
        userTable.setSelectionForeground(textColor);
        userTable.setFocusable(false);
        userTable.setAutoCreateRowSorter(true);
        userTable.setBorder(null);
        
        // Style table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        
        // Center the header text
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Set column widths
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Họ tên
        columnModel.getColumn(2).setPreferredWidth(150); // Email
        columnModel.getColumn(3).setPreferredWidth(120); // SĐT
        columnModel.getColumn(4).setPreferredWidth(100); // Vai trò
        
        setupPopupMenu();
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        
        return wrapperPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        exportButton = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10, false);
        exportButton.setPreferredSize(new Dimension(100, 45));
        exportButton.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        addButton = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10, false);
        addButton.setPreferredSize(new Dimension(100, 45));
        
        buttonPanel.add(exportButton);
        buttonPanel.add(addButton);
        
        return buttonPanel;
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(e -> searchUsers());
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUsers();
                }
            }
        });
        
        // Handle table mouse events for popup menu and double-click
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        showUserDetails();
                    }
                } else {
                    userTable.clearSelection();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        
        addButton.addActionListener(e -> showAddUserDialog());
    }
    
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        viewItem = createStyledMenuItem("Xem Chi Tiết");
        editMenuItem = createStyledMenuItem("Chỉnh Sửa");
        deleteMenuItem = createStyledMenuItem("Xóa");
        
        deleteMenuItem.setForeground(accentColor);
        
        popupMenu.add(viewItem);
        popupMenu.addSeparator();
        popupMenu.add(editMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(deleteMenuItem);
        
        viewItem.addActionListener(e -> {
            if (userTable.getSelectedRow() != -1) {
                showUserDetails();
            }
        });
        
        editMenuItem.addActionListener(e -> {
            if (userTable.getSelectedRow() != -1) {
                showEditUserDialog();
            }
        });
        
        deleteMenuItem.addActionListener(e -> {
            if (userTable.getSelectedRow() != -1) {
                deleteSelectedUser();
            }
        });
        
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // Make sure the row is selected when right-clicking
                int row = userTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(row, row);
                } else {
                    userTable.clearSelection();
                }
                
                showPopup(e);
            }
            
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger() && userTable.getSelectedRow() != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(panelColor);
        menuItem.setForeground(textColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return menuItem;
    }
    
    private void loadUserData() {
        tableModel.setRowCount(0);
        
        try {
            java.util.List<NguoiDung> users = controller.getAllUsers();
            
            for (NguoiDung user : users) {
                Object[] rowData = {
                    user.getIdNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai(),
                    user.getVaiTro()
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải dữ liệu người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showUserDetails() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showInfoMessage("Vui lòng chọn người dùng để xem chi tiết.");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            NguoiDung user = controller.getNguoiDungById(userId);
            
            if (user != null) {
                JDialog dialog = createStyledDialog("Chi Tiết Người Dùng", 500, 420);
                dialog.setLocationRelativeTo(this);
                
                JPanel contentPane = new JPanel(new BorderLayout());
                contentPane.setBackground(panelColor);
                
                // Header panel
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(primaryColor);
                headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
                
                JLabel titleLabel = new JLabel("Chi tiết người dùng");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(Color.WHITE);
                
                headerPanel.add(titleLabel, BorderLayout.CENTER);
                
                // Detail panel
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                detailsPanel.setBackground(panelColor);
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
                
                JPanel contentPanel = new JPanel(new GridLayout(0, 1, 0, 15));
                contentPanel.setBackground(panelColor);
                contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                
                contentPanel.add(createDetailRow("ID:", String.valueOf(user.getIdNguoiDung())));
                contentPanel.add(createDetailRow("Họ tên:", user.getHoTen()));
                contentPanel.add(createDetailRow("Email:", user.getEmail()));
                contentPanel.add(createDetailRow("Số điện thoại:", user.getSoDienThoai()));
                contentPanel.add(createDetailRow("Vai trò:", user.getVaiTro()));
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
                buttonPanel.setBackground(panelColor);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
                
                JButton editButton = createRoundedButton("Chỉnh sửa", warningColor, buttonTextColor, 10, false);
                JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10, false);
                
                editButton.setPreferredSize(new Dimension(120, 40));
                closeButton.setPreferredSize(new Dimension(120, 40));
                
                editButton.addActionListener(e -> {
                    dialog.dispose();
                    showEditUserDialog();
                });
                
                closeButton.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(editButton);
                buttonPanel.add(closeButton);
                
                detailsPanel.add(contentPanel);
                
                contentPane.add(headerPanel, BorderLayout.NORTH);
                contentPane.add(detailsPanel, BorderLayout.CENTER);
                contentPane.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.setContentPane(contentPane);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi lấy thông tin chi tiết người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(panelColor);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(textColor);
        labelComponent.setPreferredSize(new Dimension(140, 28));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(textColor);
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JDialog createStyledDialog(String title, int width, int height) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        return dialog;
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showInfoMessage("Vui lòng chọn người dùng để sửa.");
            return;
        }
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            NguoiDung user = controller.getNguoiDungById(userId);
            if (user != null) {
                java.util.List<String> availableRoles;
                try {
                    availableRoles = controller.getAllRoles();
                } catch (SQLException e) {
                    availableRoles = new java.util.ArrayList<>();
                    availableRoles.add("Admin");
                    availableRoles.add("Bác sĩ");
                    availableRoles.add("Lễ tân");
                    availableRoles.add("Kế toán");
                    availableRoles.add("Quản kho");
                    availableRoles.add("Nhân viên");
                    availableRoles.add("Khách hàng");
                }
                if (!availableRoles.contains("Kế toán")) availableRoles.add("Kế toán");
                if (!availableRoles.contains("Quản kho")) availableRoles.add("Quản kho");
                String[] roles = availableRoles.toArray(new String[0]);
                // Dialog lớn, có scroll
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Người Dùng", true);
                dialog.setSize(540, 650);
                dialog.setLocationRelativeTo(this);
                dialog.setResizable(false);
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.setBackground(Color.WHITE);
                JPanel headerPanel = new JPanel();
                headerPanel.setBackground(primaryColor);
                headerPanel.setLayout(new BorderLayout());
                headerPanel.setPreferredSize(new Dimension(0, 70));
                headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
                JLabel titleLabel = new JLabel("CHỈNH SỬA NGƯỜI DÙNG");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                headerPanel.add(titleLabel, BorderLayout.CENTER);
                mainPanel.add(headerPanel, BorderLayout.NORTH);
                JPanel formPanel = new JPanel();
                formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
                formPanel.setBackground(Color.WHITE);
                formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
                // Input fields
                JTextField hoTenField = createStyledTextField(user.getHoTen());
                JTextField emailField = createStyledTextField(user.getEmail());
                JTextField soDienThoaiField = createStyledTextField(user.getSoDienThoai());
                // Ngày sinh dùng JDateChooser
                com.toedter.calendar.JDateChooser ngaySinhChooser = new com.toedter.calendar.JDateChooser();
                ngaySinhChooser.setDateFormatString("yyyy-MM-dd");
                if (user.getNgaySinh() != null) ngaySinhChooser.setDate(user.getNgaySinh());
                // Giới tính
                String[] gioiTinhArr = {"Nam", "Nữ", "Khác"};
                JComboBox<String> gioiTinhBox = new JComboBox<>(gioiTinhArr);
                if (user.getGioiTinh() != null) gioiTinhBox.setSelectedItem(user.getGioiTinh());
                // Vai trò
                JComboBox<String> vaiTroBox = new JComboBox<>(roles);
                vaiTroBox.setSelectedItem(user.getVaiTro());
                // Mật khẩu mới
                JPasswordField matKhauField = createStyledPasswordField();
                JPasswordField xacNhanMatKhauField = createStyledPasswordField();
                // Label lỗi
                JLabel hoTenError = createErrorLabel();
                JLabel emailError = createErrorLabel();
                JLabel phoneError = createErrorLabel();
                JLabel ngaySinhError = createErrorLabel();
                JLabel gioiTinhError = createErrorLabel();
                JLabel matKhauError = createErrorLabel();
                JLabel xacNhanMatKhauError = createErrorLabel();
                JLabel vaiTroError = createErrorLabel();
                // Add fields
                formPanel.add(createFormRow("Họ tên:", hoTenField));
                formPanel.add(hoTenError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Email:", emailField));
                formPanel.add(emailError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Số điện thoại:", soDienThoaiField));
                formPanel.add(phoneError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Ngày sinh:", ngaySinhChooser));
                formPanel.add(ngaySinhError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Giới tính:", gioiTinhBox));
                formPanel.add(gioiTinhError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Vai trò:", vaiTroBox));
                formPanel.add(vaiTroError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Mật khẩu mới:", matKhauField));
                formPanel.add(matKhauError);
                formPanel.add(Box.createVerticalStrut(12));
                formPanel.add(createFormRow("Xác nhận mật khẩu:", xacNhanMatKhauField));
                formPanel.add(xacNhanMatKhauError);
                formPanel.add(Box.createVerticalStrut(18));
                JScrollPane scrollPane = new JScrollPane(formPanel);
                scrollPane.setBorder(null);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                // Button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.setBorder(new EmptyBorder(10, 30, 20, 30));
                JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10, false);
                JButton cancelButton = createRoundedButton("Hủy", primaryColor, buttonTextColor, 10, false);
                saveButton.setPreferredSize(new Dimension(100, 40));
                cancelButton.setPreferredSize(new Dimension(100, 40));
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setContentPane(mainPanel);
                // Sự kiện nút Lưu
                saveButton.addActionListener(e -> {
                    // Reset lỗi
                    hoTenError.setText(" "); emailError.setText(" "); phoneError.setText(" ");
                    ngaySinhError.setText(" "); gioiTinhError.setText(" "); matKhauError.setText(" ");
                    xacNhanMatKhauError.setText(" "); vaiTroError.setText(" ");
                    String hoTen = hoTenField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = soDienThoaiField.getText().trim();
                    String gioiTinh = (String) gioiTinhBox.getSelectedItem();
                    String selectedRole = (String) vaiTroBox.getSelectedItem();
                    java.util.Date ngaySinhDate = ngaySinhChooser.getDate();
                    String password = new String(matKhauField.getPassword());
                    String confirmPassword = new String(xacNhanMatKhauField.getPassword());
                    boolean isValid = true;
                    if (hoTen.isEmpty()) { hoTenError.setText("Họ tên không được để trống"); isValid = false; }
                    if (email.isEmpty()) { emailError.setText("Email không được để trống"); isValid = false; }
                    else if (!isValidEmail(email)) { emailError.setText("Email không hợp lệ"); isValid = false; }
                    if (phone.isEmpty()) { phoneError.setText("Số điện thoại không được để trống"); isValid = false; }
                    else if (!isValidPhoneNumber(phone)) { phoneError.setText("Số điện thoại không hợp lệ"); isValid = false; }
                    if (ngaySinhDate == null) { ngaySinhError.setText("Vui lòng chọn ngày sinh"); isValid = false; }
                    else if (ngaySinhDate.after(new java.util.Date())) { ngaySinhError.setText("Ngày sinh không được lớn hơn hôm nay"); isValid = false; }
                    if (selectedRole == null || selectedRole.isEmpty()) { vaiTroError.setText("Vui lòng chọn vai trò"); isValid = false; }
                    // Nếu nhập mật khẩu mới thì kiểm tra
                    if (!password.isEmpty() || !confirmPassword.isEmpty()) {
                        if (!password.equals(confirmPassword)) { xacNhanMatKhauError.setText("Mật khẩu xác nhận không khớp"); isValid = false; }
                        else if (!isValidPassword(password)) { matKhauError.setText("Mật khẩu phải đủ mạnh"); isValid = false; }
                    }
                    // Kiểm tra email/sđt trùng với user khác
                    try {
                        if (!email.equals(user.getEmail()) && controller.isEmailExists(email)) {
                            emailError.setText("Email này đã được sử dụng"); isValid = false;
                        }
                        if (!phone.equals(user.getSoDienThoai()) && controller.isPhoneExists(phone)) {
                            phoneError.setText("Số điện thoại này đã được sử dụng"); isValid = false;
                        }
                    } catch (Exception ex) {
                        showErrorMessage("Lỗi kiểm tra email/số điện thoại: " + ex.getMessage());
                        isValid = false;
                    }
                    if (!isValid) return;
                    try {
                        user.setHoTen(hoTen);
                        user.setEmail(email);
                        user.setSoDienThoai(phone);
                        user.setGioiTinh(gioiTinh);
                        user.setVaiTro(selectedRole);
                        user.setNgaySinh(ngaySinhDate != null ? new java.sql.Date(ngaySinhDate.getTime()) : null);
                        if (!password.isEmpty()) user.setMatKhau(password); // chỉ cập nhật nếu nhập mới
                        else user.setMatKhau(null); // không đổi mật khẩu
                        controller.updateUser(user);
                        showSuccessMessage("Cập nhật thông tin người dùng thành công!");
                        dialog.dispose();
                        loadUserData();
                    } catch (Exception ex) {
                        showErrorMessage("Lỗi khi cập nhật thông tin: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                cancelButton.addActionListener(e -> dialog.dispose());
                dialog.setVisible(true);
            }
        } catch (Exception ex) {
            showErrorMessage("Lỗi khi lấy thông tin người dùng: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius, boolean isFilled) {
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
    
    private JPanel createFormRow(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setBackground(panelColor);
        
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel.setForeground(textColor);
        jLabel.setPreferredSize(new Dimension(140, 30));
        
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(regularFont);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(regularFont);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items, String selectedItem) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(regularFont);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor));
        comboBox.setSelectedItem(selectedItem);
        
        return comboBox;
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showInfoMessage("Vui lòng chọn người dùng để xóa.");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        
        JDialog confirmDialog = new JDialog();
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setModal(true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa người dùng <b>" + userName + "</b>?</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        panel.add(messagePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8, false);
        cancelButton.addActionListener(e -> confirmDialog.dispose());

        JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8, false);
        deleteButton.addActionListener(e -> {
            try {
                controller.deleteUser(userId);
                confirmDialog.dispose();
                SwingUtilities.invokeLater(() -> {
                    loadUserData();
                });
            } catch (SQLException ex) {
                showErrorMessage("Lỗi khi xóa người dùng", ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        confirmDialog.setContentPane(panel);
        confirmDialog.setVisible(true);
    }
    
    private void searchUsers() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            // If search text is empty, we'll just reload all data instead of showing a message
            loadUserData();
            showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            return;
        }
        
        try {
            java.util.List<NguoiDung> users = controller.searchUsers(keyword);
            
            // Clear the table before adding new results
            tableModel.setRowCount(0);
            
            // Add search results to the table
            for (NguoiDung user : users) {
                Object[] rowData = {
                    user.getIdNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai(),
                    user.getVaiTro()
                };
                tableModel.addRow(rowData);
            }
            
            // Show notification based on search results
            if (users.isEmpty()) {
                showNotification("Không tìm thấy người dùng nào phù hợp với từ khóa: " + keyword, 
                                 NotificationType.WARNING);
            } else {
                showNotification("Tìm thấy " + users.size() + " kết quả phù hợp!", 
                                 NotificationType.SUCCESS);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tìm kiếm người dùng: " + e.getMessage());
            e.printStackTrace();
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

        // Position at bottom right
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
            screenSize.width - toastDialog.getWidth() - 20,
            screenSize.height - toastDialog.getHeight() - 60
        );

        toastDialog.setVisible(true);

        // Auto-hide after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                toastDialog.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public enum NotificationType {
        SUCCESS(new Color(86, 156, 104), "Thành công"),
        WARNING(new Color(237, 187, 85), "Cảnh báo"),
        ERROR(new Color(192, 80, 77), "Lỗi"),
        INFO(new Color(79, 129, 189), "Thông báo");
        
        private final Color color;
        private final String title;
        
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Người Dùng Mới", true);
        dialog.setSize(540, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        JLabel titleLabel = new JLabel("THÊM NGƯỜI DÙNG MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.weightx = 1.0;
        Color requiredFieldColor = new Color(255, 0, 0);
        // Họ tên
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblHoTen = new JLabel("Họ tên: ");
        lblHoTen.setFont(regularFont);
        JPanel hoTenLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hoTenLabelPanel.setBackground(Color.WHITE);
        hoTenLabelPanel.add(lblHoTen);
        JLabel starHoTen = new JLabel("*");
        starHoTen.setForeground(requiredFieldColor);
        starHoTen.setFont(regularFont);
        hoTenLabelPanel.add(starHoTen);
        formPanel.add(hoTenLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JTextField hoTenField = createStyledTextField("");
        hoTenField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(hoTenField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel hoTenError = createErrorLabel();
        formPanel.add(hoTenError, gbc);
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblNgaySinh = new JLabel("Ngày sinh: ");
        lblNgaySinh.setFont(regularFont);
        JPanel ngaySinhLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ngaySinhLabelPanel.setBackground(Color.WHITE);
        ngaySinhLabelPanel.add(lblNgaySinh);
        JLabel starNgaySinh = new JLabel("*");
        starNgaySinh.setForeground(requiredFieldColor);
        starNgaySinh.setFont(regularFont);
        ngaySinhLabelPanel.add(starNgaySinh);
        formPanel.add(ngaySinhLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JDateChooser dateChooserNgaySinh = new JDateChooser();
        dateChooserNgaySinh.setFont(regularFont);
        dateChooserNgaySinh.setPreferredSize(new Dimension(230, 32));
        dateChooserNgaySinh.setDateFormatString("dd/MM/yyyy");
        formPanel.add(dateChooserNgaySinh, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel ngaySinhError = createErrorLabel();
        formPanel.add(ngaySinhError, gbc);
        // Giới tính
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblGioiTinh = new JLabel("Giới tính: ");
        lblGioiTinh.setFont(regularFont);
        JPanel gioiTinhLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gioiTinhLabelPanel.setBackground(Color.WHITE);
        gioiTinhLabelPanel.add(lblGioiTinh);
        formPanel.add(gioiTinhLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        String[] genders = {"Nam", "Nữ", "Khác"};
        JComboBox<String> gioiTinhBox = new JComboBox<>(genders);
        gioiTinhBox.setFont(regularFont);
        gioiTinhBox.setPreferredSize(new Dimension(230, 32));
        formPanel.add(gioiTinhBox, gbc);
        gbc.gridx = 1; gbc.gridy++;
        formPanel.add(Box.createVerticalStrut(10), gbc);
        // Email
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblEmail = new JLabel("Email: ");
        lblEmail.setFont(regularFont);
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailLabelPanel.setBackground(Color.WHITE);
        emailLabelPanel.add(lblEmail);
        JLabel starEmail = new JLabel("*");
        starEmail.setForeground(requiredFieldColor);
        starEmail.setFont(regularFont);
        emailLabelPanel.add(starEmail);
        formPanel.add(emailLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JTextField emailField = createStyledTextField("");
        emailField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(emailField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel emailError = createErrorLabel();
        formPanel.add(emailError, gbc);
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblSoDienThoai = new JLabel("Số điện thoại: ");
        lblSoDienThoai.setFont(regularFont);
        JPanel soDienThoaiLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        soDienThoaiLabelPanel.setBackground(Color.WHITE);
        soDienThoaiLabelPanel.add(lblSoDienThoai);
        JLabel starSoDienThoai = new JLabel("*");
        starSoDienThoai.setForeground(requiredFieldColor);
        starSoDienThoai.setFont(regularFont);
        soDienThoaiLabelPanel.add(starSoDienThoai);
        formPanel.add(soDienThoaiLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JTextField soDienThoaiField = createStyledTextField("");
        soDienThoaiField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(soDienThoaiField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel phoneError = createErrorLabel();
        formPanel.add(phoneError, gbc);
        // Mật khẩu
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblMatKhau = new JLabel("Mật khẩu: ");
        lblMatKhau.setFont(regularFont);
        JPanel matKhauLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        matKhauLabelPanel.setBackground(Color.WHITE);
        matKhauLabelPanel.add(lblMatKhau);
        JLabel starMatKhau = new JLabel("*");
        starMatKhau.setForeground(requiredFieldColor);
        starMatKhau.setFont(regularFont);
        matKhauLabelPanel.add(starMatKhau);
        formPanel.add(matKhauLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JPasswordField matKhauField = createStyledPasswordField();
        matKhauField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(matKhauField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel matKhauError = createErrorLabel();
        formPanel.add(matKhauError, gbc);
        // Xác nhận mật khẩu
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblXacNhanMatKhau = new JLabel("Xác nhận mật khẩu: ");
        lblXacNhanMatKhau.setFont(regularFont);
        JPanel xacNhanMatKhauLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        xacNhanMatKhauLabelPanel.setBackground(Color.WHITE);
        xacNhanMatKhauLabelPanel.add(lblXacNhanMatKhau);
        JLabel starXacNhan = new JLabel("*");
        starXacNhan.setForeground(requiredFieldColor);
        starXacNhan.setFont(regularFont);
        xacNhanMatKhauLabelPanel.add(starXacNhan);
        formPanel.add(xacNhanMatKhauLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        JPasswordField xacNhanMatKhauField = createStyledPasswordField();
        xacNhanMatKhauField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(xacNhanMatKhauField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel xacNhanMatKhauError = createErrorLabel();
        formPanel.add(xacNhanMatKhauError, gbc);
        // Vai trò
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblVaiTro = new JLabel("Vai trò: ");
        lblVaiTro.setFont(regularFont);
        JPanel vaiTroLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        vaiTroLabelPanel.setBackground(Color.WHITE);
        vaiTroLabelPanel.add(lblVaiTro);
        JLabel starVaiTro = new JLabel("*");
        starVaiTro.setForeground(requiredFieldColor);
        starVaiTro.setFont(regularFont);
        vaiTroLabelPanel.add(starVaiTro);
        formPanel.add(vaiTroLabelPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        java.util.List<String> availableRoles;
        try {
            availableRoles = controller.getAllRoles();
        } catch (SQLException e) {
            availableRoles = new java.util.ArrayList<>();
            availableRoles.add("Admin");
            availableRoles.add("Bác sĩ");
            availableRoles.add("Lễ tân");
            availableRoles.add("Kế toán");
            availableRoles.add("Quản kho");
            availableRoles.add("Nhân viên");
            availableRoles.add("Khách hàng");
        }
        if (!availableRoles.contains("Kế toán")) availableRoles.add("Kế toán");
        if (!availableRoles.contains("Quản kho")) availableRoles.add("Quản kho");
        String[] roles = availableRoles.toArray(new String[0]);
        JComboBox<String> vaiTroBox = new JComboBox<>(roles);
        vaiTroBox.setFont(regularFont);
        vaiTroBox.setPreferredSize(new Dimension(230, 32));
        formPanel.add(vaiTroBox, gbc);
        gbc.gridx = 1; gbc.gridy++;
        JLabel vaiTroError = createErrorLabel();
        formPanel.add(vaiTroError, gbc);
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        Dimension buttonSize = new Dimension(90, 36);
        JButton btnLuu = createRoundedButton("Lưu", successColor, buttonTextColor, 10, false);
        btnLuu.setPreferredSize(buttonSize);
        btnLuu.setMinimumSize(buttonSize);
        btnLuu.setMaximumSize(buttonSize);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        JButton btnHuy = createRoundedButton("Hủy", accentColor, buttonTextColor, 10, false);
        btnHuy.setBorder(new LineBorder(borderColor, 1));
        btnHuy.setPreferredSize(buttonSize);
        btnHuy.setMinimumSize(buttonSize);
        btnHuy.setMaximumSize(buttonSize);
        btnHuy.setFocusPainted(false);
        btnHuy.setBorderPainted(false);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        // Bọc formPanel trong JScrollPane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setContentPane(mainPanel);
        // Xử lý sự kiện nút Lưu
        btnLuu.addActionListener(e -> {
            // Reset lỗi
            hoTenError.setText(" "); emailError.setText(" "); phoneError.setText(" ");
            ngaySinhError.setText(" "); matKhauError.setText(" "); xacNhanMatKhauError.setText(" "); vaiTroError.setText(" ");
            String hoTen = hoTenField.getText().trim();
            String email = emailField.getText().trim();
            String phone = soDienThoaiField.getText().trim();
            String password = new String(matKhauField.getPassword());
            String confirmPassword = new String(xacNhanMatKhauField.getPassword());
            String selectedRole = (String) vaiTroBox.getSelectedItem();
            java.util.Date ngaySinhDate = dateChooserNgaySinh.getDate();
            String gioiTinh = (String) gioiTinhBox.getSelectedItem();
            boolean isValid = true;
            if (hoTen.isEmpty()) { hoTenError.setText("Họ tên không được để trống"); isValid = false; }
            if (ngaySinhDate == null) { ngaySinhError.setText("Ngày sinh không được để trống"); isValid = false; }
            else if (ngaySinhDate.after(new java.util.Date())) { ngaySinhError.setText("Ngày sinh không được lớn hơn hôm nay"); isValid = false; }
            if (email.isEmpty()) { emailError.setText("Email không được để trống"); isValid = false; }
            else if (!isValidEmail(email)) { emailError.setText("Email không hợp lệ"); isValid = false; }
            if (phone.isEmpty()) { phoneError.setText("Số điện thoại không được để trống"); isValid = false; }
            else if (!isValidPhoneNumber(phone)) { phoneError.setText("Số điện thoại không hợp lệ"); isValid = false; }
            if (password.isEmpty()) { matKhauError.setText("Mật khẩu không được để trống"); isValid = false; }
            else if (!isValidPassword(password)) { matKhauError.setText("Mật khẩu phải đủ mạnh"); isValid = false; }
            if (!password.equals(confirmPassword)) { xacNhanMatKhauError.setText("Mật khẩu xác nhận không khớp"); isValid = false; }
            if (selectedRole == null || selectedRole.isEmpty()) { vaiTroError.setText("Vui lòng chọn vai trò"); isValid = false; }
            if (!isValid) return;
            try {
                NguoiDung newUser = new NguoiDung();
                newUser.setHoTen(hoTen);
                newUser.setEmail(email);
                newUser.setSoDienThoai(phone);
                newUser.setGioiTinh(gioiTinh);
                newUser.setVaiTro(selectedRole);
                newUser.setNgaySinh(ngaySinhDate != null ? new java.sql.Date(ngaySinhDate.getTime()) : null);
                if (!password.isEmpty()) newUser.setMatKhau(password);
                else newUser.setMatKhau(null);
                controller.addUser(newUser);
                showSuccessMessage("Thêm người dùng mới thành công!");
                dialog.dispose();
                loadUserData();
            } catch (Exception ex) {
                showErrorMessage("Lỗi khi thêm người dùng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnHuy.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // Add validation methods
    private boolean isValidEmail(String email) {
        // Kiểm tra email có đúng định dạng và không chứa ký tự đặc biệt không hợp lệ
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        // Kiểm tra số điện thoại Việt Nam (bắt đầu bằng 0 hoặc 84, theo sau là 9 số)
        String phoneRegex = "^(0|84)([0-9]{9})$";
        return phone != null && phone.matches(phoneRegex);
    }

    private boolean isValidPassword(String password) {
        // Kiểm tra mật khẩu có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else if ("!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) >= 0) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasNumber && hasSpecial;
    }
    
    private void showSuccessMessage(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }
    
    private void showErrorMessage(String message) {
        showNotification(message, NotificationType.ERROR);
    }
    
    private void showWarningMessage(String message) {
        showNotification(message, NotificationType.WARNING);
    }
    
    private void showInfoMessage(String message) {
        showNotification(message, NotificationType.INFO);
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    }
    
    // Method to get table model for export
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    // Method to get table headers for export
    public String[] getTableHeaders() {
        String[] headers = new String[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            headers[i] = tableModel.getColumnName(i);
        }
        return headers;
    }
    
    // Method to get table data for export
    public Object[][] getTableData() {
        Object[][] data = new Object[tableModel.getRowCount()][tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                data[i][j] = tableModel.getValueAt(i, j);
            }
        }
        return data;
    }
    
    // Method to refresh table data
    public void refreshData() {
        loadUserData();
        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
    }

	@Override
	public void onDataChanged() {
		// TODO Auto-generated method stub
		
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

    // Thêm class CustomBorder vào cuối file để bo góc cho ô input tìm kiếm
    private class CustomBorder extends LineBorder {
        private int radius;
        public CustomBorder(int radius, Color color) {
            super(color);
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(lineColor);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }

    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setVisible(true);
        errorLabel.setPreferredSize(new Dimension(230, 16));
        errorLabel.setMinimumSize(new Dimension(230, 16));
        return errorLabel;
    }
}