package view;

import model.DieuTri;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TreatmentsDialog extends JDialog {
    public TreatmentsDialog(JFrame parent, List<DieuTri> treatments) {
        super(parent, "Điều Trị", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table with treatments
        String[] columns = {
            "ID", "Bệnh Nhân", "Tên Điều Trị", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Trạng Thái"
        };
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add treatment data
        for (DieuTri treatment : treatments) {
            Object[] rowData = {
                treatment.getIdDieuTri(),
                treatment.getTenBenhNhan(),
                treatment.getTenDieuTri(),
                treatment.getNgayBatDau(),
                treatment.getNgayKetThuc(),
                treatment.getTrangThai()
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