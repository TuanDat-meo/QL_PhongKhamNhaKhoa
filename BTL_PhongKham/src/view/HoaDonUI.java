package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.HoaDonController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HoaDonUI extends JPanel {
    private HoaDonController qlHoaDon;
    private JTextField txtIdBenhNhan, txtIdHoaDon;
    private JButton btnHienThi, btnApDungBHYT;
    private JTable table;
    private DefaultTableModel tableModel;

    public HoaDonUI() {
        qlHoaDon = new HoaDonController();
        setLayout(new BorderLayout());

        // Panel nhập ID bệnh nhân
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tìm hóa đơn"));

        txtIdBenhNhan = new JTextField();
        btnHienThi = new JButton("Hiển thị hóa đơn");

        inputPanel.add(new JLabel("ID Bệnh Nhân:"));
        inputPanel.add(txtIdBenhNhan);
        inputPanel.add(btnHienThi);

        // Bảng hiển thị danh sách hóa đơn
        String[] columnNames = {"ID Hóa Đơn", "Ngày Tạo", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel áp dụng bảo hiểm
        JPanel bhytPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        bhytPanel.setBorder(BorderFactory.createTitledBorder("Áp dụng BHYT"));

        txtIdHoaDon = new JTextField();
        btnApDungBHYT = new JButton("Áp dụng BHYT");

        bhytPanel.add(new JLabel("ID Hóa Đơn:"));
        bhytPanel.add(txtIdHoaDon);
        bhytPanel.add(btnApDungBHYT);

        // Xử lý sự kiện
        btnHienThi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiHoaDon();
            }
        });

        btnApDungBHYT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apDungBHYT();
            }
        });

        // Thêm vào giao diện
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bhytPanel, BorderLayout.SOUTH);
    }

    // Hiển thị danh sách hóa đơn
    private void hienThiHoaDon() {
        try {
            int idBenhNhan = Integer.parseInt(txtIdBenhNhan.getText().trim());
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            qlHoaDon.hienThiHoaDon(idBenhNhan);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Áp dụng bảo hiểm y tế
    private void apDungBHYT() {
        try {
            int idHoaDon = Integer.parseInt(txtIdHoaDon.getText().trim());
            int idBenhNhan = Integer.parseInt(txtIdBenhNhan.getText().trim());
            qlHoaDon.apDungBaoHiem(idHoaDon, idBenhNhan);
            hienThiHoaDon(); // Cập nhật lại bảng
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
