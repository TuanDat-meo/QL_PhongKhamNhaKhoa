package view;

import controller.DoanhThuController;
import util.DataChangeListener;
import util.ExportManager;
import util.ExportManager.MessageCallback;
import util.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;

import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoanhThuUI extends JPanel implements MessageCallback, DataChangeListener {
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
    private Color totalRowColor = new Color(232, 240, 254);   // Light blue for total row
    
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);
    
    private DefaultTableModel modelDoanhThu;
    private JTable tableDoanhThu;
    private JTable tableTotalRow;
    private DefaultTableModel modelTotalRow;
    private JButton btnXuatFile; // Thêm biến này
    private JButton btnThemMoiDoanhThu;
    private JTextField txtTimKiemDoanhThu;
    private JButton btnTimKiemDoanhThu;
    private TableRowSorter<DefaultTableModel> sorterDoanhThu;
    private JPopupMenu popupMenuDoanhThu;
    private JMenuItem menuItemSuaDoanhThu;
    private JMenuItem menuItemXoaDoanhThu;
    private JMenuItem menuItemXemChiTiet;
    private JFrame parentFrame;
    private double totalRevenue = 0;
    private List<Object[]> originalData = new ArrayList<>();
    // Business Logic
    private DoanhThuController doanhThuController;
    private NumberFormat currencyFormat;
    private ExportManager exportManager;
    
    public DoanhThuUI() {
        initializePanel();
        initializeFormatters();
        // Khởi tạo bảng trước để đảm bảo modelDoanhThu không null
        buildTablePanel();
        initializeController();
        buildHeaderPanel();
        buildButtonPanel();
        setupEventListeners();
        setupPopupMenu();
        // Vô hiệu hóa nút Xuất file ban đầu
        btnXuatFile.setEnabled(false);
        SwingUtilities.invokeLater(() -> {
            doanhThuController.loadDoanhThuData();
            // Kích hoạt nút Xuất file sau khi dữ liệu được tải
            btnXuatFile.setEnabled(modelDoanhThu.getRowCount() > 0);
        });
    }

    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20,20, 20, 20));
    }

    private void initializeFormatters() {
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);
    }

    private void initializeController() {
        doanhThuController = new DoanhThuController(this);
        if (modelDoanhThu == null) {
            throw new IllegalStateException("modelDoanhThu chưa được khởi tạo!");
        }
        exportManager = new ExportManager(this, modelDoanhThu, this);
    }

    private void buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ DOANH THU");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        txtTimKiemDoanhThu = new JTextField(18);
        txtTimKiemDoanhThu.setFont(regularFont);
        
        txtTimKiemDoanhThu.setPreferredSize(new Dimension(220, 38));
        txtTimKiemDoanhThu.setBorder(BorderFactory.createCompoundBorder(
                new util.CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        
        btnTimKiemDoanhThu = createStyledButton("Tìm kiếm");
        btnTimKiemDoanhThu.setPreferredSize(new Dimension(120, 38));
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiemDoanhThu);
        searchPanel.add(btnTimKiemDoanhThu);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeTable();
        styleTable();
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tableDoanhThu);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(tableTotalRow, BorderLayout.CENTER);
        
        tablesContainer.add(scrollPane, BorderLayout.CENTER);
        tablesContainer.add(totalPanel, BorderLayout.SOUTH);
        
        tablePanel.add(tablesContainer, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }

    private void initializeTable() {
        String[] columns = {"ID", "ID Hóa Đơn", "Tên Bệnh Nhân", "Tháng/Năm", "Tổng Thu", "Trạng Thái"};
        
        modelDoanhThu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) {
                    return Integer.class; // ID columns are integers
                } else if (columnIndex == 4) {
                    return Double.class; // Total column is double
                }
                return String.class; // Default to String
            }
        };
        
        tableDoanhThu = new JTable(modelDoanhThu) {
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
        sorterDoanhThu = new TableRowSorter<>(modelDoanhThu);
        tableDoanhThu.setRowSorter(sorterDoanhThu);
        modelTotalRow = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTotalRow = new JTable(modelTotalRow) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                comp.setBackground(totalRowColor);
                return comp;
            }
        };
        modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", 0.0, null});
    }

    private void styleTable() {
        styleMainTable(tableDoanhThu);
        styleMainTable(tableTotalRow);
        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45);
        tableTotalRow.setTableHeader(null); 
        tableTotalRow.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setBackground(totalRowColor);
                if (column == 3) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    ((JLabel) c).setFont(totalRowFont);
                } else if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value) + " VND");
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setFont(totalRowFont);
                } else {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    public void updateTotalRow(double totalAmount) {
        if (modelTotalRow.getRowCount() == 0) {
            modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", totalAmount, null});
        } else {
            modelTotalRow.setValueAt("Tổng:", 0, 3);
            modelTotalRow.setValueAt(totalAmount, 0, 4);
        }
        tableTotalRow.repaint();
    }

    private void styleMainTable(JTable table) {
        table.setFont(tableFont);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(229, 243, 255));
        table.setSelectionForeground(textColor);
        table.setFocusable(false);
        table.setAutoCreateRowSorter(true);
        table.setBorder(null);
        if (table == tableDoanhThu) {
            JTableHeader header = table.getTableHeader();
            header.setFont(tableHeaderFont);
            header.setBackground(tableHeaderColor);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getWidth(), 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
            header.setReorderingAllowed(false);
            ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // ID Hóa Đơn
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Tên Bệnh Nhân
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Tháng/Năm
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Tổng Thu
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Trạng Thái
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                
                // Set horizontal alignment to CENTER for all cells
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                
                // Format currency for column 4 (Tổng Thu)
                if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value) + " VND");
                }
                
                // Keep some padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Create Export Button
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> {
            if (modelDoanhThu == null || modelDoanhThu.getRowCount() == 0) {
                showNotification("Không có dữ liệu để xuất!", NotificationType.WARNING);
                return;
            }
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        });

        // Create Add Button
        btnThemMoiDoanhThu = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThemMoiDoanhThu.setPreferredSize(new Dimension(100, 45));
        btnThemMoiDoanhThu.addActionListener(e -> showThemMoiDialog());
        
        // Add buttons to panel
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThemMoiDoanhThu);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a styled button with hover effects
     */
    private JButton createStyledButton(String text) {
        return createRoundedButton(text, primaryColor, buttonTextColor, 10);
    }

    /**
     * Creates a rounded button with hover effects
     */
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

    /**
     * Darkens a color for hover effects
     */
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }

    /**
     * Sets up event listeners for table and controls
     */
    private void setupEventListeners() {
        // Setup search button action
        btnTimKiemDoanhThu.addActionListener(e -> {
            // Check if search field is empty
            if (txtTimKiemDoanhThu.getText().trim().isEmpty()) {
                // If empty, refresh the data
                refreshData();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                // If not empty, filter the data
                filterDoanhThu();
            }
        });
        
        // Setup enter key on search field
        txtTimKiemDoanhThu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiemDoanhThu.getText().trim().isEmpty()) {
                        // If empty, refresh the data
                        refreshData();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
                        // If not empty, filter the data
                        filterDoanhThu();
                    }
                }
            }
        });
        
        tableDoanhThu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tableDoanhThu.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableDoanhThu.getRowCount()) {
                    tableDoanhThu.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        showPopupMenu(e);
                    } else if (e.getClickCount() == 2) {
                        xemChiTietDoanhThuAction();
                    }
                } else {
                    tableDoanhThu.clearSelection();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
    }
    
    /**
     * Sets up the popup menu for table row actions
     */
    private void setupPopupMenu() {
        popupMenuDoanhThu = new JPopupMenu();
        popupMenuDoanhThu.setBorder(new LineBorder(borderColor, 1));
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSuaDoanhThu = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoaDoanhThu = createStyledMenuItem("Xóa");
        
        menuItemXoaDoanhThu.setForeground(accentColor);
        
        popupMenuDoanhThu.add(menuItemXemChiTiet);
        popupMenuDoanhThu.addSeparator();
        popupMenuDoanhThu.add(menuItemSuaDoanhThu);
        popupMenuDoanhThu.addSeparator();
        popupMenuDoanhThu.add(menuItemXoaDoanhThu);

        menuItemXemChiTiet.addActionListener(e -> {
            if (tableDoanhThu.getSelectedRow() != -1) {
                xemChiTietDoanhThuAction();
            }
        });

        menuItemSuaDoanhThu.addActionListener(e -> {
            if (tableDoanhThu.getSelectedRow() != -1) {
                suaDoanhThuAction();
            }
        });
        
        menuItemXoaDoanhThu.addActionListener(e -> {
            if (tableDoanhThu.getSelectedRow() != -1) {
                xoaDoanhThuAction();
            }
        });
    }

    /**
     * Creates a styled menu item with hover effects
     */
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(textColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        
        return menuItem;
    }
    
    /**
     * Shows the popup menu at the specified location
     */
    private void showPopupMenu(MouseEvent e) {
        if (tableDoanhThu.getSelectedRow() >= 0) {
            popupMenuDoanhThu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    /**
     * Handles the view details action
     */
    private void xemChiTietDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableDoanhThu.convertRowIndexToModel(selectedRow);
            
            // Get values with proper type handling
            int idDoanhThu = (int) modelDoanhThu.getValueAt(modelRow, 0);
            int idHoaDon = (int) modelDoanhThu.getValueAt(modelRow, 1);
            String tenBenhNhan = modelDoanhThu.getValueAt(modelRow, 2).toString();
            String thangNam = modelDoanhThu.getValueAt(modelRow, 3).toString();
            
            // Handle possible Double value for tongThu
            Object tongThuObj = modelDoanhThu.getValueAt(modelRow, 4);
            String tongThu;
            if (tongThuObj instanceof Double) {
                tongThu = currencyFormat.format((Double) tongThuObj) + " VND";
            } else {
                tongThu = tongThuObj.toString();
            }
            
            String trangThai = modelDoanhThu.getValueAt(modelRow, 5).toString();
            
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                showChiTietDoanhThuDialog(topFrame, idDoanhThu, idHoaDon, tenBenhNhan, thangNam, tongThu, trangThai);
            } else {
                showNotification("Không tìm thấy cửa sổ cha!", NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để xem chi tiết!", NotificationType.WARNING);
        }
    }

    private void showThemMoiDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) {
            showNotification("Không tìm thấy cửa sổ cha!", NotificationType.ERROR);
            return;
        }
        
        // Create dialog
        JDialog dialog = new JDialog(topFrame, "Thêm Mới Doanh Thu", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("THÊM MỚI DOANH THU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Create form fields
        JTextField txtIdHoaDon = new JTextField(20);
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("MM/yyyy");
        
        // Readonly text field for total amount
        JTextField txtTongThu = new JTextField(20);
        txtTongThu.setEditable(false);
        txtTongThu.setBackground(new Color(240, 240, 240));
        
        // Add form fields to panel
        addFormField(formPanel, "ID Hóa Đơn:", txtIdHoaDon);
        addFormField(formPanel, "Tháng/Năm:", dateChooser);
        addFormField(formPanel, "Tổng Thu:", txtTongThu);
        
        // Add an action listener to automatically fetch invoice amount when ID changes
        txtIdHoaDon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (!txtIdHoaDon.getText().trim().isEmpty()) {
                        int idHoaDon = Integer.parseInt(txtIdHoaDon.getText().trim());
                        // Call controller to get invoice amount
                        double invoiceAmount = doanhThuController.getHoaDonAmount(idHoaDon);
                        if (invoiceAmount > 0) {
                            txtTongThu.setText(currencyFormat.format(invoiceAmount) + " VND");
                            txtTongThu.setCaretPosition(0);
                        } else {
                            txtTongThu.setText("Không tìm thấy hóa đơn");
                        }
                    } else {
                        txtTongThu.setText("");
                    }
                } catch (NumberFormatException ex) {
                    txtTongThu.setText("ID không hợp lệ");
                }
            }
        });
        
        formPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (txtIdHoaDon.getText().trim().isEmpty() || dateChooser.getDate() == null) {
                showNotification("Vui lòng điền đầy đủ thông tin!", NotificationType.WARNING);
                return;
            }
            
            try {
                int idHoaDon = Integer.parseInt(txtIdHoaDon.getText().trim());
                Date thangNam = dateChooser.getDate();
                
                // Get invoice amount from controller
                double tongThu = doanhThuController.getHoaDonAmount(idHoaDon);
                
                if (tongThu <= 0) {
                    showNotification("Không tìm thấy hóa đơn hoặc hóa đơn không hợp lệ!", NotificationType.ERROR);
                    return;
                }
                
                // Call controller with updated parameters
                boolean success = doanhThuController.themDoanhThu(thangNam, tongThu, idHoaDon);
                
                if (success) {
                    dialog.dispose();
                    showNotification("Thêm doanh thu thành công!", NotificationType.SUCCESS);
                }
            } catch (NumberFormatException ex) {
                showNotification("ID hóa đơn không hợp lệ! Vui lòng kiểm tra lại.", NotificationType.ERROR);
            }
        });
        
        JButton cancelButton = createRoundedButton("Hủy", Color.WHITE, textColor, 10);
        cancelButton.setBorder(new LineBorder(borderColor, 1));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(topFrame);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(450, 60));
        fieldPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setPreferredSize(new Dimension(180, 30));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showChiTietDoanhThuDialog(JFrame parent, int idDoanhThu, int idHoaDon, 
            String tenBenhNhan, String thangNam, String tongThu, String trangThai) {
        JDialog dialog = new JDialog(parent, "Chi Tiết Doanh Thu", true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("CHI TIẾT DOANH THU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Mã doanh thu: " + idDoanhThu);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanelWrapper = new JPanel(new BorderLayout());
        titlePanelWrapper.setBackground(primaryColor);
        titlePanelWrapper.add(titleLabel, BorderLayout.NORTH);
        titlePanelWrapper.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanelWrapper, BorderLayout.CENTER);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);
        
        addDetailField(detailsPanel, "ID Doanh Thu:", String.valueOf(idDoanhThu));
        addDetailField(detailsPanel, "ID Hóa Đơn:", String.valueOf(idHoaDon));
        addDetailField(detailsPanel, "Tên Bệnh Nhân:", tenBenhNhan);
        addDetailField(detailsPanel, "Tháng/Năm:", thangNam);
        addDetailField(detailsPanel, "Tổng Thu:", tongThu);
        addDetailField(detailsPanel, "Trạng Thái:", trangThai);
        
        detailsPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton editButton = createRoundedButton("Chỉnh Sửa", warningColor, buttonTextColor, 10);
        editButton.addActionListener(e -> {
            dialog.dispose();
            suaDoanhThuAction();
        });
        
        JButton closeButton = createRoundedButton("Đóng", Color.WHITE, textColor, 10);
        closeButton.setBorder(new LineBorder(borderColor, 1));
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
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
    
    private void suaDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableDoanhThu.convertRowIndexToModel(selectedRow);
            Object[] data = new Object[modelDoanhThu.getColumnCount()];
            for (int i = 0; i < data.length; i++) {
                data[i] = modelDoanhThu.getValueAt(modelRow, i);
            }
            
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                SuaDoanhThuDialog suaDoanhThuDialog = new SuaDoanhThuDialog(topFrame, data, this);
                suaDoanhThuDialog.setVisible(true);
            } else {
                showNotification("Không tìm thấy cửa sổ cha!", NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để sửa!", NotificationType.WARNING);
        }
    }
    
    private void xoaDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableDoanhThu.convertRowIndexToModel(selectedRow);
            int idDoanhThu = (int) modelDoanhThu.getValueAt(modelRow, 0);
            
            int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn xóa doanh thu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                doanhThuController.xoaDoanhThu(idDoanhThu);
                doanhThuController.loadDoanhThuData();
                showNotification("Đã xóa doanh thu thành công!", NotificationType.SUCCESS);
            } else {
                showNotification("Hủy xóa thành công.", NotificationType.SUCCESS);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để xóa!", NotificationType.WARNING);
        }
    }

    private void filterDoanhThu() {
        String searchText = txtTimKiemDoanhThu.getText().trim().toLowerCase();
        
        // If search text is empty, restore all original data
        if (searchText.isEmpty()) {
            restoreOriginalData();
            return;
        }
        
        // Clear the current table data but keep the columns
        modelDoanhThu.setRowCount(0);
        
        // Counter for matching rows
        int matchCount = 0;
        double totalAmount = 0.0;
        
        // Loop through the original data and add only matching rows
        for (Object[] row : originalData) {
            boolean match = false;
            
            // Check each column for a match
            for (int col = 0; col < row.length; col++) {
                if (row[col] != null) {
                    String stringValue;
                    if (row[col] instanceof Double) {
                        stringValue = currencyFormat.format((Double) row[col]);
                    } else {
                        stringValue = row[col].toString();
                    }
                    
                    if (stringValue.toLowerCase().contains(searchText)) {
                        match = true;
                        break;
                    }
                }
            }
            
            // If a match is found, add the row to the table
            if (match) {
                modelDoanhThu.addRow(row);
                matchCount++;
                
                // Add to total for "Tổng Thu" column (index 4)
                if (row[4] instanceof Double) {
                    totalAmount += (Double) row[4];
                }
            }
        }
        
        // Update the total row
        updateTotalRow(totalAmount);
        
        // Force repaint
        tableDoanhThu.repaint();
        // Show notification if no results found
        if (matchCount == 0) {
            showNotification("Không tìm thấy kết quả nào cho: '" + searchText + "'", NotificationType.WARNING);
        }
    }

    private void restoreOriginalData() {
        // Clear the current table
        modelDoanhThu.setRowCount(0);
        
        // Add back all the original data
        double totalAmount = 0.0;
        for (Object[] row : originalData) {
            modelDoanhThu.addRow(row);
            
            // Add to total for "Tổng Thu" column (index 4)
            if (row[4] instanceof Double) {
                totalAmount += (Double) row[4];
            }
        }
        
        // Update the total row
        updateTotalRow(totalAmount);
        
        // Force repaint
        tableDoanhThu.repaint();
    }

    public void clearOriginalData() {
        originalData.clear();
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

    public DoanhThuController getDoanhThuController() {
        return doanhThuController;
    }

    public DefaultTableModel getModelDoanhThu() {
        return modelDoanhThu;
    }

    public void loadDoanhThuData(Object[] rowData) {
        modelDoanhThu.addRow(rowData);
        // Make a copy of the row data and store it
        Object[] dataCopy = Arrays.copyOf(rowData, rowData.length);
        originalData.add(dataCopy);
    }

    public void updateDoanhThuRow(int row, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            modelDoanhThu.setValueAt(rowData[i], row, i);
        }
    }

    public void removeDoanhThuRow(int row) {
        // Remove from model
        modelDoanhThu.removeRow(row);
        
        // Also remove from original data if it exists
        if (row < originalData.size()) {
            originalData.remove(row);
        }
        
        // Recalculate total
        double totalAmount = 0.0;
        for (int i = 0; i < modelDoanhThu.getRowCount(); i++) {
            Object value = modelDoanhThu.getValueAt(i, 4);
            if (value instanceof Double) {
                totalAmount += (Double) value;
            }
        }
        updateTotalRow(totalAmount);
    }

    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }

    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void refreshData() {
        // Clear the current table data
        modelDoanhThu.setRowCount(0);
        
        // Clear original data collection
        clearOriginalData();
        
        // Recalculate total
        totalRevenue = 0.0;
        updateTotalRow(totalRevenue);
        
        // Force table to repaint
        tableDoanhThu.repaint();
        
        // Reload data from controller
        doanhThuController.loadDoanhThuData();
    }

    @Override
    public void onDataChanged() {
        // Khi nhận được thông báo, tải lại dữ liệu
        refreshData();
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        // TODO Auto-generated method stub
    }
}