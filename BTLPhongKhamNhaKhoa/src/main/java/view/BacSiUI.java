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
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
    private String[] getChuyenKhoaOptions() {
        return new String[]{
            "Nha khoa tổng quát",
            "Răng hàm mặt",
            "Chỉnh nha",
            "Nha chu",
            "Nội nha",
            "Phục hình răng",
            "Nha khoa trẻ em",
            "Implant nha khoa",
            "Phẫu thuật miệng",
            "Tẩy trắng răng",
            "Nha khoa thẩm mỹ"
        };
    }
    private String showChuyenKhoaSelectionDialog(String currentChuyenKhoa) {
        String[] options = getChuyenKhoaOptions();
        
        // Tìm chỉ số của chuyên khoa hiện tại (nếu có)
        int selectedIndex = 0;
        if (currentChuyenKhoa != null && !currentChuyenKhoa.trim().isEmpty()) {
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(currentChuyenKhoa)) {
                    selectedIndex = i;
                    break;
                }
            }
        }
        
        String selectedOption = (String) JOptionPane.showInputDialog(
            parentFrame,
            "Chọn chuyên khoa:",
            "Chuyên khoa",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[selectedIndex]
        );
        
        return selectedOption;
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
        
        if (hasFutureAppointments) {
            // Lấy danh sách TẤT CẢ bác sĩ khác (loại trừ bác sĩ hiện tại)
            List<BacSi> allOtherDoctors = bacSiController.getAllBacSi();
            List<BacSi> replacementDoctors = new ArrayList<>();
            
            // Lọc chỉ lấy bác sĩ có CÙNG CHUYÊN KHOA VÀ CÙNG PHÒNG KHÁM và loại bỏ bác sĩ hiện tại
            String currentSpecialty = currentDoctor.getChuyenKhoa();
            String currentRoom = currentDoctor.getTenPhong();
            
            for (BacSi doctor : allOtherDoctors) {
                if (doctor.getIdBacSi() != currentBacSiId && 
                    doctor.getChuyenKhoa() != null && 
                    doctor.getChuyenKhoa().equals(currentSpecialty) &&
                    doctor.getTenPhong() != null &&
                    doctor.getTenPhong().equals(currentRoom)) {
                    replacementDoctors.add(doctor);
                }
            }
            
            if (replacementDoctors.isEmpty()) {
                // Không có bác sĩ nào cùng chuyên khoa và cùng phòng khám - hiển thị dialog cảnh báo đẹp
                showCannotDeleteDialog(currentDoctor, currentSpecialty, currentRoom);
                return;
            }
            
            // Hiển thị dialog chọn bác sĩ thay thế với giao diện đẹp
            BacSi selectedReplacement = showReplacementDoctorDialog(currentDoctor, replacementDoctors, currentSpecialty, currentRoom);
            
            if (selectedReplacement == null) {
                return; // User cancelled - DỪNG LẠI HOÀN TOÀN
            }
            
            // Hiển thị dialog xác nhận cuối cùng với giao diện đẹp
            boolean finalConfirmed = showFinalDeleteConfirmationDialog(currentDoctor, selectedReplacement, currentSpecialty, currentRoom);
            if (finalConfirmed) {
                boolean success = bacSiController.deleteBacSiWithReplacement(currentBacSiId, selectedReplacement.getIdBacSi());
                if (success) {
                    showSuccessToast(String.format("Đã xóa bác sĩ thành công. Lịch hẹn đã được chuyển cho bác sĩ %s.", 
                        selectedReplacement.getHoTenBacSi()));
                    loadBacSiData();
                    currentBacSiId = -1;
                } else {
                    showErrorMessage("Lỗi", "Không thể xóa bác sĩ. Vui lòng thử lại sau.");
                }
            }
        } else {
            // Không có lịch hẹn trong tương lai - hiển thị dialog xác nhận đẹp
            boolean simpleConfirmed = showSimpleDeleteConfirmationDialog(currentDoctor);
            if (simpleConfirmed) {
                boolean success = bacSiController.deleteBacSi(currentBacSiId);
                if (success) {
                    showSuccessToast("Đã xóa bác sĩ thành công.");
                    loadBacSiData();
                    currentBacSiId = -1;
                } else {
                    showErrorMessage("Lỗi", "Không thể xóa bác sĩ. Vui lòng thử lại sau.");
                }
            }
        }
    }
    private void showCannotDeleteDialog(BacSi currentDoctor, String currentSpecialty, String currentRoom) {
        JDialog dialog = new JDialog(parentFrame, "Không thể xóa bác sĩ", true);
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        // Header panel với màu cảnh báo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(warningColor);
        headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel("KHÔNG THỂ XÓA BÁC SĨ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(new EmptyBorder(18, 18, 12, 18));
        
        // Thông tin bác sĩ
        JPanel doctorInfoPanel = new JPanel(new BorderLayout());
        doctorInfoPanel.setBackground(new Color(248, 249, 250));
        doctorInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        doctorInfoPanel.setMaximumSize(new Dimension(480, 85));
        
        JLabel doctorNameLabel = new JLabel("Bác sĩ: " + currentDoctor.getHoTenBacSi());
        doctorNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        doctorNameLabel.setForeground(textColor);
        
        JLabel specialtyLabel = new JLabel("Chuyên khoa: " + currentSpecialty);
        specialtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        specialtyLabel.setForeground(textColor);
        
        JLabel roomLabel = new JLabel("Phòng khám: " + currentRoom);
        roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roomLabel.setForeground(textColor);
        
        JPanel doctorDetailsPanel = new JPanel();
        doctorDetailsPanel.setLayout(new BoxLayout(doctorDetailsPanel, BoxLayout.Y_AXIS));
        doctorDetailsPanel.setBackground(new Color(248, 249, 250));
        doctorDetailsPanel.add(doctorNameLabel);
        doctorDetailsPanel.add(Box.createVerticalStrut(4));
        doctorDetailsPanel.add(specialtyLabel);
        doctorDetailsPanel.add(Box.createVerticalStrut(4));
        doctorDetailsPanel.add(roomLabel);
        
        doctorInfoPanel.add(doctorDetailsPanel, BorderLayout.CENTER);
        
        // Lý do không thể xóa
        JLabel reasonTitle = new JLabel("Lý do không thể xóa:");
        reasonTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        reasonTitle.setForeground(textColor);
        reasonTitle.setBorder(new EmptyBorder(12, 0, 6, 0));
        
        JTextArea reasonText = new JTextArea();
        reasonText.setText("Bác sĩ này có lịch hẹn trong tương lai và không có bác sĩ nào khác " +
                          "cùng chuyên khoa và cùng phòng khám trong hệ thống để thay thế.");
        reasonText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reasonText.setForeground(textColor);
        reasonText.setBackground(Color.WHITE);
        reasonText.setEditable(false);
        reasonText.setWrapStyleWord(true);
        reasonText.setLineWrap(true);
        reasonText.setBorder(new EmptyBorder(6, 0, 0, 0));
        
        // Hướng dẫn giải quyết
        JLabel solutionTitle = new JLabel("Để xóa bác sĩ này, bạn có thể:");
        solutionTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        solutionTitle.setForeground(textColor);
        solutionTitle.setBorder(new EmptyBorder(10, 0, 6, 0));
        
        JTextArea solutionText = new JTextArea();
        solutionText.setText("1. Hủy tất cả lịch hẹn trong tương lai của bác sĩ này\n" +
                            "2. Thêm bác sĩ khác cùng chuyên khoa và cùng phòng khám vào hệ thống");
        solutionText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        solutionText.setForeground(textColor);
        solutionText.setBackground(Color.WHITE);
        solutionText.setEditable(false);
        solutionText.setBorder(new EmptyBorder(6, 0, 0, 0));
        
        mainContentPanel.add(doctorInfoPanel);
        mainContentPanel.add(reasonTitle);
        mainContentPanel.add(reasonText);
        mainContentPanel.add(solutionTitle);
        mainContentPanel.add(solutionText);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(backgroundColor);
        
        JButton okButton = createRoundedButton("Đã hiểu", primaryColor, buttonTextColor, 6, false);
        okButton.setPreferredSize(new Dimension(90, 32));
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        dialog.add(headerPanel, BorderLayout.NORTH); 
        dialog.add(mainContentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    private BacSi showReplacementDoctorDialog(BacSi currentDoctor, List<BacSi> replacementDoctors, String currentSpecialty, String currentRoom) {
	    JDialog dialog = new JDialog(parentFrame, "Chọn bác sĩ thay thế", true);
	
	    // Kích thước dialog đã giảm chiều cao
	    dialog.setSize(600, 450); // Giảm từ 520 xuống 450
	    dialog.setLocationRelativeTo(parentFrame);
	    dialog.setLayout(new BorderLayout());
	
	    final BacSi[] selectedDoctor = {null};
	    final boolean[] userCancelled = {false};
	
	    // Xử lý sự kiện đóng dialog
	    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dialog.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            userCancelled[0] = true;
	            selectedDoctor[0] = null;
	            dialog.dispose();
	        }
	    });
	
	    // Header panel
	    JPanel headerPanel = new JPanel(new BorderLayout());
	    headerPanel.setBackground(primaryColor);
	    headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
	
	    JLabel titleLabel = new JLabel("CHỌN BÁC SĨ THAY THẾ");
	    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
	    titleLabel.setForeground(Color.WHITE);
	    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	
	    headerPanel.add(titleLabel, BorderLayout.CENTER);
	
	    // Main content panel
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(Color.WHITE);
	    mainPanel.setBorder(new EmptyBorder(15, 15, 8, 15));
	
	    // Thông tin bác sĩ hiện tại
	    JPanel currentDoctorPanel = createUniformInfoPanel(
	        "Bác sĩ cần xóa:", 
	        currentDoctor.getHoTenBacSi(), 
	        currentSpecialty, 
	        currentRoom,
	        currentDoctor.getKinhNghiem(),
	        currentDoctor.getBangCap(),
	        new Color(252, 248, 248), 
	        accentColor
	    );
	
	    // Thông báo
	    JLabel messageLabel = new JLabel(String.format(
	        "Bác sĩ này có lịch hẹn trong tương lai. Tìm thấy %d bác sĩ thay thế cùng chuyên khoa và phòng khám:",
	        replacementDoctors.size()));
	    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	    messageLabel.setForeground(textColor);
	    messageLabel.setBorder(new EmptyBorder(8, 0, 8, 0));
	
	    // Container cho danh sách bác sĩ với chiều cao cố định cho 2 bác sĩ
	    JPanel listContainer = new JPanel(new BorderLayout());
	    listContainer.setBackground(Color.WHITE);
	    listContainer.setPreferredSize(new Dimension(570, 130)); // Giảm từ 163 xuống 130 (đủ cho 2 items)
	
	    // Tạo JList với custom renderer
	    DefaultListModel<BacSi> listModel = new DefaultListModel<>();
	    for (BacSi doctor : replacementDoctors) {
	        listModel.addElement(doctor);
	    }
	
	    JList<BacSi> doctorsList = new JList<>(listModel);
	    doctorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    doctorsList.setSelectedIndex(0); // Chọn mặc định item đầu tiên
	    doctorsList.setFixedCellHeight(65); // Chiều cao cố định cho mỗi item
	    selectedDoctor[0] = replacementDoctors.get(0);
	
	    // Custom cell renderer cho list
	    doctorsList.setCellRenderer(new ListCellRenderer<BacSi>() {
	        @Override
	        public Component getListCellRendererComponent(JList<? extends BacSi> list, BacSi doctor, 
	                int index, boolean isSelected, boolean cellHasFocus) {
	
	            JPanel panel = new JPanel(new BorderLayout());
	            panel.setBorder(new EmptyBorder(8, 12, 8, 12));
	            panel.setPreferredSize(new Dimension(540, 45)); // Kích thước cố định
	
	            if (isSelected) {
	                panel.setBackground(new Color(230, 247, 255));
	                panel.setBorder(BorderFactory.createCompoundBorder(
	                    new LineBorder(primaryColor, 2),
	                    new EmptyBorder(6, 10, 6, 10)
	                ));
	            } else {
	                panel.setBackground(Color.WHITE);
	                panel.setBorder(BorderFactory.createCompoundBorder(
	                    new LineBorder(borderColor, 1),
	                    new EmptyBorder(7, 11, 7, 11)
	                ));
	            }
	
	            // Thông tin bác sĩ
	            JPanel doctorInfoPanel = new JPanel();
	            doctorInfoPanel.setLayout(new BoxLayout(doctorInfoPanel, BoxLayout.Y_AXIS));
	            doctorInfoPanel.setBackground(panel.getBackground());
	
	            JLabel nameLabel = new JLabel(doctor.getHoTenBacSi());
	            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
	            nameLabel.setForeground(isSelected ? primaryColor : textColor);
	
	            String experienceInfo = String.format("Kinh nghiệm: %d năm", doctor.getKinhNghiem());
	            JLabel experienceLabel = new JLabel(experienceInfo);
	            experienceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	            experienceLabel.setForeground(new Color(108, 117, 125));
	
	            String degreeInfo = String.format("Bằng cấp: %s", doctor.getBangCap());
	            JLabel degreeLabel = new JLabel(degreeInfo);
	            degreeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	            degreeLabel.setForeground(new Color(108, 117, 125));
	
	            doctorInfoPanel.add(nameLabel);
	            doctorInfoPanel.add(Box.createVerticalStrut(2));
	            doctorInfoPanel.add(experienceLabel);
	            doctorInfoPanel.add(Box.createVerticalStrut(1));
	            doctorInfoPanel.add(degreeLabel);
	
	            // Thêm icon được chọn
	            if (isSelected) {
	                JLabel selectedIcon = new JLabel("+");
	                selectedIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
	                selectedIcon.setForeground(primaryColor);
	                selectedIcon.setPreferredSize(new Dimension(20, 20));
	                selectedIcon.setVerticalAlignment(SwingConstants.CENTER);
	                panel.add(selectedIcon, BorderLayout.EAST);
	            }
	
	            panel.add(doctorInfoPanel, BorderLayout.CENTER);
	
	            return panel;
	        }
	    });
	
	    // Xử lý sự kiện chọn
	    doctorsList.addListSelectionListener(e -> {
	        if (!e.getValueIsAdjusting()) {
	            int selectedIndex = doctorsList.getSelectedIndex();
	            if (selectedIndex >= 0) {
	                selectedDoctor[0] = replacementDoctors.get(selectedIndex);
	            }
	        }
	    });
	
	    // Scroll pane với chiều cao phù hợp cho 2 items
	    JScrollPane scrollPane = new JScrollPane(doctorsList);
	    scrollPane.setBorder(new LineBorder(borderColor, 1));
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setPreferredSize(new Dimension(570, 130)); // Giảm từ 163 xuống 130
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	
	    listContainer.add(scrollPane, BorderLayout.CENTER);
	
	    // Thông tin tổng quan với thông báo về cuộn (chỉ hiện khi có > 2 bác sĩ)
	    String scrollHint = replacementDoctors.size() > 2 ? " (Cuộn để xem thêm)" : "";
	    JLabel summaryLabel = new JLabel(String.format(
	        "Chọn 1 trong %d bác sĩ ở trên để thay thế cho lịch hẹn trong tương lai%s",
	        replacementDoctors.size(), scrollHint));
	    summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
	    summaryLabel.setForeground(new Color(108, 117, 125));
	    summaryLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
	
	    // Tổ chức layout
	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
	    topPanel.setBackground(Color.WHITE);
	    topPanel.add(currentDoctorPanel);
	    topPanel.add(messageLabel);
	
	    JPanel centerPanel = new JPanel(new BorderLayout());
	    centerPanel.setBackground(Color.WHITE);
	    centerPanel.add(listContainer, BorderLayout.CENTER);
	    centerPanel.add(summaryLabel, BorderLayout.SOUTH);
	
	    mainPanel.add(topPanel, BorderLayout.NORTH);
	    mainPanel.add(centerPanel, BorderLayout.CENTER);
	
	    // Button panel
	    JPanel buttonPanel = createUniformButtonPanel(
	        new String[]{"Hủy", "Tiếp tục"},
	        new Color[]{new Color(108, 117, 125), successColor},
	        new Dimension[]{new Dimension(80, 32), new Dimension(90, 32)},
	        new ActionListener[]{
	            e -> {
	                userCancelled[0] = true;
	                selectedDoctor[0] = null;
	                dialog.dispose();
	            },
	            e -> {
	                if (selectedDoctor[0] == null) {
	                    JOptionPane.showMessageDialog(dialog, 
	                        "Vui lòng chọn một bác sĩ thay thế!", 
	                        "Thông báo", 
	                        JOptionPane.WARNING_MESSAGE);
	                    return;
	                }
	                userCancelled[0] = false;
	                dialog.dispose();
	            }
	        }
	    );
	
	    dialog.add(headerPanel, BorderLayout.NORTH);
	    dialog.add(mainPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);
	
	    dialog.setVisible(true);
	
	    if (userCancelled[0]) {
	        return null;
	    }
	
	    return selectedDoctor[0];
	}
    // Hàm tạo panel thông tin đồng nhất
    private JPanel createUniformInfoPanel(String title, String doctorName, String specialty, 
                                         String room, int experience, String degree,
                                         Color backgroundColor, Color borderColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setPreferredSize(new Dimension(550, 60));
        panel.setMaximumSize(new Dimension(550, 60));
        panel.setMinimumSize(new Dimension(550, 60));
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        infoPanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(borderColor);
        
        JLabel nameLabel = new JLabel(doctorName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(textColor);
        
        JLabel detailsLabel = new JLabel(String.format("%s - %s | KN: %d năm | %s", 
            specialty, room, experience, degree));
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        detailsLabel.setForeground(new Color(108, 117, 125));
        
        infoPanel.add(titleLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(detailsLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    // Hàm tạo panel button đồng nhất
    private JPanel createUniformButtonPanel(String[] buttonTexts, Color[] buttonColors, 
                                           Dimension[] buttonSizes, ActionListener[] listeners) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonPanel.setBackground(backgroundColor);
        
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createRoundedButton(buttonTexts[i], buttonColors[i], buttonTextColor, 6, false);
            button.setPreferredSize(buttonSizes[i]);
            button.addActionListener(listeners[i]);
            buttonPanel.add(button);
        }
        
        return buttonPanel;
    }
 // Dialog xác nhận xóa cuối cùng
    private boolean showFinalDeleteConfirmationDialog(BacSi currentDoctor, BacSi replacementDoctor, 
                                                     String currentSpecialty, String currentRoom) {
        JDialog dialog = new JDialog(parentFrame, "Xác nhận xóa bác sĩ", true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        final boolean[] confirmed = {false};
        
        // Xử lý sự kiện đóng dialog
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed[0] = false;
                dialog.dispose();
            }
        });
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel("XÁC NHẬN XÓA BÁC SĨ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        mainContentPanel.setBackground(Color.WHITE);
        
        // Cảnh báo
        JLabel warningLabel = new JLabel("Bạn đang thực hiện hành động không thể hoàn tác!");
        warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        warningLabel.setForeground(accentColor);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        
        // Thông tin bác sĩ bị xóa
        JPanel deletePanel = createCompactInfoPanel("BÁC SĨ SẼ BỊ XÓA", 
            currentDoctor.getHoTenBacSi(), currentSpecialty, currentRoom, 
            currentDoctor.getKinhNghiem(), accentColor);
        
        // Thông tin bác sĩ thay thế
        JPanel replacePanel = createCompactInfoPanel("BÁC SĨ THAY THẾ", 
            replacementDoctor.getHoTenBacSi(), currentSpecialty, currentRoom, 
            replacementDoctor.getKinhNghiem(), successColor);
        
        // Thông báo về lịch hẹn
        JLabel appointmentLabel = new JLabel("Tất cả lịch hẹn sẽ được chuyển cho bác sĩ thay thế.");
        appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appointmentLabel.setForeground(textColor);
        appointmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appointmentLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        mainContentPanel.add(warningLabel);
        mainContentPanel.add(deletePanel);
        mainContentPanel.add(Box.createVerticalStrut(8));
        mainContentPanel.add(replacePanel);
        mainContentPanel.add(appointmentLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(backgroundColor);
        
        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 6, false);
        cancelButton.setPreferredSize(new Dimension(70, 32));
        cancelButton.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        
        JButton confirmButton = createRoundedButton("Xác nhận xóa", accentColor, buttonTextColor, 6, false);
        confirmButton.setPreferredSize(new Dimension(110, 32));
        confirmButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
        return confirmed[0];
    }
 // Dialog xác nhận xóa đơn giản
    private boolean showSimpleDeleteConfirmationDialog(BacSi currentDoctor) {
        JDialog dialog = new JDialog(parentFrame, "Xác nhận xóa bác sĩ", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        final boolean[] confirmed = {false};
        
        // Xử lý sự kiện đóng dialog
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed[0] = false;
                dialog.dispose();
            }
        });
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel("XÁC NHẬN XÓA BÁC SĨ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        mainContentPanel.setBackground(Color.WHITE);
        
        // Cảnh báo
        JLabel warningLabel = new JLabel("Bạn đang thực hiện hành động không thể hoàn tác!");
        warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        warningLabel.setForeground(accentColor);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        
        // Thông tin bác sĩ
        JPanel doctorPanel = createCompactInfoPanel("BÁC SĨ SẼ BỊ XÓA", 
            currentDoctor.getHoTenBacSi(), currentDoctor.getChuyenKhoa(), 
            currentDoctor.getTenPhong(), currentDoctor.getKinhNghiem(), accentColor);
        
        // Thông báo không có lịch hẹn
        JLabel noAppointmentLabel = new JLabel("Bác sĩ này không có lịch hẹn trong tương lai.");
        noAppointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noAppointmentLabel.setForeground(new Color(108, 117, 125));
        noAppointmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noAppointmentLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        mainContentPanel.add(warningLabel);
        mainContentPanel.add(doctorPanel);
        mainContentPanel.add(noAppointmentLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(backgroundColor);
        
        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 6, false);
        cancelButton.setPreferredSize(new Dimension(70, 32));
        cancelButton.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        
        JButton confirmButton = createRoundedButton("Xác nhận xóa", accentColor, buttonTextColor, 6, false);
        confirmButton.setPreferredSize(new Dimension(110, 32));
        confirmButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
        return confirmed[0];
    }
    // Tạo info panel compact hơn
    private JPanel createCompactInfoPanel(String title, String doctorName, String specialty, 
                                         String room, int experience, Color borderColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1), // Giảm border width
            new EmptyBorder(10, 12, 10, 12) // Giảm padding
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(450, 90)); // Giảm chiều cao
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Giảm font size
        titleLabel.setForeground(borderColor);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(doctorName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Giảm font size
        nameLabel.setForeground(textColor);
        
        JLabel detailsLabel = new JLabel(String.format("%s | %s | KN: %d năm", specialty, room, experience));
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Gộp thông tin và giảm font size
        detailsLabel.setForeground(textColor);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(detailsLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
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