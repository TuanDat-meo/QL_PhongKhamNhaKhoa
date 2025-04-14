package view;

import model.LichHen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AppointmentsDialog extends JDialog {
    public AppointmentsDialog(JFrame parent, List<LichHen> appointments) {
        super(parent, "Lịch Hẹn", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table with appointments
        String[] columns = {
            "ID", "Bệnh Nhân", "Ngày Hẹn", "Giờ Hẹn", "Lý Do", "Trạng Thái"
        };
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
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
            model.addRow(rowData);
        }
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}