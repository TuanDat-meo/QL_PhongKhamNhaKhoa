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
                    availableRoles.add("Nhân viên");
                    availableRoles.add("Khách hàng");
                }
                
                String[] roles = availableRoles.toArray(new String[0]);
                
                JDialog dialog = createStyledDialog("Sửa Thông Tin Người Dùng", 450, 450);
                
                JPanel contentPane = new JPanel(new BorderLayout());
                contentPane.setBackground(panelColor);
                
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(primaryColor);
                headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
                
                JLabel titleLabel = new JLabel("Sửa thông tin người dùng");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(Color.WHITE);
                
                headerPanel.add(titleLabel, BorderLayout.CENTER);
                
                JPanel formPanel = new JPanel();
                formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
                formPanel.setBackground(panelColor);
                formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
                
                JTextField hoTenField = createStyledTextField(user.getHoTen());
                JTextField emailField = createStyledTextField(user.getEmail());
                JTextField soDienThoaiField = createStyledTextField(user.getSoDienThoai());
                JPasswordField matKhauField = createStyledPasswordField();
                JComboBox<String> vaiTroBox = createStyledComboBox(roles, user.getVaiTro());
                
                formPanel.add(createFormRow("Họ tên:", hoTenField));
                formPanel.add(Box.createVerticalStrut(15));
                formPanel.add(createFormRow("Email:", emailField));
                formPanel.add(Box.createVerticalStrut(15));
                formPanel.add(createFormRow("Số điện thoại:", soDienThoaiField));
                formPanel.add(Box.createVerticalStrut(15));
                formPanel.add(createFormRow("Mật khẩu mới:", matKhauField));
                formPanel.add(Box.createVerticalStrut(15));
                formPanel.add(createFormRow("Vai trò:", vaiTroBox));
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                buttonPanel.setBackground(panelColor);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
                
                JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10, false);
                JButton cancelButton = createRoundedButton("Hủy", primaryColor, buttonTextColor, 10, false);
                
                saveButton.setPreferredSize(new Dimension(100, 40));
                cancelButton.setPreferredSize(new Dimension(100, 40));
                
                saveButton.addActionListener(e -> {
                    if (hoTenField.getText().trim().isEmpty() || 
                        emailField.getText().trim().isEmpty() ||
                        soDienThoaiField.getText().trim().isEmpty()) {
                        showWarningMessage("Vui lòng điền đầy đủ thông tin.");
                        return;
                    }
                    
                    user.setHoTen(hoTenField.getText().trim());                    
                    user.setEmail(emailField.getText().trim());
                    user.setSoDienThoai(soDienThoaiField.getText().trim());
                    user.setVaiTro((String) vaiTroBox.getSelectedItem());
                    
                    String newPassword = new String(matKhauField.getPassword());
                    if (!newPassword.isEmpty()) {
                        // Thêm kiểm tra mật khẩu mới nếu người dùng nhập
                        if (!isValidPassword(newPassword)) {
                            showErrorMessage("Mật khẩu mới không hợp lệ. Mật khẩu phải có ít nhất 8 ký tự, bao gồm:\n" +
                                           "- Ít nhất 1 chữ hoa\n" +
                                           "- Ít nhất 1 chữ thường\n" +
                                           "- Ít nhất 1 số\n" +
                                           "- Ít nhất 1 ký tự đặc biệt (!@#$%^&*()_+-=[]{}|;:,.<>?) ");
                            return;
                        }
                        user.setMatKhau(newPassword);
                    }
                    
                    try {
                        controller.updateUser(user);
                        showSuccessMessage("Cập nhật thông tin người dùng thành công!");
                        dialog.dispose();
                        loadUserData();
                    } catch (SQLException ex) {
                        showErrorMessage("Lỗi khi cập nhật thông tin: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                
                cancelButton.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                
                contentPane.add(headerPanel, BorderLayout.NORTH);
                contentPane.add(formPanel, BorderLayout.CENTER);
                contentPane.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.setContentPane(contentPane);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
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
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa người dùng '" + userName + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteUser(userId);
                showSuccessMessage("Xóa người dùng thành công!");
                loadUserData();
            } catch (SQLException e) {
                showErrorMessage("Lỗi khi xóa người dùng: " + e.getMessage());
                e.printStackTrace();
            }
        }
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
        java.util.List<String> availableRoles;
        try {
            availableRoles = controller.getAllRoles();
        } catch (SQLException e) {
            availableRoles = new java.util.ArrayList<>();
            availableRoles.add("Admin");
            availableRoles.add("Nhân viên");
            availableRoles.add("Khách hàng");
        }
        
        String[] roles = availableRoles.toArray(new String[0]);
        // Create a new array with "Lựa chọn" at the beginning
        String[] rolesWithDefault = new String[roles.length + 1];
        rolesWithDefault[0] = "Lựa chọn";
        System.arraycopy(roles, 0, rolesWithDefault, 1, roles.length);
        
        JDialog dialog = createStyledDialog("Thêm Người Dùng Mới", 450, 500);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(panelColor);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor); 
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("THÊM NGƯỜI DÙNG MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        JTextField hoTenField = createStyledTextField("");
        JTextField emailField = createStyledTextField("");
        JTextField soDienThoaiField = createStyledTextField("");
        JPasswordField matKhauField = createStyledPasswordField();
        JPasswordField xacNhanMatKhauField = createStyledPasswordField();
        
        JComboBox<String> vaiTroBox = createStyledComboBox(rolesWithDefault, "Lựa chọn");
        
        formPanel.add(createFormRow("Họ tên:", hoTenField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormRow("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormRow("Số điện thoại:", soDienThoaiField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormRow("Mật khẩu:", matKhauField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormRow("Xác nhận mật khẩu:", xacNhanMatKhauField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormRow("Vai trò:", vaiTroBox));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        JButton cancelButton = createRoundedButton("Hủy", Color.WHITE, textColor, 10, false);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10, true);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.addActionListener(e -> {
            String hoTen = hoTenField.getText().trim();
            String email = emailField.getText().trim();
            String phone = soDienThoaiField.getText().trim();
            String password = new String(matKhauField.getPassword());
            String confirmPassword = new String(xacNhanMatKhauField.getPassword());
            String selectedRole = (String) vaiTroBox.getSelectedItem();

            // Kiểm tra các trường bắt buộc
            if (hoTen.isEmpty() || email.isEmpty() || phone.isEmpty() || 
                password.isEmpty() || confirmPassword.isEmpty()) {
                showWarningMessage("Vui lòng điền đầy đủ thông tin.");
                return;
            }

            // Kiểm tra ràng buộc cho vai trò
            if ("Lựa chọn".equals(selectedRole)) {
                showWarningMessage("Vui lòng chọn một vai trò hợp lệ.");
                return;
            }

            // Kiểm tra định dạng email
            if (!isValidEmail(email)) {
                showErrorMessage("Email không hợp lệ. Vui lòng nhập email đúng định dạng (ví dụ: example@domain.com)");
                return;
            }

            // Kiểm tra định dạng số điện thoại
            if (!isValidPhoneNumber(phone)) {
                showErrorMessage("Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại đúng định dạng (ví dụ: 0123456789 hoặc 84123456789)");
                return;
            }

            // Kiểm tra mật khẩu
            if (!password.equals(confirmPassword)) {
                showErrorMessage("Mật khẩu và xác nhận mật khẩu không khớp.");
                return;
            }

            if (!isValidPassword(password)) {
                showErrorMessage("Mật khẩu phải có ít nhất 8 ký tự, bao gồm:\n" +
                               "- Ít nhất 1 chữ hoa\n" +
                               "- Ít nhất 1 chữ thường\n" +
                               "- Ít nhất 1 số\n" +
                               "- Ít nhất 1 ký tự đặc biệt (!@#$%^&*()_+-=[]{}|;:,.<>?)");
                return;
            }

            try {
                // Kiểm tra email đã tồn tại
                if (controller.isEmailExists(email)) {
                    showErrorMessage("Email này đã được sử dụng. Vui lòng sử dụng email khác.");
                    return;
                }

                // Tạo người dùng mới
                NguoiDung newUser = new NguoiDung();
                newUser.setHoTen(hoTen);
                newUser.setEmail(email);
                newUser.setSoDienThoai(phone);
                newUser.setMatKhau(password);
                newUser.setVaiTro(selectedRole);

                // Thêm người dùng vào database
                controller.addUser(newUser);
                showSuccessMessage("Thêm người dùng mới thành công!");
                dialog.dispose();
                loadUserData();
            } catch (SQLException ex) {
                showErrorMessage("Lỗi khi thêm người dùng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(contentPane);
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
}