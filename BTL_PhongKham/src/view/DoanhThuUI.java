package view;

import controller.DoanhThuController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class DoanhThuUI extends JPanel {
    // Constants for consistent styling
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color TABLE_SELECTION_COLOR = new Color(52, 152, 219, 70);
    private static final Color TABLE_ALTERNATE_ROW = new Color(245, 248, 250);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    
    private static final int PADDING = 15;
    private static final int BUTTON_PADDING = 8;
    private static final int ROW_HEIGHT = 30;
    private static final int HEADER_HEIGHT = 38;
    
    // UI Components
    private DefaultTableModel modelDoanhThu;
    private JTable tableDoanhThu;
    private JButton btnThemMoiDoanhThu;
    private JTextField txtTimKiemDoanhThu;
    private JButton btnTimKiemDoanhThu;
    private TableRowSorter<DefaultTableModel> sorterDoanhThu;
    private JPopupMenu popupMenuDoanhThu;
    private JMenuItem menuItemSuaDoanhThu;
    private JMenuItem menuItemXoaDoanhThu;
    private JMenuItem menuItemXemChiTiet;

    // Business Logic
    private DoanhThuController doanhThuController;
    private NumberFormat currencyFormat;

    /**
     * Initializes the revenue management UI panel
     */
    public DoanhThuUI() {
        initializePanel();
        initializeFormatters();
        initializeController();
        buildHeaderPanel();
        buildTablePanel();
        setupEventListeners();
        setupPopupMenu();
        
        // Load initial data
        doanhThuController.loadDoanhThuData();
    }
    
    /**
     * Initializes panel properties
     */
    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
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
     * Initializes the controller
     */
    private void initializeController() {
        doanhThuController = new DoanhThuController(this);
    }
    
    /**
     * Builds the header panel with title and search controls
     */
    private void buildHeaderPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Title panel on the left
        JPanel titlePanel = createTitlePanel();
        topPanel.add(titlePanel, BorderLayout.WEST);

        // Search panel on the right
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the title panel with heading
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ DOANH THU");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        return titlePanel;
    }
    
    /**
     * Creates the search panel with search controls
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(REGULAR_FONT);
        searchLabel.setForeground(TEXT_COLOR);
        
        txtTimKiemDoanhThu = new JTextField(15);
        txtTimKiemDoanhThu.setFont(REGULAR_FONT);
        txtTimKiemDoanhThu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        btnTimKiemDoanhThu = createStyledButton("Tìm");
        
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiemDoanhThu);
        searchPanel.add(btnTimKiemDoanhThu);
        
        return searchPanel;
    }
    
    /**
     * Builds the table panel with revenue data
     */
    private void buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        initializeTable();
        styleTable();
        
        JScrollPane scrollPaneDoanhThu = new JScrollPane(tableDoanhThu);
        scrollPaneDoanhThu.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneDoanhThu.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPaneDoanhThu, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }
    private void initializeTable() {
        String[] columns = {"ID", "ID Hóa Đơn", "Tên Bệnh Nhân", "Tháng/Năm", "Tổng Thu", "Trạng Thái"};        
        modelDoanhThu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };        
        tableDoanhThu = new JTable(modelDoanhThu);
        sorterDoanhThu = new TableRowSorter<>(modelDoanhThu);
        tableDoanhThu.setRowSorter(sorterDoanhThu);
    }
    private void styleTable() {
        tableDoanhThu.setFont(REGULAR_FONT);
        tableDoanhThu.setRowHeight(ROW_HEIGHT);
        tableDoanhThu.setShowGrid(false);
        tableDoanhThu.setSelectionBackground(TABLE_SELECTION_COLOR);
        tableDoanhThu.setSelectionForeground(TEXT_COLOR);
        tableDoanhThu.setIntercellSpacing(new Dimension(0, 0));
        tableDoanhThu.setFillsViewportHeight(true);
        JTableHeader header = tableDoanhThu.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), HEADER_HEIGHT));
        header.setBorder(BorderFactory.createEmptyBorder());
        tableDoanhThu.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE_ROW);
                }
                setBorder(new EmptyBorder(0, 10, 0, 0)); // Add padding to cell
                return c;
            }
        });
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, 15, BUTTON_PADDING, 15));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });        
        return button;
    }
    private void setupEventListeners() {
        btnTimKiemDoanhThu.addActionListener(e -> filterDoanhThu());
        txtTimKiemDoanhThu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterDoanhThu();
                }
            }
        });
        tableDoanhThu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e);
            }
        });
        
    }
    private void setupPopupMenu() {
        popupMenuDoanhThu = new JPopupMenu();
        popupMenuDoanhThu.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 1));
        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSuaDoanhThu = createStyledMenuItem("Sửa");
        menuItemXoaDoanhThu = createStyledMenuItem("Xóa");
        
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
    private void showChiTietDoanhThuDialog(JFrame parent, int idDoanhThu, int idHoaDon, 
            String tenBenhNhan, String thangNam, String tongThu, String trangThai) {
        JDialog dialog = new JDialog(parent, "Chi Tiết Doanh Thu", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("CHI TIẾT DOANH THU");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        addFieldToPanel(fieldsPanel, "ID Doanh Thu:", String.valueOf(idDoanhThu));
        addFieldToPanel(fieldsPanel, "ID Hóa Đơn:", String.valueOf(idHoaDon));
        addFieldToPanel(fieldsPanel, "Tên Bệnh Nhân:", tenBenhNhan);
        addFieldToPanel(fieldsPanel, "Tháng/Năm:", thangNam);
        addFieldToPanel(fieldsPanel, "Tổng Thu:", tongThu);
        addFieldToPanel(fieldsPanel, "Trạng Thái:", trangThai);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);        
        JButton closeButton = createStyledButton("Đóng");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(fieldsPanel);
        contentPanel.add(buttonPanel);        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    private void addFieldToPanel(JPanel panel, String label, String value) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(TEXT_COLOR);        
        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(REGULAR_FONT);
        fieldValue.setForeground(TEXT_COLOR);        
        panel.add(fieldLabel);
        panel.add(fieldValue);
    }
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(REGULAR_FONT);
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(TEXT_COLOR);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 20));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(TABLE_ALTERNATE_ROW);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        
        return menuItem;
    }
    private void filterDoanhThu() {
        String text = txtTimKiemDoanhThu.getText();
        if (text.trim().isEmpty()) {
            sorterDoanhThu.setRowFilter(null);
        } else {
            sorterDoanhThu.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    private void showPopupMenu(MouseEvent e) {
        int row = tableDoanhThu.rowAtPoint(e.getPoint());
        if (row >= 0 && row < tableDoanhThu.getRowCount()) {
            tableDoanhThu.setRowSelectionInterval(row, row);
        } else {
            tableDoanhThu.clearSelection();
        }

        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0 && e.isPopupTrigger()) {
            popupMenuDoanhThu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    private void suaDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            Object[] data = new Object[modelDoanhThu.getColumnCount()];
            for (int i = 0; i < data.length; i++) {
                data[i] = modelDoanhThu.getValueAt(selectedRow, i);
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
            int idDoanhThu = (int) modelDoanhThu.getValueAt(selectedRow, 0);
            JDialog confirmDialog = createConfirmDialog("Bạn có chắc chắn muốn xóa doanh thu này?", idDoanhThu);
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            confirmDialog.setLocationRelativeTo(topFrame);
            confirmDialog.setVisible(true);
        } else {
            showNotification("Vui lòng chọn một dòng để xóa!", NotificationType.WARNING);
        }
    }
    private JDialog createConfirmDialog(String message, int idDoanhThu) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(topFrame, "Xác nhận", true);
        dialog.setSize(350, 150);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(REGULAR_FONT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);        
        JButton confirmButton = createStyledButton("Xác nhận");
        JButton cancelButton = createSecondaryButton("Hủy");        
        confirmButton.addActionListener(e -> {
            doanhThuController.xoaDoanhThu(idDoanhThu);
            doanhThuController.loadDoanhThuData();
            dialog.dispose();
            showNotification("Đã xóa doanh thu thành công!", NotificationType.SUCCESS);
        });        
        cancelButton.addActionListener(e -> dialog.dispose());        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);        
        dialog.add(contentPanel);
        dialog.setLocationRelativeTo(topFrame);
        return dialog;
    }
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, 15, BUTTON_PADDING, 15));        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR.darker());
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });        
        return button;
    }
    private enum NotificationType {
        SUCCESS(new Color(46, 204, 113), "Thành công"),
        WARNING(new Color(241, 196, 15), "Cảnh báo"),
        ERROR(new Color(231, 76, 60), "Lỗi");        
        private final Color color;
        private final String title;        
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }
    private void showNotification(String message, NotificationType type) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), type.title, false);
        dialog.setSize(300, 120);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(type.color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));        
        JLabel titleLabel = new JLabel(type.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(type.color);        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(REGULAR_FONT);
        messageLabel.setForeground(TEXT_COLOR);        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(messagePanel, BorderLayout.CENTER);
        dialog.add(panel);
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    public DoanhThuController getDoanhThuController() {
        return doanhThuController;
    }
    public DefaultTableModel getModelDoanhThu() {
        return modelDoanhThu;
    }
    public void loadDoanhThuData(Object[] rowData) {
        modelDoanhThu.addRow(rowData);
    }
    public void updateDoanhThuRow(int row, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            modelDoanhThu.setValueAt(rowData[i], row, i);
        }
    }
    public void removeDoanhThuRow(int row) {
        modelDoanhThu.removeRow(row);
    }
    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }
}