package view;

import controller.BacSiController;
import model.BacSi;
import util.CustomBorder;
import util.DataChangeListener;
import util.ExportManager;
import util.ExportManager.MessageCallback;
import view.BenhNhanUI.NotificationType;
import util.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BacSiUI extends JPanel implements MessageCallback, DataChangeListener {
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
    private String currentUserRole;
    private JFrame parentFrame;
    private ExportManager exportManager;
    private JDialog inputDialog;
    private JTextField txtHoTen, txtChuyenKhoa, txtBangCap, txtKinhNghiem, txtTenPhong, txtEmail, txtSoDienThoai;
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();
    public BacSiUI() {
        bacSiController = new BacSiController();
        exportManager = new ExportManager(this, tableModel, null);
        
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
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (searchField.getText().trim().isEmpty()) {
                    	loadBacSiData();
                        showSuccessToast("Dữ liệu đã được làm mới!");
                    } else {
                    	searchBacSi();
                    }
                }
            }
        }); 
        searchButton = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10, false);
        searchButton.setPreferredSize(new Dimension(120, 38));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) {
            	loadBacSiData();
                showSuccessToast("Dữ liệu đã được làm mới!");
            } else {
            	searchBacSi();
            }
        });
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Table panel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {
        	    "ID", "Họ Tên", "Chuyên Khoa", "Kinh Nghiệm", "Phòng Khám", "Email", "Số Điện Thoại"
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
        
        TableColumnModel columnModel = bacSiTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID 
        columnModel.getColumn(1).setPreferredWidth(180);  // Họ Tên
        columnModel.getColumn(2).setPreferredWidth(180);  // Chuyên Khoa
        columnModel.getColumn(3).setPreferredWidth(120);  // Kinh Nghiệm
        columnModel.getColumn(4).setPreferredWidth(180);  // Phòng Khám
        columnModel.getColumn(5).setPreferredWidth(180);  // Email
        columnModel.getColumn(6).setPreferredWidth(140);  // Số Điện Thoại

        // Set minimum widths to prevent columns from becoming too narrow
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(1).setMinWidth(150);
        columnModel.getColumn(2).setMinWidth(150);
        columnModel.getColumn(3).setMinWidth(100); 
        columnModel.getColumn(4).setMinWidth(150);
        columnModel.getColumn(5).setMinWidth(150);
        columnModel.getColumn(6).setMinWidth(120);
        // Alternating row colors
        bacSiTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set alternating row colors
                if (!isSelected) {
                    if (value == null || value.toString().trim().isEmpty()) {
                        component.setBackground(Color.WHITE);
                    } else {
                        component.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    }
                }
                
                JLabel label = (JLabel) component;
                
                // Căn giữa tất cả nội dung trong table
                label.setHorizontalAlignment(SwingConstants.CENTER);
                
                // Add some padding for better visual appearance
                label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                
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
        
        exportButton = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10, false);
        exportButton.setPreferredSize(new Dimension(100, 45));
        exportButton.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        
        addButton = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10,false);
        addButton.setPreferredSize(new Dimension(100, 45));
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
            popupMenu.addSeparator();
            JMenuItem editItem = createMenuItem("Chỉnh Sửa");
            editItem.addActionListener(e -> showEditBacSiDialog());
            popupMenu.add(editItem);
            popupMenu.addSeparator();
            JMenuItem deleteItem = createMenuItem("Xóa");
            deleteItem.setForeground(accentColor);
            deleteItem.addActionListener(e -> deleteBacSi());
            popupMenu.add(deleteItem);
            
            popupMenu.addSeparator();
            
            JMenuItem appointmentsItem = createMenuItem("Xem Lịch Hẹn");
            appointmentsItem.addActionListener(e -> showAppointments());
            popupMenu.add(appointmentsItem);
            popupMenu.addSeparator();
            JMenuItem treatmentsItem = createMenuItem("Xem Điều Trị");
            treatmentsItem.addActionListener(e -> showTreatments());
            popupMenu.add(treatmentsItem);
            
            popupMenu.show(bacSiTable, x, y);
        }
    }
    
    private JMenuItem createMenuItem(String text) {
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
    private void showBacSiDetails(BacSi bacSi) {
        JDialog detailsDialog = new JDialog(parentFrame, "Chi Tiết Bác Sĩ", true);
        detailsDialog.setSize(500, 470);
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
        detailsPanel.setBorder(new EmptyBorder(0, 20, 20, 0));
        detailsPanel.setBackground(Color.WHITE);
        
        addDetailField(detailsPanel, "ID:", String.valueOf(bacSi.getIdBacSi()));
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
        Dimension buttonSize = new Dimension(90, 36);
        JButton editButton = createRoundedButton("Chỉnh Sửa", warningColor, buttonTextColor, 10,true );
        editButton.setPreferredSize(buttonSize);
        editButton.setMinimumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        editButton.addActionListener(e -> {
            detailsDialog.dispose();
            showEditBacSiDialog();
        });
        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10, false );
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMinimumSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
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
        // Sử dụng padding khác nhau tùy theo button
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Padding nhỏ hơn cho "Chỉnh Sửa"
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding bình thường cho "Đóng"
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
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
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
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadBacSiData();
            return;
        }
        
        try {
            // Lấy tất cả dữ liệu bác sĩ
            List<BacSi> allBacSi = bacSiController.getAllBacSi();
            tableModel.setRowCount(0);
            
            int matchCount = 0;
            
            for (BacSi bacSi : allBacSi) {
                // Kiểm tra từng trường dữ liệu có chứa từ khóa tìm kiếm không (không phân biệt hoa thường)
                boolean isMatch = false;
                
                // Tìm theo ID (chuyển sang string để so sánh)
                if (String.valueOf(bacSi.getIdBacSi()).toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo họ tên
                if (bacSi.getHoTenBacSi() != null && 
                    bacSi.getHoTenBacSi().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo chuyên khoa
                if (bacSi.getChuyenKhoa() != null && 
                    bacSi.getChuyenKhoa().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo bằng cấp
                if (bacSi.getBangCap() != null && 
                    bacSi.getBangCap().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo kinh nghiệm (chuyển sang string)
                if (String.valueOf(bacSi.getKinhNghiem()).toLowerCase().contains(searchTerm) ||
                    (String.valueOf(bacSi.getKinhNghiem()) + " năm").toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo tên phòng
                if (bacSi.getTenPhong() != null && 
                    bacSi.getTenPhong().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo email
                if (bacSi.getEmailNguoiDung() != null && 
                    bacSi.getEmailNguoiDung().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Tìm theo số điện thoại
                if (bacSi.getSoDienThoaiNguoiDung() != null && 
                    bacSi.getSoDienThoaiNguoiDung().toLowerCase().contains(searchTerm)) {
                    isMatch = true;
                }
                
                // Nếu có khớp thì thêm vào bảng
                if (isMatch) {
                    Object[] rowData = {
                        bacSi.getIdBacSi(),
                        bacSi.getHoTenBacSi(),
                        bacSi.getChuyenKhoa(),
                        bacSi.getKinhNghiem() + " năm",
                        bacSi.getTenPhong(),
                        bacSi.getEmailNguoiDung(),
                        bacSi.getSoDienThoaiNguoiDung()
                    };
                    tableModel.addRow(rowData);
                    matchCount++;
                }
            }
            
            currentBacSiId = -1;
            
            // Hiển thị thông báo kết quả tìm kiếm
            if (matchCount == 0) {
                showNotification("Không tìm thấy kết quả nào cho: '" + searchTerm + "'", NotificationType.WARNING);
            } else {
                showNotification("Tìm thấy " + matchCount + " kết quả cho: '" + searchTerm + "'", NotificationType.SUCCESS);
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tìm kiếm bác sĩ", e.getMessage());
            e.printStackTrace();
        }
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
    private void showAddBacSiDialog() {
        BacSiDialog dialog = new BacSiDialog(parentFrame, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    
    public void focusOnDoctor(int doctorId) {
        try {
            // Find the row containing the doctor with the corresponding ID
            int rowCount = bacSiTable.getRowCount();
            
            for (int i = 0; i < rowCount; i++) {
                // The first column in the table contains the doctor ID
                int currentDoctorId = (Integer) bacSiTable.getValueAt(i, 0);
                
                if (currentDoctorId == doctorId) {
                    // Select the found row
                    bacSiTable.setRowSelectionInterval(i, i);
                    
                    // Scroll to the selected row
                    Rectangle rect = bacSiTable.getCellRect(i, 0, true);
                    bacSiTable.scrollRectToVisible(rect);
                    
                    currentBacSiId = doctorId;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Lỗi", "Không thể hiển thị thông tin bác sĩ: " + e.getMessage());
        }
    }
    
    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
        
        // Adjust permissions based on role
        if ("admin".equalsIgnoreCase(role)) {
            addButton.setEnabled(true);
            exportButton.setEnabled(true);
        } else if ("staff".equalsIgnoreCase(role)) {
            addButton.setEnabled(true);
            exportButton.setEnabled(true);
        } else if ("doctor".equalsIgnoreCase(role)) {
            addButton.setEnabled(false);
            exportButton.setEnabled(true);
        } else {
            // Default or guest role
            addButton.setEnabled(false);
            exportButton.setEnabled(false);
        }
    }
    
    private void showEditBacSiDialog() {
        if (currentBacSiId == -1) {
            showMessage("Vui lòng chọn một bác sĩ để chỉnh sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        BacSi bacSi = bacSiController.getBacSiById(currentBacSiId);
        if (bacSi == null) {
            showErrorMessage("Lỗi", "Không thể tìm thấy thông tin bác sĩ.");
            return;
        }
        
        BacSiDialog dialog = new BacSiDialog(parentFrame, bacSi);
        
        // Add property change listener to capture the doctorDataChanged event
        dialog.addPropertyChangeListener("doctorDataChanged", evt -> {
            if ((boolean) evt.getNewValue()) {
                loadBacSiData(); // Reload data when change is confirmed
            }
        });
        
        dialog.setVisible(true);
        
        // This check will still work as a fallback
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    
    public void deleteBacSi() {
        if (currentBacSiId == -1) {
            showWarningMessage("Vui lòng chọn bác sĩ để xóa.");
            return;
        }
        
        BacSi currentDoctor = bacSiController.getBacSiById(currentBacSiId);
        if (currentDoctor == null) {
            showErrorMessage("Lỗi", "Không thể tìm thấy thông tin bác sĩ.");
            return;
        }
        
        // Kiểm tra xem bác sĩ có lịch hẹn trong tương lai không
        boolean hasFutureAppointments = bacSiController.hasFutureAppointments(currentBacSiId);
        
        // Lấy danh sách bác sĩ cùng chuyên khoa để thay thế
        List<BacSi> replacementDoctors = bacSiController.getOtherDoctorsBySpecialty(currentBacSiId);
        
        String message;
        if (hasFutureAppointments && !replacementDoctors.isEmpty()) {
            message = "Bác sĩ này có lịch hẹn trong tương lai.\n" +
                     "Bạn có muốn chuyển lịch hẹn cho bác sĩ khác cùng chuyên khoa không?\n\n" +
                     "Chọn 'Yes' để chuyển lịch hẹn\n" +
                     "Chọn 'No' để hủy tất cả lịch hẹn trong tương lai";
        } else if (hasFutureAppointments && replacementDoctors.isEmpty()) {
            message = "Bác sĩ này có lịch hẹn trong tương lai.\n" +
                     "Không có bác sĩ khác cùng chuyên khoa để chuyển lịch hẹn.\n" +
                     "Tất cả lịch hẹn trong tương lai sẽ bị hủy.\n\n" +
                     "Bạn có chắc chắn muốn xóa bác sĩ này?";
        } else {
            message = "Bạn có chắc chắn muốn xóa bác sĩ này?";
        }
        
        int choice = JOptionPane.showConfirmDialog(parentFrame, 
            message,
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            int replacementDoctorId = -1;
            
            // Nếu có lịch hẹn và có bác sĩ thay thế, cho phép chọn bác sĩ thay thế
            if (hasFutureAppointments && !replacementDoctors.isEmpty()) {
                String[] doctorNames = new String[replacementDoctors.size() + 1];
                doctorNames[0] = "Hủy tất cả lịch hẹn (không chuyển)";
                
                for (int i = 0; i < replacementDoctors.size(); i++) {
                    BacSi doctor = replacementDoctors.get(i);
                    doctorNames[i + 1] = doctor.getHoTenBacSi() + " - " + doctor.getTenPhong();
                }
                
                String selectedDoctor = (String) JOptionPane.showInputDialog(
                    parentFrame,
                    "Chọn bác sĩ thay thế hoặc hủy lịch hẹn:",
                    "Chọn bác sĩ thay thế",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    doctorNames,
                    doctorNames[0]
                );
                
                if (selectedDoctor == null) {
                    // User cancelled the selection
                    showMessage("Đã hủy thao tác xóa bác sĩ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Tìm ID của bác sĩ được chọn
                for (int i = 0; i < doctorNames.length; i++) {
                    if (doctorNames[i].equals(selectedDoctor) && i > 0) {
                        replacementDoctorId = replacementDoctors.get(i - 1).getIdBacSi();
                        break;
                    }
                }
            }
            
            // Thực hiện xóa với bác sĩ thay thế (nếu có)
            boolean success;
            if (replacementDoctorId > 0) {
                success = bacSiController.deleteBacSiWithReplacement(currentBacSiId, replacementDoctorId);
                if (success) {
                    String replacementDoctorName = "";
                    for (BacSi doctor : replacementDoctors) {
                        if (doctor.getIdBacSi() == replacementDoctorId) {
                            replacementDoctorName = doctor.getHoTenBacSi();
                            break;
                        }
                    }
                    showSuccessToast("Đã xóa bác sĩ thành công. Lịch hẹn đã được chuyển cho bác sĩ " + replacementDoctorName + ".");
                }
            } else {
                // Xóa bình thường (sẽ hủy lịch hẹn)
                success = bacSiController.deleteBacSi(currentBacSiId);
                if (success) {
                    if (hasFutureAppointments) {
                        showSuccessToast("Đã xóa bác sĩ thành công. Tất cả lịch hẹn trong tương lai đã được hủy.");
                    } else {
                        showSuccessToast("Đã xóa bác sĩ thành công.");
                    }
                }
            }
            
            if (success) {
                loadBacSiData();
                // Reset current selection
                currentBacSiId = -1;
            } else {
                showErrorMessage("Lỗi", "Không thể xóa bác sĩ. Vui lòng thử lại sau.");
            }
        } else {
            // User clicked "No" - show "cancel deletion successful" message
            showMessage("Đã hủy thao tác xóa bác sĩ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showAppointments() {
        if (currentBacSiId == -1) {
            showErrorMessage("Lỗi", "Vui lòng chọn một bác sĩ để xem lịch hẹn.");
            return;
        }
        
        List<model.LichHen> appointments = bacSiController.getFutureAppointments(currentBacSiId);
        
        if (appointments.isEmpty()) {
            showMessage("Bác sĩ này không có lịch hẹn nào trong tương lai.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        AppointmentsDialog dialog = new AppointmentsDialog(parentFrame, appointments);
        dialog.setVisible(true);
    }
    
    private void showTreatments() {
        if (currentBacSiId == -1) {
            showErrorMessage("Lỗi", "Vui lòng chọn một bác sĩ để xem điều trị.");
            return;
        }
        
        List<model.DieuTri> treatments = bacSiController.getDieuTriByBacSi(currentBacSiId);
        
        if (treatments.isEmpty()) {
            showMessage("Bác sĩ này không có điều trị nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        TreatmentsDialog dialog = new TreatmentsDialog(parentFrame, treatments);
        dialog.setVisible(true);
    }
    
    // Set parent frame reference
    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    // Implementation of DataChangeListener interface
    @Override
    public void onDataChanged() {
        // Reload data when notified of changes
        loadBacSiData();
    }
    
    // Implementation of MessageCallback interface
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
    
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(parentFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(parentFrame, message, title, messageType);
    }
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
}