package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import controller.DichVuController;
import model.DichVu;

public class DichVuPanel extends JPanel {
    private JTable table;
    private DefaultTableModel modelTable;
    private JTextField tfTen, tfGia;
    private DichVuController controller;

    public DichVuPanel(Connection conn) {
        this.controller = new DichVuController(conn);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // === TABLE ===
        modelTable = new DefaultTableModel(new Object[]{"ID", "Tên dịch vụ", "Giá"}, 0);
        table = new JTable(modelTable);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === INPUT FORM ===
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin dịch vụ"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfTen = new JTextField(15);
        tfGia = new JTextField(10);
        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên dịch vụ:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(tfTen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(tfGia, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(btnAdd, gbc);
        gbc.gridx = 1;
        inputPanel.add(btnDelete, gbc);

        add(inputPanel, BorderLayout.EAST);

        // === LOAD DATA ===
        loadTable();

        // === EVENTS ===
        btnAdd.addActionListener(e -> {
            try {
                String ten = tfTen.getText().trim();
                double gia = Double.parseDouble(tfGia.getText().trim());
                controller.themDichVu(ten, gia);
                tfTen.setText("");
                tfGia.setText("");
                loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) modelTable.getValueAt(row, 0);
                    try {
                        controller.xoaDichVu(id);
                        loadTable();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để xóa.");
            }
        });
    }

    private void loadTable() {
        try {
            modelTable.setRowCount(0);
            List<DichVu> list = controller.getDanhSach();
            for (DichVu dv : list) {
                modelTable.addRow(new Object[]{dv.getId(), dv.getTenDichVu(), dv.getGia()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Dịch Vụ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 450);
            frame.setLocationRelativeTo(null);
            // Dummy connection để demo, bạn cần truyền đúng Connection từ DB thực tế
            Connection conn = null;
            frame.setContentPane(new DichVuPanel(conn));
            frame.setVisible(true);
        });
    }
}
