package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import connect.connectMySQL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import controller.DichVuController;
import model.DichVu;


public class DichVuKhachHangPanel extends JPanel {
    
    private JTable dichVuTable;
    private DefaultTableModel tableModel;
    private DichVuController dichVuController;
    private JTextField searchField;
    private JPanel mainPanel;
    
    // Colors to match the GiaoDienKhachHang theme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Soft blue
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Lighter blue for hover effects
    private final Color LIGHT_COLOR = new Color(236, 240, 241);     // Off-white for contrast
    private final Color ACCENT_COLOR = new Color(26, 188, 156);     // Teal accent
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    // Number formatter for currency
    private final NumberFormat currencyFormat = NumberFormat
            .getCurrencyInstance(new Locale("vi", "VN"));
    
    public DichVuKhachHangPanel() {
        try {
            // Create connection and controller
            Connection conn = connectMySQL.getConnection();
            dichVuController = new DichVuController(conn);
            
            initializeUI();
            loadDichVuData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo giao diện: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel with services table
        mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title and description
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Danh Sách Dịch Vụ");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel descLabel = new JLabel("Dưới đây là các dịch vụ nha khoa mà chúng tôi cung cấp");
        descLabel.setFont(CONTENT_FONT);
        descLabel.setForeground(Color.DARK_GRAY);
        
        titlePanel.add(titleLabel);
        titlePanel.add(descLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(LABEL_FONT);
        
        searchField = new JTextField(15);
        searchField.setFont(CONTENT_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(204, 204, 204), 1, true),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        
        // Search action
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterDichVuTable(searchField.getText().trim());
            }
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        
        // Create table model with columns
        String[] columnNames = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Giá"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        
        dichVuTable = new JTable(tableModel);
        customizeTable();
        
        JScrollPane scrollPane = new JScrollPane(dichVuTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Info Panel at bottom
        JPanel infoPanel = createInfoPanel();
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void customizeTable() {
        // Set table properties
        dichVuTable.setRowHeight(32);
        dichVuTable.setFont(CONTENT_FONT);
        dichVuTable.setSelectionBackground(new Color(52, 152, 219, 60));
        dichVuTable.setSelectionForeground(Color.BLACK);
        dichVuTable.setShowGrid(true);
        dichVuTable.setGridColor(new Color(230, 230, 230));
        dichVuTable.setFillsViewportHeight(true);
        
        // Customize header
        JTableHeader header = dichVuTable.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        // Set column widths
        dichVuTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        dichVuTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        dichVuTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        // Center-align the ID and price columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        dichVuTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Custom renderer for price column
        dichVuTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (value != null && value instanceof Double) {
                    setText(currencyFormat.format((Double) value));
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 5, 5, 5)
        ));
        
        // Info text
        JTextPane infoTextPane = new JTextPane();
        infoTextPane.setContentType("text/html");
        infoTextPane.setEditable(false);
        infoTextPane.setText(
            "<html><body style='font-family: Segoe UI; font-size: 13px; color: #555;'>" +
            "• Giá dịch vụ đã bao gồm VAT.<br>" +
            "• Tất cả dịch vụ đều được thực hiện bởi đội ngũ bác sĩ chuyên nghiệp.<br>" +
            "• Vui lòng liên hệ trực tiếp tại phòng khám hoặc gọi điện để được tư vấn chi tiết.<br>" +
            "• Đặt lịch hẹn trước để có trải nghiệm tốt nhất." +
            "</body></html>"
        );
        infoTextPane.setBackground(new Color(245, 245, 245));
        infoTextPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        infoPanel.add(infoTextPane, BorderLayout.CENTER);
        
        return infoPanel;
    }
    private void loadDichVuData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Load data from controller
            List<DichVu> danhSachDichVu = dichVuController.getDanhSach();
            
            // Add data to table model
            for (DichVu dv : danhSachDichVu) {
                Object[] row = {
                    dv.getId(),
                    dv.getTenDichVu(),
                    dv.getGia()
                };
                tableModel.addRow(row);
            }
            
            if (danhSachDichVu.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Không có dịch vụ nào trong cơ sở dữ liệu.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi tải dữ liệu dịch vụ: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void filterDichVuTable(String searchText) {
        try {
            if (searchText.isEmpty()) {
                loadDichVuData(); // Reload all data if search text is empty
                return;
            }
            
            String lowerSearchText = searchText.toLowerCase();
            
            // Load all services and filter manually
            List<DichVu> allServices = dichVuController.getDanhSach();
            tableModel.setRowCount(0);
            
            for (DichVu dv : allServices) {
                if (dv.getTenDichVu().toLowerCase().contains(lowerSearchText) || 
                    String.valueOf(dv.getId()).contains(lowerSearchText) ||
                    String.valueOf(dv.getGia()).contains(lowerSearchText)) {
                    
                    Object[] row = {
                        dv.getId(),
                        dv.getTenDichVu(),
                        dv.getGia()
                    };
                    tableModel.addRow(row);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi tìm kiếm dịch vụ: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}