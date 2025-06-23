package view;

import controller.DoanhThuController;
import util.DataChangeListener;
import util.ExportManager;
import util.ExportManager.MessageCallback;
import util.RoundedPanel;
import util.ValidationUtils;

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
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
    private Color statisticsColor = new Color(58, 175, 169);  // Teal color for statistics
    
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);
    
    private DefaultTableModel modelDoanhThu;
    private JTable tableDoanhThu;
    private JTable tableTotalRow;
    private DefaultTableModel modelTotalRow;
    private JButton btnXuatFile;
    private JButton btnThemMoiDoanhThu;
    private JButton btnThongKe; // New statistics button
    private JTextField txtTimKiemDoanhThu;
    private JButton btnTimKiemDoanhThu;
    private TableRowSorter<DefaultTableModel> sorterDoanhThu;
    private JPopupMenu popupMenuDoanhThu;
    private JMenuItem menuItemSuaDoanhThu;
    private JMenuItem menuItemXoaDoanhThu;
    private JMenuItem menuItemXemChiTiet;
    private double totalRevenue = 0;
    private List<Object[]> originalData = new ArrayList<>();
    
    // Main content panel that will switch between table view and statistics view
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel tableViewPanel;
    private ThongKeDoanhThuPanel statisticsPanel;
    private boolean isShowingStatistics = false;
    private JLabel titleLabel;
    private JLabel viewIndicator;
    private JPanel searchPanel;
    // Business Logic
    private DoanhThuController doanhThuController;
    private NumberFormat currencyFormat;
    private ExportManager exportManager;
    
    private int highlightedRowId = -1; // ID của bản ghi đang được highlight
    private Color highlightColor = new Color(237, 187, 85); // Màu highlight
    private Timer highlightTimer; // Timer để tắt highlight
    
    public DoanhThuUI() {
        initializePanel();
        initializeFormatters();
        buildMainLayout();
        initializeController();
        buildHeaderPanel();
        buildButtonPanel();
        setupEventListeners();
        setupPopupMenu();
        btnXuatFile.setEnabled(false);
        SwingUtilities.invokeLater(() -> {
            doanhThuController.loadDoanhThuData();
            btnXuatFile.setEnabled(modelDoanhThu.getRowCount() > 0);
        });
    }
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    private void initializeFormatters() {
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getNumberInstance(localeVN); // Sử dụng NumberFormat.getNumberInstance để không có ký hiệu "₫"
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setMaximumFractionDigits(0);
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
        
        // Dynamic title based on current view
        JLabel titleLabel = new JLabel("QUẢN LÝ DOANH THU");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        // Add view toggle indicator
        JLabel viewIndicator = new JLabel("» Danh sách");
        viewIndicator.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        viewIndicator.setForeground(secondaryColor);
        titlePanel.add(viewIndicator);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Search panel - only show when in table view
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
        
        // Store references for dynamic updates
        this.titleLabel = titleLabel;
        this.viewIndicator = viewIndicator;
        this.searchPanel = searchPanel;
        
        add(headerPanel, BorderLayout.NORTH);
    }
    private void buildMainLayout() {
        // Create CardLayout for switching between views
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(backgroundColor);
        
        // Build table view panel
        buildTableViewPanel();
        
        // Build statistics panel
        buildStatisticsPanel();
        
        // Add panels to card layout
        mainContentPanel.add(tableViewPanel, "TABLE_VIEW");
        mainContentPanel.add(statisticsPanel, "STATISTICS_VIEW");
        
        add(mainContentPanel, BorderLayout.CENTER);
    }
    private void buildTableViewPanel() {
        tableViewPanel = new JPanel(new BorderLayout());
        tableViewPanel.setBackground(backgroundColor);
        buildTablePanel();
    }
    private void buildStatisticsPanel() {
        statisticsPanel = new ThongKeDoanhThuPanel();
    }
    private void updateHeaderForCurrentView() {
        if (isShowingStatistics) {
            titleLabel.setText("THỐNG KÊ DOANH THU");
            searchPanel.setVisible(false);
        } else {
            titleLabel.setText("QUẢN LÝ DOANH THU");
            viewIndicator.setText("» Danh sách");
            searchPanel.setVisible(true);
        }
        // Refresh the header panel
        ((JPanel) titleLabel.getParent().getParent()).revalidate();
        ((JPanel) titleLabel.getParent().getParent()).repaint();
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
        
        tableViewPanel.add(wrapperPanel, BorderLayout.CENTER);
    }
    private void initializeTable() {
        String[] columns = {"ID", "ID Hóa Đơn", "Tên Bệnh Nhân", "Tháng/Năm", "Tổng Thu", "Trạng Thái"};
        
        modelDoanhThu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        
        tableDoanhThu = new JTable(modelDoanhThu) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
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
        
        tableTotalRow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setBackground(totalRowColor);
                if (column == 3) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    ((JLabel) c).setFont(totalRowFont);
                } else if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value) + " VNĐ");
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

        // Custom renderer for all columns
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                int rowId = (Integer) modelDoanhThu.getValueAt(modelRow, 0);

                // Handle background color
                if (!isSelected) {
                    if (highlightedRowId > 0 && rowId == highlightedRowId) {
                        c.setBackground(highlightColor);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }

                // Center align data for all columns
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);

                // Handle display for "Tổng Thu" column (Double)
                if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value));
                } 
                // Handle display for "ID" and "ID Hóa Đơn" columns (Integer)
                else if ((column == 0 || column == 1) && value instanceof Integer) {
                    ((JLabel) c).setText(String.valueOf(value));
                } 
                // Handle display for String columns
                else if (column == 2 || column == 3 || column == 5) {
                    ((JLabel) c).setText(value != null ? value.toString() : "");
                }

                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        
        // Ensure Integer and Double renderers also center align
        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) c).setText(value != null ? value.toString() : "");
                return c;
            }
        });
        
        table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                if (column == 4 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value));
                } else {
                    ((JLabel) c).setText(value != null ? value.toString() : "");
                }
                return c;
            }
        });
    }
    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // View toggle button
        btnThongKe = createRoundedButton("Thống kê", statisticsColor, buttonTextColor, 10);
        btnThongKe.setPreferredSize(new Dimension(120, 45));
        btnThongKe.addActionListener(e -> toggleView());
        
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> {
            if (modelDoanhThu == null || modelDoanhThu.getRowCount() == 0) {
                showNotification("Không có dữ liệu để xuất!", NotificationType.WARNING);
                return;
            }
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        });

        btnThemMoiDoanhThu = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThemMoiDoanhThu.setPreferredSize(new Dimension(100, 45));
        btnThemMoiDoanhThu.addActionListener(e -> showThemMoiDialog());
        
        // Add buttons in order
        buttonPanel.add(btnThongKe);
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThemMoiDoanhThu);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void toggleView() {
        isShowingStatistics = !isShowingStatistics;
        
        if (isShowingStatistics) {
            // Switch to statistics view
            cardLayout.show(mainContentPanel, "STATISTICS_VIEW");
            btnThongKe.setText("Danh sách");
            btnThongKe.setBackground(primaryColor);
            
            btnThemMoiDoanhThu.setVisible(false);
            
        } else {
            cardLayout.show(mainContentPanel, "TABLE_VIEW");
            btnThongKe.setText("Thống kê");
            btnThongKe.setBackground(statisticsColor);
            btnThemMoiDoanhThu.setVisible(true);
        }
        updateHeaderForCurrentView();
        SwingUtilities.invokeLater(() -> {
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
    }
    private JButton createStyledButton(String text) {
        return createRoundedButton(text, primaryColor, buttonTextColor, 10);
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
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
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
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    private void setupEventListeners() {
        btnTimKiemDoanhThu.addActionListener(e -> {
            if (txtTimKiemDoanhThu.getText().trim().isEmpty()) {
                refreshData();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                filterDoanhThu();
            }
        });        
        txtTimKiemDoanhThu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiemDoanhThu.getText().trim().isEmpty()) {
                        refreshData();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
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
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, tableDoanhThu);
        if (scrollPane != null) {
            scrollPane.addMouseWheelListener(e -> {
                if (highlightedRowId > 0) {
                    resetHighlightState();
                }
            });
            scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
                if (highlightedRowId > 0 && e.getValueIsAdjusting()) {
                    resetHighlightState();
                }
            });
        }
        tableDoanhThu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (highlightedRowId > 0 && isNavigationKey(e.getKeyCode())) {
                    resetHighlightState();
                }
            }
            private boolean isNavigationKey(int keyCode) {
                return keyCode == KeyEvent.VK_UP ||
                       keyCode == KeyEvent.VK_DOWN ||
                       keyCode == KeyEvent.VK_PAGE_UP ||
                       keyCode == KeyEvent.VK_PAGE_DOWN ||
                       keyCode == KeyEvent.VK_HOME ||
                       keyCode == KeyEvent.VK_END;
            }
        });
        tableDoanhThu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (highlightedRowId > 0) {
                    resetHighlightState();
                }
            }
        });
    }    
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
    private void showPopupMenu(MouseEvent e) {
        if (tableDoanhThu.getSelectedRow() >= 0) {
            popupMenuDoanhThu.show(e.getComponent(), e.getX(), e.getY());
        }
    }    
    private void xemChiTietDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableDoanhThu.convertRowIndexToModel(selectedRow);
            
            int idDoanhThu = (int) modelDoanhThu.getValueAt(modelRow, 0);
            int idHoaDon = (int) modelDoanhThu.getValueAt(modelRow, 1);
            String tenBenhNhan = modelDoanhThu.getValueAt(modelRow, 2).toString();
            String thangNam = modelDoanhThu.getValueAt(modelRow, 3).toString();
            
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
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(230, 38));
        textField.setMinimumSize(new Dimension(230, 38));
        textField.setMaximumSize(new Dimension(230, 38));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new util.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        textField.setBackground(Color.WHITE);
        textField.setOpaque(true);
        textField.setHorizontalAlignment(JTextField.LEFT);
        return textField;
    }
    private void showThemMoiDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) {
            showNotification("Không tìm thấy cửa sổ cha!", NotificationType.ERROR);
            return;
        }

        JDialog dialog = new JDialog(topFrame, "Thêm Mới Doanh Thu", true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        JLabel titleLabel = new JLabel("THÊM MỚI DOANH THU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 0, 4);
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.LINE_START;

        Color requiredFieldColor = new Color(255, 0, 0);

        // ID Hóa Đơn field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblId = new JLabel("ID Hóa Đơn: ");
        lblId.setFont(regularFont);
        JPanel idLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idLabelPanel.setBackground(Color.WHITE);
        idLabelPanel.add(lblId);
        JLabel starId = new JLabel("*");
        starId.setForeground(requiredFieldColor);
        starId.setFont(regularFont);
        idLabelPanel.add(starId);
        formPanel.add(idLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextField txtIdHoaDon = createStyledTextField();
        txtIdHoaDon.setPreferredSize(new Dimension(230, 38));
        formPanel.add(txtIdHoaDon, gbc);

        // Error label for ID Hóa Đơn
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 8, 4);
        JLabel idErrorLabel = createErrorLabel();
        formPanel.add(idErrorLabel, gbc);

        // Tháng/Năm field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(8, 4, 0, 4);
        JLabel lblDate = new JLabel("Tháng/Năm: ");
        lblDate.setFont(regularFont);
        JPanel dateLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateLabelPanel.setBackground(Color.WHITE);
        dateLabelPanel.add(lblDate);
        JLabel starDate = new JLabel("*");
        starDate.setForeground(requiredFieldColor);
        starDate.setFont(regularFont);
        dateLabelPanel.add(starDate);
        formPanel.add(dateLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JDateChooser dateChooser = createStyledDateChooser();
        dateChooser.setPreferredSize(new Dimension(230, 38));
        dateChooser.setDateFormatString("MM/yyyy");
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setHorizontalAlignment(JTextField.LEFT);
        formPanel.add(dateChooser, gbc);

        // Error label for Tháng/Năm
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 8, 4);
        JLabel dateErrorLabel = createErrorLabel();
        formPanel.add(dateErrorLabel, gbc);

        // Tổng Thu field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(8, 4, 0, 4);
        JLabel lblTongThu = new JLabel("Tổng Thu: ");
        lblTongThu.setFont(regularFont);
        JPanel tongThuLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tongThuLabelPanel.setBackground(Color.WHITE);
        tongThuLabelPanel.add(lblTongThu);
        formPanel.add(tongThuLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextField txtTongThu = createStyledTextField();
        txtTongThu.setPreferredSize(new Dimension(230, 38));
        txtTongThu.setEditable(false);
        txtTongThu.setBackground(new Color(220, 220, 220));
        txtTongThu.setHorizontalAlignment(JTextField.LEFT);
        formPanel.add(txtTongThu, gbc);

        // Add strut for consistent spacing
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 8, 4);
        formPanel.add(Box.createVerticalStrut(10), gbc);

        // Push remaining space to bottom
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createVerticalGlue(), gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        Dimension buttonSize = new Dimension(90, 36);

        JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        saveButton.setPreferredSize(buttonSize);
        saveButton.setMinimumSize(buttonSize);
        saveButton.setMaximumSize(buttonSize);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e ->
            luuDoanhThu(txtIdHoaDon, dateChooser, txtTongThu, idErrorLabel, dateErrorLabel, dialog)
        );

        JButton cancelButton = createRoundedButton("Hủy", Color.WHITE, textColor, 10);
        cancelButton.setBorder(new LineBorder(borderColor, 1));
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.setMinimumSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        // Enter key navigation
        txtIdHoaDon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dateTextField.requestFocus();
                }
            }
        });

        dateTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveButton.requestFocus();
                }
            }
        });

        // Set default button
        dialog.getRootPane().setDefaultButton(saveButton);

        // ID Hóa Đơn validation
        txtIdHoaDon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ValidationUtils.clearValidationError(txtIdHoaDon, idErrorLabel);
                String idText = txtIdHoaDon.getText().trim();
                if (!idText.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idText);
                        double amt = doanhThuController.getHoaDonAmount(id);
                        if (amt > 0) {
                            txtTongThu.setText(currencyFormat.format(amt) + " VND");
                            txtTongThu.setCaretPosition(0);
                            txtIdHoaDon.setForeground(Color.BLACK);
                            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                                new util.CustomBorder(8, successColor),
                                BorderFactory.createEmptyBorder(5, 12, 5, 12)
                            ));
                        } else {
                            txtTongThu.setText("Không tìm thấy hóa đơn");
                            txtIdHoaDon.setForeground(Color.RED);
                            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                                new util.CustomBorder(8, new Color(220, 53, 69)),
                                BorderFactory.createEmptyBorder(5, 12, 5, 12)
                            ));
                        }
                    } catch (NumberFormatException ex) {
                        txtTongThu.setText("ID không hợp lệ");
                        txtIdHoaDon.setForeground(Color.RED);
                        txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                            new util.CustomBorder(8, new Color(220, 53, 69)),
                            BorderFactory.createEmptyBorder(5, 12, 5, 12)
                        ));
                    }
                } else {
                    txtTongThu.setText("");
                    txtIdHoaDon.setForeground(Color.BLACK);
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new util.CustomBorder(8, borderColor),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                }
            }
        });

        // Tháng/Năm validation
        dateTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ValidationUtils.clearValidationError(dateChooser, dateErrorLabel);
                String text = dateTextField.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        new SimpleDateFormat("MM/yyyy").parse(text);
                        dateTextField.setForeground(Color.BLACK);
                    } catch (ParseException ex) {
                        dateTextField.setForeground(Color.RED);
                    }
                } else {
                    dateTextField.setForeground(Color.BLACK);
                }
            }
        });

        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
            ValidationUtils.clearValidationError(dateChooser, dateErrorLabel);
            if (evt.getNewValue() != null) {
                try {
                    String text = dateTextField.getText();
                    new SimpleDateFormat("MM/yyyy").parse(text);
                    dateTextField.setForeground(Color.BLACK);
                } catch (ParseException ex) {
                    dateTextField.setForeground(Color.RED);
                }
            } else {
                dateTextField.setForeground(Color.BLACK);
            }
        });

        dialog.setVisible(true);
    }
    
    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(300, 35));
        dateChooser.setBorder(new util.CustomBorder(8, borderColor));
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));
        dateTextField.setBackground(Color.WHITE);
        dateTextField.setOpaque(true);
        return dateChooser;
    }

    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setVisible(true);
        errorLabel.setPreferredSize(new Dimension(300, 16));
        errorLabel.setMinimumSize(new Dimension(300, 16));
        return errorLabel;
    }
    
    private void luuDoanhThu(JTextField txtIdHoaDon, JDateChooser dateChooser, JTextField txtTongThu,
            JLabel idErrorLabel, JLabel dateErrorLabel, JDialog dialog) {
        ValidationUtils.clearValidationError(txtIdHoaDon, idErrorLabel);
        ValidationUtils.clearValidationError(dateChooser, dateErrorLabel);

        String idText = txtIdHoaDon.getText().trim();
        String dateText = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText().trim();
        Date thangNam = dateChooser.getDate();
        boolean isValid = true;

        txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
            new util.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        if (idText.isEmpty()) {
            ValidationUtils.showValidationError(txtIdHoaDon, idErrorLabel, "ID Hóa Đơn không được để trống");
            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                new util.CustomBorder(8, new Color(220, 53, 69)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            txtIdHoaDon.requestFocus();
            isValid = false;
        } else {
            try {
                int idHoaDon = Integer.parseInt(idText);
                double tongThu = doanhThuController.getHoaDonAmount(idHoaDon);
                if (tongThu <= 0) {
                    ValidationUtils.showValidationError(txtIdHoaDon, idErrorLabel, "Không tìm thấy hóa đơn");
                    txtTongThu.setText("Không tìm thấy hóa đơn");
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new util.CustomBorder(8, new Color(220, 53, 69)),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                    txtIdHoaDon.requestFocus();
                    isValid = false;
                } else {
                    txtTongThu.setText(currencyFormat.format(tongThu) + " VND");
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new util.CustomBorder(8, successColor),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                }
            } catch (NumberFormatException ex) {
                ValidationUtils.showValidationError(txtIdHoaDon, idErrorLabel, "ID không hợp lệ");
                txtTongThu.setText("ID không hợp lệ");
                txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                    new util.CustomBorder(8, new Color(220, 53, 69)),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                txtIdHoaDon.requestFocus();
                isValid = false;
            }
        }

        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
            new util.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        if (dateText.isEmpty()) {
            ValidationUtils.showValidationError(dateChooser, dateErrorLabel, "Tháng/Năm không được để trống");
            dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new util.CustomBorder(8, new Color(220, 53, 69)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            if (isValid) {
                dateChooser.requestFocus();
                isValid = false;
            }
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                sdf.setLenient(false);
                thangNam = sdf.parse(dateText);
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new util.CustomBorder(8, successColor),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
            } catch (ParseException ex) {
                ValidationUtils.showValidationError(dateChooser, dateErrorLabel, "Tháng/Năm không hợp lệ");
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new util.CustomBorder(8, new Color(220, 53, 69)),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                if (isValid) {
                    dateChooser.requestFocus();
                    isValid = false;
                }
            }
        }

        if (!isValid) {
            String tongThuText = txtTongThu.getText().trim();
            if (idText.isEmpty()) {
                showNotification("ID Hóa Đơn không được để trống!", NotificationType.ERROR);
            } else if (tongThuText.equals("Không tìm thấy hóa đơn") || tongThuText.equals("ID không hợp lệ")) {
                showNotification("ID không hợp lệ hoặc không tìm thấy hóa đơn!", NotificationType.ERROR);
            }
            if (dateText.isEmpty()) {
                showNotification("Tháng/Năm không được để trống!", NotificationType.ERROR);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                    sdf.setLenient(false);
                    sdf.parse(dateText);
                } catch (ParseException ex) {
                    showNotification("Tháng/Năm không hợp lệ!", NotificationType.ERROR);
                }
            }
            return;
        }

        try {
            int idHoaDon = Integer.parseInt(idText);
            double tongThu = doanhThuController.getHoaDonAmount(idHoaDon);
            int newId = doanhThuController.themDoanhThu(thangNam, tongThu, idHoaDon);
            if (newId > 0) {
                txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                    new util.CustomBorder(8, successColor),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                dialog.dispose();
            } else {
                txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                    new util.CustomBorder(8, new Color(220, 53, 69)),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                showNotification("Thêm doanh thu thất bại!", NotificationType.ERROR);
            }
        } catch (NumberFormatException ex) {
            ValidationUtils.showValidationError(txtIdHoaDon, idErrorLabel, "ID không hợp lệ");
            txtTongThu.setText("ID không hợp lệ");
            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                new util.CustomBorder(8, new Color(220, 53, 69)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            txtIdHoaDon.requestFocus();
            showNotification("ID hóa đơn không hợp lệ! Vui lòng kiểm tra lại.", NotificationType.ERROR);
        }
    }

    private void showChiTietDoanhThuDialog(JFrame parent, int idDoanhThu, int idHoaDon, 
            String tenBenhNhan, String thangNam, String tongThu, String trangThai) {
        JDialog dialog = new JDialog(parent, "Chi Tiết Doanh Thu", true);
        dialog.setSize(500, 485);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        Dimension buttonSize = new Dimension(115, 36); // Tăng chiều rộng từ 90 lên 100

        JButton deleteButton = createRoundedButton("Xóa", accentColor, buttonTextColor, 10);
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMinimumSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                dialog,
                "Bạn có chắc chắn muốn xóa doanh thu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                doanhThuController.xoaDoanhThu(idDoanhThu);
                dialog.dispose();
                showNotification("Đã xóa doanh thu thành công!", NotificationType.SUCCESS);
            } else {
                showNotification("Hủy xóa thành công.", NotificationType.SUCCESS);
            }
        });

        JButton editButton = createRoundedButton("Chỉnh Sửa", warningColor, buttonTextColor, 10);
        editButton.setPreferredSize(buttonSize);
        editButton.setMinimumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        editButton.addActionListener(e -> {
            dialog.dispose();
            Object[] data = { idDoanhThu, idHoaDon, tenBenhNhan, thangNam, tongThu, trangThai };
            SuaDoanhThuDialog suaDoanhThuDialog = new SuaDoanhThuDialog(parent, data, this);
            suaDoanhThuDialog.setVisible(true);
        });
        
        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10);
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMinimumSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
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

            int idDoanhThu = (int) modelDoanhThu.getValueAt(modelRow, 0);
            int idHoaDon = (int) modelDoanhThu.getValueAt(modelRow, 1);
            String tenBenhNhan = (String) modelDoanhThu.getValueAt(modelRow, 2);
            String thangNam = (String) modelDoanhThu.getValueAt(modelRow, 3);
            String tongThu = modelDoanhThu.getValueAt(modelRow, 4).toString();
            String trangThai = (String) modelDoanhThu.getValueAt(modelRow, 5);

            Object[] data = { idDoanhThu, idHoaDon, tenBenhNhan, thangNam, tongThu, trangThai };

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
        
        if (searchText.isEmpty()) {
            restoreOriginalData();
            return;
        }
        
        modelDoanhThu.setRowCount(0);
        
        int matchCount = 0;
        double totalAmount = 0.0;
        
        for (Object[] row : originalData) {
            boolean match = false;
            
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
            
            if (match) {
                modelDoanhThu.addRow(row);
                matchCount++;
                
                if (row[4] instanceof Double) {
                    totalAmount += (Double) row[4];
                }
            }
        }
        
        updateTotalRow(totalAmount);
        
        tableDoanhThu.repaint();
        if (matchCount == 0) {
            showNotification("Không tìm thấy kết quả nào cho: '" + searchText + "'", NotificationType.WARNING);
        }
    }

    private void restoreOriginalData() {
        modelDoanhThu.setRowCount(0);
        
        double totalAmount = 0.0;
        for (Object[] row : originalData) {
            modelDoanhThu.addRow(row);
            
            if (row[4] instanceof Double) {
                totalAmount += (Double) row[4];
            }
        }
        
        updateTotalRow(totalAmount);
        
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
        showNotification(message, NotificationType.ERROR);
    }

    public DoanhThuController getDoanhThuController() {
        return doanhThuController;
    }

    public DefaultTableModel getModelDoanhThu() {
        return modelDoanhThu;
    }

    public void loadDoanhThuData(Object[] rowData, int highlightId) {
        // Kiểm tra kiểu dữ liệu
        if (!(rowData[1] instanceof Integer)) {
            System.err.println("Dữ liệu ID Hóa Đơn không phải Integer: " + rowData[1]);
            rowData[1] = 0; // Gán giá trị mặc định
        }
        if (!(rowData[4] instanceof Double)) {
            System.err.println("Dữ liệu Tổng Thu không phải Double: " + rowData[4]);
            try {
                // Thử chuyển đổi nếu dữ liệu là chuỗi dạng khoa học
                rowData[4] = Double.parseDouble(rowData[4].toString());
            } catch (NumberFormatException e) {
                System.err.println("Không thể chuyển đổi Tổng Thu: " + rowData[4]);
                rowData[4] = 0.0; // Gán giá trị mặc định
            }
        }

        if (highlightId > 0 && rowData[0].equals(highlightId)) {
            modelDoanhThu.insertRow(0, rowData);
            originalData.add(0, Arrays.copyOf(rowData, rowData.length));
            
            highlightedRowId = highlightId;
            tableDoanhThu.setRowSelectionInterval(0, 0);
            tableDoanhThu.scrollRectToVisible(tableDoanhThu.getCellRect(0, 0, true));
            
            if (highlightTimer != null && highlightTimer.isRunning()) {
                highlightTimer.stop();
            }
            highlightTimer = new Timer(10000, e -> resetHighlightState());
            highlightTimer.setRepeats(false);
            highlightTimer.start();
            
            tableDoanhThu.repaint();
        } else {
            modelDoanhThu.addRow(rowData);
            originalData.add(Arrays.copyOf(rowData, rowData.length));
        }
        
        double totalAmount = 0.0;
        for (int i = 0; i < modelDoanhThu.getRowCount(); i++) {
            Object value = modelDoanhThu.getValueAt(i, 4);
            if (value instanceof Double) {
                totalAmount += (Double) value;
            }
        }
        updateTotalRow(totalAmount);
    }

    public void loadDoanhThuData(Object[] rowData) {
        loadDoanhThuData(rowData, -1);
    }

    public void updateDoanhThuRow(int row, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            modelDoanhThu.setValueAt(rowData[i], row, i);
        }
    }

    public void removeDoanhThuRow(int row) {
        modelDoanhThu.removeRow(row);
        
        if (row < originalData.size()) {
            originalData.remove(row);
        }
        
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

    public void refreshData() {
        modelDoanhThu.setRowCount(0);
        clearOriginalData();
        totalRevenue = 0.0;
        updateTotalRow(totalRevenue);
        tableDoanhThu.repaint();
        doanhThuController.loadDoanhThuData();
    }

    private void resetHighlightState() {
        if (highlightTimer != null && highlightTimer.isRunning()) {
            highlightTimer.stop();
        }
        highlightedRowId = -1;
        tableDoanhThu.repaint();
        refreshData();
    }

    @Override
    public void onDataChanged() {
        refreshData();
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        NotificationType type;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                type = NotificationType.ERROR;
                break;
            case JOptionPane.WARNING_MESSAGE:
                type = NotificationType.WARNING;
                break;
            default:
                type = NotificationType.SUCCESS;
                break;
        }
        showNotification(message, type);
    }
}