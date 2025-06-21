package view;

import controller.LuongController;
import model.Luong;
import util.CustomBorder;
import util.DataChangeListener;
import util.ExportManager;
import util.RoundedPanel;
import util.ExportManager.MessageCallback;
import view.DoanhThuUI.NotificationType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LuongUI extends JPanel implements MessageCallback, DataChangeListener {
    // Color scheme
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
    
    // UI Components
    private JTable tblLuong;
    private JTable tableTotalRow;
    private DefaultTableModel modelLuong;
    private DefaultTableModel modelTotalRow;
    private JTextField txtTimKiem;
    private JTextField txtIdLuong;
    private JComboBox<String> cmbNhanVien;
    private JTextField txtLuongCoBan;
    private JTextField txtThuong;
    private JTextField txtKhauTru;
    private JTextField txtTongLuong;
    private JButton btnThem;
    private JButton btnTimKiem;
    private JButton btnXuatFile;
    private com.toedter.calendar.JDateChooser dateThangNam;
    
    private LuongController controller;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private List<Object[]> originalData;
    private JPopupMenu popupMenu;
    private TableRowSorter<DefaultTableModel> sorterLuong;
    private ExportManager exportManager;
    
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
    public LuongUI() {
        initializePanel();
        initializeFormatters();
        initializeFields();
        buildHeaderPanel();
        buildTablePanel();
        buildButtonPanel();
        createPopupMenu();
        
        controller = new LuongController(this);
        exportManager = new ExportManager(this, modelLuong, this);
        // Load data
        SwingUtilities.invokeLater(() -> {
            controller.loadLuongData();
            controller.loadNhanVienComboBox(cmbNhanVien);
        });
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
     * Initialize formatters for currency and date
     */
    private void initializeFormatters() {
        // Currency formatter
        currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        currencyFormat.setMinimumFractionDigits(0);
        
        if (currencyFormat instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) currencyFormat;
            df.applyPattern("###,###");
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumIntegerDigits(15);
            df.setRoundingMode(java.math.RoundingMode.HALF_UP);
        }
        
        // Date formatter
        dateFormat = new SimpleDateFormat("MM/yyyy");
    }

    /**
     * Initialize form fields
     */
    private void initializeFields() {
        originalData = new ArrayList<>();
        txtIdLuong = new JTextField();
        txtLuongCoBan = new JTextField();
        txtThuong = new JTextField(); 
        txtKhauTru = new JTextField();
        txtTongLuong = new JTextField();
        dateThangNam = new com.toedter.calendar.JDateChooser();
        cmbNhanVien = new JComboBox<>();
    }

    /**
     * Build header panel with title and search
     */
    private void buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ LƯƠNG NHÂN VIÊN");
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
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiem();
                }
            }
        });
        
        btnTimKiem = createStyledButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        btnTimKiem.addActionListener(e -> timKiem());
        
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Build table panel with data display
     */
    private void buildTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeTable();
        styleTable();
        
        // Container for both tables
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        
        // Main table with scrolling
        JScrollPane scrollPane = new JScrollPane(tblLuong);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Total row table at bottom
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
     * Initialize tables with appropriate columns
     */
    private void initializeTable() {
        // Define column names
        String[] columns = {"ID", "Tên Nhân Viên", "Tháng/Năm", "Lương cơ bản", "Thưởng", "Khấu trừ", "Tổng lương"};
        
        // Main table model
        modelLuong = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class; // ID column is integer
                } else if (columnIndex >= 3 && columnIndex <= 6) {
                    return Double.class; // Money columns are doubles
                }
                return String.class; // Default to String
            }
        };
        
        tblLuong = new JTable(modelLuong) {
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
        
        // Create and configure the sorter
        sorterLuong = new TableRowSorter<>(modelLuong);
        tblLuong.setRowSorter(sorterLuong);
        
        // Set up mouse listener for row selection and popup menu
        tblLuong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tblLuong.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tblLuong.getRowCount()) {
                    tblLuong.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        showPopupMenu(e);
                    } else if (e.getClickCount() == 2) {
                        xemChiTietLuong();
                    }
                } else {
                    tblLuong.clearSelection();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
        
        // Total row table model
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
        modelTotalRow.addRow(new Object[]{"Tổng", "", "", 0.0, 0.0, 0.0, 0.0});
    }

    /**
     * Apply styling to the tables
     */
    private void styleTable() {
        // Style main table
        styleMainTable(tblLuong);
        
        // Style total row table
        styleMainTable(tableTotalRow);
        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45); // Make total row taller
        tableTotalRow.setTableHeader(null); // No header for total table
        
        // Format the total row
        tableTotalRow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setBackground(totalRowColor);
                
                // Set alignment to center for all columns
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) c).setFont(totalRowFont);
                
                // Format monetary columns
                if (column >= 3 && column <= 6 && value instanceof Number) {
                    ((JLabel) c).setText(currencyFormat.format(((Number) value).doubleValue()) + " VND");
                }
                
                // Add padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    /**
     * Apply common styling to tables
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
        
        // Style header only for main table
        if (table == tblLuong) {
            JTableHeader header = table.getTableHeader();
            header.setFont(tableHeaderFont);
            header.setBackground(tableHeaderColor);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getWidth(), 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
            header.setReorderingAllowed(false);
            ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200);   // Tên Nhân Viên
        table.getColumnModel().getColumn(2).setPreferredWidth(100);   // Tháng/Năm
        table.getColumnModel().getColumn(3).setPreferredWidth(120);   // Lương cơ bản
        table.getColumnModel().getColumn(4).setPreferredWidth(100);   // Thưởng
        table.getColumnModel().getColumn(5).setPreferredWidth(100);   // Khấu trừ
        table.getColumnModel().getColumn(6).setPreferredWidth(120);   // Tổng lương
        
        // Custom cell renderer with center alignment for all columns
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                
                // Set center alignment for all columns
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                
                // Format currency columns
                if (column >= 3 && column <= 6 && value instanceof Number) {
                    ((JLabel) c).setText(currencyFormat.format(((Number) value).doubleValue()) + " VND");
                }
                
                // Add padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    /**
     * Build button panel at the bottom
     */
    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        
        // Create Add Button
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> showLuongDialog(null, DialogMode.ADD));
                
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a styled button with primary colors
     */
    private JButton createStyledButton(String text) {
        return createRoundedButton(text, primaryColor, buttonTextColor, 10);
    }

    /**
     * Creates a rounded button with custom colors
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
     * Create popup menu for table row actions
     */
    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        
        JMenuItem menuItemView = createStyledMenuItem("Xem chi tiết");
        menuItemView.addActionListener(e -> xemChiTietLuong());
        
        JMenuItem menuItemEdit = createStyledMenuItem("Chỉnh sửa");
        menuItemEdit.addActionListener(e -> suaLuongAction());
        
        JMenuItem menuItemDelete = createStyledMenuItem("Xóa");
        menuItemDelete.setForeground(accentColor);
        menuItemDelete.addActionListener(e -> xoaLuongAction());
        
        popupMenu.add(menuItemView);
        popupMenu.addSeparator();
        popupMenu.add(menuItemEdit);
        popupMenu.addSeparator();
        popupMenu.add(menuItemDelete);
    }

    /**
     * Shows the popup menu at the specified location
     */
    private void showPopupMenu(MouseEvent e) {
        if (tblLuong.getSelectedRow() >= 0) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Method to view details of selected salary record
     */
    private void xemChiTietLuong() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
            Luong luong = controller.getLuongById(idLuong);
            if (luong != null) {
                showLuongDialog(luong, DialogMode.VIEW);
            }
        }
    }
    private void suaLuongAction() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
            Luong luong = controller.getLuongById(idLuong);
            if (luong != null) {
                showLuongDialog(luong, DialogMode.EDIT);
            }
        }
    }

    /**
     * Method to delete selected salary record
     */
    private void xoaLuongAction() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
            
            // Confirm dialog with modern styling
            JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
            confirmPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
            JLabel msgLabel = new JLabel("Bạn có chắc chắn muốn xóa bản ghi lương này?");
            msgLabel.setFont(regularFont);
            
            confirmPanel.add(iconLabel, BorderLayout.WEST);
            confirmPanel.add(msgLabel, BorderLayout.CENTER);
            
            int confirm = JOptionPane.showOptionDialog(
                this,
                confirmPanel,
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"Xóa", "Hủy"},
                "Hủy"
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                xoaLuong(idLuong);
            }
        }
    }
    private void timKiem() {
    	String keyword = txtTimKiem.getText().trim();
        
        // Nếu từ khóa trống thì làm mới dữ liệu
        if (keyword.isEmpty()) {
            lamMoi();
            showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            return;
        }
        
        // Lưu lại số lượng hàng trước khi tìm kiếm
        int rowCountBefore = modelLuong.getRowCount();
        
        // Thực hiện tìm kiếm
        controller.timKiemLuong(keyword);
        
        // Lấy số lượng kết quả tìm thấy
        int rowCountAfter = modelLuong.getRowCount();
        
        // Hiển thị thông báo dựa trên kết quả tìm kiếm
        if (rowCountAfter > 0) {
            String message = String.format("Tìm thấy %d kết quả phù hợp!", rowCountAfter);
            showNotification(message, NotificationType.SUCCESS);
        } else {
            showNotification("Không tìm thấy kết quả phù hợp!", NotificationType.WARNING);
        }
    }
    private enum DialogMode {
        ADD, EDIT, VIEW
    }
    // Method to show dialog for adding/editing/viewing salary records
 // Inside LuongUI class

 // Inside LuongUI class

 // Add fields for individual error labels
    private JLabel thangNamErrorLabel;
    private JLabel luongCoBanErrorLabel;
    private JLabel thuongErrorLabel;
    private JLabel khauTruErrorLabel;

    private void showLuongDialog(Luong luong, DialogMode mode) {
        String title;
        switch (mode) {
            case ADD:
                title = "Thêm Mới Lương";
                break;
            case EDIT:
                title = "Chỉnh Sửa Lương";
                break;
            case VIEW:
                title = "Chi Tiết Lương";
                break;
            default:
                title = "THÔNG TIN LƯƠNG";
        }
        
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 500);
        dialog.setLocationRelativeTo(this);
        
        // Title Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("THÊM MỚI LƯƠNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form Panel với GridBagLayout
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Tạo border bo góc cho các ô nhập liệu
        Border roundedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true);
        
        // Initialize error labels
        thangNamErrorLabel = new JLabel("");
        thangNamErrorLabel.setForeground(Color.RED);
        thangNamErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        luongCoBanErrorLabel = new JLabel("");
        luongCoBanErrorLabel.setForeground(Color.RED);
        luongCoBanErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        thuongErrorLabel = new JLabel("");
        thuongErrorLabel.setForeground(Color.RED);
        thuongErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        khauTruErrorLabel = new JLabel("");
        khauTruErrorLabel.setForeground(Color.RED);
        khauTruErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Configure text fields và components với bo góc và kích thước giống hình
        txtIdLuong.setEditable(false);
        txtIdLuong.setBorder(roundedBorder);
        txtIdLuong.setPreferredSize(new Dimension(300, 30));
        
        dateThangNam.setDateFormatString("MM/yyyy");
        dateThangNam.setBorder(roundedBorder);
        dateThangNam.setPreferredSize(new Dimension(300, 30));
        
        Calendar calendar = Calendar.getInstance();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        dateThangNam.setDate(calendar.getTime());
        
        txtTongLuong.setEditable(false);
        txtTongLuong.setBorder(roundedBorder);
        txtTongLuong.setPreferredSize(new Dimension(300, 30));
        
        txtLuongCoBan.setBorder(roundedBorder);
        txtLuongCoBan.setPreferredSize(new Dimension(300, 30));
        
        txtThuong.setBorder(roundedBorder);
        txtThuong.setPreferredSize(new Dimension(300, 30));
        
        txtKhauTru.setBorder(roundedBorder);
        txtKhauTru.setPreferredSize(new Dimension(300, 30));
        
        cmbNhanVien.setBorder(roundedBorder);
        cmbNhanVien.setPreferredSize(new Dimension(300, 30));
        
        // Set fields editable based on mode
        boolean editable = mode != DialogMode.VIEW;
        cmbNhanVien.setEnabled(editable);
        dateThangNam.setEnabled(editable);
        txtLuongCoBan.setEditable(editable);
        txtThuong.setEditable(editable);
        txtKhauTru.setEditable(editable);
        
        // THÊM PHẦN VALIDATION THỜI GIAN THỰC CHỈ KHI Ở CHẾ ĐỘ CHỈNH SỬA
        if (editable) {
            // Validation cho trường Lương cơ bản
            txtLuongCoBan.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateLuongCoBan();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateLuongCoBan();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateLuongCoBan();
                }
                
                private void validateLuongCoBan() {
                    String text = txtLuongCoBan.getText().trim();
                    if (text.isEmpty()) {
                        luongCoBanErrorLabel.setText("Lương cơ bản không được trống!");
                    } else if (!text.matches("\\d+(\\.\\d+)?")) {
                        luongCoBanErrorLabel.setText("Số không hợp lệ!");
                    } else {
                        luongCoBanErrorLabel.setText(""); // Xóa lỗi khi hợp lệ
                    }
                }
            });
            
            // Validation cho trường Thưởng
            txtThuong.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateThuong();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateThuong();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateThuong();
                }
                
                private void validateThuong() {
                    String text = txtThuong.getText().trim();
                    if (text.isEmpty()) {
                        thuongErrorLabel.setText("Lương thưởng không được trống!");
                    } else if (!text.matches("\\d+(\\.\\d+)?")) {
                        thuongErrorLabel.setText("Số không hợp lệ!");
                    } else {
                        thuongErrorLabel.setText(""); // Xóa lỗi khi hợp lệ
                    }
                }
            });
            
            // Validation cho trường Khấu trừ
            txtKhauTru.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateKhauTru();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateKhauTru();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateKhauTru();
                }
                
                private void validateKhauTru() {
                    String text = txtKhauTru.getText().trim();
                    if (text.isEmpty()) {
                        khauTruErrorLabel.setText("Tiền khấu trừ không được trống!");
                    } else if (!text.matches("\\d+(\\.\\d+)?")) {
                        khauTruErrorLabel.setText("Số không hợp lệ!");
                    } else {
                        khauTruErrorLabel.setText(""); // Xóa lỗi khi hợp lệ
                    }
                }
            });
        }
        
        // Add components to panelForm
        int row = 0;
        
        // ID (không hiển thị trong hình, nhưng giữ lại trong mã)
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panelForm.add(idLabel, gbc);
        gbc.gridx = 1;
        panelForm.add(txtIdLuong, gbc);
        row++;
        
        // Nhân viên
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel nhanVienLabel = new JLabel("Nhân viên:");
        nhanVienLabel.setHorizontalAlignment(SwingConstants.LEFT);
        if (editable) {
            JLabel asterisk = new JLabel("*");
            asterisk.setForeground(Color.RED);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.add(nhanVienLabel);
            labelPanel.add(asterisk);
            panelForm.add(labelPanel, gbc);
        } else {
            panelForm.add(nhanVienLabel, gbc);
        }
        gbc.gridx = 1;
        panelForm.add(cmbNhanVien, gbc);
        row++;
        
        // Tháng/Năm with red asterisk
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel thangNamLabel = new JLabel("Tháng/Năm:");
        thangNamLabel.setHorizontalAlignment(SwingConstants.LEFT);
        if (editable) {
            JLabel asterisk = new JLabel("*");
            asterisk.setForeground(Color.RED);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.add(thangNamLabel);
            labelPanel.add(asterisk);
            panelForm.add(labelPanel, gbc);
        } else {
            panelForm.add(thangNamLabel, gbc);
        }
        gbc.gridx = 1;
        panelForm.add(dateThangNam, gbc);
        row++;
        
        // Tháng/Năm error label
        gbc.gridx = 1;
        gbc.gridy = row;
        panelForm.add(thangNamErrorLabel, gbc);
        row++;
        
        // Lương cơ bản with red asterisk
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel luongCoBanLabel = new JLabel("Lương cơ bản:");
        luongCoBanLabel.setHorizontalAlignment(SwingConstants.LEFT);
        if (editable) {
            JLabel asterisk = new JLabel("*");
            asterisk.setForeground(Color.RED);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.add(luongCoBanLabel);
            labelPanel.add(asterisk);
            panelForm.add(labelPanel, gbc);
        } else {
            panelForm.add(luongCoBanLabel, gbc);
        }
        gbc.gridx = 1;
        panelForm.add(txtLuongCoBan, gbc);
        row++;
        
        // Lương cơ bản error label
        gbc.gridx = 1;
        gbc.gridy = row;
        panelForm.add(luongCoBanErrorLabel, gbc);
        row++;
        
        // Thưởng with red asterisk
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel thuongLabel = new JLabel("Thưởng:");
        thuongLabel.setHorizontalAlignment(SwingConstants.LEFT);
        if (editable) {
            JLabel asterisk = new JLabel("*");
            asterisk.setForeground(Color.RED);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.add(thuongLabel);
            labelPanel.add(asterisk);
            panelForm.add(labelPanel, gbc);
        } else {
            panelForm.add(thuongLabel, gbc);
        }
        gbc.gridx = 1;
        panelForm.add(txtThuong, gbc);
        row++;
        
        // Thưởng error label
        gbc.gridx = 1;
        gbc.gridy = row;
        panelForm.add(thuongErrorLabel, gbc);
        row++;
        
        // Khấu trừ with red asterisk
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel khauTruLabel = new JLabel("Khấu trừ:");
        khauTruLabel.setHorizontalAlignment(SwingConstants.LEFT);
        if (editable) {
            JLabel asterisk = new JLabel("*");
            asterisk.setForeground(Color.RED);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.add(khauTruLabel);
            labelPanel.add(asterisk);
            panelForm.add(labelPanel, gbc);
        } else {
            panelForm.add(khauTruLabel, gbc);
        }
        gbc.gridx = 1;
        panelForm.add(txtKhauTru, gbc);
        row++;
        
        // Khấu trừ error label
        gbc.gridx = 1;
        gbc.gridy = row;
        panelForm.add(khauTruErrorLabel, gbc);
        row++;
        
        // Tổng lương (không có trong hình, nhưng giữ lại trong mã)
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel tongLuongLabel = new JLabel("Tổng lương:");
        tongLuongLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panelForm.add(tongLuongLabel, gbc);
        gbc.gridx = 1;
        panelForm.add(txtTongLuong, gbc);
        
        // Button Panel
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        switch (mode) {
            case ADD:
                // Add mode
                txtIdLuong.setText("");
                txtLuongCoBan.setText("");
                txtThuong.setText("");
                txtKhauTru.setText("");
                txtTongLuong.setText("");
                thangNamErrorLabel.setText("");
                luongCoBanErrorLabel.setText("");
                thuongErrorLabel.setText("");
                khauTruErrorLabel.setText("");
                
                JButton btnAdd = new JButton("Lưu");
                btnAdd.setBackground(new Color(100, 180, 100));
                btnAdd.setForeground(Color.WHITE);
                btnAdd.setPreferredSize(new Dimension(80, 30));
                btnAdd.addActionListener(e -> {
                    if (themLuong()) {
                        dialog.dispose();
                    }
                });
                
                JButton btnCancelAdd = new JButton("Hủy");
                btnCancelAdd.setBackground(Color.LIGHT_GRAY);
                btnCancelAdd.setForeground(Color.BLACK);
                btnCancelAdd.setPreferredSize(new Dimension(80, 30));
                btnCancelAdd.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnCancelAdd);
                panelButtons.add(btnAdd);
                break;
                
            case EDIT:
                // Edit mode
                displayLuongDetails(luong);
                
                JButton btnSave = new JButton("Lưu");
                btnSave.setBackground(new Color(100, 180, 100));
                btnSave.setForeground(Color.WHITE);
                btnSave.setPreferredSize(new Dimension(80, 30));
                btnSave.addActionListener(e -> {
                    suaLuong();
                    dialog.dispose();
                });
                
                JButton btnCancelEdit = new JButton("Hủy");
                btnCancelEdit.setBackground(Color.LIGHT_GRAY);
                btnCancelEdit.setForeground(Color.BLACK);
                btnCancelEdit.setPreferredSize(new Dimension(80, 30));
                btnCancelEdit.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnCancelEdit);
                panelButtons.add(btnSave);
                break;
                
            case VIEW:
                // View mode
                displayLuongDetails(luong);
                
                JButton btnEdit = new JButton("Chỉnh sửa");
                btnEdit.setBackground(new Color(70, 130, 180));
                btnEdit.setForeground(Color.WHITE);
                btnEdit.setPreferredSize(new Dimension(80, 30));
                btnEdit.addActionListener(e -> {
                    dialog.dispose();
                    showLuongDialog(luong, DialogMode.EDIT);
                });
                
                JButton btnDelete = new JButton("Xóa");
                btnDelete.setBackground(new Color(220, 80, 80));
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setPreferredSize(new Dimension(80, 30));
                btnDelete.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Bạn có chắc chắn muốn xóa bản ghi lương này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        dialog.dispose();
                        xoaLuong(Integer.parseInt(txtIdLuong.getText()));
                    }
                });
                
                JButton btnClose = new JButton("Đóng");
                btnClose.setBackground(Color.LIGHT_GRAY);
                btnClose.setForeground(Color.BLACK);
                btnClose.setPreferredSize(new Dimension(80, 30));
                btnClose.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnEdit);
                panelButtons.add(btnDelete);
                panelButtons.add(btnClose);
                break;
        }
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        buttonPanel.add(panelButtons, BorderLayout.CENTER);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(panelForm, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean themLuong() {
        try {
            // Xóa thông báo lỗi trước đó
            thangNamErrorLabel.setText("");
            luongCoBanErrorLabel.setText("");
            thuongErrorLabel.setText("");
            khauTruErrorLabel.setText("");

            // Lấy dữ liệu từ các trường nhập liệu
            String selectedNhanVien = (String) cmbNhanVien.getSelectedItem();
            java.util.Date selectedDate = dateThangNam.getDate();
            String luongCoBanText = txtLuongCoBan.getText().trim();
            String thuongText = txtThuong.getText().trim();
            String khauTruText = txtKhauTru.getText().trim();

            boolean hasError = false;

            // 1. Kiểm tra các trường bắt buộc có bị trống không
            if (selectedDate == null) {
                thangNamErrorLabel.setText("Tháng/Năm chưa được chọn!");
                hasError = true;
            }
            if (luongCoBanText.isEmpty()) {
                luongCoBanErrorLabel.setText("Lương cơ bản không được trống!");
                hasError = true;
            }
            if (thuongText.isEmpty()) {
                thuongErrorLabel.setText("Lương thưởng không được trống!");
                hasError = true;
            }
            if (khauTruText.isEmpty()) {
                khauTruErrorLabel.setText("Tiền khấu trừ không được trống!");
                hasError = true;
            }

            // 2. Kiểm tra định dạng số (chỉ khi trường đó không trống)
            if (!luongCoBanText.isEmpty() && !luongCoBanText.matches("\\d+(\\.\\d+)?")) {
                luongCoBanErrorLabel.setText("Số không hợp lệ!");
                hasError = true;
            }
            if (!thuongText.isEmpty() && !thuongText.matches("\\d+(\\.\\d+)?")) {
                thuongErrorLabel.setText("Số không hợp lệ!");
                hasError = true;
            }
            if (!khauTruText.isEmpty() && !khauTruText.matches("\\d+(\\.\\d+)?")) {
                khauTruErrorLabel.setText("Số không hợp lệ!");
                hasError = true;
            }

            // Nếu có bất kỳ lỗi nào, trả về false
            if (hasError) {
                return false;
            }

            // Nếu tất cả các kiểm tra đều thành công, tiến hành thêm bản ghi lương
            int idNguoiDung = controller.getIdNguoiDungByHoTen(selectedNhanVien);
            Date thangNam = new Date(selectedDate.getTime());
            double luongCoBan = Double.parseDouble(luongCoBanText);
            double thuong = Double.parseDouble(thuongText);
            double khauTru = Double.parseDouble(khauTruText);

            controller.themLuong(idNguoiDung, thangNam, luongCoBan, thuong, khauTru);
            lamMoi();
            return true; // Thêm thành công
        } catch (NumberFormatException e) {
            System.err.println("Lỗi định dạng số không mong muốn: " + e.getMessage());
            return false;
        } catch (Exception e) {
            thangNamErrorLabel.setText("Lỗi: " + e.getMessage());
            return false;
        }
    }
    
    private void displayLuongDetails(Luong luong) {
        txtIdLuong.setText(String.valueOf(luong.getIdLuong()));        
        for (int i = 0; i < cmbNhanVien.getItemCount(); i++) {
            String hoTen = cmbNhanVien.getItemAt(i);
            if (controller.getIdNguoiDungByHoTen(hoTen) == luong.getIdNguoiDung()) {
                cmbNhanVien.setSelectedIndex(i);
                break;
            }
        }        
        dateThangNam.setDate(luong.getThangNam());
        DecimalFormat numberFormat = new DecimalFormat("###,###,###.##");
        numberFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("vi", "VN")));
        txtLuongCoBan.setText(numberFormat.format(luong.getLuongCoBan()));
        txtThuong.setText(numberFormat.format(luong.getThuong()));
        txtKhauTru.setText(numberFormat.format(luong.getKhauTru()));
        txtTongLuong.setText(numberFormat.format(luong.getTongLuong()));
    }    
    public void loadLuongData(Object[] rowData) {
        modelLuong.addRow(rowData);
        originalData.add(rowData);
    }
    public void clearTable() {
        modelLuong.setRowCount(0);
        originalData.clear();
    }    
    public List<Object[]> getOriginalData() {
        return originalData;
    }   
    
    private void xoaLuong() {
        try {
            String idText = txtIdLuong.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int idLuong = Integer.parseInt(idText);
            xoaLuong(idLuong);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID lương không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaLuong(int idLuong) {
        controller.xoaLuong(idLuong);
        lamMoi();
    }
    
    private void suaLuong() {
        try {
            String idText = txtIdLuong.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int idLuong = Integer.parseInt(idText);
            String selectedNhanVien = cmbNhanVien.getSelectedItem().toString();
            int idNguoiDung = controller.getIdNguoiDungByHoTen(selectedNhanVien);
            
            java.util.Date selectedDate = dateThangNam.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Date thangNam = new Date(selectedDate.getTime());
            String luongCoBanText = txtLuongCoBan.getText().trim();
            String thuongText = txtThuong.getText().trim();
            String khauTruText = txtKhauTru.getText().trim();
            String cleanLuongCoBan = luongCoBanText.replace(".", "").replace(",", ".");
            String cleanThuong = thuongText.replace(".", "").replace(",", ".");
            String cleanKhauTru = khauTruText.replace(".", "").replace(",", ".");
            
            double luongCoBan, thuong, khauTru;            
            try {
                luongCoBan = Double.parseDouble(cleanLuongCoBan);
                thuong = Double.parseDouble(cleanThuong);
                khauTru = Double.parseDouble(cleanKhauTru);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Số liệu không hợp lệ. Vui lòng nhập đúng định dạng số!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (luongCoBan < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Lương cơ bản không thể âm!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final double MAX_SALARY_VALUE = 2147483647; // For INT type
            if (luongCoBan > MAX_SALARY_VALUE) {
                JOptionPane.showMessageDialog(this, 
                    "Lương cơ bản quá lớn! Giá trị tối đa là " + MAX_SALARY_VALUE, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (thuong < 0 || thuong > MAX_SALARY_VALUE) {
                JOptionPane.showMessageDialog(this, 
                    "Thưởng phải nằm trong khoảng từ 0 đến " + MAX_SALARY_VALUE, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (khauTru < 0 || khauTru > MAX_SALARY_VALUE) {
                JOptionPane.showMessageDialog(this, 
                    "Khấu trừ phải nằm trong khoảng từ 0 đến " + MAX_SALARY_VALUE, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }            
            controller.suaLuong(idLuong, idNguoiDung, thangNam, luongCoBan, thuong, khauTru);
            lamMoi();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi định dạng số: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Print full stack trace for debugging
        }
    }    
    private void lamMoi() {
        txtIdLuong.setText("");
        if (cmbNhanVien.getItemCount() > 0) {
            cmbNhanVien.setSelectedIndex(0);
        }
        
        Calendar calendar = Calendar.getInstance();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        dateThangNam.setDate(calendar.getTime());
        
        txtLuongCoBan.setText("");
        txtThuong.setText("");
        txtKhauTru.setText("");
        txtTongLuong.setText("");
        txtTimKiem.setText("");
        
        tblLuong.clearSelection();
        controller.loadLuongData();
    }
    
    public void updateTotalRow(double totalLuongCoBan, double totalThuong, double totalKhauTru, double totalTongLuong) {
        DefaultTableModel totalModel = (DefaultTableModel) tableTotalRow.getModel();
        if (totalModel.getRowCount() == 0) {
            totalModel.addRow(new Object[]{"Tổng", "", "", 0.0, 0.0, 0.0, 0.0});
        }
        totalModel.setValueAt("Tổng", 0, 0);
        totalModel.setValueAt("", 0, 1);
        totalModel.setValueAt("", 0, 2);
        totalModel.setValueAt(totalLuongCoBan, 0, 3);
        totalModel.setValueAt(totalThuong, 0, 4);
        totalModel.setValueAt(totalKhauTru, 0, 5);
        totalModel.setValueAt(totalTongLuong, 0, 6);
        tableTotalRow.revalidate();
        tableTotalRow.repaint();
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
    
    public DefaultTableModel getModelLuong() {
        return modelLuong;
    }
    
    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }

	@Override
	public void onDataChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showSuccessToast(String message) {
		showNotification(message, NotificationType.SUCCESS);
		
	}

	@Override
	public void showErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		
	}

	@Override
	public void showMessage(String message, String title, int messageType) {
		// TODO Auto-generated method stub
		
	}
}