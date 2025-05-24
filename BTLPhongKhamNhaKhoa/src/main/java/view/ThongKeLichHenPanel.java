package view;

import model.LichHen;
import controller.LichHenController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThongKeLichHenKhachHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private LichHenController controller;

    public ThongKeLichHenKhachHangPanel() {
        setLayout(new BorderLayout());

        controller = new LichHenController();

        JLabel titleLabel = new JLabel("Thống kê Lịch hẹn & Khách hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {
                "ID Lịch hẹn", "Tên bác sĩ", "Tên bệnh nhân", "Ngày hẹn", "Giờ hẹn", "Phòng khám", "Trạng thái", "Mô tả"
        });

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        hienThiDanhSachLichHen();
    }

    private void hienThiDanhSachLichHen() {
        try {
            List<LichHen> danhSach = controller.getAllLichHen();
            tableModel.setRowCount(0);

            for (LichHen lh : danhSach) {
                tableModel.addRow(new Object[] {
                        lh.getIdLichHen(),
                        lh.getHoTenBacSi(),
                        lh.getHoTenBenhNhan(),
                        lh.getNgayHen(),
                        lh.getGioHen(),
                        lh.getIdPhongKham(),
                        lh.getTrangThai(),
                        lh.getMoTa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
        }
    }
}