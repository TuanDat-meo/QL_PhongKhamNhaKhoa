package view;

import controller.NhaCungCapController;
import model.NhaCungCap;
import util.CustomBorder;
import util.DataChangeListener;
import util.ExportManager;
import util.RoundedPanel;
import util.ExportManager.MessageCallback;
import view.DoanhThuUI.NotificationType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NhaCungCapUI extends JPanel implements MessageCallback, DataChangeListener {
    private NhaCungCapController nhaCungCapController;
    private DefaultTableModel nhaCungCapTableModel;
    private JTable nhaCungCapTable;

    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;

    private JButton btnThem;
    private JButton btnXuatFile;
    private JButton btnTimKiem;

    private JFrame parentFrame;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;

    private JTextField txtTimKiem;
    private JLabel lblTimKiem;

    // Modern styling properties
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
    private Color totalRowColor = new Color(232, 240, 254); // Light blue for total row

    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);

    // Business Logic
    private ExportManager exportManager;

    public NhaCungCapUI() {
        initializeUI();
        setupEventListeners();
        
        // Initialize ExportManager
        exportManager = new ExportManager(this, nhaCungCapTableModel, this);
        
        lamMoiDanhSach();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 10, 20, 10));
        setBackground(backgroundColor);

        // Add header panel with title and search
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Add main table panel
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Add button panel at bottom
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        // Setup popup menu for right-click actions
        setupPopupMenu();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 10, 15, 10));

        // Title Panel with text
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Search Panel with rounded styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(regularFont);
        lblTimKiem.setForeground(textColor);
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        
        // Create rounded border with padding
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                
        // Add key listener for Enter key
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterDanhSach();
                }
            }
        });

        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));

        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        // Create a panel with shadow effect
        RoundedPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create table model
        nhaCungCapTableModel = new DefaultTableModel(
            new Object[]{"ID", "Tên NCC", "Ngày đăng ký", "Loại hàng", "Số điện thoại", "Mã số thuế", "Địa chỉ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table with styling
        nhaCungCapTable = new JTable(nhaCungCapTableModel) {
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
        for (int i = 0; i < nhaCungCapTable.getColumnCount(); i++) {
            nhaCungCapTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Table styling
        nhaCungCapTable.setFont(tableFont);
        nhaCungCapTable.setRowHeight(40);
        nhaCungCapTable.setShowGrid(false);
        nhaCungCapTable.setIntercellSpacing(new Dimension(0, 0));
        nhaCungCapTable.setSelectionBackground(new Color(229, 243, 255));
        nhaCungCapTable.setSelectionForeground(textColor);
        nhaCungCapTable.setFocusable(false);
        nhaCungCapTable.setAutoCreateRowSorter(true);
        nhaCungCapTable.setBorder(null);

        // Style table header
        JTableHeader header = nhaCungCapTable.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        
        // Center the header text
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        // Set column widths
        TableColumnModel columnModel = nhaCungCapTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(120);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(250);
        
        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(nhaCungCapTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        return wrapperPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        
        return buttonPanel;
    }
    
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSua = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoa = createStyledMenuItem("Xóa");
        
        menuItemXoa.setForeground(accentColor);
        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemSua);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoa);

        // Add mouse listener to table for right-click popup menu
        nhaCungCapTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                int r = nhaCungCapTable.rowAtPoint(evt.getPoint());
                if (r >= 0 && r < nhaCungCapTable.getRowCount()) {
                    nhaCungCapTable.setRowSelectionInterval(r, r);
                } else {
                    nhaCungCapTable.clearSelection();
                }
                
                if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
                
                // Double click to view details
                if (evt.getClickCount() == 2 && !evt.isPopupTrigger()) {
                    xemChiTietNhaCungCap();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
                    int r = nhaCungCapTable.rowAtPoint(evt.getPoint());
                    if (r >= 0 && r < nhaCungCapTable.getRowCount()) {
                        nhaCungCapTable.setRowSelectionInterval(r, r);
                    } else {
                        nhaCungCapTable.clearSelection();
                    }
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
    }
    
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return menuItem;
    }    
    private void setupEventListeners() {
        // Table selection listener
        nhaCungCapTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = nhaCungCapTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Lưu dữ liệu đã chọn (để sửa hoặc xóa)
                    selectedRow = nhaCungCapTable.convertRowIndexToModel(selectedRow);
                    txtMaNCC = new JTextField(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0)));
                    txtTenNCC = new JTextField(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1)));
                    txtDiaChi = new JTextField(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 6)));
                    txtSoDienThoai = new JTextField(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 4)));
                }
            }
        });
        btnTimKiem.addActionListener(e -> {
            // Check if search field is empty
            if (txtTimKiem.getText().trim().isEmpty()) {
                // If empty, refresh the data
            	lamMoiDanhSach();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                // If not empty, filter the data
            	filterDanhSach();
            }
        });
        
        // Setup enter key on search field
        btnTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        // If empty, refresh the data
                    	lamMoiDanhSach();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
                        // If not empty, filter the data
                    	filterDanhSach();
                    }
                }
            }
        });
        // Button listeners
        btnThem.addActionListener(e -> {
            NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), null, NhaCungCapUI.this);
            dialog.setVisible(true);
        });

        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        btnTimKiem.addActionListener(e -> filterDanhSach());
        
        // Popup menu action listeners
        menuItemXemChiTiet.addActionListener(e -> xemChiTietNhaCungCap());
        menuItemSua.addActionListener(e -> suaNhaCungCapTuPopup());
        menuItemXoa.addActionListener(e -> xoaNhaCungCapTuPopup());
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
    public void showNotification(String message, NotificationType type) {
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
    
    /**
     * Hiển thị hộp thoại thông báo lỗi
     * @param message Nội dung lỗi
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Hiển thị hộp thoại thông báo lỗi với tiêu đề tùy chỉnh
     * @param title Tiêu đề
     * @param message Nội dung lỗi
     */
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            title, 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            title, 
            messageType
        );
    }
    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }
    private void xemChiTietNhaCungCap() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = nhaCungCapTable.convertRowIndexToModel(selectedRow);
            String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
            String tenNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1));
            String ngayDangKy = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 2));
            String loaiHang = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 3));
            String soDienThoai = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 4));
            String maSoThue = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 5));
            String diaChi = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 6));
            
            // Create a styled panel for details
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBackground(panelColor);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Add styled fields to panel
            addDetailField(detailsPanel, "Mã NCC:", maNCC);
            addDetailField(detailsPanel, "Tên NCC:", tenNCC);
            addDetailField(detailsPanel, "Ngày đăng ký:", ngayDangKy);
            addDetailField(detailsPanel, "Loại hàng:", loaiHang);
            addDetailField(detailsPanel, "Số điện thoại:", soDienThoai);
            addDetailField(detailsPanel, "Mã số thuế:", maSoThue);
            addDetailField(detailsPanel, "Địa chỉ:", diaChi);
            
            // Create and show dialog with custom styling
            JDialog detailDialog = new JDialog(getParentFrame(), "Chi tiết nhà cung cấp", true);
            detailDialog.setLayout(new BorderLayout());
            
            // Add a header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.setBackground(primaryColor);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            
            JLabel headerLabel = new JLabel("Chi tiết nhà cung cấp");
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            
            // Add a close button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(panelColor);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
            
            JButton closeButton = createRoundedButton("Đóng", primaryColor, Color.WHITE, 8);
            closeButton.addActionListener(e -> detailDialog.dispose());
            buttonPanel.add(closeButton);
            
            // Add components to dialog
            detailDialog.add(headerPanel, BorderLayout.NORTH);
            detailDialog.add(detailsPanel, BorderLayout.CENTER);
            detailDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Configure and show dialog
            detailDialog.setSize(450, 400);
            detailDialog.setLocationRelativeTo(this);
            detailDialog.setResizable(false);
            detailDialog.setVisible(true);
        }
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

    // Getter cho parentFrame
    private JFrame getParentFrame() {
        if (parentFrame == null) {
            parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        }
        return parentFrame;
    }

    // Getter cho nhaCungCapController
    private NhaCungCapController getNhaCungCapController() {
        if (nhaCungCapController == null) {
            nhaCungCapController = new NhaCungCapController();
        }
        return nhaCungCapController;
    }

    private void filterDanhSach() {
        String searchText = txtTimKiem.getText().toLowerCase();
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();

        List<NhaCungCap> danhSachDaLoc = danhSachNCC.stream()
                .filter(ncc -> String.valueOf(ncc.getMaNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getTenNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getDiaChi()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getSoDienThoai()).toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        for (NhaCungCap ncc : danhSachDaLoc) {
            Object[] rowData = {
                ncc.getMaNCC(), 
                ncc.getTenNCC(), 
                "2023-04-10", // Placeholder for ngày đăng ký
                "Thiết bị",   // Placeholder for loại hàng
                ncc.getSoDienThoai(), 
                "MST" + ncc.getMaNCC(), // Placeholder for mã số thuế
                ncc.getDiaChi()
            };
            nhaCungCapTableModel.addRow(rowData);
        }
        if (danhSachDaLoc.isEmpty()) {
            showNotification("Không tìm thấy kết quả phù hợp!", NotificationType.WARNING);
        } else {
            showNotification("Tìm thấy " + danhSachDaLoc.size() + " kết quả phù hợp!", NotificationType.SUCCESS);
        }
        updateTableAppearance();
    }

    // Add method to update the table appearance
    private void updateTableAppearance() {
        SwingUtilities.invokeLater(() -> {
            nhaCungCapTable.repaint();
        });
    }

    private void suaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow != -1) {
            String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
            String tenNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1));
            String soDienThoai = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 4));
            String diaChi = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 6));

            NhaCungCap nccToEdit = new NhaCungCap(maNCC, tenNCC, diaChi, soDienThoai);
            NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), nccToEdit, NhaCungCapUI.this);
            dialog.setVisible(true);
        } else {
            showErrorMessage("Vui lòng chọn một nhà cung cấp để sửa.");
        }
    }

    private void xoaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Vui lòng chọn một nhà cung cấp để xóa.");
            return;
        }

        String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));

        int option = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn xóa nhà cung cấp này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (getNhaCungCapController().xoaNhaCungCap(maNCC)) {
                showInfoMessage("Xóa nhà cung cấp thành công.");
                lamMoiDanhSach();
            } else {
                showErrorMessage("Xóa nhà cung cấp thất bại.");
            }
        }
    }
    public void lamMoiDanhSach() {
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();
        // Thêm dữ liệu mẫu nếu danh sách trống
        if (danhSachNCC.isEmpty()) {
            // Thêm dữ liệu mẫu
            Object[][] sampleData = {
                {"1", "Công ty TNHH ABC", "2023-01-15", "Thiết bị y tế", "0978123456", "123456789012", "Đà Nẵng"},
                {"2", "Công ty CP XYZ", "2022-12-30", "Dược phẩm", "0967543210", "345678901234", "Hải Phòng"},
                {"3", "Công ty TNHH LMN", "2023-04-18", "Vật tư y tế", "0987654321", "456789012345", "Cần Thơ"},
                {"4", "Công ty CP EFG", "2022-11-25", "Dược phẩm", "0934567890", "567890123456", "Bình Dương"},
                {"5", "Công ty TNHH RST", "2023-06-10", "Thiết bị y tế", "0923456789", "678901234567", "Huế"}
            };
            
            for (Object[] row : sampleData) {
                nhaCungCapTableModel.addRow(row);
            }
        } else {
            for (NhaCungCap ncc : danhSachNCC) {
                Object[] rowData = {
                    ncc.getMaNCC(), 
                    ncc.getTenNCC(), 
                    "2023-04-10", // Placeholder for ngày đăng ký
                    "Thiết bị",   // Placeholder for loại hàng
                    ncc.getSoDienThoai(), 
                    "MST" + ncc.getMaNCC(), 
                    ncc.getDiaChi()
                };
                nhaCungCapTableModel.addRow(rowData);
            }
        }
        updateTableAppearance();
    }

    public void cleanup() {
        if (nhaCungCapController != null) {
            nhaCungCapController.closeConnection();
        }
    }

	@Override
	public void onDataChanged() {
		lamMoiDanhSach();
		
	}
}