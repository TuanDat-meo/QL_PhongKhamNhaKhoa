package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser; // Import JDateChooser

import controller.BenhNhanController;
import model.BenhNhan;
import util.ExportManager;
import util.RoundedPanel;
import util.ValidationUtils;
import view.DoanhThuUI.NotificationType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BenhNhanUI extends JPanel implements ExportManager.MessageCallback {
    private BenhNhanController qlBenhNhan;
    private JTable tableBenhNhan;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen, txtSoDienThoai, txtCccd, txtDiaChi;
    private JDateChooser dateChooserNgaySinh; // Replace JTextField with JDateChooser
    private JComboBox<String> cbGioiTinh;
    private JDialog inputDialog;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet, menuItemSuaBenhNhan, menuItemXoaBenhNhan;
    private JButton btnThem, btnXoa, btnTimKiem;
    private JTextField txtTimKiem;
    private JButton btnXuatFile;
    private ExportManager exportManager;
    // Modern Theme Colors
    private Color primaryColor = new Color(79, 129, 189); // Professional blue
    private Color secondaryColor = new Color(141, 180, 226); // Lighter blue
    private Color accentColor = new Color(192, 80, 77); // Refined red for delete
    private Color successColor = new Color(86, 156, 104); // Elegant green for add
    private Color warningColor = new Color(237, 187, 85); // Softer yellow for edit
    private Color backgroundColor = new Color(248, 249, 250); // Extremely light gray background
    private Color textColor = new Color(33, 37, 41); // Near-black text
    private Color panelColor = new Color(255, 255, 255); // White panels
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189); // Match primary color
    private Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private Color borderColor = new Color(222, 226, 230); // Light gray borders

    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

    public BenhNhanUI() {
        qlBenhNhan = new BenhNhanController();
        initialize();
        exportManager = new ExportManager(this, tableModel, this);
    }

    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);

        // Header Panel with Title and Search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table Panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        setupEventListeners();
        // Create input dialog
        createInputDialog();

        // Load data
        loadDanhSachBenhNhan();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title Panel with icon and text
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ BỆNH NHÂN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Search Panel with rounded styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        
        // Create rounded border with padding
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                
        txtTimKiem.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemBenhNhan();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        btnTimKiem.addActionListener(e -> timKiemBenhNhan());

        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

 // Replace the existing createTablePanel() method with this updated version
    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        // Create a panel with shadow effect
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create table model and table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Họ tên");
        tableModel.addColumn("Ngày sinh");
        tableModel.addColumn("Giới tính");
        tableModel.addColumn("Số điện thoại");
        tableModel.addColumn("CCCD");
        tableModel.addColumn("Địa chỉ");

        tableBenhNhan = new JTable(tableModel) {
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
        for (int i = 0; i < tableBenhNhan.getColumnCount(); i++) {
            tableBenhNhan.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tableBenhNhan.setFont(tableFont);
        tableBenhNhan.setRowHeight(40);
        tableBenhNhan.setShowGrid(false);
        tableBenhNhan.setIntercellSpacing(new Dimension(0, 0));
        tableBenhNhan.setSelectionBackground(new Color(229, 243, 255));
        tableBenhNhan.setSelectionForeground(textColor);
        tableBenhNhan.setFocusable(false);
        tableBenhNhan.setAutoCreateRowSorter(true);
        tableBenhNhan.setBorder(null);

        // Style table header
        JTableHeader header = tableBenhNhan.getTableHeader();
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
        TableColumnModel columnModel = tableBenhNhan.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // ID column narrow
        columnModel.getColumn(1).setPreferredWidth(150); // Họ tên column wider
        columnModel.getColumn(2).setPreferredWidth(100); // Ngày sinh
        columnModel.getColumn(3).setPreferredWidth(80); // Giới tính
        columnModel.getColumn(4).setPreferredWidth(120); // Số điện thoại
        columnModel.getColumn(5).setPreferredWidth(120); // CCCD
        columnModel.getColumn(6).setPreferredWidth(200); // Địa chỉ wider

        setupPopupMenu();

        JScrollPane scrollPane = new JScrollPane(tableBenhNhan);
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
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> showInputDialog(true));
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);

        return buttonPanel;
    }

    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSuaBenhNhan = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoaBenhNhan = createStyledMenuItem("Xóa");
        
        menuItemXoaBenhNhan.setForeground(accentColor);
        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemSuaBenhNhan);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoaBenhNhan);

        menuItemXemChiTiet.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                xemChiTietBenhNhan();
            }
        });

        menuItemSuaBenhNhan.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                showInputDialog(false); // false means we're updating, not adding
            }
        });
        
        menuItemXoaBenhNhan.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                xoaBenhNhan();
            }
        });
        
        // Add mouse listener to table for right-click popup menu
        tableBenhNhan.addMouseListener(new MouseAdapter() {
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
            
            private void showPopupMenu(MouseEvent e) {
                int row = tableBenhNhan.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableBenhNhan.getRowCount()) {
                    tableBenhNhan.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    private void setupEventListeners() {
        // Setup search button action
        btnTimKiem.addActionListener(e -> {
            // Check if search field is empty
            if (txtTimKiem.getText().trim().isEmpty()) {
                // If empty, refresh the data
                loadDanhSachBenhNhan();
                showSuccessToast("Dữ liệu đã được làm mới!");
            } else {
                // If not empty, filter the data
                timKiemBenhNhan();
            }
        });
        
        // Setup enter key on search field
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        // If empty, refresh the data
                        loadDanhSachBenhNhan();
                        showSuccessToast("Dữ liệu đã được làm mới!");
                    } else {
                        // If not empty, filter the data
                        timKiemBenhNhan();
                    }
                }
            }
        });
        
        // Setup table mouse listener for double-click action and context menu
        tableBenhNhan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tableBenhNhan.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableBenhNhan.getRowCount()) {
                    tableBenhNhan.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTietBenhNhan();
                    }
                } else {
                    tableBenhNhan.clearSelection();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    // Add this method to create styled menu items
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setBackground(Color.WHITE);
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(new Color(240, 240, 240));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        
        return menuItem;
    }
    private void xemChiTietBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn bệnh nhân để xem chi tiết.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String hoTen = (String) tableModel.getValueAt(selectedRow, 1);
        String ngaySinh = (String) tableModel.getValueAt(selectedRow, 2);
        String gioiTinh = (String) tableModel.getValueAt(selectedRow, 3);
        String soDienThoai = (String) tableModel.getValueAt(selectedRow, 4);
        String cccd = (String) tableModel.getValueAt(selectedRow, 5);
        String diaChi = (String) tableModel.getValueAt(selectedRow, 6);

        // Create a custom styled dialog for details
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Chi tiết bệnh nhân");
        detailsDialog.setModal(true);
        detailsDialog.setSize(400, 450);
        detailsDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with patient name
        JLabel headerLabel = new JLabel(hoTen);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(primaryColor);
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Add details
        addDetailRow(detailsPanel, "ID:", String.valueOf(id));
        addDetailRow(detailsPanel, "Họ tên:", hoTen);
        addDetailRow(detailsPanel, "Ngày sinh:", ngaySinh);
        addDetailRow(detailsPanel, "Giới tính:", gioiTinh);
        addDetailRow(detailsPanel, "Số điện thoại:", soDienThoai);
        addDetailRow(detailsPanel, "CCCD:", cccd);
        addDetailRow(detailsPanel, "Địa chỉ:", diaChi);
        
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton closeButton = createRoundedButton("Đóng", primaryColor, Color.WHITE, 10);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setContentPane(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(textColor);
        labelComp.setPreferredSize(new Dimension(120, 25));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(textColor);
        
        rowPanel.add(labelComp, BorderLayout.WEST);
        rowPanel.add(valueComp, BorderLayout.CENTER);
        
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(5)); // Spacing between rows
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
    private void createInputDialog() {
        inputDialog = new JDialog();
        inputDialog.setTitle("Thông tin bệnh nhân");
        inputDialog.setModal(true);
        inputDialog.setSize(520, 550);
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setResizable(false);

        JPanel mainPanel = new RoundedPanel(0, false);
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainPanel.setBackground(backgroundColor);

        // Title panel with gradient background
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, primaryColor, getWidth(), 0, secondaryColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setPreferredSize(new Dimension(0, 70));
        
        JLabel titleLabel = new JLabel("THÔNG TIN BỆNH NHÂN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(backgroundColor);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JPanel formPanel = new RoundedPanel(15, true);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.weightx = 1.0;

        addFormField(formPanel, gbc, "Họ tên:", txtHoTen = createStyledTextField(), true);

        dateChooserNgaySinh = createStyledDateChooser();
        addFormField(formPanel, gbc, "Ngày sinh:", dateChooserNgaySinh, true);

        String[] genders = {"Nam", "Nữ", "Khác"};
        cbGioiTinh = new JComboBox<>(genders);
        cbGioiTinh.setFont(regularFont);
        cbGioiTinh.setBackground(Color.WHITE);
        cbGioiTinh.setPreferredSize(new Dimension(200, 40));
        cbGioiTinh.setBorder(new CompoundBorder(
                new CustomBorder(8, borderColor),
                new EmptyBorder(5, 10, 5, 10)));
        cbGioiTinh.setFocusable(false);
        addFormField(formPanel, gbc, "Giới tính:", cbGioiTinh, true);

        // Số điện thoại field
        addFormField(formPanel, gbc, "Số điện thoại:", txtSoDienThoai = createStyledTextField(), false);

        // CCCD field
        addFormField(formPanel, gbc, "CCCD:", txtCccd = createStyledTextField(), false);

        // Địa chỉ field
        addFormField(formPanel, gbc, "Địa chỉ:", txtDiaChi = createStyledTextField(), false);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(formWrapper, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanelDialog.setBackground(backgroundColor);
        buttonPanelDialog.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnLuu = createRoundedButton("Lưu", primaryColor, buttonTextColor, 10);
        btnLuu.setPreferredSize(new Dimension(130, 45));
        btnLuu.addActionListener(e -> luuBenhNhan());

        JButton btnHuy = createRoundedButton("Hủy", new Color(153, 153, 153), buttonTextColor, 10);
        btnHuy.setPreferredSize(new Dimension(130, 45));
        btnHuy.addActionListener(e -> inputDialog.setVisible(false));

        buttonPanelDialog.add(btnLuu);
        buttonPanelDialog.add(btnHuy);
        mainPanel.add(buttonPanelDialog, BorderLayout.SOUTH);

        inputDialog.setContentPane(mainPanel);
        
        // Set up Enter key navigation between fields
        setupEnterKeyNavigation();
        
        // Set default button (responds to Enter key)
        inputDialog.getRootPane().setDefaultButton(btnLuu);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(200, 40));
        textField.setBorder(new CompoundBorder(
                new CustomBorder(8, borderColor),
                new EmptyBorder(5, 12, 5, 12)));
        textField.setBackground(Color.WHITE);
        return textField;
    }
    
    // New method to create a styled JDateChooser
    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(200, 40));
        dateChooser.setBorder(new CustomBorder(8, borderColor));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        
        // Style the text field inside the date chooser
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));
        
        return dateChooser;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, boolean required) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        labelPanel.setBackground(panelColor);
        
        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setForeground(textColor);
        labelPanel.add(label);
        
        if (required) {
            JLabel requiredLabel = new JLabel("*");
            requiredLabel.setFont(regularFont);
            requiredLabel.setForeground(accentColor);
            labelPanel.add(requiredLabel);
        }
        
        panel.add(labelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
    }
    private void showInputDialog(boolean isThem) {
        if (!isThem) {
            int selectedRow = tableBenhNhan.getSelectedRow();
            if (selectedRow == -1) {
                showWarningMessage("Vui lòng chọn bệnh nhân để sửa.");
                return;
            }

            txtHoTen.setText((String) tableModel.getValueAt(selectedRow, 1));
            
            // Thiết lập giá trị cho JDateChooser thay vì txtNgaySinh
            try {
                String dateString = (String) tableModel.getValueAt(selectedRow, 2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(dateString);
                dateChooserNgaySinh.setDate(date);
            } catch (ParseException e) {
                showErrorMessage("Lỗi định dạng ngày", "Không thể đọc định dạng ngày tháng");
            }
            
            String gioiTinhValue = (String) tableModel.getValueAt(selectedRow, 3);
            for (int i = 0; i < cbGioiTinh.getItemCount(); i++) {
                if (cbGioiTinh.getItemAt(i).equals(gioiTinhValue)) {
                    cbGioiTinh.setSelectedIndex(i);
                    break;
                }
            }
            
            txtSoDienThoai.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtCccd.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtDiaChi.setText((String) tableModel.getValueAt(selectedRow, 6));

            inputDialog.setTitle("Sửa thông tin bệnh nhân");
        } else {
            clearInputFields();
            inputDialog.setTitle("Thêm bệnh nhân mới");
        }

        // Center dialog on screen
        inputDialog.setLocationRelativeTo(this);
        inputDialog.setVisible(true);
    }
    private void loadDanhSachBenhNhan() {
        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);
            for (BenhNhan benhNhan : danhSach) {
                tableModel.addRow(new Object[]{
                        benhNhan.getIdBenhNhan(),
                        benhNhan.getHoTen(),
                        new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                        benhNhan.getGioiTinh(),
                        benhNhan.getSoDienThoai(),
                        benhNhan.getCccd(),
                        benhNhan.getDiaChi()
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải dữ liệu bệnh nhân", e.getMessage());
        }
    }

    private void luuBenhNhan() {
        // Lấy dữ liệu từ các trường nhập liệu
        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String cccd = txtCccd.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        Date ngaySinh = dateChooserNgaySinh.getDate();
        String gioiTinh = cbGioiTinh.getSelectedItem().toString();
        
        // Đặt lại tất cả các hiển thị lỗi trước đó
        ValidationUtils.resetValidationErrors(txtHoTen, txtSoDienThoai, txtCccd, txtDiaChi, dateChooserNgaySinh);
        
        // Kiểm tra từng trường và chỉ hiển thị lỗi đầu tiên
        if (!ValidationUtils.validateHoTen(hoTen, txtHoTen)) {
            showWarningMessage(ValidationUtils.getErrorMessage(txtHoTen));
            txtHoTen.requestFocus();
            return;
        }
        
        if (!ValidationUtils.validateSoDienThoai(soDienThoai, txtSoDienThoai)) {
            showWarningMessage(ValidationUtils.getErrorMessage(txtSoDienThoai));
            txtSoDienThoai.requestFocus();
            return;
        }
        
        if (!ValidationUtils.validateCCCD(cccd, txtCccd)) {
            showWarningMessage(ValidationUtils.getErrorMessage(txtCccd));
            txtCccd.requestFocus();
            return;
        }
        
        if (!ValidationUtils.validateDiaChi(diaChi, txtDiaChi)) {
            showWarningMessage(ValidationUtils.getErrorMessage(txtDiaChi));
            txtDiaChi.requestFocus();
            return;
        }
        
        if (!ValidationUtils.validateNgaySinh(ngaySinh, dateChooserNgaySinh)) {
            showWarningMessage(ValidationUtils.getErrorMessage(dateChooserNgaySinh));
            dateChooserNgaySinh.requestFocus();
            return;
        }
        
        // Sanitize tất cả các đầu vào để ngăn chặn XSS
        hoTen = ValidationUtils.sanitizeInput(hoTen);
        soDienThoai = ValidationUtils.sanitizeInput(soDienThoai);
        cccd = ValidationUtils.sanitizeInput(cccd);
        diaChi = ValidationUtils.sanitizeInput(diaChi);
        
        try {
            // Lấy ngày từ JDateChooser
            java.sql.Date sqlDate = new java.sql.Date(ngaySinh.getTime());
            
            int selectedRow = tableBenhNhan.getSelectedRow();
            int idBenhNhan = (selectedRow != -1) ? (int) tableModel.getValueAt(selectedRow, 0) : 0;
            
            BenhNhan benhNhan = new BenhNhan(
                idBenhNhan,
                hoTen,
                sqlDate,
                gioiTinh,
                soDienThoai,
                cccd,
                diaChi
            );
            
            // Xác định thêm mới hay cập nhật
            if (selectedRow != -1 && !inputDialog.getTitle().contains("Thêm")) {
                qlBenhNhan.capNhatBenhNhan(benhNhan);
                showSuccessToast("Thông tin bệnh nhân đã được cập nhật thành công!");
            } else {
                qlBenhNhan.themBenhNhan(benhNhan);
                showSuccessToast("Bệnh nhân mới đã được thêm thành công!");
            }
            
            loadDanhSachBenhNhan();
            inputDialog.setVisible(false);
            
        } catch (SQLException e) {
            showErrorMessage("Lỗi cơ sở dữ liệu", e.getMessage());
        }
    }

    private void xoaBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn bệnh nhân để xóa.");
            return;
        }

        int modelRow = tableBenhNhan.convertRowIndexToModel(selectedRow);
        int idBenhNhan = (int) tableModel.getValueAt(modelRow, 0);
        String tenBenhNhan = (String) tableModel.getValueAt(modelRow, 1);

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
        
        JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa bệnh nhân <b>" + tenBenhNhan + "</b>?</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8);
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8);
        deleteButton.addActionListener(e -> {
            try {
                // Delete from database
                qlBenhNhan.xoaBenhNhan(idBenhNhan);
                
                // Close dialog first
                confirmDialog.dispose();
                
                // Then reload data and show success message
                SwingUtilities.invokeLater(() -> {
                    loadDanhSachBenhNhan();
                    showSuccessToast("Bệnh nhân đã được xóa thành công!");
                });
            } catch (SQLException ex) {
                showErrorMessage("Lỗi khi xóa bệnh nhân", ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setContentPane(panel);
        confirmDialog.setVisible(true);
    }
    private void timKiemBenhNhan() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDanhSachBenhNhan();
            return;
        }

        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);

            for (BenhNhan benhNhan : danhSach) {
                if (benhNhan.getHoTen().toLowerCase().contains(keyword) ||
                        (benhNhan.getSoDienThoai() != null && benhNhan.getSoDienThoai().toLowerCase().contains(keyword)) ||
                        (benhNhan.getCccd() != null && benhNhan.getCccd().toLowerCase().contains(keyword)) ||
                        (benhNhan.getDiaChi() != null && benhNhan.getDiaChi().toLowerCase().contains(keyword))) {

                    tableModel.addRow(new Object[]{
                            benhNhan.getIdBenhNhan(),
                            benhNhan.getHoTen(),
                            new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                            benhNhan.getGioiTinh(),
                            benhNhan.getSoDienThoai(),
                            benhNhan.getCccd(),
                            benhNhan.getDiaChi()
                    });
                }
            }
            if (tableModel.getRowCount() == 0) {
                showNotification("Không tìm thấy kết quả nào cho: '" + keyword + "'", NotificationType.WARNING);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tìm kiếm", e.getMessage());
        }
    }

    private void clearInputFields() {
        txtHoTen.setText("");
        dateChooserNgaySinh.setDate(null); // Đặt lại JDateChooser
        cbGioiTinh.setSelectedIndex(0);
        txtSoDienThoai.setText("");
        txtCccd.setText("");
        txtDiaChi.setText("");
    }
    @Override
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

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showSuccessMessage(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    // Custom rounded border
    class CustomBorder extends LineBorder {
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
    
    // Panel with rounded corners and optional shadow
  
    private void setupEnterKeyNavigation() {
        // Create an array of components in the desired tab order
        JComponent[] components = new JComponent[] {
            txtHoTen,
            dateChooserNgaySinh.getDateEditor().getUiComponent(), // Get the text field component
            cbGioiTinh,
            txtSoDienThoai,
            txtCccd,
            txtDiaChi
        };
        
        // Add key listeners to each component except the last one
        for (int i = 0; i < components.length - 1; i++) {
            final int nextIndex = i + 1;
            if (components[i] instanceof JTextField) {
                ((JTextField) components[i]).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            components[nextIndex].requestFocus();
                        }
                    }
                });
            }
        }
        
        // For the last component, add a listener to save the form when Enter is pressed
        if (components[components.length - 1] instanceof JTextField) {
            ((JTextField) components[components.length - 1]).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        luuBenhNhan();
                    }
                }
            });
        }
        
        // Add special handling for JComboBox since it needs an ActionListener
        cbGioiTinh.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSoDienThoai.requestFocus();
                }
            }
        });
        
        // Add listener to JDateChooser's text component
        ((JTextField) dateChooserNgaySinh.getDateEditor().getUiComponent()).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cbGioiTinh.requestFocus();
                }
            }
        });
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
    @Override
	public void showMessage(String message, String title, int messageType) {
		// TODO Auto-generated method stub
		
	}
}