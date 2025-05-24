package view;

import controller.BacSiController;
import model.BacSi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThongKeBacSiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private BacSiController controller;

    public ThongKeBacSiPanel() {
        setLayout(new BorderLayout());

        controller = new BacSiController();

        JLabel titleLabel = new JLabel("Thống kê Danh sách Bác sĩ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {
                "ID", "Họ tên", "ID Người dùng", "ID Phòng khám", "Chuyên khoa", "Bằng cấp", "Kinh nghiệm"
        });

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        hienThiDanhSachBacSi();
    }

    private void hienThiDanhSachBacSi() {
        try {
            List<BacSi> danhSach = controller.layDanhSachBacSi();
            tableModel.setRowCount(0); // Xóa dữ liệu cũ

            for (BacSi bs : danhSach) {
                tableModel.addRow(new Object[] {
                        bs.getIdBacSi(),
                        bs.getHoTenBacSi(),
                        bs.getIdNguoiDung(),
                        bs.getIdPhongKham(),
                        bs.getChuyenKhoa(),
                        bs.getBangCap(),
                        bs.getKinhNghiem()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách bác sĩ: " + e.getMessage());
        }
    }
}