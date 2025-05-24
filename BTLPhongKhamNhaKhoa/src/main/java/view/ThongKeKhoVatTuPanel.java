package view;

import controller.KhoVatTuController;
import model.KhoVatTu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThongKeKhoVatTuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private KhoVatTuController controller;

    public ThongKeKhoVatTuPanel() {
        setLayout(new BorderLayout());

        controller = new KhoVatTuController();

        JLabel titleLabel = new JLabel("Thống kê Kho vật tư");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {
                "ID Vật tư", "Tên vật tư", "Số lượng", "Đơn vị tính", "Tên nhà cung cấp", "Phân loại"
        });

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        hienThiDanhSachVatTu();
    }

    private void hienThiDanhSachVatTu() {
        try {
            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
            tableModel.setRowCount(0);

            for (KhoVatTu vt : danhSach) {
                tableModel.addRow(new Object[] {
                        vt.getIdVatTu(),
                        vt.getTenVatTu(),
                        vt.getSoLuong(),
                        vt.getDonViTinh(),
                        vt.getMaNCC(),
                        vt.getPhanLoai()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách vật tư: " + e.getMessage());
        }
    }
}