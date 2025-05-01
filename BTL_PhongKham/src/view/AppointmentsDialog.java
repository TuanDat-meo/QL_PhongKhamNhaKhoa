package view;

import model.LichHen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AppointmentsDialog extends JDialog {
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    
    public AppointmentsDialog(JFrame parent, List<LichHen> appointments) {
        super(parent, "Lịch Hẹn", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        // Main container with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("QUẢN LÝ LỊCH HẸN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create table with appointments
        String[] columns = {
            "ID", "Bệnh Nhân", "Ngày Hẹn", "Giờ Hẹn", "Lý Do", "Trạng Thái"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add appointment data
        for (LichHen appointment : appointments) {
            Object[] rowData = {
                appointment.getIdLichHen(),
                appointment.getHoTenBenhNhan(),
                appointment.getNgayHen(),
                appointment.getGioHen(),
                appointment.getMoTa(),
                appointment.getTrangThai()
            };
            tableModel.addRow(rowData);
        }
        
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        appointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        appointmentsTable.setShowGrid(true);
        appointmentsTable.setGridColor(new Color(230, 230, 230));
        
        // Style the table header
        JTableHeader header = appointmentsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(66, 139, 202));
        header.setForeground(Color.WHITE);
        
        // Alternate row colors
        appointmentsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton viewButton = new JButton("Xem Chi Tiết");
        viewButton.setBackground(new Color(66, 139, 202));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(220, 53, 69));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);
        
        // Wrap button panel in a container to add padding
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomContainer.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        // Apply custom styling to the dialog
        getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
        
        setContentPane(mainPanel);
        setResizable(true);
    }
}