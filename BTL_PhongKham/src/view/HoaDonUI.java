package view;

import controller.HoaDonController;
import controller.BenhNhanController;
import controller.DoanhThuController;
import model.HoaDon;
import model.ThanhToanBenhNhan;
import util.ExportManager;
import util.ExportManager.MessageCallback;
import view.DoanhThuUI.NotificationType;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HoaDonUI extends JPanel implements MessageCallback {
    // Color scheme based on DoanhThuUI
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
    
    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);
    
    // Controllers
    private HoaDonController hoaDonController;
    private BenhNhanController benhNhanController;
    
    // UI Components
    private DefaultTableModel modelHoaDon;
    private JTable tableHoaDon;
    private JTable tableTotalRow;
    private DefaultTableModel tableModel;
    private DefaultTableModel modelTotalRow;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnThem;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;
    private NumberFormat currencyFormat;
    private double totalAmount = 0.0;
    private ExportManager exportManager;
    private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
    public HoaDonUI() {
        initializeControllers();
        initializePanel();
        initializeFormatters();
        buildHeaderPanel();
        buildTablePanel();
        buildButtonPanel();
        setupEventListeners();
        setupPopupMenu();
        
        // Load initial data
        loadTableData();
    }
    
    /**
     * Initializes controllers
     */
    private void initializeControllers() {
        hoaDonController = new HoaDonController();
        benhNhanController = new BenhNhanController();
        exportManager = new ExportManager(this, modelHoaDon, this);
    }
    
    /**
     * Initializes panel properties
     */
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    
    /**
     * Initializes formatters for currency display
     */
    private void initializeFormatters() {
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);
    }
    
    /**
     * Builds the header panel with title and search controls
     */
    private void buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title panel on the left
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ HÓA ĐƠN");
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
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        
        btnTimKiem = createStyledButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void buildTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        // Create a rounded panel that will contain the table
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeTable();
        styleTable();
        
        // Create a panel to contain both main table and total table
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        
        // Main table with scrolling
        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Total row table fixed at bottom
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(tableTotalRow, BorderLayout.CENTER);
        
        tablesContainer.add(scrollPane, BorderLayout.CENTER);
        tablesContainer.add(totalPanel, BorderLayout.SOUTH);
        
        tablePanel.add(tablesContainer, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initializes the table with appropriate columns
     */
    private void initializeTable() {
        String[] columns = {"ID", "ID Bệnh Nhân", "Tên Bệnh Nhân", "Ngày Tạo", "Tổng Tiền", "Trạng Thái"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) {
                    return Integer.class;
                } else if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
        };
        
        tableHoaDon = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                return comp;
            }
        };
        
        sorter = new TableRowSorter<>(tableModel);
        tableHoaDon.setRowSorter(sorter);
        
        // Create the total row table
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
        
        // Add total row
        modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", 0.0, null});
    }
    
    /**
     * Applies styling to the tables
     */
    private void styleTable() {
        // Style main table
        styleMainTable(tableHoaDon);
        
        // Style total row table
        styleMainTable(tableTotalRow);
        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45); // Make total row taller
        tableTotalRow.setTableHeader(null); // No header for total table
        
        // Format the total row
        tableTotalRow.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setBackground(totalRowColor);
                
                // Set alignment based on column
                if (column == 3) {
                    // "Tổng:" column - right align
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    ((JLabel) c).setFont(totalRowFont);
                } else if (column == 4 && value instanceof Double) {
                    // Format total amount column
                    ((JLabel) c).setText(currencyFormat.format((Double) value) + " VND");
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setFont(totalRowFont);
                } else {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                // Add padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }
    
    /**
     * Applies common styling to tables
     */
    /**
     * Applies common styling to tables
     */
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
        
        // Only style header for main table
        if (table == tableHoaDon) {
            // Style table header
            JTableHeader header = table.getTableHeader();
            header.setFont(tableHeaderFont);
            header.setBackground(tableHeaderColor);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getWidth(), 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
            header.setReorderingAllowed(false);
            
            // Center align table headers
            ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        // Set column widths - proportional to content
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // ID Bệnh Nhân
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Tên Bệnh Nhân
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Ngày Tạo
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Tổng Tiền
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Trạng Thái
        
        // Cell renderer for formatting - Đảm bảo căn giữa tất cả các cell
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                
                // Center align all cells
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                
                // Format currency for column 4 (Tổng Tiền)
                if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value) + " VND");
                }
                
                // Add padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        
        // Explicitly center align all cell renderers for each column type
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Make sure number columns are centered
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer();
        numberRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(numberRenderer); // ID
        table.getColumnModel().getColumn(1).setCellRenderer(numberRenderer); // ID Bệnh Nhân
        
        // Special renderer for currency column
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value instanceof Double) {
                    setText(currencyFormat.format((Double) value) + " VND");
                }
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                
                return c;
            }
        };
        
        // Apply currency renderer to the total amount column
        table.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer); // Tổng Tiền
    }
    
    /**
     * Builds the button panel at the bottom
     */
    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));

        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> {
            try {
                hienThiFormThemHoaDon();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showNotification("Lỗi khi hiển thị form thêm hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
        
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets up event listeners for table and controls
     */
    private void setupEventListeners() {
        btnTimKiem.addActionListener(e -> filterTable());
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterTable();
                }
            }
        });
        
        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tableHoaDon.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableHoaDon.getRowCount()) {
                    tableHoaDon.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        showPopupMenu(e);
                    } else if (e.getClickCount() == 2) {
                        xemChiTietHoaDon();
                    }
                } else {
                    tableHoaDon.clearSelection();
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

        menuItemXemChiTiet.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                xemChiTietHoaDon();
            }
        });

        menuItemSua.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                try {
					suaHoaDon();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        
        menuItemXoa.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                xoaHoaDon();
            }
        });
    }
    
    /**
     * Shows the popup menu at the specified location
     */
    private void showPopupMenu(MouseEvent e) {
        if (tableHoaDon.getSelectedRow() >= 0) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
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
     * Darkens a color for hover effects
     */
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    
    /**
     * Loads table data from controller
     */
    public void loadTableData() {
        tableModel.setRowCount(0);
        totalAmount = 0.0;
        
        List<HoaDon> danhSach = hoaDonController.layDanhSachHoaDon();
        for (HoaDon hd : danhSach) {
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hd.getIdBenhNhan());
            tableModel.addRow(new Object[]{
                hd.getIdHoaDon(),
                hd.getIdBenhNhan(),
                benhNhan != null ? benhNhan.getHoTen() : "N/A",
                dateFormatter.format(hd.getNgayTao()),  // Format date consistently here
                hd.getTongTien(),
                hd.getTrangThai()
            });
            
            totalAmount += hd.getTongTien();
        }
        
        // Update total row
        updateTotalRow();
    }
    
    /**
     * Updates the total row to show the current sum
     */
    private void updateTotalRow() {
        if (modelTotalRow.getRowCount() == 0) {
            modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", totalAmount, null});
        } else {
            modelTotalRow.setValueAt("Tổng:", 0, 3);
            modelTotalRow.setValueAt(totalAmount, 0, 4);
        }
        tableTotalRow.repaint();
    }
    
    /**
     * Filters the table based on search text
     */
    private void filterTable() {
        String text = txtTimKiem.getText().trim();
        if (text.isEmpty()) {
            // If search field is empty, refresh data instead of just clearing filter
            sorter.setRowFilter(null);
            loadTableData(); // Refresh data
            showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    /**
     * Handles the view details action
     */
    private void xemChiTietHoaDon() {
        int selectedRow = tableHoaDon.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupChiTiet(hoaDon);
            } else {
                showNotification("Không tìm thấy hóa đơn với ID: " + idHoaDon, NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để xem chi tiết!", NotificationType.WARNING);
        }
    }
    
    /**
     * Handles the edit action
     * @throws SQLException 
     */
    private void suaHoaDon() throws SQLException {
        int selectedRow = tableHoaDon.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupSua(hoaDon);
            } else {
                showNotification("Không tìm thấy hóa đơn với ID: " + idHoaDon, NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để chỉnh sửa!", NotificationType.WARNING);
        }
    }
    
    /**
     * Handles the delete action
     */
    private void xoaHoaDon() {
        int selectedRow = tableHoaDon.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            
            int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn xóa hóa đơn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    hoaDonController.xoaHoaDon(idHoaDon);
                    loadTableData();
                    showNotification("Đã xóa hóa đơn thành công!", NotificationType.SUCCESS);
                } catch (Exception ex) {
                    showNotification("Lỗi khi xóa hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
                }
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để xóa!", NotificationType.WARNING);
        }
    }
    
    /**
     * Show dialog to add a new invoice
     */
    private void hienThiFormThemHoaDon() throws SQLException {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm Hóa Đơn Mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("THÊM HÓA ĐƠN MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Lấy danh sách bệnh nhân từ controller
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement("Chọn bệnh nhân");
        
        for (BenhNhan bn : danhSachBenhNhan) {
            comboBoxModel.addElement(bn.getHoTen());
        }
        
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(comboBoxModel);
        styleComboBox(cmbTenBenhNhan);
        
        JTextField txtTongTien = new JTextField();
        styleTextField(txtTongTien);
        
        // Format date field with placeholder text
        JTextField txtNgayTao = new JTextField();
        txtNgayTao.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtNgayTao.setToolTipText("Định dạng: YYYY-MM-DD");
        styleTextField(txtNgayTao);
        
        // Thay đổi từ JTextField sang JComboBox cho trạng thái
        String[] trangThaiOptions = {"Đã thanh toán", "Chưa thanh toán", "Đang xử lý", "Đã hủy"};
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiOptions);
        styleComboBox(cmbTrangThai);
        
        // Create form fields
        addFormField(formPanel, "Tên Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Ngày Tạo:", txtNgayTao);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        
        formPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton cancelButton = createRoundedButton("Hủy", Color.WHITE, textColor, 10);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        submitButton.addActionListener(e -> {
            try {
                String tenBenhNhanDaChon = (String) cmbTenBenhNhan.getSelectedItem();
                
                // Validation
                if (tenBenhNhanDaChon == null || tenBenhNhanDaChon.equals("Chọn bệnh nhân")) {
                    showNotification("Vui lòng chọn tên bệnh nhân.", NotificationType.WARNING);
                    return;
                }
                
                if (txtTongTien.getText().trim().isEmpty()) {
                    showNotification("Vui lòng nhập tổng tiền.", NotificationType.WARNING);
                    return;
                }
                
                // Validate date format
                String ngayTaoStr = txtNgayTao.getText().trim();
                Date ngayTao;
                try {
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.setLenient(false);
                    ngayTao = dateFormat.parse(ngayTaoStr);
                } catch (Exception ex) {
                    showNotification("Vui lòng nhập ngày tạo đúng định dạng YYYY-MM-DD.", NotificationType.WARNING);
                    return;
                }
                
                // Lấy trạng thái từ combobox
                String trangThaiUI = (String) cmbTrangThai.getSelectedItem();
                
                // Chuyển đổi trạng thái từ UI sang định dạng trong Controller
                String trangThaiController;
                switch (trangThaiUI) {
                    case "Đã thanh toán":
                        trangThaiController = "DaThanhToan";
                        break;
                    case "Chưa thanh toán":
                        trangThaiController = "ChuaThanhToan";
                        break;
                    case "Đang xử lý":
                        trangThaiController = "DangXuLy";
                        break;
                    case "Đã hủy":
                        trangThaiController = "DaHuy";
                        break;
                    default:
                        trangThaiController = "ChuaThanhToan";
                }
                
                // Tìm ID bệnh nhân dựa trên tên đã chọn
                BenhNhan benhNhanDaTim = null;
                for (BenhNhan bn : danhSachBenhNhan) {
                    if (bn.getHoTen().equals(tenBenhNhanDaChon)) {
                        benhNhanDaTim = bn;
                        break;
                    }
                }
                
                if (benhNhanDaTim == null) {
                    showNotification("Không tìm thấy ID bệnh nhân cho tên đã chọn.", NotificationType.ERROR);
                    return;
                }
                
                int idBenhNhan = benhNhanDaTim.getIdBenhNhan();
                double tongTien = Double.parseDouble(txtTongTien.getText().trim());
                
                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdBenhNhan(idBenhNhan);
                hoaDon.setNgayTao(ngayTao);
                hoaDon.setTongTien(tongTien);
                hoaDon.setTrangThai(trangThaiController);
                
                // Thêm hóa đơn (HoaDonController sẽ tự động cập nhật DoanhThu nếu trạng thái là "DaThanhToan")
                hoaDonController.themHoaDon(hoaDon);
                
                // Nếu trạng thái là "Đã thanh toán", thêm bản ghi thanh toán
                if (trangThaiController.equals("DaThanhToan")) {
                    try {
                        // Tạo đối tượng ThanhToanBenhNhan
                        ThanhToanBenhNhan thanhToan = new ThanhToanBenhNhan();
                        thanhToan.setIdHoaDon(hoaDon.getIdHoaDon());  // Lấy ID hóa đơn sau khi đã thêm
                        thanhToan.setSoTien(tongTien);
                        thanhToan.setHinhThucThanhToan("Tiền mặt");  // Mặc định hoặc thêm field để chọn
                        thanhToan.setMaQR("");  // Để trống nếu là tiền mặt
                        thanhToan.setTrangThai("ThanhToanThanhCong");
                        
                        // Thêm vào bảng thanh toán
                        hoaDonController.themThanhToan(thanhToan);
                    } catch (Exception ex) {
                        System.err.println("Warning: Không thể thêm thanh toán: " + ex.getMessage());
                        showNotification("Lưu ý: Hóa đơn đã được tạo nhưng có lỗi khi thêm thông tin thanh toán.", NotificationType.WARNING);
                    }
                }
                
                loadTableData();
                dialog.dispose();
                showNotification("Thêm hóa đơn thành công!", NotificationType.SUCCESS);
            } catch (NumberFormatException ex) {
                showNotification("Vui lòng nhập đúng định dạng số cho Tổng Tiền.", NotificationType.ERROR);
            } catch (Exception ex) {
                showNotification("Lỗi khi thêm hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Add all panels to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    
    /**
     * Displays a detail popup for the selected invoice
     */
    private void hienThiPopupChiTiet(HoaDon hoaDon) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết Hóa Đơn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 450);
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("CHI TIẾT HÓA ĐƠN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Detail panel
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailPanel.setBackground(Color.WHITE);
        
        BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        ThanhToanBenhNhan thanhToan = hoaDonController.layThanhToanTheoIdHoaDon(hoaDon.getIdHoaDon());
        
        // Create detail fields
        addDetailField(detailPanel, "ID Hóa Đơn:", String.valueOf(hoaDon.getIdHoaDon()));
        addDetailField(detailPanel, "ID Bệnh Nhân:", String.valueOf(hoaDon.getIdBenhNhan()));
        addDetailField(detailPanel, "Tên Bệnh Nhân:", benhNhan != null ? benhNhan.getHoTen() : "N/A");
        addDetailField(detailPanel, "Ngày Tạo:", hoaDon.getNgayTao().toString());
        addDetailField(detailPanel, "Tổng Tiền:", currencyFormat.format(hoaDon.getTongTien()) + " VND");
        addDetailField(detailPanel, "Trạng Thái:", hoaDon.getTrangThai());
        addDetailField(detailPanel, "Phương thức TT:", thanhToan != null ? thanhToan.getHinhThucThanhToan() : "Chưa có");
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10);
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        
        // Add all panels to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(detailPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    
    /**
     * Displays an edit popup for the selected invoice
     * @throws SQLException 
     */
    private void hienThiPopupSua(HoaDon hoaDon) throws SQLException {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa Hóa Đơn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("CHỈNH SỬA HÓA ĐƠN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Create read-only fields
        JTextField txtIdHoaDon = new JTextField(String.valueOf(hoaDon.getIdHoaDon()));
        txtIdHoaDon.setEnabled(false);
        txtIdHoaDon.setBackground(new Color(245, 245, 245));
        
        // Lấy danh sách bệnh nhân từ controller
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        
        BenhNhan benhNhanHienTai = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        String tenBenhNhanHienTai = benhNhanHienTai != null ? benhNhanHienTai.getHoTen() : "N/A";
        
        comboBoxModel.addElement(tenBenhNhanHienTai);
        for (BenhNhan bn : danhSachBenhNhan) {
            if (!bn.getHoTen().equals(tenBenhNhanHienTai)) {
                comboBoxModel.addElement(bn.getHoTen());
            }
        }
        
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(comboBoxModel);
        styleComboBox(cmbTenBenhNhan);
        
        JTextField txtTongTien = new JTextField(String.valueOf(hoaDon.getTongTien()));
        styleTextField(txtTongTien);
        
        // Thay đổi từ JTextField sang JComboBox cho trạng thái
        String[] trangThaiOptions = {"Đã thanh toán", "Chưa thanh toán", "Đang xử lý", "Đã hủy"};
        DefaultComboBoxModel<String> trangThaiModel = new DefaultComboBoxModel<>(trangThaiOptions);
        
        // Đặt trạng thái hiện tại làm mục được chọn mặc định
        String currentTrangThai = hoaDon.getTrangThai();
        boolean found = false;
        for (String option : trangThaiOptions) {
            if (option.equals(currentTrangThai)) {
                found = true;
                break;
            }
        }
        if (!found) {
            trangThaiModel.insertElementAt(currentTrangThai, 0);
        }
        
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiModel);
        cmbTrangThai.setSelectedItem(currentTrangThai);
        styleComboBox(cmbTrangThai);
        
        // Create form fields
        addFormField(formPanel, "ID Hóa Đơn:", txtIdHoaDon);
        addFormField(formPanel, "Tên Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        
        formPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton cancelButton = createRoundedButton("Hủy", Color.WHITE, textColor, 10);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createRoundedButton("Cập nhật", warningColor, buttonTextColor, 10);
        submitButton.addActionListener(e -> {
            try {
                String tenBenhNhanDaChon = (String) cmbTenBenhNhan.getSelectedItem();
                
                // Tìm ID bệnh nhân dựa trên tên đã chọn
                BenhNhan benhNhanDaTim = null;
                for (BenhNhan bn : danhSachBenhNhan) {
                    if (bn.getHoTen().equals(tenBenhNhanDaChon)) {
                        benhNhanDaTim = bn;
                        break;
                    }
                }
                
                if (benhNhanDaTim != null) {
                    hoaDon.setIdBenhNhan(benhNhanDaTim.getIdBenhNhan());
                }
                
                hoaDon.setTongTien(Double.parseDouble(txtTongTien.getText().trim()));
                // Lấy trạng thái từ combobox thay vì text field
                hoaDon.setTrangThai((String) cmbTrangThai.getSelectedItem());
                
                hoaDonController.capNhatHoaDon(hoaDon);
                loadTableData();
                dialog.dispose();
                showNotification("Cập nhật hóa đơn thành công!", NotificationType.SUCCESS);
            } catch (NumberFormatException ex) {
                showNotification("Vui lòng nhập đúng định dạng số cho Tổng Tiền.", NotificationType.ERROR);
            } catch (Exception ex) {
                showNotification("Lỗi khi cập nhật hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Add all panels to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    
    /**
     * Helper method to add form fields with consistent styling
     */
    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setPreferredSize(new Dimension(150, 30));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
    }
    
    /**
     * Helper method to add detail fields with consistent styling
     */
    private void addDetailField(JPanel panel, String labelText, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(150, 30));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(regularFont);
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    /**
     * Styles a text field with consistent formatting
     */
    private void styleTextField(JTextField textField) {
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
    }
    
    /**
     * Styles a combo box with consistent formatting
     */
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(regularFont);
        comboBox.setPreferredSize(new Dimension(200, 35));
        ((JComponent) comboBox.getRenderer()).setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
    }
    
    /**
     * Shows a styled notification popup
     */
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
    
    /**
     * Enum for notification types
     */
    private enum NotificationType {
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
    
    /**
     * Custom rounded border for components
     */
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
    
    /**
     * Panel with rounded corners
     */
    /**
     * Panel with rounded corners and enhanced shadow effect
     */
    /**
     * Panel with rounded corners and subtle shadow effect
     */
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean hasShadow;
        private int shadowSize = 3;  // Reduced from 5
        private int shadowOpacity = 20;  // Reduced from 60 to create a very light shadow
        
        public RoundedPanel(int radius, boolean hasShadow) {
            super();
            this.cornerRadius = radius;
            this.hasShadow = hasShadow;
            setOpaque(false);
            
            // Add minimal padding to accommodate the shadow
            if (hasShadow) {
                setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow if enabled - with very light opacity
            if (hasShadow) {
                // Create multiple layers of shadow with reduced opacity
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = shadowOpacity / 255f * (shadowSize - i) / shadowSize;
                    // Use a lighter color like light gray instead of black
                    g2.setColor(new Color(120, 120, 120, (int)(opacity * 255)));
                    g2.fill(new RoundRectangle2D.Float(
                        i, i, 
                        getWidth() - (i * 2), 
                        getHeight() - (i * 2), 
                        cornerRadius + i, cornerRadius + i));
                }
            }
            
            // Draw panel background
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(
                shadowSize, shadowSize, 
                getWidth() - (shadowSize * 2), 
                getHeight() - (shadowSize * 2), 
                cornerRadius, cornerRadius));
            
            // Draw a very light border
            g2.setColor(new Color(222, 226, 230)); // Very light gray border
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Float(
                shadowSize, shadowSize, 
                getWidth() - (shadowSize * 2) - 1, 
                getHeight() - (shadowSize * 2) - 1, 
                cornerRadius, cornerRadius));
            
            g2.dispose();
        }
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
}