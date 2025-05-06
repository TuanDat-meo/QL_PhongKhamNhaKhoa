package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.sql.Date;
import java.sql.SQLException;
import controller.LichHenController;
import controller.BenhNhanController;
import model.LichHen;
import model.NguoiDung;

public class LichSuLichHenPanel extends JPanel {
    private LichHenController controller;
    private BenhNhanController benhNhanController;
    private JTable lichHenTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private JTable upcomingTable;
    private DefaultTableModel upcomingModel;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JButton btnChiTiet;
    private JButton btnHuy;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnReset;
    private NguoiDung currentUser = null;
    
    // Color scheme
    private final Color BG_PRIMARY = new Color(245, 247, 250);
    private final Color BG_SECONDARY = new Color(255, 255, 255);
    private final Color BG_ACCENT = new Color(232, 240, 254);
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color PRIMARY_DARK = new Color(21, 101, 192);
    private final Color PRIMARY_LIGHT = new Color(66, 165, 245);
    private final Color SECONDARY_COLOR = new Color(66, 66, 66);
    private final Color ACCENT_COLOR = new Color(211, 47, 47);
    private final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private final Color WARNING_COLOR = new Color(237, 108, 2);
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private final Color TEXT_SECONDARY = new Color(97, 97, 97);
    private final Color TEXT_LIGHT = new Color(158, 158, 158);
    private final Color BORDER_COLOR = new Color(224, 224, 224);
    private final Color DIVIDER_COLOR = new Color(238, 238, 238);
    private final Color COLOR_MORNING = new Color(232, 245, 253);
    private final Color COLOR_AFTERNOON = new Color(255, 243, 224);
    private final Color COLOR_SELECTED = new Color(187, 222, 251);
    private final Color COLOR_BOOKED = new Color(224, 242, 241);
    private final Color COLOR_OWN_BOOKED = new Color(200, 230, 201);
    private final Color TABLE_HEADER_BG = new Color(25, 118, 210);
    private final Color TABLE_HEADER_FG = Color.WHITE;
    private final Color TABLE_ROW_ALT = new Color(250, 250, 250);
    
    // Font settings
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    // Columns for appointment table
    private final String[] COLUMN_NAMES = {
        "ID", "Ngày hẹn", "Giờ hẹn", "Bác sĩ", "Phòng khám", "Dịch vụ", "Trạng thái"
    };
    
    // Constructor
    public LichSuLichHenPanel(NguoiDung user) {
        this.currentUser = user;
        controller = new LichHenController();
        benhNhanController = new BenhNhanController();
        setupUI();
        loadData();
        setupEventListeners();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_PRIMARY);
        
        // Create the header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create the content panel
        JPanel contentPanel = createContentPanel();
        
        // Create the footer panel
        JPanel footerPanel = createFooterPanel();
        
        // Add panels to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(BG_PRIMARY);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setBackground(BG_PRIMARY);
        JLabel titleLabel = new JLabel("LỊCH SỬ LỊCH HẸN");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(PRIMARY_DARK);
        titlePanel.add(titleLabel);
        
        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
        searchPanel.setBackground(BG_PRIMARY);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(FONT_REGULAR);
        searchLabel.setForeground(TEXT_PRIMARY);
        searchLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(FONT_REGULAR);
        txtTimKiem.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        btnTimKiem = createStyledButton("Tìm kiếm", PRIMARY_COLOR);
        btnReset = createStyledButton("Đặt lại", SECONDARY_COLOR);
        
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(btnReset);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(BG_PRIMARY);
        
        // Create tabbed pane with custom styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_SUBTITLE);
        tabbedPane.setBackground(BG_SECONDARY);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        // Customize tabbed pane appearance
        UIManager.put("TabbedPane.selected", BG_ACCENT);
        UIManager.put("TabbedPane.contentAreaColor", BG_SECONDARY);
        UIManager.put("TabbedPane.focus", PRIMARY_LIGHT);
        UIManager.put("TabbedPane.light", BG_SECONDARY);
        UIManager.put("TabbedPane.tabAreaBackground", BG_SECONDARY);
        
        // Create upcoming appointments tab
        JPanel upcomingPanel = createAppointmentPanel(true);
        tabbedPane.addTab("Lịch hẹn sắp tới", upcomingPanel);
        
        // Create appointment history tab
        JPanel historyPanel = createAppointmentPanel(false);
        tabbedPane.addTab("Lịch sử lịch hẹn", historyPanel);
        
        // Add tabbed pane to content panel with a border
        JPanel tabbedPaneContainer = new JPanel(new BorderLayout());
        tabbedPaneContainer.setBackground(BG_SECONDARY);
        tabbedPaneContainer.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        tabbedPaneContainer.add(tabbedPane, BorderLayout.CENTER);
        
        contentPanel.add(tabbedPaneContainer, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createAppointmentPanel(boolean isUpcoming) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        DefaultTableModel model = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(DIVIDER_COLOR);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(COLOR_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_SECONDARY);
        table.setForeground(TEXT_PRIMARY);
        
        // Configure table header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBTITLE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        
        // Configure cell renderer for center alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        cell.setBackground(BG_SECONDARY);
                    } else {
                        cell.setBackground(TABLE_ROW_ALT);
                    }
                }
                
                // Status column styling (last column)
                if (column == 6) {
                    String status = value.toString();
                    if (status.equalsIgnoreCase("Đã hủy")) {
                        cell.setForeground(ACCENT_COLOR);
                    } else if (status.equalsIgnoreCase("Hoàn thành")) {
                        cell.setForeground(SUCCESS_COLOR);
                    } else if (status.equalsIgnoreCase("Đang chờ")) {
                        cell.setForeground(WARNING_COLOR);
                    } else {
                        cell.setForeground(PRIMARY_COLOR);
                    }
                } else {
                    cell.setForeground(isSelected ? TEXT_PRIMARY : TEXT_SECONDARY);
                }
                
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return cell;
            }
        };
        
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày hẹn
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Giờ hẹn
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Bác sĩ
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Phòng khám
        table.getColumnModel().getColumn(5).setPreferredWidth(250); // Dịch vụ
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Trạng thái
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_SECONDARY);
        
        // Add a title label based on the tab
        String title = isUpcoming ? "Lịch hẹn sắp tới" : "Lịch sử lịch hẹn";
        JLabel tabTitle = new JLabel(title);
        tabTitle.setFont(FONT_HEADING);
        tabTitle.setForeground(TEXT_PRIMARY);
        tabTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(tabTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Store references
        if (isUpcoming) {
            upcomingTable = table;
            upcomingModel = model;
        } else {
            historyTable = table;
            historyModel = model;
        }
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        footerPanel.setBackground(BG_PRIMARY);
        
        btnChiTiet = createStyledButton("Xem chi tiết", PRIMARY_COLOR);
        btnHuy = createStyledButton("Hủy lịch hẹn", ACCENT_COLOR);
        
        footerPanel.add(btnChiTiet);
        footerPanel.add(btnHuy);
        
        return footerPanel;
    }
    
    private void loadData() {
        if (currentUser == null) return;
        
        // Clear existing data
        upcomingModel.setRowCount(0);
        historyModel.setRowCount(0);
        
        try {
            // Get all appointments for the current user
            List<LichHen> dsLichHen = controller.getLichHenByUserId(currentUser.getIdNguoiDung());
            
            // Get current date
            Date currentDate = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            // Process each appointment
            for (LichHen lichHen : dsLichHen) {
                Object[] rowData = {
                    lichHen.getIdLichHen(),
                    dateFormat.format(lichHen.getNgayHen()),
                    timeFormat.format(lichHen.getGioHen()),
                    lichHen.getHoTenBacSi(),
                    lichHen.getTenPhong(),
                    lichHen.getMoTa(), // Using moTa as the service description
                    lichHen.getTrangThai()
                };
                
                // Check if the appointment is upcoming or in the past
                boolean isUpcoming = lichHen.getNgayHen().after(currentDate) || 
                                     lichHen.getNgayHen().equals(currentDate);
                
                if (isUpcoming) {
                    upcomingModel.addRow(rowData);
                } else {
                    historyModel.addRow(rowData);
                }
            }
            
            // Sort upcoming appointments by date (closest first)
            if (upcomingTable.getRowCount() > 0) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(upcomingModel);
                upcomingTable.setRowSorter(sorter);
                
                List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // Sort by date
                sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING)); // Then by time
                sorter.setSortKeys(sortKeys);
                sorter.sort();
                
                // Select the first row (closest appointment)
                upcomingTable.setRowSelectionInterval(0, 0);
                
                // Switch to upcoming tab
                tabbedPane.setSelectedIndex(0);
            }
            
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải dữ liệu lịch hẹn: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupEventListeners() {
        // Search button action
        btnTimKiem.addActionListener(e -> timKiemLichHen());
        
        // Reset button action
        btnReset.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });
        
        // Enter key in search field
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemLichHen();
                }
            }
        });
        
        // Chi tiết button action
        btnChiTiet.addActionListener(e -> xemChiTiet());
        
        // Hủy lịch hẹn button action
        btnHuy.addActionListener(e -> huyLichHen());
        
        // Double-click on table row
        upcomingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    xemChiTiet();
                }
            }
        });
        
        historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    xemChiTiet();
                }
            }
        });
        
        // Tab change listener to update button states
        tabbedPane.addChangeListener(e -> {
            updateButtonStates();
        });
    }
    
    private void updateButtonStates() {
        boolean isUpcomingTab = tabbedPane.getSelectedIndex() == 0;
        btnHuy.setEnabled(isUpcomingTab);
    }
    
    private void timKiemLichHen() {
        String searchText = txtTimKiem.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadData();
            return;
        }
        
        filterTable(upcomingModel, upcomingTable, searchText);
        filterTable(historyModel, historyTable, searchText);
    }
    
    private void filterTable(DefaultTableModel model, JTable table, String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        RowFilter<DefaultTableModel, Object> rowFilter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                for (int i = 1; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                        return true;
                    }
                }
                return false;
            }
        };
        
        sorter.setRowFilter(rowFilter);
    }
    
    private void xemChiTiet() {
        // Get the selected appointment
        LichHen selectedLichHen = getSelectedLichHen();
        
        if (selectedLichHen == null) {
            showInfoDialog("Vui lòng chọn một lịch hẹn để xem chi tiết");
            return;
        }
        
        // Display appointment details
        showLichHenDetails(selectedLichHen);
    }
    
    private void huyLichHen() {
        // Only allow cancelling upcoming appointments
        if (tabbedPane.getSelectedIndex() != 0) {
            showInfoDialog("Chỉ có thể hủy lịch hẹn sắp tới");
            return;
        }
        
        // Get the selected appointment
        LichHen selectedLichHen = getSelectedLichHen();
        
        if (selectedLichHen == null) {
            showInfoDialog("Vui lòng chọn một lịch hẹn để hủy");
            return;
        }
        
        // Confirm cancellation
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn hủy lịch hẹn này không?", 
            "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                // Set status to "Đã hủy"
                selectedLichHen.setTrangThai("Đã hủy");
                
                // Update the appointment in the database
                boolean success = controller.updateLichHen(selectedLichHen);
                
                if (success) {
                    showSuccessDialog("Đã hủy lịch hẹn thành công");
                    loadData();
                } else {
                    showErrorDialog("Không thể hủy lịch hẹn");
                }
            } catch (Exception e) {
                showErrorDialog("Lỗi khi hủy lịch hẹn: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private LichHen getSelectedLichHen() {
        JTable activeTable = tabbedPane.getSelectedIndex() == 0 ? upcomingTable : historyTable;
        DefaultTableModel activeModel = tabbedPane.getSelectedIndex() == 0 ? upcomingModel : historyModel;
        
        int selectedRow = activeTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        
        // Convert view index to model index if table is sorted
        int modelRow = activeTable.convertRowIndexToModel(selectedRow);
        
        // Get appointment ID from the hidden column
        int lichHenId = Integer.parseInt(activeModel.getValueAt(modelRow, 0).toString());
        
        try {
            return controller.getLichHenById(lichHenId);
        } catch (Exception e) {
            showErrorDialog("Lỗi khi lấy thông tin lịch hẹn: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void showLichHenDetails(LichHen lichHen) {
        // Create a styled dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết lịch hẹn", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_SECONDARY);
        
        // Create a panel for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_SECONDARY);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Thông tin lịch hẹn");
        headerLabel.setFont(FONT_HEADING);
        headerLabel.setForeground(PRIMARY_DARK);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        infoPanel.setBackground(BG_SECONDARY);
        infoPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        // Add appointment details
        addDetailRow(infoPanel, "Ngày hẹn:", dateFormat.format(lichHen.getNgayHen()));
        addDetailRow(infoPanel, "Giờ hẹn:", timeFormat.format(lichHen.getGioHen()));
        addDetailRow(infoPanel, "Bác sĩ:", lichHen.getHoTenBacSi());
        addDetailRow(infoPanel, "Phòng khám:", lichHen.getTenPhong());
        addDetailRow(infoPanel, "Dịch vụ:", lichHen.getMoTa());
        
        // Add status with color
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(FONT_BOLD);
        statusLabel.setForeground(TEXT_PRIMARY);
        
        JLabel statusValue = new JLabel(lichHen.getTrangThai());
        statusValue.setFont(FONT_REGULAR);
        
        // Set status color
        if (lichHen.getTrangThai().equalsIgnoreCase("Đã hủy")) {
            statusValue.setForeground(ACCENT_COLOR);
        } else if (lichHen.getTrangThai().equalsIgnoreCase("Hoàn thành")) {
            statusValue.setForeground(SUCCESS_COLOR);
        } else if (lichHen.getTrangThai().equalsIgnoreCase("Đang chờ")) {
            statusValue.setForeground(WARNING_COLOR);
        } else {
            statusValue.setForeground(PRIMARY_COLOR);
        }
        
        infoPanel.add(statusLabel);
        infoPanel.add(statusValue);
        
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_SECONDARY);
        
        JButton closeButton = createStyledButton("Đóng", PRIMARY_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(buttonPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(FONT_BOLD);
        labelComponent.setForeground(TEXT_PRIMARY);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(FONT_REGULAR);
        valueComponent.setForeground(TEXT_SECONDARY);
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}