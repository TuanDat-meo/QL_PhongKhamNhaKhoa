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
    private void showLuongDialog(Luong luong, DialogMode mode) {
        String title;
        switch (mode) {
            case ADD:
                title = "Thêm mới lương";
                break;
            case EDIT:
                title = "Chỉnh sửa lương";
                break;
            case VIEW:
                title = "Chi tiết lương";
                break;
            default:
                title = "Thông tin lương";
        }
        
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 15));
        panelForm.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // No need to reinitialize txtIdLuong here, just configure it
        txtIdLuong.setEditable(false);
        
        // Configure dateThangNam which was already initialized
        dateThangNam.setDateFormatString("MM/yyyy");
        
        Calendar calendar = Calendar.getInstance();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        dateThangNam.setDate(calendar.getTime());
        
        // No need to reinitialize these text fields
        txtTongLuong.setEditable(false);
        
        // Set fields editable based on mode
        boolean editable = mode != DialogMode.VIEW;
        cmbNhanVien.setEnabled(editable);
        dateThangNam.setEnabled(editable);
        txtLuongCoBan.setEditable(editable);
        txtThuong.setEditable(editable);
        txtKhauTru.setEditable(editable);
        
        panelForm.add(new JLabel("ID:"));
        panelForm.add(txtIdLuong);
        panelForm.add(new JLabel("Nhân viên:"));
        panelForm.add(cmbNhanVien);
        panelForm.add(new JLabel("Tháng/Năm:"));
        panelForm.add(dateThangNam);
        panelForm.add(new JLabel("Lương cơ bản:"));
        panelForm.add(txtLuongCoBan);
        panelForm.add(new JLabel("Thưởng:"));
        panelForm.add(txtThuong);
        panelForm.add(new JLabel("Khấu trừ:"));
        panelForm.add(txtKhauTru);
        panelForm.add(new JLabel("Tổng lương:"));
        panelForm.add(txtTongLuong);
        
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        switch (mode) {
            case ADD:
                // Add mode
                // Reset form fields
                txtIdLuong.setText("");
                txtLuongCoBan.setText("");
                txtThuong.setText("");
                txtKhauTru.setText("");
                txtTongLuong.setText("");
                
                JButton btnAdd = new JButton("Thêm");
                btnAdd.setBackground(new Color(100, 180, 100));
                btnAdd.setForeground(Color.WHITE);
                btnAdd.addActionListener(e -> {
                    themLuong();
                    dialog.dispose();
                });
                
                JButton btnCancelAdd = new JButton("Hủy");
                btnCancelAdd.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnAdd);
                panelButtons.add(btnCancelAdd);
                break;
                
            case EDIT:
                // Edit mode
                displayLuongDetails(luong);
                
                JButton btnSave = new JButton("Lưu");
                btnSave.setBackground(new Color(70, 130, 180));
                btnSave.setForeground(Color.WHITE);
                btnSave.addActionListener(e -> {
                    suaLuong();
                    dialog.dispose();
                });
                
                JButton btnCancelEdit = new JButton("Hủy");
                btnCancelEdit.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnSave);
                panelButtons.add(btnCancelEdit);
                break;
                
            case VIEW:
                // View mode
                displayLuongDetails(luong);
                
                JButton btnEdit = new JButton("Chỉnh sửa");
                btnEdit.setBackground(new Color(70, 130, 180));
                btnEdit.setForeground(Color.WHITE);
                btnEdit.addActionListener(e -> {
                    dialog.dispose();
                    showLuongDialog(luong, DialogMode.EDIT);
                });
                
                JButton btnDelete = new JButton("Xóa");
                btnDelete.setBackground(new Color(220, 80, 80));
                btnDelete.setForeground(Color.WHITE);
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
                btnClose.addActionListener(e -> dialog.dispose());
                
                panelButtons.add(btnEdit);
                panelButtons.add(btnDelete);
                panelButtons.add(btnClose);
                break;
        }
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        buttonPanel.add(panelButtons, BorderLayout.CENTER);
        
        dialog.add(panelForm, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
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
    private void themLuong() {
        try {
            String selectedNhanVien = cmbNhanVien.getSelectedItem().toString();
            int idNguoiDung = controller.getIdNguoiDungByHoTen(selectedNhanVien);
            
            java.util.Date selectedDate = dateThangNam.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Date thangNam = new Date(selectedDate.getTime());
            
            double luongCoBan = Double.parseDouble(txtLuongCoBan.getText().trim());
            double thuong = Double.parseDouble(txtThuong.getText().trim());
            double khauTru = Double.parseDouble(txtKhauTru.getText().trim());
            
            controller.themLuong(idNguoiDung, thangNam, luongCoBan, thuong, khauTru);
            lamMoi();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho các trường lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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