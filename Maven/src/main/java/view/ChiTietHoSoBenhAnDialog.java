// view/ChiTietHoSoBenhAnDialog.java
package view;

import model.ChiTietDonThuoc;
import model.DonThuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChiTietHoSoBenhAnDialog extends JDialog {
    public ChiTietHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                                   int idHoSo, String tenBenhNhan, String chuanDoan,
                                   String ghiChu, String ngayTao, String trangThai,
                                   List<DonThuoc> danhSachDonThuoc) {
        super(owner, title, modal);
        setLayout(new BorderLayout(10, 10));

        JPanel thongTinPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        thongTinPanel.add(new JLabel("ID Hồ sơ:"));
        thongTinPanel.add(new JLabel(String.valueOf(idHoSo)));
        thongTinPanel.add(new JLabel("Tên Bệnh nhân:"));
        thongTinPanel.add(new JLabel(tenBenhNhan));
        thongTinPanel.add(new JLabel("Chuẩn đoán:"));
        thongTinPanel.add(new JLabel(chuanDoan));
        thongTinPanel.add(new JLabel("Ghi chú:"));
        thongTinPanel.add(new JScrollPane(new JTextArea(ghiChu)));
        thongTinPanel.add(new JLabel("Ngày tạo:"));
        thongTinPanel.add(new JLabel(ngayTao));
        thongTinPanel.add(new JLabel("Trạng thái:"));
        thongTinPanel.add(new JLabel(trangThai));

        JPanel donThuocPanel = new JPanel(new BorderLayout());
        donThuocPanel.setBorder(BorderFactory.createTitledBorder("Đơn Thuốc"));
        DefaultTableModel donThuocTableModel = new DefaultTableModel(new Object[]{"ID ĐT", "Tên thuốc", "Số lượng", "Hướng dẫn"}, 0);
        JTable donThuocTable = new JTable(donThuocTableModel);
        JScrollPane donThuocScrollPane = new JScrollPane(donThuocTable);
        donThuocPanel.add(donThuocScrollPane, BorderLayout.CENTER);

        if (danhSachDonThuoc != null && !danhSachDonThuoc.isEmpty()) {
            for (DonThuoc donThuoc : danhSachDonThuoc) {
                if (donThuoc.getChiTietDonThuocs() != null && !donThuoc.getChiTietDonThuocs().isEmpty()) {
                    for (ChiTietDonThuoc chiTiet : donThuoc.getChiTietDonThuocs()) {
                        Object[] rowData = {
                                donThuoc.getIdDonThuoc(),
                                chiTiet.getThuoc().getTenThuoc(),
                                chiTiet.getSoLuong(),
                                chiTiet.getHuongDanSuDung()
                        };
                        donThuocTableModel.addRow(rowData);
                    }
                } else {
                    donThuocTableModel.addRow(new Object[]{donThuoc.getIdDonThuoc(), "Không có thuốc", "", ""});
                }
            }
        } else {
            donThuocTableModel.addRow(new Object[]{"", "Không có đơn thuốc", "", ""});
        }

        add(thongTinPanel, BorderLayout.NORTH);
        add(donThuocPanel, BorderLayout.CENTER);

        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnDong);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}