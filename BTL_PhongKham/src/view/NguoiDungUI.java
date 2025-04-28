package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import controller.NguoiDungController;
import model.NguoiDung;

public class NguoiDungUI extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, searchButton;
    private NguoiDungController controller;
    private JPopupMenu popupMenu;
    
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
    
    public NguoiDungUI() {
        controller = new NguoiDungController();
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        
        initializeComponents();
        loadUserData();
    }
    
    private void initializeComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        headerPanel.setBackground(panelColor);
        
        JLabel titleLabel = new JLabel("Quản lý Người Dùng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(panelColor);
        
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton, primaryColor);
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Họ Tên", "Email", "Số điện thoại", "Vai trò"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setGridColor(Color.BLACK);
        userTable.setShowVerticalLines(true);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setFillsViewportHeight(true);
        
        JTableHeader header = userTable.getTableHeader();
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(primaryColor));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        userTable.setSelectionBackground(secondaryColor);
        userTable.setSelectionForeground(Color.WHITE);
        
        createPopupMenu();
        
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = userTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < userTable.getRowCount()) {
                        userTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else if (e.getClickCount() == 2) {
                    int row = userTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < userTable.getRowCount()) {
                        userTable.setRowSelectionInterval(row, row);
                        showUserDetails();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        buttonPanel.setBackground(panelColor);
        
        addButton = new JButton("Thêm người dùng");
        styleButton(addButton, successColor);
        
        buttonPanel.add(addButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> showAddUserDialog());
        searchButton.addActionListener(e -> searchUsers());
    }
    
    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(borderColor));
        
        JMenuItem viewItem = new JMenuItem("Xem chi tiết");
        JMenuItem editItem = new JMenuItem("Chỉnh sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        
        Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);
        viewItem.setFont(menuFont);
        editItem.setFont(menuFont);
        deleteItem.setFont(menuFont);
        
        viewItem.setBackground(Color.WHITE);
        editItem.setBackground(Color.WHITE);
        deleteItem.setBackground(Color.WHITE);
        
        viewItem.setForeground(primaryColor);
        editItem.setForeground(warningColor);
        deleteItem.setForeground(accentColor);
        
        viewItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        editItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        deleteItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        viewItem.addActionListener(e -> showUserDetails());
        editItem.addActionListener(e -> showEditUserDialog());
        deleteItem.addActionListener(e -> deleteSelectedUser());
        
        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
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
                
                JPanel contentPane = new JPanel(new BorderLayout());
                contentPane.setBackground(panelColor);
                
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                detailsPanel.setBackground(panelColor);
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
                
                JLabel headerLabel = new JLabel("Thông tin chi tiết người dùng");
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                headerLabel.setForeground(primaryColor);
                headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JSeparator separator = new JSeparator();
                separator.setForeground(borderColor);
                separator.setAlignmentX(Component.LEFT_ALIGNMENT);
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                
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
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                
                JButton editButton = new JButton("Chỉnh sửa");
                JButton closeButton = new JButton("Đóng");
                
                styleButton(editButton, warningColor);
                styleButton(closeButton, primaryColor);
                
                buttonPanel.add(editButton);
                buttonPanel.add(closeButton);
                
                editButton.addActionListener(e -> {
                    dialog.dispose();
                    showEditUserDialog();
                });
                
                closeButton.addActionListener(e -> dialog.dispose());
                
                detailsPanel.add(headerLabel);
                detailsPanel.add(Box.createVerticalStrut(10));
                detailsPanel.add(separator);
                detailsPanel.add(Box.createVerticalStrut(15));
                detailsPanel.add(contentPanel);
                
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
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(buttonTextColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor, 0.1f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private Color darkenColor(Color color, float fraction) {
        int r = Math.max(0, Math.round(color.getRed() * (1 - fraction)));
        int g = Math.max(0, Math.round(color.getGreen() * (1 - fraction)));
        int b = Math.max(0, Math.round(color.getBlue() * (1 - fraction)));
        return new Color(r, g, b);
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
                
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");
                
                styleButton(saveButton, successColor);
                styleButton(cancelButton, primaryColor);
                
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                
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
    
    private JDialog createStyledDialog(String title, int width, int height) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        return dialog;
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items, String selectedItem) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
    
    public NguoiDungController getController() {
        return controller;
    }
    
    private void searchUsers() {
        String keyword = searchField.getText().trim();
        
        tableModel.setRowCount(0);
        
        try {
            java.util.List<NguoiDung> users;
            
            if (keyword.isEmpty()) {
                users = controller.getAllUsers();
            } else {
                users = controller.searchUsers(keyword);
            }
            
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
            
            if (users.isEmpty()) {
                showInfoMessage("Không tìm thấy người dùng nào phù hợp.");
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tìm kiếm người dùng: " + e.getMessage());
            e.printStackTrace();
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
        
        JDialog dialog = createStyledDialog("Thêm Người Dùng Mới", 450, 500);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(panelColor);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(successColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("Thêm người dùng mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        JTextField hoTenField = createStyledTextField("");
        JTextField emailField = createStyledTextField("");
        JTextField soDienThoaiField = createStyledTextField("");
        JPasswordField matKhauField = createStyledPasswordField();
        JPasswordField xacNhanMatKhauField = createStyledPasswordField();
        
        String defaultRole = availableRoles.contains("Khách hàng") ? "Khách hàng" : (roles.length > 0 ? roles[0] : "");
        JComboBox<String> vaiTroBox = createStyledComboBox(roles, defaultRole);
        
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
        
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        styleButton(saveButton, successColor);
        styleButton(cancelButton, primaryColor);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            if (hoTenField.getText().trim().isEmpty() ||                 
                emailField.getText().trim().isEmpty() ||
                soDienThoaiField.getText().trim().isEmpty() ||
                matKhauField.getPassword().length == 0 ||
                xacNhanMatKhauField.getPassword().length == 0) {
                showWarningMessage("Vui lòng điền đầy đủ thông tin.");
                return;
            }
            
            String password = new String(matKhauField.getPassword());
            String confirmPassword = new String(xacNhanMatKhauField.getPassword());
            
            if (!password.equals(confirmPassword)) {
                showErrorMessage("Mật khẩu và xác nhận mật khẩu không khớp.");
                return;
            }
            
            NguoiDung newUser = new NguoiDung();
            newUser.setHoTen(hoTenField.getText().trim());            
            newUser.setEmail(emailField.getText().trim());
            newUser.setSoDienThoai(soDienThoaiField.getText().trim());
            newUser.setMatKhau(password);
            newUser.setVaiTro((String) vaiTroBox.getSelectedItem());
            
            try {
                controller.addUser(newUser);
                showSuccessMessage("Thêm người dùng mới thành công!");
                dialog.dispose();
                loadUserData();
            } catch (SQLException ex) {
                showErrorMessage("Lỗi khi thêm người dùng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(contentPane);
        dialog.setVisible(true);
    }
    private void showSuccessMessage(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thành công", true);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panelColor);
        
        JButton okButton = new JButton("OK");
        styleButton(okButton, successColor);
        
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    private void showErrorMessage(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lỗi", true);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panelColor);
        
        JButton okButton = new JButton("OK");
        styleButton(okButton, accentColor);
        
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    private void showWarningMessage(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Cảnh báo", true);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panelColor);
        
        JButton okButton = new JButton("OK");
        styleButton(okButton, warningColor);
        
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    private void showInfoMessage(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông báo", true);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panelColor);
        
        JButton okButton = new JButton("OK");
        styleButton(okButton, primaryColor);
        
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}